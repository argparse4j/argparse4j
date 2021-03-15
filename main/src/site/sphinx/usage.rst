The Argparse4j User Manual
==========================

Argparse4j is a command line argument parser library for Java based on
Python's `argparse <https://docs.python.org/3/library/argparse.html>`_
module.  Because of the difference of language features, we cannot use
same syntax and usage of original, but we have tried to bring the same
touch and feel as much as possible.  We also use same terminology as
much as possible.

This manual was written based on argparse's manual and most of the the
sentences are almost identical, just replaced code examples. We use
this approach because argparse manual is well written and since both
are do the same thing, using existing manual makes us create a manual
with good quality in a short time. Thanks to Python community for the
great module and documentation.

We omitted package names from Java classes in this documentation for
readability because they tend to be quite long. Most Java programmers
use IDE (e.g., eclipse) and it has powerful auto-completion
features. And each class in argparse4j has unique name so it is not a
big problem after all. The ``import`` statements are also omitted from
the code snippet by the same reason.

Examples
--------

The following code is a Java program that takes a list of integers
and produces either the sum or the max::

    public class Prog {

        private static interface Accumulate {
            int accumulate(Collection<Integer> ints);
        }

        private static class Sum implements Accumulate {
            @Override
            public int accumulate(Collection<Integer> ints) {
                int sum = 0;
                for (Integer i : ints) {
                    sum += i;
                }
                return sum;
            }

            @Override
            public String toString() {
                return getClass().getSimpleName();
            }
        }

        private static class Max implements Accumulate {
            @Override
            public int accumulate(Collection<Integer> ints) {
                return Collections.max(ints);
            }

            @Override
            public String toString() {
                return getClass().getSimpleName();
            }
        }

        public static void main(String[] args) {
            ArgumentParser parser = ArgumentParsers.newFor("prog").build()
                    .description("Process some integers.");
            parser.addArgument("integers")
                    .metavar("N")
                    .type(Integer.class)
                    .nargs("+")
                    .help("an integer for the accumulator");
            parser.addArgument("--sum")
                    .dest("accumulate")
                    .action(Arguments.storeConst())
                    .setConst(new Sum())
                    .setDefault(new Max())
                    .help("sum the integers (default: find the max)");
            try {
                Namespace res = parser.parseArgs(args);
                System.out.println(((Accumulate) res.get("accumulate"))
                        .accumulate((List<Integer>) res.get("integers")));
            } catch (ArgumentParserException e) {
                parser.handleError(e);
            }
        }
    }


It can be run at the command line and provides useful help messages:

.. code-block:: console

    $ java Prog -h
    usage: prog [-h] [--sum] N [N ...]

    Process some integers.

    positional arguments:
      N                      an integer for the accumulator

    named arguments:
      -h, --help             show this help message and exit
      --sum                  sum the integers (default: find the max)

When run with the appropriate arguments, it prints either the sum or
the max of the command-line integers:

.. code-block:: console

    $ java Prog  1 2 3 4
    4
    $ java Prog  1 2 3 4 --sum
    10

If invalid arguments are passed in, it will throw an exception. The
user program can catch the exception and show error message:

.. code-block:: console

    $ java Prog  a b c
    usage: prog [-h] [--sum] N [N ...]
    prog: error: argument integers: could not construct class java.lang.Integer from a (For input string: "a")

The following sections walk you through this example.

Creating a parser
^^^^^^^^^^^^^^^^^

The first step using the argparse4j is creating
:javadoc:`inf.ArgumentParser` object. To do this, use
|ArgumentParsers.newFor| static method of :javadoc:`ArgumentParsers`
class. This will return a builder for the parser. (Note: Prior to
0.8.0 an ArgumentParser object was created using
``newArgumentParser(...)`` methods of ``ArgumentParsers``. See
:doc:`migration`.) Use method |ArgumentParserBuilder.build| to create
the parser::

    ArgumentParser parser = ArgumentParsers.newFor("prog").build()
        .description("Process some integers.");

The :javadoc:`inf.ArgumentParser` object will hold all the information
necessary to parse the command line into Java data types.

Since 0.9.0 there is a variant of |ArgumentParsers.newForDefaults|
that allows you to choose of which version the defaults for
configuration settings must be used. This makes it easy to use all
improvements of a specific version without having to change all
configuration settings individually::

    ArgumentParser parser = ArgumentParsers.newFor("prog", DefaultSettings.VERSION_0_9_0_DEFAULT_SETTINGS)
        .build()
        .description("Process some integers.");

The following versions have default settings that differ from the
previous version:

* 0.9.0
    * The help text for a mutually-exclusive group will include an
      extra paragraph explaining that at most 1 argument of that
      group may be given:
      :ref:`ArgumentParserBuilder-mustHelpTextIncludeMutualExclusivity`

When upgrading to a newer version of argparse4j the chosen defaults
will be honored, so the behavior of applications does not change
without the developer explicitly opting in to improvements.


.. _Adding-arguments:

Adding arguments
^^^^^^^^^^^^^^^^

Filling an ArgumentParser with information about program arguments is
done by making calls to the |ArgumentParser.addArgument| method.
Generally, this calls tell the ArgumentParser how to take the strings
on the command line and turn them into objects. This information is
stored and used when |ArgumentParser.parseArgs| is called. For
example::

    parser.addArgument("integers")
            .metavar("N")
            .type(Integer.class)
            .nargs("+")
            .help("an integer for the accumulator");
    parser.addArgument("--sum")
            .dest("accumulate")
            .action(Arguments.storeConst())
            .setConst(new Sum())
            .setDefault(new Max())
            .help("sum the integers (default: find the max)");

Later, calling |ArgumentParser.parseArgs| will return an
:javadoc:`inf.Namespace` object with two attributes, ``integers`` and
``accumulate``. The ``integers`` attribute will be a
:javatype:`List<Integer>` which has one or more ints, and the
``accumulate`` attribute will be either the :javatype:`Sum` object, if
``--sum`` was specified at the command line, or the :javatype:`Max`
object if it was not.

Passing arguments
^^^^^^^^^^^^^^^^^

ArgumentParser parses arguments through the
|ArgumentParser.parseArgs| method. This will inspect the command
line, convert each argument to the appropriate type and then invoke
the appropriate action. In most cases, this means a simple
:javadoc:`inf.Namespace` object will have attributes parsed out of the
command line. The following code::

    Namespace res = parser.parseArgs(new String[] { "--sum", "7", "-1", "42" });
    System.out.println(res);

will display:

.. code-block:: console

    Namespace(integers=[7, -1, 42], accumulate=Sum)

In Java, the command line arguments are typically given as ``String[]
argv``.  To parse the command line, pass this object to
|ArgumentParser.parseArgs| method.

ArgumentParser objects
----------------------

To create :javadoc:`inf.ArgumentParser` object, use
|ArgumentParsers.newFor| static method of
:javadoc:`ArgumentParsers` class.  This will return a builder for
the parser.  The following parameter must be specified:

* :ref:`ArgumentParsers-newFor-prog` - The name of the
  program. This is necessary because ``main()`` method in Java does
  not provide program name.

Configure the parser to be build using methods of the builder:

* :ref:`ArgumentParserBuilder-addHelp` - Add a -h/--help
  option to the parser.  (default: ``true``).

* :ref:`ArgumentParserBuilder-prefixChars` - The set of
  characters that prefix named arguments. (default: '-')

* :ref:`ArgumentParserBuilder-fromFilePrefix` - The
  set of characters that prefix file path from which additional
  arguments are read. (default: ``null``)

* :ref:`ArgumentParserBuilder-locale` - The locale to use for
  messages. (default: ``Locale.getDefault()``)

* :ref:`ArgumentParserBuilder-cjkWidthHack` - Treat Unicode
  characters having East Asian Width property Wide/Full/Ambiguous to
  have twice a width of ascii characters when formatting help message
  if locale is "ja", "zh" or "ko". (default: ``true``)

* :ref:`ArgumentParserBuilder-defaultFormatWidth` - The default
  width (in columns) for formatting messages. This value is used if
  terminal width detection is disabled or fails. (default: ``75``)

* :ref:`ArgumentParserBuilder-terminalWidthDetection` - Detect the
  width of the terminal the application is running in. If the
  terminal width cannot be detected, the default format width is
  used. (default: ``true``)

* :ref:`ArgumentParserBuilder-singleMetavar` - Show the metavar
  string in help message only after the last flag instead of each
  flag. (default: ``false``)

* :ref:`ArgumentParserBuilder-includeArgumentNamesAsKeysInResult` -
  Also add the argument name as a key in the result (if a value for
  the argument will be added for ``dest``). The argument name is the
  name of positional arguments, and the first long flag, or otherwise
  first flag, without the prefix for named arguments.

* :ref:`ArgumentParserBuilder-mustHelpTextIncludeMutualExclusivity` -
  Add a text after the description and before the arguments of a
  mutually-exclusive group explaining that at most 1 of the arguments
  may be given.

* :ref:`ArgumentParserBuilder-noDestConversionForPositionalArgs` -
  Do not perform any conversion to produce "dest" value from
  positional argument name. (default: ``false``)

The parser is created using method
|ArgumentParserBuilder.build|.

After creation of the instance, several additional parameters can be
specified using following methods:

* :ref:`ArgumentParser-description` - Text
  to display before the argument help.

* :ref:`ArgumentParser-epilog` - Text to display after the argument
  help.

* :ref:`ArgumentParser-defaultHelp` - Display default value to help
  message. (default: ``false``)

* :ref:`ArgumentParser-usage` - The string describing the program
  usage (default: generated)

* :ref:`ArgumentParser-version` - The string describing the program
  version.

The following sections describes how each of these are used.

.. _ArgumentParsers-newFor-prog:

prog
^^^^

In Java, the name of the program is not included in the argument in
`main()` method. Because of this, the name of the program must be
supplied to |ArgumentParsers.newFor|.

.. _ArgumentParserBuilder-addHelp:

addHelp
^^^^^^^

By default, :javadoc:`inf.ArgumentParser` objects add an option which
simply displays the parser's help message. For example, consider
following code::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--foo").help("foo help");
        Namespace res = parser.parseArgs(args);
    }

If ``-h`` or ``--help`` is supplied at the command line, the
ArgumentParser will display help message:

.. code-block:: console

    $ java Demo --help
    usage: prog [-h] [--foo FOO]

    named arguments:
      -h, --help             show this help message and exit
      --foo FOO              foo help

Occasionally, it may be useful to disable the addition of this help
option.  This can be achieved by passing ``false`` as the
addHelp_ argument to |ArgumentParserBuilder.addHelp|::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers
            .newFor("prog").addHelp(false).build();
        parser.addArgument("--foo").help("foo help");
        parser.printHelp();
    }

.. code-block:: console

    $ java Demo
    usage: prog [--foo FOO]

    named arguments:
      --foo FOO              foo help

The help option is typically ``-h/--help``. The exception to this is
if the :ref:`ArgumentParserBuilder-prefixChars` is
specified and does not include ``-``, in which case ``-h`` and
``--help`` are not valid options. In this case, the first character in
:ref:`ArgumentParserBuilder-prefixChars` is used to prefix
the help options::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers
            .newFor("prog").addHelp(true).prefixChars("+/").build();
        parser.printHelp();
    }

