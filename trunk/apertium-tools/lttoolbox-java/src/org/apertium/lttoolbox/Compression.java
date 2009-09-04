package org.apertium.lttoolbox;/*
 * Copyright (C) 2005 Universitat d'Alacant / Universidad de Alicante
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


import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;

public class Compression {

    static void multibyte_write(int value, Writer output) throws IOException {
        if (value < 0x00000040) {
            output.write((char) value);
        } else if (value < 0x00004000) {
            char low = (char) value;
            char up = (char) (value >> 8);
            up |= 0x40;
            output.write(up);
            output.write(low);
        } else if (value < 0x00400000) {
            char low = (char) value;
            char middle = (char) (value >> 8);
            char up = (char) (value >> 16);
            up |= 0x80;
            output.write(up);
            output.write(middle);
            output.write(low);
        } else if (value < 0x40000000) {
            char low = (char) value;
            char middlelow = (char) (value >> 8);
            char middleup = (char) (value >> 16);
            char up = (char) (value >> 24);
            up |= 0xc0;
            output.write(up);
            output.write(middleup);
            output.write(middlelow);
            output.write(low);
        } else {
            throw new RuntimeException("Out of range: " + value);

        }
    }

    void multibyte_write2(int value, Writer output) throws IOException {
        if (value < 0x00000040) {
            char b = (char) value;
            output.write((b));
        } else if (value < 0x00004000) {
            char low = (char) value;
            char up = (char) (value >> 8);
            up |= 0x40;
            output.write((up));
            output.write((low));
        } else if (value < 0x00400000) {
            char low = (char) value;
            char middle = (char) (value >> 8);
            char up = (char) (value >> 16);
            up |= 0x80;
            output.write((up));
            output.write((middle));
            output.write((low));

        } else if (value < 0x40000000) {
            char low = (char) value;
            char middlelow = (char) (value >> 8);
            char middleup = (char) (value >> 16);
            char up = (char) (value >> 24);
            up |= 0xc0;

            output.write((up));
            output.write((middleup));
            output.write((middlelow));
            output.write((low));
        } else {
            throw new RuntimeException("Out of range: " + value);

        }
    }

    int multibyte_read2(FileInputStream input) throws IOException {
        char up;
        int result = 0;

        up = (char) input.read();
        if (up < 0x40) {
            result = (int) up;
        } else if (up < 0x80) {
            up &= 0x3f;
            int aux = (int) up;
            aux = aux << 8;
            char low = (char) input.read();
            result = (int) low;
            result |= aux;
        } else if (up < 0xc0) {
            up &= 0x3f;
            int aux = (int) up;
            aux = aux << 8;
            char middle = (char) input.read();
            result = (int) middle;
            aux |= aux;
            aux = aux << 8;
            char low = (char) input.read();
            result = (int) low;
            result |= aux;
        } else {
            up &= 0x3f;
            int aux = (int) up;
            aux = aux << 8;
            char middleup = (char) input.read();
            result = (int) middleup;
            aux |= aux;
            aux = aux << 8;
            char middlelow = (char) input.read();
            result = (int) middlelow;
            aux |= aux;
            aux = aux << 8;
            char low = (char) input.read();
            result = (int) low;
            result |= aux;
        }

        return result;
    }

    public static int multibyte_read(FileInputStream input) throws IOException {
        char up;
        int result = 0;

        up = (char) input.read();
        if (up < 0x40) {
            result = (int) up;
        } else if (up < 0x80) {
            up &= 0x3f;
            int aux = (int) up;
            aux = aux << 8;
            char low = (char) input.read();
            result = (int) low;
            result |= aux;
        } else if (up < 0xc0) {
            up &= 0x3f;
            int aux = (int) up;
            aux = aux << 8;
            char middle;
            middle = (char) input.read();
            result = (int) middle;
            aux |= aux;
            aux = aux << 8;
            char low;
            low = (char) input.read();
            result = (int) low;
            result |= aux;
        } else {
            up &= 0x3f;
            int aux = (int) up;
            aux = aux << 8;
            char middleup;
            middleup = (char) input.read();
            result = (int) middleup;
            aux |= aux;
            aux = aux << 8;
            char middlelow;
            middlelow = (char) input.read();
            result = (int) middlelow;
            aux |= aux;
            aux = aux << 8;
            char low;
            low = (char) input.read();
            result = (int) low;
            result |= aux;
        }

        return result;
    }

    static void String_write(String str, Writer output) throws IOException {
        multibyte_write(str.length(), output);
        for (int i = 0,  limit = str.length(); i != limit; i++) {
            multibyte_write((int) (str.charAt(i)), output);
        }
    }

    String String_read(FileInputStream input) throws IOException {
        String retval = "";
        for (int i = 0,  limit = multibyte_read(input); i != limit; i++) {
            retval += (multibyte_read(input));
        }
        return retval;
    }
}
