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
package org.scalemt.router.logic;


import org.scalemt.rmi.transferobjects.Format;
import org.scalemt.rmi.transferobjects.LanguagePair;
import java.util.Map;


/**
 * Manages an history of the requests received by the system.
 * @author vitaka
 */
public interface IRequestHistory {
    /**
     * Records a request.
     * @param pair Language pair.
     * @param numCharacters Number of characters.
     * @param format Format
     */
    public void addRequest(LanguagePair pair, int numCharacters,Format format, Requester requester);

    /**
     * Gets the requests received since the last time this method was called.
     *
     * @return Requests received since the last time this method was called.
     */
    public Map<LanguagePair,RequestsHistoryTO> getHistoryAndReset();

    /**
     * Gets the total cpu cost of the requests sent by a user in the last period.
     *
     * @param rq
     * @return
     */
    public int getCostUser(Requester rq);
}
