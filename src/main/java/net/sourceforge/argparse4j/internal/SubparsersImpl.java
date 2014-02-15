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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
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
    /**
     * The key is subparser command or alias name and value is subparser object.
     * The real command and alias names share the same subparser object. To
     * identify the aliases, check key equals to
     * {@link SubparserImpl#getCommand()}. If they are equal, it is not alias.
     */
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
    public SubparserImpl addParser(String command) {
        return addParser(command, true, mainParser_.getPrefixChars());
    }

    @Override
    public SubparserImpl addParser(String command, boolean addHelp) {
        return addParser(command, addHelp, mainParser_.getPrefixChars());
    }

    @Override
    public SubparserImpl addParser(String command, boolean addHelp,
            String prefixChars) {
        if (command == null || command.isEmpty()) {
            throw new IllegalArgumentException(
                    "command cannot be null or empty");
        } else if (parsers_.containsKey(command)) {
            throw new IllegalArgumentException(String.format(
                    TextHelper.LOCALE_ROOT,
                    "command '%s' has been already used", command));
        }
        SubparserImpl parser = new SubparserImpl(mainParser_.getProg(),
                addHelp, prefixChars, mainParser_.getFromFilePrefixChars(),
                mainParser_.getTextWidthCounter(), command, mainParser_);
        parsers_.put(command, parser);
        return parser;
    }

    @Override
    public SubparsersImpl dest(String dest) {
        dest_ = TextHelper.nonNull(dest);
        return this;
    }

    @Override
    public SubparsersImpl help(String help) {
        help_ = TextHelper.nonNull(help);
        return this;
    }

    @Override
    public SubparsersImpl title(String title) {
        title_ = TextHelper.nonNull(title);
        return this;
    }

    public String getTitle() {
        return title_;
    }

    @Override
    public SubparsersImpl description(String description) {
        description_ = TextHelper.nonNull(description);
        return this;
    }

    public String getDescription() {
        return description_;
    }

    @Override
    public SubparsersImpl metavar(String metavar) {
        metavar_ = TextHelper.nonNull(metavar);
        return this;
    }

    public boolean hasSubCommand() {
        return !parsers_.isEmpty();
    }

    /**
     * Get next SubparserImpl from given command and state. This function
     * resolves abbreviated command input as well. If the given command is
     * ambiguous, {@link ArgumentParserException} will be thrown. If no matching
     * SubparserImpl is found, this function returns null.
     * 
     * @param state
     * @param command
     * @return next SubparserImpl or null
     * @throws ArgumentParserException
     */
    private SubparserImpl resolveNextSubparser(ParseState state, String command)
            throws ArgumentParserException {
        if (command.isEmpty()) {
            return null;
        }
        SubparserImpl ap = parsers_.get(command);
        if (ap == null) {
            List<String> cand = TextHelper.findPrefix(parsers_.keySet(),
                    command);
            int size = cand.size();
            if (size == 1) {
                ap = parsers_.get(cand.get(0));
            } else if (size > 1) {
                // Sort it to make unit test easier
                Collections.sort(cand);
                throw new ArgumentParserException(String.format(
                        TextHelper.LOCALE_ROOT,
                        "ambiguous command: %s could match %s", command,
                        TextHelper.concat(cand, 0, ", ")), mainParser_);
            }
        }
        return ap;
    }

    public void parseArg(ParseState state, Map<String, Object> opts)
            throws ArgumentParserException {
        if (parsers_.isEmpty()) {
            throw new IllegalArgumentException("too many arguments");
        }
        SubparserImpl ap = resolveNextSubparser(state, state.getArg());
        if (ap == null) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, SubparserImpl> entry : parsers_.entrySet()) {
                sb.append("'").append(entry.getKey()).append("', ");
            }
            sb.delete(sb.length() - 2, sb.length());
            throw new UnrecognizedCommandException(String.format(
                    TextHelper.LOCALE_ROOT,
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

    /**
     * Writes the help message for this and descendants.
     * 
     * @param writer
     *            The writer to output
     * @param format_width
     *            column width
     */
    public void printSubparserHelp(PrintWriter writer, int format_width) {
        TextHelper.printHelp(writer, formatShortSyntax(), help_,
                mainParser_.getTextWidthCounter(), format_width);
        for (Map.Entry<String, SubparserImpl> entry : parsers_.entrySet()) {
            // Don't generate help for aliases.
            if (entry.getKey().equals(entry.getValue().getCommand())) {
                entry.getValue().printSubparserHelp(writer, format_width);
            }
        }
    }

    /**
     * Returns collection of the sub-command name under this object.
     * 
     * @return collection of the sub-comman name
     */
    public Collection<String> getCommands() {
        return parsers_.keySet();
    }

    /**
     * Adds Subparser alias names for given Subparser. For each SubparsersImpl
     * instance, alias names and commands must be unique. If duplication is
     * found, {@link IllegalArgumentException} is thrown.
     * 
     * @param subparser
     *            Subparser to add alias names
     * @param alias
     *            alias name
     */
    public void addAlias(SubparserImpl subparser, String... alias) {
        for (String command : alias) {
            if (parsers_.containsKey(command)) {
                throw new IllegalArgumentException(String.format(
                        TextHelper.LOCALE_ROOT,
                        "command '%s' has been already used", command));
            } else {
                parsers_.put(command, subparser);
            }
        }
    }
}
