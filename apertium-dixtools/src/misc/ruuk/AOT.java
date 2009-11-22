/*
 * Author: Jimmy O'Regan
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

package misc.ruuk;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.LineNumberReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 *
 * @author jimregan
 */

public class AOT {
    private String fileName;
    private ArrayList<String> parlines;
    private ArrayList<String> voclines;

    public AOT () {
        parlines = new ArrayList<String>();
        voclines = new ArrayList<String>();
    }

    public AOT (String file) {
        this();
        fileName = file;
    }

    public void setFile (String file) {
        fileName = file;
    }

    public String[] getParLines () {
        String[] list = {};
        list = parlines.toArray(list);
        return list;
    }

    public String[] getVocLines () {
        String[] list = {};
        list = voclines.toArray(list);
        return list;
    }

    public void read_aot(String encoding) {
        try {
            FileInputStream fstream = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, encoding));
            String strLine = "";

            while ((strLine = br.readLine()) != null) {
                if (!strLine.matches("^(\\d*)$")) {
                    continue;
                } else if (strLine.startsWith("%")) {
                    // Paradigm line
                    parlines.add(strLine);
                } else if (strLine.matches(" 200\\d;")) {
                    // Changelog line
                    continue;
                } else if (strLine.matches(" (\\d+) (\\d+) (\\d+) ")) {
                    // Vocabulary
                    voclines.add(strLine);
                } else {
                    System.err.println("Unknown input: " + strLine);
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

}
