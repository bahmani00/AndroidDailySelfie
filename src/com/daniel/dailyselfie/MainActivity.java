package com.daniel.dailyselfie;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends ListActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final int REQUEST_IMAGE_DELETE = 2;
	
	private final long INITIAL_ALARM_DELAY_20MINs = 20 * 60 * 1000L;

	private MySimpleAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setAlarm();

		initializeListView();
	}

	private void setAlarm() {

		PendingIntent mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(this,
				AlarmReceiver.REQUEST_CODE_ALARM_RECEIVER, new Intent(this, AlarmReceiver.class),
				PendingIntent.FLAG_UPDATE_CURRENT);

		// Set single alarm
		AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INITIAL_ALARM_DELAY_20MINs,
				INITIAL_ALARM_DELAY_20MINs, mNotificationReceiverPendingIntent);

		Toast.makeText(getApplicationContext(), "Selfie Alarm has been set!", Toast.LENGTH_LONG).show();
	}

	private void initializeListView() {
		final ListView listView = getListView();

		mAdapter = new MySimpleAdapter(this);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String picPath = ((String) parent.getAdapter().getItem(position));
				Intent intent = new Intent(parent.getContext(), PictureActivity.class);
				intent.putExtra("picPath", picPath);
				startActivityForResult(intent, REQUEST_IMAGE_DELETE);
			}
		});

		listView.setAdapter(mAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mAdapter.getCount() == 0)
			loadData();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {			
			saveImage(data);			
		} else if (requestCode == REQUEST_IMAGE_DELETE && resultCode == RESULT_OK) {
			// reload the items
			loadData();
		}
	}

	private void saveImage(Intent data) {
		Bundle extras = data.getExtras();
		Bitmap imageBitmap = (Bitmap) extras.get("data");

		try {

			String picturePath = ImageHelper.saveImageFile(imageBitmap);

			// bind the new image to adapter
			mAdapter.add(picturePath);

		} catch (Exception e) {
			Toast.makeText(this, "Error occurred in saving the image File", Toast.LENGTH_LONG).show();
			//e.printStackTrace();
		}
		
	}

	private void dispatchTakePictureIntent() {
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			Toast.makeText(this, "This device has no camera", Toast.LENGTH_LONG);
			return;
		}

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}

	private void loadData() {
		mAdapter.clear();

		final List<String> images = ImageHelper.getSelfieFiles();
		for (String imagePath : images) {
			mAdapter.add(imagePath);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_show_camera) {

			dispatchTakePictureIntent();

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
