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
package engine.utils;

import engine.subtitles.Subtitle;
import engine.subtitles.Subtitles;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class SRTReader {

    private String fileName;
    private String fileEncoding;
    private Subtitles blockList;

    public SRTReader(String fileName) {
        this.fileName = fileName;
        this.fileEncoding = "UTF-8";
    }

    public final Subtitles read() {
        Subtitles subtitles = new Subtitles();
        try {
            FileInputStream instr = new FileInputStream(this.fileName);
            InputStreamReader instrR = new InputStreamReader(instr, this.fileEncoding);

            this.setFileEncoding(instrR.getEncoding());
            //System.out.println("Encoding: " + this.fileEncoding);
            BufferedReader input = new BufferedReader(instrR);
            //BufferedReader input = new BufferedReader(new FileReader(this.fileName));
            Subtitle subtitle = null;
            try {
                String line = null;
                int i = 0;
                subtitle = new Subtitle();
                while ((line = input.readLine()) != null) {
                    if (line.length() > 0 || line.equals("\n")) {
                        if (i == 0) {
                            if (!subtitle.isEmpty()) {
                                subtitles.add(subtitle);
                            }
                            subtitle = new Subtitle();
                            subtitle.setId(line);
                        }
                        if (i == 1) {
                            subtitle.setTime(line);
                        }
                        if (i > 1) {
                            subtitle.addLine(line);
                        }
                        i++;
                    } else {
                        i = 0;
                    }
                }
            } finally {
                subtitles.add(subtitle);
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.setBlockList(subtitles);


        return subtitles;
    }

    public Subtitles getBlockList() {
        return blockList;
    }

    public void setBlockList(Subtitles blockList) {
        this.blockList = blockList;
    }

    /**
     * @return the fileEncoding
     */
    public String getFileEncoding() {
        return fileEncoding;
    }

    /**
     * @param fileEncoding the fileEncoding to set
     */
    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }
}
