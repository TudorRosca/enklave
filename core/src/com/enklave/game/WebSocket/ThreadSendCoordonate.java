package com.enklave.game.WebSocket;

import com.enklave.game.MapsService.Bounds;

/**
 * Created by adrian on 18.03.2016.
 */
public class ThreadSendCoordonate implements Runnable{
    private double latitude = 0.0,longitude = 0.0,refLat=0.0,refLong=0.0;
    private WebSocketLocal webSocket;

    public ThreadSendCoordonate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        webSocket = WebSocketLocal.getInstance();
        refLat = webSocket.getReferenceLatitude();
        refLong = webSocket.getReferenceLongitude();
        if(refLat == 0.0 && refLong == 0.0){
            refLat = latitude;
            webSocket.setReferenceLatitude(refLat);
            refLong = longitude;
            webSocket.setReferenceLongitude(refLong);
            webSocket.sendMsg(latitude,longitude);
        }
    }

    @Override
    public void run() {
        if (latitude != refLat || longitude != refLong) {
            double dist = new Bounds().calcDisance(latitude, longitude, refLat, refLong);
            if (dist > 12.5) {
                webSocket.setReferenceLatitude(latitude);
                webSocket.setReferenceLongitude(longitude);
                webSocket.sendMsg(latitude, longitude);
            }
        }
    }
}
