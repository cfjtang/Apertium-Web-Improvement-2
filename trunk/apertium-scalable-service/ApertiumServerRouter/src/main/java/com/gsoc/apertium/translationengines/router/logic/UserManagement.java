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
package com.gsoc.apertium.translationengines.router.logic;

import com.gsoc.apertium.translationengines.router.persistence.ExistingNameException;
import com.gsoc.apertium.translationengines.router.persistence.IUserDAO;
import com.gsoc.apertium.translationengines.router.persistence.UserDAOFactory;
import com.gsoc.apertium.translationengines.router.persistence.UserEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Manages users registered with the application. Allows registering users and
 * checking their key.
 * @author vmsanchez
 */
public class UserManagement {

    /**
     * Commons-logging logger
     */
     static Log logger = LogFactory.getLog(UserManagement.class);

     /**
      * Singleton instance
      */
    private static UserManagement instance=null;

    /**
     * Gets the singleton instance
     * @return Singleton instance
     */
    public static UserManagement getInstance()
    {
        if(instance==null)
            instance=new UserManagement();
        return instance;
    }

    /**
     * <code>IUserDAO</code> reference to perform database operations.
     */
    private IUserDAO dao;

    /**
     * Default constructor with no parameters.
     */
    private UserManagement()
    {
        dao=UserDAOFactory.getUserDAO();
    }

    /**
     * Registers a new user and generates his/her API key
     *
     * @param userName User name
     * @return API key, or null if there is any error
     */
    public String registerUser(String userName)
    {
        try
        {
            return dao.createUser(userName).getApiKey().toString();
        }
        catch(ExistingNameException e)
        {
            return null;
        }
        catch(Exception e)
        {
            logger.error("Exception registering user", e);
            return null;
        }
    }

    /**
     * Checks if the key belong to a registered user
     * @param key API key
     * @return <code>true</code> if the key belongs to a registered user, <code>false</code> if it doesn't belong or there is an unexpected error
     */
    public boolean isKeyValid(String key)
    {
        try
        {
        UserEntity user = dao.getUser(key);
        if(user!=null)
            return true;
        else
            return false;
        }
        catch(Exception e)
        {
            logger.error("Exception checking key", e);
            return false;
        }
    }

}
