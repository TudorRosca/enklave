package com.enklave.game.Screens;

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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.enklave.game.Combat.CombatFitght;
import com.enklave.game.Combat.ListOfAttachers;
import com.enklave.game.Combat.ListOfDefenders;
import com.enklave.game.Combat.Request.StartCombat;
import com.enklave.game.Combat.Request.UnsubscribeCombat;
import com.enklave.game.Combat.UpdateDisplayCombat;
import com.enklave.game.Enklave.DescEnklave.GetEnklaveDetails;
import com.enklave.game.Enklave.DescEnklave.InformationEnklave;
import com.enklave.game.FontLabel.Font;
import com.enklave.game.GameManager;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.MapsService.Bounds;
import com.enklave.game.MapsService.MapPixmap;
import com.enklave.game.MapsService.MyLocation;
import com.enklave.game.Profile.InformationProfile;
import com.enklave.game.Utils.NameFiles;

public class ScreenCombat implements Screen,InputProcessor,GestureDetector.GestureListener{
    private final PerspectiveCamera camera;
    protected final Integer[] centerPlayer;
    private GameManager gameManager;
    private Stage stage;
    private ProgressBarEnergy progressBarEnergy;
    private Decal decalBackground;
    private DecalBatch decalBatch;
    private Group groupCenter;
    private Group groupTop;
    String bup = "button-up",bdown = "button-down", bcheck = "button-checked";
    private DrawDefenders defenders;
    private Group groupBtnDefender;
    private DrawAttachers attachers;
    private ManagerAssets managerAssets;
    private boolean clickdefender = false;
    private boolean clickattachers = false;
    private float dist = 0;
    private final int WIDTH = Gdx.graphics.getWidth(),HEIGHT = Gdx.graphics.getHeight();
    private CombatFitght combatFight;
    private OrthographicCamera cam;
    private ImageTextButton buttonJoinStartCombat;
    private Label labelDistance;
    private boolean circleShow = false,showtextexit = false;
    private float startTime;
    BitmapFont bitmapFont = Font.getFont((int)(HEIGHT*0.025f));
    BitmapFont fontdialog3 = Font.getFont((int)(HEIGHT*0.03f));
    private String messageShow = "";
    private Stage stagefordialog;

    public ScreenCombat(GameManager gameManager) {
        this.gameManager = gameManager;
        managerAssets = ManagerAssets.getInstance();
        cam = new OrthographicCamera();
        UpdateDisplayCombat.getInstance().setScreenCombat(this);
        camera = new PerspectiveCamera(40, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 0, 350);//initial 180
        camera.lookAt(0, 0, 0);
        camera.far = 2500;
        camera.near = 1;
        camera.update();
        centerPlayer = new Integer[7];
        for(int i=0;i<7;i++){
            centerPlayer[i]=-1;
        }
    }

