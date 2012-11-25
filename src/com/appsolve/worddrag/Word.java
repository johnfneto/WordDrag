/**
 * 
 */
package com.appsolve.worddrag;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.content.Context;
import android.content.res.Resources;
import com.appsolve.worddrag.R;


/**
 * This is a test droid that is dragged, dropped, moved, smashed against
 * the wall and done other terrible things with.
 * Wait till it gets a weapon!
 * 
 * @author John Neto
 *
 */
public class Word {

	private static final String TAG = "GrabWord";
	
	public static final int PLAYING	= 1;
	public static final int IMPLODE	= 2;
	public static final int EXPLODE	= 3;
	public static final int STOPED	= 4;
	public static final int OUT_OF_SCREEN	= 5;
	
	private String text;	// the actual bitmap
	private int color;
	private int x;			// the X coordinate
	private int y;			// the Y coordinate
	private int state;
	private boolean touched;	// if word is touched/picked up
	private boolean stopped;	// word is stopped at bottom screen
	private boolean gone;
	private boolean center;
	private Speed speed;	// the speed with its directions
	private int width;
	private int height;
	//Bitmap top_bitmap;
	
	public Word(String text, int color, int x, int y, int xDirection, int yDirection) {
		this.text = text;
		this.color = color;
		this.x = x;
		this.y = y;
		this.speed = new Speed();
		this.speed.setxDirection(xDirection);
		this.speed.setyDirection(yDirection);
		this.state = PLAYING;
		this.stopped = false;
		this.touched = false;
		this.center = false;
		
		//top_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.half_base3);
	}
	
