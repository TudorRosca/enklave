package com.enklave.game.Enklave;

import com.enklave.game.MapsService.Bounds;
import com.enklave.game.MapsService.MapPixmap;

/**
 * Created by adrian on 28.03.2016.
 */
public class IdEnklave {
    public double lat;
    public double lng;
    public int id;
    public double coordDrawLat;
    public double coordDrawLng;
    public int numberbricks;
    public int faction;

    public IdEnklave(double lat, double lng, int id,int nrbricks,int faction) {
        this.lat = lat;
        this.lng = lng;
        this.id = id;
        this.numberbricks = nrbricks;
        this.faction = faction;
        Bounds bounds = new Bounds();
        bounds.getCorners(lat,lng , 17.0, 640.0, 640.0);
        double unitlong = (bounds.calcDisance(bounds.latSW, bounds.longSW, bounds.latSW, bounds.longNE) + bounds.calcDisance(bounds.latNE, bounds.longNE, bounds.latNE, bounds.longSW)) / 2.0;
        unitlong=600/unitlong;
        double unitlat = bounds.calcDisance(bounds.latSW, bounds.longSW, bounds.latNE, bounds.longSW);
        unitlat= 600/unitlat;
        MapPixmap map = MapPixmap.getInstance();
        coordDrawLat = bounds.calcDisance(map.getMatrix()[1][1].getLatitude(), map.getMatrix()[1][1].getLongitude(), lat, map.getMatrix()[1][1].getLongitude()) * unitlat;
        coordDrawLng = bounds.calcDisance(map.getMatrix()[1][1].getLatitude(), map.getMatrix()[1][1].getLongitude(), map.getMatrix()[1][1].getLatitude(), lng) * unitlong;
        if(map.getMatrix()[1][1].getLatitude() > lat)
            coordDrawLat = -coordDrawLat;
        if(map.getMatrix()[1][1].getLongitude() > lng)
            coordDrawLng = -coordDrawLng;
    }
}
