package net.sourceforge.argparse4j.internal;

import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;


public class HelpScreenException extends ArgumentParserException {

    private static final long serialVersionUID = -7303433847334132539L;

    public HelpScreenException(ArgumentParser parser) {
        super("Help Screen", parser);
    }
}

