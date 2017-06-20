package com.enklave.game.Screens;

import com.enklave.game.FontLabel.Font;
import com.enklave.game.GameManager;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;

public class TutorialDialog extends Dialog {
    Skin skin = new Skin();
    String bup = "button-up";String bdown = "button-down";String bcheck = "button-checked";
    public Boolean isSetYesTutorial = false;
    private Group grPuls;
    private GameManager gameManager;
    private ManagerAssets managerAssets = ManagerAssets.getInstance();
    private int WIDTH = Gdx.graphics.getWidth();
    private int HEIGHT = Gdx.graphics.getHeight();

    public TutorialDialog(String title, Skin skin,GameManager gmaps) {
        super(title, skin);
        this.gameManager = gmaps;
    }
    public TutorialDialog(GameManager gMaps){
        super("", new WindowStyle(new BitmapFont(false), Color.BLUE, new BaseDrawable()));
        this.gameManager = gMaps;
    }
    public TutorialDialog getstartTutorial(String title){
        Pixmap p = new Pixmap(WIDTH,HEIGHT, Pixmap.Format.RGBA8888);
        p.setColor(1.0f, 1.0f, 1.0f, 0.7f);
        p.fill();
        skin.add("black-background", new Texture(p));
        skin.add("default", new Label.LabelStyle(new BitmapFont(false), Color.WHITE));
        skin.add("default", new Window.WindowStyle(new BitmapFont(false), Color.WHITE, skin.newDrawable("black-background", Color.DARK_GRAY)));
        final TutorialDialog dia = new TutorialDialog(title,skin,gameManager);
        p = new Pixmap(0,0, Pixmap.Format.RGBA8888);
        p.dispose();
        ImageTextButton left = addButtonLeft();
        left.setPosition(WIDTH / 4, HEIGHT / 4);
        dia.addActor(left);
        ImageTextButton right = addButtonRight();
        right.setPosition(WIDTH / 2, HEIGHT / 4);
        dia.addActor(right);
        Label info = new Label("It helps you level up faster.",new Label.LabelStyle(Font.getFont((int) (HEIGHT * 0.02f)),Color.WHITE));
        info.setPosition(WIDTH / 2 - info.getWidth() / 2, HEIGHT * 0.3525f);
        dia.addActor(info);
        Texture txt = managerAssets.getAssetsTutorial().getTexture(NameFiles.logoHeader);
        Vector2 crop = Scaling.fit.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        Image logotutorial = new Image(new TextureRegion(txt));
        logotutorial.setSize(crop.x * 0.28f, crop.y * 0.28f);
        logotutorial.setPosition(WIDTH / 2 - logotutorial.getWidth() / 2, HEIGHT / 2);
        dia.addActor(logotutorial);
        txt = managerAssets.getAssetsTutorial().getTexture(NameFiles.labeltextfollow);
        crop = Scaling.fit.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        Image labelFollow = new Image(new TextureRegion(txt));
        labelFollow.setSize(crop.x *0.65f, crop.y * 0.65f);
        labelFollow.setPosition(WIDTH / 2 - labelFollow.getWidth() / 2, HEIGHT * 0.4f);
        dia.addActor(labelFollow);
        left.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dia.isSetYesTutorial = true;
                skin.dispose();
                Preferences pref = Gdx.app.getPreferences("Profile");
                pref.putBoolean("tutorialShow", true);
                pref.flush();
                gameManager.mapsScreen.setcontrollerMap(false);
                managerAssets.getAssetsTutorial().dispose();

                dia.hide();
            }
        });
        right.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dia.isSetYesTutorial = true;
                Preferences pref = Gdx.app.getPreferences("Profile");
                pref.putBoolean("tutorialShow", true);
                pref.flush();
                dia.hide();
                skin.dispose();
                TutorialDialog d = dia.getInfoforProfile("");
                gameManager.mapsScreen.getStage().addActor(d);
            }
        });
        txt = new Texture(0,0, Pixmap.Format.RGBA8888);
        txt.dispose();
        return dia;
    }

    public TutorialDialog getInfoforProfile(String title){
        Pixmap p = managerAssets.getAssetsTutorial().getPixmap(NameFiles.tutorialProfile);
        skin = new Skin();
        skin.add("black-background", new Texture(p));
        skin.add("default", new Label.LabelStyle(new BitmapFont(false), Color.WHITE));
        skin.add("default", new Window.WindowStyle(new BitmapFont(false), Color.WHITE, skin.newDrawable("black-background", Color.WHITE)));
        final TutorialDialog dia = new TutorialDialog(title,skin,gameManager);
        Vector2 crop = Scaling.fit.apply(p.getWidth(),p.getHeight(),WIDTH,HEIGHT);
        dia.setSize(crop.x * 0.6f, crop.y * 0.6f);
        dia.setPosition(WIDTH * 0.2f, HEIGHT * 0.687f);
        gameManager.mapsScreen.getStage().addActor(createPuls(WIDTH * 0.038f, HEIGHT * 0.9f ));//Width * 0.04f, Height -(profile.getHeight()* 1.25f)
        ImageTextButton bt = dia.makeButtonTutorial();
        bt.setPosition(dia.getWidth()/2 - bt.getWidth()/2, dia.getHeight() * 0.03f);
        bt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dia.hide();
                skin.dispose();
                TutorialDialog d = dia.makeTutorialforEnergy("");
                gameManager.mapsScreen.getStage().addActor(d);
                grPuls.remove();
            }
        });
        dia.addActor(bt);
        p = new Pixmap(0,0, Pixmap.Format.RGBA8888);
        p.dispose();
        return dia;
    }
    public TutorialDialog maketutorialforchat(String title){
        Pixmap p = managerAssets.getAssetsTutorial().getPixmap(NameFiles.tutorialComm);
        skin.add("black-background", new Texture(p));
        skin.add("default", new Label.LabelStyle(new BitmapFont(false), Color.WHITE));
        skin.add("default", new Window.WindowStyle(new BitmapFont(false), Color.WHITE, skin.newDrawable("black-background", Color.WHITE)));
        final TutorialDialog dia = new TutorialDialog(title,skin,gameManager);
        gameManager.mapsScreen.getStage().addActor(createPuls(WIDTH * 0.725f, HEIGHT * 0.0075f));
        Vector2 crop = Scaling.fit.apply(p.getWidth(), p.getHeight(), WIDTH, HEIGHT);
        dia.setSize(crop.x*0.6f, crop.y*0.6f);
        dia.setPosition(WIDTH * 0.4f, HEIGHT * 0.12f);
        ImageTextButton bt = dia.makeButtonTutorial();
        bt.setPosition(dia.getWidth() / 2 - bt.getWidth() / 2, dia.getHeight() * 0.15f);
        bt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dia.hide();
                skin.dispose();
                TutorialDialog d = dia.maketutorialforCrafting("");
                gameManager.mapsScreen.getStage().addActor(d);
                grPuls.remove();
            }
        });
        dia.addActor(bt);
        p = new Pixmap(0,0, Pixmap.Format.RGBA8888);
        p.dispose();
        return dia;
    }
    public TutorialDialog maketutorialforCrafting(String title){
        Pixmap p = managerAssets.getAssetsTutorial().getPixmap(NameFiles.tutorialCrafting);
        skin.add("black-background", new Texture(p));
        skin.add("default", new Label.LabelStyle(new BitmapFont(false), Color.WHITE));
        skin.add("default", new Window.WindowStyle(new BitmapFont(false), Color.WHITE, skin.newDrawable("black-background", Color.WHITE)));
        final TutorialDialog dia = new TutorialDialog(title,skin,gameManager);
        gameManager.mapsScreen.getStage().addActor(createPuls(WIDTH * 0.845f, HEIGHT * 0.017f));
        Vector2 crop = Scaling.fit.apply(p.getWidth(), p.getHeight(), WIDTH,HEIGHT);
        dia.setSize(crop.x*0.6f, crop.y*0.6f);
        dia.setPosition(WIDTH * 0.39f, HEIGHT * 0.13f);
        ImageTextButton bt = dia.makeButtonTutorial();
        bt.setPosition(dia.getWidth()/2 - bt.getWidth()/2, dia.getHeight() * 0.13f);
        bt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dia.hide();
                skin.dispose();
                TutorialDialog d = dia.maketutorialforScrap("");
                gameManager.mapsScreen.getStage().addActor(d);
                grPuls.remove();
            }
        });
        dia.addActor(bt);
        p = new Pixmap(0,0, Pixmap.Format.RGBA8888);
        p.dispose();
        return dia;
    }
    public TutorialDialog maketutorialforScrap(String title){
        Pixmap p = managerAssets.getAssetsTutorial().getPixmap(NameFiles.tutorialScrap);
        skin.add("black-background", new Texture(p));
        skin.add("default", new Label.LabelStyle(new BitmapFont(false), Color.WHITE));
        skin.add("default", new Window.WindowStyle(new BitmapFont(false), Color.WHITE, skin.newDrawable("black-background", Color.WHITE)));
        Vector2 crop = Scaling.fit.apply(p.getWidth(),p.getHeight(),WIDTH,HEIGHT);
        final TutorialDialog dia = new TutorialDialog(title,skin,gameManager);
        dia.setSize(crop.x*0.7f, crop.y*0.7f);
        dia.setPosition(WIDTH / 2 - dia.getWidth() / 2, HEIGHT / 2 - dia.getHeight() / 2);
        ImageTextButton bt = dia.makeButtonTutorial();
        bt.setPosition(dia.getWidth()/2 - bt.getWidth()/2f, dia.getHeight() * 0.05f);
        bt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dia.hide();
                skin.dispose();
                managerAssets.getAssetsTutorial().dispose();
                gameManager.mapsScreen.setcontrollerMap(false);
            }
        });
        dia.addActor(bt);
        p = new Pixmap(0,0, Pixmap.Format.RGBA8888);
        p.dispose();
        return dia;
    }
    public TutorialDialog makeTutorialforEnergy(String title){
        gameManager.mapsScreen.getProgressBarEnergy().FadeIn();
        Pixmap p = managerAssets.getAssetsTutorial().getPixmap(NameFiles.tutorialEnergy);
        skin.add("black-background", new Texture(p));
        skin.add("default", new Label.LabelStyle(new BitmapFont(false), Color.WHITE));
        skin.add("default", new Window.WindowStyle(new BitmapFont(false), Color.WHITE, skin.newDrawable("black-background", Color.WHITE)));
        final TutorialDialog dia = new TutorialDialog(title,skin,gameManager);
        gameManager.mapsScreen.getStage().addActor(createPuls(WIDTH / 2.3f, HEIGHT* 0.915f));
        Vector2 crop = Scaling.fit.apply(p.getWidth(), p.getHeight(), WIDTH,HEIGHT);
        dia.setSize(crop.x*0.65f, crop.y*0.65f);
        dia.setPosition(WIDTH * 0.18f, HEIGHT * 0.7f);
        ImageTextButton bt = dia.makeButtonTutorial();
        bt.setPosition(dia.getWidth()/2 - bt.getWidth()/2, dia.getHeight() * 0.05f);
        bt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dia.hide();
                skin.dispose();
                TutorialDialog d = dia.maketutorialforchat("");
                gameManager.mapsScreen.getStage().addActor(d);
                gameManager.mapsScreen.getProgressBarEnergy().Fadeout();
                grPuls.remove();
            }
        });
        dia.addActor(bt);
        p = new Pixmap(0,0, Pixmap.Format.RGBA8888);
        p.dispose();
        return dia;
    }

    public ImageTextButton makeButtonTutorial(){
        Skin s=new Skin();
        TextureAtlas txta = new TextureAtlas();
        Texture t = managerAssets.getAssetsButton().get(NameFiles.button_describe);
        Vector2 crop = Scaling.fit.apply(t.getWidth(), t.getHeight(),WIDTH,HEIGHT);
        txta.addRegion(bup, new TextureRegion(t,0,0,t.getWidth(),t.getHeight()/2));
        txta.addRegion(bdown, new TextureRegion(t, 0, t.getHeight() / 2, t.getWidth(), t.getHeight() / 2));
        txta.addRegion(bcheck, new TextureRegion(t, 0, 0, t.getWidth(), t.getHeight() / 2));
        s.addRegions(txta);
        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle();
        style.up = s.getDrawable(bup);
        style.down = s.getDrawable(bdown);
        style.checked = s.getDrawable(bcheck);
        style.font = Font.getFont((int) (Gdx.graphics.getHeight() * 0.03));
        s = new Skin();
        s.dispose();
        txta = new TextureAtlas();
        txta.dispose();
        t = new Texture(0,0, Pixmap.Format.RGBA8888);
        t.dispose();
        ImageTextButton btn = new ImageTextButton("GOT IT",style);
        btn.setSize(crop.x*0.5f,crop.y*0.25f);
        return btn;
    }
    public ImageTextButton addButtonLeft(){
        String bup = "button-up";String bdown = "button-down";String bcheck = "button-checked";
        Skin skin =new Skin();
        TextureAtlas txta = new TextureAtlas();
        Texture txt = managerAssets.getAssetsButton().get(NameFiles.buttonLeft);
        Vector2 crop = Scaling.fit.apply(txt.getWidth(), txt.getHeight(), WIDTH, HEIGHT);
        txta.addRegion(bup, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 2 - 1));
        txta.addRegion(bdown, new TextureRegion(txt, 0, txt.getHeight() / 2 - 1, txt.getWidth(), txt.getHeight() / 2));
        txta.addRegion(bcheck, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 2 - 1));
        skin.addRegions(txta);
        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle();
        style.up = skin.getDrawable(bup);
        style.down = skin.getDrawable(bdown);
        style.checked = skin.getDrawable(bcheck);
        style.font = Font.getFont((int) (Gdx.graphics.getHeight() * 0.03f));
        skin = new Skin();
        skin.dispose();
        txta = new TextureAtlas();
        txta.dispose();
        txt = new Texture(0,0, Pixmap.Format.RGBA8888);
        txt.dispose();
        ImageTextButton btn = new ImageTextButton("NO",style);
        btn.setSize(crop.x*0.3f,crop.y*0.15f);
        return btn;
    }
    public ImageTextButton addButtonRight(){
        Skin skin = new Skin();
        Texture txt = managerAssets.getAssetsButton().get(NameFiles.buttonRight);
        Vector2 crop = Scaling.fit.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        TextureAtlas txta = new TextureAtlas();
        txta.addRegion(bup, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 2));
        txta.addRegion(bdown, new TextureRegion(txt, 0, txt.getHeight() / 2, txt.getWidth(), txt.getHeight() / 2));
        txta.addRegion(bcheck, new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight() / 2));
        skin.addRegions(txta);
        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle();
        style.up = skin.getDrawable(bup);
        style.down = skin.getDrawable(bdown);
        style.checked = skin.getDrawable(bcheck);
        style.font = Font.getFont((int) (HEIGHT * 0.03f));
        skin = new Skin();
        skin.dispose();
        txta = new TextureAtlas();
        txta.dispose();
        txt = new Texture(0,0, Pixmap.Format.RGBA8888);
        txt.dispose();
        ImageTextButton btn = new ImageTextButton("Yes",style);
        btn.setSize(crop.x*0.3f,crop.y*0.15f);
        return btn;
    }

    public Group createPuls(float x, float y){
        grPuls = new Group();
        Texture txt = managerAssets.getAssetsTutorial().getTexture(NameFiles.circlePulsTutorial);
        Image image = new Image(new TextureRegion(txt));
        Vector2 crop = Scaling.fit.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        image.setSize(crop.x * 0.135f, crop.y * 0.135f);
        image.setPosition(x, y);
        grPuls.addActor(image);
        txt = managerAssets.getAssetsTutorial().getTexture(NameFiles.PulsCircleScalable);
        Image puls = new Image(new TextureRegion(txt));
        crop = Scaling.fit.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        puls.setPosition(image.getRight() - image.getWidth() / 2, image.getTop() - image.getHeight() / 2);
        puls.setSize(1, 1);
        MoveToAction move = new MoveToAction();
        move.setDuration(1);
        move.setPosition(image.getRight() - image.getWidth() / 2 - crop.x*0.085f, image.getTop() - image.getHeight() / 2 - crop.y*0.085f);
        MoveToAction mo = new MoveToAction();
        mo.setDuration(0);
        mo.setPosition(image.getRight() - image.getWidth() / 2, image.getTop() - image.getHeight() / 2);
        ScaleToAction scale = new ScaleToAction();
        scale.setScale(WIDTH*0.17f);
        scale.setDuration(1);
        ScaleToAction sc = new ScaleToAction();
        sc.setDuration(0);
        sc.setScale(0);
        RepeatAction repeat = new RepeatAction();
        repeat.setCount(RepeatAction.FOREVER);
        repeat.setAction(new SequenceAction(scale, sc));
        puls.addAction(repeat);
        RepeatAction r = new RepeatAction();
        r.setCount(RepeatAction.FOREVER);
        r.setAction(new SequenceAction(move, mo));
        puls.addAction(r);
        grPuls.addActor(puls);
        return grPuls;
    }
}
