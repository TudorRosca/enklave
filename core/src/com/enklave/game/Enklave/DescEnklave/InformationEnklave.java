package com.enklave.game.Enklave.DescEnklave;

public class InformationEnklave {
    private static InformationEnklave ourInstance = new InformationEnklave();
    private int combatId;
    public int selectTargetEnklave = -1;

    public static InformationEnklave getInstance() {
        return ourInstance;
    }

    private InformationEnklave() {
        id =- 1;
        faction = 0;
        latitude = 0;
        longitude = 0;
        name ="";
        description = "";
        statusCombat = false;
        combatId = 0;
        rooms = 0;
        nr_shields = 0;
        turrets = 0;
        bricks = 0;
        extensions = 0;
        usercreate = "";
        energyBrick = 0;
        energyBrickfull = 0;
        energyShield = 0;
        energyfullshield = 0;

    }
    private int id,faction,energyBrick,energyBrickfull,energyShield,energyfullshield;
    private float latitude,longitude;
    private String name,description;
    private boolean statusCombat;
    private int rooms,extensions,turrets,nr_shields,bricks;
    private String usercreate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStatusCombat() {
        return statusCombat;
    }

    public void setStatusCombat(boolean statusCombat) {
        this.statusCombat = statusCombat;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public int getTurrets() {
        return turrets;
    }

    public void setTurrets(int turrets) {
        this.turrets = turrets;
    }

    public int getShields() {
        return energyShield;
    }

    public void setShields(int shields) {
        this.energyShield = shields;
    }

    public int getBricks() {
        return bricks;
    }

    public void setBricks(int bricks) {
        this.bricks = bricks;
    }

    public String getUsercreate() {
        return usercreate;
    }

    public void setUsercreate(String usercreate) {
        this.usercreate = usercreate;
    }

    public int getFaction() {
        return faction;
    }

    public void setFaction(int faction) {
        this.faction = faction;
    }

    public int getExtensions() {
        return extensions;
    }

    public void setExtensions(int extensions) {
        this.extensions = extensions;
    }

    public int getCombatId() {
        return combatId;
    }

    public void setCombatId(int combatId) {
        this.combatId = combatId;
    }

    public int getEnergyBrick() {
        return energyBrick;
    }

    public void setEnergyBrick(int energyBrick) {
        this.energyBrick = energyBrick;
    }

    public int getEnergyBrickfull() {
        return energyBrickfull;
    }

    public void setEnergyBrickfull(int energyBrickfull) {
        this.energyBrickfull = energyBrickfull;
    }

    public int getEnergyShield() {
        return energyShield;
    }

    public void setEnergyShield(int energyShield) {
        this.energyShield = energyShield;
    }

    public int getEnergyfullshield() {
        return energyfullshield;
    }

    public void setEnergyfullshield(int energyfullshield) {
        this.energyfullshield = energyfullshield;
    }

    public int getNr_shields() {
        return nr_shields;
    }

    public void setNr_shields(int nr_shields) {
        this.nr_shields = nr_shields;
    }
}
