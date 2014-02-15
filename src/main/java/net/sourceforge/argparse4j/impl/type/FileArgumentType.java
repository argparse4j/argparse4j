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
package net.sourceforge.argparse4j.impl.type;

import java.io.File;
import java.io.IOException;

import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

/**
 * ArgumentType subclass for File type, using fluent style API.
 * 
 * This object can convert path string to {@link java.io.File} object. The
 * command-line programs traditionally accept the file path "-" as standard
 * input. This object supports this when
 * {@link FileArgumentType#acceptSystemIn()} is used. Also there are several
 * convenient verification features such as checking readability or existence.
 */
public class FileArgumentType implements ArgumentType<File> {

    private boolean acceptSystemIn = false;
    private boolean verifyExists = false;
    private boolean verifyNotExists = false;
    private boolean verifyIsFile = false;
    private boolean verifyIsDirectory = false;
    private boolean verifyCanRead = false;
    private boolean verifyCanWrite = false;
    private boolean verifyCanWriteParent = false;
    private boolean verifyCanCreate = false;
    private boolean verifyCanExecute = false;
    private boolean verifyIsAbsolute = false;

    public FileArgumentType() {
    }

    /**
     * If the argument is "-", accept it as standard input. If this method is
     * used, all verification methods will be ignored.
     * 
     * @return this
     */
    public FileArgumentType acceptSystemIn() {
        acceptSystemIn = true;
        return this;
    }

    /**
     * Verifies that the specified path exists. If the verification fails, error
     * will be reported.
     * 
     * @return this
     */
    public FileArgumentType verifyExists() {
        verifyExists = true;
        return this;
    }

    /**
     * Verifies that the specified path does not exist. If the verification
     * fails, error will be reported.
     * 
     * @return this
     */
    public FileArgumentType verifyNotExists() {
        verifyNotExists = true;
        return this;
    }

    /**
     * Verifies that the specified path is a regular file. If the verification
     * fails, error will be reported.
     * 
     * @return this
     */
    public FileArgumentType verifyIsFile() {
        verifyIsFile = true;
        return this;
    }

    /**
     * Verifies that the specified path is a directory. If the verification
     * fails, error will be reported.
     * 
     * @return this
     */
    public FileArgumentType verifyIsDirectory() {
        verifyIsDirectory = true;
        return this;
    }

    /**
     * Verifies that the specified path is readable. If the verification fails,
     * error will be reported.
     * 
     * @return this
     */
    public FileArgumentType verifyCanRead() {
        verifyCanRead = true;
        return this;
    }

    /**
     * Verifies that the specified path is writable. If the verification fails,
     * error will be reported.
     * 
     * @return this
     */
    public FileArgumentType verifyCanWrite() {
        verifyCanWrite = true;
        return this;
    }

    /**
     * Verifies that the parent directory of the specified path is writable. If
     * the verification fails, error will be reported.
     * 
     * @return this
     */
    public FileArgumentType verifyCanWriteParent() {
        verifyCanWriteParent = true;
        return this;
    }

    /**
     * Verifies that the specified path is writable. If the verification fails,
     * error will be reported.
     * 
     * @return this
     */
    public FileArgumentType verifyCanCreate() {
        verifyCanCreate = true;
        return this;
    }

    /**
     * Verifies that the specified path is executable. If the verification
     * fails, error will be reported.
     * 
     * @return this
     */
    public FileArgumentType verifyCanExecute() {
        verifyCanExecute = true;
        return this;
    }

    /**
     * Verifies that the specified path is an absolute path. If the verification
     * fails, error will be reported.
     * 
     * @return this
     */
    public FileArgumentType verifyIsAbsolute() {
        verifyIsAbsolute = true;
        return this;
    }

