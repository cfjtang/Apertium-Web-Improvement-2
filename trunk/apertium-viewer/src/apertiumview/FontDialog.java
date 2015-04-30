/*
 * Shameless copy of http://forums.sun.com/thread.jspa?threadID=417735&messageID=2103866
 * until somebody cares to make a decent work about font dialogs
 * 
 */

package apertiumview;

/**
 *
 * @author j
 */
 
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.beans.*;
 
class FontDialog extends JDialog implements ActionListener{
 
    private JButton doneButton,reset,cancel;
	private JComboBox font, style, size;
    //private PropertyFacilitator propertyfacilitator = null;
    private PropertyEditor editor = null;
    private int propNum = 0;
	private Font f, initial;
 
	public FontDialog(Frame f) 
	{
		super(f,true);
		getContentPane().setLayout(new BorderLayout());
		initial = new Font("Arial",Font.PLAIN,8);
		
		JPanel fontstuff = new JPanel();
		fontstuff.setPreferredSize(new Dimension(400,40));
		fontstuff.setLayout(new FlowLayout());
		getContentPane().add(fontstuff,BorderLayout.NORTH);
 
		GraphicsEnvironment ge  = GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		Object[] fontlist = ge.getAvailableFontFamilyNames();
		font = new JComboBox(fontlist);
		//font.setPreferredSize(new Dimension(150,30));
		fontstuff.add(font);
 
		Object[] stylelist = {"Plain", "Bold", "Italic"};
		style = new JComboBox(stylelist);
		//style.setPreferredSize(new Dimension(60,30));
		fontstuff.add(style);
 
		Object[] sizelist = {"8", "9", "10","11","12", "13", "14","15", "16","18","20", "22", "24","26","28", "32", "36","72"};
		size = new JComboBox(sizelist);
		//size.setPreferredSize(new Dimension(40,30));
		fontstuff.add(size);
		
 
 
		JPanel p = new JPanel();
		p.setPreferredSize(new Dimension(400,50));
		p.setLayout(new FlowLayout());
		getContentPane().add(p,BorderLayout.SOUTH);
 
		doneButton = new JButton("Done");
//		doneButton.setPreferredSize(new Dimension(80,30));
		doneButton.addActionListener(this);
		p.add(doneButton);
 
		reset = new JButton("Reset");
//		reset.setPreferredSize(new Dimension(80,30));
		reset.addActionListener(this);
		p.add(reset);
		
		cancel = new JButton("Cancel");
//		cancel.setPreferredSize(new Dimension(80,30));
		cancel.addActionListener(this);
		p.add(cancel);
 
		pack();
			
	}
 
	public void setInitialFont(Font f)
	{
		initial = f;

    System.err.println("initial = " + initial);

		font.setSelectedItem(f.getFamily());
		
		if (f.getStyle() == Font.BOLD)
		{
			style.setSelectedItem("Bold");
		}
		else if (f.getStyle() == Font.ITALIC)
		{
			style.setSelectedItem("Italic");
		}
		else
		{
			style.setSelectedItem("Plain");
		}
		size.setSelectedItem(String.valueOf(f.getSize()));	
		
	}
 
	public void setFont(Font ff)
	{
		System.out.println(ff);
		f = ff;
		System.out.println(f);
	}
			
	public Font getFont()
	{
		return f;
	}
		
 
	
	public void actionPerformed(ActionEvent evt) {
		// Button down.
		
		// RIGHT HERE
		if (evt.getSource() == cancel)
		{
			dispose();
 
		}
		if (evt.getSource() == reset)
		{
			//dispose();
			setInitialFont(initial);
		}
		if (evt.getSource() == doneButton)
		{
			System.out.println("selected font " + font.getSelectedItem());
			int s = 0;
			if (style.getSelectedItem().equals("Bold"))
			{
				s = Font.BOLD;
 
			}
			else if (style.getSelectedItem().equals("Italic"))
			{
				s = Font.ITALIC;
			}
			else
			{
				s = Font.PLAIN;
			}
			Font newfont = new Font((String)font.getSelectedItem(),s, Integer.parseInt((String)size.getSelectedItem()));
			setFont(newfont);
			dispose();
			//setVisible(false);
			//propertyfacilitator.valueChanged();
		}
	 }	
 
 
	
 
}
 
 
