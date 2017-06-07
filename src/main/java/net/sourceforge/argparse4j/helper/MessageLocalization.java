package net.sourceforge.argparse4j.helper;

import java.util.ResourceBundle;

import net.sourceforge.argparse4j.inf.ArgumentParser;

/**
 * <p>
 * For localization of messages the configuration of
 * {@link ConfiguredArgumentParser} is needed, but some methods must be backward
 * compatible and use {@link ArgumentParser} as its parameter type.
 * </p>
 * <p>
 * Using the function in this class the messages will be localized, if the
 * parser is a {@code ConfiguredArgumentParser}. Otherwise the unlocalized
 * message is used.
 * </p>
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
