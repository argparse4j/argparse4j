package net.sourceforge.argparse4j.impl.type;

import static org.junit.Assert.*;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.mock.MockArgument;

import org.junit.Test;

public class EnumArgumentTypeTest {

    enum Foo {
        ALPHA, BRAVO, CHARLIE
    }

    @Test
    public void testConvert() throws ArgumentParserException {
        EnumArgumentType<Foo> type = new EnumArgumentType<Foo>(Foo.class);
        assertEquals(Foo.BRAVO, type.convert(null, null, "BRAVO"));
        try {
            type.convert(null, new MockArgument(), "DELTA");
        } catch (ArgumentParserException e) {
            assertEquals(
                    "argument null: could not convert 'DELTA' to enum type (choose from {ALPHA,BRAVO,CHARLIE})",
                    e.getMessage());
        }
    }

}
