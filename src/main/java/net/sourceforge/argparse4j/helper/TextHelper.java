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
import java.util.Iterator;

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
        if (a.length - offset > 0) {
            sb.append(a[offset]);
            for (int i = offset + 1, len = a.length; i < len; ++i) {
                sb.append(sep).append(a[i]);
            }
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
        Iterator<T> it;
        for (it = a.iterator(); offset > 0 && it.hasNext(); --offset, it.next())
            ;
        if (offset == 0 && it.hasNext()) {
            sb.append(it.next());
            while (it.hasNext()) {
                sb.append(sep).append(it.next());
            }
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
        BreakIterator iter = BreakIterator.getLineInstance();
        iter.setText(s);
        StringBuilder sb = new StringBuilder(initialIndent);
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
            if (sub.endsWith("\n")) {
                sb.append(subsequentIndent);
                currentWidth = subsequentIndent.length();
            }
        }

        return sb.toString();
    }

    public static void printHelp(PrintWriter writer, String title, String help,
            TextWidthCounter textWidthCounter, int width) {
        int INDENT_WIDTH = 25;
        writer.format("  %s", title);
        if (!help.isEmpty()) {
            int titleWidth = textWidthCounter.width(title);
            int indentWidth = INDENT_WIDTH;
            if (titleWidth <= 21) {
                indentWidth -= titleWidth + 2;
            } else {
                writer.write("\n");
            }
            String fmt = String.format("%%%ds%%s\n", indentWidth);
            writer.format(
                    fmt,
                    "",
                    wrap(textWidthCounter, help, width, INDENT_WIDTH, "",
                            "                         "));
        } else {
            writer.write("\n");
        }
    }

    public static String nonNull(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

}
