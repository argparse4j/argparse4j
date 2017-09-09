package net.sourceforge.argparse4j.impl.type;

import java.io.File;

class NotExistingNonreadableVerification
        extends FileVerification {
    @Override
    protected boolean exists(File file) {
        return false;
    }

    @Override
    protected boolean canRead(File file) {
        return false;
    }
}
