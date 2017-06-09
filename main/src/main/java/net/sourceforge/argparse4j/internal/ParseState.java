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
package net.sourceforge.argparse4j.internal;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Index of positional argument (Argument object) we are currently
     * processing.
     */
    public int posargIndex;

    /**
     * The number of arguments (well, parameters) consumed for the current
     * positional Argument object.
     */
    public int posargConsumed;

    /**
     * Accumulated positional arguments we have seen so far.
     */
    public List<String> posargArgs;

    /**
     * Accumulated unknown arguments, if not null.
     */
    public List<String> unknown;

    public ParseState(String args[], int index, boolean negNumFlag,
            List<String> unknown) {
        this.args = args;
        this.index = index;
        this.lastFromFileArgIndex = -1;
        this.negNumFlag = negNumFlag;
        this.deferredException = null;
        this.posargIndex = 0;
        this.posargConsumed = 0;
        this.posargArgs = new ArrayList<String>();
        this.unknown = unknown;
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

    void resetPosargs() {
        this.posargIndex = 0;
        this.posargConsumed = 0;
        this.posargArgs.clear();
    }
}
