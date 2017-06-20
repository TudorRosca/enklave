package com.enklave.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.enklave.game.Combat.ListOfDefenders;
import com.enklave.game.Enklave.DescEnklave.InformationEnklave;
import com.enklave.game.FontLabel.Font;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.Profile.InformationProfile;
import com.enklave.game.Utils.NameFiles;

import java.util.Arrays;


public class DrawDefenders {
    private Group[] arrayDefenders;
    Group groupDefenders;
    private Integer[] centerPlayer;
    private int defendSelect = -1;
    private final float distanceReference = Gdx.graphics.getHeight()*0.14f;
    private Vector2 p0,p1,p2,p4,p5,p6,d01,d12,d45,d56;
    private ManagerAssets managerAssets;
    private int WIDTH = Gdx.graphics.getWidth(),HEIGHT = Gdx.graphics.getHeight();
    private ListOfDefenders listOfDefenders;
    private BitmapFont bt = Font.getFont((int) (Gdx.graphics.getHeight() * 0.02));

    public DrawDefenders() {
        groupDefenders = new Group();
        managerAssets = ManagerAssets.getInstance();
        listOfDefenders = ListOfDefenders.getInstance();
        p0 = new Vector2(WIDTH * 0.16f, -HEIGHT*0.03f);
        p1 = new Vector2(WIDTH * 0.06f, -HEIGHT * 0.12f);
        p2 = new Vector2(0,-HEIGHT*0.14f);
        p4 = new Vector2(0,HEIGHT*0.14f);
        p5 = new Vector2(WIDTH * 0.06f, HEIGHT*0.32f);
        p6 = new Vector2(WIDTH * 0.16f, HEIGHT*0.53f);

        d01 = new Vector2(p0.x-p1.x,p1.y-p0.y);
        d12 = new Vector2(p1.x,p2.y-p1.y);
        d45 = new Vector2(p5.x,p5.y-p4.y);
        d56 = new Vector2(p6.x-p5.x,p6.y-p5.y);
        centerPlayer = new Integer[7];
        for(int i=0;i<7;i++){
            centerPlayer[i] = -1;
        }
        arrayDefenders = new Group[listOfDefenders.size()];
        for(int i=0;i<listOfDefenders.size();i++){
            arrayDefenders[i] = drawoneDefenders(NameFiles.framePlayers,listOfDefenders.getPlayer(i).name,listOfDefenders.getPlayer(i).faction,NameFiles.imgplayerBlue,listOfDefenders.getPlayer(i).energy,listOfDefenders.getPlayer(i).energyMax);
        }
        for (Group arrayDefender : arrayDefenders) {
            groupDefenders.addActor(arrayDefender);
        }
        ordonateDefenders(arrayDefenders);
        modifyview(arrayDefenders);
    }

