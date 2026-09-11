package com.aditya1962.RealTimeHomeTheftDetect.theftdetection;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class CustomList extends ArrayAdapter<String>{
 
private final Activity context;
private final String[] web;
private final Integer[] imageId;
public CustomList(Activity context, String[] web, Integer[] imageId) {
	
	super(context, com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.layout.list_single, web);
	this.context = context;
	this.web = web;
	this.imageId = imageId;
 
}

@Override
public View getView(int position, View view, ViewGroup parent) {

		LayoutInflater inflater = context.getLayoutInflater();
		View rowView= inflater.inflate(com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.layout.list_single, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.id.txt);
		 
		ImageView imageView = (ImageView) rowView.findViewById(com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.id.img);
		txtTitle.setText(web[position]);
		 
		imageView.setImageResource(imageId[position]);
		return rowView;

	}
}