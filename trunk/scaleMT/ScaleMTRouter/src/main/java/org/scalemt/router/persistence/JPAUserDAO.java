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
    public UserEntity createUser(String email,String url,String key) throws DAOException {

      EntityManager em = emf.createEntityManager();
      em.getTransaction().begin();
      try
      {
    
      List results =   em.createNamedQuery("userByKey").setParameter("parKey",key).getResultList();
      if(results.size()>0)
          throw new ExistingKeyException();
      results =   em.createNamedQuery("userByEmail").setParameter("parEmail",email).getResultList();
      if(results.size()>0)
          throw new ExistingNameException();

      UserEntity userEntity=new UserEntity();
      userEntity.setEmail(email);
      userEntity.setApi(key);
      userEntity.setUrl(url);
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
    public UserEntity getUserByEmail(String email) throws DAOException
    {
        UserEntity user=null;
        EntityManager em = emf.createEntityManager();
      em.getTransaction().begin();
      List users = em.createNamedQuery("userByEmail").setParameter("parEmail",email).getResultList();
      if(users.size()>0)
        user= (UserEntity) users.get(0);
      em.getTransaction().commit();
      em.close();
      return user;
    }

    @Override
    public UserEntity getUser(String key) throws DAOException {
        UserEntity user=null;
        EntityManager em = emf.createEntityManager();
      em.getTransaction().begin();
      List users = em.createNamedQuery("userByKey").setParameter("parKey",key).getResultList();
      if(users.size()>0)
        user= (UserEntity) users.get(0);
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
      UserEntity existingUser = (UserEntity) em.createNamedQuery("userByEmail").setParameter("parEmail",name).getSingleResult();
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
