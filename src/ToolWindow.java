import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

public class ToolWindow extends JPanel{
	
	private static final int SIZE = 20;
	private static final int SCALE = 2;
	
	private BufferedImage tileSet;
	private JTabbedPane tabs;
	private MatPanel[] mats;
	private JLabel zoomLab, xLab, yLab, cLab;
	private ImageIcon selectedRightLab, selectedLeftLab;
	private JButton save, load, zoomIn, zoomOut;
	private String[] zoomText;
	private int zoom;
	private Tile[][] tiles;
	private JButton pen, fill;
	private JLabel penFill;
	
	public Tile selectedRight, selectedLeft;
	
	public ToolWindow(Hashtable tilesIn, MouseListener ml, ActionListener al){
		tiles = new Tile[8][16];
		
		int x = 0;
		for(int n = 0; n<4; n++){
			for(int i = 0; i<16; i++){
				tiles[n][i] = (Tile)tilesIn.get((byte)(n*16 + i));
			}
			x++;
		}
		x=0;
		for(int n = 4; n<8; n++){
			for(int i = 0; i<16; i++){
				tiles[n][i] = (Tile)tilesIn.get((byte)((n-4)*16 + i + 128));
			}
			x++;
		}
		
		save = new JButton("Save");
		save.setActionCommand("SAVE");
		save.addActionListener(al);
		
		load = new JButton("Load");
		load.setActionCommand("LOAD");
		load.addActionListener(al);
		
		pen = new JButton("Pen");
		pen.setActionCommand("PEN");
		pen.addActionListener(al);
		pen.setSelected(true);
		
		fill = new JButton("Fill");
		fill.setActionCommand("FILL");
		fill.addActionListener(al);
		fill.setSelected(false);
		
		penFill = new JLabel(" Pen ");
		selectedLeftLab = new ImageIcon(tiles[0][0].scaledImage);
		selectedLeft = tiles[0][0];
		selectedRightLab = new ImageIcon(tiles[0][1].scaledImage);
		selectedRight = tiles[0][1];
		
		xLab = new JLabel(" X: ");
		yLab = new JLabel(" Y: ");
		cLab = new JLabel(" C: ");
		
		zoomText = new String[6];
		zoomText[0] = "25%";
		zoomText[1] = "50%";
		zoomText[2] = "100%";
		zoomText[3] = "200%";
		zoomText[4] = "300%";
		zoomText[5] = "400%";
		zoom = 2;
		zoomLab = new JLabel(zoomText[zoom]);
		zoomLab.setHorizontalAlignment(JLabel.LEFT);
		zoomLab.setVerticalAlignment(JLabel.CENTER);
		zoomIn = new JButton("+");
		zoomIn.setActionCommand("ZOOMIN");
		zoomIn.addActionListener(al);
		zoomOut = new JButton("-");
		zoomOut.setActionCommand("ZOOMOUT");
		zoomOut.addActionListener(al);
		
		
		mats = new MatPanel[8];
		
		tabs = new JTabbedPane();
		mats[0] = new MatPanel("Base", tiles[0], ml);
		mats[1] = new MatPanel("Passable 1", tiles[1], ml);
		mats[2] = new MatPanel("Passable 2", tiles[2], ml);
		mats[3] = new MatPanel("Passable 3", tiles[3], ml);
		mats[4] = new MatPanel("Ass", tiles[4], ml);
		mats[5] = new MatPanel("Blocked 1", tiles[5], ml);
		mats[6] = new MatPanel("Blocked 2", tiles[6], ml);
		mats[7] = new MatPanel("Blocked 3", tiles[7], ml);
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0; c.gridx = 0;
		
		tabs.add(mats[0], "Base");
		tabs.add(mats[1], "Pass 1");
		tabs.add(mats[2], "Pass 2");
		tabs.add(mats[3], "Pass 3");
		tabs.add(mats[4], "Ass");
		tabs.add(mats[5], "Block 1");
		tabs.add(mats[6], "Block 2");
		tabs.add(mats[7], "Block 3");
		
		this.add(save, c);
		c.gridx = 1;
		this.add(load, c);
		c.gridy++; c.gridx = 0;
		
		this.add(pen,  c);
		c.gridx = 1;
		this.add(fill, c);
		c.gridx = 2;
		this.add(penFill, c);
		c.gridy++;
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0; c.gridheight =3;
		this.add(new JLabel(selectedLeftLab), c);
		c.gridx = 1;
		this.add(new JLabel(selectedRightLab), c);
		c.anchor = GridBagConstraints.WEST;
		c.gridheight =1;
		c.gridx = 2;
		this.add(xLab, c);
		c.gridy++;
		this.add(yLab, c);
		c.gridy++;
		this.add(cLab, c);
		
		c.gridx = 0; c.gridy++;
		c.anchor = GridBagConstraints.WEST;
		this.add(zoomOut, c);
		c.gridx = 1;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(zoomLab, c);
		c.gridx = 2; c.gridwidth =1;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		this.add(zoomIn, c);
		c.gridy++; c.gridx =0; c.gridwidth = 3;
		this.add(tabs, c);
	}
	
	public void setTiles(Hashtable tilesIn){
		tiles = new Tile[8][16];
		int x = 0;
		for(int n = 0; n<4; n++){
			for(int i = 0; i<16; i++){
				tiles[n][i] = (Tile)tilesIn.get((byte)(n*16 + i));
			}
			mats[n].setImages(tiles[n]);
			x++;
		}
		x=0;
		for(int n = 4; n<8; n++){
			for(int i = 0; i<16; i++){
				tiles[n][i] = (Tile)tilesIn.get((byte)((n-4)*16 + i + 128));
			}
			mats[n].setImages(tiles[n]);
			x++;
		}
		
	}
	
	public JLabel getTile(int n){
		return mats[tabs.getSelectedIndex()].getTile(n);
	}
	
	public void setMouseOver(int x, int y, boolean xb, boolean yb, Tile c){
		if(xb) xLab.setForeground(Color.red);
		else xLab.setForeground(Color.BLACK);
		if(yb) yLab.setForeground(Color.red);
		else yLab.setForeground(Color.BLACK);
		xLab.setText(" X: "+x);
		yLab.setText(" Y: "+y);
		if(c != null)
			cLab.setText(" C:" + c.code);
		else 
			cLab.setText(" C: null");
	}
	
	public void selectTile(MouseEvent e, String s){
		for(int i=0; i<16; i++){
			if(e.getSource().equals(mats[tabs.getSelectedIndex()].getTile(i))){
				if(s == "RIGHT"){
					selectedRight = tiles[tabs.getSelectedIndex()][i];
					selectedRightLab.setImage(selectedRight.scaledImage);
				} else {
					selectedLeft = tiles[tabs.getSelectedIndex()][i];
					selectedLeftLab.setImage(selectedLeft.scaledImage);
				}
			}
		}
		this.repaint();
	}
	
	public void zoom(String s){
		if(s == "OUT"){
			zoom--;
			if(zoom < 0) zoom = 0;
			zoomLab.setText(zoomText[zoom]);
		} else {
			zoom++;
			if(zoom > 5) zoom = 5;
			zoomLab.setText(zoomText[zoom]);
		}
	}
	
	public float getZoom(){
		if(zoom == 0){
			return (float)0.25;
		} else if (zoom == 1){
			return (float)0.5;
		} else {
			return zoom-1;
		}
	}
	
	public void setPenFill(boolean b){
			if(b)
				penFill.setText(" Fill ");
			else 
				penFill.setText(" Pen ");
	}
}
