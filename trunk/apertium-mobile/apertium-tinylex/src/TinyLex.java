/*
 * Copyright (C) 2008 Enrique Benimeli Bofarull
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the imsplied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import org.netbeans.microedition.lcdui.SplashScreen;
import org.netbeans.microedition.lcdui.WaitScreen;
import org.netbeans.microedition.util.SimpleCancellableTask;

/**
 * @author Enrique Benimeli Bofarull
 */
public class TinyLex extends MIDlet implements CommandListener {

    /**
     * 
     */
    private boolean midletPaused = false;
    /**
     * Current left-to-right dictionary
     */
    private Dictionary sltldic;
    /**
     * Current right-to-left dictionary
     */
    private Dictionary tlsldic;
    /**
     * 
     */
    private Hashtable properties;
    /**
     * 
     */
    private Hashtable loaded;
    /**
     * 
     */
    private String direction;
    /**
     * 
     */
    private String sltlString;
    /**
     * 
     */
    private String tlslString;
    /**
     * 
     */
    boolean sltlLoaded = false;
    /**
     * 
     */
    int currentDic = -1;
    /**
     * 
     */
    final int SLTL = 0;
    /**
     * 
     */
    final int TLSL = 1;
    /**
     * 
     */
    boolean tlslLoaded = false;
    /**
     * 
     */
    private String[][] idxLR;
    /**
     * 
     */
    private String[][] idxRL;
    //<editor-fold defaultstate="collapsed" desc=" Generated Fields ">//GEN-BEGIN:|fields|0|
    private Form aboutForm;
    private StringItem aboutText1;
    private StringItem stringItem1;
    private Spacer spacer1;
    private SplashScreen splashScreen;
    private WaitScreen waitScreen;
    private List menuList;
    private Form lookUpForm;
    private TextField srcWord;
    private List langSelectList;
    private Alert wordNotFoundAlert;
    private Form dicLoaderForm;
    private Spacer spacer;
    private StringItem dicLoaderMessage;
    private Gauge progressBar;
    private Command okCommandAboutForm;
    private Command okCommand3;
    private Command backCommandAboutForm;
    private Command okCommandMenuList;
    private Command backCommandLookUp;
    private Command okCommandLookUp;
    private Command okCommandSelectDirection;
    private Command backCommandSelectDirection;
    private Command exitCommandMenuList;
    private Command backCommand2;
    private Command okCommand2;
    private Command backCommand3;
    private Command backCommand4;
    private Command cancelCommandDicLoader;
    private SimpleCancellableTask task;
    private Image image1;
    private Font font;
    private SimpleCancellableTask task1;
    private SimpleCancellableTask task2;
    //</editor-fold>//GEN-END:|fields|0|
    /**
     * The TinyLex constructor.
     */
    public TinyLex() {
    }

