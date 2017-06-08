import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ViewPanel extends JPanel{
	
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static final int SIZE = 20;
	
	private BufferedImage map;
	boolean fullZoom;
//	Graphics g;
	
	private float xOffset, yOffset, scale;
	
	public ViewPanel(BufferedImage tileSetIn, TileMap tm, Insets inset, MouseListener ml)
	{
		scale = 1;
		xOffset = 0; yOffset = 0;
		map = new BufferedImage(tm.getWidth() * SIZE, tm.getHeight() * SIZE, BufferedImage.TYPE_INT_RGB);
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.addMouseListener(ml);
		this.addMouseMotionListener((MouseMotionListener)ml);
		fullZoom = false;
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		g.setColor(Color.green);
		g.fillRect(0, 0, WIDTH, HEIGHT);	
		int w = (int)Math.floor(WIDTH/scale);
		int h = (int)Math.floor(HEIGHT/scale);
		if(w > map.getWidth() | h > map.getHeight()){
			fullZoom = true;
		} else {
			fullZoom = false;
		}
		if(!fullZoom)
			g.drawImage(map.getSubimage((int)xOffset, (int)yOffset, w, h), 0, 0, WIDTH, HEIGHT, null);
		else
			g.drawImage(map.getSubimage(0, 0, map.getWidth(), map.getHeight()), (int)(-xOffset), (int)(-yOffset), (int)Math.floor(map.getWidth()*scale), (int)Math.floor(map.getHeight()*scale), null);
	}
	
	public void redrawMap(TileMap tileMap)
	{
		Graphics g = map.getGraphics();
		for(int y = 0; y < tileMap.getHeight(); y++){
			for(int x = 0; x < tileMap.getWidth(); x++){
				if(tileMap.get(x, y) != -1){
					g.drawImage(Controler.getTileByCode(tileMap.get(x, y)).image, x*20, y*20, null);
				} else {
					g.setColor(Color.CYAN);
					g.fillRect(x*20, y*20, 20, 20);
				}
			}
		}
		g.dispose();
	}
	
	public void drawTile(int x, int y, byte t)
	{
		Graphics g = map.getGraphics();
		if(t != -1)
		{
			g.drawImage(Controler.getTileByCode(t).image, x*20, y*20, null);
		}
		else
		{
			g.setColor(Color.CYAN);
			g.fillRect(x*20, y*20, 20, 20);
		}
		g.dispose();
	}
	
	public void setZoom(float n)
	{
		scale = n;
		if(xOffset+WIDTH/scale > map.getWidth()) xOffset = map.getWidth()-(int)Math.floor(WIDTH/scale);
		if(yOffset+HEIGHT/scale > map.getHeight()) yOffset = map.getHeight()-(int)Math.floor(HEIGHT/scale);
		if(xOffset < 0) xOffset = 0;
		if(yOffset < 0) yOffset = 0;
	}
	
	public float getXOffset()
	{
		return xOffset;
	}
	
	public float getYOffset()
	{
		return yOffset;
	}
	
	public void setOffset(float x, float y)
	{
		xOffset = x; yOffset = y;
		if(xOffset+WIDTH/scale > map.getWidth()) xOffset = map.getWidth()-(int)Math.floor(WIDTH/scale);
		if(yOffset+HEIGHT/scale > map.getHeight()) yOffset = map.getHeight()-(int)Math.floor(HEIGHT/scale);
		if(xOffset < 0) xOffset = 0;
		if(yOffset < 0) yOffset = 0;
	}
	
	public BufferedImage getMapImage()
	{
		return map;
	}

	public boolean getFullScreened()
	{
		int w = (int)Math.floor(WIDTH/scale);
		int h = (int)Math.floor(HEIGHT/scale);
		if(w > map.getWidth() | h > map.getHeight())
		{
			return true;
		}
		return false;
	}
	
	public int getMapWidth()
	{
		return map.getWidth();
	}
	
	public int getMapHeight()
	{
		return map.getHeight();
	}
	
	public void setMapImage(BufferedImage mapIn)
	{
		map = mapIn;
	}
	
	public void resetTileMap(TileMap tm)
	{
		System.out.println("TM SIZE IN VIEW PAN RSET: W: "+(tm.getWidth()* SIZE)+" H: "+(tm.getHeight()* SIZE) + " TOT: "+((tm.getHeight()* SIZE) * (tm.getWidth()* SIZE)) +" MAX: "+Integer.MAX_VALUE);
		map = new BufferedImage(tm.getWidth() * SIZE, tm.getHeight() * SIZE, BufferedImage.TYPE_INT_RGB);
		this.redrawMap(tm);
	}
}
