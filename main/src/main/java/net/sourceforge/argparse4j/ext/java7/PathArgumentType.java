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
package net.sourceforge.argparse4j.ext.java7;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import net.sourceforge.argparse4j.helper.MessageLocalization;
import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.impl.type.FileVerification;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

/**
 * <p>
 * ArgumentType subclass for Path type, using fluent style API.
 * </p>
 *
 * <p>
 * This object can convert path string to {@link Path} object. The
 * command-line programs traditionally accept the file path "-" as standard
 * input. This object supports this when
 * {@link PathArgumentType#acceptSystemIn()} is used. Also there are several
 * convenient verification features such as checking readability or existence.
 * </p>
 *
 * @since 0.8.0
 */
public class PathArgumentType implements ArgumentType<Path> {

    private final FileSystem fileSystem;
    private boolean acceptSystemIn = false;
    private final FileVerification firstFileVerification = new FileVerification();
    private FileVerification currentFileVerification = firstFileVerification;

    /**
     * Create an instance using the default file system for resolving the path.
     */
    public PathArgumentType() {
        fileSystem = FileSystems.getDefault();
    }

    /**
     * <p>
     * Create an instance using the given file system for resolving the path.
     * </p>
     * <p>
     * <strong>Warning</strong>: Using the non-default file system disables all
     * file verifications.
     * </p>
     *
     * @param fileSystem
     *         The file system to use for resolving paths
     */
    public PathArgumentType(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
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
        currentFileVerification.verifyExists = true;
        return this;
    }

    /**
     * Verifies that the specified path does not exist. If the verification
     * fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyNotExists() {
        currentFileVerification.verifyNotExists = true;
        return this;
    }

    /**
     * Verifies that the specified path is a regular file. If the verification
     * fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyIsFile() {
        currentFileVerification.verifyIsFile = true;
        return this;
    }

    /**
     * Verifies that the specified path is a directory. If the verification
     * fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyIsDirectory() {
        currentFileVerification.verifyIsDirectory = true;
        return this;
    }

    /**
     * Verifies that the specified path is readable. If the verification fails,
     * error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyCanRead() {
        currentFileVerification.verifyCanRead = true;
        return this;
    }

    /**
     * Verifies that the specified path is writable. If the verification fails,
     * error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyCanWrite() {
        currentFileVerification.verifyCanWrite = true;
        return this;
    }

    /**
     * Verifies that the parent directory of the specified path is writable. If
     * the verification fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyCanWriteParent() {
        currentFileVerification.verifyCanWriteParent = true;
        return this;
    }

    /**
     * Verifies that the specified path is writable. If the verification fails,
     * error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyCanCreate() {
        currentFileVerification.verifyCanCreate = true;
        return this;
    }

    /**
     * Verifies that the specified path is executable. If the verification
     * fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyCanExecute() {
        currentFileVerification.verifyCanExecute = true;
        return this;
    }

    /**
     * Verifies that the specified path is an absolute path. If the verification
     * fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyIsAbsolute() {
        currentFileVerification.verifyIsAbsolute = true;
        return this;
    }

    /**
     * Start a new verification group. Of all verification groups at least 1
     * must be verified successfully for the path to be accepted.
     *
     * @return this
     */
    public PathArgumentType or() {
        currentFileVerification = currentFileVerification.or();
        return this;
    }

    @Override
    public Path convert(ArgumentParser parser, Argument arg,
            String value) throws ArgumentParserException {
        Path path;
        try {
            path = fileSystem.getPath(value);
        } catch (InvalidPathException e) {
            String localizedTypeName = Java7ExtensionResourceBundle
                    .get(parser.getConfig().getLocale()).getString("path");
            throw new ArgumentParserException(
                    String.format(TextHelper.LOCALE_ROOT, MessageLocalization
                                    .localize(parser.getConfig().getResourceBundle(),
                                            "couldNotConvertToError"), value,
                            localizedTypeName), e.getCause(), parser, arg);

        }

        try {
            File file = path.toFile();
            if (!isSystemIn(value)) {
                firstFileVerification.verify(parser, arg, file);
            }
        } catch (UnsupportedOperationException e) {
            // Ignore: Not the default file system provider, so conversion to a
            // file is not possible. Simply skip the file verifications.
        }
        return path;
    }

    private boolean isSystemIn(String path) {
        return acceptSystemIn && path.equals("-");
    }
}
