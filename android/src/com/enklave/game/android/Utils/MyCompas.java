package com.enklave.game.android.Utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.enklave.game.MapsService.MyLocation;

/**
 * Created by adrian on 03.03.2016.
 */
public class MyCompas implements SensorEventListener {
    private Activity activity;
    private SensorManager sensorManager;
    Sensor compas;
    Sensor accelerometer;
    float comp[]=new float[3];
    float acc[]=new float[3];
    boolean c=false,a=false;

    public MyCompas(Activity act) {
        this.activity = act;
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        compas = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
    public void registersensor(){
        sensorManager.registerListener(this,compas,SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_GAME);
    }
    public void unregistersensor(){
        sensorManager.unregisterListener(this, compas);
        sensorManager.unregisterListener(this, accelerometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor == compas){
            System.arraycopy(event.values,0,comp,0,event.values.length);
            c=true;
        }
        else if(event.sensor == accelerometer){
            System.arraycopy(event.values,0,acc,0,event.values.length);
            a=true;
        }
        if(c && a) {
            float []r = new float[9],orien=new float[3];
            SensorManager.getRotationMatrix(r, null, acc, comp);
            SensorManager.getOrientation(r, orien);
            float grad = (float)((Math.toDegrees(orien[0])+360)%360);
            MyLocation.getInstance().setCompas(grad);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
