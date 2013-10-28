package com.powerblock.inspiriappbeta;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Fragment3 extends Fragment {
	
	private TextView timerTextView;
	private TextView messageTextView;
	private FragmentActivity context;
	private Button startButton;
	private Button infoButton;
	private RelativeLayout layout;
	private float displayFloatDimen;
	private float messageTextViewMargin;
	private float infoButtonDimen;
	private Resources res;
	private Drawable infoButtonDrawable;
	private boolean active = false;
	
	@SuppressWarnings("deprecation")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container,savedInstanceState);
		
		
		res = getResources();
		displayFloatDimen = res.getDimension(R.dimen.timer_display_size);
		messageTextViewMargin = res.getDimension(R.dimen.message_margin_dimen);
		infoButtonDimen = res.getDimension(R.dimen.infoButtonSize);
		infoButtonDrawable = res.getDrawable(android.R.drawable.ic_menu_info_details);
		
		//Set up relativeLayout Params
		RelativeLayout.LayoutParams timerTextViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		timerTextViewLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		timerTextViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		
		RelativeLayout.LayoutParams startButtonLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		startButtonLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		startButtonLayoutParams.addRule(RelativeLayout.BELOW, 101);
		
		RelativeLayout.LayoutParams infoButtonLayoutParams = new RelativeLayout.LayoutParams((int) infoButtonDimen, (int) infoButtonDimen);
		infoButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		infoButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
		RelativeLayout.LayoutParams messageTextViewLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		messageTextViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		messageTextViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		messageTextViewLayoutParams.setMargins(0, (int) messageTextViewMargin, 0, 0);
		//Set up timer textView
		timerTextView = new TextView(context);
		timerTextView.setLayoutParams(timerTextViewLayoutParams);
		timerTextView.setId(101);
		timerTextView.setTextSize(displayFloatDimen);
		timerTextView.setText("02:00");
		//Set up the message TextView
		messageTextView = new TextView(context);
		messageTextView.setLayoutParams(messageTextViewLayoutParams);
		messageTextView.setText("Welcome to the timer section. This is used to give you time to reflect about your life and what you could do to improve it, find a comfortable place to sit or lie down and press start to begin");
		
		//Set up startButton
		startButton = new Button(context);
		startButton.setLayoutParams(startButtonLayoutParams);
		startButton.setId(102);
		startButton.setText("Start");
		startButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!active){
					countdownStart();
					active = true;
					timerTextView.setText("01:59");
				}
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
		
		//Set up relative Layout
		layout = new RelativeLayout(context);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		layout.addView(timerTextView);
		layout.addView(startButton);
		layout.addView(infoButton);
		//layout.addView(messageTextView);
		//View v = inflater.inflate(R.layout.fragment3_layout, container, false);
		layout.setBackgroundColor(00000000);
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
		new CountDownTimer(120000, 1000){
			@Override
			public void onFinish() {
				timerTextView.setText("00:00");
				active = false;
			}

			@SuppressLint("DefaultLocale")
			@Override
			public void onTick(long millisUntilFinished) {
				int seconds = (int) (millisUntilFinished / 1000);
				String timeString = String.format("%02d:%02d", seconds / 60, seconds % 60);
				timerTextView.setText(timeString);
			}
			
		}.start();
	}
}
