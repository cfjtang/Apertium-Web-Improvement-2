package org.scalemt.util;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class StreamGobbler extends Thread
{
    InputStream is;
    //StringBuilder builder;
    //StringWriter sw;
    
    ByteArrayOutputStream baos;
    byte[] buffer;

    public StreamGobbler(InputStream is)
    {
        this.is = is;
        baos = new ByteArrayOutputStream();
        buffer=new byte[1000];
    }
    
    @Override
    public void run()
    {
        try
        {
            int bytesRead;

            while ( (bytesRead = is.read(buffer)) != -1)
            {
                baos.write(buffer, 0, bytesRead);
            }
            is.close();
            baos.close();
            
            
        } catch (IOException ioe)
            {
            ioe.printStackTrace();  
            }
    }

    
    public String getReadedStr()
    {
    	return new String(baos.toByteArray());
    }
    
     

    public byte[] getReadBytes()
    {
        return baos.toByteArray();
    }
}
