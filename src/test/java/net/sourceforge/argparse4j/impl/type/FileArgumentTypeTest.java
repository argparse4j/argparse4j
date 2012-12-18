package net.sourceforge.argparse4j.impl.type;

import static org.junit.Assert.*;

import java.io.File;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.mock.MockArgument;

import org.junit.Test;

public class FileArgumentTypeTest {

    @Test
    public void testConvert() throws ArgumentParserException {
        FileArgumentType type = new FileArgumentType();
        File file = type.convert(null, null, "foobar");
        assertEquals("foobar", file.getName());
        file = new FileArgumentType().verifyCanRead().acceptSystemIn()
                .convert(null, null, "-");
        assertEquals("-", file.getName());
        try {
            new FileArgumentType().verifyCanRead().convert(null,
                    new MockArgument(), "-");
        } catch (ArgumentParserException e) {
            assertEquals(
                    "argument null: Insufficient permissions to read file: '-'",
                    e.getMessage());
        }
    }

}
