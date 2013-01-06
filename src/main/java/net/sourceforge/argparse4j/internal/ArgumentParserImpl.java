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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.annotation.Arg;
import net.sourceforge.argparse4j.helper.ASCIITextWidthCounter;
import net.sourceforge.argparse4j.helper.PrefixPattern;
import net.sourceforge.argparse4j.helper.ReflectHelper;
import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.helper.TextWidthCounter;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;

/**
 * <strong>The application code must not use this class directly.</strong>
 * 
 */
public final class ArgumentParserImpl implements ArgumentParser {

    private Map<String, ArgumentImpl> optargIndex_ = new HashMap<String, ArgumentImpl>();
    private List<ArgumentImpl> optargs_ = new ArrayList<ArgumentImpl>();
    private List<ArgumentImpl> posargs_ = new ArrayList<ArgumentImpl>();
    private List<ArgumentGroupImpl> arggroups_ = new ArrayList<ArgumentGroupImpl>();
    private Map<String, Object> defaults_ = new HashMap<String, Object>();
    private SubparsersImpl subparsers_ = new SubparsersImpl(this);
    private ArgumentParserImpl mainParser_;
    private String command_;
    private String prog_;
    private String description_ = "";
    private String epilog_ = "";
    private String version_ = "";
    private PrefixPattern prefixPattern_;
    private PrefixPattern fromFilePrefixPattern_;
    private boolean defaultHelp_ = false;
    private boolean negNumFlag_ = false;
    private TextWidthCounter textWidthCounter_;
    private static final Pattern NEG_NUM_PATTERN = Pattern.compile("-\\d+");
    private static final Pattern SHORT_OPTS_PATTERN = Pattern
            .compile("-[^-].*");

    public static final String PREFIX_CHARS = "-";

    public ArgumentParserImpl(String prog) {
        this(prog, true, PREFIX_CHARS, null, new ASCIITextWidthCounter(), null,
                null);
    }

    public ArgumentParserImpl(String prog, boolean addHelp) {
        this(prog, addHelp, PREFIX_CHARS, null, new ASCIITextWidthCounter(),
                null, null);
    }

    public ArgumentParserImpl(String prog, boolean addHelp, String prefixChars) {
        this(prog, addHelp, prefixChars, null, new ASCIITextWidthCounter(),
                null, null);
    }

    public ArgumentParserImpl(String prog, boolean addHelp, String prefixChars,
            String fromFilePrefix) {
        this(prog, addHelp, prefixChars, fromFilePrefix,
                new ASCIITextWidthCounter(), null, null);
    }

    public ArgumentParserImpl(String prog, boolean addHelp, String prefixChars,
            String fromFilePrefix, TextWidthCounter textWidthCounter) {
        this(prog, addHelp, prefixChars, fromFilePrefix, textWidthCounter, null, null);
    }

    public ArgumentParserImpl(String prog, boolean addHelp, String prefixChars,
            String fromFilePrefix, TextWidthCounter textWidthCounter,
            String command, ArgumentParserImpl mainParser) {
        this.prog_ = TextHelper.nonNull(prog);
        this.command_ = command;
        this.mainParser_ = mainParser;
        this.textWidthCounter_ = textWidthCounter;
        if (prefixChars == null || prefixChars.isEmpty()) {
            throw new IllegalArgumentException(
                    "prefixChars cannot be a null or empty");
        }
        this.prefixPattern_ = new PrefixPattern(prefixChars);
        if (fromFilePrefix != null) {
            this.fromFilePrefixPattern_ = new PrefixPattern(fromFilePrefix);
        }
        if (addHelp) {
            String prefix = prefixChars.substring(0, 1);
            addArgument(prefix + "h", prefix + prefix + "help")
                    .action(Arguments.help())
                    .help("show this help message and exit")
                    .setDefault(Arguments.SUPPRESS);
        }
    }

    @Override
    public ArgumentImpl addArgument(String... nameOrFlags) {
        return addArgument(null, nameOrFlags);
    }

