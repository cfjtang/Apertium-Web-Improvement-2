/*
 * Copyright (C) 2015 j
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package apertiumview;

import java.nio.file.Path;

/**
 *
 * @author j
 */
public class Tools {
	public static void openTerminalWindow(Path apertiumDir) {
		String[] terminals = {
			"gnome-terminal --working-directory='DIR'",
			"/usr/bin/open -a Terminal /bin/bash", // Mac
			"konsole",
			"xterm",
			"cmd c/start cmd.exe", // Windows (!)
		};
		for (String t : terminals) try {
			System.out.println(t.replaceAll("DIR", apertiumDir.toString()));
			Runtime.getRuntime().exec(t.replaceAll("DIR", apertiumDir.toString()), null, apertiumDir.toFile());
			break;
		} catch (Exception e) { System.out.println("Terminal "+t+" didnt work: "+e); 	}
	}


}
