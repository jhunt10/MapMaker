import java.awt.image.BufferedImage;

public class Tile 
{
	public byte code;
	public BufferedImage image, scaledImage;
	
	//---------------------------------------
	//	Simple class to hold tile data together
	//---------------------------------------
	public Tile(byte c, BufferedImage i, BufferedImage s)
	{
		code = c; image = i; scaledImage = s;
	}
}
