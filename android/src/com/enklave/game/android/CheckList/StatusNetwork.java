package com.enklave.game.android.CheckList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.enklave.game.GameManager;
import com.enklave.game.Screens.ScreenCircleLoading;
import com.enklave.game.WebSocket.WebSocketLocal;

public class StatusNetwork extends BroadcastReceiver {
    private static StatusNetwork ourInstance = new StatusNetwork();

    public static StatusNetwork getInstance() {
        return ourInstance;
    }

    private StatusNetwork() {
    }
    private GameManager gameManager;
    private boolean connectionLost = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkInfo net = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if(net != null){
            if(net.getType() == ConnectivityManager.TYPE_MOBILE){
                Log.d("Connection","MOBILE DATE");
            }else if(net.getType() == ConnectivityManager.TYPE_WIFI){
                Log.d("Connection","WIFI");
            }
            if(connectionLost) {
                WebSocketLocal.getInstance().reconnectSocket();
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        gameManager.setScreen(gameManager.screenLoading);
                    }
                });
            }
        }else{
            Log.d("Connection","lost");
            connectionLost = true;
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    gameManager.setScreen(new ScreenCircleLoading());
                }
            });

        }
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
}
