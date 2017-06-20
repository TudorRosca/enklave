package com.enklave.game.Screens;

import com.enklave.game.FontLabel.Font;
import com.enklave.game.GameManager;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


public class Intro implements Screen {
    private final Image bg;
    private final ManagerAssets managerassets;
    private Vector2 cropanim1 ,cropanim2;
    private Stage stage;
    private GameManager gameManager;
    private Texture texture1;
    private Texture texture2;
    private Animation animation1;
    private Animation animation2;
    private SpriteBatch spriteBatch;
    private float stateTime = 0;
    int WIDTH = Gdx.graphics.getWidth();
    int HEIGHT = Gdx.graphics.getHeight();

    public Intro(final GameManager gameManager) {
        this.gameManager = gameManager;
        managerassets = ManagerAssets.getInstance();
        managerassets.loadAssetsIntro();
        managerassets.getAssetsIntro().finish();
        texture1 =managerassets.getAssetsIntro().get(NameFiles.intro_sprite_enklave_logo);
        cropanim1 = new Vector2(Scaling.fit.apply(texture1.getWidth()/5,texture1.getHeight()/3,WIDTH,HEIGHT));
        TextureRegion[] textureregion = new TextureRegion[15];
        TextureRegion[][] tmp = TextureRegion.split(texture1, texture1.getWidth()/5, texture1.getHeight()/3);
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                textureregion[index++] = tmp[i][j];
            }
        }
        animation1 = new Animation(0.100f, textureregion);
        texture2 =managerassets.getAssetsIntro().get(NameFiles.intro_sprite_enklave);
        cropanim2 = new Vector2(Scaling.fit.apply(texture2.getWidth() / 6, texture2.getHeight() / 4, WIDTH, HEIGHT));
        textureregion = new TextureRegion[24];
        tmp = TextureRegion.split(texture2, texture2.getWidth()/6, texture2.getHeight()/4);
        index=0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                textureregion[index++] = tmp[i][j];
            }
        }
        animation2 = new Animation(0.100f, textureregion);
        Texture txt = managerassets.getAssetsIntro().get(NameFiles.intro_logo_center);
        bg = new Image(new TextureRegion(txt));
        Vector2 crop = Scaling.fit.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        bg.setSize(crop.x*0.65f,crop.y*0.65f);
        bg.setX((WIDTH / 2) - (bg.getWidth() / 2));
        bg.setY((HEIGHT / 2) - (bg.getHeight() / 2));
        managerassets.getAssetsButton().finish();
        String bup = "button-up";String bdown = "button-down";String bcheck = "button-checked";
        Skin skin =new Skin();
        TextureAtlas txta = new TextureAtlas();
        txt = managerassets.getAssetsButton().get(NameFiles.buttonLeft);
        txta.addRegion(bup, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 2 - 1));
        txta.addRegion(bdown, new TextureRegion(txt, 0, txt.getHeight() / 2 - 1, txt.getWidth(), txt.getHeight() / 2));
        txta.addRegion(bcheck, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 2 - 1));
        skin.addRegions(txta);
        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle();
        style.up = skin.getDrawable(bup);
        style.down = skin.getDrawable(bdown);
        style.checked = skin.getDrawable(bcheck);
        style.font = Font.getFont((int) (HEIGHT * 0.03f));
        ImageTextButton exit= new ImageTextButton("EXIT",style);//(new TextureRegion(assets.get(namefiles.intro_button_exit, Texture.class)));
        crop = Scaling.fit.apply(txt.getWidth(),txt.getHeight()/2,WIDTH,HEIGHT);
        exit.setSize(crop.x * 0.3f, crop.y * 0.3f);
        exit.setPosition(WIDTH / 2 - exit.getWidth() + (WIDTH * 0.028f), HEIGHT * 0.2f);

        skin = new Skin();
        txt = managerassets.getAssetsButton().get(NameFiles.buttonRight);
        txta = new TextureAtlas();
        txta.addRegion(bup, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 2 ));
        txta.addRegion(bdown, new TextureRegion(txt, 0, txt.getHeight() / 2, txt.getWidth(), txt.getHeight() / 2));
        txta.addRegion(bcheck, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 2));
        skin.addRegions(txta);
        style = new ImageTextButton.ImageTextButtonStyle();
        style.up = skin.getDrawable(bup);
        style.down = skin.getDrawable(bdown);
        style.checked = skin.getDrawable(bcheck);
        style.font = Font.getFont((int) (HEIGHT * 0.03f));
        ImageTextButton play= new ImageTextButton("PLAY",style);
        play.setSize(crop.x * 0.3f, crop.y * 0.3f);
        //play.setSize(WIDTH * 0.35f, HEIGHT * 0.08f);
        play.setPosition(WIDTH / 2 - WIDTH * 0.028f, HEIGHT * 0.2f);
        txt = managerassets.getAssetsButton().get(NameFiles.logoEnklave);
        Image logo = new Image(new TextureRegion(txt));
        crop = Scaling.fit.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        logo.setSize(crop.x * 0.09f, crop.y * 0.09f);
        logo.setPosition(WIDTH / 2 - logo.getWidth() / 2, play.getY() + HEIGHT * 0.006f);
        bg.setVisible(false);

        skin = new Skin();
        skin.dispose();
        txt = new Texture(0,0, Pixmap.Format.RGBA8888);
        txt.dispose();
        txta = new TextureAtlas();
        txta.dispose();
        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameManager.setScreen(new ScreenBricks(gameManager));
            }
        });
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        stage.addActor(bg);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame;
        if (stateTime >= 2 && stateTime < 5) {
//                if(!soundintro.isPlaying())
//                {
//                    soundintro.setMymusic(Gdx.audio.newMusic(Gdx.files.internal("sounds/fx-glitch4.mp3")));
//                    soundintro.play();
//                }
            currentFrame = animation1.getKeyFrame(stateTime, true);
            spriteBatch.begin();
            spriteBatch.draw(currentFrame, WIDTH/2-cropanim1.x/2, HEIGHT/2-cropanim1.y/2, cropanim1.x, cropanim1.y);
            spriteBatch.end();
        } else if (stateTime >= 5 && stateTime < 8) {
            bg.setVisible(true);
//                if (!musicintro.isPlaying()){
//                    musicintro.setMymusic(musicintro.randomintro());
//                    musicintro.play();
//                }
        }
        if (stateTime >= 8 && stateTime < 10) {
            bg.setVisible(false);
//                if(!soundintro.isPlaying())
//                {
//                    soundintro.setMymusic(Gdx.audio.newMusic(Gdx.files.internal("sounds/fx-glitch5.mp3")));
//                    soundintro.play();
//                }
//                if(musicintro.isPlaying())
//                {
//                    musicintro.setVolume(50);
//                }
            currentFrame = animation2.getKeyFrame(stateTime, true);
            spriteBatch.begin();
            spriteBatch.draw(currentFrame, WIDTH/2-cropanim2.x/2, HEIGHT/2-cropanim2.y/2, cropanim2.x, cropanim2.y);
            spriteBatch.end();
        } else if (stateTime > 10 && stateTime < 15) {
            bg.setVisible(true);
//                if(musicintro.isPlaying())
//                {
//                    musicintro.setVolume(100);
//                }
            currentFrame = animation2.getKeyFrame(stateTime,false);  // #16
            spriteBatch.begin();
            spriteBatch.draw(currentFrame, WIDTH/2-cropanim2.x/2, HEIGHT/2-cropanim2.y/2, cropanim2.x, cropanim2.y);
            spriteBatch.end();
            if(stateTime>11){
                gameManager.setScreen(new ScreenBricks(gameManager));
            }
        }
//        if (stateTime > 8) {
//                if (!musicintro.isPlaying()){
//                    musicintro.setMymusic(musicintro.randomintro());
//                    musicintro.play();
//                }
//        }
        stage.draw();
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
        texture1.dispose();
        texture2.dispose();
        stage.dispose();
        managerassets.getAssetsIntro().dispose();
    }
}