.. code-block:: console

    $ java Demo
    usage: prog [+h]

    named arguments:
      +h, ++help             show this help message and exit


.. _ArgumentParserBuilder-prefixChars:

prefixChars
^^^^^^^^^^^

Most command line options will use ``-`` as the prefix, e.g.
``-f/--foo``. Parsers that need to support different or additional
prefix characters, e.g. for options like ``+f`` or ``/foo``, may
specify them using the *prefixChars* to
|ArgumentParserBuilder.prefixChars|::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").prefixChars("-+")
                .build();
        parser.addArgument("+f");
        parser.addArgument("++bar");
        Namespace res = parser.parseArgs(args);
        System.out.println(res);
    }

.. code-block:: console

    $ java Demo +f X ++bar Y
    Namespace(f=X, bar=Y)

The *prefixChars* argument defaults to ``-`` (you can use
:javafield:`ArgumentParsers.DEFAULT_PREFIX_CHARS` for this). Supplying
a set of characters that does not include ``-`` will cause
``-f/--foo`` options to be disallowed.

.. _ArgumentParserBuilder-fromFilePrefix:

fromFilePrefix
^^^^^^^^^^^^^^

It is sometimes useful to read arguments from file other than typing
them in command line, for example, when lots of arguments are needed.
If *fromFilePrefix* is given as non ``null`` string, arguments starts
with one of these characters are treated as file path and
ArgumentParser reads additional arguments from the file.  For example:

.. code-block:: console

    $ cat args.txt
    -f
    bar

::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog")
                .fromFilePrefix("@").build();
        parser.addArgument("-f");
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }

.. code-block:: console

    $ java Demo -f foo @args.txt
    Namespace(f=bar)

The each line of the file is treated as one argument. Please be aware
that trailing empty lines or line with only white spaces are also
considered as arguments, although it is not readily noticeable to the
user. The empty line is treated as empty string.

By default, *fromFilePrefix* is ``null``, which means no argument is
treated as file path.

.. _ArgumentParserBuilder-locale:

locale
^^^^^^

The locale for messages of :javadoc:`inf.ArgumentParser` objects can
be changed by using |ArgumentParserBuilder.locale|. For example::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog")
                .locale(new Locale("nl")).build();
        parser.printHelp();
    }

.. code-block:: console

    $ java Demo
    gebruik: prog [-h]

    optionele argumenten:
      -h, --help             toon dit hulpbericht en sluit af

Currently messages have been (partially) translated to Dutch,
English, German and Russian.

The default locale is the default locale of the JVM
(``Locale.getDefault()``). Upto 0.9.0 the fallback locale for the
argparse4j resource bundle was overridden to be "en_US". Because of
the switch to named modules to support Java 9+, the override has been
removed.

.. _ArgumentParserBuilder-cjkWidthHack:

cjkWidthHack
^^^^^^^^^^^^

A number of characters in Chinese, Japanese and Korean (CJK) are
wider than others. If those characters are treated to have the same
width as other characters, texts may extend past the right margin
when printed. By enabling the CJK width, 2 columns are used for these
wide characters during the determination of line breaks, resulting in
better formatted text. To enable or disable handling of wide CJK
characters use |ArgumentParserBuilder.cjkWidthHack|::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog")
                .cjkWidthHack(false).build()
        ...
    }

*cjkWidthHack* is ``true`` by default.

.. _ArgumentParserBuilder-defaultFormatWidth:

defaultFormatWidth
^^^^^^^^^^^^^^^^^^

Messages generated by :javadoc:`inf.ArgumentParser` objects are
formatted to fit within a number of columns.
|ArgumentParserBuilder.defaultFormatWidth| can be used to set the
number of columns to use when
:ref:`ArgumentParserBuilder-terminalWidthDetection` is disabled or
when the detection cannot determine the number of columns from the
environment::

    public static void main(String[] arguments) {
        ArgumentParser parser = ArgumentParsers.newFor("prog")
                .defaultFormatWidth(40).build()
                .description(
                        "A program showing how argparse4j formats long messages.");
        parser.printHelp();
    }

.. code-block:: console

    $ java Demo
    usage: prog [-h]

    A   program   showing   how   argparse4j
    formats long messages.

    ...

The default value for *defaultFormatWidth* is ``75``.

.. _ArgumentParserBuilder-terminalWidthDetection:

terminalWidthDetection
^^^^^^^^^^^^^^^^^^^^^^

Argparse4j tries to format messages so they fit the terminal the
application is running in. It does this by looking at environment
variable ``COLUMNS``, or running ``stty`` on platforms that support
it. The detection can be enabled or disabled using
|ArgumentParserBuilder.terminalWidthDetection|. When disabling the
detection :ref:`ArgumentParserBuilder-defaultFormatWidth` is used as
the number of columns::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog")
                .terminalWidthDetection(false).build()
                .description(
                        "A program showing how argparse4j formats long messages. " +
                                "Terminal width detection has been disabled, " +
                                "so this description is formatted to 75 characters wide.");
        parser.printHelp();
    }

.. code-block:: console

    $ java Demo
    usage: prog [-h]

    A program showing  how  argparse4j  formats  long  messages. Terminal width
    detection has  been  disabled,  so  this  description  is  formatted  to 75
    characters wide.

    ...

By default *terminalWidthDection* is ``true``.

.. _ArgumentParserBuilder-singleMetavar:

singleMetavar
^^^^^^^^^^^^^

The metavariable of an argument can be printed after each argument
name or only once after all argument names. This behavior is
controlled using |ArgumentParserBuilder.singleMetavar|. Here is an
example using a single metavariable::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("single")
                .singleMetavar(true).build();
        parser.addArgument("-f", "-file").nargs("+").metavar("FILE");
        parser.printHelp();
    }

.. code-block:: console

    $ java Demo
    usage: single [-h] [-f FILE [FILE ...]]

    named arguments:
      -h, --help             show this help message and exit
      -f, -file FILE [FILE ...]

Compare this with the output if using multiple metavariables::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("multiple")
                .singleMetavar(false).build();
        parser.addArgument("-f", "-file").nargs("+").metavar("FILE");
        parser.printHelp();
    }

.. code-block:: console

    $ java Demo
    usage: multiple [-h] [-f FILE [FILE ...]]

    named arguments:
      -h, --help             show this help message and exit
      -f FILE [FILE ...], -file FILE [FILE ...]

*singleMetavar* defaults to ``false``, so the metavariable is printed
after each argument name.

.. _ArgumentParserBuilder-includeArgumentNamesAsKeysInResult:

includeArgumentNamesAsKeysInResult
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The ``dest`` value of an argument is used as a key in the parse
result. It is different from (but based on) the name or a flag of the
argument. This requires clients to maintain 2 constants for working
with arguments, if the argument contains dashes: 1 for the argument
and 1 for ``dest``. With this flag you tell argparse4j to also
include the name (see below) of the argument in the result.

The name of an argument is determined as follows:

* Positional arguments: the name of the argument

* Named arguments: the first long flag, or if there is no long flag,
  the first flag. The prefix will be removed

Example for positional argument::

    public static void main(String[] arguments) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog")
                .includeArgumentNamesAsKeysInResult(true)
                .build();
        parser.addArgument("foo-bar");
        System.out.println(parser.parseArgs(arguments));
    }

.. code-block:: console

    $ java Demo value
    Namespace(foo_bar=value, foo-bar=value)

Example for named argument::

    public static void main(String[] arguments) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog")
                .includeArgumentNamesAsKeysInResult(true)
                .build();
        parser.addArgument("-foo-bar");
        System.out.println(parser.parseArgs(arguments));
    }

.. code-block:: console

    $ java Demo -foo-bar value
    Namespace(foo_bar=value, foo-bar=value)

Example for named argument with long argument::

    public static void main(String[] arguments) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog")
                .includeArgumentNamesAsKeysInResult(true)
                .build();
        parser.addArgument("-f-b", "--foo-bar");
        System.out.println(parser.parseArgs(arguments));
    }

.. code-block:: console

    $ java Demo -f-b value
    Namespace(foo_bar=value, foo-bar=value)
    $ java Demo --foo-bar value
    Namespace(foo_bar=value, foo-bar=value)

By default *includeArgumentNamesAsKeysInResult* is ``false``, so only
``dest`` of arguments is used for keys in the result.


.. _ArgumentParserBuilder-mustHelpTextIncludeMutualExclusivity:

mustHelpTextIncludeMutualExclusivity
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The mutual exclusivity of a group is normally only shown in the usage
of a parser. By setting this option to ``true``, or specifying
``DefaultSettings.VERSION_0_9_0_DEFAULT_SETTINGS`` (or higher) when
creating a parser builder, a paragraph after the description and
before the arguments of a mutually-exclusive group will be added to
the help. This paragraph explains that at most 1 of the arguments of
the group may be given. Example output:

.. code-block:: console

    MutexGroup:
      A mutually-exclusive group.

      At most 1 of the arguments below may be given.

      -f First argument
      -s Second argument

By default *mustHelpTextIncludeMutualExclusivity* is ``false``,
resulting in mutual exclusivity of a group being shown only in the
usage.


.. _ArgumentParserBuilder-noDestConversionForPositionalArgs:

noDestConversionForPositionalArgs
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Prior to 0.5.0 the destination for positional arguments was not
automatically determined from the argument name. See
:doc:`migration`.
|ArgumentParserBuilder.noDestConversionForPositionalArgs| can be used
to revert back to the pre 0.5.0 behavior. Note that you must
explicitly set a destination when you enable this option, because
otherwise the destination will be ``null``::

    public static void main(String[] arguments) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog")
                .noDestConversionForPositionalArgs(true)
                .build();
        parser.addArgument("foo-bar").dest("explicit-dest");
        System.out.println(parser.parseArgs(arguments));
    }

.. code-block:: console

    $ java Demo value
    Namespace(explicit-dest=value)

By default *noDestConversionForPositionalArgs* is ``false``, so the
names of positional arguments are automatically converted to destinations.

.. _ArgumentParser-description:

ArgumentParser.description()
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The |ArgumentParser.description| gives a brief description of what the
program does and how it works. In help message, the description is
displayed between command line usage string and the help messages for
the various arguments::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build()
            .description("A foo that bars");
        parser.printHelp();
    }

.. code-block:: console

    $ java Demo
    usage: prog [-h]

    A foo that bars

    named arguments:
      -h, --help             show this help message and exit

By default, the description will be line-wrapped so that it fits
within the given space.

.. _ArgumentParser-epilog:

ArgumentParser.epilog()
^^^^^^^^^^^^^^^^^^^^^^^

Some programs like to display additional description of the program
after the description of the arguments. Such text can be specified
using |ArgumentParser.epilog| method::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build()
                .description("A foo that bars")
                .epilog("And that's how you'd foo a bar");
        parser.printHelp();
    }

.. code-block:: console

    $ java Demo
    usage: prog [-h]

    A foo that bars

    named arguments:
      -h, --help             show this help message and exit

    And that's how you'd foo a bar

