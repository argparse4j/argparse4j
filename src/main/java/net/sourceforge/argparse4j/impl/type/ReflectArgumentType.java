package net.sourceforge.argparse4j.impl.type;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

public class ReflectArgumentType<T> implements ArgumentType<T> {

    private Class<T> type_;

    public ReflectArgumentType(Class<T> type) {
        type_ = type;
    }

    /**
     * <p>
     * Creates {@link ReflectArgumentType} object with given {@code type}.
     * </p>
     * <p>
     * This object first tries to convert given String using
     * {@code valueOf(java.langString)} static method of given {@code type}. If
     * that failed, then use constructor of given {@code type} for conversion.
     * The constructor of {@code type} must have 1 String argument.
     * </p>
     * <p>
     * Because the enums have {@code valueOf} static method, this object works
     * with them as well. The enums in its nature have limited number of
     * members. In {@link #convert(ArgumentParser, Argument, String)}, string
     * value will be converted to one of them. If it cannot be converted,
     * {@link #convert(ArgumentParser, Argument, String)} will throw
     * {@link ArgumentParserException}. This means it already act like a
     * {@link Argument#choices(Object...)}.
     * </p>
     * 
     * @param type
     *            The type String value should be converted to.
     */
    @Override
    public T convert(ArgumentParser parser, Argument arg, String value)
            throws ArgumentParserException {
        Method m = null;
        try {
            m = type_.getMethod("valueOf", String.class);
        } catch (NoSuchMethodException e) {
            // If no valueOf static method found, try constructor.
            return convertUsingConstructor(parser, arg, value);
        } catch (SecurityException e) {
            handleInstatiationError(e);
        }
        // Only interested in static valueOf method.
        if (!Modifier.isStatic(m.getModifiers())
                || !type_.isAssignableFrom(m.getReturnType())) {
            return convertUsingConstructor(parser, arg, value);
        }
        try {
            m.setAccessible(true);
        } catch (SecurityException e) {
            return convertUsingConstructor(parser, arg, value);
        }
        Object obj = null;
        try {
            obj = m.invoke(null, value);
        } catch (IllegalAccessException e) {
            return convertUsingConstructor(parser, arg, value);
        } catch (IllegalArgumentException e) {
            handleInstatiationError(e);
        } catch (InvocationTargetException e) {
            throwArgumentParserException(parser, arg, value,
                    e.getCause() == null ? e : e.getCause());
        }
        return (T) obj;
    }

    private T convertUsingConstructor(ArgumentParser parser, Argument arg,
            String value) throws ArgumentParserException {
        T obj = null;
        try {
            obj = type_.getConstructor(String.class).newInstance(value);
        } catch (InstantiationException e) {
            handleInstatiationError(e);
        } catch (IllegalAccessException e) {
            handleInstatiationError(e);
        } catch (InvocationTargetException e) {
            throwArgumentParserException(parser, arg, value,
                    e.getCause() == null ? e : e.getCause());
        } catch (NoSuchMethodException e) {
            handleInstatiationError(e);
        }
        return obj;
    }

    private void throwArgumentParserException(ArgumentParser parser,
            Argument arg, String value, Throwable t)
            throws ArgumentParserException {
        if (type_.isEnum()) {
            String choices = TextHelper.concat(type_.getEnumConstants(), 0,
                    ",", "{", "}");
            throw new ArgumentParserException(String.format(
                    "could not convert '%s' (choose from %s)", value, choices),
                    t, parser, arg);

        } else {
            throw new ArgumentParserException(String.format(
                    "could not convert '%s' to %s (%s)", value,
                    type_.getSimpleName(), t.getMessage()), t, parser, arg);
        }
    }

    private void handleInstatiationError(Exception e) {
        throw new IllegalArgumentException("reflect type conversion error", e);
    }
}
