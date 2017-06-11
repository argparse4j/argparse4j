package net.sourceforge.argparse4j.ext.java7;

import java.util.Locale;
import java.util.ResourceBundle;

class Java7ExtensionResourceBundle {
    private Java7ExtensionResourceBundle() {
    }
    
    static ResourceBundle get(Locale locale) {
        return ResourceBundle.getBundle(
                "net/sourceforge/argparse4j/ext/java7/Java7Extension", locale);
    }
}