    private Group drawoneDefenders(String typeFrame,String name,Color type,String photo,float energy,float energymax){
        Group gr = new Group();
        Texture txt = managerAssets.getAssetsCombat().getTexture(typeFrame);
        Vector2 crop = Scaling.fit.apply(txt.getWidth(), txt.getHeight(), WIDTH, HEIGHT);
        Image frame = new Image(new TextureRegion(txt));
        frame.setName("frame");
        frame.setSize(crop.x * 0.18f, crop.y * 0.2f);
        frame.setPosition(WIDTH * 0.025f, HEIGHT / 2.3f);
        gr.addActor(frame);
        if(InformationProfile.getInstance().getDateUserGame().getFaction() == InformationEnklave.getInstance().getFaction())
            txt = managerAssets.getAssetsCombat().getTexture(NameFiles.targetRecharge);
        else
            txt = managerAssets.getAssetsCombat().getTexture(NameFiles.target);
        crop = Scaling.fit.apply(txt.getWidth(), txt.getHeight(), WIDTH, HEIGHT);
        Image frameselect = new Image(new TextureRegion(txt));
        frameselect.setName("frameselect");
        frameselect.toFront();
        frameselect.setSize(crop.x * 0.18f, crop.y * 0.2f);
        frameselect.setPosition(WIDTH * 0.025f, HEIGHT / 2.3f);
        frameselect.setVisible(false);
        gr.addActor(frameselect);
        Label labelName = new Label(name.substring(0,name.length()>9 ? 9 : name.length()),new Label.LabelStyle(bt,type));
        labelName.setAlignment(Align.center);
        labelName.setSize(WIDTH * 0.18f, HEIGHT * 0.02f);
        labelName.setPosition(frame.getX(), frame.getY() + frame.getHeight() * 0.25f);
        gr.addActor(labelName);
        txt = managerAssets.getAssetsCombat().getTexture(photo);
        crop = Scaling.fit.apply(txt.getWidth(), txt.getHeight(), WIDTH, HEIGHT);
        Image profile = new Image(new TextureRegion(txt));
//        profile.setColor(Color.BLUE);
        profile.setSize(crop.x*0.07f, crop.y*0.07f);
        profile.setPosition(frame.getRight() - frame.getWidth() / 2 - profile.getWidth() / 2, frame.getY() + frame.getHeight() * 0.42f);
        gr.addActor(profile);
        Skin skin = new Skin();
        skin.add("white", new TextureRegion(managerAssets.getAssetsCombat().getTexture(NameFiles.barLifeWhite),0,0,(int)(WIDTH*0.004),(int)(WIDTH*0.014)));
        ProgressBar.ProgressBarStyle barStyle = new ProgressBar.ProgressBarStyle(skin.newDrawable("white", Color.WHITE), skin.newDrawable("white",type));
        barStyle.knobBefore = barStyle.knob;
        ProgressBar bar = new ProgressBar(0, energymax, 1, false, barStyle);
        bar.setSize(WIDTH * 0.14f, HEIGHT * 0.012f);
        bar.setPosition(frame.getX()+frame.getWidth()*0.1f, frame.getY()+frame.getHeight()*0.07f);
        bar.setValue(energy);
        gr.addActor(bar);
        return gr;
    }

    public void updateList(){
        listOfDefenders = ListOfDefenders.getInstance();
        arrayDefenders = new Group[listOfDefenders.size()];
        for(int i =0;i<listOfDefenders.size();i++){
            arrayDefenders[i] = drawoneDefenders(NameFiles.framePlayers, listOfDefenders.getPlayer(i).name, listOfDefenders.getPlayer(i).faction, NameFiles.imgplayerGreen, listOfDefenders.getPlayer(i).energy,listOfDefenders.getPlayer(i).energyMax);
        }
        for (Group arrayAttacher : arrayDefenders) {
            groupDefenders.addActor(arrayAttacher);
        }
        ordonateDefenders(arrayDefenders);
        modifyview(arrayDefenders);
    }

    public void removeGroup(){
        for(int i=0;i<arrayDefenders.length;i++){
            arrayDefenders[i].remove();
        }
    }

    private void ordonateDefenders(Group[] arrayGroup) {
        if(arrayGroup.length == 1){
            centerPlayer[3] = 0;
        }
        if(arrayGroup.length > 1 && arrayGroup.length <4){
            for(int i = 0;i < arrayGroup.length;i++){
                switch (i) {
                    case 0:{
                        move2(arrayGroup[i],0,0.1f);
                        centerPlayer[2] = i;
                        break;
                    }
                    case 1:{
                        centerPlayer[3] = i;
                        break;
                    }
                    case 2:{
                        move4( arrayGroup[i],0,0.1f);
                        centerPlayer[4] = i;
                        break;
                    }
                }
            }

        }
        if(arrayGroup.length > 3 && arrayGroup.length <6){
            for(int i = 0;i < arrayGroup.length;i++){
                switch (i) {
                    case 0:{
                        move1(arrayGroup[i],0,0.1f);
                        centerPlayer[1] = i;
                        break;
                    }
                    case 1:{
                        move2( arrayGroup[i],0,0.1f);
                        centerPlayer[2] = i;
                        break;
                    }
                    case 2:{
                        centerPlayer[3] = i;
                        break;
                    }
                    case 3:{
                        move4(arrayGroup[i],0,0.1f);
                        centerPlayer[4] = i;
                        break;
                    }
                    case 4:{
                        move5(arrayGroup[i],0,0.1f);
                        centerPlayer[5] = i;
                        break;
                    }
                }
            }
        }
        if(arrayGroup.length>5){
            for(int i=0;i<arrayGroup.length;i++){
                switch (i){
                    case 0:{
                        centerPlayer[i] = i;
                        move0(arrayGroup[i],0,0.1f);
                        break;
                    }
                    case 1:{
                        centerPlayer[i] = i;
                        move1(arrayGroup[i],0,0.1f);
                        break;
                    }
                    case 2:{
                        centerPlayer[i] = i;
                        move2(arrayGroup[i],0,0.1f);
                        break;
                    }
                    case 3:{
                        centerPlayer[i] = i;
                        break;
                    }
                    case 4:{
                        centerPlayer[i] = i;
                        move4(arrayGroup[i],0,0.1f);
                        break;
                    }
                    case 5:{
                        centerPlayer[i] = i;
                        move5(arrayGroup[i],0,0.1f);
                        break;
                    }
                    case 6:{
                        centerPlayer[i] = i;
                        move6(arrayGroup[i],0,0.1f);
                        break;
                    }
                    default:{
                        arrayGroup[i].setVisible(false);
                        break;
                    }
                }
            }
            arrayGroup[1].toFront();
            arrayGroup[5].toFront();
            arrayGroup[2].toFront();
            arrayGroup[4].toFront();
            arrayGroup[3].toFront();
        }
    }

