package net.sourceforge.argparse4j.impl.type;

import java.io.File;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

import org.junit.Test;

public class FileVerificationOrTest {
    private static final ArgumentParser SOME_PARSER = ArgumentParsers
            .newFor("FileVerificationOrTest").build();
    private static final Argument SOME_ARGUMENT = SOME_PARSER
            .addArgument("someargument");
    private static final File SOME_FILE = null;
    private static final File NOT_A_DIRECTORY = new File("does not exist");

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
}
