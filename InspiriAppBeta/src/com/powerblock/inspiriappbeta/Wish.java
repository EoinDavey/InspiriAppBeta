package com.powerblock.inspiriappbeta;

import java.io.Serializable;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class Wish implements Serializable, OnWishlistChangeListener{
	
	private static final long serialVersionUID = 7139785155501046178L;
	private static final int LAYOUT_ORIENTATION = LinearLayout.HORIZONTAL;
	private long id;
	private FragmentActivity context;
	private ViewGroup parentViewGroup;
	private LinearLayout mainLinearLayout;
	private LinearLayout buttonCheckBoxLayout;
	private Button removeButton;
	private Button editButton;
	private TextView textView;
	private CheckBox checkBox;
	private Resources res;
	private Drawable removeButtonBackground;
	private Drawable editButtonBackground;
	private DatabaseHandler databaseHandler;
	private float wishlistRemoveButtonDimenFloat;
	private float textViewDimenFloat;
	private int textViewDimen;
	private int wishlistButtonDimen;
	private int heightDimen;
	private int layoutColour;
	private Drawable bottomWishShape;

	public Wish(ViewGroup viewgroup, FragmentActivity c, DatabaseHandler db){
		this.context = c;
		this.parentViewGroup = viewgroup;
		this.res = this.context.getResources();
		this.layoutColour = res.getColor(R.color.wishlistbackground);
		this.removeButtonBackground = res.getDrawable(R.drawable.wishlistremovebuttonselector);
		this.editButtonBackground = res.getDrawable(R.drawable.pencilsmall);
		this.wishlistRemoveButtonDimenFloat = res.getDimension(R.dimen.wishlist__remove_button_dimen);
		float heightFloat = res.getDimension(R.dimen.height_dimen);
		this.bottomWishShape = res.getDrawable(R.drawable.bottomwishshape);
		this.heightDimen = Math.round(heightFloat);
		this.wishlistButtonDimen = Math.round(wishlistRemoveButtonDimenFloat);
		this.textViewDimenFloat = res.getDimension(R.dimen.wishlist_text_view_dimen);
		this.textViewDimen = Math.round(textViewDimenFloat);
		this.databaseHandler = db;
	}
	
	//This Builder is used for new wishes
	public void newBuilder(String message){
		build();
		setText(message);
		this.id = databaseHandler.addWish(this);
		fixShape();
		databaseHandler.logAllWishes();
	}
	
	//This Builder is used for reinstantiating Wishes from the database
	public void updateBuilder(String message, Boolean checked, int id){
		setId(id);
		build();
		setText(message);
		setChecked(checked);
		databaseHandler.updateWish(this);
	}

	@SuppressWarnings("deprecation")
	private void build() {
		//Declare the main linear layout
		this.mainLinearLayout = new LinearLayout(context);
		mainLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, heightDimen));
		mainLinearLayout.setOrientation(LAYOUT_ORIENTATION);
		//Instantiate the TextView
		this.textView = new TextView(this.context);
		textView.setLayoutParams(new LayoutParams(textViewDimen, LayoutParams.WRAP_CONTENT));
		textView.setText("null");
		textView.setGravity(Gravity.CENTER_VERTICAL);
		//Instantiate the Button CheckBox Layout
		this.buttonCheckBoxLayout = new LinearLayout(this.context);
		buttonCheckBoxLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		buttonCheckBoxLayout.setGravity(Gravity.RIGHT);
		//Instantiate the removeButton
		this.removeButton = new Button(this.context);
		removeButton.setLayoutParams(new LayoutParams(wishlistButtonDimen, wishlistButtonDimen));
		removeButton.setBackgroundDrawable(removeButtonBackground);
		removeButton.setGravity(Gravity.CENTER_VERTICAL);
		removeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				remove();
			}
		});
		//Instantiate the Edit Button
		this.editButton = new Button(this.context);
		editButton.setLayoutParams(new LayoutParams(wishlistButtonDimen, wishlistButtonDimen));
		editButton.setBackgroundDrawable(editButtonBackground);
		editButton.setGravity(Gravity.CENTER_VERTICAL);
		editButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editText();
			}
		});
		//Instantiate the CheckBox
		this.checkBox = new CheckBox(this.context);
		checkBox.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		checkBox.setGravity(Gravity.CENTER_VERTICAL);
		checkBox.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				checkHandler(checkBox.isChecked(), true);
			}
		});
		//add The Button and Checkbox to the buttonCheckBoxLayout
		buttonCheckBoxLayout.addView(editButton, 0);
		buttonCheckBoxLayout.addView(removeButton, 1);
		buttonCheckBoxLayout.addView(checkBox, 2);
		//Add the textView to the main LinearLayout
		mainLinearLayout.addView(textView);
		//Add the buttonCheckBoxLayout to the mainLinearLayout
		mainLinearLayout.addView(buttonCheckBoxLayout);
		
		fixShape();
		register(databaseHandler);
		register((WishlistObservable) context);
		
		//Add the main Linear Layout to the parent ViewGroup
		parentViewGroup.addView(mainLinearLayout);
	}
	
	public void checkHandler(Boolean isChecked, Boolean isManual){
		if(isChecked && isManual){
			Toast.makeText(context, "Congratulations on completing \"" + textView.getText().toString() + "\"", Toast.LENGTH_SHORT).show();
		}
		databaseHandler.updateWish(this);
	}
	
	public void setText(String message){
		try{
			this.textView.setText(message);
		} catch(NullPointerException e){
			Log.e("Wishlist Entry", "setText called before Builder()");
		}
		databaseHandler.updateWish(this);
	}
	
	public void setChecked(Boolean checked){
		this.checkBox.setChecked(checked);
		databaseHandler.updateWish(this);
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	private void remove(){
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle("Remove Entry")
		.setMessage("Are you sure you wish to remove \"" + this.textView.getText().toString() + "\"")
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				removeHandler();
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		AlertDialog dialog = dialogBuilder.create();
		dialog.show();
	}
	
	public void removeHandler(){
		try{
			mainLinearLayout.setVisibility(View.GONE);
			textView.setText("");
			checkBox.setChecked(false);
			Fragment4.WishlistCounterSubtract();
			databaseHandler.deleteWish(this);
			unregister(databaseHandler);
			unregister((WishlistObservable) context);
		} catch(NullPointerException e){
			Log.e("Wishlist Entry", "remove called before Builder()");
		}
	}
	
	public String getMessage(){
		String text = this.textView.getText().toString();
		return text;
	}
	
	public String getChecked(){
		String b = "false";
		if(!this.checkBox.equals(null)){
			if(this.checkBox.isChecked()){
				b = "true";
			}
		}
		return b;
	}
	
	public long getId(){
		return this.id;
	}
	
	public void editText(){
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		final EditText et = new EditText(context);
		et.setText(textView.getText());
		dialogBuilder.setTitle("Edit Wish");
		dialogBuilder.setView(et);
		//final EditText et = (EditText) context.findViewById(R.id.editTextBox);
		dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String message = et.getText().toString().trim();
				editHandler(message);
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		AlertDialog dialog = dialogBuilder.create();
		dialog.show();
	}
	
	private void editHandler(String message){
		if(!message.equals("") && !message.equals(null)){
			setText(message);
		}
	}
	
	private Boolean isLast(){
		return databaseHandler.isLast(this);
	}
	
	@SuppressWarnings("deprecation")
	private void fixShape(){
		//Check if last for design
		if(isLast()){
			if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
				mainLinearLayout.setBackgroundDrawable(bottomWishShape);
			} else {
				mainLinearLayout.setBackground(bottomWishShape);
			}
		} else {
			mainLinearLayout.setBackgroundColor(layoutColour);
		}
	}

	@Override
	public void wishlistChanged() {
		fixShape();
	}
	
	public void register(WishlistObservable observable){
		observable.add(this);
	}

	public void unregister(WishlistObservable observable){
		observable.remove(this);
	}
	
}