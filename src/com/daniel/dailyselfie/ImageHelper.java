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
	
    private static final String TAG = "dailyselfie";
	private static String DIR = "DailySelfie";
	
    public static List<String> getSelfieFiles() {
    	
        final List<String> mItems = new ArrayList<String>();

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if(storageDir == null) return mItems;
        	
    	if(!storageDir.exists()) storageDir.mkdirs();

        storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), DIR);
    	
    	if(!storageDir.exists()) {
    		storageDir.mkdirs();
    	} else {
        
            File[] files = storageDir.listFiles();
            for (File file : files) {
                if (!file.isDirectory() && file.getAbsolutePath().endsWith(".jpg")) {
                	mItems.add(file.getAbsolutePath());
                }
            }
        }
    	
    	return mItems;
    }

    public static String saveImageFile(String imageFileName, Bitmap imageBitmap) throws Exception {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), DIR);
        File file = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        file.createNewFile();
        FileOutputStream ostream = new FileOutputStream(file);
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
        ostream.close();

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
        }else{
        	Log.v(TAG, filePath + " doesn't exist!");
        }
        
        return false;
    }
    

    public static String getFileName(String picturePath){
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

        if(targetH == 0)targetH = 100;
        if(targetW == 0)targetW = 100;

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