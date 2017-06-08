package net.sourceforge.argparse4j.inf;

import java.util.ResourceBundle;

/**
 * ArgumentParserConfiguration is a configuration interface of ArgumentParser.
 * 
 * @since 0.8.0
 */
public interface ArgumentParserConfiguration {

    /**
     * Returns ResourceBundle for this configuration.
     * 
     * @return ResourceBundle
     */
    ResourceBundle getResourceBundle();
}
