/*
 * Copyright (C) 2007 Universitat d'Alacant / Universidad de Alicante
 * Author: Enrique Benimeli Bofarull
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


import java.util.Vector;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class EntryParser {

    /**
     * 
     */
    private String line;

    /**
     * 
     * @param line
     */
    public EntryParser(String line) {
        this.line = line;
    }

    /**
     * 
     * @return
     */
    public Vector parse() {
        if (line == null) {
            return null;
        }
        Vector entries = new Vector();
        int lineLength = line.length();
        StringBuffer bfs = new StringBuffer();
        String token = "";
        final int SEPARATOR = 2;
        final int NORMAL = 3;
        int lastChar = NORMAL;
        boolean sl = false;
        Entry entry = null;
        for (int i = 0; i < lineLength; i++) {
            lastChar = NORMAL;
            char c = line.charAt(i);

            if (c == '[') {
                entry = new Entry();
                lastChar = SEPARATOR;
            }

            if (c == ']') {
                token = bfs.toString();
                lastChar = SEPARATOR;
                sl = true;
            }

            if (c == ':') {
                token = bfs.toString();
                if (sl) {
                    entry.setSlLemma(token);
                } else {
                    entry.setTlLemma(token);
                }
                lastChar = SEPARATOR;
            }

            if (c == '.') {
                token = bfs.toString();
                if (sl) {
                    entry.addSlPoS(token);
                } else {
                    entry.addTlPoS(token);
                }
                lastChar = SEPARATOR;
            }

            if (c == ';') {
                entries.addElement(entry);
                sl = true;
                entry = new Entry();
                lastChar = SEPARATOR;
            }

            if (c == '?') {
                sl = false;
                lastChar = SEPARATOR;
            }

            if (c == '$') {
                lastChar = SEPARATOR;
                return entries;
            }

            if (lastChar == SEPARATOR) {
                bfs = new StringBuffer();
            } else {
                bfs.append(c);
            }
        }
        return entries;
    }
}
