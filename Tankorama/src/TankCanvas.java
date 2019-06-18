import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;


public class TankCanvas extends JComponent {
	private final int WIDTH;
	private final int HEIGHT;
	
	private boolean start;
	private boolean one;
	private boolean two;
	private static int startDelay = 1000;
	
	private boolean back;
	private boolean walls;
	
	private List<Tank> tanks;
	private int score1int;
	JLabel score1;
	private int score2int;
	JLabel score2;
	
	private boolean deathSequence;
	private static int deathDelay = 4000;
	
	private final int adjustX = 33;
	private final int adjustY = 36;
	private final int adjustX2 = 10;
	private final int adjustY2 = 10;
	
	
	public TankCanvas(int width, int height) {
		WIDTH = width;
		HEIGHT = height;
		tanks = new ArrayList<Tank>();
		deathSequence = false;
		start = false;
		one = false;
		two = false;
		back = true;
		walls = true;
		
		
		Image tank1pic = new ImageIcon("tank1.png").getImage();
		Image tank1pic2 = new ImageIcon("tank1dead.png").getImage();
		tanks.add(new Tank(tank1pic, tank1pic2, 1));
		//score1int =  0;
		
		Image tank2pic = new ImageIcon("tank2.png").getImage();
		Image tank2pic2 = new ImageIcon("tank2dead.png").getImage();
		tanks.add(new Tank(tank2pic, tank2pic2, 2));
		//score2int =  0;
		
		//if()
		
		
		//score1 = new JLabel("Count: " + score1int);
		//score2 = new JLabel("Count: " + score2int);
	}
	
	public void reset() {
		Tank.wipeObs(); //wipe old obstacles
		for(int t = 0; t < tanks.size(); t++) {
			Tank tank = tanks.get(t);
			tank.wipeBullets();
			tank.respawn(); //this spawns new obstacles 
		}
		
	}
	
	public void startGame(int i) {
		if(!start) {
			if(i == 0) {
				one = true;
			}
			else if (i == 1) {
				two = true;
			}
			if(one && two) {
				TimerTask timerTask = new StartTask();
		        Timer timer = new Timer(true);
		        timer.schedule(timerTask, startDelay);  
		        
		        reset(); 
			}
		}
	}
		
		public class StartTask extends TimerTask {
		    public void run() {
		    	start = true;
		    }
		}
	
	public void paintComponent(Graphics gr) {//called automatically, but not always
		Graphics2D g = (Graphics2D) gr;
		
		if(!start) { //BEFORE GAME HAS STARTED---------------------------------------------------
			Image titlescreen = new ImageIcon("titlescreen.png").getImage();
			g.drawImage(titlescreen, 240, 120, this);
			g.setFont(new Font("Comic Sans", Font.BOLD, 30));
			if(one) {
				g.setColor(new Color(9, 25, 255));
				g.drawString("Player One Ready!", 340, 250);
			}
			if(two) {
				g.setColor(new Color(255, 31, 17));
				g.drawString("Player Two Ready!", 680, 250);
			}
		}
		else { //AFTER GAME HAS STARTED---------------------------------------------------
			
			//DRAW BACKGROUND----------------------------------------------------------
			if(back) {
				g.drawImage(new ImageIcon("background.png").getImage(), 0, 0, this);
			}
			//DRAW OBSTACLES---------------------------------------------------------
					List<Obstacle> obstacles = Tank.getObs();
					
					for(Obstacle ob: obstacles) {
						g.drawImage(ob.getImage(), (int)ob.getRect().getX(), (int)ob.getRect().getY(), this);
//						Rectangle rect = ob.getRect();
//						g.drawRect(rect.x, rect.y, rect.width, rect.height); //for testing hitboxes
					}
			
			//DRAW TANKS-------------------------------------------------
			for(int t = 0; t < tanks.size(); t++) {
			Tank tank = tanks.get(t);
			Image im1 = tank.getImage();
			Image im2 = tank.getDead();
			
			int newX = tank.getX() - adjustX;
			int newY = tank.getY() - adjustY;
			AffineTransform at = AffineTransform.getTranslateInstance(newX, newY);
			at.rotate(tank.getAngle(), adjustX, adjustY);
			
			if(tank.getAlive()) 
				g.drawImage(im1, at, this);
			else 
				g.drawImage(im2, at, this);
			}
			
			
			//DRAW BULLETS---------------------------------------
			for(int t = 0; t < tanks.size(); t++) {
				Tank tank = tanks.get(t);
			
				for(int i = 0; i < tank.getBulletSize(); i++) {
					List<Bullet> bullets = tank.getBullets();
					Bullet b = bullets.get(i);
					if(b.keep()) {
						g.drawImage(b.getImage(), (int)b.getX()-adjustX2, (int)b.getY()- adjustY2, this);
					}
					else 
						bullets.remove(i--);
				}
			}
			
			
			
			
			
			//DRAW WORDS------------------------------------------------------------
			g.setFont(new Font("Times New Roman", Font.PLAIN, 20));
			Image whitebox = new ImageIcon("whitebox.png").getImage();
			g.drawImage(whitebox, 200, 0, this);
			g.drawImage(whitebox, 1000, 0, this);
			g.drawString("Player One Score: " + score1int, 200, 15);
			g.drawString("Player One Score: " + score2int, 1000, 15);	
		}
	}
	
