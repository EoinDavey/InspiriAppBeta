package com.powerblock.inspiriappbeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Fragment1 extends Fragment {
	
	private Activity mActivity;
	private Button mButton;
	private TextView mQuoteTextView;
	private TextView mNameTextView;
	private View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			pickQuote();
		}
	};
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment1_layout, container, false);
		mButton =(Button) v.findViewById(R.id.QuoteRefreshButton);
		mButton.setOnClickListener(listener);
		v.setBackgroundColor(00000000);
		return v;
	}
	
	@Override
	public void onStart(){
		super.onStart();
		mQuoteTextView = (TextView) mActivity.findViewById(R.id.Fragment1QuoteTextView);
		mNameTextView = (TextView) mActivity.findViewById(R.id.Fragment1NameTextView);
		mButton = (Button) mActivity.findViewById(R.id.QuoteRefreshButton);
		Log.v("mButton", "setting on click listener");
		//mButton.setOnClickListener(listener);
		pickQuote();
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		mActivity = activity;
	}
	
	private ArrayList<Quote> performParse(){
		XmlParser parser = new XmlParser(mActivity);
		ArrayList<Quote> list = new ArrayList<Quote>();
		try {
			list = parser.Parse(R.xml.quotes);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	private void pickQuote(){
		ArrayList<Quote> list = new ArrayList<Quote>();
		Random randomGen = new Random();
		list = performParse();
		int randomIndex = randomGen.nextInt(getQuoteCount(list));
		Quote quote = list.get(randomIndex);
		mQuoteTextView.setText(quote.getQuote());
		mNameTextView.setText("-" + quote.getName());
		Log.v("Final Quote", quote.getQuote() + " -" + quote.getName());
	}
	
	private int getQuoteCount(ArrayList<Quote> list){
		return list.size();
	}

}
