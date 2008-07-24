

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
import java.io.Reader;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class LineReader {

    /**
     * 
     */
    private Reader r;

    /**
     * 
     * @param reader
     */
    public LineReader(Reader reader) {
        this.r = reader;

    }

    /**
     * 
     * @return
     */
    public final String readLine() {
        String s = "";
        try {
            int c = r.read();
            if (c == -1) {
                return null;
            }
            while (c != -1 && c != '\n' && c != '\r') {
                s += (char) c;
                c = r.read();
            }
        } catch (Exception e) {
        }
        return s;
    }
}
