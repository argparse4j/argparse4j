package net.sourceforge.argparse4j.impl.type;

import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

/**
 * <p>
 * ArgumentType subclass for enum type.
 * </p>
 * <p>
 * Since enum does not have a constructor with string argument, it cannot be
 * used with {@link Argument#type(Class)}. Instead use this class to specify
 * enum type. The enums in its nature have limited number of members. In
 * {@link #convert(ArgumentParser, Argument, String)}, String value will be
 * converted to one of them. If it cannot be converted,
 * {@link #convert(ArgumentParser, Argument, String)} will throw
 * {@link ArgumentParserException}. This means it already act like a
 * {@link Argument#choices(Object...)}.
 * </p>
 * 
 * @deprecated Use {@link ReflectArgumentType} instead.
 * @param <T>
 *            Type of enum
 */
public class EnumArgumentType<T extends Enum<T>> implements ArgumentType<T> {

    private Class<T> type_;

    public EnumArgumentType(Class<T> type) {
        type_ = type;
    }

    @Override
    public T convert(ArgumentParser parser, Argument arg, String value)
            throws ArgumentParserException {
        try {
            return Enum.valueOf(type_, value);
        } catch (IllegalArgumentException e) {
            String choices = TextHelper.concat(type_.getEnumConstants(), 0,
                    ",", "{", "}");
            throw new ArgumentParserException(String.format(
                    TextHelper.LOCALE_ROOT,
                    "could not convert '%s' (choose from %s)", value, choices),
                    e, parser, arg);
        }
    }

}
