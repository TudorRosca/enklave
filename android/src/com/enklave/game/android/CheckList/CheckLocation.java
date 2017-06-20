package com.enklave.game.android.CheckList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;

import com.enklave.game.android.LocalStorage.CheckDatabase;

public class CheckLocation  {
    private static Location location;

    public static Location getLocation() {
        return location;
    }

    public static boolean check(final Activity activity, final CheckDatabase check) {
        boolean flag = false;
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            flag = true;
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("No location services are enabled. Please ensure one or more is enabled to continue.")
                    .setPositiveButton("Enable...", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivity(intent);
                        }
                    }).setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    activity.finish();
                    System.exit(0);
                }
            }).setTitle("Warning").setCancelable(false);

            builder.create().show();
        }
        return flag;
    }
}
