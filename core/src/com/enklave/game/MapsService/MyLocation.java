package com.enklave.game.MapsService;

/**
 * Created by adrian on 07.03.2016.
 */
public class MyLocation {
    private static MyLocation ourInstance = new MyLocation();

    public static MyLocation getInstance() {
        return ourInstance;
    }
    private double latitude;
    private double longitude;
    private double speed;
    private double altitude;
    private double compas;
    private double distance;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    private double accuracy;

    private MyLocation() {
        latitude = 0;
        longitude = 0;
        speed = 0;
        altitude = 0;
        compas = 0;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getCompas() {
        return compas;
    }

    public void setCompas(double compas) {
        this.compas = compas;
    }
}
