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
 * This interface defines a method to conceptually group up {@link Argument}
 * objects.
 * 
 */
public interface ArgumentGroup {

    /**
     * Sets description to customize help message of this group.
     * 
     * @param description
     *            The description of this group.
     * @return this
     */
    ArgumentGroup description(String description);

    /**
     * <p>
     * Creates new {@link Argument} and adds it to the underlining parser and
     * returns it.
     * </p>
     * <p>
     * See {@link ArgumentParser#addArgument(String...)} for details.
     * </p>
     * 
     * @param nameOrFlags
     *            A name or a list of option strings of new {@link Argument}.
     * @return {@link Argument} object.
     */
    Argument addArgument(String... nameOrFlags);
}
