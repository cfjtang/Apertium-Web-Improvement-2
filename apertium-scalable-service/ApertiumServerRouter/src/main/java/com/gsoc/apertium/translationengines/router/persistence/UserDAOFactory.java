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

package com.gsoc.apertium.translationengines.router.persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory that creates instances of the default implementation of {@link IUserDAO}.
 * @author vmsanchez
 */
public class UserDAOFactory {

    /**
     * Commons-logging logger
     */
    static Log logger = LogFactory.getLog(UserDAOFactory.class);

    /**
     * Returns a new instance of the default implementation of {@link IUserDAO}.
     * @return A new instance of {@link JPAUserDAO}, or <code>null</code> if there is an exception creating the instance.
     */
    public static IUserDAO getUserDAO()
    {
        try
        {

            return new JPAUserDAO();
        }
        catch(Exception e)
        {
            logger.error("Exception creating DAO", e);
              return null;
        }
    }
}
