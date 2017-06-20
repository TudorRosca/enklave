package com.enklave.game.LoadResursed;

import com.enklave.game.Interfaces.InterfaceAssetsManager;
import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by adrian on 10.03.2016.
 */
public class AssetsDescribeEnklave implements InterfaceAssetsManager {
    private AssetManager manager;
    public AssetsDescribeEnklave() {
        manager = new AssetManager();
        manager.load(NameFiles.buttonExtendCollapse,Texture.class);
        manager.load(NameFiles.frameEnk,Texture.class);
        manager.load(NameFiles.profileButtonimage,Texture.class);
        manager.load(NameFiles.buttonViewGallery,Texture.class);
        manager.load(NameFiles.buttonFavoriteEnklave,Texture.class);
        manager.load(NameFiles.proportionEnklave,Texture.class);
        manager.load(NameFiles.profileButtonimage,Texture.class);
        manager.load(NameFiles.cornerBG,Texture.class);
        manager.load(NameFiles.imageBarrack,Texture.class);
        manager.load(NameFiles.imageCommCenter,Texture.class);
        manager.load(NameFiles.imageLaboratory,Texture.class);
        manager.load(NameFiles.imageExtensionAutoTurret,Texture.class);
        manager.load(NameFiles.imageExtensionRemoteTurret,Texture.class);
        manager.load(NameFiles.imageExtensionRegularShield,Texture.class);
        manager.load(NameFiles.imageExtensionGammaShield,Texture.class);
    }
    public boolean update(){
        return manager.update();
    }
    public Texture getTexture(String name){
        return manager.get(name, Texture.class);
    }
    public void finish(){
        manager.finishLoading();
    }
    public void dispose(){
        manager.clear();
        manager.dispose();
    }
}
