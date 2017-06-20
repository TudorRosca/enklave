package com.enklave.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.enklave.game.FontLabel.Font;
import com.enklave.game.GameManager;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.Utils.NameFiles;

public class ScreenRaider implements Screen {
    private final PerspectiveCamera camera;
    private GameManager gameManager;
    private boolean draw = false;
    private int Width = Gdx.graphics.getWidth(),Height = Gdx.graphics.getHeight();
    private Stage stage;
    private ManagerAssets managerAssets;
    private Decal decalBackground;
    private DecalBatch decalBatch;
    private Group groupStage;
    private ScrollPane scrollpane;
    private ModelInstance instance;
    private ModelBatch modelBatch;
    private Environment environment;

    public ScreenRaider(GameManager gameManager) {
        this.gameManager = gameManager;
        managerAssets = ManagerAssets.getInstance();
        camera = new PerspectiveCamera(67,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        camera.position.set(0, 20, 80);
        camera.near = 1;
        camera.far = 2200;
        camera.lookAt(0, -10, 0);
        camera.update();
    }
    private void addBackground(){
        decalBackground = Decal.newDecal(new TextureRegion(managerAssets.getAssetsCrafting().getTexture(NameFiles.backgroundCrafting)));
        decalBackground.setDimensions(100,200);
        decalBackground.setPosition(0,0,0);
        environment = new Environment();
//        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
        environment.set(new ColorAttribute(ColorAttribute.Diffuse));
        environment.set(new ColorAttribute(ColorAttribute.Specular));
        environment.set(new ColorAttribute(ColorAttribute.Reflection));
        environment.add(new DirectionalLight().set(0.51f, 0.5f, 0.5f, 0f, -2f, -30f));
        Model model = managerAssets.getAssetsRaider().getModel(NameFiles.raiderFull);
        model.nodes.get(2).translation.set(-12,28.6f,-5.5f);
//        model.nodes.get(0).translation.set(0,28f,29.2f);
//        model.nodes.get(2).translation.set(0,13,-1);
        instance = new ModelInstance(model);
        instance.transform.trn(0,-20,25).rotate(0,1,0,-25);
        instance.transform.scale(1.5f,1.5f,1.5f);
    }
    private void addtoStage(){
        groupStage = new Group();
        String bup = "button-up";String bdown = "button-down";String bcheck = "button-checked";
        Skin skin = new Skin();
        TextureAtlas txta = new TextureAtlas();
        Texture txt = managerAssets.getAssetsButton().get(NameFiles.buttonBack1);
        Vector2 crop = Scaling.fit.apply(txt.getWidth(),txt.getHeight(),Width,Height);
        txta.addRegion(bup, new TextureRegion(txt));
        txta.addRegion(bdown, new TextureRegion(txt));
        txta.addRegion(bcheck, new TextureRegion(txt));
        skin.addRegions(txta);
        ImageButton.ImageButtonStyle sty = new ImageButton.ImageButtonStyle();
        sty.up = skin.getDrawable(bup);
        sty.down = skin.getDrawable(bdown);
        sty.checked = skin.getDrawable(bcheck);
        ImageButton backButton = new ImageButton(sty);
        backButton.setSize(crop.x *0.15f,crop.y * 0.15f);
        backButton.setPosition(backButton.getWidth() * 0.25f, Height - (backButton.getHeight() * 1.25f));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameManager.setScreen(gameManager.mapsScreen);
            }
        });
        groupStage.addActor(backButton);
        groupStage.addActor(InformationRaider());
        skin = new Skin();
        txta = new TextureAtlas();
        txt = new Texture(0,0, Pixmap.Format.RGBA8888);
        skin.dispose();
        txta.dispose();
        txt.dispose();
    }
    private Group InformationRaider(){
        Pixmap color = new Pixmap(10,10, Pixmap.Format.RGBA8888);
        color.setColor(new Color());
        final Image imageBackground = new Image(new TextureRegion(managerAssets.getAssetsProfile().getTexture(NameFiles.backgroundScroll)));
        imageBackground.setSize(Width,Height/5.3f);
        imageBackground.setPosition(0,0);
        Group groupInformation = new Group();
        groupInformation.addActor(imageBackground);
        final Group group = new Group();
        group.addActor(addScrollInfo("ENERGY",69,96,imageBackground.getTop()));
//        group.addActor(addScrollInfo("EXPERIENCE",profile.getDateUserGame().getExperience(),profile.getDateUserGame().getExperienceLevel(),((Group)(group.findActor("ENERGY"))).findActor("ENERGY").getTop()));
//        group.addActor(addScrollInfo("TIME ",69,666,((Group)(group.findActor("EXPERIENCE"))).findActor("EXPERIENCE").getTop()));
        group.addActor(addScrollInfo("Electronics", 35,0,((Group)(group.findActor("ENERGY"))).findActor("ENERGY").getTop()));
        group.setSize(Width,group.findActor("ENERGY").getHeight()+group.findActor("Electronics").getHeight());
        group.setPosition(0,imageBackground.getTop()-group.getHeight());
        Table tab = new Table();
        tab.add(group);
        scrollpane = new ScrollPane(tab);
        scrollpane.layout();
        scrollpane.setScrollingDisabled(true,true);
        scrollpane.setForceScroll(false,false);
        final Table table = new Table();
        table.setFillParent(false);
        table.add(scrollpane).fill().expand();
        table.setBounds(0,0,Width,imageBackground.getHeight());
        table.addListener(new ActorGestureListener(){
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if(imageBackground.getScaleY()== 1) {
                    scrollpane.setForceScroll(false,true);
                    ScaleToAction scale = new ScaleToAction();
                    scale.setScale(1, 3f);
                    scale.setDuration(1);
                    imageBackground.addAction(scale);
                    table.setBounds(0,0,Width,imageBackground.getHeight()*3);
                    MoveToAction move = new MoveToAction();
                    move.setPosition(0,(imageBackground.getTop()+imageBackground.getHeight()*2)-group.getHeight());
                    move.setDuration(1);
                    group.addAction(move);
                }
                else{
                    scrollpane.setForceScroll(false,false);
                    scrollpane.setScrollingDisabled(true,true);
                    ScaleToAction scale = new ScaleToAction();
                    scale.setScale(1, 1f);
                    scale.setDuration(1);
                    imageBackground.addAction(scale);
                    new Timer().scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            table.setBounds(0,0,Width,imageBackground.getHeight());
                        }
                    },1);
                    MoveToAction move = new MoveToAction();
                    move.setPosition(0,imageBackground.getTop()-group.getHeight());
                    move.setDuration(1);
                    group.addAction(move);
                }
            }
        });
        groupInformation.addActor(table);
        return groupInformation;
    }
    private Group addScrollInfo(String name,int value, int maxvalue,float y){
        Group group = new Group();
        group.setName(name);
        Label labelName = new Label(name,new Label.LabelStyle(Font.getFont((int)(Height*0.03)), Color.WHITE));
        labelName.setPosition(Width*0.05f,y-labelName.getHeight()*1.5f);
        Label labelvalue = new Label(""+value,new Label.LabelStyle(Font.getFont((int)(Height*0.03)),Color.ORANGE));
        labelvalue.setPosition(Width*0.95f-labelvalue.getWidth(),labelName.getY());
        group.addActor(labelvalue);
        group.addActor(labelName);
        Pixmap pixmap = new Pixmap((int) (Gdx.graphics.getHeight() * 0.1), (int) (Gdx.graphics.getHeight() * 0.0175), Pixmap.Format.RGBA8888);
        pixmap.setColor(0.521568627f, 0.992156863f, 0.290196078f, 1);
        pixmap.fill();
        Skin skin = new Skin();
        skin.add("gray", new Texture(pixmap));
        pixmap = new Pixmap((int)(Gdx.graphics.getHeight()*0.1),(int)(Gdx.graphics.getHeight()*0.0175), Pixmap.Format.RGBA8888);
        pixmap.setColor(0.160784314f, 0.184313725f, 0.184313725f, 1);
        pixmap.fill();
        skin.add("gray1", new Texture(pixmap));
        if (maxvalue != 0) {
            ProgressBar.ProgressBarStyle barStyle = new ProgressBar.ProgressBarStyle(skin.newDrawable("gray1", Color.WHITE), skin.newDrawable("gray",Color.WHITE));
            barStyle.knobBefore = barStyle.knob;
            ProgressBar bar = new ProgressBar(0, maxvalue, 1f, false, barStyle);
            bar.setSize(Gdx.graphics.getWidth() * 0.9f, Gdx.graphics.getHeight() * 0.0175f);
            bar.setPosition(Gdx.graphics.getWidth() *0.05f, labelName.getY()-bar.getHeight()*1.2f);
            bar.setAnimateDuration(5);
            bar.setValue(value);
            group.addActor(bar);
            Image sep = new Image(skin.newDrawable("gray1",Color.WHITE));
            sep.setName(name);
            sep.setSize(Width*0.9f,Height*0.002f);
            sep.setPosition(Width*0.05f,bar.getY()-bar.getHeight());
            group.addActor(sep);
            group.setSize(Width,y-sep.getY());
        }else{
            Image sep = new Image(skin.newDrawable("gray1",Color.WHITE));
            sep.setName(name);
            sep.setSize(Width*0.9f,Height*0.002f);
            sep.setPosition(Width*0.05f,labelName.getY()-labelName.getHeight()/2);
            group.addActor(sep);
            group.setSize(Width,y-sep.getY());
        }
        return group;
    }

    @Override
    public void show() {
        if(!draw){
            draw = true;
            addBackground();
            addtoStage();
        }
        stage = new Stage(new StretchViewport(Width,Height));
        decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
        stage.addActor(groupStage);
        modelBatch = new ModelBatch();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//        instance.transform.rotate(0,1,0,1);
        decalBatch.add(decalBackground);
        decalBatch.flush();
        modelBatch.begin(camera);
        modelBatch.render(instance,environment);
        modelBatch.end();
        stage.act();
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

    }
    @Override
    public void dispose() {

    }
}
