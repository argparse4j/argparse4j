package net.sourceforge.argparse4j.inf;

import java.util.Locale;
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

    /**
     * Returns Locale for this configuration.
     * 
     * @return Locale
     */
    Locale getLocale();
}
