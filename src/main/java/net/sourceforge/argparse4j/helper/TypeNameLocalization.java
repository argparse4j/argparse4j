package net.sourceforge.argparse4j.helper;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.sourceforge.argparse4j.inf.ArgumentParser;

/**
 * <p>
 * Localization of names for argument types. Localized names for commonly used
 * types in the JDK (e.g. {@code Integer} and {@code Boolean}) are provided, but
 * for custom types, the developer of the type must provide the localization.
 * This class tries to load the display name by using the following sources. The
 * first display name that is found is used:
 * </p>
 * <ol>
 * <li>Key <code>displayName</code> of resource bundle
 * <code>&lt;fully-qualified custom type name&gt;-argparse4j</code> (with the
 * dots replaced by slashes).</li>
 * <li>Key <code>type.&lt;simple class name of the type&gt;</code> of resource
 * bundle <code>net/sourceforge/argparse4j/internal/ArgumentParserImpl</code>.
 * </li>
 * <li>The simple class name of the type.</li>
 * </ol>
 * <p>
 * <strong>The application code should not use this class directly.</strong>
 * </p>
 * 
 * @since 0.8.0
 */
public class TypeNameLocalization {
    private TypeNameLocalization() {
    }

    public static String localizeTypeNameIfPossible(ArgumentParser parser,
            Class<?> type) {
        return localizeTypeName(parser, type);
    }

    private static String localizeTypeName(ArgumentParser parser,
            Class<?> type) {
        try {
            ResourceBundle typeBundle = ResourceBundle.getBundle(
                    type.getName().replace('.', '/') + "-argparse4j");
            return typeBundle.getString("displayName");
        } catch (MissingResourceException e) {
            String simpleTypeName = type.getSimpleName();
            return MessageLocalization.localize(
                    parser.getConfig().getResourceBundle(),
                    "type." + simpleTypeName, simpleTypeName);
        }
    }
}
