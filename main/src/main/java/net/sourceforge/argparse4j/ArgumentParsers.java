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
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.internal.ArgumentParserConfigurationImpl;
import net.sourceforge.argparse4j.internal.ArgumentParserImpl;
import net.sourceforge.argparse4j.internal.TerminalWidth;

/**
 * Factory class to create new ArgumentParser.
 */
public final class ArgumentParsers {

    /**
     * Intentionally made private to avoid to get instantiated in application
     * code.
     */
    private ArgumentParsers() {
    }

    /**
     * Default prefix characters.
     */
    public static final String DEFAULT_PREFIX_CHARS = "-";

    /**
     * <p>
     * Creates {@link ArgumentParserBuilder} with given program name.
     * </p>
     *
     * @param prog
     *         The program name
     * @return ArgumentParserBuilder object
     * @since 0.8.0
     */
    public static ArgumentParserBuilder newFor(String prog) {
        return new ArgumentParserBuilder(prog);
    }

    /**
     * <p>
     * Creates {@link ArgumentParser} with given program name.
     * </p>
     * 
     * <p>
     * This is equivalent with {@code newArgumentParser(prog, true, "-", null)}.
     * </p>
     * 
     * @param prog
     *            The program name
     * @return ArgumentParser object
     * @deprecated This is not thread safe. Use {@link #newFor(String)} instead.
     */
    @Deprecated
    public static ArgumentParser newArgumentParser(String prog) {
        return newArgumentParser(prog, true, DEFAULT_PREFIX_CHARS, null);
    }

    /**
     * <p>
     * Creates {@link ArgumentParser} with given program name and addHelp.
     * </p>
     * 
     * <p>
     * This is equivalent with {@code ArgumentParser(prog, addHelp, "-", null)}.
     * </p>
     * 
     * @param prog
     *            The program name
     * @param addHelp
     *            If true, {@code -h/--help} are available. If false, they are
     *            not.
     * @return ArgumentParser object
     * @deprecated This is not thread safe. Use {@link #newFor(String)} instead.
     */
    @Deprecated
    public static ArgumentParser newArgumentParser(String prog, boolean addHelp) {
        return newArgumentParser(prog, addHelp, DEFAULT_PREFIX_CHARS, null);
    }

