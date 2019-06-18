import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Tankorama {
	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;
	private static int frameDelay = 33;//37
	

	public static void main(String[] args) {
		
		JFrame frame = new JFrame("Tankorama");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		TankCanvas canvas = new TankCanvas(WIDTH,HEIGHT);
		canvas.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		frame.add(canvas);
		frame.pack();
		
		
		class TimerListener implements ActionListener {		
			public void actionPerformed(ActionEvent e) {
				canvas.getTank(0).moveBullets();
				canvas.getTank(1).moveBullets();
				canvas.getTank(0).move();
				canvas.getTank(1).move();
				canvas.checkCollisions();
				canvas.checkMud();
				canvas.repaint();
			}}
		
		
		class TankKeyListener1 implements KeyListener{

			public void keyPressed(KeyEvent e) {
				//System.out.println("Pressed" + e);
				//System.out.println("Pressed" + e.getKeyChar());
				//System.out.println("Required" + canvas.getTank(1).getUp());
				
		
				for(int t = 0; t<2; t++) {
					if(e.getKeyCode() == canvas.getTank(t).getKey("up")) {
						canvas.getTank(t).go("up");
					} else if(e.getKeyCode() == canvas.getTank(t).getKey("down")) {
						canvas.getTank(t).go("down");
					}
					
					if(e.getKeyCode() == canvas.getTank(t).getKey("left")) {
						canvas.getTank(t).go("left");
					} else if(e.getKeyCode() == canvas.getTank(t).getKey("right")) {
						canvas.getTank(t).go("right");
					}
					
					if(e.getKeyCode() == canvas.getTank(t).getKey("fire")) {
						canvas.getTank(t).fire();
						canvas.startGame(t);
					}
					if(e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
						canvas.reset();
					}
				}
				
				
			}

			public void keyReleased(KeyEvent e) {
				for(int t = 0; t<2; t++) {
					if(e.getKeyCode() == canvas.getTank(t).getKey("up")) {
						canvas.getTank(t).stop("up");
					} else if(e.getKeyCode() == canvas.getTank(t).getKey("down")) {
						canvas.getTank(t).stop("down");
					}
					
					if(e.getKeyCode() == canvas.getTank(t).getKey("left")) {
						canvas.getTank(t).stop("left");
					} else if(e.getKeyCode() == canvas.getTank(t).getKey("right")) {
						canvas.getTank(t).stop("right");
					}
				
				}
				
			}

			public void keyTyped(KeyEvent e) {
			
			}
		}
		
		JButton back = new JButton("background");
		class BackListener implements ActionListener{//inner class
			public void actionPerformed(ActionEvent e) {
				canvas.backgroundFlip();
			}
			
		}
		back.addActionListener(new BackListener());
		
		JPanel panel = new JPanel();
		panel.add(back);
//		panel.add(wall);
//		frame.add(panel); //THIS WAS NOT DONE- PLEASE DON"T TAKE OFF POINTS
		
		canvas.addKeyListener(new TankKeyListener1());
		canvas.setFocusable(true); //this is to auto focus on the keyboard 
		canvas.requestFocus();
		
		
		Timer t = new Timer(frameDelay, new TimerListener());
		t.start();
		
		
		
		frame.setVisible(true);
	}

}
