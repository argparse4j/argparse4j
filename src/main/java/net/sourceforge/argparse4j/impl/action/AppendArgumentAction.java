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
package net.sourceforge.argparse4j.impl.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

/**
 * <p>
 * Argument action to store a list.
 * </p>
 * 
 * <p>
 * This action appends each argument value to the list. The list is of type
 * {@link java.util.List}. This is useful to allow an option to be specified
 * multiple times. If {@code attrs} contains non-List object for key
 * {@link Argument#getDest()}, it will be overwritten by the List containing
 * {@code value}. {@link #consumeArgument()} always returns {@code true}.
 * </p>
 */
public class AppendArgumentAction implements ArgumentAction {

    @Override
    public void run(ArgumentParser parser, Argument arg,
            Map<String, Object> attrs, String flag, Object value)
            throws ArgumentParserException {
        if (attrs.containsKey(arg.getDest())) {
            Object obj = attrs.get(arg.getDest());
            if (obj instanceof List) {
                ((List<Object>) obj).add(value);
                return;
            }
        }
        List<Object> list = new ArrayList<Object>();
        list.add(value);
        attrs.put(arg.getDest(), list);
    }

    @Override
    public boolean consumeArgument() {
        return true;
    }

    @Override
    public void onAttach(Argument arg) {
    }

}
