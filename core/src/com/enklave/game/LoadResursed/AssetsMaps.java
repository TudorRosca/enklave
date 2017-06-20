package com.enklave.game.LoadResursed;

import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by adrian on 07.03.2016.
 */
public class AssetsMaps {
    private AssetManager manager;

    public AssetsMaps() {
        manager = new AssetManager();
        manager.load(NameFiles.imgPossition, Texture.class);
        manager.load(NameFiles.imgCirclePos , Texture.class);
        manager.load(NameFiles.buttonChat,Texture.class);
        manager.load(NameFiles.buttoncrafting,Texture.class);
        manager.load(NameFiles.imageFactionEdenites,Texture.class);
        manager.load(NameFiles.imageFactionPrometheans,Texture.class);
        manager.load(NameFiles.imageFactionArhitects,Texture.class);
        manager.load(NameFiles.CursorPositionBlue, Texture.class);
        manager.load(NameFiles.CursorPositionGreen,Texture.class);
        manager.load(NameFiles.CursorPositionRed,Texture.class);
    }
    public boolean update(){
        return manager.update();
    }

    public Texture getTexture(String name){
        return manager.get(name,Texture.class);
    }

    public void dispose(){
        manager.clear();
        manager.dispose();
    }
}
