package com.panayotis.jubler.tools.translate;
import com.panayotis.jubler.subs.SubEntry;
import java.util.Iterator;
import java.util.Vector;
import static com.panayotis.jubler.i18n.I18N._;

/**
 *
 * @author sourcemorph
 */
public abstract class GenericApertiumTranslator implements com.panayotis.jubler.tools.translate.Translator {

    protected static Vector<Language> lang;
    static {
        lang = new Vector<Language>();
        lang.add(new Language("ar", _("Arabic")));
        lang.add(new Language("bg", _("Bulgarian")));
        lang.add(new Language("zh-CN", _("Chinese")));
        lang.add(new Language("hr", _("Croatian")));
        lang.add(new Language("cs", _("Czech")));
        lang.add(new Language("da", _("Danish")));
        lang.add(new Language("nl", _("Dutch")));
        lang.add(new Language("en", _("English")));
        lang.add(new Language("fi", _("Finnish")));
        lang.add(new Language("fr", _("French")));
        lang.add(new Language("de", _("German")));
        lang.add(new Language("el", _("Greek")));
        lang.add(new Language("hi", _("Hindi")));
        lang.add(new Language("it", _("Italian")));
        lang.add(new Language("ja", _("Japanese")));
        lang.add(new Language("ko", _("Korean")));
        lang.add(new Language("no", _("Norwegian")));
        lang.add(new Language("pl", _("Polish")));
        lang.add(new Language("pt", _("Portuguese")));
        lang.add(new Language("ro", _("Romanian")));
        lang.add(new Language("ru", _("Russian")));
        lang.add(new Language("es", _("Spanish")));
        lang.add(new Language("sv", _("Swedish")));
    }

    public String findLanguage(String language) {
        for (Language l : getLanguages()) {
            if (l.getName().equals(language)) {
                return l.getID();
            }
        }
        return "";
    }

    public String findLanguageID(String langID) {
        for (Language l : getLanguages()) {
            if (l.getID().equals(langID)) {
                return l.getName();
            }
        }
        return "";
    }

    protected Vector<Language> getLanguages() {
        return lang;
    }

    public boolean translate (Vector<SubEntry> subs, String from_language, String to_language) {
        Iterator<SubEntry> it = subs.iterator();
        SubEntry current;
        String origSent, transSent, so_lang, de_lang;
        //System.out.println(from_language);
        //System.out.println(to_language);
        so_lang = findLanguage(from_language);
        de_lang = findLanguage(to_language);
        while(it.hasNext()) {
            current = it.next();
            origSent = current.getText();
            transSent = machineTranslation(origSent, so_lang, de_lang);
            if(transSent.length() == 0) {
                transSent = origSent;
                System.out.println("Apertium did not translate");
            }
            current.setText(machineTranslation(transSent, so_lang, de_lang));
        }
        return true;
    }

    public abstract String machineTranslation(String text, String so_lang, String de_lang);
}