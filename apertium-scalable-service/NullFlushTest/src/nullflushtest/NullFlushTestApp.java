/*
 * NullFlushTestApp.java
 */

package nullflushtest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class NullFlushTestApp extends SingleFrameApplication {

    

   

    private class ProcessReader implements Runnable
    {

        PrintStream stream;
        BufferedReader reader;
        public ProcessReader(BufferedReader reader,PrintStream stream) {
            this.stream=stream;
            this.reader=reader;
        }




        public void run(){
            try {
                String line = null;
                int intchar;
                while((intchar= reader.read())!=-1)
                {
                    char c=(char) intchar;
                    stream.print(c);
                    stream.flush();
                }
                /*
                while ((line = processReader.readLine()) != null) {
                    System.out.println(line);
                }*/
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
    }


    private NullFlushTestView view;
    private Process process;
    private ProcessReader pReader;
    private BufferedWriter processWriter;
    private BufferedReader processReader;
    private BufferedReader processErrReader;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        view =  new NullFlushTestView(this);
        process=null;
        show(view);
    }

    void startCommand(String commandLine) throws Exception
    {
        String[] command = new String[3];
        command[0]="/bin/sh";
        command[1]="-c";
        command[2]=commandLine;

        process=Runtime.getRuntime().exec(command);
        processWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        processErrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        Thread t = new Thread(new ProcessReader(processReader,System.out));
        Thread terr = new Thread(new ProcessReader(processErrReader, System.err));
        t.start();
        terr.start();
    }

    void stopCommand() throws Exception{
        synchronized(processWriter)
        {
            processWriter.close();

        }

        process.waitFor();
        process=null;
    }

    void sendText(String text)  throws Exception{
        synchronized(processWriter)
        {
            System.out.println("Writing: "+text+"\n");
            processWriter.write(text);
            processWriter.write("\n");
            processWriter.write(0);
            processWriter.flush();
        }
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of NullFlushTestApp
     */
    public static NullFlushTestApp getApplication() {
        return Application.getInstance(NullFlushTestApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(NullFlushTestApp.class, args);
    }
}
