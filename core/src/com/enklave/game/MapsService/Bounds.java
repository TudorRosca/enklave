package com.enklave.game.MapsService;

import java.math.RoundingMode;
import java.text.DecimalFormat;


public class Bounds {
    public double latSW,longSW,latNE,longNE;
    private double latbearing=0.0,longbearing=0.0;
    double pixelOrigin_x,pixelOrigin_y ,pixelsPerLonDegree_,pixelsPerLonRadian_;
    double MERCATOR_RANGE = 256;
    double point_x,point_y,lat,lng;
    DecimalFormat df = new DecimalFormat("#.#######");

    private double bound(double value,double opt_min,double opt_max){
        if (opt_min != 0) value = Math.max(value, opt_min);
        if (opt_max != 0) value = Math.min(value, opt_max);
        return value;
    }
    private double rad2deg(double distance) {
        return ((distance * 180) / Math.PI);
    }

    public double getLatbearing() {
        return latbearing;
    }

    public double getLongbearing() {
        return longbearing;
    }

    private double deg2rad(double lat1) {
        return (lat1 * Math.PI / 180.0);
    }
    private void init(){
        pixelOrigin_x = MERCATOR_RANGE / 2;
        pixelOrigin_y = MERCATOR_RANGE / 2;
        pixelsPerLonDegree_ = MERCATOR_RANGE / 360;
        pixelsPerLonRadian_ = MERCATOR_RANGE / ( 2 * Math.PI);
    }
    private void fromLatLngToPoint (double l,double ln){
        double origin_x=pixelOrigin_x,origin_y=pixelOrigin_y;
        point_x = origin_x + ln * pixelsPerLonDegree_;
        double siny = bound(Math.sin(deg2rad(l)),-0.9999,0.9999);
        point_y = origin_y +0.5 * Math.log((1+siny)/(1-siny))* -pixelsPerLonRadian_;
    }
    private void fromPointToLatLng (double x,double y){
        double origin_x=pixelOrigin_x,origin_y=pixelOrigin_y;
        lng = (x - origin_x)/pixelsPerLonDegree_;
        double latRad = (y - origin_y)/-pixelsPerLonRadian_;
        lat = rad2deg(2 * Math.atan(Math.exp(latRad))-Math.PI/2);
    }
    public void getCorners (double l,double ln,double zoom,double w,double h){
        double scale = Math.pow(2,zoom);
        init();
        fromLatLngToPoint(l,ln);
        double px = point_x - (w/2)/scale;
        double py = point_y + (h/2)/scale;
        fromPointToLatLng(px,py);
        latSW = lat;longSW=lng;
        px = point_x + (w/2)/scale;
        py = point_y -(h/2)/scale;
        fromPointToLatLng(px,py);
        latNE = lat; longNE = lng;
    }
    public double calcDisance(double lat1,double long1,double lat2,double long2){
        double distance;
        double theta = long1 - long2;
        distance = Math.sin(Math.toRadians(lat1))*Math.sin(Math.toRadians(lat2))+Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*Math.cos(Math.toRadians(theta));//deg2rad(lat1))*Math.sin(deg2rad(lat2))+Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        if(distance > 1){
            distance = 1;
        }else if(distance<-1){
            distance = -1;
        }
        distance = Math.acos(distance);
        distance = Math.toDegrees(distance);
        distance = distance * 60 *1.1515;
        distance = distance * 1.609344;
        distance = distance *1000;
        return distance;
    }
    public void calcNewCoordonate (double lat1,double long1,double bearing,double dist){
        dist = dist / 6371000;
        df.setRoundingMode(RoundingMode.CEILING);
        bearing = deg2rad(bearing);
        lat1 = deg2rad(lat1);
        long1 = deg2rad(long1);
        latbearing = Math.asin(Math.sin(lat1) * Math.cos(dist) + Math.cos(lat1) * Math.sin(dist) * Math.cos(bearing));
        longbearing = long1 + Math.atan2(Math.sin(bearing) * Math.sin(dist) * Math.cos(lat1), Math.cos(dist) - Math.sin(lat1) * Math.sin(latbearing));
        latbearing = (double)Math.round(rad2deg(latbearing)*10000000d)/10000000d;
        longbearing = (double)Math.round(rad2deg(longbearing)*10000000d)/10000000d;
    }
    public PointCoordonate[][] calcMatrix(double latitude,double longitude){
        PointCoordonate[][] matrix=initmatrix();
        getCorners(latitude, longitude, 17.0, 640.0, 640.0);
        double e,s,w,n;
        double dist = calcDisance(latSW, longSW, latNE, longSW);
        calcNewCoordonate(latitude, longitude, 0, dist);
        n = getLatbearing();
        calcNewCoordonate(latitude, longitude, 90, dist);
        e = getLongbearing();
        calcNewCoordonate(latitude, longitude, 180, dist);
        s = getLatbearing();
        calcNewCoordonate(latitude, longitude, 270, dist);
        w = getLongbearing();
        matrix[0][0] = initOnePoint(n,w);//.setLatitude(n);matrix[0][0].setLongitude(w);//
        matrix[0][1] = initOnePoint(n,longitude);//.setLatitude(n);matrix[0][1].setLongitude(longitude);//
        matrix[0][2] = initOnePoint(n,e);//.setLatitude(n);matrix[0][2].setLongitude(e);//
        matrix[1][0] = initOnePoint(latitude,w);//.setLatitude(latitude);matrix[1][0].setLongitude(w);//
        matrix[1][1] = initOnePoint(latitude,longitude);//.setLatitude(latitude);matrix[1][1].setLongitude(longitude);//
        matrix[1][2] = initOnePoint(latitude,e);//.setLatitude(latitude);matrix[1][2].setLongitude(e);//
        matrix[2][0] = initOnePoint(s,w);//.setLatitude(s);matrix[2][0].setLongitude(w);//
        matrix[2][1] = initOnePoint(s,longitude);//.setLatitude(s);matrix[2][1].setLongitude(longitude);//
        matrix[2][2] = initOnePoint(s,e);//.setLatitude(s);matrix[2][2].setLongitude(e); //
        return matrix;
    }
    private PointCoordonate initOnePoint(double lat,double lng){
        PointCoordonate pointCoordonate = new PointCoordonate();
        pointCoordonate.setLatitude(lat);
        pointCoordonate.setLongitude(lng);
        pointCoordonate.setDistance(calcDisance(0.0, 0.0, lat, lng));
        pointCoordonate.setDeltaLatitude(calcDisance(0, lng, lat, lng));
        pointCoordonate.setDeltaLongitude(calcDisance(lat,0,lat,lng));
        return pointCoordonate;
    }
    public PointCoordonate[][] initmatrix() {
        PointCoordonate[][] matrix = new PointCoordonate[3][3];
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                matrix[i][j] = new PointCoordonate();
            }
        }
        return matrix;
    }
}
