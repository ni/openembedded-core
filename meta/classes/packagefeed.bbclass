
FEED_CLASSES = "packagefeed_${IMAGE_PKGTYPE} packagegroup"
inherit ${FEED_CLASSES}

FEED_PATH = "${DEPLOY_DIR_FEED}/${PN}"
FEED_DEPENDS ??= ""

fakeroot python do_packagefeed() {
    from oe.packagefeed import create_packagefeed

    create_packagefeed(d)
}
addtask packagefeed before do_build
do_packagefeed[nostamp] = "1"
do_packagefeed[cleandirs] += "${FEED_PATH}"
do_packagefeed[rdepends] += "${@' '.join([x + ':do_packagefeed' for x in d.getVar('FEED_DEPENDS').split()])}"
