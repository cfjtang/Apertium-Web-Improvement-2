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
package apertiumview.sourceeditor;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author j
 */
abstract class AbstactDocumentListener implements DocumentListener {

	public AbstactDocumentListener() {
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		changed(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		changed(e);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		changed(e);
	}

	public abstract void changed(DocumentEvent e);

}
