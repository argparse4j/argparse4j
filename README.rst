Argparse4j - The Java command-line argument parser library
==========================================================

.. image:: https://api.travis-ci.com/argparse4j/argparse4j.svg?branch=master
    :target: https://travis-ci.com/github/argparse4j/argparse4j

Argparse4j is a command line argument parser library for Java based
on Python's
`argparse <https://docs.python.org/3/library/argparse.html>`_ module.

Argparse4j is available in Maven central repository:

.. code-block:: xml

  <dependency>
    <groupId>net.sourceforge.argparse4j</groupId>
    <artifactId>argparse4j</artifactId>
    <version>0.9.0</version>
  </dependency>

**IMPORTANT**: When upgrading, read `Migration
<https://argparse4j.github.io/migration.html>`_.
There is an important change in 0.5.0 which might break your code.  The
documentation describes the change and how to migrate from earlier
versions.

There are still missing features which exist in argparse but not in
argparse4j, but there are also new features which only exist in
argparse4j.

Here is summary of features:

* Supported positional arguments and named arguments.
* Variable number of arguments.
* Generates well formatted line-wrapped help message.
* Suggests named arguments/sub-command if unrecognized
  arguments/sub-command were given, e.g.:

  .. code-block:: console

    unrecognized argument '--tpye'
    Did you mean:
      --type

* Takes into account East Asian Width ambiguous characters when
  line-wrap.
* Sub-commands like, ``git add``.
* Sub-command alias names, e.g., ``co`` for ``checkout``.
* Customizable option prefix characters, e.g. ``+f`` and ``/h``.
* Print default values in help message.
* Choice from given collection of values.
* Type conversion from option strings.
* Can directly assign values into user defined classes using annotation.
* Group arguments so that it will be printed in help message in
  more readable way.
* Mutually exclusive argument group.
* Read additional arguments from file.
* Argument/sub-command abbreviations.

Requirements
------------

Java 8 or higher is needed.

The main JAR contains module information for the Java Module System. The
module name is ``net.sourceforge.argparse4j``.

Building
--------

To build you need Java 9 or higher, and Maven 3.2.3 or higher.

By default the build signs the artifacts. This requires `GNU Privacy Guard
<https://gnupg.org/>`_ and the setup of a personal key. Signing can be
disabled by adding ``-Dgpg.skip=true`` to the arguments passed to Maven.

The primary documentation is done using `Sphinx
<https://www.sphinx-doc.org/en/master/>`_.  You need Sphinx to run ``mvn site``.
