package net.sourceforge.argparse4j.internal;

import net.sourceforge.argparse4j.inf.ArgumentParserException;

public class ParseState {
    /**
     * Array of arguments.
     */
    public String[] args;
    /**
     * Index in args array, which points next argument to process.
     */
    public int index;
    /**
     * Index in {@link #args} array, which points to the last argument read from
     * file. -1 means that no argument is read from file. If arguments are read
     * from file recursively (e.g., argument file is found in argument file),
     * this value is properly extended to point to the actual last argument by
     * position.
     */
    public int lastFromFileArgIndex;
    /**
     * True if special argument "--" is found and consumed.
     */
    public boolean consumedSeparator;
    /**
     * True if negative number like flag is registered in the parser.
     */
    public boolean negNumFlag;

    /**
     * Deferred exception encountered while parsing. This will be thrown after
     * parsing completed and no other exception was thrown.
     */
    public ArgumentParserException deferredException;

    public ParseState(String args[], int index, boolean negNumFlag) {
        this.args = args;
        this.index = index;
        this.lastFromFileArgIndex = -1;
        this.negNumFlag = negNumFlag;
        this.deferredException = null;
    }

    void resetArgs(String args[]) {
        this.args = args;
        this.index = 0;
    }

    String getArg() {
        return args[index];
    }

    boolean isArgAvail() {
        return index < args.length;
    }
}
