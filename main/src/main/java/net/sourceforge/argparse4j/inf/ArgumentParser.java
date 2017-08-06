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
package net.sourceforge.argparse4j.inf;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.annotation.Arg;

/**
 * <p>
 * This interface defines behavior of ArgumentParser.
 * </p>
 * <p>
 * The typical usage is set description using {@link #description(String)} and
 * add arguments using {@link #addArgument(String...)}. To add sub-command,
 * first call {@link #addSubparsers()} to obtain {@link Subparsers} object.
 * {@link Subparsers} object provides necessary methods to add sub-commands. To
 * make a conceptual group of arguments, first call
 * {@link #addArgumentGroup(String)} to create {@link ArgumentGroup} object. And
 * add argument to that group using {@link ArgumentGroup#addArgument(String...)}
 * . Similarly, to add the mutually exclusive group of arguments, use
 * {@link #addMutuallyExclusiveGroup(String)} to create
 * {@link MutuallyExclusiveGroup} object. To parse command-line arguments, call
 * {@link #parseArgs(String[])} or several overloaded methods.
 * </p>
 */
public interface ArgumentParser extends ArgumentContainer {
    /**
     * <p>
     * Creates new {@link ArgumentGroup} object and adds to this parser and
     * returns the object.
     * </p>
     * <p>
     * The {@code title} is printed in help message as a title of this group.
     * {@link ArgumentGroup} provides a way to conceptually group up command
     * line arguments.
     * </p>
     * 
     * @param title
     *            The title printed in help message.
     * @return {@link ArgumentGroup} object.
     */
    ArgumentGroup addArgumentGroup(String title);

    /**
     * <p>
     * Creates new mutually exclusive group, {@link MutuallyExclusiveGroup}
     * object, without title and adds to this parser and returns the object.
     * </p>
     * 
     * @return {@link MutuallyExclusiveGroup} object.
     */
    MutuallyExclusiveGroup addMutuallyExclusiveGroup();

    /**
     * <p>
     * Creates new mutually exclusive group, {@link MutuallyExclusiveGroup}
     * object, and adds to this parser and returns the object.
     * </p>
     * <p>
     * The arguments added to this group are mutually exclusive; if more than
     * one argument belong to the group are specified, an error will be
     * reported. The {@code title} is printed in help message as a title of this
     * group.
     * </p>
     * 
     * @param title
     *            The title printed in help message.
     * @return The {@link MutuallyExclusiveGroup} object.
     */
    MutuallyExclusiveGroup addMutuallyExclusiveGroup(String title);

    /**
     * <p>
     * Returns {@link Subparsers}.
     * </p>
     * <p>
     * The method name is rather controversial because repeated call of this
     * method does not add new {@link Subparsers} object. Instead, this method
     * always returns same {@link Subparsers} object. {@link Subparsers} object
     * provides a way to add sub-commands.
     * </p>
     * 
     * @return {@link Subparsers} object.
     */
    Subparsers addSubparsers();

    /**
     * <p>
     * Sets the text to display as usage line. By default, the usage line is
     * calculated from the arguments this object contains.
     * </p>
     * <p>
     * If the given usage contains <tt>${prog}</tt> string, it will be replaced
     * with the program name given in
     * {@link ArgumentParsers#newArgumentParser(String)}.
     * </p>
     * 
     * @param usage
     *            usage text
     * @return this
     */
    ArgumentParser usage(String usage);

    @Override
    ArgumentParser description(String description);

    /**
     * Sets the text to display after the argument help.
     * 
     * @param epilog
     *            The text to display after the argument help.
     * @return this
     */
    ArgumentParser epilog(String epilog);

    /**
     * <p>
     * Sets version string. It will be displayed {@link #printVersion()}.
     * </p>
     * <p>
     * If the given usage contains <tt>${prog}</tt> string, it will be replaced
     * with the program name given in
     * {@link ArgumentParsers#newArgumentParser(String)}. This processed text
     * will be printed without text-wrapping.
     * </p>
     * 
     * @param version
     *            The version string.
     * @return this
     */
    ArgumentParser version(String version);

    /**
     * <p>
     * If defaultHelp is {@code true}, the default values of arguments are
     * printed in help message.
     * </p>
     * <p>
     * By default, the default values are not printed in help message.
     * </p>
     * 
     * @param defaultHelp
     *            Switch to display the default value in help message.
     * @return this
     */
    ArgumentParser defaultHelp(boolean defaultHelp);

    /**
     * Prints help message in stdout.
     */
    void printHelp();

    /**
     * Prints help message in writer.
     * 
     * @param writer
     *            Writer to print message.
     */
    void printHelp(PrintWriter writer);

