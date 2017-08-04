/*
 * Copyright (C) 2013 Tatsuhiro Tsujikawa
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.sourceforge.argparse4j.impl.type;

import static net.sourceforge.argparse4j.helper.TypeNameLocalization.localizeTypeNameIfPossible;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.sourceforge.argparse4j.helper.MessageLocalization;
import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;
import net.sourceforge.argparse4j.inf.MetavarInference;

/**
 * <p>
 * This implementation converts String value into given type using type's
 * {@code valueOf(java.lang.String)} static method or its constructor.
 * 
 * This class implements {@link MetavarInference} interface, and performs
 * special handling when {@link Boolean} class is passed to the constructor. In
 * that case, {@link ReflectArgumentType#inferMetavar()} returns convenient
 * metavar string for Boolean values, and it is used when
 * {@link Argument#metavar(String...)} is not used.
 * </p>
 */
public class ReflectArgumentType<T> implements ArgumentType<T>,
        MetavarInference {

    private Class<T> type_;

    /**
     * <p>
     * Creates {@link ReflectArgumentType} object with given {@code type}.
     * </p>
     * <p>
     * This object first tries to convert given String using
     * {@code valueOf(java.lang.String)} static method of given {@code type}. If
     * that failed, then use constructor of given {@code type} for conversion.
     * {@code valueOf()} method and/or constructor must be declared as public.
     * Otherwise, they cannot be invoked. The constructor of {@code type} must
     * accept 1 String argument.
     * </p>
     * <p>
     * If error occurred inside the {@code valueOf} static method or
     * constructor, {@link ArgumentParserException} will be thrown. If error
     * occurred in other locations, subclass of {@link RuntimeException} will be
     * thrown.
     * </p>
     * <p>
     * This object works with enums as well. The enums in its nature have
     * limited number of members. In
     * {@link #convert(ArgumentParser, Argument, String)}, string value will be
     * converted to one of them. If it cannot be converted,
     * {@link #convert(ArgumentParser, Argument, String)} will throw
     * {@link ArgumentParserException}. This means it already act like a
     * {@link Argument#choices(Object...)}. Please note that this conversion
     * does not take into account {@link Enum#toString()} override. If
     * application passes enums with toString() overridden with the different
     * value than enum name, it may not work like it expects. To take into
     * account {@link Enum#toString()} on conversion, use
     * {@link Arguments#enumStringType(Class)} instead.
     * </p>
     * 
     * @param type
     *            The type String value should be converted to.
     */
    public ReflectArgumentType(Class<T> type) {
        type_ = type;
    }

    @Override
    public T convert(ArgumentParser parser, Argument arg, String value)
            throws ArgumentParserException {
        // Handle enums separately. Enum.valueOf() is very convenient here.
        // It somehow can access private enum values, where normally T.valueOf()
        // cannot without setAccessible(true).
        if (type_.isEnum()) {
            try {
                //noinspection unchecked
                return (T) Enum.valueOf((Class<Enum>) type_, value);
            } catch (IllegalArgumentException e) {
                throw new ArgumentParserException(
                        String.format(TextHelper.LOCALE_ROOT,
                                MessageLocalization.localize(
                                        parser.getConfig().getResourceBundle(),
                                        "couldNotConvertChooseFromError"),
                                value, inferMetavar()[0]),
                        parser, arg);
            }
        }
        Method m = null;
        try {
            m = type_.getMethod("valueOf", String.class);
        } catch (NoSuchMethodException e) {
            // If no valueOf static method found, try constructor.
            return convertUsingConstructor(parser, arg, value);
        } catch (SecurityException e) {
            handleInstantiationError(e);
        }
        // Only interested in static valueOf method.
        if (!Modifier.isStatic(m.getModifiers())
                || !type_.isAssignableFrom(m.getReturnType())) {
            return convertUsingConstructor(parser, arg, value);
        }
        Object obj = null;
        try {
            obj = m.invoke(null, value);
        } catch (IllegalAccessException e) {
            return convertUsingConstructor(parser, arg, value);
        } catch (IllegalArgumentException e) {
            handleInstantiationError(e);
        } catch (InvocationTargetException e) {
            throwArgumentParserException(parser, arg, value,
                    e.getCause() == null ? e : e.getCause());
        }
        //noinspection unchecked
        return (T) obj;
    }

    private T convertUsingConstructor(ArgumentParser parser, Argument arg,
            String value) throws ArgumentParserException {
        T obj = null;
        try {
            obj = type_.getConstructor(String.class).newInstance(value);
        } catch (InstantiationException e) {
            handleInstantiationError(e);
        } catch (IllegalAccessException e) {
            handleInstantiationError(e);
        } catch (InvocationTargetException e) {
            throwArgumentParserException(parser, arg, value,
                    e.getCause() == null ? e : e.getCause());
        } catch (NoSuchMethodException e) {
            handleInstantiationError(e);
        }
        return obj;
    }

    private void throwArgumentParserException(ArgumentParser parser,
            Argument arg, String value, Throwable t)
            throws ArgumentParserException {
        String localizedTypeName = localizeTypeNameIfPossible(parser, type_);
        throw new ArgumentParserException(String.format(TextHelper.LOCALE_ROOT,
                MessageLocalization.localize(
                        parser.getConfig().getResourceBundle(),
                        "couldNotConvertToError"),
                value, localizedTypeName), t, parser, arg);
    }

    private void handleInstantiationError(Exception e) {
        throw new IllegalArgumentException("reflect type conversion error", e);
    }

    /**
     * <p>
     * Infers metavar based on given type.
     * </p>
     * <p>
     * If {@link Boolean} class is passed to constructor, this method returns
     * metavar string "{true,false}" for convenience.
     * </p>
     * <p>
     * If enum type is passed to constructor, this method returns metavar
     * containing all enum names defined in that type. This uses
     * {@link Enum#name()} method, instead of {@link Object#toString()} method.
     * If you are looking for the latter, consider to use
     * {@link EnumStringArgumentType}.
     * </p>
     * <p>
     * Otherwise, returns null.
     * </p>
     * 
     * @see net.sourceforge.argparse4j.inf.MetavarInference#inferMetavar()
     * @since 0.7.0
     */
    @Override
    public String[] inferMetavar() {
        if (Boolean.class.equals(type_)) {
            return new String[] { TextHelper.concat(new String[] { "true",
                    "false" }, 0, ",", "{", "}") };
        }

        if (type_.isEnum()) {
            T[] enumConstants = type_.getEnumConstants();
            String[] names = new String[enumConstants.length];
            int i = 0;
            for (T t : enumConstants) {
                names[i++] = ((Enum<?>) t).name();
            }
            return new String[] { TextHelper.concat(names, 0, ",", "{", "}") };
        }

        return null;
    }
}
