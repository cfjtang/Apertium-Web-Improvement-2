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

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

/**
 * JPA Entity with the information of a user.
 * @author vmsanchez
 */
@org.hibernate.annotations.Cache(usage = org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE)
@Entity
@NamedQuery(name="userByName",
				         query="SELECT u " +
				               "FROM UserEntity u " +
				               "WHERE u.username = :parName ")
public class UserEntity implements Serializable {

    /**
     * Name of the user. Only an user can exist with the same name
     */
    private String username;

    /**
     * API key. Generated field. Only an user can exist with the same API key
     */
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long apiKey;

    public Long getApiKey() {
        return apiKey;
    }

    public void setApiKey(Long apiKey) {
        this.apiKey = apiKey;
    }

    

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
