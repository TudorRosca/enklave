package com.enklave.game.android.CheckList;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;


public class CheckPermission {
    private static String TAG = "Permission";
    boolean flag = false;
    public CheckPermission() {
    }
    public boolean check (final Activity activity,Context context){

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
            int permissionfineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionfineLocation != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission to Fine Location denied");
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Permission to access the GPS is required for this app to location.").setTitle("Permission Required").setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i(TAG, "Clicked");
                            makeRequest(activity, Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    makeRequest(activity, Manifest.permission.ACCESS_FINE_LOCATION);
                }
                flag = false;
            }else{
                flag = true;
            }
        }else{
            flag = true;
        }
        return flag;
    }
    private void makeRequest(Activity activity,String permission){
        ActivityCompat.requestPermissions(activity,new String[]{permission},101);
    }
}
