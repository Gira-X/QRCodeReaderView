package com.example.qr_readerexample;

import android.*;
import android.content.pm.*;
import android.graphics.*;
import android.os.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;

import com.dlazaro66.qrcodereaderview.*;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView.*;

public class DecoderActivity extends AppCompatActivity
		implements ActivityCompat.OnRequestPermissionsResultCallback, OnQRCodeReadListener {

	private static final int MY_PERMISSION_REQUEST_CAMERA = 0;

	private ViewGroup mainLayout;

	private TextView resultTextView;
	private QRCodeReaderView qrCodeReaderView;
	private PointsOverlayView pointsOverlayView;
	private View content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_decoder);

		mainLayout = findViewById(R.id.main_layout);

		content = getLayoutInflater().inflate(R.layout.content_decoder, mainLayout, true);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
				== PackageManager.PERMISSION_GRANTED) {

			prepareViews();
			inflateDecoderViewStub();
		} else {
			requestCameraPermission();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (qrCodeReaderView != null) {
			qrCodeReaderView.startCamera();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (qrCodeReaderView != null) {
			qrCodeReaderView.stopCamera();
		}
	}

	@Override
	public void onRequestPermissionsResult(
			int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

		if (requestCode != MY_PERMISSION_REQUEST_CAMERA) {
			return;
		}

		if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			Snackbar.make(mainLayout, "Camera permission was granted.", Snackbar.LENGTH_SHORT).show();
			inflateDecoderViewStub();
		} else {
			Snackbar.make(mainLayout, "Camera permission request was denied.", Snackbar.LENGTH_SHORT)
					.show();
		}
	}

	// Called when a QR is decoded
	// "text" : the text encoded in QR
	// "points" : points where QR control points are placed
	@Override
	public void onQRCodeRead(String text, PointF[] points) {
		resultTextView.setText(text);
		pointsOverlayView.setPoints(points);
	}

	private void requestCameraPermission() {
		if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
			Snackbar.make(mainLayout, "Camera access is required to display the camera preview.",
					Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					ActivityCompat.requestPermissions(DecoderActivity.this, new String[]{
							Manifest.permission.CAMERA
					}, MY_PERMISSION_REQUEST_CAMERA);
				}
			}).show();
		} else {
			Snackbar.make(mainLayout, "Permission is not available. Requesting camera permission.",
					Snackbar.LENGTH_SHORT).show();
			ActivityCompat.requestPermissions(this, new String[]{
					Manifest.permission.CAMERA
			}, MY_PERMISSION_REQUEST_CAMERA);
		}
	}

	private void prepareViews() {
		resultTextView = content.findViewById(R.id.result_text_view);
		pointsOverlayView = content.findViewById(R.id.points_overlay_view);
	}

	private void inflateDecoderViewStub() {
		final ViewStub decoderStub = content.findViewById(R.id.decoder_stub);
		decoderStub.inflate();
		//qrCodeReaderView = (QRCodeReaderView) decoderStub.inflate();
				//.setVisibility(View.VISIBLE);

		CheckBox flashlightCheckBox = content.findViewById(R.id.flashlight_checkbox);
		CheckBox enableDecodingCheckBox = content.findViewById(R.id.enable_decoding_checkbox);
		qrCodeReaderView = content.findViewById(R.id.qrdecoderview);
		qrCodeReaderView.setAutofocusInterval(2000L);
		qrCodeReaderView.setOnQRCodeReadListener(this);
		qrCodeReaderView.setBackCamera();
		flashlightCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				qrCodeReaderView.setTorchEnabled(isChecked);
			}
		});
		enableDecodingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				qrCodeReaderView.setQRDecodingEnabled(isChecked);
			}
		});
		qrCodeReaderView.startCamera();
	}
}