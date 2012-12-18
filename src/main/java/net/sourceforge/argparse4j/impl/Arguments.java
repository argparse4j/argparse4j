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
package net.sourceforge.argparse4j.impl;

import java.util.List;

import net.sourceforge.argparse4j.impl.action.AppendArgumentAction;
import net.sourceforge.argparse4j.impl.action.AppendConstArgumentAction;
import net.sourceforge.argparse4j.impl.action.HelpArgumentAction;
import net.sourceforge.argparse4j.impl.action.StoreArgumentAction;
import net.sourceforge.argparse4j.impl.action.StoreConstArgumentAction;
import net.sourceforge.argparse4j.impl.action.StoreFalseArgumentAction;
import net.sourceforge.argparse4j.impl.action.StoreTrueArgumentAction;
import net.sourceforge.argparse4j.impl.action.VersionArgumentAction;
import net.sourceforge.argparse4j.impl.choice.RangeArgumentChoice;
import net.sourceforge.argparse4j.impl.type.EnumArgumentType;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.FeatureControl;

/**
 * <p>
 * This class provides useful shortcuts and constants.
 * </p>
 * 
 * <p>
 * They are mainly used to specify parameter to {@link Argument} object.
 * </p>
 * 
 */
public final class Arguments {

    /**
     * Intentionally made private to avoid instantiation in application code.
     */
    private Arguments() {
    }

    /**
     * <p>
     * Creates new range constrained choice.
     * </p>
     * <p>
     * The value specified in command line will be checked to see whether it
     * fits in given range [min, max], inclusive.
     * </p>
     * 
     * @param min
     *            The lowerbound of the range, inclusive.
     * @param max
     *            The upperbound of the range, inclusive.
     * @return {@link RangeArgumentChoice} object.
     */
    public static <T extends Comparable<T>> RangeArgumentChoice<T> range(T min,
            T max) {
        return new RangeArgumentChoice<T>(min, max);
    }

    private static final StoreArgumentAction store_ = new StoreArgumentAction();
    private static final StoreTrueArgumentAction storeTrue_ = new StoreTrueArgumentAction();
    private static final StoreFalseArgumentAction storeFalse_ = new StoreFalseArgumentAction();
    private static final StoreConstArgumentAction storeConst_ = new StoreConstArgumentAction();
    private static final AppendArgumentAction append_ = new AppendArgumentAction();
    private static final AppendConstArgumentAction appendConst_ = new AppendConstArgumentAction();
    private static final HelpArgumentAction help_ = new HelpArgumentAction();
    private static final VersionArgumentAction version_ = new VersionArgumentAction();

    /**
     * <p>
     * The value of {@link FeatureControl#SUPPRESS}.
     * </p>
     * 
     * <p>
     * If value is used with {@link Argument#setDefault(FeatureControl)}, no
     * attribute is added if the command line argument was not present.
     * Otherwise, the default value, which defaults to null, will be added to
     * the object, regardless of the presence of command line argument, returned
     * by {@link ArgumentParser#parseArgs(String[])}.
     * </p>
     */
    public static final FeatureControl SUPPRESS = FeatureControl.SUPPRESS;

    /**
     * Returns store action.
     * 
     * @return {@link StoreArgumentAction} object.
     */
    public static StoreArgumentAction store() {
        return store_;
    }

    /**
     * <p>
     * Returns storeTrue action.
     * </p>
     * 
     * <p>
     * If this action is used, the value specified using
     * {@link Argument#nargs(int)} will be ignored.
     * </p>
     * 
     * @return {@link StoreTrueArgumentAction} object.
     */
    public static StoreTrueArgumentAction storeTrue() {
        return storeTrue_;
    }

    /**
     * <p>
     * Returns storeFalse action.
     * </p>
     * 
     * <p>
     * If this action is used, the value specified using
     * {@link Argument#nargs(int)} will be ignored.
     * </p>
     * 
     * @return {@link StoreFalseArgumentAction} object.
     */
    public static StoreFalseArgumentAction storeFalse() {
        return storeFalse_;
    }

    /**
     * <p>
     * Returns storeConst action.
     * </p>
     * 
     * <p>
     * If this action is used, the value specified using
     * {@link Argument#nargs(int)} will be ignored.
     * </p>
     * 
     * @return {@link StoreConstArgumentAction} object.
     */
    public static StoreConstArgumentAction storeConst() {
        return storeConst_;
    }

    /**
     * <p>
     * Returns append action.
     * </p>
     * <p>
     * If this action is used, the attribute will be of type {@link List}. If
     * used with {@link Argument#nargs(int)}, the element of List will be List.
     * This is because {@link Argument#nargs(int)} produces List.
     * </p>
     * 
     * @return {@link AppendArgumentAction} object.
     */
    public static AppendArgumentAction append() {
        return append_;
    }

    /**
     * <p>
     * Returns appendConst action.
     * </p>
     * <p>
     * If this action is used, the value specified using
     * {@link Argument#nargs(int)} will be ignored.
     * </p>
     * 
     * @return {@link AppendConstArgumentAction} object.
     */
    public static AppendConstArgumentAction appendConst() {
        return appendConst_;
    }

    /**
     * <p>
     * Returns help action.
     * </p>
     * <p>
     * This is used for an option printing help message. Please note that this
     * action terminates program after printing help message.
     * </p>
     * 
     * @return {@link HelpArgumentAction} object.
     */
    public static HelpArgumentAction help() {
        return help_;
    }

    /**
     * <p>
     * Returns version action.
     * </p>
     * <p>
     * This is used for an option printing version message. Please note that
     * this action terminates program after printing version message.
     * </p>
     * 
     * @return {@link VersionArgumentAction} object.
     */
    public static VersionArgumentAction version() {
        return version_;
    }

    /**
     * <p>
     * Returns {@link EnumArgumentType} with given enum {@code type}.
     * </p>
     * <p>
     * Since enum does not have a constructor with string argument, you cannot
     * use {@link Argument#type(Class)}. Instead use this convenient function.
     * </p>
     * 
     * @param type
     *            The enum type
     * @return {@link EnumArgumentType} object
     */
    public static <T extends Enum<T>> EnumArgumentType<T> enumType(Class<T> type) {
        return new EnumArgumentType<T>(type);
    }

    /**
     * <p>
     * Returns new {@link FileArgumentType} object.
     * </p>
     *
     * @return {@link FileArgumentType} object
     */
    public static FileArgumentType fileType() {
        return new FileArgumentType();
    }
}
