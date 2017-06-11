package net.sourceforge.argparse4j.ext.java7;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.util.Locale;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.ext.java7.mock.MockArgument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

import org.junit.Test;

public class PathArgumentTypeTest {

    private static final String ALWAYS_INVALID_PATH = "<always invalid>";

    @Test
    public void testConvert() throws ArgumentParserException {
        PathArgumentType type = new PathArgumentType();
        Path path = type.convert(null, null, "foobar");
        assertEquals("foobar", path.getFileName().toString());
        path = new PathArgumentType().verifyCanRead().acceptSystemIn()
                .convert(null, null, "-");
        assertEquals("-", path.getFileName().toString());
        try {
            ArgumentParser ap = ArgumentParsers
                    .newFor("argparse4j").locale(Locale.US).build();
            new PathArgumentType().verifyCanRead().convert(ap,
                    new MockArgument(), "-");
        } catch (ArgumentParserException e) {
            assertEquals(
                    "argument null: Insufficient permissions to read file: '-'",
                    e.getMessage());
        }
    }

    @Test(expected = ArgumentParserException.class)
    public void testInvalidPath() throws ArgumentParserException {
        ArgumentParser ap = ArgumentParsers
                .newFor("argparse4j").locale(Locale.US).build();
        PathArgumentType type = new PathArgumentType(
                new AlwaysInvalidPathFileSystem());
        type.convert(ap, new MockArgument(), ALWAYS_INVALID_PATH);
    }

    @Test
    public void testInvalidPathErrorMessage() {
        try {
            ArgumentParser ap = ArgumentParsers
                    .newFor("argparse4j").locale(Locale.US).build();
            PathArgumentType type = new PathArgumentType(
                    new AlwaysInvalidPathFileSystem());
            type.convert(ap, new MockArgument(), ALWAYS_INVALID_PATH);
        } catch (ArgumentParserException e) {
            assertEquals(
                    "argument null: could not convert '<always invalid>' to path",
                    e.getMessage());
        }
    }
}
