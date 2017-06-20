package com.enklave.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
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
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.enklave.game.Enklave.DescEnklave.GetEnklaveDetails;
import com.enklave.game.Enklave.DescEnklave.InformationEnklave;
import com.enklave.game.Enklave.DrawEnklaves;
import com.enklave.game.Enklave.Enklave3D;
import com.enklave.game.Enklave.ListEnklaves;
import com.enklave.game.FontLabel.Font;
import com.enklave.game.GameManager;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.MapsService.Bounds;
import com.enklave.game.MapsService.MapPixmap;
import com.enklave.game.MapsService.MyLocation;
import com.enklave.game.MapsService.MyQueue;
import com.enklave.game.MapsService.MyThread;
import com.enklave.game.Profile.InformationProfile;
import com.enklave.game.Raider.RaiderMap;
import com.enklave.game.Utils.NameFiles;
import com.enklave.game.WebSocket.ThreadSendCoordonate;

import java.util.Timer;
import java.util.TimerTask;

import static com.badlogic.gdx.graphics.VertexAttributes.Usage.Normal;
import static com.badlogic.gdx.graphics.VertexAttributes.Usage.Position;

public class MapsScreen implements Screen,GestureDetector.GestureListener,InputProcessor {

    private final Environment environment;
    private final PerspectiveCamera camera;
    private final DecalBatch batch;
    private final Bounds bounds;
    private final GameManager gameManager;
    private final MyLocation myLocation;
    private QueueDisplay queueDisplay;
    private MyThread mthread;
    private double compass;
    private double unitlong,unitlat;
    private MyQueue queue;
    private MapPixmap matrixPixmap;
    private Vector2[][] pos;
    private Dialog dia;
    private Decal position,position1,decalcircle;
    private ManagerAssets managerAssets;
    private Image profile,profileback, imageCrafting;
    private Stage stage;
    private CameraInputController controller;
    private ModelBatch modelBatch;
    private boolean isdrawmap = true;
    private Label scoreLabel;
    private Decal[] extendmaps;
    private Preferences pref;
    private ProgressBarEnergy progressBarEnergy;
    private int numberOfFingers = 0;
    private int[] pointerx,pointery;
    private int clickEnklave;
    private double distAB = 0.0,dif = 0;
    private float semn=0;
    private boolean flagdraw=false;
    private double latitude = 0.0,startlat=0.0,longitude = 0.0,startlong=0.0,distanta = 0;
    private Double vdx=0.0,vdy=0.0;
    private Enklave3D[] arrayEnklave;
    private boolean translateCamera = false;
    private Model model;
    private ModelInstance instance;
    private int Width = Gdx.graphics.getWidth(),Height = Gdx.graphics.getHeight();
    private DrawEnklaves enklavesArray;
    private Timer timerEnergy;
    private RaiderMap raiderMap;
    private ScreenChat screenChat;
    private InformationProfile informationProfile;

    public MapsScreen(GameManager game) {
        gameManager = game;
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
        environment.add(new DirectionalLight().set(1f, 1f, 1f, -1f, -0.8f, -0.2f));
        camera = new PerspectiveCamera(40, Width, Height);
        camera.lookAt(0, 0, 0);
        camera.far = 4000;
        camera.near = 1;
        camera.update();
        pos = new Vector2[3][3];
        CameraGroupStrategy cameraGroupStategy = new CameraGroupStrategy(camera);
        batch = new DecalBatch(cameraGroupStategy);
        bounds = new Bounds();
        matrixPixmap = MapPixmap.getInstance();
        managerAssets = ManagerAssets.getInstance();
        myLocation = MyLocation.getInstance();
        pointerx = new int[4];
        pointery = new int[4];
        compass = myLocation.getCompas();
        queue = MyQueue.getInstance();
        //calc bounds
        bounds.getCorners(matrixPixmap.getMatrix()[1][1].getLatitude(),matrixPixmap.getMatrix()[1][1].getLongitude() , 17.0, 640.0, 640.0);
        unitlong = (bounds.calcDisance(bounds.latSW, bounds.longSW, bounds.latSW, bounds.longNE)+bounds.calcDisance(bounds.latNE,bounds.longNE,bounds.latNE,bounds.longSW))/2.0;
        unitlong=600/unitlong;
        unitlat = bounds.calcDisance(bounds.latSW, bounds.longSW, bounds.latNE, bounds.longSW);
        unitlat= 600/unitlat;
        timerEnergy = new Timer();
        informationProfile = InformationProfile.getInstance();
    }

