package com.enklave.game.android.LocalStorage;

import android.content.Context;
import android.database.Cursor;

import com.enklave.game.Interfaces.InterfaceNewMaps;
import com.enklave.game.MapsService.MapPixmap;
import com.enklave.game.MapsService.Bounds;
import com.enklave.game.MapsService.PointCoordonate;

/**
 * Created by adrian on 04.03.2016.
 */
public class CheckDatabase implements InterfaceNewMaps {
    private double lat;
    private double lng;
    private DatabaseHandler databaseHandler;
    private int[][] matrixforload;
    private PointCoordonate[][] matrix;
    private Context context;
    private Bounds bounds;
    private MapPixmap matrixPixmap;

    public CheckDatabase(Context context) {
        this.context = context;
        databaseHandler = new DatabaseHandler(this.context);
        bounds = new Bounds();
        matrixPixmap = MapPixmap.getInstance();
        matrixPixmap.translateMaps.setInterfaceNewMaps(this);
        matrixforload =new int[3][3];
        initializeMatrixLoad();
    }
    private void initializeMatrixLoad(){
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                matrixforload[i][j] = 0;
            }
        }
    }

    public void checkDB(double latitude,double longitude) {
        this.lat = latitude;
        this.lng = longitude;
        initializeMatrixLoad();
        matrix = bounds.calcMatrix(lat, lng);
        boolean findflag = false;
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (checkpoint(matrix[i][j].getLatitude(), matrix[i][j].getLongitude(), i, j) != 0) {
                    findflag = true;
                    matrixforload[i][j] = 1;
                    count++;
                }
            }
        }
        if (!findflag) {
            PointCoordonate l = databaseHandler.getimagenear(bounds.calcDisance(0.0, lng, lat, lng), bounds.calcDisance(lat, 0.0, lat, lng), lat, lng);
            if (l.getLatitude() != 0.0 || l.getLatitude() != 0.0) {
                l = setnewcenter(lat, lng, l);
                matrix = bounds.calcMatrix(l.getLatitude(), l.getLongitude());
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        //calculateDlat(i, j, matrix[i][j].getLatitude(), matrix[i][j].getLongitude());
                        databaseHandler.addimage(matrix[i][j].getLatitude(), matrix[i][j].getLongitude(), matrix[i][j].getDeltaLatitude(), matrix[i][j].getDeltaLongitude(), matrix[i][j].getDistance(), "" + matrix[i][j].getLatitude() + ":" + matrix[i][j].getLongitude());
                    }
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        databaseHandler.addimage(matrix[i][j].getLatitude(), matrix[i][j].getLongitude(), matrix[i][j].getDeltaLatitude(), matrix[i][j].getDeltaLongitude(), matrix[i][j].getDistance(), "" + matrix[i][j].getLatitude() + ":" + matrix[i][j].getLongitude());
                    }
                }
            }
        }
        if (count < 9 && count > 0) {
            if (matrixforload[0][0] == 1) {
                bounds.getCorners(matrix[0][0].getLatitude(), matrix[0][0].getLongitude(), 17.0, 640.0, 640.0);
                double e, s;
                double dist = bounds.calcDisance(bounds.latSW, bounds.longSW, bounds.latSW, bounds.longNE);
                for (int i = 1; i < 3; i++) {
                    if (matrixforload[0][i] == 0) {
                        bounds.calcNewCoordonate(matrix[0][i - 1].getLatitude(), matrix[0][i - 1].getLongitude(), 90, dist);
                        e = bounds.getLongbearing();
                        matrix[0][i].setLatitude(matrix[0][i - 1].getLatitude());
                        matrix[0][i].setLongitude(e);
                        matrix[0][i].setDistance(bounds.calcDisance(0.0, 0.0, matrix[0][i].getLatitude(), matrix[0][i].getLongitude()));
                        matrix[0][i].setDeltaLatitude(bounds.calcDisance(0.0, matrix[0][i].getLongitude(), matrix[0][i].getLatitude(), matrix[0][i].getLongitude()));
                        matrix[0][i].setDeltaLongitude(bounds.calcDisance(matrix[0][i].getLatitude(), 0.0, matrix[0][i].getLatitude(), matrix[0][i].getLongitude()));
                        databaseHandler.addimage(matrix[0][i].getLatitude(), matrix[0][i].getLongitude(), matrix[0][i].getDeltaLatitude(), matrix[0][i].getDeltaLongitude(), matrix[0][i].getDistance(), "" + matrix[0][i].getLatitude() + ":" + matrix[0][i].getLongitude());
                    }
                    if (matrixforload[i][0] == 0) {
                        bounds.calcNewCoordonate(matrix[i - 1][0].getLatitude(), matrix[i - 1][0].getLongitude(), 180, dist);
                        s = bounds.getLatbearing();
                        matrix[i][0].setLongitude(matrix[i - 1][0].getLongitude());
                        matrix[i][0].setLatitude(s);
                        matrix[i][0].setDistance(bounds.calcDisance(0.0, 0.0, matrix[i][0].getLatitude(), matrix[i][0].getLongitude()));
                        matrix[i][0].setDeltaLatitude(bounds.calcDisance(0.0, matrix[i][0].getLongitude(), matrix[i][0].getLatitude(), matrix[i][0].getLongitude()));
                        matrix[i][0].setDeltaLongitude(bounds.calcDisance(matrix[i][0].getLatitude(), 0.0, matrix[i][0].getLatitude(), matrix[i][0].getLongitude()));
                        databaseHandler.addimage(matrix[i][0].getLatitude(), matrix[i][0].getLongitude(), matrix[i][0].getDeltaLatitude(), matrix[i][0].getDeltaLongitude(), matrix[i][0].getDistance(), "" + matrix[i][0].getLatitude() + ":" + matrix[i][0].getLongitude());
                    }
                    //diagonala
                    if (matrixforload[i][i] == 0) {
                        matrix[i][i].setLatitude(matrix[i][0].getLatitude());
                        matrix[i][i].setLongitude(matrix[0][i].getLongitude());
                        matrix[i][i].setDistance(bounds.calcDisance(0.0, 0.0, matrix[i][i].getLatitude(), matrix[i][i].getLongitude()));
                        matrix[i][i].setDeltaLatitude(bounds.calcDisance(0.0, matrix[i][i].getLongitude(), matrix[i][i].getLatitude(), matrix[i][i].getLongitude()));
                        matrix[i][i].setDeltaLongitude(bounds.calcDisance(matrix[i][i].getLatitude(), 0.0, matrix[i][i].getLatitude(), matrix[i][i].getLongitude()));
                        databaseHandler.addimage(matrix[i][i].getLatitude(), matrix[i][i].getLongitude(), matrix[i][i].getDeltaLatitude(), matrix[i][i].getDeltaLongitude(), matrix[i][i].getDistance(), "" + matrix[i][i].getLatitude() + ":" + matrix[i][i].getLongitude());
                    }
                }
                //restul
                if (matrixforload[1][2] == 0) {
                    matrix[1][2].setLatitude(matrix[1][1].getLatitude());
                    matrix[1][2].setLongitude(matrix[0][2].getLongitude());
                    matrix[1][2].setDistance(bounds.calcDisance(0.0, 0.0, matrix[1][2].getLatitude(), matrix[1][2].getLongitude()));
                    matrix[1][2].setDeltaLatitude(bounds.calcDisance(0.0, matrix[1][2].getLongitude(), matrix[1][2].getLatitude(), matrix[1][2].getLongitude()));
                    matrix[1][2].setDeltaLongitude(bounds.calcDisance(matrix[1][2].getLatitude(), 0.0, matrix[1][2].getLatitude(), matrix[1][2].getLongitude()));
                    databaseHandler.addimage(matrix[1][2].getLatitude(), matrix[1][2].getLongitude(), matrix[1][2].getDeltaLongitude(), matrix[1][2].getDeltaLongitude(), matrix[1][2].getDistance(), "" + matrix[1][2].getLatitude() + ":" + matrix[1][2].getLongitude());
                }
                //restul
                if (matrixforload[2][1] == 0) {
                    matrix[2][1].setLatitude(matrix[2][0].getLatitude());
                    matrix[2][1].setLongitude(matrix[1][1].getLongitude());
                    matrix[2][1].setDistance(bounds.calcDisance(0.0, 0.0, matrix[2][1].getLatitude(), matrix[2][1].getLongitude()));
                    matrix[2][1].setDeltaLatitude(bounds.calcDisance(0.0, matrix[2][1].getLongitude(), matrix[2][1].getLatitude(), matrix[2][1].getLongitude()));
                    matrix[2][1].setDeltaLongitude(bounds.calcDisance(matrix[2][1].getLatitude(), 0.0, matrix[2][1].getLatitude(), matrix[2][1].getLongitude()));
                    databaseHandler.addimage(matrix[2][1].getLatitude(), matrix[2][1].getLongitude(), matrix[2][1].getDeltaLatitude(), matrix[2][1].getDeltaLongitude(), matrix[2][1].getDistance(), "" + matrix[2][1].getLatitude() + ":" + matrix[2][1].getLongitude());
                }
            } else if (matrixforload[0][2] == 1) {
                bounds.getCorners(matrix[0][2].getLatitude(), matrix[0][2].getLongitude(), 17.0, 640.0, 640.0);
                double s, w;
                double dist = bounds.calcDisance(bounds.latSW, bounds.longSW, bounds.latSW, bounds.longNE);
                for (int i = 1; i >= 0; i--) {
                    if (matrixforload[0][i] == 0) {
                        bounds.calcNewCoordonate(matrix[0][i + 1].getLatitude(), matrix[0][i + 1].getLongitude(), 270, dist);
                        w = bounds.getLongbearing();
                        matrix[0][i].setLatitude(matrix[0][i + 1].getLatitude());
                        matrix[0][i].setLongitude(w);
                        matrix[0][i].setDistance(bounds.calcDisance(0.0, 0.0, matrix[0][i].getLatitude(), matrix[0][i].getLongitude()));
                        matrix[0][i].setDeltaLatitude(bounds.calcDisance(0.0, matrix[0][i].getLongitude(), matrix[0][i].getLatitude(), matrix[0][i].getLongitude()));
                        matrix[0][i].setDeltaLongitude(bounds.calcDisance(matrix[0][i].getLatitude(), 0.0, matrix[0][i].getLatitude(), matrix[0][i].getLongitude()));
                        databaseHandler.addimage(matrix[0][i].getLatitude(), matrix[0][i].getLongitude(), matrix[0][i].getDeltaLatitude(), matrix[0][i].getDeltaLongitude(), matrix[0][i].getDistance(), "" + matrix[0][i].getLatitude() + ":" + matrix[0][i].getLongitude());
                    }
                    if (matrixforload[2 - i][2] == 0) {
                        bounds.calcNewCoordonate(matrix[1 - i][2].getLatitude(), matrix[1 - i][2].getLongitude(), 180, dist);
                        s = bounds.getLatbearing();
                        matrix[2 - i][2].setLongitude(matrix[1 - i][2].getLongitude());
                        matrix[2 - i][2].setLatitude(s);
                        matrix[2 - i][2].setDistance(bounds.calcDisance(0.0, 0.0, matrix[2 - i][2].getLatitude(), matrix[2 - i][2].getLongitude()));
                        matrix[2 - i][2].setDeltaLatitude(bounds.calcDisance(0.0, matrix[2 - i][2].getLongitude(), matrix[2 - i][2].getLatitude(), matrix[2 - i][2].getLongitude()));
                        matrix[2 - i][2].setDeltaLongitude(bounds.calcDisance(matrix[2 - i][2].getLatitude(), 0.0, matrix[2 - i][2].getLatitude(), matrix[2 - i][2].getLongitude()));
                        databaseHandler.addimage(matrix[2 - i][2].getLatitude(), matrix[2 - i][2].getLongitude(), matrix[2 - i][2].getDeltaLatitude(), matrix[2 - i][2].getDeltaLongitude(), matrix[2 - i][2].getDistance(), "" + matrix[2 - i][2].getLatitude() + ":" + matrix[2 - i][2].getLongitude());
                    }
                    if (matrixforload[2 - i][i] == 0) {
                        //diagonala secundara
                        matrix[2 - i][i].setLatitude(matrix[2 - i][2].getLatitude());
                        matrix[2 - i][i].setLongitude(matrix[0][i].getLongitude());
                        matrix[2 - i][i].setDistance(bounds.calcDisance(0.0, 0.0, matrix[2 - i][i].getLatitude(), matrix[2 - i][i].getLongitude()));
                        matrix[2 - i][i].setDeltaLatitude(bounds.calcDisance(0.0, matrix[2 - i][i].getLongitude(), matrix[2 - i][i].getLatitude(), matrix[2 - i][i].getLongitude()));
                        matrix[2 - i][i].setDeltaLongitude(bounds.calcDisance(matrix[2 - i][i].getLatitude(), 0.0, matrix[2 - i][i].getLatitude(), matrix[2 - i][i].getLongitude()));
                        databaseHandler.addimage(matrix[2 - i][i].getLatitude(), matrix[2 - i][i].getLongitude(), matrix[2 - i][i].getDeltaLatitude(), matrix[2 - i][i].getDeltaLongitude(), matrix[2 - i][i].getDistance(), "" + matrix[2 - i][i].getLatitude() + ":" + matrix[2 - i][i].getLongitude());
                    }
                }
                //restul
                if (matrixforload[1][0] == 0) {
                    matrix[1][0].setLatitude(matrix[1][1].getLatitude());
                    matrix[1][0].setLongitude(matrix[0][0].getLongitude());
                    matrix[1][0].setDistance(bounds.calcDisance(0.0, 0.0, matrix[1][0].getLatitude(), matrix[1][0].getLongitude()));
                    matrix[1][0].setDeltaLatitude(bounds.calcDisance(0.0, matrix[1][0].getLongitude(), matrix[1][0].getLatitude(), matrix[1][0].getLongitude()));
                    matrix[1][0].setDeltaLongitude(bounds.calcDisance(matrix[1][0].getLatitude(), 0.0, matrix[1][0].getLatitude(), matrix[1][0].getLongitude()));
                    databaseHandler.addimage(matrix[1][0].getLatitude(), matrix[1][0].getLongitude(), matrix[1][0].getDeltaLatitude(), matrix[1][0].getDeltaLongitude(), matrix[1][0].getDistance(), "" + matrix[1][0].getLatitude() + ":" + matrix[1][0].getLongitude());
                }
                if (matrixforload[2][1] == 0) {
                    //restul
                    matrix[2][1].setLatitude(matrix[2][0].getLatitude());
                    matrix[2][1].setLongitude(matrix[1][1].getLongitude());
                    matrix[2][1].setDistance(bounds.calcDisance(0.0, 0.0, matrix[2][1].getLatitude(), matrix[2][1].getLongitude()));
                    matrix[2][1].setDeltaLatitude(bounds.calcDisance(0.0, matrix[2][1].getLongitude(), matrix[2][1].getLatitude(), matrix[2][1].getLongitude()));
                    matrix[2][1].setDeltaLongitude(bounds.calcDisance(matrix[2][1].getLatitude(), 0.0, matrix[2][1].getLatitude(), matrix[2][1].getLongitude()));
                    databaseHandler.addimage(matrix[2][1].getLatitude(), matrix[2][1].getLongitude(), matrix[2][1].getDeltaLatitude(), matrix[2][1].getDeltaLongitude(), matrix[2][1].getDistance(), "" + matrix[2][1].getLatitude() + ":" + matrix[2][1].getLongitude());
                }
            } else if (matrixforload[2][0] == 1) {
                bounds.getCorners(matrix[2][0].getLatitude(), matrix[2][0].getLongitude(), 17.0, 640.0, 640.0);
                double e, n;
                double dist = bounds.calcDisance(bounds.latSW, bounds.longSW, bounds.latSW, bounds.longNE);
                for (int i = 1; i >= 0; i--) {
                    if (matrixforload[i][0] == 0) {
                        bounds.calcNewCoordonate(matrix[i + 1][0].getLatitude(), matrix[i + 1][0].getLongitude(), 0, dist);
                        n = bounds.getLatbearing();
                        matrix[i][0].setLatitude(n);
                        matrix[i][0].setLongitude(matrix[i + 1][0].getLongitude());
                        matrix[i][0].setDistance(bounds.calcDisance(0.0, 0.0, matrix[i][0].getLatitude(), matrix[i][0].getLongitude()));
                        matrix[i][0].setDeltaLatitude(bounds.calcDisance(0.0, matrix[i][0].getLongitude(), matrix[i][0].getLatitude(), matrix[i][0].getLongitude()));
                        matrix[i][0].setDeltaLongitude(bounds.calcDisance(matrix[i][0].getLatitude(), 0.0, matrix[i][0].getLatitude(), matrix[i][0].getLongitude()));
                        databaseHandler.addimage(matrix[i][0].getLatitude(), matrix[i][0].getLongitude(), matrix[i][0].getDeltaLatitude(), matrix[i][0].getDeltaLongitude(), matrix[i][0].getDistance(), "" + matrix[i][0].getLatitude() + ":" + matrix[i][0].getLongitude());
                    }
                    if (matrixforload[2][2 - i] == 0) {
                        bounds.calcNewCoordonate(matrix[2][1 - i].getLatitude(), matrix[2][1 - i].getLongitude(), 90, dist);
                        e = bounds.getLongbearing();
                        matrix[2][2 - i].setLongitude(e);
                        matrix[2][2 - i].setLatitude(matrix[2][1 - i].getLatitude());
                        matrix[2][2 - i].setDistance(bounds.calcDisance(0.0, 0.0, matrix[2][2 - i].getLatitude(), matrix[2][2 - i].getLongitude()));
                        matrix[2][2 - i].setDeltaLatitude(bounds.calcDisance(0.0, matrix[2][2 - i].getLongitude(), matrix[2][2 - i].getLatitude(), matrix[2][2 - i].getLongitude()));
                        matrix[2][2 - i].setDeltaLongitude(bounds.calcDisance(matrix[2][2 - i].getLatitude(), 0.0, matrix[2][2 - i].getLatitude(), matrix[2][2 - i].getLongitude()));
                        databaseHandler.addimage(matrix[2][2 - i].getLatitude(), matrix[2][2 - i].getLongitude(), matrix[2][2 - i].getDeltaLatitude(), matrix[2][2 - i].getDeltaLongitude(), matrix[2][2 - i].getDistance(), "" + matrix[2][2 - i].getLatitude() + ":" + matrix[2][2 - i].getLongitude());
                    }
                    //diagonala secundara
                    if (matrixforload[i][2 - i] == 0) {
                        matrix[i][2 - i].setLatitude(matrix[i][0].getLatitude());
                        matrix[i][2 - i].setLongitude(matrix[2][2 - i].getLongitude());
                        matrix[i][2 - i].setDistance(bounds.calcDisance(0.0, 0.0, matrix[i][2 - i].getLatitude(), matrix[i][2 - i].getLongitude()));
                        matrix[i][2 - i].setDeltaLatitude(bounds.calcDisance(0.0, matrix[i][2 - i].getLongitude(), matrix[i][2 - i].getLatitude(), matrix[i][2 - i].getLongitude()));
                        matrix[i][2 - i].setDeltaLongitude(bounds.calcDisance(matrix[i][2 - i].getLatitude(), 0.0, matrix[i][2 - i].getLatitude(), matrix[i][2 - i].getLongitude()));
                        databaseHandler.addimage(matrix[i][2 - i].getLatitude(), matrix[i][2 - i].getLongitude(), matrix[i][2 - i].getDeltaLatitude(), matrix[i][2 - i].getDeltaLongitude(), matrix[i][2 - i].getDistance(), "" + matrix[i][2 - i].getLatitude() + ":" + matrix[i][2 - i].getLongitude());
                    }
                }
                //restul
                if (matrixforload[0][1] == 0) {
                    matrix[0][1].setLatitude(matrix[0][0].getLatitude());
                    matrix[0][1].setLongitude(matrix[1][1].getLongitude());
                    matrix[0][1].setDistance(bounds.calcDisance(0.0, 0.0, matrix[0][1].getLatitude(), matrix[0][1].getLongitude()));
                    matrix[0][1].setDeltaLatitude(bounds.calcDisance(0.0, matrix[0][1].getLongitude(), matrix[0][1].getLatitude(), matrix[0][1].getLongitude()));
                    matrix[0][1].setDeltaLongitude(bounds.calcDisance(matrix[0][1].getLatitude(), 0.0, matrix[0][1].getLatitude(), matrix[0][1].getLongitude()));
                    databaseHandler.addimage(matrix[0][1].getLatitude(), matrix[0][1].getLongitude(), matrix[0][1].getDeltaLatitude(), matrix[0][1].getDeltaLongitude(), matrix[0][1].getDistance(), "" + matrix[0][1].getLatitude() + ":" + matrix[0][1].getLongitude());
                }
                //restul
                if (matrixforload[1][2] == 0) {
                    matrix[1][2].setLatitude(matrix[1][1].getLatitude());
                    matrix[1][2].setLongitude(matrix[0][2].getLongitude());
                    matrix[1][2].setDistance(bounds.calcDisance(0.0, 0.0, matrix[1][2].getLatitude(), matrix[1][2].getLongitude()));
                    matrix[1][2].setDeltaLatitude(bounds.calcDisance(0.0, matrix[1][2].getLongitude(), matrix[1][2].getLatitude(), matrix[1][2].getLongitude()));
                    matrix[1][2].setDeltaLongitude(bounds.calcDisance(matrix[1][2].getLatitude(), 0.0, matrix[1][2].getLatitude(), matrix[1][2].getLongitude()));
                    databaseHandler.addimage(matrix[1][2].getLatitude(), matrix[1][2].getLongitude(), matrix[1][2].getDeltaLatitude(), matrix[1][2].getDeltaLongitude(), matrix[1][2].getDistance(), "" + matrix[1][2].getLatitude() + ":" + matrix[1][2].getLongitude());
                }
            } else if (matrixforload[2][2] == 1) {
                bounds.getCorners(matrix[2][2].getLatitude(), matrix[2][2].getLongitude(), 17.0, 640.0, 640.0);
                double w, n;
                double dist = bounds.calcDisance(bounds.latSW, bounds.longSW, bounds.latSW, bounds.longNE);
                for (int i = 1; i >= 0; i--) {
                    if (matrixforload[i][2] == 0) {
                        bounds.calcNewCoordonate(matrix[i + 1][2].getLatitude(), matrix[i + 1][2].getLongitude(), 0, dist);
                        n = bounds.getLatbearing();
                        matrix[i][2].setLatitude(n);
                        matrix[i][2].setLongitude(matrix[i + 1][2].getLongitude());
                        matrix[i][2].setDistance(bounds.calcDisance(0.0, 0.0, matrix[i][2].getLatitude(), matrix[i][2].getLongitude()));
                        matrix[i][2].setDeltaLatitude(bounds.calcDisance(0.0, matrix[i][2].getLongitude(), matrix[i][2].getLatitude(), matrix[i][2].getLongitude()));
                        matrix[i][2].setDeltaLongitude(bounds.calcDisance(matrix[i][2].getLatitude(), 0.0, 0.0, matrix[i][2].getLongitude()));
                        databaseHandler.addimage(matrix[i][2].getLatitude(), matrix[i][2].getLongitude(), matrix[i][2].getDeltaLatitude(), matrix[i][2].getDeltaLongitude(), matrix[i][2].getDistance(), "" + matrix[i][2].getLatitude() + ":" + matrix[i][2].getLongitude());
                    }
                    if (matrixforload[2][i] == 0) {
                        bounds.calcNewCoordonate(matrix[2][i + 1].getLatitude(), matrix[2][i + 1].getLongitude(), 270, dist);
                        w = bounds.getLongbearing();
                        matrix[2][i].setLongitude(w);
                        matrix[2][i].setLatitude(matrix[2][i + 1].getLatitude());
                        matrix[2][i].setDistance(bounds.calcDisance(0.0, 0.0, matrix[2][i].getLatitude(), matrix[2][i].getLongitude()));
                        matrix[2][i].setDeltaLatitude(bounds.calcDisance(0.0, matrix[2][i].getLongitude(), matrix[2][i].getLatitude(), matrix[2][i].getLongitude()));
                        matrix[2][i].setDeltaLongitude(bounds.calcDisance(matrix[2][i].getLatitude(), 0.0, 0.0, matrix[2][i].getLongitude()));
                        databaseHandler.addimage(matrix[2][i].getLatitude(), matrix[2][i].getLongitude(), matrix[2][i].getDeltaLatitude(), matrix[2][i].getDeltaLongitude(), matrix[2][i].getDistance(), "" + matrix[2][i].getLatitude() + ":" + matrix[2][i].getLongitude());
                    }
                    //diagonala secundara
                    if (matrixforload[i][i] == 0) {
                        matrix[i][i].setLatitude(matrix[i][2].getLatitude());
                        matrix[i][i].setLongitude(matrix[2][i].getLongitude());
                        matrix[i][i].setDistance(bounds.calcDisance(0.0, 0.0, matrix[i][i].getLatitude(), matrix[i][i].getLongitude()));
                        matrix[i][i].setDeltaLatitude(bounds.calcDisance(0.0, matrix[i][i].getLongitude(), matrix[i][i].getLatitude(), matrix[i][i].getLongitude()));
                        matrix[i][i].setDeltaLongitude(bounds.calcDisance(matrix[i][i].getLatitude(), 0.0, 0.0, matrix[i][i].getLongitude()));
                        databaseHandler.addimage(matrix[i][i].getLatitude(), matrix[i][i].getLongitude(), matrix[i][i].getDeltaLatitude(), matrix[i][i].getDeltaLongitude(), matrix[i][i].getDistance(), "" + matrix[i][i].getLatitude() + ":" + matrix[i][i].getLongitude());
                    }
                }
                if (matrixforload[0][1] == 0) {
                    //restul
                    matrix[0][1].setLatitude(matrix[0][2].getLatitude());
                    matrix[0][1].setLongitude(matrix[1][1].getLongitude());
                    matrix[0][1].setDistance(bounds.calcDisance(0.0, 0.0, matrix[0][1].getLatitude(), matrix[0][1].getLongitude()));
                    matrix[0][1].setDeltaLatitude(bounds.calcDisance(0.0, matrix[0][1].getLongitude(), matrix[0][1].getLatitude(), matrix[0][1].getLongitude()));
                    matrix[0][1].setDeltaLongitude(bounds.calcDisance(matrix[0][1].getLatitude(), 0.0, 0.0, matrix[0][1].getLongitude()));
                    databaseHandler.addimage(matrix[0][1].getLatitude(), matrix[0][1].getLongitude(), matrix[0][1].getDeltaLatitude(), matrix[0][1].getDeltaLongitude(), matrix[0][1].getDistance(), "" + matrix[0][1].getLatitude() + ":" + matrix[0][1].getLongitude());
                }
                //restul
                if (matrixforload[1][0] == 0) {
                    matrix[1][0].setLatitude(matrix[1][1].getLatitude());
                    matrix[1][0].setLongitude(matrix[2][0].getLongitude());
                    matrix[1][0].setDistance(bounds.calcDisance(0.0, 0.0, matrix[1][0].getLatitude(), matrix[1][0].getLongitude()));
                    matrix[1][0].setDeltaLatitude(bounds.calcDisance(0.0, matrix[1][0].getLongitude(), matrix[1][0].getLatitude(), matrix[1][0].getLongitude()));
                    matrix[1][0].setDeltaLongitude(bounds.calcDisance(matrix[1][0].getLatitude(), 0.0, 0.0, matrix[1][0].getLongitude()));
                    databaseHandler.addimage(matrix[1][0].getLatitude(), matrix[1][0].getLongitude(), matrix[1][0].getDeltaLatitude(), matrix[1][0].getDeltaLongitude(), matrix[1][0].getDistance(), "" + matrix[1][0].getLatitude() + ":" + matrix[1][0].getLongitude());
                }
            }
        }
        gettranslate(lat, lng, matrix[1][1].getLatitude(), matrix[1][1].getLongitude());
        matrixPixmap.setMatrix(matrix);
        matrixPixmap.setMatrixExist(matrixforload);
        new Thread(new Runnable() {
            @Override
            public void run() {
                new UpdateDatabase(context).execute("");
            }
        }).start();


        //dbcheck.setDownloaddatabase(true);
