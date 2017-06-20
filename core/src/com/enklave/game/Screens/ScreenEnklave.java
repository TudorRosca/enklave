package com.enklave.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.enklave.game.Combat.Request.SubscribeCombat;
import com.enklave.game.Craft.QueueBuildCraft;
import com.enklave.game.Enklave.DescEnklave.InformationEnklave;
import com.enklave.game.Enklave.Enklave3D;
import com.enklave.game.Enklave.RangeEnklave;
import com.enklave.game.FontLabel.Font;
import com.enklave.game.GameManager;
import com.enklave.game.Interfaces.InterfaceQueueFactory;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.MapsService.Bounds;
import com.enklave.game.MapsService.MapPixmap;
import com.enklave.game.MapsService.MyLocation;
import com.enklave.game.Profile.InformationProfile;
import com.enklave.game.Utils.NameFiles;


public class ScreenEnklave implements Screen,InterfaceQueueFactory {

    private final InformationProfile prof;
    private final Bounds bounds;
    private Vector2 coordonatedraw;
    private Vector2[][] pos;
    private GameManager gameManager;
    private final int WIDTH = Gdx.graphics.getWidth(), HEIGHT = Gdx.graphics.getHeight();
    private boolean drawone = true;
    private Enklave3D enklave3D;
    private String bup = "button-up",bdown = "button-down", bcheck = "button-checked";
    private ImageButton backButton;
    private ProgressBarEnergy progressBarEnergy;
    private Decal[] extendmaps;
    private Image logo,disaibleButton,disaibleButton1 = null;
    private Label labelName;
    private ImageTextButton buttonCombat,buttonDeveloper;
    private PerspectiveCamera camera;
    private CameraGroupStrategy cameraGroupStrategy;
    private DecalBatch batch;
    private Stage stage;
    private ManagerAssets manager;
    private ModelBatch mmodelBatch;
    private Group informationEnklave,deployBricks;
    private int countbrick = 0;
    private int range = 0;
    private InformationEnklave infoEnklave;
    private ImageTextButton over;
    private RangeEnklave rangeEnklave;
    private MyLocation location = MyLocation.getInstance();
    private QueueDisplay queueDisplay;
    private Label labelValue;
    private Label labelLevel;
    private ProgressBar bar;

