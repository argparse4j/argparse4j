package net.sourceforge.argparse4j.inf;

import java.io.PrintWriter;
import java.util.Map;

/**
 * <p>
 * Super interface of ArgumentParser.
 * </p>
 * <p>
 * This interface defines common behavior of all derived ArgumentParsers.
 * </p>
 */
public interface BaseArgumentParser {

    /**
     * <p>
     * Creates new {@link Argument} object and adds to this parser and returns
     * the object.
     * </p>
     * <p>
     * The {@code nameOrFlags} is either a single name of positional argument or
     * a list of option strings for optional argument, e.g. {@code foo} or
     * {@code -f, --foo}.
     * </p>
     * 
     * @param nameOrFlags
     *            A name or a list of option strings of new {@link Argument}.
     * @return {@link Argument} object.
     */
    Argument addArgument(String... nameOrFlags);

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
     * Sets the text to display before the argument help.
     * 
     * @param description
     *            The text to display before the argument help.
     * @return this
     */
    BaseArgumentParser description(String description);

    /**
     * Sets the text to display after the argument help.
     * 
     * @param epilog
     *            The text to display after the argument help.
     * @return this
     */
    BaseArgumentParser epilog(String epilog);

    /**
     * <p>
     * Sets version string.
     * </p>
     * <p>
     * It will be displayed {@link #printVersion()}. Please note that given
     * version string is displayed as is: no text-wrapping will be made.
     * </p>
     * 
     * @param version
     *            The version string.
     * @return this
     */
    BaseArgumentParser version(String version);

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
    BaseArgumentParser defaultHelp(boolean defaultHelp);

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
    BaseArgumentParser setDefault(String dest, Object value);

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
    BaseArgumentParser setDefaults(Map<String, Object> attrs);

    /**
     * <p>
     * Returns default value of given {@code dest}.
     * </p>
     * <p>
     * Returns default value set by {@link Argument#setDefault(Object)},
     * {@link BaseArgumentParser#setDefault(String, Object)} or
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
}
