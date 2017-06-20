package com.enklave.game.LoadResursed;

import com.enklave.game.Interfaces.InterfaceAssetsManager;
import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by adrian on 14.03.2016.
 */
public class AssetsCombat implements InterfaceAssetsManager{
    private AssetManager manager;
    public boolean setCombat = false;
    public AssetsCombat() {
        manager = new AssetManager();
        manager.load(NameFiles.setCombat,Texture.class);
        manager.load(NameFiles.labelCombat,Texture.class);
        manager.load(NameFiles.profileButtonimage,Texture.class);
        manager.load(NameFiles.framePlayers,Texture.class);
        manager.load(NameFiles.framedefender,Texture.class);
        manager.load(NameFiles.frameattachers,Texture.class);
        manager.load(NameFiles.imgplayerBlue,Texture.class);
        manager.load(NameFiles.imgplayerGreen,Texture.class);
        manager.load(NameFiles.imgplayerRed,Texture.class);
        manager.load(NameFiles.barLifeWhite,Texture.class);
        manager.load(NameFiles.topenklaveimage,Texture.class);
        manager.load(NameFiles.imageArrowBottom,Texture.class);
        manager.load(NameFiles.logoHeader,Texture.class);
        manager.load(NameFiles.buttonFire,Texture.class);
        manager.load(NameFiles.buttonRecharge,Texture.class);
        manager.load(NameFiles.target,Texture.class);
        manager.load(NameFiles.targetRecharge,Texture.class);
        manager.load(NameFiles.turretsimpleCombat,Texture.class);
        manager.load(NameFiles.brickLoader,Texture.class);
        manager.load(NameFiles.maskLoader,Texture.class);
        manager.load(NameFiles.enklaveblue,Texture.class);
        manager.load(NameFiles.enklavegreen,Texture.class);
        manager.load(NameFiles.enklavered,Texture.class);
        manager.load(NameFiles.progressbarcircular,Texture.class);
        manager.load(NameFiles.buttonStartCombat,Texture.class);
    }
    public Texture getTexture(String name){
        return manager.get(name,Texture.class);
    }
    public boolean update(){
        return manager.update() && setCombat;
    }
    public void dispose(){
        manager.dispose();
    }
}
