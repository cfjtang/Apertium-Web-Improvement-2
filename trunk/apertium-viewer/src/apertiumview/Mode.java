/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package apertiumview;

import java.io.File;

/**
 *
 * @author j
 */
class Mode {
    String[] commandChain = {
        "lt-proc /usr/share/apertium/apertium-eo-en/en-eo.automorf.bin",
        "apertium-tagger -g /usr/share/apertium/apertium-eo-en/en-eo.prob",
        "apertium-pretransfer",
        "apertium-transfer /usr/share/apertium/apertium-eo-en/apertium-eo-en.en-eo.t1x /usr/share/apertium/apertium-eo-en/en-eo.t1x.bin /usr/share/apertium/apertium-eo-en/en-eo.autobil.bin",
        "apertium-interchunk /usr/share/apertium/apertium-eo-en/apertium-eo-en.en-eo.t2x /usr/share/apertium/apertium-eo-en/en-eo.t2x.bin",
        "apertium-postchunk /usr/share/apertium/apertium-eo-en/apertium-eo-en.en-eo.t3x /usr/share/apertium/apertium-eo-en/en-eo.t3x.bin",
        "lt-proc -g /usr/share/apertium/apertium-eo-en/en-eo.autogen.bin",
    
         };

    String name = "en-eo (std)";
    File file;
    
    public String toString()  
    {
         return name; 
    }
    

}
