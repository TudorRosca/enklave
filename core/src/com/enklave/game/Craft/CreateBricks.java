package com.enklave.game.Craft;

import com.enklave.game.GameManager;
import com.enklave.game.Profile.InformationProfile;
import com.enklave.game.Screens.QueueDisplay;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Timer;

import org.json.JSONObject;
//import java.util.Timer;
//import java.util.TimerTask;


public class CreateBricks {
    private String url = "http://enklave-1720445391.us-west-2.elb.amazonaws.com";
    private String prof = "/crafting/brick/build/";
    private InformationProfile informationProfile;
    private QueueDisplay queueDisplay = QueueDisplay.getInstance();
    public CreateBricks() {
        informationProfile = InformationProfile.getInstance();
    }
    public void makeRequest(final GameManager game, final QueueBuildCraft queue){
        Preferences pref = Gdx.app.getPreferences("informationLog");
        String acces = pref.getString("accesstoken");
        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.POST).url(url + prof).build();
        request.setHeader("Authorization", "Bearer " + acces);
        updateprofile(-1);
        queue.getElement();
        final long startTime = System.currentTimeMillis();
        try {
            Thread.sleep((System.currentTimeMillis() > queue.dateStartLastAction) ? 0 : (queue.dateStartLastAction - System.currentTimeMillis()));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Gdx.app.log("intra brick ","error");
        }
        queue.dateStartLastAction = System.currentTimeMillis() + (informationProfile.getDateBrick().getRateTime()*1000);
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                JSONObject json = new JSONObject(httpResponse.getResultAsString());
                if(httpResponse.getStatus().getStatusCode()<300){
                    if(json.has("duration")) {
                        new Timer().schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                informationProfile.getDateBrick().setNumber(informationProfile.getDateBrick().getNumber() + 1);
                                queueDisplay.labelfinishaction.setText("Brick");
                                queueDisplay.labelcurrentaction.setText("");
                                queueDisplay.labelnextaction.setText("");
                                game.screenCrafting.updateDisplay();
                                Gdx.app.log("number bricks", "ga " + informationProfile.getDateBrick().getNumber());
                                if(!queue.forcestopcrafting)
                                    queue.startThread();
                            }
                        },informationProfile.getDateBrick().getRateTime());
                    }else {
                        Gdx.app.log("response error create bricks "+httpResponse.getStatus().getStatusCode()," "+json);
//                        game.screenCrafting.createDialog("Error create brick "+json);
                        updateprofile(1);
                        queue.clearQueue();
                        queueDisplay.labelfinishaction.setText("");
                        queueDisplay.labelcurrentaction.setText("brick error");
                        queueDisplay.labelnextaction.setText("");
                    }
                }else{
//                    game.screenCrafting.createDialog("Error create brick " +json);
                    Gdx.app.log("response error create bricks "+httpResponse.getStatus().getStatusCode()," "+json);
                    updateprofile(1);
                    queue.clearQueue();
                    queueDisplay.labelfinishaction.setText("");
                    queueDisplay.labelcurrentaction.setText("brick error ");
                    queueDisplay.labelnextaction.setText("");
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("response create bricks","failed ");
            }

            @Override
            public void cancelled() {
                Gdx.app.log("response create bricks"," canceled");
            }
        });
        new Timer().scheduleTask(new Timer.Task() {
            @Override
            public void run() {
//                if(!queue.forcestopcrafting)
//                    queue.startThread();
            }
        },informationProfile.getDateBrick().getRateTime() + 0.5f);
    }
    public void updateprofile(float v){
        informationProfile.setValuescrapuse((int) (informationProfile.getValuescrapuse() + (informationProfile.getDateBrick().getRateScrap()*v)));
        informationProfile.setValueenergyuse((int) (informationProfile.getValueenergyuse() + (informationProfile.getDateBrick().getRateEnergy()*v)));
        informationProfile.getDateUserGame().setScrap((int) (informationProfile.getDateUserGame().getScrap() + (informationProfile.getDateBrick().getRateScrap()*v)));
        informationProfile.getDateUserGame().setEnergy((int) (informationProfile.getDateUserGame().getEnergy() + (informationProfile.getDateBrick().getRateEnergy()*v)));
    }
}
