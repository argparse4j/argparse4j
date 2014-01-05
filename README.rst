Argparse4j - The Java command-line argument parser library
==========================================================

Argparse4j is a command line argument parser library for Java based
on Python's
`argparse <http://docs.python.org/3/library/argparse.html>`_ module.

Argparse4j is available in Maven central repository:

.. code-block:: xml

  <dependency>
    <groupId>net.sourceforge.argparse4j</groupId>
    <artifactId>argparse4j</artifactId>
    <version>0.4.3</version>
  </dependency>

There are still missing features which exist in argparse but not in
argparse4j, but there are also new features which only exist in
argparse4j.

Here is summary of features:

* Supported positional arguments and optional arguments.
* Variable number of arguments.
* Generates well formatted line-wrapped help message.
* Suggests optional arguments/sub-command if unrecognized
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

The primary documentation is done using `Sphinx
<http://sphinx-doc.org/>`_.  You need Sphinx to run ``mvn site``.
