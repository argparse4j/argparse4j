package net.sourceforge.argparse4j.impl.type;

import java.io.File;

class NotExistingVerification extends FileVerification {
    @Override
    protected boolean exists(File file) {
        return false;
    }
}
