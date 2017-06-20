package com.enklave.game.LoadResursed;

import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by adrian on 12.05.2016.
 */
public class AssetsFadeInActions {
    AssetManager manager;
    public AssetsFadeInActions() {
        manager = new AssetManager();
        manager.load(NameFiles.btnfadeaction, Texture.class);
        manager.load(NameFiles.queuebackFadein,Texture.class);
        manager.load(NameFiles.btnStopaction,Texture.class);
        manager.load(NameFiles.iconfinishaction,Texture.class);
        manager.load(NameFiles.iconcurrentaction,Texture.class);
        manager.load(NameFiles.iconnextaction,Texture.class);
    }
    public boolean update(){
        return manager.update();
    }
    public Texture getTexture(String name){
        return manager.get(name,Texture.class);
    }
    public void dispose(){
        manager.clear();
        manager.dispose();
    }
}
