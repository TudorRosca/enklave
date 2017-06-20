package com.enklave.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
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
import com.enklave.game.Enum.Menu;
import com.enklave.game.FontLabel.Font;
import com.enklave.game.GameManager;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.Profile.InformationProfile;
import com.enklave.game.Utils.NameFiles;

public class ScreenProfile implements Screen {
    private GameManager gameManager;
    private boolean drawone = true;
    private PerspectiveCamera camera;
    private Environment environment;
    private ModelInstance instance;
    private ImageTextButton b1;
    private Table ta;
    private ImageButton backButton;
    private Stage stage;
    private ModelBatch modelBatch;
    private ManagerAssets managerAssets;
    private ScrollPane sp;
    private Decal decalBackground;
    private DecalBatch batchDecal;
    private int Width = Gdx.graphics.getWidth(),Height = Gdx.graphics.getHeight();
    private Group groupUser,groupInformation;
    private InformationProfile profile;
    private QueueDisplay queueDisplay;
    private ScrollPane scrollpane;
    private Table table;

    public ScreenProfile(GameManager game) {
        this.gameManager = game;
        managerAssets = ManagerAssets.getInstance();
        profile = InformationProfile.getInstance();
        camera = new PerspectiveCamera(67,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        camera.position.set(0, 17f, 63);
        camera.near = 1;
        camera.far = 2200;
        camera.lookAt(0, -5, 0);
        camera.update();
    }

    private void drawProfile() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
        environment.add(new DirectionalLight().set(1f, 1f, 1f, 1f, 1f, 1f));
        Model model = managerAssets.getAssetsProfile().getModel(NameFiles.profileObject);
        model.nodes.get(2).translation.set(0,-14.5f,9);
        instance = new ModelInstance(model);
        instance.transform.scale(0.2f,0.2f,0.2f);
        instance.transform.trn(0,-8f,27).rotate(0,1,0,55);
        decalBackground = Decal.newDecal(new TextureRegion(managerAssets.getAssetsCrafting().getTexture(NameFiles.backgroundCrafting)));
        decalBackground.setDimensions(75,150);
        decalBackground.setPosition(0,0,0);
        queueDisplay = QueueDisplay.getInstance();
    }

    private void addInfoUser(){
        groupUser = new Group();
        Texture txt;
        Color color;
        switch (InformationProfile.getInstance().getDateUserGame().getFaction()){
            case 1:{
                txt = managerAssets.getAssetsMaps().getTexture(NameFiles.imageFactionPrometheans);
                color= Color.RED;
                break;
            }
            case 2:{
                txt = managerAssets.getAssetsMaps().getTexture(NameFiles.imageFactionArhitects);
                color= Color.BLUE;
                break;
            }
            case 3:{
                txt = managerAssets.getAssetsMaps().getTexture(NameFiles.imageFactionEdenites);
                color= Color.GREEN;
                break;
            }
            default:{
                txt = managerAssets.getAssetsMaps().getTexture(NameFiles.imageFactionArhitects);
                color= Color.GRAY;
                break;
            }
        }
        Image imgFaction = new Image(new TextureRegion(txt));
        imgFaction.setSize(backButton.getWidth(),backButton.getHeight());
        imgFaction.setPosition(Width - imgFaction.getWidth()*1.25f,Height - imgFaction.getHeight()*1.25f);
        groupUser.addActor(imgFaction);
        BitmapFont bt = Font.getFont((int)(Height*0.035));
        Label labelUsername = new Label(InformationProfile.getInstance().getDateUser().getFristName(),new Label.LabelStyle(bt,color));
        labelUsername.setPosition(imgFaction.getX()-labelUsername.getWidth()*1.2f,imgFaction.getY()+imgFaction.getHeight()/2);
        groupUser.addActor(labelUsername);
        Label labelLevel = new Label("L"+InformationProfile.getInstance().getDateUserGame().getLevel(),new Label.LabelStyle(bt,Color.GOLD));
        labelLevel.setPosition(imgFaction.getX()-labelLevel.getWidth()*1.2f,imgFaction.getY());
        groupUser.addActor(labelLevel);
        txt= new Texture(0,0, Pixmap.Format.RGBA8888);
        txt.dispose();
    }

