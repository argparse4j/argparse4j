package net.sourceforge.argparse4j.impl.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.mock.MockArgument;

import org.junit.Test;

public class EnumStringArgumentTypeTest {

    enum Lang {
        PYTHON, JAVA, CPP {
            @Override
            public String toString() {
                return "C++";
            }
        }
    }

    @Test
    public void testConvert() throws ArgumentParserException {
        EnumStringArgumentType<Lang> type = EnumStringArgumentType.forEnum(Lang.class);
        assertEquals(Lang.PYTHON, type.convert(null, null, "PYTHON"));
        assertEquals(Lang.JAVA, type.convert(null, null, "JAVA"));
        assertEquals(Lang.CPP, type.convert(null, null, "C++"));
    }

    @Test
    public void testConvertErrorsWithUnknownMember() throws ArgumentParserException {
        EnumStringArgumentType<Lang> type = EnumStringArgumentType.forEnum(Lang.class);
        try {
            type.convert(null, new MockArgument(), "CPP");
            fail("Expected ArgumentParserException to be thrown");
        } catch (ArgumentParserException e) {
            assertEquals(
                    "argument null: could not convert 'CPP' (choose from {PYTHON,JAVA,C++})",
                    e.getMessage());
        }
    }
}
