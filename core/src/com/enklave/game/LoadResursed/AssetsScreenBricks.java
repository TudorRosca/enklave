package com.enklave.game.LoadResursed;

import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by adrian on 15.03.2016.
 */
public class AssetsScreenBricks {
    private AssetManager manager;

    public AssetsScreenBricks() {
        manager = new AssetManager();
        manager.load(NameFiles.bricksMaps,Texture.class);
        manager.load(NameFiles.circleEnklave,Texture.class);
        manager.load(NameFiles.insideEnklave,Texture.class);
        manager.load(NameFiles.topEnklave3D,Texture.class);
        manager.load(NameFiles.baseEnklave3D,Texture.class);
    }
    public Texture getTexture(String name){
        return manager.get(name,Texture.class);
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
