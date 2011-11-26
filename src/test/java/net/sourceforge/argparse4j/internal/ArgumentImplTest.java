package net.sourceforge.argparse4j.internal;

import static org.junit.Assert.*;

import java.util.regex.Pattern;

import net.sourceforge.argparse4j.helper.PrefixPattern;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.internal.ArgumentImpl;
import net.sourceforge.argparse4j.internal.ArgumentParserImpl;

import org.junit.Before;
import org.junit.Test;


public class ArgumentImplTest {

    private PrefixPattern prefix;

    @Before
    public void setup() {
        prefix = new PrefixPattern(ArgumentParserImpl.PREFIX_CHARS);
    }

    @Test
    public void testArgumentWithName() {
        ArgumentImpl arg = new ArgumentImpl(prefix, "foo");
        assertEquals("foo", arg.getName());
        assertEquals("foo", arg.getDest());
        assertEquals("foo", arg.textualName());
    }

    @Test
    public void testArgumentWithFlags() {
        ArgumentImpl arg = new ArgumentImpl(prefix, "-f", "--foo-bar", "--foo");
        assertNull(arg.getName());
        assertEquals("foo_bar", arg.getDest());
        assertEquals("FOO_BAR", arg.resolveMetavar()[0]);
    }

    @Test
    public void testArgumentWithBadFlags() {
        try {
            new ArgumentImpl(prefix, "f", "-f");
            fail("Exception must be thrown");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    @Test
    public void testArgumentWithPrefix() {
        PrefixPattern prefix = new PrefixPattern("-+");
        ArgumentImpl arg = new ArgumentImpl(prefix, "-f", "-+foo-bar", "++foo");
        assertNull(arg.getName());
        assertEquals("foo_bar", arg.getDest());
        assertEquals("FOO_BAR", arg.resolveMetavar()[0]);
    }
    
    @Test
    public void testNargsWithZero() {
        ArgumentImpl arg = new ArgumentImpl(prefix, "--foo");
        try {
            arg.nargs(0);
            fail();
        } catch(IllegalArgumentException e) {
        }
    }

    @Test
    public void testResolveMetavar() {
        ArgumentImpl arg = new ArgumentImpl(prefix, "--foo");
        assertEquals("FOO", arg.resolveMetavar()[0]);
        arg.dest("bar");
        assertEquals("BAR", arg.resolveMetavar()[0]);
        arg.metavar("baz");
        assertEquals("baz", arg.resolveMetavar()[0]);
        arg.choices("alpha", "bravo");
        assertEquals("{alpha,bravo}", arg.resolveMetavar()[0]);
        
        arg = new ArgumentImpl(prefix, "foo");
        assertEquals("foo", arg.resolveMetavar()[0]);
        arg.dest("bar");
        assertEquals("bar", arg.resolveMetavar()[0]);
        arg.metavar("baz");
        assertEquals("baz", arg.resolveMetavar()[0]);
        arg.choices("alpha", "bravo");
        assertEquals("{alpha,bravo}", arg.resolveMetavar()[0]);
    }
    
    @Test
    public void testTextualName() {
        ArgumentImpl arg = new ArgumentImpl(prefix, "f");
        assertEquals("f", arg.textualName());
        arg = new ArgumentImpl(prefix, "-f", "--foo", "--foo-bar");
        assertEquals("-f/--foo/--foo-bar", arg.textualName());
    }

    @Test
    public void testFormatMetavar() {
        ArgumentImpl arg = new ArgumentImpl(prefix, "--foo");
        assertEquals("FOO", arg.formatMetavar());
        arg.dest("BAZ");
        assertEquals("BAZ", arg.formatMetavar());
        arg.metavar("BAR");
        assertEquals("BAR", arg.formatMetavar());
        arg.nargs(3);
        assertEquals("BAR BAR BAR", arg.formatMetavar());
        arg.metavar("ALPHA", "BRAVO", "CHARLIE");
        assertEquals("ALPHA BRAVO CHARLIE", arg.formatMetavar());
        arg.nargs(4);
        assertEquals("ALPHA BRAVO CHARLIE CHARLIE", arg.formatMetavar());
        arg.nargs("?");
        assertEquals("[ALPHA]", arg.formatMetavar());
        arg.nargs("*");
        assertEquals("[ALPHA [BRAVO ...]]", arg.formatMetavar());
        arg.nargs("+");
        assertEquals("ALPHA [BRAVO ...]", arg.formatMetavar());
    }
    
    @Test
    public void testConvert() throws ArgumentParserException {
        ArgumentImpl arg = new ArgumentImpl(prefix, "--foo");
        assertEquals("hello", arg.convert(null, "hello"));
        arg.choices("world");
        try {
            arg.convert(null, "hello");
            fail();
        } catch(ArgumentParserException e) {
            assertEquals("argument --foo: expects value in {world}", e.getMessage());
        }
    }
    
}
