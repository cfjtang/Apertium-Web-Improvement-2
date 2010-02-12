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

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * JPA Entity with the information of a user.
 * @author vmsanchez
 */


@NamedQueries({
@NamedQuery(name="userByKey",
				         query="SELECT u " +
				               "FROM UserEntity u " +
				               "WHERE u.api = :parKey "),
@NamedQuery(name="userByEmail",
				         query="SELECT u " +
				               "FROM UserEntity u " +
				               "WHERE u.email = :parEmail ")})
/*
@NamedQuery(name="userByApiKey",
				         query="SELECT u " +
				               "FROM UserEntity u " +
				               "WHERE u.apiKey = :parApiKey ") */
@org.hibernate.annotations.Cache(usage = org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE)
@Entity
public class UserEntity implements Serializable  {

    @Id @GeneratedValue(strategy=GenerationType.TABLE)
    private long id;

    /**
     * Name of the user. Only an user can exist with the same name
     */
    private String email;

    /**
     * Url of the website that will perform the requests
     */
    private String url;

    /**
     * API key. Generated field. Only an user can exist with the same API key
     */
    private String api;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String apiKey) {
        this.api = apiKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



}