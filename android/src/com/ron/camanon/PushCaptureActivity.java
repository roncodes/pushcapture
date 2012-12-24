package com.ron.camanon;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;

@SuppressLint("NewApi")
public class PushCaptureActivity extends Activity implements SurfaceHolder.Callback {
	
	private static final String TAG = "PushCapture";
	
	private MediaRecorder mMediaRecorder;
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mHolder;
	private View mToggleButton;
	private boolean inPreview = false;
	private boolean cameraConfigured = false;
	
	public String hostname = "74.63.224.195";
	public int tcpPort = 80;
	public int rtmpPort = 1935;
	
	final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
	final java.util.Random rand = new java.util.Random();
	final Set<String> identifiers = new HashSet<String>();

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		Log.i(TAG, "PushCapture Activity started...");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media_recorder_recipe);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		mHolder = mSurfaceView.getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		mToggleButton = (ToggleButton) findViewById(R.id.toggleRecordingButton);
		mToggleButton.setOnClickListener(new OnClickListener() {
			@Override
			// toggle video recording
			public void onClick(View v) {
				if (((ToggleButton)v).isChecked())
					mMediaRecorder.start();
				else {
					try {
						mMediaRecorder.stop();
					} catch(RuntimeException e) {
						Log.e(TAG, e.getMessage().toString());
					}
					mMediaRecorder.reset();
					try {
						initRecorder(mHolder.getSurface());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
	}
	
	/*
	 * Methods to initiate MediaRecorder
	 */
	private void initRecorder(Surface surface) throws IOException {

		if(mCamera == null) {
			Log.e(TAG, "mCamera is null, this shit is going to fail...");
		}
		
		mCamera.unlock();

		if(mMediaRecorder == null) {
			mMediaRecorder = new MediaRecorder();
			Log.i(TAG, "MediaRecorder initiated");
		}

		mMediaRecorder.setCamera(mCamera);

		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mMediaRecorder.setVideoSize(640, 480);
		mMediaRecorder.setVideoFrameRate(30);
		
		/*
		 * We want to send the recording as a live stream to rtmp server
		 */
		Socket socket = new Socket(InetAddress.getByName(hostname), rtmpPort);
		ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(socket);

		mMediaRecorder.setOutputFile(pfd.getFileDescriptor());
		mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);   

		try {
			mMediaRecorder.prepare();
			Log.i(TAG, "MediaRecorder has been prepared");
		} catch (IllegalStateException e) {
			Log.e(TAG, e.getMessage().toString());
		}
		
	}
	
	/*
	 * Methods below to handle camera init and preview
	 */

	@Override
	public void onResume() {
		super.onResume();

		mCamera = Camera.open();
		startPreview();
	}

	@Override
	public void onPause() {
		if (inPreview) {
			mCamera.stopPreview();
		}

		mCamera.release();
		mCamera = null;
		inPreview = false;

		super.onPause();
	}

	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result=null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width<=width && size.height<=height) {
				if (result==null) {
					result=size;
				}
				else {
					int resultArea=result.width*result.height;
					int newArea=size.width*size.height;

					if (newArea>resultArea) {
						result=size;
					}
				}
			}
		}
		
		Log.i(TAG, parameters.getSupportedPreviewSizes().toString());

		return(result);
	}
	
	private void initPreview(int width, int height) {
	    if (mCamera!=null && mHolder.getSurface()!=null) {
	      try {
	        mCamera.setPreviewDisplay(mHolder);
	      }
	      catch (Throwable t) {
	        Log.e("PreviewDemo-surfaceCallback",
	              "Exception in setPreviewDisplay()", t);
	        Toast
	          .makeText(PushCaptureActivity.this, t.getMessage(), Toast.LENGTH_LONG)
	          .show();
	      }

	      if (!cameraConfigured) {
	        Camera.Parameters parameters = mCamera.getParameters();
	        Camera.Size size = getBestPreviewSize(width, height,
	                                            parameters);
	        
	        if (size!=null) {
	          parameters.setPreviewSize(size.width, size.height);
	          mCamera.setParameters(parameters);
	          cameraConfigured = true;
	        }
	      }
	    }
	  }
	  
	private void startPreview() {
		if (cameraConfigured && mCamera!=null) {
			mCamera.startPreview();
			inPreview=true;
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// no-op -- wait until surfaceChanged()
	}

	public void surfaceChanged(SurfaceHolder holder,
			int format, int width,
			int height) {
		initPreview(width, height);
		startPreview();
		try {
			initRecorder(holder.getSurface());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage().toString());
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// no-op
	}

	/*
	 * Method to identify stream
	 */

	@SuppressLint("SimpleDateFormat")
	public String randomIdentifier() {
		int length = 0;
		StringBuilder builder = new StringBuilder();
		while(builder.toString().length() == 0) {
			length = rand.nextInt(5)+5;
			for(int i = 0; i < length; i++)
				builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
			if(identifiers.contains(builder.toString())) 
				builder = new StringBuilder();
		}
		/* Date identifier string */
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmm");
		Date date = new Date();
		return builder.toString()+dateFormat.format(date).toString();
	}
}