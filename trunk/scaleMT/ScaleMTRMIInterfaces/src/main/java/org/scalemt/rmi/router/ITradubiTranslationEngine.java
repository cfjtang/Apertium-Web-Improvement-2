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
package org.scalemt.rmi.router;

import org.scalemt.rmi.exceptions.TranslationEngineException;
import org.scalemt.rmi.exceptions.UnsupportedLanguagePairException;
import org.scalemt.rmi.transferobjects.LanguagePair;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * Set of operations that should be performed by a translation engine. Apart from translating with its dictionaries,
 * the translation engine should be able to manage personal dictionaries, that are made of pairs of words or phrases.
 * One can specify a personal dictionary when performing a translation, and words from the input text found in it are
 * directly replaced and not translated by the translation engine rules.
 * 
 * 
 * @author vmsanchez
 *
 */
public interface ITradubiTranslationEngine extends Remote{
	
	/**
	 * Translates a piece of unformatted TEXT.
	 * 
	 * @param sourceText text to translate.
	 * @param sourceLang Source language.
	 * @param targetLang Target language.
	 * @param dictionaries Hierarchy of dictionaries to use
	 * @return Translation of the text
	 * @throws TranslationEngineException
	 * @throws UnsupportedLanguagePairException
	 */
	public String translateText(String sourceText, String sourceLang, String targetLang, List<Long> dictionaries) throws  TranslationEngineException,UnsupportedLanguagePairException, RemoteException;
	
	/**
	 * Translates a piece of html code
	 * 
	 * @param sourceHtml Html code to translate.
	 * @param sourceLang Source language.
	 * @param targetLang Target language.
	 * @param dictionaries Hierarchy of dictionaries to use
	 * @return Translation of the HTML code
	 * @throws TranslationEngineException
	 * @throws UnsupportedLanguagePairException
	 */
	public String translateHTML(String sourceHtml, String sourceLang, String targetLang, List<Long> dictionaries) throws  TranslationEngineException,UnsupportedLanguagePairException, RemoteException;
	
	/**
	 * 
	 * 
	 * @return Set of language pairs supported by the translation engine
	 * @throws RemoteException
	 */
	public Set<LanguagePair> getSupportedPairs() throws RemoteException;
	
	/**
	 * Compiles a dictionary, so the translation engine will be able to use it.
	 * This method should be called each time a change is made in the dictionary if we want
	 * the translation engine to apply the change 
	 * 
	 * @param dictionaryId
	 * @param entries 
	 * @throws TranslationEngineException
	 */
	public void compileDictionary(long dictionaryId, Map<String,String> entries, String pair) throws  TranslationEngineException,RemoteException;
	
	/**
	 * Removes a compiled dictionary
	 * 
	 * @param dictionaryid Id of the dictionary to be removed
	 * @throws TranslationEngineException
	 */
	public void removeDictionary(long dictionaryid) throws  TranslationEngineException,RemoteException;
}