	public void checkCollisions() {
		Tank tank0 = tanks.get(0);
		Tank tank1 = tanks.get(1);
		
		double X0 = tank0.getX();
		double Y0 = tank0.getY();
		double X1 = tank1.getX();
		double Y1 = tank1.getY();
		
		
		for(int t = 0; t < tanks.size(); t++) {//loop thru to get both tank bullet lists
		Tank tank = tanks.get(t);
			
			for(int i = 0; i < tank.getBulletSize(); i++) {
				List<Bullet> bullets = tank.getBullets();
				Bullet b = bullets.get(i);
				if(Point2D.distance(b.getX(), b.getY(), X0, Y0)<= Tank.getRadius() + Bullet.getRadius()) {
					tank0.die();
					bullets.remove(i--);
					
					if(!deathSequence) {
						TimerTask timerTask = new DeathTask();
				        Timer timer = new Timer(true);
				        timer.schedule(timerTask, deathDelay);  
				        
				        deathSequence = true;
			        }
				}
				if(Point2D.distance(b.getX(), b.getY(), X1, Y1)<= Tank.getRadius() + Bullet.getRadius()) {
					tank1.die();
					bullets.remove(i--);
					
					if(!deathSequence) {
						TimerTask timerTask = new DeathTask();
				        Timer timer = new Timer(true);
				        timer.schedule(timerTask, deathDelay);  
				        
				        deathSequence = true;
				    }
				}
			}	
		}	
	}
	
	public void checkMud() {
		for(int t = 0; t < tanks.size(); t++) {//loop thru to get both tank bullet lists
			Tank tank = tanks.get(t);
			tank.noMud();
			List<Obstacle> obstacles = Tank.getObs();
			for(Obstacle ob: obstacles) {
				if(ob instanceof Mud) {
					if(ob.getRect().contains((int)tank.getX(),(int)tank.getY())) {
						tank.setMud();
					}
				}
			}
		}
		
	}
	
	public void backgroundFlip() {
		if(back) {
			back = false;
		}
		else {
			back = true;
		}
	}
	
	public class DeathTask extends TimerTask {
	    public void run() {
	    	Tank tank0 = tanks.get(0);
	    	Tank tank1 = tanks.get(1);
	    	//ADD CODE TO DO POINTS
	    	if(tank0.getAlive()) {
	    		score1int++;
	    	}
	    	else if(tank1.getAlive()) {
	    		score2int++;
	    	}
	    	reset();
	    	deathSequence = false;
	    }
	}
	
	public Tank getTank(int i) {
		if(i == 0)
			return tanks.get(0);
		else if(i ==1)
			return tanks.get(1);
		else 
			return null;
	}
	
	
}
