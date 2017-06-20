package com.enklave.game.android.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.enklave.game.MapsService.MapPixmap;
import com.enklave.game.MapsService.MyLocation;
import com.enklave.game.WebSocket.ThreadSendCoordonate;
import com.enklave.game.WebSocket.WebSocketLocal;
import com.enklave.game.android.LocalStorage.CheckDatabase;

import java.util.Date;
import java.util.List;

public class ListenerLocation implements LocationListener {
    private LocationManager locationManager;
    private Activity activity;
    private MyLocation mylocation;
    private CheckDatabase checkDatabase;
    Location location = null;

    public ListenerLocation(final Activity act, CheckDatabase check) {
        this.activity = act;
        mylocation = MyLocation.getInstance();
        checkDatabase = check;
        locationManager = (LocationManager) activity.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        locationManager.requestLocationUpdates(provider, 0, 0, this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Location bestLoc = null;
                while (location == null) {
                    if (locationManager != null) {
                        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        List<String> provider = locationManager.getProviders(true);
                        for(String prov:provider){
                            location = locationManager.getLastKnownLocation(prov);
                            if(location ==null){
                                continue;
                            }
                            if(bestLoc == null || new Date(location.getTime()).after(new Date(bestLoc.getTime()))){
                                bestLoc = location;
                            }
                        }
                    }
                }
                Log.d("locatia listener: ", "lat ");
                mylocation.setLatitude(Math.round(bestLoc.getLatitude()*10000000d)/10000000d);
                mylocation.setLongitude(Math.round(bestLoc.getLongitude()*10000000d)/10000000d);
                mylocation.setSpeed(bestLoc.getSpeed());
                mylocation.setAltitude(bestLoc.getAltitude());
                mylocation.setAccuracy(bestLoc.getAccuracy());
                double lat = Math.round(bestLoc.getLatitude() * 10000000d) / 10000000d;
                double lng = Math.round(bestLoc.getLongitude() * 10000000d) / 10000000d;
                checkDatabase.checkDB(lat, lng);
                MapPixmap.getInstance().translateMaps.setInitiallat(lat);
                MapPixmap.getInstance().translateMaps.setInitiallong(lng);
                while(!WebSocketLocal.getInstance().isConnected())
                {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                new ThreadSendCoordonate(lat,lng).run();
                Log.d("locatia listener: ", "lat " + lat + " long " + lng);
            }
        }).start();
    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation.setLatitude(Math.round(location.getLatitude()*10000000d)/10000000d);
        mylocation.setLongitude(Math.round(location.getLongitude()*10000000d)/10000000d);
        mylocation.setSpeed(location.getSpeed());
        mylocation.setAltitude(location.getAltitude());
        mylocation.setAccuracy(location.getAccuracy());
        if(WebSocketLocal.getInstance().isConnected()){
            new ThreadSendCoordonate(location.getLatitude(),location.getLongitude()).run();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("intra", "listener status change");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("intra","listener provider enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("intra","listener provider disaible");
    }
}
