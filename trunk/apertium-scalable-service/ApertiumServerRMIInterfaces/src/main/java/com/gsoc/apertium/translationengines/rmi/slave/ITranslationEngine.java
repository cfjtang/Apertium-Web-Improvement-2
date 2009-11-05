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
package com.gsoc.apertium.translationengines.rmi.slave;

import com.gsoc.apertium.translationengines.rmi.exceptions.TranslationEngineException;
import com.gsoc.apertium.translationengines.rmi.transferobjects.DaemonInformation;
import com.gsoc.apertium.translationengines.rmi.transferobjects.LanguagePair;
import com.gsoc.apertium.translationengines.rmi.transferobjects.ServerInformationTO;
import com.gsoc.apertium.translationengines.rmi.transferobjects.ServerStatusTO;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;





/**
 * 
 * RMI interface with a set of operations that should be performed by a translation engine. Apart from translating,
 * the translation engine should be able to start and stop daemons required by the Request Router.
 * 
 * 
 * @author vmsanchez
 *
 */
public interface ITranslationEngine extends Remote{

    /**
     * Translates a piece of unformatted text.
     *
     * @param sourceText Text to be translated
     * @param pair Language pair.
     * @param  dictionaries List of Tradubi dictionaries
     * @return Translated text.
     * @throws com.gsoc.apertium.translationengines.rmi.exceptions.TranslationEngineException If there is
     * an unexpected exception while translating.
     * @throws java.rmi.RemoteException If there is
     */
	public String translateText(String sourceText, LanguagePair pair, List<Long> dictionaries) throws  TranslationEngineException, RemoteException;
	
	/**
     * Translates a piece of html code.
     *
     * @param sourceHtml Html code to be translated
     * @param pair Language pair.
     * @param  dictionaries List of Tradubi dictionaries
     * @return Translated html
     * @throws com.gsoc.apertium.translationengines.rmi.exceptions.TranslationEngineException If there is
     * an unexpected exception while translating.
     * @throws java.rmi.RemoteException If there is
     */
	public String translateHTML(String sourceHtml, LanguagePair pair, List<Long> dictionaries) throws  TranslationEngineException, RemoteException;
	
	/**
     * Gets server static information, i.e., that doesn't change during server's lifecycle.
     * 
     * @return Server's static information, like memory capacity, cpu capacity, etc.
     * @throws java.rmi.RemoteException If there is a communication problem
     */
	public ServerInformationTO getServerInformation() throws RemoteException;
	
	
	/**
     * Gets server dynamic information, i.e., that changes during server's lifecycle.
     *
     * @return Server's dynamic information, like running daemons, load, etc.
     * @throws java.rmi.RemoteException If there is a communication problem.
     * @throws TranslationEngineException If the server is stopped
     */
	public ServerStatusTO getServerStatus()  throws RemoteException,TranslationEngineException;
	
    /**
     * Starts a daemon capable of translating texts with the giiven pair.
     *
     * @param pair Language pair that can be translated with the new daemon.
     * @return New daemon information
     * @throws java.rmi.RemoteException If there is a communication problem.
     * @throws com.gsoc.apertium.translationengines.rmi.exceptions.TranslationEngineException If the language pair is not supported
     */
	public DaemonInformation startDaemon(LanguagePair pair) throws RemoteException,TranslationEngineException;

    /**
     *  Starts some daemons capable of translating texts with the given pair.
     *
     * @param pair Language pair that can be translated with the new daemons.
     * @param numInstances Number of daemons to start
     * @throws java.rmi.RemoteException If there is a communication problem.
     * @throws com.gsoc.apertium.translationengines.rmi.exceptions.TranslationEngineException If the language pair is not supported
     */
	public void startDaemons(LanguagePair pair, int numInstances) throws RemoteException,TranslationEngineException;

    /**
     *  Stops some daemons capable of translating texts with the given pair.
     *
     * @param pair Language pair that can be translated with the daemons to stop.
     * @param numInstances Number of daemons to stop
     * @throws java.rmi.RemoteException If there is a communication problem.
     * @throws com.gsoc.apertium.translationengines.rmi.exceptions.TranslationEngineException If there is an unexpected error.
     */
	public void stopDaemons(LanguagePair pair, int numInstances) throws RemoteException,TranslationEngineException;
	
	/**
	 * Stops a daemon
	 * @param daemonId Daemon ID.
	 * @throws RemoteException  If there is a communication problem.
	 * @throws TranslationEngineException If there is an unexpected error.
	 */
	public void stopDaemon(long daemonId) throws RemoteException,TranslationEngineException;

    /**
     *  Stops all the daemons daemons capable of translating texts with the given pair.
     * @param pair Language pair that can be translated with the daemons to stop.
     * @throws java.rmi.RemoteException If there is a communication problem.
     * @throws com.gsoc.apertium.translationengines.rmi.exceptions.TranslationEngineException If there is an unexpected error.
     */
	public void stopAllDaemonsLanguage(LanguagePair pair) throws RemoteException,TranslationEngineException;

    /**
     * Completely stops the translation engine. It won't perform more translations nor
     * execute more methods from this interface.
     *
     * @throws java.rmi.RemoteException If there is a communication problem.
     */
    public void stop() throws RemoteException;
	
}
