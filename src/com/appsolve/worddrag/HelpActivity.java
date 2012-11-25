package com.appsolve.worddrag;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


public class HelpActivity extends Activity {

   
   
	public static final String DEBUG_TAG = "GrabWordLog";
	ImageManager im;
	HelpGamePanel helpGamePanel;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        // requesting to turn the title OFF
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // making it full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // set our MainGamePanel as the View
        helpGamePanel  = new HelpGamePanel(this);
        setContentView(helpGamePanel);
        //setContentView(new MainGamePanel(this, levelNo));
        
        im = new ImageManager(this);
        
        Log.d(DEBUG_TAG, "View added");
        
    }

	@Override
	protected void onDestroy() {
		Log.d(DEBUG_TAG, "Destroying...");
		//im.recycleBitmaps();
		//System.gc();
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		Log.d(DEBUG_TAG, "Stopping...");
		super.onStop();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		helpGamePanel.thread.setRunning(false);
		finish();
	}
	
    
    
}
