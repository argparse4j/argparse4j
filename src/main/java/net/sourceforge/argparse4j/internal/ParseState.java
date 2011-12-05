package net.sourceforge.argparse4j.internal;

public class ParseState {
    /**
     * Array of arguments.
     */
    public String args[];
    /**
     * Index in args array, which points next argument to process.
     */
    public int index;
    /**
     * True if special argument "--" is found and consumed.
     */
    public boolean consumedSeparator;
    /**
     * True if negative number like flag is registered in the parser.
     */
    public boolean negNumFlag;

    public ParseState(String args[], int index, boolean negNumFlag) {
        this.args = args;
        this.index = index;
        this.negNumFlag = negNumFlag;
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
