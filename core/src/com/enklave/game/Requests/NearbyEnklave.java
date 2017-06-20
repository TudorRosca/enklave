package com.enklave.game.Requests;

import com.enklave.game.Enklave.ListEnklaves;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.net.HttpRequestBuilder;

import org.json.JSONArray;

/**
 * Created by adrian on 18.03.2016.
 */
public class NearbyEnklave {
    private String url = "http://enklave-1720445391.us-west-2.elb.amazonaws.com/";
    private String enkNearby = "enklave/nearby/";

    public NearbyEnklave() {
    }

    public void makeRequestEnklave(){
        Preferences pref = Gdx.app.getPreferences("informationLog");
        String acces = pref.getString("accesstoken");
        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.GET).url(url + enkNearby).build();
        request.setHeader("Authorization", "Bearer " + acces);
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String response = httpResponse.getResultAsString();
                JSONArray jsonArray = new JSONArray(response);
                ListEnklaves.getInstance().setListEnklave(jsonArray);
                Gdx.app.log("response enklave : " + httpResponse.getStatus().getStatusCode(), " message " +response);
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("response : " + t.getMessage(), "fdg " + t.getLocalizedMessage());
            }

            @Override
            public void cancelled() {
                Gdx.app.log("response ","cancelled");
            }
        });
    }
}
