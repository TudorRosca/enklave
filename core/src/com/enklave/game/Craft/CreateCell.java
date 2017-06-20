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

public class CreateCell {
    private String url = "http://enklave-1720445391.us-west-2.elb.amazonaws.com";
    private String prof = "/crafting/cell/build/";
    private InformationProfile informationProfile;
    private QueueDisplay queueDisplay = QueueDisplay.getInstance();
    public CreateCell() {
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
        }
        queue.dateStartLastAction = System.currentTimeMillis() + (informationProfile.getDateCell().getCellRateTime() * 1000);
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                JSONObject json = new JSONObject(httpResponse.getResultAsString());
                if (httpResponse.getStatus().getStatusCode() < 300) {
                    if (json.has("duration")) {
                        new Timer().schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                informationProfile.getDateCell().setCellNumber(informationProfile.getDateCell().getCellNumber() + 1);
                                queueDisplay.labelfinishaction.setText("Cell");
                                queueDisplay.labelcurrentaction.setText("");
                                queueDisplay.labelnextaction.setText("");
                                game.screenCrafting.updateDisplay();
                                Gdx.app.log("number cell", "ga " + informationProfile.getDateBrick().getNumber());
                                if(!queue.forcestopcrafting)
                                    queue.startThread();
                            }
                        }, informationProfile.getDateCell().getCellRateTime());
                    } else {
                        Gdx.app.log("response error create cell " + httpResponse.getStatus().getStatusCode(), " " + json);
//                        game.screenCrafting.createDialog(json.getString("detail"));
                        updateprofile(1);
                        queue.clearQueue();
                        queueDisplay.labelfinishaction.setText("");
                        queueDisplay.labelcurrentaction.setText("cell error");
                        queueDisplay.labelnextaction.setText("");
                    }
                } else {
                    Gdx.app.log("response error create cell " + httpResponse.getStatus().getStatusCode(), " " + json);
//                    game.screenCrafting.createDialog("Error create Cell!");
                    updateprofile(1);
                    queue.clearQueue();
                    queueDisplay.labelfinishaction.setText("");
                    queueDisplay.labelcurrentaction.setText("cell error");
                    queueDisplay.labelnextaction.setText("");
                }
            }

            @Override
            public void failed(Throwable t) {

            }

            @Override
            public void cancelled() {

            }
        });
        new Timer().scheduleTask(new Timer.Task() {
            @Override
            public void run() {
//                if(!queue.forcestopcrafting)
//                    queue.startThread();
            }
        },informationProfile.getDateCell().getCellRateTime() + 0.5f);
    }
    public void updateprofile(float v){
        informationProfile.setValuescrapuse((int) (informationProfile.getValuescrapuse() + (informationProfile.getDateCell().getCellRateScrap()*v)));
        informationProfile.setValueenergyuse((int) (informationProfile.getValueenergyuse() + (informationProfile.getDateCell().getCellRateEnergy()*v)));
        informationProfile.getDateUserGame().setScrap((int) (informationProfile.getDateUserGame().getScrap() + (informationProfile.getDateCell().getCellRateScrap()*v)));
        informationProfile.getDateUserGame().setEnergy((int) (informationProfile.getDateUserGame().getEnergy() + (informationProfile.getDateCell().getCellRateEnergy()*v)));
    }
}
