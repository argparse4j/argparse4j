Migration
=========

To 0.5.0:
---------

The rules for the default :ref:`Argument-dest` for positional arguments has
been made consistent with options, in that the dash (``-``) character is
is converted to an underscore (``_``)

Prior to 0.5.0::

    parser.addArgument("foo-bar");
    Namespace args = parser.parseArgs(args);
    args.get("foo-bar");

Starting from 0.5.0::

    parser.addArgument("foo-bar");
    Namespace args = parser.parseArgs(args);
    args.get("foo_bar");

We encourage the applications to review about this change and update
their code, but it is not feasible, use
``ArgumentParsers.setNoDestConversionForPositionalArgs(true)`` to tell
the library not to do this conversion.

To 0.8.0:
---------

The global variables of ``ArgumentParsers`` have been replaced by a
builder. Using builders multiple differently configured
ArgumentParser instances can be used on the same or other threads
without interfering with each other. 

Prior to 0.8.0::

    ArgumentParsers.setCJKWidthHack(true);
    ArgumentParsers.setNoDestConversionForPositionalArgs(true);
    ArgumentParsers.setSingleMetavar(true);
    ArgumentParsers.setTerminalWidthDetection(true);
    ArgumentParser ap = ArgumentParsers.newArgumentParser("prog", ...);

Starting from 0.8.0::

    ArgumentParser ap = ArgumentParsers.newFor("prog")
            ...
            .cjkWidthHack(true)
            .noDestConversionForPositionalArgs(true);
            .singleMetavar(true);                    
            .terminalWidthDetection(true)
            .build();

The ``ArgumentParsers.newArgumentParser(String, ...)`` methods and
the getters and setters for the global variables have been
deprecated. It is encouraged to port applications to the builder
style creation of an ArgumentParser object.
