/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.rmi.transferobjects;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Binary file to be translated
 *
 * @author vmsanchez
 */
public class BinaryDocument extends Content{

    byte[] bytes;

    public BinaryDocument(Format f,byte[] bytes){
        super(f);
        this.bytes = bytes;
    }


    @Override
    public boolean isBinary() {
        return true;
    }

    @Override
    public int calculateLength() {
        
        int returnValue=0;
        switch(getFormat())
        {
            //For odt, unzip, and read content.xml
            case odt:
            FileOutputStream  fos=null;
            ZipFile zfile=null;
            try
            {
                fos = new FileOutputStream(System.getProperty("java.io.tmpdir")+"/"+Thread.currentThread().toString()+".odt");
                fos.write(bytes);
                fos.close();
                
                zfile= new ZipFile(System.getProperty("java.io.tmpdir")+"/"+Thread.currentThread().toString()+".odt");
                Enumeration entries = zfile.entries();
                ZipEntry entry=null;
                while(entries.hasMoreElements())
                {
                    entry=(ZipEntry) entries.nextElement();
                    if(!entry.isDirectory() && entry.getName().equals("content.xml"))
                    {
                        returnValue= (int) entry.getSize();
                        break;
                    }
                }

            }
            catch(IOException e)
            {
               e.printStackTrace();
            }
            finally
            {
                

                if(zfile!=null)
                    try {
                zfile.close();
                } catch (IOException ex) {
                }
            }

            break;

            //For rtf, ignore lines with Base64 encoding
            case rtf:
            try {

                
                Pattern p = Pattern.compile("^\\d+$");
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                BufferedReader reader = new BufferedReader(new InputStreamReader(bais,"US-ASCII" ));
                String line;
                Matcher m;
                while((line=reader.readLine())!=null)
                {
                    m=p.matcher(line);
                    if(!m.matches())
                        returnValue+=line.length();
                }
                

            } catch (UnsupportedEncodingException ex) {
               ex.printStackTrace();
            }
            catch (IOException ex) {
               ex.printStackTrace();
            }

            break;

            default: break;

            

        }
        
        return returnValue;


    }

    @Override
    public String toString() {
        return new String(bytes);
    }

    @Override
    public byte[] toByteArray() {
        return bytes;
    }

    @Override
    protected Set<Format> getValidFormats() {
       Set<Format> validFormats = new HashSet<Format>();
       validFormats.add(Format.rtf);
       validFormats.add(Format.odt);
        return validFormats;
    }

}
