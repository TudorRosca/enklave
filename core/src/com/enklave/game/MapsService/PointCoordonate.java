package com.enklave.game.MapsService;

/**
 * Created by adrian on 04.03.2016.
 */
public class PointCoordonate {
    public double latitude,longitude,distance,deltaLatitude,deltaLongitude;

    public PointCoordonate() {
        latitude = 0;
        longitude = 0;
        distance = 0;
        deltaLatitude = 0;
        deltaLongitude = 0;
    }

    public PointCoordonate(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
//        com.adrianpopovici.game.MapsService.Bounds bounds = new com.adrianpopovici.game.MapsService.Bounds();
//        this.distance = bounds.calcDisance(0.0,0.0,latitude,longitude);
//        this.deltaLongitude = bounds.calcDisance(latitude,0,latitude,longitude);
//        this.deltaLatitude = bounds.calcDisance(0,longitude,latitude,longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDeltaLatitude() {
        return deltaLatitude;
    }

    public void setDeltaLatitude(double deltaLatitude) {
        this.deltaLatitude = deltaLatitude;
    }

    public double getDeltaLongitude() {
        return deltaLongitude;
    }

    public void setDeltaLongitude(double deltaLongitude) {
        this.deltaLongitude = deltaLongitude;
    }
}