    //<editor-fold defaultstate="collapsed" desc=" Generated Methods ">//GEN-BEGIN:|methods|0|
    //</editor-fold>//GEN-END:|methods|0|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: initialize ">//GEN-BEGIN:|0-initialize|0|0-preInitialize
    /**
     * Initilizes the application.
     * It is called only once when the MIDlet is started. The method is called before the <code>startMIDlet</code> method.
     */
    private void initialize() {//GEN-END:|0-initialize|0|0-preInitialize
    // write pre-initialize user code here
//GEN-LINE:|0-initialize|1|0-postInitialize
    // write post-initialize user code here
    }//GEN-BEGIN:|0-initialize|2|
    //</editor-fold>//GEN-END:|0-initialize|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: startMIDlet ">//GEN-BEGIN:|3-startMIDlet|0|3-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Started point.
     */
    public void startMIDlet() {//GEN-END:|3-startMIDlet|0|3-preAction
        // write pre-action user code here
        switchDisplayable(null, getSplashScreen());//GEN-LINE:|3-startMIDlet|1|3-postAction
    // write post-action user code here
    }//GEN-BEGIN:|3-startMIDlet|2|
    //</editor-fold>//GEN-END:|3-startMIDlet|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: resumeMIDlet ">//GEN-BEGIN:|4-resumeMIDlet|0|4-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Resumed point.
     */
    public void resumeMIDlet() {//GEN-END:|4-resumeMIDlet|0|4-preAction
    // write pre-action user code here
//GEN-LINE:|4-resumeMIDlet|1|4-postAction
    // write post-action user code here
    }//GEN-BEGIN:|4-resumeMIDlet|2|
    //</editor-fold>//GEN-END:|4-resumeMIDlet|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: switchDisplayable ">//GEN-BEGIN:|5-switchDisplayable|0|5-preSwitch
    /**
     * Switches a current displayable in a display. The <code>display</code> instance is taken from <code>getDisplay</code> method. This method is used by all actions in the design for switching displayable.
     * @param alert the Alert which is temporarily set to the display; if <code>null</code>, then <code>nextDisplayable</code> is set immediately
     * @param nextDisplayable the Displayable to be set
     */
    public void switchDisplayable(Alert alert, Displayable nextDisplayable) {//GEN-END:|5-switchDisplayable|0|5-preSwitch
        // write pre-switch user code here
        Display display = getDisplay();//GEN-BEGIN:|5-switchDisplayable|1|5-postSwitch
        if (alert == null) {
            display.setCurrent(nextDisplayable);
        } else {
            display.setCurrent(alert, nextDisplayable);
        }//GEN-END:|5-switchDisplayable|1|5-postSwitch
    // write post-switch user code here
    }//GEN-BEGIN:|5-switchDisplayable|2|
    //</editor-fold>//GEN-END:|5-switchDisplayable|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Displayables ">//GEN-BEGIN:|7-commandAction|0|7-preCommandAction
    /**
     * Called by a system to indicated that a command has been invoked on a particular displayable.
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:|7-commandAction|0|7-preCommandAction
        // write pre-action user code here
        if (displayable == aboutForm) {//GEN-BEGIN:|7-commandAction|1|135-preAction
            if (command == backCommandAboutForm) {//GEN-END:|7-commandAction|1|135-preAction
                // write pre-action user code here
                switchDisplayable(null, getMenuList());//GEN-LINE:|7-commandAction|2|135-postAction
            // write post-action user code here
            } else if (command == okCommandAboutForm) {//GEN-LINE:|7-commandAction|3|137-preAction
                // write pre-action user code here
                switchDisplayable(null, getMenuList());//GEN-LINE:|7-commandAction|4|137-postAction
            }//GEN-BEGIN:|7-commandAction|5|122-preAction
        } else if (displayable == dicLoaderForm) {
            if (command == cancelCommandDicLoader) {//GEN-END:|7-commandAction|5|122-preAction
                switchDisplayable(null, getLangSelectList());//GEN-LINE:|7-commandAction|6|122-postAction
            }//GEN-BEGIN:|7-commandAction|7|48-preAction
        } else if (displayable == langSelectList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|7|48-preAction
                langSelectListAction();//GEN-LINE:|7-commandAction|8|48-postAction
                this.resetLookUpForm();
                switchDisplayable(null, getLookUpForm());
            } else if (command == backCommandSelectDirection) {//GEN-LINE:|7-commandAction|9|57-preAction
                switchDisplayable(null, getMenuList());//GEN-LINE:|7-commandAction|10|57-postAction
            } else if (command == okCommandSelectDirection) {//GEN-LINE:|7-commandAction|11|54-preAction
                langSelectListAction();
                this.resetLookUpForm();
                switchDisplayable(null, getLookUpForm());//GEN-LINE:|7-commandAction|12|54-postAction
            }//GEN-BEGIN:|7-commandAction|13|44-preAction
        } else if (displayable == lookUpForm) {
            if (command == backCommandLookUp) {//GEN-END:|7-commandAction|13|44-preAction
                switchDisplayable(null, getLangSelectList());//GEN-LINE:|7-commandAction|14|44-postAction
            } else if (command == okCommandLookUp) {//GEN-LINE:|7-commandAction|15|42-preAction
                String src = this.srcWord.getString();
                String srcLemma = src.toLowerCase();
                if(srcLemma.length()>0) {
                String values = null;
                if (this.currentDic == this.SLTL) {
                    Dictionary subDic = getSubDic(srcLemma, this.SLTL);
                    values = subDic.getTranslations(srcLemma);
                }
                if (this.currentDic == this.TLSL) {
                    Dictionary subDic = getSubDic(srcLemma, this.TLSL);
                    values = subDic.getTranslations(srcLemma);
                }
                if (values != null) {
                    EntryParser entryParser = new EntryParser(values);
                    // Vector of Entry objects
                    Vector entries = entryParser.parse();

                    int nws = entries.size();
                    this.cleanLookUpFormResults();
                    for (int i = 0; i < nws; i++) {
                        Entry entry = (Entry) entries.elementAt(i);
                        String slLemma = entry.getSlLemma();
                        String tlLemma = entry.getTlLemma();
                        String slPoS = entry.getSlPoSString();
                        String tlPoS = entry.getTlPoSString();
                        StringItem str = new StringItem(slLemma + " " + slPoS, " " + tlLemma + " " + tlPoS + "\n");
                        this.lookUpForm.append(str);
                    }
                } else {
                    switchDisplayable(getWordNotFoundAlert(), getLookUpForm());//GEN-LINE:|7-commandAction|16|42-postAction
                }
                }

            // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|17|27-preAction
        } else if (displayable == menuList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|17|27-preAction
                // write pre-action user code here
                menuListAction();//GEN-LINE:|7-commandAction|18|27-postAction
 
            } else if (command == exitCommandMenuList) {//GEN-LINE:|7-commandAction|19|67-preAction
 
                exitMIDlet();//GEN-LINE:|7-commandAction|20|67-postAction
 
            } else if (command == okCommandMenuList) {//GEN-LINE:|7-commandAction|21|153-preAction
                menuListAction();
//GEN-LINE:|7-commandAction|22|153-postAction
            }//GEN-BEGIN:|7-commandAction|23|15-preAction
        } else if (displayable == splashScreen) {
            if (command == SplashScreen.DISMISS_COMMAND) {//GEN-END:|7-commandAction|23|15-preAction
                // write pre-action user code here
                switchDisplayable(null, getWaitScreen());//GEN-LINE:|7-commandAction|24|15-postAction
            // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|25|20-preAction
        } else if (displayable == waitScreen) {
            if (command == WaitScreen.FAILURE_COMMAND) {//GEN-END:|7-commandAction|25|20-preAction
            // write pre-action user code here
//GEN-LINE:|7-commandAction|26|20-postAction
            // write post-action user code here
            } else if (command == WaitScreen.SUCCESS_COMMAND) {//GEN-LINE:|7-commandAction|27|19-preAction
                // write pre-action user code here
                switchDisplayable(null, getMenuList());//GEN-LINE:|7-commandAction|28|19-postAction
            // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|29|7-postCommandAction
        }//GEN-END:|7-commandAction|29|7-postCommandAction
    // write post-action user code here
    }//GEN-BEGIN:|7-commandAction|30|
    //</editor-fold>//GEN-END:|7-commandAction|30|

    /**
     * 
     */
    private final void cleanLookUpFormResults() {
        if (lookUpForm != null) {
            TextField tf = (TextField) this.lookUpForm.get(0);
            this.lookUpForm.deleteAll();
            this.lookUpForm.append(tf);
        }
    }

