package com.enklave.game.MapsService;

import com.enklave.game.Enklave.ListEnklaves;
import com.enklave.game.Requests.NearbyEnklave;
import com.badlogic.gdx.Gdx;

/**
 * Created by adrian on 04.03.2016.
 */
public class MyThread extends Thread {
    private MyQueue queue;
    private MyLocation myLocation;
    private MapPixmap mapPixmap;
    double latitude,longitude,speed,distanta,latphoto=0.0,longphoto=0.0,startlat,startlong,latnew,longnew;
    Bounds bounds;
    double dx=0,dy=0,unitdx=0,unitdy=0,unitlat=0.0,unitlong = 0.0;
    boolean loadcoordonate = false;


    public MyThread(double slat,double sln,double lat,double lng,double latnew,double longnew, double distanta,double unitlat,double unitlong) {

        this.latitude=lat;
        this.longitude=lng;
        this.distanta = distanta;
        bounds = new Bounds();
        this.unitlat = unitlat;
        this.unitlong = unitlong;
        this.startlat = slat;this.startlong = sln;
        this.latnew = latnew;this.longnew = longnew;
        this.queue = MyQueue.getInstance();
        this.myLocation = MyLocation.getInstance();
        this.mapPixmap = MapPixmap.getInstance();
    }

    @Override
    public void run() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                distanta = bounds.calcDisance(startlat, startlong, latnew, longnew);
                //DownloadImage dwn = DownloadImage.getInstance();
                if (latitude != latnew || longitude != longnew) {
                    dx = bounds.calcDisance(latitude, longitude, latitude, longnew) * unitlong;
                    dy = bounds.calcDisance(latitude, longitude, latnew, longitude) * unitlat;
                    if (latitude < latnew) {
                        dy = -dy;
                    }
                    if (longitude < longnew) {
                        dx = -dx;
                    }
                    Gdx.app.log("intra:", "distanta: " + distanta);
                    for (int i = 0; i < 55; i++) {
                        queue.add(dx / 55.0, dy / 55.0);
                    }
                    if (distanta > 550.0 && mapPixmap.flagSignal.isDownloadimg() && !mapPixmap.flagSignal.isLoadCoordonate()) {// && myLocation.isSetUpdate() && dwn.isFlagcontinue()) {
                        mapPixmap.flagSignal.setDownloadimg(false);
                        mapPixmap.translateMaps.getInterfaceNewMaps().newMaps(latnew, longnew);
                        mapPixmap.flagSignal.setLoadCoordonate(true);
                        new NearbyEnklave().makeRequestEnklave();
                        ListEnklaves.getInstance().setSetat(false);
                    }

                    if (mapPixmap.flagSignal.isLoadCoordonate()) {
                        mapPixmap.translateMaps.setRealDirX(mapPixmap.translateMaps.getRealDirX() + (float) dx);
                        mapPixmap.translateMaps.setRealDirY(mapPixmap.translateMaps.getRealDirY() + (float) dy);
                    }
                    if (mapPixmap.flagSignal.isDownloadimg() && mapPixmap.flagSignal.isLoadCoordonate()) {
                        mapPixmap.flagSignal.setLoadCoordonate(false);
                        mapPixmap.flagSignal.setUpdateDisplay(true);
                    }
                }
            }
        });
    }
}
