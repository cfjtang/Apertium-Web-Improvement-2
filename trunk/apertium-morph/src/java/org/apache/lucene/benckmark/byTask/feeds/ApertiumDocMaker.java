package org.apache.lucene.benckmark.byTask.feeds;

import org.apache.lucene.benchmark.byTask.feeds.BasicDocMaker;
import org.apache.lucene.benchmark.byTask.feeds.DocData;
import org.apache.lucene.benchmark.byTask.feeds.NoMoreDataException;
import org.apache.lucene.benchmark.byTask.utils.Config;

/**
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;

import java.util.zip.GZIPInputStream;

/**
 *  Creates documents in the <a
 *  href="http://www.apertium.org">Apertium</a> text-based
 *  intermediate format for the test. It uses a collection of
 *  documents in that format.
 *
 * @author Felipe Sanchez Martinez (fsanchez [at] dlsi [dot] ua [dot]
 * es)<br/>&copy; 2008 Universitat d'Alacant/Universidad de Alicante 
 */
public class ApertiumDocMaker extends BasicDocMaker {

  private File dataDir = null;
  private ArrayList<File> inputFiles = new ArrayList<File>();
  private int nextFile = 0;
  private int iteration=0;

  /* (non-Javadoc)
   * @see SimpleDocMaker#setConfig(java.util.Properties)
   */
  public void setConfig(Config config) {
    super.setConfig(config);
    String d = config.get("docs.dir","docs");
    dataDir = new File(new File("indexwork"),d);

    collectFiles(dataDir,inputFiles);
    if (inputFiles.size()==0) {
      throw new RuntimeException("No files in dataDir: " + dataDir.getAbsolutePath());
    }
  }

  protected DocData getNextDocData() throws Exception {
    File f = null;
    String name = null;
    synchronized (this) {
      if (nextFile >= inputFiles.size()) { 
        // exhausted files, start a new round, unless forever set to false.
        if (!forever) {
          throw new NoMoreDataException();
        }
        nextFile = 0;
        iteration++;
      }
      f = inputFiles.get(nextFile++);
      name = f.getCanonicalPath()+"_"+iteration;
    }
    BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(f))));
    //BufferedReader reader = new BufferedReader(new FileReader(f));
    
    String line = null;
    StringBuffer bodyBuf = new StringBuffer(1024);
    while ((line = reader.readLine()) != null) {
      bodyBuf.append(line).append(' ');
    }
    reader.close();

    addBytes(f.length());
    return new DocData(name, bodyBuf.toString(), name, null, null);
  }

  /*
   *  (non-Javadoc)
   * @see DocMaker#resetIinputs()
   */
  public synchronized void resetInputs() {
    super.resetInputs();
    nextFile = 0;
    iteration = 0;
  }

  /*
   *  (non-Javadoc)
   * @see DocMaker#numUniqueTexts()
   */
  public int numUniqueTexts() {
    return inputFiles.size();
  }
}
