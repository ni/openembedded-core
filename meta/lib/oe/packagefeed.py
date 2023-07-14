#
# SPDX-License-Identifier: GPL-2.0-only
#

from abc import ABCMeta, abstractmethod
import os
import oe

class Packagefeed(object, metaclass=ABCMeta):
    """
    This is an abstract class. Do not instantiate this directly.
    """

    def __init__(self, d):
        self.d = d
        self.deploy_dir = self.d.expand('${DEPLOY_DIR_FEED}/${PN}')
        self.indexer = None

    @abstractmethod
    def _create_feed_directory(self):
        pass

    def create(self):
        bb.note("###### Generate packagefeed with index #######")

        self._create_feed_directory()

        # call the package indexer create index method
        self.indexer.write_index()

def get_class_for_type(imgtype):
    import importlib
    mod = importlib.import_module('oe.package_manager.' + imgtype + '.packagefeed')
    return mod.PkgFeed

def create_packagefeed(d):
    env_bkp = os.environ.copy()

    img_type = d.getVar('IMAGE_PKGTYPE')

    cls = get_class_for_type(img_type)
    cls(d).create()
    os.environ.clear()
    os.environ.update(env_bkp)

def create_feed_dir(d, subrepo_dir, deploydir, taskname, feed_deps):
    import errno

    taskdepdata = d.getVar("BB_TASKDEPDATA", False)
    pn = d.getVar("PN")
    feedtaskname = "packagefeed"

    bb.utils.remove(subrepo_dir, recurse=True)
    bb.utils.mkdirhier(subrepo_dir)

    feed_pkgdeps = find_task_pkg_deps(pn, taskdepdata, feedtaskname, taskname)

    # Find any packages already in feeds that this feed depends on
    # and remove them
    for feed in feed_deps.split():
        met_pkgdeps = find_task_pkg_deps(feed, taskdepdata, feedtaskname, taskname)
        feed_pkgdeps = feed_pkgdeps.difference(met_pkgdeps)

    seendirs = set()
    for dep in feed_pkgdeps:
        c = taskdepdata[dep][0]
        manifest, d2 = oe.sstatesig.find_sstate_manifest(c, taskdepdata[dep][2], taskname, d, multilibcache = {})
        if not manifest:
            bb.fatal("No manifest generated from: %s in %s" % (c, taskdepdata[dep][2]))
        if not os.path.exists(manifest):
            continue
        with open(manifest, "r") as f:
            for l in f:
                l = l.strip()
                deploydir = os.path.normpath(deploydir)
                if bb.data.inherits_class('packagefeed-stability', d):
                    dest = l.replace(deploydir + "-prediff", "")
                else:
                    dest = l.replace(deploydir, "")
                dest = subrepo_dir + dest
                if l.endswith("/"):
                    if dest not in seendirs:
                        bb.utils.mkdirhier(dest)
                        seendirs.add(dest)
                    continue
                # Try to hardlink the file, copy if that fails
                destdir = os.path.dirname(dest)
                if destdir not in seendirs:
                    bb.utils.mkdirhier(destdir)
                    seendirs.add(destdir)
                try:
                    os.link(l, dest)
                except OSError as err:
                    if err.errno == errno.EXDEV:
                        bb.utils.copyfile(l, dest)
                    else:
                        raise

def find_task_pkg_deps(pn, taskdepdata, feedtaskname, taskname):
    start_task = next((dep for dep, data in taskdepdata.items()
                  if data[1] == "do_" + feedtaskname and data[0] == pn), None)
    if start_task is None:
        bb.fatal("Couldn't find %s in BB_TASKDEPDATA?" % pn + ":do_" + feedtaskname)
    pkgdeps = set()
    tasks = [start_task]
    seen = set(start_task)
    # Support direct dependencies (do_rootfs -> do_package_write_X)
    # or indirect dependencies within PN (do_populate_sdk_ext -> do_rootfs -> do_package_write_X)
    while tasks:
        new_tasks = []
        for task in tasks:
            deps = taskdepdata[task][3]
            for dep in deps:
                if "do_" + taskname in dep:
                    pkgdeps.add(dep)
                elif dep not in seen:
                    new_tasks.append(dep)
                    seen.add(dep)
        tasks = new_tasks
    return pkgdeps
