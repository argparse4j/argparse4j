/*
 * Copyright (C) 2011 Tatsuhiro Tsujikawa
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.sourceforge.argparse4j.helper;

import java.io.PrintWriter;
import java.text.BreakIterator;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * This class provides various helper function related to text processing.
 * </p>
 * <p>
 * <strong>The application code should not use this class directly.</strong>
 * </p>
 * 
 */
public final class TextHelper {

    private TextHelper() {
    }

    public static <T> String concat(T a[], int offset, String sep,
            String start, String end) {
        StringBuilder sb = new StringBuilder();
        sb.append(start);
        for (int i = offset, len = a.length; i < len; ++i) {
            sb.append(a[i]).append(sep);
        }
        if (sb.length() > sep.length()) {
            sb.delete(sb.length() - sep.length(), sb.length());
        }
        sb.append(end);
        return sb.toString();
    }

    public static <T> String concat(T a[], int offset, String sep) {
        return concat(a, offset, sep, "", "");
    }

    public static <T> String concat(Collection<T> a, int offset, String sep,
            String start, String end) {
        StringBuilder sb = new StringBuilder();
        sb.append(start);
        for (T o : a) {
            sb.append(o).append(sep);
        }
        if (sb.length() > sep.length()) {
            sb.delete(sb.length() - sep.length(), sb.length());
        }
        sb.append(end);
        return sb.toString();
    }

    public static <T> String concat(Collection<T> a, int offset, String sep) {
        return concat(a, offset, sep, "", "");
    }

    public static String wrap(TextWidthCounter textWidthCounter, String s,
            int width, int initialOffset, String initialIndent,
            String subsequentIndent) {
        s = removeLineSeparator(s);
        BreakIterator iter = BreakIterator.getLineInstance();
        iter.setText(s);
        StringBuffer sb = new StringBuffer(initialIndent);
        int currentWidth = initialOffset + initialIndent.length();
        for (int start = iter.first(), end = iter.next(); end != BreakIterator.DONE; start = end, end = iter
                .next()) {
            String sub = s.substring(start, end);
            int subwidth = textWidthCounter.width(sub);
            currentWidth += subwidth;
            if (currentWidth > width) {
                sb.append("\n").append(subsequentIndent);
                currentWidth = subsequentIndent.length() + subwidth;
            }
            sb.append(sub);
        }

        return sb.toString();
    }

    public static void printHelp(PrintWriter writer, String title, String help,
            TextWidthCounter textWidthCounter, int width) {
        if (textWidthCounter.width(title) <= 21) {
            writer.format(
                    "  %-22s %s\n",
                    title,
                    wrap(textWidthCounter, help, width, 25, "",
                            "                         "));
        } else {
            writer.format(
                    "  %s\n                         %s\n",
                    title,
                    wrap(textWidthCounter, help, width, 25, "",
                            "                         "));
        }
    }

    public static String nonNull(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    private static final Pattern lineSepPat_ = Pattern.compile("(\r\n|\r|\n)");

    public static String removeLineSeparator(String str) {
        Matcher m = lineSepPat_.matcher(str);
        return m.replaceAll(" ");
    }
}
