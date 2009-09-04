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

import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOpt;

import java.io.*;

import org.apertium.lttoolbox.FSTProcessor;

public class LTProc {

    private static final String PACKAGE_VERSION = "0.1j";
    
    static void endProgram(String name) {
        System.out.print(name + ": process a stream with a letter transducer\n" +
                "USAGE: " + name + " [-c] [-a|-g|-n|-d|-p|-s|-t] fst_file [input_file [output_file]]\n" +
                "Options:\n" +
                "  -a:   morphological analysis (default behavior)\n" +
                "  -c:   use the literal case of the incoming characters\n" +
                "  -g:   morphological generation\n" +
                "  -n:   morph. generation without unknown word marks\n" +
                "  -p:   post-generation\n" +
                "  -s:   SAO annotation system input processing\n" +
                "  -t:   apply transliteration dictionary\n" +
                "  -z:   flush output on the null character \n" +
                "  -v:   version\n" +
                "  -h:   show this help\n");

    }

    static void checkValidity(FSTProcessor fstp) {
        if (!fstp.valid()) {

        }
    }

    public static void main(String[] argv) throws Exception {


        if (argv.length == 0) {
            argv = new String[]{"./test/en-es.automorf.bin"};
        }

        final int argc = argv.length;

        int cmd = 0;
        FSTProcessor fstp = new FSTProcessor();

        GetOpt getopt = new GetOpt(argv, "acgndpstzvh");

        while (true) {

            int c = getopt.getNextOption();

            if (c == -1) {
                break;
            }

            switch (c) {
                case 'c':
                    fstp.setCaseSensitiveMode(true);
                    break;

                case 'a':
                case 'g':
                case 'n':
                case 'd':
                case 'p':
                case 't':
                case 's':
                    if (cmd == 0) {
                        cmd = c;
                    } else {
                        endProgram("LTProc");
                    }
                    break;

                case 'z':
                    fstp.setNullFlush(true);
                    break;

                case 'v':
                    System.out.println("org.apertium.lttoolbox.LTProc version " + PACKAGE_VERSION);
                    return;

                case 'h':
                default:
                    endProgram("LTProc");
                    break;
            }
        }

        final FileInputStream input ;
        Writer output = new OutputStreamWriter(System.out);

        if (cmd == (argc - 3)) {
            FileInputStream in = fopen(argv[cmd]);
            if (in == null) {
                endProgram("LTProc");
            }

            input = fopen(argv[cmd + 1]);
            if (input == null) {
                endProgram("LTProc");
            }

            output = fout(argv[cmd + 2]);
            if (output == null) {
                endProgram("LTProc");
            }

            fstp.load(in);
            in.close();
        } else if (cmd == (argc - 2)) {
            FileInputStream in = fopen(argv[cmd]);
            if (in == null) {
                endProgram("LTProc");
            }

            input = fopen(argv[cmd + 1]);
            if (input == null) {
                endProgram("LTProc");
            }

            fstp.load(in);
            in.close();
        } else { 
            InputStreamReader isr = new InputStreamReader (System.in);
            FileWriter fw = new FileWriter (new File("TemporaryOutputFile.txt"));
            if (isr.ready()){
                int read_char=isr.read();
                while (read_char != -1) {
                    fw.write((char) read_char);
                    read_char = isr.read();
                }
            }
            isr.close();
            fw.close();
            input=fopen("TemporaryOutputFile.txt");
            if (cmd == (argc - 1)) {
                final String filename = argv[cmd];
                FileInputStream in = fopen(filename);
                if (in == null) {
                    endProgram("LTProc");
                }
                fstp.load(in);
                in.close();
            } else {
                endProgram("LTProc");
            }
        }

        try {
                FSTProcessorSave fstp2=new FSTProcessorSave();
            switch (cmd) {
                case 'n':
                    fstp.initGeneration();
                    checkValidity(fstp);
                    fstp.generation(input, output, FSTProcessor.GenerationMode.gm_clean);
                    break;

                case 'g':
                    fstp.initGeneration();
                    checkValidity(fstp);
                    fstp.generation(input, output, FSTProcessor.GenerationMode.gm_unknown);
                    break;

                case 'd':
                    fstp.initGeneration();
                    checkValidity(fstp);
                    fstp.generation(input, output, FSTProcessor.GenerationMode.gm_all);

                case 'p':
                    fstp.initPostgeneration();
                    checkValidity(fstp);
                    fstp.postgeneration(input, output);
                    break;

                case 's':
                    fstp.initAnalysis();
                    checkValidity(fstp);
                    fstp.SAO(input, output);
                    break;

                case 't':
                    fstp.initPostgeneration();
                    checkValidity(fstp);
                    fstp.transliteration(input, output);
                    break;

                case 'a':
                default:
                    fstp.initAnalysis();
                    checkValidity(fstp);
                    fstp.analysis(input, output);
                    break;
            }
        } catch (Exception e) {
            if (fstp.getNullFlush()) {
                output.write('\0');
            }
        }

        input.close();
        output.close();
    }

    private static Writer fout(String filename) throws FileNotFoundException {
        return new OutputStreamWriter(new FileOutputStream(new File(filename)));
    }

    private static FileInputStream fopen(String filename) throws FileNotFoundException {
        return new FileInputStream (new File(filename));
    }
 
}
