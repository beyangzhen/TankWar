package com.tank;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Random;


public class Tank {
	public static final int XSPEED = 5;
	public static final int YSPEED = 5;
	
	public static final int WIDTH = 30;
	public static final int HEIGHT = 30;
	
	public static final int BLOOD = 100;
	
	TankClient tc; //��������
	
	private int x, y;
	private int oldX, oldY; //�ƶ���̹����һ����x��y����
	
	private int blood = BLOOD; //̹������ֵ
	private BloodBar bb = new BloodBar();
	
	Random r = new Random(); //���������
	
	private boolean live = true; //���̹���Ƿ񱻴���
	private boolean good; //���̹�˺û�
	private boolean bL=false, bU=false, bR=false, bD=false; //��Ǽ���������Щ�����
	//enum Direction {L, LU, U, RU, R, RD, D, LD, STOP}; //����ͨ������Tank����
	
	private Direction dir = Direction.STOP;
	private Direction ptDir = Direction.D;
	
	int step = r.nextInt(12) + 3; //̹��ÿ�ƶ�step�����͸ı䷽��
	
	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}
	
	public boolean isGood() {
		return good;
	}
	
	public int getBlood() {
		return blood;
	}

	public void setBlood(int blood) {
		this.blood = blood;
	}

	
	public Tank(int x, int y, boolean good) {
		this.x = x;
		this.y = y;
		this.oldX = x; //oldX�ĳ�ʼ��
		this.oldY = y; //oldY�ĳ�ʼ��
		this.good = good;
	}
	
	public Tank(int x, int y, boolean good, Direction dir, TankClient tc) { //ͨ�����췽���������Ķ����ʼ��(��ֵ)
		this(x, y, good);
		this.dir = dir;
		this.tc = tc;
	}
	
	public void draw(Graphics g) {
		move();
		
		if(!live) { //̹�����ˣ��ʹ��������Ƴ�
			if(!good) {
				tc.tanks.remove(this);
			}
			return;
		}
		Color c = g.getColor();
		if(good) g.setColor(Color.GREEN);
		else g.setColor(Color.BLUE);
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);
		
		if(good) bb.draw(g);
		
		g.setColor(Color.WHITE);
		switch(ptDir) {
		case L:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x, y + Tank.HEIGHT / 2);
			break;
		case LU:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x, y);
			break;
		case U:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH / 2, y);
			break;
		case RU:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH, y);
			break;
		case R:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH, y + Tank.HEIGHT / 2);
			break;
		case RD:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH, y + Tank.HEIGHT);
			break;
		case D:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH / 2, y + Tank.HEIGHT);
			break;
		case LD:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x, y + Tank.HEIGHT);
			break;
		}
	}

	void move() {
		
		this.oldX = x; //��ÿ���ƶ�ǰ��x����oldX
		this.oldY = y; //��ÿ���ƶ�ǰ��x����oldY
		
		switch(dir) {
		case L:
			x -= XSPEED;
			break;
		case LU:
			x -= XSPEED;
			y -= YSPEED;
			break;
		case U:
			y -= YSPEED;
			break;
		case RU:
			x += XSPEED;
			y -= YSPEED;
			break;
		case R:
			x += XSPEED;
			break;
		case RD:
			x += XSPEED;
			y += YSPEED;
			break;
		case D:
			y += YSPEED;
			break;
		case LD:
			x -= XSPEED;
			y += YSPEED;
			break;
		case STOP:
			break;
		}
		
		//����Ͳ������̹�˷���ı�
		if(this.dir != Direction.STOP) {
			this.ptDir = this.dir;
		}
		
		//����̹�˳���
		if(x < 1) x = 1;
		if(y < 24) y = 24;
		if(x + Tank.WIDTH > TankClient.GAME_WIDTH - 3) x = TankClient.GAME_WIDTH - Tank.WIDTH - 3;
		if(y + Tank.HEIGHT > TankClient.GAME_HEIGHT - 3) y = TankClient.GAME_HEIGHT - Tank.HEIGHT - 3;
		
		//�õз�̹��ÿ�ƶ�������ɵ�step����ı��ƶ����򣬼����������һ��step�ƶ�
		if(!good) {
			Direction dirs[] = Direction.values(); //��enumת��Ϊ���飬enum����ͨ���±�ȡԪ��
			if(step == 0) {
				step = r.nextInt(12) + 3;
				int rn = r.nextInt(dirs.length);
				dir = dirs[rn];
			}
			
			step --;
			
			if(r.nextInt(40) > 38) this.fire(); //����ÿ��һ��ͼ����̹�˾ͷ�һ���ӵ�
		}
	}
	
	private void stay() { //��̹��ÿ��ײǽ�󶼻ص���һ����λ�ã���ֹ̹��ÿ����ײ���ʱ�Լ�����ǽ�ϣ�Ȼ��һֱSTOP��
		this.x = oldX;
		this.y = oldY;
	}
	
	//����"������"Ϊ true��"�ɿ�"��Ϊ false
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch(key) {
		case KeyEvent.VK_F2:
			if(!this.live) {
				this.live = true;
				this.blood = BLOOD;
			}
			break;
		case KeyEvent.VK_A:
			superFire();
			break;
		case KeyEvent.VK_LEFT :
			bL = true;
			break;
		case KeyEvent.VK_UP :
			bU = true;
			break;
		case KeyEvent.VK_RIGHT :
			bR = true;
			break;
		case KeyEvent.VK_DOWN :
			bD = true;
			break;
		}
		locateDirection();
	}
	
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch(key) {
		case KeyEvent.VK_SPACE:
			fire();
			break;
		case KeyEvent.VK_B:
			superFire();
			break;
		case KeyEvent.VK_LEFT :
			bL = false;
			break;
		case KeyEvent.VK_UP :
			bU = false;
			break;
		case KeyEvent.VK_RIGHT :
			bR = false;
			break;
		case KeyEvent.VK_DOWN :
			bD = false;
			break;
		}
		locateDirection();
	}
	
	//�ж�̹�˵�ǰ���ķ���
	void locateDirection() {
		if(bL && !bU && !bR && !bD) dir = Direction.L;
		else if(bL && bU && !bR && !bD) dir = Direction.LU;
		else if(!bL && bU && !bR && !bD) dir = Direction.U;
		else if(!bL && bU && bR && !bD) dir = Direction.RU;
		else if(!bL && !bU && bR && !bD) dir = Direction.R;
		else if(!bL && !bU && bR && bD) dir = Direction.RD;
		else if(!bL && !bU && !bR && bD) dir = Direction.D;
		else if(bL && !bU && !bR && bD) dir = Direction.LD;
		else if(!bL && !bU && !bR && !bD) dir = Direction.STOP;
	}
	
	//����Ͳ�����ڵ�
	public Missile fire() {
		if(!live) return null; //̹�����ˣ��Ͳ����ٷ��ӵ�
		int x = this.x + Tank.WIDTH / 2 - Missile.WIDTH / 2;
		int y = this.y + Tank.HEIGHT / 2 - Missile.HEIGHT / 2;
		Missile m = new Missile(x, y, good, ptDir, this.tc); //����ǰ̹�˵ĺû�good����̹���ӵ��ĺû�
		tc.missiles.add(m);
		return m;
	}
	
	//�������ⷽ�����ڵ�
	public Missile fire(Direction dir) {
		if(!live) return null; //̹�����ˣ��Ͳ����ٷ��ӵ�
		int x = this.x + Tank.WIDTH / 2 - Missile.WIDTH / 2;
		int y = this.y + Tank.HEIGHT / 2 - Missile.HEIGHT / 2;
		Missile m = new Missile(x, y, good, dir, this.tc); //����ǰ̹�˵ĺû�good����̹���ӵ��ĺû�
		tc.missiles.add(m);
		return m;
	}
	
	//���䳬���ڵ�
	public void superFire() {
		Direction dirs[] = Direction.values();
		for(int i=0; i<8; i++) {
			fire(dirs[i]);
		}
	}
	
	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	
	//̹��ײǽ����
	public boolean collidesWithWall(Wall w) {
		if(this.getRect().intersects(w.getRect()) && this.live) {
			this.stay(); 
			return true;
		}
		return false;
	}
	
	//̹��֮����ײ����
	public boolean collidesWithTanks(java.util.List<Tank> tanks) {
		for(int i=0; i<tanks.size(); i++) {
			Tank t = tanks.get(i);
			if(this != t) { //��ֹ̹���Լ����Լ���ײ���
				if(this.getRect().intersects(t.getRect()) && this.live && t.isLive()) {
					this.stay();
					t.stay();
					return true;
				}
			}
		}
		return false;
	}
	
	//�ڲ�BloodBar��
	private class BloodBar {
		public void draw(Graphics g) {
			Color c = g.getColor();
			g.setColor(Color.RED);
			g.drawRect(x, y-12, WIDTH, 6); //�����ľ���
			int w = WIDTH * blood/100;
			g.fillRect(x, y-12, w, 6); //��ʵ�ľ���
			g.setColor(c);
		}
	}
	
	public boolean eat(BloodBag bb) {
		if(this.live && bb.isLive() && this.blood != BLOOD && this.getRect().intersects(bb.getRect())) {
			this.blood = BLOOD;
			bb.setLive(false);
			return true;
		}
		return false;
	}

}