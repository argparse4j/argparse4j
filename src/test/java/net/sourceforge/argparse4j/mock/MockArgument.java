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
    public Argument setConst(Object... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Argument setDefault(Object value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Argument setDefault(Object... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Argument setDefault(FeatureControl ctrl) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Argument type(Class<?> type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Argument type(ArgumentType type) {
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
    public Argument choices(Collection<?> values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Argument choices(Object... values) {
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

}
