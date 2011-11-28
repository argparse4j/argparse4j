package net.sourceforge.argparse4j.impl.choice;

import static org.junit.Assert.*;

import org.junit.Test;

public class RangeArgumentChoiceTest {

    private RangeArgumentChoice<Integer> choice = new RangeArgumentChoice<Integer>(0, 255);
    
    @Test
    public void testContains() {
        assertFalse(choice.contains(-1));
        assertTrue(choice.contains(0));
        assertTrue(choice.contains(10));
        assertTrue(choice.contains(255));
        assertFalse(choice.contains(256));
    }

    @Test
    public void testContainsWithWrongType() {
        try {
            choice.contains("10");
            fail();
        } catch(IllegalArgumentException e) {
            // success
        }
    }

    @Test
    public void testTextualFormat() {
        assertEquals("{0..255}", choice.textualFormat());
        assertEquals("{0.3..0.9}", new RangeArgumentChoice<Double>(0.3, 0.9).textualFormat());
        assertEquals("{a..z}", new RangeArgumentChoice<String>("a", "z").textualFormat());
    }

    @Test
    public void testToString() {
        assertEquals("{0..255}", choice.toString());
    }

}
