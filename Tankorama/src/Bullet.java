import java.awt.Image;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;

import java.util.TimerTask;

public class Bullet {
	//UNIVERSAL STUFF
	private final static int WIDTH = 1280;
	private final static int HEIGHT = 720;
	private final static double BIT = .001;
	
	private static final double SPEED = 8.5;//8.5
	private static Image im;
	private static int bulletDecay = 10000;
	
	private static final int RADIUS = 10;//not sure yet
	private boolean keep;
	
	private double xPos;
	private double yPos;
	private double angle;
	
	public Bullet(int x, int y, double angleIn) {
		xPos = x;
		yPos = y;
		angle = angleIn;
		im = new ImageIcon("bullet.png").getImage();
		if(Tank.crashes(xPos,yPos,RADIUS)) {
			keep = false;
		}
		else {
		keep = true;
		//obstacles = Tank.getObs(); //see above comment
		
		TimerTask timerTask = new DecayTask();
        Timer timer = new Timer(true);
        timer.schedule(timerTask, bulletDecay);
		}
	}

	public class DecayTask extends TimerTask {
	    public void run() {
	    	keep = false;
	    }
	}
	
	public boolean keep() {
		return keep;
	}
	
	
	public Image getImage() {
		return im;
	}
	
	public double getX() {
		return xPos;
	}
	
	public double getY() {
		return yPos;
	}
	
	
	public void move() {
		double tempX = xPos + SPEED* Math.cos(angle);
		double tempY = yPos + SPEED* Math.sin(angle);

		if(!(tempX - RADIUS > 0 && tempX + RADIUS < WIDTH)){ //if it would go out of bounds
			angle = bounceX();
			tempX = xPos + SPEED* Math.cos(angle);
		}
		if(!(tempY - RADIUS > 0 && tempY + RADIUS < HEIGHT)){
			angle = bounceY();
			tempY = yPos + SPEED* Math.sin(angle);
		}
		if(Tank.crashes(tempX,yPos,RADIUS)) { //if it would crash into any obstacle
			angle = bounceX();
			tempX = xPos + SPEED* Math.cos(angle);
		}
		if(Tank.crashes(xPos,tempY,RADIUS)) {
			angle = bounceY();
			tempY = yPos + SPEED* Math.sin(angle);
		}
		xPos = tempX;
		yPos = tempY;
	}
	
	private double bounceX() {
		return Math.PI-angle;
	}
	
	private double bounceY() {
		return -angle;
	}
	
	public static int getRadius(){
		return (int)RADIUS;
	}
}
