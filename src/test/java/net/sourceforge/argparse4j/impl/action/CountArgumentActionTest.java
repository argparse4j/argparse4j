package net.sourceforge.argparse4j.impl.action;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.mock.MockArgument;

import org.junit.Before;
import org.junit.Test;

public class CountArgumentActionTest {

    private static class MyMockArgument extends MockArgument {

        @Override
        public String getDest() {
            return "dest";
        }
    }

    private MyMockArgument arg;
    private Map<String, Object> attrs;
    private CountArgumentAction act;

    @Before
    public void setUp() throws Exception {
        act = new CountArgumentAction();
        arg = new MyMockArgument();
        attrs = new HashMap<String, Object>();
        attrs.put(arg.getDest(), 0);
    }

    @Test
    public void testRun() throws ArgumentParserException {
        act.run(null, arg, attrs, "-f", null);
        assertEquals(1, attrs.get(arg.getDest()));
        act.run(null, arg, attrs, "-f", null);
        assertEquals(2, attrs.get(arg.getDest()));
    }
}
