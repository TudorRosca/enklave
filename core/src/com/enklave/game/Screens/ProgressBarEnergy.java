package com.enklave.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Timer;
import com.enklave.game.FontLabel.Font;
import com.enklave.game.Profile.InformationProfile;

public class ProgressBarEnergy {
    private static ProgressBarEnergy ourInstance = new ProgressBarEnergy();
    private final ProgressBar barExperience,bar;
    private final Group gr;
    private final Label username;
    private InformationProfile infoProfile;
    private MapsScreen mapsScreen;

    public void setMapsScreen(MapsScreen mapsScreen) {
        this.mapsScreen = mapsScreen;
    }

    public static ProgressBarEnergy getInstance() {
        return ourInstance;
    }

    private ProgressBarEnergy() {
        infoProfile = InformationProfile.getInstance();
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(1,(int)(Gdx.graphics.getHeight()*0.0175), Pixmap.Format.RGBA8888);
        pixmap.setColor(0.011764706f, 0.850980392f, 0.992156863f, 1);
        pixmap.fill();
        skin.add("blue", new Texture(pixmap));
        pixmap = new Pixmap(1,(int)(Gdx.graphics.getHeight()*0.008), Pixmap.Format.RGBA8888);
        pixmap.setColor(0.968627451f, 0.792156863f, 0.098039216f, 1);
        pixmap.fill();
        skin.add("yellow", new Texture(pixmap));
        pixmap = new Pixmap((int)(Gdx.graphics.getHeight()*0.1),(int)(Gdx.graphics.getHeight()*0.008), Pixmap.Format.RGBA8888);
        pixmap.setColor(0.250980392f, 0.235294118f, 0.231372549f, 1);
        pixmap.fill();
        skin.add("gray", new Texture(pixmap));
        pixmap = new Pixmap((int)(Gdx.graphics.getHeight()*0.1),(int)(Gdx.graphics.getHeight()*0.0175), Pixmap.Format.RGBA8888);
        pixmap.setColor(0.250980392f, 0.235294118f, 0.231372549f, 1);
        pixmap.fill();
        skin.add("gray1", new Texture(pixmap));
        ProgressBar.ProgressBarStyle barStyle = new ProgressBar.ProgressBarStyle(skin.newDrawable("gray1", Color.WHITE), skin.newDrawable("blue",Color.WHITE));
        barStyle.knobBefore = barStyle.knob;
        bar = new ProgressBar(0, infoProfile.getDateUserGame().getEnergyLevel(), 1f, false, barStyle);
        bar.setSize(Gdx.graphics.getWidth() * 0.73f, Gdx.graphics.getHeight() * 0.15625f);
        bar.setPosition((Gdx.graphics.getWidth() / 1.8f - bar.getWidth() / 2), Gdx.graphics.getHeight() * 0.87f);
        bar.setAnimateDuration(5);
        bar.setValue((float) infoProfile.getDateUserGame().getEnergy());
        ProgressBar.ProgressBarStyle barStyle1 = new ProgressBar.ProgressBarStyle(skin.newDrawable("gray",Color.WHITE),skin.newDrawable("yellow",Color.WHITE));
        barStyle1.knobBefore = barStyle1.knob;
        barExperience = new ProgressBar(0, (float) infoProfile.getDateUserGame().getExperienceLevel(), 1f, false, barStyle1);
        barExperience.setSize(Gdx.graphics.getWidth() * 0.73f, Gdx.graphics.getHeight() * 0.015625f);
        barExperience.setPosition((Gdx.graphics.getWidth() / 1.8f - barExperience.getWidth() / 2), Gdx.graphics.getHeight() * 0.92f);
        barExperience.setAnimateDuration(5);
        barExperience.setValue((float) infoProfile.getDateUserGame().getExperience());
        username = new Label("L "+infoProfile.getDateUserGame().getLevel()+" "+infoProfile.getDateUser().getFristName(), new Label.LabelStyle(Font.getFont((int) (Gdx.graphics.getHeight() * 0.035f)), new Color(1.0f, 0.7137f, 0.2196f, 1.0f)));
        username.setSize(Gdx.graphics.getWidth() * 0.55f, Gdx.graphics.getHeight() * 0.05f);
        username.setPosition((Gdx.graphics.getWidth() / 2.1f - username.getWidth() / 2f), Gdx.graphics.getHeight() * 0.95f);
        gr = new Group();
        gr.addActor(bar);
        gr.addActor(barExperience);
        gr.addActor(username);
        pixmap = new Pixmap(0,0, Pixmap.Format.RGBA8888);
        pixmap.dispose();
        skin = new Skin();
        skin.dispose();
    }