    public ArgumentImpl addArgument(ArgumentGroupImpl group, String... nameOrFlags) {
        ArgumentImpl arg = new ArgumentImpl(prefixPattern_, group, nameOrFlags);
        if (arg.isOptionalArgument()) {
            for (String flag : arg.getFlags()) {
                ArgumentImpl another = optargIndex_.get(flag);
                if (another != null) {
                    // TODO No conflict handler ATM
                    throw new IllegalArgumentException(String.format(
                            "argument %s: conflicting option string(s): %s",
                            flag, another.textualName()));
                }
            }
            for (String flag : arg.getFlags()) {
                if (NEG_NUM_PATTERN.matcher(flag).matches()) {
                    negNumFlag_ = true;
                }
                optargIndex_.put(flag, arg);
            }
            optargs_.add(arg);
        } else {
            for (ArgumentImpl another : posargs_) {
                if (arg.getName().equals(another.getName())) {
                    // TODO No conflict handler ATM
                    throw new IllegalArgumentException(String.format(
                            "argument %s: conflicting option string(s): %s",
                            arg.getName(), another.textualName()));
                }
            }
            posargs_.add(arg);
        }
        return arg;
    }

    @Override
    public Subparsers addSubparsers() {
        return subparsers_;
    }

    @Override
    public ArgumentGroup addArgumentGroup(String title) {
        ArgumentGroupImpl group = new ArgumentGroupImpl(this, title);
        group.setIndex(arggroups_.size());
        arggroups_.add(group);
        return group;
    }

    @Override
    public MutuallyExclusiveGroup addMutuallyExclusiveGroup(String title) {
        ArgumentGroupImpl group = new ArgumentGroupImpl(this, title);
        group.setIndex(arggroups_.size());
        group.setMutex(true);
        arggroups_.add(group);
        return group;
    }

    /**
     * Set text to display before the argument help.
     * 
     * @param description
     *            text to display before the argument help
     * @return this
     */
    @Override
    public ArgumentParserImpl description(String description) {
        description_ = TextHelper.nonNull(description);
        return this;
    }

    @Override
    public ArgumentParserImpl epilog(String epilog) {
        epilog_ = TextHelper.nonNull(epilog);
        return this;
    }

    @Override
    public ArgumentParserImpl version(String version) {
        version_ = TextHelper.nonNull(version);
        return this;
    }

    @Override
    public ArgumentParserImpl defaultHelp(boolean defaultHelp) {
        defaultHelp_ = defaultHelp;
        return this;
    }

    public boolean isDefaultHelp() {
        return defaultHelp_;
    }

    private void printArgumentHelp(PrintWriter writer, List<ArgumentImpl> args,
            int format_width) {
        for (ArgumentImpl arg : args) {
            if (arg.getArgumentGroup() == null) {
                arg.printHelp(writer, defaultHelp_, textWidthCounter_,
                        format_width);
            }
        }
    }

    @Override
    public void printHelp() {
        PrintWriter writer = new PrintWriter(System.out);
        printHelp(writer);
        writer.close();
    }

    @Override
    public void printHelp(PrintWriter writer) {
        int format_width = ArgumentParsers.getFormatWidth();
        printUsage(writer, format_width);
        if (!description_.isEmpty()) {
            writer.format("\n%s\n", TextHelper.wrap(textWidthCounter_,
                    description_, format_width, 0, "", ""));
        }
        boolean subparsersUntitled = subparsers_.getTitle().isEmpty()
                && subparsers_.getDescription().isEmpty();
        if (checkDefaultGroup(posargs_)
                || (subparsers_.hasSubCommand() && subparsersUntitled)) {
            writer.print("\npositional arguments:\n");
            printArgumentHelp(writer, posargs_, format_width);
            if (subparsers_.hasSubCommand() && subparsersUntitled) {
                subparsers_.printSubparserHelp(writer, format_width);
            }
        }
        if (checkDefaultGroup(optargs_)) {
            writer.print("\noptional arguments:\n");
            printArgumentHelp(writer, optargs_, format_width);
        }
        if (subparsers_.hasSubCommand() && !subparsersUntitled) {
            writer.format("\n%s:\n",
                    subparsers_.getTitle().isEmpty() ? "subcommands"
                            : subparsers_.getTitle());
            if (!subparsers_.getDescription().isEmpty()) {
                writer.format("  %s\n\n", TextHelper
                        .wrap(textWidthCounter_, subparsers_.getDescription(),
                                format_width, 2, "", "  "));
            }
            subparsers_.printSubparserHelp(writer, format_width);
        }
        for (ArgumentGroupImpl group : arggroups_) {
            writer.print("\n");
            group.printHelp(writer, format_width);
        }
        if (!epilog_.isEmpty()) {
            writer.format("\n%s\n", TextHelper.wrap(textWidthCounter_, epilog_,
                    format_width, 0, "", ""));
        }
        writer.flush();
    }

