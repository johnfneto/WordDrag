package com.appsolve.worddrag;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.appsolve.worddrag.R;

/**
 * @author John Neto
 * 
 * This is the main surface that handles the ontouch events and draws
 * the image to the screen.
 */



public class HelpGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String TAG = "GrabWord";
	
	public static final int HELP1	= 1;
	public static final int HELP2	= 2;
	public static final int HELP3	= 3;
	public static final int HELP4	= 4;
	public static final int EXAMPLE	= 5;
	public static final int THREETOGO = 6;
	public static final int FINISH = 7;	
	public static final int EXIT = 8;	
	
	DataBaseHelper db;
	ImageManager im;
	public HelpThread thread;
	CountDownTimer gameTimer, showInfoTimer;
	String correct_word[] = new String[4];
	String extra_word[] = new String[4];
	String main_word;
	Word[] word = new Word[8];
	Word masterWord;
	boolean wordFound;
	int words_left=4;
	String secs_left = "0";
	int temp_secs = 60;
	long deciSecs_left;
	Bitmap bg_bitmap;
	Bitmap timeout_bitmap, restart_bitmap, menu_bitmap;
	Bitmap level_bitmap, complete_bitmap, continue_bitmap;
	Bitmap redTop_bitmap, grayTop_bitmap, base_bitmap, half_base;
	Bitmap redTop_resizedBitmap, grayTop_resizedBitmap, base_resizedBitmap;
	Bitmap touch_screen, help1, help2, help3, help4, help5, help6, arrow1, arrow2, arrow3, arrow4, finger_bmp;
	boolean timeOut = false;
	boolean levelComplete = false;
	boolean finished = false;
	boolean stopped = false;
	boolean gone = false;
	boolean showInfo = false;
	boolean dragging = false;
	int whichDragging = 0;
	int wordHight, screenHight, screenWidth;
	Drawable bg_draw;
	Boolean sufaceCreated = false;
	
	MenuButton restartButton1, menuButton1;
	MenuButton restartButton2, menuButton2, contButton2;

	MenuButton finger;
	
	int state = HELP1;
	
	String  level;
	
	
	

	
	public HelpGamePanel(Context context) {
		super(context);
		db = new DataBaseHelper(context);
		
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		

		im = new ImageManager(context);
		
		//bg_bitmap = im.getBitmap(R.drawable.bg480x800);
		//bg_draw = im.getDrawable(R.drawable.blue_bg);
		
		
		
		bg_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg480x800);
		
		grayTop_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.gray_top);
		redTop_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_top);
		base_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.base);
		half_base = BitmapFactory.decodeResource(context.getResources(), R.drawable.half_base);
		
		touch_screen = BitmapFactory.decodeResource(context.getResources(), R.drawable.touch_screen);
		help1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.help1);
		help2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.help2);
		help3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.help3);
		help4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.help4);
		help5 = BitmapFactory.decodeResource(context.getResources(), R.drawable.help5);
		help6 = BitmapFactory.decodeResource(context.getResources(), R.drawable.help6);
		arrow1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow_1);
		arrow2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow_2);
		arrow3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow_3);
		arrow4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow_4);
		finger_bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.finger);
		//canvas.drawBitmap(restart_bitmap, getWidth()/2 - restart_bitmap.getWidth()/2, 150 + 3*timeout_bitmap.getHeight()/2, null);

		
		


		// create words and load them
		main_word = getMainWord("10");
		correct_word = getSynonyms("10");
		extra_word = getExtraWords(correct_word);
		
		Log.d(TAG, "Create Words");

        

		masterWord = new Word(main_word, Color.BLACK, 0 , 15, Speed.DIRECTION_RIGHT, Speed.DIRECTION_DOWN);
				
		word[0] = new Word(correct_word[0], Color.RED, 0, 50, Speed.DIRECTION_RIGHT, Speed.DIRECTION_DOWN);
		word[1] = new Word(extra_word[0], Color.RED, 300, 200, Speed.DIRECTION_LEFT, Speed.DIRECTION_UP);
		word[2] = new Word(correct_word[1], Color.RED, 0, 200, Speed.DIRECTION_RIGHT, Speed.DIRECTION_UP);
		word[3] = new Word(extra_word[1], Color.RED, 300, 50, Speed.DIRECTION_LEFT, Speed.DIRECTION_DOWN);
		word[4] = new Word(correct_word[2], Color.RED, 300, 430, Speed.DIRECTION_LEFT, Speed.DIRECTION_UP);
		word[5] = new Word(extra_word[2], Color.RED, 0, 430, Speed.DIRECTION_RIGHT, Speed.DIRECTION_UP);
		word[6] = new Word(correct_word[3], Color.RED, 150, 100, Speed.DIRECTION_LEFT, Speed.DIRECTION_UP);
		word[7] = new Word(extra_word[3], Color.RED, 150, 300, Speed.DIRECTION_RIGHT, Speed.DIRECTION_UP);		
		
		
		// create the game loop thread
		thread = new HelpThread(getHolder(), this);
		
		// make the GamePanel focusable so it can handle events
		setFocusable(true);
		
		
		gameTimer = new CountDownTimer(30000, 500) {
            @Override
			public void onTick(long millisUntilFinished) {
        	    long minutes = millisUntilFinished/60000;
        	    long rSeconds = millisUntilFinished%60000;
        	    long seconds = rSeconds/1000;
        	    //long millisonds = rSeconds%1000;
        	    
        	    deciSecs_left = rSeconds/100;
        	    secs_left = Long.toString(seconds + minutes*60);
        	    
        	    /*
        	    if ((seconds + minutes*60) <= 0)
    				for (int i=0; i < word.length; i++) {
    					//word[i].setState(Word.IMPLODE);
    					//word[i].setState(Word.PLAYING);
    				}
        	    
        	    if ((seconds + minutes*60) <= 0 && !levelComplete) {
        	    	timeOut = true;
    				for (int i=0; i < word.length; i++) {
    					word[i].setState(Word.IMPLODE);
    					//word[i].setState(Word.PLAYING);
    					//Log.d(TAG, "IMPLODE trigered");
    				}
            	//Log.d(TAG, " MainGamePanel secs:"+Long.toString(seconds + minutes*60));
        	    }*/
                }     
            @Override
			public void onFinish() {
            	/*
            	if (!levelComplete)
					for (int i=0; i < word.length; i++) {
						word[i].setState(Word.IMPLODE);
						//Log.d(TAG, "word["+Integer.toString(i)+"]");
						//Log.d(TAG, "word="+word[i].getWord()+"; state="+word[i].getState());
					}*/
            }  
       }.start();
	

	showInfoTimer = new CountDownTimer(2000, 500) {
        @Override
		public void onTick(long millisUntilFinished) {
        	//Log.d(TAG, " MainGamePanel secs:"+Long.toString(seconds + minutes*60));
            }

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			
		}     
 
   };
}
	
	
	//@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	//@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the game loop
		thread = new HelpThread(getHolder(), this); // line added
		Thread.currentThread().setName("Help Thread");
		thread.setRunning(true);
		thread.start();
		
		
		// Get all the mesures as soon as the surface is created
		screenHight = getHeight();
		screenWidth = getWidth();
		//Log.d(TAG, "getHeight="+Integer.toString(getHeight()));
		//Log.d(TAG, "getWidth="+Integer.toString(getWidth()));
		
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Rect bounds = new Rect();
	    paint.getTextBounds("a", 0, 1, bounds);
		wordHight = 3*bounds.height();
		//Log.d(TAG, "wordHight a="+Integer.toString(wordHight));
		
		Log.d(TAG, "resizing bitmaps...");
		
		grayTop_resizedBitmap=getResizedBitmap(grayTop_bitmap,2*wordHight,screenWidth);
		redTop_resizedBitmap=getResizedBitmap(redTop_bitmap,2*wordHight,screenWidth);
		base_resizedBitmap=getResizedBitmap(base_bitmap, 2*wordHight,screenWidth);
		
		
		finger = new MenuButton(finger_bmp, getWidth()/2 - finger_bmp.getWidth()/2, getHeight() - finger_bmp.getHeight(),getHeight()/3);
	
		sufaceCreated = true;
		Log.d(TAG, "Surface created");
		//canvas.drawBitmap(menu_bitmap, getWidth()/2 - menu_bitmap.getWidth()/2, 150 + 2*timeout_bitmap.getHeight() + restart_bitmap.getHeight(), null);			
	}

	//@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		//System.gc();

		//bg_bitmap.recycle();
		//grayTop_bitmap.recycle();
		//redTop_bitmap.recycle();
		//base_bitmap.recycle();
		//half_base.recycle();

		//timeout_bitmap.recycle();
		//restart_bitmap.recycle();
		//menu_bitmap.recycle();
		//level_bitmap.recycle();
		//complete_bitmap.recycle();
		//continue_bitmap.recycle();

		//bg_bitmap = null;
		//redTop_bitmap = null;
		//grayTop_bitmap = null;
		//base_bitmap = null;
		//half_base = null;
		//timeout_bitmap=null;
		//restart_bitmap=null;
		//menu_bitmap=null;
		//level_bitmap=null;
		//complete_bitmap=null;
		//continue_bitmap=null;


	     if(showInfoTimer != null) {
	    	 showInfoTimer.cancel();
	    	 showInfoTimer = null;
	     }
		
	     if(gameTimer != null) {
	    	 gameTimer.cancel();
	    	 gameTimer = null;
	     }
		boolean retry = true;
		while (retry) {
			try {
				thread.setRunning(false); // line added
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the thread
			}
		}
		Log.d(TAG, "Thread was shut down cleanly");
	}
	
				/*--------------------- onTouchEvent --------------------------*/	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		

			if (event.getAction() == MotionEvent.ACTION_DOWN) {  // NEXT state
				state++;
				if (state == HELP4)
					word[0].setColor(Color.WHITE);
				if (state == EXAMPLE)
					finger.setState(MenuButton.MOVING);
				if (state == EXIT)
				{
		            thread.setRunning(false);
				    ((Activity)getContext()).finish();
				}

			}
			
			
	    try {
	        Thread.sleep(16);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
		return true;
	}

	
	
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth)
	{
		
		if (bm == null)
			Log.d(TAG, " bm null!!!");
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    //Log.d(TAG, "bm.getWidth():"+Integer.toString(width));
	    //Log.d(TAG, "bm.getHeight():"+Integer.toString(height));	    
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    
	    
	    
	    // create a matrix for the manipulation
	    Matrix matrix = new Matrix();
	    // resize the bit map
	    matrix.postScale(scaleWidth, scaleHeight);
	    // recreate the new Bitmap
	    //Log.d(TAG, "createBitmap w/h :"+Integer.toString(width)+" : "+Integer.toString(height));
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	    return resizedBitmap;
	}
	
				/*--------------------- render --------------------------*/
	
	public void render(Canvas canvas) {

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Context context = getContext();
		//canvas.drawColor(Color.GRAY);
		//Bitmap bg_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_feeling);
		
		canvas.drawBitmap(bg_bitmap, 0, 0, null);
		
		
		//canvas.drawBitmap(getResizedBitmap(grayTop_bitmap,2*wordHight,screenWidth), 0, 0, null);
		//canvas.drawBitmap(getResizedBitmap(redTop_bitmap,2*wordHight,screenWidth), 0, 0, null);
		//canvas.drawBitmap(getResizedBitmap(base_bitmap, 2*wordHight,screenWidth), 0,screenHight - 2*wordHight, null);
		
		//Log.d(TAG, "render1");
		
		if (sufaceCreated) {
		canvas.drawBitmap(grayTop_resizedBitmap, 0, 0, null);
		canvas.drawBitmap(redTop_resizedBitmap, 0, 0, null);
		canvas.drawBitmap(base_resizedBitmap, 0, screenHight - 2*wordHight, null);
		}
		
		//Log.d(TAG, "render2");
		  //Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		  //paint.setTypeface(Typeface.DEFAULT_BOLD);
		  paint.setColor(Color.WHITE);
		  
		  Resources resources = context.getResources();
		  float scale = resources.getDisplayMetrics().density;
		  
		  // text size in pixels
		  //paint.setTextSize(10);
		  paint.setTextSize((int)(11*scale));
		  Rect bounds = new Rect();
		  
		  
		  paint.getTextBounds("Time left", 0, "Time left".length(), bounds);
		  int a = (getWidth()/4-bounds.width())/2;
		  canvas.drawText("Time left", a , (2*wordHight-3*bounds.height()/2)/2 + bounds.height()/2 , paint);
		  //Log.d(TAG, "bounds.height():"+Integer.toString(bounds.height()));
		  
		  paint.getTextBounds("0", 0, 1, bounds);
		  canvas.drawText("30", a , (2*wordHight-3*bounds.height()/2)/2 + bounds.height()/2+5*bounds.height()/3 , paint);

		  
		  paint.getTextBounds("33  ", 0, "33  ".length(), bounds);
		  int b = bounds.width();
		  canvas.drawText(" secs", a+b, (2*wordHight-3*bounds.height()/2)/2 + bounds.height()/2+5*bounds.height()/3 , paint);
		  
		  paint.getTextBounds("Words missing", 0, "Words missing".length(), bounds);

		  //canvas.drawText("Words missing", getWidth()*3/4 + 5 , 17 , paint);
		  canvas.drawText("Words missing", getWidth()*3/4 + (getWidth()/4-bounds.width())/2, (2*wordHight-3*bounds.height()/2)/2 + bounds.height()/2 , paint);
		  //paint.setStyle(Paint.Style.BOL);
		  canvas.drawText(Integer.toString(words_left), getWidth()*7/8 , (2*wordHight-3*bounds.height()/2)/2 + 3*bounds.height()/2 + bounds.height()/4, paint);
		  //Log.d(TAG, "2*wordHight:"+Integer.toString(2*wordHight));
		  //Log.d(TAG, "bounds.height():"+Integer.toString(bounds.height()));
		  
		  

		  
		 switch (state) {
		case HELP1:
			canvas.drawBitmap(help1, getWidth()/2 - help1.getWidth()/2, getHeight()/4, null);
			canvas.drawBitmap(touch_screen, getWidth() - touch_screen.getWidth(), getHeight()-2*wordHight -touch_screen.getHeight(), null);
			break;
		case HELP2:
			for (int i=0; i < word.length; i++) 
				word[i].draw(canvas, context);
			
			canvas.drawBitmap(help2, getWidth()/2 - help2.getWidth()/2, getHeight()/4, null);
			canvas.drawBitmap(touch_screen, 0, getHeight()-2*wordHight -touch_screen.getHeight(), null);
			canvas.drawBitmap(arrow1, getWidth()/2,  2*wordHight, null);
			break;
		case HELP3:
			for (int i=0; i < word.length; i++) 
				word[i].draw(canvas, context);
			
			canvas.drawBitmap(help3, getWidth()/2 - help3.getWidth()/2, getHeight()/4, null);
			canvas.drawBitmap(touch_screen, 0, getHeight()-2*wordHight -touch_screen.getHeight(), null);
			canvas.drawBitmap(arrow2, getWidth()/2, getHeight()/4 + help3.getHeight(), null);			
			break;
		case HELP4:
			
			for (int i=0; i < word.length; i++) 
				word[i].draw(canvas, context);
			canvas.drawBitmap(help4, getWidth()/2 - help4.getWidth()/2, getHeight()/2 -help4.getHeight()/2, null);
			canvas.drawBitmap(touch_screen, 0, getHeight()-2*wordHight -touch_screen.getHeight(), null);
			canvas.drawBitmap(arrow3, getWidth()-arrow3.getWidth(), getHeight()/2 + -help4.getHeight()/2 +help4.getHeight(), null);
			
			
			break;
		case EXAMPLE:
			for (int i=0; i < word.length; i++) 
				word[i].draw(canvas, context);
			
			//finger.setState(MenuButton.MOVING);
			if (!word[0].isStopped())
				finger.draw(canvas, context);
			
			break;	
		case THREETOGO:
			canvas.drawBitmap(help5, getWidth()/2 - help5.getWidth()/2, getHeight()/4 + help5.getHeight()/2, null);
			canvas.drawBitmap(touch_screen, 0, getHeight()-2*wordHight -touch_screen.getHeight(), null);
			canvas.drawBitmap(arrow4, getWidth() - arrow4.getWidth(), 2*wordHight, null);			
			
			break;
		case FINISH:
			canvas.drawBitmap(help6, getWidth()/2 - help6.getWidth()/2, getHeight()/4, null);
	
			
			break;			
		default:
			break;
		} 
		  

		  masterWord.draw(canvas, context);
		  
		  /*
		  if (state == EXAMPLE )
			  finger.draw(canvas, context);*/
		  
		  if (word[0].getIsStopped()) {
			  canvas.drawBitmap(getResizedBitmap(half_base,2*wordHight, screenWidth*1/4), screenWidth*3/4,screenHight - 2*wordHight, null);
		  }	
  
	}
	
				/*--------------------- update --------------------------*/

	/**
	 * This is the game update method. It iterates through all the objects
	 * and calls their update method if they have one or calls specific
	 * engine's update method.
	 */
	public void update() {
		//Log.d(TAG, "update!!!");
		masterWord.setX(getWidth()/2 - masterWord.getWidth()/2);
		masterWord.setY(2*wordHight - wordHight/2 );

		
		for (int i=0; i < word.length; i++) {
			if (state == HELP2 || state == HELP3 || state == HELP4 || state == EXAMPLE)
				if (finger.getState() != MenuButton.DRAG || i != 0)
			if (word[i].getState() == Word.PLAYING) {
					
				// check collision with right wall if heading right
				if (word[i].getSpeed().getxDirection() == Speed.DIRECTION_RIGHT
						&& word[i].getX() + word[i].getWidth()  >= getWidth()) {
					if (!word[i].getIsStopped()) word[i].getSpeed().toggleXDirection();
				}
				// check collision with left wall if heading left
				if (word[i].getSpeed().getxDirection() == Speed.DIRECTION_LEFT
						&& word[i].getX() <= 0) {
					word[i].getSpeed().toggleXDirection();
				}
				// check collision with bottom wall if heading down
				if (word[i].getSpeed().getyDirection() == Speed.DIRECTION_DOWN
						&& word[i].getY() >= getHeight()- 2*wordHight) {
					word[i].getSpeed().toggleYDirection();
				}
				// check collision with top wall if heading up
				if (word[i].getSpeed().getyDirection() == Speed.DIRECTION_UP
						&& word[i].getY() - word[i].getHeight()  <= 2*wordHight) {
					word[i].getSpeed().toggleYDirection();
				}
			}
			word[i].update(screenHight, screenWidth);
		}
		
		//Log.d(TAG, "update2");
		
		//Log.d(TAG, "finger.getState() =:"+Integer.toString(finger.getState()));
		
		//Log.d(TAG, "update2.1");
		
		
		if (state == EXAMPLE) {
		
			if (finger.getState() == MenuButton.MOVING) {
				if (finger.getY() <= finger.getFinishY()) {
					finger.setState(MenuButton.FIND);
				}	
				else 
					finger.update(screenHight, screenWidth);

			}
			
			//Log.d(TAG, "update3");
			
			if (finger.getState() == MenuButton.FIND) 		
				finger.find(screenHight, screenWidth, word[0].getX(), word[0].getY());
			
			//Log.d(TAG, "update4");
			
			if (finger.getState() == MenuButton.DRAG) {		
				word[0].setX((int)finger.getX());
				word[0].setY((int)finger.getY()+word[0].getHeight());
				word[0].setTouched(true);
				finger.getSpeed().moveDownDirection();
				finger.update(screenHight, screenWidth);

			
				if (word[0].getY() + word[0].getHeight()/2 >= getHeight()-wordHight) {
					finger.setState(MenuButton.DROP);
					wordFound = true;
					words_left--;
					word[0].setIsStopped(true);
					//word[i].setState(Word.STOPED);
					word[0].setX(screenWidth/2 - word[0].getWidth()/2);
					word[0].setY(screenHight-wordHight/2);
					word[0].getSpeed().moveRightDirection();
					word[0].setColor(Color.YELLOW);
			}
			}
			if (word[0].isStopped())
				word[0].update(screenHight, screenWidth);

		}
		
		if (word[0].getIsStopped() == true)
			if (word[0].getX() >= getWidth()) {
				word[0].setState(Word.STOPED);
				//word[0].setIsStopped(false);
				if (state == EXAMPLE)
					state = THREETOGO;
			}
		
		
	}
	
	public void upDateTimer(String secs) {
		secs_left = secs;
		
	}
	
	
	public boolean correctWord(Word word) {
		Boolean result = false;
		for (int i=0; i < correct_word.length; i++) {
			if (word.getWord().equals(correct_word[i]))  return true;
		}
		return result;
	}
	
	public String getMainWord(String id) {

        try {     
        	db.openDataBase();     
        }catch(SQLException sqle){     
        	throw sqle;     
        }

		String word = db.getWord(id);

        db.close(); 

		return word;
	}
	
	public String[] getSynonyms(String id) {
		String word_syn[] = new String[4];
        try {     
        	db.openDataBase();     
        }catch(SQLException sqle){     
        	throw sqle;     
        }

        word_syn = db.getSyn(id);
        

        db.close(); 
		return word_syn;
		
	}
	public String[] getExtraWords(String[] syn) {
		String extra[] = new String[4];
		//String extra_word[] = new String[4];
        try {     
        	db.openDataBase();     
        }catch(SQLException sqle){     
        	throw sqle;     
        }
   
        extra = db.getExtra(syn);
                  
        db.close();    	
		return extra;		
	}


}
