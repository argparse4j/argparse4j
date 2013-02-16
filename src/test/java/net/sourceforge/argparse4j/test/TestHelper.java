package net.sourceforge.argparse4j.test;

import java.util.Arrays;
import java.util.List;

public class TestHelper {

    /**
     * Returns args as List<T>
     * 
     * @param args
     * @return
     */
    public static <T> List<T> list(T... args) {
        return Arrays.asList(args);
    }

}
