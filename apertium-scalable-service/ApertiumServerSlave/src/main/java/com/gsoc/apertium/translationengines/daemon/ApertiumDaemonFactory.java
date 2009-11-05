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
package com.gsoc.apertium.translationengines.daemon;

import com.gsoc.apertium.translationengines.rmi.transferobjects.LanguagePair;
import com.gsoc.apertium.translationengines.util.ServerUtil;


/**
 * Singleton factory that creates {@link ApertiumDaemon} instances.<br/>
 * Reads the following properties from <code>configuration.properties</code>:
 * <ul>
 * <li><code>apertium_path</code>: Directory where the Apertium executable can be found</li>
 * <li><code>apertium_null_mode_suffix</code>: Suffix that, added to the language pairs, makes the name
 *     of the mode of Apertium that will be invoked. For example, if pair is es-en and mode "-null",
 *    Apertium will be invoked with the mode <code>es-en-null</code></li>
 * </ul>
 *
 * @author vmsanchez
 */
public class ApertiumDaemonFactory {

    /**
     * Singleton instance
     */
	private static ApertiumDaemonFactory instance=null;

    /**
     * Gets the singleton instance
     * @return Singleton instance
     */
	public static ApertiumDaemonFactory getInstance()
	{
		if(instance==null)
			instance=new ApertiumDaemonFactory();
		return instance;
	}
	
	
	private long lastId;
	private String modeSuffix;
	private String apertiumPath;

    /**
     * Default contructor
     */
	private ApertiumDaemonFactory() {
		lastId=0;
		
		//Read properties
		
		apertiumPath = ServerUtil.readProperty("apertium_path");
		if (apertiumPath == null)
			apertiumPath = "/usr/local/bin";
		
		modeSuffix= ServerUtil.readProperty("apertium_null_mode_suffix");
		if(modeSuffix==null)
			modeSuffix="-null";
	}

    /**
     * Creates a new daemon that can translate the given pair. The daemon ID is
     * generated.
     * @param pair Pair the new daemon will be able to translate.
     * @return New Apertium daemon.
     */
	public synchronized ApertiumDaemon newDaemon(LanguagePair pair)
	{
		return new ApertiumDaemon(lastId++,pair,modeSuffix,apertiumPath);
	}
	
}
