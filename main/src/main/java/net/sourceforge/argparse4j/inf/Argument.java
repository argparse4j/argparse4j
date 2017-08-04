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

import java.util.Collection;
import java.util.List;

/**
 * This interface provides a way to specify parameters to argument to be
 * processed in {@link ArgumentParser} object.
 */
public interface Argument {

    /**
     * <p>
     * Sets the number of command line arguments that should be consumed.
     * </p>
     * <p>
     * Don't give this method {@code '*'}, {@code '+'} or {@code '?'}. They are
     * converted to {@code int} value and it is not what you expect. For these
     * strings, use {@link #nargs(String)}.
     * </p>
     * 
     * @param n
     *            A positive integer
     * @return this
     */
    Argument nargs(int n);

    /**
     * <p>
     * Sets the number of command line arguments that should be consumed.
     * <p>
     * <p>
     * This method takes one of following string: {@code "*"}, {@code "+"} and
     * {@code "?"}. If {@code "*"} is given, All command line arguments present
     * are gathered into a {@link java.util.List}. If {@code "+"} is given, just
     * like {@code "*"}, all command line arguments present are gathered into a
     * {@link java.util.List}. Additionally, an error message will be generated
     * if there wasn't at least one command line argument present. If
     * {@code "?"} is given, one argument will be consumed from the command line
     * if possible, and produced as a single item. If no command line argument
     * is present, the value from {@link #setDefault(Object)} will be produced.
     * Note that for named arguments, there is an additional case - the
     * option string is present but not followed by a command line argument. In
     * this case the value from {@link #setConst(Object)} will be produced.
     * </p>
     * 
     * @param n
     *            {@code "*"}, {@code "+"} or {@code "?"}
     * @return this
     */
    Argument nargs(String n);

    /**
     * <p>
     * Sets constant values that are not read from the command line but are
     * required for the various actions.
     * </p>
     * 
     * <p>
     * The const value defaults to {@code null}.
     * </p>
     * 
     * @param value
     *            The const value
     * @return this
     */
    Argument setConst(Object value);

    /**
     * <p>
     * Sets list of constant values that are not read from the command line but
     * are required for the various actions.
     * </p>
     * <p>
     * The given {@code values} will be converted to {@link List}. The const
     * value defaults to {@code null}. If you want to set non-List item, use
     * {@link #setConst(Object)}.
     * </p>
     * 
     * @param values
     *            The const values
     * @return this
     */
    <E> Argument setConst(E... values);

    /**
     * <p>
     * Sets value which should be used if the command line argument is not
     * present.
     * </p>
     * <p>
     * The default value defaults to {@code null}.
     * </p>
     * 
     * @param value
     *            The default value
     * @return this
     */
    Argument setDefault(Object value);

    /**
     * <p>
     * Sets list of values which should be used if the command line argument is
     * not present.
     * </p>
     * <p>
     * The default value defaults to {@code null}. The given {@code values} will
     * be converted to {@link List}. If you want to set non-List item, use
     * {@link Argument#setDefault(Object)}.
     * </p>
     * 
     * @param values
     *            The default values
     * @return this
     */
    <E> Argument setDefault(E... values);

    /**
     * <p>
     * Sets special value to control default value handling.
     * </p>
     * <p>
     * Currently, only {@link FeatureControl#SUPPRESS} is available. If it is
     * given, default value is not add as a attribute.
     * </p>
     * 
     * @param ctrl
     *            The special value to control default value handling.
     * @return this
     */
    Argument setDefault(FeatureControl ctrl);

    /**
     * <p>
     * Sets the type which the command line argument should be converted to.
     * </p>
     * <p>
     * By default, type is String, which means no conversion is made. The type
     * must have a constructor which takes one String argument.
     * </p>
     * <p>
     * As a convenience, if one of following primitive types (boolean.class,
     * byte.class, short.class, int.class, long.class, float.class and
     * double.class) is specified, it is converted to its wrapped type
     * counterpart. For example, if int.class is given, it is silently converted
     * to Integer.class.
     * </p>
     * 
     * @param type
     *            The type which the command line argument should be converted
     *            to.
     * @return this
     */
    <T> Argument type(Class<T> type);

