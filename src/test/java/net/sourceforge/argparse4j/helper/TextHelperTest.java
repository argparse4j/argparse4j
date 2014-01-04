package net.sourceforge.argparse4j.helper;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Locale;

import org.junit.Test;

public class TextHelperTest {

    @Test
    public void testConcat() {
        assertEquals("", TextHelper.concat(new String[] {}, 0, ","));
        assertEquals("", TextHelper.concat(new String[] {""}, 0, ","));
        assertEquals("alpha",
                TextHelper.concat(new String[] { "alpha" }, 0, ","));
        assertEquals("1, 2, 3",
                TextHelper.concat(new Integer[] { 1, 2, 3 }, 0, ", "));
        assertEquals("2, 3",
                TextHelper.concat(new Integer[] { 1, 2, 3 }, 1, ", "));
        assertEquals("3",
                TextHelper.concat(new Integer[] { 1, 2, 3 }, 2, ", "));
        assertEquals("",
                TextHelper.concat(new Integer[] { 1, 2, 3 }, 3, ", "));

        assertEquals("", TextHelper.concat(Arrays.asList(), 0, ","));
        assertEquals("", TextHelper.concat(Arrays.asList(""), 0, ","));
        assertEquals("alpha",
                TextHelper.concat(Arrays.asList("alpha"), 0, ","));
        assertEquals("1, 2, 3",
                TextHelper.concat(Arrays.asList(1, 2, 3), 0, ", "));
        assertEquals("2, 3",
                TextHelper.concat(Arrays.asList(1, 2, 3), 1, ", "));
        assertEquals("3",
                TextHelper.concat(Arrays.asList(1, 2, 3), 2, ", "));
        assertEquals("",
                TextHelper.concat(Arrays.asList(1, 2, 3), 3, ", "));
    }

    @Test
    public void testTextWrap() {
        String s = String.format(
                   (Locale) null,
                   "alpha bravo charlie delta echo foxtrot golf hotel india%n"
                 + "juliet kilo lima");
        assertEquals(String.format(
                     (Locale) null,
                     "        alpha  bravo%n"
                   + "    charlie    delta%n"
                   + "    echo     foxtrot%n"
                   + "    golf       hotel%n"
                   + "    india%n"
                   + "    juliet kilo lima"),
                   TextHelper.wrap(
                           new ASCIITextWidthCounter(), s, 20, 0, "        ", "    "));
    }

    @Test
    public void testAdjustSpace() {
        StringBuilder s1 = new StringBuilder("  Do you like Java language?  ");
        assertEquals("Do  you  like  Java  language?",
                TextHelper.adjustSpace(s1, 30, 30).toString());
        assertEquals("Do  you  like   Java  language?",
                TextHelper.adjustSpace(s1, 31, 30).toString());
        StringBuilder s2 = new StringBuilder();
        assertEquals("", TextHelper.adjustSpace(s2, 30, 30).toString());
        StringBuilder s3 = new StringBuilder("The Argparse4j");
        assertEquals("The       Argparse4j", TextHelper.adjustSpace(s3, 20, 14).toString());
    }
}
