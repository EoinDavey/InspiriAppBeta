package com.powerblock.inspiriappbeta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ImageEditor extends SherlockActivity {
	
	private ImageView mImageView;
	private Bitmap mBitmap;
	
	public static final String IMAGE_LOC_TAG ="image_location";
	public static final int REQUEST_CODE = 1;
	
	public static final String BACKGROUND_FILE_NAME = "ImageBackground.jpg";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mImageView = new ImageView(this);
		String uriString = getIntent().getStringExtra(IMAGE_LOC_TAG);
		Uri target = Uri.parse(uriString);
		Bitmap bitmap;
		try{
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(target));
			mBitmap = bitmap;
			BitmapDrawable bdraw = new BitmapDrawable(getResources(), bitmap);
			mImageView.setImageDrawable(bdraw);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		setContentView(mImageView);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(R.menu.image_editor_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);
		int id = item.getItemId();
		switch(id){
		case R.id.item1:
			finish(RESULT_OK);
			break;
		case R.id.item2:
			rotate();
			break;
		case R.id.item3:
			finish(RESULT_CANCELED);
			break;
		}
		return true;
	}
	
	public void rotate(){
		Bitmap bmpOriginal = mBitmap;
		Bitmap bmResult = Bitmap.createBitmap(bmpOriginal.getWidth(), bmpOriginal.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas tempCanvas = new Canvas(bmResult);
		tempCanvas.rotate(90, bmpOriginal.getWidth()/2, bmpOriginal.getHeight()/2);
		tempCanvas.drawBitmap(bmpOriginal, 0, 0, null);
		mBitmap = bmResult;
		mImageView.setImageBitmap(bmResult);
	}
	
	public void finish(int resultcode){
		if(resultcode == RESULT_OK){
			Intent data = new Intent();
			saveImage();
			data.putExtra("File location", BACKGROUND_FILE_NAME);
			setResult(RESULT_OK, data);
			finish();
		} else if(resultcode == RESULT_CANCELED){
			setResult(RESULT_CANCELED);
			finish();
		}
	}
	
	private void saveImage(){
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/InspiriAppBackground");
		myDir.mkdirs();
		File file = new File(myDir,BACKGROUND_FILE_NAME);
		if(file.exists()){
			file.delete();
		}
		try{
			FileOutputStream fos = new FileOutputStream(file);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
			fos.flush();
			fos.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
