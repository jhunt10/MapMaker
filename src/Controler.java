
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
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Controler implements MouseListener, MouseMotionListener, ActionListener, Runnable{
	
	public static final int LEFTMOUSE = 1;
	public static final int MIDDLEMOUSE = 2;
	public static final int RIGHTMOUSE = 3;
	public static final int SIZE = 20;
	
	private JFrame viewFrame, toolFrame;
	private ToolWindow tools;
	private ViewPanel view;
	private BufferedImage tileSet;
	private TileMap tileMap;
	private static Tile[] tiles;
	
	private int mouseOverX, mouseOverY, mouseStartX, mouseStartY;
	private boolean fill;
	private int curtButton;
	private String tileSetName, mapName;
	
	private Thread thread;
	
	public Controler(){
		tileMap = new TileMap(80,80);
		this.loadTileSet("default");
		this.initTiles();
		
		toolFrame = new JFrame("Tools");
		toolFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		
		curtButton = 0;
		fill = false;
		
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getActionCommand() == "ZOOMOUT")
		{
			tools.zoom("OUT");
			view.setZoom(tools.getZoom());
		}
		else if(e.getActionCommand() == "ZOOMIN")
		{
			tools.zoom("IN");
			view.setZoom(tools.getZoom());
		}
		else if(e.getActionCommand() == "NEW")
		{
			this.createNew();
		}
		else if(e.getActionCommand() == "SAVE")
		{
			String name = (String)JOptionPane.showInputDialog(null, "Save As: ", this.mapName);
			this.saveImage(name);
			this.saveText(name);
		}
		else if(e.getActionCommand() == "LOAD")
		{
			this.loadMap();
		}
		else if(e.getActionCommand() == "PEN")
		{
			fill = false;
			tools.setPenFill(false);
			System.out.println("HIT IN PEN");
		}
		else if(e.getActionCommand() == "FILL")
		{
			fill = true;
			tools.setPenFill(true);
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
	public void mousePressed(MouseEvent e)
	{
		curtButton = e.getButton();
		if(e.getComponent().equals(view))
		{
			if(curtButton == LEFTMOUSE || curtButton == RIGHTMOUSE)
			{
				if(!fill)
				{
					this.setTile(mouseOverX, mouseOverY, tools.getSelected(curtButton));
				}
				else
				{
					fill(mouseOverX, mouseOverY, tools.getSelected(curtButton));
					fill = false;
					tools.setPenFill(fill);
				}
			}
			else if(curtButton == MIDDLEMOUSE)
			{
				mouseStartX = e.getX();
				mouseStartY = e.getY();
			}
		}
		else
		{
			if(e.getButton() == 1)	tools.selectTile(e, "LEFT");
			if(e.getButton() == 3)	tools.selectTile(e, "RIGHT");
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		curtButton = 0;
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
		this.setMouseOver(e);
		if(curtButton == MIDDLEMOUSE)
		{
			view.setOffset(
					view.getXOffset() + ((mouseStartX - e.getX()) / tools.getZoom()), 
					view.getYOffset() + ((mouseStartY - e.getY()) / tools.getZoom()));		
			mouseStartX = e.getX();
			mouseStartY = e.getY();
		}
		else if(!fill && (curtButton == LEFTMOUSE || curtButton == RIGHTMOUSE))
		{
				this.setTile(mouseOverX, mouseOverY, tools.getSelected(curtButton));
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) 
	{
		this.setMouseOver(e);
	}
	
	public void setMouseOver(MouseEvent e)
	{
		mouseOverX = (e.getX() + (int)(view.getXOffset() * tools.getZoom())) / (int)(20*tools.getZoom());
		mouseOverY = (e.getY() + (int)(view.getYOffset() * tools.getZoom())) / (int)(20*tools.getZoom());
		tools.setMouseOver(mouseOverX, mouseOverY, (mouseOverX < 16 || mouseOverX > tileMap.getWidth()-16),
				(mouseOverY < 9 || mouseOverY > tileMap.getHeight()-9), tileMap.get(mouseOverX, mouseOverY));
	}

	@Override
	public void run() 
	{
		while(true)
		{
			view.repaint();
		}
	}
	
	public boolean createNew()
	{
		JPanel start = new JPanel();
		start.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		JTextField name = new JTextField("", 8);
		JTextField width = new JTextField("80", 8);
		JTextField height = new JTextField("40", 8);
		JTextField path = new JTextField("default", 12);
		JLabel nameLab = new JLabel(" Name: ");
		JLabel widthLab = new JLabel(" W: ");
		JLabel heightLab = new JLabel(" H: ");
		JLabel tileSetLab = new JLabel(" Tile Set: ");
		JLabel tileSetOptions = new JLabel("Built In: defult, city, snow");
		c.gridx = 0; c.gridy = 0; c.fill = GridBagConstraints.BOTH;
		start.add(nameLab, c);
		c.gridx++;
		start.add(name, c);
		c.gridx = 0; c.gridy++;
		start.add(widthLab, c);
		c.gridx++;
		start.add(width, c);
		c.gridx = 0; c.gridy++;
		start.add(heightLab, c);
		c.gridx++;
		start.add(height, c);
		c.gridx = 0; c.gridy++;
		start.add(tileSetLab, c);
		c.gridx += 1;
		start.add(path, c);
		c.gridy++;
		start.add(tileSetOptions, c);
		int startResult = JOptionPane.showConfirmDialog(null, start, "Width and Height In tiles", JOptionPane.OK_CANCEL_OPTION);
		if(startResult == JOptionPane.OK_OPTION)
		{	
			long widthLong, heightLong;
			this.mapName = name.getText();
			widthLong = Long.parseLong(width.getText());
			heightLong = Long.parseLong(height.getText());
			if(widthLong * SIZE * heightLong * SIZE > Integer.MAX_VALUE ||
					widthLong * SIZE * heightLong * SIZE < 0)
			{
				JOptionPane.showMessageDialog(null, "To Big: Width * Height must be < "+(Integer.MAX_VALUE / (SIZE * SIZE)));
				return false;
			}
			try 
			{
				tileSet = ImageIO.read(new File("input\\" + path.getText() + ".png"));
				this.initTiles();
				tools.resetTiles();
				tileMap = new TileMap(Integer.parseInt(width.getText()), Integer.parseInt(height.getText()));
				view.resetTileMap(tileMap);
				return true;
		    } 
			catch (IOException ioe) 
			{
				JOptionPane.showMessageDialog(null, "Tile set: "+path.getText()+" Not Found");
			}
		}
		return false;
	}
	
	public boolean loadMap()
	{
		JPanel load = new JPanel();
		JTextField path = new JTextField("example", 12);
		JLabel pathLab = new JLabel(" Map Name: ");
		load.add(pathLab);
		load.add(path);
		int startResult = JOptionPane.showConfirmDialog(null, load, "Width and Height In tiles", JOptionPane.OK_CANCEL_OPTION);
		if(startResult == JOptionPane.OK_OPTION)
		{
			if(this.loadImage(path.getText()))
			{
				this.mapName = path.getText();
				this.loadText(path.getText());
				tools.resetTiles();
				view.redrawMap(tileMap);
				return true;
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Map: "+path.getText()+" Not Found");
				return false;
			}
		}
		else 
		{
			return false;
		}
	}
	
	public boolean saveImage(String name)
	{
		try
		{
			File out = new File("output\\"+name+".png");
			ImageIO.write(view.getMapImage(), "png", out);
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean loadImage(String name)
	{
		try
		{
			view.setMapImage(ImageIO.read(new File("output\\" +name + ".png")));
			return true;
	    } 
		catch (IOException ioe)
		{
			JOptionPane.showMessageDialog(null, "Map Image: "+name+".png Not Found");
	        return false;
		}
	}
	
	public boolean loadTileSet(String name)
	{
		try
		{
			tileSet = ImageIO.read(new File("input\\" + name + ".png"));
			this.tileSetName = name;
			this.initTiles();
			return true;
	    } 
		catch (IOException ioe)
		{
			JOptionPane.showMessageDialog(null, "Tile Set: "+name+" Not Found");
	        return false;
		}
	}
	
	public void saveText(String name)
	{
		this.mapName = name;
		String templn = "";
		try{
			PrintWriter out = new PrintWriter("output\\"+name+".txt");
			out.println(tileMap.getWidth() + " " + tileMap.getHeight() + " " + this.tileSetName);
			for(int y=0; y<tileMap.getHeight(); y++){
				templn = "";
				for(int x=0; x<tileMap.getWidth(); x++){
					if(tileMap.get(x, y) != -1)
						templn += tileMap.get(x, y) + " ";
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
	
	public boolean loadText(String name)
	{
		BufferedReader read = null;
		String line;
		String [] tokens;
		try
		{
			read = new BufferedReader(new FileReader("output\\"+name+".txt"));
			line = read.readLine();
			tokens = line.split(" ");
			tileMap = new TileMap(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
			this.loadTileSet(tokens[2]);
			int y = 0;
			while((line = read.readLine())!= null )
			{
			    tokens = line.split(" ");
			    for(int x = 0; x<tokens.length; x++){
			    	if(tokens[x].equals("null")){
			    		tileMap.set(x, y, (byte)-1);
			    	} else {
			    		byte b = Byte.parseByte(tokens[x]);
			    		tileMap.set(x, y, b);
			    	}
			    }
			    y++;
			}
			read.close();
			return true;

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Map Text File: "+name+".txt Not Found");
			return false;
		} 	
	}
	
	public void initTiles()
	{
		tiles = new Tile[128];
		BufferedImage tileScaled = new BufferedImage(800,800, BufferedImage.TYPE_INT_RGB);
		Graphics g = tileScaled.getGraphics();
		g.drawImage(tileSet, 0, 0, tileSet.getWidth()*2, tileSet.getHeight()*2, null);

		int topX, topY, curtX, curtY, code;
		for(int group = 0; group < 8; group++)
		{
			topX = (group % 4) * SIZE * 4;
			if(group < 4)
				topY = 0;
			else
				topY = SIZE * 4;
			for(int index = 0; index < 16; index++)
			{
				code = group * 16 + index;
				curtX = (index % 4) * SIZE + topX;
				curtY = (index / 4) * SIZE + topY;
				tiles[code] = new Tile((byte)(code), 
								tileSet.getSubimage(curtX, curtY, SIZE, SIZE), 
								tileScaled.getSubimage(curtX * 2, curtY * 2, SIZE * 2, SIZE * 2));
			}
		}
	}
	
	public void setTile(int x, int y, byte t)
	{
		tileMap.set(x, y, t);
		view.drawTile(x, y, t);
	}
	
	public void fill(int startX, int startY, byte code)
	{
		byte oldCode = tileMap.get(startX, startY);
		ArrayList<int[]> que = new ArrayList<int[]>();
		que.add(new int[] {startX, startY});
		boolean[][] beenAdded = new boolean[tileMap.getWidth()][tileMap.getHeight()];
		int n=0;
		while(que.size() > 0)
		{
			int[] temp = que.get(0);
			que.remove(0);
			this.setTile(temp[0], temp[1], code);
			n++;
			System.out.println("QUESIZE: "+que.size()+" BEENDONE: "+n);
			// X + 1
			if(temp[0] < tileMap.getWidth()-1 && tileMap.get(temp[0]+1, temp[1]) == oldCode && !beenAdded[temp[0] + 1][temp[1]])
			{
				que.add(new int[] {temp[0] + 1,  temp[1]});
				beenAdded[temp[0] + 1][temp[1]] = true;
			}
			// X - 1
			if(temp[0] > 0 && tileMap.get(temp[0]-1, temp[1]) == oldCode && !beenAdded[temp[0] - 1][temp[1]])
			{
				que.add(new int[] {temp[0] - 1,  temp[1]});
				beenAdded[temp[0] - 1][temp[1]] = true;
			}
			// Y + 1
			if(temp[1] < tileMap.getHeight()-1 && tileMap.get(temp[0], temp[1]+1) == oldCode && !beenAdded[temp[0]][temp[1] + 1])
			{
				que.add(new int[] {temp[0],  temp[1] + 1});
				beenAdded[temp[0]][temp[1] + 1] = true;
			}
			// Y - 1
			if(temp[1] > 0 && tileMap.get(temp[0], temp[1]-1) == oldCode && !beenAdded[temp[0]][temp[1] - 1])
			{
				que.add(new int[] {temp[0],  temp[1] - 1});
				beenAdded[temp[0]][temp[1] - 1] = true;
			}
		}
	}
	
	public static Tile getTileByCode(int i)
	{
		return tiles[i];
	}
}
