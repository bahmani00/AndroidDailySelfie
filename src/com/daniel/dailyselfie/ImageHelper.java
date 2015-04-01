package com.daniel.dailyselfie;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

public class ImageHelper {

	private static final String TAG = ImageHelper.class.getSimpleName();

	private static String DIR = "DailySelfie";

	private static File getRootStorage() {
		// Generally, any photos that the user captures with the device camera
		// should be saved on the device in the public external storage so they
		// are accessible by all apps
		// storage/emulated/0/Pictures
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	}

	public static List<String> getSelfieFiles() {

		final List<String> mItems = new ArrayList<String>();

		// storage/emulated/0/Pictures
		File rootDir = getRootStorage();

		if (rootDir == null)
			return mItems;

		if (!rootDir.exists())
			rootDir.mkdirs();

		// storage/emulated/0/Pictures/DailySelfie
		File imgStorageDir = new File(rootDir, DIR);

		if (!imgStorageDir.exists()) {
			imgStorageDir.mkdirs();
		} else {

			File[] files = imgStorageDir.listFiles();
			for (File file : files) {
				if (!file.isDirectory() && file.getAbsolutePath().endsWith(".jpg")) {
					mItems.add(file.getAbsolutePath());
				}
			}
		}

		return mItems;
	}

	/*
	 * Save image into "storage/emulated/0/Pictures/DailySelfie/" directory
	 */
	public static String saveImageFile(Bitmap imageBitmap) throws Exception {
		// storage/emulated/0/Pictures
		File rootDir = getRootStorage();
		if (!rootDir.exists())
			rootDir.mkdirs();

		// storage/emulated/0/Pictures/DailySelfie
		File imgStorageDir = new File(rootDir, DIR);

		String imageFileName = generateNewImageFileName();
		Log.v(TAG, "imageFileName: " + imageFileName);

/*		
 		//generates a number at the end 150618_102545-1961144729.jpg
 		File file = File.createTempFile(imageFileName,  prefix 
				".jpg",  suffix 
				imgStorageDir  directory 
		);
*/
		File file = new File(imgStorageDir, imageFileName + ".jpg");

		file.createNewFile();
		FileOutputStream ostream = new FileOutputStream(file);
		imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
		ostream.close();

/*		// scan the image so show up in album
		MediaScannerConnection.scanFile(this, new String[] { imagePath }, null,
				new MediaScannerConnection.OnScanCompletedListener() {
					public void onScanCompleted(String path, Uri uri) {
						if (Config.LOG_DEBUG_ENABLED) {
							Log.d(Config.LOGTAG, "scanned : " + path);
						}
					}
				});
*/
		return file.getAbsolutePath();
	}

	public static String generateNewImageFileName() {
		// Create an image file name: timeStamp
		return new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
	}

	public static boolean deleteImageFile(String filePath) {
		File file = new File(filePath);
		if (file != null && file.exists()) {
			return file.delete();
		} else {
			Log.v(TAG, filePath + " doesn't exist!");
		}

		return false;
	}

	public static String getFileName(String picturePath) {
		String[] paths = picturePath.split("/");

		Log.v(TAG, "paths[paths.length - 1]: " + paths[paths.length - 1]);

		return paths[paths.length - 1];
	}

	// http://developer.android.com/training/camera/photobasics.html
	public static void setPic(String picturePath, ImageView imgView) {
		// Get the dimensions of the View
		int targetW = imgView.getWidth();
		int targetH = imgView.getHeight();

		Log.v(TAG, "targetW: " + targetW + " , targetH: " + targetH);

		if (targetH == 0)
			targetH = 100;
		if (targetW == 0)
			targetW = 100;

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(picturePath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		Log.v(TAG, "photoW: " + photoW + " , photoH: " + photoH);

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(picturePath, bmOptions);
		imgView.setImageBitmap(bitmap);
	}

}