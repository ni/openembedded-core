#
# Copyright (C) 2008 OpenedHand Ltd.
#

SUMMARY = "Profiling tools"

PR = "r3"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PROFILE_TOOLS_X = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'sysprof', '', d)}"
# sysprof doesn't support aarch64 and nios2
PROFILE_TOOLS_X_aarch64 = ""
PROFILE_TOOLS_X_nios2 = ""
PROFILE_TOOLS_SYSTEMD = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd-analyze', '', d)}"

RRECOMMENDS_${PN} = "\
    ${PERF} \
    trace-cmd \
    blktrace \
    ${PROFILE_TOOLS_X} \
    ${PROFILE_TOOLS_SYSTEMD} \
    "

PROFILETOOLS = "\
    powertop \
    "
PERF = "perf"
PERF_libc-musl = ""

# systemtap needs elfutils which is not fully buildable on some arches/libcs
SYSTEMTAP = "systemtap"
SYSTEMTAP_libc-musl = ""
SYSTEMTAP_nios2 = ""

LTTNGTOOLS = "lttng-tools"
LTTNGTOOLS_arc = ""

BABELTRACE = "babeltrace"
BABELTRACE2 = "babeltrace2"

# valgrind does not work on the following configurations/architectures

VALGRIND = "valgrind"
VALGRIND_libc-musl = ""
VALGRIND_mipsarch = ""
VALGRIND_nios2 = ""
VALGRIND_armv4 = ""
VALGRIND_armv5 = ""
VALGRIND_armv6 = ""
VALGRIND_armeb = ""
VALGRIND_aarch64 = ""
VALGRIND_linux-gnux32 = ""
VALGRIND_linux-gnun32 = ""

RDEPENDS_${PN} = "\
    ${PROFILETOOLS} \
    ${LTTNGTOOLS} \
    ${BABELTRACE} \
    ${BABELTRACE2} \
    ${SYSTEMTAP} \
    ${VALGRIND} \
    "
