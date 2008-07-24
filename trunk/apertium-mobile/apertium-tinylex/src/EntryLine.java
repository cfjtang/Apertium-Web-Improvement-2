/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class EntryLine {

    /**
     * 
     */
    private String lemma;
    /**
     * 
     */
    private String value;

    /**
     * 
     */
    public EntryLine() {

    }

    /**
     * 
     * @param lemma
     * @param value
     */
    public EntryLine(String lemma, String value) {
        this.lemma = lemma;
        this.value = value;
    }

    /**
     * 
     * @param value
     */
    public final void setValue(String value) {
        this.value = value;
    }

    /**
     * 
     * @return
     */
    public String getLemma() {
        return lemma;
    }

    /**
     * 
     * @param lemma
     */
    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    /**
     * 
     * @return
     */
    public final String getValue() {
        return this.value;
    }
}
