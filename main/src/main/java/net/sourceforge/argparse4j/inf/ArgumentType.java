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
package net.sourceforge.argparse4j.inf;

/**
 * This interface defines type conversion method.
 * 
 * @param <T>
 *            Type this object convert to.
 */
public interface ArgumentType<T> {

    /**
     * <p>
     * Converts {@code value} to appropriate type.
     * </p>
     * <p>
     * If the objects derived from {@link RuntimeException} are thrown in
     * conversion because of invalid input from command line, subclass must
     * catch these exceptions and wrap them in {@link ArgumentParserException}
     * and give simple error message to explain what happened briefly.
     * </p>
     * 
     * @param parser
     *            The parser.
     * @param arg
     *            The argument this type attached to.
     * @param value
     *            The attribute value.
     * @return Converted object.
     * @throws ArgumentParserException
     *             If conversion fails.
     */
    T convert(ArgumentParser parser, Argument arg, String value)
            throws ArgumentParserException;
}
