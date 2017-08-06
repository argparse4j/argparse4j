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

import java.util.Locale;

/**
 * <p>
 * ArgumentType subclass for enum type using case-insensitive matching of
 * values.
 * </p>
 * <p>
 * Uses {@link Enum#toString()} instead of {@link Enum#name()} as the String
 * representation of the enum. For enums that do not override
 * {@link Enum#toString()}, this behaves the same as
 * {@link CaseInsensitiveEnumNameArgumentType}.
 *
 * @param <T>
 *            Type of enum
 * @since 0.8.0
 */
public class CaseInsensitiveEnumStringArgumentType<T extends Enum<T>>
        extends CaseInsensitiveEnumArgumentType<T> {

    public CaseInsensitiveEnumStringArgumentType(Class<T> type) {
        super(type, Locale.ROOT);
    }

    /**
     * <p>
     * Creates a {@code CaseInsensitiveEnumStringArgumentType} for the given
     * enum type.
     *
     * @param type
     *         type of the enum the {@code CaseInsensitiveEnumStringArgumentType}
     *         should convert to
     * @return a {@code CaseInsensitiveEnumStringArgumentType} that converts
     * Strings to {@code type}
     */
    public static <T extends Enum<T>> CaseInsensitiveEnumStringArgumentType<T> 
            forEnum(Class<T> type) {
        return new CaseInsensitiveEnumStringArgumentType<T>(type);
    }

    @Override
    protected String toStringRepresentation(T t) {
        return t.toString();
    }

    @Override
    protected Object[] getStringRepresentations() {
        return type_.getEnumConstants();
    }
}
