package net.sourceforge.argparse4j;

/**
 * <p>
 * Use one of these values with
 * {@link ArgumentParsers#newFor(String, DefaultSettings)} to specify of which
 * version the default settings must be used. The chosen defaults will
 * continue to be used by newer versions of argparse4j, ensuring that the
 * behavior of the application does not change when argparse4j is upgraded.
 * </p>
 */
public enum DefaultSettings {
    /**
     * <p>
     * Use the default settings of versions before 0.9.0.
     * </p>
     */
    INITIAL_DEFAULT_SETTINGS {
        @Override
        public void apply(ArgumentParserBuilder builder) {
            // No action needed. The builder initializes the defaults correctly.
        }
    },

    /**
     * <p>
     * The default settings for version 0.9.0. The following settings are
     * changed from {@link #INITIAL_DEFAULT_SETTINGS}:
     * </p>
     *
     * <ul>
     *     <li>{@link ArgumentParserBuilder#mustHelpTextIncludeMutualExclusivity(boolean) mustHelpTextIncludeMutualExclusivity}: <code>true</code></li>
     * </ul>
     */
    VERSION_0_9_0_DEFAULT_SETTINGS {
        @Override
        public void apply(ArgumentParserBuilder builder) {
            builder.mustHelpTextIncludeMutualExclusivity_ = true;
        }
    };

    abstract void apply(ArgumentParserBuilder builder);
}
