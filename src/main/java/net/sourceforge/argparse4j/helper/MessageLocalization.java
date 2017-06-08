package net.sourceforge.argparse4j.helper;

import java.util.ResourceBundle;

/**
 * <p>
 * MessageLocalization is a helper class to provide methods for localization.
 * <p>
 * <p>
 * <strong>The application code should not use this class directly.</strong>
 * </p>
 * 
 * @since 0.8.0
 */
public class MessageLocalization {
    private MessageLocalization() {
    }

    public static String localize(ResourceBundle resourceBundle, String key) {
        return resourceBundle.getString(key);
    }

    public static String localize(ResourceBundle resourceBundle, String key,
            String unlocalizedMessage) {
        return resourceBundle.containsKey(key) ? resourceBundle.getString(key)
                : unlocalizedMessage;
    }
}
