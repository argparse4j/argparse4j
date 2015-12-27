package net.sourceforge.argparse4j.impl.type;

import static org.junit.Assert.*;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.mock.MockArgument;

import org.junit.Before;
import org.junit.Test;

public class BooleanArgumentTypeTest {

    private MockArgument ma;

    @Before
    public void setup() throws Exception {
        ma = new MockArgument();
    }

    @Test
    public void testConvert() throws ArgumentParserException {
        BooleanArgumentType t = new BooleanArgumentType();

        assertEquals(Boolean.TRUE, t.convert(null, ma, "true"));
        assertEquals(Boolean.FALSE, t.convert(null, ma, "false"));

        try {
            t.convert(null, ma, "TRUE");
            fail();
        } catch (ArgumentParserException e) {
            assertEquals(
                    "argument null: could not convert 'TRUE' (choose from {true,false})",
                    e.getMessage());
        }

        t = new BooleanArgumentType("yes", "no");

        assertEquals(Boolean.TRUE, t.convert(null, ma, "yes"));
        assertEquals(Boolean.FALSE, t.convert(null, ma, "no"));

        try {
            t.convert(null, ma, "Yes");
            fail();
        } catch (ArgumentParserException e) {
            assertEquals(
                    "argument null: could not convert 'Yes' (choose from {yes,no})",
                    e.getMessage());
        }
    }

    @Test
    public void testInferMetavar() {
        BooleanArgumentType t = new BooleanArgumentType("yes", "no");

        assertArrayEquals(new String[] { "{yes,no}" }, t.inferMetavar());
    }

}
