package net.sourceforge.argparse4j.helper;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.argparse4j.helper.ReflectHelper;

import org.junit.Test;

public class ReflectHelperTest {

    @Test
    public void testList2Array() {
        int a1[] = (int[]) ReflectHelper.list2Array(int[].class, list(1, 2, 3));
        assertArrayEquals(new int[] { 1, 2, 3 }, a1);
        int a2[][] = (int[][]) ReflectHelper.list2Array(int[][].class,
                list(list(1, 2), list(3, 4)));
        assertArrayEquals(
                new int[][] { new int[] { 1, 2 }, new int[] { 3, 4 } }, a2);
    }

    private <T> List<T> list(T... args) {
        return Arrays.asList(args);
    }
}
