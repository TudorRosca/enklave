package com.enklave.game.Enklave;

import com.enklave.game.Craft.QueueBuildCraft;
import com.enklave.game.Enklave.DescEnklave.InformationEnklave;
import com.enklave.game.GameManager;
import com.enklave.game.Profile.InformationProfile;
import com.enklave.game.Screens.QueueDisplay;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Timer;

import org.json.JSONObject;

public class DeployBricks {
    private String url = "http://enklave-1720445391.us-west-2.elb.amazonaws.com/";
    private String join = "/crafting/brick/place/";
    private InformationProfile informationProfile;
    private QueueDisplay queueDisplay = QueueDisplay.getInstance();

    public DeployBricks() {
    }

    public void makeRequest(final GameManager game, int IdEnklave, final QueueBuildCraft queue){
        if(queueDisplay.checkProximity()) {
            final Preferences pref = Gdx.app.getPreferences("informationLog");
            String acces = pref.getString("accesstoken");
            JSONObject json = new JSONObject();
            json.accumulate("enklave_id", IdEnklave);
            HttpRequestBuilder builder = new HttpRequestBuilder();
            Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.POST).url(url + join).build();
            request.setHeader("Authorization", "Bearer " + acces);
            request.setHeader("Content-type", "application/json");
            request.setContent(json.toString());
            queue.getElement();
            updateprofile(-1);
            final long startTime = System.currentTimeMillis();
            try {
                Thread.sleep((System.currentTimeMillis() > queue.dateStartLastAction) ? 0 : (queue.dateStartLastAction - System.currentTimeMillis()));
            } catch (InterruptedException e) {
                e.printStackTrace();
                Gdx.app.log("intra ", "error");
            }
            queue.dateStartLastAction = System.currentTimeMillis() + (informationProfile.getDateBrick().getUsageTime() * 1000);
            Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
                @Override()
                public void handleHttpResponse(Net.HttpResponse httpResponse) {
                    JSONObject json = new JSONObject(httpResponse.getResultAsString());
                    if (httpResponse.getStatus().getStatusCode() < 300) {
                        if (json.has("duration")) {
                            new Timer().schedule(new Timer.Task() {
                                @Override
                                public void run() {
                                    InformationEnklave.getInstance().setBricks(InformationEnklave.getInstance().getBricks() + 1);
                                    queueDisplay.labelfinishaction.setText("Deploy");
                                    queueDisplay.labelcurrentaction.setText("");
                                    queueDisplay.labelnextaction.setText("");
                                    game.screenEnklave.updatelabel();
                                    game.screenCrafting.updateDisplay();
                                    Gdx.app.log("number bricks after deploy", "ga " + informationProfile.getDateBrick().getNumber());
                                    if (!queue.forcestopdeploy)
                                        queue.startThread();
                                }
                            }, informationProfile.getDateBrick().getUsageTime());
                        } else {
                            Gdx.app.log("response error deploy brick " + httpResponse.getStatus().getStatusCode(), " " + json);
//                        game.screenEnklave.createDialog(json.getString("detail"));
                            updateprofile(1);
                            queue.clearQueue();
                            queueDisplay.labelfinishaction.setText("");
                            queueDisplay.labelcurrentaction.setText("error deploy");
                            queueDisplay.labelnextaction.setText("");
                        }
                    } else {
                        Gdx.app.log("response error depkoy brick " + httpResponse.getStatus().getStatusCode(), " " + json);
//                    game.screenEnklave.createDialog(json.getString("detail"));
                        updateprofile(1);
                        queue.clearQueue();
                        queueDisplay.labelfinishaction.setText("");
                        queueDisplay.labelcurrentaction.setText("error deploy");
                        queueDisplay.labelnextaction.setText("");
                    }
                }

                @Override
                public void failed(Throwable t) {
                    Gdx.app.log("response :", "failed ");
                }

                @Override
                public void cancelled() {
                    Gdx.app.log("response :", "cancel ");
                }
            });
            new Timer().scheduleTask(new Timer.Task() {
                @Override
                public void run() {
//                if(!queue.forcestopdeploy)
//                    queue.startThread();
                }
            }, informationProfile.getDateBrick().getUsageTime() + 0.5f);
        }
    }
    public void updateprofile(float v){
        informationProfile = InformationProfile.getInstance();
        informationProfile.setValueenergyuse((int) (informationProfile.getValueenergyuse() + (informationProfile.getDateBrick().getUsageEnergy()*v)));
        informationProfile.getDateUserGame().setEnergy((int) (informationProfile.getDateUserGame().getEnergy() + (informationProfile.getDateBrick().getUsageEnergy()*v)));
        informationProfile.getDateBrick().setNumber((int) (informationProfile.getDateBrick().getNumber()+v));
        informationProfile.getDateBrick().setNumberBrickUsage((int) (informationProfile.getDateBrick().getNumberBrickUsage()+v));
    }
}
