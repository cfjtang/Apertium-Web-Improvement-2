/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.daemon;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import junit.framework.TestCase;
import org.scalemt.main.QueueElement;
import org.scalemt.rmi.transferobjects.BinaryDocument;
import org.scalemt.rmi.transferobjects.DaemonConfiguration;
import org.scalemt.rmi.transferobjects.Format;
import org.scalemt.rmi.transferobjects.LanguagePair;
import org.scalemt.rmi.transferobjects.TextContent;

/**
 *
 * @author vitaka
 */
public class DaemonTest extends TestCase {

    private static final boolean DO_TESTS=false;
    
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

    public void testTranslateTextWithoutConfigParsing()
    {
        if(!DO_TESTS)
             return;
        
        TranslationEngineInfo tei = new TranslationEngineInfo();
        tei.setName("Apertiumtest");
        tei.setDeformatOutFromPipeline(true);

        List<Program> programs = new ArrayList<Program>();

        Program p1 = new Program();
        p1.setCommand("/home/vmsanchez/local/bin/apertium-des$f");
        p1.setInput(-1);
        p1.setOutput(2);
        programs.add(p1);
        
        Program p2 = new Program();
        p2.setCommand("/home/vmsanchez/local/bin/apertium-re$f");
        p2.setInput(3);
        p2.setOutput(-2);
        programs.add(p2);

        tei.setPrograms(programs);

        DaemonConfiguration dc = new DaemonConfiguration(new LanguagePair("es","ca"), Format.txt);
        Set<DaemonConfiguration> confs = new HashSet<DaemonConfiguration>();
        confs.add(dc);
        tei.setConfigurations(confs);

        Map<Integer,VariableType> variables=new HashMap<Integer,VariableType>();
        variables.put(1, VariableType.file);
        variables.put(2, VariableType.memory);
        variables.put(3, VariableType.memory);
        tei.setVariables(variables);

        TranslationCore core = new TranslationCore();

        core.setCommand("/home/vmsanchez/local/bin/apertium -f none $p-null");
        core.setInput(2);
        core.setOutput(3);
        core.setSeparateAfterDeformat(true);
        core.setTrash(null);
        core.setTextBefore("[--apertium-translation id=\"$id\" dict=\"0\"--]");
        SeparatorRegexp sepBefore = new SeparatorRegexp(Pattern.compile("^\\[--apertium-translation id=\"(\\d+)\" dict=\"(([-]?\\d+(,[-]?\\d+)*)?)\"--\\]$"),1 );
        List<SeparatorRegexp> separatorsBefore = new ArrayList<SeparatorRegexp>();
        separatorsBefore.add(sepBefore);
        core.setRegexpsBefore(separatorsBefore);
        core.setTextAfter("[--end-apertium-translation--]");
        SeparatorRegexp sepAfter = new SeparatorRegexp(Pattern.compile("\\[--end-apertium-translation--\\]"), -1);
        List<SeparatorRegexp> separatorsAfter = new ArrayList<SeparatorRegexp>();
        separatorsAfter.add(sepAfter);
        core.setRegexpsAfter(separatorsAfter);
        core.setNullFlush(true);
        tei.setTranslationCore(core);

        Daemon d=null;
       
        try
        {
        d = new Daemon(1, dc, tei);
        d.start();
        QueueElement qe = new QueueElement(1, new TextContent(Format.txt,"Hola, esto es una frase de prueba y esto otra. Tenemos un problema con los puntos."),new LanguagePair("es", "ca"), Thread.currentThread() , null);
        d.assignQueueElement(qe);
        d.translate(qe, 10000);
        System.out.println(qe.getTranslation());
        assertFalse(qe.getTranslation().isBinary());
        assertTrue(qe.getTranslation().toString().startsWith("Hola"));

      
       

        }
        catch(Exception e)
        {
            e.printStackTrace();
            fail();
        }
        finally
        {
            d.stop();
            //is and baos do not need to be closed

            
        }
        

         
    }

