package net.sourceforge.argparse4j.impl.type;

import java.io.File;

class ExistingVerification extends FileVerification {
    @Override
    protected boolean exists(File file) {
        return true;
    }
}
