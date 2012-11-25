package com.appsolve.worddrag;


import java.io.IOException;


import android.app.Activity;
import android.content.Context;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.appsolve.worddrag.R;

public class GrabwordActivity extends Activity {
	public static final String DEBUG_TAG = "GrabWordLog";
	DataBaseHelper db = new DataBaseHelper(this);
	final Context context = this;
	TextView wordView;
	TextView[] synView = new TextView[4];
	TextView[] extraView = new TextView[4];
	String word = "";
	String syn[] = new String[4];
	String extra[] = new String[4];
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
               
        wordView = (TextView) findViewById(R.id.master_word);
        
        synView[0] = (TextView) findViewById(R.id.syn1);
        synView[1] = (TextView) findViewById(R.id.syn2);
        synView[2] = (TextView) findViewById(R.id.syn3);
        synView[3] = (TextView) findViewById(R.id.syn4);
        
        extraView[0] = (TextView) findViewById(R.id.extra1);
        extraView[1] = (TextView) findViewById(R.id.extra2);
        extraView[2] = (TextView) findViewById(R.id.extra3);
        extraView[3] = (TextView) findViewById(R.id.extra4);  
        
        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(mAddListener);
    }

    
    // Create an anonymous implementation of OnClickListener
    private OnClickListener mAddListener = new OnClickListener()
    {
    	public void onClick(View v)	{
           
            try {            	 
            	db.createDataBase();     
            } catch (IOException ioe) {     
            	throw new Error("Unable to create database");     
            }     
            try {     
            	db.openDataBase();     
            }catch(SQLException sqle){     
            	throw sqle;     
            }
                             
            //Displays the master word
            word = db.getRandomEntry();
            wordView.setText(word);                   
            syn = db.getSyn(word);            
            for (int i=0;i<4;i++)
            	synView[i].setText(syn[i]); 
                        
            extra = db.getExtra(syn);
            for (int i=0;i<4;i++)
            	extraView[i].setText(extra[i]);            
            
            db.close();      		
		}
    };    
}