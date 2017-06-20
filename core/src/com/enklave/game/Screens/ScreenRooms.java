package com.enklave.game.Screens;

import com.enklave.game.FontLabel.Font;
import com.enklave.game.GameManager;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.MapsService.MapPixmap;
import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * Created by adrian on 11.03.2016.
 */
public class ScreenRooms implements Screen {
    private final PerspectiveCamera camera;
    private final OrthographicCamera cam;
    private final ImmediateModeRenderer20 renderer;
    private GameManager gameManager;
    boolean drawone = true;
    private CameraGroupStrategy cameraGroupStrategy;
    private Stage stage;
    private SpriteBatch batchsprite;
    private DecalBatch batch;
    private Decal decalenklave,selectimage,pulsImage,decalBackground;
    private Group groupBtn,groupbtnRoomExtension,grouptimerbuild,groupBuildRoom;
    private ImageTextButton buttonExtension;
    private Label labelprocent;
    float p = 1f, v = -1f;
    String bup = "button-up",bdown = "button-down", bcheck = "button-checked";
    private boolean timerbuildroom = false;
    private int selectRoom = 0;
    private Texture lookup;
    private ManagerAssets manager;

    public ScreenRooms(GameManager gameManager) {
        this.gameManager = gameManager;
        manager = ManagerAssets.getInstance();
        camera = new PerspectiveCamera(40, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 0, Gdx.graphics.getHeight()*0.183f);//initial 180
        camera.lookAt(0, 0, 0);
        camera.far = 2500;
        camera.near = 1;
        camera.update();
        cam = new OrthographicCamera();
        renderer = new ImmediateModeRenderer20(false, true, 1);
    }
    public void addroomselect(){
        lookup = manager.getAssetsRooms().getTexture(NameFiles.progressbarcircular);
        lookup.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        final TextureRegion textureLeft = new TextureRegion(manager.getAssetsRooms().getTexture(NameFiles.selectroomleft));
        final TextureRegion textureRight = new TextureRegion(manager.getAssetsRooms().getTexture(NameFiles.selectroomright));
        final TextureRegion textureBottom = new TextureRegion(manager.getAssetsRooms().getTexture(NameFiles.selectroombottom));
        decalenklave = Decal.newDecal(new TextureRegion(manager.getAssetsRooms().getTexture(NameFiles.topenklaveimage)));
        decalenklave.setPosition(0, 0, 5);
        decalenklave.setDimensions(Gdx.graphics.getHeight()*0.104f, Gdx.graphics.getHeight()*0.104f);
        decalenklave.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        selectimage =Decal.newDecal(new TextureRegion(manager.getAssetsRooms().getTexture(NameFiles.noselectRooms)));
        selectimage.setPosition(0, 0, 8);
        selectimage.setDimensions(Gdx.graphics.getHeight()*0.104f, Gdx.graphics.getHeight()*0.104f);
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
        Button b2 =new Button(new Button.ButtonStyle());
        b2.setPosition(Gdx.graphics.getWidth() / 1.7f, Gdx.graphics.getHeight() * 0.5f);
        b2.setSize(Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getWidth() * 0.2f);
        b2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectimage.setTextureRegion(textureRight);
                selectRoom = 2;
            }
        });
        Button b3 =new Button(new Button.ButtonStyle());
        b3.setPosition(Gdx.graphics.getWidth() / 2 - b1.getWidth() / 2, Gdx.graphics.getHeight() * 0.29f);
        b3.setSize(Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getWidth() * 0.2f);
        b3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectimage.setTextureRegion(textureBottom);
                selectRoom = 3;
            }
        });
        Image stobottom = new Image(new TextureRegion(manager.getAssetsRooms().getTexture(NameFiles.imageArrowBottom)));
        stobottom.setSize(Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getWidth() * 0.22f);
        stobottom.setPosition(Gdx.graphics.getWidth() / 2 - stobottom.getWidth() / 2, Gdx.graphics.getHeight() * 0.74f);
        Image selectroomtext = new Image(new TextureRegion(manager.getAssetsRooms().getTexture(NameFiles.txtSelectRoom)));
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
        groupBtn.addActor(b1);
        groupBtn.addActor(b2);
        groupBtn.addActor(b3);
        groupBtn.addActor(stobottom);
        groupBtn.addActor(selectroomtext);
    }
    public void addButtonBottom(){
        groupbtnRoomExtension = new Group();
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
        sty.font = Font.getFont((int) (Gdx.graphics.getHeight() * 0.04f));
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
        sty.font = Font.getFont((int) (Gdx.graphics.getHeight() * 0.04f));
        buttonExtension = new ImageTextButton("ROOM\nSETUP",sty);
        buttonExtension.setSize(Gdx.graphics.getWidth() * 0.4f, Gdx.graphics.getHeight() * 0.1f);
        buttonExtension.setPosition(Gdx.graphics.getWidth() / 2 - Gdx.graphics.getWidth() * 0.028f, Gdx.graphics.getHeight() * 0.05f);
        Image logonew = new Image(new TextureRegion(manager.getAssetsRooms().getTexture(NameFiles.imageArrowRight)));
        logonew.setSize(buttonExtension.getHeight() * 0.85f, buttonExtension.getHeight() * 0.85f);
        logonew.setPosition(Gdx.graphics.getWidth() / 2 - logonew.getWidth() / 2, Gdx.graphics.getHeight() * 0.058f);
        buttonRoom.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttonExtension.getText().toString().contentEquals("CREATE")) {
                    buttonExtension.setText("ROOM\nSETUP");
                    groupBuildRoom.setVisible(false);
                    groupBtn.setTouchable(Touchable.enabled);
                } else {
                    gameManager.setScreen(gameManager.screenEnklave);
                }
            }
        });
        buttonExtension.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectRoom != 0) {
                    if (!buttonExtension.getText().toString().contentEquals("CREATE")) {
                        buttonExtension.setText("CREATE");
                        groupBtn.setTouchable(Touchable.disabled);
                        groupBuildRoom.setVisible(true);
                    } else {
                        timerbuildroom = true;
                        grouptimerbuild.setVisible(true);
                        groupbtnRoomExtension.setTouchable(Touchable.disabled);
                        groupBuildRoom.setTouchable(Touchable.disabled);
                        p = 1f;
                    }
                }
            }
        });
        groupbtnRoomExtension.addActor(buttonRoom);
        groupbtnRoomExtension.addActor(buttonExtension);
        groupbtnRoomExtension.addActor(logonew);
        skin = new Skin();
        skin.dispose();
        txt = new Texture(0,0, Pixmap.Format.RGBA8888);
        txt.dispose();
        txta = new TextureAtlas();
        txta.dispose();
    }
    private void addtimerbuildroom(){
        grouptimerbuild = new Group();
        Image bg = new Image(new TextureRegion(manager.getAssetsRooms().getTexture(NameFiles.extensionImgBackground)));
        bg.setSize(Gdx.graphics.getWidth() / 2.75f, Gdx.graphics.getHeight() *0.32f);
        bg.setPosition(Gdx.graphics.getWidth() / 2 - bg.getWidth() / 2, Gdx.graphics.getHeight() / 5f);
        grouptimerbuild.addActor(bg);
        Label labelBuildingRoom = new Label("Building Room",new Label.LabelStyle(Font.getFont((int)(Gdx.graphics.getHeight()*0.025)), Color.WHITE));
        labelBuildingRoom.setPosition(Gdx.graphics.getWidth() / 2 - labelBuildingRoom.getWidth() / 2, Gdx.graphics.getHeight() / 2.2f);
        grouptimerbuild.addActor(labelBuildingRoom);
        labelprocent = new Label("0%",new Label.LabelStyle(new Label.LabelStyle(Font.getFont((int)(Gdx.graphics.getHeight()*0.03)),Color.WHITE)));
        labelprocent.setPosition(Gdx.graphics.getWidth() / 2 - labelprocent.getWidth() / 2, Gdx.graphics.getHeight() / 3.1f);
        grouptimerbuild.addActor(labelprocent);
        grouptimerbuild.setVisible(false);
    }
    public void addPuls(){
        pulsImage = Decal.newDecal(new TextureRegion(manager.getAssetsRooms().getTexture(NameFiles.imagePulse)));
        pulsImage.setDimensions(Gdx.graphics.getHeight()*0.0104f, Gdx.graphics.getHeight()*0.0104f);
        pulsImage.setPosition(0, 0, 3);
        pulsImage.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    private void addtostage(){
        stage.addActor(groupBtn);
        stage.addActor(groupbtnRoomExtension);
        stage.addActor(groupBuildRoom);
        stage.addActor(grouptimerbuild);
    }
    private void addimagebg(){
        MapPixmap imageBG = MapPixmap.getInstance();
        decalBackground = Decal.newDecal(new TextureRegion(new Texture(imageBG.getImage(1,1))));
        decalBackground.setPosition(0, 0, 0);
        decalBackground.setDimensions(Gdx.graphics.getHeight() * 0.3125f, Gdx.graphics.getHeight() * 0.3125f);
        decalBackground.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    private void addbuildRoom(){
        groupBuildRoom = new Group();
        Image background = new Image(new TextureRegion(manager.getAssetsRooms().getTexture(NameFiles.extensionImgBackground)));
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() * 0.3f);
        background.setPosition(0, Gdx.graphics.getHeight() * 0.20f);
        groupBuildRoom.addActor(background);
        Image imCommCenter = new Image(new TextureRegion(manager.getAssetsRooms().getTexture(NameFiles.imageCommCenter)));
        imCommCenter.setSize(Gdx.graphics.getWidth() * 0.31f, Gdx.graphics.getHeight() * 0.174f);
        imCommCenter.setPosition(Gdx.graphics.getWidth() * 0.012f, Gdx.graphics.getHeight() * 0.27f);
        Image imBarrack = new Image(new TextureRegion(manager.getAssetsRooms().getTexture(NameFiles.imageBarrack)));
        imBarrack.setSize(Gdx.graphics.getWidth() * 0.31f, Gdx.graphics.getHeight() * 0.174f);
        imBarrack.setPosition(Gdx.graphics.getWidth() * 0.34f, Gdx.graphics.getHeight() * 0.27f);
        Image imLaboratory= new Image(new TextureRegion(manager.getAssetsRooms().getTexture(NameFiles.imageLaboratory)));
        imLaboratory.setSize(Gdx.graphics.getWidth() * 0.31f, Gdx.graphics.getHeight() * 0.174f);
        imLaboratory.setPosition(Gdx.graphics.getWidth() * 0.67f, Gdx.graphics.getHeight() * 0.27f);
        groupBuildRoom.addActor(imLaboratory);
        groupBuildRoom.addActor(imCommCenter);
        groupBuildRoom.addActor(imBarrack);
        Image border = new Image(new TextureRegion(manager.getAssetsRooms().getTexture(NameFiles.borderImageBlue)));
        border.setSize(Gdx.graphics.getWidth() * 0.963f, Gdx.graphics.getHeight() * 0.0055f);
        border.setPosition(Gdx.graphics.getWidth() * 0.01f, imCommCenter.getY() - 2 * border.getHeight());
        groupBuildRoom.addActor(border);
        Label lcommcenter = new Label("Comm Center",new Label.LabelStyle(Font.getFont((int)(Gdx.graphics.getHeight()*0.025)), Color.WHITE));
        lcommcenter.setSize(imCommCenter.getWidth(), Gdx.graphics.getHeight() * 0.018f);
        lcommcenter.setPosition(imCommCenter.getRight() / 6f, background.getY() + border.getHeight() * 5);
        groupBuildRoom.addActor(lcommcenter);
        Label lbarrack = new Label("Barrack",new Label.LabelStyle(Font.getFont((int)(Gdx.graphics.getHeight()*0.025)),Color.WHITE));
        lbarrack.setSize(imCommCenter.getWidth(), Gdx.graphics.getHeight() * 0.018f);
        lbarrack.setPosition(imBarrack.getRight() - imBarrack.getWidth() * 0.75f, background.getY() + border.getHeight() * 5);
        groupBuildRoom.addActor(lbarrack);
        Label llaboratory = new Label("Laboratory",new Label.LabelStyle(Font.getFont((int)(Gdx.graphics.getHeight()*0.025)),Color.WHITE));
        llaboratory.setSize(imCommCenter.getWidth(), Gdx.graphics.getHeight() * 0.018f);
        llaboratory.setPosition(imLaboratory.getRight() - imLaboratory.getWidth() * 0.85f, background.getY() + border.getHeight() * 5);
        groupBuildRoom.addActor(llaboratory);
        final Image frame2 = new Image(new TextureRegion(manager.getAssetsRooms().getTexture(NameFiles.frameEnk)));
        frame2.setSize(Gdx.graphics.getWidth() * 0.27f, Gdx.graphics.getHeight() * 0.21f);
        frame2.setPosition(Gdx.graphics.getWidth() * 0.36f, Gdx.graphics.getHeight() * 0.25f);//center 0.36
        groupBuildRoom.addActor(frame2);

        final Image bottomborder = new Image(new TextureRegion(manager.getAssetsRooms().getTexture(NameFiles.borderUpdown)));
        bottomborder.setSize(Gdx.graphics.getWidth() * 0.303f, Gdx.graphics.getHeight() * 0.027f);
        bottomborder.setPosition(Gdx.graphics.getWidth() * 0.344f, Gdx.graphics.getHeight() * 0.27f);
        groupBuildRoom.addActor(bottomborder);
        final Image topborder = new Image(new TextureRegion(manager.getAssetsRooms().getTexture(NameFiles.borderUpdown)));
        topborder.setSize(Gdx.graphics.getWidth() * 0.303f, Gdx.graphics.getHeight()*0.027f);
        topborder.setPosition(Gdx.graphics.getWidth() * 0.344f, frame2.getTop()  - topborder.getHeight() * 0.5f-frame2.getHeight()/2);
        topborder.addAction(addMove(Gdx.graphics.getWidth() * 0.344f,frame2.getTop()  - topborder.getHeight() * 0.5f));
        groupBuildRoom.addActor(topborder);
        imCommCenter.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                frame2.setPosition(Gdx.graphics.getWidth() * 0.03f, Gdx.graphics.getHeight() * 0.25f);
                bottomborder.setPosition(Gdx.graphics.getWidth() * 0.014f, Gdx.graphics.getHeight() * 0.27f);
                topborder.setPosition(Gdx.graphics.getWidth() * 0.014f, frame2.getTop() - topborder.getHeight() * 0.5f-frame2.getHeight()/2);
                topborder.addAction(addMove(Gdx.graphics.getWidth() * 0.014f,frame2.getTop()  - topborder.getHeight() * 0.5f));
            }
        });
        imBarrack.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                frame2.setPosition(Gdx.graphics.getWidth() * 0.36f, Gdx.graphics.getHeight() * 0.25f);
                bottomborder.setPosition(Gdx.graphics.getWidth() * 0.344f, Gdx.graphics.getHeight() * 0.27f);
                topborder.setPosition(Gdx.graphics.getWidth() * 0.344f, frame2.getTop() - topborder.getHeight() * 0.5f-frame2.getHeight()/2);
                topborder.addAction(addMove(Gdx.graphics.getWidth() * 0.344f,frame2.getTop()  - topborder.getHeight() * 0.5f));
            }
        });
        imLaboratory.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                frame2.setPosition(Gdx.graphics.getWidth() * 0.69f, Gdx.graphics.getHeight() * 0.25f);
                bottomborder.setPosition(Gdx.graphics.getWidth() * 0.674f, Gdx.graphics.getHeight() * 0.27f);
                topborder.setPosition(Gdx.graphics.getWidth() * 0.674f, frame2.getTop() - topborder.getHeight() * 0.5f-frame2.getHeight()/2);
                topborder.addAction(addMove(Gdx.graphics.getWidth() * 0.674f,frame2.getTop()  - topborder.getHeight() * 0.5f));
            }
        });
        groupBuildRoom.setVisible(false);
    }
    private MoveToAction addMove(float x,float y){
        MoveToAction move = new MoveToAction();
        move.setPosition(x, y);
        move.setDuration(0.5f);
        return move;
    }

    @Override
    public void show() {
        if(drawone){
            addroomselect();
            addButtonBottom();
            addPuls();
            addimagebg();
            addbuildRoom();
            addtimerbuildroom();
            drawone = false;
        }
        cameraGroupStrategy = new CameraGroupStrategy(camera);
//        CameraInputController controller = new CameraInputController(camera);
        stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()));
        batchsprite = new SpriteBatch();
        addtostage();
        batch = new DecalBatch(this.cameraGroupStrategy);
        Gdx.input.setCatchBackKey(true);
        InputMultiplexer inputmulti = new InputMultiplexer();
        inputmulti.addProcessor(stage);
        Gdx.input.setInputProcessor(inputmulti);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        scalepulse();
        batch.add(decalBackground);
        batch.add(decalenklave);
        batch.add(selectimage);
        batch.add(pulsImage);
        batch.flush();
        stage.act();
        stage.draw();
        if(timerbuildroom) {
            float r = Gdx.graphics.getHeight()*0.078f;
            float cx = Gdx.graphics.getWidth() / 2;
            float cy = Gdx.graphics.getHeight() / 3;
            float thickness = Gdx.graphics.getHeight()*0.0104f;

            //increase percentage
            p += v * 0.15f * Gdx.graphics.getDeltaTime();
            labelprocent.setText("Done!");
            progress(cx, cy, r, thickness, 1f, Color.WHITE, lookup);
            if (p > 0f) {
                labelprocent.setText((100-Math.round(p*100))+"%");
                progress(cx, cy, r, thickness, p, Color.GRAY, lookup);
            }
            if(p < -0.5f){
                timerbuildroom = false;
                grouptimerbuild.setVisible(false);
                groupBuildRoom.setVisible(false);
                buttonExtension.setText("ROOM\nSETUP");
                groupBtn.setTouchable(Touchable.enabled);
                groupbtnRoomExtension.setTouchable(Touchable.enabled);
                groupBuildRoom.setTouchable(Touchable.enabled);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            gameManager.setScreen(gameManager.screenEnklave);
        }
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
            //renderer.
            renderer.color(c.r, c.g, c.b, c.a);
            renderer.texCoord(tc, 1f);
            renderer.vertex(cx + fx * (r + halfThick), cy + fy * (r + halfThick), z);

            renderer.color(c.r, c.g, c.b, c.a);
            renderer.texCoord(tc, 0f);
            renderer.vertex(cx + fx * (r + -halfThick), cy + fy * (r + -halfThick), z);
        }
        renderer.end();
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
        dispose();
    }

    @Override
    public void dispose() {
        cameraGroupStrategy.dispose();
        batch.dispose();
        stage.dispose();
        batchsprite.dispose();
        lookup.dispose();
        //manager.getAssetsRooms().dispose();
    }
}
