import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ViewPanel extends JPanel{
	
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static final int SIZE = 20;
	
	private JPanel mapPanel;
	private BufferedImage tileSet, map, nullImage;
	boolean fullZoom;
	Graphics g;
	
	public int xOffset, yOffset;
	public float scale;
	
	public ViewPanel(BufferedImage tileSetIn, TileMap tm, Insets inset, MouseListener ml){
		scale = 1;
		xOffset = 0; yOffset = 0;
		tileSet = tileSetIn;
		map = new BufferedImage(tm.getWidth()*SIZE,tm.getHeight()*SIZE,BufferedImage.TYPE_INT_RGB);
		nullImage = new BufferedImage(20,20,BufferedImage.TYPE_INT_RGB);
		nullImage.getGraphics().setColor(Color.CYAN);
		nullImage.getGraphics().fillRect(0, 0, 20, 20);
		g = map.getGraphics();
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.addMouseListener(ml);
		this.addMouseMotionListener((MouseMotionListener)ml);
		fullZoom = false;
	}
	
	public void paintComponent(Graphics g){
		g.setColor(Color.green);
		g.fillRect(0, 0, WIDTH, HEIGHT);	
		int w = (int)Math.floor(WIDTH/scale);
		int h = (int)Math.floor(HEIGHT/scale);
		if(w>map.getWidth() | h>map.getHeight()){
			fullZoom = true;
		} else {
			fullZoom = false;
		}
		if(!fullZoom)
			g.drawImage(map.getSubimage(xOffset, yOffset, w, h), 0, 0, WIDTH, HEIGHT, null);
		else
			g.drawImage(map.getSubimage(0, 0, map.getWidth(), map.getHeight()), -xOffset, -yOffset, (int)Math.floor(map.getWidth()*scale), (int)Math.floor(map.getHeight()*scale), null);
//		g.setColor(Color.red);
//		g.fillRect(WIDTH/2-(20*scale)/2, HEIGHT/2-(20*scale)/2, 20*scale, 20*scale);
	}
	
	public void redrawMap(TileMap tileMap){
		for(int y = 0; y < tileMap.getHeight(); y++){
			for(int x = 0; x < tileMap.getWidth(); x++){
				if(tileMap.get(x, y) != null){
					g.drawImage(tileMap.get(x, y).image, x*20, y*20, null);
				} else {
					g.setColor(Color.CYAN);
					g.fillRect(x*20, y*20, 20, 20);
				}
			}
		}
	}
	
//	public void setTile(Tile t, int x, int y){
//		tileMap.set(x, y, t);
//		if(tileMap.get(x, y) != null){
//			g.drawImage(tileMap.get(x, y).image, x*20, y*20, null);
//		} else {
//			g.setColor(Color.CYAN);
//			g.fillRect(x*20, y*20, 20, 20);
//		}
//	}
	
	public void drawTile(int x, int y, Tile t){
		if(t != null){
			g.drawImage(t.image, x*20, y*20, null);
		} else {
			g.setColor(Color.CYAN);
			g.fillRect(x*20, y*20, 20, 20);
		}
	}
	
//	public Tile getTile(int x, int y){
//		if(x > 0 & x < tileMap.getWidth()-1 & y > 0 & y < tileMap.getHeight()-1)
//			return tileMap.get(x, y);
//		else 
//			return null;
//	}
	
	public void setZoom(float n){
		scale = n;
		if(xOffset+WIDTH/scale > map.getWidth()) xOffset = map.getWidth()-(int)Math.floor(WIDTH/scale);
		if(yOffset+HEIGHT/scale > map.getHeight()) yOffset = map.getHeight()-(int)Math.floor(HEIGHT/scale);
		if(xOffset < 0) xOffset = 0;
		if(yOffset < 0) yOffset = 0;
	}
	
	public int getXOffset(){
		return xOffset;
	}
	
	public int getYOffset(){
		return yOffset;
	}
	
	public void setOffset(int x,int y){
		xOffset = x; yOffset = y;
		if(xOffset+WIDTH/scale > map.getWidth()) xOffset = map.getWidth()-(int)Math.floor(WIDTH/scale);
		if(yOffset+HEIGHT/scale > map.getHeight()) yOffset = map.getHeight()-(int)Math.floor(HEIGHT/scale);
		if(xOffset < 0) xOffset = 0;
		if(yOffset < 0) yOffset = 0;
	}
	
	public BufferedImage getMapImage(){
		return map;
	}
	
//	public TileMap getTileMap(){
//		return tileMap;
//	}
	public boolean getFullScreened(){
		int w = (int)Math.floor(WIDTH/scale);
		int h = (int)Math.floor(HEIGHT/scale);
		if(w>map.getWidth() | h>map.getHeight()){
			return true;
		}
		return false;
	}
	public int getMapWidth(){
		return map.getWidth();
	}
	public int getMapHeight(){
		return map.getHeight();
	}
	public void setMapImage(BufferedImage mapIn){
		map = mapIn;
		g = map.getGraphics();
	}
}
