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

import org.xml.sax.SAXException;

import java.io.*;

public class LTExpand {

  private static final String PACKAGE_VERSION = "0.1j";

  static void endProgram(String name) {
    if (name != null) {
      System.out.println(" v" + PACKAGE_VERSION + ": expand the contents of a dictionary file" +
              "USAGE: " + name + " dictionary_file [output_file]");
    }

  }

  public static void main(String[] argv) throws IOException, SAXException {
      
    // Just for testing
    if (argv.length==0) {
        argv = new String[] { "2", "../../apertium-dixtools/test/sample.eo-en.dix" };
    }


    InputStreamReader input;
    Writer output = null;

    switch ((Integer.parseInt(argv[0]))) {
      case 2:
        input = fread(argv[1]);
        if (input == null) {
          throw new RuntimeException("Error: Cannot open file '" + argv[1] + "'.");

        }
        input.close();
        output = new OutputStreamWriter(System.out);
        break;

      case 3:
        input = fread(argv[1]);
        if (input == null) {
          throw new RuntimeException("Error: Cannot open file '" + argv[1] + "'.");

        }
        input.close();

        output = fwrite(argv[2]);
        if (output == null) {
          throw new RuntimeException("Error: Cannot open file '" + argv[2] + "'.");

        }
        break;

      default:
        endProgram("LTExpand");
        break;
    }

    assert output != null;
    
    Expander e = new Expander();
    e.expand(argv[1], output);
    output.close();

  }

  private static InputStreamReader fread(String s) throws FileNotFoundException {
    final File f = new File(s);
    return new InputStreamReader(new FileInputStream(f));
  }

  private static Writer fwrite(String s) throws FileNotFoundException {
    final File f = new File(s);
    return new OutputStreamWriter(new FileOutputStream(f));
  }

}