As with the :ref:`ArgumentParser-description` method, text specified
in |ArgumentParser.epilog| is by default line-wrapped.

.. _ArgumentParser-defaultHelp:

ArgumentParser.defaultHelp()
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The default value of each argument is not by default displayed in help
message. Specifying ``true`` to |ArgumentParser.defaultHelp| method
will display the default value of each argument in help message::


    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build()
            .defaultHelp(true);
        parser.addArgument("--foo")
            .type(Integer.class)
            .setDefault(42)
            .help("FOO!");
        parser.addArgument("bar")
            .nargs("*")
            .setDefault(1, 2, 3)
            .help("BAR!");
        parser.printHelp();
    }

.. code-block:: console

    $ java Demo
    usage: prog [-h] [-f FOO] [bar [bar ...]]

    positional arguments:
      bar                    BAR! (default: [1, 2, 3])

    named arguments:
      -h, --help             show this help message and exit
      -f FOO, --foo FOO      FOO! (default: 42)

.. _ArgumentParser-usage:

ArgumentParser.usage()
^^^^^^^^^^^^^^^^^^^^^^

By default, :javadoc:`inf.ArgumentParser` calculates the usage message
from the arguments it contains::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--foo").nargs("?").help("foo help");
        parser.addArgument("bar").nargs("+").help("bar help");
        Namespace res = parser.parseArgsOrFail(args);
    }

.. code-block:: console

    $ java Demo -h
    usage: prog [-h] [--foo [FOO]] bar [bar ...]

    positional arguments:
      bar                    bar help

    named arguments:
      -h, --help             show this help message and exit
      --foo [FOO]            foo help

The default message can be overridden with the |ArgumentParser.usage|
method::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build()
                .usage("${prog} [OPTIONS]");
        parser.addArgument("--foo").nargs("?").help("foo help");
        parser.addArgument("bar").nargs("+").help("bar help");
        Namespace res = parser.parseArgsOrFail(args);
    }

.. code-block:: console

    $ java Demo -h
    usage: prog [OPTIONS]

    positional arguments:
      bar                    bar help

    named arguments:
      -h, --help             show this help message and exit
      --foo [FOO]            foo help

The ``${prog}`` literal string in the given usage message will be
replaced with the program name
:ref:`ArgumentParsers-newFor-prog`.

.. _ArgumentParser-version:

ArgumentParser.version()
^^^^^^^^^^^^^^^^^^^^^^^^

The |ArgumentParser.version| method sets the string
describing program version. It will be displayed when
:ref:`Arguments-version` action is used.

The ``${prog}`` literal string in the given string will be replaced
with the program name :ref:`ArgumentParsers-newFor-prog`.

.. _ArgumentParser-addArgument:

The ArgumentParser.addArgument() method
---------------------------------------

|ArgumentParser.addArgument| method creates new :javadoc:`inf.Argument`
object and adds it to ArgumentParser's internal memory and returns the
object to the user code. :javadoc:`inf.Argument` object defines how a
single command line argument should be parsed.
|ArgumentParser.addArgument| method receives
:ref:`ArgumentParser-addArgument-nameOrFlags` argument, which is
either a name or a list of option strings, e.g. ``"foo"`` or ``"-f",
"--foo"``.  After obtained :javadoc:`inf.Argument` object, several
parameters can be specified using following methods:

* :ref:`Argument-action` - The basic type of action to be taken when
  this argument is encountered at the command line.

* :ref:`Argument-nargs` - The number of command line arguments that
  should be consumed.

* :ref:`Argument-setConst` - A constant value required by some
  :ref:`Argument-action` and :ref:`Argument-nargs` selections.

* :ref:`Argument-setDefault` - The value produced if the argument is
  absent from the command line.

* :ref:`Argument-type` - The type to which the command line argument
  should be converted.

* :ref:`Argument-choices` - A collection of the allowable values for
  the argument.

* :ref:`Argument-required` - Whether or not the command line option
  may be omitted (named arguments only).

* :ref:`Argument-help` - A brief description of what the argument
  does.

* :ref:`Argument-metavar` - A name for the argument in usage messages.

* :ref:`Argument-dest` - The name of the attribute to be added as a
  result of |ArgumentParser.parseArgs| method.

The following sections describe how each of these are used.

.. _Argumentparser-addArgument-nameOrFlags:

nameOrFlags
^^^^^^^^^^^

The |ArgumentParser.addArgument| method must know whether a
named argument, like ``-f`` or ``--foo``, or a positional argument,
like a list of filenames, is expected.  The arguments passed to
|ArgumentParser.addArgument| must therefore be either a series of
flags, or a simple argument name.  For example, a named argument
could be created like::

    parser.addArgument("-f", "--foo");

while a positional argument could be created like::

    parser.addArgument("bar");

When |ArgumentParser.parseArgs| is called, named arguments will
be identified by the ``-`` prefix (or one of
:ref:`ArgumentParserBuilder-prefixChars` if it is
specified, and the remaining arguments will be assumed to be
positional::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("-f", "--foo");
        parser.addArgument("bar");
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }

.. code-block:: console

    $ java Demo BAR
    Namespace(foo=null, bar=BAR)
    $ java Demo BAR --foo FOO
    Namespace(foo=FOO, bar=BAR)
    $ java Demo --foo FOO
    usage: prog [-h] [-f FOO] bar
    prog: error: too few arguments

.. _Argument-action:

Argument.action()
^^^^^^^^^^^^^^^^^

:javadoc:`inf.Argument` objects associate command line arguments with
actions. These actions can do just about anything with command line
arguments associated with them, though most of the actions simply add
an attribute to the object returned by
|ArgumentParser.parseArgs|.  The |Argument.action| method
specifies how the command line arguments should be handled. The
supported actions follow.

.. _Arguments-store:

Arguments.store()
~~~~~~~~~~~~~~~~~

|Arguments.store| just stores the argument's value. This is
the default action. For example::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("-f", "--foo");
        System.out.println(parser.parseArgs(args));
    }

.. code-block:: console

    $ java Demo --foo 1
    Namespace(foo=1)

.. _Arguments-storeConst:

Arguments.storeConst()
~~~~~~~~~~~~~~~~~~~~~~

|Arguments.storeConst| stores the value specified by the
:ref:`Argument-setConst`. (Note that by default const value is the
rather unhelpful ``null``.)  The |Arguments.storeConst| action is
most commonly used with named arguments that specify sort of
flags. For example::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--foo").action(Arguments.storeConst()).setConst(42);
        System.out.println(parser.parseArgs(args));
    }

.. code-block:: console

    $ java Demo --foo
    Namespace(foo=42)

.. _Arguments-storeBool:

Arguments.storeTrue() and Arguments.storeFalse()
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

|Arguments.storeTrue| and |Arguments.storeFalse| are special cases
of :ref:`Arguments-storeConst` using for storing values ``true`` and
``false`` respectively. In addition, they create default values of
``false`` and ``true`` respectively. For example::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--foo").action(Arguments.storeTrue());
        parser.addArgument("--bar").action(Arguments.storeFalse());
        parser.addArgument("--baz").action(Arguments.storeFalse());
        System.out.println(parser.parseArgs(args));
    }

.. code-block:: console

    $ java Demo --foo --bar
    Namespace(baz=true, foo=true, bar=false)

.. _Arguments-appendAction:

Arguments.append()
~~~~~~~~~~~~~~~~~~

|Arguments.append| stores a list, and appends each argument value to
the list. The list is of type :javatype:`List`. This is useful to
allow an option to be specified multiple times. For example::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--foo").action(Arguments.append());
        System.out.println(parser.parseArgs(args));
    }

.. code-block:: console

    $ java Demo --foo 1 --foo 2
    Namespace(foo=[1, 2])

.. _Arguments-appendConst:

Arguments.appendConst()
~~~~~~~~~~~~~~~~~~~~~~~

|Arguments.appendConst| stores a list, and appends the value specified
by :ref:`Argument-setConst` to the list. (Note that the const value
defaults to ``null``.) The list is of type :javatype:`List`. The
|Arguments.appendConst| action is typically useful when multiple
arguments need to store constants to the same list. For example::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--str")
            .dest("types")
            .action(Arguments.appendConst())
            .setConst(String.class);
        parser.addArgument("--int")
            .dest("types")
            .action(Arguments.appendConst())
            .setConst(Integer.class);
        System.out.println(parser.parseArgs(args));
    }

.. code-block:: console

    $ java Demo --str --int
    Namespace(types=[class java.lang.String, class java.lang.Integer])

.. _Arguments-count:

Arguments.count()
~~~~~~~~~~~~~~~~~

|Arguments.count| counts the number of times an option occurs. For
example, this is useful for increasing verbosity levels::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--verbose", "-v").action(Arguments.count());
        Namespace res = parser.parseArgsOrFail(args);
        System.out.println(res);
    }

.. code-block:: console

    $ java Demo -vvv
    Namespace(verbose=3)

.. _Arguments-version:

Arguments.version()
~~~~~~~~~~~~~~~~~~~

|Arguments.version| prints version string specified by
:ref:`ArgumentParser-version` and exists when invoked::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("PROG").build()
            .version("${prog} 2.0");
        parser.addArgument("--version").action(Arguments.version());
        System.out.println(parser.parseArgs(args));
    }

.. code-block:: console

    $ java Demo --version
    PROG 2.0

.. _Arguments-help:

Arguments.help()
~~~~~~~~~~~~~~~~

|Arguments.help| prints help message and exits when invoked::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").addHelp(false)
                .build();
        parser.addArgument("--help").action(Arguments.help());
        System.out.println(parser.parseArgs(args));
    }

.. code-block:: console

    $ java Demo --help
    usage: prog [--help]

    named arguments:
      --help

Custom actions
~~~~~~~~~~~~~~

You can also specify your custom action by implementing
:javadoc:`inf.ArgumentAction` interface. For example::

    private static class FooAction implements ArgumentAction {

        @Override
        public void run(ArgumentParser parser, Argument arg,
                Map<String, Object> attrs, String flag, Object value)
                throws ArgumentParserException {
            System.out.printf("%s '%s' %s\n", attrs, value, flag);
            attrs.put(arg.getDest(), value);

        }

        @Override
        public void onAttach(Argument arg) {
        }

        @Override
        public boolean consumeArgument() {
            return true;
        }
    }

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        FooAction fooAction = new FooAction();
        parser.addArgument("--foo").action(fooAction);
        parser.addArgument("bar").action(fooAction);
        System.out.println(parser.parseArgs(args));
    }

.. code-block:: console

    $ java Demo  1 --foo 2
    {foo=null, bar=null} '1' null
    {foo=null, bar=1} '2' --foo
    Namespace(foo=2, bar=1)

.. _Argument-nargs:

Argument.nargs()
^^^^^^^^^^^^^^^^

:javadoc:`inf.ArgumentParser` objects usually associate a single
command line argument with a single action to be taken. The
|Argument.nargs| associate different number of command line arguments
with a single action. The supported values are:

