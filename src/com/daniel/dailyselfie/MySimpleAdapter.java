package com.daniel.dailyselfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class MySimpleAdapter extends BaseAdapter {

    private final String TAG = "dailyselfie";
    private final Context mContext;
    private final List<String> mItems = new ArrayList<String>();

    public MySimpleAdapter(Context context) {
        mContext = context;
    }

    public void add(String picturePath) {
        mItems.add(picturePath);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return -1;//mItems.get(position).id;
    }

    public void clear()
    {
    	mItems.clear();
        this.notifyDataSetChanged();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String picturePath = mItems.get(position);

        //Log.v(TAG, client);

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.selfi_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.lblPicName = (TextView) convertView.findViewById(R.id.lblPicName);
            viewHolder.imgView = (ImageView) convertView.findViewById(R.id.imgPicture);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (picturePath != null && !picturePath.isEmpty()) {
            Log.v(TAG, "picturePath: " + picturePath);
            viewHolder.lblPicName.setText(ImageHelper.getFileName(picturePath));
            setPic(picturePath, viewHolder.imgView);
        }

        return convertView;
    }

    // http://developer.android.com/training/camera/photobasics.html
    private void setPic(String picturePath, ImageView imgView) {
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


    private static class ViewHolder{
        TextView lblPicName;
        ImageView imgView;
    }
}