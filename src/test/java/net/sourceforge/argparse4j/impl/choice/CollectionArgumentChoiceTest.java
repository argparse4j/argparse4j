package net.sourceforge.argparse4j.impl.choice;

import static org.junit.Assert.*;

import org.junit.Test;

public class CollectionArgumentChoiceTest {

    private CollectionArgumentChoice choice = new CollectionArgumentChoice(1, 2, 3);
    
    @Test
    public void testContains() {
        assertTrue(choice.contains(2));
        assertFalse(choice.contains(0));
    }

    @Test
    public void testTextualFormat() {
        assertEquals("{1,2,3}", choice.textualFormat());
    }

    @Test
    public void testToString() {
        assertEquals("{1,2,3}", choice.toString());
    }

}
