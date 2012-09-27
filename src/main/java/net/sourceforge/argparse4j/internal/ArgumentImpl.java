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
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import net.sourceforge.argparse4j.helper.PrefixPattern;
import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.helper.TextWidthCounter;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.choice.CollectionArgumentChoice;
import net.sourceforge.argparse4j.impl.type.ConstructorArgumentType;
import net.sourceforge.argparse4j.impl.type.StringArgumentType;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentChoice;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;
import net.sourceforge.argparse4j.inf.FeatureControl;

/**
 * <strong>The application code must not use this class directly.</strong>
 * 
 */
public final class ArgumentImpl implements Argument {

    private String name_;
    private String flags_[];
    private String dest_;
    private ArgumentType<?> type_ = new StringArgumentType();
    private ArgumentAction action_ = Arguments.store();
    private ArgumentChoice choice_;
    private Object const_;
    private Object default_;
    private FeatureControl defaultControl_;
    private boolean required_;
    private String metavar_[];
    private int minNumArg_ = -1;
    private int maxNumArg_ = -1;
    private String help_ = "";
    private ArgumentGroup argumentGroup_;

    public ArgumentImpl(PrefixPattern prefixPattern, String... nameOrFlags) {
        this(prefixPattern, null, nameOrFlags);
    }

    public ArgumentImpl(PrefixPattern prefixPattern,
            ArgumentGroup argumentGroup, String... nameOrFlags) {
        assert (nameOrFlags.length > 0);
        argumentGroup_ = argumentGroup;
        if (nameOrFlags.length == 1 && !prefixPattern.match(nameOrFlags[0])) {
            name_ = nameOrFlags[0];
            dest_ = name_;
        } else {
            flags_ = nameOrFlags;
            for (String flag : flags_) {
                if (!prefixPattern.match(flag)) {
                    throw new IllegalArgumentException(
                            String.format(
                                    "invalid option string '%s': must start with a character '%s'",
                                    flag, prefixPattern.getPrefixChars()));
                }
            }
            for (String flag : flags_) {
                boolean longflag = prefixPattern.matchLongFlag(flag);
                if (dest_ == null) {
                    dest_ = flag;
                    if (longflag) {
                        break;
                    }
                } else if (longflag) {
                    dest_ = flag;
                    break;
                }
            }
            dest_ = prefixPattern.removePrefix(dest_).replace('-', '_');
        }
    }

    @Override
    public String textualName() {
        if (name_ == null) {
            return TextHelper.concat(flags_, 0, "/");
        } else {
            return name_;
        }
    }

    /**
     * Short syntax is used in usage message, e.g. --foo BAR
     * 
     * @return short syntax
     */
    public String formatShortSyntax() {
        if (name_ == null) {
            StringBuilder sb = new StringBuilder();
            if (!required_) {
                sb.append("[");
            }
            sb.append(flags_[0]);
            String mv = formatMetavar();
            if (!mv.isEmpty()) {
                sb.append(" ").append(mv);
            }
            if (!required_) {
                sb.append("]");
            }
            return sb.toString();
        } else {
            return formatMetavar();
        }
    }

    public String[] resolveMetavar() {
        if (metavar_ == null) {
            String metavar[] = new String[1];
            if(choice_ == null) {
                metavar[0] = isOptionalArgument() ? dest_
                        .toUpperCase() : dest_;
            } else {
                metavar[0] = choice_.textualFormat();
            }
            return metavar;
        } else {
            return metavar_;
        }
    }

    public String formatMetavar() {
        StringBuffer sb = new StringBuffer();
        if (action_.consumeArgument()) {
            String metavar[] = resolveMetavar();
            if (minNumArg_ == 0 && maxNumArg_ == 1) {
                sb.append("[").append(metavar[0]).append("]");
            } else if (minNumArg_ == 0 && maxNumArg_ == Integer.MAX_VALUE) {
                sb.append("[").append(metavar[0]).append(" [")
                        .append(metavar.length == 1 ? metavar[0] : metavar[1])
                        .append(" ...]]");
            } else if (minNumArg_ == 1 && maxNumArg_ == Integer.MAX_VALUE) {
                sb.append(metavar[0]).append(" [")
                        .append(metavar.length == 1 ? metavar[0] : metavar[1])
                        .append(" ...]");
            } else if (minNumArg_ == -1) {
                sb.append(metavar[0]);
            } else if (minNumArg_ > 0 && minNumArg_ == maxNumArg_) {
                int i, max;
                for (i = 0, max = Math.min(minNumArg_, metavar.length); i < max; ++i) {
                    sb.append(metavar[i]).append(" ");
                }
                for (; i < minNumArg_; ++i) {
                    sb.append(metavar[metavar.length - 1]).append(" ");
                }
                sb.delete(sb.length() - 1, sb.length());
            }
        }
        return sb.toString();
    }

    private String formatHelpTitle() {
        if (isOptionalArgument()) {
            String mv = formatMetavar();
            StringBuffer sb = new StringBuffer();
            sb.setLength(0);
            for (String flag : flags_) {
                sb.append(flag);
                if (!mv.isEmpty()) {
                    sb.append(" ").append(mv);
                }
                sb.append(", ");
            }
            if (sb.length() > 2) {
                sb.delete(sb.length() - 2, sb.length());
            }
            return sb.toString();
        } else {
            return resolveMetavar()[0];
        }
    }

