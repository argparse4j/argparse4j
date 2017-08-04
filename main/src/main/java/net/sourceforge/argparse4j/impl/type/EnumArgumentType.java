/*
 * Copyright (C) 2011 Tatsuhiro Tsujikawa
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

import net.sourceforge.argparse4j.helper.MessageLocalization;
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
@Deprecated
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
            throw new ArgumentParserException(
                    String.format(TextHelper.LOCALE_ROOT,
                            MessageLocalization.localize(
                                    parser.getConfig().getResourceBundle(),
                                    "couldNotConvertChooseFromError"),
                            value, choices),
                    e, parser, arg);
        }
    }

}
