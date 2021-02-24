package net.sourceforge.argparse4j.internal;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SubParserImplArgNamesInResultTest {
    @Test
    public void singleDashOneWordNamedSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("-a");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "-a", "value"});

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void singleDashTwoWordsNamedSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("-a-b");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "-a-b", "value"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void singleDashTwoWordsNamedSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("-a-b");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "-a-b", "value"});

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void doubleDashWordNamedSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("--a");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "--a", "value"});

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void doubleDashTwoWordsNamedSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("--a-b");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "--a-b", "value"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void doubleDashTwoWordsNamedSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("--a-b");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "--a-b", "value"});

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void oneWordPositionalSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("a");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "value"});

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void twoWordPositionalSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("a-b");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "value"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void twoWordPositionalSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("a-b");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "value"});

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void singleDashOneWordNamedFallingBackToDefaultSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        Argument argument = subParser.addArgument("-a");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[]{"sub"});

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void singleDashTwoWordsNamedFallingBackToDefaultSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        Argument argument = subParser.addArgument("-a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[]{"sub"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void singleDashTwoWordsNamedFallingBackToDefaultSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        Argument argument = subParser.addArgument("-a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[]{"sub"});

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void doubleDashWordNamedFallingBackToDefaultSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        Argument argument = subParser.addArgument("--a");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[]{"sub"});

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void doubleDashTwoWordsNamedFallingBackToDefaultSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        Argument argument = subParser.addArgument("--a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[]{"sub"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void doubleDashTwoWordsNamedFallingBackToDefaultSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        Argument argument = subParser.addArgument("--a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[]{"sub"});

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void oneWordPositionalFallingBackToDefaultSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        Argument argument = subParser.addArgument("a");
        argument.nargs("*");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[]{"sub"});

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void twoWordPositionalFallingBackToDefaultSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        Argument argument = subParser.addArgument("a-b");
        argument.nargs("*");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[]{"sub"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void twoWordPositionalFallingBackToDefaultSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        Argument argument = subParser.addArgument("a-b");
        argument.nargs("*");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[]{"sub"});

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void singleOtherPrefixTwoWordsNamedSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("test")
                .prefixChars("|")
                .includeArgumentNamesAsKeysInResult(true)
                .build();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("|a-b");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "|a-b", "value"});

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void doubleOtherPrefixTwoWordsNamedSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("test")
                .prefixChars("|")
                .includeArgumentNamesAsKeysInResult(true)
                .build();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("||a-b");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "||a-b", "value"});

        assertEquals("value", namespace.get("a-b"));
    }

    private ArgumentParser createParser() {
        return ArgumentParsers.newFor("test")
                .includeArgumentNamesAsKeysInResult(true)
                .build();
    }
}