//        }
    }
    private void calculateDlat(int i, int j,double latitude,double longitude) {
        matrix[i][j].setDistance(bounds.calcDisance(0.0, 0.0, latitude, longitude));
        matrix[i][j].setDeltaLatitude(bounds.calcDisance(0.0, longitude, latitude, longitude));
        matrix[i][j].setDeltaLongitude(bounds.calcDisance(latitude, 0.0, latitude, longitude));
    }
    public void gettranslate(double lat1,double lng1,double lat2,double lng2){
        double dx = bounds.calcDisance(lat1, lng1, lat1, lng2);
        double dy = bounds.calcDisance(lat1, lng1, lat2, lng1);
        if(lat1 < lat2){
            dy = -dy;
        }
        if(lng1 < lng2){
            dx = -dx;
        }
        matrixPixmap.translateMaps.setDirx(dx);
        matrixPixmap.translateMaps.setDiry(dy);
    }

    private PointCoordonate setnewcenter(double lat, double lng, PointCoordonate l) {
        bounds.getCorners(l.getLatitude(),l.getLongitude(), 17.0, 640.0, 640.0);
        double disty = bounds.calcDisance(bounds.latSW, bounds.longSW, bounds.latNE, bounds.longSW);
        double distx = bounds.calcDisance(bounds.latSW,bounds.longSW,bounds.latSW,bounds.longNE);
        double distlat = bounds.calcDisance(l.getLatitude(),l.getLongitude(),lat,l.getLongitude());
        distlat = ((Math.round((distlat-(disty/2))/disty))*disty)+disty;
        double distlong = bounds.calcDisance(l.getLatitude(),l.getLongitude(),l.getLatitude(),lng);
        distlong = ((Math.round((distlong-(distx/2))/distx))*distx)+distx;//greseala
        double angle = Math.toDegrees(Math.atan(distlat/distlong));
        if(lat>=l.getLatitude()){
            if(lng>=l.getLongitude()){
                angle = 90 - angle;
            }else {
                angle = 270 + angle;
            }
        }else{
            if(lng>=l.getLongitude()){
                angle = 90 + angle;
            }else {
                angle = 270 - angle;
            }
        }
        double dist = Math.sqrt(Math.pow(distlat,2)+Math.pow(distlong,2));
        bounds.calcNewCoordonate(l.getLatitude(), l.getLongitude(), angle, dist);
        return new PointCoordonate(bounds.getLatbearing(),bounds.getLongbearing());
    }

    private int checkpoint(double lat, double lng,int i,int j) {
        bounds.getCorners(lat, lng, 17.0, 640.0, 640.0);
        double dist = bounds.calcDisance(bounds.latSW, bounds.longSW, bounds.latNE, bounds.longSW);

        Cursor c = databaseHandler.getcoordonateimage(matrix[i][j].getDeltaLatitude(), matrix[i][j].getDeltaLongitude(), dist / 2);
        if(c.moveToFirst()) {
            do {
                matrix[i][j].setLatitude(c.getDouble(1));
                matrix[i][j].setLongitude(c.getDouble(2));
                matrix[i][j].setDeltaLatitude(c.getDouble(3));
                matrix[i][j].setDeltaLongitude(c.getDouble(4));
                matrix[i][j].setDistance(c.getDouble(5));
            }while(c.moveToNext());
        }
        int nr = c.getCount();
        c.close();
        return nr;
    }

    @Override
    public void newMaps(double lat, double lng) {
        checkDB(lat,lng);
    }
}
