package com.enklave.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.enklave.game.Craft.QueueBuildCraft;
import com.enklave.game.FontLabel.Font;
import com.enklave.game.Interfaces.InterfaceSelectScreen;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.Requests.GetProfile;
import com.enklave.game.Screens.Intro;
import com.enklave.game.Screens.MapsScreen;
import com.enklave.game.Screens.ScreenCombat;
import com.enklave.game.Screens.ScreenCrafting;
import com.enklave.game.Screens.ScreenDescribeEnklave;
import com.enklave.game.Screens.ScreenEnklave;
import com.enklave.game.Screens.ScreenExtensions;
import com.enklave.game.Screens.ScreenLoading;
import com.enklave.game.Screens.ScreenProfile;
import com.enklave.game.Screens.ScreenRaider;
import com.enklave.game.Screens.ScreenRooms;
import com.enklave.game.Screens.ScreenSetting;



public class GameManager extends Game implements InterfaceSelectScreen {
	public Intro intro;
    private GameManager gameManager;
    public ScreenLoading screenLoading;
    public MapsScreen mapsScreen;
    public ScreenProfile screenProfile;
    public ScreenCrafting screenCrafting;
    public ScreenEnklave screenEnklave;
    public ScreenDescribeEnklave screenDescribeEnklave;
    public ScreenExtensions screenExtensions;
    public ScreenRooms screenRooms;
    public ScreenCombat screenCombat;
    public ScreenSetting screenSetting;
    public ScreenRaider raiderMap;


    public GameManager() {
        super();
    }

    @Override
	public void create () {
        new Font();
        gameManager = (GameManager)Gdx.app.getApplicationListener();
		intro = new Intro(gameManager);
        QueueBuildCraft.getInstance().setGameManager(gameManager);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        mapsScreen = new MapsScreen(gameManager);
                        screenEnklave = new ScreenEnklave(gameManager);
                        screenProfile = new ScreenProfile(gameManager);
                        screenCrafting = new ScreenCrafting(gameManager);
                        screenDescribeEnklave = new ScreenDescribeEnklave(gameManager);
                        screenExtensions = new ScreenExtensions(gameManager);
                        screenRooms = new ScreenRooms(gameManager);
                        screenCombat = new ScreenCombat(gameManager);
                        screenSetting = new ScreenSetting(gameManager);
                        raiderMap = new ScreenRaider(gameManager);
                    }
                });
            }
        }).start();
        screenLoading = new ScreenLoading(this);
        Preferences pref = Gdx.app.getPreferences("ScreenPresentation");
        if(pref.contains("presentation")){
            if(!pref.getBoolean("presentation")) {
                setScreen(screenLoading);
                pref.putBoolean("presentation", true);
                pref.flush();
            }else{
                setScreen(screenLoading);
            }
        }else{
            setScreen(screenLoading);
            pref.putBoolean("presentation", true);
            pref.flush();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        new GetProfile().makeRequestProfile();
                    }
                });
            }
        }).start();
    }

    @Override
    public void pause() {
        if(!ManagerAssets.getInstance().updateScreenLoading()){
            System.exit(0);
        }
    }

    @Override
    public void selectsScreen(com.badlogic.gdx.Screen selectedScreen) {
        setScreen(selectedScreen);
    }

    //    public WebSocketLocal getWebSocket() {
//        return webSocket;
//    }
}