    private boolean checkDefaultGroup(List<ArgumentImpl> args) {
        if (args.isEmpty()) {
            return false;
        }
        for (ArgumentImpl arg : args) {
            if (arg.getArgumentGroup() == null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String formatHelp() {
        StringWriter writer = new StringWriter();
        printHelp(new PrintWriter(writer));
        return writer.toString();
    }

    private void printArgumentUsage(PrintWriter writer, List<String> opts,
            int offset, String firstIndent, String subsequentIndent,
            int format_width) {
        int currentWidth = offset + firstIndent.length();
        writer.print(firstIndent);
        boolean first = true;
        for (String syntax : opts) {
            if (!first && currentWidth + syntax.length() + 1 > format_width) {
                writer.print("\n");
                writer.print(subsequentIndent);
                writer.print(" ");
                writer.print(syntax);
                currentWidth = subsequentIndent.length() + 1 + syntax.length();
            } else {
                writer.print(" ");
                writer.print(syntax);
                currentWidth += 1 + syntax.length();
                first = false;
            }
        }
        writer.print("\n");
    }

    @Override
    public void printUsage() {
        printUsage(new PrintWriter(System.out));
    }

    @Override
    public void printUsage(PrintWriter writer) {
        printUsage(writer, ArgumentParsers.getFormatWidth());
    }

    private void printUsage(PrintWriter writer, int format_width) {
        String usageprog = String.format("usage: %s", prog_);
        writer.print(usageprog);
        int offset;
        String firstIndent;
        String subsequentIndent;
        String indent = "                              ";
        int usageprogWidth = textWidthCounter_.width(usageprog);
        if (usageprogWidth > indent.length()) {
            writer.print("\n");
            offset = 6;
            firstIndent = subsequentIndent = indent.substring(0, offset);
        } else {
            offset = usageprogWidth;
            firstIndent = "";
            subsequentIndent = indent.substring(0, offset);
        }
        List<String> opts = new ArrayList<String>();
        addUpperParserUsage(opts, mainParser_);
        if (command_ != null) {
            opts.add(command_);
        }
        for (ArgumentImpl arg : optargs_) {
            if (arg.getHelpControl() != Arguments.SUPPRESS
                    && (arg.getArgumentGroup() == null ||
                        !arg.getArgumentGroup().isMutex())) {
                opts.add(arg.formatShortSyntax());
            }
        }
        for (ArgumentGroupImpl group : arggroups_) {
            List<ArgumentImpl> args = filterSuppressedArgs(group.getArgs());
            int numArgs = args.size();
            if (group.isMutex()) {
                if (numArgs > 1) {
                    opts.add((group.isRequired() ? "(" : "[")
                            + args.get(0).formatShortSyntaxNoBracket());
                    for (int i = 1; i < numArgs - 1; ++i) {
                        ArgumentImpl arg = args.get(i);
                        opts.add("|");
                        opts.add(arg.formatShortSyntaxNoBracket());
                    }
                    opts.add("|");
                    opts.add(args.get(numArgs - 1).formatShortSyntaxNoBracket()
                            + (group.isRequired() ? ")" : "]"));
                } else if (numArgs == 1) {
                    if (group.isRequired()) {
                        opts.add(args.get(0).formatShortSyntaxNoBracket());
                    } else {
                        opts.add(args.get(0).formatShortSyntax());
                    }
                }
            }
        }
        for (ArgumentImpl arg : posargs_) {
            if (arg.getHelpControl() != Arguments.SUPPRESS) {
                opts.add(arg.formatShortSyntax());
            }
        }
        if (subparsers_.hasSubCommand()) {
            opts.add(subparsers_.formatShortSyntax());
            opts.add("...");
        }
        printArgumentUsage(writer, opts, offset, firstIndent, subsequentIndent,
                format_width);
    }

    /**
     * Returns arguments in {@code args} whose {@link Argument#getHelpControl()}
     * do not return {@link Arguments#SUPPRESS}.
     * 
     * @param args
     * @return filtered list of arguments
     */
    private static List<ArgumentImpl> filterSuppressedArgs(
            Collection<ArgumentImpl> args) {
        ArrayList<ArgumentImpl> res = new ArrayList<ArgumentImpl>();
        for (ArgumentImpl arg : args) {
            if (arg.getHelpControl() != Arguments.SUPPRESS) {
                res.add(arg);
            }
        }
        return res;
    }

    /**
     * Appends command, required optional arguments and positional arguments in
     * {@code parser} to {@code opts} recursively. Most upper parser stores
     * first, just like post order traversal.
     * 
     * @param opts
     *            Command, required optional arguments and positional arguments.
     * @param parser
     *            The parser
     */
    private void addUpperParserUsage(List<String> opts,
            ArgumentParserImpl parser) {
        if (parser == null) {
            return;
        }
        addUpperParserUsage(opts, parser.mainParser_);
        if (parser.command_ != null) {
            opts.add(parser.command_);
        }
        for (ArgumentImpl arg : parser.optargs_) {
            if (arg.getHelpControl() != Arguments.SUPPRESS
                    && arg.isRequired()
                    && (arg.getArgumentGroup() == null ||
                        !arg.getArgumentGroup().isMutex())) {
                opts.add(arg.formatShortSyntax());
            }
        }

        for (ArgumentGroupImpl group : parser.arggroups_) {
            List<ArgumentImpl> args = filterSuppressedArgs(group.getArgs());
            int numArgs = args.size();
            if (group.isMutex()) {
                if (numArgs > 1) {
                    if (group.isRequired()) {
                        opts.add("(" + args.get(0).formatShortSyntaxNoBracket());
                        for (int i = 1; i < numArgs - 1; ++i) {
                            ArgumentImpl arg = args.get(i);
                            opts.add("|");
                            opts.add(arg.formatShortSyntaxNoBracket());
                        }
                        opts.add("|");
                        opts.add(args.get(numArgs - 1)
                                .formatShortSyntaxNoBracket() + ")");
                    }
                } else if (numArgs == 1) {
                    if (group.isRequired()) {
                        opts.add(args.get(0).formatShortSyntaxNoBracket());
                    } else if (args.get(0).isRequired()) {
                        opts.add(args.get(0).formatShortSyntax());
                    }
                }
            }
        }

        for (ArgumentImpl arg : parser.posargs_) {
            if (arg.getHelpControl() != Arguments.SUPPRESS) {
                opts.add(arg.formatShortSyntax());
            }
        }
    }

    @Override
    public String formatUsage() {
        StringWriter writer = new StringWriter();
        printUsage(new PrintWriter(writer));
        return writer.toString();
    }

    @Override
    public ArgumentParserImpl setDefault(String dest, Object value) {
        defaults_.put(dest, value);
        return this;
    }

    @Override
    public ArgumentParser setDefaults(Map<String, Object> attrs) {
        defaults_.putAll(attrs);
        return this;
    }

    /**
     * Returns default value set by {@link ArgumentImpl#setDefault(Object)} or
     * {@link ArgumentParserImpl#setDefault(String, Object)}. Please note that
     * while parser-level defaults always override argument-level defaults while
     * parsing, this method examines argument-level defaults first. If no
     * default value is found, then check parser-level defaults.
     * 
     * @param dest
     *            attribute name of default value to get.
     * @return default value of given dest.
     */
    @Override
    public Object getDefault(String dest) {
        for (ArgumentImpl arg : optargs_) {
            if (dest.equals(arg.getDest()) && arg.getDefault() != null) {
                return arg.getDefault();
            }
        }
        for (ArgumentImpl arg : posargs_) {
            if (dest.equals(arg.getDest()) && arg.getDefault() != null) {
                return arg.getDefault();
            }
        }
        return defaults_.get(dest);
    }

    @Override
    public Namespace parseArgsOrFail(String args[]) {
        try {
            Namespace ns = parseArgs(args);
            return ns;
        } catch (ArgumentParserException e) {
            handleError(e);
            System.exit(1);
        }
        return null;
    }

    @Override
    public Namespace parseArgs(String args[]) throws ArgumentParserException {
        Map<String, Object> attrs = new HashMap<String, Object>();
        parseArgs(args, attrs);
        return new Namespace(attrs);
    }

    @Override
    public void parseArgs(String[] args, Map<String, Object> attrs)
            throws ArgumentParserException {
        parseArgs(args, 0, attrs);
    }

    @Override
    public void parseArgs(String[] args, Object userData)
            throws ArgumentParserException {
        Map<String, Object> opts = new HashMap<String, Object>();
        parseArgs(args, opts, userData);
    }

	@Override
	public void parseArgs(String[] args, Map<String, Object> attrs,
			Object userData) throws ArgumentParserException {
		parseArgs(args, 0, attrs);

		Class userClass = userData.getClass();
		while (userClass != null) {
			for (Field field : userClass.getDeclaredFields()) {
				Arg ann = field.getAnnotation(Arg.class);
				if (ann != null) {
					String argDest = ann.dest();
					if (argDest.isEmpty()) {
						argDest = field.getName();
					}
					if (!attrs.containsKey(argDest)) {
						continue;
					}
					Object val = attrs.get(argDest);
					try {
						field.setAccessible(true);
						field.set(userData,
								ReflectHelper.list2Array(field.getType(), val));
					} catch (RuntimeException e) {
						if (!ann.ignoreError()) {
							throw e;
						}
					} catch (Exception e) {
						if (!ann.ignoreError()) {
							throw new IllegalArgumentException(String.format(
									"Could not set %s to field %s", val,
									field.getName()), e);
						}
					}
				}
			}
			for (Method method : userClass.getDeclaredMethods()) {
				Arg ann = method.getAnnotation(Arg.class);
				if (ann != null) {
					String argDest = ann.dest();
					if (argDest.isEmpty()) {
						argDest = method.getName();
					}
					if (!attrs.containsKey(argDest)) {
						continue;
					}
					Object val = attrs.get(argDest);
					Class<?> fargs[] = method.getParameterTypes();
					if (fargs.length != 1) {
						throw new IllegalArgumentException(String.format(
								"Method %s must have one formal parameter",
								method.getName()));
					}
					try {
						method.setAccessible(true);
						method.invoke(userData,
								ReflectHelper.list2Array(fargs[0], val));
					} catch (RuntimeException e) {
						if (!ann.ignoreError()) {
							throw e;
						}
					} catch (Exception e) {
						if (!ann.ignoreError()) {
							throw new IllegalArgumentException(String.format(
									"Could not call method %s with %s",
									method.getName(), val), e);
						}
					}
				}
			}
			userClass = userClass.getSuperclass();
		}

	}

    public void parseArgs(String args[], int offset, Map<String, Object> attrs)
            throws ArgumentParserException {
        ParseState state = new ParseState(args, offset, negNumFlag_);
        parseArgs(state, attrs);
    }

    public void parseArgs(ParseState state, Map<String, Object> attrs)
            throws ArgumentParserException {
        populateDefaults(attrs);
        Set<ArgumentImpl> used = new HashSet<ArgumentImpl>();
        ArgumentImpl groupUsed[] = new ArgumentImpl[arggroups_.size()];
        int posargIndex = 0;
        int posargsLen = posargs_.size();
        while (state.isArgAvail()) {
            // We first evaluate flagFound(state) before comparing arg to "--"
            // in order to expand arguments from file.
            if (flagFound(state) && !"--".equals(state.getArg())) {
                String term = state.getArg();
                int p = term.indexOf("=");
                String flag;
                String embeddedValue;
                if (p == -1) {
                    flag = term;
                    embeddedValue = null;
                } else {
                    flag = term.substring(0, p);
                    embeddedValue = term.substring(p + 1);
                }
                ArgumentImpl arg = optargIndex_.get(flag);
                if (arg == null) {
                    boolean shortOptsFound = false;
                    if (SHORT_OPTS_PATTERN.matcher(term).matches()
                            && optargIndex_.get(term) == null) {
                        shortOptsFound = true;
                        // Possible concatenated short options
                        for (int i = 1, termlen = term.length(); i < termlen; ++i) {
                            String shortFlag = "-" + term.substring(i, i + 1);
                            arg = optargIndex_.get(shortFlag);
                            if (arg == null) {
                                shortOptsFound = false;
                                break;
                            }
                            if (arg.getAction().consumeArgument()) {
                                flag = shortFlag;
                                shortOptsFound = true;
                                embeddedValue = term.substring(i + 1);
                                break;
                            }
                            checkMutex(arg, groupUsed);
                            arg.run(this, attrs, shortFlag, null);
                            used.add(arg);
                        }
                    }
                    if (!shortOptsFound) {
                        throw new UnrecognizedArgumentException(String.format(
                                "unrecognized arguments: %s", term), this, term);
                    }
                }
                assert (arg.getAction() != null);
                ++state.index;
                checkMutex(arg, groupUsed);
                processArg(attrs, state, arg, flag, embeddedValue);
                used.add(arg);
            } else if ("--".equals(state.getArg()) && !state.consumedSeparator) {
                state.consumedSeparator = true;
                state.negNumFlag = false;
                ++state.index;
            } else if (posargIndex < posargsLen) {
                ArgumentImpl arg = posargs_.get(posargIndex++);
                processArg(attrs, state, arg, null, null);
            } else if (!state.consumedSeparator && subparsers_.hasSubCommand()) {
                checkRequiredArgument(used, posargIndex);
                checkRequiredMutex(groupUsed);
                subparsers_.parseArg(state, attrs);
                return;
            } else {
                throw new ArgumentParserException(String.format(
                        "unrecognized arguments: %s",
                        TextHelper.concat(state.args, state.index, " ")), this);
            }
        }
        if (subparsers_.hasSubCommand()) {
            throw new ArgumentParserException("too few arguments", this);
        }
        while (posargIndex < posargsLen) {
            ArgumentImpl arg = posargs_.get(posargIndex++);
            processArg(attrs, state, arg, null, null);
        }
        checkRequiredArgument(used, posargIndex);
        checkRequiredMutex(groupUsed);
    }

    /**
     * Check that another option in mutually exclusive group has already been
     * specified. If so, throw an exception.
     * 
     * @param arg
     *            The argument currently processed
     * @param groupUsed
     *            The cache of used argument in each groups.
     * @throws ArgumentParserException
     *             If another option in mutually exclusive group has already
     *             been used.
     */
    private void checkMutex(ArgumentImpl arg, ArgumentImpl[] groupUsed)
            throws ArgumentParserException {
        if (arg.getArgumentGroup() != null) {
            if (arg.getArgumentGroup().isMutex()) {
                ArgumentImpl usedMutexArg = groupUsed[arg.getArgumentGroup()
                        .getIndex()];
                if (usedMutexArg == null) {
                    groupUsed[arg.getArgumentGroup().getIndex()] = arg;
                } else if(usedMutexArg != arg) {
                    throw new ArgumentParserException(String.format(
                            "not allowed with argument %s",
                            usedMutexArg.textualName()), this, arg);
                }
            }
        }
    }

    /**
     * @param res
     * @param state
     *            state.offset points to the argument next to flag
     * @param arg
     * @param flag
     * @param embeddedValue
     *            If optional argument is given as "foo=bar" or "-fbar" (short
     *            option), embedded value is "bar". Otherwise {@code null}
     * @throws ArgumentParserException
     */
    private void processArg(Map<String, Object> res, ParseState state,
            ArgumentImpl arg, String flag, String embeddedValue)
            throws ArgumentParserException {
        if (!arg.getAction().consumeArgument()) {
            if (embeddedValue == null) {
                arg.run(this, res, flag, null);
                return;
            } else {
                throw new ArgumentParserException(String.format(
                        "ignore implicit argument '%s'", embeddedValue), this,
                        arg);
            }
        }
        if (arg.getMinNumArg() == -1
                || (arg.getMinNumArg() == 0 && arg.getMaxNumArg() == 1)) {
            String argval = null;
            if (embeddedValue == null) {
                if (state.isArgAvail() && !flagFound(state)) {
                    argval = state.getArg();
                    ++state.index;
                }
            } else {
                argval = embeddedValue;
            }
            if (argval == null) {
                if (arg.getMinNumArg() == -1) {
                    if (arg.isOptionalArgument()) {
                        throw new ArgumentParserException(
                                "expected one argument", this, arg);
                    } else {
                        throw new ArgumentParserException("too few arguments",
                                this);
                    }
                } else if (arg.isOptionalArgument()) {
                    arg.run(this, res, flag, arg.getConst());
                }
            } else {
                arg.run(this, res, flag, arg.convert(this, argval));
            }
        } else {
            List<Object> list = new ArrayList<Object>();
            if (embeddedValue == null) {
                for (int i = 0; i < arg.getMaxNumArg() && state.isArgAvail(); ++i, ++state.index) {
                    if (flagFound(state)) {
                        break;
                    }
                    list.add(arg.convert(this, state.getArg()));
                }
            } else {
                list.add(arg.convert(this, embeddedValue));
            }
            if (list.size() < arg.getMinNumArg()) {
                if (arg.isOptionalArgument()) {
                    throw new ArgumentParserException(String.format(
                            "expected %d argument(s)", arg.getMinNumArg()),
                            this, arg);
                } else {
                    throw new ArgumentParserException("too few arguments", this);
                }
            }
            if (!list.isEmpty()) {
                arg.run(this, res, flag, list);
            }
        }
    }

    /**
     * Returns true if state.getArg() is flag. Note that if "--" is met and not
     * consumed, this function returns true, because "--" is treated as special
     * optional argument. If prefixFileChar is found in prefix of argument, read
     * arguments from that file and expand arguments in state necessary.
     * 
     * @param state
     * @return
     * @throws ArgumentParserException
     */
    private boolean flagFound(ParseState state) throws ArgumentParserException {
        while (fromFileFound(state)) {
            state.resetArgs(extendArgs(
                    fromFilePrefixPattern_.removePrefix(state.getArg()),
                    state.args, state.index + 1));
        }
        String term = state.getArg();
        if (state.consumedSeparator) {
            return false;
        } else if ("--".equals(term)) {
            return true;
        }
        return prefixPattern_.match(term)
                && (state.negNumFlag || !NEG_NUM_PATTERN.matcher(term)
                        .matches());
    }

    private boolean fromFileFound(ParseState state) {
        return fromFilePrefixPattern_ != null
                && fromFilePrefixPattern_.match(state.getArg());
    }

    /**
     * Extends arguments by reading additional arguments from file. The length
     * of returned array is oldargs.length - offset + the number of arguments in
     * the file. We discard [0,offset) in oldargs. So the returned array starts
     * with oldargs[offset].
     * 
     * @param file
     *            File from which additional arguments are read.
     * @param oldargs
     *            Old arguments
     * @param offset
     *            Offset in old arguments. This is typically the argument next
     *            to fromFilePrefix argument.
     * @return The extended new argument array.
     * @throws ArgumentParserException
     */
    private String[] extendArgs(String file, String oldargs[], int offset)
            throws ArgumentParserException {
        List<String> list = new ArrayList<String>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            throw new ArgumentParserException(String.format(
                    "Could not read arguments from file '%s'", file), e, this);
        }
        String newargs[] = new String[list.size() + oldargs.length - offset];
        list.toArray(newargs);
        System.arraycopy(oldargs, offset, newargs, list.size(), oldargs.length
                - offset);
        return newargs;
    }

    private void checkRequiredArgument(Set<ArgumentImpl> used, int posargIndex)
            throws ArgumentParserException {
        for (ArgumentImpl arg : optargs_) {
            if (arg.isRequired() && !used.contains(arg)) {
                throw new ArgumentParserException(String.format(
                        "argument %s is required", arg.textualName()), this);
            }
        }
        if (posargs_.size() > posargIndex) {
            throw new ArgumentParserException("too few arguments", this);
        }

    }

    private void checkRequiredMutex(ArgumentImpl[] used)
            throws ArgumentParserException {
        for (int i = 0; i < arggroups_.size(); ++i) {
            ArgumentGroupImpl group = arggroups_.get(i);
            if (group.isMutex() && group.isRequired() && used[i] == null) {
                StringBuilder sb = new StringBuilder();
                for (ArgumentImpl arg : group.getArgs()) {
                    sb.append(arg.textualName()).append(" ");
                }
                throw new ArgumentParserException(String.format(
                        "one of the arguments %sis required", sb.toString()),
                        this);
            }
        }
    }

    private void populateDefaults(Map<String, Object> opts) {
        for (ArgumentImpl arg : posargs_) {
            if (arg.getDefaultControl() != Arguments.SUPPRESS) {
                opts.put(arg.getDest(), arg.getDefault());
            }
        }
        for (ArgumentImpl arg : optargs_) {
            if (arg.getDefaultControl() != Arguments.SUPPRESS) {
                opts.put(arg.getDest(), arg.getDefault());
            }
        }
        for (Map.Entry<String, Object> entry : defaults_.entrySet()) {
            opts.put(entry.getKey(), entry.getValue());
        }
    }

    public String getProg() {
        return prog_;
    }

    @Override
    public void printVersion() {
        printVersion(new PrintWriter(System.out));
    }

    @Override
    public void printVersion(PrintWriter writer) {
        writer.format("%s\n", version_);
        writer.flush();
    }

    @Override
    public String formatVersion() {
        return version_;
    }

    @Override
    public void handleError(ArgumentParserException e) {
        if (e.getParser() != this) {
            e.getParser().handleError(e);
            return;
        }
        PrintWriter writer = new PrintWriter(System.err);
        printUsage(writer);
        writer.format("%s: error: %s\n", prog_, e.getMessage());
        if (e instanceof UnrecognizedArgumentException) {
            UnrecognizedArgumentException ex = (UnrecognizedArgumentException) e;
            String argument = ex.getArgument();
            if (prefixPattern_.match(argument)) {
                String flagBody = prefixPattern_.removePrefix(argument);
                if (flagBody.length() >= 2) {
                    printFlagCandidates(flagBody, writer);
                }
            }
        } else if (e instanceof UnrecognizedCommandException) {
            UnrecognizedCommandException ex = (UnrecognizedCommandException) e;
            String command = ex.getCommand();
            printCommandCandidates(command, writer);
        }
        writer.flush();
    }

    /**
     * Calculates Damerau–Levenshtein distance between string {@code a} and
     * {@code b} with given costs.
     * 
     * @param a
     *            String
     * @param b
     *            String
     * @param swap
     *            Cost to swap 2 adjacent characters.
     * @param sub
     *            Cost to substitute character.
     * @param add
     *            Cost to add character.
     * @param del
     *            Cost to delete character.
     * @return Damerau–Levenshtein distance between {@code a} and {@code b}
     */
    private int levenshtein(String a, String b, int swap, int sub, int add,
            int del) {
        int alen = a.length();
        int blen = b.length();
        int dp[][] = new int[3][blen + 1];
        for (int i = 0; i <= blen; ++i) {
            dp[1][i] = i;
        }
        for (int i = 1; i <= alen; ++i) {
            dp[0][0] = i;
            for (int j = 1; j <= blen; ++j) {
                dp[0][j] = dp[1][j - 1]
                        + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : sub);
                if (i >= 2 && j >= 2 && a.charAt(i - 1) != b.charAt(j - 1)
                        && a.charAt(i - 2) == b.charAt(j - 1)
                        && a.charAt(i - 1) == b.charAt(j - 2)) {
                    dp[0][j] = Math.min(dp[0][j], dp[2][j - 2] + swap);
                }
                dp[0][j] = Math.min(dp[0][j],
                        Math.min(dp[1][j] + del, dp[0][j - 1] + add));
            }
            int temp[] = dp[2];
            dp[2] = dp[1];
            dp[1] = dp[0];
            dp[0] = temp;
        }
        return dp[1][blen];
    }

    private static class SubjectBody {
        public String subject;
        public String body;

        public SubjectBody(String subject, String body) {
            this.subject = subject;
            this.body = body;
        }
    }

    private static class Candidate implements Comparable<Candidate> {
        public int similarity;
        public String subject;

        public Candidate(int similarity, String subject) {
            this.similarity = similarity;
            this.subject = subject;
        }

        @Override
        public int compareTo(Candidate rhs) {
            if (similarity < rhs.similarity) {
                return -1;
            } else if (similarity == rhs.similarity) {
                return subject.compareTo(rhs.subject);
            } else {
                return 1;
            }
        }
    }

    private void printFlagCandidates(String flagBody, PrintWriter writer) {
        List<SubjectBody> subjects = new ArrayList<SubjectBody>();
        for (ArgumentImpl arg : optargs_) {
            String flags[] = arg.getFlags();
            for (int i = 0, len = flags.length; i < len; ++i) {
                String body = prefixPattern_.removePrefix(flags[i]);
                if (body.length() <= 1) {
                    continue;
                }
                subjects.add(new SubjectBody(flags[i], body));
            }
        }
        printCandidates(flagBody, subjects, writer);
    }

    private void printCommandCandidates(String command, PrintWriter writer) {
        List<SubjectBody> subjects = new ArrayList<SubjectBody>();
        for (String com : subparsers_.getCommands()) {
            subjects.add(new SubjectBody(com, com));
        }
        printCandidates(command, subjects, writer);
    }

    /**
     * Prints most similar subjects in subjects to body. Similarity is
     * calculated between body and each {@link SubjectBody#body} in subjects.
     * 
     * @param body
     *            String to compare.
     * @param subjects
     *            Target to be compared.
     * @param writer
     *            Output
     */
    private void printCandidates(String body, List<SubjectBody> subjects,
            PrintWriter writer) {
        List<Candidate> candidates = new ArrayList<Candidate>();
        for (SubjectBody sub : subjects) {
            if (sub.body.startsWith(body)) {
                candidates.add(new Candidate(0, sub.subject));
                continue;
            } else {
                // Cost values were borrowed from git, help.c
                candidates.add(new Candidate(levenshtein(body, sub.body, 0, 2,
                        1, 4), sub.subject));
            }
        }
        if (candidates.isEmpty()) {
            return;
        }
        Collections.sort(candidates);
        int threshold = candidates.get(0).similarity;
        // Magic number 7 was borrowed from git, help.c
        if (threshold >= 7) {
            return;
        }
        writer.write("\nDid you mean:\n");
        for (Candidate cand : candidates) {
            if (cand.similarity > threshold) {
                break;
            }
            writer.format("\t%s\n", cand.subject);
        }
    }

    public String getCommand() {
        return command_;
    }

    public TextWidthCounter getTextWidthCounter() {
        return textWidthCounter_;
    }

    public String getPrefixChars() {
        return prefixPattern_.getPrefixChars();
    }

    public String getFromFilePrefixChars() {
        return fromFilePrefixPattern_ == null ? null : fromFilePrefixPattern_
                .getPrefixChars();
    }
}
