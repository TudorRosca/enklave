package com.enklave.game.android.Activity;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.enklave.game.GameManager;
import com.enklave.game.MapsService.MapPixmap;
import com.enklave.game.R;
import com.enklave.game.Screens.ScreenChat;
import com.enklave.game.WebSocket.WebSocketLocal;
import com.enklave.game.android.CheckList.CheckLocation;
import com.enklave.game.android.CheckList.CheckNetwork;
import com.enklave.game.android.CheckList.CheckPermission;
import com.enklave.game.android.CheckList.StatusNetwork;
import com.enklave.game.android.LocalStorage.CheckDatabase;
import com.enklave.game.android.Utils.ListenerLocation;
import com.enklave.game.android.Utils.MyCompas;

public class Game extends AndroidApplication {
    private CheckPermission permission;
    private RelativeLayout gameLayout;
    private View view;
    private ListenerLocation listenerLocation = null;
    private MyCompas compas;
    private MapPixmap matrixPixmap;
    private CheckDatabase checkDatabase;
    private Window mrootwindow;
    private View mrootview;
    private int height = 0;
    private  boolean onefirst =  false;
    private GameManager game;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.Details),0);
        if(!sharedPreferences.contains("keepScrrenOn")){
            sharedPreferences.edit().putBoolean("keepScrrenOn",true);
            setKeepScreenOn();
        }else if(sharedPreferences.getBoolean("keepScrrenOn",true)){
            setKeepScreenOn();
        }
//        config.useImmersiveMode = true;
        game = new GameManager();
        gameLayout = (RelativeLayout) findViewById(R.id.game);
        view = initializeForView(game, config);
        permission = new CheckPermission();
        gameLayout.addView(view);
        compas = new MyCompas(this);
        matrixPixmap = MapPixmap.getInstance();
        checkDatabase = new CheckDatabase(getContext());
        WebSocketLocal.getInstance();
        mrootwindow = getWindow();
        mrootview = mrootwindow.getDecorView().findViewById(R.id.game);
        mrootview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                View view = mrootwindow.getDecorView();
                view.getWindowVisibleDisplayFrame(r);
                int hei = mrootwindow.getDecorView().getHeight();
                hei = hei - r.bottom;
                if (hei != height) {
                    ScreenChat.getInstance().setSizeKeyboard(hei);
                    height = hei;
                }
            }
        });
        this.registerReceiver(StatusNetwork.getInstance(),new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        StatusNetwork.getInstance().setGameManager(game);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(permission.check(this,this)) {
            if (!CheckNetwork.check(this)) {
                gameLayout.setVisibility(View.GONE);
            } else if (!CheckLocation.check(this, checkDatabase)) {
                gameLayout.setVisibility(View.GONE);
            } else {
                gameLayout.setVisibility(View.VISIBLE);
                if (listenerLocation == null) {
                    listenerLocation = new ListenerLocation(this, checkDatabase);
                }
                compas.registersensor();
            }
        }
        else{
            gameLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        compas.unregistersensor();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]>-1){
            gameLayout.setVisibility(View.VISIBLE);
        }else{
            finish();
            System.exit(0);
        }
    }
    public void setKeepScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
