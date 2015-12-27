/*
 * Copyright (C) 2015 Tatsuhiro Tsujikawa
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
 * BooleanArgumentType provides strict conversion from input string to Boolean
 * value.
 * </p>
 * <p>
 * Passing {@link Boolean} class to {@link Argument#type(Class)} can achieve
 * boolean conversion as well, but its conversion relies on
 * {@link Boolean#valueOf(String)}. That is any strings which matches "true" in
 * case-insensitive fashion are converted to {@link Boolean#TRUE}. The other
 * strings are converted to {@link Boolean#FALSE}.
 * </p>
 * <p>
 * On the other hand, BooleanArgumentType is strict when conversion is made.
 * That is only the string which is defined as true value is converted to
 * {@link Boolean#TRUE}. Similarly, only the string which is defined as false
 * value is converted to {@link Boolean#FALSE}. The comparison is made in
 * case-sensitive manner.
 * </p>
 * <p>
 * When BooleanArgumentType is created by the default constructor,
 * {@link BooleanArgumentType#BooleanArgumentType()}, the true value is "true",
 * and the false value is "false". Probably, this is the most common use case.
 * The another constructor,
 * {@link BooleanArgumentType#BooleanArgumentType(String, String)} can take 2
 * strings, and application can specify what strings are used as true or false
 * value.
 * </p>
 * 
 * @since 0.7.0
 */
public class BooleanArgumentType implements ArgumentType<Boolean>,
        MetavarInference {

    /**
     * Creates BooleanArgumentType with "true" as true value, and "false" as
     * false value.
     */
    public BooleanArgumentType() {
        this("true", "false");
    }

    /**
     * Creates BooleanArgumentType with given values.
     * 
     * @param trueValue
     *            string used as true value
     * @param falseValue
     *            string used as false value
     */
    public BooleanArgumentType(String trueValue, String falseValue) {
        this.trueValue_ = trueValue;
        this.falseValue_ = falseValue;
    }

    @Override
    public Boolean convert(ArgumentParser parser, Argument arg, String value)
            throws ArgumentParserException {
        if (trueValue_.equals(value)) {
            return Boolean.TRUE;
        }

        if (falseValue_.equals(value)) {
            return Boolean.FALSE;
        }

        throw new ArgumentParserException(String.format(TextHelper.LOCALE_ROOT,
                "could not convert '%s' (choose from %s)", value,
                inferMetavar()[0]), parser, arg);
    }

    /**
     * <p>
     * Infers metavar based on given strings.
     * </p>
     * 
     * @see net.sourceforge.argparse4j.inf.MetavarInference#inferMetavar()
     */
    @Override
    public String[] inferMetavar() {
        return new String[] { TextHelper.concat(new String[] { trueValue_,
                falseValue_ }, 0, ",", "{", "}") };
    }

    private String trueValue_, falseValue_;
}
