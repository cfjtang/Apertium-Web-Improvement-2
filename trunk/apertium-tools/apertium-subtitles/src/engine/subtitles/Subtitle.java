/*
 * Copyright (C) 2008-2009 Enrique Benimeli Bofarull <ebenimeli.dev@gmail.com>
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
package engine.subtitles;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Subtitle {

    private String id;
    private String time;
    private ArrayList<String> lines;
    private String take;
    private String sentenceBreakTag;

    public Subtitle() {
        lines = new ArrayList();
        take = "";
        sentenceBreakTag = "<br/>";
    }

    public boolean isEmpty() {
        return (id == null);

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList getLines() {
        return lines;
    }

    public void setLines(ArrayList lines) {
        this.lines = lines;
    }

    public void addLine(final String line) {
        lines.add(line);
        take = take + line + sentenceBreakTag;
    }

    public void print() {
        if (id != null) {
            System.out.println(id);
            System.out.println(time);
            for (int i = 0; i < lines.size(); i++) {
                String theText = lines.get(i);
                theText = theText.replaceAll(sentenceBreakTag, "\n");
                System.out.println(theText);
            }
            //System.out.println("sentence: " + take);
            System.out.println("");
        }
    }

    public void printTo(OutputStreamWriter dos) {
        try {
            if (id != null) {
                dos.write(id + "\n");
                dos.write(time + "\n");
                for (int i = 0; i < lines.size(); i++) {
                    String theText = lines.get(i);
                    theText = theText.replaceAll(sentenceBreakTag, "\n");
                    dos.write(theText);
                }
                dos.write("\n");
            }

        } catch (IOException ioe) {
        }
    }

    public String getTake() {
        return take;
    }

    public void setTake(String take) {
        this.take = take;
    }
}
