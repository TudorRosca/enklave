package com.enklave.game.Enklave.DescEnklave;

import com.enklave.game.Enklave.ListEnklaves;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.net.HttpRequestBuilder;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by adrian on 27.04.2016.
 */
public class GetEnklaveDetails {
    private String url = "http://enklave-1720445391.us-west-2.elb.amazonaws.com";
    private String prof = "/enklave/details/";
    private InformationEnklave enklave;

    public GetEnklaveDetails()  {
        enklave = InformationEnklave.getInstance();
    }
    public void makeRequest(final int idEnklave, final ManagerAssets manager){
        Preferences pref = Gdx.app.getPreferences("informationLog");
        String acces = pref.getString("accesstoken");
        HashMap parameters = new HashMap<String,String>();
        parameters.put("enklave_id",String.valueOf(idEnklave));
        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.GET).url(url + prof).build();
        request.setHeader("Authorization", "Bearer " + acces);
        request.setContent(HttpParametersUtils.convertHttpParameters(parameters));
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                if(httpResponse.getStatus().getStatusCode()<300) {
                    String s = httpResponse.getResultAsString();
                    JSONObject json = new JSONObject(s);
                    enklave.setBricks(json.getInt("nr_bricks"));
                    if (json.getString("faction").contentEquals("null")) {
                        enklave.setFaction(0);
                    } else {
                        enklave.setFaction(json.getInt("faction"));
                    }
                    enklave.setId(json.getInt("id"));
                    enklave.setRooms(json.getInt("rooms"));
                    enklave.setName(json.getString("name"));
                    if(!json.isNull("created_by"))
                        enklave.setUsercreate(json.getJSONObject("created_by").getString("username"));
                    enklave.setLatitude((float) json.getDouble("latitude"));
                    enklave.setLongitude((float) json.getDouble("longitude"));
                    enklave.setExtensions(json.getInt("extensions"));
                    enklave.setTurrets(json.getInt("cells"));
                    if(json.getString("status").contentEquals("InCombat")) {
                        enklave.setStatusCombat(true);
                        enklave.setShields(json.getInt("shield"));
                        enklave.setEnergyBrick(json.getInt("brick_last"));
                    }else{
                        enklave.setShields(json.getInt("shield_full"));
                        enklave.setEnergyBrick(json.getInt("brick_last"));
                    }
                    enklave.setEnergyBrickfull(json.getInt("brick_full"));
                    enklave.setEnergyfullshield(json.getInt("shield_full"));
                    manager.getAssertEnklaveScreen().setIsupdate(true);
                    Gdx.app.log("information enklave ", "succes  " + json);
                }else{
                    Gdx.app.log("eroare"+httpResponse.getStatus().getStatusCode(),"meassajul :"+httpResponse.getResultAsString());
                    new GetEnklaveDetails().makeRequest(ListEnklaves.getInstance().get(idEnklave).id, manager);
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("information error failed"," "+ t);
                new GetEnklaveDetails().makeRequest(ListEnklaves.getInstance().get(idEnklave).id, manager);
            }

            @Override
            public void cancelled() {
                Gdx.app.log("information error cancelled","cancel");
            }
        });
    }
}
