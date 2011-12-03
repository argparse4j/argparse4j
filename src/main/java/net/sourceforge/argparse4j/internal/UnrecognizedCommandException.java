package net.sourceforge.argparse4j.internal;

import net.sourceforge.argparse4j.inf.ArgumentParserException;

/**
 * Exception thrown when unrecognized command is encountered.
 *
 */
public class UnrecognizedCommandException extends ArgumentParserException {

    /**
     * 
     */
    private static final long serialVersionUID = 2733149394568914256L;
    private String command_;
    
    public UnrecognizedCommandException(String message, String command)
    {
        super(message);
        command_ = command;
    }
    
    public String getCommand() {
        return command_;
    }

}
