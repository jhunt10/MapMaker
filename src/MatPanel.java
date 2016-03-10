import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class MatPanel extends JPanel{
	
		private JLabel[] tileIcons;
		
		private static final int SIZE = 20;
		private static final int SCALE = 2;
		//private static final Border SELECTED = BorderFactory.createLineBorder(Color.red);
		private static final Border NOTSELECTED = BorderFactory.createLineBorder(Color.black);
		
		public MatPanel(String name, Tile[] tIn, MouseListener ml){
			tileIcons = new JLabel[16];
			
			this.setSize(new Dimension(800,800));
			this.setLayout(new GridBagLayout());
			
			for(int n=0; n<16; n++){
				tileIcons[n] = new JLabel(new ImageIcon(tIn[n].scaledImage));
				tileIcons[n].addMouseListener(ml);
				tileIcons[n].setBorder(NOTSELECTED);
			}
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.CENTER;
			//c.insets = new Insets(2,2,2,2);
			c.gridy=0;
			c.gridx=0; this.add(tileIcons[0], c);
			c.gridx=1; this.add(tileIcons[1], c);
			c.gridx=2; this.add(tileIcons[2], c);
			c.gridx=3; this.add(tileIcons[3], c);
			c.gridy=1;
			c.gridx=0; this.add(tileIcons[4], c);
			c.gridx=1; this.add(tileIcons[5], c);
			c.gridx=2; this.add(tileIcons[6], c);
			c.gridx=3; this.add(tileIcons[7], c);
			c.gridy=2;
			c.gridx=0; this.add(tileIcons[8], c);
			c.gridx=1; this.add(tileIcons[9], c);
			c.gridx=2; this.add(tileIcons[10], c);
			c.gridx=3; this.add(tileIcons[11], c);
			c.gridy=3;
			c.gridx=0; this.add(tileIcons[12], c);
			c.gridx=1; this.add(tileIcons[13], c);
			c.gridx=2; this.add(tileIcons[14], c);
			c.gridx=3; this.add(tileIcons[15], c);
		}
		
		public JLabel getTile(int n){
			return tileIcons[n];
		}
		
		public void setImages(Tile[] tIn){
			for(int n=0; n<16; n++){
				tileIcons[n].setIcon(new ImageIcon(tIn[n].scaledImage));
			}
		}
		
//		public boolean selectTile(int n){
//			for(int i=0; i<16; i++){
//				if(i==n){
//					tileIcons[i].setBorder(SELECTED);
//					return true;
//				} else {
//					tileIcons[i].setBorder(NOTSELECTED);
//				}
//			}
//			
//			System.out.println("In SelectTile");
//		}
}
