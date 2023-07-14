do_packagefeed[depends] += "opkg-native:do_populate_sysroot"
do_packagefeed[recrdeptask] += "do_package_write_ipk do_package_qa"