    public void printHelp(PrintWriter writer, boolean defaultHelp,
            TextWidthCounter textWidthCounter, int width) {
        String help;
        if (defaultHelp && default_ != null) {
            StringBuilder sb = new StringBuilder(help_);
            if (!help_.isEmpty()) {
                sb.append(" ");
            }
            sb.append(String.format("(default: %s)", default_.toString()));
            help = sb.toString();
        } else {
            help = help_;
        }
        TextHelper.printHelp(writer, formatHelpTitle(), help, textWidthCounter,
                width);
    }

    public Object convert(ArgumentParserImpl parser, String value)
            throws ArgumentParserException {
        Object obj = type_.convert(parser, this, value);
        if (choice_ != null && !choice_.contains(obj)) {
            throw new ArgumentParserException(String.format(
                    "invalid choice: '%s' (choose from %s)", value,
                    choice_.textualFormat()), parser, this);
        }
        return obj;
    }

    @Override
    public ArgumentImpl nargs(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("nargs must be positive integer");
        }
        minNumArg_ = maxNumArg_ = n;
        return this;
    }

    @Override
    public ArgumentImpl nargs(String n) {
        if (n.equals("*")) {
            minNumArg_ = 0;
            maxNumArg_ = Integer.MAX_VALUE;
        } else if (n.equals("+")) {
            minNumArg_ = 1;
            maxNumArg_ = Integer.MAX_VALUE;
        } else if (n.equals("?")) {
            minNumArg_ = 0;
            maxNumArg_ = 1;
        } else {
            throw new IllegalArgumentException(
                    "narg expects positive integer or one of '*', '+' or '?'");
        }
        return this;
    }

    @Override
    public ArgumentImpl setConst(Object value) {
        // Allow null
        const_ = value;
        return this;
    }

    @Override
    public <E> ArgumentImpl setConst(E... values) {
        // Allow null
        const_ = Arrays.asList(values);
        return this;
    }

    @Override
    public ArgumentImpl setDefault(Object value) {
        // Allow null
        default_ = value;
        return this;
    }

    @Override
    public <E> ArgumentImpl setDefault(E... values) {
        // Allow null
        default_ = Arrays.asList(values);
        return this;
    }

    @Override
    public ArgumentImpl setDefault(FeatureControl ctrl) {
        defaultControl_ = ctrl;
        return this;
    }

    @Override
    public <T> ArgumentImpl type(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        type_ = new ConstructorArgumentType<T>(type);
        return this;
    }

    @Override
    public <T> ArgumentImpl type(ArgumentType<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        type_ = type;
        return this;
    }

    @Override
    public ArgumentImpl required(boolean required) {
        required_ = required;
        return this;
    }

    @Override
    public ArgumentImpl action(ArgumentAction action) {
        if (action == null) {
            throw new IllegalArgumentException("action cannot be null");
        }
        action_ = action;
        action_.onAttach(this);
        return this;
    }

    @Override
    public ArgumentImpl choices(ArgumentChoice choice) {
        if (choice == null) {
            throw new IllegalArgumentException("choice cannot be null");
        }
        choice_ = choice;
        return this;
    }

    @Override
    public <E> ArgumentImpl choices(Collection<E> values) {
        if (values == null) {
            throw new IllegalArgumentException("choice cannot be null");
        }
        choice_ = new CollectionArgumentChoice<E>(values);
        return this;
    }

    @Override
    public <E> ArgumentImpl choices(E... values) {
        if (values == null) {
            throw new IllegalArgumentException("choice cannot be null");
        }
        choice_ = new CollectionArgumentChoice<E>(values);
        return this;
    }

    @Override
    public ArgumentImpl dest(String dest) {
        if (dest == null) {
            throw new IllegalArgumentException("dest cannot be null");
        }
        dest_ = dest;
        return this;
    }

    @Override
    public ArgumentImpl metavar(String... metavar) {
        if (metavar.length == 0) {
            throw new IllegalArgumentException("No metavar specified");
        }
        for (String m : metavar) {
            if (m == null) {
                throw new IllegalArgumentException("metavar cannot be null");
            }
        }
        metavar_ = metavar;
        return this;
    }

    @Override
    public ArgumentImpl help(String help) {
        help_ = TextHelper.nonNull(help);
        return this;
    }

    public boolean isOptionalArgument() {
        return name_ == null;
    }

    public void run(ArgumentParserImpl parser, Map<String, Object> res,
            String flag, Object value) throws ArgumentParserException {
        action_.run(parser, this, res, flag, value);
    }

    // Getter methods

    @Override
    public String getDest() {
        return dest_;
    }

    @Override
    public Object getConst() {
        return const_;
    }

    @Override
    public Object getDefault() {
        if (default_ == null && maxNumArg_ > 1) {
            return new ArrayList<Object>();
        } else {
            return default_;
        }
    }

    @Override
    public FeatureControl getDefaultControl() {
        return defaultControl_;
    }

    public String getName() {
        return name_;
    }

    public boolean isRequired() {
        return required_;
    }

    public int getMinNumArg() {
        return minNumArg_;
    }

    public int getMaxNumArg() {
        return maxNumArg_;
    }

    public String[] getMetavar() {
        return metavar_;
    }

    public ArgumentGroup getArgumentGroup() {
        return argumentGroup_;
    }

    public ArgumentAction getAction() {
        return action_;
    }

    public String getHelp() {
        return help_;
    }

    public String[] getFlags() {
        return flags_;
    }
}
