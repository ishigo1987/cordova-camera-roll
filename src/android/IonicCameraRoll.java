package com.drifty.cordova.cameraroll;

import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.database.Cursor;
import android.provider.MediaStore;
import android.media.ExifInterface;
import android.net.Uri;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

public class IonicCameraRoll extends CordovaPlugin {

	public final String ACTION_GET_PHOTOS = "getPhotos";

	public final String ACTION_SAVE = "saveToCameraRoll";

    private CallbackContext callbackContext;

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

		if (action.equals(ACTION_GET_PHOTOS)) {
		    getPhotos();
            return true;
        } else if (action.equals(ACTION_SAVE)) {
            // Not implemented yet
        }
        return false;
    }

    private void getPhotos() throws JSONException {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String pathOfImage = null;
        String dateOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = this.cordova.getActivity().getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            pathOfImage = cursor.getString(column_index_data);
            dateOfImage = dateFromImagePath(pathOfImage);

            JSONObject json = new JSONObject();
            json.put("path", pathOfImage);
            json.put("date", dateOfImage);

            PluginResult r = new PluginResult(PluginResult.Status.OK, json);
            r.setKeepCallback(true);
            this.callbackContext.sendPluginResult(r);
        }
    }

    private String dateFromImagePath(String path) {
        ExifInterface intf = null;
        try {
            intf = new ExifInterface(path);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        if(intf != null) {
            return intf.getAttribute(ExifInterface.TAG_DATETIME);
        }

        return null;
    }
}