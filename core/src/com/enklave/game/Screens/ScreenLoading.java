package com.enklave.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.enklave.game.Enklave.ListEnklaves;
import com.enklave.game.FontLabel.Font;
import com.enklave.game.GameManager;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.MapsService.MapPixmap;
import com.enklave.game.Utils.NameFiles;


public class ScreenLoading implements Screen {
    private final RepeatAction translatered,translategreen,translateblue;
    private Image red,green,blue,background;
    private GameManager gameManager;
    private ManagerAssets assets;
    private Stage animred;
    int Width = Gdx.graphics.getWidth();
    int Height = Gdx.graphics.getHeight();

    public ScreenLoading(GameManager game) {
        gameManager = game;
        assets = ManagerAssets.getInstance();
        MoveToAction mred = new MoveToAction();
        mred.setPosition(Width , -Height);
        mred.setDuration(2f);
        MoveToAction mred1 = new MoveToAction();
        mred1.setPosition(-Width , Height);
        mred1.setDuration(0);
        translatered  = new RepeatAction();
        translatered.setCount(RepeatAction.FOREVER);
        translatered.setAction(new SequenceAction(new DelayAction(0.0f), mred,mred1, new DelayAction(4.0f)));
        MoveToAction mgreen = new MoveToAction();
        mgreen.setPosition(Width , -Height );
        mgreen.setDuration(2f);
        MoveToAction mgreen1 = new MoveToAction();
        mgreen1.setPosition(-Width , Height);
        mgreen1.setDuration(0);
        translategreen = new RepeatAction();
        translategreen.setCount(RepeatAction.FOREVER);
        translategreen.setAction(new SequenceAction(new DelayAction(2.0f), mgreen,mgreen1, new DelayAction(4.0f)));
        MoveToAction mblue = new MoveToAction();
        mblue.setPosition(Width , -Height);
        mblue.setDuration(2f);
        MoveToAction mblue1 = new MoveToAction();
        mblue1.setPosition(-Width , Height);
        mblue1.setDuration(0);
        translateblue = new RepeatAction();
        translateblue.setCount(RepeatAction.FOREVER);
        translateblue.setAction(new SequenceAction(new DelayAction(4.0f), mblue,mblue1,new DelayAction(4.0f)));
    }

    @Override
    public void show() {
        assets.getAssetsLoading().finish();
        animred = new Stage(new StretchViewport(Width, Height));
        red = new Image(new TextureRegion(assets.getAssetsLoading().get(NameFiles.loadtransientred)));
        red.setWidth(Width);
        red.setHeight(Height);
        red.setX(-Width);
        red.setY(Height);
        green = new Image(new TextureRegion(assets.getAssetsLoading().get(NameFiles.loadtransientgreen)));
        green.setWidth(Width);
        green.setHeight(Height);
        green.setX(-Width);
        green.setY(Height);
        blue = new Image(new TextureRegion(assets.getAssetsLoading().get(NameFiles.loadtransientblue)));
        blue.setWidth(Width);
        blue.setHeight(Height);
        blue.setX(-Width);
        blue.setY(Height);
        red.addAction(translatered);
        green.addAction(translategreen);
        blue.addAction(translateblue);
        animred.addActor(green);
        animred.addActor(blue);
        animred.addActor(red);
        Texture tex = assets.getAssetsLoading().get(NameFiles.loadoverlayshape);
        background = new Image(tex);
        Vector2 crop = Scaling.fit.apply(tex.getWidth(),tex.getHeight(),Width,Height);
        background.setSize(crop.x*1.8f, crop.y*1.78f);
        background.setPosition(Width / 2 - background.getWidth() / 2, Height / 2 - background.getHeight() / 2);
        background.toFront();
        animred.addActor(background);
        Label textLoading = new Label("Loading...", new Label.LabelStyle(Font.getFont((int) (Height * 0.025)), Color.WHITE));
        textLoading.setPosition(Width / 2 - textLoading.getWidth() / 2, Height * 0.15f);
        tex = assets.getAssetsLoading().get(NameFiles.logoTextENKLAVE);
        Image lg= new Image(tex);
        crop = Scaling.fit.apply(tex.getWidth(),tex.getHeight(),Width,Height);
        lg.setSize(crop.x*0.35f, crop.y*0.35f);
        lg.setPosition(Width / 2 - lg.getWidth() / 2, Height * 0.2f);
        animred.addActor(textLoading);
        animred.addActor(lg);
        tex = new Texture(0,0, Pixmap.Format.RGBA8888);
        tex.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//        Gdx.app.log("download"+assets.updateScreenLoading(),"fl  "+MapPixmap.getInstance().flagSignal.isDownloadimg()+"list enk   "+ListEnklaves.getInstance().isSetat());
        if(!assets.updateScreenLoading() || !MapPixmap.getInstance().flagSignal.isDownloadimg() || !ListEnklaves.getInstance().isSetat()) {
            animred.act(Gdx.graphics.getDeltaTime());
            animred.draw();
        }else{
            gameManager.setScreen(gameManager.mapsScreen);
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
        animred.dispose();
        //assets.getAssetsLoading().
    }
}
