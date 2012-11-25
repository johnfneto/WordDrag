/**
 * 
 */
package com.appsolve.worddrag;



import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.content.Context;



/**
 * This is a test droid that is dragged, dropped, moved, smashed against
 * the wall and done other terrible things with.
 * Wait till it gets a weapon!
 * 
 * @author John Neto
 *
 */
public class MenuButton {
	
	private static final String TAG = "GrabWord";
	
	public static final int HIDDEN	= 1;
	public static final int MOVING	= 2;
	public static final int STOPED	= 3;
	public static final int FIND	= 4;
	public static final int DRAG 	= 5;
	public static final int DROP 	= 6;
	
	private Bitmap bitmap;
	private int x;			// the X coordinate
	private int y;
	private int startY;			// the Y coordinate
	private int finishY;			// the Y coordinate
	private int state;
	private Speed speed;	// the speed with its directions

	public MenuButton(Bitmap bmp, int x, int startY, int finishY) {
		this.bitmap = bmp;
		this.x = x;
		this.startY = startY;
		this.finishY = finishY;
		this.y = this.startY;
		this.speed = new Speed();
		this.speed.setYv(8);
		this.speed.setxDirection(Speed.DIRECTION_NULL);
		this.speed.setyDirection(Speed.DIRECTION_UP);
		this.state = HIDDEN;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}

	public Speed getSpeed() {
		return speed;
	}

	public void setSpeed(Speed speed) {
		this.speed = speed;
	}
	public void changeSpeed(float x, float y) {
		speed.setXv(x);
		speed.setYv(y);
	}
	
	public void setState(int state) {
		this.state = state;
		//Log.d(TAG, "setState="+state);
	}	
	
	public int getState() {
		return state;
	}	
	
	public int getFinishY() {
		// TODO Auto-generated method stub
		return finishY;
	}

	public void draw(Canvas canvas, Context context) {
		if (state != HIDDEN) 
			canvas.drawBitmap(bitmap, x, y, null);
	}

	/**
	 * Method which updates the droid's internal state every tick
	 */
	public void update(int screenHight, int screenWidth) {
		if (state == MOVING) {
			//x += (speed.getXv() * speed.getxDirection()); 
			y += (speed.getYv() * speed.getyDirection());
		}
		
		if (state == DRAG) {
			if (y >= screenHight)
				state = HIDDEN;
			//Log.d(TAG, "yF1="+Integer.toString(y));
			if (y < screenHight) {
				y += (speed.getYv() * speed.getyDirection());	
				//Log.d(TAG, "Draging");
				//Log.d(TAG,"Direction="+speed.getyDirection());
				//Log.d(TAG, "yF2="+Integer.toString(y));
			}
			if (x >= screenWidth/2)
				x -= 3;
			else
				x +=3;
		}
	
	}

	public void find(int screenHight, int screenWidth, int wordX, int wordY) {
		if ((x > wordX-5 && x < wordX+5) && (y > wordY-5 && y < wordY+5) ) {
			state = DRAG;
			Log.d(TAG, "setState="+state);
		}
		else {
			if (wordX >= x)
				x += 4;
			else
				x -=4;
			
			if (wordY >= y)
				y += 4;
			else
				y -=4;		
		}
		
		
	}



	
}
