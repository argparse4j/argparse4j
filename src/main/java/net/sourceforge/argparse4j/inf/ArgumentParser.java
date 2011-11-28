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

import java.util.List;
import java.util.Map;

import net.sourceforge.argparse4j.annotation.Arg;

/**
 * <p>
 * This interface defines behavior of ArgumentParser.
 * </p>
 * <p>
 * The typical usage is set description using {@link #description(String)} and
 * add arguments using {@link #addArgument(String...)}. To add sub-command,
 * first call {@link #addSubparsers()} to obtain {@link Subparsers} object.
 * {@link Subparsers} object provides necessary methods to add sub-commands. To
 * make a conceptual group of arguments, first call
 * {@link #addArgumentGroup(String)} to create {@link ArgumentGroup} object. And
 * add argument to that group using {@link ArgumentGroup#addArgument(String...)}
 * . To parse command-line arguments, call {@link #parseArgs(String[])} or
 * several overloaded methods.
 * </p>
 */
public interface ArgumentParser extends BaseArgumentParser {

    /**
     * <p>
     * Returns {@link Subparsers}.
     * </p>
     * <p>
     * The method name is rather controversial because repeated call of this
     * method does not add new {@link Subparsers} object. Instead, this method
     * always returns same {@link Subparsers} object. {@link Subparsers} object
     * provides a way to add sub-commands.
     * </p>
     * 
     * @return {@link Subparsers} object.
     */
    Subparsers addSubparsers();

    @Override
    ArgumentGroup addArgumentGroup(String title);

    @Override
    ArgumentParser description(String description);

    @Override
    ArgumentParser epilog(String epilog);

    @Override
    ArgumentParser version(String version);

    @Override
    ArgumentParser defaultHelp(boolean defaultHelp);

    @Override
    ArgumentParser setDefault(String dest, Object value);

    @Override
    ArgumentParser setDefaults(Map<String, Object> attrs);

    /**
     * <p>
     * Parses command line arguments.
     * </p>
     * <p>
     * The resulted attributes are returned as {@link Namespace} object. This
     * method must not alter the status of this parser and can be called
     * multiple times.
     * </p>
     * 
     * @param args
     *            Command line arguments.
     * @return {@link Namespace} object.
     * @throws ArgumentParserException
     *             If an error occurred.
     */
    Namespace parseArgs(String args[]) throws ArgumentParserException;

    /**
     * <p>
     * Parses command line arguments.
     * </p>
     * <p>
     * Unlike {@link #parseArgs(String[])}, which returns {@link Namespace}
     * object, this method stores attributes in given {@code attrs}.
     * </p>
     * 
     * @param args
     *            Command line arguments.
     * @param attrs
     *            Map object to store attributes.
     * @throws ArgumentParserException
     *             If an error occurred.
     */
    void parseArgs(String[] args, Map<String, Object> attrs)
            throws ArgumentParserException;

    /**
     * <p>
     * Parses command line arguments.
     * </p>
     * <p>
     * Unlike {@link #parseArgs(String[])}, which returns {@link Namespace}
     * object, this method stores attributes in given {@code userData}. The
     * location to store value is designated using {@link Arg} annotations. User
     * don't have to specify {@link Arg} for all attributes: the missing
     * attributes are just skipped. This method performs simple {@link List} to
     * generic array conversion. For example, user can assign
     * {@code List<Integer>} attribute to generic array {@code int[]}.
     * </p>
     * 
     * @param args
     *            Command line arguments.
     * @param userData
     *            Object to store attributes.
     * @throws ArgumentParserException
     *             If an error occurred.
     */
    void parseArgs(String[] args, Object userData)
            throws ArgumentParserException;

    /**
     * <p>
     * Parses command line arguments.
     * </p>
     * <p>
     * This is a combination of {@link #parseArgs(String[], Map)} and
     * {@link #parseArgs(String[], Object)}. The all attributes will be stored
     * in {@code attrs}. The attributes specified in {@link Arg} annotations
     * will be also stored in {@code userData}.
     * </p>
     * 
     * @param args
     *            Command line arguments.
     * @param attrs
     *            Map to store attributes.
     * @param userData
     *            Object to store attributes.
     * @throws ArgumentParserException
     *             If an error occurred.
     */
    void parseArgs(String[] args, Map<String, Object> attrs, Object userData)
            throws ArgumentParserException;

    /**
     * <p>
     * Prints usage and error message.
     * </p>
     * <p>
     * Please note that this method does not terminate the program.
     * </p>
     * 
     * @param e
     *            Error thrown by {@link #parseArgs(String[])}.
     */
    void handleError(ArgumentParserException e);
}
