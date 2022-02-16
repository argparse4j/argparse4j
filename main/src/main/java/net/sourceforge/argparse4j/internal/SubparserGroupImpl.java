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
import net.sourceforge.argparse4j.inf.FeatureControl;
import net.sourceforge.argparse4j.inf.SubparserGroup;

/**
 * <strong>The application code must not use this class directly.</strong>
 */
public class SubparserGroupImpl implements SubparserGroup {

    protected final ArgumentParserImpl mainParser_;
    /**
     * The key is subparser command or alias name and value is subparser object.
     * The real command and alias names share the same subparser object. To
     * identify the aliases, check key equals to
     * {@link SubparserImpl#getCommand()}. If they are equal, it is not alias.
     */
    protected final Map<String, SubparserImpl> parsers_ = new LinkedHashMap<>();
    protected String title_ = "";

    SubparserGroupImpl(ArgumentParserImpl mainParser) {
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
        SubparserImpl parser = new SubparserImpl(
                mainParser_.getConfig().forSubparser(addHelp, prefixChars),
                command, mainParser_);
        parsers_.put(command, parser);
        return parser;
    }

    @Override
    public SubparserGroupImpl title(String title) {
        title_ = TextHelper.nonNull(title);
        return this;
    }

    public String getTitle() {
        return title_;
    }

    public Map<String, SubparserImpl> getParsers() {
        return parsers_;
    }

    void printSubparserHelp(PrintWriter writer, int format_width) {
        if (!title_.isEmpty()) {
            writer.println(" " + title_);
        }
        for (Map.Entry<String, SubparserImpl> entry : parsers_.entrySet()) {
            // Don't generate help for aliases.
            if (entry.getValue().getHelpControl() != FeatureControl.SUPPRESS &&
                    entry.getKey().equals(entry.getValue().getCommand())) {
                entry.getValue().printSubparserHelp(writer, format_width);
            }
        }
    }

    /**
     * Checks whether the SubparserGroup contains at least one Subparser.
     *
     * @return true if at least one Subparser is present in the group
     */
    boolean hasSubCommand() {
        return !getParsers().isEmpty();
    }

    /**
     * Checks whether the specified key already exists within the parsers_ map.
     *
     * @param key
     *             the key to check for
     * @return true if the key does already exist
     */
    boolean containsKey(String key)
    {
        return getParsers().containsKey(key);
    }

    /**
     * Return true if SubparserImpl has at least one sub-command whose help
     * output is not suppressed.
     *
     * @return true if SubparserImpl has at least one sub-command whose help
     *         output is not suppressed.
     */
    boolean hasNotSuppressedSubCommand() {
        for (Map.Entry<String, SubparserImpl> entry : getParsers().entrySet()) {
            if (entry.getValue().getHelpControl() != FeatureControl.SUPPRESS) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns collection of the sub-command name under this object.
     *
     * @return collection of the sub-command name
     */
    Collection<String> getCommands() {
        return getParsers().keySet();
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
    void addAlias(SubparserImpl subparser, String... alias) {
        for (String command : alias) {
            if (containsKey(command)) {
                throw new IllegalArgumentException(String.format(
                        TextHelper.LOCALE_ROOT,
                        "command '%s' has been already used", command));
            } else {
                parsers_.put(command, subparser);
            }
        }
    }
}
