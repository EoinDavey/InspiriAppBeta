package com.powerblock.inspiriappbeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.powerblock.inspiriappbeta.fragments.Fragment1;
import com.powerblock.inspiriappbeta.fragments.Fragment2;
import com.powerblock.inspiriappbeta.fragments.Fragment3;
import com.powerblock.inspiriappbeta.fragments.Fragment4;

public class MainActivity extends SherlockFragmentActivity implements
com.actionbarsherlock.app.ActionBar.TabListener, WishlistObservable {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	/**
	 * The {@link android.support.v4.Fragment} for the
	 * Wishlist fragment
	 */
	Fragment4 fragment4;
	
	final Context context = this;
	
	private ImageView mImageView;
		
	ContentValues values = new ContentValues();
	private ArrayList<OnWishlistChangeListener> listeners = new ArrayList<OnWishlistChangeListener>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.v("Main Activity","onCreate");
		
		// Set up the action bar.
		final com.actionbarsherlock.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_TABS);
		//actionBar.setDisplayShowHomeEnabled(false);
		//actionBar.setDisplayShowTitleEnabled(false);
		//Set up the Fragment 4
		fragment4 = new Fragment4();
		
		mImageView = (ImageView) findViewById(R.id.mainActivityBackground);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener((TabListener) this));
		}
		getBackgroundImage(1);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);
		if(item.getItemId() == R.id.item_action_add){
			Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, 0);
		} else if(item.getItemId() == R.id.item_action_clear){
			ClearBackground();
			Toast.makeText(this, "Background Cleared", Toast.LENGTH_LONG).show();
		}
		return true;
	}
	
	public void ClearBackground(){
		mImageView.setImageResource(android.R.color.transparent);
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/.InspiriAppBackground");
		File file = new File(myDir,ImageEditor.BACKGROUND_FILE_NAME);
		if(file.exists()){
			file.delete();
		}
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void onActivityResult(int requestcode, int resultcode, Intent intent){
		super.onActivityResult(requestcode, resultcode, intent);
		if(resultcode == RESULT_OK){
			switch(requestcode){
			case 0:
				imageEditorOpen(intent);
				break;
			case 1:
				getBackgroundImage(0);
				break;
			}
		}
	}
	
	private void imageEditorOpen(Intent intent){
		Uri targetUri = intent.getData();
		String uriString = targetUri.toString();
		Intent imageEditIntent = new Intent(this, ImageEditor.class);
		imageEditIntent.putExtra(ImageEditor.IMAGE_LOC_TAG, uriString);
		startActivityForResult(imageEditIntent, ImageEditor.REQUEST_CODE);
	}
	
	public void getBackgroundImage(int i){
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/.InspiriAppBackground");
		File file = new File(myDir, ImageEditor.BACKGROUND_FILE_NAME);
		if(!file.exists() && i == 0){
			Toast.makeText(this, "Error while retrieving image", Toast.LENGTH_LONG).show();
			return;
		} else if(!file.exists() && i == 1){
			return;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(root + "/.InspiriAppBackground/"+ImageEditor.BACKGROUND_FILE_NAME, options);
		mImageView.setImageBitmap(bitmap);
	}

	public void onTabSelected(Tab tab,
			android.support.v4.app.FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
		
	}

	public void onTabUnselected(Tab tab,
			android.support.v4.app.FragmentTransaction fragmentTransaction) {
	}

	public void onTabReselected(Tab tab,
			android.support.v4.app.FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new Fragment();
			switch (position) {
			case 0:
				fragment = new Fragment1();
				break;
			case 1:
				fragment = new Fragment2();
				break;
			case 2:
				fragment = new Fragment3();
				break;
			case 3:
				fragment = fragment4;
				break;
			default:
				Bundle args = new Bundle();
				args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
			}
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 4 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			}
			return null;
		}
	}
	
	public void ChildlineROICall(View view){
		
		try{
			dialogActivate("Ring Childline?", "This is an ROI number, are you sure you want to ring?", 1 ,"1800666666");
		} catch(Exception e){
			Log.e("custom", "Error");
		}
		
	}
	
	public void ChildlineUKCall(View view){
		dialogActivate("Ring Childline?", "This is a UK number, are you sure you want to ring?", 2, "8001111");
	}
	
	public void SamROICall(View view){
		dialogActivate("Ring Samaritans?", "This is an ROI number, are you sure you want to ring?", 1, "1850609090");
	}
	
	public void SamUKCall(View view){
		dialogActivate("Ring Samaritans","This a UK number, are you sure you want to ring?", 4, "8457909090");
	}
	
	public void OneLifeCall(View view){
		dialogActivate("Ring 1Life?", "This an ROI number, are you sure you want to call?", 1, "1800247100");
	}
	
	public void PapyrusCall(View view){
		dialogActivate("Ring Papyrus?", "This is a UK number, are you sure want to call?", 4, "8000684141");
	}
	
	public void LGBTHelplineCall(View view){
		dialogActivate("Ring The LGBT Helpline?", "This is a ROI number are you sure you want to call?", 3, "1890929539");
	}
	
	public void LlgsCall(View view){
		dialogActivate("Ring The London Lesbian & Gay Switchboard?", "This is a UK number, are you sure you want to ring?", 4, "3003300630");
	}
	
	public void ReachoutWeb(View view){
		dialogActivate("Go to Reachout?", "This is a website on bullying, are you sure you want to go there?", 5, "http://www.reachout.ie");
	}
	
	public void SpunoutWeb(View view){
		dialogActivate("Go to Spunout?", "This is a website for teenagers on a wide variety of topics, are you sure you want to go there", 5, "http://www.spunout.ie");
	}
	
	public void YoungMindsWeb(View view){
		dialogActivate("Go to YoungMinds?", "This is a website for teenagers on wids variety of topics, are you sure you want to go there", 5, "http://www.youngminds.org.uk/for_children_young_people/better_mental_health");
	}
	
	public void MindYourHeadWeb(View view){
		dialogActivate("Go to MindYourHeadStudy?", "This is a website which is a study of mental health issues", 5, "http://www.mindyourheadstudy.com/");
	}
	
	public void Call(int phoneNumber) {
		try {
	        Intent callIntent = new Intent(Intent.ACTION_CALL);
	        callIntent.setData(Uri.parse("tel:"+phoneNumber));
	        startActivity(callIntent);
	    } catch (ActivityNotFoundException activityException) {
	         Log.e("InspiriApp Dialer", "Call failed", activityException);
	    }
	}
	
	public void OCall(String number) {
		try {
			int phoneNumber = 0;
			phoneNumber = Integer.parseInt(number);
	        Intent callIntent = new Intent(Intent.ACTION_CALL);
	        callIntent.setData(Uri.parse("tel: 0"+phoneNumber));
	        startActivity(callIntent);
	    } catch (ActivityNotFoundException activityException) {
	         Log.e("InspiriApp Dialer", "Call failed", activityException);
	    }
	}
	
	public void longCall(long target){
		try {
	        Intent callIntent = new Intent(Intent.ACTION_CALL);
	        callIntent.setData(Uri.parse("tel:"+target));
	        startActivity(callIntent);
	    } catch (ActivityNotFoundException activityException) {
	         Log.e("InspiriApp Dialer", "Call failed", activityException);
	    }
	}
	
	public void OlongCall(long target){
		try {
	        Intent callIntent = new Intent(Intent.ACTION_CALL);
	        callIntent.setData(Uri.parse("tel: 0"+target));
	        startActivity(callIntent);
	    } catch (ActivityNotFoundException activityException) {
	         Log.e("InspiriApp Dialer", "Call failed", activityException);
	    }
	}
	
	public void browserLaunch(String address){
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
		startActivity(browserIntent);
	}
	
	public Boolean dialogActivate(String title, String message, final int type, final String target){
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(type){
				case 1:
					Call(Integer.parseInt(target));
					break;
				case 2:
					OCall(target);
					break;
				case 3:
					longCall(Long.parseLong(target));
					break;
				case 4:
					OlongCall(Long.parseLong(target));
					break;
				case 5:
					browserLaunch(target);
					break;
			}
		}})
		.setNegativeButton("No", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
			
		});
		AlertDialog ContactDialog = dialogBuilder.create();
		ContactDialog.show();
		return true;
	}
	
	public void wishListCreateNew(View v){
		Log.v("Creating new:","confirmed");
		//Get the message from the Edit Text
		EditText editText = (EditText) findViewById(R.id.WishListAddText);
		String message= editText.getText().toString();
		//If it is acceptable, continue
		if(!message.equals("") && Fragment4.WishlistCounter < Fragment4.SLOTS_LIMIT && !message.equals(null)){
			//Create new Wish
			Wish c = new Wish((ViewGroup) findViewById(R.id.MainLayout), this, new DatabaseHandler(this,(ViewGroup) findViewById(R.id.MainLayout)));
			c.newBuilder(message);
			Fragment4.WishlistCounter++;
			fire();
		//Explain unacceptability
		} else if(Fragment4.WishlistCounter >= Fragment4.SLOTS_LIMIT){
			Toast.makeText(context, "You cannot have over" +  String.valueOf(Fragment4.SLOTS_LIMIT) + "entries" ,Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
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
		for(OnWishlistChangeListener listener:listeners){
			listener.wishlistChanged();
		}
	}
	
	
}