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
package dictools.crossmodel;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class CrossModel {

    /**
     * 
     */
    private CrossActionList crossActions;
    /**
     * 
     */
    private String fileName;
    /**
     * 
     */
    private String filePath;
    /**
     * 
     */
    private String encoding = "UTF-8";

    /**
     * 
     * 
     */
    public CrossModel() {
        crossActions = new CrossActionList();
    }

    /**
     * 
     * @param crossAction
     */
    public void addCrossAction(final CrossAction crossAction) {
        crossActions.add(crossAction);
    }

    /**
     * 
     * @return Undefined         */
    public CrossActionList getCrossActions() {
        return crossActions;
    }

    /**
     * 
     * @param id
     * @return Undefined         */
    public final CrossAction getCrossAction(final String id) {
        for (final CrossAction ca : getCrossActions()) {
            if (ca.getId().equals(id)) {
                return ca;
            }
        }
        return null;
    }

    /**
     * 
     * @param fileName
     */
    public void printXML(final String fileName) {
        this.printXML(fileName, this.getEncoding());
    }

    /**
     * 
     * @param fileName
     * @param encoding
     */
    public void printXML(final String fileName, final String encoding) {
        BufferedOutputStream bos;
        FileOutputStream fos;
        OutputStreamWriter dos;

        try {
            fos = new FileOutputStream(fileName);
            bos = new BufferedOutputStream(fos);
            dos = new OutputStreamWriter(bos, encoding);
            dos.write("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");
            dos.write("<!-- Examples of patterns not found -->\n");
            dos.write("<cross-model>\n");
            int i = 0;
            CrossActionList cal = getCrossActions();
            Collections.sort(cal);
            for (CrossAction crossAction : cal) {
                crossAction.printXML(dos, i);
                i++;
            }
            dos.write("</cross-model>\n");
            dos.write("<!-- " + i + " cross actions. -->\n");

            fos = null;
            bos = null;
            dos.close();
            dos = null;
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final Exception eg) {
            eg.printStackTrace();
        }
    }

    /**
     * 
     */
    public final String getFileName() {
        return this.fileName;
    }

    /**
     * 
     */
    public final void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * 
     */
    public final String getFilePath() {
        return this.filePath;
    }

    /**
     * 
     */
    public final void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

    /**
     * 
     * @return Undefined
     */
    public final String getEncoding() {
        return this.encoding;
    }

    /**
     * 
     * @param encoding
     */
    public final void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    /**
     * 
     * @param crossActions
     */
    public final void setCrossActionList(final CrossActionList crossActions) {
        this.crossActions = crossActions;
    }

    /**
     * 
     */
    public final void rename() {
        CrossActionList rList = new CrossActionList();
        for (CrossAction cA : this.getCrossActions()) {
            rList.add(cA.rename());
        }
        this.setCrossActionList(rList);
    }

    /**
     * 
     * @return true if the cross model is valid
     */
    public final boolean isValid() {
        boolean errorsFound = false;
        for (CrossAction cA : this.getCrossActions()) {
            if (!cA.isValid()) {
                errorsFound = true;
            }
        }
        if (errorsFound) {
            return false;
        } else {
            return true;
        }
    }
}
