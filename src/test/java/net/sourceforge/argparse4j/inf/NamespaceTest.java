package net.sourceforge.argparse4j.inf;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class NamespaceTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGetString() {
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put("string", "string");
        attrs.put("integer", 1000000009);
        Namespace ns = new Namespace(attrs);

        assertEquals("string", ns.getString("string"));
        assertEquals("1000000009", ns.getString("integer"));
    }

}
