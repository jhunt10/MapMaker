import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.EventListener;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class Controler implements MouseListener, MouseMotionListener, ActionListener, Runnable{
	
	private static final int SIZE = 20;
	private JFrame viewFrame, toolFrame;
	private ToolWindow tools;
	private ViewPanel view;
	private BufferedImage tileSet;
	private TileMap tileMap;
	private Hashtable<Byte, Tile> tiles;
	
	int mouseOverX, mouseOverY, mouseStartX, mouseStartY;
	int startResult, mapWidth, mapHeight;
	boolean leftPressed, scrollPressed, rightPressed, fill;
	
	private Thread thread;
	
	public Controler(){
		JPanel start = new JPanel();
		start.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		JTextField width = new JTextField("80", 8);
		JTextField height = new JTextField("40", 8);
		JTextField path = new JTextField("defaultSet", 12);
		JLabel widthLab = new JLabel(" W: ");
		JLabel heightLab = new JLabel(" H: ");
		JLabel tileSetLab = new JLabel(" Tile Set to use: ");
		c.gridx = 0; c.gridy = 0; c.fill = GridBagConstraints.BOTH;
		start.add(widthLab, c);
		c.gridx = 1;
		start.add(width, c);
		c.gridx = 0; c.gridy++;
		start.add(heightLab, c);
		c.gridx = 1;
		start.add(height, c);
		c.gridwidth = 2;
		c.gridx = 0; c.gridy++;
		start.add(tileSetLab, c);
		c.gridx = 2;
		start.add(path, c);
		startResult = JOptionPane.showConfirmDialog(null, start, "Width and Height In tiles", JOptionPane.OK_CANCEL_OPTION);
		if(startResult == JOptionPane.OK_OPTION){
			tileMap = new TileMap(Integer.parseInt(width.getText()),
								Integer.parseInt(height.getText()));
			try {
				tileSet = ImageIO.read(new File("input\\" + path.getText() + ".png"));
		    } 
			catch (IOException ioe) {
			        System.out.println("Unable to load image file.");
			}
		} else {
			System.exit(0);
		}
		
		this.initTiles();
		
		
		
		
		
		
		toolFrame = new JFrame("Tools");
		tools = new ToolWindow(tiles, (MouseListener)this, (ActionListener)this);
		toolFrame.setContentPane(tools);
		toolFrame.setResizable(false);
		toolFrame.setLocation(ViewPanel.WIDTH+150, 150);
		toolFrame.pack();
		toolFrame.setVisible(true);
		
		
		
		
		viewFrame = new JFrame("Map View");
		viewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view = new ViewPanel(tileSet, tileMap, viewFrame.getInsets(),  this);
		viewFrame.setContentPane(view);
		viewFrame.setLocation(100, 100);
		viewFrame.setResizable(false);
		viewFrame.pack();
		viewFrame.setVisible(true);
		
		view.redrawMap(tileMap);
		leftPressed = false;
		scrollPressed = false;
		rightPressed = false;
		fill = false;
		
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "ZOOMOUT"){
			tools.zoom("OUT");
			view.setZoom(tools.getZoom());
		} else if(e.getActionCommand() == "ZOOMIN"){
			tools.zoom("IN");
			view.setZoom(tools.getZoom());
		} else if(e.getActionCommand() == "SAVE"){
			String name = (String)JOptionPane.showInputDialog("Save As: ");
			this.saveImage(name);
			this.saveText(name);
		} else if(e.getActionCommand() == "LOAD"){
			this.loadMap();
		} else if(e.getActionCommand() == "PEN"){
			fill = false;
			tools.setPenFill(fill);
			System.out.println("HIT IN PEN");
		} else if(e.getActionCommand() == "FILL"){
			fill = true;
			tools.setPenFill(fill);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getComponent().equals(view)){
			if(e.getButton() == 1){
				leftPressed = true;
				if(!fill){
					this.setTile(mouseOverX, mouseOverY, tools.selectedLeft);
				} else {
					fill(mouseOverX, mouseOverY, tools.selectedLeft);
					fill = false;
					tools.setPenFill(fill);
				}
			} else if(e.getButton() == 2){
				mouseStartX = e.getX();
				mouseStartY = e.getY();
				scrollPressed = true;
			} else if(e.getButton() == 3){
				rightPressed = true;
				if(!fill){
					this.setTile(mouseOverX, mouseOverY, tools.selectedRight);
				} else {
					fill(mouseOverX, mouseOverY, tools.selectedRight);
					fill = false;
					tools.setPenFill(fill);
				}
			}
		} else {
			if(e.getButton() == 1)	tools.selectTile(e, "LEFT");
			if(e.getButton() == 3)	tools.selectTile(e, "RIGHT");
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == 1){
			leftPressed = false;
		} else if(e.getButton() == 2){
			scrollPressed = false;
		} else if(e.getButton() == 3){
			rightPressed = false;
		}
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseOverX = (e.getX()+(int)(view.xOffset*tools.getZoom())) / (int)(20*tools.getZoom());
		mouseOverY = (e.getY()+(int)(view.yOffset*tools.getZoom())) / (int)(20*tools.getZoom());
		tools.setMouseOver(mouseOverX, mouseOverY, (mouseOverX < 16 || mouseOverX > tileMap.getWidth()-16),
				(mouseOverY < 9 || mouseOverY > tileMap.getHeight()-9), tileMap.get(mouseOverX, mouseOverY));
		if(scrollPressed){
			view.setOffset((int)Math.floor(view.xOffset + (mouseStartX - e.getX())/(view.scale*1)), 
							(int)Math.floor(view.yOffset + (mouseStartY - e.getY())/(view.scale*1)));
			mouseStartX = e.getX();
			mouseStartY = e.getY();
		} else if (leftPressed){
			if(!fill)
				this.setTile(mouseOverX, mouseOverY, tools.selectedLeft);
		} else if(rightPressed){
			if(!fill)
				this.setTile(mouseOverX, mouseOverY, tools.selectedRight);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseOverX = (e.getX()+(int)(view.xOffset*tools.getZoom())) / (int)(20*tools.getZoom());
		mouseOverY = (e.getY()+(int)(view.yOffset*tools.getZoom())) / (int)(20*tools.getZoom());
		tools.setMouseOver(mouseOverX, mouseOverY, (mouseOverX < 16 || mouseOverX > tileMap.getWidth()-16-1),
				(mouseOverY < 9 || mouseOverY > tileMap.getHeight()-9-1), tileMap.get(mouseOverX, mouseOverY));
	}

	@Override
	public void run() {
		while(true){
			view.repaint();
		}
	}
	
	public boolean loadMap(){
		JPanel load = new JPanel();
		JTextField path = new JTextField("mtStart", 12);
		JLabel pathLab = new JLabel(" Map Name: ");
		load.add(pathLab);
		load.add(path);
		startResult = JOptionPane.showConfirmDialog(null, load, "Width and Height In tiles", JOptionPane.OK_CANCEL_OPTION);
		if(startResult == JOptionPane.OK_OPTION){
			this.loadImage(path.getText());
			this.loadText(path.getText());
			this.loadTileSet(path.getText());
			tools.setTiles(tiles);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean saveImage(String name){
		try{
			File out = new File("output\\"+name+".png");
			ImageIO.write(view.getMapImage(), "png", out);
			return true;
		} catch (IOException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean loadImage(String name){
		try {
			view.setMapImage(ImageIO.read(new File("output\\" +name + ".png")));
			return true;
	    } 
		catch (IOException ioe) {
	        System.out.println("Unable to load image file.");
	        return false;
		}
	}
	
	public boolean loadTileSet(String name){
		try {
			tileSet = ImageIO.read(new File("input\\" + name + ".png"));
			this.initTiles();
			return true;
	    } 
		catch (IOException ioe) {
	        System.out.println("Unable to load image file.");
	        return false;
		}
	}
	
	public void saveText(String name){
		String templn = "";
		try{
			PrintWriter out = new PrintWriter("output\\"+name+".txt");
			out.println(tileMap.getWidth() + " " + tileMap.getHeight());
			for(int y=0; y<tileMap.getHeight(); y++){
				templn = "";
				for(int x=0; x<tileMap.getWidth(); x++){
					if(tileMap.get(x, y) != null)
						templn += tileMap.get(x, y).code + " ";
					else
						templn += "null ";
				}
				out.println(templn);
			}
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public boolean loadText(String name){
		BufferedReader read = null;
		String line;
		String [] tokens;
		try {
			read = new BufferedReader(new FileReader("output\\"+name+".txt"));
			line = read.readLine();
			tokens = line.split(" ");
			tileMap = new TileMap(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
			int y = 0;
			while( (line = read.readLine())!= null ){
			    tokens = line.split(" ");
			    for(int x = 0; x<tokens.length; x++){
			    	if(tokens[x].equals("null")){
			    		tileMap.set(x, y, null);
			    	} else {
			    		byte b = Byte.parseByte(tokens[x]);
			    		tileMap.set(x, y, tiles.get(b));
			    	}
			    }
			    y++;
			}
			read.close();
			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} 
		
	}
	
	public void initTiles(){
		Tile temp;
		int x = 0;
		tiles = new Hashtable<Byte, Tile>();
		BufferedImage tileScaled = new BufferedImage(800,800, BufferedImage.TYPE_INT_RGB);
		Graphics g = tileScaled.getGraphics();
		g.drawImage(tileSet, 0, 0, tileSet.getWidth()*2, tileSet.getHeight()*2, null);
		for(int n = 0; n<4; n++){
			for(int i = 0; i<16; i++){
				temp = new Tile((byte)(n*16 + i), 
								tileSet.getSubimage((x*80)+(i%4)*SIZE, (i/4)*SIZE, SIZE, SIZE), 
								tileScaled.getSubimage((x*160)+(i%4)*SIZE*2, (i/4)*SIZE*2, SIZE*2, SIZE*2));
				tiles.put(temp.code, temp);
			}
			x++;
		}
		x=0;
		for(int n = 4; n<8; n++){
			for(int i = 0; i<16; i++){
				temp = new Tile((byte)((n-4)*16 + i + 128), 
								tileSet.getSubimage((x*80)+(i%4)*SIZE, (80)+(i/4)*SIZE, SIZE, SIZE), 
								tileScaled.getSubimage((x*160)+(i%4)*SIZE*2, (160)+(i/4)*SIZE*2, SIZE*2, SIZE*2));
				tiles.put(temp.code, temp);
			}
			x++;
		}
	}
	
	public void setTile(int x, int y, Tile t){
		tileMap.set(x, y, t);
		view.drawTile(x, y, t);
	}
	
	public void fill(int x, int y, Tile newT){
		Tile old = tileMap.get(x, y);
		if(old == newT){
			return;
		} else {
			this.setTile(x, y, newT);
			if(x<tileMap.getWidth()-1 && tileMap.get(x+1, y) == old) fill(x+1, y, newT);
			if(y<tileMap.getHeight()-1 && tileMap.get(x, y+1) == old) fill(x, y+1, newT);
			if(x > 0 && tileMap.get(x-1, y) == old) fill(x-1, y, newT);
			if(y > 0 && tileMap.get(x, y-1) == old) fill(x, y-1, newT);
		}
	}
	
	

}
