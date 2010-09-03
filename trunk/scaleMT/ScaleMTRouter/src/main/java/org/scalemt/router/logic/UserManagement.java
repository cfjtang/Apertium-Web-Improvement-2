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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.scalemt.router.persistence.ExistingNameException;
import org.scalemt.router.persistence.IUserDAO;
import org.scalemt.router.persistence.UserDAOFactory;
import org.scalemt.router.persistence.UserEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.EmailValidator;
import org.apache.commons.validator.UrlValidator;
import org.apache.commons.validator.Validator;
import org.scalemt.router.persistence.Base64;
import org.scalemt.router.persistence.DAOException;
import org.scalemt.router.ws.WrongFormatException;

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

    private String encrypt(String plaintext)  {
		if (plaintext == null) {
                    return null;
		}

		try {
			final MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(plaintext.getBytes("UTF-8"));
			return Base64.encodeBytes(md.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
                return null;
	}

    public UserEntity checkEmailURL(String email,String url)
    {
        try
        {
        UserEntity user = dao.getUserByEmail(email);
        if(user.getUrl().equals(url))
            return user;
        else
            return null;
        }
        catch(DAOException e)
        {
             logger.error("Exception checking user email and url", e);
            return null;
        }
    }


    /**
     * Registers a new user and generates his/her API key
     *
     * @param userName User name
     * @return API key, or null if there is any error
     */
    public UserEntity registerUser(String email, String url) throws ExistingNameException, WrongFormatException
    {
        try
        {
            EmailValidator emailValidator = EmailValidator.getInstance();
            if(!emailValidator.isValid(email))
                throw new WrongFormatException("email");
            UrlValidator urlValidator = new UrlValidator();
            if(!urlValidator.isValid(url))
               throw new WrongFormatException("url");
            String key = encrypt(email+System.currentTimeMillis());
            return dao.createUser(email,url,key.substring(0, key.length()-1));
        }
        catch(ExistingNameException e)
        {
            throw e;
        }
        catch(WrongFormatException e)
        {
            throw e;
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
    public String checkKey(String key)
    {
        if ("".equals(key) || key==null)
            return null;
        try
        {
        UserEntity user = dao.getUser(key);
        if(user!=null)
            return user.getEmail();
        else
            return null;
        }
        catch(Exception e)
        {
            logger.error("Exception checking key", e);
            return null;
        }
    }

}
