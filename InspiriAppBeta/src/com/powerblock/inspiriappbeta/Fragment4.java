package com.powerblock.inspiriappbeta;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment4 extends Fragment {
	
	public static final int SLOTS_LIMIT = 50;
	public static int WishlistCounter;
	private DatabaseHandler databaseHandler;
	private FragmentActivity context;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment4_layout, container, false);
		return v;
	}
	
	
	@Override
	public void onStart(){
		super.onStart();
		ViewGroup vg = (ViewGroup) context.findViewById(R.id.MainLayout);
		databaseHandler = new DatabaseHandler(context, (ViewGroup) context.findViewById(R.id.MainLayout));
		vg.removeAllViews();
		databaseHandler.reinstantiateAllWishes();
		WishlistCounter = databaseHandler.getWishCount();
		databaseHandler.close();
		Log.v("onStart WishlistCounter: ", String.valueOf(WishlistCounter));
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Log.v("Resume:","called");
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		if(activity != null && !activity.equals(null)){
			this.context = (FragmentActivity) activity;
			Log.v("onAttach", "Called");
		} else {
			Log.v("onAttach","Activty is null");
		}
	}
	
	public void createNew(){
	}
	
	public static void WishlistCounterSubtract(){
		WishlistCounter--;
	}
	
	
	public void deleteAll(View v){
		databaseHandler.deleteAllWishes();
	}
	
	
}
