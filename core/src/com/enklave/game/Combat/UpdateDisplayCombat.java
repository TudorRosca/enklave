package com.enklave.game.Combat;

import com.enklave.game.Screens.ScreenCombat;

/**
 * Created by adrian on 14.06.2016.
 */
public class UpdateDisplayCombat{
    private static UpdateDisplayCombat ourInstance = new UpdateDisplayCombat();
    private ScreenCombat screenCombat = null;

    public static UpdateDisplayCombat getInstance() {
        return ourInstance;
    }

    private UpdateDisplayCombat() {
    }

    public void setScreenCombat(ScreenCombat screenCombat) {
        this.screenCombat = screenCombat;
    }

    public void update(){
        if(screenCombat != null)
            screenCombat.updatePlayers();
    }
    public void updateMak(){
        if(screenCombat != null){
            screenCombat.maskUpdate();
        }
    }
    public void messageCombat(String message){
        screenCombat.screenExit(message);
    }
}
