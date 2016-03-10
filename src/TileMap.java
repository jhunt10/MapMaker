import java.awt.image.BufferedImage;

public class TileMap {
	private Tile map[][];
	
	public TileMap(int w, int h){
		map = new Tile[w][h];
		for(int y=0; y<h; y++){
			for(int x=0; x<w; x++){
				map[x][y] = null;
			}
		}
	}
	
	public Tile get(int x, int y){
		if(x > 0 && x < map.length && y > 0 && y < map[0].length)
			return map[x][y];
		else
			return null;
	}
	
	public void set(int x, int y, Tile t){
		map[x][y] = t;
	}
	
	public int getWidth(){
		return map.length;
	}
	
	public int getHeight(){
		return map[0].length;
	}
}
