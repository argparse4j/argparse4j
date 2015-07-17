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
import java.util.Map;

import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.helper.TextWidthCounter;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

/**
 * <strong>The application code must not use this class directly.</strong>
 * 
 */
public final class SubparserImpl implements Subparser {

    private String command_;
    private List<String> aliases_ = new ArrayList<String>();
    private ArgumentParserImpl parser_;
    private String help_ = "";

    public SubparserImpl(String prog, boolean addHelp, String prefixChars,
            String fromFilePrefix, TextWidthCounter textWidthCounter,
            String command, ArgumentParserImpl mainParser) {
        command_ = command;
        parser_ = new ArgumentParserImpl(prog, addHelp, prefixChars,
                fromFilePrefix, textWidthCounter, command, mainParser);
    }

    @Override
    public Argument addArgument(String... nameOrFlags) {
        return parser_.addArgument(nameOrFlags);
    }

    @Override
    public ArgumentGroup addArgumentGroup(String title) {
        return parser_.addArgumentGroup(title);
    }

    @Override
    public MutuallyExclusiveGroup addMutuallyExclusiveGroup() {
        return parser_.addMutuallyExclusiveGroup();
    }

    @Override
    public MutuallyExclusiveGroup addMutuallyExclusiveGroup(String title) {
        return parser_.addMutuallyExclusiveGroup(title);
    }

    @Override
    public Subparsers addSubparsers() {
        return parser_.addSubparsers();
    }

    @Override
    public SubparserImpl usage(String usage) {
        parser_.usage(usage);
        return this;
    }

    @Override
    public SubparserImpl description(String description) {
        parser_.description(description);
        return this;
    }

    @Override
    public SubparserImpl epilog(String epilog) {
        parser_.epilog(epilog);
        return this;
    }

    @Override
    public SubparserImpl version(String version) {
        parser_.version(version);
        return this;
    }

    @Override
    public SubparserImpl defaultHelp(boolean defaultHelp) {
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
    public SubparserImpl setDefault(String dest, Object value) {
        parser_.setDefault(dest, value);
        return this;
    }

    @Override
    public SubparserImpl setDefaults(Map<String, Object> attrs) {
        parser_.setDefaults(attrs);
        return this;
    }

    @Override
    public Object getDefault(String dest) {
        return parser_.getDefault(dest);
    }

    @Override
    public SubparserImpl help(String help) {
        help_ = TextHelper.nonNull(help);
        return this;
    }

    @Override
    public Namespace parseArgsOrFail(String args[]) {
        return parser_.parseArgsOrFail(args);
    }

    @Override
    public Namespace parseArgs(String[] args) throws ArgumentParserException {
        return parser_.parseArgs(args);
    }

    @Override
    public void parseArgs(String[] args, Map<String, Object> attrs)
            throws ArgumentParserException {
        parser_.parseArgs(args, attrs);
    }

    @Override
    public void parseArgs(String[] args, Object userData)
            throws ArgumentParserException {
        parser_.parseArgs(args, userData);
    }

    @Override
    public void parseArgs(String[] args, Map<String, Object> attrs,
            Object userData) throws ArgumentParserException {
        parser_.parseArgs(args, attrs, userData);
    }

    @Override
    public Namespace parseKnownArgsOrFail(String[] args, List<String> unknown) {
        return parser_.parseKnownArgsOrFail(args, unknown);
    }

    @Override
    public Namespace parseKnownArgs(String[] args, List<String> unknown)
            throws ArgumentParserException {
        return parser_.parseKnownArgs(args, unknown);
    }

    @Override
    public void parseKnownArgs(String[] args, List<String> unknown,
            Map<String, Object> attrs) throws ArgumentParserException {
        parser_.parseKnownArgs(args, unknown, attrs);
    }

    @Override
    public void parseKnownArgs(String[] args, List<String> unknown,
            Object userData) throws ArgumentParserException {
        parser_.parseKnownArgs(args, unknown, userData);
    }

    @Override
    public void parseKnownArgs(String[] args, List<String> unknown,
            Map<String, Object> attrs, Object userData)
            throws ArgumentParserException {
        parser_.parseKnownArgs(args, unknown, attrs, userData);
    }

    @Override
    public void handleError(ArgumentParserException e) {
        parser_.handleError(e);
    }

    @Override
    public SubparserImpl aliases(String... alias) {
        parser_.getMainParser().addSubparsers().addAlias(this, alias);
        for (String s : alias) {
            aliases_.add(s);
        }
        return this;
    }

    public void parseArgs(ParseState state, Map<String, Object> opts)
            throws ArgumentParserException {
        parser_.parseArgs(state, opts);
    }

    public void printSubparserHelp(PrintWriter writer, int format_width) {
        if (!help_.isEmpty()) {
            String title = "  " + command_;
            if (!aliases_.isEmpty()) {
                title += " (" + TextHelper.concat(aliases_, 0, ",") + ")";
            }
            TextHelper.printHelp(writer, title, help_,
                    parser_.getTextWidthCounter(), format_width);
        }
    }

    public String getCommand() {
        return parser_.getCommand();
    }
}
