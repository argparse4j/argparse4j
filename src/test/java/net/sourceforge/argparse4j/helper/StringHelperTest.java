package net.sourceforge.argparse4j.helper;

import static org.junit.Assert.*;

import net.sourceforge.argparse4j.helper.TextHelper;

import org.junit.Test;

public class StringHelperTest {

    @Test
    public void testConcat() {
        assertEquals("", TextHelper.concat(new String[] {}, 0, ","));
        assertEquals("alpha",
                TextHelper.concat(new String[] { "alpha" }, 0, ","));
        assertEquals("1, 2, 3",
                TextHelper.concat(new Integer[] { 1, 2, 3 }, 0, ", "));
    }

    @Test
    public void testTextWrap() {
        String s = "alpha bravo charlie delta echo foxtrot golf hotel india juliet";
        assertEquals("        alpha bravo \n" + "    charlie delta \n"
                + "    echo foxtrot \n" + "    golf hotel \n"
                + "    india juliet", TextHelper.wrap(
                new ASCIITextWidthCounter(), s, 20, 0, "        ", "    "));
    }

    @Test
    public void testRemoveLineSeparator() {
        String s = "\nalpha\r\r\n\r\nbravo\r";
        assertEquals(" alpha   bravo ", TextHelper.removeLineSeparator(s));
    }
}
