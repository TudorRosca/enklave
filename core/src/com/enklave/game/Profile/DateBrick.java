package com.enklave.game.Profile;

public class DateBrick {
    private int number,rateEnergy,rateScrap,rateTime,usageEnergy,usageTime, numberBrickUsage;

    public DateBrick() {
        number = 0;
        rateEnergy = 0;
        rateScrap = 0;
        rateTime = 0;
        usageEnergy = 0;
        usageTime = 0;
        numberBrickUsage = 0;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getRateEnergy() {
        return rateEnergy;
    }

    public void setRateEnergy(int rateEnergy) {
        this.rateEnergy = rateEnergy;
    }

    public int getRateScrap() {
        return rateScrap;
    }

    public void setRateScrap(int rateScrap) {
        this.rateScrap = rateScrap;
    }

    public int getRateTime() {
        return rateTime;
    }

    public void setRateTime(int rateTime) {
        this.rateTime = rateTime;
    }

    public int getUsageTime() {
        return usageTime;
    }

    public void setUsageTime(int usageTime) {
        this.usageTime = usageTime;
    }

    public int getUsageEnergy() {
        return usageEnergy;
    }

    public void setUsageEnergy(int usageEnergy) {
        this.usageEnergy = usageEnergy;
    }

    public int getNumberBrickUsage() {
        return numberBrickUsage;
    }

    public void setNumberBrickUsage(int numberBrickUsage) {
        this.numberBrickUsage = numberBrickUsage;
    }
}
