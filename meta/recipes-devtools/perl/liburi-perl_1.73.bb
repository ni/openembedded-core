SUMMARY = "Perl module to manipulate and access URI strings"
DESCRIPTION = "This package contains the URI.pm module with friends. \
The module implements the URI class. URI objects can be used to access \
and manipulate the various components that make up these strings."

HOMEPAGE = "http://search.cpan.org/dist/URI/"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://LICENSE;md5=c453e94fae672800f83bc1bd7a38b53f"

DEPENDS += "perl"

SRC_URI = "http://www.cpan.org/authors/id/E/ET/ETHER/URI-${PV}.tar.gz"

SRC_URI[md5sum] = "37d44a08e599aa945b32a9434ffe00a5"
SRC_URI[sha256sum] = "cca7ab4a6f63f3ccaacae0f2e1337e8edf84137e73f18548ec7d659f23efe413"

S = "${WORKDIR}/URI-${PV}"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR}"

inherit cpan ptest-perl

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

do_install_prepend() {
	# these tests require "-T" (taint) command line option
	rm -rf ${B}/t/cwd.t
	rm -rf ${B}/t/file.t
}

RDEPENDS_${PN}-ptest += "libtest-needs-perl"

BBCLASSEXTEND = "native"
