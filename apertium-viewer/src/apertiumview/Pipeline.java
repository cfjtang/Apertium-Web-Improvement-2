/*
 * Copyright 2015 Jacob Nordfalk <jacob.nordfalk@gmail.com>, Mikel Artetxe <artetxem@gmail.com>
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 */
package apertiumview;

import java.io.*;
import javax.swing.SwingUtilities;

import org.apertium.pipeline.Dispatcher;
import org.apertium.pipeline.Program;

public class Pipeline {
    private static Pipeline instance = new Pipeline();

    public boolean externalProcessing;
    public boolean markUnknownWords;
    public File execPath=null;
    public String[] envp=null;
    public boolean ignoreErrorMessages;

    private Pipeline() {        
        processor.start();
    } 
    
    public static Pipeline getPipeline() {        
        return instance;
    } 

    Thread processor = new Thread() {
        public void run() {
            while (true) try {
                if (task!=null) task.run();
                synchronized(this) {
                    if (nextTask!=null) {
                        task = nextTask;
                        nextTask = null;
                    } else {
                        this.wait();                        
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    };

    PipelineTask task = null;
    PipelineTask nextTask = null;
    
    void queueAsyncProcessing(TextWidget sourceWidget, int priority, String input, TextWidget recieverWidget) {
        //System.out.println("q"+priority+" "+sourceWidget.getText());
        if (nextTask != null && nextTask.priority<priority) return;  // too low priority, ignore new task

        if (externalProcessing && task != null && task.startTime<System.currentTimeMillis()-5000) {
          String err = "task probably runned amok, destroying: "+nextTask.program;
          System.out.println(err);
          task.proces.destroy(); // task probably runned amok
          task.recieverWidget.setText(err);
          task.recieverWidget.setStatus(TextWidget.STATUS_ERROR);
        }


        PipelineTask t = new PipelineTask();
        t.priority = priority;
        t.program = recieverWidget.getProgram();
        t.input = sourceWidget.getProgram() != null ? input : input + ' ';
        //new Exception(input).printStackTrace();
        //System.out.println("q"+priority+" "+t.execstr+" "+t.input);
        if (t.input.length() > 0) {
            t.recieverWidget = recieverWidget;
            synchronized (processor) {
                nextTask = t;
                processor.notify();            
            }
            
        } else {
            recieverWidget.setText(t.input);
            recieverWidget.setStatus(TextWidget.STATUS_EQUAL);
        }
    }

  void shutdown() {
    try {
      if (task!=null && task.proces!=null) task.proces.destroy();
      if (nextTask!=null && nextTask.proces!=null) nextTask.proces.destroy();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


    private class PipelineTask implements Runnable {
        private Program program;
        private String input;
        private TextWidget recieverWidget;
        public int priority = Integer.MAX_VALUE;
        
        //Variables for external processing
        private BufferedReader std;
        private BufferedReader err;
        private OutputStreamWriter osw;
        private Process proces;
        public long startTime = System.currentTimeMillis();

        public void run() {
          try {
            PipelineTask t = this;
            
            String output_;
            int retval_;
            if (!externalProcessing && program.getProgram() != Program.ProgEnum.UNKNOWN) {
                try {
                    StringWriter sw = new StringWriter();
                    Dispatcher.dispatch(program, new StringReader(input), sw, false, markUnknownWords);
                    retval_ = 0;
                    output_ = sw.toString();
                } catch (Exception e) {
                    retval_ = -1;
                    output_ = e.toString();
										//e.printStackTrace();
                }
            }
            else {
                String cmd = t.program.toString();
                cmd = cmd.replaceAll("\\$1", markUnknownWords ? "-g" : "-n");
                cmd = cmd.replaceAll("\\$2", ""); // What is this $2 ??!??
                cmd = cmd.replaceAll("\\$3", ""); // What is this $3 ??!??
                
                t.proces = Runtime.getRuntime().exec(cmd, envp, execPath);

                // For mac users UTF-8 is needed.
                t.std = new BufferedReader(new InputStreamReader(t.proces.getInputStream(),"UTF-8"));
                t.err = new BufferedReader(new InputStreamReader(t.proces.getErrorStream(),"UTF-8"));
                t.osw = new OutputStreamWriter(t.proces.getOutputStream(),"UTF-8");            

                osw.write(input,0,input.length());
                osw.write('\n');
                osw.close();
                final StringBuilder outputsb = new StringBuilder(input.length()*2);
                String lin;
                while ( (lin=std.readLine())!=null) outputsb.append(lin).append('\n');
                while ( (lin=err.readLine())!=null) if (!ignoreErrorMessages) outputsb.append("ERR:"+lin).append('\n');
                while ( (lin=std.readLine())!=null) outputsb.append(lin).append('\n');

                retval_ = proces.waitFor();
                if (retval_ != 0) outputsb.append("Return value: "+retval_);
                err.close();
                std.close();
                task = null;
                output_ = outputsb.toString().trim();
            }
            
            final String output = output_;
            final int retval = retval_;
            
            Runnable runnable = new Runnable() {
                public void run() {
                    recieverWidget.setText(output);
                    if (retval!=0) recieverWidget.setStatus(TextWidget.STATUS_ERROR);
                    else if (output.equals(input)) recieverWidget.setStatus(TextWidget.STATUS_EQUAL);
                    else recieverWidget.setStatus(TextWidget.STATUS_OK);
                }
            };
            SwingUtilities.invokeLater(runnable);
          } catch (InterruptedException ex) {
          } catch (Throwable ex) {
						System.err.println("Error for "+program+" on input '"+input+"'");
						ex.printStackTrace();
						recieverWidget.setText(ex.getLocalizedMessage());
          } finally {
						task = null;
          }
        }    
    }
 }


