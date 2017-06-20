package com.enklave.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
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
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.enklave.game.Craft.QueueBuildCraft;
import com.enklave.game.Enum.MaterialsCraft;
import com.enklave.game.Enum.Menu;
import com.enklave.game.FontLabel.Font;
import com.enklave.game.GameManager;
import com.enklave.game.Interfaces.InterfaceQueueFactory;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.Profile.InformationProfile;
import com.enklave.game.Utils.NameFiles;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class ScreenCrafting implements Screen,InputProcessor,GestureDetector.GestureListener,InterfaceQueueFactory {
    private GameManager gameManager;
    private ManagerAssets managerAssets;
    private Stage stage;
    private ImageButton backButton,buttoncraft;
    private Table ta;
    private ImageTextButton b2;
    private ScrollPane sp;
    private Array<ModelInstance> intances;
    private ModelBatch modelBatch;
    private PerspectiveCamera camera;
    private int WIDTH = Gdx.graphics.getWidth(),HEIGHT = Gdx.graphics.getHeight();
    private Environment environment;
    private Array<AnimationController> controllers = new Array<AnimationController>();
    private Decal imageBackground;
    private DecalBatch decalBatch;
    private Image arrowCraft;
    private Label labelDesc;
    private Label labelValue;
    private boolean translateLeft = false,drawone = true;
    private int centerisequal = 0;
    private boolean touch = false;
    private InformationProfile informationProfile;
    private Table tab;
    ArrayList<Vector3> arrayPositionFront = new ArrayList<Vector3>(),arrayPositionBack = new ArrayList<Vector3>();
    private boolean translateRight = false;
    private Array<String> nameRes = new Array<String>();
    private Group gr;
    private ProgressBarEnergy progressbar;
    private Label labelBusy;
    private Array<Decal> arrayDecal;
    private float translateX = 0 ,translateY = 0,translateZ = 0;
    private QueueDisplay queueDisplay;
    private QueueBuildCraft queue;

    private int countcell,countBricks;
    private Dialog dialog;

    public ScreenCrafting(GameManager game) {
        this.gameManager = game;
        informationProfile = InformationProfile.getInstance();
        managerAssets = ManagerAssets.getInstance();
        queue = QueueBuildCraft.getInstance();
        intances = new Array<ModelInstance>();
        camera = new PerspectiveCamera(45,WIDTH,HEIGHT);
        camera.position.set(0,0,1020);
        camera.lookAt(0, 0, 0);
        camera.near = 1;
        camera.far = 1500;
        camera.update();
        nameRes.add("SCRAP");
        nameRes.add("BRICK");
        nameRes.add("CELL");
        arrayDecal = new Array<Decal>();
    }

    private void drawBackground(){
        imageBackground = Decal.newDecal(new TextureRegion(managerAssets.getAssetsCrafting().getTexture(NameFiles.backgroundCrafting)));
        imageBackground.setDimensions(1200,2000);
        imageBackground.setPosition(0,0,-30);
        queueDisplay = QueueDisplay.getInstance();
    }

    private void drawselector() {
        Texture texture = managerAssets.getAssetsCrafting().getTexture(NameFiles.arrowcrafting);
        Vector2 crop = Scaling.fit.apply(texture.getWidth(),texture.getHeight(),WIDTH,HEIGHT);
        arrowCraft = new Image(new TextureRegion(texture));
        arrowCraft .setSize(crop.x*0.15f,crop.y*0.15f);
        arrowCraft .setPosition(WIDTH / 2 - arrowCraft .getWidth() / 2,HEIGHT*0.4f);
        labelBusy = new Label("",new Label.LabelStyle(Font.getFont((int)(HEIGHT*0.03f)),Color.WHITE));
        labelBusy.setPosition(arrowCraft.getRight(),arrowCraft.getY());
        labelBusy.setVisible(false);
        Table table = new Table();
        gr = new Group();
        gr.setSize(WIDTH,HEIGHT * 0.4f);
        gr.addActor(addToScroll(WIDTH / 2,HEIGHT * 0.3f,MaterialsCraft.CELLS));
        gr.addActor(addToScroll(WIDTH / 2,HEIGHT * 0.2f,MaterialsCraft.BRICKS));
        gr.addActor(addToScroll(WIDTH / 2,HEIGHT * 0.1f,MaterialsCraft.TURRETS));
        gr.addActor(addToScroll(WIDTH / 2,HEIGHT * 0.0f,MaterialsCraft.SHIELDS));
        table.add(gr);
        ScrollPane scroll = new ScrollPane(table);
        scroll.setName("scroll");
        scroll.layout();
        scroll.setScrollingDisabled(true,false);
        tab = new Table();
        tab.setFillParent(false);
        tab.add(scroll).fill().expand();
        tab.setBounds(0,HEIGHT*0.18f,WIDTH,HEIGHT/4.5f);
        //button craft
        String bup = "button-up";String bdown = "button-down";String bcheck = "button-checked";
        Skin skin = new Skin();
        TextureAtlas txta = new TextureAtlas();
        texture = managerAssets.getAssetsCrafting().getTexture(NameFiles.buttonCraft);
        txta.addRegion(bup, new TextureRegion(texture));
        txta.addRegion(bcheck, new TextureRegion(texture));
        txta.addRegion(bdown, new TextureRegion(texture));
        skin.addRegions(txta);
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = skin.getDrawable(bup);
        style.down = skin.getDrawable(bdown);
        style.checked = skin.getDrawable(bcheck);
        buttoncraft = new ImageButton(style);
        crop = Scaling.fit.apply(texture.getWidth(),texture.getHeight(),WIDTH,HEIGHT);
        buttoncraft.setSize(crop.x*0.5f, crop.y* 0.5f);
        buttoncraft.setPosition(WIDTH / 2 - buttoncraft.getWidth() / 2, HEIGHT * 0.08f);
        buttoncraft.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                makeRequest();
            }
        });
        skin = new Skin();
        txta = new TextureAtlas();
        skin.dispose();
        txta.dispose();
    }

    private void makeRequest() {
        countBricks = Integer.parseInt(((Label)(((Group)(gr.findActor(MaterialsCraft.BRICKS.name()))).findActor(MaterialsCraft.BRICKS.name()))).getText().toString());
        countcell = Integer.parseInt(((Label)(((Group)(gr.findActor("CELLS"))).findActor("CELLS"))).getText().toString());
        if (countBricks > 0 || countcell > 0) {
            if(!queue.deployOn) {
                setisOn();
                createAddQueue();
            }else{
                queueDisplay.createexitdialog(this);
            }
        }
        ((Label) (((Group) (gr.findActor("BRICKS"))).findActor("BRICKS"))).setText("0");
        ((Label) (((Group) (gr.findActor("CELLS"))).findActor("CELLS"))).setText("0");
        updateDisplay();
        progressbar.update();
        queue.showqueue();
    }

    @Override
    public void createAddQueue(){
        int valenergy = informationProfile.getValueenergyuse() + informationProfile.getDateBrick().getRateEnergy() * countBricks + informationProfile.getDateCell().getCellRateEnergy() * countcell;
        if (informationProfile.getDateUserGame().getEnergy() >= valenergy) {
            informationProfile.setValueenergyuse(valenergy);
            for (int i = 0; i < countBricks; i++) {
                if (!queue.actionOn) {
                    queue.actionOn = true;
                    queue.addBrick();
                    queue.startThread();
                } else
                    queue.addBrick();
            }
            for (int i = 0; i < countcell; i++) {
                if (!queue.actionOn) {
                    queue.actionOn = true;
                    queue.addCell();
                    queue.startThread();
                } else
                    queue.addCell();
            }
        } else {
            resetResursed();
            createDialog("You don't have enough energy!");
        }
    }

    @Override
    public void resetResursed(){
        informationProfile.setValuescrapuse(informationProfile.getValuescrapuse() - (informationProfile.getDateBrick().getRateScrap() * countBricks + informationProfile.getDateCell().getCellRateScrap() * countcell));
    }

    @Override
    public void setisOn(){
        queue.craftOn = true;
        queue.deployOn = false;
    }

    @Override
    public void setforcestop(){
        queue.forcestopdeploy = true;
        queue.forcestopcrafting = false;
    }

    public void createDialog(String mesaj) {
        Pixmap p = new Pixmap(WIDTH,HEIGHT, Pixmap.Format.RGBA8888);
        p.setColor(1.0f, 1.0f, 1.0f, 0.0f);
        p.fill();
        Skin s = new Skin();
        s.add("black-background", new Texture(p));
        s.add("default", new Label.LabelStyle(new BitmapFont(false), Color.WHITE));
        s.add("default", new Window.WindowStyle(new BitmapFont(false), Color.WHITE, s.newDrawable("black-background", Color.DARK_GRAY)));
        dialog = new Dialog("",s);
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
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                dialog.hide();
            }
        },1500);
    }

    private Group addToScroll(float x, float y, final MaterialsCraft mat){
        Group group = new Group();
        group.setName(mat.name());
        final Label labelvalue = new Label("0",new Label.LabelStyle(Font.getFont((int)(HEIGHT*0.025f)),Color.ORANGE));
        Texture texture = managerAssets.getAssetsCrafting().getTexture(NameFiles.backgroundbuttoncreft);
        Vector2 crop = Scaling.fit.apply(texture.getWidth(),texture.getHeight(),WIDTH,HEIGHT);
        Image back1 = new Image(new TextureRegion(texture));
        back1.setSize(crop.x*0.75f,crop.y*0.75f);
        back1.setPosition(x-back1.getWidth()/2,y);//WIDTH / 2 - back1.getWidth() / 2,HEIGHT*0.2f);
        group.addActor(back1);
        texture = managerAssets.getAssetsButton().get(NameFiles.buttonMinusCraft);
        Image btnminusBrick = new Image(new TextureRegion(texture));
        btnminusBrick.setSize(back1.getHeight()*0.8f,back1.getHeight()*0.8f);
        btnminusBrick.setPosition(back1.getX()+btnminusBrick.getWidth()*0.2f,back1.getY()+back1.getHeight()/2-btnminusBrick.getHeight()/2);
        btnminusBrick.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(Integer.parseInt(labelvalue.getText().toString())>0){
                    labelvalue.setText(String.valueOf(Integer.parseInt(labelvalue.getText().toString())-1));
                    informationProfile.setValuescrapuse(informationProfile.getValuescrapuse() - informationProfile.getUsageScrap(mat));
                }
            }
        });
        group.addActor(btnminusBrick);
        texture = managerAssets.getAssetsButton().get(NameFiles.buttonPlusCraft);
        Image btnplusBrick = new Image(new TextureRegion(texture));
        btnplusBrick.setSize(back1.getHeight()*0.8f,back1.getHeight()*0.8f);
        btnplusBrick.setPosition(back1.getRight()-btnplusBrick.getWidth()*1.2f,back1.getY()+back1.getHeight()/2-btnplusBrick.getHeight()/2);
        btnplusBrick.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(informationProfile.getDateUserGame().getScrap() >= (informationProfile.getUsageScrap(mat) * (Integer.parseInt(labelvalue.getText().toString())+1)) &&  informationProfile.getUsageScrap(mat) != -1 && informationProfile.ckeckscrap(informationProfile.getUsageScrap(mat))){
                    labelvalue.setText(String.valueOf(Integer.parseInt(labelvalue.getText().toString()) + 1));
                    informationProfile.setValuescrapuse(informationProfile.getValuescrapuse() + informationProfile.getUsageScrap(mat));
                }else
                    createDialog("You don't have enough Scrap!");
            }
        });
        group.addActor(btnplusBrick);
        Label labeldesc = new Label(mat.name(),new Label.LabelStyle(Font.getFont((int)(HEIGHT*0.035f)),Color.WHITE));
        labeldesc.setPosition(back1.getX()+back1.getWidth()/2-labeldesc.getWidth()/2,back1.getTop()-labeldesc.getHeight()*1.2f);
        group.addActor(labeldesc);
        labelvalue.setName(mat.name());
        labelvalue.setPosition(back1.getX()+back1.getWidth()/2-labelvalue.getWidth()/2,back1.getY()+labelvalue.getHeight()*0.2f);
        labelvalue.setName(mat.name());
        group.addActor(labelvalue);
        texture = new Texture(0,0, Pixmap.Format.RGBA8888);
        texture.dispose();
        return group;
    }

    private void createexitdialog() {
        Pixmap p = new Pixmap(WIDTH,HEIGHT, Pixmap.Format.RGBA8888);
        p.setColor(1.0f, 1.0f, 1.0f, 0.0f);
        p.fill();
        Skin s = new Skin();
        s.add("black-background", new Texture(p));
        s.add("default", new Label.LabelStyle(new BitmapFont(false), Color.WHITE));
        s.add("default", new Window.WindowStyle(new BitmapFont(false), Color.WHITE, s.newDrawable("black-background", Color.DARK_GRAY)));
        Dialog dia = new Dialog("", s);
        s =new Skin();
        s.dispose();
        Label label = new Label("Just one action.",new Label.LabelStyle(Font.getFont((int) (HEIGHT * 0.03)),Color.ORANGE));
        label.setPosition(WIDTH/2-label.getWidth()/2,HEIGHT*0.125f);
        p = new Pixmap(WIDTH,HEIGHT, Pixmap.Format.RGBA8888);
        p.setColor(0.0f, 0.0f, 1.0f, 1.0f);
        p.fill();
        Image back = new Image(new TextureRegion(new Texture(p)));
        back.setSize(label.getWidth(),label.getHeight());
        back.setPosition(label.getX(),label.getY());
        dia.addActor(back);
        dia.addActor(label);
        dia.hide();
        p = new Pixmap(0,0, Pixmap.Format.RGBA8888);
        p.dispose();
    }

    public void addModele3D(){
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
        environment.set(new ColorAttribute(ColorAttribute.Specular, 1f, 1f, 1f, 1f));
        environment.set(new ColorAttribute(ColorAttribute.Reflection, 1f, 1f, 1f, 1f));
        environment.set(new ColorAttribute(ColorAttribute.Diffuse, 1f, 1f, 1f, 1f));
        environment.add(new DirectionalLight().set(new Color().set(1f,1f,0,1), 0f, -3000f, -1000f));
        Model model = managerAssets.getAssetsCrafting().getModel(NameFiles.scrapModel3D);
        model.materials.get(2).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE, 0.45f));
        final ModelInstance intance = new ModelInstance(model);
        arrayPositionFront.add(new Vector3(0,90f,404f));
        arrayPositionBack.add(new Vector3(136, 128, 68));
        arrayPositionFront.add(new Vector3(0,130,404f));
        arrayPositionBack.add(new Vector3(136, 150f, 68f));
        arrayPositionFront.add(new Vector3(0,40,404));
        arrayPositionBack.add(new Vector3(136,70,68));
        intance.transform.translate(arrayPositionFront.get(0));
        intance.transform.scale(0.35f,0.35f,0.35f);
        intance.transform.rotate(1,0,0,30);
        intances.add(intance);
        controllers.add(new AnimationController(intance));
        controllers.get(0).setAnimation(intance.animations.get(11).id, new AnimationController.AnimationListener() {
            @Override
            public void onEnd(AnimationController.AnimationDesc animation) {
                controllers.get(0).queue(intance.animations.get(11).id, -1, 1f, null, 0);
            }

            @Override
            public void onLoop(AnimationController.AnimationDesc animation) {

            }
        });
        model = managerAssets.getAssetsCrafting().getModel(NameFiles.brickModel3D);
        ModelInstance ins = new ModelInstance(model);
        ins.transform.translate(arrayPositionBack.get(1));
        ins.transform.rotate(1,0,0,30);
        ins.transform.scale(7f,7f,7f);
        intances.add(ins);
        model = managerAssets.getAssetsCrafting().getModel(NameFiles.cellModel3D);
        model.materials.get(2).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE, 0.45f));
        model.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE, 0.85f));
        final ModelInstance intance1 = new ModelInstance(model);
        controllers.add(new AnimationController(intance1));
        controllers.get(1).setAnimation(intance1.animations.get(13).id, new AnimationController.AnimationListener() {
            @Override
            public void onEnd(AnimationController.AnimationDesc animation) {
                controllers.get(1).queue(intance1.animations.get(13).id, -1, 1f, null, 0);
            }

            @Override
            public void onLoop(AnimationController.AnimationDesc animation) {

            }
        });
        intance1.materials.get(1).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE, 0.45f));
        intance1.materials.get(2).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE, 0.6f));
        intance1.transform.translate(arrayPositionBack.get(2).x,arrayPositionBack.get(2).y,-arrayPositionBack.get(2).z);
        intance1.transform.rotate(1,0,0,30);
        intances.add(intance1);
        model = managerAssets.getAssetsCrafting().getModel(NameFiles.suport3D);
        intances.add(new ModelInstance(model));
        intances.get(intances.size-1).transform.translate(arrayPositionBack.get(1).x,arrayPositionBack.get(1).y - 45 ,arrayPositionBack.get(1).z - 60).rotate(1,0,0,30).scale(1.7f,1.7f,1.7f);
    }

    private void addbackObject(){
        Texture t = managerAssets.getAssetsCrafting().getTexture(NameFiles.backgroundModel3d);
        Decal cellBackground = Decal.newDecal(new TextureRegion(t));
        Vector2 crop = Scaling.fit.apply(WIDTH,HEIGHT,t.getWidth(),t.getHeight());
        cellBackground.setDimensions(crop.x*0.45f,crop.y*0.33f);
        cellBackground.setPosition(0,102,311);
        arrayDecal.add(cellBackground);
        Decal scrapbackground = Decal.newDecal(new TextureRegion(t));
        scrapbackground.setDimensions(crop.x*0.45f,crop.y*0.33f);
        scrapbackground.setPosition(153,145,8.5f);
        arrayDecal.add(scrapbackground);
        Decal brickbackground = Decal.newDecal(new TextureRegion(t));
        brickbackground.setDimensions(crop.x*0.45f,crop.y*0.33f);
        brickbackground.setPosition(153,145,-8.5f);
        arrayDecal.add(brickbackground);
        t= new Texture(0,0, Pixmap.Format.RGBA8888);
        t.dispose();
    }

    private void addDescriptorModel(){
        float ypos = camera.project(arrayDecal.get(centerisequal).getPosition().cpy()).y - (HEIGHT * 0.195f);
        labelDesc = new Label(nameRes.get(0),new Label.LabelStyle(Font.getFont((int)(HEIGHT*0.03f)),Color.WHITE));labelDesc.setAlignment(Align.center);
        labelDesc.setPosition(WIDTH/2-labelDesc.getWidth()/2,ypos+labelDesc.getHeight());//HEIGHT*0.485f);
        labelValue = new Label(String.valueOf(Math.round(InformationProfile.getInstance().getDateUserGame().getScrap())),new Label.LabelStyle(Font.getFont((int)(HEIGHT*0.025f)),Color.ORANGE));
        labelValue.setPosition(WIDTH/2-labelValue.getWidth()/2,ypos);labelValue.setAlignment(Align.center);
        progressbar = ProgressBarEnergy.getInstance();
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
        style.font= Font.getFont((int) (Gdx.graphics.getHeight() * 0.025f));
        ImageTextButton b1 = new ImageTextButton(String.valueOf(Menu.CHARACTER), style);
        b1.setPosition(0, 0);
        b1.setSize(Gdx.graphics.getWidth() * 0.37f, Gdx.graphics.getHeight() * 0.04f);
        group.add(b1);

        b2 = new ImageTextButton(String.valueOf(Menu.CRAFTING),style);
        b2.setPosition(b1.getRight(), 0);
        b2.setSize(Gdx.graphics.getWidth() * 0.37f, Gdx.graphics.getHeight() * 0.04f);//b2.setChecked(true);
        group.add(b2);
        ImageTextButton b3 = new ImageTextButton(String.valueOf(Menu.TACMAP),style);
        b3.setPosition(b2.getRight(), 0);
        b3.setSize(Gdx.graphics.getWidth() * 0.37f, Gdx.graphics.getHeight() * 0.04f);
        group.add(b3);
        ImageTextButton b4 = new ImageTextButton(String.valueOf(Menu.STORE),style);
        b4.setPosition(b3.getRight(), 0);
        b4.setSize(Gdx.graphics.getWidth() * 0.37f, Gdx.graphics.getHeight() * 0.04f);
        group.add(b4);
        ImageTextButton b5 = new ImageTextButton(String.valueOf(Menu.SETTING),style);
        b5.setPosition(b4.getRight(), 0);
        b5.setSize(Gdx.graphics.getWidth() * 0.37f, Gdx.graphics.getHeight() * 0.04f);
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
                    case CHARACTER: {
                        gameManager.setScreen(gameManager.screenProfile);
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
        ta.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() * 0.055f);
        //button back

        skin = new Skin();
        txta = new TextureAtlas();
        Texture txt = managerAssets.getAssetsButton().get(NameFiles.buttonBack1);
        txta.addRegion(bup, new TextureRegion(txt));
        txta.addRegion(bdown, new TextureRegion(txt));
        txta.addRegion(bcheck, new TextureRegion(txt));
        skin.addRegions(txta);
        ImageButton.ImageButtonStyle sty = new ImageButton.ImageButtonStyle();
        sty.up = skin.getDrawable(bup);
        sty.down = skin.getDrawable(bdown);
        sty.checked = skin.getDrawable(bcheck);
        Vector2 crop = Scaling.fit.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        backButton = new ImageButton(sty);
        backButton.setSize(crop.x *0.13f,crop.y * 0.13f);
        backButton.setPosition(backButton.getWidth() * 0.25f, HEIGHT - (backButton.getHeight() * 1.25f));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameManager.setScreen(gameManager.mapsScreen);
            }
        });
        skin = new Skin();
        txta = new TextureAtlas();
        skin.dispose();
        txta.dispose();
        txt = new Texture(0,0, Pixmap.Format.RGBA8888);
        txt.dispose();
        Label labelCrafting = new Label("CRAFTING", new Label.LabelStyle(Font.getFont((int) (HEIGHT * 0.045)), Color.WHITE));
        labelCrafting.setPosition(backButton.getRight()*1.3f,backButton.getY()+backButton.getHeight()/2- labelCrafting.getHeight()/2);
    }

    public void updateDisplay(){
        if(!drawone) {
            labelDesc.setText(nameRes.get(centerisequal));
            labelValue.setText(String.valueOf(Math.round(InformationProfile.getInstance().getResursed(centerisequal))));
        }
    }

    @Override
    public void show() {
        if (drawone) {
            initializeMenuTab();
            drawselector();
            addModele3D();
            drawBackground();
            addbackObject();
            createexitdialog();
            addDescriptorModel();
            drawone = false;
        }
        stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()))
        {
            @Override
            public boolean keyDown(int keyCode) {
                if(keyCode == Input.Keys.BACK){
                    gameManager.setScreen(gameManager.mapsScreen);
                }
                return super.keyDown(keyCode);
            }
        };
        stage.addActor(backButton);
        stage.addActor(labelBusy);
        stage.addActor(arrowCraft);
        stage.addActor(buttoncraft);
        stage.addActor(labelDesc);
        stage.addActor(labelValue);
        stage.addActor(tab);
        stage.addActor(ta);
