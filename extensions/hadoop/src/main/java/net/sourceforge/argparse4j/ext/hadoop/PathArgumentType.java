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
package net.sourceforge.argparse4j.ext.hadoop;

import net.sourceforge.argparse4j.helper.MessageLocalization;
import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

import org.apache.hadoop.fs.Path;

/**
 * <p>
 * ArgumentType subclass for Hadoop HDFS Path type, using fluent style API.
 * </p>
 *
 * <p>
 * This object can convert path string to {@link Path} object. The
 * command-line programs traditionally accept the file path "-" as standard
 * input. "-" ia always a valid Hadoop Path.
 * </p>
 *
 * @since 0.8.0
 */
public class PathArgumentType implements ArgumentType<Path> {

    /**
     * Create an instance.
     */
    public PathArgumentType() {
    }

    @Override
    public Path convert(ArgumentParser parser, Argument arg,
            String value) throws ArgumentParserException {
        Path path;
        try {
            path = new Path(value);
        } catch (IllegalArgumentException e) {
            String localizedTypeName = HadoopExtensionResourceBundle
                    .get(parser.getConfig().getLocale()).getString("path");
            throw new ArgumentParserException(
                    String.format(TextHelper.LOCALE_ROOT, MessageLocalization
                                    .localize(parser.getConfig().getResourceBundle(),
                                            "couldNotConvertToError"), value,
                            localizedTypeName), e.getCause(), parser, arg);

        }
        return path;
    }
}
