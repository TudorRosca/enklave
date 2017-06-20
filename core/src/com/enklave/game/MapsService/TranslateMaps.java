package com.enklave.game.MapsService;

import com.enklave.game.Interfaces.InterfaceNewMaps;

/**
 * Created by adrian on 08.03.2016.
 */
public class TranslateMaps {
    private static TranslateMaps ourInstance = new TranslateMaps();
    private double latitude;
    private double longitude;
    private double dirx = 0.0, diry = 0.0;
    private double realDirX = 0.0, realDirY = 0.0;
    private double initiallat = 0,initiallong = 0;
    private double centerlat=0;
    private double centerlong=0;

    private InterfaceNewMaps interfaceNewMaps;

    public InterfaceNewMaps getInterfaceNewMaps() {
        return interfaceNewMaps;
    }

    public void setInterfaceNewMaps(InterfaceNewMaps interfaceNewMaps) {
        this.interfaceNewMaps = interfaceNewMaps;
    }

    public double getCenterlong() {
        return centerlong;
    }

    public void setCenterlong(double centerlong) {
        this.centerlong = centerlong;
    }

    public double getCenterlat() {
        return centerlat;
    }

    public void setCenterlat(double centerlat) {
        this.centerlat = centerlat;
    }

    public double getInitiallat() {
        return initiallat;
    }

    public void setInitiallat(double initiallat) {
        this.initiallat = initiallat;
    }

    public double getInitiallong() {
        return initiallong;
    }

    public void setInitiallong(double initiallong) {
        this.initiallong = initiallong;
    }

    public static TranslateMaps getInstance() {
        return ourInstance;
    }

    private TranslateMaps() {
    }

    public double getDirx() {
        return dirx;
    }

    public void setDirx(double dirx) {
        this.dirx = dirx;
    }

    public double getDiry() {
        return diry;
    }

    public void setDiry(double diry) {
        this.diry = diry;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getRealDirX() {
        return realDirX;
    }

    public void setRealDirX(double realDirX) {
        this.realDirX = realDirX;
    }

    public double getRealDirY() {
        return realDirY;
    }

    public void setRealDirY(double realDirY) {
        this.realDirY = realDirY;
    }
}
