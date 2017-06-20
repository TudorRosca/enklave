package com.enklave.game.Screens;

import com.enklave.game.FontLabel.Font;
import com.enklave.game.GameManager;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.MapsService.MapPixmap;
import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * Created by adrian on 11.03.2016.
 */
public class ScreenExtensions implements Screen,InputProcessor,GestureDetector.GestureListener {
    private final PerspectiveCamera camera;
    private final CameraInputController controller;
    private final OrthographicCamera cam;
    private final SpriteBatch batchsprite;
    private final GestureDetector gd;
    private final ScreenExtensions addExtensions;
    private final ImmediateModeRenderer20 renderer;
    private final ManagerAssets manager;
    private Texture lookup;
    private GameManager gameManager;
    boolean drawone = true;
    private DecalBatch batch;
    private Group groupCarousel,arrayGroup[],groupbtnRoomExtension,groupBtn,groupbtnselect,grouptimerbuild;
    private ImageTextButton buttonExtension;
    private Stage stage;
    private InputMultiplexer inputmulti;
    private Decal pulsImage,decalBackground,decalenklave,selectimage;
    private Label labelprocent;
    private boolean createcarousel = true,timerbuildroom = false;
    private int selectRoom = 0;
    private float p = 1f,v = -1f;
    private CameraGroupStrategy cameraGroupStrategy;
    private boolean tr = false;

    public ScreenExtensions(GameManager gameManager) {
        this.gameManager = gameManager;
        manager = ManagerAssets.getInstance();
        addExtensions = this;
        camera = new PerspectiveCamera(40, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 0, Gdx.graphics.getHeight() * 0.183f);//initial 180
        camera.lookAt(0, 0, 0);
        camera.far = 2500;
        camera.near = 1;
        camera.update();
        controller = new CameraInputController(camera);
        cam = new OrthographicCamera();
        batchsprite = new SpriteBatch();

        renderer = new ImmediateModeRenderer20(false, true, 1);
        gd = new GestureDetector(this);
    }

    private void createCarouselExtension() {
        lookup = manager.getAssetsExtension().getTexture(NameFiles.progressbarcircular);
        lookup.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        groupCarousel = new Group();
        arrayGroup = new Group[4];
        arrayGroup[2] = addoneExtension("GAMMA SHIELD", NameFiles.imageExtensionGammaShield,9,8,5,7,23);
        arrayGroup[2].setScale(0.8f);
        arrayGroup[2].setPosition(Gdx.graphics.getWidth() * 0.1f, 0);
        groupCarousel.addActor(arrayGroup[2]);
        arrayGroup[3] = addoneExtension("SHIELD",NameFiles.imageExtensionRegularShield,7.5,8,4,6,23);
        arrayGroup[3].setScale(0.8f);
        arrayGroup[3].setPosition(Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.28f);
        groupCarousel.addActor(arrayGroup[3]);
        arrayGroup[1] = addoneExtension("AUTO TURRET", NameFiles.imageExtensionAutoTurret,9,5,7,3,8);
        arrayGroup[1].setScale(0.8f);
        arrayGroup[1].setPosition(Gdx.graphics.getWidth() * 0.1f, -Gdx.graphics.getHeight() * 0.1f);
        groupCarousel.addActor(arrayGroup[1]);
        arrayGroup[0] = addoneExtension("REMOTE TURRET", NameFiles.imageExtensionRemoteTurret, 5, 2, 8.5, 7,45);
        groupCarousel.addActor(arrayGroup[0]);
        groupCarousel.setVisible(false);
    }

