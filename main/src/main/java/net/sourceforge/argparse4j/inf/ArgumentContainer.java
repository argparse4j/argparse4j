package net.sourceforge.argparse4j.inf;

/**
 * A container to which arguments can be added.
 * 
 * @since 0.8.0
 */
public interface ArgumentContainer {
    /**
     * <p>
     * Creates new {@link Argument}, adds it to this container and returns it.
     * </p>
     * <p>
     * The {@code nameOrFlags} is either a single name of positional argument or
     * a list of option strings for named argument, e.g. {@code foo} or
     * {@code -f, --foo}.
     * </p>
     *
     * @param nameOrFlags
     *            A name or a list of option strings of new {@link Argument}.
     * @return {@link Argument} object.
     */
    Argument addArgument(String... nameOrFlags);

    /**
     * Sets the description for the arguments of this container.
     *
     * @param description
     *            The description of this container.
     * @return this
     */
    ArgumentContainer description(String description);
}
