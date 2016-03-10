import java.awt.image.BufferedImage;

public class Tile {
	public byte code;
	public BufferedImage image, scaledImage;
	public Tile(byte c, BufferedImage i, BufferedImage s){
		code = c; image = i; scaledImage = s;
	}
}
