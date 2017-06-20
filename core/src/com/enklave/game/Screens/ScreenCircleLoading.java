package com.enklave.game.Screens;

import com.enklave.game.GameManager;
import com.enklave.game.Interfaces.InterfaceAssetsManager;
import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ScreenCircleLoading implements Screen {
    private AssetManager assetManager;
    private Animation animationLoading;
    private GameManager gameManager;
    private SpriteBatch spriteBatch;
    private float stateTime = 0.0f;
    private InterfaceAssetsManager managerAssets = null;
    private Screen newScreen;

    public ScreenCircleLoading(GameManager gameManager,Screen startScreen,InterfaceAssetsManager assetsManager) {
        managerAssets = assetsManager;
        newScreen = startScreen;
        this.gameManager = gameManager;
        init();
    }
    public ScreenCircleLoading(){
        init();
    }

    private void init() {
        assetManager = new AssetManager();
        assetManager.load(NameFiles.loadingCircle, Texture.class);
        assetManager.finishLoading();
        Texture t = assetManager.get(NameFiles.loadingCircle, Texture.class);
        TextureRegion[] textureregion = new TextureRegion[20];
        TextureRegion[][] tmp = TextureRegion.split(t, t.getWidth()/5, t.getHeight()/4);
        int index = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                textureregion[index++] = tmp[i][j];
            }
        }
        animationLoading = new Animation(0.100f,textureregion);
        t= new Texture(0,0, Pixmap.Format.RGBA8888);
        t.dispose();
    }

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        stateTime += Gdx.graphics.getDeltaTime();
        if(managerAssets == null){
            TextureRegion currentFrame;
            currentFrame = animationLoading.getKeyFrame(stateTime, true);
            spriteBatch.begin();
            spriteBatch.draw(currentFrame, Gdx.graphics.getWidth() / 2 - Gdx.graphics.getWidth() * 0.12f, Gdx.graphics.getHeight() / 2 - Gdx.graphics.getWidth() * 0.12f, Gdx.graphics.getWidth() * 0.24f, Gdx.graphics.getWidth() * 0.24f);
            spriteBatch.end();
        }else{
            if(!managerAssets.update()) {
                TextureRegion currentFrame;
                currentFrame = animationLoading.getKeyFrame(stateTime, true);
                spriteBatch.begin();
                spriteBatch.draw(currentFrame, Gdx.graphics.getWidth() / 2 - Gdx.graphics.getWidth() * 0.12f, Gdx.graphics.getHeight() / 2 - Gdx.graphics.getWidth() * 0.12f, Gdx.graphics.getWidth() * 0.24f, Gdx.graphics.getWidth() * 0.24f);
                spriteBatch.end();
            }else {
                gameManager.setScreen(newScreen);
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        assetManager.dispose();
    }
}
