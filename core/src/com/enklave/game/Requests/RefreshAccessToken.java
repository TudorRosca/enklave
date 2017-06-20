package com.enklave.game.Requests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.net.HttpRequestBuilder;

import org.json.JSONObject;

/**
 * Created by adrian on 25.05.2016.
 */
public class RefreshAccessToken {
    private String url = "http://enklave-1720445391.us-west-2.elb.amazonaws.com";
    private String prof = "/o/token/";
    public RefreshAccessToken() {

    }
    public void makeRequestProfile() {
        final Preferences pref = Gdx.app.getPreferences("informationLog");
        final String acces = pref.getString("accesstoken");
        Gdx.app.log("response ", " access token   " + acces);
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("client_id", "EeTst6oR0udDrcKUScROksiDKiBPdUvp8leTWVVO");
        jsonObject.accumulate("client_secret", "goGcpJ1QPiAoTTOQRIGya6cCau0TTNpcrpzKudHynfPvcmYHKuWh0IpH8vEB2ZHYVAt6qOKdNXQZkMP1yKUYYDXX5sEu8ACr9mthmgR9ytaDqoXindSbX568r45saSiN");
        jsonObject.accumulate("grant_type", "refresh_token");
        jsonObject.accumulate("username", pref.getString("username"));
        jsonObject.accumulate("refresh_token", pref.getString("refreshToken"));
        final String json = jsonObject.toString();
        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.POST).url(url + prof).build();
        request.setHeader("Content-type", "application/json");
        request.setContent(json.toString());
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                if(httpResponse.getStatus().getStatusCode()<300) {
                    JSONObject jsonObj = new JSONObject(httpResponse.getResultAsString());
                    pref.putString("accesstoken",jsonObj.getString("access_token"));
                    pref.putString("refreshToken",jsonObj.getString("refresh_token"));
                    pref.putLong("timeout", (System.currentTimeMillis() +((jsonObj.getLong("expires_in") - 1000) * 1000)));
                    pref.flush();
                    new GetProfile().makeRequestProfile();
                }else{
                    System.exit(0);
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("response failed","error : "+t.getMessage()+" localizedMessage: "+t.getLocalizedMessage());
            }

            @Override
            public void cancelled() {
                Gdx.app.log("response cancelled","eror");
            }
        });
    }
}
