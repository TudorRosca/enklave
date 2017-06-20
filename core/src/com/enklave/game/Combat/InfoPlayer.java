package com.enklave.game.Combat;

import com.badlogic.gdx.graphics.Color;

public class InfoPlayer {
    public int id;
    public String name;
    public int energy,energyMax;
    public int damage;
    public int timeRecharge;
    public int costEnergyHit;
    public Color faction;

    public InfoPlayer(int id, String name, int energy,int energyMax, int damage, int i,int time,int costEnergy) {
        this.id = id;
        this.name = name;
        this.energy = energy;
        this.energyMax = energyMax;
        this.damage = damage;
        switch (i){
            case 1:{
                faction = new Color(1, 0f, 0f, 1);
                break;
            }
            case 2:{
                faction = new Color(0f, 0.831f, 0.969f,1f);
                break;
            }
            case 3:{
                faction= new Color(0.129f, 0.996f, 0.29f,1);
                break;
            }
        }
        this.timeRecharge = time;
        this.costEnergyHit = costEnergy;
    }

    public InfoPlayer() {
        this.id = 0;
        this.name = "";
        this.energy = 0;
        this.energyMax = 0;
        this.damage = 0;
        this.timeRecharge = 0;
        this.faction = Color.GRAY;
        this.costEnergyHit = 0;
    }
}
