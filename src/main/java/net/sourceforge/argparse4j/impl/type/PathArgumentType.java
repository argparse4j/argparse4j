package net.sourceforge.argparse4j.impl.type;

import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ArgumentType subclass for File type, using fluent style API.
 * <p>
 * This object can convert path string to {@link java.nio.file.Path} object. The
 * command-line programs traditionally accept the file path "-" as standard
 * input. This object supports this when
 * {@link PathArgumentType#acceptSystemIn()} is used. Also there are several
 * convenient verification features such as checking readability or existence.
 */
public class PathArgumentType implements ArgumentType<Path> {

    private boolean acceptSystemIn = false;
    private boolean verifyExists = false;
    private boolean verifyIsEmpty = false;
    private boolean verifyNotExists = false;
    private boolean verifyIsFile = false;
    private boolean verifyIsDirectory = false;
    private boolean verifyCanRead = false;
    private boolean verifyCanWrite = false;
    private boolean verifyCanWriteParent = false;
    private boolean verifyCanCreate = false;
    private boolean verifyCanExecute = false;
    private boolean verifyIsAbsolute = false;
    private boolean verifyMinDepth = false;

    private int pathMinDepth = 1;

    public PathArgumentType() {
    }

    /**
     * If the argument is "-", accept it as standard input. If this method is
     * used, all verification methods will be ignored.
     *
     * @return this
     */
    public PathArgumentType acceptSystemIn() {
        acceptSystemIn = true;
        return this;
    }

    /**
     * Verifies that the specified path exists. If the verification fails, error
     * will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyExists() {
        verifyExists = true;
        return this;
    }

    /**
     * Verifies that the specified path does not exist. If the verification
     * fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyNotExists() {
        verifyNotExists = true;
        return this;
    }

    /**
     * Verifies that the specified path is a regular file. If the verification
     * fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyIsFile() {
        verifyIsFile = true;
        return this;
    }

    /**
     * Verifies that the specified path is a directory. If the verification
     * fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyIsDirectory() {
        verifyIsDirectory = true;
        return this;
    }

    /**
     * Verifies that the specified path is readable. If the verification fails,
     * error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyCanRead() {
        verifyCanRead = true;
        return this;
    }

    /**
     * Verifies that the specified path is writable. If the verification fails,
     * error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyCanWrite() {
        verifyCanWrite = true;
        return this;
    }

    /**
     * Verifies that the parent directory of the specified path is writable. If
     * the verification fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyCanWriteParent() {
        verifyCanWriteParent = true;
        return this;
    }

    /**
     * Verifies that the specified path is writable. If the verification fails,
     * error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyCanCreate() {
        verifyCanCreate = true;
        return this;
    }

    /**
     * Verifies that the specified path is executable. If the verification
     * fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyCanExecute() {
        verifyCanExecute = true;
        return this;
    }

    /**
     * Verifies that the specified path is an absolute path. If the verification
     * fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyIsAbsolute() {
        verifyIsAbsolute = true;
        return this;
    }

    /**
     * Verifies that the specified path conforms with minimal depth requirement. If the verification
     * fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyMinDepth() {
        verifyMinDepth = true;
        return this;
    }

    /**
     * Verifies that the specified path meets minimal depth requirement. Otherwise, error will be
     * reported.
     *
     * @param pathMinDepth path <code>min</code> depth
     * @return this
     */
    public PathArgumentType withPathMinDepth(int pathMinDepth) {
        this.pathMinDepth = pathMinDepth;
        return this;
    }

    /**
     * Verifies that the specified path or file is empty. If the verification
     * fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyIsEmpty() {
        verifyIsEmpty = true;
        return this;
    }

    public Path convert(ArgumentParser parser, Argument arg, String value)
            throws ArgumentParserException {
        // Converts path to normalized path.
        Path path = Paths.get(value).normalize();
        if (verifyIsAbsolute && !isSystemIn(path)) {
            verifyIsAbsolute(parser, arg, path);
        }
        if (verifyExists && !isSystemIn(path)) {
            verifyExists(parser, arg, path);
        }
        if (verifyNotExists && !isSystemIn(path)) {
            verifyNotExists(parser, arg, path);
        }
        if (verifyIsFile && !isSystemIn(path)) {
            verifyIsFile(parser, arg, path);
        }
        if (verifyIsDirectory && !isSystemIn(path)) {
            verifyIsDirectory(parser, arg, path);
        }
        if (verifyCanRead && !isSystemIn(path)) {
            verifyCanRead(parser, arg, path);
        }
        if (verifyCanWrite && !isSystemIn(path)) {
            verifyCanWrite(parser, arg, path);
        }
        if (verifyCanWriteParent && !isSystemIn(path)) {
            verifyCanWriteParent(parser, arg, path);
        }
        if (verifyCanCreate && !isSystemIn(path)) {
            verifyCanCreate(parser, arg, path);
        }
        if (verifyCanExecute && !isSystemIn(path)) {
            verifyCanExecute(parser, arg, path);
        }
        if (verifyMinDepth && !isSystemIn(path)) {
            verifyMinDepth(parser, arg, path);
        }
        if (verifyIsEmpty && !isSystemIn(path)) {
            verifyIsEmpty(parser, arg, path);
        }
        return path;
    }

    private void verifyExists(ArgumentParser parser, Argument arg, Path path)
            throws ArgumentParserException {
        if (Files.notExists(path)) {
            throw new ArgumentParserException(String.format(
                    TextHelper.LOCALE_ROOT, "Path not found: '%s'", path),
                    parser, arg);
        }
    }

    private void verifyNotExists(ArgumentParser parser, Argument arg, Path path)
            throws ArgumentParserException {
        if (Files.exists(path)) {
            throw new ArgumentParserException(String.format(
                    TextHelper.LOCALE_ROOT, "Path found: '%s'", path), parser,
                    arg);
        }
    }

    private void verifyIsFile(ArgumentParser parser, Argument arg, Path path)
            throws ArgumentParserException {
        if (!Files.isRegularFile(path)) {
            throw new ArgumentParserException(String.format(
                    TextHelper.LOCALE_ROOT, "Not a file: '%s'", path), parser,
                    arg);
        }
    }

    private void verifyIsDirectory(ArgumentParser parser, Argument arg,
                                   Path path) throws ArgumentParserException {
        if (!Files.isDirectory(path)) {
            throw new ArgumentParserException(String.format(
                    TextHelper.LOCALE_ROOT, "Not a directory: '%s'", path),
                    parser, arg);
        }
    }

    private void verifyCanRead(ArgumentParser parser, Argument arg, Path path)
            throws ArgumentParserException {
        if (!Files.isReadable(path)) {
            throw new ArgumentParserException(String.format(
                    TextHelper.LOCALE_ROOT,
                    "Insufficient permissions to read path: '%s'", path),
                    parser, arg);
        }
    }

    private void verifyCanWrite(ArgumentParser parser, Argument arg, Path path)
            throws ArgumentParserException {
        if (!Files.isWritable(path)) {
            throw new ArgumentParserException(String.format(
                    TextHelper.LOCALE_ROOT,
                    "Insufficient permissions to write path: '%s'", path),
                    parser, arg);
        }
    }

    private void verifyCanWriteParent(ArgumentParser parser, Argument arg,
                                      Path path) throws ArgumentParserException {
        Path parent = path.getParent();
        if (parent == null || !Files.isWritable(parent)) {
            throw new ArgumentParserException(String.format(
                    TextHelper.LOCALE_ROOT,
                    "Cannot write parent of path: '%s'", path), parser, arg);
        }
    }

    private void verifyCanCreate(ArgumentParser parser, Argument arg, Path path)
            throws ArgumentParserException {
        Path parent = path.getParent();
        if (parent != null && Files.isWritable(parent)) {
            return;
        }
        // An exception was thrown or the parent directory can't be written
        throw new ArgumentParserException(String.format(TextHelper.LOCALE_ROOT,
                "Cannot create file: '%s'", path), parser, arg);

    }

    private void verifyCanExecute(ArgumentParser parser, Argument arg, Path path)
            throws ArgumentParserException {
        if (!Files.isExecutable(path)) {
            throw new ArgumentParserException(String.format(
                    TextHelper.LOCALE_ROOT,
                    "Insufficient permissions to execute file: '%s'", path),
                    parser, arg);
        }
    }

    private void verifyIsAbsolute(ArgumentParser parser, Argument arg, Path path)
            throws ArgumentParserException {
        if (!path.isAbsolute()) {
            throw new ArgumentParserException(
                    String.format(TextHelper.LOCALE_ROOT,
                            "Not an absolute file: '%s'", path), parser, arg);
        }
    }

    private void verifyMinDepth(ArgumentParser parser, Argument arg, Path path)
            throws ArgumentParserException {
        if (path.getNameCount() < pathMinDepth) {
            throw new ArgumentParserException(
                    String.format(TextHelper.LOCALE_ROOT,
                            "Does not meet depth=%d requirement for path: '%s' ", pathMinDepth, path), parser, arg);
        }
    }

    private void verifyIsEmpty(ArgumentParser parser, Argument arg, Path path)
            throws ArgumentParserException {
        try {
            if (Files.isDirectory(path) && Files.newDirectoryStream(path).iterator().hasNext()) {
                throw new ArgumentParserException(String.format(
                        TextHelper.LOCALE_ROOT, "Path is not empty: '%s' ", path),
                        parser, arg);
            }
            if (Files.size(path) > 0) {
                throw new ArgumentParserException(String.format(
                        TextHelper.LOCALE_ROOT, "File is not empty: '%s' ", path),
                        parser, arg);
            }
        } catch (IOException e) {
            throw new ArgumentParserException(String.format(
                    TextHelper.LOCALE_ROOT, "%s", e.getMessage()),
                    parser, arg);
        }
    }

    private boolean isSystemIn(Path path) {
        return acceptSystemIn && path.toString().equals("-");
    }
}
