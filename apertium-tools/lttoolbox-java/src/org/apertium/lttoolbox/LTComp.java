package org.apertium.lttoolbox;

/*
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

/**
 * Language compiler
 * 
 * Created by Nic Cottrell (nic@transmachina.com) 2009-01-28
 * 
 */
public class LTComp {

    private static final String PACKAGE_VERSION = "0.1j";

    static void endProgram(String name) {
        if (name != null) {
            System.out.println(" v" + PACKAGE_VERSION + ": build a letter transducer from a dictionary\n" +
                    "USAGE: " + name + " lr | rl dictionary_file output_file [acx_file]\n" +
                    "Modes:\n" +
                    "  lr:     left-to-right compilation\n" +
                    "  rl:     right-to-left compilation\n");
        }

    }

    public static void main(String[] argv) throws IOException, SAXException {

        // Just for testing
        if (argv.length == 0) {
            argv = new String[]{"lr", "../../apertium-dixtools/test/sample.eo-en.dix", "out.bin"};
        }


        final int argc = argv.length;
        if (argc != 3 && argc != 4) {
            endProgram("LTComp");
            return;
        }

        String opc = argv[0];

        Compiler c = new Compiler();

        if (opc.equals("lr")) {
            if (argc == 4) {
                c.parseACX(argv[3], Compiler.COMPILER_RESTRICTION_LR_VAL);
            }
            c.parse(argv[1], Compiler.COMPILER_RESTRICTION_LR_VAL);
        } else if (opc.equals("rl")) {
            c.parse(argv[1], Compiler.COMPILER_RESTRICTION_RL_VAL);
        } else {
            endProgram("LTComp");
            return;
        }

        File textFile = new File(argv[2]);
        Writer output = new OutputStreamWriter(new FileOutputStream(textFile), "UTF-8");
        c.write(output);
        output.close();

    }
}
