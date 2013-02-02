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
 * <p>
 * This interface defines Subparser used to add sub-command to
 * {@link ArgumentParser}.
 * </p>
 */
public interface Subparser extends ArgumentParser {

    @Override
    Subparser description(String description);

    @Override
    Subparser epilog(String epilog);

    @Override
    Subparser version(String version);

    @Override
    Subparser defaultHelp(boolean defaultHelp);

    @Override
    Subparser setDefault(String dest, Object value);

    @Override
    Subparser setDefaults(Map<String, Object> attrs);

    /**
     * Sets the text to display in help message.
     * 
     * @param help
     *            The text to display in help message.
     * @return this
     */
    Subparser help(String help);

    /**
     * Sets alias names for this Subparser. The alias names must be unique for
     * each {@link Subparsers} instance which this object belongs to.
     * 
     * @param alias
     *            Alias name for this Subparser.
     * @return this
     */
    Subparser aliases(String... alias);
}
