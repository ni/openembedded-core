do_packagefeed[depends] += "dpkg-native:do_populate_sysroot apt-native:do_populate_sysroot"
do_packagefeed[recrdeptask] += "do_package_write_deb do_package_qa"
