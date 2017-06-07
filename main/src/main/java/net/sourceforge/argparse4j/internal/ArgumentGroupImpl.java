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
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;

/**
 * <strong>The application code must not use this class directly.</strong>
 * 
 * This class implements both mutually exclusive group and just a conceptual
 * group.
 */
public final class ArgumentGroupImpl implements ArgumentGroup,
        MutuallyExclusiveGroup {

    /**
     * Index in {@link ArgumentParserImpl}.
     */
    private int index_;
    private String title_ = "";
    private String description_ = "";
    private ArgumentParserImpl argumentParser_;
    private List<ArgumentImpl> args_ = new ArrayList<ArgumentImpl>();
    /**
     * true if this is a mutually exclusive group.
     */
    private boolean mutex_ = false;
    /**
     * true if one of the arguments in this group must be specified.
     */
    private boolean required_ = false;

    public ArgumentGroupImpl(ArgumentParserImpl argumentParser, String title) {
        argumentParser_ = argumentParser;
        title_ = TextHelper.nonNull(title);
    }

    @Override
    public ArgumentGroupImpl description(String description) {
        description_ = TextHelper.nonNull(description);
        return this;
    }

    @Override
    public ArgumentImpl addArgument(String... nameOrFlags) {
        ArgumentImpl arg = argumentParser_.addArgument(this, nameOrFlags);
        args_.add(arg);
        return arg;
    }

    @Override
    public ArgumentGroupImpl required(boolean required) {
        required_ = required;
        return this;
    }

    public void printHelp(PrintWriter writer, int format_width) {
        if (!title_.isEmpty()) {
            writer.print(title_);
            writer.println(":");
        }
        if (!description_.isEmpty()) {
            writer.print("  ");
            writer.println(TextHelper.wrap(
                    argumentParser_.getTextWidthCounter(), description_,
                    format_width, 2, "", "  "));
            writer.println();
        }
        for (ArgumentImpl arg : args_) {
            arg.printHelp(writer, argumentParser_.isDefaultHelp(),
                    argumentParser_.getTextWidthCounter(), format_width);
        }
    }

    public int getIndex() {
        return index_;
    }

    public void setIndex(int index) {
        index_ = index;
    }

    public boolean isMutex() {
        return mutex_;
    }

    public void setMutex(boolean mutex) {
        mutex_ = mutex;
    }

    public boolean isRequired() {
        return required_;
    }

    public List<ArgumentImpl> getArgs() {
        return args_;
    }

    /**
     * Returns true if the help message for this group should be in separate
     * group.
     * 
     * @return true if the help message for this group should be in separate
     *         group.
     */
    public boolean isSeparateHelp() {
        return !mutex_ || !title_.isEmpty() || !description_.isEmpty();
    }
}
