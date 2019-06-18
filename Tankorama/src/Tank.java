import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;



public class Tank {
	//UNIVERSAL STUFF
	private final static int WIDTH = 1280;
	private final static int HEIGHT = 720;
	private final static double BIT = .001;
	
	//TANK STUFF
	private Image im;
	private Image im2;
	private double angle;
	
	private boolean canFire;
	private boolean alive;
	
	//MOVEMENT
	private double xPos;
	private double yPos;
	
	private int up;
	private int down;
	private int left;
	private int right;
	private int fire;
	
	private double xAdj; //this makes stuff spawn per side	
	
	private boolean forward;
	private boolean backward;
	private boolean turnL;
	private boolean turnR;
	private boolean mud;
	
	private static final double SPEED = 6.5;
	private static final double  BACK_SPEED = -.65;
	private static final double  TURN_SPEED = Math.PI/18;//.20
	private static final double MUD_SPEED  = .5;
	
	private static final double RADIUS = 34;//determines hit boxes 34
	private static final double BARREL = 45;//determines where bullet spawns
	
	
	//BULLET STUFF
	
	private List<Bullet> bullets = new ArrayList<Bullet>();
	private static int MAX_BULLETS = 5;
	private static int bulletDelay = 200; 
	
	//OBSTACLE STUFF
	private static List<Obstacle> obstacles = new ArrayList<Obstacle>();
	
	private static final double MUD_HEIGHT = 190; //
	private static final double MUD_WIDTH = 459; //
	
	private static final double WALL_HEIGHT = 73; //
	private static final double WALL_WIDTH = 318; //
	
	public Tank(Image picIn, Image pic2In, int i) {
	im = picIn;
	im2 = pic2In;
	forward = false;
	backward = false;
	turnL = false;
	turnR = false;
	angle = 0;
	canFire = true;
	mud = false;
	alive = false;
	if(i ==1) {
		up = KeyEvent.VK_E;
		down = KeyEvent.VK_D;
		left = KeyEvent.VK_S;
		right = KeyEvent.VK_F;
		fire = KeyEvent.VK_Q;
		xAdj = 0;
	}
	else if(i ==2){
		up = KeyEvent.VK_UP;
		down = KeyEvent.VK_DOWN;
		left = KeyEvent.VK_LEFT;
		right = KeyEvent.VK_RIGHT;
		fire = KeyEvent.VK_M;
		xAdj = WIDTH/2;
	}
	}
	
	//MUTATOR METHODS HERE--------------------------------------------
	
	public void go(String key) {
		if(key == "up") {
			forward = true;
			backward = false; //possibly not necesary
		}
		else if(key == "down") {
			forward = false;
			backward = true; //possibly not necessary
		}
		if(key == "left") {
			turnL = true;
			turnR = false; //""
		}	
		else if(key == "right") {
			turnL = false;
			turnR = true; //""
		}	
	}
	
	public void stop(String key) {
		if(key == "up") {
			forward = false;
		}
		else if(key == "down") {
			backward = false;
		}
		if(key == "left") {
			turnL = false;
		}	
		else if(key == "right") {
			turnR = false; 
		}	
	}
	
	public class MyTimerTask extends TimerTask {
	    public void run() {
	    	canFire = true;
	    }
	}
		
	
	public void fire() {
		if(alive && canFire && bullets.size() < MAX_BULLETS) {
		int tempX = (int)(xPos + BARREL* Math.cos(angle));
		int tempY = (int)(yPos + BARREL* Math.sin(angle)); 
		bullets.add(new Bullet(tempX, tempY, angle));
		canFire = false;
		
		
		
		TimerTask timerTask = new MyTimerTask();
        Timer timer = new Timer(true);
        timer.schedule(timerTask, bulletDelay);  
		}
	}
	
	public void move() {
		if(!alive) {
			return;
		}
		double dir = 1;
		if(backward) 
			dir *= BACK_SPEED;
		if(mud)
			dir *= MUD_SPEED;
		if(forward || backward) {
			double tempX = xPos + dir* SPEED* Math.cos(angle);
			double tempY = yPos + dir* SPEED* Math.sin(angle);

			if(!(tempX - RADIUS > 0 && tempX + RADIUS < WIDTH)){ //if it would go out of bounds
				tempX = xPos; //then it can't go there
			}
			if(!(tempY - RADIUS > 0 && tempY + RADIUS < HEIGHT)){
				tempY = yPos;
			}
			if(crashes(tempX,yPos,RADIUS)) { //if it would crash into a wall with x mvmnt
				tempX = xPos;
		}
			if(crashes(xPos,tempY,RADIUS)) { //if it would crash into a wall with y mvmnt
				tempY = yPos;
			}
//			if(crashes(tempX,tempY){ //if it would crash into a wall with both movements
//				tempX = xPos;
//				tempY = yPos;
//			}
			xPos = tempX;
			yPos = tempY;
		}
		if(turnL) {
			angle -= TURN_SPEED;
			if(angle <=BIT) {
				angle += Math.PI*2;
			}
		}
		else if(turnR) {
			angle +=TURN_SPEED;
			angle = angle % (2*Math.PI);
		}
//		if(xAdj ==0)
//		System.out.println("Tank angle is " + angle);
	}
	