    private void modifyview(Group[] array){
        if(centerPlayer[0] != -1)
            array[centerPlayer[0]].toFront();
        if(centerPlayer[6]!= -1)
            array[centerPlayer[6]].toFront();
        if(centerPlayer[1] != -1)
            array[centerPlayer[1]].toFront();
        if(centerPlayer[5] != -1)
            array[centerPlayer[5]].toFront();
        if(centerPlayer[2] != -1)
            array[centerPlayer[2]].toFront();
        if(centerPlayer[4] != -1)
            array[centerPlayer[4]].toFront();
        if(centerPlayer[3] != -1)
            array[centerPlayer[3]].toFront();
    }

    public Group getGroupDefenders() {
        return groupDefenders;
    }

    public Group[] getArrayDefenders() {
        return arrayDefenders;
    }

    public float getXmax(){
        float xmax=-1;
        if(arrayDefenders.length>0)
            xmax = p6.x + arrayDefenders[0].findActor("frame").getWidth()*0.5f;
        return xmax;
    }

    public float getYmin(){
        if(arrayDefenders.length>0)
            return arrayDefenders[0].findActor("frame").localToStageCoordinates(new Vector2(0,0)).y;
        else
            return -1;
    }

    public float getYmax(){
        float ymax=-1;
        if(arrayDefenders.length>7){
            ymax = arrayDefenders[6].findActor("frame").localToStageCoordinates(new Vector2(0,0)).y;
            ymax = ymax + arrayDefenders[6].findActor("frame").getWidth()*0.5f;
        }else{
            if(arrayDefenders.length>0)
                ymax = arrayDefenders[arrayDefenders.length-1].findActor("frame").localToStageCoordinates(new Vector2(0,0)).y;
            if(arrayDefenders.length<4 && arrayDefenders.length > 0)
                ymax = ymax + arrayDefenders[arrayDefenders.length-1].findActor("frame").getHeight();
            else if(arrayDefenders.length>3)
                ymax = ymax + arrayDefenders[arrayDefenders.length-1].findActor("frame").getHeight()*0.8f;
            else if(arrayDefenders.length>5)
                ymax = ymax + arrayDefenders[arrayDefenders.length-1].findActor("frame").getHeight()*0.5f;
        }
        return ymax;
    }

    public void changeFrame (int poz) {
        listOfDefenders.isSelectedItem = false;
        if (arrayDefenders.length <= 7) {
            if(centerPlayer[poz+2] != -1) {
                deselect();
                arrayDefenders[centerPlayer[poz + 2]].findActor("frameselect").setVisible(true);
                listOfDefenders.isSelectedItem = true;
                if(InformationProfile.getInstance().getDateUserGame().getFaction() != InformationEnklave.getInstance().getFaction())
                    InformationEnklave.getInstance().selectTargetEnklave = listOfDefenders.getPlayer(centerPlayer[poz + 2]).id;
            }
        } else {
            deselect();
            arrayDefenders[poz + 2].findActor("frameselect").setVisible(true);
            listOfDefenders.isSelectedItem = true;
            if(InformationProfile.getInstance().getDateUserGame().getFaction() != InformationEnklave.getInstance().getFaction())
                InformationEnklave.getInstance().selectTargetEnklave = listOfDefenders.getPlayer(poz+2).id;
        }
    }

