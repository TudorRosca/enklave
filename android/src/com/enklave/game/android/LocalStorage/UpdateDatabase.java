package com.enklave.game.android.LocalStorage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.enklave.game.MapsService.MapPixmap;
import com.enklave.game.android.Activity.Logare;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by adrian on 04.03.2016.
 */
public class UpdateDatabase extends AsyncTask<String,Integer,Void> {
    private MapPixmap matrixPixmap;
    private final Context mcontext;

    public UpdateDatabase(Context context) {
        this.mcontext = context;
        matrixPixmap = MapPixmap.getInstance();
    }
    @Override
    protected Void doInBackground(String... params) {
        for(int i = 0;i<3;i++){
            for(int j=0;j<3;j++){
                if(matrixPixmap.getMatrixExist()[i][j] == 0){
                    getMap(matrixPixmap.getMatrix()[i][j].getLatitude(),matrixPixmap.getMatrix()[i][j].getLongitude(),i,j);
                    Gdx.app.log("download","map");
                }
                else if(matrixPixmap.getMatrixExist()[i][j] == 1) {

                    FileHandle fh = new FileHandle(Gdx.files.getLocalStoragePath() + "image-" + matrixPixmap.getMatrix()[i][j].getLatitude() + ":" + matrixPixmap.getMatrix()[i][j].getLongitude() + ".png");
                    if (fh.exists()) {
                        Gdx.app.log("download","map storage");
                        try{
                            matrixPixmap.setImage(new Pixmap(fh), i, j);
                        }catch (Exception e){
                            getMap(matrixPixmap.getMatrix()[i][j].getLatitude(), matrixPixmap.getMatrix()[i][j].getLongitude(), i, j);
                        }

                    } else {
                        getMap(matrixPixmap.getMatrix()[i][j].getLatitude(), matrixPixmap.getMatrix()[i][j].getLongitude(), i, j);
                        Gdx.app.log("download", "map storage but download");
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        int count =0;
        for(int i = 0;i<3;i++) {
            for (int j = 0; j < 3; j++) {
                if (matrixPixmap.getMatrixExist()[i][j] == 1) {
                    count++;
                }
            }
        }

        if(count == 9){
            //.setDownloaddatabase(true);
            matrixPixmap.flagSignal.setDownloadimg(true);
            matrixPixmap.translateMaps.setCenterlat(matrixPixmap.getMatrix()[1][1].getLatitude());
            matrixPixmap.translateMaps.setCenterlong(matrixPixmap.getMatrix()[1][1].getLongitude());
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    private void getMap(double latitude,double longitude,int i,int j){
        FileHandle fh=null;
        String stylemaps = "&style=element:labels|visibility:off&style=feature:road|element:geometry|visibility:on|color:0x3d88b7|weight:1.9|hue:0x00aaff|saturation:31|lightness:-6&style=feature:landscape|element:geometry|visibility:on|invert_lightness:true|hue:0xff0099|saturation:-66|lightness:12&style=feature:poi|element:geometry|visibility:off"+"&key=AIzaSyBo7tAycCUhodJlLcGOS4JY_wJH4BoGcRs";
        String staticmaps = "http://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=17&format=jpeg&sensor=true&size=640x640&scale=2&maptype=roadmap" + stylemaps;
        try {
            byte[] bytes = new byte[200 * 1024];
            HttpURLConnection conn = (HttpURLConnection) new URL(staticmaps).openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setUseCaches(true);
            conn.connect();
            InputStream in = conn.getInputStream();
            int readbyte = 0;
            while (true) {
                int length = in.read(bytes, readbyte, bytes.length - readbyte);
                if (length == -1) break;
                readbyte += length;
            }
            matrixPixmap.setImage(new Pixmap(bytes, 0, readbyte), i, j);
            fh = new FileHandle(Gdx.files.getLocalStoragePath() + "image-" + latitude + ":" + longitude + ".png");
            PixmapIO.writePNG(fh, new Pixmap(bytes, 0, readbyte));
            matrixPixmap.setOnePhotoExist(i, j, 1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            if(fh.exists())
                fh.delete();
            AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
            builder.setMessage("CANNOT CONNECT TO SERVER!\n PLEASE CHECK YOUR INTERNET\n CONNECTION, THEN RESTART THE GAME").setNeutralButton("RESTART", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mcontext.startActivity(new Intent(mcontext,Logare.class));
                }
            });
            builder.create().show();
        } catch (IOException e) {
            Log.d("ioexception :", " " + e);
            e.printStackTrace();
            if(fh.exists())
                fh.delete();
            AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
            builder.setMessage("CANNOT CONNECT TO SERVER!\n PLEASE CHECK YOUR INTERNET\n CONNECTION, THEN RESTART THE GAME").setNeutralButton("RESTART", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mcontext.startActivity(new Intent(mcontext,Logare.class));
                }
            });
            builder.create().show();
        }
    }
}
