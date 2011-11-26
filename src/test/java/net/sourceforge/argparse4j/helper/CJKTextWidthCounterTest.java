package net.sourceforge.argparse4j.helper;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CJKTextWidthCounterTest {

    private CJKTextWidthCounter counter;

    @Before
    public void setup() {
        counter = new CJKTextWidthCounter();
    }

    @Test
    public void testWidth() {
        String s = "こんにちは世界€ Hello world";
        assertEquals(16+12, counter.width(s));
    }

}
