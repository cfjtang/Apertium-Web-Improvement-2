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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author j
 */
public class PairLocator {

	private static void execAndCollect(String[] cmd, ArrayList<Path> allmodes) {
		try {
			File modes_xmll = File.createTempFile("apertium-viewer-modes", "out");
			File modes_xmle = File.createTempFile("apertium-viewer.modes", "err");
			final Process p = new ProcessBuilder(cmd).redirectOutput(modes_xmll).redirectError(modes_xmle).start();
			// Wait for up to 10 secs before destroying the process
			new Thread() {
				public void run() {
					try { Thread.sleep(10000); } catch (InterruptedException ex) {}
					p.destroy();
				}
			}.start();

			int ret = p.waitFor();
			p.destroy();
			System.out.println("ret="+ret + " "+modes_xmll);

			for (String line : Files.readAllLines(Paths.get(modes_xmll.getPath()), Charset.defaultCharset())) {
				File modes_xml = new File(line);
				if (!modes_xml.getName().equals("modes.xml")) continue; // wrong name, like 'action_modes.xml' etc, or error message
				if (!modes_xml.canRead()) continue;
				if (modes_xml.getParentFile().getName().matches("apertium-...")) continue; // Filter out directories matching apertium-??? as they contain monolingual resources
				if (modes_xml.getParent().contains("Thrash")) continue; // Filter 'deleted' files

				Path dir = Paths.get(line).getParent();
				if (!Files.exists(dir)) continue;
				ArrayList<Path> modes = new ArrayList<>();
				DirectoryStream<Path> ds;
				for (Path m: ds = Files.newDirectoryStream(dir, "??-??.mode")) modes.add(m); ds.close();
				for (Path m: ds = Files.newDirectoryStream(dir, "???-???.mode")) modes.add(m); ds.close();
				System.out.println("01modes_xml="+modes_xml+" gave:"+modes);
				if (modes.isEmpty()) for (Path m: ds = Files.newDirectoryStream(dir , "*.mode")) modes.add(m); ds.close();
				System.out.println("02modes_xml="+modes_xml+" gave:"+modes);

				
				if (Files.exists(dir = dir.resolve("modes")) && modes.isEmpty()) { // none yet, see in modes/ subdir
					for (Path m: ds = Files.newDirectoryStream(dir, "??-??.mode")) modes.add(m); ds.close();
					for (Path m: ds = Files.newDirectoryStream(dir, "???-???.mode")) modes.add(m); ds.close();
					System.out.println("11modes_xml="+modes_xml+" gave:"+modes);
					if (modes.isEmpty()) for (Path m: ds = Files.newDirectoryStream(dir , "*.mode")) modes.add(m); ds.close();
					System.out.println("12modes_xml="+modes_xml+" gave:"+modes);
				}
				allmodes.addAll(modes);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	static ArrayList<Path> searchForDevPairs() {
		ArrayList<Path> allmodes = new ArrayList<>();
		String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		if (os.contains("mac") || os.contains("darwin")) {
			execAndCollect(new String[] {"mdfind", "'kMDItemDisplayName==modes.xml'"}, allmodes);
		} else {
			execAndCollect(new String[] {"/bin/sh","-c","locate -eLb 'modes.xml'"}, allmodes);
		}
		execAndCollect(new String[] {"/bin/sh","-c","find ~ -maxdepth 5 -type d -name 'apertium-*-*' -print0 | xargs -0 -I{} ls '{}/modes.xml' 2>/dev/null"}, allmodes);

		TreeMap<Path,ArrayList<Path>> alldirs = new TreeMap<>();
		ArrayList<Path> allmodes2 = new ArrayList<Path>(new TreeSet<Path>(allmodes)); // sort + uniq;
		System.out.println("allmodes2="+allmodes2);
		// count number of modes in each dir
		for (Path m : allmodes2) {
			Path d = m.getParent();
			ArrayList<Path> n = alldirs.get(d);
			if (n==null) alldirs.put(d, n=new ArrayList<Path>());
			n.add(m);
		}
		System.out.println("alldirs="+alldirs);
		// Filter the list of modes where there is a parent mode
		allmodes.clear();
		for (Path dir : alldirs.keySet()) {
			// Filter the list of modes where there is a mode in parent directory
			if (alldirs.containsKey(dir.getParent())) continue;
			ArrayList<Path> n = alldirs.get(dir);
			if (n.size()>4) {
				Collections.sort(n, new Comparator<Path>(){
					public int compare(Path o1, Path o2) {
						return o1.toString().length() - o2.toString().length();
					}
				});
				n.retainAll(n.subList(0, 4)); // Only keep the 4 shortest modes in each dir
			}
			allmodes.addAll(n);
		}
		return allmodes;
	}

	public static void main(String[] args) throws InterruptedException {
//System.getProperties().list(System.out);
		while (true) {
			execAndCollect(new String[] {"/bin/sh","-c","locate -eLb 'modes.xml'"}, new ArrayList<Path>());
			System.gc();
			Thread.sleep(10);
			for (Path s : searchForDevPairs()) System.out.println("res : "+s);
	
		}
	}
}
