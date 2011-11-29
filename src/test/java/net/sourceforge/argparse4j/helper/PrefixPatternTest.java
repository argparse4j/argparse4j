package net.sourceforge.argparse4j.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class PrefixPatternTest {

    private PrefixPattern pat = new PrefixPattern("-+");

    @Test
    public void testmatch() {
        assertTrue(pat.match("-f"));
        assertTrue(pat.match("+-f"));
        assertTrue(pat.match("+f"));
        assertTrue(!pat.match("f"));
    }

    @Test
    public void testmatchLongFlag() {
        assertTrue(pat.matchLongFlag("--flag"));
        assertTrue(pat.matchLongFlag("+-+flag"));
        assertTrue(!pat.matchLongFlag("-f"));
        assertTrue(!pat.matchLongFlag("-flag"));
        assertTrue(!pat.matchLongFlag("flag"));
    }

    @Test
    public void testRemovePrefix() {
        assertEquals("foo", pat.removePrefix("--foo"));
        assertEquals("f", pat.removePrefix("+f"));
        assertEquals("foo", pat.removePrefix("foo"));
    }
}
