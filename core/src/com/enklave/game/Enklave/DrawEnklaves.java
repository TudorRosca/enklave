package com.enklave.game.Enklave;

import com.enklave.game.MapsService.PointCoordonate;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by adrian on 28.03.2016.
 */
public class DrawEnklaves {
    private final ListEnklaves arrayinfo;
    private ArrayList<Enklave3D> arrayenk;


    public DrawEnklaves() {
        arrayinfo = ListEnklaves.getInstance();
        if(arrayinfo!=null) {
            arrayenk = new ArrayList<Enklave3D>(arrayinfo.size());
            for (int i = 0; i < arrayinfo.size(); i++) {
                arrayenk.add(new Enklave3D(arrayinfo.get(i).coordDrawLat, arrayinfo.get(i).coordDrawLng,arrayinfo.get(i).faction));
            }
        }
    }
    public int lenght(){
        return arrayenk.size();
    }
//    public void add(DecalBatch batch){
//        for(int i=0;i<arrayenk.size();i++) {
//            arrayenk.get(i).addtoDecalBatch(batch);
//        }
//    }
    public void DrawEnklave(ModelBatch batch){
        for(int i=0;i<arrayenk.size();i++) {
            arrayenk.get(i).drawEnklave(batch);
        }
    }
    public void DrawInitial(PointCoordonate coord,double unitlat,double unitlng){
        for(int i=0;i<arrayinfo.size();i++){
            arrayenk.get(i).calcDrawEnklave(coord,unitlat,unitlng);
        }
    }
    public void translateEnk(float transX,float transY){
        for(int i=0;i<arrayenk.size();i++) {
            //arrayenk.get(i).translateenklave(arrayenk.get(i), transX, transY);
            arrayenk.get(i).translateEnklave(arrayenk.get(i),transX,transY);
        }
    }
    public boolean isSet(){
        return arrayinfo.isSetat();
    }
    public void set(boolean flag){
        arrayinfo.setSetat(flag);
    }
    public Enklave3D getEnk(int i){
        return arrayenk.get(i);
    }
    public Vector2 getCoordonateDraw(int i){
        return new Vector2((float)arrayinfo.get(i).lng,(float)arrayinfo.get(i).lat);
    }
    public Vector2 getEnkl(int id){
        return new Vector2((float)arrayinfo.getWithID(id).lat,(float)arrayinfo.getWithID(id).lng);
    }
}
