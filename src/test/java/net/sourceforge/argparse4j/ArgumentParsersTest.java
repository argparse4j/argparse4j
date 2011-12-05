package net.sourceforge.argparse4j;

import static org.junit.Assert.*;

import net.sourceforge.argparse4j.internal.ArgumentParserImpl;

import org.junit.Test;

public class ArgumentParsersTest {

    @Test
    public void testNewArgumentParser() {
        ArgumentParserImpl ap = (ArgumentParserImpl) ArgumentParsers
                .newArgumentParser("prog", false, "+", "@");
        assertEquals("prog", ap.getProg());
        assertEquals("+", ap.getPrefixChars());
        assertEquals("@", ap.getFromFilePrefixChars());
        // Check +h can be added because addHelp is false
        ap.addArgument("+h");
    }

}
