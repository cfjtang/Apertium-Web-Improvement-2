/*
 * Copyright (C) 2009 Enrique Benimeli Bofarull <ebenimeli.dev@gmail.com>
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

import android.text.Html;
import android.text.Spanned;
import java.net.*;
import java.io.*;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class ApertiumWS {

    private String sl;
    private String tl;
    private String text;
    private String translation;
    private boolean markUnknown;

    public ApertiumWS(String sl, String tl, String text) {
        this.sl = sl;
        this.tl = tl;
        this.text = text;
        this.markUnknown = false;
    }

    public final void setMarkUnknown(boolean value) {
        this.markUnknown = value;
    }

    public final boolean isMarkUnknown() {
        return this.markUnknown;
    }


    public final String translate() {
        String nextLine;
        URL url = null;
        URLConnection urlConn = null;
        InputStreamReader inStream = null;
        BufferedReader buff = null;
        try {
            text = URLEncoder.encode(text,"UTF-8");
            String mark = "0";
            if( this.isMarkUnknown()) {
                mark = "1";
            }
            String theURL = "http://xixona.dlsi.ua.es/webservice/ws.php?mark=" + mark + "&mode=" + sl + "-" + tl + "&text=" + text + "";
            url = new URL(theURL);
            urlConn = url.openConnection();
            inStream = new InputStreamReader(urlConn.getInputStream());
            buff = new BufferedReader(inStream);

            // Read and print the lines from index.html
            String trans = "";
            while (true) {
                nextLine = buff.readLine();
                if (nextLine != null) {
                    //System.out.println(nextLine);
                    trans += nextLine;
                } else {
                    break;
                }
            }
            this.setTranslation(trans);
            
            translation = translation.replaceAll("<libtranslate>", "");
            translation = translation.replaceAll("</libtranslate>", "");
            translation = ((Spanned)Html.fromHtml(translation)).toString();
            return this.translation;
        } catch (MalformedURLException e) {
            System.out.println("Please check the URL:" +
                    e.toString());
            return e.toString();
        } catch (IOException e1) {
            System.out.println("Can't read  from the Internet: " +
                    e1.toString());
            return e1.toString() + "/ " +  e1.getMessage();
        }
        //return "No funciona.";
    }

    /**
     * @return the translation
     */
    public String getTranslation() {
        return translation;
    }

    /**
     * @param translation the translation to set
     */
    public void setTranslation(String translation) {
        this.translation = translation;
    }
}

