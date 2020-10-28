SUMMARY = "Additional utilities for the opkg package manager"
SUMMARY_opkg-utils-shell-tools = "Additional utilities for the opkg package manager (shell scripts)"
SUMMARY_opkg-utils-python-tools = "Additional utilities for the opkg package manager (python scripts)"
SUMMARY_update-alternatives-opkg = "Utility for managing the alternatives system"
SECTION = "base"
HOMEPAGE = "http://git.yoctoproject.org/cgit/cgit.cgi/opkg-utils"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://opkg.py;beginline=2;endline=18;md5=ffa11ff3c15eb31c6a7ceaa00cc9f986"
PROVIDES += "${@bb.utils.contains('PACKAGECONFIG', 'update-alternatives', 'virtual/update-alternatives', '', d)}"

SRC_URI = "http://git.yoctoproject.org/cgit/cgit.cgi/${BPN}/snapshot/${BPN}-${PV}.tar.gz \
           file://0001-update-alternatives-correctly-match-priority.patch \
           "
UPSTREAM_CHECK_URI = "http://git.yoctoproject.org/cgit/cgit.cgi/opkg-utils/refs/"

SRC_URI[md5sum] = "7bbadb3c381f3ea935b21d3bb8cc4671"
SRC_URI[sha256sum] = "046517600fb0aed6c4645edefe02281f4fa2f1c02f71596152d93172452c0b01"

TARGET_CC_ARCH += "${LDFLAGS}"

SUBPACKAGES = "opkg-utils-shell-tools ${@bb.utils.contains('PACKAGECONFIG', 'python', 'opkg-utils-python-tools', '', d)}"

PACKAGES =+ "${SUBPACKAGES}"

# main package is a metapackage that depends on all the subpackages
ALLOW_EMPTY_${PN} = "1"
RDEPENDS_${PN} += "${SUBPACKAGES}"

# For native builds we use the host Python
PYTHONRDEPS = "python3 python3-shell python3-io python3-math python3-crypt python3-logging python3-fcntl python3-pickle python3-compression python3-stringold"
PYTHONRDEPS_class-native = ""

PACKAGECONFIG = "python update-alternatives"
PACKAGECONFIG[python] = ",,,${PYTHONRDEPS}"
PACKAGECONFIG[update-alternatives] = ",,,"

FILES_opkg-utils-shell-tools = "\
    ${bindir}/opkg-build \
    ${mandir}/man1/opkg-build.1 \
    ${bindir}/opkg-buildpackage \
    ${bindir}/opkg-diff \
    ${bindir}/opkg-extract-file \
    ${bindir}/opkg-feed \
    ${bindir}/opkg-unbuild \
"

FILES_opkg-utils-python-tools = "\
    ${bindir}/opkg-compare-indexes \
    ${bindir}/opkg-graph-deps \
    ${bindir}/opkg-list-fields \
    ${bindir}/opkg-make-index \
    ${bindir}/opkg-show-deps \
    ${bindir}/opkg-update-index \
    ${bindir}/arfile.py \
    ${bindir}/opkg.py \
"

RDEPENDS_opkg-utils-shell-tools += "bash"

# python tools depend on shell tools because opkg-compare-indexes needs opkg-diff
RDEPENDS_opkg-utils-python-tools += "${PYTHONRDEPS} opkg-utils-shell-tools"

RRECOMMENDS_${PN}_class-native = ""
RRECOMMENDS_${PN}_class-nativesdk = ""
RDEPENDS_${PN}_class-native = ""
RDEPENDS_${PN}_class-nativesdk = ""

do_install() {
	oe_runmake PREFIX=${prefix} DESTDIR=${D} install
	if ! ${@bb.utils.contains('PACKAGECONFIG', 'update-alternatives', 'true', 'false', d)}; then
		rm -f "${D}${bindir}/update-alternatives"
	fi
}

do_install_append_class-target() {
	if ! ${@bb.utils.contains('PACKAGECONFIG', 'python', 'true', 'false', d)}; then
		grep -lZ "/usr/bin/env.*python" ${D}${bindir}/* | xargs -0 rm
	fi

	if [ -e "${D}${bindir}/update-alternatives" ]; then
		sed -i ${D}${bindir}/update-alternatives -e 's,/usr/bin,${bindir},g; s,/usr/lib,${nonarch_libdir},g'
	fi
}

# These are empty and will pull python3-dev into images where it wouldn't
# have been otherwise, so don't generate them.
PACKAGES_remove = "${PN}-dbg ${PN}-dev ${PN}-staticdev"

PACKAGES =+ "update-alternatives-opkg"
FILES_update-alternatives-opkg = "${bindir}/update-alternatives"
RPROVIDES_update-alternatives-opkg = "update-alternatives update-alternatives-cworth"
RREPLACES_update-alternatives-opkg = "update-alternatives-cworth"
RCONFLICTS_update-alternatives-opkg = "update-alternatives-cworth"

pkg_postrm_update-alternatives-opkg() {
	rm -rf $D${nonarch_libdir}/opkg/alternatives
	rmdir $D${nonarch_libdir}/opkg || true
}

BBCLASSEXTEND = "native nativesdk"

CLEANBROKEN = "1"