    /**
     * <p>
     * Sets {@link ArgumentType} object which converts command line argument to
     * appropriate type.
     * </p>
     * <p>
     * This would be useful if you need to convert the command line argument
     * into a type which does not have a constructor with one String argument.
     * </p>
     * 
     * @param type
     *            The {@link ArgumentType} object
     * @return this
     */
    <T> Argument type(ArgumentType<T> type);

    /**
     * <p>
     * If {@code true} is given, this named argument must be specified in
     * command line otherwise error will be issued.
     * </p>
     * <p>
     * The default value is {@code false}. This object is a positional argument,
     * this property is ignored.
     * </p>
     * 
     * @param required
     *            {@code true} or {@code false}
     * @return this
     */
    Argument required(boolean required);

    /**
     * Sets the action to be taken when this argument is encountered at the
     * command line.
     * 
     * @param action
     *            {@link ArgumentAction} object
     * @return this
     */
    Argument action(ArgumentAction action);

    /**
     * <p>
     * Sets {@link ArgumentChoice} object which inspects value so that it
     * fulfills its criteria.
     * </p>
     * <p>
     * This method is useful if more complex inspection is necessary than basic
     * {@link #choices(Object...)}.
     * </p>
     * 
     * @param choice
     *            {@link ArgumentChoice} object.
     * @return this
     */
    Argument choices(ArgumentChoice choice);

    /**
     * Sets a collection of the allowable values for the argument.
     * 
     * @param values
     *            A collection of the allowable values
     * @return this
     */
    <E> Argument choices(Collection<E> values);

    /**
     * Sets a collection of the allowable values for the argument.
     * 
     * @param values
     *            A collection of the allowable values
     * @return this
     */
    <E> Argument choices(E... values);

    /**
     * <p>
     * The name of the attribute to be added.
     * </p>
     * <p>
     * The default value is For positional arguments, The default value is
     * normally supplied as the first argument to
     * {@link ArgumentParser#parseArgs(String[])}. For named arguments,
     * {@link ArgumentParser} generates the default value of {@code dest} by
     * taking the first long option string and stripping away the initial
     * {@code --} string. If no long option strings were supplied, {@code dest}
     * will be derived from the first short option string by stripping the
     * initial {@code -} character. Any internal {@code -} characters will be
     * converted to {@code _}.
     * </p>
     * 
     * @param dest
     *            The name of the attribute to be added
     * @return this
     */
    Argument dest(String dest);

    /**
     * Set the name for the argument in usage messages.
     * 
     * @param metavar
     *            The name for the argument in usage messages
     * @return this
     */
    Argument metavar(String... metavar);

    /**
     * Sets the brief description of what the argument does.
     * 
     * @param help
     *            The brief description of what the argument does
     * @return this
     */
    Argument help(String help);

    /**
     * <p>
     * Sets special value to control help message handling.
     * </p>
     * <p>
     * Currently, only {@link FeatureControl#SUPPRESS} is available. If it is
     * given, the help entry for this option is not displayed in the help
     * message.
     * </p>
     * 
     * @param ctrl
     *            The special value to control help message handling.
     * @return this
     */
    Argument help(FeatureControl ctrl);

    /**
     * <p>
     * Returns textual representation of the argument name.
     * </p>
     * 
     * <p>
     * For named arguments, this method returns the first argument given in
     * {@link ArgumentParser#addArgument(String...)}. For positional arguments,
     * this method returns the flags joined with "/", e.g. {@code -f/--foo}.
     * </p>
     * 
     * @return The textual representation of the argument name.
     */
    String textualName();

    // Getter methods

    /**
     * Returns dest value.
     * 
     * @return The dest value
     */
    String getDest();

    /**
     * Returns const value.
     * 
     * @return The const value
     */
    Object getConst();

    /**
     * Returns default value.
     * 
     * @return The default value
     */
    Object getDefault();

    /**
     * Returns default control.
     * 
     * @return The default control
     */
    FeatureControl getDefaultControl();

    /**
     * Returns help control.
     * 
     * @return The help control
     */
    FeatureControl getHelpControl();
}