	public String getWord() {
		return text;
	}
	public void setWord(String text) {
		this.text = text;
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
	public void calculateWidth(Paint paint) {
		//Paint paint = new Paint();
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		this.width = bounds.width();
	}
	public int getWidth() {
		return width;
	}
	
	public void calculateHeight(Paint paint) {
		//Paint paint = new Paint();
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		this.height = bounds.height();
	}
	public int getHeight() {
		return height;
	}	
	

	public boolean isTouched() {
		return touched;
	}

	public void setTouched(boolean touched) {
		this.touched = touched;
	}
	
	public boolean isStopped() {
		return stopped;
	}

	public void setIsStopped(boolean stopped) {
		this.stopped = stopped;
	}	
	
	public boolean getIsStopped() {
		return stopped;	
	}
	
	public void setIsGone(boolean gone) {
		this.gone = gone;
	}	
	
	public boolean getIsGone() {
		return gone;	
	}	
	public void setIsInCenter(boolean center) {
		this.center = center;		
	}
	public boolean getIsInCenter() {
		return center;	
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

	public void setColor(int color) {
		this.color = color;
	}	
	
	public int getColor() {
		return color;
	}	
	
	public void setState(int state) {
		this.state = state;
		//Log.d(TAG, "setState="+state);
	}	
	
	public int getState() {
		return state;
	}		

	public void draw(Canvas canvas, Context context) {
		String gText = text;

	  Resources resources = context.getResources();
	  float scale = resources.getDisplayMetrics().density;
	  //Log.d(TAG, "scale="+Float.toString(scale));
	  // new antialised Paint
	  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	  paint.setColor(color);
	  // text size in pixels
	  //paint.setTextSize((int) (14 * scale));
	  paint.setTextSize((int)(26*scale));
	  // text shadow
	  paint.setShadowLayer(1f, 0f, 1f, Color.GRAY);
	  canvas.drawText(gText, x , y , paint);
	  paint.setColor(Color.BLACK);
	  
	  calculateWidth(paint);
	  calculateHeight(paint);
	  
	  /*
	  if (this.getWord().equals("difficult")) {
			Log.d(TAG, "x="+Integer.toString(x));
			Log.d(TAG, "yW="+Integer.toString(y));
		  
	  }*/
		  
	  
	  /*
	  if (getIsStopped()) {
		  Bitmap top_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.half_base4);
		  canvas.drawBitmap(top_bitmap, 240, getHeight()-20, null);		  
	  }*/
	}

	/**
	 * Method which updates the droid's internal state every tick
	 */
	public void update(int screenHight, int screenWidth) {
		if (state == PLAYING)
		if (!touched) {
		//if (!touched && !stopped) {
			x += (speed.getXv() * speed.getxDirection()); 
			y += (speed.getYv() * speed.getyDirection());
		}
		
		if (state == IMPLODE) {
			//Log.d(TAG, "IMPLODE");
			implode(screenHight, screenWidth);
		}
		if (state == EXPLODE)	{	
			//Log.d(TAG, "EXPLODE");
			if (state != OUT_OF_SCREEN)
				explode(screenHight, screenWidth);
		}
		

		
		if (stopped) {
			//Log.d(TAG, "y="+Integer.toString(y));
			x += (speed.getXv() * speed.getxDirection()); 
		}
	}
	
	public void implode(int screenHight, int screenWidth) {
		//int centerX = 120, centerY = 240;

		int centerX = screenWidth/2 - getWidth()/2, centerY = screenHight/2;
		int deltaX, deltaY;

		//Log.d(TAG, "IMPLODE");
		//Log.d(TAG, "screenWidth"+Integer.toString(screenWidth));
		//Log.d(TAG, "getWidth()"+Integer.toString(getWidth()));

		
		//if ((x > 116 && x < 124) && (y > 236 && y < 244) ) {
		if ((x > centerX-4 && x < centerX+4) && (y > centerY-4 && y < centerY+4) ) {
			setIsInCenter(true);
			//Log.d(TAG, text+" is InCenter");
		}	
		else {
		if (x <= centerX && y < centerY) { //Left Top corner		
			if (x == centerX)
				y = y + 5;				
			else {	
				deltaX = centerX - x;
				deltaY = centerY - y;
				y = centerY  - (int) Math.floor(deltaY * ((Math.hypot(deltaX, deltaY) - 5) / (Math.hypot(deltaX, deltaY))));
				x = centerX  - (int) Math.floor(deltaX * ((Math.hypot(deltaX, deltaY) - 5) / (Math.hypot(deltaX, deltaY))));
			}
		}
		else {
		if (x < centerX && y >= centerY) { //Left Bottom corner
			if (y == centerY)
				x = x + 5;
			else {
				deltaX = centerX - x;
				deltaY = y - centerY;
				x = centerX - (int) Math.floor(deltaX * ((Math.hypot(deltaX, deltaY) - 5) / (Math.hypot(deltaX, deltaY))));
				y = centerY + (int) Math.floor(deltaY * ((Math.hypot(deltaX, deltaY) - 5) / (Math.hypot(deltaX, deltaY))));
			}
		}		
		else {
		if (x >= centerX && y > centerY) { //Right Bottom corner
			if (x == centerX)
				y = y - 5;
			else {			
				deltaX = x - centerX;
				deltaY = y - centerY;
				y = centerY  + (int) Math.floor(deltaY * ((Math.hypot(deltaX, deltaY) - 5) / (Math.hypot(deltaX, deltaY))));
				x = centerX  + (int) Math.floor(deltaX * ((Math.hypot(deltaX, deltaY) - 5) / (Math.hypot(deltaX, deltaY))));
			}
		}			
		else {
		if (x > centerX && y <= centerY) { //Right Top corner
			if (y == centerY)
				x = x - 5;
			else {
				deltaX = x - centerX;
				deltaY = centerY - y;
				x = centerX + (int) Math.floor(deltaX * ((Math.hypot(deltaX, deltaY) - 5) / (Math.hypot(deltaX, deltaY))));
				y = centerY - (int) Math.floor(deltaY * ((Math.hypot(deltaX, deltaY) - 5) / (Math.hypot(deltaX, deltaY))));
			}
		}			
		}
		}
		}
		}
		//Log.d(TAG, text+" - x: "+x+" ; y: "+y);
		/*
		if (x > 119) speed.setxDirection(Speed.DIRECTION_LEFT);
		if (x < 121) speed.setxDirection(Speed.DIRECTION_RIGHT);
		x += (speed.getXv() * speed.getxDirection()); 
		
		if (y > 239) speed.setyDirection(Speed.DIRECTION_UP);
		if (y < 241) speed.setyDirection(Speed.DIRECTION_DOWN);		
		y += (speed.getYv() * speed.getyDirection());		
		 */
		


		
		//Log.d(TAG, "word="+text+" is Stoped:"+isStopped());
	}



	public void explode(int screenHight, int screenWidth) {
		//if (x < 120) speed.setxDirection(Speed.DIRECTION_LEFT);
		//if (x > 120) speed.setxDirection(Speed.DIRECTION_RIGHT);

		x += (speed.getXv() * speed.getxDirection()) * 3; 
		
		//if (y < 240) speed.setyDirection(Speed.DIRECTION_UP);
		//if (y > 240) speed.setyDirection(Speed.DIRECTION_DOWN);
		
		y += (speed.getYv() * speed.getyDirection()) * 3;
		
		if ((x > screenWidth + getWidth()/2 || x < 0 - getWidth()/2) || (y > screenHight || y < - getHeight()) ) {

			setIsGone(true);
			setState(OUT_OF_SCREEN);
			//setState(Word.STOPED);
		}
	}	
	
	
	/**
	 * Handles the {@link MotionEvent.ACTION_DOWN} event. If the event happens on the 
	 * bitmap surface then the touched state is set to <code>true</code> otherwise to <code>false</code>
	 * @param eventX - the event's X coordinate
	 * @param eventY - the event's Y coordinate
	 */
	public void handleActionDown(int eventX, int eventY) {
		if (eventX >= (x ) && (eventX <= (x + getWidth() ))) {
			if (eventY >= (y - getHeight()) && (eventY <= y )) {
				// word touched
				setTouched(true);
			} else {
				setTouched(false);
			}
		} else {
			setTouched(false);
		}

	}
}
