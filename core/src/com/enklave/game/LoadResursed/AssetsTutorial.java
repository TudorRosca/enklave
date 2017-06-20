package com.enklave.game.LoadResursed;

import com.enklave.game.Interfaces.InterfaceAssetsManager;
import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by adrian on 08.03.2016.
 */
public class AssetsTutorial implements InterfaceAssetsManager {
    private AssetManager manager;
    public AssetsTutorial() {
        manager = new AssetManager();
    }
    public void load(){
        manager = new AssetManager();
        manager.load(NameFiles.tutorialProfile,Pixmap.class);
        manager.load(NameFiles.tutorialComm,Pixmap.class);
        manager.load(NameFiles.tutorialCrafting,Pixmap.class);
        manager.load(NameFiles.tutorialScrap, Pixmap.class);
        manager.load(NameFiles.tutorialEnergy,Pixmap.class);
        manager.load(NameFiles.circlePulsTutorial,Texture.class);
        manager.load(NameFiles.logoHeader,Texture.class);
        manager.load(NameFiles.labeltextfollow,Texture.class);
        manager.load(NameFiles.PulsCircleScalable,Texture.class);
    }
    public boolean update(){
        return manager.update();
    }
    public Texture getTexture(String name){
        return manager.get(name, Texture.class);
    }
    public Pixmap getPixmap (String name){
        return manager.get(name,Pixmap.class);
    }
    public void finish(){
        manager.finishLoading();
    }
    public int numbersAssets(){
        return manager.getLoadedAssets();
    }
    public void dispose(){
        manager.clear();
        manager.dispose();
    }
}
