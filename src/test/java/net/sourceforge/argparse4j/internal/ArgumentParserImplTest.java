package net.sourceforge.argparse4j.internal;

import static net.sourceforge.argparse4j.impl.Arguments.*;
import static net.sourceforge.argparse4j.test.TestHelper.*;
import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Map;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.annotation.Arg;
import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import net.sourceforge.argparse4j.internal.ArgumentParserImpl.Candidate;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ArgumentParserImplTest {

    private ArgumentParserImpl ap;
    private String[] zeroargs;

    @BeforeClass
    public static void init()
    {
        ArgumentParsers.setTerminalWidthDetection(false);
    }

    @Before
    public void setup() {
        ap = new ArgumentParserImpl("argparse4j");
        zeroargs = new String[]{};
    }

    @Test
    public void testCtor() throws ArgumentParserException {
        ap = new ArgumentParserImpl("prog", false, "+", "@", null);
        assertEquals("prog", ap.getProg());
        assertEquals("+", ap.getPrefixChars());
        assertEquals("@", ap.getFromFilePrefixChars());
        // Check +h can be added because addHelp is false
        ap.addArgument("+h");
    }

    @Test
    public void testParseArgs() throws ArgumentParserException {
        ap.addArgument("--foo");
        ap.addArgument("--bar").nargs("?").setConst("c");
        ap.addArgument("suites").nargs("*");
        Namespace res = ap.parseArgs(("--bar " + "--foo hello "
                + "cake dango mochi").split(" "));
        assertEquals("hello", res.get("foo"));
        assertEquals("c", res.get("bar"));
        assertEquals(list("cake", "dango", "mochi"), res.get("suites"));
    }

    @Test
    public void testRequiredOptarg() throws ArgumentParserException {
        ap.addArgument("--foo").required(true);
        try {
            ap.parseArgs(new String[] {});
            fail();
        } catch (ArgumentParserException e) {
            assertEquals("argument --foo is required", e.getMessage());
        }
    }

    @Test
    public void testEmbeddedValueWithNargsWrongValue() {
        ap.addArgument("--foo").nargs("+").choices("bar", "baz");
        try {
            ap.parseArgs("--foo=abc".split(" "));
            fail();
        } catch(ArgumentParserException e) {
            assertEquals("argument --foo: invalid choice: 'abc' (choose from {bar,baz})",
                         e.getMessage());
        }
    }

    @Test
    public void testRequiredOptargWithSubcommand()
            throws ArgumentParserException {
        ap.addArgument("--foo").required(true);
        ap.addSubparsers().addParser("install");
        try {
            ap.parseArgs("install".split(" "));
        } catch (ArgumentParserException e) {
            assertEquals("argument --foo is required", e.getMessage());
        }
    }

    @Test
    public void testTooFewArgumentForPosarg() throws ArgumentParserException {
        ap.addArgument("foo");
        try {
            ap.parseArgs(new String[] {});
            fail();
        } catch (ArgumentParserException e) {
            assertEquals("too few arguments", e.getMessage());
        }
    }

    @Test
    public void testTooFewArgumentForPosargWithNargs()
            throws ArgumentParserException {
        ap.addArgument("foo").nargs(3);
        try {
            ap.parseArgs(new String[] {});
            fail();
        } catch (ArgumentParserException e) {
            assertEquals("too few arguments", e.getMessage());
        }
    }

    @Test
    public void testAddArgumentWithConflict() throws ArgumentParserException {
        try {
            ap.addArgument("--foo");
            ap.addArgument("--foo");
            fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            ap.addArgument("foo");
            ap.addArgument("foo");
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testParseArgsWithPosargOutOfIndex()
            throws ArgumentParserException {
        ap.addArgument("foo").nargs("*");
        Namespace res = ap.parseArgs(new String[] {});
        assertEquals(list(), res.get("foo"));
    }

    @Test
    public void testParseArgsWithoutNegativeNumberLikeFlag()
            throws ArgumentParserException {
        ap.addArgument("-x");
        ap.addArgument("foo").nargs("?");
        Namespace res = ap.parseArgs("-x -1".split(" "));
        assertEquals("-1", res.get("x"));
        res = ap.parseArgs("-x -1 -5".split(" "));
        assertEquals("-1", res.get("x"));
        assertEquals("-5", res.get("foo"));
    }

    @Test
    public void testParseArgsWithNegativeNumberLikeFlag()
            throws ArgumentParserException {
        ap.addArgument("-1").dest("one");
        ap.addArgument("foo").nargs("?");
        Namespace res = ap.parseArgs("-1 X".split(" "));
        assertEquals("X", res.get("one"));
        try {
            ap.parseArgs("-2".split(" "));
            fail();
        } catch (ArgumentParserException e) {
            assertEquals("unrecognized arguments: '-2'", e.getMessage());
        }
        try {
            ap.parseArgs("-1 -1".split(" "));
            fail();
        } catch (ArgumentParserException e) {
            assertEquals("argument -1: expected one argument", e.getMessage());
        }
    }

    @Test
    public void testParseArgsWithStoreTrueFalse()
            throws ArgumentParserException {
        ap.addArgument("--foo").action(storeTrue());
        ap.addArgument("--bar").action(storeFalse());
        ap.addArgument("--baz").action(storeFalse());
        ap.addArgument("--sid").action(storeTrue());
        Namespace res = ap.parseArgs("--foo --bar".split(" "));
        assertEquals(true, res.get("foo"));
        assertEquals(false, res.get("bar"));
        assertEquals(true, res.get("baz"));
        assertEquals(false, res.get("sid"));
    }

    @Test
    public void testParseArgsWithStoreConst() throws ArgumentParserException {
        ap.addArgument("--foo").action(storeConst()).setConst("const");
        ap.addArgument("bar");
        Namespace res = ap.parseArgs("--foo bar".split(" "));
        assertEquals("const", res.get("foo"));
        assertEquals("bar", res.get("bar"));
    }

    @Test
    public void testParseArgsWithAppend() throws ArgumentParserException {
        ap.addArgument("--foo").action(append()).nargs("*");
        ap.addArgument("--bar").action(append());
        Namespace res = ap.parseArgs("--foo a --foo b --bar c --bar d"
                .split(" "));
        assertEquals(list(list("a"), list("b")), res.get("foo"));
        assertEquals(list("c", "d"), res.get("bar"));
    }

    @Test
    public void testParseArgsWithAppendConst() throws ArgumentParserException {
        ap.addArgument("--foo").action(appendConst()).setConst("X");
        ap.addArgument("bar");
        Namespace res = ap.parseArgs("--foo --foo bar".split(" "));
        assertEquals(list("X", "X"), res.get("foo"));
        assertEquals("bar", res.get("bar"));
    }

    @Test
    public void testParseArgsWithCount() throws ArgumentParserException {
        ap.addArgument("-v", "--verbose").action(count());
        ap.addArgument("--foo");
        Namespace res = ap.parseArgs("-v -vv -vvvv".split(" "));
        assertEquals(7, res.get("verbose"));
    }

    @Test
    public void testParseArgsWithConst() throws ArgumentParserException {
        ap.addArgument("--foo").setConst("X").nargs("?");
        Namespace res = ap.parseArgs("--foo".split(" "));
        assertEquals("X", res.get("foo"));
    }

    @Test
    public void testParseArgsWithQ() throws ArgumentParserException {
        ap.addArgument("--foo").nargs("?").setConst("c").setDefault("d");
        ap.addArgument("bar").nargs("?").setDefault("d");
        Namespace res = ap.parseArgs("XX --foo YY".split(" "));
        assertEquals("YY", res.get("foo"));
        assertEquals("XX", res.get("bar"));
        res = ap.parseArgs("XX --foo".split(" "));
        assertEquals("c", res.get("foo"));
        assertEquals("XX", res.get("bar"));
        res = ap.parseArgs(new String[] {});
        assertEquals("d", res.get("foo"));
        assertEquals("d", res.get("bar"));        
    }

    @Test
    public void testParseArgsWithConcatenatedShortOpts()
            throws ArgumentParserException {
        ap.addArgument("-1").action(storeTrue());
        ap.addArgument("-2");
        ap.addArgument("-3");
        ap.addArgument("-ff");
        ap.addArgument("-f");
        ap.addArgument("-c").action(appendConst()).setConst(true);
        Namespace res = ap.parseArgs("-123=x -ff=a -fx -cccc".split(" "));
        assertEquals(true, res.get("1"));
        assertEquals("3=x", res.get("2"));
        assertEquals("a", res.get("ff"));
        assertEquals("x", res.get("f"));
        assertEquals(list(true, true, true, true), res.get("c"));
        // If last option requires argument but the argument is not
        // embedded in the same term, it must take next term as an
        // argument.
        res = ap.parseArgs("-12 foo".split(" "));
        assertEquals(true, res.get("1"));
        assertEquals("foo", res.get("2"));
        // If -12" " is given in the terminal, program get this
        res = ap.parseArgs(new String[] { "-12 "});
        assertEquals(true, res.get("1"));
        assertEquals(" ", res.get("2"));
        // This is error case because the next term -fx is flag.
        try {
            res = ap.parseArgs("-12 -fx".split(" "));
            fail();
        } catch(ArgumentParserException e) {
            assertEquals("argument -2: expected one argument", e.getMessage());
        }

    }

    @Test
    public void testParseArgsWithStringChoices() throws ArgumentParserException {
        ap.addArgument("--foo").choices("chocolate", "icecream", "froyo");
        Namespace res = ap.parseArgs("--foo icecream".split(" "));
        assertEquals("icecream", res.get("foo"));
        try {
            ap.parseArgs("--foo pudding".split(" "));
            fail("Exception must be thrown");
        } catch (ArgumentParserException e) {
            // success
        }
    }

    @Test
    public void testParseArgsWithNargs() throws ArgumentParserException {
        ap.addArgument("--foo").nargs(2);
        ap.addArgument("bar");
        try {
            ap.parseArgs("--foo=3 4".split(" "));
            fail("Exception must be thrown");
        } catch (ArgumentParserException e) {
            // success
        }
    }

    @Test
    public void testParseArgsWithIntegerRange() throws ArgumentParserException {
        ap.addArgument("--port").type(Integer.class)
                .choices(range(1025, 65535));
        Namespace res = ap.parseArgs("--port 3000".split(" "));
        assertEquals(3000, res.get("port"));
        try {
            ap.parseArgs("--port 80".split(" "));
            fail("Exception must be thrown");
        } catch (ArgumentParserException e) {
            // success
        }
    }

    @Test
    public void testParseArgsWithFileInputStream()
            throws ArgumentParserException {
        ap.addArgument("--input").type(FileInputStream.class);
        try {
            ap.parseArgs("--input not_found.txt".split(" "));
            fail("Exception must be thrown");
        } catch (ArgumentParserException e) {
            // success
        }
    }

    @Test
    public void testParseArgsWithFromFilePrefixAndUnrecognizedArgs() throws ArgumentParserException {
        ap = new ArgumentParserImpl("argparse4j", true, ArgumentParsers.DEFAULT_PREFIX_CHARS, "@");
        ap.addArgument("-a").action(Arguments.storeTrue());
        ap.addArgument("-b").action(Arguments.storeTrue());
        ap.addArgument("-c").action(Arguments.storeTrue());
        ap.addArgument("-d").action(Arguments.storeTrue());
        try {
            // If unrecognized arguments was found in arguments from file, add
            // additional help message.
            ap.parseArgs("-a @target/test-classes/args5.txt".split(" "));
            fail();
        } catch(ArgumentParserException e) {
            assertEquals(String.format(
                        TextHelper.LOCALE_ROOT,
                        "unrecognized arguments: '-x'%n" +
            		"Checking trailing white spaces or new lines in @file may help."),
            		e.getMessage());
        }
        try {
            // -x is not from file, so no additional help.
            ap.parseArgs("@target/test-classes/args6.txt -x".split(" "));
            fail();
        } catch(ArgumentParserException e) {
            assertEquals("unrecognized arguments: '-x'", e.getMessage());
        }
        try {
            // Check @file inside @file extends check range (overlap case).
            ap.parseArgs("@target/test-classes/args7.txt".split(" "));
            fail();
        } catch(ArgumentParserException e) {
            assertEquals(String.format(
                        TextHelper.LOCALE_ROOT,
                        "unrecognized arguments: '-x'%n" +
            		"Checking trailing white spaces or new lines in @file may help."),
            		e.getMessage());
        }
        try {
            // Check range is updated by args5.txt (non-overlap case).
            ap.parseArgs("@target/test-classes/args6.txt @target/test-classes/args5.txt".split(" "));
            fail();
        } catch(ArgumentParserException e) {
            assertEquals(String.format(
                        TextHelper.LOCALE_ROOT,
                        "unrecognized arguments: '-x'%n" +
            		"Checking trailing white spaces or new lines in @file may help."),
            		e.getMessage());
        }
        try {
            // Unrecognized non-flag arguments
            ap.parseArgs("@target/test-classes/args8.txt".split(" "));
        } catch(ArgumentParserException e) {
            assertEquals(String.format(
                        TextHelper.LOCALE_ROOT,
                        "unrecognized arguments: ' b'%n" +
            		"Checking trailing white spaces or new lines in @file may help."),
            		e.getMessage());
        }
        // Multiple fromFilePrefix
        ap = new ArgumentParserImpl("argparse4j", true, ArgumentParsers.DEFAULT_PREFIX_CHARS, "@/");
        ap.addArgument("-a").action(Arguments.storeTrue());
        try {
            ap.parseArgs("-a @target/test-classes/args5.txt".split(" "));
            fail();
        } catch(ArgumentParserException e) {
            assertEquals(String.format(
                        TextHelper.LOCALE_ROOT,
                        "unrecognized arguments: '-x'%n" +
            		"Checking trailing white spaces or new lines in [@/]file may help."),
            		e.getMessage());
        }
    }

    @Test
    public void testParseArgsWithFromFilePrefix() throws ArgumentParserException {
        ap = new ArgumentParserImpl("argparse4j", true, ArgumentParsers.DEFAULT_PREFIX_CHARS, "@");
        ap.addArgument("-f");
        ap.addArgument("--baz").nargs(2);
        ap.addArgument("x");
        ap.addArgument("y").nargs(2);
        Subparsers subparsers = ap.addSubparsers();
        Subparser subparser = subparsers.addParser("add");
        subparser.addArgument("--foo");
        subparser.addArgument("--bar").action(Arguments.storeTrue());

        Namespace res = ap.parseArgs("-f foo @target/test-classes/args.txt --baz alpha @target/test-classes/args2.txt x y1 @target/test-classes/args3.txt add --bar @target/test-classes/args4.txt".split(" "));
        assertEquals("bar", res.getString("f"));
        assertEquals(list("alpha", "bravo"), res.getList("baz"));
        assertEquals("x", res.getString("x"));
        assertEquals(list("y1", "y2"), res.getList("y"));
        assertEquals("HELLO", res.getString("foo"));
    }

    @Test
    public void testParseArgsWithSubparsers() throws ArgumentParserException {
        ap.addArgument("-f");
        Subparsers subparsers = ap.addSubparsers();
        Subparser parserA = subparsers.addParser("install");
        parserA.addArgument("pkg1");
        parserA.setDefault("func", "install");
        Subparser parserB = subparsers.addParser("search");
        parserB.addArgument("pkg2");
        parserB.setDefault("func", "search");
        Namespace res = ap.parseArgs("install aria2".split(" "));
        assertEquals("aria2", res.get("pkg1"));
        assertEquals("install", res.get("func"));
    }

    @Test
    public void testParseArgsWithSubparsersAlias() throws ArgumentParserException {
        Subparsers subparsers = ap.addSubparsers();
        Subparser checkout = subparsers.addParser("checkout").aliases("co");
        checkout.setDefault("func", "checkout");
        Namespace res = ap.parseArgs("co".split(" "));
        assertEquals("checkout", res.get("func"));
    }

    @Test
    public void testParseArgsWithSubparsersAmbiguousCommand() throws ArgumentParserException {
        Namespace res;
        Subparsers subparsers = ap.addSubparsers();
        Subparser checkout = subparsers.addParser("clone")
                .setDefault("func", "clone");
        Subparser clean = subparsers.addParser("clean")
                .setDefault("func", "clean");

        res = ap.parseArgs("clo".split(" "));
        assertEquals("clone", res.get("func"));

        res = ap.parseArgs("cle".split(" "));
        assertEquals("clean", res.get("func"));
        try {
            ap.parseArgs("cl".split(" "));
            fail();
        } catch(ArgumentParserException e) {
            assertEquals("ambiguous command: cl could match clean, clone",
                    e.getMessage());
        }
    }

    @Test
    public void testParseArgsWithDefaults() throws ArgumentParserException {
        ap.addArgument("-f").setDefault("foo");
        ap.addArgument("-g").setDefault("bar");
        ap.addArgument("-i").setDefault("alpha");
        ap.setDefault("foo", "FOO");
        Namespace res = ap.parseArgs("-i input".split(" "));
        assertEquals("FOO", res.get("foo"));
        assertEquals("bar", res.get("g"));
        assertEquals("input", res.get("i"));
    }

    @Test
    public void testParseArgsWithNargsEmptyList() throws ArgumentParserException {
        ap.addArgument("--foo").nargs("*");
        ap.addArgument("--bar").nargs("*").setDefault("bar");
        ap.addArgument("--baz").nargs("*").action(append());
        ap.addArgument("--buzz").nargs("*").action(append()).setDefault("buzz");

        Namespace res = ap.parseArgs(zeroargs);
        assertEquals(null, res.get("foo"));
        assertEquals("bar", res.get("bar"));
        assertEquals(null, res.get("baz"));
        assertEquals("buzz", res.get("buzz"));

        // Make sure that empty list overwrites previous arguments.
        res = ap.parseArgs("--foo 1 2 --foo".split(" "));
        assertEquals(list(), res.get("foo"));

        // Make sure that empty list overwrites default value.
        res = ap.parseArgs("--bar".split(" "));
        assertEquals(list(), res.get("bar"));

        // Make sure that empty list is appended
        res = ap.parseArgs("--baz".split(" "));
        assertEquals(list(list()), res.get("baz"));

        // Make sure that empty list overwrites default value
        res = ap.parseArgs("--buzz".split(" "));
        assertEquals(list(list()), res.get("buzz"));

        // sanity check: Make sure that given list overwrites default value
        res = ap.parseArgs("--buzz 1 2".split(" "));
        assertEquals(list(list("1", "2")), res.get("buzz"));
    }

    @Test
    public void testParseArgsWithPosargNargsEmptyList() throws ArgumentParserException {
        Namespace res;

        ap.addArgument("foo").nargs("*");
        res = ap.parseArgs(zeroargs);
        assertEquals(list(), res.get("foo"));

        ap = new ArgumentParserImpl("argparse4j");
        ap.addArgument("foo").nargs("*").setDefault("foo");
        // Make sure that default value is kept
        res = ap.parseArgs(zeroargs);
        assertEquals("foo", res.get("foo"));
        // Make sure that given argument list overwrites default.
        res = ap.parseArgs("a b".split(" "));
        assertEquals(list("a", "b"), res.get("foo"));

        ap = new ArgumentParserImpl("argparse4j");
        ap.addArgument("foo").nargs("*").action(append());
        // Make sure that empty list is returned.
        res = ap.parseArgs(zeroargs);
        assertEquals(list(), res.get("foo"));

        ap = new ArgumentParserImpl("argparse4j");
        ap.addArgument("foo").nargs("*").action(append()).setDefault("foo");
        // Make sure that default stays intact without positional argument
        res = ap.parseArgs(zeroargs);
        assertEquals("foo", res.get("foo"));
        // Make sure that given argument list overwrites default.
        res = ap.parseArgs("a b".split(" "));
        assertEquals(list(list("a", "b")), res.get("foo"));
    }

    @Test
    public void testParseArgsWithsPosargNargsDefaults() throws ArgumentParserException {

        class MockAction implements ArgumentAction {
            boolean invoked;

            @Override
            public void run(ArgumentParser parser, Argument arg,
                    Map<String, Object> attrs, String flag, Object value)
                    throws ArgumentParserException {
                invoked = true;
            }

            @Override
            public void onAttach(Argument arg) {}

            @Override
            public boolean consumeArgument() { return true; }
        }

        ap.addArgument("f").nargs("*").setDefault(list("default"));
        MockAction action = new MockAction();
        ap.addArgument("b").nargs("*").setDefault(false).action(action);
        Namespace res = ap.parseArgs(new String[] {});
        assertEquals(list("default"), res.get("f"));
        assertEquals(false, action.invoked);
    }

    @Test
    public void testParseArgsWithDefaultControlSuppress()
            throws ArgumentParserException {
        ap.addArgument("-f");
        ap.addArgument("-g").setDefault(SUPPRESS);
        Namespace res = ap.parseArgs(new String[] {});
        assertTrue(res.getAttrs().containsKey("f"));
        assertFalse(res.getAttrs().containsKey("g"));
    }

    @Test
    public void testParseArgsWithUnrecognizedArgs()
            throws ArgumentParserException {
        ap.addArgument("foo");
        try {
            ap.parseArgs("alpha bravo charlie".split(" "));
            fail();
        } catch (ArgumentParserException e) {
            assertEquals("unrecognized arguments: 'bravo charlie'",
                    e.getMessage());
        }
    }

    @Test
    public void testParseArgsWithSeparator() throws ArgumentParserException {
        ap.addArgument("--foo");
        ap.addArgument("-2");
        ap.addArgument("bar");
        ap.addArgument("car");
        Namespace res = ap.parseArgs("-- -2 --".split(" "));
        assertEquals("-2", res.get("bar"));
        assertEquals("--", res.get("car"));
    }

    @Test
    public void testParseArgsWithMutexGroup() throws ArgumentParserException {
        MutuallyExclusiveGroup group = ap.addMutuallyExclusiveGroup("mutex");
        group.addArgument("--foo");
        group.addArgument("--bar");
        Namespace res = ap.parseArgs("--foo bar".split(" "));
        assertEquals("bar", res.get("foo"));
    }

    @Test
    public void testParseArgsWithMutexGroupDuplicate() throws ArgumentParserException {
        MutuallyExclusiveGroup group = ap.addMutuallyExclusiveGroup("mutex");
        group.addArgument("--foo");
        group.addArgument("--bar");
        try {
            ap.parseArgs("--foo bar --bar baz".split(" "));
            fail();
        } catch(ArgumentParserException e) {
            assertEquals("argument --bar: not allowed with argument --foo", e.getMessage());
        }
    }

    @Test
    public void testParseArgsWithMutexGroupRequired() throws ArgumentParserException {
        MutuallyExclusiveGroup group = ap.addMutuallyExclusiveGroup("mutex").required(true);
        group.addArgument("--foo");
        group.addArgument("--bar");
        Namespace res = ap.parseArgs("--foo bar".split(" "));
        assertEquals("bar", res.get("foo"));
    }

    @Test
    public void testParseArgsWithMutexGroupRequiredFail() throws ArgumentParserException {
        MutuallyExclusiveGroup group = ap.addMutuallyExclusiveGroup("mutex").required(true);
        group.addArgument("--foo");
        group.addArgument("--bar");
        try {
            ap.parseArgs(new String []{});
            fail();
        } catch(ArgumentParserException e) {
            assertEquals("one of the arguments --foo --bar is required", e.getMessage());
        }
    }

    @Test
    public void testParseArgsWithMutexGroupConcatDuplicate() throws ArgumentParserException {
        MutuallyExclusiveGroup group = ap.addMutuallyExclusiveGroup("mutex").required(true);
        group.addArgument("-a").action(Arguments.storeTrue());
        group.addArgument("-b").action(Arguments.storeTrue());
        ap.addArgument("-c").action(Arguments.storeTrue());
        try {
            ap.parseArgs("-acb".split(" "));
        } catch(ArgumentParserException e) {
            assertEquals("argument -b: not allowed with argument -a", e.getMessage());
        }
    }

    @Test
    public void testParseArgsWithMutexGroupConcat() throws ArgumentParserException {
        MutuallyExclusiveGroup group = ap.addMutuallyExclusiveGroup("mutex").required(true);
        group.addArgument("-a").action(Arguments.storeTrue());
        group.addArgument("-b").action(Arguments.storeTrue());
        ap.addArgument("-c");
        Namespace res = ap.parseArgs("-acfoo".split(" "));
        assertEquals(true, res.getBoolean("a"));
        assertEquals("foo", res.get("c"));
    }

    @Test
    public void testParseArgsWithCommandAfterSeparator() throws ArgumentParserException {
        Subparsers subparsers = ap.addSubparsers();
        subparsers.addParser("install");
        try {
            ap.parseArgs("-- install".split(" "));
            fail();
        } catch(ArgumentParserException e) {
            assertEquals("unrecognized arguments: 'install'", e.getMessage());
        }
    }

    @Test
    public void testParseArgsWithoutSubcommand() throws ArgumentParserException {
        Subparsers subparsers = ap.addSubparsers();
        subparsers.addParser("install");
        try {
            ap.parseArgs(new String[]{});
            fail();
        } catch(ArgumentParserException e) {
            assertEquals("too few arguments", e.getMessage());
        }
    }

    @Test
    public void testParseArgsWithMutualExclusiveGroupAndSuppressHelp()
            throws ArgumentParserException {
        MutuallyExclusiveGroup mutex1 = ap.addMutuallyExclusiveGroup("mutex1")
                .required(true);
        mutex1.addArgument("-a").help(Arguments.SUPPRESS);
        Argument b = mutex1.addArgument("-b");
        // Check the suppressed argument is not shown in the error message
        try {
            ap.parseArgs(new String[]{});
            fail();
        } catch(ArgumentParserException e) {
            assertEquals("one of the arguments -b is required", e.getMessage());
        }
        b.help(Arguments.SUPPRESS);
        try {
            ap.parseArgs(new String[]{});
            fail();
        } catch(ArgumentParserException e) {
            assertEquals("one of the arguments is required", e.getMessage());
        }
    }

    @Test
    public void testParseArgsWithAmbiguousOpts() throws ArgumentParserException {
        Namespace res;
        ap.addArgument("-a").action(storeTrue());
        ap.addArgument("-b");
        ap.addArgument("-aaa").action(storeTrue());
        ap.addArgument("-bbb").action(storeTrue());

        // Exact match -aaa
        res = ap.parseArgs("-aaa".split(" "));
        assertTrue(res.getBoolean("aaa"));
        // Exact match -a
        res = ap.parseArgs("-a".split(" "));
        assertTrue(res.getBoolean("a"));
        // -aa is ambiguous
        try {
            res = ap.parseArgs("-aa".split(" "));
            fail();
        } catch(ArgumentParserException e) {
            assertEquals("ambiguous option: -aa could match -a, -aaa",
                    e.getMessage());
        }
        // Exact match -b
        res = ap.parseArgs("-bx".split(" "));
        assertEquals("x", res.get("b"));
        // Exact match -bbb
        res = ap.parseArgs("-bbb".split(" "));
        assertTrue(res.getBoolean("bbb"));
        try {
            res = ap.parseArgs("-bb".split(" "));
            fail();
        } catch(ArgumentParserException e) {
            assertEquals("ambiguous option: -bb could match -b, -bbb",
                    e.getMessage());
        }
    }

    @Test
    public void testParseArgsWithDeferredException() throws ArgumentParserException {
        ap = new ArgumentParserImpl("argparse4j");
        ap.addArgument("-a").required(true);
        ap.addArgument("b");
        ap.addMutuallyExclusiveGroup().required(true).addArgument("-c");
        ap.addSubparsers().addParser("install");

        try {
            ap.parseArgs("install -h".split(" "));
        } catch(HelpScreenException e) {
            // Success
        } catch(ArgumentParserException e) {
            fail();
        }
    }

    @Test
    public void testSubparserInheritPrefixChars() throws ArgumentParserException {
        ap = new ArgumentParserImpl("argparse4j", true, "+");
        ap.addSubparsers().addParser("install").addArgument("+f");
        ap.addSubparsers().addParser("check", true, "-").addArgument("-f");
        try {
            ap.addSubparsers().addParser("test", true, "-").addArgument("+f", "++f");
            fail();
        } catch(IllegalArgumentException e) {
            assertEquals("invalid option string '+f': must start with a character '-'",
                    e.getMessage());
        }
    }
    
    @Test
    public void testSubparsersDest() throws ArgumentParserException {
        Subparsers subparsers = ap.addSubparsers().dest("command");
        subparsers.addParser("install").addArgument("--command")
                .setDefault("default");
        Namespace res = ap.parseArgs("install".split(" "));
        assertEquals("install", res.get("command"));
    }

    @Test
    public void testArgumentParserWithoutAddHelp()
            throws ArgumentParserException {
        ArgumentParserImpl ap = new ArgumentParserImpl("argparse4j", false);
        try {
            ap.parseArgs("-h".split(" "));
            fail();
        } catch (ArgumentParserException e) {
            assertEquals("unrecognized arguments: '-h'", e.getMessage());
        }
    }

    @Test
    public void testSubparserWithoutAddHelp() throws ArgumentParserException {
        Subparsers subparsers = ap.addSubparsers();
        subparsers.addParser("install", false, ArgumentParsers.DEFAULT_PREFIX_CHARS);
        try {
            ap.parseArgs("install -h".split(" "));
            fail();
        } catch (ArgumentParserException e) {
            assertEquals("unrecognized arguments: '-h'", e.getMessage());
        }
    }

    @Test
    public void testGetDefault() throws ArgumentParserException {
        ap.addArgument("--foo").setDefault("alpha");
        ap.setDefault("foo", "bravo");
        ap.setDefault("bar", "charlie");
        assertEquals("alpha", ap.getDefault("foo"));
        assertEquals("charlie", ap.getDefault("bar"));
    }

    @Test
    public void testParseArgsWithUserData() throws ArgumentParserException {
        class Out {
            @Arg(dest = "null")
            public String x;
            @Arg(dest = "username", ignoreError = true)
            private int y;
            @Arg(dest = "username")
            public String name;
            @Arg
            public String host;
            
            private int[] ints;

            @Arg(dest = "attrs")
            private void setInts(int ints[]) {
                this.ints = ints;
            }

            public int[] getInts() {
                return ints;
            }

            @Arg(dest = "attrs", ignoreError = true)
            public void setY(int y) {
                this.y = y;
            }
        }

        ap.addArgument("--username");
        ap.addArgument("--host");
        ap.addArgument("--attrs").nargs("*").type(Integer.class);
        Out out = new Out();
        ap.parseArgs("--username alice --host example.com --attrs 1 2 3".split(" "), out);
        assertEquals("alice", out.name);
        assertEquals("example.com", out.host);
        assertArrayEquals(new int[] { 1, 2, 3 }, out.getInts());
        
        // Test inheritance
        class SubOut extends Out {
        	@Arg
        	public int port;
        }
        
        ap.addArgument("--port").type(Integer.class);
        SubOut subOut = new SubOut();
        ap.parseArgs("--username alice --host example.com --port 8080 --attrs 1 2 3".split(" "), subOut);
        assertEquals("alice", subOut.name);
        assertEquals("example.com", subOut.host);
        assertArrayEquals(new int[] { 1, 2, 3 }, subOut.getInts());
        assertEquals(8080, subOut.port);
        
    }

    @Test
    public void testFormatUsage() {
        assertEquals(String.format(
                TextHelper.LOCALE_ROOT,
                "usage: argparse4j [-h]%n"), ap.formatUsage());
        ap.addArgument("-a");
        ap.addArgument("-b").required(true);
        MutuallyExclusiveGroup group = ap.addMutuallyExclusiveGroup("mutex").required(true);
        group.addArgument("-c").required(true);
        group.addArgument("-d").required(true);
        ap.addArgument("file");
        assertEquals(String.format(
                TextHelper.LOCALE_ROOT,
                "usage: argparse4j [-h] [-a A] -b B (-c C | -d D) file%n"),
                ap.formatUsage());
        Subparser foosub = ap.addSubparsers().addParser("foo");
        foosub.addArgument("hash");
        assertEquals(String.format(
                TextHelper.LOCALE_ROOT,
                "usage: argparse4j [-h] [-a A] -b B (-c C | -d D) file {foo} ...%n"),
                ap.formatUsage());
        assertEquals(String.format(
                TextHelper.LOCALE_ROOT,
                "usage: argparse4j -b B (-c C | -d D) file foo [-h] hash%n"),
                foosub.formatUsage());
        Subparser bazsub = foosub.addSubparsers().addParser("baz");
        assertEquals(String.format(
                TextHelper.LOCALE_ROOT,
                "usage: argparse4j -b B (-c C | -d D) file foo hash baz [-h]%n"),
                bazsub.formatUsage());
    }

    @Test
    public void testUsage() {
        ap.usage("<${prog}> [OPTIONS] ${prog}FILES");
        assertEquals(String.format(
                TextHelper.LOCALE_ROOT,
                "usage: <argparse4j> [OPTIONS] argparse4jFILES%n"),
                ap.formatUsage());
    }

    @Test
    public void testFormatHelpWithArgumentGroup()
            throws ArgumentParserException {
        ap.description("This is argparser4j.").epilog("This is epilog.");
        ArgumentGroup group = ap.addArgumentGroup("group1")
                .description("group1 description");
        group.addArgument("--foo");
        assertEquals(String.format(
                  TextHelper.LOCALE_ROOT,
                  "usage: argparse4j [-h] [--foo FOO]%n"
                + "%n"
                + "This is argparser4j.%n"
                + "%n"
                + "optional arguments:%n"
                + "  -h, --help             show this help message and exit%n"
                + "%n"
                + "group1:%n"
                + "  group1 description%n"
                + "%n"
                + "  --foo FOO%n"
                + "%n" + "This is epilog.%n"),
                ap.formatHelp());
    }

    @Test
    public void testFormatHelpWithArgumentGroupWithoutTitleAndDescription()
            throws ArgumentParserException {
        ap.description("This is argparser4j.").epilog("This is epilog.");
        ArgumentGroup group = ap.addArgumentGroup("");
        group.addArgument("--foo");
        assertEquals(String.format(
                  TextHelper.LOCALE_ROOT,
                  "usage: argparse4j [-h] [--foo FOO]%n"
                + "%n"
                + "This is argparser4j.%n"
                + "%n"
                + "optional arguments:%n"
                + "  -h, --help             show this help message and exit%n"
                + "%n"
                + "  --foo FOO%n"
                + "%n"
                + "This is epilog.%n"),
                ap.formatHelp());
    }

    @Test
    public void testFormatHelpWithArgumentGroupWithoutHelp() throws ArgumentParserException {
        ArgumentParserImpl ap = new ArgumentParserImpl("argparse4j", false);
        ArgumentGroup group1 = ap.addArgumentGroup("group1").description(
                "group1 description");
        group1.addArgument("foo").help("foo help");
        ArgumentGroup group2 = ap.addArgumentGroup("group2").description(
                "group2 description");
        group2.addArgument("--bar").help("bar help");
        assertEquals(String.format(
                  TextHelper.LOCALE_ROOT,
                  "usage: argparse4j [--bar BAR] foo%n"
                + "%n"
                + "group1:%n"
                + "  group1 description%n"
                + "%n"
                + "  foo                    foo help%n"
                + "%n"
                + "group2:%n"
                + "  group2 description%n"
                + "%n"
                + "  --bar BAR              bar help%n"),
                ap.formatHelp());
  
    }
    
    @Test
    public void testFormatHelpWithMutexGroup()
            throws ArgumentParserException {
        ap.description("This is argparser4j.").epilog("This is epilog.");
        MutuallyExclusiveGroup group = ap.addMutuallyExclusiveGroup("group1")
                .description("group1 description");
        group.addArgument("--foo");
        group.addArgument("--bar");
        assertEquals(String.format(
                  TextHelper.LOCALE_ROOT,
                  "usage: argparse4j [-h] [--foo FOO | --bar BAR]%n"
                + "%n"
                + "This is argparser4j.%n"
                + "%n"
                + "optional arguments:%n"
                + "  -h, --help             show this help message and exit%n"
                + "%n"
                + "group1:%n"
                + "  group1 description%n"
                + "%n"
                + "  --foo FOO%n"
                + "  --bar BAR%n"
                + "%n"
                + "This is epilog.%n"),
                ap.formatHelp());
    }

    @Test
    public void testFormatHelpWithMutexGroupWithoutTitleAndDescription()
            throws ArgumentParserException {
        ap.description("This is argparser4j.").epilog("This is epilog.");
        MutuallyExclusiveGroup group = ap.addMutuallyExclusiveGroup();
        group.addArgument("--foo");
        ap.addArgument("-b").action(Arguments.storeTrue());
        // Without title and description, options in mutually exclusive group
        // is merged into other optional arguments.
        assertEquals(String.format(
                  TextHelper.LOCALE_ROOT,
                  "usage: argparse4j [-h] [-b] [--foo FOO]%n"
                + "%n"
                + "This is argparser4j.%n"
                + "%n"
                + "optional arguments:%n"
                + "  -h, --help             show this help message and exit%n"
                + "  --foo FOO%n"
                + "  -b%n"
                + "%n"
                + "This is epilog.%n"),
                ap.formatHelp());
    }

    @Test
    public void testFormatHelp() throws ArgumentParserException {
        ap.description("This is argparser4j.").epilog("This is epilog.");
        assertEquals(String.format(
                  TextHelper.LOCALE_ROOT,
                  "usage: argparse4j [-h]%n"
                + "%n"
                + "This is argparser4j.%n"
                + "%n" + "optional arguments:%n"
                + "  -h, --help             show this help message and exit%n"
                + "%n"
                + "This is epilog.%n"),
                ap.formatHelp());
    }

    @Test
    public void testFormatHelpWithDefaultHelp() throws ArgumentParserException {
        ap.defaultHelp(true).addArgument("--foo").setDefault("alpha");
        assertEquals(String.format(
                  TextHelper.LOCALE_ROOT,
                  "usage: argparse4j [-h] [--foo FOO]%n"
                + "%n"
                + "optional arguments:%n"
                + "  -h, --help             show this help message and exit%n"
                + "  --foo FOO              (default: alpha)%n"),
                ap.formatHelp());
    }

    @Test
    public void testFormatHelpWithSuppress()
            throws ArgumentParserException {
        ArgumentGroup group = ap.addArgumentGroup("group");
        group.addArgument("--foo");
        group.addArgument("--bar").help(Arguments.SUPPRESS);
        ap.addArgument("-a").help(Arguments.SUPPRESS).required(true);
        ap.addArgument("-b");
        MutuallyExclusiveGroup mutex1 = ap.addMutuallyExclusiveGroup("mutex1");
        mutex1.addArgument("-c").help(Arguments.SUPPRESS);
        mutex1.addArgument("-d");
        MutuallyExclusiveGroup mutex2 = ap.addMutuallyExclusiveGroup("mutex2")
                .required(true);
        mutex2.addArgument("-e").help(Arguments.SUPPRESS);
        mutex2.addArgument("-f");
        mutex2.addArgument("-g");
        ap.addArgument("s");
        ap.addArgument("t");
        ap.addArgument("u").help(Arguments.SUPPRESS);
        Subparsers subparsers = ap.addSubparsers();
        Subparser sap = subparsers.addParser("add");
        sap.addArgument("-i").help(Arguments.SUPPRESS);
        sap.addArgument("-j");

        assertEquals(String.format(
                     TextHelper.LOCALE_ROOT,
                     "usage: argparse4j [-h] [--foo FOO] [-b B] [-d D] (-f F | -g G) s t {add}%n"
                   + "                  ...%n"
                   + "%n"
                   + "positional arguments:%n"
                   + "  s%n"
                   + "  t%n"
                   + "  {add}%n"
                   + "%n"
                   + "optional arguments:%n"
                   + "  -h, --help             show this help message and exit%n"
                   + "  -b B%n"
                   + "%n"
                   + "group:%n"
                   + "  --foo FOO%n"
                   + "%n"
                   + "mutex1:%n"
                   + "  -d D%n"
                   + "%n"
                   + "mutex2:%n"
                   + "  -f F%n"
                   + "  -g G%n"),
                   ap.formatHelp());
        // Check upper parsers's suppressed required arguments are not shown. 
        assertEquals(String.format(
                     TextHelper.LOCALE_ROOT,
                     "usage: argparse4j (-f F | -g G) s t add [-h] [-j J]%n"
                   + "%n"
                   + "optional arguments:%n"
                   + "  -h, --help             show this help message and exit%n"
                   + "  -j J%n"),
                sap.formatHelp());
    }


    @Test
    public void testSubparserFormatHelp() throws ArgumentParserException {
        ap.addArgument("--bar");
        Subparsers subparsers = ap.addSubparsers();
        Subparser parser = subparsers.addParser("install");
        parser.description("This is sub-command of argparser4j.").epilog(
                "This is epilog of sub-command.");
        parser.addArgument("--foo");
        assertEquals(String.format(
                  TextHelper.LOCALE_ROOT,
                  "usage: argparse4j install [-h] [--foo FOO]%n"
                + "%n"
                + "This is sub-command of argparser4j.%n"
                + "%n"
                + "optional arguments:%n"
                + "  -h, --help             show this help message and exit%n"
                + "  --foo FOO%n"
                + "%n"
                + "This is epilog of sub-command.%n"),
                parser.formatHelp());
    }

    @Test
    public void testSubparserFormatHelpWithDefaultHelp()
            throws ArgumentParserException {
        ap.addArgument("--bar");
        Subparsers subparsers = ap.addSubparsers();
        Subparser parser = subparsers.addParser("install").defaultHelp(true);
        parser.addArgument("--foo").setDefault("alpha");
        assertEquals(String.format(
                  TextHelper.LOCALE_ROOT,
                  "usage: argparse4j install [-h] [--foo FOO]%n"
                + "%n"
                + "optional arguments:%n"
                + "  -h, --help             show this help message and exit%n"
                + "  --foo FOO              (default: alpha)%n"),
                parser.formatHelp());
    }

    @Test
    public void testFormatHelpWithSubparserTitleDescription()
            throws ArgumentParserException {
        Subparsers subparsers = ap.addSubparsers().help("subcommand help")
                .title("mysubcommands").description("valid subcommands");
        subparsers.addParser("install").help("install help");
        assertEquals(String.format(
                  TextHelper.LOCALE_ROOT,
                  "usage: argparse4j [-h] {install} ...%n"
                + "%n"
                + "optional arguments:%n"
                + "  -h, --help             show this help message and exit%n"
                + "%n"
                + "mysubcommands:%n"
                + "  valid subcommands%n"
                + "%n"
                + "  {install}              subcommand help%n"
                + "    install              install help%n"),
                ap.formatHelp());
    }

    @Test
    public void testFormatHelpWithSubparserAlias()
            throws ArgumentParserException {
        Subparsers subparsers = ap.addSubparsers().help("subcommand help")
                .title("mysubcommands").description("valid subcommands");
        subparsers.addParser("clone").help("clone help");
        subparsers.addParser("checkout").aliases("co").help("checkout help");
        subparsers.addParser("remove").aliases("rm","del").help("remove help");
        assertEquals(String.format(
                  TextHelper.LOCALE_ROOT,
                  "usage: argparse4j [-h] {clone,checkout,co,remove,rm,del} ...%n"
                + "%n"
                + "optional arguments:%n"
                + "  -h, --help             show this help message and exit%n"
                + "%n"
                + "mysubcommands:%n"
                + "  valid subcommands%n"
                + "%n"
                + "  {clone,checkout,co,remove,rm,del}%n"
                + "                         subcommand help%n"
                + "    clone                clone help%n"
                + "    checkout (co)        checkout help%n"
                + "    remove (rm,del)      remove help%n"),
                ap.formatHelp());
    }

    @Test
    public void testPrintHelp() throws ArgumentParserException {
        String h = "Alice was beginning to get very tired of sitting by her sister on the bank, and of having nothing to do";
        ap.addArgument("bar").help(h);
        ap.addArgument("verylonglongpositionalargument").help(h);
        ap.addArgument("-f", "--foo").help(h);
        ap.addArgument("-1", "--1").metavar("X").nargs(2).help(h);
        ap.addArgument("-2").metavar("X").nargs("*").help(h);
        ap.addArgument("-3").metavar("X").nargs("+").help(h);
        ap.addArgument("-4").metavar("X").nargs("?").help(h);
        Subparsers subparsers = ap.addSubparsers().help("sub-command help");
        Subparser parserA = subparsers.addParser("install");
        parserA.help("parserA help");
        Subparser parserB = subparsers.addParser("search");

        // StringWriter out = new StringWriter();
        ap.printHelp(new PrintWriter(System.out));
    }

    @Test
    public void testFormatVersion() throws ArgumentParserException {
        ap.version("${prog} version 7.8.7 (Dreamliner)");
        assertEquals("argparse4j version 7.8.7 (Dreamliner)", ap.formatVersion());
    }

    @Test
    public void testCandidateEquality() {
        Candidate foo = new Candidate(15, "foo");
        Candidate fooCopy = new Candidate(15, "foo");
        Candidate bar = new Candidate(15, "bar");
        Candidate foo2 = new Candidate(16, "foo");
        assertTrue(foo.equals(foo));
        assertTrue(foo.equals(fooCopy));
        assertFalse(foo.equals(bar));
        assertFalse(foo.equals(foo2));
        assertFalse(foo.equals(null));
        assertFalse(foo.equals("foo"));
        Candidate subNull = new Candidate(15, null);
        assertFalse(foo.equals(subNull));
        assertFalse(subNull.equals(foo));

        assertEquals(foo.hashCode(), fooCopy.hashCode());
        assertFalse(foo.hashCode() == foo2.hashCode());
        assertFalse(foo.hashCode() == bar.hashCode());
        assertFalse(foo.hashCode() == subNull.hashCode());
    }
    
    @Test (expected=HelpScreenException.class)
    public void testHelpThrowsHelpScreenException() throws ArgumentParserException {
        ap.parseArgs(new String[]{"--help"});
    }
}
