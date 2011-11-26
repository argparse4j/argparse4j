package net.sourceforge.argparse4j.impl.action;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.mock.MockArgument;

import org.junit.Before;
import org.junit.Test;

public class AppendArgumentActionTest {

    private static class MyMockArgument extends MockArgument {
        
        @Override
        public String getDest() {
            return "dest";
        }
        
    }
    
    private MyMockArgument arg = new MyMockArgument();
    private AppendArgumentAction act = new AppendArgumentAction();
    private Map<String, Object> attrs;
    @Before
    public void setup() {
        attrs = new HashMap<String, Object>();
    }
    
    @Test
    public void testRun() throws ArgumentParserException {
        act.run(null, arg, attrs, null, "hello");
        assertEquals(Arrays.asList("hello"), attrs.get("dest"));
        act.run(null, arg, attrs, null, "world");
        assertEquals(Arrays.asList("hello", "world"), attrs.get("dest"));
    }

    @Test
    public void testRunWithDefaultNonList() throws ArgumentParserException {
        attrs.put("dest", "default");
        act.run(null, arg, attrs, null, "hello");
        assertEquals(Arrays.asList("hello"), attrs.get("dest"));
    }
}
