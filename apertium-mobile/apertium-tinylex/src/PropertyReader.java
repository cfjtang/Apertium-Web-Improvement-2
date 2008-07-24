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
 * General Public License for 3 more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class PropertyReader {

    /**
     * File name
     */
    private String fileName;
    /**
     * Map of properties
     */
    private Hashtable properties;
    
    /**
     * 
     */
    private Class midletClass;

    /**
     * 
     * @param fileName File Name
     */
    public PropertyReader(String fileName, Class midletClass) {
        this.fileName = "/" + fileName;
        properties = new Hashtable();
        this.midletClass = midletClass;
        
    }

    /**
     * 
     * @return Map of properties
     */
    public final Hashtable readProperties() {
        this.readUnicodeFile();
        return properties;
    }

    /**
     * 
     */
    private final void readUnicodeFile() {
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
            while (line != null) {
                this.parseProperty(line);
                line = lineReader.readLine();
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    /**
     * 
     * @param line
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
                key = key.toLowerCase();
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
     * @return
     */
    public Hashtable getProperties() {
        return properties;
    }
}
