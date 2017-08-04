package net.sourceforge.argparse4j.internal;

import java.util.Locale;
import java.util.ResourceBundle;

import net.sourceforge.argparse4j.helper.PrefixPattern;
import net.sourceforge.argparse4j.helper.TextWidthCounter;
import net.sourceforge.argparse4j.inf.ArgumentParserConfiguration;

public class ArgumentParserConfigurationImpl implements ArgumentParserConfiguration {
    final String prog_;
    final boolean addHelp_;
    final String prefixChars_;
    final PrefixPattern prefixPattern_;
    private final String fromFilePrefix_;
    final PrefixPattern fromFilePrefixPattern_;
    private final ResourceBundle resourceBundle_;
    final TextWidthCounter textWidthCounter_;
    final int formatWidth_;
    final boolean singleMetavar_;
    final boolean noDestConversionForPositionalArgs_;

    public ArgumentParserConfigurationImpl(String prog, boolean addHelp,
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

    private ArgumentParserConfigurationImpl(String prog, boolean addHelp,
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

    ArgumentParserConfigurationImpl forSubparser(boolean addHelp,
            String prefixChars) {
        return new ArgumentParserConfigurationImpl(prog_, addHelp, prefixChars,
                fromFilePrefix_, resourceBundle_, textWidthCounter_,
                formatWidth_, singleMetavar_,
                noDestConversionForPositionalArgs_);
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle_;
    }

    @Override
    public Locale getLocale() {
        return resourceBundle_.getLocale();
    }
}
