/*
 * Copyright (C) 2007 Universitat d'Alacant / Universidad de Alicante
 * Copyright (C) 2008 Enrique Benimeli Bofarull
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package dictools;

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.Element;
import dics.elements.dtd.SElement;
import dics.elements.dtd.SectionElement;
import dics.elements.dtd.TextElement;
import dics.elements.utils.SElementList;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Dix2MDix {

    /**
     * 
     */
    private DictionaryElement dic;
    /**
     * 
     */
    private String[] arguments;
    /**
     * 
     */
    private String outFileName;
    /**
     * 
     */
    private HashMap hm;
    /**
     * 
     */
    private String sltlCode;
    /**
     * 
     */
    private String sltlFull;
    /**
     * 
     */
    private Vector<String> metaInf;
    /**
     * 
     */
    private Vector<String> files;

    /**
     * 
     */
    public Dix2MDix() {
        files = new Vector<String>();

    }

    /**
     * 
     */
    public final void do_convert() {
        this.processArguments();

        metaInf = new Vector<String>();
        files.add("meta.inf");

        String slCode = sltlCode.split("-")[0];
        String tlCode = sltlCode.split("-")[1];
        String slFull = sltlFull.split("-")[0];
        String tlFull = sltlFull.split("-")[1];

        metaInf.add("@sl-code:" + slCode + "$");
        metaInf.add("@tl-code:" + tlCode + "$");
        metaInf.add("@sl-full:" + slFull + "$");
        metaInf.add("@tl-full:" + tlFull + "$");

        this.outFileName = "sltl";
        this.processDic(dic);
        dic.reverse();
        this.outFileName = "tlsl";
        this.processDic(dic);

        this.printMetaInfFile(metaInf);

        String zipFileName = sltlCode + "-data.zip";
        ZipIt zipIt = new ZipIt(files, zipFileName);
        zipIt.zip();
    }

    /**
     * 
     * @param dic
     */
    private final void processDic(DictionaryElement dic) {
        hm = new HashMap();
        Vector values = null;

        for (SectionElement section : dic.getSections()) {
            for (EElement ee : section.getEElements()) {
                if (ee.is_LR_or_LRRL() && !ee.contains("acr") && !ee.contains("np")) {
                    String left = ee.getValueNoTags("L");
                    left = left.replaceAll("_", " "); // for dictionaries like eu-es
                    left = left.toLowerCase();
                    ee = this.cleanUp(ee); // for dictionaries like eu-es

                    if (left.length() > 1) {
                        if (Character.isLetter(left.charAt(0))) {
                            if (hm.containsKey(left)) {
                                values = (Vector) hm.get(left);
                                values.add(ee);
                                hm.put(left, values);
                            } else {
                                values = new Vector();
                                values.add(ee);
                                hm.put(left, values);
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Lemmas: " + hm.size());
        Vector<Entry> vector = this.map2vector(hm);
        Collections.sort(vector);
        this.print(vector);
    }

    private final EElement cleanUp(EElement ee) {

        for (Element e : ee.getChildren("L")) {
            if (e instanceof TextElement) {
                TextElement tE = (TextElement) e;
                String v = tE.getValue();
                v = v.replaceAll("_", " ");
                tE.setValue(v);
            }

        }
        for (Element e : ee.getChildren("R")) {
            if (e instanceof TextElement) {
                TextElement tE = (TextElement) e;
                String v = tE.getValue();
                v = v.replaceAll("_", " ");
                tE.setValue(v);
            }

        }

        return ee;

    }

    /**
     * 
     * @param vector
     */
    private final void print(Vector<Entry> vector) {
        try {
            BufferedOutputStream bos;
            FileOutputStream fos;
            OutputStreamWriter dos = null;

            int fileNumber = 1;
            String fileName = this.outFileName + "_" + fileNumber + ".dix";
            files.add(fileName);

            fos = new FileOutputStream(fileName);
            bos = new BufferedOutputStream(fos);
            dos = new OutputStreamWriter(bos, "UTF-8");

            int n = 0;
            int block = 350;
            Vector<String> fileInfo = new Vector<String>();
            String content = "";
            String first = null;
            String last = null;

            Vector<Entry> partial = new Vector();
            for (Entry e : vector) {
                if (e.getKey().length() == 0) {
                    continue;
                }
                if (n < block) {
                    if (first == null) {
                        first = e.getKey();
                    }
                    last = e.getKey();
                    n++;
                    partial.add(e);
                } else {

                    content = first + "." + last + ".";
                    fileInfo.add(new String("@" + fileName + ":" + content + "$"));
                    dos.write("@size:" + n + "$\n");
                    for (Entry pe : partial) {
                        dos.write(pe.getValue());
                    }
                    dos.flush();
                    dos.close();
                    bos.close();
                    fos.close();

                    partial = new Vector<Entry>();
                    fileNumber++;
                    fileName = this.outFileName + "_" + fileNumber + ".dix";
                    files.add(fileName);
                    fos = new FileOutputStream(fileName);
                    bos = new BufferedOutputStream(fos);
                    dos = new OutputStreamWriter(bos, "UTF-8");
                    first = last = null;
                    n = 0;
                }
            }
            dos.write("@size:" + n + "$\n");
            for (Entry pe : partial) {
                dos.write(pe.getValue());
            }
            dos.flush();
            dos.close();
            bos.close();
            fos.close();


            content = first + "." + last + ".";
            fileInfo.add(new String("@" + fileName + ":" + content + "$"));
            metaInf.add("@" + this.outFileName + "_n:" + fileInfo.size() + "$");
            for (String c : fileInfo) {
                metaInf.add(c);
            }

        } catch (IOException ioe) {

        }


    }

    /**
     * 
     * @param metaInf
     */
    private final void printMetaInfFile(Vector<String> metaInf) {
        BufferedOutputStream bos;
        FileOutputStream fos;
        OutputStreamWriter dos = null;



        String fileName = "meta.inf";
        try {
            fos = new FileOutputStream(fileName);
            bos = new BufferedOutputStream(fos);
            dos = new OutputStreamWriter(bos, "UTF-8");
            for (String c : metaInf) {
                dos.write(c + "\n");
            }
            dos.close();
            bos.close();
            fos.close();
        } catch (IOException ioe) {

        }
    }

    /**
     * 
     */
    private void processArguments() {
        String fileName = this.arguments[1];
        DictionaryReader dicReader = new DictionaryReader(fileName);
        dic = dicReader.readDic();
        //dic.reverse();
        this.sltlCode = this.arguments[2];
        this.sltlFull = this.arguments[3];
    }

    /**
     * 
     * @return the arguments
     */
    public String[] getArguments() {
        return arguments;
    }

    /**
     * 
     * @param arguments
     */
    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }

    public String getOutFileName() {
        return outFileName;
    }

    public void setOutFileName(String outFileName) {
        this.outFileName = outFileName;
    }

    private final void print() {
        BufferedOutputStream bos;
        FileOutputStream fos;
        OutputStreamWriter dos;

        Set keySet = hm.keySet();
        Iterator it = keySet.iterator();
        int size = hm.size();


        System.out.println("@sl-code:$");
        System.out.println("@tl-code:$");
        System.out.println("@sl-full:$");
        System.out.println("@tl-full:$");
        System.out.println("@size:" + size + "$");

        while (it.hasNext()) {
            String key = (String) it.next();
            Vector<EElement> v = (Vector) hm.get(key);
            System.out.print("[" + key + "]");
            for (EElement ee : v) {
                String slLemma = ee.getValueNoTags("L");
                SElementList slPoS = ee.getSElements("L");
                System.out.print(slLemma + ":");
                for (SElement sE : slPoS) {
                    System.out.print(sE.getValue() + ".");
                }
                System.out.print("?");
                String tlLemma = ee.getValueNoTags("R");
                SElementList tlPoS = ee.getSElements("R");
                System.out.print(tlLemma + ":");
                for (SElement sE : tlPoS) {
                    System.out.print(sE.getValue() + ".");
                }
                System.out.print(";");
            }
            System.out.print("$\n");
        }
    }

    /**
     * 
     * @param hm
     * @return
     */
    private final Vector map2vector(HashMap hm) {
        Vector<Entry> vector = new Vector<Entry>();

        Set keySet = hm.keySet();
        Iterator it = keySet.iterator();
        //int size = hm.size();

        while (it.hasNext()) {
            Entry entry = new Entry();
            String key = (String) it.next();
            entry.setKey(key);
            Vector<EElement> v = (Vector) hm.get(key);
            String value = "";
            value = value + "[" + key + "]";
            for (EElement ee : v) {
                String slLemma = ee.getValueNoTags("L");
                SElementList slPoS = ee.getSElements("L");
                value = value + slLemma + ":";
                for (SElement sE : slPoS) {
                    value = value + sE.getValue() + ".";
                }
                value = value + "?";
                String tlLemma = ee.getValueNoTags("R");
                SElementList tlPoS = ee.getSElements("R");
                value = value + tlLemma + ":";
                for (SElement sE : tlPoS) {
                    value = value + sE.getValue() + ".";
                }
                value = value + ";";
            }
            value = value + "$\n";
            entry.setValue(value);
            vector.add(entry);
        }
        return vector;
    }

    /**
     * 
     */
    private class Entry implements Comparable<Entry> {

        /**
         * 
         */
        private String key;
        /**
         * 
         */
        private String value;

        /**
         * 
         */
        public Entry() {

        }

        /**
         * 
         * @param key
         * @param value
         */
        public Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int compareTo(Entry anotherEntry) {
            String lemma2 = anotherEntry.getKey();
            String lemma1 = this.getKey();

            if (lemma1 == null || lemma2 == null) {
                return 0;
            } else {
                if (lemma1.compareTo(lemma2) == 0) {
                    return 0;
                }
                if (lemma1.compareTo(lemma2) < 0) {
                    return -1;
                }
            }
            return 1;

        }
    }

    public class ZipIt {

        private String zipFileName;
        private Vector<String> files;

        public ZipIt(Vector<String> files, String zipFileName) {
            this.files = files;
            this.zipFileName = zipFileName;
        }

        public final void zip() {
            try {
                /*
                if (args.length < 2) {
                System.err.println("usage: java ZipIt Zip.zip file1 file2 file3");
                System.exit(-1);
                }
                 */
                File zipFile = new File(this.zipFileName);
                if (zipFile.exists()) {
                    System.err.println("Zip file already exists, please try another");
                    System.exit(-2);
                }
                FileOutputStream fos = new FileOutputStream(zipFile);
                ZipOutputStream zos = new ZipOutputStream(fos);
                int bytesRead;
                byte[] buffer = new byte[1024];
                CRC32 crc = new CRC32();
                for (int i = 0,  n = files.size(); i < n; i++) {
                    String name = files.elementAt(i);
                    //System.out.println("adding " + name);
                    File file = new File(name);
                    if (!file.exists()) {
                        System.err.println("Skipping: " + name);
                        continue;
                    }
                    BufferedInputStream bis = new BufferedInputStream(
                            new FileInputStream(file));
                    crc.reset();
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        crc.update(buffer, 0, bytesRead);
                    }
                    bis.close();
                    // Reset to beginning of input stream
                    bis = new BufferedInputStream(
                            new FileInputStream(file));
                    ZipEntry entry = new ZipEntry(name);
                    entry.setMethod(ZipEntry.STORED);
                    entry.setCompressedSize(file.length());
                    entry.setSize(file.length());
                    entry.setCrc(crc.getValue());
                    zos.putNextEntry(entry);
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        zos.write(buffer, 0, bytesRead);
                    }
                    bis.close();
                }
                zos.close();
            } catch (IOException ioe) {

            }
        }
    }
}

