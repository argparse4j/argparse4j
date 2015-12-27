/*
 * Copyright (C) 2015 Andrew January
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

import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;
import net.sourceforge.argparse4j.inf.MetavarInference;

/**
 * <p>
 * ArgumentType subclass for enum type.
 * </p>
 * <p>
 * Uses {@link Enum#toString()} instead of {@link Enum#name()} as the String
 * representation of the enum. For enums that do not override {@link Enum#toString()},
 * this behaves the same as {@link ReflectArgumentType}.
 *
 * @param <T>
 *            Type of enum
 */
public class EnumStringArgumentType<T extends Enum<T>> implements
        ArgumentType<T>, MetavarInference {

    private Class<T> type_;

    public EnumStringArgumentType(Class<T> type) {
        type_ = type;
    }

    /**
     * <p>
     * Creates an {@code EnumStringArgumentType} for the given enum type.
     * @param type
     *            type of the enum the {@code EnumStringArgumentType} should convert to
     * @return an {@code EnumStringArgumentType} that converts Strings to {@code type} 
     */
    public static <T extends Enum<T>> EnumStringArgumentType<T> forEnum(Class<T> type) {
        return new EnumStringArgumentType<T>(type);
    }

    @Override
    public T convert(ArgumentParser parser, Argument arg, String value)
            throws ArgumentParserException {
        for (T t : type_.getEnumConstants()) {
            if (t.toString().equals(value)) {
                return t;
            }
        }

        String choices = TextHelper.concat(type_.getEnumConstants(), 0,
                ",", "{", "}");
        throw new ArgumentParserException(String.format(
                TextHelper.LOCALE_ROOT,
                "could not convert '%s' (choose from %s)", value, choices),
                parser, arg);
    }

    /**
     * <p>
     * Infers metavar based on given type.
     * </p>
     * <p>
     * The inferred metavar contains all enum constant string representation,
     * obtained by calling their {@link Object#toString()} method.
     * </p>
     * 
     * @see net.sourceforge.argparse4j.inf.MetavarInference#inferMetavar()
     * @since 0.7.0
     */
    @Override
    public String[] inferMetavar() {
        return new String[] { TextHelper.concat(type_.getEnumConstants(),
                0, ",", "{", "}") };
    }
}
