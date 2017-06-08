import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class MatPanel extends JPanel
{
	private static final int SIZE = 20;
	private static final int SCALE = 2;
	private static final Border NOTSELECTED = BorderFactory.createLineBorder(Color.black);
	private int group;
	private JLabel[] tileIcons;
	
		//---------------------------------------
		//	JPanel to display tiles by group
		//	Displayed in JTabbedPane
		//---------------------------------------
		public MatPanel(String name, int group, MouseListener ml)
		{
			this.group = group;
			tileIcons = new JLabel[16];
			
			this.setLayout(new GridBagLayout());
			
			for(int n = 0; n < 16; n++)
			{
				tileIcons[n] = new JLabel(new ImageIcon(Controler.getTileByCode(group * 16 + n).scaledImage));
				tileIcons[n].addMouseListener(ml);
				tileIcons[n].setBorder(NOTSELECTED);
			}
			
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.CENTER;
			
			for(c.gridy = 0; c.gridy < 4; c.gridy++)
			for(c.gridx = 0; c.gridx < 4; c.gridx++)
			{
				this.add(tileIcons[c.gridx + (c.gridy * 4)], c);
			}
		}
		
		public JLabel getTile(int n)
		{
			return tileIcons[n];
		}
		
		public void resetImages()
		{
			for(int n=0; n<16; n++)
			{
				tileIcons[n].setIcon(new ImageIcon(Controler.getTileByCode(group * 16 + n).scaledImage));
			}
		}
}
