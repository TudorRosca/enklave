package com.enklave.game.LoadResursed;

import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by adrian on 04.03.2016.
 */
public class AssetsIntro {
    private AssetManager manager;

    public AssetsIntro() {
        manager = new AssetManager();
        manager.load(NameFiles.intro_sprite_enklave_logo, Texture.class);
        manager.load(NameFiles.intro_sprite_enklave,Texture.class);
        manager.load(NameFiles.intro_logo_center,Texture.class);
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
    public void dispose(){
        manager.clear();
        manager.dispose();
    }
}
