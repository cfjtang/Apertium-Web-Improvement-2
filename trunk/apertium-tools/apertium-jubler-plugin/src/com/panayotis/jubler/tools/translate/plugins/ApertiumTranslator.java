package com.panayotis.jubler.tools.translate.plugins;
import com.panayotis.jubler.tools.translate.GenericApertiumTranslator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import static com.panayotis.jubler.i18n.I18N._;

/**
 *
 * @author sourcemorph
 */
public class ApertiumTranslator extends GenericApertiumTranslator {
    private static BufferedReader processReader;

    public BufferedReader initStream() {
        try {
            Process Findspace = Runtime.getRuntime().exec("apertium xxx");
            processReader = new BufferedReader(new InputStreamReader(Findspace.getInputStream()));
            return processReader;
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String getDefinition() {
        return _("Apertium translate");
    }

    public String machineTranslation (String text, String so_lang, String de_lang) {
        String result = "";
        String[] cmd = {"apertium",  so_lang + "-" + de_lang};
            try {
                Process Findspace = Runtime.getRuntime().exec(cmd);
                OutputStream output = Findspace.getOutputStream();
                output.write(text.getBytes());
                output.close();
                BufferedReader Resultset = new BufferedReader(new InputStreamReader (Findspace.getInputStream(), "UTF-8"));
                result = Resultset.readLine();
                Resultset.close();
                Findspace.destroy();
            } catch(IOException ex) {
                System.out.println("Could not translate, perhaps Apertium isn't installed correctly");
                ex.printStackTrace();
            }
        return result;
    }
}
