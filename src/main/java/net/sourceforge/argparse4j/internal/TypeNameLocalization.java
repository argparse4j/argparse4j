package net.sourceforge.argparse4j.internal;

import static net.sourceforge.argparse4j.internal.MessageLocalization.optionallyLocalize;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ConfiguredArgumentParser;

/**
 * <p>
 * Localization of names for argument types. Localized names for commonly used
 * types in the JDK (e.g. {@code Integer} and {@code Boolean}) are provided, but
 * for custom types, the developer of the type must provide the localization.
 * This class tries to load the display name by using the following sources. The
 * first display name that is found is used:
 * </p>
 * <ol>
 *     <li>Key <code>displayName</code> of resource bundle
 *     <code>&lt;fully-qualified custom type name&gt;-argparse4j</code> (with
 *     the dots replaced by slashes).</li>
 *     <li>Key <code>type.&lt;simple class name of the type&gt;</code> of
 *     resource bundle
 *     <code>net/sourceforge/argparse4j/internal/ArgumentParserImpl</code>.</li>
 *     <li>The simple class name of the type.</li>
 * </ol>
 * <p>
 * If the parser does not provide a locale, i.e. it is not a
 * {@link net.sourceforge.argparse4j.inf.ConfiguredArgumentParser}, then the
 * simple class name of the type is returned.
 * </p>
*/
public class TypeNameLocalization {
    private TypeNameLocalization() {
    }
    
    public static String localizeTypeNameIfPossible(ArgumentParser parser,
            Class<?> type) {
        if (parser instanceof ConfiguredArgumentParser) {
            return localizeTypeName((ConfiguredArgumentParser)parser, type);
        }
        return type.getSimpleName();
    }

    private static String localizeTypeName(ConfiguredArgumentParser parser,
            Class<?> type) {
        try {
            ResourceBundle typeBundle = ResourceBundle.getBundle(
                    type.getName().replace('.', '/') + "-argparse4j");
            return typeBundle.getString("displayName");
        } catch (MissingResourceException e) {
            String simpleTypeName = type.getSimpleName();
            return optionallyLocalize(parser, "type." + simpleTypeName,
                    simpleTypeName);
        }
    }
}
