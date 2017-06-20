package com.enklave.game.Combat.Request;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.enklave.game.Enklave.DescEnklave.InformationEnklave;
import com.enklave.game.Profile.InformationProfile;
import com.enklave.game.Requests.NearbyEnklave;

import org.json.JSONObject;

/**
 * Created by adrian on 13.06.2016.
 */
public class CombatUserCheck {
    private String url = "http://enklave-1720445391.us-west-2.elb.amazonaws.com/";
    private String prof = "/combat/user/status/";

    public CombatUserCheck() {

    }
    public void makeRequest(){
        final Preferences pref = Gdx.app.getPreferences("informationLog");
        final String acces = pref.getString("accesstoken");
        HttpRequestBuilder builder = new HttpRequestBuilder();
        final Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.GET).url(url + prof).build();
        request.setHeader("Authorization", "Bearer " + acces);
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                if(httpResponse.getStatus().getStatusCode()<300){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    new NearbyEnklave().makeRequestEnklave();
                                }
                            });
                        }
                    }).start();
                    String res = httpResponse.getResultAsString();
                    if(!res.contentEquals("\"user is not in combat\"")){
                        InformationProfile.getInstance().getDateUserGame().setInCombat(true);
                        JSONObject json = new JSONObject(res);
                        InformationEnklave.getInstance().setId(json.getInt("enklave_id"));
                        InformationEnklave.getInstance().setCombatId(json.getInt("combatant_id"));
                    }
                    Gdx.app.log("response check combat user ","correct is: "+res);
                }
            }

            @Override
            public void failed(Throwable t) {

            }

            @Override
            public void cancelled() {

            }
        });
    }
}