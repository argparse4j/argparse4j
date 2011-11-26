package net.sourceforge.argparse4j.impl.action;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.mock.MockArgument;

import org.junit.Before;
import org.junit.Test;

public class AppendConstArgumentActionTest {

    private static class MyMockArgument extends MockArgument {
        
        @Override
        public String getDest() {
            return "dest";
        }

        @Override
        public Object getConst() {
            return "const";
        }
        
    }
    
    private MyMockArgument arg = new MyMockArgument();
    private AppendConstArgumentAction act = new AppendConstArgumentAction();
    private Map<String, Object> attrs;
    
    @Before
    public void setup() {
        attrs = new HashMap<String, Object>();
    }
    
    @Test
    public void testRun() throws ArgumentParserException {
        act.run(null, arg, attrs, null, null);
        assertEquals(Arrays.asList("const"), attrs.get("dest"));
        act.run(null, arg, attrs, null, null);
        assertEquals(Arrays.asList("const", "const"), attrs.get("dest"));
    }

    @Test
    public void testRunWithDefaultNonList() throws ArgumentParserException {
        attrs.put("dest", "default");
        act.run(null, arg, attrs, null, null);
        assertEquals(Arrays.asList("const"), attrs.get("dest"));
    }

}