    public Stage getStage() {
        return stage;
    }

    public ProgressBarEnergy getProgressBarEnergy() {
        return progressBarEnergy;
    }

    private void DrawMap(){
        startlat = matrixPixmap.getMatrix()[1][1].getLatitude();
        startlong = matrixPixmap.getMatrix()[1][1].getLongitude();
        latitude = matrixPixmap.translateMaps.getInitiallat();
        longitude = matrixPixmap.translateMaps.getInitiallong();
        new ThreadSendCoordonate(latitude,longitude).run();
        mthread = new MyThread(startlat,startlong,latitude,longitude,myLocation.getLatitude(),myLocation.getLongitude(),distanta,unitlat,unitlong);
        ModelBuilder modelBuilder = new ModelBuilder();
        model =  modelBuilder.createBox(0.1f, 0.1f, 0.1f, new Material(ColorAttribute.createDiffuse(Color.WHITE)), Position | Normal);
        instance = new ModelInstance(model);
        position = Decal.newDecal(new TextureRegion(managerAssets.getAssetsMaps().getTexture(NameFiles.imgPossition)));
        position1 = Decal.newDecal(new TextureRegion(managerAssets.getAssetsMaps().getTexture(NameFiles.imgPossition)));
        position.setPosition(0, 0, 5);
        position1.setPosition(0, 0, 8);
        position.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        position1.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        position.setDimensions(15, 15);
        position1.setDimensions(15, 15);
        position.rotateZ((float) -compass);position1.rotateZ((float) -compass);
        decalcircle = Decal.newDecal((float)(60.0*unitlong),(float)(60.0*unitlat),new TextureRegion(managerAssets.getAssetsMaps().getTexture(NameFiles.imgCirclePos)));
        decalcircle.setPosition(0, 0, 3);decalcircle.setDimensions((float) (120.0 * unitlong), (float) (120.0 * unitlat));
        decalcircle.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        //scrap
        scoreLabel = new Label("Scrap : "+informationProfile.getDateUserGame().getScrap(),new Label.LabelStyle(Font.getFont((int) (Height * 0.03)), Color.WHITE));
        //scoreLabel.setPosition(Gdx.graphics.getWidth() - scoreLabel.getWidth() - (float) (Gdx.graphics.getWidth() * 0.08), Gdx.graphics.getHeight() - scoreLabel.getHeight() - (float) (Gdx.graphics.getHeight() * 0.01));
        progressBarEnergy = ProgressBarEnergy.getInstance();
        progressBarEnergy.setMapsScreen(this);
//        if(!informationProfile.getDateUserGame().isInCombat())
//            startTimer();
        //enklava
        enklavesArray = new DrawEnklaves();
        //raider
        raiderMap = new RaiderMap();

        //initialize position matrix  lat=x  long=y
        extendmaps = new Decal[9];
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++) {
                pos[i][j] = new Vector2();
                pos[i][j].set(600 * (j - 1), -600 * (i - 1));
            }
        }

        pref = Gdx.app.getPreferences("Profile");
        if(!pref.contains("tutorialShow")) {
            pref.putBoolean("tutorialShow", false);
            pref.flush();
        }
        pref.putBoolean("showonetime", true);
        pref.flush();
        addButtonChatCrafting();
        //exit dialog
        createexitdialog();
        //matrix map
        ExtentMap();
        for (int i = 0; i < 9; i++) {
            extendmaps[i].translate((float) (-matrixPixmap.translateMaps.getDirx() * unitlong), (float) (-matrixPixmap.translateMaps.getDiry() * unitlat), 0);
        }
        enklavesArray.translateEnk((float) (-matrixPixmap.translateMaps.getDirx() * unitlong), (float) (-matrixPixmap.translateMaps.getDiry() * unitlat));
        queueDisplay = QueueDisplay.getInstance();
        screenChat = ScreenChat.getInstance();
        queueDisplay = QueueDisplay.getInstance();

    }

    private void ExtentMap() {
        int k =0;
        for(int i=0;i<3;i++) {
            for (int j = 0; j < 3; j++) {
                extendmaps[k] = Decal.newDecal(new TextureRegion(new Texture(matrixPixmap.getImage(i,j))));
                extendmaps[k].setPosition(pos[i][j].x, pos[i][j].y, 0);
                extendmaps[k].setDimensions(600, 600);
                k++;
            }
        }
    }

    private void createexitdialog() {
        Pixmap p = new Pixmap(Width,Height, Pixmap.Format.RGBA8888);
        p.setColor(1.0f, 1.0f, 1.0f, 0.7f);
        p.fill();
        Skin s = new Skin();
        s.add("black-background", new Texture(p));
        s.add("default", new Label.LabelStyle(new BitmapFont(false), Color.WHITE));
        s.add("default", new Window.WindowStyle(new BitmapFont(false), Color.WHITE, s.newDrawable("black-background", Color.DARK_GRAY)));
        dia = new Dialog("",s);
        dia.addActor(addButtonLeft());
        dia.addActor(addButtonRight());
        p = new Pixmap(0,0, Pixmap.Format.RGBA8888);
        p.dispose();
        s =new Skin();
        s.dispose();
        Label label = new Label("Are you sure that you want\n quit game?",new Label.LabelStyle(Font.getFont((int) (Height * 0.035)),Color.WHITE));
        label.setAlignment(Align.center);
        dia.text(label);
    }

    public ImageTextButton addButtonLeft(){
        String bup = "button-up",bdown = "button-down", bcheck = "button-checked";
        Skin s=new Skin();
        TextureAtlas txta = new TextureAtlas();
        Texture t = managerAssets.getAssetsButton().get(NameFiles.buttontwoLeft);
        Vector2 crop = Scaling.fit.apply(t.getWidth(), t.getHeight() / 2, Width, Height);
        txta.addRegion(bup, new TextureRegion(t, 0, 0, t.getWidth(), t.getHeight() / 2));
        txta.addRegion(bdown, new TextureRegion(t, 0, t.getHeight() / 2, t.getWidth(), t.getHeight() / 2));
        txta.addRegion(bcheck, new TextureRegion(t, 0, 0, t.getWidth(), t.getHeight() / 2));
        s.addRegions(txta);
        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle();
        style.up = s.getDrawable(bup);
        style.down = s.getDrawable(bdown);
        style.checked = s.getDrawable(bcheck);
        style.font = Font.getFont((int) (Height * 0.025));
        s = new Skin();
        s.dispose();
        txta = new TextureAtlas();
        txta.dispose();
        t = new Texture(0,0, Pixmap.Format.RGBA8888);
        t.dispose();
        ImageTextButton imgbtn = new ImageTextButton("NO",style);
        imgbtn.setSize(crop.x*0.2f,crop.y*0.2f);
        imgbtn.setPosition(Width * 0.32f, Height * 0.4f);
        imgbtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dia.hide();
            }
        });
        return imgbtn;
    }

    public ImageTextButton addButtonRight(){
        String bup = "button-up",bdown = "button-down", bcheck = "button-checked";
        Skin s=new Skin();
        Texture t = managerAssets.getAssetsButton().get(NameFiles.buttonRight);
        Vector2 crop = Scaling.fit.apply(t.getWidth(), t.getHeight() / 2, Width, Height);
        TextureAtlas txta = new TextureAtlas();
        txta.addRegion(bup, new TextureRegion(t, 0, 0, t.getWidth(), t.getHeight() / 2));
        txta.addRegion(bdown, new TextureRegion(t, 0, t.getHeight() / 2, t.getWidth(), t.getHeight() / 2));
        txta.addRegion(bcheck, new TextureRegion(t, 0, 0, t.getWidth(), t.getHeight() / 2));
        s.addRegions(txta);
        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle();
        style.up = s.getDrawable(bup);
        style.down = s.getDrawable(bdown);
        style.checked = s.getDrawable(bcheck);
        style.font = Font.getFont((int) (Height * 0.025));
        s = new Skin();
        s.dispose();
        txta = new TextureAtlas();
        txta.dispose();
        t = new Texture(0,0, Pixmap.Format.RGBA8888);
        t.dispose();
        ImageTextButton imgbtn = new ImageTextButton("YES",style);
        imgbtn.setSize(crop.x * 0.2f, crop.y * 0.2f);
        imgbtn.setPosition(Width / 2, Height * 0.40f);
        imgbtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dia.hide();
                Gdx.app.exit();
                System.exit(1);
            }
        });
        return imgbtn;
    }

    private void buttonProfile(){
        //button for profile
        Texture tex;
        switch (informationProfile.getDateUserGame().getFaction()){
            case 1:{
                position.setTextureRegion(new TextureRegion(managerAssets.getAssetsMaps().getTexture(NameFiles.CursorPositionRed)));
                position1.setTextureRegion(new TextureRegion(managerAssets.getAssetsMaps().getTexture(NameFiles.CursorPositionRed)));
                tex = managerAssets.getAssetsMaps().getTexture(NameFiles.imageFactionPrometheans);
                break;
            }
            case 2:{
                position.setTextureRegion(new TextureRegion(managerAssets.getAssetsMaps().getTexture(NameFiles.CursorPositionBlue)));
                position1.setTextureRegion(new TextureRegion(managerAssets.getAssetsMaps().getTexture(NameFiles.CursorPositionBlue)));
                tex = managerAssets.getAssetsMaps().getTexture(NameFiles.imageFactionArhitects);
                break;
            }
            case 3:{
                position.setTextureRegion(new TextureRegion(managerAssets.getAssetsMaps().getTexture(NameFiles.CursorPositionGreen)));
                position1.setTextureRegion(new TextureRegion(managerAssets.getAssetsMaps().getTexture(NameFiles.CursorPositionGreen)));
                tex = managerAssets.getAssetsMaps().getTexture(NameFiles.imageFactionEdenites);
                break;
            }
            default:{
                tex = managerAssets.getAssetsMaps().getTexture(NameFiles.imageFactionArhitects);
                break;
            }
        }
        Vector2 crop = Scaling.fit.apply(tex.getWidth(), tex.getHeight(), Width, Height);
        profile = new Image(new TextureRegion(tex));
        profile.setSize(crop.x * 0.12f, crop.y * 0.12f);
        profile.setPosition(Width * 0.04f, Height - (profile.getHeight() * 1.25f));
        profile.setOrigin(profile.getOriginX() + (profile.getWidth() / 2), profile.getOriginY() + (profile.getHeight() / 2));
        TextureRegion texReg = new TextureRegion(tex);
        texReg.flip(true,false);
        profileback = new Image(texReg);
        profileback.setSize(crop.x * 0.12f, crop.y * 0.12f);
        profileback.setPosition(Width * 0.04f, Height -(profile.getHeight()* 1.25f));
        profileback.setOrigin(profileback.getOriginX() + (profileback.getWidth() / 2), profileback.getOriginY() + (profileback.getHeight() / 2));
        SequenceAction s = new SequenceAction(Actions.scaleTo(0, 1, 3), Actions.delay(6), Actions.scaleTo(1, 1, 3));
        RepeatAction r= new RepeatAction();
        r.setAction(s);
        r.setCount(RepeatAction.FOREVER);
        profile.addAction(r);
        s = new SequenceAction(Actions.scaleTo(0, 1), Actions.delay(3), Actions.scaleTo(1, 1, 3), Actions.scaleTo(0, 1, 3), Actions.delay(3));
        r= new RepeatAction();
        r.setAction(s);
        r.setCount(RepeatAction.FOREVER);
        profileback.addAction(r);
        tex = new Texture(0,0, Pixmap.Format.RGBA8888);
        tex.dispose();
        progressBarEnergy.updateVisual();
    }

    public void addButtonChatCrafting(){
        Texture tex = managerAssets.getAssetsMaps().getTexture(NameFiles.buttoncrafting);
        Vector2 crop = Scaling.fit.apply(tex.getWidth(),tex.getHeight(),Width,Height);
        imageCrafting = new Image(new TextureRegion(tex));
        imageCrafting.setSize(crop.x * 0.175f, crop.y * 0.175f);
        imageCrafting.setPosition(Width - imageCrafting.getWidth(), Height * 0.005f);
        imageCrafting.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameManager.setScreen(gameManager.screenCrafting);
            }
        });
        tex = new Texture(0,0, Pixmap.Format.RGBA8888);
        tex.dispose();
    }

    public void startTimer(){
        timerEnergy = new Timer();
        timerEnergy.schedule(new TimerTask() {

            @Override
            public void run() {
                InformationProfile infoProfile = InformationProfile.getInstance();
                if (infoProfile.getDateUserGame().getEnergy() < infoProfile.getDateUserGame().getEnergyLevel()) {
                    infoProfile.getDateUserGame().setEnergy(infoProfile.getDateUserGame().getEnergy() + infoProfile.getDateUserGame().getEnergyProgress());
                    progressBarEnergy.RegenerationEnergy();
                } else {
                    progressBarEnergy.Fadeout();
                    timerEnergy.cancel();
                }
            }
        }, 0, 1000);
    }

    public void setcontrollerMap(boolean onlystage){
        if(!onlystage){
            InputMultiplexer inputmulti = new InputMultiplexer();
            GestureDetector gd = new GestureDetector(this);
            inputmulti.addProcessor(gd);
            inputmulti.addProcessor(this);
            inputmulti.addProcessor(stage);
            Gdx.input.setInputProcessor(inputmulti);
        }else{
            Gdx.input.setInputProcessor(stage);
        }

    }

    public void addToStage(){
        stage.addActor(profile);
        stage.addActor(profileback);
        stage.addActor(imageCrafting);
        stage.addActor(scoreLabel);
        screenChat.addToStage(stage);
        queueDisplay.AddtoStage(stage);
        progressBarEnergy.addGrouptoStage(stage);
        if(!pref.getBoolean("tutorialShow")){
            if(pref.getBoolean("showonetime")) {
                Gdx.app.log("maps "+managerAssets.getAssetsTutorial().numbersAssets()," sdf "+managerAssets.getAssetsTutorial().update());
                if( managerAssets.getAssetsTutorial().numbersAssets() == 0)
                    managerAssets.loadAssetsTutorial();
                if (!managerAssets.getAssetsTutorial().update()) {
                    gameManager.setScreen(new ScreenCircleLoading(gameManager, gameManager.mapsScreen, managerAssets.getAssetsTutorial()));
                } else {
                    TutorialDialog dialog = new TutorialDialog(gameManager);
                    dialog = dialog.getstartTutorial("NO");
                    dialog.show(stage);
                    setcontrollerMap(true);
                    pref.putBoolean("showonetime", false);
                    pref.flush();
                }
            }else
                setcontrollerMap(false);
        }else
            setcontrollerMap(false);
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight())){
            @Override
            public boolean keyDown(int keyCode) {
                if(keyCode == Input.Keys.BACK){
                    if(screenChat.isVisibleChat()){
                        screenChat.fadeout();
                    }else {
                        dia.show(stage);
                    }
                }
                return super.keyDown(keyCode);
            }
        };
        if(isdrawmap) {
            DrawMap();
            Gdx.app.log("combat"," "+!informationProfile.getDateUserGame().isInCombat());
            if(!informationProfile.getDateUserGame().isInCombat())
                progressBarEnergy.update();
            isdrawmap = false;
            screenChat.setGameManager(gameManager);
            if(informationProfile.getDateUserGame().isInCombat()){
                managerAssets.loadAssetsEnklaveScreen();
                new GetEnklaveDetails().makeRequest(InformationEnklave.getInstance().getId(), managerAssets);
                gameManager.screenEnklave.setEnklave3D(enklavesArray.getEnkl(InformationEnklave.getInstance().getId()));
                gameManager.setScreen(new ScreenCircleLoading(gameManager,gameManager.screenEnklave,managerAssets.getAssertEnklaveScreen()));
            }
        }
        buttonProfile();
        addToStage();
        controller = new CameraInputController(camera);
        modelBatch = new ModelBatch();
        camera.position.set(0, -100, 210);//initial 180
        camera.lookAt(0, 0, 0);
        camera.update();

        translateCamera = false;
        numberOfFingers = 0;
        Gdx.input.setCatchBackKey(true);