    /**
     * Returns help message.
     * 
     * @return The help message.
     */
    String formatHelp();

    /**
     * Print a brief description of how the program should be invoked on the
     * command line in stdout.
     */
    void printUsage();

    /**
     * Print a brief description of how the program should be invoked on the
     * command line in writer.
     * 
     * @param writer
     *            Writer to print message.
     */
    void printUsage(PrintWriter writer);

    /**
     * Returns a brief description of how the program should be invoked on the
     * command line.
     * 
     * @return Usage text.
     */
    String formatUsage();

    /**
     * Prints version string in stdout.
     */
    void printVersion();

    /**
     * Prints version string in writer.
     * 
     * @param writer
     *            Writer to print version string.
     */
    void printVersion(PrintWriter writer);

    /**
     * Returns version string.
     * 
     * @return The version string.
     */
    String formatVersion();

    /**
     * <p>
     * Sets parser-level default value of attribute {@code dest}.
     * </p>
     * <p>
     * The parser-level defaults always override argument-level defaults.
     * </p>
     * 
     * @param dest
     *            The attribute name.
     * @param value
     *            The default value.
     * @return this
     */
    ArgumentParser setDefault(String dest, Object value);

    /**
     * <p>
     * Sets parser-level default values from {@code attrs}.
     * </p>
     * <p>
     * All key-value pair in {@code attrs} are registered to parser-level
     * defaults. The parser-level defaults always override argument-level
     * defaults.
     * </p>
     * 
     * @param attrs
     *            The parser-level default values to add.
     * @return this
     */
    ArgumentParser setDefaults(Map<String, Object> attrs);

    /**
     * <p>
     * Returns default value of given {@code dest}.
     * </p>
     * <p>
     * Returns default value set by {@link Argument#setDefault(Object)},
     * {@link ArgumentParser#setDefault(String, Object)} or
     * {@link #setDefaults(Map)}. Please note that while parser-level defaults
     * always override argument-level defaults while parsing, this method
     * examines argument-level defaults first. If no default value is found,
     * then check parser-level defaults. If no default value is found, returns
     * {@code null}.
     * </p>
     * 
     * @param dest
     *            The attribute name of default value to get.
     * @return The default value of given dest.
     */
    Object getDefault(String dest);

    /**
     * <p>
     * Parses command line arguments, handling any errors.
     * </p>
     * <p>
     * This is a shortcut method that combines {@link #parseArgs} and
     * {@link #handleError }. If the arguments can be successfully parsed, the
     * resulted attributes are returned as a {@link Namespace} object.
     * Otherwise, the program exits with a <code>1</code> return code.
     * 
     * </p>
     * 
     * @param args
     *            Command line arguments.
     * @return {@link Namespace} object.
     */
    Namespace parseArgsOrFail(String args[]);

    /**
     * <p>
     * Parses command line arguments.
     * </p>
     * <p>
     * The resulted attributes are returned as {@link Namespace} object. This
     * method must not alter the status of this parser and can be called
     * multiple times.
     * </p>
     * 
     * @param args
     *            Command line arguments.
     * @return {@link Namespace} object.
     * @throws ArgumentParserException
     *             If an error occurred.
     */
    Namespace parseArgs(String args[]) throws ArgumentParserException;

    /**
     * <p>
     * Parses command line arguments.
     * </p>
     * <p>
     * Unlike {@link #parseArgs(String[])}, which returns {@link Namespace}
     * object, this method stores attributes in given {@code attrs}.
     * </p>
     * 
     * @param args
     *            Command line arguments.
     * @param attrs
     *            Map object to store attributes.
     * @throws ArgumentParserException
     *             If an error occurred.
     */
    void parseArgs(String[] args, Map<String, Object> attrs)
            throws ArgumentParserException;

    /**
     * <p>
     * Parses command line arguments.
     * </p>
     * <p>
     * Unlike {@link #parseArgs(String[])}, which returns {@link Namespace}
     * object, this method stores attributes in given {@code userData}. The
     * location to store value is designated using {@link Arg} annotations. User
     * don't have to specify {@link Arg} for all attributes: the missing
     * attributes are just skipped. This method performs simple {@link List} to
     * generic array conversion. For example, user can assign
     * {@code List<Integer>} attribute to generic array {@code int[]}.
     * </p>
     * 
     * @param args
     *            Command line arguments.
     * @param userData
     *            Object to store attributes.
     * @throws ArgumentParserException
     *             If an error occurred.
     */
    void parseArgs(String[] args, Object userData)
            throws ArgumentParserException;

