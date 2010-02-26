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

package org.scalemt.router.persistence;

/**
 * Interface to perform user management database operations
 * @author vmsanchez
 */
public interface IUserDAO {
    /**
     * Creates a new user with the given name. The id will be automatically generated
     * @param name User name
     * @return Object with user name and id
     * @throws ExistingNameException If the name already exists
     * @throws com.gsoc.apertium.translationengines.router.persistence.DAOException If there is a database error
     */
    public UserEntity createUser(String email,String url,String key) throws ExistingNameException,DAOException;
    /**
     * Gets information abour the user with the given key
     * @param key User's key
     * @return Information about the user with the given key, or <code>null</code> if there is no user with such key
     * @throws com.gsoc.apertium.translationengines.router.persistence.DAOException If there is a database error
     */
    public UserEntity getUser(String key) throws DAOException;

     public UserEntity getUserByEmail(String email) throws DAOException;
    /**
     * Deletes the user with the given email
     * @param email Email of the user to be deleted
     * @throws com.gsoc.apertium.translationengines.router.persistence.DAOException If there is a database error or the user doesn't exist
     */
    public void deleteUser(String email) throws DAOException;
}
