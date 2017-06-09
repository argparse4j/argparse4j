/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.argparse4j.internal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Returns the column width of the command line terminal from which this program
 * was started. Typically the column width is around 80 characters or so.
 * 
 * Currently works on Linux and OSX.
 * 
 * Returns -1 if the column width cannot be determined for some reason.
 */
public class TerminalWidth {

    private static final int UNKNOWN_WIDTH = -1;

    public static void main(String[] args) {
        System.out.println("terminalWidth: "
                + new TerminalWidth().getTerminalWidth());
    }

    public int getTerminalWidth() {
        String width = System.getenv("COLUMNS");
        if (width != null) {
            try {
                return Integer.parseInt(width);
            } catch (NumberFormatException e) {
                return UNKNOWN_WIDTH;
            }
        }

        try {
            return getTerminalWidth2();
        } catch (IOException e) {
            return UNKNOWN_WIDTH;
        }
    }

    // see
    // http://grokbase.com/t/gg/clojure/127qwgscvc/how-do-you-determine-terminal-console-width-in-%60lein-repl%60
    private int getTerminalWidth2() throws IOException {
        String osName = System.getProperty("os.name");
        boolean isOSX = osName.startsWith("Mac OS X");
        boolean isLinux = osName.startsWith("Linux")
                || osName.startsWith("LINUX");
        if (!isLinux && !isOSX) {
            return UNKNOWN_WIDTH; // actually, this might also work on Solaris
                                  // but this hasn't been tested
        }
        ProcessBuilder builder = new ProcessBuilder(which("sh").toString(),
                "-c", "stty -a < /dev/tty");
        builder.redirectErrorStream(true);
        Process process = builder.start();
        InputStream in = process.getInputStream();
        ByteArrayOutputStream resultBytes = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) >= 0) {
                resultBytes.write(buf, 0, len);
            }
        } finally {
            in.close();
        }

        String result = new String(resultBytes.toByteArray());
        // System.out.println("result=" + result);

        try {
            if (process.waitFor() != 0) {
                return UNKNOWN_WIDTH;
            }
        } catch (InterruptedException e) {
            return UNKNOWN_WIDTH;
        }

        String pattern;
        if (isOSX) {
            // Extract columns from a line such as this:
            // speed 9600 baud; 39 rows; 80 columns;
            pattern = "(\\d+) columns";
        } else {
            // Extract columns from a line such as this:
            // speed 9600 baud; rows 50; columns 83; line = 0;
            pattern = "columns (\\d+)";
        }
        Matcher m = Pattern.compile(pattern).matcher(result);
        if (!m.find()) {
            return UNKNOWN_WIDTH;
        }
        result = m.group(1);

        try {
            return Integer.parseInt(result);
        } catch (NumberFormatException e) {
            return UNKNOWN_WIDTH;
        }
    }

    private File which(String cmd) throws IOException {
        String path = System.getenv("PATH");
        if (path != null) {
          for (String dir : path.split(Pattern.quote(File.pathSeparator))) {
              File command = new File(dir.trim(), cmd);
              if (command.canExecute()) {
                  return command.getAbsoluteFile();
              }
          }
        }
        throw new IOException("No command '" + cmd + "' on path " + path);
    }
}
