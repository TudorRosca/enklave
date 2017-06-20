package com.enklave.game.LoadResursed;

import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by adrian on 17.05.2016.
 */
public class AssetsScreenChat {
    private AssetManager manager;

    public AssetsScreenChat() {
        manager = new AssetManager();
        manager.load(NameFiles.backgroundchat,Texture.class);
        manager.load(NameFiles.backgroundchatbottom,Texture.class);
        manager.load(NameFiles.backgroundchatmiddle,Texture.class);
        manager.load(NameFiles.backgroundchattop,Texture.class);
        manager.load(NameFiles.btnchatfadein,Texture.class);
        manager.load(NameFiles.btnchatfadeout,Texture.class);
        manager.load(NameFiles.btnchatsend,Texture.class);
        manager.load(NameFiles.btnchattabmenu,Texture.class);
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
