package net.sourceforge.argparse4j.impl.choice;

import static org.junit.Assert.*;

import org.junit.Test;

public class CollectionArgumentChoiceTest {

    private CollectionArgumentChoice<Integer> choice = new CollectionArgumentChoice<Integer>(
            1, 2, 3);

    @Test
    public void testContains() {
        assertTrue(choice.contains(2));
        assertFalse(choice.contains(0));
    }

    @Test
    public void testContainsWithEmptyCollection() {
        CollectionArgumentChoice<Integer> choice = new CollectionArgumentChoice<Integer>();
        assertFalse(choice.contains(0));
        assertFalse(choice.contains("0"));
    }

    @Test
    public void testContainsWithWrongType() {
        try {
            choice.contains("2");
            fail();
        } catch (IllegalArgumentException e) {
            // success
        }
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