    public void deselect(){
        for(int i=0;i<arrayDefenders.length;i++)
            arrayDefenders[i].findActor("frameselect").setVisible(false);

    }

    //translate slow
    public float translateslow(Group[] array, float distance,boolean stop) {
        float p = distance / distanceReference;
        if (array.length > 1 && array.length <= 7) {
            if((array.length - 1 != centerPlayer[3] || distance <0) && (distance > 0  || centerPlayer[3] != 0)) {
                if (Math.abs(p) < 1) {
                    for (int i = 0; i < array.length; ++i) {
                        int x = Arrays.asList(centerPlayer).indexOf(i);
                        if (x != -1) {
                            controllermoveslow(array[i], x, p, 0);
                        }
                    }
                } else if (!stop) {
                    distance = 0;
                    for (int i = 0; i < array.length; ++i) {
                        int x = Arrays.asList(centerPlayer).indexOf(i);
                        if (x != -1) {
                            controllermoveslow(array[i], x, p, 0.02f);
                        }
                    }
                    reorderArray(array, p);
                }
                if (stop) {
                    if (Math.abs(p) < 0.5) {
                        revert(array, p);
                    } else {
                        continuetranslate(array, p);
                    }
                    reorderArray(array, p);
                    modifyview(array);
                }
                modifyview(array);
            }
        } else if (array.length > 7) {
            if (Math.abs(p) < 1){
                for (int i = 0; i < 7; i++) {
                    controllermoveslow(array[i], i,p,0);
                }
            }else if(!stop){
                distance = 0;
                for (int i = 0; i < 7; i++) {
                    controllermoveslow(array[i], i,p,0);
                }
                reorderArrayFull(array,p);
            }
            if(stop){
                if (Math.abs(p) < 0.5) {
                    revert(array, p);
                } else {
                    continuetranslate(array, p);
                }
                reorderArrayFull(array,p);
            }
        }
        return distance;
    }

    private void reorderArrayFull(Group[] array,float procent) {
        if(procent>0.5){
            Group aux = array[0];
            System.arraycopy(array, 1, array, 0, array.length - 1);
            array[array.length - 1] = aux;
            listOfDefenders.shiftLeft();
        }else if(procent<-0.5){
            Group aux = array[array.length - 1];
            System.arraycopy(array, 0, array, 1, array.length - 1);
            array[0] = aux;
            listOfDefenders.shiftright();
        }
        array[1].toFront();
        array[5].toFront();

        array[2].toFront();
        array[4].toFront();
        array[3].toFront();
        if (defendSelect == 0)
            defendSelect = array.length - 1;
        else
            defendSelect--;
    }

    private void reorderArray(Group[] array, float procent) {
        if (procent > 0.5) {
            System.arraycopy(centerPlayer, 1, centerPlayer, 0, 6);
            if (centerPlayer[6] < array.length - 1 && centerPlayer[6] != -1) {
                centerPlayer[6] = centerPlayer[5] + 1;
            } else {
                centerPlayer[6] = -1;
            }
        } else if(procent < -0.5){
            System.arraycopy(centerPlayer, 0, centerPlayer, 1, 6);
            if (centerPlayer[1] > 0) {
                centerPlayer[0] = centerPlayer[1] - 1;
            } else {
                centerPlayer[0] = -1;
            }
        }
    }

    private void continuetranslate(Group[] array, float procent) {
        for (int i = 0; i < array.length; ++i) {
            int x = Arrays.asList(centerPlayer).indexOf(i);
            if (x != -1) {
                controllermoveslow(array[i], x, procent,0.2f);
            }
        }
    }

    private void revert(Group[] array, float procent) {
        for (int i = 0; i < array.length; ++i) {
            int x = Arrays.asList(centerPlayer).indexOf(i);
            if (x != -1) {
                controllermoveslow(array[i], x, procent,0.2f);
            }
        }
    }

