package com.enklave.game.LoadResursed;

import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;


public class AssetsLoadingN {
    private AssetManager manager;

    public AssetsLoadingN() {
        manager =new AssetManager();
        manager.load(NameFiles.loadoverlayshape, Texture.class);
        manager.load(NameFiles.loadtransientblue,Texture.class);
        manager.load(NameFiles.loadtransientgreen,Texture.class);
        manager.load(NameFiles.loadtransientred,Texture.class);
        manager.load(NameFiles.logoTextENKLAVE,Texture.class);
    }
    public boolean update(){
        return manager.update();
    }
    public Texture get(String file){
        return manager.get(file, Texture.class);
    }
    public void finish(){
        manager.finishLoading();
    }
}
