package net.sourceforge.argparse4j;

import java.util.Locale;
import java.util.ResourceBundle;

import net.sourceforge.argparse4j.helper.PrefixPattern;
import net.sourceforge.argparse4j.helper.TextWidthCounter;
import net.sourceforge.argparse4j.internal.ArgumentParserImpl;

public class ArgumentParserConfiguration {
    public final String prog_;
    public final boolean addHelp_;
    public final String prefixChars_;
    public final PrefixPattern prefixPattern_;
    private final String fromFilePrefix_;
    public final PrefixPattern fromFilePrefixPattern_;
    private final ResourceBundle resourceBundle_;
    public final TextWidthCounter textWidthCounter_;
    public final int formatWidth_;
    public final boolean singleMetavar_;
    public final boolean noDestConversionForPositionalArgs_;

    public ArgumentParserConfiguration(String prog, boolean addHelp,
            String prefixChars, String fromFilePrefix, Locale locale,
            TextWidthCounter textWidthCounter, int formatWidth,
            boolean singleMetavar, boolean noDestConversionForPositionalArgs) {
        prog_ = prog;
        addHelp_ = addHelp;
        prefixChars_ = prefixChars;
        prefixPattern_ = new PrefixPattern(prefixChars);
        fromFilePrefix_ = fromFilePrefix;
        fromFilePrefixPattern_ = fromFilePrefix == null ? null : new PrefixPattern(fromFilePrefix);
        resourceBundle_ = ResourceBundle
                .getBundle(ArgumentParserImpl.class.getName(), locale);
        textWidthCounter_ = textWidthCounter;
        formatWidth_ = formatWidth;
        singleMetavar_ = singleMetavar;
        noDestConversionForPositionalArgs_ = noDestConversionForPositionalArgs;
    }

    private ArgumentParserConfiguration(String prog, boolean addHelp,
            String prefixChars, String fromFilePrefix,
            ResourceBundle resourceBundle, TextWidthCounter textWidthCounter,
            int formatWidth, boolean singleMetavar,
            boolean noDestConversionForPositionalArgs) {
        prog_ = prog;
        addHelp_ = addHelp;
        prefixChars_ = prefixChars;
        prefixPattern_ = new PrefixPattern(prefixChars);
        fromFilePrefix_ = fromFilePrefix;
        fromFilePrefixPattern_ = fromFilePrefix == null ? null : new PrefixPattern(fromFilePrefix);
        resourceBundle_ = resourceBundle;
        textWidthCounter_ = textWidthCounter;
        formatWidth_ = formatWidth;
        singleMetavar_ = singleMetavar;
        noDestConversionForPositionalArgs_ = noDestConversionForPositionalArgs;
    }

    public String optionallyLocalize(String key,
            String unlocalizedMessage) {
        return resourceBundle_.containsKey(key) ? resourceBundle_
                .getString(key) : unlocalizedMessage;
    }

    public ArgumentParserConfiguration forSubparser(boolean addHelp,
            String prefixChars) {
        return new ArgumentParserConfiguration(prog_, addHelp, prefixChars,
                fromFilePrefix_, resourceBundle_, textWidthCounter_,
                formatWidth_, singleMetavar_,
                noDestConversionForPositionalArgs_);
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle_;
    }
}
