package net.sourceforge.argparse4j.inf;

import net.sourceforge.argparse4j.ArgumentParserConfiguration;

public interface ConfiguredArgumentParser extends ArgumentParser {
    /**
     * <p>
     * Get the configuration of this argument parser.
     * </p>
     *
     * @return The argument parser configuration.
     */
    ArgumentParserConfiguration getConfig();
}
