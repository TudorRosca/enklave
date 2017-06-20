package com.enklave.game.Combat;

import java.util.ArrayList;

public class ListOfAttachers {
    private static ListOfAttachers ourInstance = new ListOfAttachers();
    private ArrayList<InfoPlayer> players;
    public boolean isSelectedItem = false;

    public static ListOfAttachers getInstance() {
        return ourInstance;
    }

    private ListOfAttachers() {
        players = new ArrayList<InfoPlayer>();
    }

    public void add(InfoPlayer player){
        players.add(player);
    }

    public void clearList(){
        players.clear();
    }

    public int size(){
        return players.size();
    }

    public InfoPlayer getPlayer(int i){
        return players.get(i);
    }

    public void removePlayer(int id){
        int i=0;
        while(i<players.size()){
            if(players.get(i).id == id){
                players.remove(i);
            }
            i++;
        }
    }

    public InfoPlayer get(int id){
        int i=0;
        while(i<players.size()){
            if(players.get(i).id == id){
                return players.get(i);
            }
            i++;
        }
        return null;
    }

    public void shiftright(){
        players.add(0,players.remove(players.size()-1));
    }

    public void shiftLeft(){
        players.add(players.remove(0));
    }
}
