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

    public void verify(ArgumentParser parser, Argument arg, File file) throws
            ArgumentParserException {
        if (verifyIsAbsolute) {
            verifyIsAbsolute(parser, arg, file);
        }
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
        if (!file.exists()) {
            throwException(parser, arg, file, "fileNotFoundError");
        }
    }

    private void verifyNotExists(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        if (file.exists()) {
            throwException(parser, arg, file, "fileFoundError");
        }
    }

    private void verifyIsFile(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        if (!file.isFile()) {
            throwException(parser, arg, file, "notAFileError");
        }
    }

    private void verifyIsDirectory(ArgumentParser parser, Argument arg,
            File file) throws ArgumentParserException {
        if (!file.isDirectory()) {
            throwException(parser, arg, file, "notADirectoryError");
        }
    }

    private void verifyCanRead(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        if (!file.canRead()) {
            throwException(parser, arg, file,
                    "insufficientPermissionsToReadFileError");
        }
    }

    private void verifyCanWrite(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        if (!file.canWrite()) {
            throwException(parser, arg, file,
                    "insufficientPermissionsToWriteFileError");
        }
    }

    private void verifyCanWriteParent(ArgumentParser parser, Argument arg,
            File file) throws ArgumentParserException {
        File parent = file.getParentFile();
        if (parent == null || !parent.canWrite()) {
            throwException(parser, arg, file, "cannotWriteParentOfFileError");
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
        throwException(parser, arg, file, "cannotCreateFileError");
    }

    private void verifyCanExecute(ArgumentParser parser, Argument arg, File file)
            throws ArgumentParserException {
        if (!file.canExecute()) {
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
}