    public ScreenEnklave(GameManager gameManager) {
        this.gameManager = gameManager;
        manager = ManagerAssets.getInstance();
        pos = new Vector2[3][3];
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++) {
                pos[i][j] = new Vector2();
                pos[i][j].set((HEIGHT * 0.3125f) * (j - 1),-(HEIGHT*0.3125f)*(i-1));
            }
        }
        prof = InformationProfile.getInstance();
        infoEnklave = InformationEnklave.getInstance();
        bounds = new Bounds();
    }

    private void adddrawenklave() {
        Skin skin = new Skin();
        TextureAtlas txta = new TextureAtlas();
        Texture txt = manager.getAssetsButton().get(NameFiles.buttonBack1);
        Vector2 crop = Scaling.fit.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        txta.addRegion(bup, new TextureRegion(txt));
        txta.addRegion(bdown, new TextureRegion(txt));
        txta.addRegion(bcheck, new TextureRegion(txt));
        skin.addRegions(txta);
        ImageButton.ImageButtonStyle sty = new ImageButton.ImageButtonStyle();
        sty.up = skin.getDrawable(bup);
        sty.down = skin.getDrawable(bdown);
        sty.checked = skin.getDrawable(bcheck);
        backButton = new ImageButton(sty);
        backButton.setSize(crop.x*0.15f, crop.y*0.15f);
        backButton.setPosition(backButton.getWidth()*0.25f, HEIGHT - (backButton.getHeight() * 1.25f));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttonCombat.getText().toString().contentEquals("ROOMS ")) {
                    buttonCombat.setText("COMBAT ");
                    buttonDeveloper.setText("    DEVELOPER");
                    deployBricks.setVisible(false);
                    informationEnklave.setVisible(true);
                    disaibleButton.setVisible(true);
                    if(disaibleButton1 !=null)
                        disaibleButton1.setVisible(true);
                } else {
                    manager.getAssertEnklaveScreen().setIsupdate(false);
                    gameManager.setScreen(gameManager.mapsScreen);
                }
            }
        });
        skin = new Skin();
        skin.dispose();
        txta = new TextureAtlas();
        txta.dispose();
        progressBarEnergy = ProgressBarEnergy.getInstance();
    }

    private void addMap() {
        MapPixmap imagesmap = MapPixmap.getInstance();
        extendmaps = new Decal[9];
        int k = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                extendmaps[k] = Decal.newDecal(new TextureRegion(new Texture(imagesmap.getImage(i, j))));
                extendmaps[k].setDimensions((HEIGHT*0.3125f), (HEIGHT*0.3125f));
                k++;
            }
        }
        repositionEnklave();
        queueDisplay = QueueDisplay.getInstance();
    }

    private void addToolInformation() {
        informationEnklave = new Group();
        Texture txt = manager.getAssetsButton().get(NameFiles.imageInformationEnklave);
        Vector2 crop = Scaling.fit.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        Image inform = new Image(new TextureRegion(txt));
        inform.setSize(crop.x * 0.8f, crop.y * 0.8f);
        inform.setPosition(WIDTH / 2 - inform.getWidth() / 2, HEIGHT * 0.6f);
        txt = new Texture(Gdx.files.internal("Images/arcul-de-triumf.jpg"));
        crop = Scaling.fit.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        Image photo = new Image(new TextureRegion(txt));
        photo.setSize(crop.x * 0.23f, crop.y * 0.285f);
        photo.setPosition(WIDTH / 2 - inform.getWidth() / 2.15f, inform.getY()+inform.getHeight()*0.425f);
        informationEnklave.addActor(photo);
        informationEnklave.addActor(inform);
        int level = infoEnklave.getBricks()/9;
        labelLevel = new Label("LVL "+level+" (Bricks:"+infoEnklave.getBricks()+")",new Label.LabelStyle(Font.getFont((int) (Gdx.graphics.getWidth() * 0.025f)), Color.WHITE));
        labelLevel.setPosition( photo.getRight() + labelLevel.getWidth()*0.1f, inform.getTop() - labelLevel.getHeight()*2.5f);
        informationEnklave.addActor(labelLevel);
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap((int)(HEIGHT*0.0105), (int)(HEIGHT*0.0105), Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        pixmap = new Pixmap((int)(HEIGHT*0.0131),(int)(HEIGHT*0.0131), Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        skin.add("green", new Texture(pixmap));
        ProgressBar.ProgressBarStyle barStyle = new ProgressBar.ProgressBarStyle(skin.newDrawable("white", Color.RED), skin.newDrawable("green",Color.CYAN));
        barStyle.knobBefore = barStyle.knob;
        bar = new ProgressBar(0, 9, 1f, false, barStyle);
        bar.setSize(inform.getWidth()/3, HEIGHT * 0.0131f);
        bar.setPosition(labelLevel.getX() , labelLevel.getY() - bar.getHeight() * 2.25f);
        bar.setAnimateDuration(3);
        bar.setValue(infoEnklave.getBricks()%9);
        informationEnklave.addActor(bar);
        labelName = new Label(infoEnklave.getName(),new Label.LabelStyle(Font.getFont((int)(Gdx.graphics.getWidth()*0.03f)), Color.CYAN));
        labelName.setPosition(labelLevel.getX(), bar.getY()- labelName.getHeight()*1.5f);
        informationEnklave.addActor(labelName);
        TextureAtlas txta = new TextureAtlas();
        txt = manager.getAssetsButton().get(NameFiles.buttonShowEnklave);
        txta.addRegion(bup, new TextureRegion(txt));
        txta.addRegion(bdown, new TextureRegion(txt));
        txta.addRegion(bcheck, new TextureRegion(txt));
        skin.addRegions(txta);
        ImageTextButton.ImageTextButtonStyle sty = new ImageTextButton.ImageTextButtonStyle();
        sty.up = skin.getDrawable(bup);
        sty.down = skin.getDrawable(bdown);
        sty.checked = skin.getDrawable(bcheck);
        sty.font = Font.getFont((int) (Gdx.graphics.getHeight() * 0.04));
        ImageTextButton buttonShow = new ImageTextButton("SHOW",sty);
        buttonShow.setSize(inform.getRight()-bar.getRight() - inform.getHeight()*0.05f, photo.getHeight() * 0.9f);
        buttonShow.setPosition(bar.getRight() + inform.getHeight()*0.025f, photo.getY() + inform.getHeight()*0.05f);
        buttonShow.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.loadDescribeEnkla();
                gameManager.setScreen(new ScreenCircleLoading(gameManager,gameManager.screenDescribeEnklave,manager.getAssetsDescribeEnklave()));
            }
        });
        informationEnklave.addActor(buttonShow);
        txta= new TextureAtlas();
        txta.dispose();
        skin =new Skin();
        skin.dispose();
        pixmap =new Pixmap(0,0, Pixmap.Format.RGBA8888);
        pixmap.dispose();
    }

    public void updateinformationEnk(){
        int level = infoEnklave.getBricks()/9;
        labelLevel.setText("LVL "+level+" (Bricks:"+infoEnklave.getBricks()+")");
        bar.setAnimateDuration(2);
        bar.setValue(infoEnklave.getBricks()%9);
        bar.act(Gdx.graphics.getDeltaTime());
    }

    private void addDeployBrick(){
        deployBricks = new Group();
        deployBricks.setVisible(false);
        labelValue = new Label(countbrick+" of "+ prof.getDateBrick().getNumber(),new Label.LabelStyle(Font.getFont((int)(HEIGHT*0.0225f)),Color.ORANGE));
        Texture txt = manager.getAssetsButton().get(NameFiles.backDeployBricks);
        final Image background = new Image(new TextureRegion(txt));
        background.setSize(WIDTH*0.95f, HEIGHT*0.1f);
        background.setPosition(WIDTH*0.025f,HEIGHT*0.75f);
        deployBricks.addActor(background);
        txt = manager.getAssetsButton().get(NameFiles.buttonRight);
        TextureAtlas txta = new TextureAtlas();
        txta.addRegion(bup, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 2));
        txta.addRegion(bdown, new TextureRegion(txt, 0, txt.getHeight() / 2, txt.getWidth(), txt.getHeight() / 2));
        txta.addRegion(bcheck, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 2));
        Skin skin = new Skin();
        skin.addRegions(txta);
        ImageTextButton.ImageTextButtonStyle sty = new ImageTextButton.ImageTextButtonStyle();
        sty.up = skin.getDrawable(bup);
        sty.down = skin.getDrawable(bdown);
        sty.checked = skin.getDrawable(bcheck);
        sty.font = Font.getFont((int) (Gdx.graphics.getHeight() * 0.035));
        ImageTextButton btn = new ImageTextButton("BUILD",sty);
        btn.setSize(background.getWidth()*0.4f,background.getHeight()*0.8f);
        btn.setPosition(background.getX()+background.getWidth()*0.55f,background.getY()+background.getHeight()*0.1f);
        final InterfaceQueueFactory interfaceQueueFactory = this;
        btn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (countbrick > 0 && ((prof.getDateBrick().getUsageEnergy()* countbrick) +prof.getValueenergyuse()) <= prof.getDateUserGame().getEnergy()) {
                    QueueBuildCraft queue = QueueBuildCraft.getInstance();
                    if(!queue.craftOn) {
                        setisOn();
                        createAddQueue();
                    }else {
                        queueDisplay.createexitdialog(interfaceQueueFactory);
                    }
                }else if(countbrick > 0 ){
                    createDialog("You don't have enough energy!");
                }
            }
        });
        deployBricks.addActor(btn);
        txt = manager.getAssetsButton().get(NameFiles.buttonMinusCraft);
        Image btnminus = new Image(new TextureRegion(txt));
        btnminus.setSize(background.getHeight()/2,background.getHeight()/2);
        btnminus.setPosition(background.getX()+btnminus.getWidth(),background.getY()+background.getHeight()/2-btnminus.getHeight()/2);
        final Timer timer = new Timer();
        btnminus.addListener(new ActorGestureListener(){
            @Override
            public boolean longPress(Actor actor, float x, float y) {
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        if(countbrick>0){
                            countbrick--;
                            labelValue.setText(countbrick+" of "+ (prof.getDateBrick().getNumber() - prof.getDateBrick().getNumberBrickUsage()));
                        }
                    }
                },0,0.2f);
                return super.longPress(actor, x, y);
            }

            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if(countbrick>0){
                    countbrick--;
                    labelValue.setText(countbrick+" of "+ (prof.getDateBrick().getNumber() - prof.getDateBrick().getNumberBrickUsage()));
                }
                timer.clear();
            }
        });
        deployBricks.addActor(btnminus);
        Label labelBricks = new Label("BRICKS",new Label.LabelStyle(Font.getFont((int)(HEIGHT*0.035f)),Color.WHITE));
        labelBricks.setPosition(btnminus.getRight()+labelBricks.getWidth()*0.2f,background.getTop()-background.getHeight()/2);
        deployBricks.addActor(labelBricks);
        labelValue.setAlignment(Align.center);
        labelValue.setSize(labelBricks.getWidth(),labelValue.getHeight());
        labelValue.setPosition(labelBricks.getX(),background.getY()+labelValue.getHeight()*0.5f);
        deployBricks.addActor(labelValue);
        Image btnplus = new Image(new TextureRegion(manager.getAssetsButton().get(NameFiles.buttonPlusCraft)));
        btnplus.setSize(btnminus.getWidth(),btnminus.getHeight());
        btnplus.setPosition(labelBricks.getRight()+labelBricks.getWidth()*0.2f,btnminus.getY());
        btnplus.addListener(new ActorGestureListener(){
            @Override
            public boolean longPress(Actor actor, float x, float y) {
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        if(countbrick<InformationProfile.getInstance().getDateBrick().getNumber()){
                            countbrick++;
                            labelValue.setText(countbrick+" of "+ (prof.getDateBrick().getNumber() - prof.getDateBrick().getNumberBrickUsage()));
                        }
                    }
                },0,0.2f);
                return super.longPress(actor, x, y);
            }

            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if(countbrick<InformationProfile.getInstance().getDateBrick().getNumber()){
                    countbrick++;
                    labelValue.setText(countbrick+" of "+ (prof.getDateBrick().getNumber() - prof.getDateBrick().getNumberBrickUsage()));
                }
                timer.clear();
            }
        });
        ActorGestureListener gestureListener = new ActorGestureListener();

        deployBricks.addActor(btnplus);
        txt = new Texture(0,0, Pixmap.Format.RGBA8888);
        txt.dispose();
    }

    @Override
    public void createAddQueue(){
        prof.setValueenergyuse((prof.getDateBrick().getUsageEnergy()* countbrick) +prof.getValueenergyuse());
        prof.getDateBrick().setNumberBrickUsage(prof.getDateBrick().getNumberBrickUsage() + countbrick);
        QueueBuildCraft queue = QueueBuildCraft.getInstance();
        queue.setIdEnk(infoEnklave.getId());
        updatelabel();
        if (queue.isEmpty()) {
            for (int i = 0; i < countbrick; i++) {
                queue.addDeploy();
            }
            queue.startThread();
        } else {
            for (int i = 0; i < countbrick; i++) {
                queue.addDeploy();
            }
        }
        countbrick = 0;
        labelValue.setText(countbrick+" of "+(prof.getDateBrick().getNumber() - prof.getDateBrick().getNumberBrickUsage()));
    }

    @Override
    public void resetResursed(){
        prof.getDateBrick().setNumber(prof.getDateBrick().getNumber() + prof.getDateBrick().getNumberBrickUsage());
        prof.getDateBrick().setNumberBrickUsage(0);
    }

    @Override
    public void setisOn(){
        QueueBuildCraft queue = QueueBuildCraft.getInstance();
        queue.craftOn = false;
        queue.deployOn = true;
    }

    @Override
    public void setforcestop() {
        QueueBuildCraft queue = QueueBuildCraft.getInstance();
        queue.forcestopcrafting = true;
        queue.forcestopdeploy = false;
    }

    public void updatelabel(){
        labelValue.setText(countbrick + " of "+ (prof.getDateBrick().getNumber() - prof.getDateBrick().getNumberBrickUsage()));
        updateinformationEnk();
    }

    private void addButtonsBottom(){
        Skin skin = new Skin();
        BitmapFont bt = Font.getFont((int) (HEIGHT * 0.04));
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
        sty.font = bt;
        buttonCombat = new ImageTextButton("COMBAT ",sty);
        buttonCombat.setSize(Gdx.graphics.getWidth() * 0.4f, Gdx.graphics.getHeight() * 0.1f);
        buttonCombat.setPosition(Gdx.graphics.getWidth() / 2 - buttonCombat.getWidth() + Gdx.graphics.getWidth() * 0.028f, Gdx.graphics.getHeight() * 0.05f);
        buttonCombat.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttonCombat.getText().toString().contentEquals("COMBAT ")) {
                    if(infoEnklave.getFaction()>0) {
                        manager.loadAssetsCombat();
                        new SubscribeCombat().makeRequest(infoEnklave.getId());
                        gameManager.setScreen(new ScreenCircleLoading(gameManager, gameManager.screenCombat, manager.getAssetsCombat()));
                    }
                } else {
                    manager.loadAssetsRooms();
                    gameManager.setScreen(new ScreenCircleLoading(gameManager,gameManager.screenRooms,manager.getAssetsRooms()));
                }
            }
        });
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
        sty.font = bt;
        buttonDeveloper = new ImageTextButton("    DEVELOPER",sty);
        buttonDeveloper.setSize(Gdx.graphics.getWidth() * 0.4f, Gdx.graphics.getHeight() * 0.1f);
        buttonDeveloper.setPosition(Gdx.graphics.getWidth() / 2 - Gdx.graphics.getWidth() * 0.028f, Gdx.graphics.getHeight() * 0.05f);
        buttonDeveloper.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttonCombat.getText().toString().contentEquals("COMBAT ")) {
                    buttonCombat.setText("ROOMS ");
                    buttonDeveloper.setText("    EXTENSIONS");
                    informationEnklave.setVisible(false);
                    deployBricks.setVisible(true);
                    disaibleButton.setVisible(false);
                    if(disaibleButton1 !=null)
                        disaibleButton1.setVisible(false);
                } else {
                    manager.loadAssetExtension();
                    gameManager.setScreen(new ScreenCircleLoading(gameManager,gameManager.screenExtensions,manager.getAssetsExtension()));
                }
            }
        });
        logo = new Image(new TextureRegion(manager.getAssetsButton().get(NameFiles.logoEnklave)));
        logo.setSize(buttonDeveloper.getHeight() * 0.85f, buttonDeveloper.getHeight() * 0.85f);
        logo.setPosition(Gdx.graphics.getWidth() / 2 - logo.getWidth() / 2, Gdx.graphics.getHeight() * 0.057f);
        Pixmap p =new Pixmap(10,10, Pixmap.Format.RGBA8888);
        p.setColor(new Color(0,0,0,0.65f));
        p.fill();
        skin.add("outofrange",new TextureRegion(new Texture(p)));
        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle();
        style.font = Font.getFont((int)(HEIGHT*0.03f));
        style.up = skin.getDrawable("outofrange");
        style.down = skin.getDrawable("outofrange");
        style.checked = skin.getDrawable("outofrange");
        over = new ImageTextButton("Out of Range",style);
        over.setPosition(buttonCombat.getX(),buttonCombat.getY());
        over.setSize(buttonCombat.getWidth() + buttonDeveloper.getWidth(),buttonCombat.getHeight());
        txt = new Texture(0,0, Pixmap.Format.RGBA8888);
        skin = new Skin();
        skin.dispose();
        txt.dispose();
        txta = new TextureAtlas();
        txta.dispose();
    }

    private void repositionEnklave() {
        int k =0;
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++) {
                extendmaps[k].setPosition(-enklave3D.getCoordraw().x + pos[i][j].x, -enklave3D.getCoordraw().y+pos[i][j].y, 0);
                k++;
            }
        }
    }

    public void setEnklave3D(Vector2 latLong) {
        this.coordonatedraw = latLong;
    }

    private void addinfo(Stage st){
        st.addActor(informationEnklave);
        st.addActor(deployBricks);
        st.addActor(buttonCombat);
        st.addActor(buttonDeveloper);
        if(prof.getDateUserGame().getFaction() != infoEnklave.getFaction() ){
            disaibleButton = new Image(new TextureRegion(manager.getAssetsButton().get(NameFiles.disaibleButton)));
            disaibleButton.setSize(buttonDeveloper.getLabel().getWidth(),buttonDeveloper.getHeight());
            disaibleButton.setPosition(buttonDeveloper.getX(),buttonDeveloper.getY());
            st.addActor(disaibleButton);
            if(infoEnklave.getFaction() == 0){
                TextureRegion textureRegion = new TextureRegion(manager.getAssetsButton().get(NameFiles.disaibleButton));
                textureRegion.flip(true,true);
                disaibleButton1 = new Image(textureRegion);
                disaibleButton1.setSize(buttonCombat.getWidth(),buttonCombat.getHeight());
                disaibleButton1.setPosition(buttonCombat.getX(),buttonCombat.getY());
                st.addActor(disaibleButton1);
                disaibleButton.remove();
            }
        }else if(!infoEnklave.isStatusCombat() ){
            TextureRegion textureRegion = new TextureRegion(manager.getAssetsButton().get(NameFiles.disaibleButton));
            textureRegion.flip(true,true);
            disaibleButton = new Image(textureRegion);
            disaibleButton.setSize(buttonCombat.getWidth(),buttonCombat.getHeight());
            disaibleButton.setPosition(buttonCombat.getX(),buttonCombat.getY());
            st.addActor(disaibleButton);
        }
        st.addActor(logo);
        st.addActor(over);
        queueDisplay.AddtoStage(st);
    }

    @Override
    public void show() {
        enklave3D = new Enklave3D(new Vector2(),InformationEnklave.getInstance().getFaction());
        enklave3D.FrontEnklave(InformationEnklave.getInstance().getFaction());
        this.enklave3D.setCoordraw(coordonatedraw);
        camera = new PerspectiveCamera(35, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, -222, 350);//initial 180
        camera.lookAt(0, 50, 0);
        camera.far = 2500;
        camera.near = 1;
        camera.update();
        this.cameraGroupStrategy = new CameraGroupStrategy(camera);
        CameraInputController controller = new CameraInputController(camera);
        batch = new DecalBatch(this.cameraGroupStrategy);
        stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())){
            @Override
            public boolean keyDown(int keyCode) {
                if(keyCode == Input.Keys.BACK){
                    if (buttonCombat.getText().toString().contentEquals("ROOMS ")) {
                        buttonCombat.setText("COMBAT ");
                        buttonDeveloper.setText("    DEVELOPER");
                        deployBricks.setVisible(false);
                        informationEnklave.setVisible(true);
                        disaibleButton.setVisible(true);
                        if(disaibleButton1 !=null)
                            disaibleButton1.setVisible(true);
                    } else {
                        manager.getAssertEnklaveScreen().setIsupdate(false);
                        gameManager.setScreen(gameManager.mapsScreen);
                    }
                }
                return super.keyDown(keyCode);
            }
        };
        if(drawone){
            adddrawenklave();
            addToolInformation();
            addDeployBrick();
            addButtonsBottom();
            addMap();
            if (InformationProfile.getInstance().getDateUserGame().isInCombat()) {
                manager.loadAssetsCombat();
                new SubscribeCombat().makeRequest(InformationEnklave.getInstance().getId());
                gameManager.setScreen(new ScreenCircleLoading(gameManager, gameManager.screenCombat, manager.getAssetsCombat()));
            }
            drawone =false;
        }
        updatelabel();
        rangeEnklave = new RangeEnklave((float)location.getLatitude(),(float)location.getLongitude(),infoEnklave.getLatitude(),infoEnklave.getLongitude(),this,range);
        if (infoEnklave.getId() !=16066)rangeEnklave.start();
        else setvisibility(false);
        manager.getAssertEnklaveScreen().setIsupdate(false);
        labelName.setText(InformationEnklave.getInstance().getName());
        backButton.setChecked(false);
        progressBarEnergy.addGrouptoStage(stage);
        stage.addActor(backButton);
        addinfo(stage);
        int level = infoEnklave.getBricks()/9;
        if(QueueBuildCraft.getInstance().deployOn) {
            labelLevel.setText("LVL " + level + " (Bricks:" + (infoEnklave.getBricks()-1) + ")");
        }else{
            labelLevel.setText("LVL " + level + " (Bricks:" + infoEnklave.getBricks() + ")");
        }
        mmodelBatch = new ModelBatch();
        progressBarEnergy.FadeIn();
        controller.camera.lookAt(enklave3D.getDecalbrick(0).getCenterX(), enklave3D.getDecalbrick(0).getCenterY(), enklave3D.getDecalbrick(0).getCenterZ());
        Gdx.input.setCatchBackKey(true);
        InputMultiplexer inputmulti = new InputMultiplexer();
        inputmulti.addProcessor(stage);
        Gdx.input.setInputProcessor(inputmulti);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        if (!rangeEnklave.isAlive() & infoEnklave.getId() != 16066){
            rangeEnklave = new RangeEnklave((float)location.getLatitude(),(float)location.getLongitude(),infoEnklave.getLatitude(),infoEnklave.getLongitude(),this,range);
            rangeEnklave.start();
        }
        for (int i = 0; i < 9; i++) {
            batch.add(extendmaps[i]);
        }
        batch.flush();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        enklave3D.getInstanceModel().transform.rotate(0,1,0,1);
        mmodelBatch.begin(camera);
        enklave3D.EnklaveDraw(mmodelBatch);
        mmodelBatch.end();

        stage.act(delta);
        stage.draw();
