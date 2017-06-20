package com.enklave.game.LoadResursed;

import com.enklave.game.Interfaces.InterfaceAssetsManager;
import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by adrian on 11.03.2016.
 */
public class AssetsRooms implements InterfaceAssetsManager {
    private AssetManager manager;
    public AssetsRooms() {
        manager = new AssetManager();
        manager.load(NameFiles.topenklaveimage,Texture.class);
        manager.load(NameFiles.noselectRooms,Texture.class);
        manager.load(NameFiles.selectroombottom,Texture.class);
        manager.load(NameFiles.selectroomleft,Texture.class);
        manager.load(NameFiles.selectroomright,Texture.class);
        manager.load(NameFiles.txtSelectRoom,Texture.class);
        manager.load(NameFiles.imageArrowBottom,Texture.class);
        manager.load(NameFiles.imageArrowRight,Texture.class);
        manager.load(NameFiles.imagePulse,Texture.class);
        manager.load(NameFiles.extensionImgBackground,Texture.class);
        manager.load(NameFiles.borderImageBlue,Texture.class);
        manager.load(NameFiles.borderUpdown,Texture.class);
        manager.load(NameFiles.progressbarcircular,Texture.class);
        manager.load(NameFiles.imageBarrack,Texture.class);
        manager.load(NameFiles.imageCommCenter,Texture.class);
        manager.load(NameFiles.imageLaboratory,Texture.class);
        manager.load(NameFiles.frameEnk,Texture.class);
    }

    public Texture getTexture(String file){
        return manager.get(file, Texture.class);
    }
    @Override
    public boolean update() {
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
