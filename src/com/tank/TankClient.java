package com.tank;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 *  "我方坦克和血包"死后，live设为false，在draw()中不画它们，但该对象还存在，所以碰撞检测时if条件中要加上"live(即： live == true)"
 *   (只是没画出来，但该对象还存在)
 *  "敌方坦克，子弹和爆炸"死后，live设为false，但该对象还存在，所以碰撞检测后将"tanks.remove(对象)"，这时该对象就不存在了
 */
public class TankClient extends Frame {
	
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;
	
	private int pass = 1; //第几关
	private int number = 6; //每关坦克的数量
	
	Tank myTank = new Tank(50, 200, true, Direction.STOP, this);
	
	//墙的厚度要大于子弹（坦克）的speed（即：线程每画一次移动的距离）。否者子弹（坦克）可能会穿过墙，因为碰撞检测是线程每重画一次才检测一次的
	Wall w1 = new Wall(150, 200, 20, 180, this), w2 = new Wall(350, 200, 200, 20, this);
	
	List<Missile> missiles = new ArrayList<Missile> ();
	List<Explode> explodes = new ArrayList<Explode> ();
	List<Tank> tanks = new ArrayList<Tank> ();
	
	BloodBag bb = new BloodBag();
	
	Random r = new Random();
	
	Image offScreenImage = null;
	
	public void paint(Graphics g) {	
		
		if(tanks.size() <= 0) {
			number = number + pass;
			for(int i=0; i<number; i++) {
				tanks.add(new Tank(r.nextInt(GAME_WIDTH - 24) + 24, r.nextInt(GAME_HEIGHT - 3) + 3, false, Direction.D, this));
			}
			pass ++;
		}
		
		myTank.draw(g);
		myTank.eat(bb);
		myTank.collidesWithWall(w1);
		myTank.collidesWithWall(w2);
		
		w1.draw(g);
		w2.draw(g);
		
		bb.draw(g);
		
		for(int i=0; i<missiles.size(); i++) {
			Missile m = missiles.get(i);
            m.hitTanks(tanks); //"碰撞检测"必须时时刻刻进行检测（如：写在paint()方法中）
            m.hitTank(myTank);
            m.hitWall(w1);
            m.hitWall(w2);
			if(!m.isLive()) missiles.remove(m); //出界的炮弹移除,使List容器中元素不会变得太多
			else m.draw(g); //没出界的继续画出来
		}
		
		for(int i=0; i<explodes.size(); i++) {
			Explode e = explodes.get(i);
			e.draw(g);
		}
		
		for(int i=0; i<tanks.size(); i++) {
			Tank t = tanks.get(i);
			t.collidesWithWall(w1);
			t.collidesWithWall(w2);
			t.collidesWithTanks(tanks);
			if(t.collidesWithWall(w1) && t.collidesWithWall(w2) && t.collidesWithTanks(tanks)) {
				tanks.remove(t);
				tanks.add(new Tank(r.nextInt(GAME_WIDTH - 24) + 24, r.nextInt(GAME_HEIGHT - 3) + 3, false, Direction.D, this));
			} else {
				t.draw(g);
			}
		}
		
		g.setColor(Color.GREEN);
		g.drawString("game      pass: " + pass, 10, 50);
		g.drawString("tanks    count: " + tanks.size(), 10, 70);
		g.drawString("missiles count: " + missiles.size(), 10, 90);
		g.drawString("explodes count: " + explodes.size(), 10, 110);		
		g.drawString("tanks    blood: " + myTank.getBlood(), 10, 130);
	}
	
	//双缓冲
	public void update(Graphics g) { //显示屏幕的画笔
		if(offScreenImage == null) {
			offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
		}
		Graphics gOffScreen = offScreenImage.getGraphics(); //虚拟屏幕的画笔
		Color c = gOffScreen.getColor(); //重画后背景重刷新成空白的绿色
		gOffScreen.setColor(Color.BLACK);
		gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		gOffScreen.setColor(c);
		paint(gOffScreen); //先用虚拟屏幕的画笔画出图片
		g.drawImage(offScreenImage, 0, 0, null); //在显示屏幕上画出图片
	}

	public void lauchFrame() {
		
		//添加机器坦克
		for(int i=0; i<6; i++) {
			tanks.add(new Tank(r.nextInt(GAME_WIDTH - 24) + 24, r.nextInt(GAME_HEIGHT - 3) + 3, false, Direction.D, this));
		}
		
		this.setLocation(300, 100);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setTitle("TankWar");
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		this.setResizable(false);
		this.setBackground(Color.GREEN);
		
		this.addKeyListener(new KeyMonitor());
		
		setVisible(true);
		
		new Thread(new PaintThread()).start();
	}
	
	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.lauchFrame();
	}
	
	private class PaintThread implements Runnable {

		public void run() {
			while(true) {
				repaint(); //会调用update(),update()在调用paint()（但不用repaint()，paint()也可以自己调用）
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class KeyMonitor extends KeyAdapter {
		
		//键盘"按下"后触发该事件，然后调用该方法
		public void keyPressed(KeyEvent e) {
			myTank.keyPressed(e); //函数封装语句块
		}
		
		//键盘"松开"后触发该事件，然后调用该方法
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}
	}
	
}
