package com.powerblock.inspiriappbeta.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.powerblock.inspiriappbeta.DatabaseHandler;
import com.powerblock.inspiriappbeta.R;
import com.powerblock.inspiriappbeta.Wish;

public class Fragment4 extends Fragment {
	
	public static final int SLOTS_LIMIT = 50;
	public static int WishlistCounter;
	private DatabaseHandler databaseHandler;
	private FragmentActivity context;
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment4_layout, container, false);
		v.setBackgroundColor(00000000);
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
	
	
	public static void WishlistCounterSubtract(){
		WishlistCounter--;
	}
	
	
	public void deleteAll(View v){
		databaseHandler.deleteAllWishes();
	}

	public int getPos(Wish wish) {
		int pos = (int) wish.getId();
		return pos;
	}

	public Boolean isLast(Wish wish) {
		int pos = getPos(wish);
		int size = databaseHandler.getWishCount();
		if(pos == size){
			return true;
		} else {
			return false;
		}
	}
}
