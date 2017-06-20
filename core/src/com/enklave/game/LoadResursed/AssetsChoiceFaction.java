package com.enklave.game.LoadResursed;

import com.enklave.game.Interfaces.InterfaceAssetsManager;
import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;

public class AssetsChoiceFaction implements InterfaceAssetsManager {
    private AssetManager manager;

    public AssetsChoiceFaction() {
    }
    public void loadResurse(){
        manager = new AssetManager();

        manager.load(NameFiles.labelEdenites,Texture.class);
        manager.load(NameFiles.labelPrometheans,Texture.class);
        manager.load(NameFiles.labelArhitects,Texture.class);
        manager.load(NameFiles.txtDescribeEdenites,Texture.class);
        manager.load(NameFiles.txtDescribePrometheans,Texture.class);
        manager.load(NameFiles.txtDescribeArhitects,Texture.class);
        manager.load(NameFiles.frameSelected,Texture.class);
        manager.load(NameFiles.labelTitle,Texture.class);
        manager.load(NameFiles.logoArhitects3D, Model.class);
        manager.load(NameFiles.logoEdenites3D,Model.class);
        manager.load(NameFiles.logoEdenites3D,Model.class);
        manager.load(NameFiles.logoPrometheans3D,Model.class);
    }
    public Texture getTexture(String file){
        return manager.get(file,Texture.class);
    }
    public Model getModel(String file){
        return manager.get(file,Model.class);
    }
    public boolean update(){
        return manager.update();
    }
    public void finish(){
        manager.finishLoading();
    }
    public void dispose(){
        manager.clear();
        manager.dispose();
    }
}
