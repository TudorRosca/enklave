package com.enklave.game.Profile;


public class DateUserGame {
    private int scrap, level, faction,electronics;
    private int energy, energyProgress, energyLevel;
    private int experience, experienceLevel;
    private double distanceWalked;
    private boolean InCombat = false;
    private int enklaveCombatId = -1;

    public DateUserGame() {
        scrap = 0;
        level = 0;
        faction = 0;
        energy = 0;
        energyProgress = 0;
        energyLevel = 0;
        experience = 0;
        experienceLevel = 0;
        distanceWalked = 0;
        electronics = 0;
    }

    public double getDistanceWalked() {
        return distanceWalked;
    }

    public void setDistanceWalked(double distanceWalked) {
        this.distanceWalked = distanceWalked;
    }

    public int getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(int experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getEnergyLevel() {
        return energyLevel;
    }

    public void setEnergyLevel(int energyLevel) {
        this.energyLevel = energyLevel;
    }

    public int getEnergyProgress() {
        return energyProgress;
    }

    public void setEnergyProgress(int energyProgress) {
        this.energyProgress = energyProgress;
    }

    public int getFaction() {
        return faction;
    }

    public void setFaction(int faction) {
        this.faction = faction;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getScrap() {
        return scrap;
    }

    public void setScrap(int scrap) {
        this.scrap = scrap;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        if(energy >= 0 && energy<=energyLevel){
            this.energy = energy;
        }
        else if(energy>energyLevel){
            this.energy = energyLevel;
        }else {
            this.energy = 0;
        }
    }

    public int getElectronics() {
        return electronics;
    }

    public void setElectronics(int electronics) {
        this.electronics = electronics;
    }

    public boolean isInCombat() {
        return InCombat;
    }

    public void setInCombat(boolean inCombat) {
        InCombat = inCombat;
    }

    public int getEnklaveCombatId() {
        return enklaveCombatId;
    }

    public void setEnklaveCombatId(int enklaveCombatId) {
        this.enklaveCombatId = enklaveCombatId;
    }
}
