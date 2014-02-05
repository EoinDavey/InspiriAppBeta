package com.powerblock.inspiriappbeta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ImageEditor extends SherlockActivity {
	
	private ImageView mImageView;
	private Bitmap mBitmap;
	
	private int displayWidth;
	private int displayHeight;
	
	public static final String IMAGE_LOC_TAG ="image_location";
	public static final int REQUEST_CODE = 1;
	
	public static final String BACKGROUND_FILE_NAME = "ImageBackground.jpg";
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mImageView = new ImageView(this);
		String uriString = getIntent().getStringExtra(IMAGE_LOC_TAG);
		Uri target = Uri.parse(uriString);
		
		Display display = getWindowManager().getDefaultDisplay(); 
		if(Build.VERSION.SDK_INT < 13){
			displayWidth = display.getWidth();  // deprecated
			displayHeight = display.getHeight();
		} else {
			Point size = new Point();
			display.getSize(size);
			displayWidth = size.x;
			displayHeight = size.y;
		}
		mBitmap = openImage(target);
		mImageView.setImageBitmap(mBitmap);
		setContentView(mImageView);
		Toast.makeText(this, new StringBuilder().append("display: ").append(displayHeight).append("x").append(displayWidth), Toast.LENGTH_SHORT).show();
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
	
	
	public Bitmap openImage(Uri target){
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		//only loads info about picture, avoids memory problems at this stage
		options.inJustDecodeBounds = true;
		try{
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(target), null, options);
			int height = options.outHeight;
			int width = options.outWidth;
			Toast.makeText(this, new StringBuilder("Image Size:").append(height).append(" x ").append(width).toString(), Toast.LENGTH_LONG).show();
			options.inSampleSize = calculateInSampleSize(options, displayHeight, displayWidth);
			Toast.makeText(this, "InSampleSize:"+String.valueOf(options.inSampleSize), Toast.LENGTH_SHORT).show();
			options.inJustDecodeBounds=false;
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(target), null, options);
			/*mBitmap = bitmap;
			int size = bitmap.getRowBytes() * bitmap.getHeight();
			Toast.makeText(this, String.valueOf(size), Toast.LENGTH_LONG).show();
			mImageView.setImageBitmap(bitmap);*/
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	public void rotate(){
		Bitmap bmpOriginal = mBitmap;
		Bitmap bmResult = Bitmap.createBitmap(bmpOriginal.getWidth(), bmpOriginal.getHeight(), Bitmap.Config.ARGB_8888);
		Matrix matrix = new Matrix();
		matrix.postRotate(90, bmpOriginal.getWidth()/2, bmpOriginal.getHeight()/2);
		bmResult = Bitmap.createBitmap(bmpOriginal, 0, 0, bmpOriginal.getWidth(), bmpOriginal.getHeight(), matrix, true);
		//Canvas tempCanvas = new Canvas(bmResult);
		//tempCanvas.rotate(90, bmpOriginal.getWidth()/2, bmpOriginal.getHeight()/2);
		//tempCanvas.drawBitmap(bmpOriginal, 0, 0, null);
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
		File myDir = new File(root + "/.InspiriAppBackground");
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
	
	private int calculateInSampleSize(BitmapFactory.Options options, int reqHeight, int reqWidth){
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		
		 if (height > reqHeight || width > reqWidth) {

		        final int halfHeight = height / 2;
		        final int halfWidth = width / 2;

		        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
		        // height and width larger than the requested height and width.
		        while ((halfHeight / inSampleSize) > reqHeight
		                || (halfWidth / inSampleSize) > reqWidth) {
		            inSampleSize *= 2;
		        }
		    }
		 return inSampleSize;
	}
	
}
