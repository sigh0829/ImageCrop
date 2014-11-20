package com.jiff.cordova.plugin.image;

import java.io.ByteArrayOutputStream;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import eu.janmuller.android.simplecropimage.*;

public class ImageCrop extends CordovaPlugin {
  public CallbackContext callbackContext;
  public static final String CROP_IMAGE = "image:crop";
  public static final String FILE_CROP_FAILED_ERR = "Error: Image crop failed";
  public static final String FILE_CROP_ABORTED_ERR = "Error: Image crop aborted";

  private static final String LOG_TAG = "ImageCrop";

  public boolean execute(String action, final String rawArgs, final CallbackContext callbackContext) throws JSONException {
    this.callbackContext = callbackContext;
    if ("crop".equals(action)) {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          try {
            JSONArray args = new JSONArray(rawArgs);
            String filePath = args.getString(0);

            crop(filePath);
          } catch (Exception e) {
            Log.d(LOG_TAG, "Error: Crop - " + e.getMessage());
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, e.getMessage()));
          }
        }
      });
    }
  }

  public void crop(String filePath){
    Intent cropIntent = new Intent(this.callbackContext, CropImage.class);
    cropIntent.putExtra(CropImage.IMAGE_PATH, filePath);
    cropIntent.putExtra(CropImage.SCALE, true);
    cropIntent.putExtra(CropImage.ASPECT_X, 1);
    cropIntent.putExtra(CropImage.ASPECT_Y, 1);
    cropIntent.putExtra(CropImage.OUTPUT_X, 300);
    cropIntent.putExtra(CropImage.OUTPUT_Y, 300);
    cropIntent.putExtra(CropImage.RETURN_DATA, true);
    cordova.startActivityResult((CordovaPlugin) this, cropIntent, CROP_IMAGE);
    return;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if (requestCode == CROP_IMAGE) {
      if (resultCode != RESULT_OK) {
        this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, FILE_CROP_FAILED_ERR));
        return;
      }
      String path = intent.getStringExtra(CropImage.IMAGE_PATH);

      if (path == null) {
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, FILE_CROP_ABORTED_ERR));
        return;
      }

      Bitmap bitmap = BitmapFactory.decodeFile(path);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

      bm.recycle();
      bm = null;

      byte[] b = baos.toByteArray();
      String b64 = Base64.encodeToString(b, Base64.DEFAULT);
      this.callbackContext.success(b64);
    }
  }
}