* ``N`` (an integer). ``N`` arguments from the command line will be
  gathered into a :javatype:`List`. For example::

       public static void main(String[] args) throws ArgumentParserException {
           ArgumentParser parser = ArgumentParsers.newFor("prog").build();
           parser.addArgument("--foo").nargs(2);
           parser.addArgument("bar").nargs(1);
           System.out.println(parser.parseArgs(args));
       }

  .. code-block:: console

       $ java Demo c --foo a b
       Namespace(foo=[a, b], bar=[c])

  Note that ``nargs(1)`` produces a list of one item. This is different
  from the default, in which the item is produced by itself.

* ``"?"``.  One argument will be consumed from the command line if
  possible, and produced as a single item. If no command line argument
  is present, the value from :ref:`Argument-setDefault` will be
  produced. Note that for named arguments, there is an additional
  case - the option string is present but not followed by a command
  line argument. In this case the value from :ref:`Argument-setConst`
  will be produced. Some examples to illustrate this::

       public static void main(String[] args) throws ArgumentParserException {
           ArgumentParser parser = ArgumentParsers.newFor("prog").build();
           parser.addArgument("--foo").nargs("?").setConst("c").setDefault("d");
           parser.addArgument("bar").nargs("?").setDefault("d");
           System.out.println(parser.parseArgs(args));
       }

  .. code-block:: console

       $ java Demo XX --foo YY
       Namespace(foo=YY, bar=XX)
       $ java Demo XX --foo
       Namespace(foo=c, bar=XX)
       $ java Demo
       Namespace(foo=d, bar=d)

  One of the more common usage of ``nargs("?")`` is to allow
  optional input and output files::

       public static void main(String[] args) throws ArgumentParserException {
           ArgumentParser parser = ArgumentParsers.newFor("prog").build();
           parser.addArgument("infile").nargs("?").type(FileInputStream.class)
                   .setDefault(System.in);
           parser.addArgument("outfile").nargs("?").type(PrintStream.class)
                   .setDefault(System.out);
           System.out.println(parser.parseArgs(args));
       }

  .. code-block:: console

       $ java Demo input.txt output.txt
       Namespace(infile=java.io.FileInputStream@4ce86da0, outfile=java.io.PrintStream@2f754ad2)
       $ java Demo
       Namespace(infile=java.io.BufferedInputStream@e05d173, outfile=java.io.PrintStream@1ff9dc36)

  It is not obvious that outfile points to output.txt from the abolve
  output, but it is actually PrintStream to outfile.txt.

* ``"*"``. All command line arguments present are gathered into a
  :javatype:`List`. Note that it generally does not make sense to have
  more than one positional argument with ``nargs("*")``, but multiple
  optional arguments with ``nargs("*")`` is possible. For example::

       public static void main(String[] args) throws ArgumentParserException {
           ArgumentParser parser = ArgumentParsers.newFor("prog").build();
           parser.addArgument("--foo").nargs("*");
           parser.addArgument("--bar").nargs("*");
           parser.addArgument("baz").nargs("*");
           System.out.println(parser.parseArgs(args));
       }

  .. code-block:: console

       $ java Demo
       Namespace(baz=[], foo=null, bar=null)
       $ java Demo a b --foo x y --bar 1 2
       Namespace(baz=[a, b], foo=[x, y], bar=[1, 2])

* ``"+"``. Just like ``"*"``, all command line arguments present are
  gathered into a :javatype:`List`. Additionally, an error message
  will be generated if there wasn't at least one command line argument
  present. For example::

       public static void main(String[] args) {
           ArgumentParser parser = ArgumentParsers.newFor("prog").build();
           parser.addArgument("foo").nargs("+");
           try {
               System.out.println(parser.parseArgs(args));
           } catch (ArgumentParserException e) {
               parser.handleError(e);
           }
       }

  .. code-block:: console

       $ java Demo a b
       Namespace(foo=[a, b])
       $ java Demo
       usage: prog [-h] foo [foo ...]
       prog: error: too few arguments

If |Argument.nargs| is not used, the number of arguments consumed is
determined by the :ref:`Argument-action`.  Generally this means a
single command line argument will be consumed and a single item(not a
:javatype:`List`) will be produced.  Please note that |Argument.nargs|
are ignored if one of :ref:`Arguments-storeConst`,
:ref:`Arguments-appendConst`, :ref:`Arguments-storeBool` is
provided. More specifically, subclass of :javadoc:`inf.ArgumentAction`
whose :javafunc:`consumeArgument()` returns ``false`` ignores
|Argument.nargs|.

In argparse4j 0.5.0 or earlier, ``nargs("*")`` or ``nargs("+")`` for
positional argument greedily consume all available positional
arguments.  For example, if we have the following program::

    public static void main(String[] args) {
        ArgumentParser ap = ArgumentParsers.newArgumentParser("prog");
        ap.addArgument("foo").nargs("*");
        ap.addArgument("bar");
        ap.parseArgsOrFail(args);
    }

If we give 1, 2, 3, 4, and 5 as command-line arguments, ``foo``
consumes everything.  Because ``bar`` is required, the program will
show error "too few arguments".  Since argparse4j 0.6.0,
``nargs("*")`` or ``nargs("+")`` for positional argument leave
arguments to the remaining positional arguments to satisfy them with
the minimum number of arguments.  In the above example, ``foo`` now
consumes only 1, 2, 3, and 4, and leaves 5 to ``bar``, because ``bar``
is required argument, and consumes just 1 argument.

.. _Argument-setConst:

Argument.setConst()
^^^^^^^^^^^^^^^^^^^

The |Argument.setConst| is used to hold constant values that are not
read from the command line but are required for the various actions.
The two most common uses of it are:

* When :ref:`Arguments-storeConst` or :ref:`Arguments-appendConst` are
  specified.  These actions add the value spcified by
  |Argument.setConst| to one of the attributes of the object
  returned by |ArgumentParser.parseArgs|.  See the
  :ref:`Argument-action` for examples.

* When |ArgumentParser.addArgument| is called with option strings
  (like ``-f`` or ``--foo``) and ``nargs("?")`` is used.  This creates
  a named argument that can be followed by zero or one command
  line argument. When parsing the command line, if the option string
  is encountered with no command line argument following it, the value
  specified by |Argument.setConst| will be assumed instead.  See the
  :ref:`Argument-nargs` description for examples.  The const value
  defauls to ``null``.

.. _Argument-setDefault:

Argument.setDefault()
^^^^^^^^^^^^^^^^^^^^^

All named arguments and some positional arguments may be omitted at
the command line.  The |Argument.setDefault| specifies what
value should be used if the command line argument is not present. The
default value defaults to ``null``. For named arguments, the
default value is used when the option string was not present at the
command line::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--foo").setDefault(42);
        System.out.println(parser.parseArgs(args));
    }

.. code-block:: console

    $ java Demo --foo 2
    Namespace(foo=2)
    $ java Demo
    Namespace(foo=42)

For positional arguments with ``nargs("?")`` or ``nargs("*")``, the
default value is used when no command line argument was present::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("foo").nargs("?").setDefault(42);
        System.out.println(parser.parseArgs(args));
    }

.. code-block:: console

    $ java Demo a
    Namespace(foo=a)
    $ java Demo
    Namespace(foo=42)

Providing :javafield:`Arguments.SUPPRESS` causes no attribute to be
added if the command line argument was not present::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--foo").setDefault(Arguments.SUPPRESS);
        System.out.println(parser.parseArgs(args));
    }

.. code-block:: console

    $ java Demo
    Namespace()
    $ java Demo --foo 1
    Namespace(foo=1)

.. _Argument-type:

Argument.type()
^^^^^^^^^^^^^^^

By default, :javadoc:`inf.ArgumentParser` objects read command line
arguments in as simple strings. However, quite often the command line
string should instead be interpreted as another type, like a
:javatype:`Float` or :javatype:`Integer`. The |Argument.type| allows
any necessary type-checking and type conversions to be performed.  The
Classes which have :javafunc:`valueOf()` static method with 1 String
argument or a constructor with 1 String argument can be passed
directly::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("foo").type(Integer.class);
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }

.. code-block:: console

    $ java Demo 100
    Namespace(foo=100)

As a convenience, if one of following primitive types
(``boolean.class``, ``byte.class``, ``short.class``, ``int.class``,
``long.class``, ``float.class`` and ``double.class``) is specified, it
is converted to its wrapped type counterpart. For example, if
``int.class`` is given, it is automatically converted to
``Integer.class``.

Passing ``Boolean.class`` to |Argument.type| has a caveat.  Since it
relies on ``Boolean.valueOf`` method, any string which matches "true"
in case-insensitive fashion is converted to ``Boolean.TRUE``, and
other strings are converted to ``Boolean.FALSE``::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build()
                .defaultHelp(true);
        parser.addArgument("-f").type(Boolean.class);

        Namespace res = parser.parseArgsOrFail(args);
        System.out.printf("f=%b\n", res.get("f"));
    }

.. code-block:: console

    $ java Demo -f TRue
    f=true
    $ java Demo -f foo
    f=false

If more strict boolean conversion is desirable, use
:javadocfunc:`impl.Arguments.booleanType()`.  It only allows input
string ``true`` as true value, and ``false`` as false value.  Otherwise,
reports error::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build()
                .defaultHelp(true);
        parser.addArgument("-f").type(Arguments.booleanType());

        Namespace res = parser.parseArgsOrFail(args);
        System.out.printf("f=%b\n", res.get("f"));
    }


.. code-block:: console

    $ java Demo -f true
    f=true
    $ java Demo -f TRue
    usage: prog [-h] [-f {true,false}]
    prog: error: argument  -f:  could  not  convert  'TRue'  (choose from {true,
    false})
    $ java Demo -f foo
    usage: prog [-h] [-f {true,false}]
    prog: error: argument  -f:  could  not  convert  'foo'  (choose  from {true,
    false})

If application wants to change the valid input strings which can be
converted to true/false values, use
:javadocfunc:`impl.Arguments.booleanType(java.lang.String,java.lang.String)`.
For example, to use ``yes``, and ``no`` as true and false values
respectively instead of ``true`` and ``false``::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build()
                .defaultHelp(true);
        parser.addArgument("-f").type(Arguments.booleanType("yes", "no"));

        Namespace res = parser.parseArgsOrFail(args);
        System.out.printf("f=%b\n", res.get("f"));
    }

.. code-block:: console

    $ java Demo -f yes
    f=true
    $ java Demo -f no
    f=false
    $ java Demo -f true
    usage: prog [-h] [-f {yes,no}]
    prog: error: argument -f: could not convert 'true' (choose from {yes,no})

The |Argument.type| can accept enums.  Since enums have limited number
of members, type conversion effectively acts like a choice from
members. For example::

    enum Enums {
        FOO, BAR, BAZ
    }

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("-x").type(Enums.class);
        try {
            Namespace res = parser.parseArgs(args);
            System.out.println(res);
            Enums x = (Enums) res.get("x");
            System.out.printf("x=%s\n", x.name());
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }

.. code-block:: console

    $ java Demo -x BAR
    Namespace(x=BAR)
    x=BAR
    $ java Demo -h
    usage: prog [-h] [-x X]

    named arguments:
      -h, --help             show this help message and exit
      -x X