//        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
//            manager.getAssertEnklaveScreen().setIsupdate(false);
//            gameManager.setScreen(gameManager.mapsScreen);
//
//        }
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
        progressBarEnergy.Fadeout();
        dispose();
    }

    @Override
    public void dispose() {
        cameraGroupStrategy.dispose();
        batch.dispose();
        stage.dispose();
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setvisibility(boolean fl){
        over.setVisible(fl);
        if(deployBricks.isVisible() && fl) {
            if (buttonCombat.getText().toString().contentEquals("ROOMS ")) {
                buttonCombat.setText("COMBAT ");
                buttonDeveloper.setText("    DEVELOPER");
                deployBricks.setVisible(false);
                informationEnklave.setVisible(true);
            }
        }
    }

    public void createDialog(String mesaj) {
        Pixmap p = new Pixmap(WIDTH,HEIGHT, Pixmap.Format.RGBA8888);
        p.setColor(1.0f, 1.0f, 1.0f, 0.0f);
        p.fill();
        Skin s = new Skin();
        s.add("black-background", new Texture(p));
        s.add("default", new Label.LabelStyle(new BitmapFont(false), Color.WHITE));
        s.add("default", new Window.WindowStyle(new BitmapFont(false), Color.WHITE, s.newDrawable("black-background", Color.DARK_GRAY)));
        final Dialog dialog = new Dialog("",s);
        s =new Skin();
        s.dispose();
        Label label = new Label(mesaj,new Label.LabelStyle(Font.getFont((int) (HEIGHT * 0.03)),Color.ORANGE));
        label.setPosition(WIDTH/2-label.getWidth()/2,HEIGHT*0.125f);
        p = new Pixmap(WIDTH,HEIGHT, Pixmap.Format.RGBA8888);
        p.setColor(0.0f, 0.0f, 1.0f, 1.0f);
        p.fill();
        Image back = new Image(new TextureRegion(new Texture(p)));
        back.setSize(label.getWidth(),label.getHeight());
        back.setPosition(label.getX(),label.getY());
        dialog.addActor(back);
        dialog.addActor(label);
        p = new Pixmap(0,0, Pixmap.Format.RGBA8888);
        p.dispose();
        dialog.show(stage);
        new Timer().schedule(new Timer.Task() {
            @Override
            public void run() {
                dialog.hide();
            }
        } ,1.5f);
//        {
//            @Override
//            public void run() {
//                dialog.hide();
//            }
//        },1500);
    }
}
