package com.daniel.dailyselfie;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


public class PictureActivity extends Activity {

    public static final int MENU_DELETE = 1;
    private String picPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        picPath = getIntent().getStringExtra("picPath");
        if (picPath != null && !picPath.isEmpty()) {
            ImageView imgPicture = new ImageView(this);

            LinearLayout.LayoutParams vp =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            imgPicture.setLayoutParams(vp);

            setContentView(imgPicture);

            imgPicture.setImageBitmap(BitmapFactory.decodeFile(picPath));

        } else {
            Toast.makeText(getApplicationContext(), R.string.image_not_exist,
                    Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        MenuItem menuItem = menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, R.string.action_delete);
        menuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == MENU_DELETE) {
            if (ImageHelper.deleteImageFile(picPath)) {
                
            	 Intent returnIntent = new Intent();
                 setResult(RESULT_OK, returnIntent);
                 this.finish();

                Toast.makeText(getApplicationContext(), R.string.image_deleted,
                        Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }



}
