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
