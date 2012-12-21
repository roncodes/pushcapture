package com.ron.camanon;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.util.Log;

public class PushCapture extends CordovaPlugin { 

	String TAG = "PushCapture";

	@SuppressWarnings("deprecation")
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if ("capture".equals(action)) {
			Log.i(TAG, "Attempting to launch PushCaptureActivity");
			Intent i = new Intent(this.cordova.getContext(), PushCaptureActivity.class);
			this.cordova.startActivityForResult(this, i, 0);
			//callbackContext.success();
			return true;
		}
		callbackContext.error("Fail");
		return false;
	}
}

