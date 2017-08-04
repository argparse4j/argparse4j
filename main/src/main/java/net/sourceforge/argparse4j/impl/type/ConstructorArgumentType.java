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

import static net.sourceforge.argparse4j.helper.TypeNameLocalization.localizeTypeNameIfPossible;

import java.lang.reflect.InvocationTargetException;

import net.sourceforge.argparse4j.helper.MessageLocalization;
import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

/**
 * <p>
 * This implementation converts String value into given type using given type's
 * constructor.
 * </p>
 * <p>
 * The constructor must have 1 String argument. If error occurred inside the
 * constructor, {@link ArgumentParserException} will be thrown. If error
 * occurred in other locations, subclass of {@link RuntimeException} will be
 * thrown.
 * </p>
 * 
 * @deprecated Use {@link ReflectArgumentType} instead.
 */
@Deprecated
public class ConstructorArgumentType<T> implements ArgumentType<T> {

    private Class<T> type_;

    /**
     * <p>
     * Creates {@link ConstructorArgumentType} object with given {@code type}.
     * </p>
     * <p>
     * The constructor of {@code type} must have 1 String argument.
     * </p>
     * 
     * @param type
     *            The type String value should be converted to.
     */
    public ConstructorArgumentType(Class<T> type) {
        type_ = type;
    }

    @Override
    public T convert(ArgumentParser parser, Argument arg, String value)
            throws ArgumentParserException {
        T obj = null;
        try {
            obj = type_.getConstructor(String.class).newInstance(value);
        } catch (InstantiationException e) {
            handleInstantiationError(e);
        } catch (IllegalAccessException e) {
            handleInstantiationError(e);
        } catch (InvocationTargetException e) {
            String localizedTypeName = localizeTypeNameIfPossible(parser,
                    type_);
            throw new ArgumentParserException(
                    String.format(TextHelper.LOCALE_ROOT,
                            MessageLocalization.localize(
                                    parser.getConfig().getResourceBundle(),
                                    "couldNotConvertToError"),
                            value, localizedTypeName),
                    e.getCause(), parser, arg);
        } catch (NoSuchMethodException e) {
            handleInstantiationError(e);
        }
        return obj;
    }

    private void handleInstantiationError(Exception e) {
        throw new IllegalArgumentException("Failed to instantiate object", e);
    }

}
