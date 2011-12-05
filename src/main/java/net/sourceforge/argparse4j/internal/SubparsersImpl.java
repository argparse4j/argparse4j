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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

/**
 * <strong>The application code must not use this class directly.</strong>
 * 
 */
public final class SubparsersImpl implements Subparsers {

    private ArgumentParserImpl mainParser_;
    private Map<String, SubparserImpl> parsers_ = new LinkedHashMap<String, SubparserImpl>();
    private String help_ = "";
    private String title_ = "";
    private String description_ = "";
    private String dest_ = "";
    private String metavar_ = "";

    public SubparsersImpl(ArgumentParserImpl mainParser) {
        mainParser_ = mainParser;
    }

    @Override
    public Subparser addParser(String command) {
        return addParser(command, true, ArgumentParserImpl.PREFIX_CHARS);
    }

    @Override
    public Subparser addParser(String command, boolean addHelp) {
        return addParser(command, addHelp, ArgumentParserImpl.PREFIX_CHARS);
    }

    @Override
    public Subparser addParser(String command, boolean addHelp,
            String prefixChars) {
        if (command == null || command.isEmpty()) {
            throw new IllegalArgumentException(
                    "command cannot be null or empty");
        }
        SubparserImpl parser = new SubparserImpl(mainParser_.getProg(),
                addHelp, prefixChars, mainParser_.getFromFilePrefixChars(),
                mainParser_.getTextWidthCounter(), command, mainParser_);
        parsers_.put(command, parser);
        return parser;
    }

    @Override
    public Subparsers dest(String dest) {
        dest_ = TextHelper.nonNull(dest);
        return this;
    }

    @Override
    public Subparsers help(String help) {
        help_ = TextHelper.nonNull(help);
        return this;
    }

    @Override
    public Subparsers title(String title) {
        title_ = TextHelper.nonNull(title);
        return this;
    }

    public String getTitle() {
        return title_;
    }

    @Override
    public Subparsers description(String description) {
        description_ = TextHelper.nonNull(description);
        return this;
    }

    public String getDescription() {
        return description_;
    }

    @Override
    public Subparsers metavar(String metavar) {
        metavar_ = TextHelper.nonNull(metavar);
        return this;
    }

    public boolean hasSubCommand() {
        return !parsers_.isEmpty();
    }

    public void parseArg(ParseState state, Map<String, Object> opts)
            throws ArgumentParserException {
        if (parsers_.isEmpty()) {
            throw new IllegalArgumentException("too many arguments");
        }
        SubparserImpl ap = parsers_.get(state.getArg());
        if (ap == null) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, SubparserImpl> entry : parsers_.entrySet()) {
                sb.append("'").append(entry.getKey()).append("', ");
            }
            sb.delete(sb.length() - 2, sb.length());
            throw new UnrecognizedCommandException(String.format(
                    "invalid choice: '%s' (choose from %s)", state.getArg(),
                    sb.toString()), mainParser_, state.getArg());
        } else {
            ++state.index;
            ap.parseArgs(state, opts);
            // Call after parseArgs to overwrite dest_ attribute set by
            // sub-parsers.
            if (!dest_.isEmpty()) {
                opts.put(dest_, ap.getCommand());
            }
        }
    }

    public String formatShortSyntax() {
        if (metavar_.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            for (Map.Entry<String, SubparserImpl> entry : parsers_.entrySet()) {
                sb.append(entry.getKey()).append(",");
            }
            if (sb.length() > 1) {
                sb.delete(sb.length() - 1, sb.length());
            }
            sb.append("}");
            return sb.toString();
        } else {
            return metavar_;
        }
    }

    public void printSubparserHelp(PrintWriter writer) {
        TextHelper.printHelp(writer, formatShortSyntax(), help_,
                mainParser_.getTextWidthCounter(),
                ArgumentParserImpl.FORMAT_WIDTH);
        for (Map.Entry<String, SubparserImpl> entry : parsers_.entrySet()) {
            entry.getValue().printSubparserHelp(writer);
        }
    }

    public Collection<String> getCommands() {
        return parsers_.keySet();
    }
}
