package com.powerblock.inspiriappbeta;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

public class XmlParser {
	
	private Activity mActivity;
	private Resources res;
	
	public XmlParser(Activity activity){
		mActivity = activity;
		res = mActivity.getResources();
	}
	
	public ArrayList<Quote> Parse(int id) throws XmlPullParserException, IOException{
		ArrayList<Quote> list = new ArrayList<Quote>();
		
		XmlResourceParser parser = res.getXml(R.xml.quotes);
		try{
			//parser.setFeature(XmlResourceParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.next();
			parser.require(XmlPullParser.START_DOCUMENT, null, null);
			while(parser.next() != XmlPullParser.END_DOCUMENT){
				if(parser.getEventType() != XmlPullParser.START_TAG){
					continue;
				}
				
				String name = parser.getName();
				Log.v("XmlParser", name);
				if(name.equalsIgnoreCase("Quote")){
					list.add(readEntry(parser));
				}
			}
			Log.v("XmlParser", "finished");
		} finally {
			parser.close();
		}
		return list;
	}
	
	/*private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException{
		
	}*/
	
	private Quote readEntry(XmlResourceParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, null, "Quote");
		String name = parser.getAttributeValue(null, "name");
		String quoteText = parser.nextText();
		Log.v("readEntry", quoteText + " -" + name);
		Quote quote = new Quote(quoteText, name);
		return quote;
	}

}
