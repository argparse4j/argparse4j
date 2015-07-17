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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * This object performs operations related to prefixChars of option flags.
 * </p>
 * <p>
 * <strong>The application code should not use this class directly.</strong>
 * </p>
 */
public class PrefixPattern {

    private String prefixChars_;
    private Pattern prefixPattern_;

    /**
     * Creates this object using given {@code prefixChars}.
     * 
     * @param prefixChars
     *            The prefixChars
     */
    public PrefixPattern(String prefixChars) {
        prefixChars_ = prefixChars;
        prefixPattern_ = compilePrefixPattern(prefixChars);
    }

    /**
     * Returns {@code true} if flag string {@code str} matches prefixChars.
     * 
     * @param str
     *            The flag string to match
     * @return {@code true} or {@code false}
     */
    public boolean match(String str) {
        Matcher m = prefixPattern_.matcher(str);
        return m.find() && !m.group(0).equals(str);
    }

    /**
     * Returns {@code true} if flag string {@code str} matches prefixChars and
     * it is long flag.
     * 
     * @param str
     *            The flag string to match
     * @return {@code true} or {@code false}
     */
    public boolean matchLongFlag(String str) {
        Matcher m = prefixPattern_.matcher(str);
        return m.find() && !m.group(0).equals(str) && m.group(0).length() >= 2;
    }

    /**
     * Returns {@code true} if flag string {@code str} matches prefixChars and
     * it is short flag, that is, its matched prefix length must be 1.
     * 
     * @param str
     *            The flag string to match
     * @return {@code true} or {@code false}
     */
    public boolean matchShortFlag(String str) {
        Matcher m = prefixPattern_.matcher(str);
        return m.find() && !m.group(0).equals(str) && m.group(0).length() == 1;
    }

    /**
     * <p>
     * Removes prefixChars from given flag string.
     * </p>
     * <p>
     * If given flag string does not contains prefixChars, it is returned as is.
     * </p>
     * 
     * @param str
     *            The flag string
     * @return The string after prefixChars are removed from {@code str}
     */
    public String removePrefix(String str) {
        Matcher m = prefixPattern_.matcher(str);
        if (m.find() && !m.group(0).equals(str)) {
            return m.replaceFirst("");
        } else {
            return str;
        }
    }

    /**
     * Returns prefixChars with this object constructed.
     * 
     * @return prefixChars
     */
    public String getPrefixChars() {
        return prefixChars_;
    }

    /**
     * Returns compiled regular expression pattern of prefixChars.
     * 
     * @return The compiled regular expression pattern of prefixChars.
     */
    public Pattern getPrefixPattern() {
        return prefixPattern_;
    }

    private static Pattern compilePrefixPattern(String prefixChars) {
        String qs = Pattern.quote(prefixChars);
        return Pattern.compile("^[" + qs + "]+");
    }

}
