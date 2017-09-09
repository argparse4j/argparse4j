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
package net.sourceforge.argparse4j.impl.type;

import java.io.File;
import java.io.IOException;

import net.sourceforge.argparse4j.helper.MessageLocalization;
import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

/**
 * <p>
 * Verifies properties of files. This can be used by argument types to add extra
 * validation to the file or directory paths that they accept.
 * </p>
 * <p>
 * To use it, create an instance, enable the desired verifications and call
 * {@link #verify(ArgumentParser, Argument, File)}.
 * </p>
 *
 * @since 0.8.0
 */
public class FileVerification {
    public boolean verifyExists = false;
    public boolean verifyNotExists = false;
    public boolean verifyIsFile = false;
    public boolean verifyIsDirectory = false;
    public boolean verifyCanRead = false;
    public boolean verifyCanWrite = false;
    public boolean verifyCanWriteParent = false;
    public boolean verifyCanCreate = false;
    public boolean verifyCanExecute = false;
    public boolean verifyIsAbsolute = false;
    private FileVerification nextFileVerification;
    
    public FileVerification or() {
        nextFileVerification = new FileVerification();
        return nextFileVerification;
    }

    public void verify(ArgumentParser parser, Argument arg, File file) throws
            ArgumentParserException {
        if (verifyIsAbsolute) {
            verifyIsAbsolute(parser, arg, file);
        }
        
        boolean mustContinueWithNextFileVerification;
        try {
            verifyPresenceAndType(parser, arg, file);
            mustContinueWithNextFileVerification = false;
        } catch (ArgumentParserException e) {
            if (nextFileVerification == null) {
                throw e;
            } else {
                mustContinueWithNextFileVerification = true;
            }
        }
        
        if (mustContinueWithNextFileVerification) {
            nextFileVerification.verify(parser, arg, file);
        } else {
            verifyPermissions(parser, arg, file);
        }
    }

    private void verifyPresenceAndType(ArgumentParser parser, Argument arg,
            File file) throws ArgumentParserException {
        if (verifyExists) {
            verifyExists(parser, arg, file);
        }
        if (verifyNotExists) {
            verifyNotExists(parser, arg, file);
        }
        if (verifyIsFile) {
            verifyIsFile(parser, arg, file);
        }
        if (verifyIsDirectory) {
            verifyIsDirectory(parser, arg, file);
        }
    }

    private void verifyPermissions(ArgumentParser parser, Argument arg,
            File file) throws ArgumentParserException {
        if (verifyCanRead) {
            verifyCanRead(parser, arg, file);
        }
        if (verifyCanWrite) {
            verifyCanWrite(parser, arg, file);
        }
        if (verifyCanWriteParent) {
            verifyCanWriteParent(parser, arg, file);
        }
        if (verifyCanCreate) {
            verifyCanCreate(parser, arg, file);
        }
        if (verifyCanExecute) {
            verifyCanExecute(parser, arg, file);
        }
    }

    private void verifyExists(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        if (!exists(file)) {
            throwException(parser, arg, file, "fileNotFoundError");
        }
    }

    private void verifyNotExists(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        if (exists(file)) {
            throwException(parser, arg, file, "fileFoundError");
        }
    }

    private void verifyIsFile(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        if (!isFile(file)) {
            throwException(parser, arg, file, "notAFileError");
        }
    }

    private void verifyIsDirectory(ArgumentParser parser, Argument arg,
            File file) throws ArgumentParserException {
        if (!isDirectory(file)) {
            throwException(parser, arg, file, "notADirectoryError");
        }
    }

    private void verifyCanRead(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        if (!canRead(file)) {
            throwException(parser, arg, file,
                    "insufficientPermissionsToReadFileError");
        }
    }

    private void verifyCanWrite(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        if (!canWrite(file)) {
            throwException(parser, arg, file,
                    "insufficientPermissionsToWriteFileError");
        }
    }

    private void verifyCanWriteParent(ArgumentParser parser, Argument arg,
            File file) throws ArgumentParserException {
        File parent = file.getParentFile();
        if (parent == null || !canWrite(parent)) {
            throwException(parser, arg, file, "cannotWriteParentOfFileError");
        }
    }

    private void verifyCanCreate(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        try {
            File parent = file.getCanonicalFile().getParentFile();
            if (parent != null && canWrite(parent)) {
                return;
            }
        } catch (IOException e) {
            // No action needed. Throw an exception indicating creation is not possible later.
        }

        // An exception was thrown or the parent directory can't be written
        throwException(parser, arg, file, "cannotCreateFileError");
    }

    private void verifyCanExecute(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        if (!canExecute(file)) {
            throwException(parser, arg, file,
                    "insufficientPermissionsToExecuteFileError");
        }
    }

    private void verifyIsAbsolute(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        if (!file.isAbsolute()) {
            throwException(parser, arg, file, "notAnAbsoluteFileError");
        }
    }

    private void throwException(ArgumentParser parser, Argument arg, File file,
            String messageKey)
            throws ArgumentParserException {
        throw new ArgumentParserException(
                String.format(TextHelper.LOCALE_ROOT,
                        MessageLocalization.localize(
                                parser.getConfig().getResourceBundle(),
                                messageKey),
                        file),
                parser, arg);
    }

    // Methods to allow mocking the return values of "File" methods.
    
    protected boolean exists(File file) {
        return file.exists();
    }

    protected boolean isDirectory(File file) {
        return file.isDirectory();
    }

    protected boolean isFile(File file) {
        return file.isFile();
    }

    protected boolean canRead(File file) {
        return file.canRead();
    }

    protected boolean canWrite(File file) {
        return file.canWrite();
    }

    protected boolean canExecute(File file) {
        return file.canExecute();
    }
}
