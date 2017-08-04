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

class ParseState {
    /**
     * Array of arguments.
     */
    public String[] args;
    /**
     * Index in args array, which points next argument to process.
     */
    int index;
    /**
     * Index in {@link #args} array, which points to the last argument read from
     * file. -1 means that no argument is read from file. If arguments are read
     * from file recursively (e.g., argument file is found in argument file),
     * this value is properly extended to point to the actual last argument by
     * position.
     */
    int lastFromFileArgIndex;
    /**
     * True if special argument "--" is found and consumed.
     */
    boolean consumedSeparator;
    /**
     * True if negative number like flag is registered in the parser.
     */
    boolean negNumFlag;

    /**
     * Deferred exception encountered while parsing. This will be thrown after
     * parsing completed and no other exception was thrown.
     */
    ArgumentParserException deferredException;

    /**
     * Index of positional argument (Argument object) we are currently
     * processing.
     */
    int posArgIndex;

    /**
     * The number of arguments (well, parameters) consumed for the current
     * positional Argument object.
     */
    int posArgConsumed;

    /**
     * Accumulated positional arguments we have seen so far.
     */
    List<String> posArgArgs;

    /**
     * Accumulated unknown arguments, if not null.
     */
    List<String> unknown;

    ParseState(String[] args, boolean negNumFlag, List<String> unknown) {
        this.args = args;
        this.index = 0;
        this.lastFromFileArgIndex = -1;
        this.negNumFlag = negNumFlag;
        this.deferredException = null;
        this.posArgIndex = 0;
        this.posArgConsumed = 0;
        this.posArgArgs = new ArrayList<String>();
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

    void resetPosArgs() {
        this.posArgIndex = 0;
        this.posArgConsumed = 0;
        this.posArgArgs.clear();
    }
}