//        if(InformationEnklave.getInstance().getFaction() == 0){
//            enklavesArray = new DrawEnklaves();
//        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        scoreLabel.setText("Scrap : "+Math.round(InformationProfile.getInstance().getDateUserGame().getScrap() - informationProfile.getValuescrapuse()));
        startlat = matrixPixmap.translateMaps.getCenterlat();
        startlong = matrixPixmap.translateMaps.getCenterlong();
        if (!mthread.isAlive()) {
            double latnew = myLocation.getLatitude(), longnew = myLocation.getLongitude();
            mthread = new MyThread(startlat, startlong, latitude, longitude, latnew, longnew, distanta, unitlat, unitlong);
            distanta = myLocation.getDistance();
            latitude = latnew;
            longitude = longnew;
            mthread.start();
        }
        if (!queue.isEmpty()) {
            Double[] dxdy;
            dxdy = queue.remove();
            if (extendmaps[0] != null) {
                for (int i = 0; i < 9; i++) {
                    extendmaps[i].translate((float) (vdx + dxdy[0].floatValue()), (float) (vdy + dxdy[1].floatValue()), 0);
                }
                enklavesArray.translateEnk(vdy.floatValue()+dxdy[0].floatValue(), vdy.floatValue() + dxdy[1].floatValue());
                vdx = 0.0;
                vdy = 0.0;
            } else {
                vdx += dxdy[0];
                vdy += dxdy[1];
            }
        }
        if(matrixPixmap.flagSignal.isUpdateDisplay() && enklavesArray.isSet()) {
            ExtentMap();
            int k = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    extendmaps[k].setPosition(pos[i][j].x, pos[i][j].y, 0);
                    extendmaps[k].setDimensions(600, 600);
                    k++;
                }
            }
            double dix = bounds.calcDisance(startlat, startlong, startlat, myLocation.getLongitude()) * unitlong;
            double diy = bounds.calcDisance(startlat, startlong, myLocation.getLatitude(), startlong) * unitlat;
            if (startlat < myLocation.getLatitude()) {
                diy = -diy;
            }
            if (startlong < myLocation.getLongitude()) {
                dix = -dix;
            }
            for (int i = 0; i < 9; i++) {
                extendmaps[i].translate((float) dix, (float) diy, 0);
//                extendmaps[i].translate((float) (-matrixPixmap.translateMaps.getDirx() * unitlong), (float) (-matrixPixmap.translateMaps.getDiry() * unitlat), 0);
//                extendmaps[i].translate((float) -matrixPixmap.translateMaps.getRealDirX(),(float) -matrixPixmap.translateMaps.getRealDirY(), 0);
            }
            enklavesArray = new DrawEnklaves();
            matrixPixmap.flagSignal.setUpdateDisplay(false);
        }
        if (flagdraw) {
            position.rotateZ(semn);
            position1.rotateZ(semn);
            if (dif < 1) {
                flagdraw = false;
            }
            dif = dif - 2;
        } else {
            calculateRotation();
        }

        for (int i = 0; i < 9; i++) {
            batch.add(extendmaps[i]);
        }
        Gdx.gl.glEnable(GL20.GL_BLEND);
        //for (com.adrianpopovici.game.Enklave.Enklave3D anArrayEnklave : arrayEnklave) anArrayEnklave.addtoDecalBatch(batch);
        batch.add(position);
        batch.add(position1);
        batch.add(decalcircle);
        batch.flush();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        modelBatch.begin(camera);
        enklavesArray.DrawEnklave(modelBatch);
        raiderMap.DrawRaider(modelBatch);
        modelBatch.end();
        stage.act(delta);
        stage.draw();
        if (translateCamera) {
            if(clickEnklave<enklavesArray.lenght()) {
                enklavesArray.getEnk(clickEnklave).translateCamera(controller);
                if (controller.camera.position.z < 60) {
                    translateCamera = false;
                    gameManager.screenEnklave.setEnklave3D(enklavesArray.getCoordonateDraw(clickEnklave));
                    gameManager.setScreen(new ScreenCircleLoading(gameManager,gameManager.screenEnklave,managerAssets.getAssertEnklaveScreen()));
                }
            }else{
                raiderMap.translateCamera(controller);
                if (controller.camera.position.z < 90) {
                    translateCamera = false;
                    gameManager.setScreen(new ScreenCircleLoading(gameManager,gameManager.raiderMap,managerAssets.getAssetsRaider()));
                }
            }
        }
        controller.camera.update();
    }

    private void calculateRotation(){
        double pos = myLocation.getCompas();
        if (pos != compass) {
            if ((Math.abs(pos - compass)) < 180) {
                if (pos > (compass + 30)) {
                    flagdraw = true;
                    dif = pos - compass;
                    compass = pos;
                    semn = -2;
                } else if (pos < (compass - 30)) {
                    flagdraw = true;
                    dif = compass - pos;
                    compass = pos;
                    semn = 2;
                }
            } else {
                if (pos < 180) {
                    if ((360 - compass + pos) > 20) {
                        flagdraw = true;
                        dif = 360 - compass + pos;
                        compass = pos;
                        semn = -2;
                    }
                } else if (pos > 180) {
                    if ((360 - pos + compass) > 20) {
                        flagdraw = true;
                        dif = 360 - pos + compass;
                        compass = pos;
                        semn = 2;
                    }
                }
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
        stage.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {

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
        numberOfFingers++;
        pointerx[pointer] = screenX;
        pointery[pointer] = screenY;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        numberOfFingers--;
        if(!translateCamera) {
            clickEnklave = 0;
            if (raiderMap.TouchRaider(screenX, screenY, camera)) {
                managerAssets.getAssetsRaider().updateRaider();
                translateCamera = true;
            }
            if (enklavesArray.lenght() > 0) {
                while (!enklavesArray.getEnk(clickEnklave).touchEnklave(camera, screenX, screenY)) {
                    clickEnklave++;
                    if (clickEnklave == enklavesArray.lenght()) {
                        break;
                    }
                }
            }
            if (clickEnklave < enklavesArray.lenght()) {
                managerAssets.loadAssetsEnklaveScreen();
                new GetEnklaveDetails().makeRequest(ListEnklaves.getInstance().get(clickEnklave).id, managerAssets);
//                InformationEnklave.getInstance().setBricks(ListEnklaves.getInstance().get(clickEnklave).numberbricks);
                translateCamera = true;
            }

            if ((screenX > profile.getX() && screenX < profile.getRight()) && (screenY < (Gdx.graphics.getHeight() - profile.getY()) && screenY > (Gdx.graphics.getHeight() - profile.getTop()))) {
                gameManager.setScreen(gameManager.screenProfile);
            }
            progressBarEnergy.clickProgressBar(screenX, screenY);
            controller.camera.update();
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(numberOfFingers == 2 && !translateCamera) {
            double tangenta = 0;
            if (pointer == 0) {
                tangenta = valueAngle(new Vector2(pointerx[1], pointery[1]), new Vector2(pointerx[0], pointery[0]), new Vector2(screenX, screenY));
            } else {
                tangenta = valueAngle(new Vector2(pointerx[0], pointery[0]), new Vector2(pointerx[1], pointery[1]), new Vector2(screenX, screenY));
            }
            controller.camera.rotateAround(new Vector3(0, 0, 200), new Vector3(0, 0, 1), (float) tangenta);

            if (distAB == 0.0)
                distAB = Math.sqrt(Math.pow(pointerx[0] - pointerx[1], 2) + Math.pow(pointery[0] - pointery[1], 2));
            double d = Math.sqrt(Math.pow(pointerx[0] - pointerx[1], 2) + Math.pow(pointery[0] - pointery[1], 2));
            if ((distAB - 15) > d || (distAB + 15) < d) {
                double scale = d / distAB;
                if (scale > 1) {//zoom in
                    if (controller.camera.position.z > 95) {
                        controller.camera.position.z = (float) (controller.camera.position.z - (6 * scale));
                    }
                } else {//zoom out
                    if (controller.camera.position.z < 550) {
                        controller.camera.position.z = (float) (controller.camera.position.z + (6 * scale));
                    }
                }
                distAB = d;
            }
            pointerx[pointer] = screenX;
            pointery[pointer] = screenY;
            controller.camera.lookAt(0, 0, 0);
        }
//        if(numberOfFingers == 1) {
//            if (controller.camera.position.z > 50) {
//                if (screenY > (pointery[0])) {
//                    if (variatia > -30) {
//                        variatia--;
//                        if (controller.camera.up.x <= 0.0f && controller.camera.up.y <= 0.0f) {
//                            controller.camera.position.x += Math.abs(1 * controller.camera.up.x);
//                            controller.camera.position.y += Math.abs(1 * (1 - controller.camera.up.x));
//                        } else if (controller.camera.up.x < 0.0f && controller.camera.up.y > 0.0f) {
//                            controller.camera.position.x += Math.abs(controller.camera.up.x);
//                            controller.camera.position.y -= 1 - Math.abs(controller.camera.up.x);
//                        } else if (controller.camera.up.x >= 0.0f && controller.camera.up.y >= 0.0f) {
//                            controller.camera.position.x -= Math.abs(controller.camera.up.x);
//                            controller.camera.position.y -= 1 - Math.abs(controller.camera.up.x);
//                        } else if (controller.camera.up.x > 0.0f && controller.camera.up.y < 0.0f) {
//                            controller.camera.position.x -= Math.abs(1 * controller.camera.up.x);
//                            controller.camera.position.y += Math.abs(1 * (1 - controller.camera.up.x));
//                        }
//                        controller.camera.position.z -= 1;
//                    }
//                } else {
//                    if (variatia < 60) {
//                        variatia++;
//                        if (controller.camera.up.x <= 0.0f && controller.camera.up.y <= 0.0f) {
//                            controller.camera.position.x -= Math.abs(1 * controller.camera.up.x);
//                            controller.camera.position.y -= Math.abs(1 * (1 - controller.camera.up.x));
//                        } else if (controller.camera.up.x < 0.0f && controller.camera.up.y > 0.0f) {
//                            controller.camera.position.x -= Math.abs(controller.camera.up.x);
//                            controller.camera.position.y += 1 - Math.abs(controller.camera.up.x);
//                        } else if (controller.camera.up.x >= 0.0f && controller.camera.up.y >= 0.0f) {
//                            controller.camera.position.x += Math.abs(controller.camera.up.x);
//                            controller.camera.position.y += 1 - Math.abs(controller.camera.up.x);
//                        } else if (controller.camera.up.x > 0.0f && controller.camera.up.y < 0.0f) {
//                            controller.camera.position.x += Math.abs(1 * controller.camera.up.x);
//                            controller.camera.position.y -= Math.abs(1 * (1 - controller.camera.up.x));
//                        }
//                        controller.camera.position.z += 1;
//                    }
//                }
//
//                pointerx[pointer] = screenX;
//                pointery[pointer] = screenY;
//            }
//            controller.camera.lookAt(0, 0, 0);
//        }
        controller.camera.update();
        return false;
    }
    public double valueAngle(Vector2 pivot,Vector2 p1,Vector2 p2){
        double angle=0;
        double alpha = Math.toDegrees(Math.atan(((p1.y - pivot.y)/(p1.x - pivot.x))));
        double betha = Math.toDegrees(Math.atan(((p2.y - pivot.y)/(p2.x - pivot.x))));
        angle = betha - alpha;
        return Math.abs(angle) > 90 ? 0 : angle;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
