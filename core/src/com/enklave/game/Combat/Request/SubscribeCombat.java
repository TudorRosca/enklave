package com.enklave.game.Combat.Request;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.net.HttpRequestBuilder;

import org.json.JSONObject;

/**
 * Created by adrian on 06.06.2016.
 */
public class SubscribeCombat {
    private String url = "http://enklave-1720445391.us-west-2.elb.amazonaws.com/";
    private String prof = "/combat/subscribe/enklave/";

    public SubscribeCombat() {
    }
    public void makeRequest(final int id){
        final Preferences pref = Gdx.app.getPreferences("informationLog");
        final String acces = pref.getString("accesstoken");
        JSONObject parameters = new JSONObject();
        parameters.accumulate("enklave_id",id);
        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.POST).url(url + prof).build();
        request.setHeader("Authorization", "Bearer " + acces);
        request.setHeader("Content-type", "application/json");
        request.setContent(parameters.toString());
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
//                Gdx.app.log("response "+httpResponse.getStatus().getStatusCode()," subscribe "+httpResponse.getResultAsString());
                if(httpResponse.getStatus().getStatusCode()<300){
                    new CheckStatus().makeRequest(id);
                }else{

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
