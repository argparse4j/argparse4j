package net.sourceforge.argparse4j.internal;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SubParserImplArgNamesInResultTest {
    @Test
    public void singlePrefixOneWordNamedSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("-a");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "-a", "value"});

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void singlePrefixTwoWordsNamedSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("-a-b");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "-a-b", "value"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void singlePrefixTwoWordsNamedSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("-a-b");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "-a-b", "value"});

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void doublePrefixWordNamedSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("--a");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "--a", "value"});

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void doublePrefixTwoWordsNamedSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("--a-b");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "--a-b", "value"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void doublePrefixTwoWordsNamedAfterSinglePrefixSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("-a", "--a-b");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "--a-b", "value"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void doubleAndSinglePrefixAndSinglePrefixArgumentSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("-a", "--a-b");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "-a", "value"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void doublePrefixTwoWordsNamedSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("--a-b");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "--a-b", "value"});

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void doublePrefixTwoWordsNamedAfterSinglePrefixSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("-a", "--a-b");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "--a-b", "value"});

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void doubleAndSinglePrefixAndSinglePrefixArgumentSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        subParser.addArgument("-a", "--a-b");

        Namespace namespace = parser.parseArgs(new String[]{"sub", "-a", "value"});

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
    public void singlePrefixOneWordNamedFallingBackToDefaultSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        Argument argument = subParser.addArgument("-a");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[]{"sub"});

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void singlePrefixTwoWordsNamedFallingBackToDefaultSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        Argument argument = subParser.addArgument("-a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[]{"sub"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void singlePrefixTwoWordsNamedFallingBackToDefaultSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        Argument argument = subParser.addArgument("-a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[]{"sub"});

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void doublePrefixWordNamedFallingBackToDefaultSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        Argument argument = subParser.addArgument("--a");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[]{"sub"});

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void doublePrefixTwoWordsNamedFallingBackToDefaultSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        Argument argument = subParser.addArgument("--a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[]{"sub"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void doublePrefixTwoWordsNamedAfterSinglePrefixFallingBackToDefaultSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        Argument argument = subParser.addArgument("-a", "--a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[]{"sub"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void doublePrefixTwoWordsNamedFallingBackToDefaultSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        Argument argument = subParser.addArgument("--a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[]{"sub"});

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void doublePrefixTwoWordsNamedAfterSinglePrefixFallingBackToDefaultSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Subparser subParser = parser.addSubparsers().addParser("sub");
        Argument argument = subParser.addArgument("-a", "--a-b");
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