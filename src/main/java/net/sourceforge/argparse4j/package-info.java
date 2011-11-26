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
/**
 * <p>
 * The argparse4j is Java port of Python's famous <a
 * href="http://docs.python.org/library/argparse.html">argparse</a> module.
 * </p>
 * 
 * <p>
 * Because of the difference of language, we cannot use same syntax and usage of
 * original, but we tried to bring the same touch and feel as much as possible.
 * We also use same terminology as much as possible.
 * </p>
 * 
 * <p>
 * This manual was written based on argparse's manual and most of the
 * the sentences are almost identical, just replaced code examples.
 * We use this approach because argparse manual is well written and since
 * both are do the same thing, using existing manual makes us create a manual
 * with good quality in a short time. Thanks to Python community for the great
 * module and documentation.
 * </p>
 *
 * <h4>Examples</h4>
 * 
 * <p>
 * The following code is a Java program that takes a list of
 * integers and produces either the sum or the max:
 * </p>
 *
 * <pre>
 * public class Prog {
 * 
 *     private static interface Accumulate {
 *         int accumulate(Collection&lt;Integer&gt; ints);
 *     }
 * 
 *     private static class Sum implements Accumulate {
 *         &#064;Override
 *         public int accumulate(Collection&lt;Integer&gt; ints) {
 *             int sum = 0;
 *             for (Integer i : ints) {
 *                 sum += i;
 *             }
 *             return sum;
 *         }
 * 
 *         &#064;Override
 *         public String toString() {
 *             return getClass().getSimpleName();
 *         }
 *     }
 * 
 *     private static class Max implements Accumulate {
 *         &#064;Override
 *         public int accumulate(Collection&lt;Integer&gt; ints) {
 *             return Collections.max(ints);
 *         }
 * 
 *         &#064;Override
 *         public String toString() {
 *             return getClass().getSimpleName();
 *         }
 *     }
 * 
 *     public static void main(String[] args) {
 *         ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;).description(
 *                 &quot;Process some integers.&quot;);
 *         parser.addArgument(&quot;integers&quot;).metavar(&quot;N&quot;).type(Integer.class)
 *                 .nargs(&quot;+&quot;).help(&quot;an integer for the accumulator&quot;);
 *         parser.addArgument(&quot;--sum&quot;).dest(&quot;accumulate&quot;)
 *                 .action(Arguments.storeConst()).setConst(new Sum())
 *                 .setDefault(new Max())
 *                 .help(&quot;sum the integers (default: find the max)&quot;);
 *         try {
 *             Namespace res = parser.parseArgs(args);
 *             System.out.println(((Accumulate) res.get(&quot;accumulate&quot;))
 *                     .accumulate((List&lt;Integer&gt;) res.get(&quot;integers&quot;)));
 *         } catch (ArgumentParserException e) {
 *             parser.handleError(e);
 *         }
 *     }
 * }
 * </pre>
 * 
 * <p>
 * It can be run at the command line and provides useful help messages:
 * </p>
 * 
 * <pre>
 * $ java Prog -h
 * usage: prog [-h] [--sum] N [N ...]
 * 
 * Process some integers.
 * 
 * positional arguments:
 *   N                      an integer for the accumulator
 * 
 * optional arguments:
 *   -h, --help             show this help message and exit
 *   --sum                  sum the integers (default: find the max)
 * </pre>
 * 
 * <p>
 * When run with the appropriate arguments, it prints either the sum or the max
 * of the command-line integers:
 * </p>
 * 
 * <pre>
 * $ java Prog  1 2 3 4
 * 4
 * $ java Prog  1 2 3 4 --sum
 * 10
 * </pre>
 * 
 * <p>
 * If invalid arguments are passed in, it will throw an exception. The user
 * program can catch the exception and show error message:
 * </p>
 * 
 * <pre>
 * $ java Prog  a b c
 * usage: prog [-h] [--sum] N [N ...]
 * prog: error: argument integers: could not construct class java.lang.Integer from a (For input string: "a")
 * </pre>
 * 
 * <p>
 * The following sections walk you through this example.
 * </p>
 * 
 * <h4>Creating a parser</h4>
 * 
 * <p> 
 * The first step in using the argparse4j is creating
 * ArgumentParser object:
 * </p>
 * 
 * <pre>
 * ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;).description(
 *         &quot;Process some integers.&quot;);
 * </pre>
 * 
 * <p>
 * The ArgumentParser object will hold all the information necessary to parse
 * the command line into Java data types.
 * </p>
 * 
 * <h5>Adding arguments</h5>
 * 
 * <p>
 * Filling an ArgumentParser with information about program arguments is done by
 * making calls to the {@link net.sourceforge.argparse4j.inf.ArgumentParser#addArgument(String...)} method.
 * Generally, this calls tell the ArgumentParser how to take the strings on the
 * command line and turn them into objects. This information is stored and used
 * when {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])} is called. For example:
 * </p>
 * 
 * <pre>
 * parser.addArgument(&quot;integers&quot;).metavar(&quot;N&quot;).type(Integer.class).nargs(&quot;+&quot;)
 *         .help(&quot;an integer for the accumulator&quot;);
 * parser.addArgument(&quot;--sum&quot;).dest(&quot;accumulate&quot;).action(Arguments.storeConst())
 *         .setConst(new Sum()).setDefault(new Max())
 *         .help(&quot;sum the integers (default: find the max)&quot;);
 * </pre>
 * 
 * <p>
 * Later, calling {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])} will return an
 * {@link net.sourceforge.argparse4j.inf.Namespace} object with two attributes, {@code integers} and
 * {@code accumulate}. The {@code integers} attribute will be a
 * {@code List<Integer>} which has one or more ints, and the {@code accumulate}
 * attribute will be either the {@code Sum} object, if {@code --sum} was
 * specified at the command line, or the {@code Max} object if it was not.
 * </p>
 * 
 * <h5>Passing arguments</h5>
 * 
 * <p>
 * ArgumentParser parses arguments through the
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])} method. This will inspect the
 * command line, convert each argument to the appropriate type and then invoke
 * the appropriate action. In most cases, this means a simple {@link net.sourceforge.argparse4j.inf.Namespace}
 * object will have attributes parsed out of the command line. The following
 * code:
 * </p>
 * 
 * <pre>
 * Namespace res = parser.parseArgs(new String[] { &quot;--sum&quot;, &quot;7&quot;, &quot;-1&quot;, &quot;42&quot; });
 * System.out.println(res);
 * </pre>
 * 
 * <p>
 * will display:
 * </p>
 * 
 * <pre>
 * Namespace(integers=[7, -1, 42], accumulate=Sum)
 * </pre>
 * 
 * <p>
 * In Java, the command line arguments are given in the type {@code String[]}.
 * To parse the command line, pass this object to
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])} method.
 * </p>
 * 
 * <h4>ArgumentParser objects</h4>
 * 
 * <p>
 * In the ArgumentParser factory method
 * {@link net.sourceforge.argparse4j.ArgumentParsers#newArgumentParser(String, boolean, String)}, following parameters
 * can be specified:
 * </p>
 * 
 * <ul>
 * <li><a href="#ag4j_prog">prog</a> - The name of the program. This is
 * necessary because main() method in Java does not provide program name.</li>
 * <li><a href="#ag4j_addHelp">addHelp</a> - Add a {@code -h/--help} option to
 * the parser.(default: {@code true}).</li>
 * <li><a href="#ag4j_prefixChars">prefixChars</a> - The set of characters that
 * prefix optional arguments.(default: {@code -})</li>
 * </ul>
 * 
 * <p>
 * After creation of the instance, several additional parameters can be
 * specified using following methods:
 * </p>
 * 
 * <ul>
 * <li><a href="#ag4j_description">description()</a> - Text to display before
 * the argument help.</li>
 * <li><a href="#ag4j_epilog">epilog()</a> - Text to display after the argument
 * help.</li>
 * <li><a href="#ag4j_defaultHelp">defaultHelp()</a> - Display default value to
 * help message.(default: {@code false})</li>
 * </ul>
 * 
 * <p>
 * The following sections describes how each of these are used.
 * </p>
 * 
 * <h5 id="ag4j_prog">prog</h5>
 * 
 * <p>
 * In Java, the name of the program is not included in the argument in
 * {@code main()} method. Because of this, the name of the program must be
 * supplied to {@link net.sourceforge.argparse4j.ArgumentParsers#newArgumentParser(String)}.
 * </p>
 * 
 * <h5 id="ag4j_addHelp">addHelp</h5>
 * 
 * <p>
 * By default, ArgumentParser objects add an option which simply displays the
 * parser's help message. For example, consider following code:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;--foo&quot;).help(&quot;foo help&quot;);
 *     Namespace res = parser.parseArgs(args);
 * }
 * </pre>
 * 
 * <p>
 * If {@code -h} or {@code --help} is supplied at the command line, the
 * ArgumentParser will display help message:
 * </p>
 * 
 * <pre>
 * $ java Demo --help
 * usage: prog [-h] [--foo FOO]
 * 
 * optional arguments:
 *   -h, --help             show this help message and exit
 *   --foo FOO              foo help
 * </pre>
 * 
 * <p>
 * Occasionally, it may be useful to disable the addition of this help option.
 * This can be achieved by passing {@code false} as the {@code addHelp} argument
 * to {@link net.sourceforge.argparse4j.ArgumentParsers#newArgumentParser(String, boolean)}:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;, false);
 *     parser.addArgument(&quot;--foo&quot;).help(&quot;foo help&quot;);
 *     parser.printHelp();
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo
 * usage: prog [--foo FOO]
 * 
 * optional arguments:
 *   --foo FOO              foo help
 * </pre>
 * 
 * <p>
 * The help option is typically {@code -h/--help}. The exception to this is if
 * the {@code prefixChars} is specified and does not include {@code -}, in which
 * case {@code -h} and {@code --help} are not valid options. In this case, the
 * first character in {@code prefixChars} is used to prefix the help options:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;, true, &quot;+/&quot;);
 *     parser.printHelp();
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo
 * usage: prog [+h]
 * 
 * optional arguments:
 *   +h, ++help             show this help message and exit
 * </pre>
 * 
 * <h5 id="ag4j_prefixChars">prefixChars</h5>
 * 
 * Most command line options will use {@code -} as the prefix, e.g.
 * {@code -f/--foo}. Parsers that need to support different or additional prefix
 * characters, e.g. for options like {@code +f} or {@code /foo}, may specify
 * them using the {@code prefixChars} argument to
 * {@link net.sourceforge.argparse4j.ArgumentParsers#newArgumentParser(String, boolean, String)}:
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;, true, &quot;-+&quot;);
 *     parser.addArgument(&quot;+f&quot;);
 *     parser.addArgument(&quot;++bar&quot;);
 *     Namespace res = parser.parseArgs(args);
 *     System.out.println(res);
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo +f X ++bar Y
 * Namespace(f=X, bar=Y)
 * </pre>
 * 
 * The {@code prefixChars} argument defaults to {@code "-"}. Supplying a set of
 * characters that does not include {@code -} will cause {@code -f/--foo}
 * options to be disallowed.
 * 
 * <h5 id="ag4j_description">description()</h5>
 * 
 * It gives a brief description of what the program does and how it works. In
 * help message, the description is displayed between command line usage string
 * and the help messages for the various arguments:
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;).description(
 *             &quot;A foo that bars&quot;);
 *     parser.printHelp();
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo
 * usage: prog [-h]
 * 
 * A foo that bars
 * 
 * optional arguments:
 * -h, --help             show this help message and exit
 * </pre>
 * 
 * By default, the description will be line-wrapped so that it fits within the
 * given space.
 * 
 * <h5 id="ag4j_epilog">epilog()</h5>
 * 
 * Some programs like to display additional description of the program after the
 * description of the arguments. Such text can be specified using
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#epilog(String)} method:
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;)
 *             .description(&quot;A foo that bars&quot;)
 *             .epilog(&quot;And that's how you'd foo a bar&quot;);
 *     parser.printHelp();
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo
 * usage: prog [-h]
 * 
 * A foo that bars
 * 
 * optional arguments:
 *   -h, --help             show this help message and exit
 * 
 * And that's how you'd foo a bar
 * </pre>
 * 
 * As with the {@link net.sourceforge.argparse4j.inf.ArgumentParser#description(String)} method, text specified
 * in {@link net.sourceforge.argparse4j.inf.ArgumentParser#epilog(String)} is by default line-wrapped.
 * 
 * <h5 id="ag4j_defaultHelp">defaultHelp()</h5>
 * 
 * The default value of each argument is not by default displayed in help
 * message. Specifying {@code true} to
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#defaultHelp(boolean)} method will display the default
 * value of each argument in help message:
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;).defaultHelp(true);
 *     parser.addArgument(&quot;--foo&quot;).type(Integer.class).setDefault(42).help(&quot;FOO!&quot;);
 *     parser.addArgument(&quot;bar&quot;).nargs(&quot;*&quot;).setDefault(1, 2, 3).help(&quot;BAR!&quot;);
 *     parser.printHelp();
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo
 * usage: prog [-h] [-f FOO] [bar [bar ...]]
 * 
 * positional arguments:
 *   bar                    BAR! (default: [1, 2, 3])
 * 
 * optional arguments:
 *   -h, --help             show this help message and exit
 *   -f FOO, --foo FOO      FOO! (default: 42)
 * </pre>
 * 
 * <h4 id="ag4j_addArgument">The addArgument() method</h4>
 * 
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#addArgument(String...)} method creates new
 * {@link net.sourceforge.argparse4j.inf.Argument} object and adds it to ArgumentParser's internal memory and
 * returns the object to the user code. {@link net.sourceforge.argparse4j.inf.Argument} object defines how a
 * single command line argument should be parsed.
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#addArgument(String...)} method receives <a
 * href="#ag4j_nameOrFlags">nameOrFlags</a> argument, which is either a name or
 * a list of option strings, e.g. {@code foo} or {@code -f, --foo}.
 * 
 * After obtained {@link net.sourceforge.argparse4j.inf.Argument} object, several parameters can be specified
 * using following methods:
 * 
 * <ul>
 * <li><a href="#ag4j_action">action()</a> - The basic type of action to be
 * taken when this argument is encountered at the command line.</li>
 * <li><a href="#ag4j_nargs">nargs()</a> - The number of command line arguments
 * that should be consumed.</li>
 * <li><a href="#ag4j_setConst">setConst()</a> - A constant value required by
 * some <a href="#ag4j_action">action()</a> and <a
 * href="#ag4j_nargs">nargs()</a> selections.</li>
 * <li><a href="#ag4j_type">type()</a> - The type to which the command line
 * argument should be converted.</li>
 * <li><a href="#ag4j_choices">choices()</a> - A collection of the allowable
 * values for the argument.</li>
 * <li><a href="#ag4j_required">required()</a> - Whether or not the command line
 * option may be omitted(optional arguments only).</li>
 * <li><a href="#ag4j_help">help()</a> - A brief description of what the
 * argument does.</li>
 * <li><a href="#ag4j_metavar">metavar()</a> - A name for the argument in usage
 * messages.</li>
 * <li><a href="#ag4j_dest">dest()</a> - The name of the attribute to be added
 * as a result of {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])}.</li>
 * </ul>
 * 
 * The following sections describe how each of these are used.
 * 
 * <h5 id="ag4j_nameOrFlags">nameOrFlags</h5>
 * 
 * The {@link net.sourceforge.argparse4j.inf.ArgumentParser#addArgument(String...)} method must know whether an
 * optional argument, like {@code -f} or {@code --foo}, or a positional
 * argument, like a list of filenames, is expected. The arguments passed to
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#addArgument(String...)} must therefore be either a
 * series of flags, or a simple argument name. For example, an optional argument
 * could be created like:
 * 
 * <pre>
 * parser.addArgument(&quot;-f&quot;, &quot;--foo&quot;);
 * </pre>
 * 
 * while a positional argument could be created like:
 * 
 * <pre>
 * parser.addArgument(&quot;bar&quot;);
 * </pre>
 * 
 * When {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])} is called, optional arguments
 * will be identified by the {@code -} prefix(or one of {@code prefixChars} if
 * it is specified in {@link net.sourceforge.argparse4j.ArgumentParsers#newArgumentParser(String, boolean, String)}),
 * and the remaining arguments will be assumed to be positional:
 * 
 * <pre>
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;-f&quot;, &quot;--foo&quot;);
 *     parser.addArgument(&quot;bar&quot;);
 *     try {
 *         System.out.println(parser.parseArgs(args));
 *     } catch (ArgumentParserException e) {
 *         parser.handleError(e);
 *     }
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo BAR
 * Namespace(foo=null, bar=BAR)
 * $ java Demo BAR --foo FOO
 * Namespace(foo=FOO, bar=BAR)
 * $ java Demo --foo FOO
 * usage: prog [-h] [-f FOO] bar
 * prog: error: too few arguments
 * </pre>
 * 
 * <h5 id="ag4j_action">action()</h5>
 * 
 * {@link net.sourceforge.argparse4j.inf.Argument} objects associate command line arguments with actions. These
 * actions can do just about anything with command line arguments associated
 * with them, though most of the actions simply add an attribute to the object
 * returned by {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])}. The
 * {@link net.sourceforge.argparse4j.inf.Argument#action(net.sourceforge.argparse4j.inf.ArgumentAction)} method specifies how the command line
 * arguments should be handled. The supported actions are:
 * 
 * <ul>
 * <li>{@link net.sourceforge.argparse4j.impl.Arguments#store()} - This just stores the argument's value. This
 * is the default action. For example:
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;-f&quot;, &quot;--foo&quot;);
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo --foo 1
 * Namespace(foo=1)
 * </pre>
 * 
 * </li>
 * 
 * <li>{@link net.sourceforge.argparse4j.impl.Arguments#storeConst()} - This stores the value specified by the
 * <a href="#ag4j_setConst">setConst()</a>. (Note that by default const value is
 * the rather unhelpful {@code null}.) The {@link net.sourceforge.argparse4j.impl.Arguments#storeConst()} action
 * is most commonly used with optional arguments that specify sort of flags. For
 * example:
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;--foo&quot;).action(Arguments.storeConst()).setConst(42);
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo --foo
 * Namespace(foo=42)
 * </pre>
 * 
 * </li>
 * 
 * <li>{@link net.sourceforge.argparse4j.impl.Arguments#storeTrue()} and {@link net.sourceforge.argparse4j.impl.Arguments#storeFalse()} - These
 * are special cases of {@link net.sourceforge.argparse4j.impl.Arguments#storeConst()} using for storing values
 * {@code true} and {@code false} respectively. In addition, they create default
 * values of {@code false} and {@code true} respectively. For example:
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;--foo&quot;).action(Arguments.storeTrue());
 *     parser.addArgument(&quot;--bar&quot;).action(Arguments.storeFalse());
 *     parser.addArgument(&quot;--baz&quot;).action(Arguments.storeFalse());
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo --foo --bar
 * Namespace(baz=true, foo=true, bar=false)
 * </pre>
 * 
 * </li>
 * 
 * <li>{@link net.sourceforge.argparse4j.impl.Arguments#append()} - This stores a list, and appends each
 * argument value to the list. The list is of type {@link java.util.List}. This is useful
 * to allow an option to be specified multiple times. For example:
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;--foo&quot;).action(Arguments.append());
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo --foo 1 --foo 2
 * Namespace(foo=[1, 2])
 * </pre>
 * 
 * </li>
 * 
 * <li>{@link net.sourceforge.argparse4j.impl.Arguments#appendConst()} - This stores a list, and appends the
 * value specified by <a href="#ag4j_setConst">setConst()</a> to the list. (Note
 * that the const value defaults to {@code null}.) The list is of type
 * {@link java.util.List}. The {@link net.sourceforge.argparse4j.impl.Arguments#appendConst()} action is typically useful
 * when multiple arguments need to store constants to the same list. For
 * example:
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;--str&quot;).dest(&quot;types&quot;).action(Arguments.appendConst())
 *             .setConst(String.class);
 *     parser.addArgument(&quot;--int&quot;).dest(&quot;types&quot;).action(Arguments.appendConst())
 *             .setConst(Integer.class);
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo --str --int
 * Namespace(types=[class java.lang.String, class java.lang.Integer])
 * </pre>
 * 
 * </li>
 * <li>{@link net.sourceforge.argparse4j.impl.Arguments#version()} - This action prints version string specified
 * by {@link net.sourceforge.argparse4j.inf.ArgumentParser#version(String)} and exists when invoked.
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;).version(&quot;prog 2.0&quot;);
 *     parser.addArgument(&quot;--version&quot;).action(Arguments.version());
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo --version
 * prog 2.0
 * </pre>
 * 
 * </li>
 * 
 * <li>{@link net.sourceforge.argparse4j.impl.Arguments#help()} - This action prints help message and exits when
 * invoked.
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;, false);
 *     parser.addArgument(&quot;--help&quot;).action(Arguments.help());
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo --help
 * usage: prog [--help]
 * 
 * optional arguments:
 *   --help
 * </pre>
 * 
 * </ul>
 * 
 * You can also specify your costum action by implementing
 * {@link net.sourceforge.argparse4j.inf.ArgumentAction} interface. For example:
 * 
 * <pre>
 * private static class FooAction implements ArgumentAction {
 * 
 *     &#064;Override
 *     public void run(ArgumentParser parser, Argument arg,
 *             Map&lt;String, Object&gt; attrs, String flag, Object value)
 *             throws ArgumentParserException {
 *         System.out.printf(&quot;%s '%s' %s\n&quot;, attrs, value, flag);
 *         attrs.put(arg.getDest(), value);
 * 
 *     }
 * 
 *     &#064;Override
 *     public void onAttach(Argument arg) {
 *     }
 * 
 *     &#064;Override
 *     public boolean consumeArgument() {
 *         return true;
 *     }
 * }
 * 
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     FooAction fooAction = new FooAction();
 *     parser.addArgument(&quot;--foo&quot;).action(fooAction);
 *     parser.addArgument(&quot;bar&quot;).action(fooAction);
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo  1 --foo 2
 * {foo=null, bar=null} '1' null
 * {foo=null, bar=1} '2' --foo
 * Namespace(foo=2, bar=1)
 * </pre>
 * 
 * <h5 id="ag4j_nargs">nargs()</h5>
 * 
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser} objects usually associate a single command line
 * argument with a single action to be taken. The {@link net.sourceforge.argparse4j.inf.Argument#nargs(int)}
 * and {@link net.sourceforge.argparse4j.inf.Argument#nargs(String)} associate different number of command line
 * arguments with a single action.
 * 
 * <ul>
 * <li>{@code N}(an positive integer). {@link net.sourceforge.argparse4j.inf.Argument#nargs(int)} take a
 * positive integer {@code N}. {@code N} arguments from the command line will be
 * gathered into a {@link java.util.List}. For example:
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;--foo&quot;).nargs(2);
 *     parser.addArgument(&quot;bar&quot;).nargs(1);
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo c --foo a b
 * Namespace(foo=[a, b], bar=[c])
 * </pre>
 * 
 * Note that {@code nargs(1)} produces a list of one item. This is different
 * from the default, in which the item is produced by itself.</li>
 * 
 * <li>{@code "?"}. {@link net.sourceforge.argparse4j.inf.Argument#nargs(String)} can take string {@code "?"}.
 * One argument will be consumed from the command line if possible, and produced
 * as a single item. If no command line argument is present, the value from <a
 * href="#ag4j_setDefault">setDefault()</a> will be produced. Note that for
 * optional arguments, there is an additional case - the option string is
 * present but not followed by a command line argument. In this case the value
 * from <a href="#ag4j_setConst">setConst()</a> will be produced. Some examples
 * to illustrate this:
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;--foo&quot;).nargs(&quot;?&quot;).setConst(&quot;c&quot;).setDefault(&quot;d&quot;);
 *     parser.addArgument(&quot;bar&quot;).nargs(&quot;?&quot;).setDefault(&quot;d&quot;);
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo XX --foo YY
 * Namespace(foo=YY, bar=XX)
 * $ java Demo XX --foo
 * Namespace(foo=c, bar=XX)
 * $ java Demo
 * Namespace(foo=d, bar=d)
 * </pre>
 * 
 * One of the more common usage of {@code nargs("?")} is to allow optional input
 * and output files:
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;infile&quot;).nargs(&quot;?&quot;).type(FileInputStream.class)
 *             .setDefault(System.in);
 *     parser.addArgument(&quot;outfile&quot;).nargs(&quot;?&quot;).type(PrintStream.class)
 *             .setDefault(System.out);
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo input.txt output.txt
 * Namespace(infile=java.io.FileInputStream@4ce86da0, outfile=java.io.PrintStream@2f754ad2)
 * $ java Demo
 * Namespace(infile=java.io.BufferedInputStream@e05d173, outfile=java.io.PrintStream@1ff9dc36)
 * </pre>
 * 
 * It is not obvious that outfile points to output.txt from the abolve output,
 * but it is actually PrintStream to outfile.txt.
 * 
 * </li>
 * 
 * <li>{@code "*"}. {@link net.sourceforge.argparse4j.inf.Argument#nargs(String)} can take string {@code "*"}.
 * All command line arguments present are gathered into a {@link java.util.List}. Note
 * that it generally does not make sense to have more than one positional
 * argument with {@code nargs("*")}, but multiple optional arguments with
 * {@code nargs("*")} is possible. For example:
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;--foo&quot;).nargs(&quot;*&quot;);
 *     parser.addArgument(&quot;--bar&quot;).nargs(&quot;*&quot;);
 *     parser.addArgument(&quot;baz&quot;).nargs(&quot;*&quot;);
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo
 * Namespace(baz=[], foo=null, bar=null)
 * $ java Demo a b --foo x y --bar 1 2
 * Namespace(baz=[a, b], foo=[x, y], bar=[1, 2])
 * </pre>
 * 
 * </li>
 * 
 * <li>{@code "+"}. {@link net.sourceforge.argparse4j.inf.Argument#nargs(String)} can take string {@code "+"}.
 * Just like {@code "*"}, all command line arguments present are gathered into a
 * {@link java.util.List}. Additionally, an error message will be generated if there
 * wasn't at least one command line argument present. For example:
 * 
 * <pre>
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;foo&quot;).nargs(&quot;+&quot;);
 *     try {
 *         System.out.println(parser.parseArgs(args));
 *     } catch (ArgumentParserException e) {
 *         parser.handleError(e);
 *     }
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo a b
 * Namespace(foo=[a, b])
 * $ java Demo
 * usage: prog [-h] foo [foo ...]
 * prog: error: too few arguments
 * </pre>
 * 
 * </li>
 * </ul>
 * 
 * If neither {@link net.sourceforge.argparse4j.inf.Argument#nargs(int)} nor {@link net.sourceforge.argparse4j.inf.Argument#nargs(String)} are
 * provided, the number of arguments consumed is determined by the <a
 * href="#ag4j_action">action</a>. Generally this means a single command line
 * argument will be consumed and a single item(not a {@link java.util.List} will be
 * produced.
 * 
 * Please note that {@link net.sourceforge.argparse4j.inf.Argument#nargs(int)} and
 * {@link net.sourceforge.argparse4j.inf.Argument#nargs(String)} are ignored if one of
 * {@link net.sourceforge.argparse4j.impl.Arguments#storeConst()}, {@link net.sourceforge.argparse4j.impl.Arguments#appendConst()},
 * {@link net.sourceforge.argparse4j.impl.Arguments#storeTrue()} is provided. More specifically, subclass of
 * {@link net.sourceforge.argparse4j.inf.ArgumentAction} whose {@link net.sourceforge.argparse4j.inf.ArgumentAction#consumeArgument()} returns
 * {@code false} ignores {@code nargs()}.
 * 
 * <h5 id="ag4j_setConst">setConst()</h5>
 * 
 * The {@link net.sourceforge.argparse4j.inf.Argument#setConst(Object)} is used to hold constant values that
 * are not read from the command line but are required for the various actions.
 * The two most common uses of it are:
 * 
 * <ul>
 * <li>When {@link net.sourceforge.argparse4j.impl.Arguments#storeConst()} and {@link net.sourceforge.argparse4j.impl.Arguments#appendConst()}
 * are specified. These actions add the value spcified by
 * {@link net.sourceforge.argparse4j.inf.Argument#setConst(Object)} to one of the attributes of the object
 * returned by {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])}. See the <a
 * href="#ag4j_action">action description</a> for examples.</li>
 * <li>When {@link net.sourceforge.argparse4j.inf.ArgumentParser#addArgument(String...)} is called with option
 * strings (like {@code -f} or {@code --foo} and {@code nargs("?")} is used.
 * This creates an optional argument that can be followed by zero or one command
 * line argument. When parsing the command line, if the option string is
 * encountered with no command line argument following it, the value specified
 * by {@link net.sourceforge.argparse4j.inf.Argument#setConst(Object)} will be assumed instead. See the <a
 * href="#ag4j_nargs">nargs()</a> description for examples.</li>
 * </ul>
 * 
 * The const value defauls to {@code null}.
 * 
 * <h5 id="ag4j_setDefault">setDefault()</h5>
 * 
 * All optional arguments and some positional arguments may be omitted at the
 * command line. The {@link net.sourceforge.argparse4j.inf.Argument#setDefault(Object)} specifies what value
 * should be used if the command line argument is not present. The default value
 * defaults to {@code null}. For optional arguments, the default value is used
 * when the option string was not present at the command line:
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;--foo&quot;).setDefault(42);
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo --foo 2
 * Namespace(foo=2)
 * $ java Demo
 * Namespace(foo=42)
 * </pre>
 * 
 * For positional arguments with {@link net.sourceforge.argparse4j.inf.Argument#nargs(String)} equals to
 * {@code "?"} or {@code "*"}, the default value is used when no command line
 * argument was present:
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;foo&quot;).nargs(&quot;?&quot;).setDefault(42);
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo a
 * Namespace(foo=a)
 * $ java Demo
 * Namespace(foo=42)
 * </pre>
 * 
 * Providing {@link net.sourceforge.argparse4j.impl.Arguments#SUPPRESS} causes no attribute to be added if the
 * command ine argument was not present:
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;--foo&quot;).setDefault(Arguments.SUPPRESS);
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo
 * Namespace()
 * $ java Demo --foo 1
 * Namespace(foo=1)
 * </pre>
 * 
 * <h5 id="ag4j_type">type()</h5>
 * 
 * By default, {@link net.sourceforge.argparse4j.inf.ArgumentParser} objects read command line arguments in as
 * simple strings. However, quite often the command line string should instead
 * be interpreted as another type, like a {@code float} or {@code int}. The
 * {@link net.sourceforge.argparse4j.inf.Argument#type(Class)} allows any necessary type-checking and type
 * conversions performed. Common primitive types and classes which has
 * construtor with 1 string argument can be used directly as the value of
 * {@link net.sourceforge.argparse4j.inf.Argument#type(Class)}:
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;foo&quot;).type(Integer.class);
 *     parser.addArgument(&quot;bar&quot;).type(FileInputStream.class);
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo 2 input.txt
 * Namespace(foo=2, bar=java.io.FileInputStream@2f754ad2)
 * </pre>
 * 
 * For types whose constructor does not have 1 string argument, you can subclass
 * {@link net.sourceforge.argparse4j.inf.ArgumentType} and pass its instance to
 * {@link net.sourceforge.argparse4j.inf.Argument#type(net.sourceforge.argparse4j.inf.ArgumentType)}:
 * 
 * <pre>
 * private static class PerfectSquare implements ArgumentType {
 * 
 *     &#064;Override
 *     public Object convert(ArgumentParser parser, Argument arg, String value)
 *             throws ArgumentParserException {
 *         try {
 *             int n = Integer.parseInt(value);
 *             double sqrt = Math.sqrt(n);
 *             if (sqrt != (int) sqrt) {
 *                 throw new ArgumentParserException(String.format(
 *                         &quot;%d is not a perfect square&quot;, n));
 *             }
 *             return n;
 *         } catch (NumberFormatException e) {
 *             throw new ArgumentParserException(e);
 *         }
 *     }
 * }
 * 
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;foo&quot;).type(new PerfectSquare());
 *     try {
 *         System.out.println(parser.parseArgs(args));
 *     } catch (ArgumentParserException e) {
 *         parser.handleError(e);
 *     }
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo 9
 * Namespace(foo=9)
 * $ java Demo 7
 * usage: prog [-h] foo
 * prog: error: 7 is not a perfect square
 * </pre>
 * 
 * The {@link net.sourceforge.argparse4j.inf.Argument#choices(Object...)} may be more convenient for type
 * checkers that simply check against a range of values:
 * 
 * <pre>
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;foo&quot;).type(Integer.class)
 *             .choices(Arguments.range(5, 10));
 *     try {
 *         System.out.println(parser.parseArgs(args));
 *     } catch (ArgumentParserException e) {
 *         parser.handleError(e);
 *     }
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo 7
 * Namespace(foo=7)
 * $ java Demo 11
 * usage: prog [-h] foo
 * prog: error: foo expects value in range [5, 10], inclusive
 * </pre>
 * 
 * See <a href="#ag4j_choices">choices()</a> for more details.
 * 
 * <h5 id="ag4j_choices">choices()</h5>
 * 
 * Some command line arguments should be selected from a restricted set of
 * values. These can be handled by passing a list of objects to
 * {@link net.sourceforge.argparse4j.inf.Argument#choices(Object...)}. When the command line is parsed,
 * argument values will be checked, and an error message will be displayed if
 * the argument was not one of the accepted values:
 * 
 * <pre>
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;foo&quot;).choices(&quot;a&quot;, &quot;b&quot;, &quot;c&quot;);
 *     try {
 *         System.out.println(parser.parseArgs(args));
 *     } catch (ArgumentParserException e) {
 *         parser.handleError(e);
 *     }
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo c
 * Namespace(foo=c)
 * $ java Demo X
 * usage: prog [-h] {a,b,c}
 * prog: error: argument foo: invalid choice: 'X' (choose from {a,b,c})
 * </pre>
 * 
 * Note that inclusion in the choices list is checked after any type conversions
 * have been performed,
 * 
 * If a list of value is not enough, you can create your own by subclassing
 * {@link net.sourceforge.argparse4j.inf.ArgumentChoice}. For example, argparse4j provides
 * {@link net.sourceforge.argparse4j.impl.Arguments#range(Comparable, Comparable)} to check whether an integer
 * is in specified range:
 * 
 * <pre>
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;foo&quot;).type(Integer.class)
 *             .choices(Arguments.range(1, 10));
 *     try {
 *         System.out.println(parser.parseArgs(args));
 *     } catch (ArgumentParserException e) {
 *         parser.handleError(e);
 *     }
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo 1
 * Namespace(foo=1)
 * $ java Demo 11
 * usage: prog [-h] foo
 * prog: error: foo expects value in range [1, 10], inclusive
 * </pre>
 * 
 * <h5 id="ag4j_required">required()</h5>
 * 
 * In general, the argparse module assumes that flags like {@code -f} and
 * {@code --bar} indicate optional arguments, which can always be omitted at the
 * command line. To make an option required, {@code true} can be specified for
 * {@link net.sourceforge.argparse4j.inf.Argument#required(boolean)}:
 * 
 * <pre>
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;--foo&quot;).required(true);
 *     try {
 *         System.out.println(parser.parseArgs(args));
 *     } catch (ArgumentParserException e) {
 *         parser.handleError(e);
 *     }
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo --foo BAR
 * Namespace(foo=BAR)
 * $ java Demo
 * usage: prog [-h] --foo FOO
 * prog: error: argument --foo is required
 * </pre>
 * 
 * <p>
 * As the example shows, if an option is marked as required,
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])} will report an error if that
 * option is not present at the command line. {@link net.sourceforge.argparse4j.inf.Argument#required(boolean)}
 * will be ignored for positional arguments.
 * </p>
 * 
 * <p>
 * Note: Required options are generally considered bad form because users expect
 * options to be optional, and thus they should be avoided when possible.
 * </p>
 * 
 * <h5 id="ag4j_help">help()</h5>
 * 
 * <p>
 * {@link net.sourceforge.argparse4j.inf.Argument#help(String)} can take string containing a brief description
 * of the argument. When a user requests help (usually by using {@code -h} or
 * {@code --help} at the command line), these help descriptions will be
 * displayed with each argument:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;--foo&quot;).action(Arguments.storeTrue())
 *             .help(&quot;foo the bars before frobbling&quot;);
 *     parser.addArgument(&quot;bar&quot;).nargs(&quot;+&quot;).help(&quot;one of the bars to be frobbled&quot;);
 *     try {
 *         System.out.println(parser.parseArgs(args));
 *     } catch (ArgumentParserException e) {
 *         parser.handleError(e);
 *     }
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo  -h
 * usage: prog [-h] [--foo] bar [bar ...]
 * 
 * positional arguments:
 *   bar                    one of the bars to be frobbled
 * 
 * optional arguments:
 *   -h, --help             show this help message and exit
 *   --foo                  foo the bars before frobbling
 * </pre>
 * 
 * <p>
 * The help strings are used as is: no special string replacement will not be
 * done.
 * </p>
 * 
 * <h5 id="ag4j_metavar">metavar()</h5>
 * 
 * <p>
 * When {@link net.sourceforge.argparse4j.inf.ArgumentParser} generates help messages, it need some way to
 * referer to each expected argument. By default, {@link net.sourceforge.argparse4j.inf.ArgumentParser} objects
 * use the <a href="#ag4j_dest">dest()</a> value as the "name" of each object.
 * By default, for positional arguments, the dest value is used directly, and
 * for optional arguments, the dest value is uppercased. So, a single positional
 * argument with {@code dest("bar")} will be referred to as {@code bar}. A
 * single optional argument {@code --foo} that should be followed by a single
 * command line argument will be referred to as {@code FOO}. For example:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;--foo&quot;);
 *     parser.addArgument(&quot;bar&quot;);
 *     parser.printHelp();
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo
 * usage: prog [-h] [--foo FOO] bar
 * 
 * positional arguments:
 *   bar                    
 * 
 * optional arguments:
 *   -h, --help             show this help message and exit
 *   --foo FOO
 * </pre>
 * 
 * <p>
 * An alternative name can be specified with {@link net.sourceforge.argparse4j.inf.Argument#metavar(String...)}:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;--foo&quot;).metavar(&quot;YY&quot;);
 *     parser.addArgument(&quot;bar&quot;).metavar(&quot;XX&quot;);
 *     parser.printHelp();
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo
 * usage: prog [-h] [--foo YY] XX
 * 
 * positional arguments:
 *   XX                     
 * 
 * optional arguments:
 *   -h, --help             show this help message and exit
 *   --foo YY
 * </pre>
 * 
 * <p>
 * Note that {@link net.sourceforge.argparse4j.inf.Argument#metavar(String...)} only changes the displayed name -
 * the name of the attribute in the object returned by
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])} is still determined by the <a
 * href="#ag4j_dest">dest()</a> value.
 * </p>
 * 
 * <p>
 * Different values of <a href="#ag4j_nargs">nargs()</a> may cause the metavar
 * to be used multiple times. Providing multiple values to
 * {@link net.sourceforge.argparse4j.inf.Argument#metavar(String...)} specifies a different display for each of
 * the arguments:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;-x&quot;).nargs(2);
 *     parser.addArgument(&quot;--foo&quot;).nargs(2).metavar(&quot;bar&quot;, &quot;baz&quot;);
 *     parser.printHelp();
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo
 * usage: prog [-h] [-x X X] [--foo bar baz]
 * 
 * optional arguments:
 *   -h, --help             show this help message and exit
 *   -x X X                 
 *   --foo bar baz
 * </pre>
 * 
 * <p>
 * If the number of values specified in {@link net.sourceforge.argparse4j.inf.Argument#metavar(String...)} is
 * not sufficient for the number of arguments given in <a
 * href="#ag4j_nargs">nargs()</a>, the last value of metavar is repeated.
 * </p>
 * 
 * <h5 id="ag4j_dest">dest()</h5>
 * 
 * <p>
 * Most {@link net.sourceforge.argparse4j.inf.ArgumentParser} actions add some values as an attribute of the
 * object returned by {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])}. The name of
 * this attribute is determined by {@code dest}. For positional arguments,
 * {@code dest} is normally supplied as the first argument to
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#addArgument(String...)}:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;bar&quot;);
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo  XX
 * Namespace(bar=XX)
 * </pre>
 * 
 * <p>
 * For optional arguments, the value of {@code dest} is normally inferred from
 * the option strings. {@link net.sourceforge.argparse4j.inf.ArgumentParser} generates the value of
 * {@code dest} by taking the first long option string and stripping away the
 * initial {@code --} string. If no long option strings were supplied,
 * {@code dest} will be derived from the first short option string by stripping
 * the initial {@code -} character. Any internal {@code -} characters will be
 * converted to {@code _}. The example below illustrate this behavior:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;-f&quot;, &quot;--foo-bar&quot;, &quot;--foo&quot;);
 *     parser.addArgument(&quot;-x&quot;, &quot;-y&quot;);
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo  -f 1 -x 2
 * Namespace(x=2, foo_bar=1)
 * $ java Demo  --foo 1 -y 2
 * Namespace(x=2, foo_bar=1)
 * </pre>
 * 
 * <p>
 * {@link net.sourceforge.argparse4j.inf.Argument#dest(String)} allows a custom attribute name to be provided:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;--foo&quot;).dest(&quot;bar&quot;);
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo --foo XX
 * Namespace(bar=XX)
 * </pre>
 * 
 * <h4>The parseArgs() method</h4>
 * 
 * <p>
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])} converts argument strings to
 * objects and populates {@link net.sourceforge.argparse4j.inf.Namespace} object with these values. The
 * populated {@link net.sourceforge.argparse4j.inf.Namespace} is returned.
 * </p>
 * 
 * <p>
 * Previous calls to {@link net.sourceforge.argparse4j.inf.ArgumentParser#addArgument(String...)} determine
 * exactly what objects are created and how they are assigned. See the
 * documentation for <a href="#ag4j_addArgument">addArgument</a> for details.
 * </p>
 * 
 * <h5>Option value syntax</h5>
 * 
 * <p>
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])} supports several ways of
 * specifying the value of an option (if it takes one). In the simplest case,
 * the option and its value are passed as two separate arguments:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;-x&quot;);
 *     parser.addArgument(&quot;--foo&quot;);
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo -x X
 * Namespace(foo=null, x=X)
 * $ java Demo --foo FOO
 * Namespace(foo=FOO, x=null)
 * </pre>
 * 
 * <p>
 * For long options (options with names longer than single character), the
 * option and value can also be passed as a single command line argument, using
 * {@code =} to separate them:
 * </p>
 * 
 * <pre>
 *  $ java Demo --foo=FOO
 * Namespace(foo=FOO, x=null)
 * </pre>
 * 
 * <p>
 * For short options (options only one character long), the option and its value
 * can be concatenated:
 * </p>
 * 
 * <pre>
 *  $ java Demo -xX
 * Namespace(foo=null, x=X)
 * </pre>
 * 
 * <p>
 * Several short options can be joined together, using only a single {@code -}
 * prefix, as long as only the last option (or none of them) requires a value:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) throws ArgumentParserException {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;-x&quot;).action(Arguments.storeTrue());
 *     parser.addArgument(&quot;-y&quot;).action(Arguments.storeTrue());
 *     parser.addArgument(&quot;-z&quot;);
 *     System.out.println(parser.parseArgs(args));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo -xyzZ
 * Namespace(z=Z, y=true, x=true)
 * </pre>
 * 
 * <h5>Invalid arguments</h5>
 * 
 * <p>
 * While parsing the command line, {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])}
 * checks for a variety of errors, including invalid types, invalid options,
 * wrong number of positional arguments, etc. When it encounters such an error,
 * it throws {@link net.sourceforge.argparse4j.inf.ArgumentParserException}. The typical error handling is
 * catch the exception and use
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#handleError(net.sourceforge.argparse4j.inf.ArgumentParserException)} to print error
 * message and exit the program:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;--foo&quot;).type(Integer.class);
 *     parser.addArgument(&quot;bar&quot;).nargs(&quot;?&quot;);
 *     try {
 *         System.out.println(parser.parseArgs(args));
 *     } catch (ArgumentParserException e) {
 *         parser.handleError(e);
 *         System.exit(1);
 *     }
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo --foo spam
 * usage: prog [-h] [--foo FOO] [bar]
 * prog: error: argument --foo: could not convert 'spam' to Integer (For input string: "spam")
 * $ java Demo --bar
 * usage: prog [-h] [--foo FOO] [bar]
 * prog: error: unrecognized arguments: --bar
 * $ java Demo spam badger
 * usage: prog [-h] [--foo FOO] [bar]
 * prog: error: unrecognized arguments: badger
 * </pre>
 * 
 * <h5>Arguments containing {@code "-"}</h5>
 * 
 * <p>
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])} attempts to give errors whenever
 * the user has cearly made a mistake, but some situations are inherently
 * ambiguous. For example, the command line argument {@code -1} could either be
 * an attempt to specify an option or an attempt to provide a positional
 * argument. The {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])} is cautious here:
 * positional arguments may only begin with {@code -} if they look like negative
 * numbers and there are no options in the parser that look like negative
 * numbers:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;-x&quot;);
 *     parser.addArgument(&quot;foo&quot;).nargs(&quot;?&quot;);
 *     try {
 *         System.out.println(parser.parseArgs(args));
 *     } catch (ArgumentParserException e) {
 *         parser.handleError(e);
 *         System.exit(1);
 *     }
 * }
 * </pre>
 * 
 * <pre>
 * $ # no negative number options, so -1 is a positional argument
 * $ java Demo -x -1
 * Namespace(foo=null, x=-1)
 * $ # no negative number options, so -1 and -5 are positional arguments
 * $ java Demo -x -1 -5
 * Namespace(foo=-5, x=-1)
 * </pre>
 * 
 * <pre>
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;-1&quot;).dest(&quot;one&quot;);
 *     parser.addArgument(&quot;foo&quot;).nargs(&quot;?&quot;);
 *     try {
 *         System.out.println(parser.parseArgs(args));
 *     } catch (ArgumentParserException e) {
 *         parser.handleError(e);
 *         System.exit(1);
 *     }
 * }
 * </pre>
 * 
 * <pre>
 * $ # negative number options present, so -1 is an option
 * $ java Demo -1 X
 * Namespace(one=X, foo=null)
 * $ # negative number options present, so -2 is an option
 * $ java Demo -2
 * usage: prog [-h] [-1 ONE] [foo]
 * prog: error: unrecognized arguments: -2
 * $ # negative number options present, so both -1s are options
 * $ java Demo -1 -1
 * usage: prog [-h] [-1 ONE] [foo]
 * prog: error: argument -1: expected one argument
 * </pre>
 * 
 * <p>
 * If you have positional arguments that must begin with {@code -} and don't
 * look like negative numbers, you can insert the pseudo-argument {@code --}
 * which tells {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])} that everything after
 * that is a positional argument:
 * </p>
 * 
 * <pre>
 * $ java Demo -- -f
 * Namespace(one=null, foo=-f)
 * </pre>
 * 
 * <p>
 * Please note that whatever {@code prefixChars} is specified in
 * {@link net.sourceforge.argparse4j.ArgumentParsers#newArgumentParser(String, boolean, String)}, pseudo-argument is
 * {@code --}. After {@code --}, sub-command cannot be recognized.
 * </p>
 * 
 * <h5>The Namespace object</h5>
 * 
 * <p>
 * {@link net.sourceforge.argparse4j.inf.Namespace} object is used to store attributes as a result of
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])}. Currently, it is just a wrapper
 * to {@link java.util.Map} and several shortcut getter methods are provided. The actual
 * attributes are stored in {@link java.util.Map} object and can be retrieved using
 * {@link net.sourceforge.argparse4j.inf.Namespace#getAttrs()}.
 * </p>
 * 
 * <h4>Other utilities</h4>
 * 
 * <h5 id="ag4j_Sub-commands">Sub-commands</h5>
 * 
 * <p>
 * Many programs split up their functionality into a number of sub-commands, for
 * example, the {@code git} program can invoke sub-commands like
 * {@code git stash}, {@code git checkout} and {@code git commit}. Splitting up
 * functionality this way can be a particularly good idea when a program
 * performs several different functions which requires different kinds of
 * command-line arguments. {@link net.sourceforge.argparse4j.inf.ArgumentParser} supports the creation of such
 * sub-commands with the {@link net.sourceforge.argparse4j.inf.ArgumentParser#addSubparsers()}.
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#addSubparsers()} is normally called with no arguments
 * and returns {@link net.sourceforge.argparse4j.inf.Subparsers} object. This object has
 * {@link net.sourceforge.argparse4j.inf.Subparsers#addParser(String)}, which takes a command name and returns
 * {@link net.sourceforge.argparse4j.inf.Subparser} object. {@link net.sourceforge.argparse4j.inf.Subparser} object implements
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser}, but several methods are not supported.
 * </p>
 * 
 * <p>
 * Some example usage:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;--foo&quot;).action(Arguments.storeTrue()).help(&quot;foo help&quot;);
 *     Subparsers subparsers = parser.addSubparsers().help(&quot;sub-command help&quot;);
 * 
 *     Subparser parserA = subparsers.addParser(&quot;a&quot;).help(&quot;a help&quot;);
 *     parserA.addArgument(&quot;bar&quot;).type(Integer.class).help(&quot;bar help&quot;);
 * 
 *     Subparser parserB = subparsers.addParser(&quot;b&quot;).help(&quot;b help&quot;);
 *     parserB.addArgument(&quot;--baz&quot;).choices(&quot;X&quot;, &quot;Y&quot;, &quot;Z&quot;).help(&quot;baz help&quot;);
 *     try {
 *         System.out.println(parser.parseArgs(args));
 *     } catch (ArgumentParserException e) {
 *         parser.handleError(e);
 *         System.exit(1);
 *     }
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo a 12
 * Namespace(foo=false, bar=12)
 * $ java Demo --foo b --baz Z
 * Namespace(baz=Z, foo=true)
 * </pre>
 * 
 * <p>
 * Note that the object returned by {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])}
 * will only contain attributes for the main parser and the subparser that was
 * selected by the command line (and not any other subparsers). So in the
 * example above, when the {@code a} command is specified, only the {@code foo}
 * and {@code bar} attributes are present, and when the {@code b} command is
 * specified, only {@code foo} and {@code baz} attributes are present.
 * </p>
 * 
 * <p>
 * Similarly, when a help message is requested from a subparser, only the help
 * for that particular parser will be printed. The help message will not include
 * parent parser or sibling parser messages. (A help message for each subparser
 * command, however, can be given using {@link net.sourceforge.argparse4j.inf.Subparser#help(String)}.
 * </p>
 * 
 * <pre>
 * $ java Demo --help
 * usage: prog [-h] [--foo] {a,b} ...
 * 
 * positional arguments:
 *   {a,b}                  sub-command help
 *     a                    a help
 *     b                    b help
 * 
 * optional arguments:
 *   -h, --help             show this help message and exit
 *   --foo                  foo help
 * 
 * $ java Demo a --help
 * usage: prog a [-h] bar
 * 
 * positional arguments:
 *   bar                    bar help
 * 
 * optional arguments:
 *   -h, --help             show this help message and exit
 * 
 * $ java Demo b --help
 * usage: prog b [-h] [--baz BAZ]
 * 
 * optional arguments:
 *   -h, --help             show this help message and exit
 *   --baz BAZ              baz help
 * </pre>
 * 
 * <p>
 * {@link net.sourceforge.argparse4j.inf.Subparsers} also has {@link net.sourceforge.argparse4j.inf.Subparsers#title(String)} and
 * {@link net.sourceforge.argparse4j.inf.Subparsers#description(String)}. When either is present, the
 * subparser's commands will appear i their own group in the help output. For
 * example:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     Subparsers subparsers = parser.addSubparsers().title(&quot;subcommands&quot;)
 *             .description(&quot;valid subcommands&quot;).help(&quot;additional help&quot;);
 *     subparsers.addParser(&quot;foo&quot;);
 *     subparsers.addParser(&quot;bar&quot;);
 *     try {
 *         System.out.println(parser.parseArgs(args));
 *     } catch (ArgumentParserException e) {
 *         parser.handleError(e);
 *         System.exit(1);
 *     }
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo -h
 * usage: prog [-h] {foo,bar} ...
 * 
 * optional arguments:
 *   -h, --help             show this help message and exit
 * 
 * subcommands:
 *   valid subcommands
 * 
 *   {foo,bar}              additional help
 * </pre>
 * 
 * <p>
 * One particularly effective way of handling sub-commands is to combine the use
 * of {@link net.sourceforge.argparse4j.inf.Subparser#setDefault(String, Object)} so that each subparser knows
 * which function it should execute. For example:
 * </p>
 * 
 * <pre>
 * private static interface Accumulate {
 *     int accumulate(Collection&lt;Integer&gt; ints);
 * }
 * 
 * private static class Sum implements Accumulate {
 *     &#064;Override
 *     public int accumulate(Collection&lt;Integer&gt; ints) {
 *         int sum = 0;
 *         for (Integer i : ints) {
 *             sum += i;
 *         }
 *         return sum;
 *     }
 * 
 *     &#064;Override
 *     public String toString() {
 *         return getClass().getSimpleName();
 *     }
 * }
 * 
 * private static class Max implements Accumulate {
 *     &#064;Override
 *     public int accumulate(Collection&lt;Integer&gt; ints) {
 *         return Collections.max(ints);
 *     }
 * 
 *     &#064;Override
 *     public String toString() {
 *         return getClass().getSimpleName();
 *     }
 * }
 * 
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     Subparsers subparsers = parser.addSubparsers();
 *     Subparser parserSum = subparsers.addParser(&quot;sum&quot;).setDefault(&quot;func&quot;,
 *             new Sum());
 *     parserSum.addArgument(&quot;ints&quot;).type(Integer.class).nargs(&quot;*&quot;);
 *     Subparser parserMax = subparsers.addParser(&quot;max&quot;).setDefault(&quot;func&quot;,
 *             new Max());
 *     parserMax.addArgument(&quot;ints&quot;).type(Integer.class).nargs(&quot;+&quot;);
 *     try {
 *         Namespace res = parser.parseArgs(args);
 *         System.out.println(((Accumulate) res.get(&quot;func&quot;))
 *                 .accumulate((Collection&lt;Integer&gt;) res.get(&quot;ints&quot;)));
 *     } catch (ArgumentParserException e) {
 *         parser.handleError(e);
 *     }
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo  sum 1 3 2
 * 6
 * $ java Demo  max 1 3 2
 * 3
 * </pre>
 * 
 * <p>
 * The alternative way is use {@link net.sourceforge.argparse4j.inf.Subparsers#dest(String)}. With this dest
 * value, the selected command name is stored as an attribute:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     Subparsers subparsers = parser.addSubparsers().dest(&quot;subparser_name&quot;);
 *     Subparser subparser1 = subparsers.addParser(&quot;1&quot;);
 *     subparser1.addArgument(&quot;-x&quot;);
 *     Subparser subparser2 = subparsers.addParser(&quot;2&quot;);
 *     subparser2.addArgument(&quot;y&quot;);
 *     try {
 *         System.out.println(parser.parseArgs(args));
 *     } catch (ArgumentParserException e) {
 *         parser.handleError(e);
 *     }
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo 2 frobble
 * Namespace(subparser_name=2, y=frobble)
 * </pre>
 * 
 * <h5>Argument groups</h5>
 * 
 * <p>
 * By default, {@link net.sourceforge.argparse4j.inf.ArgumentParser} groups command line arguments into
 * "positional arguments" and "optional arguments" when displaying help
 * messages. When there is a better conceptual grouping of arguments than this
 * default one, appropriate groups can be created using the
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#addArgumentGroup(String)}:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     ArgumentGroup group = parser.addArgumentGroup(&quot;group&quot;);
 *     group.addArgument(&quot;--foo&quot;).help(&quot;foo help&quot;);
 *     group.addArgument(&quot;bar&quot;).help(&quot;bar help&quot;);
 *     try {
 *         System.out.println(parser.parseArgs(args));
 *     } catch (ArgumentParserException e) {
 *         parser.handleError(e);
 *     }
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo -h
 * usage: prog [-h] [--foo FOO] bar
 * 
 * positional arguments:
 * 
 * optional arguments:
 *   -h, --help             show this help message and exit
 * 
 * group:
 *   --foo FOO              foo help
 *   bar                    bar help
 * </pre>
 * 
 * <p>
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#addArgumentGroup(String)} returns {@link net.sourceforge.argparse4j.inf.ArgumentGroup}
 * object which has {@link net.sourceforge.argparse4j.inf.ArgumentGroup#addArgument(String...)} just like a
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser}. When an argument is added to the group, the parser
 * treats it just like a normal argument, but displays the argument in a
 * separate group for help messages. With the title string specified in
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#addArgumentGroup(String)} and the description specified
 * in {@link net.sourceforge.argparse4j.inf.ArgumentGroup#description(String)}, you can customize the help
 * message:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;, false);
 *     ArgumentGroup group1 = parser.addArgumentGroup(&quot;group1&quot;).description(
 *             &quot;group1 description&quot;);
 *     group1.addArgument(&quot;foo&quot;).help(&quot;foo help&quot;);
 *     ArgumentGroup group2 = parser.addArgumentGroup(&quot;group2&quot;).description(
 *             &quot;group2 description&quot;);
 *     group2.addArgument(&quot;--bar&quot;).help(&quot;bar help&quot;);
 *     parser.printHelp();
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo
 * usage: prog [--bar BAR] foo
 * 
 * group1:
 *   group1 description
 * 
 *   foo                    foo help
 * 
 * group2:
 *   group2 description
 * 
 *   --bar BAR              bar help
 * </pre>
 * 
 * <p>
 * Note that any arguments not in your user defined groups will end up back in
 * the usual "positional arguments" and "optional arguments" sections.
 * </p>
 * 
 * <h5>Parser defaults</h5>
 * 
 * <p>
 * Most of the time, the attributes of the object returned by
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])} will be fully determined by
 * inspecting the command line arguments and the argument actions.
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#setDefault(String, Object)} allows some additional
 * attributes that are determined without any inspection of the command line to
 * be added:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;foo&quot;).type(Integer.class);
 *     parser.setDefault(&quot;bar&quot;, 42).setDefault(&quot;baz&quot;, &quot;badger&quot;);
 *     try {
 *         System.out.println(parser.parseArgs(args));
 *     } catch (ArgumentParserException e) {
 *         parser.handleError(e);
 *     }
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo 736
 * Namespace(baz=badger, foo=736, bar=42)
 * </pre>
 * 
 * <p>
 * Note that parser-level defaults always override argument-level defaults:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;--foo&quot;).setDefault(&quot;bar&quot;);
 *     parser.setDefault(&quot;foo&quot;, &quot;spam&quot;);
 *     try {
 *         System.out.println(parser.parseArgs(args));
 *     } catch (ArgumentParserException e) {
 *         parser.handleError(e);
 *     }
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo
 * Namespace(foo=spam)
 * </pre>
 * 
 * <p>
 * Parser-level defaults can be particularly useful when working with multiple
 * parsers. See <a href="#ag4j_Sub-commands">Sub-commands</a> for an example of
 * this type.
 * </p>
 * 
 * <p>
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#getDefault(String)} returns the default value for a
 * attribute, as set by either {@link net.sourceforge.argparse4j.inf.Argument#setDefault(Object)} or by
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#setDefault(String, Object)}:
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) {
 *     ArgumentParser parser = ArgumentParsers.newArgumentParser(&quot;prog&quot;);
 *     parser.addArgument(&quot;--foo&quot;).setDefault(&quot;badger&quot;);
 *     System.out.println(parser.getDefault(&quot;foo&quot;));
 * }
 * </pre>
 * 
 * <pre>
 * $ java Demo
 * badger
 * </pre>
 * 
 * <h5>Printing help</h5>
 * 
 * <p>
 * In most typical applications, {@link net.sourceforge.argparse4j.inf.ArgumentParser#parseArgs(String[])} will
 * take care of formatting and printing any usage or error messages. However,
 * several formatting methods are available:
 * </p>
 * 
 * <ul>
 * <li>{@link net.sourceforge.argparse4j.inf.ArgumentParser#printUsage(java.io.PrintWriter)} - Print a brief
 * description of how the program should be invoked on the command line.
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#printUsage()} does the same thing but prints the
 * message in {@link java.lang.System#out}.</li>
 * <li>{@link net.sourceforge.argparse4j.inf.ArgumentParser#printHelp(java.io.PrintWriter)} - Print a help
 * message, including the program usage and information about the arguments
 * registered with {@link net.sourceforge.argparse4j.inf.ArgumentParser}. {@link net.sourceforge.argparse4j.inf.ArgumentParser#printHelp()}
 * does the same thing but prints the message in {@link java.lang.System#out}.</li>
 * </ul>
 * 
 * <p>
 * There are also variants of these methods that simply return a string instead
 * of printing it:
 * </p>
 * 
 * <ul>
 * <li>{@link net.sourceforge.argparse4j.inf.ArgumentParser#formatUsage()} - Returns a string message that
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#printUsage(java.io.PrintWriter)} writes to
 * {@link java.io.PrintWriter}</li>
 * <li>{@link net.sourceforge.argparse4j.inf.ArgumentParser#formatHelp()} - Returns a string message that
 * {@link net.sourceforge.argparse4j.inf.ArgumentParser#printHelp(java.io.PrintWriter)} writes to
 * {@link java.io.PrintWriter}.</li>
 * </ul>
 */
package net.sourceforge.argparse4j;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentChoice;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

