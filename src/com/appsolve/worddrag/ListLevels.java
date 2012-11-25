package com.appsolve.worddrag;



import java.io.IOException;
import java.util.ArrayList;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
	import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
	import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import com.appsolve.worddrag.R;

	public class ListLevels extends ListActivity {
		public static final String DEBUG_TAG = "GrabWordLog";
		DataBaseHelper db = new DataBaseHelper(this);
		private static Context context;
		ListView listView;
		
		static final String[] LEVELS = new String[] 
				{"Level 1",
				"Level 2",
				"Level 3", 
				"Level 4", 
				" 5 scrupulous",
				" 6 compulsory",
				"Level 7",
				"Level 8", 
				"Level 9", 
				" 30 flamboyant",
				"Level 11", 		
				"Level 12",
				"Level 13", 
				"Level 14", 
				"Level 15",
				"Level 16",
				"Level 17",
				"Level 18", 
				"Level 19",
				"Level 20",
				"Level 21", 		
				"Level 22",
				"Level 23", 
				"Level 24", 
				"Level 25",
				"Level 26",
				"Level 27",
				"Level 28", 
				"Level 29",			
				"Level 30"};

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.list_main);
			//setListAdapter(new ArrayAdapter<String>(this, R.layout.list_mobile,
			//		R.id.label, MOBILE_OS));

			ListLevels.context = getApplicationContext();
			
	        Button backButton = (Button) findViewById(R.id.backButton);
			
	        // Register a callback to be invoked when button1 is clicked.
	        backButton.setOnClickListener(new View.OnClickListener() {			
				public void onClick(View v) {
					// If button1 is clicked GessMeaning Activity starts
					startActivity(new Intent(ListLevels.this, MenuActivity.class));				
				}
			});		
			
	        
			listView = this.getListView();
			
					
			//setListAdapter(new ListArrayAdapter(this, LEVELS));
			
			ArrayList<LevelDetails> levelList = getDBLevels();
			setListAdapter(new LevelsBaseAdapter(this,levelList));
	
			/*
			setListAdapter(new ArrayAdapter<String>(this,
					R.layout.level_list,
					R.id.label, LEVELS));
			*/
			
			getListView().setDivider(this.getResources().getDrawable(android.R.color.transparent));			
			this.getListView().setCacheColorHint(0);
			getWindow().setBackgroundDrawableResource(R.drawable.cowboy);

		}

		//@Override
		protected void onListItemClick(ListView l, View v, int position, long id) {

			//get selected items
			Log.d(DEBUG_TAG, "position:"+Integer.toString(position));
			//String levelNo = (String) getListAdapter().getItem(position);
			//Log.d(DEBUG_TAG, "levelNo= " + levelNo);
			//Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
			
        	Intent intent = new Intent(ListLevels.this, GameActivity.class);
        	Bundle b = new Bundle();
        	b.putString("level", Integer.toString(position+1)); //Your id
        	intent.putExtras(b); //Put your id to your next Intent
        	startActivity(intent);

		}
		

		
		private ArrayList<LevelDetails> getDBLevels() {
			ArrayList<LevelDetails> results = new ArrayList<LevelDetails>();
			
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
			
            results = db.getLevels();
			
            db.close(); 
			
			return results;
		}
	}
