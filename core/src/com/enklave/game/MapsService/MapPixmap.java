package com.enklave.game.MapsService;

import com.badlogic.gdx.graphics.Pixmap;

public class MapPixmap {
    private static MapPixmap ourInstance = new MapPixmap();

    public static MapPixmap getInstance() {
        return ourInstance;
    }

    private Pixmap [][] imagesMap;
    private PointCoordonate[][] matrix;
    private int[][] matrixExist;
    public TranslateMaps translateMaps;
    public FlagSignal flagSignal;

    public boolean isFlagComplete() {
        return flagComplete;
    }

    public void setFlagComplete(boolean flagComplete) {
        this.flagComplete = flagComplete;
    }

    private boolean flagComplete = false;

    private MapPixmap() {
        imagesMap = new Pixmap[3][3];
        matrix = new PointCoordonate[3][3];
        matrixExist = new int[3][3];
        for(int i=0;i<3;i++) {
            for (int j = 0; j < 3; j++) {
                matrixExist[i][j] = 0;
                matrix[i][j] = new PointCoordonate();
            }
        }
        translateMaps = TranslateMaps.getInstance();
        flagSignal = FlagSignal.getInstance();
    }
    public void setImage(Pixmap image,int i,int j) {
        this.imagesMap[i][j] = image;
    }
    public Pixmap getImage(int i,int j){
        return imagesMap[i][j];
    }
    public void setOnePhotoExist(int i,int j,int ok){
        matrixExist[i][j] = ok;
    }

    public PointCoordonate[][] getMatrix() {
        return matrix;
    }

    public int[][] getMatrixExist() {
        return matrixExist;
    }

    public void setMatrixExist(int[][] matrixExist) {
        this.matrixExist = matrixExist;
    }

    public void setMatrix(PointCoordonate[][] matrix) {
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                this.matrix[i][j].setLatitude(matrix[i][j].getLatitude());
                this.matrix[i][j].setLongitude(matrix[i][j].getLongitude());
                this.matrix[i][j].setDeltaLatitude(matrix[i][j].getDeltaLatitude());
                this.matrix[i][j].setDeltaLongitude(matrix[i][j].getDeltaLongitude());
                this.matrix[i][j].setDistance(matrix[i][j].getDistance());
            }
        }
        this.matrix = matrix;
    }
}
