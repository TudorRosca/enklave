package com.enklave.game.LoadResursed;

import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by adrian on 15.03.2016.
 */
public class AssetsSettings {
    private AssetManager manager;
    public AssetsSettings() {
        manager = new AssetManager();
        manager.load(NameFiles.buttonSwith, Texture.class);
        manager.load(NameFiles.extensionImgBackground,Texture.class);
    }
    public void finish(){
        manager.finishLoading();
    }
    public boolean update(){
        return manager.update();
    }
    public Texture getTexture(String name){
        return manager.get(name,Texture.class);
    }
}
