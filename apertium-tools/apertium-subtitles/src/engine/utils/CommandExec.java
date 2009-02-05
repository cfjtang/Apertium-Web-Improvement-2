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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class CommandExec {

    private String[] cmd;
    private String stdErr = "";
    private String stdOut = "";

    public CommandExec(final String[] cmd) {
        this.cmd = cmd;
    }

    public final void exec() {
        String s = "";

        try {
            Process p = Runtime.getRuntime().exec(cmd);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream(), "UTF-8"));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                this.stdOut += s;
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                this.stdErr += s;
            }
        } catch (IOException ioe) {
            System.out.println("Error running Apertium");
            ioe.printStackTrace();
            System.exit(-1);
        }
    }

    public final String getStdOut() {
        return this.stdOut;
    }

    public final String getStdErr() {
        return this.stdErr;
    }
}