    /**
     * 
     */
    private final void resetLookUpForm() {
        this.cleanLookUpFormResults();
        this.srcWord.setString("");
    }

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: splashScreen ">//GEN-BEGIN:|13-getter|0|13-preInit
    /**
     * Returns an initiliazed instance of splashScreen component.
     * @return the initialized component instance
     */
    public SplashScreen getSplashScreen() {
        if (splashScreen == null) {//GEN-END:|13-getter|0|13-preInit
            // write pre-init user code here
            splashScreen = new SplashScreen(getDisplay());//GEN-BEGIN:|13-getter|1|13-postInit
            splashScreen.setTitle("mobileDIX");
            splashScreen.setCommandListener(this);
            splashScreen.setFullScreenMode(true);
            splashScreen.setImage(getImage1());
            splashScreen.setTimeout(1000);//GEN-END:|13-getter|1|13-postInit
        // write post-init user code here
        }//GEN-BEGIN:|13-getter|2|
        return splashScreen;
    }
    //</editor-fold>//GEN-END:|13-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: waitScreen ">//GEN-BEGIN:|16-getter|0|16-preInit
    /**
     * Returns an initiliazed instance of waitScreen component.
     * @return the initialized component instance
     */
    public WaitScreen getWaitScreen() {
        if (waitScreen == null) {//GEN-END:|16-getter|0|16-preInit
            // write pre-init user code here
            waitScreen = new WaitScreen(getDisplay());//GEN-BEGIN:|16-getter|1|16-postInit
            waitScreen.setCommandListener(this);
            waitScreen.setText("Loading configuration...");
            waitScreen.setTask(getTask());//GEN-END:|16-getter|1|16-postInit
        }//GEN-BEGIN:|16-getter|2|
        return waitScreen;
    }
    //</editor-fold>//GEN-END:|16-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: menuList ">//GEN-BEGIN:|25-getter|0|25-preInit
    /**
     * Returns an initiliazed instance of menuList component.
     * @return the initialized component instance
     */
    public List getMenuList() {
        if (menuList == null) {//GEN-END:|25-getter|0|25-preInit
            // write pre-init user code here
            menuList = new List("Menu", Choice.IMPLICIT);//GEN-BEGIN:|25-getter|1|25-postInit
            menuList.append("Look up", null);
            menuList.append("About", null);
            menuList.append("Exit", null);
            menuList.addCommand(getExitCommandMenuList());
            menuList.addCommand(getOkCommandMenuList());
            menuList.setCommandListener(this);
            menuList.setSelectedFlags(new boolean[] { false, false, false });//GEN-END:|25-getter|1|25-postInit
        // write post-init user code here
        }//GEN-BEGIN:|25-getter|2|
        return menuList;
    }
    //</editor-fold>//GEN-END:|25-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: menuListAction ">//GEN-BEGIN:|25-action|0|25-preAction
    /**
     * Performs an action assigned to the selected list element in the menuList component.
     */
    public void menuListAction() {//GEN-END:|25-action|0|25-preAction
        // enter pre-action user code here
        String __selectedString = getMenuList().getString(getMenuList().getSelectedIndex());//GEN-BEGIN:|25-action|1|30-preAction
        if (__selectedString != null) {
            if (__selectedString.equals("Look up")) {//GEN-END:|25-action|1|30-preAction
                // write pre-action user code here
                switchDisplayable(null, getLangSelectList());//GEN-LINE:|25-action|2|30-postAction
            // write post-action user code here
            } else if (__selectedString.equals("About")) {//GEN-LINE:|25-action|3|32-preAction
                // write pre-action user code here
                switchDisplayable(null, getAboutForm());//GEN-LINE:|25-action|4|32-postAction
            // write post-action user code here
            } else if (__selectedString.equals("Exit")) {//GEN-LINE:|25-action|5|68-preAction
                // write pre-action user code here
                exitMIDlet();//GEN-LINE:|25-action|6|68-postAction
            // write post-action user code here
            }//GEN-BEGIN:|25-action|7|25-postAction
        }//GEN-END:|25-action|7|25-postAction
    // enter post-action user code here
    }//GEN-BEGIN:|25-action|8|
    //</editor-fold>//GEN-END:|25-action|8|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: task ">//GEN-BEGIN:|21-getter|0|21-preInit
    /**
     * Returns an initiliazed instance of task component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getTask() {
        if (task == null) {//GEN-END:|21-getter|0|21-preInit
            // write pre-init user code here
            task = new SimpleCancellableTask();//GEN-BEGIN:|21-getter|1|21-execute
            task.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|21-getter|1|21-execute
                    readPropertyFile(); // reads meta.inf file                    
                }//GEN-BEGIN:|21-getter|2|21-postInit
            });//GEN-END:|21-getter|2|21-postInit
        // write post-init user code here
        }//GEN-BEGIN:|21-getter|3|
        return task;
    }
    //</editor-fold>//GEN-END:|21-getter|3|
    /**
     * 
     */
    private final void readPropertyFile() {
        this.loaded = new Hashtable();
        PropertyReader propReader = new PropertyReader("meta.inf", this.getClass());
        properties = propReader.readProperties();
        // source language full name
        String slfull = (String) properties.get("sl-full");
        // target language full name
        String tlfull = (String) properties.get("tl-full");
        sltlString = slfull + "-" + tlfull;
        direction = sltlString;
        tlslString = tlfull + "-" + slfull;
        this.buildSLTL();
        this.buildTLSL();
    }

