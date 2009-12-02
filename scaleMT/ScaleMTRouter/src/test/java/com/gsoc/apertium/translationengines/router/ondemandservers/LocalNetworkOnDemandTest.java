/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gsoc.apertium.translationengines.router.ondemandservers;

import org.scalemt.router.ondemandservers.LocalNetworkOnDemandServer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vmsanchez
 */
public class LocalNetworkOnDemandTest {

    public LocalNetworkOnDemandTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    @Test
     public void testServerStart() {
        try
        {
        LocalNetworkOnDemandServer localNetworkOnDemandServer = new LocalNetworkOnDemandServer();
        //String serverStarted=localNetworkOnDemandServer.startServer();
        //assertNotNull(serverStarted);
        //localNetworkOnDemandServer.stopServer(serverStarted);
        }
        catch(Exception e)
        {
            fail("Exception thrown: "+e.getMessage());
            
        }
    }

}