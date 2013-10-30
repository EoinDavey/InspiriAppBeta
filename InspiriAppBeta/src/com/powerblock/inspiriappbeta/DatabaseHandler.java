package com.powerblock.inspiriappbeta;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ViewGroup;

public class DatabaseHandler extends SQLiteOpenHelper implements WishlistObservable {
	
	private static ArrayList<OnWishlistChangeListener> listeners = new ArrayList<OnWishlistChangeListener>();
	
	//ViewGroup and Context and Layout Inflater for Instantiating Wishes
	private ViewGroup viewGroup;
	private FragmentActivity context;
	
	//Database Version
	private static final int DATABASE_VERSION = 1;
	
	//Database Name
	private static final String DATABASE_NAME = "wishlistManager";
	
	//Database Table Name
	private static final String TABLE_WISHES = "wishlistTable";
	
	//Database Table Column Names
	private static final String WISHLIST_KEY_ID = "id";
	private static final String WISHLIST_KEY_MESSAGE = "Message";
	private static final String WISHLIST_KEY_CHECKED = "Checked";

	public DatabaseHandler(FragmentActivity context, ViewGroup viewGroup) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.viewGroup = viewGroup;
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_WISHLIST_TABLE = "CREATE TABLE " + TABLE_WISHES + "(" + WISHLIST_KEY_ID + " INTEGER PRIMARY KEY,"
			+ WISHLIST_KEY_MESSAGE + " TEXT," + WISHLIST_KEY_CHECKED + " TEXT" + ")";
		/*String CREATE_QUOTES_TABLE = "CREATE TABLE " + TABLE_QUOTES + "(" + QUOTES_KEY_ID + " INTEGER PRIMARY KEY,"
				+ QUOTES_KEY_QUOTE + " TEXT," + QUOTES_KEY_NAME + " TEXT)";*/
		db.execSQL(CREATE_WISHLIST_TABLE);
		//db.execSQL(CREATE_QUOTES_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WISHES);
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUOTES);
		onCreate(db);
	}
	
	/**
	 * Adds wishes to the database
	 * @param Wish the wish to be added
	 * @return the position that it was inserted into the database
	 */
	public long addWish(Wish Wish){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(WISHLIST_KEY_MESSAGE, Wish.getMessage());
		values.put(WISHLIST_KEY_CHECKED, Wish.getChecked());
		
		long i = db.insert(TABLE_WISHES, null, values);
		db.close();
		return i;
	}
	
	//FUCK YOU JESUS
	public void getWish(int id){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_WISHES, new String[] {WISHLIST_KEY_ID, WISHLIST_KEY_MESSAGE, WISHLIST_KEY_CHECKED }, WISHLIST_KEY_ID + " = ?", new String[] {String.valueOf(id)}, null, null, null, null);
		if(cursor != null){
			cursor.moveToFirst();
		}
		
		Wish wish = new Wish(viewGroup, context, this);
		wish.newBuilder(cursor.getString(1));
		if(cursor.getString(2) == "true"){
			wish.setChecked(true);
		} else {
			wish.setChecked(false);
		}
		wish.setId(cursor.getInt(0));
		cursor.close();
		fire();
	}
	
	/**
	 * Returns the number of wishes in the wish list
	 * @return the number of wishes in the wish list
	 */
	public int getWishCount(){
		int i;
		String countQuery = "SELECT  * FROM " + TABLE_WISHES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		i = cursor.getCount();
		cursor.close();
		db.close();
		return i;
	}
	
	public int updateWish(Wish wish){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(WISHLIST_KEY_MESSAGE, wish.getMessage());
		values.put(WISHLIST_KEY_CHECKED, wish.getChecked());
		//updating row
		int i = db.update(TABLE_WISHES, values, WISHLIST_KEY_ID + " = ?", new String[] {String.valueOf(wish.getId())});
		db.close();
		return i;
	}
	
	public void deleteWish(Wish Wish){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_WISHES, WISHLIST_KEY_ID + " = ?", new String[] { String.valueOf(Wish.getId())});
		db.close();
		fire();
	}
	
	public void logAllWishes(){
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT  * FROM " + TABLE_WISHES;
		Cursor cursor = db.rawQuery(selectQuery, null);
		if(cursor.moveToFirst()){
			Log.v("Reading:", "Logging All Wishes");
			do{
				Log.v("Reading:","Id: " + cursor.getString(0) + " Message: " + cursor.getString(1) + " isChecked: " + cursor.getString(2));
			} while(cursor.moveToNext());
		}
		cursor.close();
	}
	
	public void deleteAllWishes(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_WISHES, null, null);
		db.close();
	}
	
	public void reinstantiateAllWishes(){
		 SQLiteDatabase db = this.getReadableDatabase();
		 String selectQuery = "SELECT  * FROM " + TABLE_WISHES;
		 Cursor cursor = db.rawQuery(selectQuery, null);
		 if(cursor.moveToFirst()){
			 do{
				Boolean isCheckedBoolean = false;
				if(cursor.getString(2).equals("true")){
					isCheckedBoolean = true;
				}
				Wish wish = new Wish(viewGroup, context, this);
				wish.updateBuilder(cursor.getString(1), isCheckedBoolean, cursor.getInt(0));
			 } while(cursor.moveToNext());
		 }
		 cursor.close();
		logAllWishes();
		fire();
		db.close();
	 }
	 
	public Boolean isLast(Wish wish){
		 SQLiteDatabase db = this.getReadableDatabase();
		 String selectQuery = "SELECT  * FROM " + TABLE_WISHES;
		 Cursor cursor = db.rawQuery(selectQuery, null);
		 Boolean b = false;
		 if(cursor.moveToLast()){
			Log.v("Id:",String.valueOf(wish.getId())); 
			 if(Integer.parseInt(cursor.getString(0)) == wish.getId()){
				 b = true;
			 } else {
				 b = false;
			 }
		 	}
		 cursor.close();
		 db.close();
		 return b;
	}

	@Override
	public void add(OnWishlistChangeListener listener) {
		listeners.add(listener);
	}

	@Override
	public void remove(OnWishlistChangeListener listener) {
		listeners.remove(listener);
	}
	
	public void fire(){
		Log.v("fire()","firing");
		for(OnWishlistChangeListener listener:listeners){
			listener.wishlistChanged();
		}
	}
	 
}