    private Group addoneExtension(String nameex,String namePhoto, double precision,double damage,double firerate,double strength,int number) {
        Group extensiongroup = new Group();
        BitmapFont bt = Font.getFont((int) (Gdx.graphics.getHeight() * 0.02f));
        Image frame = new Image(new TextureRegion(manager.getAssetsExtension().getTexture(NameFiles.frameCarousel)));
        frame.setName("frame");
        frame.setSize(Gdx.graphics.getWidth() * 0.63f, Gdx.graphics.getHeight() * 0.65f);
        frame.setPosition(Gdx.graphics.getWidth() / 2 - frame.getWidth() / 2, Gdx.graphics.getHeight() * 0.235f);
        extensiongroup.addActor(frame);
        Image border = new Image(new TextureRegion(manager.getAssetsExtension().getTexture(NameFiles.cornerBG)));
        border.setSize(Gdx.graphics.getWidth() * 0.61f, Gdx.graphics.getHeight() * 0.1f);
        border.setPosition(Gdx.graphics.getWidth() / 2 - border.getWidth() / 2, Gdx.graphics.getHeight() * 0.4f);
        extensiongroup.addActor(border);
        Label nameextension = new Label(nameex,new Label.LabelStyle(Font.getFont((int)(Gdx.graphics.getHeight()*0.03)), Color.WHITE));
        nameextension.setSize(Gdx.graphics.getWidth() * 0.4f, Gdx.graphics.getHeight() * 0.1f);
        nameextension.setPosition(border.getX() * 1.1f, Gdx.graphics.getHeight() * 0.39f);
        extensiongroup.addActor(nameextension);
        Label labelprecion = new Label("Precision",new Label.LabelStyle(bt,Color.WHITE));
        labelprecion.setSize(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() * 0.1f);
        labelprecion.setPosition(border.getX() * 1.2f, Gdx.graphics.getHeight() * 0.34f);
        extensiongroup.addActor(labelprecion);
        Label labeldamage = new Label("Damage",new Label.LabelStyle(bt,Color.WHITE));
        labeldamage.setSize(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() * 0.1f);
        labeldamage.setPosition(border.getX() * 1.2f, Gdx.graphics.getHeight() * 0.3f);
        extensiongroup.addActor(labeldamage);
        Label labelfirerate = new Label("Fire Rate",new Label.LabelStyle(bt,Color.WHITE));
        labelfirerate.setSize(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() * 0.1f);
        labelfirerate.setPosition(border.getX() * 1.2f, Gdx.graphics.getHeight() * 0.26f);
        extensiongroup.addActor(labelfirerate);
        Label labelstrenght = new Label("Strenght",new Label.LabelStyle(bt,Color.WHITE));
        labelstrenght.setSize(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() * 0.1f);
        labelstrenght.setPosition(border.getX() * 1.2f, Gdx.graphics.getHeight() * 0.22f);
        extensiongroup.addActor(labelstrenght);
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap((int)(Gdx.graphics.getHeight()*0.0052), (int)(Gdx.graphics.getHeight()*0.0052), Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(1f, 0.5529f, 0.117647f, 1f));
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        skin.add("green", new Texture(pixmap));
        ProgressBar.ProgressBarStyle barStyle = new ProgressBar.ProgressBarStyle(skin.newDrawable("white",Color.GRAY ), skin.newDrawable("green",Color.WHITE));
        barStyle.knobBefore = barStyle.knob;
        ProgressBar bar = new ProgressBar(0, 10, 0.5f, false, barStyle);
        bar.setSize(Gdx.graphics.getWidth() * 0.28f, Gdx.graphics.getHeight() * 0.015625f);
        bar.setPosition(Gdx.graphics.getWidth() / 2.3f, Gdx.graphics.getHeight() * 0.38f);
        bar.setAnimateDuration(5);
        bar.setValue((float) precision);
        extensiongroup.addActor(bar);
        ProgressBar bardamage = new ProgressBar(0, 10, 0.5f, false, barStyle);
        bardamage.setSize(Gdx.graphics.getWidth() * 0.28f, Gdx.graphics.getHeight() * 0.015625f);
        bardamage.setPosition(Gdx.graphics.getWidth() / 2.3f, Gdx.graphics.getHeight() * 0.34f);
        bardamage.setAnimateDuration(5);
        bardamage.setValue((float) damage);
        extensiongroup.addActor(bardamage);
        ProgressBar barfirerate = new ProgressBar(0, 10, 0.5f, false, barStyle);
        barfirerate.setSize(Gdx.graphics.getWidth() * 0.28f, Gdx.graphics.getHeight() * 0.015625f);
        barfirerate.setPosition(Gdx.graphics.getWidth() / 2.3f, Gdx.graphics.getHeight() * 0.3f);
        barfirerate.setAnimateDuration(5);
        barfirerate.setValue((float) firerate);
        extensiongroup.addActor(barfirerate);
        ProgressBar barstrenght = new ProgressBar(0, 10, 0.5f, false, barStyle);
        barstrenght.setSize(Gdx.graphics.getWidth() * 0.28f, Gdx.graphics.getHeight() * 0.015625f);
        barstrenght.setPosition(Gdx.graphics.getWidth() / 2.3f, Gdx.graphics.getHeight() * 0.26f);
        barstrenght.setAnimateDuration(5);
        barstrenght.setValue((float) strength);
        extensiongroup.addActor(barstrenght);
        Image slotextension = new Image(new TextureRegion(manager.getAssetsExtension().getTexture(namePhoto)));
        slotextension.setSize(Gdx.graphics.getWidth() * 0.61f, Gdx.graphics.getHeight() * 0.349f);
        slotextension.setPosition(Gdx.graphics.getWidth() / 2 - slotextension.getWidth() / 2, Gdx.graphics.getHeight() * 0.485f);
        extensiongroup.addActor(slotextension);
        Label labelnumber = new Label(""+number,new Label.LabelStyle(Font.getFont((int)(Gdx.graphics.getHeight()*0.035)),Color.WHITE));
        labelnumber.setSize(Gdx.graphics.getWidth() * 0.08f, Gdx.graphics.getWidth() * 0.1f);
        labelnumber.setPosition(frame.getRight() - labelnumber.getWidth(), frame.getTop() - labelnumber.getHeight());
        extensiongroup.addActor(labelnumber);
        skin= new Skin();
        skin.dispose();
        pixmap = new Pixmap(0,0, Pixmap.Format.RGBA8888);
        pixmap.dispose();
        return extensiongroup;
    }

