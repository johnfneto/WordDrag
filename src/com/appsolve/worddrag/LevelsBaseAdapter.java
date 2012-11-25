package com.appsolve.worddrag;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.appsolve.worddrag.R;

public class LevelsBaseAdapter extends BaseAdapter {
	public static final String DEBUG_TAG = "GrabWordLog";
	 private static ArrayList<LevelDetails> levelList;
	 private static LayoutInflater inflater=null;
	 private final Context context;

	 
	 private LayoutInflater l_Inflater;

	 public LevelsBaseAdapter(Context context, ArrayList<LevelDetails> results) {
		this.context = context;
		levelList = results;
		inflater = LayoutInflater.from(context);
	 }

	 public int getCount() {
	  return levelList.size();
	 }

	 public Object getItem(int position) {
	  return levelList.get(position);
	 }

	 public long getItemId(int position) {
	  return position;
	 }

	 public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.level_list, null);
		 //ViewHolder holder;
		
		 /*
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.level_list, parent, false);
		*/
		 
	 
		TextView levelView = (TextView) vi.findViewById(R.id.level);
		ImageView lockView = (ImageView) vi.findViewById(R.id.lock);
		TextView secsView = (TextView) vi.findViewById(R.id.time);		
		ImageView star1View = (ImageView) vi.findViewById(R.id.star1);
		ImageView star2View = (ImageView) vi.findViewById(R.id.star2);
		ImageView star3View = (ImageView) vi.findViewById(R.id.star3);
		
		// If level Locked
		if (levelList.get(position).getLocked()) {
			lockView.setImageResource(R.drawable.lock1);
			levelView.setText("Level "+Integer.toString(position+1));			
		}
		else {
			lockView.setImageResource(R.drawable.blank_lock);
			levelView.setText(levelList.get(position).getWord());
		}
				
		if (levelList.get(position).getSecs() != 0)
			secsView.setText(Integer.toString(levelList.get(position).getSecs())+" secs");		
		
		switch (levelList.get(position).getStars()) {
	        case 0:  
	    		star1View.setImageResource(R.drawable.star_off);
	    		star2View.setImageResource(R.drawable.star_off);
	    		star3View.setImageResource(R.drawable.star_off);    		
	        	break;
	        case 1:  
	    		star1View.setImageResource(R.drawable.star_on);
	    		star2View.setImageResource(R.drawable.star_off);
	    		star3View.setImageResource(R.drawable.star_off);        	
	            break;
	        case 2:  
	    		star1View.setImageResource(R.drawable.star_on);
	    		star2View.setImageResource(R.drawable.star_on);
	    		star3View.setImageResource(R.drawable.star_off);             	
	            break;
	        case 3:  
	    		star1View.setImageResource(R.drawable.star_on);
	    		star2View.setImageResource(R.drawable.star_on);
	    		star3View.setImageResource(R.drawable.star_on);      
	            break;
	
	        default: 
	        	break;
		}
	  return vi;
	 }
}
