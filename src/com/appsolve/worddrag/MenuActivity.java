package com.appsolve.worddrag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.appsolve.worddrag.R;

//import com.google.ads.*;

public class MenuActivity extends Activity {
	public static final String DEBUG_TAG = "GrabWordLog";
	public static final String MY_AD_UNIT_ID = "1324812";
	//private AdView adView;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        
        
        // Create the adView
        //adView = new AdView(this, AdSize.BANNER, MY_AD_UNIT_ID);
        
        LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout);

        // Add the adView to it
        //layout.addView(adView);

        // Initiate a generic request to load it with an ad
        //adView.loadAd(new AdRequest());        
        
        
        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        
        button1.setOnClickListener(new View.OnClickListener() {
			

			public void onClick(View v) {
				
				startActivity( new Intent( MenuActivity.this, ListLevels.class ) );
				
			}
		});
        
        button2.setOnClickListener(new View.OnClickListener() {
			

			public void onClick(View v) {
				
				startActivity( new Intent( MenuActivity.this, HelpActivity.class ) );
				
			}
		});
        
        button3.setOnClickListener(new View.OnClickListener() {
			

			public void onClick(View v) {

				startActivity( new Intent( MenuActivity.this, MoreActivity.class ) );
				
			}
		});
        
    }
    @Override
    public void onDestroy() {
      //if (adView != null) {
        //adView.destroy();
      //}
      super.onDestroy();
    }
}