    /**
     * <p>
     * Creates {@link ArgumentParser} with given program name, addHelp and
     * prefixChars.
     * </p>
     * 
     * <p>
     * This is equivalent with
     * {@code ArgumentParser(prog, addHelp, prefixChars, null)}.
     * </p>
     * 
     * @param prog
     *            The program name
     * @param addHelp
     *            If true, {@code -h/--help} are available. If false, they are
     *            not.
     * @param prefixChars
     *            The set of characters that prefix named arguments.
     * @return ArgumentParser object.
     * @deprecated This is not thread safe. Use {@link #newFor(String)} instead.
     */
    @Deprecated
    public static ArgumentParser newArgumentParser(String prog,
            boolean addHelp, String prefixChars) {
        return newArgumentParser(prog, addHelp, prefixChars, null);
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
     *            The set of characters that prefix named arguments.
     * @param fromFilePrefix
     *            The set of characters that prefix file path from which
     *            additional arguments should be read. Specify {@code null} to
     *            disable reading arguments from file.
     * @return ArgumentParser object.
     * @deprecated This is not thread safe. Use {@link #newFor(String)} instead.
     */
    @Deprecated
    public static ArgumentParser newArgumentParser(String prog,
            boolean addHelp, String prefixChars, String fromFilePrefix) {
        ArgumentParserConfigurationImpl config = new ArgumentParserConfigurationImpl(
                prog, addHelp, prefixChars, fromFilePrefix, Locale.getDefault(),
                cjkWidthHack_ && cjkWidthLangs_.contains(Locale.getDefault()
                        .getLanguage()) ? new CJKTextWidthCounter() : new ASCIITextWidthCounter(),
                getFormatWidth(), isSingleMetavar(),
                getNoDestConversionForPositionalArgs());
        return new ArgumentParserImpl(config);
    }

    private static final String cjkWidthLangsSrc_[] = { "ja", "zh", "ko" };
    static List<String> cjkWidthLangs_ = Arrays
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
     * @deprecated This is not thread safe. Use {@link #newFor(String)} instead.
     */
    @Deprecated
    public static void setCJKWidthHack(boolean flag) {
        cjkWidthHack_ = flag;
    }

    /**
     * Returns true iff CJK width hack is enabled.
     * 
     * @return {@code true} or {@code false}
     * @deprecated This is not thread safe. Use {@link #newFor(String)} instead.
     */
    @Deprecated
    public static boolean getCjkWidthHack() {
        return cjkWidthHack_;
    }

    private static boolean terminalWidthDetection_ = true;

    /**
     * <p>
     * Set {@code true} to enable terminal width detection.
     * </p>
     * 
     * <p>
     * If this feature is enabled, argparse4j will automatically detect the
     * terminal width and use it to format help messages.
     * </p>
     * 
     * @param flag
     *            {@code true} or {@code false}
     * @deprecated This is not thread safe. Use {@link #newFor(String)} instead.
     */
    @Deprecated
    public static void setTerminalWidthDetection(boolean flag) {
        terminalWidthDetection_ = flag;
    }

    /**
     * Returns true iff terminal width detection is enabled.
     * 
     * @return {@code true} or {@code false}
     * @deprecated This is not thread safe. Use {@link #newFor(String)} instead.
     */
    @Deprecated
    public static boolean getTerminalWidthDetection() {
        return terminalWidthDetection_;
    }

    /**
     * Default format width of text output.
     */
    public static final int DEFAULT_FORMAT_WIDTH = 75;

    /**
     * Returns the width of formatted text. If the terminal width detection is
     * enabled, this method will detect the terminal width automatically and
     * calculate the width based on it. If it is not enabled or auto-detection
     * was failed, the {@link ArgumentParsers#DEFAULT_FORMAT_WIDTH} is returned.
     * 
     * @return the width of formatted text
     * @deprecated This is not thread safe. Use {@link #newFor(String)} instead.
     */
    @Deprecated
    public static int getFormatWidth() {
        if (terminalWidthDetection_) {
            int w = new TerminalWidth().getTerminalWidth() - 5;
            return w <= 0 ? DEFAULT_FORMAT_WIDTH : w;
        } else {
            return DEFAULT_FORMAT_WIDTH;
        }
    }

    private static boolean singleMetavar_ = false;

    /**
     * <p>
     * If singleMetavar is {@code true}, a metavar string in help message is
     * only shown after the last flag instead of each flag.
     * </p>
     * <p>
     * By default and {@code false} is given to this method, a metavar is shown
     * after each flag:
     * </p>
     * 
     * <pre>
     * -f FOO, --foo FOO
     * </pre>
     * <p>
     * If {@code true} is given to this method, a metavar string is shown only
     * once:
     * </p>
     * 
     * <pre>
     * -f, --foo FOO
     * </pre>
     * 
     * @param singleMetavar
     *            Switch to display a metavar only after the last flag.
     * @deprecated This is not thread safe. Use {@link #newFor(String)} instead.
     */
    @Deprecated
    public static void setSingleMetavar(boolean singleMetavar) {
        singleMetavar_ = singleMetavar;
    }

    /**
     * Returns true iff a metavar is shown only after the last flag.
     * 
     * @return {@code true} or {@code false}
     * @deprecated This is not thread safe. Use {@link #newFor(String)} instead.
     */
    @Deprecated
    public static boolean isSingleMetavar() {
        return singleMetavar_;
    }

    private static boolean noDestConversionForPositionalArgs_ = false;

    /**
     * <p>
     * Do not perform any conversion to produce "dest" value (See
     * {@link Argument#getDest()}) from positional argument name.
     * </p>
     * 
     * <p>
     * Prior 0.5.0, no conversion is made to produce "dest" value from
     * positional argument name. Since 0.5.0, "dest" value is generated by
     * replacing "-" with "_" in positional argument name. This is the same
     * conversion rule for named arguments.
     * </p>
     * 
     * <p>
     * By default, this is set to {@code false} (which means, conversion will be
     * done). Application is advised to update its implementation to cope with
     * this change. But if it is not feasible, call this method with
     * {@code true} to turn off the conversion and retain the same behaviour
     * with pre-0.5.0 version.
     * </p>
     * 
     * @param flag
     *            Switch not to perform conversion to produce dest for
     *            positional arguments. If {@code true} is given, no conversion
     *            is made.
     * @deprecated This is not thread safe. Use {@link #newFor(String)} instead.
     */
    @Deprecated
    public static void setNoDestConversionForPositionalArgs(boolean flag)
    {
        noDestConversionForPositionalArgs_ = flag;
    }

    /**
     * Returns {@code true} iff no destination value conversion for positional
     * arguments is enabled.
     *
     * @return {@code true} or {@code false}
     * @deprecated This is not thread safe. Use {@link #newFor(String)} instead.
     */
    @Deprecated
    public static boolean getNoDestConversionForPositionalArgs()
    {
        return noDestConversionForPositionalArgs_;
    }

}
