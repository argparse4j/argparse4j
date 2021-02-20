package net.sourceforge.argparse4j.internal;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ArgumentParserImplArgNamesInResultTest {
    @Test
    public void singleDashOneWordNamedSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("-a");

        Namespace namespace = parser.parseArgs(new String[]{"-a", "value"});

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void singleDashTwoWordsNamedSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("-a-b");

        Namespace namespace = parser.parseArgs(new String[]{"-a-b", "value"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void singleDashTwoWordsNamedSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("-a-b");

        Namespace namespace = parser.parseArgs(new String[]{"-a-b", "value"});

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void doubleDashWordNamedSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("--a");

        Namespace namespace = parser.parseArgs(new String[]{"--a", "value"});

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void doubleDashTwoWordsNamedSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("--a-b");

        Namespace namespace = parser.parseArgs(new String[]{"--a-b", "value"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void doubleDashTwoWordsNamedSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("--a-b");

        Namespace namespace = parser.parseArgs(new String[]{"--a-b", "value"});

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void oneWordPositionalSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("a");

        Namespace namespace = parser.parseArgs(new String[]{"value"});

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void twoWordPositionalSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("a-b");

        Namespace namespace = parser.parseArgs(new String[]{"value"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void twoWordPositionalSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("a-b");

        Namespace namespace = parser.parseArgs(new String[]{"value"});

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void singleDashOneWordNamedFallingBackToDefaultSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("-a");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void singleDashTwoWordsNamedFallingBackToDefaultSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("-a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void singleDashTwoWordsNamedFallingBackToDefaultSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("-a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void doubleDashWordNamedFallingBackToDefaultSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("--a");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void doubleDashTwoWordsNamedFallingBackToDefaultSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("--a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void doubleDashTwoWordsNamedFallingBackToDefaultSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("--a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void oneWordPositionalFallingBackToDefaultSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("a");
        argument.nargs("*");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void twoWordPositionalFallingBackToDefaultSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("a-b");
        argument.nargs("*");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void twoWordPositionalFallingBackToDefaultSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("a-b");
        argument.nargs("*");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void singleOtherPrefixTwoWordsNamedSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("test")
                .prefixChars("|")
                .includeArgumentNamesAsKeysInResult(true)
                .build();
        parser.addArgument("|a-b");

        Namespace namespace = parser.parseArgs(new String[]{"|a-b", "value"});

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void doubleOtherPrefixTwoWordsNamedSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("test")
                .prefixChars("|")
                .includeArgumentNamesAsKeysInResult(true)
                .build();
        parser.addArgument("||a-b");

        Namespace namespace = parser.parseArgs(new String[]{"||a-b", "value"});

        assertEquals("value", namespace.get("a-b"));
    }

    private ArgumentParser createParser() {
        return ArgumentParsers.newFor("test")
                .includeArgumentNamesAsKeysInResult(true)
                .build();
    }
}