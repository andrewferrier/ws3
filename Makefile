# Modified by Andrew Ferrier to version 0.2.10
#
# Makefile for WS3 (aka WSSS, Web Server Simulation System)
#
# See http://www.new-destiny.co.uk/andrew/project/
#
# Todo:
# 	- Allow changing of PREFIX to work with shell script.

SHELL		= /bin/sh
RM              = rm -f
MV              = mv -f
CP		= cp
CHMOD		= chmod
XARGS		= xargs
CAT		= cat
FIND            = find
ZIP		= zip
TAR		= tar
RMDIR		= rmdir
JAVA		= java

INSTALL         = install
INSTALL_FILE    = $(INSTALL) -m $(MODE_NOEXEC)
INSTALL_FILE_EC = $(INSTALL) -m $(MODE_EXEC)
INSTALL_DIR     = $(INSTALL) -m $(MODE_EXEC) -d

MODE_NOEXEC     = 644
MODE_EXEC       = 755

JAVAC           = javac
JAVADOC         = javadoc
JAR             = jar

JAVAC_FLAGS     = -deprecation -target 1.5
JAVADOC_FLAGS   = -version -author
JAR_FLAGS       = cvfm

CLASSPATH_SUFFIX = .:./xerces.jar

ifdef CLASSPATH
        export CLASSPATH := $(CLASSPATH):$(CLASSPATH_SUFFIX)
else
        export CLASSPATH := $(CLASSPATH_SUFFIX)
endif

# Installing

SCRIPT_UNIX	= ws3
SCRIPT_WIN	= ws3.bat
SCHEMA		= ws3.xsd

PREFIX		= /usr/local/
SCRIPT_DIR	= $(PREFIX)bin
JAR_DIR	        = $(PREFIX)lib/ws3
SCHEMA_DIR	= $(PREFIX)share/ws3
DOC_DIR         = $(PREFIX)docs/ws3

# Testing

RUNME		= $(SCRIPT_DIR)/$(SCRIPT_UNIX)
RUNME_NOINSTALL = $(JAVA) -jar $(JAR_FILE)

# Packaging

PACKAGES = \
	doc.ajf98.websim \
	doc.ajf98.websim.objects \
	doc.ajf98.websim.exceptions \
	doc.ajf98.websim.processes \
	doc.ajf98.websim.processes.abs \
	doc.ajf98.SimTools.exceptions \
	doc.ajf98.SimTools \
	doc.ajf98.util

TOPLEVEL	= .

ZIP_FILE	= $(TOPLEVEL)/ws3.zip
JAR_FILE        = $(TOPLEVEL)/ws3.jar
GZ_FILE		= $(TOPLEVEL)/ws3.tar.gz
BZ2_FILE	= $(TOPLEVEL)/ws3.tar.bz2
XERCES_JAR	= $(TOPLEVEL)/xerces.jar

MAKEFILE	= Makefile
MANIFEST	= manifest
LICENCE		= LICENCE LICENCE.xerces
README		= README

