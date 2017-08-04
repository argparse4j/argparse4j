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

import net.sourceforge.argparse4j.helper.MessageLocalization;
import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.helper.TextWidthCounter;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.choice.CollectionArgumentChoice;
import net.sourceforge.argparse4j.impl.type.ReflectArgumentType;
import net.sourceforge.argparse4j.impl.type.StringArgumentType;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentChoice;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;
import net.sourceforge.argparse4j.inf.FeatureControl;
import net.sourceforge.argparse4j.inf.MetavarInference;

/**
 * <strong>The application code must not use this class directly.</strong>
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
    private FeatureControl helpControl_;
    private boolean required_;
    private String metavar_[];
    private int minNumArg_ = -1;
    private int maxNumArg_ = -1;
    private String help_ = "";
    private ArgumentParserConfigurationImpl config_;
    private ArgumentGroupImpl argumentGroup_;

    ArgumentImpl(ArgumentParserConfigurationImpl config,
            String... nameOrFlags) {
        this(config, null, nameOrFlags);
    }

    ArgumentImpl(ArgumentParserConfigurationImpl config,
            ArgumentGroupImpl argumentGroup, String... nameOrFlags) {
        if (nameOrFlags.length == 0) {
            throw new IllegalArgumentException("no nameOrFlags was specified");
        }
        config_ = config;
        argumentGroup_ = argumentGroup;
        if (nameOrFlags.length == 1
                && !config.prefixPattern_.match(nameOrFlags[0])) {
            if (argumentGroup_ != null && argumentGroup_.isMutex()) {
                throw new IllegalArgumentException(
                        "mutually exclusive arguments must be optional");
            }
            name_ = nameOrFlags[0];
            if (!config.noDestConversionForPositionalArgs_) {
                dest_ = name_.replace('-', '_');
            }
        } else {
            flags_ = nameOrFlags;
            for (String flag : flags_) {
                if (!config.prefixPattern_.match(flag)) {
                    throw new IllegalArgumentException(
                            String.format(
                                    TextHelper.LOCALE_ROOT,
                                    localize("invalidOptionStringError"),
                                    flag, config.prefixPattern_.getPrefixChars()));
                }
            }
            for (String flag : flags_) {
                boolean longFlag = config.prefixPattern_.matchLongFlag(flag);
                if (dest_ == null) {
                    dest_ = flag;
                    if (longFlag) {
                        break;
                    }
                } else if (longFlag) {
                    dest_ = flag;
                    break;
                }
            }
            dest_ = config.prefixPattern_.removePrefix(dest_).replace('-', '_');
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
    String formatShortSyntax() {
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

    /**
     * Short syntax is used in usage message, e.g. --foo BAR, but without
     * bracket when this is not required option.
     * 
     * @return short syntax
     */
    String formatShortSyntaxNoBracket() {
        if (name_ == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(flags_[0]);
            String mv = formatMetavar();
            if (!mv.isEmpty()) {
                sb.append(" ").append(mv);
            }
            return sb.toString();
        } else {
            return formatMetavar();
        }
    }

    String[] resolveMetavar() {
        if (metavar_ == null) {
            if (choice_ == null) {
                if (type_ instanceof MetavarInference) {
                    String[] metavar = ((MetavarInference) type_)
                            .inferMetavar();
                    if (metavar != null) {
                        return metavar;
                    }
                }

                if (isNamedArgument()) {
                    return new String[] { dest_.toUpperCase() };
                }

                return new String[] { dest_ };
            }

            return new String[] { choice_.textualFormat() };
        }

        return metavar_;
    }

    String formatMetavar() {
        StringBuilder sb = new StringBuilder();
        if (action_.consumeArgument()) {
            String[] metavar = resolveMetavar();
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
        if (isNamedArgument()) {
            String mv = formatMetavar();
            StringBuilder sb = new StringBuilder();

            if(config_.singleMetavar_) {
                for (String flag : flags_) {
                    if(sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(flag);
                }
                if (!mv.isEmpty()) {
                    sb.append(" ").append(mv);
                }
            } else {
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
            }
            return sb.toString();
        } else {
            return resolveMetavar()[0];
        }
    }

    public void printHelp(PrintWriter writer, boolean defaultHelp,
            TextWidthCounter textWidthCounter, int width) {
        if (helpControl_ == Arguments.SUPPRESS) {
            return;
        }
        String help;
        if (defaultHelp && default_ != null) {
            StringBuilder sb = new StringBuilder(help_);
            if (!help_.isEmpty()) {
                sb.append(" ");
            }
            sb.append("(default: ").append(default_.toString()).append(")");
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
                    TextHelper.LOCALE_ROOT, localize("invalidChoiceError"),
                    value, choice_.textualFormat()), parser, this);
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

    private <T> ReflectArgumentType<T> createReflectArgumentType(Class<T> type) {
        return new ReflectArgumentType<T>(type);
    }

    @Override
    public <T> ArgumentImpl type(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        if (type.isPrimitive()) {
            // Convert primitive type class to its object counterpart
            if (type == boolean.class) {
                type_ = createReflectArgumentType(Boolean.class);
            } else if (type == byte.class) {
                type_ = createReflectArgumentType(Byte.class);
            } else if (type == short.class) {
                type_ = createReflectArgumentType(Short.class);
            } else if (type == int.class) {
                type_ = createReflectArgumentType(Integer.class);
            } else if (type == long.class) {
                type_ = createReflectArgumentType(Long.class);
            } else if (type == float.class) {
                type_ = createReflectArgumentType(Float.class);
            } else if (type == double.class) {
                type_ = createReflectArgumentType(Double.class);
            } else {
                // void and char are not supported.
                // char.class does not have valueOf(String) method
                throw new IllegalArgumentException("unexpected primitive type");
            }
        } else {
            type_ = createReflectArgumentType(type);
        }
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

    @Override
    public ArgumentImpl help(FeatureControl ctrl) {
        helpControl_ = ctrl;
        return this;
    }

    boolean isNamedArgument() {
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
        // For positional arguments, we perform special treatment if
        // they are configured with nargs("*") and default is null.
        // In this case, return empty list.
        if (!isNamedArgument() && default_ == null && maxNumArg_ > 1) {
            return new ArrayList<Object>();
        } else {
            return default_;
        }
    }

    @Override
    public FeatureControl getDefaultControl() {
        return defaultControl_;
    }

    @Override
    public FeatureControl getHelpControl() {
        return helpControl_;
    }

    public String getName() {
        return name_;
    }

    public boolean isRequired() {
        return required_;
    }

    int getMinNumArg() {
        return minNumArg_;
    }

    int getMaxNumArg() {
        return maxNumArg_;
    }

    public String[] getMetavar() {
        return metavar_;
    }

    ArgumentGroupImpl getArgumentGroup() {
        return argumentGroup_;
    }

    public ArgumentAction getAction() {
        return action_;
    }

    public String getHelp() {
        return help_;
    }

    String[] getFlags() {
        return flags_;
    }

    private String localize(String messageKey) {
        return MessageLocalization.localize(config_.getResourceBundle(),
                messageKey);
    }
}
