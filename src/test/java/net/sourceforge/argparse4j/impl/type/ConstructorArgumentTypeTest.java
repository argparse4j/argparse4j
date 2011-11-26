package net.sourceforge.argparse4j.impl.type;

import static org.junit.Assert.*;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.mock.MockArgument;

import org.junit.Test;

public class ConstructorArgumentTypeTest {

    @Test
    public void testConvert() throws ArgumentParserException {
        ConstructorArgumentType at = new ConstructorArgumentType(Integer.class);
        assertEquals(100, at.convert(null, null, "100"));
        try {
            at.convert(null, new MockArgument(), "0x100");
            fail();
        } catch(ArgumentParserException e) {
        }
    }

}
