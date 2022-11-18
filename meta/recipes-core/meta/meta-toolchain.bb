SUMMARY = "Meta package for building a installable toolchain"
LICENSE = "MIT"

PR = "r7"

do_populate_sdk:prepend() {
    # It does not make sense to build a toolchain when BUILD_IMAGES_FROM_FEEDS
    # is enabled. The SDK packages for the given SDK_MACHINE are built and
    # indexed as part of this recipe, and are not available in any feed.
    if d.getVar("BUILD_IMAGES_FROM_FEEDS"):
        bb.warn("BUILD_IMAGES_FROM_FEEDS should be turned off when building a toolchain. Disabling.")
    d.setVar("BUILD_IMAGES_FROM_FEEDS", 0)
}

inherit populate_sdk