    public void FadeIn(){
        gr.clearActions();
        MoveToAction in = new MoveToAction();
        in.setPosition(0.0f, 0.0f);
        in.setDuration(2f);
        gr.addAction(in);
    }

    public void Fadeout(){
        MoveToAction out = new MoveToAction();
        out.setPosition(0.0f, Gdx.graphics.getHeight() * 0.1f);
        out.setDuration(2f);
        gr.addAction(new SequenceAction(new DelayAction(7f), out));
    }

    public void addGrouptoStage(Stage stage){
        stage.addActor(gr);
    }

    public void clickProgressBar(int screenX,int screenY){
        if((screenX>barExperience.getX()) && (screenY <(Gdx.graphics.getHeight()-barExperience.getY())))
        {
            FadeIn();
            gr.addAction(new DelayAction(5f));
            Fadeout();
        }
    }

    public void downenergy(int value){
        bar.act(Gdx.graphics.getDeltaTime());
        infoProfile.getDateUserGame().setEnergy(infoProfile.getDateUserGame().getEnergy() - value);
        FadeIn();
        new Timer().schedule(new Timer.Task() {
            @Override
            public void run() {
                bar.setAnimateDuration(3);
                bar.setValue((float) infoProfile.getDateUserGame().getEnergy());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mapsScreen.startTimer();
            }
        }, 1000);
    }

//    public void usingCellEnergy(float value){
//        infoProfile.setEnergy(infoProfile.getEnergy()+value);
//        FadeIn();
//        bar.setAnimateDuration(3.0f);
//        bar.setValue((float) infoProfile.getEnergy());
//        bar.act(Gdx.graphics.getDeltaTime());
//    }

    public void RegenerationEnergy(){
        FadeIn();
        bar.act(Gdx.graphics.getDeltaTime());
        bar.setAnimateDuration(1f);
        bar.setValue((float) infoProfile.getDateUserGame().getEnergy());
        bar.act(Gdx.graphics.getDeltaTime());
        //bar.addAction(new DelayAction(1));
    }

    public void update() {
        bar.act(Gdx.graphics.getDeltaTime());
        infoProfile.getDateUserGame().setEnergy(infoProfile.getDateUserGame().getEnergy());
        username.setText("L "+infoProfile.getDateUserGame().getLevel()+" "+infoProfile.getDateUser().getFristName());
        bar.setValue(infoProfile.getDateUserGame().getEnergy() - infoProfile.getValueenergyuse());
        FadeIn();
        new Timer().schedule(new Timer.Task() {
            @Override
            public void run() {
                bar.setAnimateDuration(3);
                bar.setValue((float) infoProfile.getDateUserGame().getEnergy());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(infoProfile.getDateUserGame().getEnklaveCombatId() == -1)
                    mapsScreen.startTimer();
            }
        } ,1);
    }

    public void updateEnergyCombat(){
        bar.act(Gdx.graphics.getDeltaTime());
        bar.setAnimateDuration(1);
        bar.setValue((float) infoProfile.getDateUserGame().getEnergy());
//        bar.act(Gdx.graphics.getDeltaTime());
    }

    public void updateVisual(){
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(1,(int)(Gdx.graphics.getHeight()*0.0175), Pixmap.Format.RGBA8888);
        switch (infoProfile.getDateUserGame().getFaction()){
            case 1:{
                pixmap.setColor(1, 0f, 0f, 1);
                break;
            }
            case 2:{
                pixmap.setColor(0f, 0.831f, 0.969f,1f);
                break;
            }
            case 3:{
                pixmap.setColor(0.129f, 0.996f, 0.29f,1);
                break;
            }
        }
        pixmap.fill();
        skin.add("blue", new Texture(pixmap));
        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle(bar.getStyle().background,skin.newDrawable("blue",Color.WHITE));
        style.knobBefore = style.knob;
        bar.setStyle(style);
    }

    public void updateCombat(){
        bar.act(Gdx.graphics.getDeltaTime());
        bar.setValue(infoProfile.getDateUserGame().getEnergy() - infoProfile.getValueenergyuse());
    }
}
