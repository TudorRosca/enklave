package com.enklave.game.LoadResursed;

import com.enklave.game.Utils.NameSound;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by adrian on 04.03.2016.
 */
public class AssetsSound {
    private AssetManager manager;

    public AssetsSound() {
        manager = new AssetManager();
        manager.load(NameSound.fx_glitch3mp3,Sound.class);
        manager.load(NameSound.fx_glitch4mp3,Sound.class);
        manager.load(NameSound.fx_glitch5mp3,Sound.class);
        manager.load(NameSound.tr_escape_changes_survivemp3,Sound.class);
        manager.load(NameSound.tr_escape_countingsecondsmp3,Sound.class);
        manager.load(NameSound.tr_escape_radioactiveogg,Sound.class);
    }
    public boolean update(){
        return manager.update();
    }
    public Sound get(String file){
        return manager.get(file,Sound.class);
    }
}
