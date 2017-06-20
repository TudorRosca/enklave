package com.enklave.game.Enklave;

import com.enklave.game.MapsService.Bounds;
import com.enklave.game.Screens.ScreenEnklave;
import com.badlogic.gdx.Gdx;


public class RangeEnklave extends Thread {
    private Bounds bounds;
    private float latEnk,longEnk,lat,lng;
    private ScreenEnklave screenEnklave;
    private float distanta;int range = 0;

    public RangeEnklave(float latEnk, float longEnk, float lat, float lng, ScreenEnklave enk,int prox) {
        this.latEnk = latEnk;
        this.longEnk = longEnk;
        this.lat = lat;
        this.lng = lng;
        range = prox;
        screenEnklave = enk;
        bounds = new Bounds();
    }

    @Override
    public void run() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                distanta = (float) bounds.calcDisance(lat,lng, latEnk, longEnk);
                if(distanta <= (55 + range)){
                    screenEnklave.setvisibility(false);
                    if(!(range == 5)){
                        screenEnklave.setRange(3);
                    }
                }else{
                    if(range == 3){
                        screenEnklave.setvisibility(false);
                        screenEnklave.setRange(5);
                        screenEnklave.createDialog("Please come back in the range");
                    }else {
                        if(range == 5){
                            screenEnklave.createDialog("You are out of range");
                        }
                        screenEnklave.setvisibility(true);
                        screenEnklave.setRange(0);
                    }
                }
            }
        });
    }
}
