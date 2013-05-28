package me.kaidul.cropimagewithaspectratio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_GALLERY = 2;
	ImageView imgview;
	int aspectX, aspectY, outputX, outputY;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		imgview = (ImageView) findViewById(R.id.imageView1);

		ScreenResolution sr = deviceDimensions();
		// use Euclid's theorem to calculate the proper aspect ratio i.e. screen
		// resolution : 480 * 800 So aspect Ratio 3:5
		int gcd = GCD(sr.width, sr.height);
		aspectX = sr.width / gcd;
		aspectY = sr.height / gcd;
		// define the output image width & height possibly low as android
		// default crop is not well suited to pick big image :(
		outputX = sr.width - aspectX * 30;
		outputY = sr.height - aspectY * 30;

		Button buttonCamera = (Button) findViewById(R.id.btn_take_camera);
		Button buttonGallery = (Button) findViewById(R.id.btn_select_gallery);
		buttonCamera.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// call android default camera
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				intent.putExtra(MediaStore.EXTRA_OUTPUT,
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
				pickAndCrop(intent);
				try {

					intent.putExtra("return-data", true);
					startActivityForResult(intent, PICK_FROM_CAMERA);

				} catch (ActivityNotFoundException e) {
					Toast.makeText(MainActivity.this,
							"Whoops! This device has no crop feature!",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		buttonGallery.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// call android default gallery
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				pickAndCrop(intent);
				try {

					intent.putExtra("return-data", true);
					startActivityForResult(Intent.createChooser(intent,
							getString(R.string.complete_action_with)),
							PICK_FROM_GALLERY);

				} catch (ActivityNotFoundException e) {
					Toast.makeText(MainActivity.this,
							"Whoops! This device has no crop feature!",
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	void pickAndCrop(Intent intent) {
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", aspectX);
		intent.putExtra("aspectY", aspectY);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
	}

	int GCD(int a, int b) {
		return (b == 0 ? a : GCD(b, a % b));
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	ScreenResolution deviceDimensions() {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			return new ScreenResolution(size.x, size.y);
		} else {
			Display display = getWindowManager().getDefaultDisplay();
			return new ScreenResolution(display.getWidth(), display.getHeight());
		}
	}

	private class ScreenResolution {
		int width, height;

		public ScreenResolution(int width, int height) {
			this.width = width;
			this.height = height;
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == PICK_FROM_CAMERA) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				imgview.setImageBitmap(photo);

			}
		}

		if (requestCode == PICK_FROM_GALLERY) {
			Bundle extras2 = data.getExtras();
			if (extras2 != null) {
				Bitmap photo = extras2.getParcelable("data");
				imgview.setImageBitmap(photo);

			}
		}
	}
}