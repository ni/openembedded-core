#
#   Creates package indices for the IMAGE_PKGTYPE
#

do_package_index[nostamp] = "1"
do_package_index[depends] += "${PACKAGEINDEXDEPS}"
do_package_index[recrdeptask] += 'do_package_write_deb'
do_package_index[recrdeptask] += 'do_package_write_ipk'
do_package_index[recrdeptask] += 'do_package_write_rpm'

python do_package_index() {
    from oe.rootfs import generate_index_files
    generate_index_files(d)
}

# Package indexes are required for the dummy SDK architectures
# to support scenarios where SDK images are built from feeds.
PACKAGE_ARCHS:append:task-package-index = " sdk-provides-dummy-target"
SDK_PACKAGE_ARCHS:append:task-package-index = " sdk-provides-dummy-${SDKPKGSUFFIX}"

addtask do_package_index before do_build
