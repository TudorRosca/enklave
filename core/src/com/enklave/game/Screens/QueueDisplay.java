package com.enklave.game.Screens;

import com.enklave.game.Craft.QueueBuildCraft;
import com.enklave.game.Enklave.DescEnklave.InformationEnklave;
import com.enklave.game.FontLabel.Font;
import com.enklave.game.Interfaces.InterfaceQueueFactory;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.MapsService.Bounds;
import com.enklave.game.MapsService.MyLocation;
import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;


public class QueueDisplay {
    private static QueueDisplay ourInstance = new QueueDisplay();
    private final ManagerAssets managerAssets;
    private int WIDTH = Gdx.graphics.getWidth(),HEIGHT = Gdx.graphics.getHeight();
    private Image image;
    private Group group;
    private boolean visibleGroup = false;
    public Label labelnextaction,labelcurrentaction,labelfinishaction;
    private Stage thisstage;
    private Dialog dia;
    private boolean advertismentOn = false;

    public static QueueDisplay getInstance() {
        return ourInstance;
    }

    private QueueDisplay() {
        QueueBuildCraft.getInstance().setQueueDisplay(this);
        managerAssets = ManagerAssets.getInstance();
        init();
    }

    public void init(){
        Texture txt = managerAssets.getAssetsFadeInActions().getTexture(NameFiles.btnfadeaction);
        Vector2 crop = Scaling.fill.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        group = new Group();
        image = new Image(new TextureRegion(txt));
        image.setSize(crop.x*0.09f,crop.y*0.09f);
        image.setPosition(WIDTH - image.getWidth(),HEIGHT * 0.23f);
        txt = managerAssets.getAssetsFadeInActions().getTexture(NameFiles.queuebackFadein);
        final Image background = new Image(new TextureRegion(txt));
        background.setSize(WIDTH/2,HEIGHT/4);
        group.setSize(image.getWidth()+background.getWidth(),background.getHeight());
        group.setPosition(image.getRight(),image.getTop() - image.getHeight()/2 - background.getHeight()/2);
        image.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!visibleGroup) {
                    MoveToAction move = new MoveToAction();
                    move.setDuration(0.5f);
                    move.setPosition(image.getX() - background.getWidth(), image.getY());
                    image.addAction(move);
                    move = new MoveToAction();
                    move.setDuration(0.5f);
                    move.setPosition(image.getX() - background.getWidth() + image.getWidth(), group.getY());
                    group.addAction(move);
                    visibleGroup = true;
                }else{
                    MoveToAction move = new MoveToAction();
                    move.setDuration(0.5f);
                    move.setPosition(WIDTH - image.getWidth(),HEIGHT * 0.23f);
                    image.addAction(move);
                    move = new MoveToAction();
                    move.setDuration(0.5f);
                    move.setPosition(group.getRight() - image.getWidth(),group.getY());
                    group.addAction(move);
                    visibleGroup = false;
                }
            }
        });
        background.setPosition(0,0);//image.getRight(),);
        background.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MoveToAction move = new MoveToAction();
                move.setDuration(0.5f);
                move.setPosition(WIDTH - image.getWidth(),HEIGHT * 0.23f);
                image.addAction(move);
                move = new MoveToAction();
                move.setDuration(0.5f);
                move.setPosition(group.getRight() - image.getWidth(),group.getY());
                group.addAction(move);
                visibleGroup = false;
            }
        });
        group.addActor(background);
        txt= managerAssets.getAssetsFadeInActions().getTexture(NameFiles.btnStopaction);
        crop = Scaling.fill.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        ImageButton button = new ImageButton(new TextureRegionDrawable(new TextureRegion(txt)));
        button.setSize(crop.x*0.05f,crop.y*0.05f);
        button.setPosition(background.getX()+background.getWidth()/2 - button.getWidth()/2,background.getY()+button.getHeight()*0.2f);
        button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                QueueBuildCraft.getInstance().clearQueue();
            }
        });
        group.addActor(button);
        txt = managerAssets.getAssetsFadeInActions().getTexture(NameFiles.iconnextaction);
        crop = Scaling.fill.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        Image nextaction = new Image(new TextureRegion(txt));
        nextaction.setSize(crop.x*0.05f,crop.y*0.05f);
        nextaction.setPosition(background.getX()+nextaction.getWidth()*0.3f,button.getTop()+nextaction.getHeight()*0.1f);
        group.addActor(nextaction);
        txt = managerAssets.getAssetsFadeInActions().getTexture(NameFiles.iconcurrentaction);
        crop = Scaling.fill.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        Image currentaction = new Image(new TextureRegion(txt));
        currentaction.setSize(crop.x*0.05f,crop.y*0.05f);
        currentaction.setPosition(background.getX()+currentaction.getWidth()*0.3f,nextaction.getTop()+currentaction.getHeight()*0.1f);
        group.addActor(currentaction);
        txt = managerAssets.getAssetsFadeInActions().getTexture(NameFiles.iconfinishaction);
        crop = Scaling.fill.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        Image finishaction = new Image(new TextureRegion(txt));
        finishaction.setSize(crop.x*0.05f,crop.y*0.05f);
        finishaction.setPosition(background.getX()+nextaction.getWidth()*0.3f,currentaction.getTop()+finishaction.getHeight()*0.1f);
        group.addActor(finishaction);
        labelnextaction = new Label("",new Label.LabelStyle(Font.getFont((int)(HEIGHT*0.021f)),Color.WHITE));
        labelnextaction.setAlignment(Align.center);
        labelnextaction.setWidth(background.getRight() - nextaction.getRight());
        labelnextaction.setPosition(nextaction.getRight(),nextaction.getTop() - nextaction.getHeight()/2 - labelnextaction.getHeight()/2);
        group.addActor(labelnextaction);
        labelcurrentaction = new Label("",new Label.LabelStyle(Font.getFont((int)(HEIGHT*0.021f)),Color.WHITE));
        labelcurrentaction.setAlignment(Align.center);
        labelcurrentaction.setWidth(background.getRight() - currentaction.getRight());
        labelcurrentaction.setPosition(currentaction.getRight(),currentaction.getTop() - currentaction.getHeight()/2 - labelcurrentaction.getHeight()/2);
        group.addActor(labelcurrentaction);
        labelfinishaction = new Label("",new Label.LabelStyle(Font.getFont((int)(HEIGHT*0.021f)),Color.WHITE));
        labelfinishaction.setAlignment(Align.center);
        labelfinishaction.setWidth(background.getRight() - finishaction.getRight());
        labelfinishaction.setPosition(finishaction.getRight(),finishaction.getTop()-finishaction.getHeight()/2 - labelfinishaction.getHeight()/2);
        group.addActor(labelfinishaction);

    }

    public void AddtoStage(Stage stage){
        thisstage = stage;
        stage.addActor(image);
        stage.addActor(group);
    }

    public void createexitdialog(InterfaceQueueFactory screenqueue) {
        Pixmap p = new Pixmap(WIDTH,HEIGHT, Pixmap.Format.RGBA8888);
        p.setColor(1.0f, 1.0f, 1.0f, 0.8f);
        p.fill();
        Skin s = new Skin();
        s.add("black-background", new Texture(p));
        s.add("default", new Label.LabelStyle(new BitmapFont(false), Color.WHITE));
        s.add("default", new Window.WindowStyle(new BitmapFont(false), Color.WHITE, s.newDrawable("black-background", Color.DARK_GRAY)));
        dia = new Dialog("",s);
        dia.addActor(addButtonLeft(screenqueue));
        dia.addActor(addButtonRight(screenqueue));
        p = new Pixmap(0,0, Pixmap.Format.RGBA8888);
        p.dispose();
        s =new Skin();
        s.dispose();
        Label label = new Label("For start this action\n is needed to finish or stop\n the other actions?",new Label.LabelStyle(Font.getFont((int) (HEIGHT * 0.03)),Color.WHITE));
        label.setAlignment(Align.center);
        dia.text(label);
        dia.show(thisstage);
    }

    public ImageTextButton addButtonLeft(final InterfaceQueueFactory queueFactory){
        String bup = "button-up",bdown = "button-down", bcheck = "button-checked";
        Skin s=new Skin();
        TextureAtlas txta = new TextureAtlas();
        Texture t = managerAssets.getAssetsButton().get(NameFiles.buttontwoLeft);
        Vector2 crop = Scaling.fit.apply(t.getWidth(), t.getHeight() / 2, WIDTH, HEIGHT);
        txta.addRegion(bup, new TextureRegion(t, 0, 0, t.getWidth(), t.getHeight() / 2));
        txta.addRegion(bdown, new TextureRegion(t, 0, t.getHeight() / 2, t.getWidth(), t.getHeight() / 2));
        txta.addRegion(bcheck, new TextureRegion(t, 0, 0, t.getWidth(), t.getHeight() / 2));
        s.addRegions(txta);
        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle();
        style.up = s.getDrawable(bup);
        style.down = s.getDrawable(bdown);
        style.checked = s.getDrawable(bcheck);
        style.font = Font.getFont((int) (HEIGHT * 0.025));
        s = new Skin();
        s.dispose();
        txta = new TextureAtlas();
        txta.dispose();
        t = new Texture(0,0, Pixmap.Format.RGBA8888);
        t.dispose();
        ImageTextButton imgbtn = new ImageTextButton("NO",style);
        imgbtn.setSize(crop.x*0.2f,crop.y*0.2f);
        imgbtn.setPosition(WIDTH * 0.32f, HEIGHT * 0.4f);
        imgbtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dia.hide();
                queueFactory.resetResursed();
            }
        });
        return imgbtn;
    }

    public ImageTextButton addButtonRight(final InterfaceQueueFactory queueFactory){
        String bup = "button-up",bdown = "button-down", bcheck = "button-checked";
        Skin s=new Skin();
        Texture t = managerAssets.getAssetsButton().get(NameFiles.buttonRight);
        Vector2 crop = Scaling.fit.apply(t.getWidth(), t.getHeight() / 2, WIDTH, HEIGHT);
        TextureAtlas txta = new TextureAtlas();
        txta.addRegion(bup, new TextureRegion(t, 0, 0, t.getWidth(), t.getHeight() / 2));
        txta.addRegion(bdown, new TextureRegion(t, 0, t.getHeight() / 2, t.getWidth(), t.getHeight() / 2));
        txta.addRegion(bcheck, new TextureRegion(t, 0, 0, t.getWidth(), t.getHeight() / 2));
        s.addRegions(txta);
        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle();
        style.up = s.getDrawable(bup);
        style.down = s.getDrawable(bdown);
        style.checked = s.getDrawable(bcheck);
        style.font = Font.getFont((int) (HEIGHT * 0.025));
        s = new Skin();
        s.dispose();
        txta = new TextureAtlas();
        txta.dispose();
        t = new Texture(0,0, Pixmap.Format.RGBA8888);
        t.dispose();
        ImageTextButton imgbtn = new ImageTextButton("YES",style);
        imgbtn.setSize(crop.x * 0.2f, crop.y * 0.2f);
        imgbtn.setPosition(WIDTH / 2, HEIGHT * 0.40f);
        imgbtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dia.hide();
                QueueBuildCraft.getInstance().clearQueue();
                queueFactory.setisOn();
                queueFactory.setforcestop();
                queueFactory.createAddQueue();
            }
        });
        return imgbtn;
    }

    public boolean checkProximity(){
        Bounds bounds = new Bounds();
        double distanta = bounds.calcDisance(MyLocation.getInstance().getLatitude(),MyLocation.getInstance().getLongitude(), InformationEnklave.getInstance().getLatitude(),InformationEnklave.getInstance().getLongitude());
        if(distanta >= 60){
            if(!advertismentOn){
                advertismentOn = true;
                createdialog("You have 20 sec to return\n in the enklave area, otherwise\n the actions queue will\n be cancelled!");
                return true;
            }else{
                createdialog("The actions queue is cancelled!");
                QueueBuildCraft.getInstance().clearQueue();
                advertismentOn = false;
                return false;
            }
        }else{
            advertismentOn = false;
            return true;
        }

    }
    public void createdialog(String message) {
        Pixmap p = new Pixmap(WIDTH,HEIGHT, Pixmap.Format.RGBA8888);
        p.setColor(1.0f, 1.0f, 1.0f, 0.8f);
        p.fill();
        Skin s = new Skin();
        s.add("black-background", new Texture(p));
        s.add("default", new Label.LabelStyle(new BitmapFont(false), Color.WHITE));
        s.add("default", new Window.WindowStyle(new BitmapFont(false), Color.WHITE, s.newDrawable("black-background", Color.DARK_GRAY)));
        dia = new Dialog("",s);
        p = new Pixmap(0,0, Pixmap.Format.RGBA8888);
        p.dispose();
        s =new Skin();
        s.dispose();
        Label label = new Label(message,new Label.LabelStyle(Font.getFont((int) (HEIGHT * 0.03)),Color.WHITE));
        label.setAlignment(Align.center);
        dia.text(label);
        dia.show(thisstage);
        new Timer().scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                dia.hide();
            }
        },1.5f);
    }
}
