package com.enklave.game.LoadResursed;

import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;

/**
 * Created by adrian on 08.03.2016.
 */
public class AssetsEnklave3D {
    private AssetManager manager;
    public AssetsEnklave3D() {
        manager = new AssetManager();
        manager.load(NameFiles.baseEnklave3D,Texture.class);
        manager.load(NameFiles.insideEnklave,Texture.class);
        manager.load(NameFiles.topEnklave3D,Texture.class);
        manager.load(NameFiles.circleEnklave,Texture.class);
//        manager.load(NameFiles.enklave3D, Model.class);
        manager.load(NameFiles.enklaveBlue3D,Model.class);
        manager.load(NameFiles.enklaveGreen3D,Model.class);
        manager.load(NameFiles.enklaveGrey3D,Model.class);
        manager.load(NameFiles.enklaveRed3D,Model.class);
    }

    public boolean update(){
        return manager.update();
    }
    public Texture getTexture(String name){
        return manager.get(name, Texture.class);
    }
    public Model getModel(String file){
        return manager.get(file,Model.class);
    }
    public void finish(){
        manager.finishLoading();
    }
    public boolean procent(String file){
        return manager.isLoaded(file);
    }
    public void dispose(){
        manager.clear();
        manager.dispose();
    }
}
