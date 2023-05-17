SUMMARY = "Rebuilds the package index"
LICENSE = "MIT"

INHIBIT_DEFAULT_DEPS = "1"
PACKAGES = ""

inherit nopackages package_index

deltask do_fetch
deltask do_unpack
deltask do_patch
deltask do_configure
deltask do_compile
deltask do_install
deltask do_populate_lic
deltask do_populate_sysroot

EXCLUDE_FROM_WORLD = "1"