    private void addButtonBottom() {
        groupbtnRoomExtension = new Group();
        String bup = "button-up",bdown = "button-down", bcheck = "button-checked";
        Skin skin = new Skin();
        TextureAtlas txta = new TextureAtlas();
        Texture txt = manager.getAssetsButton().get(NameFiles.buttontwoLeft);
        txta.addRegion(bup, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 2));
        txta.addRegion(bdown, new TextureRegion(txt, 0, txt.getHeight() / 2, txt.getWidth(), txt.getHeight() / 2));
        txta.addRegion(bcheck, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 2));
        skin.addRegions(txta);
        ImageTextButton.ImageTextButtonStyle sty = new ImageTextButton.ImageTextButtonStyle();
        sty.up = skin.getDrawable(bup);
        sty.down = skin.getDrawable(bdown);
        sty.checked = skin.getDrawable(bcheck);
        sty.font = Font.getFont((int)(Gdx.graphics.getHeight()*0.03));
        ImageTextButton buttonRoom = new ImageTextButton("BACK",sty);
        buttonRoom.setSize(Gdx.graphics.getWidth() * 0.4f, Gdx.graphics.getHeight() * 0.1f);
        buttonRoom.setPosition(Gdx.graphics.getWidth() / 2 - buttonRoom.getWidth() + Gdx.graphics.getWidth() * 0.028f, Gdx.graphics.getHeight() * 0.05f);
        txt = manager.getAssetsButton().get(NameFiles.buttonRight);
        txta = new TextureAtlas();
        txta.addRegion(bup, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 2));
        txta.addRegion(bdown, new TextureRegion(txt, 0, txt.getHeight() / 2, txt.getWidth(), txt.getHeight() / 2));
        txta.addRegion(bcheck, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 2));
        skin = new Skin();
        skin.addRegions(txta);
        sty = new ImageTextButton.ImageTextButtonStyle();
        sty.up = skin.getDrawable(bup);
        sty.down = skin.getDrawable(bdown);
        sty.checked = skin.getDrawable(bcheck);
        sty.font = Font.getFont((int) (Gdx.graphics.getHeight() * 0.03));
        buttonExtension = new ImageTextButton(" Select\n  Extension",sty);
        buttonExtension.setSize(Gdx.graphics.getWidth() * 0.4f, Gdx.graphics.getHeight() * 0.1f);
        buttonExtension.setPosition(Gdx.graphics.getWidth() / 2 - Gdx.graphics.getWidth() * 0.028f, Gdx.graphics.getHeight() * 0.05f);
        Image logonew = new Image(new TextureRegion(manager.getAssetsExtension().getTexture(NameFiles.imageArrowRight)));
        logonew.setSize(buttonExtension.getHeight() * 0.85f, buttonExtension.getHeight() * 0.85f);
        logonew.setPosition(Gdx.graphics.getWidth() / 2 - logonew.getWidth() / 2, Gdx.graphics.getHeight() * 0.058f);

        buttonRoom.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttonExtension.getText().toString().contentEquals(" Select\n  Extension")) {
                    gameManager.setScreen(gameManager.screenEnklave);
                } else {
                    buttonExtension.setText(" Select\n  Extension");
                    groupbtnselect.setTouchable(Touchable.enabled);
                    groupCarousel.setVisible(false);
                    inputmulti.removeProcessor(addExtensions);
                    inputmulti.removeProcessor(gd);
                }
            }
        });
        buttonExtension.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (createcarousel) {
                    createCarouselExtension();
                    addtimerbuildroom();
                    createcarousel = false;
                }

                if (buttonExtension.getText().toString().contentEquals(" Install\n  Extension")) {
                    timerbuildroom = true;
                    stage.addActor(grouptimerbuild);
                    //grouptimerbuild.toFront();
                    grouptimerbuild.setVisible(true);
                    groupbtnRoomExtension.setTouchable(Touchable.disabled);
                    p = 1f;
                    inputmulti.removeProcessor(addExtensions);
                    inputmulti.removeProcessor(gd);
                } else if (selectRoom != 0) {
                    stage.addActor(groupCarousel);
                    groupbtnRoomExtension.toFront();

                    buttonExtension.setText(" Install\n  Extension");
                    groupbtnselect.setTouchable(Touchable.disabled);
                    groupCarousel.setVisible(true);

                    inputmulti.addProcessor(gd);
                    inputmulti.addProcessor(addExtensions);
                }
            }
        });
        groupbtnRoomExtension.addActor(buttonRoom);
        groupbtnRoomExtension.addActor(buttonExtension);
        groupbtnRoomExtension.addActor(logonew);
        groupbtnRoomExtension.toFront();
        skin = new Skin();
        skin.dispose();
        txt = new Texture(0,0, Pixmap.Format.RGBA8888);
        txt.dispose();
        txta = new TextureAtlas();
        txta.dispose();
    }

    private void addBackgroundExtension() {
        Image stobottom = new Image(new TextureRegion(manager.getAssetsExtension().getTexture(NameFiles.imageArrowBottom)));
        stobottom.setSize(Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getWidth() * 0.22f);
        stobottom.setPosition(Gdx.graphics.getWidth() / 2 - stobottom.getWidth() / 2, Gdx.graphics.getHeight() * 0.74f);
        Image selectroomtext = new Image(new TextureRegion(manager.getAssetsExtension().getTexture(NameFiles.txtSelectExtension)));
        selectroomtext.setSize(Gdx.graphics.getWidth() * 0.44f, Gdx.graphics.getHeight() * 0.12f);
        selectroomtext.setPosition(Gdx.graphics.getWidth() / 2 - selectroomtext.getWidth() / 2, Gdx.graphics.getHeight() * 0.815f);
        RepeatAction repeatActioan = new RepeatAction();
        MoveToAction fadedown = new MoveToAction();
        fadedown.setPosition(Gdx.graphics.getWidth() / 2 - stobottom.getWidth() / 2, Gdx.graphics.getHeight() * 0.71f);
        fadedown.setDuration(0.5f);
        MoveToAction fadeup = new MoveToAction();
        fadeup.setPosition(Gdx.graphics.getWidth() / 2 - stobottom.getWidth() / 2, Gdx.graphics.getHeight() * 0.74f);
        fadeup.setDuration(1f);
        repeatActioan.setAction(new SequenceAction(fadedown, fadeup));
        repeatActioan.setCount(RepeatAction.FOREVER);
        stobottom.addAction(repeatActioan);
        groupBtn = new Group();
        groupBtn.addActor(stobottom);
        groupBtn.addActor(selectroomtext);
    }
    public void addPuls(){
        pulsImage = Decal.newDecal(new TextureRegion(manager.getAssetsExtension().getTexture(NameFiles.imagePulse)));
        pulsImage.setDimensions(Gdx.graphics.getHeight()*0.0104f, Gdx.graphics.getHeight()*0.0104f);
        pulsImage.setPosition(0, 0, Gdx.graphics.getHeight()*0.001f);
        pulsImage.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    private void addtimerbuildroom(){
        grouptimerbuild = new Group();
        Image bg = new Image(new TextureRegion(manager.getAssetsExtension().getTexture(NameFiles. extensionImgBackground)));
        bg.setSize(Gdx.graphics.getWidth() / 2.75f, Gdx.graphics.getHeight() * 0.33f);
        bg.setPosition(Gdx.graphics.getWidth() / 2 - bg.getWidth() / 2, Gdx.graphics.getHeight()*0.35f);
        grouptimerbuild.addActor(bg);
        Label labelBuildingRoom = new Label("Installing\nExtension",new Label.LabelStyle(Font.getFont((int)(Gdx.graphics.getHeight()*0.025)),Color.WHITE));
        labelBuildingRoom.setPosition(Gdx.graphics.getWidth() / 2 - labelBuildingRoom.getWidth() / 2, Gdx.graphics.getHeight() / 1.65f);
        grouptimerbuild.addActor(labelBuildingRoom);
        labelprocent = new Label("0%",new Label.LabelStyle(new Label.LabelStyle(Font.getFont((int)(Gdx.graphics.getHeight()*0.03)),Color.WHITE)));
        labelprocent.setPosition(Gdx.graphics.getWidth() / 2 - labelprocent.getWidth() / 2, Gdx.graphics.getHeight() / 2f - labelprocent.getHeight() / 2);
        grouptimerbuild.addActor(labelprocent);
        grouptimerbuild.setVisible(false);
    }
    private void addimagebg(){
        final TextureRegion textureLeft = new TextureRegion(manager.getAssetsExtension().getTexture(NameFiles.leftselectextension));
        final TextureRegion textureRight = new TextureRegion(manager.getAssetsExtension().getTexture(NameFiles.rightselectextension));
        final TextureRegion textureBottom = new TextureRegion(manager.getAssetsExtension().getTexture(NameFiles.bottomselectextension));
        final TextureRegion texturecenter = new TextureRegion(manager.getAssetsExtension().getTexture(NameFiles.centerselectextension));
        MapPixmap imageBG = MapPixmap.getInstance();
        decalBackground = Decal.newDecal(new TextureRegion(new Texture(imageBG.getImage(1, 1))));
        decalBackground.setPosition(0, 0, 0);
        decalBackground.setDimensions(Gdx.graphics.getHeight() * 0.3125f, Gdx.graphics.getHeight() * 0.3125f);
        decalBackground.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        decalenklave = Decal.newDecal(new TextureRegion(manager.getAssetsExtension().getTexture(NameFiles.topenklaveimage)));
        decalenklave.setPosition(0, 0, Gdx.graphics.getHeight()*0.003f);
        decalenklave.setDimensions(Gdx.graphics.getHeight() * 0.105f, Gdx.graphics.getHeight() * 0.105f);
        decalenklave.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        selectimage =Decal.newDecal(new TextureRegion(manager.getAssetsExtension().getTexture(NameFiles.noselectextension)));
        selectimage.setPosition(0, 0, Gdx.graphics.getHeight()*0.005f);
        selectimage.setDimensions(Gdx.graphics.getHeight() * 0.105f, Gdx.graphics.getHeight() * 0.105f);
        selectimage.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        Button b1 = new Button(new Button.ButtonStyle());
        b1.setSize(Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getWidth() * 0.2f);
        b1.setPosition(Gdx.graphics.getWidth() / 2 - b1.getWidth() * 1.5f, Gdx.graphics.getHeight() * 0.5f);
        b1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectimage.setTextureRegion(textureLeft);
                selectRoom = 1;
            }
        });
        Button b2 = new Button(new Button.ButtonStyle());
        b2.setPosition(Gdx.graphics.getWidth() / 1.7f, Gdx.graphics.getHeight() * 0.5f);
        b2.setSize(Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getWidth() * 0.2f);
        b2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectimage.setTextureRegion(textureRight);
                selectRoom = 2;
            }
        });
        Button b3 = new Button(new Button.ButtonStyle());
        b3.setPosition(Gdx.graphics.getWidth() / 2 - b1.getWidth() / 2, Gdx.graphics.getHeight() * 0.29f);
        b3.setSize(Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getWidth() * 0.2f);
        b3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectimage.setTextureRegion(textureBottom);
                selectRoom = 3;
            }
        });
        Button b4 = new Button(new Button.ButtonStyle());
        b4.setSize(Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getWidth() * 0.2f);
        b4.setPosition(Gdx.graphics.getWidth() / 2 - b1.getWidth() / 2, Gdx.graphics.getHeight() / 2 - b4.getHeight() / 2);
        b4.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectimage.setTextureRegion(texturecenter);
                selectRoom = 4;
            }
        });
        groupbtnselect = new Group();
        groupbtnselect.addActor(b1);
        groupbtnselect.addActor(b2);
        groupbtnselect.addActor(b3);
        groupbtnselect.addActor(b4);
    }
    private void addtostage() {
        stage.addActor(groupBtn);
        stage.addActor(groupbtnselect);
        stage.addActor(groupbtnRoomExtension);
    }
    @Override
    public void show() {
        if(drawone){
            addBackgroundExtension();
            addPuls();
            addimagebg();
            addButtonBottom();
            drawone = false;
        }
        cameraGroupStrategy = new CameraGroupStrategy(camera);
        batch = new DecalBatch(cameraGroupStrategy);
        stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()));
        addtostage();
        Gdx.input.setCatchBackKey(true);
        inputmulti = new InputMultiplexer();
        inputmulti.addProcessor(stage);
        Gdx.input.setInputProcessor(inputmulti);
    }
    public void progress(float cx, float cy, float r, float thickness, float amt, Color c, Texture lookup) {
        //start and end angles
        float start = 0f;
        float end = amt * 360f;

        lookup.bind();
        renderer.begin(cam.combined, GL20.GL_TRIANGLE_STRIP);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        int segs = (int)(24 * Math.cbrt(r));
        end += 90f;
        start += 90f;
        float halfThick = thickness/2f;
        float step = 360f / segs;
        for (float angle=start; angle<(end+step); angle+=step) {
            float tc = 0.5f;
            if (angle==start)
                tc = 0f;
            else if (angle>=end)
                tc = 1f;

            float fx = MathUtils.cosDeg(angle);
            float fy = MathUtils.sinDeg(angle);

            float z = 0f;
            renderer.color(c.r, c.g, c.b, c.a);
            renderer.texCoord(tc, 1f);
            renderer.vertex(cx + fx * (r + halfThick), cy + fy * (r + halfThick), z);

            renderer.color(c.r, c.g, c.b, c.a);
            renderer.texCoord(tc, 0f);
            renderer.vertex(cx + fx * (r + -halfThick), cy + fy * (r + -halfThick), z);
        }
        renderer.end();
    }

    float count=1;
    private void scalepulse(){
        count+=0.03f;
        pulsImage.setScale(count);
        //pulsImage.set
        if(count>17){
            count=1;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        scalepulse();
        batch.add(decalBackground);
        batch.add(pulsImage);
        batch.add(decalenklave);
        batch.add(selectimage);
        batch.flush();
        stage.act();
        stage.draw();
        if(timerbuildroom) {
            float r = Gdx.graphics.getHeight()*0.078f;
            float cx = Gdx.graphics.getWidth() / 2;
            float cy = Gdx.graphics.getHeight() / 2;
            float thickness = Gdx.graphics.getHeight()*0.0105f;

            //increase percentage
            p += v * 0.15f * Gdx.graphics.getDeltaTime();
            labelprocent.setText("Done!");
            progress(cx, cy, r, thickness, 1f, Color.WHITE, lookup);
            if (p > 0f) {
                labelprocent.setText((100-Math.round(p*100))+"%");
                progress(cx, cy, r, thickness, p, Color.GRAY, lookup);
            }
            if (p < -0.5f){
                timerbuildroom = false;
                grouptimerbuild.setVisible(false);
                groupbtnselect.setTouchable(Touchable.enabled);
                groupbtnRoomExtension.setTouchable(Touchable.enabled);
                groupCarousel.setVisible(false);
                buttonExtension.setText(" Select\n  Extension");
                inputmulti.removeProcessor(addExtensions);
                inputmulti.removeProcessor(gd);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            gameManager.setScreen(gameManager.screenEnklave);
        }
    }

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho(false);
        batchsprite.setProjectionMatrix(cam.combined);
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
        stage.dispose();
        cameraGroupStrategy.dispose();
        batch.dispose();
        batchsprite.dispose();
        manager.getAssetsExtension().dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        if(pointer == 0){
            int xmin = (int) arrayGroup[0].findActor("frame").getX();
            int xmax = (int)(arrayGroup[0].findActor("frame").getRight());
            int ymin = (int)(Gdx.graphics.getHeight()-(arrayGroup[0].findActor("frame").getTop()));
            int ymax = (int) (Gdx.graphics.getHeight() - arrayGroup[0].findActor("frame").getY());
            if(x>xmin && x < xmax && y >ymin && y < ymax) {
                tr = true;
            }
        }
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        if(tr) {
            if (velocityY > 1250) {
                translatebottom(arrayGroup);
                reorderGroupDown();
            } else if (velocityY < -1250) {
                translateup(arrayGroup);
                reorderGroupup();
            }
        }
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(pointer == 0){
            int xmin = (int) arrayGroup[3].findActor("frame").localToStageCoordinates(new Vector2(0,0)).x;
            int xmax = (int)(arrayGroup[3].findActor("frame").getWidth()*0.8f+xmin);
            int ymin = (int)(Gdx.graphics.getHeight()-((arrayGroup[3].findActor("frame").localToStageCoordinates(new Vector2(0,0)).y)+(arrayGroup[3].findActor("frame").getHeight()*0.8f)));
            int ymax = (int) (Gdx.graphics.getHeight() - arrayGroup[0].findActor("frame").getTop());
            if(screenX>xmin && screenX < xmax && screenY >ymin && screenY < ymax){
                translatebottom(arrayGroup);
                reorderGroupDown();
                tr = true;
            }
            ymax = (int)(Gdx.graphics.getHeight()-arrayGroup[1].findActor("frame").localToStageCoordinates(new Vector2(0,0)).y);
            ymin = (int)(Gdx.graphics.getHeight()-arrayGroup[0].findActor("frame").getY());
            if(screenX>xmin && screenX < xmax && screenY >ymin && screenY < ymax){
                translateup(arrayGroup);
                reorderGroupup();
                tr = true;
            }
        }
        tr = false;
//        xdown = 0;
//        ydown = 0;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
    private void reorderGroupup() {
        Group aux= arrayGroup[3];
        arrayGroup[3] = arrayGroup[0];
        arrayGroup[0] = arrayGroup[1];
        arrayGroup[1] = arrayGroup[2];
        arrayGroup[2] = aux;
    }

    private void translateup(Group[] arrayGroup) {
        ScaleToAction scaleToAction = new ScaleToAction();
        scaleToAction.setScale(1, 1);
        scaleToAction.setDuration(0.4f);
        MoveToAction moveToAction = new MoveToAction();
        moveToAction.setPosition(0, 0);
        moveToAction.setDuration(0.4f);
        arrayGroup[1].toFront();
        arrayGroup[1].addAction(moveToAction);
        arrayGroup[1].addAction(scaleToAction);
        ScaleToAction scaleToAction1 = new ScaleToAction();
        scaleToAction1.setScale(0.8f, 0.8f);
        scaleToAction1.setDuration(0.4f);
        MoveToAction moveToAction1 = new MoveToAction();
        moveToAction1.setPosition(Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.28f);
        moveToAction1.setDuration(0.4f);
        arrayGroup[0].addAction(scaleToAction1);
        arrayGroup[0].addAction(moveToAction1);
        MoveToAction moveToAction2 = new MoveToAction();
        moveToAction2.setPosition(Gdx.graphics.getWidth() * 0.1f, -Gdx.graphics.getHeight() * 0.1f);
        moveToAction2.setDuration(0.4f);
        arrayGroup[2].addAction(moveToAction2);
        MoveToAction moveToAction3 = new MoveToAction();
        moveToAction3.setPosition(Gdx.graphics.getWidth() * 0.1f, 0);
        moveToAction3.setDuration(0.4f);
        arrayGroup[3].addAction(moveToAction3);
    }

    private void reorderGroupDown() {
        Group aux = arrayGroup[3];
        arrayGroup[3] = arrayGroup[2];
        arrayGroup[2] = arrayGroup[1];
        arrayGroup[1] = arrayGroup[0];
        arrayGroup[0] = aux;
    }


    private void translatebottom(Group[] group) {
        ScaleToAction scaleToAction = new ScaleToAction();
        scaleToAction.setScale(1, 1);
        scaleToAction.setDuration(0.4f);
        MoveToAction moveToAction = new MoveToAction();
        moveToAction.setPosition(0, 0);
        moveToAction.setDuration(0.4f);
        group[3].toFront();
        group[3].addAction(moveToAction);
        group[3].addAction(scaleToAction);
        ScaleToAction scaleToAction1 = new ScaleToAction();
        scaleToAction1.setScale(0.8f, 0.8f);
        scaleToAction1.setDuration(0.4f);
        MoveToAction moveToAction1 = new MoveToAction();
        moveToAction1.setPosition(Gdx.graphics.getWidth() * 0.1f, -Gdx.graphics.getHeight() * 0.1f);
        moveToAction1.setDuration(0.4f);
        group[0].addAction(moveToAction1);
        group[0].addAction(scaleToAction1);
        MoveToAction moveToAction2 = new MoveToAction();
        moveToAction2.setPosition(Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.28f);
        moveToAction2.setDuration(0.4f);
        group[2].addAction(moveToAction2);
        MoveToAction moveToAction3 = new MoveToAction();
        moveToAction3.setPosition(Gdx.graphics.getWidth() * 0.1f, 0);
        moveToAction3.setDuration(0.4f);
        group[1].addAction(moveToAction3);
    }
}
