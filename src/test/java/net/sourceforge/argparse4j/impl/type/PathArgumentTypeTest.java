package net.sourceforge.argparse4j.impl.type;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.mock.MockArgument;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;


public class PathArgumentTypeTest {
    String string1 = "/path1";
    String string2 = "/path1//path2";
    PathArgumentType pathArgumentType;
    Path path1;
    Path path2;

    @Before
    public void setup() throws Exception {
        Files.createDirectories(Paths.get(string2));

        pathArgumentType = new PathArgumentType();
        path1 = pathArgumentType.convert(null, null, string1);
        path2 = pathArgumentType.convert(null, null, string2);

        assertEquals(true, Files.exists(path1));
        assertEquals(true, Files.exists(path2));
    }

    @After
    public void teardown() throws Exception {
        Files.delete(path2);
        Files.delete(path1);
        pathArgumentType = null;

        assertEquals(false, Files.exists(path1));
        assertEquals(false, Files.exists(path2));
        assertEquals(null, pathArgumentType);
    }

    @Test
    public void verifyMinDepth() throws Exception {
        assertEquals(Paths.get(string1).toString(), path1.toString());
        assertEquals(Paths.get(string2).toString(), path2.toString());
        assertEquals(2, path2.getNameCount());

        try {
            new PathArgumentType().verifyMinDepth().withPathMinDepth(3).convert(null,
                    new MockArgument(), string2);
        } catch (ArgumentParserException e) {
            assertEquals(
                    "argument null: Does not meet depth=3 requirement for path: '\\path1\\path2' ",
                    e.getMessage());
        }
    }

    @Test
    public void verifyIsEmpty() throws Exception {
        try {
            new PathArgumentType().verifyIsEmpty().convert(null, new MockArgument(), string1);
        } catch (ArgumentParserException e) {
            assertEquals("argument null: Path is not empty: '\\path1' ", e.getMessage());
        }
    }

}
