package net.sourceforge.argparse4j.impl.type;

import java.util.Locale;

import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;
import net.sourceforge.argparse4j.inf.MetavarInference;

public class CaseInsensitiveEnumArgumentType<T extends Enum<T>>
        implements ArgumentType<T>, MetavarInference {
    protected Class<T> type_;
    private Locale lowerCasingLocale_;

    /**
     * <p>
     * Do not use. This constructor creates a case insensitive enum name
     * argument type, but converts the enum names and the values passed on the
     * command line to lower case in a way that depends on the current user
     * locale. This may result in values not matching an enum name if the
     * program is run by a user with a different locale.
     * </p>
     *
     * @param type
     *         the enum type.
     * @deprecated Use one of the subclasses, which always convert case
     * correctly.
     */
    @Deprecated
    public CaseInsensitiveEnumArgumentType(Class<T> type) {
        this(type, Locale.getDefault());
    }

    /**
     * <p>
     * Create an instance. 
     * </p>
     *
     * @param type
     *         the enum type.
     * @param lowerCasingLocale
     *         the locale to use for converting to lower case. Sub classes
     *         should always pass {@link Locale#ROOT}.
     * @since 0.8.0
     */
    protected CaseInsensitiveEnumArgumentType(Class<T> type,
            Locale lowerCasingLocale) {
        type_ = type;
        lowerCasingLocale_ = lowerCasingLocale;
    }

    @Override
    public T convert(ArgumentParser parser, Argument arg, String value)
            throws ArgumentParserException {
        String valueForComparison = toCaseInsensitiveForm(value);
        for (T t : type_.getEnumConstants()) {
            // Not using "equalsIgnoreCase(String)" as this will cause tests
            // "testIgnoresLocaleOfParserForCaseInsensitivity" of the subclasses
            // to fail.
            if (toCaseInsensitiveForm(toStringRepresentation(t))
                    .equals(valueForComparison)) {
                return t;
            }
        }

        String choices = TextHelper.concat(getStringRepresentations(), 0,
                ",", "{", "}");
        throw new ArgumentParserException(String.format(
                TextHelper.LOCALE_ROOT,
                "could not convert '%s' (choose from %s)", value, choices),
                parser, arg);
    }

    /**
     * <p>
     * Convert the given enum value to its string representation.
     * </p>
     *
     * @param t
     *         the enum value to convert.
     * @return the string representation of {@code t}.
     * @since 0.8.0
     */
    protected String toStringRepresentation(T t) {
        return t.name();        
    }

    /**
     * <p>
     * Infers metavar based on given type.
     * </p>
     * <p>
     * The inferred metavar contains all enum constant string representation.
     * </p>
     *
     * @see MetavarInference#inferMetavar()
     * @since 0.7.0
     */
    @Override
    public String[] inferMetavar() {
        return new String[] { TextHelper.concat(getStringRepresentations(),
                0, ",", "{", "}") };
    }

    /**
     * <p>
     * Get the objects to be used to generate the String representations of all
     * enum constants. {@link Object#toString()} will be invoked on these
     * objects to obtain the actual String representation.
     * </p>
     * @return The objects used to generate String representations.
     * @since 0.8.0
     */
    protected Object[] getStringRepresentations() {
        return type_.getEnumConstants();
    }

    /**
     * Get the String representation of the given value.
     * 
     * @param value
     *            The value for which to get the String representation.
     * @return The String representation of <code>value</code>.
     */
    private String toCaseInsensitiveForm(String value) {
        return value.toLowerCase(lowerCasingLocale_);
    }
}
