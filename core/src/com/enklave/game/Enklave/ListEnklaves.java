package com.enklave.game.Enklave;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ListEnklaves {
    private static ListEnklaves ourInstance = new ListEnklaves();
    private ArrayList<IdEnklave> listEnklave;
    private boolean setat = false;

    public static ListEnklaves getInstance(){
        return ourInstance;
    }

    private ListEnklaves() {
        listEnklave = new ArrayList<IdEnklave>();
    }

    public void setListEnklave(JSONArray json){
        for(int i=0;i<json.length();i++){
            JSONObject object = json.getJSONObject(i);
            double lat = object.getDouble("latitude");
            double lng = object.getDouble("longitude");
            int id = object.getInt("id");
            int nrbricks = object.getInt("nr_bricks");
            int faction = 0;
            if(!object.isNull("faction")) {
                switch (object.getInt("faction")) {
                    case 1: {
                        faction = 1;
                        break;
                    }
                    case 2: {
                        faction = 2;
                        break;
                    }
                    case 3: {
                        faction = 3;
                        break;
                    }
                }
            }
            listEnklave.add(new IdEnklave(lat,lng,id,nrbricks,faction));
        }
        setat = true;
    }
    public int size(){
        return listEnklave.size();
    }
    public IdEnklave get(int index){
        return listEnklave.get(index);
    }

    public boolean isSetat() {
        return setat;
    }

    public void setSetat(boolean setat) {
        this.setat = setat;
    }

    public ArrayList<IdEnklave> getListEnklave() {
        return listEnklave;
    }

    public IdEnklave getWithID(int id){
        int i=0;
        while(i<listEnklave.size()){
            if(listEnklave.get(i).id == id){
                return listEnklave.get(i);
            }
            i++;
        }
        return null;
    }
}