The available enum values are automatically used as metavar, if
metavar and choices are not explicitly set by application::

    parser.addArgument("-x").type(Enums.class);

.. code-block:: console

    $ java Demo -h
    usage: prog [-h] [-x {FOO,BAR,BAZ}]

    named arguments:
      -h, --help             show this help message and exit
      -x {FOO,BAR,BAZ}

To limit enum values to choose from, specify them in
|Argument.choices|::

    parser.addArgument("-x").type(Enums.class).choices(Enums.FOO, Enums.BAZ);

.. code-block:: console

    $ java Demo -h
    usage: prog [-h] [-x {FOO,BAZ}]

    named arguments:
      -h, --help             show this help message and exit
      -x {FOO,BAZ}

There is a caveat when ``Enum.toString()`` method is overridden.  For
instance::

    enum Lang {
        PYTHON, JAVA, CPP {
            @Override
            public String toString() {
                return "C++";
            }
        }
    }

    ...

    parser.addArgument("--lang").type(Lang.class).choices(Lang.values());

We override ``toString()`` method of enum ``CPP``.  The help message
prints fine::

    usage: prog [-h] [--lang {PYTHON,JAVA,C++}]

But when we supply "C++" as parameter to ``--lang``, argparse4j
complains like so::

    prog: error: argument --lang:  could  not  convert  'C++'  to  Lang (No enum
    constant Demo.Lang.C++)

This is because |Argument.type| does not take into account
``toString()`` method override, and it still accepts "CPP" as
parameter (e.g., ``--lang CPP``).  We could not fix treatment of enum
within |Argument.type|, since it could break existing code.  So, we
introduced |Arguments.enumStringType| method (it returns object
:javadoc:`impl.type.EnumStringArgumentType` which implements
:javadoc:`inf.ArgumentType` we will talk abount soon).  It uses solely
``toString()`` method when converting String to enum value.  If we use
this new type instead::

    parser.addArgument("--lang")
            .type(Arguments.enumStringType(Lang.class));

Passing ``--lang "C++"`` just works as expected.
Please note that ``--lang CPP`` no longer works in this case.

To use case-insensitive matching of enum value names, use either
|Arguments.caseInsensitiveEnumType| (uses ``name()``) or
|Arguments.caseInsensitiveEnumStringType| (uses ``toString()``). Note
that ``Locale.ROOT`` is used for case-insensitive comparison to
ensure that correct parsing of arguments is not dependent on the
locale of the parser.

The |Argument.type| has a version which accepts an object which
implements :javadoc:`inf.ArgumentType` interface::

    private static class PerfectSquare implements ArgumentType<Integer> {

        @Override
        public Integer convert(ArgumentParser parser, Argument arg, String value)
                throws ArgumentParserException {
            try {
                int n = Integer.parseInt(value);
                double sqrt = Math.sqrt(n);
                if (sqrt != (int) sqrt) {
                    throw new ArgumentParserException(String.format(
                            "%d is not a perfect square", n), parser);
                }
                return n;
            } catch (NumberFormatException e) {
                throw new ArgumentParserException(e, parser);
            }
        }
    }

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("foo").type(new PerfectSquare());
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }

.. code-block:: console

    $ java Demo 9
    Namespace(foo=9)
    $ java Demo 7
    usage: prog [-h] foo
    prog: error: 7 is not a perfect square

The :ref:`Argument-choices` may be more convenient for type checkers
that simply check against a range of values::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("foo").type(Integer.class)
                .choices(Arguments.range(5, 10));
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }

.. code-block:: console

    $ java Demo 7
    Namespace(foo=7)
    $ java Demo 11
    usage: prog [-h] foo
    prog: error: foo expects value in range [5, 10], inclusive

See :ref:`Argument-choices` for more details.

In some cases, type itself may infer metavar.  In that case, it is
more convenient to get metavar from type instead of setting metavar
for each argument.  To achieve this, if
:javadoc:`inf.MetavarInference` is implemented as well, it can infer
metavar through its interface method.  We mentioned that special
handling of ``Boolean.class`` for default metavar in
:ref:`Argument-metavar` section.  It is implemented using
:javadoc:`inf.MetavarInference`.  Here is an example of implementation
of :javadocfunc:`inf.MetavarInference.inferMetavar()` from
:javadoc:`impl.ReflectArgumentType`::

    @Override
    public String[] inferMetavar() {
        if (!Boolean.class.equals(type_)) {
            return null;
        }

        return new String[] { TextHelper.concat(
                new String[] { "true", "false" }, 0, ",", "{", "}") };
    }

The name of types in messages can be localized. Names for the
primitive types (and their wrappers) are provided. Names for custom
types are looked up using the following methods. The first name that
is found is returned:

1. The key ``displayName`` is looked up in resource bundle
   ``<fully-qualified type name>-argparse4j``. For examaple: Resource
   bundle ``com/example/CustomType-argparse4j*.properties`` is used
   for ``com.example.CustomType``. 
#. The simple name of the class of the type prepended with ``type.``
   is used as a key to look up the name in the resource bundle of
   argparse4j. This method provides the names for primitive type
   wrappers. For example: The key ``type.Integer`` is used  for
   ``java.lang.Integer``.
#. The simple name of the class of the custom type.

Note that if your custom type has the same simple name as a wrapper
for a primitive type, the localized name of that wrapper will also be
used for your custom type.

.. _Argument-choices:

Argument.choices()
^^^^^^^^^^^^^^^^^^

Some command line arguments should be selected from a restricted set
of values. These can be handled by passing a list of objects to
|Argument.choices|. When the command line is parsed, argument values
will be checked, and an error message will be displayed if the
argument was not one of the accepted values::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("foo").choices("a", "b", "c");
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }

.. code-block:: console

    $ java Demo c
    Namespace(foo=c)
    $ java Demo X
    usage: prog [-h] {a,b,c}
    prog: error: argument foo: invalid choice: 'X' (choose from {a,b,c})

Note that inclusion in the choices list is checked after any type
conversions have been performed. If a list of value is not enough, you
can create your own by subclassing :javadoc:`inf.ArgumentChoice`. For
example, argparse4j provides |Arguments.range| to check whether an
integer is in specified range::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("foo").type(Integer.class)
                .choices(Arguments.range(1, 10));
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }


.. code-block:: console

    $ java Demo 1
    Namespace(foo=1)
    $ java Demo 11
    usage: prog [-h] foo
    prog: error: foo expects value in range [1, 10], inclusive

Please pay attention to the type specified in :ref:`Argument-type` and
type in |Argument.choices|. If they are not compatible, subclass of
:javatype:`RuntimeException` will be thrown.


.. _Argument-required:

Argument.required()
^^^^^^^^^^^^^^^^^^^

In general, the :javadoc:`inf.ArgumentParser` assumes that flags like
``-f`` and ``--bar`` indicate named arguments, which can always
be omitted at the command line. To make an option required, ``true``
can be specified for |Argument.required|::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--foo").required(true);
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }

.. code-block:: console

    $ java Demo --foo BAR
    Namespace(foo=BAR)
    $ java Demo
    usage: prog [-h] --foo FOO
    prog: error: argument --foo is required

As the example shows, if an option is marked as required,
|ArgumentParser.parseArgs| will report an error if that option is not
present at the command line.  |Argument.required| will be ignored for
positional arguments.

..  Note::

  Required options are generally considered bad form because users
  expect options to be optional, and thus they should be avoided when
  possible.

.. _Argument-help:

Argument.help()
^^^^^^^^^^^^^^^

|Argument.help| method can take string containing a brief description
of the argument. When a user requests help (usually by using ``-h`` or
``--help`` at the command line), these help descriptions will be
displayed with each argument::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--foo").action(Arguments.storeTrue())
                .help("foo the bars before frobbling");
        parser.addArgument("bar").nargs("+").help("one of the bars to be frobbled");
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }

.. code-block:: console

    $ java Demo  -h
    usage: prog [-h] [--foo] bar [bar ...]

    positional arguments:
      bar                    one of the bars to be frobbled

    named arguments:
      -h, --help             show this help message and exit
      --foo                  foo the bars before frobbling

The help strings are used as is: no special string replacement will
not be done.

The argparse4j supports silencing the help entry for certain options,
by passing :javafield:`Arguments.SUPPRESS` to |Argument.help| method::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--foo").help(Arguments.SUPPRESS);
        try {
            Namespace ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }

.. code-block:: console

   $ java Demo -h
   usage: prog [-h]

   named arguments:
     -h, --help             show this help message and exit

.. _Argument-metavar:

Argument.metavar()
^^^^^^^^^^^^^^^^^^

When :javadoc:`inf.ArgumentParser` generates help messages, it need
some way to referer to each expected argument. By default,
:javadoc:`inf.ArgumentParser` objects use the "dest" value (see
:ref:`Argument-dest` about "dest" value) as the "name" of each object.
If ``Boolean.class`` is given to |Argument.type|, and if no metavar and
no choices are set, ``{true,false}`` is used as metavar automatically
for convenience.
Similarly, if enum type is given, and if no metavar and no choices are set,
a metavar containing their all names is automatically used for convenience
(these names are from ``Enum.names()`` instead of ``Enum.toString()``).
By default, for positional arguments, the dest value is used directly,
and for named arguments, the dest value is uppercased. So, a single
positional argument with ``dest("bar")`` will be referred to as
``bar``. A single named argument ``--foo`` that should be followed
by a single command line argument will be referred to as ``FOO``. For
example::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--foo");
        parser.addArgument("bar");
        parser.printHelp();
    }

.. code-block:: console

    $ java Demo
    usage: prog [-h] [--foo FOO] bar

    positional arguments:
      bar

    named arguments:
      -h, --help             show this help message and exit
      --foo FOO

An alternative name can be specified with |Argument.metavar|
method::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--foo").metavar("YY");
        parser.addArgument("bar").metavar("XX");
        parser.printHelp();
    }

.. code-block:: console

    $ java Demo
    usage: prog [-h] [--foo YY] XX

    positional arguments:
      XX

    named arguments:
      -h, --help             show this help message and exit
      --foo YY

Note that |Argument.metavar| method only changes the displayed name -
the name of the attribute in the object returned by
|ArgumentParser.parseArgs| method is still determined by the dest
value.  Different values of :ref:`Argument-nargs` may cause the
metavar to be used multiple times. Providing multiple values to
|Argument.metavar| method specifies a different display for each of
the arguments::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("-x").nargs(2);
        parser.addArgument("--foo").nargs(2).metavar("bar", "baz");
        parser.printHelp();
    }

.. code-block:: console

    $ java Demo
    usage: prog [-h] [-x X X] [--foo bar baz]

    named arguments:
      -h, --help             show this help message and exit
      -x X X
      --foo bar baz

If the number of values specified in |Argument.metavar| is not
sufficient for the number of arguments given in :ref:`Argument-nargs`,
the last value of metavar is repeated.

.. _Argument-dest:

Argument.dest()
^^^^^^^^^^^^^^^

