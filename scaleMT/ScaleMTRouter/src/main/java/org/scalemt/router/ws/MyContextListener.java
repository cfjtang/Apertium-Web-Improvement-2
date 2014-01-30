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
package org.scalemt.router.ws;

import org.scalemt.rmi.router.IApplicationRouter;
import org.scalemt.rmi.router.ITradubiTranslationEngine;
import org.scalemt.router.logic.ApplicationRouterImpl;
import org.scalemt.router.logic.LoadBalancer;
import org.scalemt.router.logic.Util;
import org.scalemt.router.tradubi.ApertiumTranslationEngineTradubi;
import com.xerox.amazonws.ec2.RegionInfo;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Performs some actions when servlet context is initialized and destroyed.
 *
 * @author vitaka
 */
public class MyContextListener implements ServletContextListener {

    /**
     * Commons-logging logger
     */
    static Log logger = LogFactory.getLog(MyContextListener.class);
    List<Remote> remoteObjs=new ArrayList<Remote>();

    /**
     * Actions performed when servlet context starts.
     * RMI Registry is started and a remote object {@link ApplicationRouterImpl} is exported
     *
     * @param sce
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        System.setProperty("java.rmi.server.hostname", Util.readConfigurationProperty("requestrouter_rmi_host"));

        System.out.println(RegionInfo.REGIONURL_EU_WEST);
        try {

            //start RMI registry
            Registry registry = null;
            try {
                registry = LocateRegistry.getRegistry(Util.readConfigurationProperty("requestrouter_rmi_host"),Integer.parseInt(Util.readConfigurationProperty("rmi_registry_port")));
                registry.list();
                logger.info("RMI Registry already running on port " + Integer.parseInt(Util.readConfigurationProperty("rmi_registry_port")));
            } catch (Exception e) {

                logger.error("Cannot find RMI registry", e);
                return;
            }




            if ("yes".equals(Util.readConfigurationProperty("tradubi_on"))) {
                try {
                    //Instantiate and export remote objects
                    ITradubiTranslationEngine objt = new ApertiumTranslationEngineTradubi();
                    ITradubiTranslationEngine stubt = (ITradubiTranslationEngine) UnicastRemoteObject.exportObject(objt, Integer.parseInt(Util.readConfigurationProperty("tradubi_interface_rmi_port")));
                    remoteObjs.add(objt);

                    //Bind objects to registry
                    registry.rebind(Util.readConfigurationProperty("tradubi_interface_rmi_name"), stubt);
                    logger.info("RMI remote object " + Util.readConfigurationProperty("tradubi_interface_rmi_name") + " bound OK: Name: " + Util.readConfigurationProperty("tradubi_interface_rmi_name") + ". Port:" + Util.readConfigurationProperty("tradubi_interface_rmi_port"));
                } catch (Exception e) {
                    logger.error("Cannot bind tradubi object to registry", e);
                }
            }

            try
            {
                
            //Instantiate and export remote objects
            IApplicationRouter obj = new ApplicationRouterImpl();
            IApplicationRouter stub = (IApplicationRouter) UnicastRemoteObject.exportObject(obj, Integer.parseInt(Util.readConfigurationProperty("requestrouter_rmi_port")));
            //Bind objects to registry
            registry.rebind(Util.readConfigurationProperty("requestrouter_rmi_name"), stub);
            remoteObjs.add(obj);

            logger.info("RMI remote object " + Util.readConfigurationProperty("requestrouter_rmi_name") +  " bound OK: Name: " + Util.readConfigurationProperty("requestrouter_rmi_name") + ". Port:" + Util.readConfigurationProperty("requestrouter_rmi_port"));

            LoadBalancer.getInstance();
            }
            catch(Exception e) {
                    logger.error("Cannot bind object to registry", e);
                }


        } catch (Exception e) {
            logger.error("Unexpected error starting RMI service", e);
        }
    }

    /**
     * Actions performed when servlet context is destroyed.
     * Object exported in {@link MyContextListener#contextInitialized(javax.servlet.ServletContextEvent) } is unbound.
     *
     * @param sce
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry(Integer.parseInt(Util.readConfigurationProperty("rmi_registry_port")));
            registry.unbind(Util.readConfigurationProperty("requestrouter_rmi_name"));
            for(Remote obj: remoteObjs)
            {
                UnicastRemoteObject.unexportObject(obj, true);
            }

            /*
            if(ownRegistry!=null)
            {
            UnicastRemoteObject.unexportObject(ownRegistry, true);
            logger.info("Stopped RMI Registry");
            }
             * */
        } catch (Exception e) {
            logger.warn("Cannot unbind object", e);
        }
    }
}
