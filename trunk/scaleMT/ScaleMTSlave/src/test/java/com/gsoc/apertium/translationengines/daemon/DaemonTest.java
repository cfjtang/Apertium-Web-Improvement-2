/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gsoc.apertium.translationengines.daemon;

import org.scalemt.daemon.DaemonFactory;
import org.scalemt.main.QueueElement;
import org.scalemt.rmi.exceptions.TranslationEngineException;
import org.scalemt.rmi.transferobjects.Format;
import org.scalemt.rmi.transferobjects.LanguagePair;
import junit.framework.TestCase;

/**
 *
 * @author vitaka
 */
public class DaemonTest extends TestCase {
    
    private DaemonFactory daemonFactory;

    public DaemonTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
       // daemonFactory.getInstance();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}

    public void testConfigParse()
    {
        /*
        LanguagePair paireseu = new LanguagePair("es", "eu");
        DaemonFactory df = DaemonFactory.getInstance();
        Daemon daemon = df.newDaemon(paireseu);
        assertEquals(paireseu, daemon.getPair());
         * */
    }

    public void testTranslate()
    {
        /*
        Daemon d =daemonFactory.getInstance().newDaemon(new LanguagePair("es", "eu"));
        try
        {
        
       
        d.start();
        QueueElement qe = new QueueElement(1, "Hola, esto es una frase de prueba y esto otra. Tenemos un problema con los puntos.", Format.txt,new LanguagePair("es", "eu"), Thread.currentThread() , null);
        QueueElement qe2= new QueueElement(2, "Una frase con errores. ¿Por qué los puntos salen cuando les da la gana?", Format.txt,new LanguagePair("es", "eu"), Thread.currentThread() , null);
        d.assignQueueElement(qe);
        d.translate(qe, 1000000000);
        System.out.println(qe.getTranslation());
        assertNotNull(qe.getTranslation());

        d.assignQueueElement(qe2);
        d.translate(qe2, 1000000000);
        System.out.println(qe2.getTranslation());
        assertNotNull(qe2.getTranslation());
        }
        catch(TranslationEngineException e)
        {
            fail();
        }
        finally
        {
            d.stop();
        }
         */
    }

    public void testThrashLines()
    {
        /*
         Daemon d =daemonFactory.getInstance().newDaemon(new LanguagePair("es", "eu"));
         int lines = d.computeThrashNeededToFlush();
         System.out.println(lines);
         assertTrue(lines>0);

         */
    }

}
