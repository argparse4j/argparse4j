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
    private final FileVerification firstFileVerification = new FileVerification();
    private FileVerification currentFileVerification = firstFileVerification;

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
        currentFileVerification.verifyExists = true;
        return this;
    }

    /**
     * Verifies that the specified path does not exist. If the verification
     * fails, error will be reported.
     * 
     * @return this
     */
    public FileArgumentType verifyNotExists() {
        currentFileVerification.verifyNotExists = true;
        return this;
    }

    /**
     * Verifies that the specified path is a regular file. If the verification
     * fails, error will be reported.
     * 
     * @return this
     */
    public FileArgumentType verifyIsFile() {
        currentFileVerification.verifyIsFile = true;
        return this;
    }

    /**
     * Verifies that the specified path is a directory. If the verification
     * fails, error will be reported.
     * 
     * @return this
     */
    public FileArgumentType verifyIsDirectory() {
        currentFileVerification.verifyIsDirectory = true;
        return this;
    }

    /**
     * Verifies that the specified path is readable. If the verification fails,
     * error will be reported.
     * 
     * @return this
     */
    public FileArgumentType verifyCanRead() {
        currentFileVerification.verifyCanRead = true;
        return this;
    }

    /**
     * Verifies that the specified path is writable. If the verification fails,
     * error will be reported.
     * 
     * @return this
     */
    public FileArgumentType verifyCanWrite() {
        currentFileVerification.verifyCanWrite = true;
        return this;
    }

    /**
     * Verifies that the parent directory of the specified path is writable. If
     * the verification fails, error will be reported.
     * 
     * @return this
     */
    public FileArgumentType verifyCanWriteParent() {
        currentFileVerification.verifyCanWriteParent = true;
        return this;
    }

    /**
     * Verifies that the specified path is writable. If the verification fails,
     * error will be reported.
     * 
     * @return this
     */
    public FileArgumentType verifyCanCreate() {
        currentFileVerification.verifyCanCreate = true;
        return this;
    }

    /**
     * Verifies that the specified path is executable. If the verification
     * fails, error will be reported.
     * 
     * @return this
     */
    public FileArgumentType verifyCanExecute() {
        currentFileVerification.verifyCanExecute = true;
        return this;
    }

    /**
     * Verifies that the specified path is an absolute path. If the verification
     * fails, error will be reported.
     * 
     * @return this
     */
    public FileArgumentType verifyIsAbsolute() {
        currentFileVerification.verifyIsAbsolute = true;
        return this;
    }

    /**
     * Start a new verification group. Of all verification groups at least 1
     * must be verified successfully for the file to be accepted.
     *
     * @return this
     */
    public FileArgumentType or() {
        currentFileVerification = currentFileVerification.or();
        return this;
    }

    @Override
    public File convert(ArgumentParser parser, Argument arg, String value)
            throws ArgumentParserException {
        File file = new File(value);
        if (!isSystemIn(file)) {
            firstFileVerification.verify(parser, arg, file);
        }
        return file;
    }

    private boolean isSystemIn(File file) {
        return acceptSystemIn && file.getPath().equals("-");
    }

}