    /**
     * 
     */
    private final void buildSLTL() {
        // number of dictionaries (parts) for sltl
        String sndics = (String) properties.get("sltl_n");
        int ndics = Integer.parseInt(sndics);
        this.idxLR = new String[ndics][2];
        
        for (int i = 1; i <= ndics; i++) {
            String fileName = "sltl_" + i + ".dix";
            String range = (String) properties.get(fileName);
            Vector values = this.parsePrefixes(range);          
            idxLR[i-1][0] = (String)values.elementAt(0);
            idxLR[i-1][1] = (String)values.elementAt(1);
        }
    }

    /**
     * 
     */
    private final void buildTLSL() {
        // number of dictionaries (parts) for sltl
        String sndics = (String) properties.get("tlsl_n");
        int ndics = Integer.parseInt(sndics);
        this.idxRL = new String[ndics][2];
        
        for (int i = 1; i <= ndics; i++) {
            String fileName = "tlsl_" + i + ".dix";
            String range = (String) properties.get(fileName);
            Vector values = this.parsePrefixes(range);          
            idxRL[i-1][0] = (String)values.elementAt(0);
            idxRL[i-1][1] = (String)values.elementAt(1);
        }
    }

    /**
     * 
     * @param prefixes
     * @return
     */
    private final Vector parsePrefixes(String prefixes) {
        Vector prefs = new Vector();
        int leng = prefixes.length();
        StringBuffer bfs = new StringBuffer();
        for (int i = 0; i < leng; i++) {
            char c = prefixes.charAt(i);
            if (c == '.') {
                String v = bfs.toString();
                prefs.addElement(v);
                bfs = new StringBuffer();
            } else {
                bfs.append(c);
            }
        }
        return prefs;
    }

    /**
     * 
     * @param srcLemma
     * @param direction
     * @return
     */
    private final Dictionary getSubDic(String srcLemma, int direction) {
        Dictionary dic = null;
        String fileName = null;
        if (direction == this.SLTL) {
            fileName = (String) this.getBlock(srcLemma, this.SLTL);
        }
        if (direction == this.TLSL) {
            fileName = (String) this.getBlock(srcLemma, this.TLSL);
        }
        if (loaded.get(fileName) == null) {
            if (loaded.size() > 2) {
                loaded = new Hashtable();
            }
            this.switchDisplayable(null, this.getDicLoaderForm());
            DicReader dicReader = new DicReader(fileName, this.getClass());
            dic = dicReader.read(progressBar);
            loaded.put(fileName, dic);
        } else {
            dic = (Dictionary) loaded.get(fileName);
        }
        this.switchDisplayable(null, this.getLookUpForm());
        return dic;
    }
    
