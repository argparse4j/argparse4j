/*
 * Copyright (C) 2015 andrewj
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
package net.sourceforge.argparse4j.impl.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Locale;
import java.util.ResourceBundle;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.internal.ArgumentParserImpl;
import net.sourceforge.argparse4j.mock.MockArgument;

import org.junit.Test;

public class CaseInsensitiveEnumStringArgumentTypeTest {

    enum Lang {
        PYTHON, JAVA, CPP {
            @Override
            public String toString() {
                return "C++";
            }
        }, INTERLISP {
            @Override
            public String toString() {
                return "Interlisp";
            }
        }
    }

    @Test
    public void testConvert() throws ArgumentParserException {
        ArgumentParser ap = ArgumentParsers.newFor("argparse4j")
                .locale(Locale.US).build();
        CaseInsensitiveEnumStringArgumentType<Lang> type = CaseInsensitiveEnumStringArgumentType
                .forEnum(Lang.class);
        assertEquals(Lang.PYTHON, type.convert(ap, null, "PYTHON"));
        assertEquals(Lang.PYTHON, type.convert(ap, null, "Python"));
        assertEquals(Lang.PYTHON, type.convert(ap, null, "pYTHON"));
        assertEquals(Lang.JAVA, type.convert(ap, null, "JAVA"));
        assertEquals(Lang.CPP, type.convert(ap, null, "C++"));
        assertEquals(Lang.INTERLISP, type.convert(ap, null, "INTERLISP"));
    }

    @Test
    public void testConvertErrorsWithUnknownMember() throws ArgumentParserException {
        ArgumentParser ap = ArgumentParsers.newFor("argparse4j")
                .locale(Locale.US).build();
        CaseInsensitiveEnumStringArgumentType<Lang> type = CaseInsensitiveEnumStringArgumentType
                .forEnum(Lang.class);
        try {
            type.convert(ap, new MockArgument(), "CPP");
            fail("Expected ArgumentParserException to be thrown");
        } catch (ArgumentParserException e) {
            assertEquals(
                    "argument null: could not convert 'CPP' (choose from {PYTHON,JAVA,C++,Interlisp})",
                    e.getMessage());
        }
    }
    
    @Test
    public void testIgnoresLocaleOfParserForCaseInsensitivity() throws
            ArgumentParserException {
        Locale turkish = new Locale("tr");
        ArgumentParser ap = ArgumentParsers.newFor("argparse4j")
                .locale(turkish).build();
        CaseInsensitiveEnumStringArgumentType<Lang> type = CaseInsensitiveEnumStringArgumentType
                .forEnum(Lang.class);
        try {
            type.convert(ap, new MockArgument(), "ınterlisp");
            fail("Expected ArgumentParserException to be thrown");
        } catch (ArgumentParserException e) {
            ResourceBundle bundle = ResourceBundle.getBundle(ArgumentParserImpl.class.getName(), turkish);
            String localizedExpectedErrorMessage = String.format(
                    bundle.getString("couldNotConvertChooseFromError"),
                    "ınterlisp",
                    "{PYTHON,JAVA,C++,Interlisp}");
            String localizedExpectedFullErrorMessage = String.format(
                    bundle.getString("argument"),
                    null,
                    localizedExpectedErrorMessage);
            assertEquals(
                    localizedExpectedFullErrorMessage,
                    e.getMessage());
        }
    }
}