    private void controllermoveslow(Group gr, int move,float procent,float time) {
        switch (move) {
            case 0:{
                move0(gr,procent,time);
                break;
            }
            case 1: {
                move1(gr,procent,time);
                break;
            }
            case 2: {
                move2(gr, procent,time);
                break;
            }
            case 3: {
                move3(gr,procent,time);
                break;
            }
            case 4: {
                move4(gr,procent,time);
                break;
            }
            case 5: {
                move5(gr, procent, time);
                break;
            }
            case 6: {
                move6(gr,procent,time);
                break;
            }
        }
    }

    private void move0(Group gr, float procent, float time) {
        MoveToAction move = new MoveToAction();
        move.setDuration(time);
        ScaleToAction scale = new ScaleToAction();
        scale.setDuration(time);
        if(time == 0) {
            gr.clearActions();
            gr.clearActions();
            if (procent < 0) {
                scale.setScale(0.5f + (Math.abs(procent) * 0.3f));
                move.setPosition(p0.x + procent * d01.x, p0.y + (1 - (procent * d01.y)));
                addfirst();
            }else{
                scale.setScale(0.5f);
                move.setPosition(p0.x,p0.y);
            }
        }else{
            if(Math.abs(procent)<=0.5){
                scale.setScale(0.5f);
                move.setPosition(p0.x,p0.y);
            }else if(procent < -0.5) {
                scale.setScale(0.8f);
                move.setPosition(p1.x, p1.y);
                addfirst();
            }else{
                scale.setScale(0.5f);
                move.setPosition(p0.x,p0.y);
            }
        }
        gr.addAction(scale);
        gr.addAction(move);
    }

    private void move1(Group gr, float procent, float time) {
        MoveToAction move = new MoveToAction();
        move.setDuration(time);
        ScaleToAction scale = new ScaleToAction();
        scale.setDuration(time);
        if(time == 0) {
            gr.clearActions();
            gr.clearActions();
            if (procent > 0) {
                scale.setScale(0.8f - (Math.abs(procent) * 0.3f));
                move.setPosition(p1.x + (procent * d01.x), p1.y + (1 - (procent * d01.y)));
            } else {
                scale.setScale(0.8f + (Math.abs(procent) * 0.2f));
                move.setPosition(p1.x + (procent * d12.x), p1.y + (1 - (procent * d12.y)));
            }
        }else{
            if(Math.abs(procent)<=0.5){
                scale.setScale(0.8f);
                move.setPosition(p1.x,p1.y);
            }else if(procent > 0.5){
                scale.setScale(0.5f);
                move.setPosition(p0.x, p0.y);
            }else{
                scale.setScale(1);
                move.setPosition(p2.x,p2.y);
            }
        }
        gr.addAction(scale);
        gr.addAction(move);
    }

    private void move2(Group gr, float procent, float time) {
        MoveToAction move = new MoveToAction();
        move.setDuration(time);
        ScaleToAction scale = new ScaleToAction();
        scale.setDuration(time);
        if(time == 0) {
            if (procent > 0) {
                scale.setScale(1 - (Math.abs(procent) * 0.2f));
                move.setPosition(p2.x + procent * d12.x, p2.y + (1 - (procent * d12.y)));
            } else {
                scale.setScale(1);
                move.setPosition(0, p2.y - procent * distanceReference);
            }
        }else{
            if(Math.abs(procent)<=0.5){
                scale.setScale(1);
                move.setPosition(p2.x,p2.y);
            }else if(procent>0.5){
                scale.setScale(0.8f);
                move.setPosition(p1.x,p1.y);
            }else{
                scale.setScale(1);
                move.setPosition(0,0);
            }
        }
        gr.addAction(scale);
        gr.addAction(move);
    }

    private void move3(Group gr, float procent, float time) {
        MoveToAction move = new MoveToAction();
        move.setDuration(time);
        if(time==0) {
            move.setPosition(0, -procent * distanceReference);
        }else {
            if(Math.abs(procent)<=0.5) {
                move.setPosition(0, 0);
            }else if(procent>0.5){
                move.setPosition(p2.x,p2.y);
            }else{
                move.setPosition(p4.x,p4.y);
            }
        }
        gr.addAction(move);
    }

