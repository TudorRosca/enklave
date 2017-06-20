package com.enklave.game.LoadResursed;

import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;

/**
 * Created by adrian on 08.03.2016.
 */
public class AssetsCrafting {
    private AssetManager manager;
    public AssetsCrafting() {
        manager = new AssetManager();
//        manager.load(NameFiles.craftingbuttonNext,Texture.class);
//        manager.load(NameFiles.craftingbuttonnextchek,Texture.class);
//        manager.load(NameFiles.craftingbuttonback,Texture.class);
//        manager.load(NameFiles.craftingbuttonbackcheck,Texture.class);
//        manager.load(NameFiles.craftingimgnext,Texture.class);
//        manager.load(NameFiles.craftingimgnextcheck,Texture.class);
//        manager.load(NameFiles.craftingimgback,Texture.class);
//        manager.load(NameFiles.craftingimgbackcheck,Texture.class);
        manager.load(NameFiles.suport3D,Model.class);
        manager.load(NameFiles.scrapModel3D, Model.class);
        manager.load(NameFiles.cellModel3D,Model.class);
        manager.load(NameFiles.brickModel3D,Model.class);
        manager.load(NameFiles.backgroundCrafting,Texture.class);
        manager.load(NameFiles.backgroundModel3d,Texture.class);
        manager.load(NameFiles.buttonCraft,Texture.class);
        manager.load(NameFiles.backgroundbuttoncreft,Texture.class);
        manager.load(NameFiles.arrowcrafting,Texture.class);
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
    public void dispose(){
        manager.clear();
        manager.dispose();
    }
}
