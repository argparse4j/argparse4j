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
import java.util.Map;

import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.helper.TextWidthCounter;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

/**
 * <strong>The application code must not use this class directly.</strong>
 *
 */
public final class SubparserImpl implements Subparser {

    private String command_;
    private ArgumentParserImpl parser_;
    private String help_ = "";

    public SubparserImpl(String prog, boolean addHelp, String prefixChars,
            TextWidthCounter textWidthCounter, String command,
            ArgumentParserImpl mainParser) {
        command_ = command;
        parser_ = new ArgumentParserImpl(prog, addHelp, prefixChars,
                textWidthCounter, command, mainParser);
    }

    @Override
    public Argument addArgument(String... nameOrFlags) {
        return parser_.addArgument(nameOrFlags);
    }

    @Override
    public Subparsers addSubparsers() {
        throw new UnsupportedOperationException(
                "Subparser does not support addSubparsers() method");
    }

    @Override
    public ArgumentGroup addArgumentGroup(String title) {
        return parser_.addArgumentGroup(title);
    }

    @Override
    public Subparser description(String description) {
        parser_.description(description);
        return this;
    }

    @Override
    public Subparser epilog(String epilog) {
        parser_.epilog(epilog);
        return this;
    }

    @Override
    public Subparser version(String version) {
        parser_.version(version);
        return this;
    }

    @Override
    public Subparser defaultHelp(boolean defaultHelp) {
        parser_.defaultHelp(defaultHelp);
        return this;
    }

    @Override
    public void printHelp() {
        parser_.printHelp();
    }

    @Override
    public void printHelp(PrintWriter writer) {
        parser_.printHelp(writer);
    }

    @Override
    public String formatHelp() {
        return parser_.formatHelp();
    }

    @Override
    public void printUsage() {
        parser_.printUsage();
    }

    @Override
    public void printUsage(PrintWriter writer) {
        parser_.printUsage(writer);
    }

    @Override
    public String formatUsage() {
        return parser_.formatUsage();
    }

    @Override
    public void printVersion() {
        parser_.printVersion();
    }

    @Override
    public void printVersion(PrintWriter writer) {
        parser_.printVersion(writer);
    }

    @Override
    public String formatVersion() {
        return parser_.formatVersion();
    }

    @Override
    public Subparser setDefault(String dest, Object value) {
        parser_.setDefault(dest, value);
        return this;
    }

    @Override
    public Subparser setDefaults(Map<String, Object> attrs) {
        parser_.setDefaults(attrs);
        return this;
    }

    @Override
    public Object getDefault(String dest) {
        return parser_.getDefault(dest);
    }

    @Override
    public Namespace parseArgs(String[] args) throws ArgumentParserException {
        throw new UnsupportedOperationException(
                "Subparser does not support parseArgs() method");
    }

    @Override
    public void parseArgs(String[] args, Map<String, Object> attrs) {
        throw new UnsupportedOperationException(
                "Subparser does not support parseArgs() method");
    }

    @Override
    public void parseArgs(String[] args, Object userData)
            throws ArgumentParserException {
        throw new UnsupportedOperationException(
                "Subparser does not support parseArgs() method");
    }

    @Override
    public void parseArgs(String[] args, Map<String, Object> attrs,
            Object userData) throws ArgumentParserException {
        throw new UnsupportedOperationException(
                "Subparser does not support parseArgs() method");
    }

    @Override
    public void handleError(ArgumentParserException e) {
        throw new UnsupportedOperationException(
                "Subparser does not support handleError() method");
    }

    @Override
    public Subparser help(String help) {
        help_ = TextHelper.nonNull(help);
        return this;
    }

    public void parseArgs(String args[], int offset, Map<String, Object> opts)
            throws ArgumentParserException {
        parser_.parseArgs(args, offset, opts);
    }

    public void printSubparserHelp(PrintWriter writer) {
        if (!help_.isEmpty()) {
            TextHelper.printHelp(writer, "  " + command_, help_,
                    parser_.getTextWidthCounter(),
                    ArgumentParserImpl.FORMAT_WIDTH);
        }
    }

    public String getCommand() {
        return parser_.getCommand();
    }
}
