package com.enklave.game.Screens;

import com.enklave.game.Chat.SendFaction;
import com.enklave.game.Chat.SendLocation;
import com.enklave.game.FontLabel.Font;
import com.enklave.game.GameManager;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.Profile.InformationProfile;
import com.enklave.game.Utils.NameFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ScreenChat {
    private final Label labelTest,labelTest1;
    private GameManager gameManager;
    private ManagerAssets manager;
    private static ScreenChat ourInstance = new ScreenChat();
    private Image image1;
    private int WIDTH = Gdx.graphics.getWidth(),HEIGHT = Gdx.graphics.getHeight();
    private Group group,groupbotttom;
    private Image background1,background2;
    private VerticalGroup grchatfaction,grchatlocation;
    private Table ta,tablechatpublic;
    private ScrollPane sp,scrollchatpublic;
    private boolean visibleChat;
    BitmapFont font = Font.getFont((int)(HEIGHT*0.02f));
    private TextField textField;

    public static ScreenChat getInstance() {
        return ourInstance;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    private ScreenChat() {
        manager = ManagerAssets.getInstance();
        labelTest = new Label(" ",new Label.LabelStyle(font,null));
        labelTest1 = new Label(" ",new Label.LabelStyle(font,null));
        init();
        Addtogroup();
        addchatlocation();
    }

    private void init(){
        Texture txt = manager.getAssetsChatScreen().getTexture(NameFiles.btnchatfadein);
        Vector2 crop = Scaling.fill.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        group = new Group();
        group.setSize(WIDTH,HEIGHT);
        group.setPosition(WIDTH,0);
        groupbotttom = new Group();
        groupbotttom.setSize(0,0);
        groupbotttom.setPosition(WIDTH,0);
        image1 = new Image(new TextureRegion(txt));
        image1.setSize(crop.x*0.06f,crop.y*0.075f);
        image1.setPosition(WIDTH - image1.getWidth(),HEIGHT * 0.15f);
        image1.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                image1.setVisible(false);
                MoveToAction move = new MoveToAction();
                move.setDuration(1);
                move.setPosition(0,0f);
                group.addAction(move);
                move = new MoveToAction();
                move.setDuration(1);
                move.setPosition(0,0f);
                groupbotttom.addAction(move);
                gameManager.mapsScreen.setcontrollerMap(true);
                visibleChat = true;
            }
        });
        txt = manager.getAssetsChatScreen().getTexture(NameFiles.backgroundchatbottom);
        crop = Scaling.fit.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        Image background = new Image(new TextureRegion(txt));
        background.setSize(crop.x,crop.y);
        background.setPosition(0,0);
        groupbotttom.addActor(background);
        txt = manager.getAssetsChatScreen().getTexture(NameFiles.backgroundchatmiddle);
        crop = Scaling.fit.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        background1 = new Image(new TextureRegion(txt));
        background1.setSize(crop.x,crop.y*3.5f);
        background1.setPosition(0,background.getTop());
        group.addActor(background1);
        txt = manager.getAssetsChatScreen().getTexture(NameFiles.backgroundchattop);
        crop = Scaling.fit.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        background2 = new Image(new TextureRegion(txt));
        background2.setSize(crop.x,crop.y);
        background2.setPosition(0,background1.getTop());
        group.addActor(background2);
        txt = manager.getAssetsChatScreen().getTexture(NameFiles.btnchatfadeout);
        crop = Scaling.fit.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        Image btnFadeout = new Image(new TextureRegion(txt));
        btnFadeout.setSize(crop.x*0.13f,crop.y*0.13f);
        btnFadeout.setPosition(background2.getX()+background2.getWidth()*0.02f,background2.getTop() - btnFadeout.getHeight()-background2.getWidth()*0.013f);
        btnFadeout.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.setOnscreenKeyboardVisible(false);
                image1.setVisible(true);
                MoveToAction move = new MoveToAction();
                move.setDuration(1);
                move.setPosition(WIDTH,0);
                group.addAction(move);
                move = new MoveToAction();
                move.setDuration(1);
                move.setPosition(WIDTH,0);
                groupbotttom.addAction(move);
                gameManager.mapsScreen.setcontrollerMap(false);
                visibleChat = false;
            }
        });
        group.addActor(btnFadeout);
        final ImageTextButton btn1 = getImageButton("PUBLIC");
        btn1.setChecked(true);
        btn1.setPosition(btnFadeout.getRight() + btnFadeout.getWidth()*0.19f,background2.getTop() - btn1.getHeight()*1.4f);
        group.addActor(btn1);
        final ImageTextButton btn2 = getImageButton("FACTION");
        btn2.setPosition(btn1.getRight(),background2.getTop() - btn1.getHeight()*1.4f);
        group.addActor(btn2);
        final ImageTextButton btn3 = getImageButton("ALERT");
        btn3.setPosition(btn2.getRight(),background2.getTop() - btn1.getHeight()*1.4f);
        group.addActor(btn3);
        txt = manager.getAssetsChatScreen().getTexture(NameFiles.btnchatsend);
        crop = Scaling.fit.apply(txt.getWidth(),txt.getHeight(),WIDTH,HEIGHT);
        final Image btnsend = new Image(new TextureRegion(txt));
        TextField.TextFieldStyle textstyle = new TextField.TextFieldStyle(Font.getFont((int)(HEIGHT*0.025f)), Color.WHITE,new TextureRegionDrawable(new TextureRegion(manager.getAssetsButton().get(NameFiles.cursorTextField))),new BaseDrawable(),new BaseDrawable());
        textstyle.cursor.setMinWidth(10);
        textField = new TextField(" ",textstyle);

        btn1.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                btn2.setChecked(false);
                btn3.setChecked(false);
                ta.setVisible(false);
                tablechatpublic.setVisible(true);
                textField.setVisible(true);
                btnsend.setVisible(true);
            }
        });
        btn2.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                btn1.setChecked(false);
                btn3.setChecked(false);
                ta.setVisible(true);
                tablechatpublic.setVisible(false);
                textField.setVisible(true);
                btnsend.setVisible(true);;
            }
        });
        btn3.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                btn1.setChecked(false);
                btn2.setChecked(false);
                ta.setVisible(false);
                tablechatpublic.setVisible(false);
                textField.setVisible(false);
                btnsend.setVisible(false);
                Gdx.input.setOnscreenKeyboardVisible(false);
            }
        });
        btnsend.setSize(crop.x*0.2f,crop.y*0.2f);
        btnsend.setPosition(background.getRight() - btnsend.getWidth()*1.25f,background.getY()+btnsend.getHeight()*0.85f);

        textField.setWidth(WIDTH - btnsend.getWidth()*1.5f);
        textField.setPosition(0,0);//background.getX()+WIDTH*0.05f,btnsend.getY()-textField.getHeight()*0.1f);
        final Group grouptext = new Group();
        grouptext.setSize(textField.getWidth(),textField.getHeight());
        grouptext.setPosition(background.getX()+WIDTH*0.065f,btnsend.getY()-textField.getHeight()*0.1f);
        grouptext.addActor(textField);

        final ScrollPane scrolltext = new ScrollPane(grouptext);
        scrolltext.setFillParent(true);
        scrolltext.setScrollingDisabled(false,true);
        scrolltext.layout();
        Table tabscroll = new Table();
        tabscroll.setFillParent(false);
        tabscroll.add(scrolltext).fill().expand();
        tabscroll.setBounds(background.getX()+WIDTH*0.05f,btnsend.getY()-textField.getHeight()*0.1f,WIDTH - btnsend.getWidth()*1.65f,145);textField.layout();
        scrolltext.act(Gdx.graphics.getDeltaTime());
        scrolltext.setScrollPercentX(100);
        scrolltext.updateVisualScroll();
        scrolltext.layout();
        groupbotttom.addActor(tabscroll);
        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                GlyphLayout layout = new GlyphLayout();
                layout.setText(Font.getFont((int)(HEIGHT*0.025f)),textField.getText());
                textField.setWidth(layout.width);
                grouptext.setWidth(layout.width);
                scrolltext.act(Gdx.graphics.getDeltaTime());
                scrolltext.setScrollPercentX(100);
                scrolltext.updateVisualScroll();
                scrolltext.layout();
            }
        });

        btnsend.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!textField.getText().contentEquals("")){
                    if(btn1.isChecked()){
                        new SendLocation().sendMessage(textField.getText());
                    }else if(btn2.isChecked()){
                        new SendFaction().sendMessage(textField.getText());
                    }
                    textField.setText("");
                    textField.setWidth(WIDTH - btnsend.getWidth()*1.5f);
                }
            }
        });
        groupbotttom.addActor(btnsend);

    }

    private ImageTextButton getImageButton(String title){
        Skin skin = new Skin();
        String bup = "button-up",bdown = "button-down", bcheck = "button-checked";
        TextureAtlas txta = new TextureAtlas();
        Texture t = manager.getAssetsChatScreen().getTexture(NameFiles.btnchattabmenu);
        Vector2 crop = Scaling.fit.apply(t.getWidth(), t.getHeight() / 2, WIDTH, HEIGHT);
        txta.addRegion(bup, new TextureRegion(t, 0, 0, t.getWidth(), t.getHeight() / 2));
        txta.addRegion(bdown, new TextureRegion(t, 0, t.getHeight() / 2, t.getWidth(), t.getHeight() / 2));
        txta.addRegion(bcheck, new TextureRegion(t, 0, t.getHeight() / 2, t.getWidth(), t.getHeight() / 2));
        skin.addRegions(txta);
        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle();
        style.up = skin.getDrawable(bup);
        style.down = skin.getDrawable(bdown);
        style.checked = skin.getDrawable(bcheck);
        style.font = Font.getFont((int) (HEIGHT * 0.025));
        skin = new Skin();
        skin.dispose();
        txta = new TextureAtlas();
        txta.dispose();
        t = new Texture(0,0, Pixmap.Format.RGBA8888);
        t.dispose();
        ImageTextButton btn = new ImageTextButton(title,style);
        btn.setSize(crop.x * 0.23f,crop.y*0.23f);
        return btn;
    }

    public void addToStage(Stage stage){
        stage.addActor(group);
        stage.addActor(groupbotttom);
        stage.addActor(image1);
    }

    public void setSizeKeyboard(final int sizeKeyboard) {
        MoveToAction move = new MoveToAction();
        move.setDuration(0.1f);
        move.setPosition(0, sizeKeyboard);
        groupbotttom.addAction(move);
        ta.setBounds(WIDTH * 0.05f, background1.getY(), WIDTH * 0.9f, background1.getHeight()*1.05f - sizeKeyboard);
        sp.setScrollPercentY(100);
        sp.act(Gdx.graphics.getDeltaTime());
        sp.updateVisualScroll();
        tablechatpublic.setBounds(WIDTH * 0.05f, background1.getY(), WIDTH * 0.9f, background1.getHeight()*1.05f - sizeKeyboard);
        scrollchatpublic.setScrollPercentY(100);
        scrollchatpublic.act(Gdx.graphics.getDeltaTime());
        scrollchatpublic.updateVisualScroll();
        scrollchatpublic.layout();
        if(sizeKeyboard>WIDTH*0.1){
            GlyphLayout layout = new GlyphLayout();
            layout.setText(Font.getFont((int)(HEIGHT*0.025f)),textField.getText());
            textField.setWidth(layout.width);
        }else{
            textField.setWidth(WIDTH *0.72f);
        }
    }

    public void Addtogroup(){
        grchatfaction = new VerticalGroup();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
        try {
            grchatfaction.addActor(addComment(sdf.parse("2016/5/17/13:34:23"),"adrian", Color.BLUE,"Welcome Chat!"));
            grchatfaction.addActor(addComment(sdf.parse("2016/5/17/13:34:23"),"adrian", Color.BLUE,"Faction!"));
            grchatfaction.addActor(labelTest1);
        } catch (ParseException e) {
            e.printStackTrace();
            Gdx.app.log("eroare","intra");
        }
        sp = new ScrollPane(grchatfaction);
        sp.layout();
        sp.setScrollingDisabled(true, false);
        sp.setFillParent(true);sp.setLayoutEnabled(true);
        ta = new Table();
        ta.setFillParent(false);
        ta.add(sp).fill().expand();
        ta.setBounds(WIDTH *0.05f,background1.getY(), WIDTH*0.9f,background1.getHeight() * 1.05f);
        ta.setVisible(false);
        groupbotttom.addActor(ta);
        sp.setScrollPercentY(200);
        sp.act(Gdx.graphics.getDeltaTime());
        sp.updateVisualScroll();
    }

    public void addchatlocation(){
        grchatlocation = new VerticalGroup();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
        try {
            grchatlocation.addActor(addComment(sdf.parse("2016/5/17/13:34:23"),"adrian", Color.RED,"Welcome Chat!"));
            grchatlocation.addActor(addComment(sdf.parse("2016/5/17/13:34:23"),"adrian", Color.GREEN,"Location!"));
            grchatlocation.addActor(labelTest);
        } catch (ParseException e) {
            e.printStackTrace();
            Gdx.app.log("eroare","intra");
        }
        scrollchatpublic = new ScrollPane(grchatlocation);
        scrollchatpublic.layout();
        scrollchatpublic.setScrollingDisabled(true, false);
        scrollchatpublic.setFillParent(true);
        scrollchatpublic.setLayoutEnabled(true);
        tablechatpublic = new Table();
        tablechatpublic.setFillParent(false);
        tablechatpublic.add(scrollchatpublic).fill().expand();
        tablechatpublic.setBounds(WIDTH *0.05f,background1.getY(), WIDTH*0.9f,background1.getHeight() * 1.05f);
        groupbotttom.addActor(tablechatpublic);
        scrollchatpublic.setScrollPercentY(100);
        scrollchatpublic.act(Gdx.graphics.getDeltaTime());
        scrollchatpublic.updateVisualScroll();
    }

    public void addmessage(String value,String name,Date date){
        grchatfaction.addActorBefore(labelTest1,addComment(date,name,detcolor(InformationProfile.getInstance().getDateUserGame().getFaction()),value));
        sp.setScrollPercentY(100);
        sp.act(Gdx.graphics.getDeltaTime());
        sp.updateVisualScroll();
        sp.layout();
    }

    public void addmesschatpublic(String value,Color c,String user,Date date){
        grchatlocation.addActorBefore(labelTest,addComment(date,user,c,value));
        scrollchatpublic.setScrollPercentY(100);
        scrollchatpublic.act(Gdx.graphics.getDeltaTime());
        scrollchatpublic.updateVisualScroll();
        scrollchatpublic.layout();
    }

    public Group addComment(Date date, String Username, Color color, String value){
        Group gr = new Group();
        font.getData().markupEnabled = true;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        Label labeldate;
        if(date.after(c.getTime())){
            SimpleDateFormat dateFormat = new SimpleDateFormat("mm", Locale.US);
            c.setTime(date);
            labeldate = new Label(c.get(Calendar.HOUR_OF_DAY)+":"+dateFormat.format(c.getTime()),new Label.LabelStyle(font,Color.GOLD));
        }else{
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM", Locale.US);
            c.setTime(date);
            labeldate = new Label(dateFormat.format(c.getTime()).toUpperCase()+"."+c.get(Calendar.DAY_OF_MONTH),new Label.LabelStyle(font,Color.GOLD));
        }
        labeldate.setWrap(true);
        labeldate.setAlignment(Align.center);
        labeldate.setWidth(WIDTH*0.2f);
        Label labeltext = new Label("[#"+color.toString()+"]"+Username+": [#"+Integer.toHexString(Color.WHITE.toIntBits()).substring(2)+"]"+value,new Label.LabelStyle(font,null));
        labeltext.setWrap(true);
        labeltext.setAlignment(Align.left);
        labeltext.setWidth(WIDTH*0.75f);
        labeltext.pack();
        labeltext.setWidth(WIDTH*0.75f);
        labeldate.setPosition(0,labeltext.getHeight() - labeldate.getHeight());
        labeltext.setPosition(labeldate.getRight(),labeldate.getTop() - labeltext.getHeight());
        gr.addActor(labeldate);
        gr.addActor(labeltext);
        gr.setSize(WIDTH*0.9f,labeltext.getHeight());
        gr.setX(WIDTH*0.05f);
        return gr;
    }

    public Color detcolor(int c){
        switch (c){
            case 1:{
                return Color.RED;
            }
            case 2:{
                return Color.BLUE;
            }
            case 3:{
                return Color.GREEN;
            }
        }
        return Color.BLACK;
    }

    public boolean isVisibleChat() {
        return visibleChat;
    }

    public void fadeout(){
        image1.setVisible(true);
        MoveToAction move = new MoveToAction();
        move.setDuration(1);
        move.setPosition(WIDTH,0);
        group.addAction(move);
        move = new MoveToAction();
        move.setDuration(1);
        move.setPosition(WIDTH,0);
        groupbotttom.addAction(move);
        gameManager.mapsScreen.setcontrollerMap(false);
        visibleChat = false;
    }
}