    /**
     * <p>
     * Parses command line arguments.
     * </p>
     * <p>
     * This is a combination of {@link #parseArgs(String[], Map)} and
     * {@link #parseArgs(String[], Object)}. The all attributes will be stored
     * in {@code attrs}. The attributes specified in {@link Arg} annotations
     * will be also stored in {@code userData}.
     * </p>
     * 
     * @param args
     *            Command line arguments.
     * @param attrs
     *            Map to store attributes.
     * @param userData
     *            Object to store attributes.
     * @throws ArgumentParserException
     *             If an error occurred.
     */
    void parseArgs(String[] args, Map<String, Object> attrs, Object userData)
            throws ArgumentParserException;

    /**
     * <p>
     * Just like {@link #parseArgsOrFail(String[])}, but parses only known
     * arguments without throwing exception for unrecognized arguments. If
     * {@code unknown} is not null, unrecognized arguments will be stored in it.
     * </p>
     * 
     * @param args
     *            Command line arguments.
     * @param unknown
     *            Output variable to store unrecognized arguments, or null
     * @return {@link Namespace} object.
     * @since 0.7.0
     */
    Namespace parseKnownArgsOrFail(String args[], List<String> unknown);

    /**
     * <p>
     * Just like {@link #parseArgs(String[])}, but parses only known arguments
     * without throwing exception for unrecognized arguments. If {@code unknown}
     * is not null, unrecognized arguments will be stored in it.
     * </p>
     * 
     * @param args
     *            Command line arguments.
     * @param unknown
     *            Output variable to store unrecognized arguments, or null
     * @return {@link Namespace} object.
     * @throws ArgumentParserException
     *             If an error occurred.
     * @since 0.7.0
     */
    Namespace parseKnownArgs(String args[], List<String> unknown)
            throws ArgumentParserException;

    /**
     * <p>
     * Just like {@link #parseArgs(String[], Map)}, but parses only known
     * arguments without throwing exception for unrecognized arguments. If
     * {@code unknown} is not null, unrecognized arguments will be stored in it.
     * </p>
     * 
     * @param args
     *            Command line arguments.
     * @param unknown
     *            Output variable to store unrecognized arguments, or null
     * @param attrs
     *            Map object to store attributes.
     * @throws ArgumentParserException
     *             If an error occurred.
     * @since 0.7.0
     */
    void parseKnownArgs(String[] args, List<String> unknown,
            Map<String, Object> attrs) throws ArgumentParserException;

    /**
     * <p>
     * Just like {@link #parseArgs(String[], Object)}, but parses only known
     * arguments without throwing exception for unrecognized arguments. If
     * {@code unknown} is not null, unrecognized arguments will be stored in it.
     * </p>
     * 
     * @param args
     *            Command line arguments.
     * @param unknown
     *            Output variable to store unrecognized arguments, or null
     * @param userData
     *            Object to store attributes.
     * @throws ArgumentParserException
     *             If an error occurred.
     * @since 0.7.0
     */
    void parseKnownArgs(String[] args, List<String> unknown, Object userData)
            throws ArgumentParserException;

    /**
     * <p>
     * Just like {@link #parseArgs(String[], Map, Object)}, but parses only
     * known arguments without throwing exception for unrecognized arguments. If
     * {@code unknown} is not null, unrecognized arguments will be stored in it.
     * </p>
     * 
     * @param args
     *            Command line arguments.
     * @param unknown
     *            Output variable to store unrecognized arguments, or null
     * @param attrs
     *            Map to store attributes.
     * @param userData
     *            Object to store attributes.
     * @throws ArgumentParserException
     *             If an error occurred.
     * @since 0.7.0
     */
    void parseKnownArgs(String[] args, List<String> unknown,
            Map<String, Object> attrs, Object userData)
            throws ArgumentParserException;

    /**
     * <p>
     * Prints usage and error message.
     * </p>
     * <p>
     * Please note that this method does not terminate the program.
     * </p>
     * 
     * @param e
     *            Error thrown by {@link #parseArgs(String[])}.
     */
    void handleError(ArgumentParserException e);

    /**
     * <p>
     * Prints usage and error message to the given writer.
     * </p>
     * <p>
     * Please note that this method does not terminate the program.
     * </p>
     * 
     * @param e
     *            Error thrown by {@link #parseArgs(String[])}.
     * @since 0.8.0
     */
    void handleError(ArgumentParserException e, PrintWriter writer);

    /**
     * <p>
     * Get the configuration of this argument parser.
     * </p>
     *
     * @return The argument parser configuration.
     * @since 0.8.0
     */
    ArgumentParserConfiguration getConfig();
}
