package com.appsolve.worddrag;


import android.content.Context;
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



public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String TAG = "GrabWord";
	
	DataBaseHelper db;
	ImageManager im;
	public MainThread thread;
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
	
	MenuButton restartButton1, menuButton1;
	MenuButton restartButton2, menuButton2, contButton2;

	
	String  level;
	
	
	

	
	public MainGamePanel(Context context, String levelNo) {
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
		
		timeout_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.time_over);
		restart_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.restart);
		menu_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.menu);
		level_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.level);
		complete_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.complete);
		continue_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.continue_button);

		//canvas.drawBitmap(restart_bitmap, getWidth()/2 - restart_bitmap.getWidth()/2, 150 + 3*timeout_bitmap.getHeight()/2, null);

		
		
		level = levelNo;

		// create words and load them
		main_word = getMainWord(levelNo);
		correct_word = getSynonyms(levelNo);
		extra_word = getExtraWords(correct_word);
		
		Log.d(TAG, "Creat Words");
        Log.d(TAG, "LevelNo:"+levelNo);
        

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
		thread = new MainThread(getHolder(), this);
		
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
        	    }
                }     
            @Override
			public void onFinish() {
            	if (!levelComplete)
					for (int i=0; i < word.length; i++) {
						word[i].setState(Word.IMPLODE);
						//Log.d(TAG, "word["+Integer.toString(i)+"]");
						//Log.d(TAG, "word="+word[i].getWord()+"; state="+word[i].getState());
					}
            }  
       }.start();
	

	showInfoTimer = new CountDownTimer(2000, 500) {
        @Override
		public void onTick(long millisUntilFinished) {
        	//Log.d(TAG, " MainGamePanel secs:"+Long.toString(seconds + minutes*60));
            }     
        @Override
		public void onFinish() {
			gameTimer.start();
			words_left=4;
			timeOut = false;
			levelComplete = false;
			showInfo = false;
			// Save the current level score
			//unlock the next level
			//load new word for next level

			for (int j=0; j < word.length; j++)
				word[j].setState(Word.PLAYING);	
					
			// create words and load them
			int i;
			i = Integer.parseInt(level);
			i++;
			level = Integer.toString(i);
			main_word = getMainWord(level);
			correct_word = getSynonyms(level);
			extra_word = getExtraWords(correct_word);
			
			masterWord = new Word(main_word, Color.BLACK, 0 , 15, Speed.DIRECTION_RIGHT, Speed.DIRECTION_DOWN);
			//((GameActivity)getContext()).finish();
			
			word[0].setWord(correct_word[0]);
			word[1].setWord(extra_word[0]);
			word[2].setWord(correct_word[1]);
			word[3].setWord(extra_word[1]);
			word[4].setWord(correct_word[2]);
			word[5].setWord(extra_word[2]);
			word[6].setWord(correct_word[3]);
			word[7].setWord(extra_word[3]);
			
			restartWord(0, 0, 50, Speed.DIRECTION_RIGHT, Speed.DIRECTION_DOWN);
			restartWord(1, 300, 200, Speed.DIRECTION_LEFT, Speed.DIRECTION_UP);
			restartWord(2, 0, 200, Speed.DIRECTION_RIGHT, Speed.DIRECTION_UP);
			restartWord(3, 300, 50, Speed.DIRECTION_LEFT, Speed.DIRECTION_DOWN);
			restartWord(4, 300, 430, Speed.DIRECTION_LEFT, Speed.DIRECTION_UP);
			restartWord(5, 0, 430, Speed.DIRECTION_RIGHT, Speed.DIRECTION_UP);
			restartWord(6, 150, 100, Speed.DIRECTION_LEFT, Speed.DIRECTION_UP);
			restartWord(7, 150, 300, Speed.DIRECTION_RIGHT, Speed.DIRECTION_UP);
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
		thread = new MainThread(getHolder(), this); // line added
		Thread.currentThread().setName("Main Thread");
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
		

		grayTop_resizedBitmap=getResizedBitmap(grayTop_bitmap,2*wordHight,screenWidth);
		redTop_resizedBitmap=getResizedBitmap(redTop_bitmap,2*wordHight,screenWidth);
		base_resizedBitmap=getResizedBitmap(base_bitmap, 2*wordHight,screenWidth);
		
		
		// x, start position, end position
		restartButton1 = new MenuButton(restart_bitmap, getWidth()/2 - restart_bitmap.getWidth()/2, getHeight(),getHeight()/3 + 3*timeout_bitmap.getHeight()/2);
		menuButton1 = new MenuButton(menu_bitmap, getWidth()/2 - menu_bitmap.getWidth()/2, getHeight(),getHeight()/3 + 2*timeout_bitmap.getHeight() + restart_bitmap.getHeight());

		
		//canvas.drawBitmap(continue_bitmap, getWidth()/2 - continue_bitmap.getWidth()/2, 100+2*complete_bitmap.getHeight() + continue_bitmap.getHeight(), null);
		//canvas.drawBitmap(menu_bitmap, getWidth()/2 - menu_bitmap.getWidth()/2, 100+2*complete_bitmap.getHeight() +5*continue_bitmap.getHeight()/2, null);	
		//canvas.drawBitmap(restart_bitmap, getWidth()/2 - restart_bitmap.getWidth()/2, 100+2*complete_bitmap.getHeight() +4*continue_bitmap.getHeight(), null);			
		
		contButton2 = new MenuButton(continue_bitmap, getWidth()/2 - continue_bitmap.getWidth()/2, getHeight(),100+2*complete_bitmap.getHeight() + continue_bitmap.getHeight());	
		menuButton2 = new MenuButton(menu_bitmap, getWidth()/2 - menu_bitmap.getWidth()/2, getHeight(),100+2*complete_bitmap.getHeight() +5*continue_bitmap.getHeight()/2);	
		restartButton2 = new MenuButton(restart_bitmap, getWidth()/2 - restart_bitmap.getWidth()/2, getHeight(),100+2*complete_bitmap.getHeight() +4*continue_bitmap.getHeight());
	
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
		Context context = getContext();
		
		if (timeOut || gone) {  //TIME OUT   
			if (menuButton1.getState() == MenuButton.STOPED)
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (event.getX() >= getWidth()/2 - restart_bitmap.getWidth()/2 && event.getX() <= getWidth()/2 + restart_bitmap.getWidth()/2 ) {
					if (event.getY() >= getHeight()/3 + 3*timeout_bitmap.getHeight()/2 && event.getY() <= getHeight()/3 + 3*timeout_bitmap.getHeight()/2 + restart_bitmap.getHeight() ) {
						Log.d(TAG, "Restart touched!");
						gameTimer.start();
						words_left=4;
						timeOut = false;
						gone = false;
						
						for (int j=0; j < word.length; j++)
							word[j].setState(Word.PLAYING);	

						restartWord(0, 0, 30, Speed.DIRECTION_RIGHT, Speed.DIRECTION_DOWN);
						restartWord(1, 300, 200, Speed.DIRECTION_LEFT, Speed.DIRECTION_UP);
						restartWord(2, 0, 200, Speed.DIRECTION_RIGHT, Speed.DIRECTION_UP);
						restartWord(3, 300, 30, Speed.DIRECTION_LEFT, Speed.DIRECTION_DOWN);
						restartWord(4, 300, 430, Speed.DIRECTION_LEFT, Speed.DIRECTION_UP);
						restartWord(5, 0, 430, Speed.DIRECTION_RIGHT, Speed.DIRECTION_UP);
						restartWord(6, 150, 100, Speed.DIRECTION_LEFT, Speed.DIRECTION_UP);
						restartWord(7, 150, 300, Speed.DIRECTION_RIGHT, Speed.DIRECTION_UP);
						
						restartButton1.setState(MenuButton.HIDDEN);
						menuButton1.setState(MenuButton.HIDDEN);
						restartButton1.setY(getHeight());
						menuButton1.setY(getHeight());
					}	
				}  // getWidth()/2 - menu_bitmap.getWidth()/2, 150 + 2*timeout_bitmap.getHeight() + restart_bitmap.getHeight()
				if (event.getX() >= getWidth()/2 - menu_bitmap.getWidth()/2 && event.getX() <= getWidth()/2 + menu_bitmap.getWidth()/2) {
					if (event.getY() >= getHeight()/3 + 2*timeout_bitmap.getHeight() + restart_bitmap.getHeight() && event.getY() <= getHeight()/3 + 2*timeout_bitmap.getHeight() + restart_bitmap.getHeight() +  menu_bitmap.getHeight()) {
						Log.d(TAG, "Menu touched!");
						((GameActivity)getContext()).finish();
					}
				}
			}						
		}

		
		if (levelComplete) {  // LEVEL COMPLETE getWidth()/2 - restart_bitmap.getWidth()/2, 100+2*complete_bitmap.getHeight() +7*continue_bitmap.getHeight()/2
			if (restartButton2.getState() == MenuButton.STOPED)
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (event.getX() >= getWidth()/2 - restart_bitmap.getWidth()/2 && event.getX() <= getWidth()/2 + restart_bitmap.getWidth()/2 ) {
					if (event.getY() >= 100+2*complete_bitmap.getHeight() +4*continue_bitmap.getHeight() && event.getY() <= 100+2*complete_bitmap.getHeight() +4*continue_bitmap.getHeight() +  restart_bitmap.getHeight()) {
						Log.d(TAG, "Restart touched!");
						gameTimer.start();
						words_left=4;
						timeOut = false;
						levelComplete = false;
						for (int j=0; j < word.length; j++)
							word[j].setState(Word.PLAYING);	

						restartWord(0, 0, 50, Speed.DIRECTION_RIGHT, Speed.DIRECTION_DOWN);
						restartWord(1, 300, 200, Speed.DIRECTION_LEFT, Speed.DIRECTION_UP);
						restartWord(2, 0, 200, Speed.DIRECTION_RIGHT, Speed.DIRECTION_UP);
						restartWord(3, 300, 50, Speed.DIRECTION_LEFT, Speed.DIRECTION_DOWN);
						restartWord(4, 300, 430, Speed.DIRECTION_LEFT, Speed.DIRECTION_UP);
						restartWord(5, 0, 430, Speed.DIRECTION_RIGHT, Speed.DIRECTION_UP);
						restartWord(6, 150, 100, Speed.DIRECTION_LEFT, Speed.DIRECTION_UP);
						restartWord(7, 150, 300, Speed.DIRECTION_RIGHT, Speed.DIRECTION_UP);
						
						contButton2.setState(MenuButton.HIDDEN);
						menuButton2.setState(MenuButton.HIDDEN);
						restartButton2.setState(MenuButton.HIDDEN);
						contButton2.setY(getHeight());
						menuButton2.setY(getHeight());
						restartButton2.setY(getHeight());
					}	
				}  // getWidth()/2 - menu_bitmap.getWidth()/2, 100+2*complete_bitmap.getHeight() +5*continue_bitmap.getHeight()/2
				if (event.getX() >= getWidth()/2 - menu_bitmap.getWidth()/2 && event.getX() <= getWidth()/2 + menu_bitmap.getWidth()/2) {
					if (event.getY() >= 100+2*complete_bitmap.getHeight() +5*continue_bitmap.getHeight()/2 && event.getY() <= 100+2*complete_bitmap.getHeight() +5*continue_bitmap.getHeight()/2 + menu_bitmap.getHeight() ) {
						Log.d(TAG, "Menu touched!");
						((GameActivity)getContext()).finish();
					}
				}  //getWidth()/2 - continue_bitmap.getWidth()/2, 100+2*complete_bitmap.getHeight() + continue_bitmap.getHeight()
				if (event.getX() >= getWidth()/2 - continue_bitmap.getWidth()/2 && event.getX() <= getWidth()/2 + continue_bitmap.getWidth()/2) {
					if (event.getY() >= 100+2*complete_bitmap.getHeight() + continue_bitmap.getHeight() && event.getY() <= 100+2*complete_bitmap.getHeight() + continue_bitmap.getHeight() + complete_bitmap.getHeight()) {
						Log.d(TAG, "Next Level!");
						levelComplete = false;
						showInfoTimer.start();
						showInfo = true;
						
						contButton2.setState(MenuButton.HIDDEN);
						menuButton2.setState(MenuButton.HIDDEN);
						restartButton2.setState(MenuButton.HIDDEN);
						contButton2.setY(getHeight());
						menuButton2.setY(getHeight());
						restartButton2.setY(getHeight());
					}
				}				
			}						
		}		
		
		
		//  Process Touch events
		for (int i=0; i < word.length; i++) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				// delegating event handling to the droid
				if (!dragging)
					word[i].handleActionDown((int)event.getX(), (int)event.getY());
					if (word[i].isTouched()) {
						dragging = true;
						whichDragging = i;
					}

			} if (event.getAction() == MotionEvent.ACTION_MOVE) {
				// the word is moving
				if (word[i].isTouched()) {
					// the droid was picked up and is being dragged
					word[i].setX((int)event.getX());
					word[i].setY((int)event.getY());
				}
			} if (event.getAction() == MotionEvent.ACTION_UP) {
				// touch was released
				if (word[i].isTouched()) {
					word[i].setTouched(false);
					dragging = false;
					    // If the word is droped in the box 
					if (word[i].getY() + word[i].getHeight()/2 >= getHeight()-20) {
						if (correctWord(word[i])) {
							wordFound = true;
							words_left--;
							word[i].setIsStopped(true);
							//word[i].setState(Word.STOPED);
							word[i].setX(screenWidth/2 - word[i].getWidth()/2);
							word[i].setY(screenHight-wordHight/2);
							word[i].getSpeed().moveRightDirection();
							word[i].setColor(Color.YELLOW);
							
							MediaPlayer mp = MediaPlayer.create(context, R.raw.gong1);	
							mp.start();
							
							if (words_left == 0) {
								levelComplete = true;
								gameTimer.cancel();
							}
						}
						else {
							word[i].setY(screenHight - 2*wordHight);
							word[i].changeSpeed(2,2);

							MediaPlayer mp = MediaPlayer.create(context, R.raw.wrong);
							mp.start();
						}
					}
				}
			}
		}
		
	    try {
	        Thread.sleep(16);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
		return true;
		
	}

	
	public void restartWord(int i, int x, int y, int xDirection, int yDirection) {
		word[i].setX(x);
		word[i].setY(y);
		word[i].getSpeed().setxDirection(xDirection);
		word[i].getSpeed().setyDirection(yDirection);
		word[i].setIsStopped(false);
		word[i].setIsInCenter(false);
		word[i].setIsGone(false);
		word[i].setColor(Color.RED);
		
		//Log.d(TAG, "restartWord");
		
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
		
		canvas.drawBitmap(grayTop_resizedBitmap, 0, 0, null);
		canvas.drawBitmap(redTop_resizedBitmap, 0, 0, null);
		canvas.drawBitmap(base_resizedBitmap, 0, screenHight - 2*wordHight, null);
		
		
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
		  canvas.drawText(secs_left, a , (2*wordHight-3*bounds.height()/2)/2 + bounds.height()/2+5*bounds.height()/3 , paint);

		  
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
		  
		  
		  if (deciSecs_left <= 60)	// Words will Flash White and Red		
			if (deciSecs_left != temp_secs) {
				temp_secs = (int) deciSecs_left;
				for (int i=0; i < word.length; i++) {
					if (word[i].getColor() == Color.WHITE)
						word[i].setColor(Color.RED);
					else
						word[i].setColor(Color.WHITE);					
				}
			}
			

		  if (!levelComplete && !showInfo)
				for (int i=0; i < word.length; i++) {
					word[i].draw(canvas, context);
					  if (word[i].getIsStopped()) {
						  canvas.drawBitmap(getResizedBitmap(half_base,2*wordHight, screenWidth*1/4), screenWidth*3/4,screenHight - 2*wordHight, null);
					  }	
				}
		  
		if (gone) {  // TIME OVER
			canvas.drawBitmap(timeout_bitmap, getWidth()/2 - timeout_bitmap.getWidth()/2, getHeight()/4, null);
			//canvas.drawBitmap(restart_bitmap, getWidth()/2 - restart_bitmap.getWidth()/2, 150 + 3*timeout_bitmap.getHeight()/2, null);
			//canvas.drawBitmap(menu_bitmap, getWidth()/2 - menu_bitmap.getWidth()/2, 150 + 2*timeout_bitmap.getHeight() + restart_bitmap.getHeight(), null);			
			restartButton1.setState(MenuButton.MOVING);
			
			
			
		}
		  if (levelComplete) {  // LEVEL COMPLETE
			canvas.drawBitmap(level_bitmap, getWidth()/2 - level_bitmap.getWidth()/2, 100, null);
			canvas.drawBitmap(complete_bitmap, getWidth()/2 - complete_bitmap.getWidth()/2, 100+ 3*complete_bitmap.getHeight()/2, null);
			
			contButton2.setState(MenuButton.MOVING);
			
			//canvas.drawBitmap(continue_bitmap, getWidth()/2 - continue_bitmap.getWidth()/2, 100+2*complete_bitmap.getHeight() + continue_bitmap.getHeight(), null);
			//canvas.drawBitmap(menu_bitmap, getWidth()/2 - menu_bitmap.getWidth()/2, 100+2*complete_bitmap.getHeight() +5*continue_bitmap.getHeight()/2, null);	
			//canvas.drawBitmap(restart_bitmap, getWidth()/2 - restart_bitmap.getWidth()/2, 100+2*complete_bitmap.getHeight() +4*continue_bitmap.getHeight(), null);							  
		  }
		  if (showInfo) {
			  // Display LEVEL No
			  paint.setColor(Color.WHITE);
			  // text size in pixels
			  paint.setTextSize((int)(30*scale));
			  //paint.setTextSize(30);
			  paint.setFakeBoldText(true);
			  // text shadow
			  paint.setShadowLayer(1f, 0f, 1f, Color.BLUE);
			  int i = Integer.parseInt(level);
			  i++;
			  String str = Integer.toString(i);
			  int textWidth = (int) paint.measureText ("LEVEL  ");
			  //Log.d(TAG, " textWidth:"+Integer.toString(textWidth));
			  canvas.drawText("LEVEL "+str, getWidth()/2 - textWidth/2 , 150 , paint); 
		  }			  		  
		  masterWord.draw(canvas, context);
		  
		  restartButton1.draw(canvas, context);
		  menuButton1.draw(canvas, context);
		  
		  restartButton2.draw(canvas, context);
		  menuButton2.draw(canvas, context);		  
		  contButton2.draw(canvas, context);		  
	}
	
				/*--------------------- update --------------------------*/

	/**
	 * This is the game update method. It iterates through all the objects
	 * and calls their update method if they have one or calls specific
	 * engine's update method.
	 */
	public void update() {
		int words_inCenter = 0;
		int words_gone = 0;
		//Log.d(TAG, "update!!!");
		masterWord.setX(getWidth()/2 - masterWord.getWidth()/2);
		masterWord.setY(2*wordHight - wordHight/2 );
		//masterWord.setY(2*wordHight - wordHight/3 );
		//Log.d(TAG, "wordHight="+wordHight);
		//Log.d(TAG, "masterWord.getHight()="+masterWord.getHeight());
		//Log.d(TAG, "update 2!!!");
		for (int i=0; i < word.length; i++) {
			
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
			if (word[i].getIsInCenter())
				words_inCenter++;

			if (words_inCenter == 8) {
				for (int j=0; j < word.length; j++) {
					word[j].setState(Word.EXPLODE);
					word[j].setColor(Color.WHITE);
					switch (j) {
					case 0:
						word[j].getSpeed().setxDirection(Speed.DIRECTION_NULL);
						word[j].getSpeed().setyDirection(Speed.DIRECTION_UP);
						break;
					case 1:
						word[j].getSpeed().setxDirection(Speed.DIRECTION_RIGHT);
						word[j].getSpeed().setyDirection(Speed.DIRECTION_UP);
						break;
					case 2:
						word[j].getSpeed().setxDirection(Speed.DIRECTION_RIGHT);
						word[j].getSpeed().setyDirection(Speed.DIRECTION_NULL);
						break;						
					case 3:
						word[j].getSpeed().setxDirection(Speed.DIRECTION_RIGHT);
						word[j].getSpeed().setyDirection(Speed.DIRECTION_DOWN);
						break;						
					case 4:
						word[j].getSpeed().setxDirection(Speed.DIRECTION_NULL);
						word[j].getSpeed().setyDirection(Speed.DIRECTION_DOWN);
						break;						
					case 5:
						word[j].getSpeed().setxDirection(Speed.DIRECTION_LEFT);
						word[j].getSpeed().setyDirection(Speed.DIRECTION_DOWN);
						break;						
					case 6:
						word[j].getSpeed().setxDirection(Speed.DIRECTION_LEFT);
						word[j].getSpeed().setyDirection(Speed.DIRECTION_NULL);
						break;
					case 7:
						word[j].getSpeed().setxDirection(Speed.DIRECTION_LEFT);
						word[j].getSpeed().setyDirection(Speed.DIRECTION_UP);
						break;			
					default:
						
						break;
					}
					//Log.d(TAG, "Set EXPLODE!");
				}
				words_inCenter = 0;
			}
			
			if (word[i].getIsGone()) {
				words_gone++;
				//Log.d(TAG, "gone! "+word[i].getWord());
			}
			if (words_gone == 8) {
				gone = true;
				//Log.d(TAG, "gone!");
			}
			if (word[i].getIsStopped() == true)
				if (word[i].getX() >= getWidth() + word[i].getWidth()) {
					word[i].setState(Word.STOPED);
					word[i].setIsStopped(false);
				}
				
			if (!levelComplete && !showInfo && !gone)
			
			// Update each word
			word[i].update(screenHight, screenWidth);
		}
		//Log.d(TAG, "update 3!!!");
		
		if (restartButton1 != null)
			//Log.d(TAG, "restartButton1 == null");
			// If restartButton is moving and reached its final position Stop it and Start menuButton
		if (restartButton1.getState() == MenuButton.MOVING) {
			//Log.d(TAG, "update 3 (if)!!");
			if (restartButton1.getY() <= restartButton1.getFinishY()) {
				//Log.d(TAG, "update 3 (if)!!!");
				restartButton1.setState(MenuButton.STOPED);
				menuButton1.setState(MenuButton.MOVING);
			}
			else // If not update it
				//Log.d(TAG, "update 3 (else)!!!");
				restartButton1.update(screenHight, screenWidth);
		}
		
		//Log.d(TAG, "update 4!!!");
		if (menuButton1 != null)
		// If menuButton is moving and reached its final position Stop it		
		if (menuButton1.getState() == MenuButton.MOVING) {
			if (menuButton1.getY() <= menuButton1.getFinishY()) {
				menuButton1.setState(MenuButton.STOPED);
			}
			else // If not update it
				menuButton1.update(screenHight, screenWidth);	
		}

		//Log.d(TAG, "update 5!!!");
		if (contButton2 != null)
		// If contButton is moving and reached its final position Stop it and Start menuButton
	if (contButton2.getState() == MenuButton.MOVING) {
		if (contButton2.getY() <= contButton2.getFinishY()) {
			contButton2.setState(MenuButton.STOPED);
			menuButton2.setState(MenuButton.MOVING);
		}
		else // If not update it
			contButton2.update(screenHight, screenWidth);	
	}		
		
		if (menuButton2 != null)
		// If menuButton is moving and reached its final position Stop it and Start restartButton
	if (menuButton2.getState() == MenuButton.MOVING) {
		if (menuButton2.getY() <= menuButton2.getFinishY()) {
			menuButton2.setState(MenuButton.STOPED);
			restartButton2.setState(MenuButton.MOVING);
		}
		else // If not update it
			menuButton2.update(screenHight, screenWidth);	
	}
		
	//Log.d(TAG, "update 6!!!");
	if (restartButton2 != null)
	// If menuButton is moving and reached its final position Stop it		
	if (restartButton2.getState() == MenuButton.MOVING) {
		if (restartButton2.getY() <= restartButton2.getFinishY()) {
			restartButton2.setState(MenuButton.STOPED);
		}
		else // If not update it
			restartButton2.update(screenHight, screenWidth);	
	}		
		
	//Log.d(TAG, "update 7!!!");
		
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
