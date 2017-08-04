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
package net.sourceforge.argparse4j.inf;

import java.util.Locale;

/**
 * The exception thrown from {@link ArgumentParser#parseArgs(String[])} if error
 * occurred while processing command line argument. The argument {@code parser}
 * in constructor is the ArgumentParser object where an error occurred.
 */
public class ArgumentParserException extends Exception {

    private static final long serialVersionUID = 1L;
    private transient ArgumentParser parser_;

    public ArgumentParserException(ArgumentParser parser) {
        super();
        parser_ = parser;
    }

    public ArgumentParserException(String message, ArgumentParser parser) {
        super(message);
        parser_ = parser;
    }

    public ArgumentParserException(String message, Throwable cause,
            ArgumentParser parser) {
        super(message, cause);
        parser_ = parser;
    }

    public ArgumentParserException(Throwable cause, ArgumentParser parser) {
        super(cause);
        parser_ = parser;
    }

    public ArgumentParserException(String message, ArgumentParser parser,
            Argument arg) {
        super(formatMessage(message, arg, parser.getConfig()));
        parser_ = parser;
    }

    public ArgumentParserException(String message, Throwable cause,
            ArgumentParser parser, Argument arg) {
        super(formatMessage(message, arg, parser.getConfig()), cause);
        parser_ = parser;
    }

    private static String formatMessage(String message, Argument arg,
            ArgumentParserConfiguration config) {
        return String.format(Locale.ROOT,
                config.getResourceBundle().getString("argument"),
                arg.textualName(), message);
    }

    public ArgumentParser getParser() {
        return parser_;
    }
}