     public void testTranslateDocumentWithoutConfigParsing()
    {

         if(!DO_TESTS)
             return;
         
        TranslationEngineInfo tei = new TranslationEngineInfo();
        tei.setName("Apertiumtest");
        tei.setDeformatOutFromPipeline(true);

        List<Program> programs = new ArrayList<Program>();

        Program p1 = new Program();
        p1.setCommand("/home/vmsanchez/local/bin/apertium-des$f");
        p1.setInput(-1);
        p1.setOutput(2);
        programs.add(p1);

        Program p2 = new Program();
        p2.setCommand("/home/vmsanchez/local/bin/apertium-re$f");
        p2.setInput(3);
        p2.setOutput(-2);
        programs.add(p2);

        tei.setPrograms(programs);

        DaemonConfiguration dc = new DaemonConfiguration(new LanguagePair("es","ca"), Format.rtf);
        Set<DaemonConfiguration> confs = new HashSet<DaemonConfiguration>();
        confs.add(dc);
        tei.setConfigurations(confs);

        Map<Integer,VariableType> variables=new HashMap<Integer,VariableType>();
        variables.put(1, VariableType.file);
        variables.put(2, VariableType.memory);
        variables.put(3, VariableType.memory);
        tei.setVariables(variables);

        TranslationCore core = new TranslationCore();

        core.setCommand("/home/vmsanchez/local/bin/apertium -f none $p-null");
        core.setInput(2);
        core.setOutput(3);
        core.setSeparateAfterDeformat(true);
        core.setTrash(null);
        core.setTextBefore("[--apertium-translation id=\"$id\" dict=\"0\"--]");
        SeparatorRegexp sepBefore = new SeparatorRegexp(Pattern.compile("^\\[--apertium-translation id=\"(\\d+)\" dict=\"(([-]?\\d+(,[-]?\\d+)*)?)\"--\\]$"),1 );
        List<SeparatorRegexp> separatorsBefore = new ArrayList<SeparatorRegexp>();
        separatorsBefore.add(sepBefore);
        core.setRegexpsBefore(separatorsBefore);
        core.setTextAfter("[--end-apertium-translation--]");
        SeparatorRegexp sepAfter = new SeparatorRegexp(Pattern.compile("\\[--end-apertium-translation--\\]"), -1);
        List<SeparatorRegexp> separatorsAfter = new ArrayList<SeparatorRegexp>();
        separatorsAfter.add(sepAfter);
        core.setRegexpsAfter(separatorsAfter);
        core.setNullFlush(true);
        tei.setTranslationCore(core);

        Daemon d=null;
        InputStream is=null;
        ByteArrayOutputStream baos =null;
        FileOutputStream fos =null;
        try
        {
        d = new Daemon(1, dc, tei);
        d.start();
       

        byte[] buffer= new byte[1000];
        is =DaemonTest.class.getResourceAsStream("/test.rtf");
        baos = new ByteArrayOutputStream();

        int read;
        while(( read=is.read(buffer))!=-1)
        {
            baos.write(buffer, 0, read);
        }
        is.close();
        baos.close();

        QueueElement qe2 = new QueueElement(1, new BinaryDocument(Format.rtf,baos.toByteArray()),new LanguagePair("es", "ca"), Thread.currentThread() , null);
        d.assignQueueElement(qe2);
        d.translate(qe2, 10000);
        assertTrue(qe2.getTranslation().isBinary());
        assertTrue(qe2.getTranslation().toByteArray().length >0);

        fos = new FileOutputStream(System.getProperty("java.io.tmpdir")+"/"+"outtest.rtf");
        fos.write(qe2.getTranslation().toByteArray());


        }
        catch(Exception e)
        {
            e.printStackTrace();
            fail();
        }
        finally
        {
            d.stop();
            //is and baos do not need to be closed

            if(fos!=null)
                try
                {
                     fos.close();
                }
                catch(Exception e) {}
        }
        
     }

}
