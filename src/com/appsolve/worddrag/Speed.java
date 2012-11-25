/**
 * 
 */
package com.appsolve.worddrag;

import com.appsolve.worddrag.R;

/**
 * The Speed class keeps track of the bearing of an object
 * in the 2D plane. It holds the speed values on both axis 
 * and the directions on those. An object with the ability
 * to move will contain this class and the move method will
 * update its position according to the speed. 
 *   
 * @author John Neto
 *
 */
public class Speed {
	
	public static final int DIRECTION_RIGHT	= 1;
	public static final int DIRECTION_LEFT	= -1;
	public static final int DIRECTION_UP	= -1;
	public static final int DIRECTION_DOWN	= 1;
	public static final int DIRECTION_NULL = 0;
	
	private float xv = 3;	// velocity value on the X axis
	private float yv = 3;	// velocity value on the Y axis
	
	private int xDirection = DIRECTION_RIGHT;
	private int yDirection = DIRECTION_DOWN;
	
	public Speed() {
		this.xv = 3;
		this.yv = 3;
	}

	public Speed(float xv, float yv) {
		this.xv = xv;
		this.yv = yv;
	}

	public float getXv() {
		return xv;
	}
	public void setXv(float xv) {
		this.xv = xv;
	}
	public float getYv() {
		return yv;
	}
	public void setYv(float yv) {
		this.yv = yv;
	}

	public int getxDirection() {
		return xDirection;
	}
	public void setxDirection(int xDirection) {
		this.xDirection = xDirection;
	}
	public int getyDirection() {
		return yDirection;
	}
	public void setyDirection(int yDirection) {
		this.yDirection = yDirection;
	}

	// changes the direction on the X axis
	public void toggleXDirection() {
		xDirection = xDirection * -1;
	}

	// changes the direction on the Y axis
	public void toggleYDirection() {
		yDirection = yDirection * -1;
	}
	
	// changes the direction on the Y axis
	public void moveRightDirection() {
		xDirection = 1;
		yDirection = 0;
	}
	public void moveDownDirection() {
		xDirection = 0;
		yDirection = 1;
	}
}