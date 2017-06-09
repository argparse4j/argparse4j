package net.sourceforge.argparse4j.impl.type;

import java.util.Locale;

import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;
import net.sourceforge.argparse4j.inf.MetavarInference;

public abstract class CaseInsensitiveEnumArgumentType<T extends Enum<T>>
        implements ArgumentType<T>, MetavarInference {
    protected Class<T> type_;
    
    protected CaseInsensitiveEnumArgumentType(Class<T> type) {
        this.type_ = type;
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

    protected abstract String toStringRepresentation(T t);

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
     */
    protected abstract Object[] getStringRepresentations();

    /**
     * Get the String representation of the given value.
     * 
     * @param value
     *            The value for which to get the String representation.
     * @return The String representation of <code>value</code>.
     */
    private String toCaseInsensitiveForm(String value) {
        return value.toLowerCase(Locale.ROOT);
    }
}
