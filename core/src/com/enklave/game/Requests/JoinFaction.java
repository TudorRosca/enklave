package com.enklave.game.Requests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.net.HttpRequestBuilder;

import org.json.JSONObject;


public class JoinFaction {
    private String url = "http://enklave-1720445391.us-west-2.elb.amazonaws.com/";
    private String join = "/faction/join/";

    public JoinFaction() {
    }

    public void makeRequest(int id){
        final Preferences pref = Gdx.app.getPreferences("informationLog");
        String acces = pref.getString("accesstoken");
        JSONObject json = new JSONObject();
        json.accumulate("faction_id",id);
        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.POST).url(url + join).build();
        request.setHeader("Authorization", "Bearer " + acces);
        request.setHeader("Content-type", "application/json");
        request.setContent(json.toString());
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                //Gdx.app.log("response :","message "+httpResponse.getResultAsString());
            }

            @Override
            public void failed(Throwable t) {
                //Gdx.app.log("response :","failed ");
            }

            @Override
            public void cancelled() {
                //Gdx.app.log("response :","cancel ");
            }
        });
    }
}
