package com.powerblock.inspiriappbeta;

public class Quote {
	
	private String mQuote;
	private String mName;
	
	public Quote(String quote, String name){
		mName = name;
		mQuote = quote;
	}
	
	public String getName(){
		if(!mName.equals(null)){
			return mName;
		} else {
			return "Error please report";
		}
	}
	
	public void setName(String newName){
		if(!newName.equals(null)){
			mName = newName;
		} else {
			mName = "John Doe";
		}
	}
	
	public String getQuote(){
		if(!mQuote.equals(null)){
			return mQuote;
		} else {
			return "Error Please report";
		}
	}
	
	public void setQuote(String newQuote){
		if(!newQuote.equals(null)){
			mQuote = newQuote;
		} else { 
			mQuote = "Error please report";
		}
	}

}
