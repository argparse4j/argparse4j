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

import java.util.Map;

/**
 * This interface defines behavior of action when an argument is encountered at
 * the command line.
 * 
 */
public interface ArgumentAction {

    /**
     * <p>
     * Executes this action.
     * </p>
     * <p>
     * If the objects derived from {@link RuntimeException} are thrown in this
     * method because of invalid input from command line, subclass must catch
     * these exceptions and wrap them in {@link ArgumentParserException} and
     * give simple error message to explain what happened briefly.
     * </p>
     * 
     * @param parser
     *            The parser.
     * @param arg
     *            The argument this action attached to.
     * @param attrs
     *            Map to store attributes.
     * @param flag
     *            The actual option flag in command line if {@code arg} is an
     *            optional arguments. {@code null} if {@code arg} is a
     *            positional argument.
     * @param value
     *            The attribute value. This may be null if this action does not
     *            consume any arguments.
     * @throws ArgumentParserException
     *             If error occurred.
     */
    void run(ArgumentParser parser, Argument arg, Map<String, Object> attrs,
            String flag, Object value) throws ArgumentParserException;

    /**
     * Called when ArgumentAction is added to {@link Argument} using
     * {@link Argument#action(ArgumentAction)}.
     * 
     * @param arg
     *            {@link Argument} object to which this object is added.
     */
    void onAttach(Argument arg);

    /**
     * Returns {@code true} if this action consumes argument. Otherwise returns
     * {@code false}.
     * 
     * @return {@code true} or {@code false}.
     */
    boolean consumeArgument();
}