    private void move4(Group gr, float procent, float time) {
        MoveToAction move = new MoveToAction();
        move.setDuration(time);
        ScaleToAction scale = new ScaleToAction();
        scale.setDuration(time);
        if (time ==0) {
            if (procent > 0) {
                scale.setScale(1);
                move.setPosition(0, p4.y - procent * distanceReference);
            } else {
                scale.setScale(1 - (Math.abs(procent) * 0.2f));
                move.setPosition(p4.x - procent * d45.x, p4.y + (1 - (procent * d45.y)));
            }
        }else{
            if(Math.abs(procent)<=0.5){
                scale.setScale(1);
                move.setPosition(p4.x,p4.y);
            }else if(procent>=0.5){
                scale.setScale(1);
                move.setPosition(0,0);
            }else{
                scale.setScale(0.8f);
                move.setPosition(p5.x,p5.y);
            }
        }
        gr.addAction(scale);
        gr.addAction(move);
    }

    private void move5(Group gr, float procent, float time) {
        MoveToAction move = new MoveToAction();
        move.setDuration(time);
        ScaleToAction scale = new ScaleToAction();
        scale.setDuration(time);
        if(time == 0) {
            gr.clearActions();
            gr.clearActions();
            if (procent > 0) {
                scale.setScale(0.8f + (Math.abs(procent) * 0.2f));
                move.setPosition(p5.x - (procent * d45.x), p5.y + (1 - (procent * d45.y)));
            } else {
                scale.setScale(0.8f - (Math.abs(procent) * 0.3f));
                move.setPosition(p5.x - (procent * d56.x),p5.y + (1-(procent*d56.y)));
            }
        }else{
            if(Math.abs(procent)<=0.5){
                scale.setScale(0.8f);
                move.setPosition(p5.x,p5.y);
            }else if(procent > 0.5){
                scale.setScale(1);
                move.setPosition(p4.x, p4.y);
            }else{
                scale.setScale(0.5f);
                move.setPosition(p6.x,p6.y);
            }
        }
        gr.addAction(scale);
        gr.addAction(move);
    }

    private void move6(Group gr, float procent, float time) {
        MoveToAction move = new MoveToAction();
        move.setDuration(time);
        ScaleToAction scale = new ScaleToAction();
        scale.setDuration(time);
        if(time == 0) {
            gr.clearActions();
            gr.clearActions();
            if (procent > 0) {
                scale.setScale(0.5f + (Math.abs(procent) * 0.3f));
                move.setPosition(p6.x - procent * d56.x, p6.y + (1 - (procent * d56.y)));
                addlast();
            }else{
                scale.setScale(0.5f);
                move.setPosition(p6.x,p6.y);
            }
        }else{
            if(Math.abs(procent)<=0.5){
                scale.setScale(0.5f);
                move.setPosition(p6.x,p6.y);
            }else if(procent > 0.5) {
                scale.setScale(0.8f);
                move.setPosition(p5.x, p5.y);
                addlast();
            }else{
                scale.setScale(0.5f);
                move.setPosition(p6.x,p6.y);
            }
        }
        gr.addAction(scale);
        gr.addAction(move);
    }

    private void addlast() {
        if(arrayDefenders.length>7) {
            arrayDefenders[7].setVisible(true);
            arrayDefenders[7].toBack();
            MoveToAction move = new MoveToAction();
            move.setDuration(0);
            move.setPosition(p6.x, p6.y);
            arrayDefenders[7].addAction(move);
            ScaleToAction scale = new ScaleToAction();
            scale.setDuration(0);
            scale.setScale(0.5f);
            arrayDefenders[7].addAction(scale);
        }
    }

    private void addfirst() {
        if(arrayDefenders.length>7) {
            arrayDefenders[arrayDefenders.length - 1].setVisible(true);
            arrayDefenders[arrayDefenders.length - 1].toBack();
            MoveToAction move = new MoveToAction();
            move.setDuration(0);
            move.setPosition(p0.x, p0.y);
            arrayDefenders[arrayDefenders.length - 1].addAction(move);
            ScaleToAction scale = new ScaleToAction();
            scale.setDuration(0);
            scale.setScale(0.5f);
            arrayDefenders[arrayDefenders.length - 1].addAction(scale);
        }
    }

    public ListOfDefenders getListOfDefenders() {
        return listOfDefenders;
    }
}
