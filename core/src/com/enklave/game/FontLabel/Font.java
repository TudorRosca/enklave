package com.enklave.game.FontLabel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Created by adrian on 10.02.2016.
 */
public class Font {
    private static SmartFontGenerator fontGen;
    private static FileHandle exoFile;

    public Font() {
        fontGen = new SmartFontGenerator();
        exoFile = Gdx.files.internal("FontLabel/LiberationMono-Regular.ttf");
    }
    public static BitmapFont getFont(int size){
        return fontGen.createFont(exoFile,"exo-small"+size,size);
    }
}