    /**
     * 
     * @param srcLemma
     * @param dir
     * @return
     */
    private final String getBlock(String srcLemma, int dir) {
        String [][] idx;
        String fileName = "";
        if(dir == this.SLTL) {
            idx = this.idxLR;
        } else {
            idx = this.idxRL;
        }       
        int max = idx.length;
        int pt = -1;
        for(int i=0; i<max; i++) {
            String first = idx[i][0];
            first = first.toLowerCase();
            String last = idx[i][1];
            last = last.toLowerCase();
            
            if(first.compareTo(srcLemma) == 0 || (last.compareTo(srcLemma)) == 0) {
                pt = i+1;
                
            } else {
            if(first.compareTo(srcLemma) < 0 && (last.compareTo(srcLemma)) > 0) {
                pt = i+1;
            }
            }
            if( pt != -1) {
                     if(dir == this.SLTL) {
            fileName = "sltl_" + pt + ".dix";
            } else {
            fileName = "tlsl_" + pt + ".dix";
         }   
            return fileName;
            }
        }
        return null;        
    }

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: lookUpForm ">//GEN-BEGIN:|33-getter|0|33-preInit
    /**
     * Returns an initiliazed instance of lookUpForm component.
     * @return the initialized component instance
     */
    public Form getLookUpForm() {
        if (lookUpForm == null) {//GEN-END:|33-getter|0|33-preInit
            // write pre-init user code here
            lookUpForm = new Form(null, new Item[] { getSrcWord() });//GEN-BEGIN:|33-getter|1|33-postInit
            lookUpForm.addCommand(getOkCommandLookUp());
            lookUpForm.addCommand(getBackCommandLookUp());
            lookUpForm.setCommandListener(this);//GEN-END:|33-getter|1|33-postInit
            // write post-init user code here
            this.lookUpForm.setTitle(direction);
        }//GEN-BEGIN:|33-getter|2|
        return lookUpForm;
    }
    //</editor-fold>//GEN-END:|33-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: srcWord ">//GEN-BEGIN:|35-getter|0|35-preInit
    /**
     * Returns an initiliazed instance of srcWord component.
     * @return the initialized component instance
     */
    public TextField getSrcWord() {
        if (srcWord == null) {//GEN-END:|35-getter|0|35-preInit
            // write pre-init user code here
            srcWord = new TextField("Word:", null, 32, TextField.ANY);//GEN-LINE:|35-getter|1|35-postInit
        // write post-init user code here
        }//GEN-BEGIN:|35-getter|2|
        return srcWord;
    }
    //</editor-fold>//GEN-END:|35-getter|2|
    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: font ">//GEN-BEGIN:|40-getter|0|40-preInit
    /**
     * Returns an initiliazed instance of font component.
     * @return the initialized component instance
     */
    public Font getFont() {
        if (font == null) {//GEN-END:|40-getter|0|40-preInit
            // write pre-init user code here
            font = Font.getDefaultFont();//GEN-LINE:|40-getter|1|40-postInit
        // write post-init user code here
        }//GEN-BEGIN:|40-getter|2|
        return font;
    }
    //</editor-fold>//GEN-END:|40-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommandLookUp ">//GEN-BEGIN:|41-getter|0|41-preInit
    /**
     * Returns an initiliazed instance of okCommandLookUp component.
     * @return the initialized component instance
     */
    public Command getOkCommandLookUp() {
        if (okCommandLookUp == null) {//GEN-END:|41-getter|0|41-preInit
            // write pre-init user code here
            okCommandLookUp = new Command("Ok", Command.OK, 0);//GEN-LINE:|41-getter|1|41-postInit
        // write post-init user code here
            
            
        }//GEN-BEGIN:|41-getter|2|
        return okCommandLookUp;
    }
    //</editor-fold>//GEN-END:|41-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommandLookUp ">//GEN-BEGIN:|43-getter|0|43-preInit
    /**
     * Returns an initiliazed instance of backCommandLookUp component.
     * @return the initialized component instance
     */
    public Command getBackCommandLookUp() {
        if (backCommandLookUp == null) {//GEN-END:|43-getter|0|43-preInit
            // write pre-init user code here
            backCommandLookUp = new Command("Back", Command.BACK, 0);//GEN-LINE:|43-getter|1|43-postInit
        // write post-init user code here
        }//GEN-BEGIN:|43-getter|2|
        return backCommandLookUp;
    }
    //</editor-fold>//GEN-END:|43-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: langSelectList ">//GEN-BEGIN:|47-getter|0|47-preInit
    /**
     * Returns an initiliazed instance of langSelectList component.
     * @return the initialized component instance
     */
    public List getLangSelectList() {
        if (langSelectList == null) {//GEN-END:|47-getter|0|47-preInit
            // source language full name
            //String slfull = (String) this.properties.get("slfull");
            // target language full name
            //String tlfull = (String) this.properties.get("tlfull");

            //sltlString = slfull + "-" + tlfull;
            //tlslString = tlfull + "-" + slfull;

            langSelectList = new List("Select direction", Choice.IMPLICIT);//GEN-BEGIN:|47-getter|1|47-postInit
            langSelectList.append(sltlString, null);
            langSelectList.append(tlslString, null);
            langSelectList.addCommand(getOkCommandSelectDirection());
            langSelectList.addCommand(getBackCommandSelectDirection());
            langSelectList.setCommandListener(this);
            langSelectList.setSelectedFlags(new boolean[] { false, false });//GEN-END:|47-getter|1|47-postInit
        // write post-init user code here
            
        }//GEN-BEGIN:|47-getter|2|
        return langSelectList;
    }
    //</editor-fold>//GEN-END:|47-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: langSelectListAction ">//GEN-BEGIN:|47-action|0|47-preAction
    /**
     * Performs an action assigned to the selected list element in the langSelectList component.
     */
    public void langSelectListAction() {//GEN-END:|47-action|0|47-preAction
        String __selectedString = getLangSelectList().getString(getLangSelectList().getSelectedIndex());//GEN-BEGIN:|47-action|1|62-preAction
        if (__selectedString != null) {
            if (__selectedString.equals(sltlString)) {//GEN-END:|47-action|1|62-preAction
                direction = sltlString;
                switchDisplayable(null, getDicLoaderForm());//GEN-LINE:|47-action|2|62-postAction
                this.dicLoaderMessage.setLabel(direction);
                this.tlsldic = null;
                Runtime.getRuntime().gc();
                this.tlslLoaded = false;
                if (!this.sltlLoaded) {
                    //DicReader dicReader = new DicReader("sltl.dix");
                    //this.sltldic = dicReader.read(this.progressBar);
                    this.sltlLoaded = true;
                }
                this.currentDic = this.SLTL;
                this.switchDisplayable(null, this.getLookUpForm());
                lookUpForm.setTitle(direction);
            } else if (__selectedString.equals(tlslString)) {//GEN-LINE:|47-action|3|63-preAction
                direction = tlslString;
                this.sltldic = null;
                Runtime.getRuntime().gc();
                this.sltlLoaded = false;
                switchDisplayable(null, getDicLoaderForm());//GEN-LINE:|47-action|4|63-postAction
                this.dicLoaderMessage.setLabel(direction);
                if (!this.tlslLoaded) {
                    //DicReader dicReader = new DicReader("tlsl.dix");
                    //this.tlsldic = dicReader.read(this.progressBar);
                    this.tlslLoaded = true;
                }
                this.currentDic = this.TLSL;
                this.switchDisplayable(null, this.getLookUpForm());
                lookUpForm.setTitle(direction);
            }//GEN-BEGIN:|47-action|5|47-postAction
        }//GEN-END:|47-action|5|47-postAction
    // enter post-action user code here
    }//GEN-BEGIN:|47-action|6|
    //</editor-fold>//GEN-END:|47-action|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommandSelectDirection ">//GEN-BEGIN:|53-getter|0|53-preInit
    /**
     * Returns an initiliazed instance of okCommandSelectDirection component.
     * @return the initialized component instance
     */
    public Command getOkCommandSelectDirection() {
        if (okCommandSelectDirection == null) {//GEN-END:|53-getter|0|53-preInit
            // write pre-init user code here
            okCommandSelectDirection = new Command("Ok", Command.OK, 0);//GEN-LINE:|53-getter|1|53-postInit
        // write post-init user code here
        }//GEN-BEGIN:|53-getter|2|
        return okCommandSelectDirection;
    }
    //</editor-fold>//GEN-END:|53-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommandSelectDirection ">//GEN-BEGIN:|56-getter|0|56-preInit
    /**
     * Returns an initiliazed instance of backCommandSelectDirection component.
     * @return the initialized component instance
     */
    public Command getBackCommandSelectDirection() {
        if (backCommandSelectDirection == null) {//GEN-END:|56-getter|0|56-preInit
            // write pre-init user code here
            backCommandSelectDirection = new Command("Back", Command.BACK, 0);//GEN-LINE:|56-getter|1|56-postInit
        // write post-init user code here
        }//GEN-BEGIN:|56-getter|2|
        return backCommandSelectDirection;
    }
    //</editor-fold>//GEN-END:|56-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: exitCommandMenuList ">//GEN-BEGIN:|66-getter|0|66-preInit
    /**
     * Returns an initiliazed instance of exitCommandMenuList component.
     * @return the initialized component instance
     */
    public Command getExitCommandMenuList() {
        if (exitCommandMenuList == null) {//GEN-END:|66-getter|0|66-preInit
            // write pre-init user code here
            exitCommandMenuList = new Command("Exit", Command.EXIT, 0);//GEN-LINE:|66-getter|1|66-postInit
        // write post-init user code here
        }//GEN-BEGIN:|66-getter|2|
        return exitCommandMenuList;
    }
    //</editor-fold>//GEN-END:|66-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: wordNotFoundAlert ">//GEN-BEGIN:|71-getter|0|71-preInit
    /**
     * Returns an initiliazed instance of wordNotFoundAlert component.
     * @return the initialized component instance
     */
    public Alert getWordNotFoundAlert() {
        if (wordNotFoundAlert == null) {//GEN-END:|71-getter|0|71-preInit
            // write pre-init user code here
            wordNotFoundAlert = new Alert(null, "Word not found!", null, null);//GEN-BEGIN:|71-getter|1|71-postInit
            wordNotFoundAlert.setTimeout(2000);//GEN-END:|71-getter|1|71-postInit
        // write post-init user code here
        }//GEN-BEGIN:|71-getter|2|
        return wordNotFoundAlert;
    }
    //</editor-fold>//GEN-END:|71-getter|2|
    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: task1 ">//GEN-BEGIN:|80-getter|0|80-preInit
    /**
     * Returns an initiliazed instance of task1 component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getTask1() {
        if (task1 == null) {//GEN-END:|80-getter|0|80-preInit
            // write pre-init user code here
            task1 = new SimpleCancellableTask();//GEN-BEGIN:|80-getter|1|80-execute
            task1.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|80-getter|1|80-execute
                    if (direction.equals(sltlString)) {
                        if (!sltlLoaded) {
                            //DicReader dicReader = new DicReader("../sltl.dix");
                            //dic = dicReader.read();
                            //properties = dicReader.getProperties();
                            sltlLoaded = true;
                            tlslLoaded = false;
                        }
                    }
                    if (direction.equals(tlslString)) {
                        if (!tlslLoaded) {
//                            DicReader dicReader = new DicReader("../tlsl.dix", this.getClass());
                            //dic = dicReader.read();
                            //properties = dicReader.getProperties();
                            tlslLoaded = true;
                            sltlLoaded = false;
                        }
                    }
                }//GEN-BEGIN:|80-getter|2|80-postInit
            });//GEN-END:|80-getter|2|80-postInit
        }//GEN-BEGIN:|80-getter|3|
        return task1;
    }
    //</editor-fold>//GEN-END:|80-getter|3|
    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand2 ">//GEN-BEGIN:|92-getter|0|92-preInit
    /**
     * Returns an initiliazed instance of backCommand2 component.
     * @return the initialized component instance
     */
    public Command getBackCommand2() {
        if (backCommand2 == null) {//GEN-END:|92-getter|0|92-preInit
            // write pre-init user code here
            backCommand2 = new Command("Back", Command.BACK, 0);//GEN-LINE:|92-getter|1|92-postInit
        // write post-init user code here
        }//GEN-BEGIN:|92-getter|2|
        return backCommand2;
    }
    //</editor-fold>//GEN-END:|92-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand2 ">//GEN-BEGIN:|94-getter|0|94-preInit
    /**
     * Returns an initiliazed instance of okCommand2 component.
     * @return the initialized component instance
     */
    public Command getOkCommand2() {
        if (okCommand2 == null) {//GEN-END:|94-getter|0|94-preInit
            // write pre-init user code here
            okCommand2 = new Command("Ok", Command.OK, 0);//GEN-LINE:|94-getter|1|94-postInit
        // write post-init user code here
        }//GEN-BEGIN:|94-getter|2|
        return okCommand2;
    }
    //</editor-fold>//GEN-END:|94-getter|2|
    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: task2 ">//GEN-BEGIN:|106-getter|0|106-preInit
    /**
     * Returns an initiliazed instance of task2 component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getTask2() {
        if (task2 == null) {//GEN-END:|106-getter|0|106-preInit
            // write pre-init user code here
            task2 = new SimpleCancellableTask();//GEN-BEGIN:|106-getter|1|106-execute
            task2.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|106-getter|1|106-execute
                // write task-execution user code here
                }//GEN-BEGIN:|106-getter|2|106-postInit
            });//GEN-END:|106-getter|2|106-postInit
        // write post-init user code here
        }//GEN-BEGIN:|106-getter|3|
        return task2;
    }
    //</editor-fold>//GEN-END:|106-getter|3|
    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand3 ">//GEN-BEGIN:|112-getter|0|112-preInit
    /**
     * Returns an initiliazed instance of backCommand3 component.
     * @return the initialized component instance
     */
    public Command getBackCommand3() {
        if (backCommand3 == null) {//GEN-END:|112-getter|0|112-preInit
            // write pre-init user code here
            backCommand3 = new Command("Back", Command.BACK, 0);//GEN-LINE:|112-getter|1|112-postInit
        // write post-init user code here
        }//GEN-BEGIN:|112-getter|2|
        return backCommand3;
    }
    //</editor-fold>//GEN-END:|112-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: dicLoaderForm ">//GEN-BEGIN:|116-getter|0|116-preInit
    /**
     * Returns an initiliazed instance of dicLoaderForm component.
     * @return the initialized component instance
     */
    public Form getDicLoaderForm() {
        if (dicLoaderForm == null) {//GEN-END:|116-getter|0|116-preInit
            // write pre-init user code here
            dicLoaderForm = new Form("", new Item[] { getDicLoaderMessage(), getProgressBar(), getSpacer() });//GEN-BEGIN:|116-getter|1|116-postInit
            dicLoaderForm.addCommand(getCancelCommandDicLoader());
            dicLoaderForm.setCommandListener(this);//GEN-END:|116-getter|1|116-postInit
        // write post-init user code here
            
        }//GEN-BEGIN:|116-getter|2|
        return dicLoaderForm;
    }
    //</editor-fold>//GEN-END:|116-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: dicLoaderMessage ">//GEN-BEGIN:|119-getter|0|119-preInit
    /**
     * Returns an initiliazed instance of dicLoaderMessage component.
     * @return the initialized component instance
     */
    public StringItem getDicLoaderMessage() {
        if (dicLoaderMessage == null) {//GEN-END:|119-getter|0|119-preInit
            // write pre-init user code here
            dicLoaderMessage = new StringItem(direction, "\nLoading dictionary...");//GEN-LINE:|119-getter|1|119-postInit
        // write post-init user code here
        }//GEN-BEGIN:|119-getter|2|
        return dicLoaderMessage;
    }
    //</editor-fold>//GEN-END:|119-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: progressBar ">//GEN-BEGIN:|120-getter|0|120-preInit
    /**
     * Returns an initiliazed instance of progressBar component.
     * @return the initialized component instance
     */
    public Gauge getProgressBar() {
        if (progressBar == null) {//GEN-END:|120-getter|0|120-preInit
            // write pre-init user code here
            progressBar = new Gauge("Progress", false, 100, 50);//GEN-LINE:|120-getter|1|120-postInit
        // write post-init user code here
        }//GEN-BEGIN:|120-getter|2|
        return progressBar;
    }
    //</editor-fold>//GEN-END:|120-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: cancelCommandDicLoader ">//GEN-BEGIN:|121-getter|0|121-preInit
    /**
     * Returns an initiliazed instance of cancelCommandDicLoader component.
     * @return the initialized component instance
     */
    public Command getCancelCommandDicLoader() {
        if (cancelCommandDicLoader == null) {//GEN-END:|121-getter|0|121-preInit
            // write pre-init user code here
            cancelCommandDicLoader = new Command("Cancel", Command.CANCEL, 0);//GEN-LINE:|121-getter|1|121-postInit
        
            // write post-init user code here
        }//GEN-BEGIN:|121-getter|2|
        return cancelCommandDicLoader;
    }
    //</editor-fold>//GEN-END:|121-getter|2|



    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: aboutForm ">//GEN-BEGIN:|132-getter|0|132-preInit
    /**
     * Returns an initiliazed instance of aboutForm component.
     * @return the initialized component instance
     */
    public Form getAboutForm() {
        if (aboutForm == null) {//GEN-END:|132-getter|0|132-preInit
            // write pre-init user code here
            aboutForm = new Form("TinyLex v0.3", new Item[] { getAboutText1(), getSpacer1(), getStringItem1() });//GEN-BEGIN:|132-getter|1|132-postInit
            aboutForm.addCommand(getBackCommandAboutForm());
            aboutForm.addCommand(getOkCommandAboutForm());
            aboutForm.setCommandListener(this);//GEN-END:|132-getter|1|132-postInit
        // write post-init user code here
        }//GEN-BEGIN:|132-getter|2|
        return aboutForm;
    }
    //</editor-fold>//GEN-END:|132-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand4 ">//GEN-BEGIN:|126-getter|0|126-preInit
    /**
     * Returns an initiliazed instance of backCommand4 component.
     * @return the initialized component instance
     */
    public Command getBackCommand4() {
        if (backCommand4 == null) {//GEN-END:|126-getter|0|126-preInit
            // write pre-init user code here
            backCommand4 = new Command("Back", Command.BACK, 0);//GEN-LINE:|126-getter|1|126-postInit
        // write post-init user code here
        }//GEN-BEGIN:|126-getter|2|
        return backCommand4;
    }
    //</editor-fold>//GEN-END:|126-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand3 ">//GEN-BEGIN:|129-getter|0|129-preInit
    /**
     * Returns an initiliazed instance of okCommand3 component.
     * @return the initialized component instance
     */
    public Command getOkCommand3() {
        if (okCommand3 == null) {//GEN-END:|129-getter|0|129-preInit
            // write pre-init user code here
            okCommand3 = new Command("Ok", Command.OK, 0);//GEN-LINE:|129-getter|1|129-postInit
        // write post-init user code here
        }//GEN-BEGIN:|129-getter|2|
        return okCommand3;
    }
    //</editor-fold>//GEN-END:|129-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommandAboutForm ">//GEN-BEGIN:|134-getter|0|134-preInit
    /**
     * Returns an initiliazed instance of backCommandAboutForm component.
     * @return the initialized component instance
     */
    public Command getBackCommandAboutForm() {
        if (backCommandAboutForm == null) {//GEN-END:|134-getter|0|134-preInit
            // write pre-init user code here
            backCommandAboutForm = new Command("Back", Command.BACK, 0);//GEN-LINE:|134-getter|1|134-postInit
        // write post-init user code here
        }//GEN-BEGIN:|134-getter|2|
        return backCommandAboutForm;
    }
    //</editor-fold>//GEN-END:|134-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommandAboutForm ">//GEN-BEGIN:|136-getter|0|136-preInit
    /**
     * Returns an initiliazed instance of okCommandAboutForm component.
     * @return the initialized component instance
     */
    public Command getOkCommandAboutForm() {
        if (okCommandAboutForm == null) {//GEN-END:|136-getter|0|136-preInit
            // write pre-init user code here
            okCommandAboutForm = new Command("Ok", Command.OK, 0);//GEN-LINE:|136-getter|1|136-postInit
        // write post-init user code here
        }//GEN-BEGIN:|136-getter|2|
        return okCommandAboutForm;
    }
    //</editor-fold>//GEN-END:|136-getter|2|









    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: aboutText1 ">//GEN-BEGIN:|144-getter|0|144-preInit
    /**
     * Returns an initiliazed instance of aboutText1 component.
     * @return the initialized component instance
     */
    public StringItem getAboutText1() {
        if (aboutText1 == null) {//GEN-END:|144-getter|0|144-preInit
            // write pre-init user code here
            aboutText1 = new StringItem("About TinyLex", "\nTinyLex is a J2ME (Java 2 Micro Edition) program for mobile devices which looks up dictionary entries. It is free software and released under the terms of the GNU General Public License v2.0.\n(c) 2008 Enrique Benimeli Bofarull.\n", Item.PLAIN);//GEN-BEGIN:|144-getter|1|144-postInit
            aboutText1.setLayout(ImageItem.LAYOUT_LEFT | ImageItem.LAYOUT_NEWLINE_AFTER);//GEN-END:|144-getter|1|144-postInit
        // write post-init user code here
        }//GEN-BEGIN:|144-getter|2|
        return aboutText1;
    }
    //</editor-fold>//GEN-END:|144-getter|2|
    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: spacer ">//GEN-BEGIN:|146-getter|0|146-preInit
    /**
     * Returns an initiliazed instance of spacer component.
     * @return the initialized component instance
     */
    public Spacer getSpacer() {
        if (spacer == null) {//GEN-END:|146-getter|0|146-preInit
            // write pre-init user code here
            spacer = new Spacer(16, 1);//GEN-LINE:|146-getter|1|146-postInit
        // write post-init user code here
        }//GEN-BEGIN:|146-getter|2|
        return spacer;
    }
    //</editor-fold>//GEN-END:|146-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: image1 ">//GEN-BEGIN:|147-getter|0|147-preInit
    /**
     * Returns an initiliazed instance of image1 component.
     * @return the initialized component instance
     */
    public Image getImage1() {
        if (image1 == null) {//GEN-END:|147-getter|0|147-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|147-getter|1|147-@java.io.IOException
                image1 = Image.createImage("/apertium.png");
            } catch (java.io.IOException e) {//GEN-END:|147-getter|1|147-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|147-getter|2|147-postInit
        // write post-init user code here
        }//GEN-BEGIN:|147-getter|3|
        return image1;
    }
    //</editor-fold>//GEN-END:|147-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem1 ">//GEN-BEGIN:|148-getter|0|148-preInit
    /**
     * Returns an initiliazed instance of stringItem1 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem1() {
        if (stringItem1 == null) {//GEN-END:|148-getter|0|148-preInit
            // write pre-init user code here
            stringItem1 = new StringItem("About linguistic data", "\nThe linguistic data used by this program is based on Apertium dictionaries. Information about authors and license in www.apertium.org");//GEN-LINE:|148-getter|1|148-postInit
        // write post-init user code here
        }//GEN-BEGIN:|148-getter|2|
        return stringItem1;
    }
    //</editor-fold>//GEN-END:|148-getter|2|





    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: spacer1 ">//GEN-BEGIN:|151-getter|0|151-preInit
    /**
     * Returns an initiliazed instance of spacer1 component.
     * @return the initialized component instance
     */
    public Spacer getSpacer1() {
        if (spacer1 == null) {//GEN-END:|151-getter|0|151-preInit
            // write pre-init user code here
            spacer1 = new Spacer(16, 1);//GEN-LINE:|151-getter|1|151-postInit
        // write post-init user code here
        }//GEN-BEGIN:|151-getter|2|
        return spacer1;
    }
    //</editor-fold>//GEN-END:|151-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommandMenuList ">//GEN-BEGIN:|152-getter|0|152-preInit
    /**
     * Returns an initiliazed instance of okCommandMenuList component.
     * @return the initialized component instance
     */
    public Command getOkCommandMenuList() {
        if (okCommandMenuList == null) {//GEN-END:|152-getter|0|152-preInit
            // write pre-init user code here
            okCommandMenuList = new Command("Ok", Command.OK, 0);//GEN-LINE:|152-getter|1|152-postInit
            // write post-init user code here
        }//GEN-BEGIN:|152-getter|2|
        return okCommandMenuList;
    }
    //</editor-fold>//GEN-END:|152-getter|2|
    /**
     * Returns a display instance.
     * @return the display instance.
     */
    public Display getDisplay() {
        return Display.getDisplay(this);
    }

    /**
     * Exits MIDlet.
     */
    public void exitMIDlet() {
        switchDisplayable(null, null);
        destroyApp(true);
        notifyDestroyed();
    }

    /**
     * Called when MIDlet is started.
     * Checks whether the MIDlet have been already started and initialize/starts or resumes the MIDlet.
     */
    public void startApp() {
        if (midletPaused) {
            resumeMIDlet();
        } else {
            initialize();
            startMIDlet();
        }
        midletPaused = false;
    }

    /**
     * Called when MIDlet is paused.
     */
    public void pauseApp() {
        midletPaused = true;
    }

    /**
     * Called to signal the MIDlet to terminate.
     * @param unconditional if true, then the MIDlet has to be unconditionally terminated and all resources has to be released.
     */
    public void destroyApp(boolean unconditional) {
    }
}
