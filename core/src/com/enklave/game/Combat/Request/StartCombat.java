package com.enklave.game.Combat.Request;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.enklave.game.Enklave.DescEnklave.InformationEnklave;
import com.enklave.game.Profile.InformationProfile;
import com.enklave.game.Screens.ScreenCombat;

import org.json.JSONObject;

/**
 * Created by adrian on 06.06.2016.
 */
public class StartCombat {
    private String url = "http://enklave-1720445391.us-west-2.elb.amazonaws.com/";
    private String prof = "/combat/start/enklave/";
    private InformationProfile informationProfile;
    public StartCombat() {
        informationProfile = InformationProfile.getInstance();
    }
    public void makeRequest(int id, final ScreenCombat screenCombat){
        final Preferences pref = Gdx.app.getPreferences("informationLog");
        final String acces = pref.getString("accesstoken");
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("enklave_id", id);
        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.POST).url(url + prof).build();
        request.setHeader("Authorization", "Bearer " + acces);
        request.setHeader("Content-type", "application/json");
        request.setContent(jsonObject.toString());
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String res = httpResponse.getResultAsString();
                Gdx.app.log("raspuns start code "+httpResponse.getStatus().getStatusCode(),"rasp mess: "+res);
                if(httpResponse.getStatus().getStatusCode()<300){
                    JSONObject j = new JSONObject(res);
                    informationProfile.getDateUserGame().setEnklaveCombatId(j.getInt("enklave_combatant_id"));
                    InformationEnklave.getInstance().setCombatId(j.getInt("enklave_combat_id"));
                    informationProfile.getDateUserGame().setInCombat(true);
                }else{
                    JSONObject json = new JSONObject(res);
                    if(json.getString("detail").contentEquals("You already joined this combat once"))
                        InformationProfile.getInstance().getDateUserGame().setEnklaveCombatId(-1);
                    informationProfile.getDateUserGame().setInCombat(true);
                    screenCombat.dialogExit(json.getString("detail").substring(0,20)+"\n"+json.getString("detail").substring(21));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("error parse data : ","err: "+t);
            }

            @Override
            public void cancelled() {

            }
        });
    }
}
