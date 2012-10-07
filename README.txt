Argparse4j - The command line argument parser library
=====================================================

The argparse4j is a command line argument parser library for Java
based on Python's argparse.

To see how to use argparse4j, see http://argparse4j.sourceforge.net/

There are still missing features which exist in argparse but not in
argparse4j, such as mutual exclusion group. On the other hand, there
are also new features only exist in argparse4j.

Here is a summary of features:

* Supported positional arguments and optional arguments.
* Variable number of arguments.
* Generates well formatted line-wrapped help message.
* Suggests optional arguments/sub-command if unrecognized
  arguments/sub-command were given, e.g. "unrecognized argument
  '--tpye'. Did you mean: --type".
* Takes into account East Asian Width ambiguous characters when
  line-wrap.
* Sub-commands like, git add.
* Customizable option prefix characters, e.g. '+f' and '/h'.
* Print default values in help message.
* Choice from given collection of values.
* Type conversion from option strings.
* Can directly assign values into user defined classes using
  annotation.
* Group arguments so that it will be printed in help message in more
  readable way.
* Read additional arguments from file.
