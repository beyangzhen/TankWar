package com.tank;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;


public class BloodBag {
	int x, y, w, h;
	
	int step = 0; //��¼Ѫ���ƶ����ڼ���
	
	private TankClient tc;
	
	private boolean live = true; //��� Ѫ���Ƿ����

	//Ѫ�������ƶ��Ĺ켣����
	private int pos[][] = {
							{650, 450}, {680, 450}, {710, 450}, {710, 480}, {710, 510}, {680, 510}, {650, 510}, {650, 480}
						  };
	
	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}
	
	public BloodBag() {
		this.x = pos[0][0];
		this.y = pos[0][1];
		w = h = 15;
	}
	
	public void draw(Graphics g) {
		if(!live) return;
				
		move();
		
		Color c = g.getColor();
		g.setColor(Color.MAGENTA);
		g.fillRect(x, y, w, h);
		g.setColor(c);
	}

	private void move() {
		step ++;
		if(step == pos.length) {
			step = 0;
		}
		x = pos[step][0];
		y = pos[step][1];
	}
	
	public Rectangle getRect() {
		return new Rectangle(x, y, w, h); 
	}
	
}
