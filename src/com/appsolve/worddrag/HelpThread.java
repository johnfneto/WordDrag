/**
 * 
 */
package com.appsolve.worddrag;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import com.appsolve.worddrag.R;


/**
 * @author John Neto
 *
 * The Main thread which contains the game loop. The thread must have access to 
 * the surface view and holder to trigger events every game tick.
 */
public class HelpThread extends Thread {

	private static final String TAG = "GrabWord";

	// Surface holder that can access the physical surface
	private SurfaceHolder surfaceHolder;
	// The actual view that handles inputs
	// and draws to the surface
	private HelpGamePanel helpGamePanel;

	// flag to hold game state 
	private boolean running;
	public void setRunning(boolean running) {
		this.running = running;
	}

	public HelpThread(SurfaceHolder surfaceHolder, HelpGamePanel helpGamePanel) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.helpGamePanel = helpGamePanel;
	}

	@Override
	public void run() {
		Canvas canvas;
		Log.d(TAG, "Starting game loop");
		
		while (running) {
			canvas = null;
			// try locking the canvas for exclusive pixel editing
			// in the surface
			try {
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {
					// update game state 
					//Log.d(TAG, "before update");
					this.helpGamePanel.update();
					//Log.d(TAG, "inbetween");
					// render state to the screen
					// draws the canvas on the panel
					this.helpGamePanel.render(canvas);	
					//Log.d(TAG, "after render");
				}
			} finally {
				// in case of an exception the surface is not left in 
				// an inconsistent state
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}	// end finally
		}
	}
	
}
