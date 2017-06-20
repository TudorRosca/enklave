package com.enklave.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
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
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.enklave.game.Enklave.DescEnklave.GetEnklaveDetails;
import com.enklave.game.Enum.Menu;
import com.enklave.game.FontLabel.Font;
import com.enklave.game.GameManager;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.Utils.NameFiles;

public class ScreenSetting implements Screen {
    private GameManager gameManager;
    private Stage stage;
    private int Width = Gdx.graphics.getWidth(),Height = Gdx.graphics.getHeight();
    private ManagerAssets manager;
    String bup = "button-up",bdown = "button-down", bcheck = "button-checked";
    private ImageButton backButton;
    private ImageTextButton b5;
    private Table ta;
    private boolean drawone = true;
    private Group groupTutorial,groupPresentation,groupChangeFaction,groupKeepScreenOn,groupTestCombat;
    private Button buttonswith,buttonswithTutorial,buttonswithscreenOn;
    private ScrollPane sp;
    private Dialog dia;

    public ScreenSetting(GameManager gameManager) {
        this.gameManager = gameManager;
        manager = ManagerAssets.getInstance();
    }

    private void addSettingPresentation() {
        groupPresentation = new Group();
        Image background = new Image(new TextureRegion(manager.getAssetsSettings().getTexture(NameFiles.extensionImgBackground)));
        background.setSize(Width - Width * 0.03f, Height * 0.1f);
        background.setPosition(Width / 2 - background.getWidth() / 2, Height * 0.60f);
        groupPresentation.addActor(background);
        Label labeltutorial = new Label("Reset Screen Presentation",new Label.LabelStyle(Font.getFont((int)(Height*0.025)), Color.WHITE));
        labeltutorial.setPosition(background.getX()+background.getWidth()*0.05f,background.getY()+background.getHeight()/2-labeltutorial.getHeight()/2);
        groupPresentation.addActor(labeltutorial);
        Skin skin = new Skin();
        TextureAtlas txta = new TextureAtlas();
        Texture txt = manager.getAssetsSettings().getTexture(NameFiles.buttonSwith);
        txta.addRegion(bup, new TextureRegion(txt, 0, 0, txt.getWidth()/2, txt.getHeight()));
        txta.addRegion(bdown, new TextureRegion(txt, 0, 0, txt.getWidth()/2, txt.getHeight()));
        txta.addRegion(bcheck, new TextureRegion(txt, txt.getWidth() / 2, 0, txt.getWidth()/2, txt.getHeight()));
        skin.addRegions(txta);
        ImageButton.ImageButtonStyle sty = new ImageButton.ImageButtonStyle();
        sty.up = skin.getDrawable(bup);
        sty.down = skin.getDrawable(bdown);
        sty.checked = skin.getDrawable(bcheck);
        buttonswith = new Button(sty);
        Vector2 crop = Scaling.fit.apply(txt.getWidth()/2,txt.getHeight(),Width,Height);
        buttonswith.setSize(crop.x * 0.2f, crop.y * 0.2f);
        buttonswith.setPosition(background.getRight() - buttonswith.getWidth() * 1.2f, background.getY() + background.getHeight() / 2 - buttonswith.getHeight() / 2);
        txt = new Texture(0,0, Pixmap.Format.RGBA8888);
        txt.dispose();
        skin = new Skin();
        skin.dispose();
        txta = new TextureAtlas();
        txta.dispose();
        final Preferences preferences = Gdx.app.getPreferences("ScreenPresentation");
        buttonswith.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(buttonswith.isChecked()){
                    preferences.putBoolean("presentation",false);
                    preferences.flush();
                }else{
                    preferences.putBoolean("presentation",true);
                    preferences.flush();
                }
            }
        });
        groupPresentation.addActor(buttonswith);
    }

    private void addSettingTutorial() {
        groupTutorial = new Group();
        Image background = new Image(new TextureRegion(manager.getAssetsSettings().getTexture(NameFiles.extensionImgBackground)));
        background.setSize(Width - Width * 0.03f, Height * 0.1f);
        background.setPosition(Width / 2 - background.getWidth() / 2, Height * 0.75f);
        groupTutorial.addActor(background);
        Label labeltutorial = new Label("Reset all tutorials",new Label.LabelStyle(Font.getFont((int)(Height*0.025)), Color.WHITE));
        labeltutorial.setPosition(background.getX()+background.getWidth()*0.05f,background.getY()+background.getHeight()/2-labeltutorial.getHeight()/2);
        groupTutorial.addActor(labeltutorial);
        Skin skin = new Skin();
        TextureAtlas txta = new TextureAtlas();
        Texture txt = manager.getAssetsSettings().getTexture(NameFiles.buttonSwith);
        txta.addRegion(bup, new TextureRegion(txt, 0, 0, txt.getWidth()/2, txt.getHeight()));
        txta.addRegion(bdown, new TextureRegion(txt, 0, 0, txt.getWidth()/2, txt.getHeight()));
        txta.addRegion(bcheck, new TextureRegion(txt, txt.getWidth()/2, 0, txt.getWidth()/2, txt.getHeight()));
        skin.addRegions(txta);
        ImageButton.ImageButtonStyle sty = new ImageButton.ImageButtonStyle();
        sty.up = skin.getDrawable(bup);
        sty.down = skin.getDrawable(bdown);
        sty.checked = skin.getDrawable(bcheck);
        buttonswithTutorial = new Button(sty);
        Vector2 crop = Scaling.fit.apply(txt.getWidth()/2,txt.getHeight(),Width,Height);
        buttonswithTutorial.setSize(crop.x*0.2f,crop.y*0.2f);
        buttonswithTutorial.setPosition(background.getRight() - buttonswithTutorial.getWidth() * 1.2f, background.getY() + background.getHeight() / 2 - buttonswithTutorial.getHeight() / 2);
        txt = new Texture(0,0, Pixmap.Format.RGBA8888);
        txt.dispose();
        skin = new Skin();
        skin.dispose();
        txta = new TextureAtlas();
        txta.dispose();
        final Preferences pref = Gdx.app.getPreferences("Profile");
        buttonswithTutorial.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(buttonswithTutorial.isChecked()){
                    pref.putBoolean("tutorialShow",false);
                    pref.putBoolean("showonetime",true);
                    pref.flush();
                }else{
                    pref.putBoolean("tutorialShow", true);
                    pref.flush();
                }
            }
        });
        groupTutorial.addActor(buttonswithTutorial);
    }

    private void addChangeFaction(){
        groupChangeFaction = new Group();
        Image background = new Image(new TextureRegion(manager.getAssetsSettings().getTexture(NameFiles.extensionImgBackground)));
        background.setSize(Width - Width * 0.03f, Height * 0.1f);
        background.setPosition(Width / 2 - background.getWidth() / 2, Height * 0.3f);
        groupChangeFaction.addActor(background);
        Label labelFac = new Label("Change Faction",new Label.LabelStyle(Font.getFont((int)(Height*0.025f)),Color.WHITE));
        labelFac.setPosition(background.getX()+background.getWidth()*0.05f,background.getY()+background.getHeight()/2-labelFac.getHeight()/2);
        labelFac.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.loadAssetsChoiceFaction();
                gameManager.setScreen(new ScreenCircleLoading(gameManager,new ScreenChoiceFaction(gameManager),manager.getAssetsChoiceFaction()));
            }
        });
        groupChangeFaction.addActor(labelFac);
    }

    private void addKeepScreenOn(){
        groupKeepScreenOn = new Group();
        Image background = new Image(new TextureRegion(manager.getAssetsSettings().getTexture(NameFiles.extensionImgBackground)));
        background.setSize(Width - Width * 0.03f, Height * 0.1f);
        background.setPosition(Width / 2 - background.getWidth() / 2, Height * 0.45f);
        groupKeepScreenOn.addActor(background);
        Label labeltutorial = new Label("Keep Screen ON",new Label.LabelStyle(Font.getFont((int)(Height*0.025)), Color.WHITE));
        labeltutorial.setPosition(background.getX()+background.getWidth()*0.05f,background.getY()+background.getHeight()/2-labeltutorial.getHeight()/2);
        groupKeepScreenOn.addActor(labeltutorial);
        Skin skin = new Skin();
        TextureAtlas txta = new TextureAtlas();
        Texture txt = manager.getAssetsSettings().getTexture(NameFiles.buttonSwith);
        txta.addRegion(bup, new TextureRegion(txt, 0, 0, txt.getWidth()/2, txt.getHeight()));
        txta.addRegion(bdown, new TextureRegion(txt, 0, 0, txt.getWidth()/2, txt.getHeight()));
        txta.addRegion(bcheck, new TextureRegion(txt, txt.getWidth()/2, 0, txt.getWidth()/2, txt.getHeight()));
        skin.addRegions(txta);
        ImageButton.ImageButtonStyle sty = new ImageButton.ImageButtonStyle();
        sty.up = skin.getDrawable(bup);
        sty.down = skin.getDrawable(bdown);
        sty.checked = skin.getDrawable(bcheck);
        buttonswithscreenOn = new Button(sty);
        Vector2 crop = Scaling.fit.apply(txt.getWidth()/2,txt.getHeight(),Width,Height);
        buttonswithscreenOn.setSize(crop.x*0.2f,crop.y*0.2f);
        buttonswithscreenOn.setPosition(background.getRight() - buttonswithTutorial.getWidth() * 1.2f, background.getY() + background.getHeight() / 2 - buttonswithTutorial.getHeight() / 2);
        txt = new Texture(0,0, Pixmap.Format.RGBA8888);
        txt.dispose();
        skin = new Skin();
        skin.dispose();
        txta = new TextureAtlas();
        txta.dispose();
        final Preferences pref = Gdx.app.getPreferences("informationLog");
        buttonswithscreenOn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pref.putBoolean("keepScrrenOn",buttonswithscreenOn.isChecked());
                pref.flush();
                createexitdialog();
            }
        });
        groupKeepScreenOn.addActor(buttonswithscreenOn);
    }

    private void createexitdialog() {
        Pixmap p = new Pixmap(Width,Height, Pixmap.Format.RGBA8888);
        p.setColor(1.0f, 1.0f, 1.0f, 0.8f);
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
        Label label = new Label("This setting will only\ntake effect the next time\nEnklave is started.",new Label.LabelStyle(Font.getFont((int) (Height * 0.03)),Color.WHITE));
        label.setAlignment(Align.center);
        dia.text(label);
        dia.show(stage);
    }

    private void addTestCombat(){
        groupTestCombat = new Group();
        Image background = new Image(new TextureRegion(manager.getAssetsSettings().getTexture(NameFiles.extensionImgBackground)));
        background.setSize(Width - Width * 0.03f, Height * 0.1f);
        background.setPosition(Width / 2 - background.getWidth() / 2, Height * 0.15f);
        groupChangeFaction.addActor(background);
        Label labelFac = new Label("Combat Training",new Label.LabelStyle(Font.getFont((int)(Height*0.025f)),Color.WHITE));
        labelFac.setPosition(background.getX()+background.getWidth()*0.05f,background.getY()+background.getHeight()/2-labelFac.getHeight()/2);
        labelFac.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.loadAssetsEnklaveScreen();
                new GetEnklaveDetails().makeRequest(16066, manager);
                gameManager.screenEnklave.setEnklave3D(new Vector2(0,0));
                gameManager.setScreen(new ScreenCircleLoading(gameManager,gameManager.screenEnklave,manager.getAssertEnklaveScreen()));;
            }
        });
        groupChangeFaction.addActor(labelFac);
    }

    public ImageTextButton addButtonLeft(){
        String bup = "button-up",bdown = "button-down", bcheck = "button-checked";
        Skin s=new Skin();
        TextureAtlas txta = new TextureAtlas();
        Texture t = manager.getAssetsButton().get(NameFiles.buttontwoLeft);
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
        ImageTextButton imgbtn = new ImageTextButton("LATER",style);
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
        Texture t = manager.getAssetsButton().get(NameFiles.buttonRight);
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
        ImageTextButton imgbtn = new ImageTextButton("  RESTART",style);
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

    private void initializeMenuTab() {
        String bup = "button-up";String bdown = "button-down";String bcheck = "button-checked";
        final ButtonGroup<Button> group = new ButtonGroup<Button>();
        group.setMaxCheckCount(1);
        group.setMinCheckCount(1);
        Skin skin = new Skin();
        TextureAtlas txta = new TextureAtlas();
        Texture texture = manager.getAssetsButton().get(NameFiles.buttonTabCraft);
        txta.addRegion(bup, new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight() / 2));
        txta.addRegion(bdown, new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight() / 2));
        txta.addRegion(bcheck, new TextureRegion(texture, 0, texture.getHeight() / 2, texture.getWidth(), texture.getHeight() / 2));
        skin.addRegions(txta);
        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle();
        style.up = skin.getDrawable(bup);
        style.down = skin.getDrawable(bdown);
        style.checked = skin.getDrawable(bcheck);
        style.font = Font.getFont((int) (Gdx.graphics.getHeight() * 0.025f));

        ImageTextButton b1 = new ImageTextButton(String.valueOf(Menu.CHARACTER),style);
        b1.setPosition(0, 0);
        b1.setSize(Gdx.graphics.getWidth() * 0.37f, Gdx.graphics.getHeight() * 0.04f);
        group.add(b1);

        ImageTextButton b2 = new ImageTextButton(String.valueOf(Menu.CRAFTING),style);
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
        b5 = new ImageTextButton(String.valueOf(Menu.SETTING),style);
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
                    case CRAFTING: {
                        gameManager.setScreen(gameManager.screenCrafting);
                        break;
                    }
                    case CHARACTER: {
                        gameManager.setScreen(gameManager.screenProfile);
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
        sp.setScrollBarPositions(true, false);
        sp.setScrollingDisabled(false, true);
        sp.setFillParent(true);
        sp.setScrollPercentX(100);
        sp.updateVisualScroll();
        ta = new Table();
        ta.setFillParent(false);
        ta.add(sp).fill().expand();
        ta.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() * 0.04f);
        //button back
        skin = new Skin();
        txta = new TextureAtlas();
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
        backButton.setSize(Gdx.graphics.getHeight()*0.065f,Gdx.graphics.getHeight()*0.065f);
        backButton.setPosition(0, Gdx.graphics.getHeight() - (backButton.getHeight() * 1.75f));
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

    private void checkButtons(){
        buttonswithTutorial.setChecked(!Gdx.app.getPreferences("Profile").getBoolean("tutorialShow"));
        buttonswith.setChecked(!Gdx.app.getPreferences("ScreenPresentation").getBoolean("presentation"));
        buttonswithscreenOn.setChecked(Gdx.app.getPreferences("informationLog").getBoolean("keepScrrenOn"));
    }

    private void addtoStage(){
        stage.addActor(backButton);
        stage.addActor(ta);
        stage.addActor(groupTutorial);
        stage.addActor(groupPresentation);
        stage.addActor(groupChangeFaction);
        stage.addActor(groupKeepScreenOn);
        stage.addActor(groupTestCombat);
    }
    @Override
    public void show() {
        stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()));
        if(drawone) {
            initializeMenuTab();
            manager.loadAssetsSettings();
            manager.getAssetsSettings().finish();
            addSettingTutorial();
            addSettingPresentation();
            addChangeFaction();
            addKeepScreenOn();
            addTestCombat();
            drawone = false;
        }
        addtoStage();
        checkButtons();
        b5.setChecked(true);
        sp.setScrollPercentX(100);
        sp.updateVisualScroll();
        backButton.setChecked(false);
        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        stage.act();
        stage.draw();
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            gameManager.setScreen(gameManager.mapsScreen);
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
}
