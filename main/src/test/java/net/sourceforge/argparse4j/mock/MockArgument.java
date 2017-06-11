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
package net.sourceforge.argparse4j.mock;

import java.util.Collection;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentChoice;
import net.sourceforge.argparse4j.inf.ArgumentType;
import net.sourceforge.argparse4j.inf.FeatureControl;

public class MockArgument implements Argument {

    @Override
    public Argument nargs(int n) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Argument nargs(String n) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Argument setConst(Object value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> Argument setConst(E... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Argument setDefault(Object value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> Argument setDefault(E... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Argument setDefault(FeatureControl ctrl) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> Argument type(Class<T> type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> Argument type(ArgumentType<T> type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Argument required(boolean required) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Argument action(ArgumentAction action) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Argument choices(ArgumentChoice choice) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> Argument choices(Collection<E> values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> Argument choices(E... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Argument dest(String dest) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Argument metavar(String... metavar) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Argument help(String help) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Argument help(FeatureControl help) {
        return null;
    }

    @Override
    public String textualName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDest() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getConst() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getDefault() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FeatureControl getDefaultControl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FeatureControl getHelpControl() {
        return null;
    }
}
