package com.appsolve.worddrag;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import com.appsolve.worddrag.R;

public class GameActivity extends Activity {

   
   
	public static final String DEBUG_TAG = "GrabWordLog";
	ImageManager im;
	MainGamePanel mainGamePanel;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle b = getIntent().getExtras();
        String levelNo = b.getString("level");
        Log.d(DEBUG_TAG, "LevelNo:"+levelNo);
        
        // requesting to turn the title OFF
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // making it full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // set our MainGamePanel as the View
        mainGamePanel  = new MainGamePanel(this, levelNo);
        setContentView(mainGamePanel);
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
		mainGamePanel.thread.setRunning(false);
		finish();
	}
	
    
    
}

