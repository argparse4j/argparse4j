package net.sourceforge.argparse4j.internal;

import junit.framework.TestCase;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;

import java.util.Locale;

public class ArgumentGroupImplHelpTest extends TestCase {

    public static final String EOL = System.lineSeparator();

    public void testDoesNotIncludeMutualExclusivityIfNotMutuallyExclusiveGroup() {
        ArgumentParser parser = ArgumentParsers.newFor("group help")
                .mustHelpTextIncludeMutualExclusivity(false)
                .locale(Locale.ENGLISH)
                .build();
        ArgumentGroup group = parser.addArgumentGroup("group")
                .description("non-exclusive group");
        group.addArgument("-a");
        group.addArgument("-b");

        String help = parser.formatHelp();

        assertEquals("usage: group help [-h] [-a A] [-b B]" + EOL +
                EOL +
                "named arguments:" + EOL +
                "  -h, --help             show this help message and exit" + EOL +
                EOL +
                "group:" + EOL +
                "  non-exclusive group" + EOL +
                EOL +
                "  -a A" + EOL +
                "  -b B" + EOL, help);
    }

    public void testDoesNotIncludeMutualExclusivityIfNotEnabled() {
        ArgumentParser parser = ArgumentParsers.newFor("group help")
                .mustHelpTextIncludeMutualExclusivity(false)
                .locale(Locale.ENGLISH)
                .build();
        MutuallyExclusiveGroup group = parser.addMutuallyExclusiveGroup("group")
                .description("mutually-exclusive group");
        group.addArgument("-a");
        group.addArgument("-b");

        String help = parser.formatHelp();

        assertEquals("usage: group help [-h] [-a A | -b B]" + EOL +
                EOL +
                "named arguments:" + EOL +
                "  -h, --help             show this help message and exit" + EOL +
                EOL +
                "group:" + EOL +
                "  mutually-exclusive group" + EOL +
                EOL +
                "  -a A" + EOL +
                "  -b B" + EOL, help);
    }

    public void testIncludesMutualExclusivityIfMutuallyExclusiveGroupAndEnabled() {
        ArgumentParser parser = ArgumentParsers.newFor("group help")
                .mustHelpTextIncludeMutualExclusivity(true)
                .locale(Locale.ENGLISH)
                .build();
        MutuallyExclusiveGroup group = parser.addMutuallyExclusiveGroup("group")
                .description("mutually-exclusive group");
        group.addArgument("-a");
        group.addArgument("-b");

        String help = parser.formatHelp();

        assertEquals("usage: group help [-h] [-a A | -b B]" + EOL +
                EOL +
                "named arguments:" + EOL +
                "  -h, --help             show this help message and exit" + EOL +
                EOL +
                "group:" + EOL +
                "  mutually-exclusive group" + EOL +
                EOL +
                "  At most 1 of the arguments below may be given." + EOL +
                EOL +
                "  -a A" + EOL +
                "  -b B" + EOL, help);
    }
}
