/*
 *  ApertiumServer. Highly scalable web service implementation for Apertium.
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
package com.gsoc.apertium.translationengines.rmi.exceptions;

/**
 * Any unexpected exception produced while performing a translation.
 * 
 * @author vmsanchez
 *
 */
public class TranslationEngineException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TranslationEngineException() {
		
	}

	public TranslationEngineException(String arg0) {
		super(arg0);
		
	}

	public TranslationEngineException(Throwable arg0) {
		super(arg0);
		
	}

	public TranslationEngineException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		
	}

}
