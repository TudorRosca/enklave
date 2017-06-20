package com.enklave.game.LoadResursed;

import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;

/**
 * Created by adrian on 08.03.2016.
 */
public class AssetsProfile {
    private AssetManager manager;
    public AssetsProfile() {
        manager = new AssetManager();
        manager.load(NameFiles.profileObject, Model.class);
        manager.load(NameFiles.backgroundScroll,Texture.class);
//        manager.load(NameFiles.logoArhitects3D,Model.class);
    }
    public boolean update(){
        return manager.update();
    }
    public Texture getTexture(String name){
        return manager.get(name,Texture.class);
    }
    public Model getModel(String file){
        return manager.get(file,Model.class);
    }
    public void dispose(){
        manager.clear();
        manager.dispose();
    }
}
