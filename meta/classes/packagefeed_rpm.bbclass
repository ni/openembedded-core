do_packagefeed[depends] += "rpm-native:do_populate_sysroot dnf-native:do_populate_sysroot createrepo-c-native:do_populate_sysroot"
do_packagefeed[recrdeptask] += "do_package_write_rpm do_package_qa"