    private void drawtopcombat(){
        groupTop = new Group();
        Texture lookup = managerAssets.getAssetsCombat().getTexture(NameFiles.progressbarcircular);
        lookup.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        progressBarEnergy = ProgressBarEnergy.getInstance();
        Image imageLeftprofile = new Image(new TextureRegion(managerAssets.getAssetsButton().get(NameFiles.buttonBack1)));
        imageLeftprofile.setSize(Gdx.graphics.getWidth() * 0.15f, Gdx.graphics.getWidth() * 0.15f);
        imageLeftprofile.setPosition(imageLeftprofile.getWidth() * 0.1f, Gdx.graphics.getHeight() - imageLeftprofile.getHeight() * 1.1f);
        imageLeftprofile.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(InformationProfile.getInstance().getDateUserGame().getEnklaveCombatId() == -1)
                    gameManager.setScreen(gameManager.screenEnklave);
                else
                    dialogExit("You can't out from combat!");
            }
        });
        groupTop.addActor(imageLeftprofile);
    }

    public void dialogExit(String text){
        Pixmap p = new Pixmap(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(), Pixmap.Format.RGBA8888);
        p.setColor(1.0f, 1.0f, 1.0f, 0.7f);
        p.fill();
        Skin s = new Skin();
        s.add("black-background", new Texture(p));
        s.add("default", new Label.LabelStyle(new BitmapFont(false), Color.WHITE));
        s.add("default", new Window.WindowStyle(new BitmapFont(false), Color.WHITE, s.newDrawable("black-background", Color.DARK_GRAY)));
        final Dialog dia = new Dialog("", s);
        p = new Pixmap(0,0, Pixmap.Format.RGBA8888);
        p.dispose();
        s =new Skin();
        s.dispose();
        Label label = new Label(text,new Label.LabelStyle(fontdialog3,Color.GOLD));
        label.setAlignment(Align.center);
        dia.text(label);
        dia.show(stagefordialog);
        new Timer().scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                dia.remove();
            }
        },3f);
    }

    private void drawmap() {
        MapPixmap imageBG = MapPixmap.getInstance();
        decalBackground = Decal.newDecal(new TextureRegion(new Texture(imageBG.getImage(1, 1))));
        decalBackground.setPosition(0, 0, 0);
        decalBackground.setDimensions(800, 800);
        decalBackground.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Texture text = null;
        groupCenter = new Group();
        switch (InformationEnklave.getInstance().getFaction()){
            case 1:{
                text = managerAssets.getAssetsCombat().getTexture(NameFiles.enklavered);
                break;
            }
            case 2:{
                text = managerAssets.getAssetsCombat().getTexture(NameFiles.enklaveblue);
                break;
            }
            case 3:{
                text = managerAssets.getAssetsCombat().getTexture(NameFiles.enklavegreen);
                break;
            }
        }

        Vector2 crop = Scaling.fit.apply(text.getWidth(),text.getHeight(),WIDTH,HEIGHT);
        Image enklave = new Image(new TextureRegion(text));
        enklave.setSize(crop.x*0.6f,crop.y*0.6f);
        enklave.setPosition(WIDTH / 2 - enklave.getWidth() / 2, HEIGHT / 2 - enklave.getHeight() / 1.7F);
        groupCenter.addActor(enklave);
        labelDistance = new Label("Distance: ",new Label.LabelStyle(Font.getFont((int)(HEIGHT*0.025f)),Color.ORANGE));
        groupCenter.addActor(labelDistance);
    }

    public void showCover(){
        circleShow = true;
        startTime = 0;
    }

    public void screenExit(String message){
        messageShow = message;
        showtextexit = true;
        circleShow = false;
        InformationProfile.getInstance().getDateUserGame().setEnklaveCombatId(-1);
        Label labelmess = new Label("You already joined this combat once!",new Label.LabelStyle(bitmapFont,Color.WHITE));
        labelmess.setPosition(WIDTH /2 - labelmess.getWidth() /2,labelmess.getHeight()*2);
        stage.addActor(labelmess);
        combatFight.deselect();
        attachers.deselect();
        defenders.deselect();
        if(!message.contentEquals("You lose!")){
            new Timer().scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    managerAssets.getAssertEnklaveScreen().setIsupdate(false);
                    new GetEnklaveDetails().makeRequest(InformationEnklave.getInstance().getId(), managerAssets);
//                    gameManager.setScreen(gameManager.screenEnklave);
                }
            },1);
        }
    }

    public void maskUpdate(){
        combatFight.updateMaskBrick();
    }

    private boolean showmessageone = false;

    public void updateDistance(){
        Bounds bounds = new Bounds();
        double distance = Math.round(bounds.calcDisance(MyLocation.getInstance().getLatitude(),MyLocation.getInstance().getLongitude(),InformationEnklave.getInstance().getLatitude(),InformationEnklave.getInstance().getLongitude())*100)/100.0;
        if(distance>53 && !showmessageone){
            showmessageone = true;
            dialogExit("Please come back in proximity\n of the enklave, otherwise you\n will be removed from combat.");
        }else if(distance<53){
            showmessageone = false;
        }
        labelDistance.setText("Distance: "+distance);
    }

    public void deselectplayers(){
        attachers.deselect();
        defenders.deselect();
    }

    public void addBtnStartCombat(){
        Skin s=new Skin();
        Texture t = managerAssets.getAssetsButton().get(NameFiles.buttonStartCombat);
        TextureAtlas txta = new TextureAtlas();
        txta.addRegion(bup, new TextureRegion(t));
        txta.addRegion(bdown,new TextureRegion(t));
        txta.addRegion(bcheck, new TextureRegion(t));
        s.addRegions(txta);
        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle();
        style.up = s.getDrawable(bup);
        style.down = s.getDrawable(bdown);
        style.checked = s.getDrawable(bcheck);
        style.font = Font.getFont((int) (Gdx.graphics.getHeight() * 0.035));
        s = new Skin();
        s.dispose();
        txta = new TextureAtlas();
        txta.dispose();
        buttonJoinStartCombat = new ImageTextButton("",style);
        buttonJoinStartCombat.setSize(Gdx.graphics.getWidth() * 0.3f, Gdx.graphics.getWidth() * 0.3f);
        buttonJoinStartCombat.setPosition(WIDTH / 2 - buttonJoinStartCombat.getWidth() / 2, HEIGHT * 0.025f);
        buttonJoinStartCombat.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(InformationProfile.getInstance().getDateUserGame().getEnergy() > InformationProfile.getInstance().getDateUserGame().getEnergyLevel()/2) {
                    new StartCombat().makeRequest(InformationEnklave.getInstance().getId(),ScreenCombat.this);
                    buttonJoinStartCombat.setVisible(false);
                }else{
                    dialogExit("You don't have enough energy!");
                }
            }
        });
        if(!InformationProfile.getInstance().getDateUserGame().isInCombat()) {
            if (InformationEnklave.getInstance().isStatusCombat()) {
                buttonJoinStartCombat.setText("JOIN");
            } else {
                buttonJoinStartCombat.setText("START");
            }
        }else{
            buttonJoinStartCombat.setVisible(false);
            if(InformationProfile.getInstance().getDateUserGame().getEnklaveCombatId() == -1){
                Label labelmess = new Label("You already joined this combat once!",new Label.LabelStyle(Font.getFont((int)(HEIGHT*0.025f)),Color.WHITE));
                labelmess.setPosition(WIDTH /2 - labelmess.getWidth() /2,labelmess.getHeight()*2);
                stage.addActor(labelmess);
            }
        }
    }

    //draw defender
    private void drawdefenders(){
        defenders = new DrawDefenders();
        groupBtnDefender = new Group();
        Button btn1 = new Button(new ImageButton.ImageButtonStyle());
        btn1.setName("btn1");
        btn1.setSize(Gdx.graphics.getWidth() * 0.18f, Gdx.graphics.getWidth() * 0.2f);
        btn1.setPosition(Gdx.graphics.getWidth() * 0.025f, Gdx.graphics.getHeight() / 2.3f - Gdx.graphics.getHeight() * 0.14f);
        groupBtnDefender.addActor(btn1);
        Button btn2 = new Button(new ImageButton.ImageButtonStyle());
        btn2.setName("btn2");
        btn2.setSize(Gdx.graphics.getWidth() * 0.18f, Gdx.graphics.getWidth() * 0.2f);
        btn2.setPosition(Gdx.graphics.getWidth() * 0.025f, Gdx.graphics.getHeight() / 2.3f);
        groupBtnDefender.addActor(btn2);
        Button btn3 = new Button(new ImageButton.ImageButtonStyle());
        btn3.setName("btn3");
        btn3.setSize(Gdx.graphics.getWidth() * 0.18f, Gdx.graphics.getWidth() * 0.2f);
        btn3.setPosition(Gdx.graphics.getWidth() * 0.025f, Gdx.graphics.getHeight() / 2.3f + Gdx.graphics.getHeight() * 0.14f);
        groupBtnDefender.addActor(btn3);
    }

    //drawattachers
    private void drawAttachers(){
        attachers = new DrawAttachers();
        Button btn1 = new Button(new ImageButton.ImageButtonStyle());
        btn1.setName("btn4");
        btn1.setSize(Gdx.graphics.getWidth() * 0.18f, Gdx.graphics.getWidth() * 0.2f);
        btn1.setPosition(Gdx.graphics.getWidth() - btn1.getWidth() * 1.13f, Gdx.graphics.getHeight() / 2.3f - Gdx.graphics.getHeight() * 0.14f);
        groupBtnDefender.addActor(btn1);
        Button btn2 = new Button(new ImageButton.ImageButtonStyle());
        btn2.setName("btn5");
        btn2.setSize(Gdx.graphics.getWidth() * 0.18f, Gdx.graphics.getWidth() * 0.2f);
        btn2.setPosition(Gdx.graphics.getWidth() - btn2.getWidth() * 1.13f, Gdx.graphics.getHeight() / 2.3f);
        groupBtnDefender.addActor(btn2);
        Button btn3 = new Button(new ImageButton.ImageButtonStyle());
        btn3.setName("btn6");
        btn3.setSize(Gdx.graphics.getWidth() * 0.18f, Gdx.graphics.getWidth() * 0.2f);
        btn3.setPosition(Gdx.graphics.getWidth() - btn3.getWidth() * 1.13f, Gdx.graphics.getHeight() / 2.3f + Gdx.graphics.getHeight() * 0.14f);
        groupBtnDefender.addActor(btn3);
    }

    public void updatePlayers(){
        attachers.removeGroup();
        defenders.removeGroup();
        attachers.updateList();
        defenders.updateList();
        stage.addActor(attachers.getGroupattachers());
        stage.addActor(defenders.getGroupDefenders());
    }

    private void addcomponenttostage() {
        progressBarEnergy.addGrouptoStage(stage);
        stage.addActor(groupCenter);
        stage.addActor(groupTop);
        stage.addActor(defenders.getGroupDefenders());
        stage.addActor(attachers.getGroupattachers());
        stage.addActor(buttonJoinStartCombat);
        combatFight.AddStage(stage);
    }

