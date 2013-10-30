package com.powerblock.inspiriappbeta;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Fragment3 extends Fragment {
	
	private TextView timerTextView;
	private FragmentActivity context;
	private Button startButton;
	private Button infoButton;
	private RelativeLayout layout;
	private float displayFloatDimen;
	private float messageTextViewMargin;
	private float infoButtonDimen;
	private Resources res;
	private Drawable infoButtonDrawable;
	private CountDownTimerWithPause timer;
	private Button mPauseButton;
	private LinearLayout mLinearLayout;
	private Drawable timerBackground;
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressWarnings("deprecation")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container,savedInstanceState);
		
		res = getResources();
		displayFloatDimen = res.getDimension(R.dimen.timer_display_size);
		messageTextViewMargin = res.getDimension(R.dimen.message_margin_dimen);
		infoButtonDimen = res.getDimension(R.dimen.infoButtonSize);
		infoButtonDrawable = res.getDrawable(android.R.drawable.ic_menu_info_details);
		timerBackground = res.getDrawable(R.drawable.clock_background_shape);
		
		//Set up relativeLayout Params
		RelativeLayout.LayoutParams timerTextViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		timerTextViewLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		timerTextViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		
		RelativeLayout.LayoutParams linearLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		linearLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		linearLayoutParams.addRule(RelativeLayout.BELOW, 101);
		
		RelativeLayout.LayoutParams pauseAndStartButtonLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		pauseAndStartButtonLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		
		RelativeLayout.LayoutParams infoButtonLayoutParams = new RelativeLayout.LayoutParams((int) infoButtonDimen, (int) infoButtonDimen);
		infoButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		infoButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
		RelativeLayout.LayoutParams messageTextViewLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		messageTextViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		messageTextViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		messageTextViewLayoutParams.setMargins(0, (int) messageTextViewMargin, 0, 0);
		//Set up the linear layout
		mLinearLayout = new LinearLayout(context);
		mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		mLinearLayout.setLayoutParams(linearLayoutParams);
		//Set up timer textView
		timerTextView = new TextView(context);
		timerTextView.setLayoutParams(timerTextViewLayoutParams);
		timerTextView.setId(101);
		timerTextView.setTextSize(displayFloatDimen);
		timerTextView.setText("02:00");

		//Set up startButton
		startButton = new Button(context);
		startButton.setLayoutParams(pauseAndStartButtonLayoutParams);
		startButton.setId(102);
		startButton.setText("Start");
		startButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
					countdownStart();
					timerTextView.setText("01:59");
				}
			}
		);
		//Set up pause button
		mPauseButton = new Button(context);
		mPauseButton.setLayoutParams(pauseAndStartButtonLayoutParams);
		mPauseButton.setText("Pause");
		mPauseButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				countdownPause();
			}
		});
		//set up infoButton
		infoButton = new Button(context);
		infoButton.setLayoutParams(infoButtonLayoutParams);
		infoButton.setBackgroundDrawable(infoButtonDrawable);
		infoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				infoButtonHandler();
			}
		});
		
		//add buttons to layout
		mLinearLayout.addView(startButton);
		mLinearLayout.addView(mPauseButton);
		
		//Set up relative Layout
		layout = new RelativeLayout(context);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		layout.addView(timerTextView);
		layout.addView(mLinearLayout);
		layout.addView(infoButton);
		layout.setBackgroundColor(00000000);
		
		//Deal with different API's
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
			timerTextView.setBackgroundDrawable(timerBackground);
		} else {
			timerTextView.setBackground(timerBackground);
		}
		
		timer = new CountDownTimerWithPause(120000, 1000, false){
			@Override
			public void onFinish() {
				timerTextView.setText("00:00");
			}

			@SuppressLint("DefaultLocale")
			@Override
			public void onTick(long millisUntilFinished) {
				int seconds = (int) (millisUntilFinished / 1000);
				String timeString = String.format("%02d:%02d", seconds / 60, seconds % 60);
				timerTextView.setText(timeString);
			}
			
		}.create();		
		return layout;
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		this.context = (FragmentActivity) activity;
		
	}
	
	public void infoButtonHandler(){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("This is used to give you time to reflect about your life " +
				"and what you could do to improve it, find a comfortable place to sit or lie down and press start to begin");
		builder.setTitle("Welcome to the timer section");
		builder.setNegativeButton("Ok", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
			
		}).create().show();
	}
	
	public void countdownStart(){
		if(!timer.isRunning() && !timer.isPaused()){
			timer.resume();
		} else if(timer.isPaused()){
			timer.resume();
		}
	}
	
	public void countdownPause(){
		if(timer.isRunning() && !timer.isPaused()){
			timer.pause();
		}
	}
}
