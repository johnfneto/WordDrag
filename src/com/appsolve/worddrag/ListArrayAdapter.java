package com.appsolve.worddrag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.appsolve.worddrag.R;

public class ListArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values;

	public ListArrayAdapter(Context context, String[] values) {
		super(context, R.layout.level_list, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.level_list, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.level);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.star1);
		ImageView imageView2 = (ImageView) rowView.findViewById(R.id.lock);
		textView.setText(values[position]);

		// Change icon based on name
		String s = values[position];

		//System.out.println(s);


		imageView.setImageResource(R.drawable.star_on);
		//imageView2.setVisibility(4);
		imageView2.setImageResource(R.drawable.lock1);

		return rowView;
	}
}
