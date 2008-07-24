

/*
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import javax.microedition.lcdui.Gauge;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class DicReader {

    /**
     * 
     */
    private String fileName;
    /**
     * 
     */
    private Dictionary dic;
    /**
     * 
     */
    private Hashtable properties;
    /**
     * 
     */
    private Gauge progressBar;
   /**
     * 
     */
    private Class midletClass;

    /**
     * 
     * @param fileName
     */
    public DicReader(final String fileName, Class midletClass) {
        this.fileName = "/" + fileName;
        properties = new Hashtable();
        this.midletClass = midletClass;
    }

    /**
     * 
     * @return The dictionary element
     */
    public final Dictionary read(Gauge progressBar) {
        this.progressBar = progressBar;
        this.readUnicodeFile();
        return dic;
    }

    /**
     * 
     */
    private final void readUnicodeFile() {
        dic = new Dictionary();
        InputStream is = null;
        InputStreamReader isr = null;
        try {
            Class c = this.midletClass;
            is = c.getResourceAsStream(fileName);
           if (is == null) {
                throw new Exception("File '" + fileName + "' does not exist");
            }

            isr = new InputStreamReader(is, "UTF8");
            LineReader lineReader = new LineReader(isr);
            String line = lineReader.readLine();
            double size = -1;
            double n = 0;
            while (line != null) {
                if (line.startsWith("@")) {
                    this.parseProperty(line);
                    if (properties.containsKey("size")) {
                        size = (double) Integer.parseInt((String) properties.get("size"));
                    }
                } else {
                    n++;
                    int perc = this.getProgress(n, size);
                    if (perc % 5 == 0) {
                        this.updateProgressBar(perc);
                    }
                    EntryLine entry = this.parseEntry(line);
                    dic.add(entry);
                }
                line = lineReader.readLine();
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    /**
     * 
     * @param n
     * @param size
     * @return Progress
     */
    private final int getProgress(double n, double size) {
        double tmp = n / size;
        double percd = (double) (tmp * 100.0);
        int perc = (int) percd;
        return perc;
    }

    /**
     * 
     * @param perc
     */
    private final void updateProgressBar(int perc) {
        if (progressBar != null) {
            this.progressBar.setValue(perc);
            this.progressBar.setLabel(perc + "%");
        }
    }

    /**
     * 
     * @param line
     * @return
     */
    private final EntryLine parseEntry(String line) {
        EntryLine entry = new EntryLine();
        int leng = line.length();
        StringBuffer bfs = new StringBuffer();
        for (int i = 0; i < leng; i++) {
            char c = line.charAt(i);
            if (c == '[') {
            // skip
            } else {
                if (c == ']') {
                    String key = bfs.toString();
                    key = key.toLowerCase();
                    entry.setLemma(key);
                    entry.setValue(line);
                } else {
                    bfs.append(c);
                }
            }
        }
        return entry;
    }

    /**
     * 
     * @param line
     * @return The entry
     */
    private final void parseProperty(final String line) {
        int lineLength = line.length();
        StringBuffer bfs = new StringBuffer();
        String token = "";
        final int SEPARATOR = 2;
        final int NORMAL = 3;
        int lastChar = NORMAL;
        String key = "";
        String value = "";

        for (int i = 0; i < lineLength; i++) {
            lastChar = NORMAL;
            char c = line.charAt(i);

            if (c == '@') {
                lastChar = SEPARATOR;
            }

            if (c == ':') {
                token = bfs.toString();
                key = token;
                lastChar = SEPARATOR;
            }

            if (c == '$') {
                token = bfs.toString();
                value = token;
                getProperties().put(key, value);
                lastChar = SEPARATOR;
            }

            if (lastChar != SEPARATOR) {
                bfs.append(c);
            } else {
                bfs = new StringBuffer();
            }
        }
    }

    /**
     * 
     * @return Hash table with properties
     */
    public Hashtable getProperties() {
        return properties;
    }
}
