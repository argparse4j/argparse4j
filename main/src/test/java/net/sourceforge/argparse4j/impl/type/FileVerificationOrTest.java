package net.sourceforge.argparse4j.impl.type;

import java.io.File;
import java.io.IOException;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileVerificationOrTest {
    private static final ArgumentParser SOME_PARSER = ArgumentParsers
            .newFor("FileVerificationOrTest").build();
    private static final Argument SOME_ARGUMENT = SOME_PARSER
            .addArgument("someargument");
    private static final File SOME_FILE = null;
    private static final File NOT_A_DIRECTORY = new File("does not exist");
    private static File writableFile;
    private static File nonWritableFile;

    @BeforeClass
    public static void createTestFiles() throws IOException {
        writableFile = File
                .createTempFile(FileVerificationOrTest.class.getSimpleName(),
                        ".txt");

        nonWritableFile = File
                .createTempFile(FileVerificationOrTest.class.getSimpleName(),
                        ".txt");
        nonWritableFile.setWritable(false);
    }

    @AfterClass
    public static void deleteTestFiles() {
        writableFile.delete();

        nonWritableFile.setWritable(true);
        nonWritableFile.delete();
    }

    @Test(expected = ArgumentParserException.class)
    public void verifyNotExistsFailsIfNoNextFileVerification() throws
            ArgumentParserException {
        FileVerification verification = new ExistingVerification();
        verification.verifyNotExists = true;

        verification.verify(SOME_PARSER, SOME_ARGUMENT, SOME_FILE);
    }

    @Test
    public void verifyNotExistsDoesNotFailIfNextFileVerification() throws
            ArgumentParserException {
        FileVerification verification = new ExistingVerification();
        verification.verifyNotExists = true;
        verification.or();

        verification.verify(SOME_PARSER, SOME_ARGUMENT, SOME_FILE);
    }

    @Test(expected = ArgumentParserException.class)
    public void verificationsAfterNotExistsAreRunIfNotExistsSucceeds() throws
            ArgumentParserException {
        FileVerification verification = new NotExistingNonreadableVerification();
        verification.verifyNotExists = true;
        verification.verifyCanRead = true;

        verification.verify(SOME_PARSER, SOME_ARGUMENT, SOME_FILE);
    }

    @Test
    public void verificationsAfterNotExistsAreNotRunIfNotExistsFailsAndNextFileVerification() throws
            ArgumentParserException {
        FileVerification verification = new ExistingNonreadableVerification();
        verification.verifyNotExists = true;
        verification.verifyCanRead = true;
        verification.or();

        verification.verify(SOME_PARSER, SOME_ARGUMENT, SOME_FILE);
    }

    @Test
    public void nextFileVerificationIsNotRunIfNotExistsSucceeds() throws
            ArgumentParserException {
        FileVerification verification = new NotExistingVerification();
        verification.verifyNotExists = true;

        FileVerification nextVerification = verification.or();
        nextVerification.verifyIsDirectory = true;

        verification.verify(SOME_PARSER, SOME_ARGUMENT, SOME_FILE);
    }

    @Test(expected = ArgumentParserException.class)
    public void nextFileVerificationIsRunIfNotExistsFails() throws
            ArgumentParserException {
        FileVerification verification = new ExistingVerification();
        verification.verifyNotExists = true;

        FileVerification nextVerification = verification.or();
        nextVerification.verifyIsDirectory = true;

        verification.verify(SOME_PARSER, SOME_ARGUMENT, NOT_A_DIRECTORY);
    }

    @Test(expected = ArgumentParserException.class)
    public void verifyCanWriteFailsIfNoNextFileVerification() throws
            ArgumentParserException {
        FileVerification verification = new ExistingVerification();
        verification.verifyCanWrite = true;

        verification.verify(SOME_PARSER, SOME_ARGUMENT, nonWritableFile);
    }

    @Test
    public void verifyCanWriteDoesNotFailIfNextFileVerification() throws
            ArgumentParserException {
        FileVerification verification = new ExistingVerification();
        verification.verifyCanWrite = true;
        verification.or();

        verification.verify(SOME_PARSER, SOME_ARGUMENT, nonWritableFile);
    }

    @Test
    public void nextFileVerificationIsNotRunIfCanWriteSucceeds() throws
            ArgumentParserException {
        FileVerification verification = new NotExistingVerification();
        verification.verifyCanWrite = true;

        FileVerification nextVerification = verification.or();
        nextVerification.verifyIsDirectory = true;

        verification.verify(SOME_PARSER, SOME_ARGUMENT, writableFile);
    }

    @Test(expected = ArgumentParserException.class)
    public void nextFileVerificationIsRunIfCanWriteFails() throws
            ArgumentParserException {
        FileVerification verification = new ExistingVerification();
        verification.verifyCanWrite = true;

        FileVerification nextVerification = verification.or();
        nextVerification.verifyIsDirectory = true;

        verification.verify(SOME_PARSER, SOME_ARGUMENT, nonWritableFile);
    }
}