Most :javadoc:`inf.ArgumentParser` actions add some values as an
attribute of the object returned by |ArgumentParser.parseArgs|
method. The name of this attribute is determined by "dest". For
positional arguments, dest is normally supplied as the first argument
to |ArgumentParser.addArgument| method, with any internal ``-`` converted
to ``_``::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("bar");
        parser.addArgument("foo-bar");
        System.out.println(parser.parseArgs(args));
    }

.. code-block:: console

    $ java Demo XX YY
    Namespace(bar=XX, foo_bar=YY)

For named arguments, the value of dest is normally inferred from
the option strings. :javadoc:`inf.ArgumentParser` generates the value
of dest by taking the first long option string and stripping away the
initial ``--`` string. If no long option strings were supplied, dest
will be derived from the first short option string by stripping the
initial ``-`` character. Any internal ``-`` characters will be
converted to ``_``. The example below illustrate this behavior::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("-f", "--foo-bar", "--foo");
        parser.addArgument("-x", "-y");
        System.out.println(parser.parseArgs(args));
    }


.. code-block:: console

    $ java Demo  -f 1 -x 2
    Namespace(x=2, foo_bar=1)
    $ java Demo  --foo 1 -y 2
    Namespace(x=2, foo_bar=1)

|Argument.dest| method allows a custom attribute name to be provided::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--foo").dest("bar");
        System.out.println(parser.parseArgs(args));
    }

.. code-block:: console

    $ java Demo --foo XX
    Namespace(bar=XX)

.. _ArgumentParser-parseArgs:

The ArgumentParser.parseArgs() method
-------------------------------------

|ArgumentParser.parseArgs| method converts argument strings to objects
and populates :javadoc:`inf.Namespace` object with these values. The
populated :javadoc:`inf.Namespace` object is returned.  Previous calls
to |ArgumentParser.addArgument| method determine exactly what objects
are created and how they are assigned. See the documentation for
:ref:`Adding-arguments` for details.

:javadoc:`inf.ArgumentParser` also provides a way to populate
attributes other than using :javadoc:`inf.Namespace` object. See
:ref:`Namespace` for details.

Option value syntax
^^^^^^^^^^^^^^^^^^^

|ArgumentParser.parseArgs| method supports several ways of specifying
the value of an option (if it takes one). In the simplest case, the
option and its value are passed as two separate arguments::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("-x");
        parser.addArgument("--foo");
        System.out.println(parser.parseArgs(args));
    }

.. code-block:: console

    $ java Demo -x X
    Namespace(foo=null, x=X)
    $ java Demo --foo FOO
    Namespace(foo=FOO, x=null)

For long options (options with names longer than single character),
the option and value can also be passed as a single command line
argument, using ``=`` to separate them:

.. code-block:: console

    $ java Demo --foo=FOO
    Namespace(foo=FOO, x=null)

For short options (options only one character long), the option and
its value can be concatenated:

.. code-block:: console

    $ java Demo -xX
    Namespace(foo=null, x=X)

Several short options can be joined together, using only a single
``-`` prefix, as long as only the last option (or none of them)
requires a value::

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("-x").action(Arguments.storeTrue());
        parser.addArgument("-y").action(Arguments.storeTrue());
        parser.addArgument("-z");
        System.out.println(parser.parseArgs(args));
    }

.. code-block:: console

    $ java Demo -xyzZ
    Namespace(z=Z, y=true, x=true)

Invalid arguments
^^^^^^^^^^^^^^^^^

While parsing the command line, |ArgumentParser.parseArgs| method
checks for a variety of errors, including invalid types, invalid
options, wrong number of positional arguments, etc. When it encounters
such an error, it throws :javadoc:`inf.ArgumentParserException`. The
typical error handling is catch the exception and use
|ArgumentParser.handleError| method to print error message and exit
the program::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--foo").type(Integer.class);
        parser.addArgument("bar").nargs("?");
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }

.. code-block:: console

    $ java Demo --foo spam
    usage: prog [-h] [--foo FOO] [bar]
    prog: error: argument --foo: could not convert 'spam' to integer (32 bits)
    $ java Demo --bar
    usage: prog [-h] [--foo FOO] [bar]
    prog: error: unrecognized arguments: --bar
    $ java Demo spam badger
    usage: prog [-h] [--foo FOO] [bar]
    prog: error: unrecognized arguments: badger

Arguments containing "-"
^^^^^^^^^^^^^^^^^^^^^^^^

|ArgumentParser.parseArgs| method attempts to give errors whenever the
user has cearly made a mistake, but some situations are inherently
ambiguous. For example, the command line argument ``-1`` could either
be an attempt to specify an option or an attempt to provide a
positional argument. The |ArgumentParser.parseArgs| method is cautious
here: positional arguments may only begin with ``-`` if they look like
negative numbers and there are no options in the parser that look like
negative numbers::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("-x");
        parser.addArgument("foo").nargs("?");
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }

.. code-block:: console

    $ # no negative number options, so -1 is a positional argument
    $ java Demo -x -1
    Namespace(foo=null, x=-1)
    $ # no negative number options, so -1 and -5 are positional arguments
    $ java Demo -x -1 -5
    Namespace(foo=-5, x=-1)

::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("-1").dest("one");
        parser.addArgument("foo").nargs("?");
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }


.. code-block:: console

    $ # negative number options present, so -1 is an option
    $ java Demo -1 X
    Namespace(one=X, foo=null)
    $ # negative number options present, so -2 is an option
    $ java Demo -2
    usage: prog [-h] [-1 ONE] [foo]
    prog: error: unrecognized arguments: -2
    $ # negative number options present, so both -1s are options
    $ java Demo -1 -1
    usage: prog [-h] [-1 ONE] [foo]
    prog: error: argument -1: expected one argument

If you have positional arguments that must begin with ``-`` and don't
look like negative numbers, you can insert the pseudo-argument ``--``
which tells |ArgumentParser.parseArgs| method that everything after
that is a positional argument:

.. code-block:: console

    $ java Demo -- -f
    Namespace(one=null, foo=-f)


Please note that whatever
:ref:`ArgumentParserBuilder-prefixChars` is, pseudo-argument is
``--``.

After ``--``, sub-command cannot be recognized.

Argument abbreviations
^^^^^^^^^^^^^^^^^^^^^^

The |ArgumentParser.parseArgs| method allows long options to be
abbreviated if the abbreviation is unambiguous::


    public static void main(String[] args) {
        ArgumentParser ap = ArgumentParsers.newFor("prog").build();
        ap.addArgument("-bacon");
        ap.addArgument("-badger");
        Namespace res = ap.parseArgsOrFail(args);
        System.out.println(res);
    }

.. code-block:: console

    $ java Demo -bac MMM
    Namespace(bacon=MMM, badger=null)
    $ java Demo -bad WOOD
    Namespace(bacon=null, badger=WOOD)
    $ java Demo -ba BA
    usage: prog [-h] [-bacon BACON] [-badger BADGER]
    prog: error: ambiguous option: -ba could match -bacon, -badger

An error is produced for arguments that could produce more than one
options.

.. _Namespace:

The Namespace object
--------------------

:javadoc:`inf.Namespace` object is used to store attributes as a
result of |ArgumentParser.parseArgs| method. It is just a wrapper to
:javatype:`Map` and several shortcut getter methods are provided. The
actual attributes are stored in :javatype:`Map` object and can be
retrieved using |Namespace.getAttrs| method.

You don't have to use :javadoc:`inf.Namespace` object.  You can
directly populate attributes to your :javatype:`Map` object using
|ArgumentParser.parseArgs| method.

You can also assign values to user defined object. In this case, you
can use :javadoc:`annotation.Arg` annotation to designate where the
attribute to be stored.  To specify the name of attribute to assign,
use :javatype:`Arg.dest`; if it is not specified the name of the
attribute or the method will be used instead.  For example::

    private static class Option {

        @Arg(dest = "filename")
        public String filename;

        @Arg(dest = "rows")
        public int matrix[][];

        @Arg
        public String url;
    }

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--rows").type(Integer.class).nargs("+")
                .action(Arguments.append()).metavar("N");
        parser.addArgument("--filename");
        parser.addArgument("--url");

        Option opt = new Option();
        try {
            parser.parseArgs(args, opt);
            System.out.println("outusername=" + opt.filename);
            System.out.println("outurl=" + opt.url);
            int rows = opt.matrix.length;
            for (int i = 0; i < rows; ++i) {
                int cols = opt.matrix[i].length;
                for (int j = 0; j < cols; ++j) {
                    System.out.printf("%d\t", opt.matrix[i][j]);
                }
                System.out.println();
            }
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }

.. code-block:: console

   $ java Demo --rows 1 2 3 --rows 4 5 6 --filename out --url http://example.com
   outusername=out
   http://example.com
   1    2       3
   4    5       6

As shown above, argparse4j supports simple :javatype:`List` to array
conversion.  This is useful if you want primitive int array instead of
:javatype:`List` of Integers.

Other utilities
---------------

.. _Sub-commands:

Sub-commands
^^^^^^^^^^^^

Many programs split up their functionality into a number of
sub-commands, for example, the git program can invoke sub-commands
like ``git stash``, ``git checkout`` and ``git commit``. Splitting up
functionality this way can be a particularly good idea when a program
performs several different functions which requires different kinds of
command-line arguments. :javadoc:`inf.ArgumentParser` supports the
creation of such sub-commands with the |ArgumentParser.addSubparsers|
method.  |ArgumentParser.addSubparsers| method is normally called with
no arguments and returns :javadoc:`inf.Subparsers` object. This object
has |Subparsers.addParser| method, which takes a command name and
returns :javadoc:`inf.Subparser` object. |Subparsers.addParser| method
can take *prefixChars* argument just like
:ref:`ArgumentParserBuilder-prefixChars`. If a version of
|Subparsers.addParser| method without *prefixChars* is used,
*prefixChars* is inherited from main parser. Some example usage::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--foo").action(Arguments.storeTrue()).help("foo help");
        Subparsers subparsers = parser.addSubparsers().help("sub-command help");

        Subparser parserA = subparsers.addParser("a").help("a help");
        parserA.addArgument("bar").type(Integer.class).help("bar help");

        Subparser parserB = subparsers.addParser("b").help("b help");
        parserB.addArgument("--baz").choices("X", "Y", "Z").help("baz help");
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }

.. code-block:: console

    $ java Demo a 12
    Namespace(foo=false, bar=12)
    $ java Demo --foo b --baz Z
    Namespace(baz=Z, foo=true)

Note that the object returned by |ArgumentParser.parseArgs| method
will only contain attributes for the main parser and the subparser
that was selected by the command line (and not any other
subparsers). So in the example above, when the ``a`` command is
specified, only the ``foo`` and ``bar`` attributes are present, and
when the ``b`` command is specified, only ``foo`` and ``baz``
attributes are present.  Similarly, when a help message is requested
from a subparser, only the help for that particular parser will be
printed. The help message will not include parent parser or sibling
parser messages (A help message for each subparser command, however,
can be given using |Subparser.help| method.):

