/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.sourceforge.argparse4j.impl.type;

import java.io.File;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

/**
 * ArgumentType subclass for File type, using fluent style API.
 */
public class FileArgumentType implements ArgumentType<File> {
  
  private boolean acceptSystemIn = false;
  private boolean verifyExists = false;
  private boolean verifyNotExists = false;
  private boolean verifyIsFile = false;
  private boolean verifyIsDirectory = false;
  private boolean verifyCanRead = false;
  private boolean verifyCanWrite = false;
  private boolean verifyCanWriteParent = false;
  private boolean verifyCanExecute = false;
  private boolean verifyIsAbsolute = false;

  public FileArgumentType() {}
  
  public FileArgumentType acceptSystemIn() {
    acceptSystemIn = true;
    return this;
  }
  
  public FileArgumentType verifyExists() {
    verifyExists = true;
    return this;
  }
  
  public FileArgumentType verifyNotExists() {
    verifyNotExists = true;
    return this;
  }
  
  public FileArgumentType verifyIsFile() {
    verifyIsFile = true;
    return this;
  }
  
  public FileArgumentType verifyIsDirectory() {
    verifyIsDirectory = true;
    return this;
  }
  
  public FileArgumentType verifyCanRead() {
    verifyCanRead = true;
    return this;
  }
  
  public FileArgumentType verifyCanWrite() {
    verifyCanWrite = true;
    return this;
  }
  
  public FileArgumentType verifyCanWriteParent() {
    verifyCanWriteParent = true;
    return this;
  }
  
  public FileArgumentType verifyCanExecute() {
    verifyCanExecute = true;
    return this;
  }
  
  public FileArgumentType verifyIsAbsolute() {
    verifyIsAbsolute = true;
    return this;
  }
  
  @Override
  public File convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
    File file = new File(value);
    if (verifyExists && !isSystemIn(file)) {
      verifyExists(parser, file);
    }
    if (verifyNotExists && !isSystemIn(file)) {
      verifyNotExists(parser, file);
    }
    if (verifyIsFile && !isSystemIn(file)) {
      verifyIsFile(parser, file);
    }
    if (verifyIsDirectory && !isSystemIn(file)) {
      verifyIsDirectory(parser, file);
    }
    if (verifyCanRead && !isSystemIn(file)) {
      verifyCanRead(parser, file);
    }
    if (verifyCanWrite && !isSystemIn(file)) {
      verifyCanWrite(parser, file);
    }
    if (verifyCanWriteParent && !isSystemIn(file)) {
      verifyCanWriteParent(parser, file);
    }
    if (verifyCanExecute && !isSystemIn(file)) {
      verifyCanExecute(parser, file);
    }
    if (verifyIsAbsolute && !isSystemIn(file)) {
      verifyIsAbsolute(parser, file);
    }
    return file;
  }
  
  private void verifyExists(ArgumentParser parser, File file) throws ArgumentParserException {
    if (!file.exists()) {
      throw new ArgumentParserException("File not found: " + file, parser);
    }
  }    
  
  private void verifyNotExists(ArgumentParser parser, File file) throws ArgumentParserException {
    if (file.exists()) {
      throw new ArgumentParserException("File found: " + file, parser);
    }
  }    
  
  private void verifyIsFile(ArgumentParser parser, File file) throws ArgumentParserException {
    if (!file.isFile()) {
      throw new ArgumentParserException("Not a file: " + file, parser);
    }
  }    
  
  private void verifyIsDirectory(ArgumentParser parser, File file) throws ArgumentParserException {
    if (!file.isDirectory()) {
      throw new ArgumentParserException("Not a directory: " + file, parser);
    }
  }    
  
  private void verifyCanRead(ArgumentParser parser, File file) throws ArgumentParserException {
    if (!file.canRead()) {
      throw new ArgumentParserException("Insufficient permissions to read file: " + file, parser);
    }
  }    
  
  private void verifyCanWrite(ArgumentParser parser, File file) throws ArgumentParserException {
    if (!file.canWrite()) {
      throw new ArgumentParserException("Insufficient permissions to write file: " + file, parser);
    }
  }    
  
  private void verifyCanWriteParent(ArgumentParser parser, File file) throws ArgumentParserException {
    File parent = file.getParentFile();
    if (parent == null || !parent.canWrite()) {
      throw new ArgumentParserException("Cannot write parent of file: " + file, parser);
    }
  }    
  
  private void verifyCanExecute(ArgumentParser parser, File file) throws ArgumentParserException {
    if (!file.canExecute()) {
      throw new ArgumentParserException("Insufficient permissions to execute file: " + file, parser);
    }
  }    
  
  private void verifyIsAbsolute(ArgumentParser parser, File file) throws ArgumentParserException {
    if (!file.isAbsolute()) {
      throw new ArgumentParserException("Not an absolute file: " + file, parser);
    }
  }    

  private boolean isSystemIn(File file) {
    return acceptSystemIn && file.getPath().equals("-");
  }
  
}
