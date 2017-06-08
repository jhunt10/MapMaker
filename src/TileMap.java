public class TileMap 
{
	private byte map[][];
	
	//---------------------------------------
	//	Basically just a fancy two dim array
	//---------------------------------------
	
	public TileMap(int w, int h)
	{
		map = new byte[w][h];
		for(int y = 0; y < h; y++)
		for(int x = 0; x < w; x++)
		{
			map[x][y] = -1;
		}
	}
	
	public byte get(int x, int y)
	{
		if(x >= 0 && x < map.length && y >= 0 && y < map[0].length)
			return map[x][y];
		else
			return -1;
	}
	
	public void set(int x, int y, byte t)
	{
		if(x >= 0 && x < map.length && y >= 0 && y < map[0].length)
			map[x][y] = t;
	}
	
	public int getWidth()
	{
		return map.length;
	}
	
	public int getHeight()
	{
		return map[0].length;
	}
}
