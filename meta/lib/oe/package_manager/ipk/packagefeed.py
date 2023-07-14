#
# SPDX-License-Identifier: GPL-2.0-only
#

from oe.package_manager.ipk import OpkgIndexer
from oe.packagefeed import Packagefeed, create_feed_dir

class PkgFeed(Packagefeed):
    def __init__(self, d):
        super(PkgFeed, self).__init__(d)
        self.indexer = OpkgIndexer(self.d, self.deploy_dir)

    def _create_feed_directory(self):
        create_feed_dir(self.d, self.deploy_dir, self.d.getVar('DEPLOY_DIR_IPK'), "package_write_ipk", self.d.getVar('FEED_DEPENDS'))
