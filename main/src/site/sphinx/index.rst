.. include:: ../../../../README.rst

To see how to use argparse4j, see :doc:`usage`.  See also
:doc:`examples`.

Contents
--------

.. toctree::
   :maxdepth: 2

   usage
   examples
   migration

Demo
----

Here is the working demo program to calculate checksum. Argparse4j
is used to parse command line arguments (Java 7 required to compile
this source code)::

  import java.io.IOException;
  import java.nio.ByteBuffer;
  import java.nio.channels.ByteChannel;
  import java.nio.file.Files;
  import java.nio.file.Path;
  import java.nio.file.Paths;
  import java.nio.file.StandardOpenOption;
  import java.security.MessageDigest;
  import java.security.NoSuchAlgorithmException;

  import net.sourceforge.argparse4j.ArgumentParsers;
  import net.sourceforge.argparse4j.inf.ArgumentParser;
  import net.sourceforge.argparse4j.inf.ArgumentParserException;
  import net.sourceforge.argparse4j.inf.Namespace;

  public class Checksum {

    public static void main(String[] args) {
      ArgumentParser parser = ArgumentParsers.newFor("Checksum").build()
          .defaultHelp(true)
          .description("Calculate checksum of given files.");
      parser.addArgument("-t", "--type")
          .choices("SHA-256", "SHA-512", "SHA1").setDefault("SHA-256")
          .help("Specify hash function to use");
      parser.addArgument("file").nargs("*")
          .help("File to calculate checksum");
      Namespace ns = null;
      try {
        ns = parser.parseArgs(args);
      } catch (ArgumentParserException e) {
        parser.handleError(e);
        System.exit(1);
      }
      MessageDigest digest = null;
      try {
        digest = MessageDigest.getInstance(ns.getString("type"));
      } catch (NoSuchAlgorithmException e) {
        System.err.printf("Could not get instance of algorithm %s: %s",
            ns.getString("type"), e.getMessage());
        System.exit(1);
      }
      for (String name : ns.<String>getList("file")) {
        Path path = Paths.get(name);
        try (ByteChannel channel = Files.newByteChannel(path,
            StandardOpenOption.READ)) {
          ByteBuffer buffer = ByteBuffer.allocate(4096);
          while (channel.read(buffer) > 0) {
            buffer.flip();
            digest.update(buffer);
            buffer.clear();
          }
        } catch (IOException e) {
          System.err
              .printf("%s: failed to read data: %s", name, e.getMessage());
          continue;
        }
        byte[] md = digest.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : md) {
          String x = Integer.toHexString(0xff & b);
          if (x.length() == 1) {
            sb.append("0");
          }
          sb.append(x);
        }
        System.out.printf("%s  %s\n", sb, name);
      }
    }

  }

When executed:

.. code-block:: console

  $ java Checksum -h
  usage: Checksum [-h] [-t {SHA-256,SHA-512,SHA1}] [file [file ...]]

  Calculate checksum of given files.

  positional arguments:
    file                   File to calculate checksum

  named arguments:
    -h, --help             show this help message and exit
    -t {SHA-256,SHA-512,SHA1}, --type {SHA-256,SHA-512,SHA1}
			   Specify hash function to use (default: SHA-256)
  $ java Checksum file1.cc file1.h
  6bd85bf4b936bc8870c70bea04cd12d4fe3745934f511e6e188d718d32154a79  file1.cc
  839ef370cbd54f62985bac7b974cc575eaaa24a8edd6ae7787cfc71829ceda40  file1.h

  $ java Checksum --tpye file1.cc
  usage: Checksum [-h] [-t {SHA-256,SHA-512,SHA1}] [file [file ...]]
  Checksum: error: unrecognized arguments: --tpye

  Did you mean:
	  --type

  $ java Checksum -t SHA1 file1.cc
  20bada64dde97b98faaba09ebbfdb70af71476f1  file1.cc
