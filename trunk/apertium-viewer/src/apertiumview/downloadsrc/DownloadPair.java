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
package apertiumview.downloadsrc;

import java.awt.Container;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author j
 */
public class DownloadPair {
	private final Container mp;
	private String pair;

	public DownloadPair(Container mainPanel) {
		mp = mainPanel;
	}

	public void warnUser(String txt) {
		System.out.println("warnUser(" + txt);
		JOptionPane.showMessageDialog(mp, txt, "Warning", JOptionPane.WARNING_MESSAGE);
	}

	public void download(String pair) {
		this.pair = pair;
		try {
			dl();
		} catch (Exception ex) {
			warnUser("An error occured:"+ex.toString());
		}
	}

	private void dl() throws Exception {
	}
}
