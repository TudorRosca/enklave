package com.enklave.game.LoadResursed;

import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;

/**
 * Created by adrian on 04.03.2016.
 */
public class AssetsButton {
    private AssetManager manager;
    public AssetsButton() {
        manager = new AssetManager();
        manager.load(NameFiles.buttonLeft, Texture.class);
        manager.load(NameFiles.buttonRight,Texture.class);
        manager.load(NameFiles.buttontwoLeft,Texture.class);
        manager.load(NameFiles.button_describe,Texture.class);
        manager.load(NameFiles.logoEnklave,Texture.class);
        manager.load(NameFiles.buttonBack1,Texture.class);
        manager.load(NameFiles.buttonShowEnklave,Texture.class);
        manager.load(NameFiles.imageInformationEnklave,Texture.class);
        manager.load(NameFiles.buttonTabCraft,Texture.class);
        manager.load(NameFiles.imageFactionArhitects,Texture.class);
        manager.load(NameFiles.backDeployBricks,Texture.class);
        manager.load(NameFiles.buttonMinusCraft,Texture.class);
        manager.load(NameFiles.buttonPlusCraft,Texture.class);
        manager.load(NameFiles.cursorTextField,Texture.class);
        manager.load(NameFiles.logoEdenites3D, Model.class);
        manager.load(NameFiles.logoArhitects3D,Model.class);
        manager.load(NameFiles.logoPrometheans3D,Model.class);
        manager.load(NameFiles.disaibleButton,Texture.class);
        manager.load(NameFiles.buttonStartCombat,Texture.class);
    }

    public Texture get(String texture){
        return manager.get(texture,Texture.class);
    }
    public boolean update(){
        return manager.update();
    }
    public void finish(){
        manager.finishLoading();
    }
    public Model getModel(String name){
        return manager.get(name,Model.class);
    }
    public void dispose(){
        manager.clear();
        manager.dispose();
    }
    public void unload(String name){
        manager.unload(name);
    }
}
