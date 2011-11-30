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
package net.sourceforge.argparse4j;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import net.sourceforge.argparse4j.helper.ASCIITextWidthCounter;
import net.sourceforge.argparse4j.helper.CJKTextWidthCounter;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.internal.ArgumentParserImpl;

/**
 * Factory class to create new ArgumentParser.
 * 
 */
public final class ArgumentParsers {

    /**
     * Intentionally made private to avoid to get instantiated in application
     * code.
     */
    private ArgumentParsers() {
    }

    /**
     * <p>
     * Creates {@link ArgumentParser} with given program name.
     * </p>
     * 
     * <p>
     * This is equivalent with {@code newArgumentParser(prog, true, "-")}.
     * </p>
     * 
     * @param prog
     *            The program name
     * @return ArgumentParser object
     */
    public static ArgumentParser newArgumentParser(String prog) {
        return newArgumentParser(prog, true, ArgumentParserImpl.PREFIX_CHARS);
    }

    /**
     * <p>
     * Creates {@link ArgumentParser} with given program name and addHelp.
     * </p>
     * 
     * <p>
     * This is equivalent with {@code ArgumentParser(prog, addHelp, "-")}.
     * </p>
     * 
     * @param prog
     *            The program name
     * @param addHelp
     *            If true, {@code -h/--help} are available. If false, they are
     *            not.
     * @return ArgumentParser object
     */
    public static ArgumentParser newArgumentParser(String prog, boolean addHelp) {
        return newArgumentParser(prog, addHelp, ArgumentParserImpl.PREFIX_CHARS);
    }

    /**
     * <p>
     * Creates {@link ArgumentParser} with given program name, addHelp and
     * prefixChars.
     * </p>
     * 
     * @param prog
     *            The program name
     * @param addHelp
     *            If true, {@code -h/--help} are available. If false, they are
     *            not.
     * @param prefixChars
     *            The set of characters that prefix optional arguments.
     * @return ArgumentParser object.
     */
    public static ArgumentParser newArgumentParser(String prog,
            boolean addHelp, String prefixChars) {
        return new ArgumentParserImpl(prog, addHelp, prefixChars,
                cjkWidthHack_
                        && cjkWidthLangs_.contains(Locale.getDefault()
                                .getLanguage()) ? new CJKTextWidthCounter()
                        : new ASCIITextWidthCounter());
    }

    private static final String cjkWidthLangsSrc_[] = { "ja", "zh", "ko" };
    private static List<String> cjkWidthLangs_ = Arrays
            .asList(cjkWidthLangsSrc_);

    private static boolean cjkWidthHack_ = true;

    /**
     * <p>
     * Set {@code true} to enable CJK width hack.
     * </p>
     * 
     * <p>
     * The CJK width hack is treat Unicode characters having East Asian Width
     * property Wide/Full/Ambiguous to have twice a width of ascii characters
     * when formatting help message if locale is "ja", "zh" or "ko". This
     * feature is enabled by default.
     * </p>
     * 
     * @param flag
     *            {@code true} or {@code false}
     */
    public static void setCJKWidthHack(boolean flag) {
        cjkWidthHack_ = flag;
    }

    /**
     * Returns true iff CJK width hack is enabled.
     * 
     * @return {@code true} or {@code false}
     */
    public static boolean getCjkWidthHack() {
        return cjkWidthHack_;
    }
}
