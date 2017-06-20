package com.enklave.game.Profile;

/**
 * Created by adrian on 15.06.2016.
 */
public class DataCombat {
    int timeRecharging;
    int damage;

    public DataCombat() {
        timeRecharging = 5;
        damage = 50;
    }

    public int getTimeRecharging() {
        return timeRecharging;
    }

    public void setTimeRecharging(int timeRecharging) {
        this.timeRecharging = timeRecharging;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
