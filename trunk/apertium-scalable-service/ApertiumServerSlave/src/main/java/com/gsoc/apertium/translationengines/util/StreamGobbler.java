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
package com.gsoc.apertium.translationengines.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

public class StreamGobbler extends Thread
{
    InputStream is;
    StringBuilder builder;
    StringWriter sw;
    
    public StreamGobbler(InputStream is)
    {
        this.is = is;
     
        
    }
    
    public void run()
    {
        try
        {
        	sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw);
                
                
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null)
            {
                if (pw != null)
                    pw.println(line);
            }
            if (pw != null)
            {
                pw.flush();
                pw.close();
            }
            isr.close();
            
            
        } catch (IOException ioe)
            {
            ioe.printStackTrace();  
            }
    }
    
    public String getReadedStr()
    {
    	return sw.toString();
    }
}
