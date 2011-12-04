package net.sourceforge.argparse4j.internal;

import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

/**
 * Exception thrown when unrecognized argument is encountered.
 * 
 */
public class UnrecognizedArgumentException extends ArgumentParserException {

    /**
     * 
     */
    private static final long serialVersionUID = -256412358164687976L;
    private String argument_;

    public UnrecognizedArgumentException(String message, ArgumentParser parser,
            String argument) {
        super(message, parser);
        argument_ = argument;
    }

    public String getArgument() {
        return argument_;
    }
}
