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
    public void singleDashTwoWordsNamedSucceedsForUnderscore() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("-a-b");

        Namespace namespace = parser.parseArgs(new String[]{"-a-b", "value"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void singleDashTwoWordsNamedSucceedsForDash() throws ArgumentParserException {
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
    public void doubleDashTwoWordsNamedSucceedsForUnderscore() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("--a-b");

        Namespace namespace = parser.parseArgs(new String[]{"--a-b", "value"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void doubleDashTwoWordsNamedSucceedsForDash() throws ArgumentParserException {
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
    public void twoWordPositionalSucceedsForUnderscore() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("a-b");

        Namespace namespace = parser.parseArgs(new String[]{"value"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void twoWordPositionalSucceedsForDash() throws ArgumentParserException {
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
    public void singleDashTwoWordsNamedFallingBackToDefaultSucceedsForUnderscore() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("-a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void singleDashTwoWordsNamedFallingBackToDefaultSucceedsForDash() throws ArgumentParserException {
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
    public void doubleDashTwoWordsNamedFallingBackToDefaultSucceedsForUnderscore() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("--a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void doubleDashTwoWordsNamedFallingBackToDefaultSucceedsForDash() throws ArgumentParserException {
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
    public void twoWordPositionalFallingBackToDefaultSucceedsForUnderscore() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("a-b");
        argument.nargs("*");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void twoWordPositionalFallingBackToDefaultSucceedsForDash() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("a-b");
        argument.nargs("*");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a-b"));
    }

    private ArgumentParser createParser() {
        return ArgumentParsers.newFor("test")
                .includeArgumentNamesAsKeysInResult(true)
                .build();
    }
}