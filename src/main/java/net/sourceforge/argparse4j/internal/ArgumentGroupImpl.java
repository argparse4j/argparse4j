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
package net.sourceforge.argparse4j.internal;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.inf.ArgumentGroup;

/**
 * <strong>The application code must not use this class directly.</strong>
 *
 */
public final class ArgumentGroupImpl implements ArgumentGroup {

    private String title_ = "";
    private String description_ = "";
    private ArgumentParserImpl argumentParser_;
    private List<ArgumentImpl> args_ = new ArrayList<ArgumentImpl>();

    public ArgumentGroupImpl(ArgumentParserImpl argumentParser, String title) {
        argumentParser_ = argumentParser;
        title_ = TextHelper.nonNull(title);
    }

    @Override
    public ArgumentGroup description(String description) {
        description_ = TextHelper.nonNull(description);
        return this;
    }

    @Override
    public ArgumentImpl addArgument(String... nameOrFlags) {
        ArgumentImpl arg = argumentParser_.addArgument(this, nameOrFlags);
        args_.add(arg);
        return arg;
    }

    public void printHelp(PrintWriter writer) {
        if (!title_.isEmpty()) {
            writer.format("%s:\n", title_);
        }
        if (!description_.isEmpty()) {
            writer.format("  %s\n\n", TextHelper.wrap(
                    argumentParser_.getTextWidthCounter(), description_,
                    ArgumentParserImpl.FORMAT_WIDTH, 2, "", "  "));
        }
        for (ArgumentImpl arg : args_) {
            arg.printHelp(writer, argumentParser_.isDefaultHelp(),
                    argumentParser_.getTextWidthCounter(),
                    ArgumentParserImpl.FORMAT_WIDTH);
        }
    }
}
