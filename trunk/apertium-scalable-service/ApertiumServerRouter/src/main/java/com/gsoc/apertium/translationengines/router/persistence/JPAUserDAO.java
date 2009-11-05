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

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * JPA implementation of {@link IUserDAO}
 * Configured in file <code>persistence.xml</code>
 * @author vmsanchez
 */
public class JPAUserDAO implements IUserDAO{

    protected EntityManagerFactory emf;


    public JPAUserDAO() {
        emf=Persistence.createEntityManagerFactory("jpausers");

    }


    @Override
    public UserEntity createUser(String name) throws DAOException {

      EntityManager em = emf.createEntityManager();
      em.getTransaction().begin();
      try
      {
    
      List results =   em.createNamedQuery("userByName").setParameter("parName",name).getResultList();
      if(results.size()>0)
          throw new ExistingNameException();
      UserEntity userEntity=new UserEntity();
      userEntity.setUsername(name);
      em.persist(userEntity);
      return userEntity;
      }
      finally
      {
      em.getTransaction().commit();
      em.close();
      }
    }

    @Override
    public UserEntity getUser(String key) throws DAOException {
        EntityManager em = emf.createEntityManager();
      em.getTransaction().begin();
      UserEntity user= em.find(UserEntity.class, Long.parseLong(key));
      em.getTransaction().commit();
      em.close();
      return user;

    }

    @Override
    public void deleteUser(String name) throws DAOException {
        EntityManager em = emf.createEntityManager();
      em.getTransaction().begin();
      try
      {
      UserEntity existingUser = (UserEntity) em.createNamedQuery("userByName").setParameter("parName",name).getSingleResult();
      if(existingUser!=null)
          em.remove(existingUser);
      }
      finally
      {
      em.getTransaction().commit();
      em.close();
      }
    }

}
