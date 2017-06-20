package com.enklave.game.Screens;

import com.enklave.game.FontLabel.Font;
import com.enklave.game.GameManager;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by adrian on 10.03.2016.
 */
public class ScreenDescribeEnklave implements Screen {
    private GameManager gameManager;
    boolean drawone = true;
    String bup = "button-up",bdown = "button-down", bcheck = "button-checked";
    private ImageButton backButton;
    private Group groupActiveRooms,groupActiveExtension, groupAddExtensions,groupBuildRoom,groupDescribeEnklave;
    private Image imagebg,imageleftTop;
    private Label labelName,labelLevel;
    private Stage stage;
    private ManagerAssets manager;
    private BitmapFont bt = Font.getFont((int)(Gdx.graphics.getHeight()*0.025));

    public ScreenDescribeEnklave(GameManager gameManager) {
        this.gameManager = gameManager;
        manager = ManagerAssets.getInstance();
    }

    private void addActiveRooms() {
        Skin skin = new Skin();
        TextureAtlas txta = new TextureAtlas();
        Texture txt = manager.getAssetsButton().get(NameFiles.buttonBack1);
        txta.addRegion(bup, new TextureRegion(txt));//, 0, txt.getHeight() / 2, txt.getWidth(), txt.getHeight() / 2));
        txta.addRegion(bdown, new TextureRegion(txt));//, 0, 0, txt.getWidth(), txt.getHeight() / 2));
        txta.addRegion(bcheck, new TextureRegion(txt));//, 0, 0, txt.getWidth(), txt.getHeight() / 2));
        skin.addRegions(txta);
        ImageButton.ImageButtonStyle sty = new ImageButton.ImageButtonStyle();
        sty.up = skin.getDrawable(bup);
        sty.down = skin.getDrawable(bdown);
        sty.checked = skin.getDrawable(bcheck);
        backButton = new ImageButton(sty);
        backButton.setSize(Gdx.graphics.getWidth() * 0.116f, Gdx.graphics.getHeight() * 0.065f);
        backButton.setPosition(0, Gdx.graphics.getHeight() - (backButton.getHeight() * 1.5f));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameManager.setScreen(gameManager.screenEnklave);
            }
        });
        skin = new Skin();
        skin.dispose();
        txta = new TextureAtlas();
        txta.dispose();
        txt = new Texture(0,0, Pixmap.Format.RGBA8888);
        txt.dispose();
        Image cornerBG = new Image(new TextureRegion(manager.getAssetsDescribeEnklave().getTexture(NameFiles.cornerBG)));
        cornerBG.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() * 0.15f);
        cornerBG.setPosition(0, Gdx.graphics.getHeight() / 4f);
        Label labelActiveRoom = new Label("Active Rooms", new Label.LabelStyle(Font.getFont((int) (Gdx.graphics.getHeight() * 0.04)), Color.WHITE));
        labelActiveRoom.setPosition(Gdx.graphics.getWidth() * 0.04f, Gdx.graphics.getHeight() / 3.5f);
        Image cornerExtension = new Image(new TextureRegion(manager.getAssetsDescribeEnklave().getTexture(NameFiles.cornerBG)));
        cornerExtension.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() * 0.15f);
        cornerExtension.setPosition(0, cornerExtension.getHeight() * 0.6f);
        Label labelActiveExtensions = new Label("Active Extensions", new Label.LabelStyle(Font.getFont((int)(Gdx.graphics.getHeight()*0.04)), Color.WHITE));
        labelActiveExtensions.setPosition(Gdx.graphics.getWidth() * 0.04f, cornerExtension.getHeight() * 0.6f + Gdx.graphics.getHeight() * 0.03f);
        final ImageButton buttonexRoom = addButtonExtend();
        buttonexRoom.setSize(Gdx.graphics.getWidth() * 0.162f, Gdx.graphics.getHeight() * 0.091f);
        buttonexRoom.setPosition(Gdx.graphics.getWidth() - buttonexRoom.getWidth(), cornerBG.getY() * 1.1f);
        final ImageButton buttonExtension = addButtonExtend();
        buttonExtension.setSize(Gdx.graphics.getWidth() * 0.162f, Gdx.graphics.getHeight() * 0.091f);
        buttonExtension.setPosition(Gdx.graphics.getWidth() - buttonExtension.getWidth(), cornerExtension.getY() * 1.13f);
        buttonexRoom.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttonexRoom.isChecked()) {
                    buttonExtension.setChecked(false);
                    moveupExtensions(-0.1f);
                    moveupDescribe(0.15f);
                    moveupRooms(0.185f);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            groupBuildRoom.setVisible(true);
                        }
                    },500);
                } else {
                    moveDownExtensions();
                    moveDownDescribe();
                    moveDownRooms();
                }
            }
        });
        buttonexRoom.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!buttonexRoom.isChecked()){
                    groupBuildRoom.setVisible(false);
                }
            }
        });
        buttonExtension.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(buttonExtension.isChecked()){
                    buttonexRoom.setChecked(false);
                    moveupRooms(0.3f);
                    moveupDescribe(0.25f);
                    moveupExtensions(0.35f);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            groupAddExtensions.setVisible(true);
                        }
                    }, 500);
                }
                else{
                    moveDownExtensions();
                    moveDownRooms();
                    moveDownDescribe();
                }

            }
        });
        buttonExtension.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!buttonExtension.isChecked()){
                    groupAddExtensions.setVisible(false);
                }
            }
        });
        groupActiveRooms = new Group();
        groupActiveRooms.addActor(cornerBG);
        groupActiveRooms.addActor(labelActiveRoom);
        groupActiveRooms.addActor(buttonexRoom);
        groupActiveExtension = new Group();
        groupActiveExtension.addActor(cornerExtension);
        groupActiveExtension.addActor(labelActiveExtensions);
        groupActiveExtension.addActor(buttonExtension);

    }
    private void moveupDescribe(float x){
        MoveToAction move = new MoveToAction();
        move.setPosition(0, Gdx.graphics.getHeight() * x);
        move.setDuration(0.5f);
        groupDescribeEnklave.addAction(move);
    }
    private void moveDownDescribe(){
        MoveToAction move = new MoveToAction();
        move.setPosition(0,0);
        move.setDuration(0.5f);
        groupDescribeEnklave.addAction(move);
    }
    private void moveupRooms(float x){
        MoveToAction move = new MoveToAction();
        move.setPosition(0,Gdx.graphics.getHeight()*x);
        move.setDuration(0.5f);
        groupActiveRooms.addAction(move);
    }
    private void moveDownRooms(){
        MoveToAction move = new MoveToAction();
        move.setPosition(0,0);
        move.setDuration(0.5f);
        groupActiveRooms.addAction(move);
    }
    private void moveupExtensions(float x){
        MoveToAction move = new MoveToAction();
        move.setPosition(0,Gdx.graphics.getHeight()*x);
        move.setDuration(0.5f);
        groupActiveExtension.addAction(move);
    }
    private void moveDownExtensions(){
        MoveToAction move = new MoveToAction();
        move.setPosition(0,0);
        move.setDuration(0.5f);
        groupActiveExtension.addAction(move);
    }
    private ImageButton addButtonExtend(){
        Skin skin = new Skin();
        TextureAtlas txta = new TextureAtlas();
        Texture txt =manager.getAssetsDescribeEnklave().getTexture(NameFiles.buttonExtendCollapse);
        txta.addRegion(bup, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 3));
        txta.addRegion(bdown, new TextureRegion(txt, 0, txt.getHeight() / 3, txt.getWidth(), txt.getHeight() / 3));
        txta.addRegion(bcheck, new TextureRegion(txt, 0, 2 * txt.getHeight() / 3, txt.getWidth(), txt.getHeight() / 3));
        skin.addRegions(txta);
        ImageButton.ImageButtonStyle sty = new ImageButton.ImageButtonStyle();
        sty.up = skin.getDrawable(bup);
        sty.down = skin.getDrawable(bdown);
        sty.checked = skin.getDrawable(bcheck);
        skin = new Skin();
        skin.dispose();
        txta = new TextureAtlas();
        txta.dispose();
        txt = new Texture(0,0, Pixmap.Format.RGBA8888);
        txt.dispose();
        return new ImageButton(sty);
    }
    private void createBuildRoom(){
        groupBuildRoom = new Group();
        Skin skin = new Skin();
        TextureAtlas txta = new TextureAtlas();
        Texture txt = manager.getAssetsButton().get(NameFiles.button_describe);
        txta.addRegion(bup, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 2));
        txta.addRegion(bdown, new TextureRegion(txt, 0, txt.getHeight() / 2, txt.getWidth(), txt.getHeight() / 2));
        txta.addRegion(bcheck, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 2));
        skin.addRegions(txta);
        ImageTextButton.ImageTextButtonStyle sty = new ImageTextButton.ImageTextButtonStyle();
        sty.up = skin.getDrawable(bup);
        sty.down = skin.getDrawable(bdown);
        sty.checked = skin.getDrawable(bcheck);
        sty.font = Font.getFont((int)(Gdx.graphics.getHeight()*0.03));
        ImageTextButton buttonbuildroom = new ImageTextButton("Build Room",sty);
        buttonbuildroom.setSize(Gdx.graphics.getWidth() * 0.65f, Gdx.graphics.getHeight() * 0.065f);
        buttonbuildroom.setPosition(Gdx.graphics.getWidth() / 2 - buttonbuildroom.getWidth() / 2, Gdx.graphics.getHeight() * 0.13f);
        buttonbuildroom.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //gameManager.setScreen(gameManager.buildRooms);
            }
        });
        groupBuildRoom.addActor(buttonbuildroom);
        Texture t = manager.getAssetsDescribeEnklave().getTexture(NameFiles.frameEnk);
        Image frame1 = new Image(new TextureRegion(t));
        frame1.setSize(Gdx.graphics.getWidth() * 0.315f, Gdx.graphics.getHeight() * 0.177f);
        frame1.setPosition(Gdx.graphics.getWidth() * 0.009f, Gdx.graphics.getHeight() * 0.25f);
        Image frame2 = new Image(new TextureRegion(t));
        frame2.setSize(Gdx.graphics.getWidth() * 0.315f, Gdx.graphics.getHeight() * 0.177f);
        frame2.setPosition(Gdx.graphics.getWidth() * 0.3433f, Gdx.graphics.getHeight() * 0.25f);
        Image frame3 = new Image(new TextureRegion(t));
        frame3.setSize(Gdx.graphics.getWidth() * 0.315f, Gdx.graphics.getHeight() * 0.177f);
        frame3.setPosition(Gdx.graphics.getWidth() * 0.6846f, Gdx.graphics.getHeight() * 0.25f);
        t = new Texture(0,0, Pixmap.Format.RGBA8888);
        t.dispose();
        groupBuildRoom.addActor(frame1);
        groupBuildRoom.addActor(frame2);
        groupBuildRoom.addActor(frame3);
        Label lBarrack = new Label("Barrack",new Label.LabelStyle(bt,Color.WHITE));
        lBarrack.setPosition(frame1.getX()+frame1.getWidth()/2-lBarrack.getWidth()/2, frame1.getY() - lBarrack.getHeight());
        groupBuildRoom.addActor(lBarrack);
        Label llaboratory = new Label("Laboratory",new Label.LabelStyle(bt,Color.WHITE));
        llaboratory.setPosition(frame2.getX() + frame2.getWidth()/2 - llaboratory.getWidth()/2, frame1.getY() - lBarrack.getHeight());
        groupBuildRoom.addActor(llaboratory);
        Label lcommcenter = new Label("Comm Center",new Label.LabelStyle(bt,Color.WHITE));
        lcommcenter.setPosition(frame3.getX() +frame3.getWidth()/2 - lcommcenter.getWidth()/2, frame1.getY() - lBarrack.getHeight());
        groupBuildRoom.addActor(lcommcenter);
        Image imBarrack = new Image(new TextureRegion(manager.getAssetsDescribeEnklave().getTexture(NameFiles.imageBarrack)));
        imBarrack.setSize(Gdx.graphics.getWidth() * 0.31f, Gdx.graphics.getHeight() * 0.174f);
        imBarrack.setPosition(Gdx.graphics.getWidth() * 0.012f, Gdx.graphics.getHeight() * 0.25f);
        Image imlaboratory = new Image(new TextureRegion(manager.getAssetsDescribeEnklave().getTexture(NameFiles.imageLaboratory)));
        imlaboratory.setSize(Gdx.graphics.getWidth() * 0.31f, Gdx.graphics.getHeight() * 0.174f);
        imlaboratory.setPosition(Gdx.graphics.getWidth() * 0.3463f, Gdx.graphics.getHeight() * 0.25f);
        Image imcommcenter = new Image(new TextureRegion(manager.getAssetsDescribeEnklave().getTexture(NameFiles.imageCommCenter)));
        imcommcenter.setSize(Gdx.graphics.getWidth() * 0.31f, Gdx.graphics.getHeight() * 0.174f);
        imcommcenter.setPosition(Gdx.graphics.getWidth() * 0.6876f, Gdx.graphics.getHeight() * 0.25f);
        groupBuildRoom.addActor(imcommcenter);
        groupBuildRoom.addActor(imBarrack);
        groupBuildRoom.addActor(imlaboratory);
        groupBuildRoom.setVisible(false);
        skin = new Skin();
        skin.dispose();
        txta = new TextureAtlas();
        txta.dispose();
        txt = new Texture(0,0, Pixmap.Format.RGBA8888);
        txt.dispose();
    }
    private void createAddExtensionGroup(){
        groupAddExtensions = new Group();
        Skin skin = new Skin();
        TextureAtlas txta = new TextureAtlas();
        Texture txt = manager.getAssetsButton().get(NameFiles.button_describe);
        txta.addRegion(bup, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 2));
        txta.addRegion(bdown, new TextureRegion(txt, 0, txt.getHeight() / 2, txt.getWidth(), txt.getHeight() / 2));
        txta.addRegion(bcheck, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 2));
        skin.addRegions(txta);
        ImageTextButton.ImageTextButtonStyle sty = new ImageTextButton.ImageTextButtonStyle();
        sty.up = skin.getDrawable(bup);
        sty.down = skin.getDrawable(bdown);
        sty.checked = skin.getDrawable(bcheck);
        sty.font = Font.getFont((int) (Gdx.graphics.getHeight() * 0.03));
        ImageTextButton buttonbuildroom = new ImageTextButton("Add Extension",sty);
        buttonbuildroom.setSize(Gdx.graphics.getWidth() * 0.65f, Gdx.graphics.getHeight() * 0.065f);
        buttonbuildroom.setPosition(Gdx.graphics.getWidth() / 2 - buttonbuildroom.getWidth() / 2, Gdx.graphics.getHeight() * 0.01f);
        buttonbuildroom.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //gameManager.setScreen(gameManager.addExtensions);
            }
        });
        groupAddExtensions.addActor(buttonbuildroom);
        Texture t = manager.getAssetsDescribeEnklave().getTexture(NameFiles.frameEnk);
        Image frame1 = new Image(new TextureRegion(t));
        frame1.setSize(Gdx.graphics.getWidth() * 0.315f, Gdx.graphics.getHeight() * 0.177f);
        frame1.setPosition(Gdx.graphics.getWidth() * 0.009f, Gdx.graphics.getHeight() * 0.28f);
        Image frame2 = new Image(new TextureRegion(t));
        frame2.setSize(Gdx.graphics.getWidth() * 0.315f, Gdx.graphics.getHeight() * 0.177f);
        frame2.setPosition(Gdx.graphics.getWidth() * 0.3433f, Gdx.graphics.getHeight() * 0.28f);
        Image frame3 = new Image(new TextureRegion(t));
        frame3.setSize(Gdx.graphics.getWidth() * 0.315f, Gdx.graphics.getHeight() * 0.177f);
        frame3.setPosition(Gdx.graphics.getWidth() * 0.6846f, Gdx.graphics.getHeight() * 0.28f);
        Image frame4 = new Image(new TextureRegion(t));
        frame4.setSize(Gdx.graphics.getWidth() * 0.315f, Gdx.graphics.getHeight() * 0.177f);
        frame4.setPosition(Gdx.graphics.getWidth() * 0.3433f, Gdx.graphics.getHeight() * 0.08f);
        t = new Texture(0,0, Pixmap.Format.RGBA8888);
        t.dispose();
        groupAddExtensions.addActor(frame1);
        groupAddExtensions.addActor(frame2);
        groupAddExtensions.addActor(frame3);
        groupAddExtensions.addActor(frame4);
        Image imturretauto = new Image(new TextureRegion(manager.getAssetsDescribeEnklave().getTexture(NameFiles.imageExtensionAutoTurret)));
        imturretauto.setSize(Gdx.graphics.getWidth() * 0.31f, Gdx.graphics.getHeight() * 0.174f);
        imturretauto.setPosition(Gdx.graphics.getWidth() * 0.012f, Gdx.graphics.getHeight() * 0.28f);
        Image imremoteturret = new Image(new TextureRegion(manager.getAssetsDescribeEnklave().getTexture(NameFiles.imageExtensionRemoteTurret)));
        imremoteturret.setSize(Gdx.graphics.getWidth() * 0.31f, Gdx.graphics.getHeight() * 0.174f);
        imremoteturret.setPosition(Gdx.graphics.getWidth() * 0.3463f, Gdx.graphics.getHeight() * 0.28f);
        Image imgammaShield = new Image(new TextureRegion(manager.getAssetsDescribeEnklave().getTexture(NameFiles.imageExtensionGammaShield)));
        imgammaShield.setSize(Gdx.graphics.getWidth() * 0.31f, Gdx.graphics.getHeight() * 0.174f);
        imgammaShield.setPosition(Gdx.graphics.getWidth() * 0.6876f, Gdx.graphics.getHeight() * 0.28f);
        Image imregularShield = new Image(new TextureRegion(manager.getAssetsDescribeEnklave().getTexture(NameFiles.imageExtensionRegularShield)));
        imregularShield.setSize(Gdx.graphics.getWidth() * 0.31f, Gdx.graphics.getHeight() * 0.174f);
        imregularShield.setPosition(Gdx.graphics.getWidth() * 0.3463f, Gdx.graphics.getHeight() * 0.08f);
        groupAddExtensions.addActor(imturretauto);
        groupAddExtensions.addActor(imremoteturret);
        groupAddExtensions.addActor(imgammaShield);
        groupAddExtensions.addActor(imregularShield);
        Label lautoturret = new Label("Auto Turret",new Label.LabelStyle(bt,Color.WHITE));
        lautoturret.setPosition(frame1.getX() + frame1.getWidth()/2-lautoturret.getWidth()/2, frame1.getY() - lautoturret.getHeight());
        groupAddExtensions.addActor(lautoturret);
        Label lremoteTurret = new Label("Remote Turret",new Label.LabelStyle(bt,Color.WHITE));
        lremoteTurret.setPosition(frame2.getX() + frame2.getWidth()/2 -lremoteTurret.getWidth()/2, frame1.getY() - lautoturret.getHeight());
        groupAddExtensions.addActor(lremoteTurret);
        Label lgammaShield = new Label("Gamma Shield",new Label.LabelStyle(bt,Color.WHITE));
        lgammaShield.setPosition(frame3.getX() + frame3.getWidth()/2 -lgammaShield.getWidth()/2, frame1.getY() - lautoturret.getHeight());
        groupAddExtensions.addActor(lgammaShield);
        Label lRegularShield = new Label("Regular Shield",new Label.LabelStyle(bt,Color.WHITE));
        lRegularShield.setPosition(frame4.getX() + frame4.getWidth()/2 -lRegularShield.getWidth()/2, frame4.getY());
        groupAddExtensions.addActor(lRegularShield);
        groupAddExtensions.setVisible(false);
        skin = new Skin();
        skin.dispose();
        txta = new TextureAtlas();
        txta.dispose();
        txt = new Texture(0,0, Pixmap.Format.RGBA8888);
        txt.dispose();
    }
    private void addDescribeEnklave() {
        imagebg = new Image(new TextureRegion(new Texture(Gdx.files.internal("Images/arcul-de-triumf.jpg"))));
        imagebg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getWidth() * 0.8f);
        imagebg.setPosition(0, Gdx.graphics.getHeight() * 0.75f - imagebg.getHeight() / 2);
        labelName = new Label("Arcul de Triumf",new Label.LabelStyle(Font.getFont((int)(Gdx.graphics.getHeight()*0.04)), Color.WHITE));
        labelName.setPosition(backButton.getRight(), backButton.getY()+backButton.getHeight()/2-labelName.getHeight()/2);
        labelLevel = new Label("L 1",new Label.LabelStyle(Font.getFont((int)(Gdx.graphics.getHeight()*0.04)),Color.CYAN));
        labelLevel.setPosition(labelName.getRight()*1.05f, labelName.getY());
        imageleftTop = new Image(new TextureRegion(manager.getAssetsDescribeEnklave().getTexture(NameFiles.proportionEnklave)));
        imageleftTop.setSize(Gdx.graphics.getWidth()*0.14f,Gdx.graphics.getWidth()*0.14f);
        imageleftTop.setPosition(Gdx.graphics.getWidth()-imageleftTop.getWidth()*1.1f,Gdx.graphics.getHeight()-imageleftTop.getHeight());
        Pixmap pixBlack = new Pixmap(5,5, Pixmap.Format.RGBA8888);
        pixBlack.setColor(Color.BLACK);
        pixBlack.fill();
        Texture txt = new Texture(pixBlack);
        Image backgroundBlack = new Image(new TextureRegion(txt));
        backgroundBlack.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getWidth() /2);
        backgroundBlack.setPosition(0,Gdx.graphics.getHeight() / 2.3f-Gdx.graphics.getWidth()*0.32f);
        Skin skin = new Skin();
        TextureAtlas txta = new TextureAtlas();
        txt = manager.getAssetsDescribeEnklave().getTexture(NameFiles.buttonFavoriteEnklave);
        txta.addRegion(bup, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 2));
        txta.addRegion(bdown, new TextureRegion(txt, 0, txt.getHeight() / 2, txt.getWidth(), txt.getHeight() / 2));
        txta.addRegion(bcheck, new TextureRegion(txt, 0, txt.getHeight() / 2, txt.getWidth(), txt.getHeight() / 2));
        skin.addRegions(txta);
        ImageButton.ImageButtonStyle sty = new ImageButton.ImageButtonStyle();
        sty.up = skin.getDrawable(bup);
        sty.down = skin.getDrawable(bdown);
        sty.checked = skin.getDrawable(bcheck);
        ImageButton buttonfavorite = new ImageButton(sty);
        buttonfavorite.setSize(Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getWidth() * 0.1f);
        buttonfavorite.setPosition(Gdx.graphics.getWidth() - buttonfavorite.getWidth() * 2.35f, Gdx.graphics.getHeight() / 1.9f);
        Image viewGallery = new Image(new TextureRegion(manager.getAssetsDescribeEnklave().getTexture(NameFiles.buttonViewGallery)));
        viewGallery.setSize(Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getWidth() * 0.1f);
        viewGallery.setPosition(Gdx.graphics.getWidth() - buttonfavorite.getWidth() * 1.2f, Gdx.graphics.getHeight() / 1.9f);
        Image imageprofile = new Image(new TextureRegion(manager.getAssetsDescribeEnklave().getTexture(NameFiles.profileButtonimage)));
        imageprofile.setSize(Gdx.graphics.getWidth() * 0.18f, Gdx.graphics.getWidth() * 0.18f);
        imageprofile.setPosition(Gdx.graphics.getWidth() * 0.075f, Gdx.graphics.getHeight() / 2.3f);
        //submitted
        Label labeSubmited = new Label("Submitted by ", new Label.LabelStyle(Font.getFont((int) (Gdx.graphics.getHeight() * 0.02)), Color.WHITE));
        labeSubmited.setPosition(imageprofile.getRight() * 1.1f, imageprofile.getY() + imageprofile.getHeight() / 2);
        Label labelNameSubmitted = new Label("@flocirescu", new Label.LabelStyle(Font.getFont((int)(Gdx.graphics.getHeight()*0.02)), Color.RED));
        labelNameSubmitted.setPosition(labeSubmited.getRight(), labeSubmited.getY());
        //captured
        Label labeCaptured = new Label("Captured by ", new Label.LabelStyle(Font.getFont((int) (Gdx.graphics.getHeight() * 0.02)), Color.WHITE));
        labeCaptured.setPosition(labeSubmited.getX(), labeSubmited.getY() - labeCaptured.getHeight());
        Label labelNameCaptured = new Label("@X-ulescu", new Label.LabelStyle(Font.getFont((int) (Gdx.graphics.getHeight() * 0.02)), Color.CYAN));
        labelNameCaptured.setPosition(labeCaptured.getRight(), labeCaptured.getY());
        //BRICKS
        Label labelnoBricks = new Label("0 / 9 Bricks (L 1)", new Label.LabelStyle(Font.getFont((int)(Gdx.graphics.getHeight()*0.02)), Color.WHITE));
        labelnoBricks.setPosition(labeSubmited.getX(), labeCaptured.getY() - labelnoBricks.getHeight());
        groupDescribeEnklave = new Group();
        groupDescribeEnklave.addActor(backgroundBlack);
        groupDescribeEnklave.addActor(buttonfavorite);
        groupDescribeEnklave.addActor(viewGallery);
        groupDescribeEnklave.addActor(imageprofile);
        groupDescribeEnklave.addActor(labeSubmited);
        groupDescribeEnklave.addActor(labelNameSubmitted);
        groupDescribeEnklave.addActor(labeCaptured);
        groupDescribeEnklave.addActor(labelNameCaptured);
        groupDescribeEnklave.addActor(labelnoBricks);
        pixBlack = new Pixmap(0,0, Pixmap.Format.RGBA8888);
        pixBlack.dispose();
        skin = new Skin();
        skin.dispose();
        txta = new TextureAtlas();
        txta.dispose();
        txt = new Texture(0,0, Pixmap.Format.RGBA8888);
        txt.dispose();
    }
    private void addtoStage(Stage st){
        st.addActor(imagebg);
        st.addActor(labelLevel);
        st.addActor(labelName);
        st.addActor(imageleftTop);
        st.addActor(groupDescribeEnklave);
        st.addActor(groupActiveRooms);
        st.addActor(groupActiveExtension);
        st.addActor(groupBuildRoom);
        st.addActor(groupAddExtensions);
    }

    @Override
    public void show() {
        if(drawone){
            addActiveRooms();
            createBuildRoom();
            createAddExtensionGroup();
            addDescribeEnklave();
            drawone = false;
        }
        backButton.setChecked(false);
        Gdx.input.setCatchBackKey(true);
        stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()));
        addtoStage(stage);
        stage.addActor(backButton);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            gameManager.setScreen(gameManager.screenEnklave);
        }
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
    }
}
