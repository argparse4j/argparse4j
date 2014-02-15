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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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

    /**
     * Language neutral locale. Defined here for Java5.
     */
    public static final Locale LOCALE_ROOT = new Locale("", "", "");

    public static final String LINESEP = System.getProperty("line.separator");

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
        StringBuilder res = new StringBuilder(initialIndent);
        StringBuilder sb = new StringBuilder();
        int currentWidth = initialOffset + initialIndent.length();
        for (int start = iter.first(), end = iter.next(); end != BreakIterator.DONE; start = end, end = iter
                .next()) {
            String sub = s.substring(start, end);
            int subwidth = textWidthCounter.width(sub);
            currentWidth += subwidth;
            if (currentWidth > width) {
                res.append(adjustSpace(sb, width, currentWidth - subwidth))
                        .append(TextHelper.LINESEP).append(subsequentIndent);
                sb.delete(0, sb.length());
                currentWidth = subsequentIndent.length() + subwidth;
            }
            sb.append(sub);
            // What if the application specifies text with line separator \n,
            // while TextHelper.LINESEP is not \n (e.g., \r\n)? Historically, we
            // just checked only \n here. For backward compatibility, We also
            // check that line ends with \n too.
            if (sub.endsWith(TextHelper.LINESEP) || sub.endsWith("\n")) {
                res.append(sb).append(subsequentIndent);
                sb.delete(0, sb.length());
                currentWidth = subsequentIndent.length();
            }
        }
        res.append(sb);
        return res.toString();
    }

    /**
     * Given the maximum line width and current line width in sb, insert white
     * spaces in sb to make it look more "natural". The insertion points are the
     * contagious block of white spaces. Before the processing, leading and
     * trailing white spaces are removed from sb.
     * 
     * @param sb
     *            String to adjust
     * @param width
     *            maximum line width
     * @param curwidth
     *            current line width
     * @return adjusted sb
     */
    public static StringBuilder adjustSpace(StringBuilder sb, int width,
            int curwidth) {
        int i, len = sb.length();
        int origLen = len;
        for (i = 0; i < len && sb.charAt(i) == ' '; ++i)
            ;
        sb.delete(0, i);
        len = sb.length();
        for (i = len - 1; i >= 0 && sb.charAt(i) == ' '; --i)
            ;
        sb.delete(i + 1, len);
        len = sb.length();
        curwidth -= origLen - len;

        int numWsBlock = 0;
        boolean cont = false;
        for (i = 0; i < len; ++i) {
            if (sb.charAt(i) == ' ') {
                if (!cont) {
                    cont = true;
                    ++numWsBlock;
                }
            } else {
                cont = false;
            }
        }
        if (numWsBlock == 0) {
            return sb;
        }
        // Distribute needWs white spaces to numWsBlock blocks.
        // Put one more space to the middle of the blocks to look nicer if
        // needWs is not divisible by numWsBlock.
        int needWs = width - curwidth;
        int eachWs = needWs / numWsBlock;
        int rem = needWs % numWsBlock;
        int remStart = (numWsBlock - rem + 1) / 2;
        int remEnd = remStart + rem;
        cont = false;
        int b = 0;
        for (i = 0; i < len; ++i) {
            if (sb.charAt(i) == ' ') {
                if (!cont) {
                    cont = true;
                    int add = eachWs + (remStart <= b && b < remEnd ? 1 : 0);
                    for (int j = 0; j < add; ++j) {
                        sb.insert(i, ' ');
                    }
                    len = sb.length();
                    ++b;
                }
            } else {
                cont = false;
            }
        }
        return sb;
    }

    public static void printHelp(PrintWriter writer, String title, String help,
            TextWidthCounter textWidthCounter, int width) {
        int INDENT_WIDTH = 25;
        writer.print("  ");
        writer.print(title);
        if (!help.isEmpty()) {
            int titleWidth = textWidthCounter.width(title);
            int indentWidth = INDENT_WIDTH;
            if (titleWidth <= 21) {
                indentWidth -= titleWidth + 2;
            } else {
                writer.println();
            }
            for (int i = 0; i < indentWidth; ++i) {
                writer.print(" ");
            }
            writer.println(wrap(textWidthCounter, help, width, INDENT_WIDTH,
                    "", "                         "));
        } else {
            writer.println();
        }
    }

    public static String nonNull(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    /**
     * From src, find string whose prefix is prefix and store them in List and
     * return it.
     * 
     * @param src
     *            collection contains strings to inspect
     * @param prefix
     *            prefix
     * @return List of strings matched
     */
    public static List<String> findPrefix(Collection<String> src, String prefix) {
        List<String> res = new ArrayList<String>();
        for (String s : src) {
            if (s.startsWith(prefix)) {
                res.add(s);
            }
        }
        return res;
    }
}
