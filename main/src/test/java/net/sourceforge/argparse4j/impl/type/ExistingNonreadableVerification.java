package net.sourceforge.argparse4j.impl.type;

import java.io.File;

class ExistingNonreadableVerification
        extends FileVerification {
    @Override
    protected boolean exists(File file) {
        return true;
    }

    @Override
    protected boolean canRead(File file) {
        return false;
    }
}
