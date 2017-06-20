package com.enklave.game.LoadResursed;

import com.enklave.game.Interfaces.InterfaceAssetsManager;
import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Model;

/**
 * Created by adrian on 28.04.2016.
 */
public class AssetsRaider implements InterfaceAssetsManager {
    private AssetManager manager;
    public AssetsRaider() {
        manager = new AssetManager();
        manager.load(NameFiles.raiderMap, Model.class);
    }
    public void RaiderMap(){

    }
    public void updateRaider(){
        manager.load(NameFiles.raiderFull,Model.class);
    }
    public boolean update(){
        return manager.update();
    }
    public Model getModel(String file){
        return manager.get(file,Model.class);
    }
    public void dispose(){
        manager.clear();
        manager.dispose();
    }
    public void unload(String file){
        manager.unload(file);
    }
}
