package com.enklave.game.LoadResursed;

import com.enklave.game.Interfaces.InterfaceAssetsManager;
import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by adrian on 11.03.2016.
 */
public class AssetsExtension implements InterfaceAssetsManager{
    private AssetManager manager;
    public AssetsExtension() {
        manager = new AssetManager();
        manager.load(NameFiles.imageExtensionGammaShield,Texture.class);
        manager.load(NameFiles.imageExtensionRegularShield,Texture.class);
        manager.load(NameFiles.imageExtensionRemoteTurret,Texture.class);
        manager.load(NameFiles.imageExtensionAutoTurret,Texture.class);
        manager.load(NameFiles.frameCarousel,Texture.class);
        manager.load(NameFiles.imageArrowRight,Texture.class);
        manager.load(NameFiles.imageArrowBottom,Texture.class);
        manager.load(NameFiles.txtSelectExtension,Texture.class);
        manager.load(NameFiles.imagePulse,Texture.class);
        manager.load(NameFiles.extensionImgBackground,Texture.class);
        manager.load(NameFiles.leftselectextension,Texture.class);
        manager.load(NameFiles.rightselectextension,Texture.class);
        manager.load(NameFiles.bottomselectextension,Texture.class);
        manager.load(NameFiles.centerselectextension,Texture.class);
        manager.load(NameFiles.noselectextension,Texture.class);
        manager.load(NameFiles.topenklaveimage,Texture.class);
        manager.load(NameFiles.cornerBG ,Texture.class);
        manager.load(NameFiles.progressbarcircular,Texture.class);
    }
    public Texture getTexture(String name){
        return manager.get(name,Texture.class);
    }
    public boolean update(){
        return manager.update();
    }
    public void dispose(){
        manager.clear();
        manager.dispose();
    }
}