.. code-block:: console

    $ java Demo --help
    usage: prog [-h] [--foo] {a,b} ...

    positional arguments:
      {a,b}                  sub-command help
        a                    a help
        b                    b help

    named arguments:
      -h, --help             show this help message and exit
      --foo                  foo help

    $ java Demo a --help
    usage: prog a [-h] bar

    positional arguments:
      bar                    bar help

    named arguments:
      -h, --help             show this help message and exit

    $ java Demo b --help
    usage: prog b [-h] [--baz BAZ]

    named arguments:
      -h, --help             show this help message and exit
      --baz BAZ              baz help

:javadoc:`inf.Subparsers` also has |Subparsers.title| method and
|Subparsers.description| method. When either is present, the
subparser's commands will appear in their own group in the help
output. For example::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        Subparsers subparsers = parser.addSubparsers()
                .title("subcommands")
                .description("valid subcommands")
                .help("additional help");
        subparsers.addParser("foo");
        subparsers.addParser("bar");
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }

.. code-block:: console

    $ java Demo -h
    usage: prog [-h] {foo,bar} ...

    named arguments:
      -h, --help             show this help message and exit

    subcommands:
      valid subcommands

      {foo,bar}              additional help

As you can see above, all sub-commands are printed in help message.
It would be good for only 2 or 3 sub-commands, but if there are many
sub-commands, the display will become quite ugly.  In that case,
|Subparsers.metavar| method sets text to use instead of all
sub-command names. For example::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        Subparsers subparsers = parser.addSubparsers().title("subcommands")
                .description("valid subcommands").help("additional help")
                .metavar("COMMAND");
        subparsers.addParser("foo").help("foo help");
        subparsers.addParser("bar").help("bar help");
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }

.. code-block:: console

    $ java Demo -h
    usage: prog [-h] COMMAND ...

    named arguments:
      -h, --help             show this help message and exit

    subcommands:
      valid subcommands

      COMMAND                additional help
        foo                  foo help
        bar                  bar help

The argparse4j supports silencing the help entry for certain
:javadoc:`inf.Subparser`, by passing :javafield:`Arguments.SUPPRESS`
to |Subparser.help| method::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        Subparsers subparsers = parser.addSubparsers()
                .title("subcommands")
                .description("valid subcommands")
                .help("additional help");
        subparsers.addParser("foo");
        subparsers.addParser("bar").help(Arguments.SUPPRESS);
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }

.. code-block:: console

    $ java Demo -h
    usage: prog [-h] {foo} ...

    optional arguments:
      -h, --help             show this help message and exit

    subcommands:
      valid subcommands

      {foo}                  additional help

Furthermore, :javadoc:`inf.Subparser` supports alias names, which
allows multiple strings to refer to the same subparser. This example,
like ``svn``, aliases ``co`` as a shorthand for ``checkout``::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        Subparsers subparsers = parser.addSubparsers();
        Subparser checkout = subparsers.addParser("checkout").aliases("co");
        checkout.addArgument("foo");
        Namespace ns = parser.parseArgsOrFail(args);
        System.out.println(ns);
    }

.. code-block:: console

    $ java Demo co bar
    Namespace(foo=bar)

One particularly effective way of handling sub-commands is to combine
the use of |Subparser.setDefault| method so that each subparser knows
which function it should execute. For example::

    private static interface Accumulate {
        int accumulate(Collection<Integer> ints);
    }

    private static class Sum implements Accumulate {
        @Override
        public int accumulate(Collection<Integer> ints) {
            int sum = 0;
            for (Integer i : ints) {
                sum += i;
            }
            return sum;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }

    private static class Max implements Accumulate {
        @Override
        public int accumulate(Collection<Integer> ints) {
            return Collections.max(ints);
        }

        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        Subparsers subparsers = parser.addSubparsers();
        Subparser parserSum = subparsers.addParser("sum")
                .setDefault("func", new Sum());
        parserSum.addArgument("ints").type(Integer.class).nargs("*");
        Subparser parserMax = subparsers.addParser("max")
                .setDefault("func", new Max());
        parserMax.addArgument("ints").type(Integer.class).nargs("+");
        try {
            Namespace res = parser.parseArgs(args);
            System.out.println(((Accumulate) res.get("func"))
                    .accumulate((Collection<Integer>) res.get("ints")));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }

.. code-block:: console

    $ java Demo  sum 1 3 2
    6
    $ java Demo  max 1 3 2
    3

The alternative way is use |Subparsers.dest| method. With this dest
value, the selected command name is stored as an attribute::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        Subparsers subparsers = parser.addSubparsers().dest("subparser_name");
        Subparser subparser1 = subparsers.addParser("1");
        subparser1.addArgument("-x");
        Subparser subparser2 = subparsers.addParser("2");
        subparser2.addArgument("y");
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }

.. code-block:: console

    $ java Demo 2 frobble
    Namespace(subparser_name=2, y=frobble)

Subparsers allows sub-command names to be abbreviated as long as the
abbreviation is unambiguous, just like long options::


    enum Command {
        CLONE, CLEAN
    };

    public static void main(String[] args) {
        ArgumentParser ap = ArgumentParsers.newFor("prog").build();
        Subparsers subparsers = ap.addSubparsers();
        subparsers.addParser("clone").setDefault("command", Command.CLONE);
        subparsers.addParser("clean").setDefault("command", Command.CLEAN);
        Namespace res = ap.parseArgsOrFail(args);
        System.out.println(res);
    }

.. code-block:: console

    $ java Demo clo
    Namespace(command=CLONE)
    $ java Demo cle
    Namespace(command=CLEAN)
    $ java Demo cl
    usage: prog [-h] {clone,clean} ...
    prog: error: ambiguous command: cl could match clean, clone

An error is produced for arguments that could produce more than one
sub-commands.

fileType()
^^^^^^^^^^

The |Arguments.fileType| will convert an argument to :javatype:`File`
object. It has several convenient verification features such as
checking readability or existence of a given path.  The command-line
programs traditionally accept ``-`` as standard input.  The
|Arguments.fileType| supports this tradition too. To enable this, just
use `acceptSystemIn()` method::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build()
                .defaultHelp(true);
        parser.addArgument("-i", "--in")
                .type(Arguments.fileType().acceptSystemIn().verifyCanRead())
                .setDefault("-");
        parser.addArgument("-o", "--out").type(Arguments.fileType());
        try {
            Namespace ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }

.. code-block:: console

    $ java Demo -h
    usage: prog [-h] [-i IN] [-o OUT]

    named arguments:
      -h, --help             show this help message and exit
      -i IN, --in IN         (default: -)
      -o OUT, --out OUT
    $ java Demo -i not-found
    usage: prog [-h] [-i IN] [-o OUT]
    prog: error: argument -i/--in: Insufficient permissions to read file: 'not-found'

The verifications can be used in any combination, but not all
combinations make sense. The verifications can be split in 2
categories: presence & type, and permissions & properties. You should
always use exactly 1 verification from the presence & type category,
and then you can use 0 or more permission & property verifications:

=================== ============== ====== ======= ============
Permission/property Does not exist Exists Is file Is directory
=================== ============== ====== ======= ============
Is absolute         ●              ●      ●       ●  
------------------- -------------- ------ ------- ------------
Read                               ●      ●       ●  
------------------- -------------- ------ ------- ------------
Write                              ●      ●       ●  
------------------- -------------- ------ ------- ------------
Execute                            ●      ●       ●  
------------------- -------------- ------ ------- ------------
Write parent        ●              ●      ●       ●  
------------------- -------------- ------ ------- ------------
Create              ●                              
=================== ============== ====== ======= ============

From version 0.8.0 it is also possible to specify verification groups,
of which 1 must verify successfully. This is useful where you can
create the needed file or directory yourself if it does not exist yet.
For example::

    <file argument type>
            // The output directory does not exist, but it is possible to create it.
            .verifyNotExists().verifyCanCreate()
            .or()
            // The output directory already exists, and it is possible to write to it.
            .verifyIsDirectory().verifyCanWrite()

Argument groups
^^^^^^^^^^^^^^^

By default, :javadoc:`inf.ArgumentParser` groups command line
arguments into "positional arguments" and "named arguments" when
displaying help messages. When there is a better conceptual grouping
of arguments than this default one, appropriate groups can be created
using the |ArgumentParser.addArgumentGroup|::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        ArgumentGroup group = parser.addArgumentGroup("group");
        group.addArgument("--foo").help("foo help");
        group.addArgument("bar").help("bar help");
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }

.. code-block:: console

    $ java Demo -h
    usage: prog [-h] [--foo FOO] bar

    positional arguments:

    named arguments:
      -h, --help             show this help message and exit

    group:
      --foo FOO              foo help
      bar                    bar help

|ArgumentParser.addArgumentGroup| returns :javadoc:`inf.ArgumentGroup`
object which has |ArgumentGroup.addArgument| just like a
:javadoc:`inf.ArgumentParser`. When an argument is added to the group,
the parser treats it just like a normal argument, but displays the
argument in a separate group for help messages. With the title string
specified in |ArgumentParser.addArgumentGroup| and the description
specified in |ArgumentGroup.description|, you can customize the help
message::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog")
                .addHelp(false).build();
        ArgumentGroup group1 = parser.addArgumentGroup("group1")
                .description("group1 description");
        group1.addArgument("foo").help("foo help");
        ArgumentGroup group2 = parser.addArgumentGroup("group2")
                .description("group2 description");
        group2.addArgument("--bar").help("bar help");
        parser.printHelp();
    }

.. code-block:: console

    $ java Demo
    usage: prog [--bar BAR] foo

    group1:
      group1 description

      foo                    foo help

    group2:
      group2 description

      --bar BAR              bar help

Note that any arguments not in your user defined groups will end up
back in the usual "positional arguments" and "named arguments"
sections.

Mutual exclusion
^^^^^^^^^^^^^^^^

|ArgumentParser.addMutuallyExclusiveGroup| creates a mutually
exclusive group. :javadoc:`inf.ArgumentParser` will make sure that
only one of the arguments in the mutually exclusive group was present
on the command line::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        MutuallyExclusiveGroup group = parser.addMutuallyExclusiveGroup();
        group.addArgument("--foo").action(Arguments.storeTrue());
        group.addArgument("--bar").action(Arguments.storeFalse());
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }

.. code-block:: console

    $ java Demo --foo
    Namespace(foo=true, bar=true)
    $ java Demo --foo --bar
    usage: prog [-h] [--foo | --bar]
    prog: error: argument --bar: not allowed with argument --foo

Specifying ``true`` to |MutuallyExclusiveGroup.required| indicates
that at least one of the mutually exclusive arguments is required::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        MutuallyExclusiveGroup group = parser.addMutuallyExclusiveGroup("group")
                .required(true);
        group.addArgument("--foo").action(Arguments.storeTrue());
        group.addArgument("--bar").action(Arguments.storeFalse());
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }

.. code-block:: console

    $ java Demo
    usage: prog [-h] (--foo | --bar)
    prog: error: one of the arguments --foo --bar is required

