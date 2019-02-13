SUMMARY = "Jitter RNG Daemon"
DESCRIPTION = "Using the Jitter RNG core, the rngd provides an entropy source \
that feeds into the Linux /dev/random device if its entropy runs low. It updates \
the /dev/random entropy estimator such that the newly provided entropy unblocks /dev/random. \
\
The seeding of /dev/random also ensures that /dev/urandom benefits from entropy.  \
Especially during boot time, when the entropy of Linux is low, the Jitter RNGd \
provides a source of sufficient entropy."
HOMEPAGE = "http://www.chronox.de/jent.html"

LICENSE = "GPLv2+ | BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=e52365752b36cfcd7f9601d80de7d8c6 \
                    file://COPYING.bsd;md5=66a5cedaf62c4b2637025f049f9b826f \
                    file://COPYING.gplv2;md5=eb723b61539feef013de476e68b5c50a \
"

SRC_URI = "git://github.com/smuellerDD/jitterentropy-rngd.git;protocol=https \
           file://0001-Makefile-support-cross-compiling.patch \
           file://init-jitterentropy-rngd \
"

PV = "1.0+git${SRCPV}"
SRCREV = "23be1542a232aefb33a1efa8bc5eef017f2ae261"

S = "${WORKDIR}/git"

inherit update-rc.d systemd

do_install () {
	oe_runmake install DESTDIR="${D}" \
			PREFIX="${exec_prefix}" \
			UNITDIR="${systemd_unitdir}"

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/init-jitterentropy-rngd ${D}${sysconfdir}/init.d/jitterentropy-rngd
}

INSANE_SKIP_${PN} += "already-stripped"

INITSCRIPT_NAME = "jitterentropy-rngd"
INITSCRIPT_PARAMS = "start 3 S . stop 1 0 6 ."

SYSTEMD_SERVICE_${PN} = "jitterentropy.service"