    private void addInformation(){
        groupInformation = new Group();
        Pixmap color = new Pixmap(10,10, Pixmap.Format.RGBA8888);
        color.setColor(new Color());
        final Image imageBackground = new Image(new TextureRegion(managerAssets.getAssetsProfile().getTexture(NameFiles.backgroundScroll)));
        imageBackground.setSize(Width,Height/5.5f);
        imageBackground.setPosition(0,Height * 0.04f);
        groupInformation.addActor(imageBackground);
        final Group group = new Group();
        group.addActor(addScrollInfo("MASS",200,1000,0));
        group.addActor(addScrollInfo("BASIC DAMAGE",100,0,((Group)(group.findActor("MASS"))).findActor("MASS").getTop()));
        group.addActor(addScrollInfo("DISTANCE TRAVELED (km)", (int) profile.getDateUserGame().getDistanceWalked(),0,((Group)(group.findActor("BASIC DAMAGE"))).findActor("BASIC DAMAGE").getTop()));
        group.addActor(addScrollInfo("SKILL 1",69,666,((Group)(group.findActor("DISTANCE TRAVELED (km)"))).findActor("DISTANCE TRAVELED (km)").getTop()));
        group.addActor(addScrollInfo("EXPERIENCE",profile.getDateUserGame().getExperience(),profile.getDateUserGame().getExperienceLevel(),((Group)(group.findActor("SKILL 1"))).findActor("SKILL 1").getTop()));
        group.addActor(addScrollInfo("ENERGY",profile.getDateUserGame().getEnergy(),profile.getDateUserGame().getEnergyLevel(),((Group)(group.findActor("EXPERIENCE"))).findActor("EXPERIENCE").getTop()));
        group.setSize(Width,group.findActor("ENERGY").getHeight()+group.findActor("EXPERIENCE").getHeight()+group.findActor("SKILL 1").getHeight()+group.findActor("DISTANCE TRAVELED (km)").getHeight()+group.findActor("BASIC DAMAGE").getHeight()+group.findActor("MASS").getHeight());
        group.setPosition(0,imageBackground.getTop()-group.getHeight());
        final Table tab = new Table();
        tab.add(group);
        scrollpane = new ScrollPane(tab);
        scrollpane.layout();scrollpane.setFillParent(true);
        scrollpane.setScrollingDisabled(true,false);
        table = new Table();
        table.setFillParent(false);
        table.add(scrollpane).fill().expand();
        table.setBounds(0,Height * 0.04f,Width,imageBackground.getHeight());
        table.addListener(new ActorGestureListener(){
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if(imageBackground.getScaleY()== 1) {
                    table.remove();
                    group.setPosition(0,imageBackground.getTop() - group.getHeight());
                    groupInformation.addActor(group);
                    ScaleToAction scale = new ScaleToAction();
                    scale.setScale(1, 2.5f);
                    scale.setDuration(1);
                    imageBackground.addAction(scale);
                    final MoveToAction move = new MoveToAction();
                    move.setPosition(0,imageBackground.getTop()+imageBackground.getHeight() *1.5f -group.getHeight());
                    move.setDuration(1);
                    group.addAction(move);
                    new Timer().scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            group.setPosition(0,imageBackground.getTop() + imageBackground.getHeight() * 1.3f - group.getHeight());
                            table.setBounds(0,Height * 0.04f,Width,imageBackground.getHeight()*2.5f);
                            scrollpane.setForceScroll(false,true);
                            scrollpane.layout();
                            groupInformation.removeActor(group);
                            tab.addActor(group);
                            groupInformation.addActor(table);
                        }
                    },1);
                }
                else{
                    ScaleToAction scale = new ScaleToAction();
                    scale.setScale(1, 1f);
                    scale.setDuration(1.15f);
                    imageBackground.addAction(scale);
                    MoveToAction move = new MoveToAction();
                    move.setPosition(0,imageBackground.getTop()-group.getHeight());
                    move.setDuration(1);
                    group.addAction(move);
                    new Timer().scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            table.setBounds(0,Height * 0.04f,Width,Height/5.5F);
                            tab.removeActor(group);
                            group.setPosition(0,imageBackground.getTop() - group.getHeight());
                            scrollpane.setFillParent(true);
                            tab.add(group);
                        }
                    },1);
                }
            }
        });
        groupInformation.addActor(table);
    }

    private Group addScrollInfo(String name,int value, int maxvalue,float y){
        Group group = new Group();
        group.setName(name);
        Pixmap pixmap = new Pixmap((int) (Gdx.graphics.getHeight() * 0.1), (int) (Gdx.graphics.getHeight() * 0.0175), Pixmap.Format.RGBA8888);
        pixmap.setColor(0.521568627f, 0.992156863f, 0.290196078f, 1);
        pixmap.fill();
        Skin skin = new Skin();
        skin.add("gray", new Texture(pixmap));
        pixmap = new Pixmap((int)(Gdx.graphics.getHeight()*0.1),(int)(Gdx.graphics.getHeight()*0.0175), Pixmap.Format.RGBA8888);
        pixmap.setColor(0.160784314f, 0.184313725f, 0.184313725f, 1);
        pixmap.fill();
        skin.add("gray1", new Texture(pixmap));
        Image sep = new Image(skin.newDrawable("gray1",Color.WHITE));
        sep.setSize(Width*0.9f,Height*0.002f);
        sep.setPosition(Width*0.05f,y+sep.getHeight()*5);
        group.addActor(sep);
        if (maxvalue != 0) {
            ProgressBar.ProgressBarStyle barStyle = new ProgressBar.ProgressBarStyle(skin.newDrawable("gray1", Color.WHITE), skin.newDrawable("gray",Color.WHITE));
            barStyle.knobBefore = barStyle.knob;
            ProgressBar bar = new ProgressBar(0, maxvalue, 1f, false, barStyle);
            bar.setSize(Gdx.graphics.getWidth() * 0.9f, Gdx.graphics.getHeight() * 0.0175f);
            bar.setPosition(Gdx.graphics.getWidth() *0.05f, sep.getTop()+bar.getHeight()*1.2f);
            bar.setAnimateDuration(5);
            bar.setValue(value);
            group.addActor(bar);
            group.setSize(Width,y-sep.getY());
            Label labelName = new Label(name,new Label.LabelStyle(Font.getFont((int)(Height*0.03)),Color.WHITE));
            labelName.setName(name);
            labelName.setPosition(Width*0.05f,bar.getTop()+labelName.getHeight()*0.25f);
            Label labelvalue = new Label(""+value,new Label.LabelStyle(Font.getFont((int)(Height*0.03)),Color.ORANGE));
            labelvalue.setPosition(Width*0.95f-labelvalue.getWidth(),labelName.getY());
            group.addActor(labelvalue);
            group.addActor(labelName);
            group.setSize(Width,labelName.getTop() - y);
        }else{
            Label labelName = new Label(name,new Label.LabelStyle(Font.getFont((int)(Height*0.03)),Color.WHITE));
            labelName.setName(name);
            labelName.setPosition(Width*0.05f,sep.getTop()+labelName.getHeight()*0.25f);
            Label labelvalue = new Label(""+value,new Label.LabelStyle(Font.getFont((int)(Height*0.03)),Color.ORANGE));
            labelvalue.setPosition(Width*0.95f-labelvalue.getWidth(),labelName.getY());
            group.addActor(labelvalue);
            group.addActor(labelName);
            group.setSize(Width,labelName.getTop() - y);
        }
        return group;
    }

    private void initializeMenuTab() {
        String bup = "button-up";String bdown = "button-down";String bcheck = "button-checked";
        final ButtonGroup<Button> group = new ButtonGroup<Button>();
        group.setMaxCheckCount(1);
        group.setMinCheckCount(1);
        Skin skin = new Skin();
        TextureAtlas txta = new TextureAtlas();
        Texture texture = managerAssets.getAssetsButton().get(NameFiles.buttonTabCraft);
        txta.addRegion(bup, new TextureRegion(texture,0,0,texture.getWidth(),texture.getHeight()/2));
        txta.addRegion(bdown, new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight() / 2));
        txta.addRegion(bcheck, new TextureRegion(texture, 0, texture.getHeight() / 2, texture.getWidth(), texture.getHeight() / 2));
        skin.addRegions(txta);
        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle();
        style.up = skin.getDrawable(bup);
        style.down = skin.getDrawable(bdown);
        style.checked = skin.getDrawable(bcheck);
        style.font = Font.getFont((int)(Gdx.graphics.getHeight()*0.025f));

        b1=new ImageTextButton(String.valueOf(Menu.CHARACTER),style);
        b1.setPosition(0, 0);
        b1.setSize(Width * 0.37f, Height * 0.04f);
        group.add(b1);

        ImageTextButton b2 = new ImageTextButton(String.valueOf(Menu.CRAFTING),style);
        b2.setPosition(b1.getRight(), 0);
        b2.setSize(Width * 0.37f, Height * 0.04f);
        group.add(b2);
        ImageTextButton b3 = new ImageTextButton(String.valueOf(Menu.TACMAP),style);
        b3.setPosition(b2.getRight(), 0);
        b3.setSize(Width * 0.37f, Height * 0.04f);
        group.add(b3);
        ImageTextButton b4 = new ImageTextButton(String.valueOf(Menu.STORE),style);
        b4.setPosition(b3.getRight(), 0);
        b4.setSize(Width * 0.37f, Height * 0.04f);
        group.add(b4);
        ImageTextButton b5 = new ImageTextButton(String.valueOf(Menu.SETTING),style);
        b5.setPosition(b4.getRight(), 0);
        b5.setSize(Width * 0.37f, Height * 0.04f);
        group.add(b5);
        Table t = new Table();
        t.add(b1).width(b1.getWidth()).height(b1.getHeight());
        t.add(b2).width(b2.getWidth()).height(b2.getHeight());
        t.add(b3).width(b3.getWidth()).height(b3.getHeight());
        t.add(b4).width(b4.getWidth()).height(b4.getHeight());
        t.add(b5).width(b5.getWidth()).height(b5.getHeight());
        t.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Menu aux = Menu.valueOf(((ImageTextButton) group.getChecked()).getLabel().getText().toString());
                switch (aux) {
                    case CRAFTING: {
                        gameManager.setScreen(gameManager.screenCrafting);
                        break;
                    }
                    case SETTING: {
                        gameManager.setScreen(gameManager.screenSetting);
                        break;
                    }
                    default: {
                        Gdx.app.log("select:", " " + ((ImageTextButton) group.getChecked()).getLabel().getText());
                        break;
                    }
                }
            }
        });
        sp = new ScrollPane(t);
        sp.layout();
        sp.setScrollingDisabled(false, true);

        sp.setFillParent(true);
        ta = new Table();
        ta.setFillParent(false);
        ta.add(sp).fill().expand();
        ta.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() * 0.04f);
        //button back
        skin = new Skin();
        txta = new TextureAtlas();
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
        backButton = new ImageButton(sty);
        backButton.setSize(crop.x *0.13f,crop.y * 0.13f);
        backButton.setPosition(backButton.getWidth() * 0.25f, Height - (backButton.getHeight() * 1.25f));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameManager.setScreen(gameManager.mapsScreen);
            }
        });
        skin = new Skin();
        txta = new TextureAtlas();
        txt = new Texture(0,0, Pixmap.Format.RGBA8888);
        texture = new Texture(0,0, Pixmap.Format.RGBA8888);
        texture.dispose();
        skin.dispose();
        txta.dispose();
        txt.dispose();
    }

    @Override
    public void show() {
        if(drawone) {
            initializeMenuTab();
            drawProfile();
            addInformation();
            drawone = false;
        }
        addInfoUser();
        sp.setScrollPercentX(0);
        sp.updateVisualScroll();
        b1.setChecked(true);
        stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight())){
            @Override
            public boolean keyDown(int keyCode) {
                if(keyCode == Input.Keys.BACK)
                    gameManager.setScreen(gameManager.mapsScreen);
                return super.keyDown(keyCode);
            }
        };
        stage.addActor(backButton);
        stage.addActor(groupUser);
        stage.addActor(groupInformation);
        stage.addActor(ta);
        queueDisplay.AddtoStage(stage);
        batchDecal = new DecalBatch(new CameraGroupStrategy(camera));
        modelBatch = new ModelBatch();
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        batchDecal.add(decalBackground);
        batchDecal.flush();
        instance.transform.rotate(0, 1, 0, 1);
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
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        batchDecal.dispose();
        modelBatch.dispose();
    }
}
