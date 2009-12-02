/*
 *  ScaleMT. Highly scalable framework for machine translation web services
 *  Copyright (C) 2009  Víctor Manuel Sánchez Cartagena
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.scalemt.rmi.exceptions;


/**
 * Exception thrown when there is not running daemon capable to perform a translation with the requested language pair.
 *
 * @author vmsanchez
 */
public class NotAvailableDaemonException extends TranslationEngineException {

	
	private static final long serialVersionUID = 221428828192184987L;

	public NotAvailableDaemonException() {
		
	}

	public NotAvailableDaemonException(String arg0) {
		super(arg0);
		
	}

	public NotAvailableDaemonException(Throwable arg0) {
		super(arg0);
		
	}

	public NotAvailableDaemonException(String arg0, Throwable arg1) {
		super(arg0, arg1);

	}

}