//        screenChat.addToStage(stage);
        queueDisplay.AddtoStage(stage);
        updateDisplay();
        progressbar.addGrouptoStage(stage);
//        progressbar.update();
        b2.setChecked(true);
        backButton.setChecked(false);
        sp.setScrollPercentX(0);
        sp.updateVisualScroll();
        decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
        modelBatch = new ModelBatch();
        GestureDetector gd = new GestureDetector(this);
        InputMultiplexer input = new InputMultiplexer();
        input.addProcessor(gd);
        input.addProcessor(stage);
        Gdx.input.setInputProcessor(input);
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        decalBatch.add(imageBackground);
        for(int i=0;i<arrayDecal.size;i++){
            decalBatch.add(arrayDecal.get(i));
        }
        decalBatch.flush();
//        intances.get(0).transform.rotate(1,0,0,1);
        intances.get(1).transform.rotate(0,1,0,-1);
        modelBatch.begin(camera);
        modelBatch.render(intances,environment);
        modelBatch.end();

        stage.act(delta);
        stage.draw();
        for(int i=0;i<controllers.size;i++){
            controllers.get(i).update(Gdx.graphics.getDeltaTime());
        }
        if(translateLeft){
            MovetranslateLeft();
        }
        if(translateRight){
            MovetranslateRight();
        }
    }

    private void MovetranslateRight() {
        if (arrayDecal.get(centerisequal-1).getX() < 0) {
            if(centerisequal == 1) {
                arrayDecal.get(0).translate(translateX, translateY, translateZ);
                arrayDecal.get(1).translate(translateX, -translateY, -translateZ);
                intances.get(0).transform.trn(-(arrayPositionFront.get(0).x - arrayPositionBack.get(0).x) / 60, -(arrayPositionBack.get(0).y - arrayPositionFront.get(0).y) / 60, -(arrayPositionBack.get(0).z - arrayPositionFront.get(0).z) / 60);
                intances.get(1).transform.trn(-(arrayPositionFront.get(1).x - arrayPositionBack.get(1).x) / 60, -(arrayPositionFront.get(1).y - arrayPositionBack.get(1).y) / 60, -(arrayPositionFront.get(1).z - arrayPositionBack.get(1).z) / 60);
                intances.get(3).transform.trn(-(arrayPositionFront.get(1).x - arrayPositionBack.get(1).x) / 60, -(arrayPositionFront.get(1).y - arrayPositionBack.get(1).y) / 60, -(arrayPositionFront.get(1).z - arrayPositionBack.get(1).z) / 60);
                intances.get(2).transform.trn(0,0,(-2*arrayPositionBack.get(0).z)/60);
            }else if (centerisequal == 2){
                arrayDecal.get(1).translate(translateX, translateY, translateZ);
                arrayDecal.get(2).translate(translateX, -translateY, -translateZ);
                intances.get(0).transform.trn(0,0,(2*arrayPositionBack.get(0).z)/60);
                intances.get(1).transform.trn(-(arrayPositionFront.get(1).x - arrayPositionBack.get(1).x) / 60, (arrayPositionFront.get(1).y - arrayPositionBack.get(1).y) / 60, (arrayPositionFront.get(1).z - arrayPositionBack.get(1).z) / 60);
                intances.get(3).transform.trn(-(arrayPositionFront.get(1).x - arrayPositionBack.get(1).x) / 60, (arrayPositionFront.get(1).y - arrayPositionBack.get(1).y) / 60, (arrayPositionFront.get(1).z - arrayPositionBack.get(1).z) / 60);
                intances.get(2).transform.trn(-(arrayPositionFront.get(2).x - arrayPositionBack.get(2).x) / 60, -(arrayPositionFront.get(2).y - arrayPositionBack.get(2).y) / 60, -(arrayPositionFront.get(2).z - arrayPositionBack.get(2).z) / 60);
            }
        } else {
            if(centerisequal == 1) {
                arrayDecal.get(0).setPosition(0, 102, 311);
                arrayDecal.get(1).setPosition(153f, 145f, 8.5f);
                arrayDecal.get(2).setPosition(153f, 145f, -8.5f);
                intances.get(0).transform.setToTranslation(arrayPositionFront.get(0));
                intances.get(1).transform.setToTranslation(arrayPositionBack.get(1));
                intances.get(3).transform.setToTranslation(arrayPositionBack.get(1).x,arrayPositionBack.get(1).y - 45 ,arrayPositionBack.get(1).z - 60 );
                intances.get(2).transform.setToTranslation(arrayPositionBack.get(2).x,arrayPositionBack.get(2).y,-arrayPositionBack.get(2).z);
            }else if (centerisequal == 2){
                arrayDecal.get(0).setPosition(-153,145,8.5f);
                arrayDecal.get(1).setPosition(0,102,311);
                arrayDecal.get(2).setPosition(153,145,8.5f);
                intances.get(0).transform.setToTranslation(-arrayPositionBack.get(0).x,arrayPositionBack.get(0).y,arrayPositionBack.get(0).z);
                intances.get(1).transform.setToTranslation(arrayPositionFront.get(1));
                intances.get(3).transform.setToTranslation(arrayPositionFront.get(1).x,arrayPositionFront.get(1).y-45,arrayPositionFront.get(1).z - 60);
                intances.get(2).transform.setToTranslation(arrayPositionBack.get(2));
            }
            intances.get(0).transform.scale(0.35f,0.35f,0.35f);
            intances.get(0).transform.rotate(1,0,0,30);
            intances.get(1).transform.scale(7f,7f,7f);
            intances.get(1).transform.rotate(1,0,0,30);
            intances.get(2).transform.rotate(1,0,0,30);
            intances.get(3).transform.rotate(1,0,0,30).scale(1.7f,1.7f,1.7f);
            centerisequal--;
            translateRight = false;
            touch = false;
            labelDesc.setText(nameRes.get(centerisequal));
            labelDesc.setVisible(true);
            labelValue.setText(String.valueOf(Math.round(InformationProfile.getInstance().getResursed(centerisequal))));
            labelValue.setVisible(true);
        }
    }

    private void MovetranslateLeft() {
        if (arrayDecal.get(centerisequal+1).getX() > 0) {
            if(centerisequal == 0) {
                arrayDecal.get(0).translate(translateX, -translateY, -translateZ);
                arrayDecal.get(1).translate(translateX, translateY, translateZ);
                intances.get(0).transform.trn((arrayPositionFront.get(0).x - arrayPositionBack.get(0).x) / 60, (arrayPositionBack.get(0).y - arrayPositionFront.get(0).y) / 60, (arrayPositionBack.get(0).z - arrayPositionFront.get(0).z) / 60);
                intances.get(1).transform.trn((arrayPositionFront.get(1).x - arrayPositionBack.get(1).x) / 60, (arrayPositionFront.get(1).y - arrayPositionBack.get(1).y) / 60, (arrayPositionFront.get(1).z - arrayPositionBack.get(1).z) / 60);
                intances.get(3).transform.trn((arrayPositionFront.get(1).x - arrayPositionBack.get(1).x) / 60, (arrayPositionFront.get(1).y - arrayPositionBack.get(1).y) / 60, (arrayPositionFront.get(1).z - arrayPositionBack.get(1).z) / 60);
                intances.get(2).transform.trn(0,0,(2*arrayPositionBack.get(2).z) / 60);
            }else if(centerisequal == 1){
                arrayDecal.get(1).translate(translateX, -translateY, -translateZ);
                arrayDecal.get(2).translate(translateX, translateY, translateZ);
                intances.get(0).transform.trn(0f, 0, (-2*arrayPositionBack.get(0).z)/60);
                intances.get(1).transform.trn((arrayPositionFront.get(1).x - arrayPositionBack.get(1).x) / 60, -(arrayPositionFront.get(1).y - arrayPositionBack.get(1).y) / 60, -(arrayPositionFront.get(1).z - arrayPositionBack.get(1).z)/60);
                intances.get(3).transform.trn((arrayPositionFront.get(1).x - arrayPositionBack.get(1).x) / 60, -(arrayPositionFront.get(1).y - arrayPositionBack.get(1).y) / 60, -(arrayPositionFront.get(1).z - arrayPositionBack.get(1).z)/60);
                intances.get(2).transform.trn((arrayPositionFront.get(2).x - arrayPositionBack.get(2).x) / 60, (arrayPositionFront.get(2).y - arrayPositionBack.get(2).y) / 60, (arrayPositionFront.get(2).z - arrayPositionBack.get(2).z)/60);
            }
        } else {
            if(centerisequal == 0) {
                arrayDecal.get(0).setPosition(-153,145,8.5f);
                arrayDecal.get(1).setPosition(0,102,311);
                arrayDecal.get(2).setPosition(153f,145f, 8.5f);
                intances.get(0).transform.setToTranslation(-arrayPositionBack.get(0).x,arrayPositionBack.get(0).y,arrayPositionBack.get(0).z);
                intances.get(1).transform.setToTranslation(arrayPositionFront.get(1));
                intances.get(3).transform.setToTranslation(arrayPositionFront.get(1).x,arrayPositionFront.get(1).y - 45,arrayPositionFront.get(1).z - 60);
                intances.get(2).transform.setToTranslation(arrayPositionBack.get(2));
            }else if (centerisequal == 1){
                arrayDecal.get(0).setPosition(-153,145,-8.5f);
                arrayDecal.get(1).setPosition(-153,145,8.5f);
                arrayDecal.get(2).setPosition(0,102,311);
                intances.get(0).transform.setToTranslation(-arrayPositionBack.get(0).x,arrayPositionBack.get(0).y,-arrayPositionBack.get(0).z);
                intances.get(1).transform.setToTranslation(-arrayPositionBack.get(1).x,arrayPositionBack.get(1).y,arrayPositionBack.get(0).z);
                intances.get(3).transform.setToTranslation(-arrayPositionBack.get(1).x,arrayPositionBack.get(1).y-45,arrayPositionBack.get(0).z - 60);
                intances.get(2).transform.setToTranslation(arrayPositionFront.get(2));
            }
            intances.get(0).transform.scale(0.35f,0.35f,0.35f);
            intances.get(0).transform.rotate(1,0,0,30);
            intances.get(1).transform.scale(7f,7f,7f);
            intances.get(1).transform.rotate(1,0,0,30);
            intances.get(2).transform.rotate(1,0,0,30);
            intances.get(3).transform.rotate(1,0,0,30).scale(1.7f,1.7f,1.7f);
            centerisequal++;
            translateLeft = false;
            touch = false;
            labelDesc.setText(nameRes.get(centerisequal));
            labelDesc.setVisible(true);
            labelValue.setText(String.valueOf(Math.round(InformationProfile.getInstance().getResursed(centerisequal))));
            labelValue.setVisible(true);
        }
    }

    @Override
    public void resize(int width, int height) {
//        stage.setViewport();
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
        decalBatch.dispose();
        modelBatch.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        if(y<HEIGHT/2){
            touch = true;
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
        if(touch && !translateRight && !translateLeft){
            if(velocityX<-1000 && centerisequal+1<arrayDecal.size){
                labelDesc.setVisible(false);labelValue.setVisible(false);
                translateX = (arrayDecal.get(centerisequal).getX() - arrayDecal.get(centerisequal+1).getX())/60;
                translateY = (arrayDecal.get(centerisequal).getY() - arrayDecal.get(centerisequal+1).getY())/60;
                translateZ = (arrayDecal.get(centerisequal).getZ() - arrayDecal.get(centerisequal+1).getZ())/60;
                translateLeft = true;
            }else if(velocityX >1000 && centerisequal-1>=0){
                labelDesc.setVisible(false);labelValue.setVisible(false);
                translateX = (arrayDecal.get(centerisequal).getX() - arrayDecal.get(centerisequal-1).getX())/60;
                translateY = (arrayDecal.get(centerisequal).getY() - arrayDecal.get(centerisequal-1).getY())/60;
                translateZ = (arrayDecal.get(centerisequal).getZ() - arrayDecal.get(centerisequal-1).getZ())/60;
                translateRight = true;
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
}
