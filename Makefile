#
# A makefile for CSC2 Semester 2 Assignment 1
# Angus Longmore
# 2020

JAVAC=/usr/bin/javac
.SUFFIXES: .java .class
SRCDIR=src
BINDIR=bin
DOCDIR=doc

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<

CLASSES= \
	Terrain.class \
	FlowPanel.class \
	Flow.class
	
	
	

CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)
default: $(CLASS_FILES)

run:
	java -cp $(BINDIR) Flow data/medsample_in.txt
clean:
	rm $(BINDIR)/*.class
docs:
	javadoc -d $(DOCDIR) src/*.java
