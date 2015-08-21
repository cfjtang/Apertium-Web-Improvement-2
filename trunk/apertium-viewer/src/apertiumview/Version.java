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

import static apertiumview.ApertiumView.prefs;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author j
 */
public class Version {
	public static final String APERTIUM_VIEWER_VERSION = "2.5.2";

	static String checkForNewVersion() throws IOException {
		final String REPO_URL = "http://javabog.dk/filer/apertium/apertium-viewer-newest-version.txt";
		StringBuilder sb = new StringBuilder();
		InputStreamReader isr = new InputStreamReader(new URL(REPO_URL).openStream());
		int ch = isr.read();
		while (ch != -1) { sb.append((char)ch); ch = isr.read(); }
		isr.close();
		int n = sb.indexOf("\n");
		String newestVersion = sb.substring(0, n).trim();
		if (APERTIUM_VIEWER_VERSION.equals(newestVersion)) return null;
		String ret = "\nA new version of Apertium-viewer is available\nCurrent version: "+APERTIUM_VIEWER_VERSION
				+"\nNew version: "+sb;
		return ret;
	}

}