	public static boolean crashes(double X, double Y, double radius) {
		for(Obstacle o: obstacles) {
			if(o instanceof Wall) {
				if(collides(X,Y,o.getRect(),radius)) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	public void die() {
		alive = false;
	}
	
	public void respawn() {
		alive = true;
		xPos = xAdj + RADIUS + (Math.random()*(WIDTH/2 - 2*RADIUS));
		yPos = RADIUS + (Math.random()*(HEIGHT - 2*RADIUS));
		obAdder();
	}
	
	public void setMud() {
		mud = true;
	}
	
	public void noMud() {
		mud = false;
	}
	
	
	
	//ACCESSOR METHODS HERE-------------------------------------------------
	
	public int getKey(String key) {
		if(key == "up")
			return up;
		else if(key == "down")
			return down;
		else if(key == "left")
			return left;
		else if(key == "right")
			return right;
		else if(key == "fire")
			return fire;
		else 
			return -1;
	}
	
	public Image getImage() {
		return im;
	}
	
	public Image getDead() {
		return im2;
	}
	
	public int getX() {
		return (int)xPos;
	}
	
	public int getY() {
		return (int)yPos;
	}
	
	public double getAngle() {
		return angle;
	}
	
	public static int getRadius(){
		return (int)RADIUS;
	}
	
	
	
	
	//ALL BULLET STUFF HERE----------------------------------------
	
	
	public double getX2(){
		return xPos + RADIUS* Math.cos(angle);
	}
	
	public double getY2(){
		return yPos + RADIUS* Math.sin(angle);
	}
	
	public  List<Bullet> getBullets() {
		return bullets;
	}
	
	public boolean getAlive() {
		return alive;
	}
	
	public void decBullets() {
		bullets.remove(0);
	}
	
	public int getBulletSize() {
		return bullets.size();
	}
	

	public void moveBullets() {
		for(Bullet b : bullets) {
		b.move();	
		}
	}
	
	public void wipeBullets() {
		bullets = new ArrayList<Bullet>();
	}
	
	//OBSTACLES HERE----------------------------------------------------------------------
	
	public void obAdder() {
		//spawn mud, min  0 max 2
		if(Math.random()>.5) { 
			double x = xAdj + (Math.random()*(WIDTH/2 - MUD_WIDTH));
			double y = (Math.random()*(HEIGHT - MUD_HEIGHT));
			Rectangle rect = new Rectangle((int)x, (int)y, (int)MUD_WIDTH, (int)MUD_HEIGHT);
			obstacles.add(new Mud(rect));
		}
		
		//SPAWN WALLS min 0 max 8, but average 5.6
		for(int i = 0; i < 4; i++) {
			//if it overlaps then don' add it and decrement i
			if(Math.random()<.7) {
				if(Math.random()<.5) { //spawn horz wall
					double x = xAdj + (Math.random()*(WIDTH/2 - WALL_WIDTH));
					double y = (Math.random()*(HEIGHT - WALL_HEIGHT));
					
					Rectangle rect = new Rectangle((int)x, (int)y, (int)WALL_WIDTH, (int)WALL_HEIGHT);
					if(collides(xPos,yPos,rect,RADIUS)){
						i--; 
					} 
					else if(overlap(rect)) {
						i--;
					}
					else {
						obstacles.add(new Wall(rect,new ImageIcon("wallhorz.png").getImage()));
					}
				}
				else { //spawn vert wall
					double x = xAdj + (Math.random()*(WIDTH/2 - WALL_HEIGHT));
					double y = (Math.random()*(HEIGHT - WALL_WIDTH));
					
					Rectangle rect = new Rectangle((int)x, (int)y, (int)WALL_HEIGHT, (int)WALL_WIDTH);
					if(collides(xPos,yPos,rect,RADIUS)){
						i--; 
					} 
					else if(overlap(rect)) {
						i--;	
					}
					else {
						obstacles.add(new Wall(rect,new ImageIcon("wallvert.png").getImage()));
					}
				}
			}
		}
//		Rectangle rect = new Rectangle((int)xAdj+200 , 200, (int)WALL_HEIGHT, (int)WALL_WIDTH);
//		obstacles.add(new Wall(rect,new ImageIcon("wallvert.png").getImage()));
			
	}
	
	public static List<Obstacle> getObs(){
		return obstacles;
	}
	
	public static void wipeObs() {
		obstacles = new ArrayList<Obstacle>();
	}
	
	// EXTRA HELPER METHODS---------------------------------------------------------------
	
	private static boolean collides(double X, double Y, Rectangle r1, double radius) {
	    double closestX = clamp(X, r1.x, r1.x + r1.width);
	    double closestY = clamp(Y, r1.y , r1.y + r1.height);
	 
	    double distanceX = X - closestX;
	    double distanceY = Y - closestY;
	 
	    return Math.pow(distanceX, 2) + Math.pow(distanceY, 2) < Math.pow(radius, 2);
	}
	 
	private static double clamp(double value, double min, double max) {
	    double x = value;
	    if (x < min) {
	        x = min;
	    } else if (x > max) {
	        x = max;
	    }
	    return x;
	}
	
	private static boolean overlap(Rectangle rect) {
		for(Obstacle o: obstacles) {
			if(o instanceof Wall) {
				if(rect.intersects(o.getRect())) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	

	
	
	
	
}
