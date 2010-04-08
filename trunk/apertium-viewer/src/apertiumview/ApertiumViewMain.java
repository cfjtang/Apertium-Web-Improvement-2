/*
 * ApertiumViewMain.java
 */

package apertiumview;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.SwingUtilities;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class ApertiumViewMain extends SingleFrameApplication {
    private ApertiumView mainFrame;

    
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(mainFrame=new ApertiumView(this));

        java.awt.Window root= SwingUtilities.getWindowAncestor(mainFrame.mainPanel);
        String geom = mainFrame.prefs.get("geometry",null);
        //System.out.println("geom = " + geom);
        if (geom!= null)  try {
            root.pack();
            String[] g = geom.split(",");
            root.setLocation( Integer.parseInt(g[0]), Integer.parseInt(g[1]));
            root.setSize(Integer.parseInt(g[2]), Integer.parseInt(g[3]));
            //System.out.println("geom = " + root);

            //root.setLocation( 0,0);
            //root.setSize(300,400);
            root.validate();
        } catch (Exception e) {
            e.printStackTrace();
        } else
            root.pack();

        if (mainFrame.prefs.get("dividerLocation", null)==null) {
            mainFrame.fitToText();
        }
        // Seems like closing window doesent exit the app - so force exit
        root.addWindowListener(new WindowAdapter() {
        @Override
          public void windowClosing(WindowEvent e) {shutdown(); System.exit(0);}
        });
        
    }

     @Override protected void shutdown() {
         mainFrame.shutdown();
         java.awt.Window root= SwingUtilities.getWindowAncestor(mainFrame.mainPanel);
        Point location = root.getLocation();

        Dimension size = root.getSize();
        mainFrame.prefs.put("geometry", ""+location.x+","+location.y+","+size.width+","+size.height);
        System.err.println("shutdown size = " + size);

         super.shutdown();
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
     * @return the instance of ApertiumViewMain
     */
    public static ApertiumViewMain getApplication() {
        return Application.getInstance(ApertiumViewMain.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(ApertiumViewMain.class, args);
    }
}
