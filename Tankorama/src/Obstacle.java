import java.awt.Rectangle;
import java.awt.Image;

public abstract class Obstacle {
	
	private double x1;
	private int hi;
	private Rectangle rect;
	private Image im;
	
	public Obstacle(Image imIn, Rectangle rectIn) {
		 im = imIn;
		 rect = rectIn;
	}
	
	
	public Image getImage() {
		return im;
	}
	
	public Rectangle getRect() {
		return rect;
	}
}