    @Override
    public File convert(ArgumentParser parser, Argument arg, String value)
            throws ArgumentParserException {
        File file = new File(value);
        if (verifyIsAbsolute && !isSystemIn(file)) {
            verifyIsAbsolute(parser, arg, file);
        }
        if (verifyExists && !isSystemIn(file)) {
            verifyExists(parser, arg, file);
        }
        if (verifyNotExists && !isSystemIn(file)) {
            verifyNotExists(parser, arg, file);
        }
        if (verifyIsFile && !isSystemIn(file)) {
            verifyIsFile(parser, arg, file);
        }
        if (verifyIsDirectory && !isSystemIn(file)) {
            verifyIsDirectory(parser, arg, file);
        }
        if (verifyCanRead && !isSystemIn(file)) {
            verifyCanRead(parser, arg, file);
        }
        if (verifyCanWrite && !isSystemIn(file)) {
            verifyCanWrite(parser, arg, file);
        }
        if (verifyCanWriteParent && !isSystemIn(file)) {
            verifyCanWriteParent(parser, arg, file);
        }
        if (verifyCanCreate && !isSystemIn(file)) {
            verifyCanCreate(parser, arg, file);
        }
        if (verifyCanExecute && !isSystemIn(file)) {
            verifyCanExecute(parser, arg, file);
        }
        return file;
    }

    private void verifyExists(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        if (!file.exists()) {
            throw new ArgumentParserException(String.format(
                    TextHelper.LOCALE_ROOT, "File not found: '%s'", file),
                    parser, arg);
        }
    }

    private void verifyNotExists(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        if (file.exists()) {
            throw new ArgumentParserException(String.format(
                    TextHelper.LOCALE_ROOT, "File found: '%s'", file), parser,
                    arg);
        }
    }

    private void verifyIsFile(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        if (!file.isFile()) {
            throw new ArgumentParserException(String.format(
                    TextHelper.LOCALE_ROOT, "Not a file: '%s'", file), parser,
                    arg);
        }
    }

    private void verifyIsDirectory(ArgumentParser parser, Argument arg,
            File file) throws ArgumentParserException {
        if (!file.isDirectory()) {
            throw new ArgumentParserException(String.format(
                    TextHelper.LOCALE_ROOT, "Not a directory: '%s'", file),
                    parser, arg);
        }
    }

    private void verifyCanRead(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        if (!file.canRead()) {
            throw new ArgumentParserException(String.format(
                    TextHelper.LOCALE_ROOT,
                    "Insufficient permissions to read file: '%s'", file),
                    parser, arg);
        }
    }

    private void verifyCanWrite(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        if (!file.canWrite()) {
            throw new ArgumentParserException(String.format(
                    TextHelper.LOCALE_ROOT,
                    "Insufficient permissions to write file: '%s'", file),
                    parser, arg);
        }
    }

    private void verifyCanWriteParent(ArgumentParser parser, Argument arg,
            File file) throws ArgumentParserException {
        File parent = file.getParentFile();
        if (parent == null || !parent.canWrite()) {
            throw new ArgumentParserException(String.format(
                    TextHelper.LOCALE_ROOT,
                    "Cannot write parent of file: '%s'", file), parser, arg);
        }
    }

    private void verifyCanCreate(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        try {
            File parent = file.getCanonicalFile().getParentFile();
            if (parent != null && parent.canWrite()) {
                return;
            }
        } catch (IOException e) {
        }

        // An exception was thrown or the parent directory can't be written
        throw new ArgumentParserException(String.format(TextHelper.LOCALE_ROOT,
                "Cannot create file: '%s'", file), parser, arg);

    }

    private void verifyCanExecute(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        if (!file.canExecute()) {
            throw new ArgumentParserException(String.format(
                    TextHelper.LOCALE_ROOT,
                    "Insufficient permissions to execute file: '%s'", file),
                    parser, arg);
        }
    }

    private void verifyIsAbsolute(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        if (!file.isAbsolute()) {
            throw new ArgumentParserException(
                    String.format(TextHelper.LOCALE_ROOT,
                            "Not an absolute file: '%s'", file), parser, arg);
        }
    }

    private boolean isSystemIn(File file) {
        return acceptSystemIn && file.getPath().equals("-");
    }

}
