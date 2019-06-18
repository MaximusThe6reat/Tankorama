import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Mud extends Obstacle{
	
	
	public Mud(Rectangle rectIn) {
		super(new ImageIcon("mud.png").getImage(), rectIn);
	}
}
