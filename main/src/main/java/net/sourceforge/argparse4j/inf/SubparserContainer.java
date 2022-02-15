package net.sourceforge.argparse4j.inf;

import net.sourceforge.argparse4j.ArgumentParsers;

/**
 * A container to which subparsers can be added.
 */
public interface SubparserContainer {

    /**
     * <p>
     * Adds and returns {@link Subparser} object with given sub-command name.
     * The given command must be unique for each SubparserContainer instance.
     * </p>
     * <p>
     * The prefixChars is inherited from main ArgumentParser.
     * </p>
     *
     * @param command
     *            Sub-command name
     * @return {@link Subparser} object.
     */
    Subparser addParser(String command);

    /**
     * <p>
     * Adds and returns {@link Subparser} object with given sub-command name and
     * addHelp. The given command must be unique for each SubparserContainer
     * instance.
     * </p>
     * <p>
     * For {@code addHelp}, see
     * {@link ArgumentParsers#newArgumentParser(String, boolean, String)}. The
     * prefixChars is inherited from main ArgumentParser.
     * </p>
     *
     * @param command
     *            Sub-command name
     * @param addHelp
     *            If true, {@code -h/--help} are available. If false, they are
     *            not.
     * @return {@link Subparser} object
     */
    Subparser addParser(String command, boolean addHelp);

    /**
     * <p>
     * Adds and returns {@link Subparser} object with given sub-command name,
     * addHelp and prefixChars. The given command must be unique for each
     * SubparserContainer instance.
     * </p>
     * <p>
     * For {@code addHelp}, see
     * {@link ArgumentParsers#newArgumentParser(String, boolean, String)}.
     * </p>
     *
     * @param command
     *            Sub-command name
     * @param addHelp
     *            If true, {@code -h/--help} are available. If false, they are
     *            not.
     * @param prefixChars
     *            The set of characters that prefix named arguments.
     * @return {@link Subparser} object
     */
    Subparser addParser(String command, boolean addHelp, String prefixChars);
}
