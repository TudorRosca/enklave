package com.enklave.game.Screens;

import com.enklave.game.FontLabel.Font;
import com.enklave.game.GameManager;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.Profile.InformationProfile;
import com.enklave.game.Requests.JoinFaction;
import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class ScreenChoiceFaction implements Screen {
    private GameManager gameManager;
    private ManagerAssets manager;
    private int WIDTH = Gdx.graphics.getWidth(),HEIGHT = Gdx.graphics.getHeight();
    private Group group;
    private Stage stage;
    private Image frameEdinetes,framePrometheans,frameArhitects;
    private int selectId = 0;
    private Array<ModelInstance> intances;
    private ModelBatch modelBatch;
    private PerspectiveCamera camera;

    public ScreenChoiceFaction(GameManager gameManager) {
        this.gameManager = gameManager;
        manager = ManagerAssets.getInstance();
        group = new Group();
        intances = new Array<ModelInstance>();
        camera = new PerspectiveCamera(65,WIDTH,HEIGHT);
        camera.position.set(-175,0,550);
        camera.lookAt(0, 0, 0);
        camera.near = 1;
        camera.far = 700;
        camera.update();
    }

    private void make(){
        Texture txt = manager.getAssetsChoiceFaction().getTexture(NameFiles.labelTitle);
        Vector2 crop = Scaling.fit.apply(txt.getWidth(), txt.getHeight(), WIDTH, HEIGHT);
        Image labelTile = new Image(new TextureRegion(txt));
        labelTile.setSize(crop.x * 0.5f, crop.y * 0.5f);
        labelTile.setPosition(WIDTH / 2 - labelTile.getWidth() / 2, HEIGHT * 0.9f);
        group.addActor(labelTile);

        group.addActor(makeArhitects());
        group.addActor(makePrometheans());
        group.addActor(makeEdenites());

        Skin skin = new Skin();
        TextureAtlas txta = new TextureAtlas();
        txta.addRegion("bup", new TextureRegion(manager.getAssetsChoiceFaction().getTexture(NameFiles.frameSelected)));
        skin.addRegions(txta);
        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle();
        style.up = skin.getDrawable("bup");
        style.checked = skin.getDrawable("bup");
        style.down = skin.getDrawable("bup");
        style.font= Font.getFont((int) (HEIGHT * 0.035f));
        ImageTextButton btn = new ImageTextButton("ADVANCE",style);
        btn.setSize(WIDTH * 0.4f, HEIGHT * 0.08f);
        btn.setPosition(WIDTH / 2 - btn.getWidth() / 2, HEIGHT * 0.05f);
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectId != 0) {
                    InformationProfile.getInstance().getDateUserGame().setFaction(selectId);
                    new JoinFaction().makeRequest(selectId);
                    gameManager.setScreen(gameManager.screenLoading);
                }
            }
        });
        group.addActor(btn);
        addObject3D();
    }

    private Group makeEdenites() {
        Group gr = new Group();
        ImageButton imgEdenites = new ImageButton(new ImageButton.ImageButtonStyle());
        imgEdenites.setSize(WIDTH * 0.18f, WIDTH * 0.18f);
        imgEdenites.setPosition(WIDTH * 0.075f, HEIGHT * 0.2f);
        imgEdenites.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                deselect();
                frameEdinetes.setVisible(true);
                selectId = 3;
            }
        });
        gr.addActor(imgEdenites);
        Texture txt = manager.getAssetsChoiceFaction().getTexture(NameFiles.labelEdenites);
        Vector2 crop = Scaling.fit.apply(txt.getWidth(), txt.getHeight(), WIDTH, HEIGHT);
        Image labelfaction = new Image(new TextureRegion(txt));
        labelfaction.setSize(crop.x * 0.2f, crop.y * 0.2f);
        labelfaction.setPosition(imgEdenites.getRight() + imgEdenites.getWidth() * 0.2f, imgEdenites.getTop() - labelfaction.getHeight());
        labelfaction.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                deselect();
                frameEdinetes.setVisible(true);
                selectId = 3;
            }
        });
        gr.addActor(labelfaction);
        txt = manager.getAssetsChoiceFaction().getTexture(NameFiles.txtDescribeEdenites);
        crop = Scaling.fit.apply(txt.getWidth(), txt.getHeight(), WIDTH, HEIGHT);
        Image img = new Image(new TextureRegion(txt));
        img.setSize(crop.x * 0.65f, crop.y * 0.65f);
        img.setPosition(labelfaction.getX(), labelfaction.getY() - img.getHeight() * 1.3f);
        img.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                deselect();
                frameEdinetes.setVisible(true);
                selectId = 3;
            }
        });
        gr.addActor(img);
        txt = manager.getAssetsChoiceFaction().getTexture(NameFiles.frameSelected);
        crop = Scaling.fit.apply(txt.getWidth(), txt.getHeight(), WIDTH, HEIGHT);
        frameEdinetes = new Image(new TextureRegion(txt));
        frameEdinetes.setSize(crop.x * 0.9f, imgEdenites.getTop() - img.getY() + img.getWidth() * 0.25f);
        frameEdinetes.setPosition(WIDTH / 2 - frameEdinetes.getWidth() / 2, img.getY() - img.getWidth() * 0.125f);
        frameEdinetes.setVisible(false);
        gr.addActor(frameEdinetes);
        return gr;
    }

    protected Group makePrometheans() {
        Group gr=new Group();
        ImageButton imgprometheans = new ImageButton(new ImageButton.ImageButtonStyle());
        imgprometheans.setSize(WIDTH * 0.18f, WIDTH * 0.18f);
        imgprometheans.setPosition(WIDTH * 0.075f, HEIGHT * 0.475f);
        imgprometheans.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                deselect();
                framePrometheans.setVisible(true);
                selectId = 1;
            }
        });
        gr.addActor(imgprometheans);
        Texture txt = manager.getAssetsChoiceFaction().getTexture(NameFiles.labelPrometheans);
        Vector2 crop = Scaling.fit.apply(txt.getWidth(), txt.getHeight(), WIDTH, HEIGHT);
        Image labelfaction = new Image(new TextureRegion(txt));
        labelfaction.setSize(crop.x * 0.3f, crop.y * 0.3f);
        labelfaction.setPosition(imgprometheans.getRight() + imgprometheans.getWidth() * 0.2f, imgprometheans.getTop() - labelfaction.getHeight());
        labelfaction.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                deselect();
                framePrometheans.setVisible(true);
                selectId = 1;
            }
        });
        gr.addActor(labelfaction);
        txt = manager.getAssetsChoiceFaction().getTexture(NameFiles.txtDescribePrometheans);
        crop = Scaling.fit.apply(txt.getWidth(), txt.getHeight(), WIDTH, HEIGHT);
        Image img = new Image(new TextureRegion(txt));
        img.setSize(crop.x * 0.65f, crop.y * 0.65f);
        img.setPosition(labelfaction.getX(), labelfaction.getY() - img.getHeight() * 1.3f);
        img.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                deselect();
                framePrometheans.setVisible(true);
                selectId = 1;
            }
        });
        gr.addActor(img);
        txt = manager.getAssetsChoiceFaction().getTexture(NameFiles.frameSelected);
        crop = Scaling.fit.apply(txt.getWidth(), txt.getHeight(), WIDTH, HEIGHT);
        framePrometheans = new Image(new TextureRegion(txt));
        framePrometheans.setSize(crop.x * 0.9f, imgprometheans.getTop() - img.getY() + img.getWidth() * 0.25f);
        framePrometheans.setPosition(WIDTH / 2 - framePrometheans.getWidth() / 2, img.getY() - img.getWidth() * 0.125f);
        framePrometheans.setVisible(false);
        gr.addActor(framePrometheans);
        return gr;
    }

    private Group makeArhitects() {
        Group gr = new Group();
        ImageButton imgarhitects = new ImageButton(new ImageButton.ImageButtonStyle());
        imgarhitects.setSize(WIDTH * 0.18f, WIDTH * 0.18f);
        imgarhitects.setPosition(WIDTH * 0.075f, HEIGHT * 0.725f);
        imgarhitects.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                deselect();
                frameArhitects.setVisible(true);
                selectId = 2;
            }
        });
        gr.addActor(imgarhitects);
        Texture txt = manager.getAssetsChoiceFaction().getTexture(NameFiles.labelArhitects);
        Vector2 crop = Scaling.fit.apply(txt.getWidth(), txt.getHeight(), WIDTH, HEIGHT);
        Image labelArhitects = new Image(new TextureRegion(txt));
        labelArhitects.setSize(crop.x * 0.24f, crop.y * 0.24f);
        labelArhitects.setPosition(imgarhitects.getRight() + imgarhitects.getWidth() * 0.2f, imgarhitects.getTop() - labelArhitects.getHeight());
        labelArhitects.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                deselect();
                frameArhitects.setVisible(true);
                selectId = 2;
            }
        });
        gr.addActor(labelArhitects);
        txt = manager.getAssetsChoiceFaction().getTexture(NameFiles.txtDescribeArhitects);
        crop = Scaling.fit.apply(txt.getWidth(), txt.getHeight(), WIDTH, HEIGHT);
        Image img = new Image(new TextureRegion(txt));
        img.setSize(crop.x * 0.65f, crop.y * 0.65f);
        img.setPosition(labelArhitects.getX(), labelArhitects.getY() - img.getHeight() * 1.3f);
        img.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                deselect();
                frameArhitects.setVisible(true);
                selectId = 2;
            }
        });
        gr.addActor(img);
        txt = manager.getAssetsChoiceFaction().getTexture(NameFiles.frameSelected);
        crop = Scaling.fit.apply(txt.getWidth(), txt.getHeight(), WIDTH, HEIGHT);
        frameArhitects = new Image(new TextureRegion(txt));
        frameArhitects.setSize(crop.x * 0.9f, imgarhitects.getTop() - img.getY() + img.getWidth() * 0.25f);
        frameArhitects.setPosition(WIDTH / 2 - frameArhitects.getWidth() / 2, img.getY() - img.getWidth() * 0.125f);
        frameArhitects.setVisible(false);
        gr.addActor(frameArhitects);
        return gr;
    }

    public void addObject3D(){
        Model model = manager.getAssetsChoiceFaction().getModel(NameFiles.logoArhitects3D);
        ModelInstance instance = new ModelInstance(model);
        instance.transform.rotate(1, 0, 0, 90);
        instance.transform.translate(-160, 0, -175);
        intances.add(instance);
        model = manager.getAssetsChoiceFaction().getModel(NameFiles.logoEdenites3D);
        instance = new ModelInstance(model);
        instance.transform.rotate(1, 0, 0, 90);
        instance.transform.translate(-160, 0, 185);
        intances.add(instance);
        model = manager.getAssetsChoiceFaction().getModel(NameFiles.logoPrometheans3D);
        instance = new ModelInstance(model);
        instance.transform.rotate(1,0,0,90);
        instance.transform.translate(-160, 0, 0);
        intances.add(instance);
    }

    private void deselect(){
        frameArhitects.setVisible(false);
        frameEdinetes.setVisible(false);
        framePrometheans.setVisible(false);
    }

    @Override
    public void show() {
        manager.loadAssetsChoiceFaction();
        manager.getAssetsChoiceFaction().finish();
        make();
        stage = new Stage(new StretchViewport(WIDTH,HEIGHT));
        modelBatch = new ModelBatch();
        stage.addActor(group);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
        for(int i=0;i<intances.size;i++){
            intances.get(i).transform.rotate(0, 0, 1, 1);
        }
        modelBatch.begin(camera);
        modelBatch.render(intances);
        modelBatch.end();
        Gdx.input.setInputProcessor(stage);
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
        stage.dispose();
        modelBatch.dispose();
        intances.clear();
        manager.getAssetsChoiceFaction().dispose();
    }
}
