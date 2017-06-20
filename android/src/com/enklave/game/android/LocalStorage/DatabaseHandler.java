package com.enklave.game.android.LocalStorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.enklave.game.MapsService.PointCoordonate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PropertiesImage";
    private static final String TABLE_PROPERTIES = "IMAGE";
    private static final String TABLE_NEIGHBORHOOD = "NEIGHBORHOOD";

    private static final String KEY_ID = "ID";
    private static final String KEY_LAT = "LAT";
    private static final String KEY_LNG  = "LNG";
    private static final String KEY_DLAT = "DLAT";
    private static final String KEY_DLNG = "DLNG";
    private static final String KEY_DIST = "DISTANTA";
    private static final String KEY_ADDR = "ADDRESS";
    private static final String KEY_RANK = "RANK";
    private static final String KEY_DATE = "CREATED_AT";

    //neighborhood
    private static final String KEY_NOD = "NOD";
    private static final String KEY_TYPEREF = "TYPEREF";
    private static final String KEY_REF = "REF";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PROPERTIESIMAGE = "CREATE TABLE IF NOT EXISTS "+ TABLE_PROPERTIES + " ("+KEY_ID+" INTEGER AUTO_INCREMENT,"+ KEY_LAT+ " DOUBLE NOT NULL,"+KEY_LNG+" DOUBLE NOT NULL,"
                +KEY_DLAT+" DOUBLE NOT NULL,"+KEY_DLNG+" DOUBLE NOT NULL,"+KEY_DIST+" DOUBLE NOT NULL,"+KEY_ADDR+" VARCHAR2 NOT NULL,"+KEY_RANK+" INTEGER NOT NULL, " + KEY_DATE+ " DATE NOT NULL);";
        String CREATE_NEIGHBORHOOD = "CREATE TABLE IF NOT EXISTS "+TABLE_NEIGHBORHOOD+" ("+KEY_NOD+" INTEGER NOT NULL, "+KEY_TYPEREF+ " VARCHAR2 NOT NULL, "+KEY_REF+ " INTEGER NOT NULL);";
        db.execSQL(CREATE_PROPERTIESIMAGE);
        db.execSQL(CREATE_NEIGHBORHOOD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void addimage(double latitude,double longitude,double dlat,double dlng,double dist,String address){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LAT,latitude);
        values.put(KEY_LNG,longitude);
        values.put(KEY_DLAT,dlat);
        values.put(KEY_DLNG,dlng);
        values.put(KEY_DIST,dist);
        values.put(KEY_ADDR,address);
        values.put(KEY_RANK, 1);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        values.put(KEY_DATE, dateFormat.format(new Date()));
        db.beginTransaction();
        db.insert(TABLE_PROPERTIES, null, values);
        try{
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
        db.close();
    }
    public void remove(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROPERTIES, KEY_RANK + " < 5 AND " + KEY_DATE + " < SYSDATE()-7", null);
        db.close();

    }
    public Cursor getcoordonateimage(double dlat,double dlng,double dist){
        String select ="SELECT "+KEY_ID+", "+KEY_LAT+", "+KEY_LNG+", "+KEY_DLAT+" ," +KEY_DLNG+" , "+KEY_DIST+" , "+KEY_ADDR+" FROM "+TABLE_PROPERTIES+" WHERE ("+dlat+" BETWEEN ("+KEY_DLAT +"-"+dist+ ") AND ("+KEY_DLAT+"+"+dist+")) AND ("
                +dlng+ " BETWEEN ("+KEY_DLNG+"-"+dist+") AND ("+KEY_DLNG+"+"+dist+" ));";

        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery(select, null);
        if(cursor.moveToFirst()){
            do{
            }while(cursor.moveToNext());
        }
        return cursor;
    }
    public PointCoordonate getimagenear(double dlat,double dlng,double lat,double lng){
        showtable();
        Cursor c = getcoordonateimage(dlat,dlng,50000);
        PointCoordonate newcoord = new PointCoordonate();
//        Bounds bounds = new Bounds();
//        double mindist = Double.MAX_VALUE;
//        if (c.moveToFirst()){
//            do{
//                double d = bounds.calcDisance(lat,lng,c.getDouble(1),c.getDouble(2));
//                if(mindist>d){
//                    mindist = d;
//                    newcoord.setLat(c.getDouble(1));
//                    newcoord.setLng(c.getDouble(2));
//                }
//            }while (c.moveToNext());
//        }
        return newcoord;
    }
    public void showtable(){
        String select = "SELECT * FROM "+TABLE_PROPERTIES+" ;";
        SQLiteDatabase db2 = this.getReadableDatabase();
        Cursor c = db2.rawQuery(select,null);
        int i=0;
        if(c.moveToFirst()){
            do {
                Log.d("table id:" + c.getInt(0), "lat:" + c.getDouble(1) + " long:" + c.getDouble(2));
                Log.d("table dlat:"+c.getDouble(3),"dlong:"+c.getDouble(4)+" dist:"+c.getDouble(5));
            }while(c.moveToNext());
        }
        db2.close();
    }
}
