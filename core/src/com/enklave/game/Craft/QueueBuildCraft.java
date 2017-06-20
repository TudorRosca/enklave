package com.enklave.game.Craft;

import com.enklave.game.Enklave.DeployBricks;
import com.enklave.game.GameManager;
import com.enklave.game.Profile.InformationProfile;
import com.enklave.game.Screens.QueueDisplay;
import com.badlogic.gdx.Gdx;

import java.util.LinkedList;

/**
 * Created by adrian on 10.05.2016.
 */
public class QueueBuildCraft {
    public enum QueueTerms{
        Deploy,Brick,Cell
    };
    private QueueDisplay queueDisplay;

    public boolean craftOn = false,deployOn = false,forcestopcrafting = false,forcestopdeploy = false,actionOn = false;
    public long dateStartLastAction = System.currentTimeMillis() - 100000;

    private int IdEnk = -1;

    public int getIdEnk() {
        return IdEnk;
    }

    public void setIdEnk(int idEnk) {
        IdEnk = idEnk;
    }

    private GameManager gameManager;

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    private static QueueBuildCraft ourInstance = new QueueBuildCraft();

    public static QueueBuildCraft getInstance() {
        return ourInstance;
    }

    private QueueBuildCraft() {
        queue = new LinkedList<QueueTerms>();
    }

    private LinkedList<QueueTerms> queue ;

    public void addDeploy(){
        queue.addLast(QueueTerms.Deploy);
    }

    public void addBrick(){
        queue.addLast(QueueTerms.Brick);
    }

    public void addCell(){
        queue.addLast(QueueTerms.Cell);
    }

    public QueueTerms getElement(){
        return queue.removeFirst();
    }

    public boolean isEmpty(){
        return  queue.isEmpty();
    }

    public void startThread(){
        if(!queue.isEmpty()){
            switch (queue.getFirst()){
                case Deploy:{
                    queueDisplay.labelcurrentaction.setText(QueueTerms.Deploy.name());
                    if(queue.size()>1){
                        queueDisplay.labelnextaction.setText(queue.get(1).name());
                    }else{
                        queueDisplay.labelnextaction.setText("");
                    }
                    new DeployBricks().makeRequest(gameManager,IdEnk,this);
                    break;
                }
                case Brick:{
                    queueDisplay.labelcurrentaction.setText(QueueTerms.Brick.name());
                    if(queue.size()>1){
                        queueDisplay.labelnextaction.setText(queue.get(1).name());
                    }else{
                        queueDisplay.labelnextaction.setText("");
                    }
                    new CreateBricks().makeRequest(gameManager,this);
                    break;
                }
                case Cell:{
                    queueDisplay.labelcurrentaction.setText(QueueTerms.Cell.name());
                    if(queue.size()>1){
                        queueDisplay.labelnextaction.setText(queue.get(1).name());
                    }else{
                        queueDisplay.labelnextaction.setText("");
                    }
                    new CreateCell().makeRequest(gameManager,this);
                    break;
                }
            }
        }else{
            finishAllActions();
        }
    }

    private void finishAllActions() {
        deployOn = false;
        craftOn = false;
        forcestopcrafting = false;
        forcestopdeploy = false;
        actionOn = false;
    }

    public void showqueue(){
        for(int i=0;i<queue.size();i++){
            Gdx.app.log("queue "+i,"este  "+queue.get(i));
        }
    }

    public void clearQueue(){
        queue.clear();
        refreshInformation();
    }

    public void refreshInformation(){
        InformationProfile prof =InformationProfile.getInstance();
        prof.setValueenergyuse(0);
        prof.setValuescrapuse(0);
        prof.getDateBrick().setNumberBrickUsage(0);
    }

    public void setQueueDisplay(QueueDisplay queueDisplay) {
        this.queueDisplay = queueDisplay;
    }

    public void ShowResursed(){
        InformationProfile prof = InformationProfile.getInstance();
        Gdx.app.log("scrap : "+prof.getDateUserGame().getScrap()," "+prof.getValuescrapuse());
        Gdx.app.log("bricks : "+prof.getDateBrick().getNumber() , " "+prof.getDateBrick().getNumberBrickUsage());
        Gdx.app.log("cell :" + prof.getDateCell().getCellNumber()," empty ");
        Gdx.app.log("energy : "+ prof.getDateUserGame().getEnergy()," "+prof.getValueenergyuse());
    }
}