JAVA_DIRS	= $(subst .,/,$(PACKAGES)) $(TOPLEVEL)
JAVA_SRC	= $(foreach dir, $(JAVA_DIRS), $(wildcard $(dir)/*.java))
JAVA_OBJS	= $(JAVA_SRC:.java=.class)

JAR_OBJS	= \( -name '*.class' \)

JAVADOC_DIR 	= $(TOPLEVEL)/docs

TESTS_DIR	= tests
TESTS		= $(wildcard $(TOPLEVEL)/$(TESTS_DIR)/*.xml)

PACKAGE_FILES	= $(JAVA_SRC) $(MAKEFILE) $(MANIFEST) $(JAVA_OBJS) \
			$(JAR_FILE) $(XERCES_JAR) $(LICENCE) $(README) \
			$(TESTS) $(SCHEMA)

%.class: %.java
	$(JAVAC) $(JAVAC_FLAGS) $<

%.jar: 	$(JAVA_OBJS) $(MANIFEST)
	$(FIND) $(TOPLEVEL) $(JAR_OBJS) -print | $(XARGS) \
	$(JAR) $(JAR_FLAGS) $(JAR_FILE) $(MANIFEST)
	$(JAR) -i $(JAR_FILE)

.PHONY: all jar install installdoc installjar uninstalldoc \
	uninstalljar uninstall doc clean zip gz bz2 test

all:	classes jar doc

classes: $(JAVA_OBJS)

jar:	$(JAR_FILE)

doc:	$(JAVA_SRC)
	$(INSTALL_DIR) $(JAVADOC_DIR)
	$(JAVADOC) -d $(JAVADOC_DIR) $(JAVADOC_FLAGS) $(PACKAGES)
	
randomtest: $(TOPLEVEL)/doc/ajf98/websim/RandomTest.class
	$(JAVA) doc.ajf98.websim.RandomTest

test:	install 
	$(foreach tfile, $(TESTS), $(RUNME) $(tfile) $(SCHEMA_DIR)/$(SCHEMA) ;)
	
# The following rule may be a bit buggy --- I've had to install
# a .. for no apparent reason.

test_noinstall:	jar
	$(foreach tfile, $(TESTS), $(RUNME_NOINSTALL) $(tfile) $(TOPLEVEL)/../$(SCHEMA);)

install: installjar installdoc installschema

uninstall: uninstalljar uninstalldoc uninstallschema

#installscript:
#	$(INSTALL_FILE_EC) $(SCRIPT_UNIX) $(SCRIPT_DIR)
#	$(INSTALL_FILE) $(SCRIPT_WIN) $(SCRIPT_DIR)
	
installschema:
	$(INSTALL_DIR) $(SCHEMA_DIR)
	$(INSTALL_FILE)	$(SCHEMA) $(SCHEMA_DIR)

installjar: jar
	$(INSTALL_DIR) $(JAR_DIR)
	$(INSTALL_FILE) $(JAR_FILE) $(JAR_DIR)
	$(INSTALL_FILE) $(XERCES_JAR) $(JAR_DIR)

installdoc: doc
	$(INSTALL_DIR) $(DOC_DIR)
	$(CP) -R $(JAVADOC_DIR)/* $(DOC_DIR)
	$(CHMOD) -R a+X $(DOC_DIR)/

uninstalljar:
	$(RM) $(JAR_DIR)/$(JAR_FILE)
	$(RM) $(JAR_DIR)/$(XERCES_JAR)
	-$(RMDIR) $(JAR_DIR)

uninstalldoc:
	$(RM) -R $(DOC_DIR)/*
	-$(RMDIR) $(DOC_DIR)

#uninstallscript:
#	$(RM) $(SCRIPT_DIR)/$(SCRIPT_UNIX)
#	$(RM) $(SCRIPT_DIR)/$(SCRIPT_WIN)
	
uninstallschema:
	$(RM) $(SCHEMA_DIR)/$(SCHEMA)

zip:	$(PACKAGE_FILES)
	$(ZIP) $(ZIP_FILE) $(PACKAGE_FILES)

gz:	$(PACKAGE_FILES)
	$(TAR) cvzf $(GZ_FILE) $(PACKAGE_FILES)

bz2:	$(PACKAGE_FILES)
	$(TAR) cvjf $(BZ2_FILE) $(PACKAGE_FILES)

clean:
	$(FIND) . \( -name '*~' -o -name '*.class' \) -print | $(XARGS) $(RM)  	
	$(RM) $(JAR_FILE)
	$(RM) -R $(JAVADOC_DIR)
	$(RM) $(ZIP_FILE) $(GZ_FILE) $(BZ2_FILE)
	$(RM) $(TOPLEVEL)/$(TESTS_DIR)/*.csv
	$(RM) $(TOPLEVEL)/$(TESTS_DIR)/*.txt

cleanbak: clean
	$(FIND) . \( -name '*.bak' \) -print | $(XARGS) $(RM) 
