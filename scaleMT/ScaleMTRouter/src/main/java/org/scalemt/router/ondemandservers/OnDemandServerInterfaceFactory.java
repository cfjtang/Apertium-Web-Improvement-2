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
package org.scalemt.router.ondemandservers;

import org.scalemt.router.logic.Util;

/**
 * Factory that instantiates a suitable implementation of {@link  IOnDemandServerInterface }
 *
 * @author vitaka
 */
public class OnDemandServerInterfaceFactory {

    /**
     * Instantiates and returns an instance of the class defined in the property <code>class</code> from <code>OnDemandServerInterface.properties</code>
     * @return An implementation of {@link  IOnDemandServerInterface }
     */
    public static IOnDemandServerInterface getOnDemandServerInterface()
    {
        try
        {
            return (IOnDemandServerInterface) Class.forName(Util.readPropertyFromFile("class", "/OnDemandServerInterface.properties")).newInstance();
        }
        catch(Exception e)
        {
                return null;
        }
    }
}