//    public ProgressBarEnergy getProgressBarEnergy() {
//        return progressBarEnergy;
//   }

    @Override
    public void show() {
        CameraGroupStrategy cameraGroupStrategy = new CameraGroupStrategy(camera);
        decalBatch = new DecalBatch(cameraGroupStrategy);
        stagefordialog = new Stage(new StretchViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()));
        stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())) {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.BACK) {
                    if (InformationProfile.getInstance().getDateUserGame().getEnklaveCombatId() == -1)
                        gameManager.setScreen(gameManager.screenEnklave);
                    else
                        dialogExit("You can't out from combat!");
                }
                return super.keyDown(keyCode);
            }
        };
        drawtopcombat();
        drawmap();
        drawdefenders();
        drawAttachers();
        addBtnStartCombat();
        combatFight = new CombatFitght(this, WIDTH * 0.6f);
        addcomponenttostage();
        progressBarEnergy.FadeIn();
        Gdx.input.setCatchBackKey(true);
        InputMultiplexer inputmulti = new InputMultiplexer();
        GestureDetector gd = new GestureDetector(this);
        inputmulti.addProcessor(stage);
        inputmulti.addProcessor(gd);
        inputmulti.addProcessor(this);
        Gdx.input.setInputProcessor(inputmulti);
    }
    private float valShield = InformationEnklave.getInstance().getShields();
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        updateDistance();
        decalBatch.add(decalBackground);
        //decalBatch.add(decalenklave);
        decalBatch.flush();
        stage.act();
        stage.draw();
        combatFight.renderCercle(cam, 1, Color.WHITE);
        if(valShield > InformationEnklave.getInstance().getShields()) {
            combatFight.renderCercle(cam, (1 - ((valShield -= 2) /(float) InformationEnklave.getInstance().getEnergyfullshield())), Color.BLACK);
        }else{
            combatFight.renderCercle(cam, (1 - ((float)InformationEnklave.getInstance().getShields() / (float)InformationEnklave.getInstance().getEnergyfullshield())), Color.BLACK);
            valShield = InformationEnklave.getInstance().getShields();
        }
        if(circleShow){
            startTime += Gdx.graphics.getDeltaTime();
            combatFight.renderCercleRecharge(cam, 1, Color.WHITE);
            combatFight.renderCercleRecharge(cam, startTime/InformationProfile.getInstance().getDataCombat().getTimeRecharging(), Color.BLACK);
            if(startTime > InformationProfile.getInstance().getDataCombat().getTimeRecharging()){
                circleShow = false;
                combatFight.StartTimer();
            }
        }
        if(showtextexit){
            dialogExit(messageShow);
            showtextexit = false;
            if(!messageShow.contentEquals("You lose!")){
                new Timer().scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        managerAssets.getAssertEnklaveScreen().setIsupdate(false);
                        new GetEnklaveDetails().makeRequest(InformationEnklave.getInstance().getId(), managerAssets);
                        gameManager.setScreen(gameManager.screenEnklave);
                    }
                },2f);
            }
        }
        stagefordialog.act();
        stagefordialog.draw();
    }

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho(false);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        new UnsubscribeCombat().makeRequest(InformationEnklave.getInstance().getId());
        managerAssets.getAssetsCombat().setCombat = false;
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        //renderer.dispose();
        //managerAssets.getAssetsCombat().dispose();
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
        clickdefender = false;
        clickattachers = false;
        dist = 0;
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

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        if(pointer == 0){
            if(x <defenders.getXmax() && y < Gdx.graphics.getHeight()-defenders.getYmin() && y > Gdx.graphics.getHeight() - defenders.getYmax()){
                clickdefender = true;
            }
            else if(x > attachers.getXmin() && y < Gdx.graphics.getHeight()-attachers.getYmin() && y > Gdx.graphics.getHeight() - attachers.getYmax()){
                clickattachers = true;
            }
        }return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        if(!buttonJoinStartCombat.isVisible() && InformationProfile.getInstance().getDateUserGame().getEnklaveCombatId() != -1) {
            if (x > groupBtnDefender.findActor("btn1").getX() && x < groupBtnDefender.findActor("btn1").getRight() && y > Gdx.graphics.getHeight() - groupBtnDefender.findActor("btn1").getTop() && y < Gdx.graphics.getHeight() - groupBtnDefender.findActor("btn1").getY()) {
                if(defenders.getListOfDefenders().size()>0) {
                    clickDefenders(0);
                }
            } else if (x > groupBtnDefender.findActor("btn2").getX() && x < groupBtnDefender.findActor("btn2").getRight() && y > Gdx.graphics.getHeight() - groupBtnDefender.findActor("btn2").getTop() && y < Gdx.graphics.getHeight() - groupBtnDefender.findActor("btn2").getY()) {
                if(defenders.getListOfDefenders().size()>0) {
                    clickDefenders(1);
                }
            } else if (x > groupBtnDefender.findActor("btn3").getX() && x < groupBtnDefender.findActor("btn3").getRight() && y > Gdx.graphics.getHeight() - groupBtnDefender.findActor("btn3").getTop() && y < Gdx.graphics.getHeight() - groupBtnDefender.findActor("btn3").getY()) {
                if(defenders.getListOfDefenders().size()>0) {
                    clickDefenders(2);
                }
            } else if (x > groupBtnDefender.findActor("btn4").getX() && x < groupBtnDefender.findActor("btn4").getRight() && y > Gdx.graphics.getHeight() - groupBtnDefender.findActor("btn4").getTop() && y < Gdx.graphics.getHeight() - groupBtnDefender.findActor("btn4").getY()) {
                if(attachers.getListOfAttachers().size()>0) {
                    clickAttachers(0);
                }
            } else if (x > groupBtnDefender.findActor("btn5").getX() && x < groupBtnDefender.findActor("btn5").getRight() && y > Gdx.graphics.getHeight() - groupBtnDefender.findActor("btn5").getTop() && y < Gdx.graphics.getHeight() - groupBtnDefender.findActor("btn5").getY()) {
                if(attachers.getListOfAttachers().size()>0) {
                    clickAttachers(1);
                }
            } else if (x > groupBtnDefender.findActor("btn6").getX() && x < groupBtnDefender.findActor("btn6").getRight() && y > Gdx.graphics.getHeight() - groupBtnDefender.findActor("btn6").getTop() && y < Gdx.graphics.getHeight() - groupBtnDefender.findActor("btn6").getY()) {
                if(attachers.getListOfAttachers().size()>0) {
                    clickAttachers(2);
                }
            }
        }
        return false;
    }
    public void clickDefenders(int i){
        defenders.changeFrame(i);
        if(ListOfDefenders.getInstance().isSelectedItem) {
            attachers.deselect();
            combatFight.deselect();
            if (InformationEnklave.getInstance().getFaction() != InformationProfile.getInstance().getDateUserGame().getFaction())
                combatFight.SelectFire();
            else
                combatFight.SelectRecharge();
        }
    }
    public  void clickAttachers(int i){
        attachers.changeFrame(i);
        if(ListOfAttachers.getInstance().isSelectedItem) {
            defenders.deselect();
            combatFight.deselect();
            if (InformationEnklave.getInstance().getFaction() == InformationProfile.getInstance().getDateUserGame().getFaction() )
                combatFight.SelectFire();
            else
                combatFight.SelectRecharge();
        }
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
        if(!buttonJoinStartCombat.isVisible() && InformationProfile.getInstance().getDateUserGame().getEnklaveCombatId() != -1) {
            dist += deltaY ;
            if (clickattachers) {
                dist = attachers.translateslow(attachers.getArrayAttachers(), dist, false);
            } else if (clickdefender) {
                dist = defenders.translateslow(defenders.getArrayDefenders(), dist, false);
            }
        }
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        if(!buttonJoinStartCombat.isVisible() && InformationProfile.getInstance().getDateUserGame().getEnklaveCombatId() != -1) {
            if (clickattachers) {
                dist = attachers.translateslow(attachers.getArrayAttachers(), dist, true);
            } else if (clickdefender) {
                dist = defenders.translateslow(defenders.getArrayDefenders(), dist, true);
            }
            clickdefender = false;
            clickattachers = false;
            dist = 0;
        }
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
}
