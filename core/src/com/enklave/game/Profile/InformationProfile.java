package com.enklave.game.Profile;

import com.enklave.game.Enum.MaterialsCraft;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class InformationProfile {
    private Date readyForNextAction = new Date();
    private boolean incombat=false;
    private DateUser dateUser;
    private DateUserGame dateUserGame;
    private DateBrick dateBrick;
    private DateCell dateCell;
    private DataCombat dataCombat;
    private int valuescrapuse = 0;
    private int valueenergyuse = 0;

    private static InformationProfile ourInstance = new InformationProfile();

    public static InformationProfile getInstance() {
        return ourInstance;
    }

    private InformationProfile() {
        dateUser = new DateUser();
        dateUserGame = new DateUserGame();
        dateBrick = new DateBrick();
        dateCell = new DateCell();
        dataCombat = new DataCombat();
    }

    public Date getReadyForNext() {
        return readyForNextAction;
    }

    public void setReadyForNext(String readyForNext) {
        Calendar cal = GregorianCalendar.getInstance();
        String s= readyForNext.replace("Z","+00:00");
        Date date =null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(s);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.readyForNextAction = date;
    }

    public int getResursed(int val){
        switch (val){
            case 0:{
                return dateUserGame.getScrap() - valuescrapuse;
            }
            case 1:{
                return dateBrick.getNumber() - dateBrick.getNumberBrickUsage();
            }
            case 2:{
                return  dateCell.getCellNumber();
            }
            default:{
                return 0;
            }
        }
    }

    public int getUsageScrap(MaterialsCraft mat){
        switch (mat){
            case BRICKS:{
                return dateBrick.getRateScrap();
            }
            case CELLS:{
                return dateCell.getCellRateScrap();
            }
            default:{
                return -1;
            }
        }
    }

    public boolean isIncombat() {
        return incombat;
    }

    public void setIncombat(boolean incombat) {
        this.incombat = incombat;
    }

    public DateUser getDateUser() {
        return dateUser;
    }

    public DateUserGame getDateUserGame() {
        return dateUserGame;
    }

    public DateBrick getDateBrick() {
        return dateBrick;
    }

    public DateCell getDateCell() {
        return dateCell;
    }

    public boolean ckeckscrap(int v){
        if(getDateUserGame().getScrap() >= (valuescrapuse+v)){
            return true;
        }else
            return false;
    }

    public void setValuescrapuse(int valuescrapuse) {
        this.valuescrapuse = valuescrapuse;
    }

    public int getValuescrapuse() {
        return valuescrapuse;
    }

    public int getValueenergyuse() {
        return valueenergyuse;
    }

    public void setValueenergyuse(int valueenergyuse) {
        this.valueenergyuse = valueenergyuse;
    }

    public DataCombat getDataCombat() {
        return dataCombat;
    }
}