The :javadoc:`inf.MutuallyExclusiveGroup` support the title and
description just like :javadoc:`inf.ArgumentGroup` object.  If both
title and description are not specified, the help message for this
group is merged into the other named arguments. With either or both
title and description, the help message is in separate group::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        MutuallyExclusiveGroup group = parser.addMutuallyExclusiveGroup("group");
                .description("group description");
        group.addArgument("--foo").action(Arguments.storeTrue());
        group.addArgument("--bar").action(Arguments.storeFalse());
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }

.. code-block:: console

    $ java Demo -h
    usage: prog [-h] (--foo | --bar)

    named arguments:
      -h, --help             show this help message and exit

    group:
      group description

      --foo
      --bar

Parser defaults
^^^^^^^^^^^^^^^

Most of the time, the attributes of the object returned by
|ArgumentParser.parseArgs| will be fully determined by inspecting the
command line arguments and the argument actions.
|ArgumentParser.setDefault| allows some additional attributes that are
determined without any inspection of the command line to be added::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("foo").type(Integer.class);
        parser.setDefault("bar", 42).setDefault("baz", "badger");
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }

.. code-block:: console

    $ java Demo 736
    Namespace(baz=badger, foo=736, bar=42)

Note that parser-level defaults always override argument-level
defaults::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--foo").setDefault("bar");
        parser.setDefault("foo", "spam");
        try {
            System.out.println(parser.parseArgs(args));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }

.. code-block:: console

    $ java Demo
    Namespace(foo=spam)

Parser-level defaults can be particularly useful when working with
multiple parsers. See :ref:`Sub-commands` for an example of this type.
|ArgumentParser.getDefault| returns the default value for a attribute,
as set by either |Argument.setDefault| or by
|ArgumentParser.setDefault|::

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("prog").build();
        parser.addArgument("--foo").setDefault("badger");
        System.out.println(parser.getDefault("foo"));
    }

.. code-block:: console

    $ java Demo
    badger

Printing help
^^^^^^^^^^^^^

In most typical applications, |ArgumentParser.parseArgs| and
|ArgumentParser.handleError| will take care of formatting and printing
any usage or error messages.  However, several formatting methods are
available:

* |ArgumentParser.printUsage| - Print a brief description of how the
  program should be invoked on the command line.

* |ArgumentParser.printHelp| - Print a help message, including the
  program usage and information about the arguments registered with
  :javadoc:`inf.ArgumentParser`.

Extensions
----------

.. _Java 7:

Java 7
^^^^^^

Since the switch to Java 8 in version 0.9.0, the extensions for Java
7 have been added to the main JAR. The package has not changed, so
when upgrading to 0.9.0 or higher, the only thing that needs to
be done is the removal from the Java 7 extensions JAR from your
dependencies.

The following argument types are available:

``java.nio.file.Path``
  Argument type:
  ``net.sourceforge.argparse4j.ext.java7.PathArgumentType``. The
  no-arg constructor used the default file system. If you want to
  resolve paths for another file system, use the constructor
  accepting a file system. Note that using the non-default file
  system disables all file verification checks.

.. _Hadoop:

Hadoop
^^^^^^

Argument types for classes and interfaces of Hadoop are available in
module ``argparse4j-hadoop``. The following argument types are
available: 

``org.apache.hadoop.fs.Path``
  Argument type:
  ``net.sourceforge.argparse4j.ext.hadoop.PathArgumentType``.

.. |Argument.action| replace:: :javadocfunc:`inf.Argument.action(net.sourceforge.argparse4j.inf.ArgumentAction)`
.. |Argument.choices| replace:: :javadocfunc:`inf.Argument.choices(E...)`
.. |Argument.dest| replace:: :javadocfunc:`inf.Argument.dest(java.lang.String)`
.. |Argument.help| replace:: :javadocfunc:`inf.Argument.help(java.lang.String)`
.. |Argument.metavar| replace:: :javadocfunc:`inf.Argument.metavar(java.lang.String...)`
.. |Argument.nargs| replace:: :javadocfunc:`inf.Argument.nargs(int)`
.. |Argument.required| replace:: :javadocfunc:`inf.Argument.required(boolean)`
.. |Argument.setConst| replace:: :javadocfunc:`inf.Argument.setConst(java.lang.Object)`
.. |Argument.setDefault| replace:: :javadocfunc:`inf.Argument.setDefault(java.lang.Object)`
.. |Argument.type| replace:: :javadocfunc:`inf.Argument.type(java.lang.Class)`
.. |ArgumentGroup.addArgument| replace:: :javadocfunc:`inf.ArgumentGroup.addArgument(java.lang.String...)`
.. |ArgumentGroup.description| replace:: :javadocfunc:`inf.ArgumentGroup.description(java.lang.String)`
.. |ArgumentParser.addArgumentGroup| replace:: :javadocfunc:`inf.ArgumentParser.addArgumentGroup(java.lang.String)`
.. |ArgumentParser.addArgument| replace:: :javadocfunc:`inf.ArgumentParser.addArgument(java.lang.String...)`
.. |ArgumentParser.addMutuallyExclusiveGroup| replace:: :javadocfunc:`inf.ArgumentParser.addMutuallyExclusiveGroup()`
.. |ArgumentParser.addSubparsers| replace:: :javadocfunc:`inf.ArgumentParser.addSubparsers()`
.. |ArgumentParser.defaultHelp| replace:: :javadocfunc:`inf.ArgumentParser.defaultHelp(boolean)`
.. |ArgumentParser.description| replace:: :javadocfunc:`inf.ArgumentParser.description(java.lang.String)`
.. |ArgumentParser.epilog| replace:: :javadocfunc:`inf.ArgumentParser.epilog(java.lang.String)`
.. |ArgumentParser.getDefault| replace:: :javadocfunc:`inf.ArgumentParser.getDefault(java.lang.String)`
.. |ArgumentParser.handleError| replace:: :javadocfunc:`inf.ArgumentParser.handleError(net.sourceforge.argparse4j.inf.ArgumentParserException)`
.. |ArgumentParser.parseArgs| replace:: :javadocfunc:`inf.ArgumentParser.parseArgs(java.lang.String[])`
.. |ArgumentParser.printHelp| replace:: :javadocfunc:`inf.ArgumentParser.printHelp()`
.. |ArgumentParser.printUsage| replace:: :javadocfunc:`inf.ArgumentParser.printUsage()`
.. |ArgumentParser.setDefault| replace:: :javadocfunc:`inf.ArgumentParser.setDefault(java.lang.String,java.lang.Object)`
.. |ArgumentParser.usage| replace:: :javadocfunc:`inf.ArgumentParser.usage(java.lang.String)`
.. |ArgumentParser.version| replace:: :javadocfunc:`inf.ArgumentParser.version(java.lang.String)`
.. |ArgumentParserBuilder.addHelp| replace:: :javadocfunc:`ArgumentParserBuilder.addHelp(boolean)`
.. |ArgumentParserBuilder.build| replace:: :javadocfunc:`ArgumentParserBuilder.build()`
.. |ArgumentParserBuilder.cjkwidthHack| replace:: :javadocfunc:`ArgumentParserBuilder.cjkwidthHack(boolean)`
.. |ArgumentParserBuilder.defaultFormatWidth| replace:: :javadocfunc:`ArgumentParserBuilder.defaultFormatWidth(int)`
.. |ArgumentParserBuilder.includeArgumentNamesAsKeysInResult| replace:: :javadocfunc:`ArgumentParserBuilder.includeArgumentNamesAsKeysInResult(boolean)`
.. |ArgumentParserBuilder.locale| replace:: :javadocfunc:`ArgumentParserBuilder.locale(java.util.Locale)`
.. |ArgumentParserBuilder.noDestConversionForPositionalArgs| replace:: :javadocfunc:`ArgumentParserBuilder.noDestConversionForPositionalArgs(boolean)`
.. |ArgumentParserBuilder.prefixChars| replace:: :javadocfunc:`ArgumentParserBuilder.prefixChars(java.lang.String)`
.. |ArgumentParserBuilder.singleMetavar| replace:: :javadocfunc:`ArgumentParserBuilder.singleMetavar(boolean)`
.. |ArgumentParserBuilder.terminalWidthDetection| replace:: :javadocfunc:`ArgumentParserBuilder.terminalWidthDetection(boolean)`
.. |ArgumentParsers.newFor| replace:: :javadocfunc:`ArgumentParsers.newFor(java.lang.String)`
.. |ArgumentParsers.newForDefaults| replace:: :javadocfunc:`ArgumentParsers.newFor(java.lang.String,net.sourceforge.argparse4j.DefaultSettings)`
.. |Arguments.appendConst| replace:: :javadocfunc:`impl.Arguments.appendConst()`
.. |Arguments.append| replace:: :javadocfunc:`impl.Arguments.append()`
.. |Arguments.caseInsensitiveEnumType| replace:: :javadocfunc:`impl.Arguments.caseInsensitiveEnumType(java.lang.Class)`
.. |Arguments.caseInsensitiveEnumStringType| replace:: :javadocfunc:`impl.Arguments.caseInsensitiveEnumStringType(java.lang.Class)`
.. |Arguments.count| replace:: :javadocfunc:`impl.Arguments.count()`
.. |Arguments.enumStringType| replace:: :javadocfunc:`impl.Arguments.enumStringType(java.lang.Class)`
.. |Arguments.fileType| replace:: :javadocfunc:`impl.Arguments.fileType()`
.. |Arguments.help| replace:: :javadocfunc:`impl.Arguments.help()`
.. |Arguments.range| replace:: :javadocfunc:`impl.Arguments.range(T,T)`
.. |Arguments.storeConst| replace:: :javadocfunc:`impl.Arguments.storeConst()`
.. |Arguments.storeFalse| replace:: :javadocfunc:`impl.Arguments.storeFalse()`
.. |Arguments.storeTrue| replace:: :javadocfunc:`impl.Arguments.storeTrue()`
.. |Arguments.store| replace:: :javadocfunc:`impl.Arguments.store()`
.. |Arguments.version| replace:: :javadocfunc:`impl.Arguments.version()`
.. |MutuallyExclusiveGroup.required| replace:: :javadocfunc:`inf.MutuallyExclusiveGroup.required(boolean)`
.. |Namespace.getAttrs| replace:: :javadocfunc:`inf.Namespace.getAttrs()`
.. |Subparser.dest| replace:: :javadocfunc:`inf.Subparser.dest(java.lang.String)`
.. |Subparser.help| replace:: :javadocfunc:`inf.Subparser.help(java.lang.String)`
.. |Subparser.setDefault| replace:: :javadocfunc:`inf.Subparser.setDefault(java.lang.String,java.lang.Object)`
.. |Subparsers.addParser| replace:: :javadocfunc:`inf.Subparsers.addParser(java.lang.String)`
.. |Subparsers.description| replace:: :javadocfunc:`inf.Subparsers.description(java.lang.String)`
.. |Subparsers.dest| replace:: :javadocfunc:`inf.Subparsers.dest(java.lang.String)`
.. |Subparsers.metavar| replace:: :javadocfunc:`inf.Subparsers.metavar(java.lang.String)`
.. |Subparsers.title| replace:: :javadocfunc:`inf.Subparsers.title(java.lang.String)`
