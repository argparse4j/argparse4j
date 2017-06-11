/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
 * This object can convert path string to {@link java.nio.file.Path} object. The
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
    private final FileVerification fileVerification = new FileVerification();

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
        fileVerification.verifyExists = true;
        return this;
    }

    /**
     * Verifies that the specified path does not exist. If the verification
     * fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyNotExists() {
        fileVerification.verifyNotExists = true;
        return this;
    }

    /**
     * Verifies that the specified path is a regular file. If the verification
     * fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyIsFile() {
        fileVerification.verifyIsFile = true;
        return this;
    }

    /**
     * Verifies that the specified path is a directory. If the verification
     * fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyIsDirectory() {
        fileVerification.verifyIsDirectory = true;
        return this;
    }

    /**
     * Verifies that the specified path is readable. If the verification fails,
     * error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyCanRead() {
        fileVerification.verifyCanRead = true;
        return this;
    }

    /**
     * Verifies that the specified path is writable. If the verification fails,
     * error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyCanWrite() {
        fileVerification.verifyCanWrite = true;
        return this;
    }

    /**
     * Verifies that the parent directory of the specified path is writable. If
     * the verification fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyCanWriteParent() {
        fileVerification.verifyCanWriteParent = true;
        return this;
    }

    /**
     * Verifies that the specified path is writable. If the verification fails,
     * error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyCanCreate() {
        fileVerification.verifyCanCreate = true;
        return this;
    }

    /**
     * Verifies that the specified path is executable. If the verification
     * fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyCanExecute() {
        fileVerification.verifyCanExecute = true;
        return this;
    }

    /**
     * Verifies that the specified path is an absolute path. If the verification
     * fails, error will be reported.
     *
     * @return this
     */
    public PathArgumentType verifyIsAbsolute() {
        fileVerification.verifyIsAbsolute = true;
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
                fileVerification.verify(parser, arg, file);
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
