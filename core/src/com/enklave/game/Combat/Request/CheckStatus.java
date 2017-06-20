package com.enklave.game.Combat.Request;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.enklave.game.Combat.InfoPlayer;
import com.enklave.game.Combat.ListOfAttachers;
import com.enklave.game.Combat.ListOfDefenders;
import com.enklave.game.Enklave.DescEnklave.InformationEnklave;
import com.enklave.game.LoadResursed.ManagerAssets;
import com.enklave.game.Profile.InformationProfile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by adrian on 06.06.2016.
 */
public class CheckStatus {
    private String url = "http://enklave-1720445391.us-west-2.elb.amazonaws.com/";
    private String prof = "/combat/status/enklave/";
    private ListOfAttachers listOfAttachers;
    private ListOfDefenders listOfDefenders;
    private InformationProfile informationProfile;
    public CheckStatus() {
        informationProfile = InformationProfile.getInstance();
        listOfAttachers = ListOfAttachers.getInstance();
        listOfDefenders = ListOfDefenders.getInstance();
        listOfAttachers.clearList();
        listOfDefenders.clearList();
    }
    public void makeRequest(int id){
        final Preferences pref = Gdx.app.getPreferences("informationLog");
        final String acces = pref.getString("accesstoken");
        HashMap parameters = new HashMap<String,String>();
        parameters.put("enklave_id",String.valueOf(id));
        HttpRequestBuilder builder = new HttpRequestBuilder();
        final Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.GET).url(url + prof).build();
        request.setHeader("Authorization", "Bearer " + acces);
        request.setContent(HttpParametersUtils.convertHttpParameters(parameters));
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                if(httpResponse.getStatus().getStatusCode()<300){
                    informationProfile.getDateUserGame().setInCombat(false);
                    JSONObject response = new JSONObject(httpResponse.getResultAsString());
                    Gdx.app.log("raspuns "," :  "+response.toString());
                    if(!response.has("detail")) {
                        JSONArray arraycombat = response.getJSONArray("combatants");
                        for (int i = 0; i < arraycombat.length(); i++) {
                            JSONObject jsobject = arraycombat.getJSONObject(i);
                            String user = jsobject.getString("user_username");
                            int iduser = jsobject.getInt("user_id");
                            if (iduser == informationProfile.getDateUser().getId()) {
                                if (jsobject.isNull("date_left")) {
                                    informationProfile.getDateUserGame().setEnklaveCombatId(jsobject.getInt("combatant_id"));
                                } else {
                                    informationProfile.getDateUserGame().setEnklaveCombatId(-1);
                                }
                                informationProfile.getDateUserGame().setInCombat(true);
                            }
                            int type_id = jsobject.getInt("type_id");
                            int id = jsobject.getInt("combatant_id");
                            int energy = jsobject.getInt("energy");
                            int energyMax = jsobject.getJSONObject("energy_config").getInt("PLAYER_ENERGY_PROGRESSION");
                            int damage = jsobject.getJSONObject("combat_config").getInt("PLAYER_DAMAGE_PROGRESSION");
                            int faction = jsobject.getInt("faction");
                            int costenergy = jsobject.getJSONObject("combat_config").getInt("PLAYER_SHOT_COST_PROGRESSION");
                            int timerecharge = 5;
                            if (type_id == 1 && jsobject.isNull("date_left")) {
                                listOfDefenders.add(new InfoPlayer(id, user, energy, energyMax, damage, faction, timerecharge, costenergy));
                            } else if (type_id == 2 && jsobject.isNull("date_left")) {
                                listOfAttachers.add(new InfoPlayer(id, user, energy, energyMax, damage, faction, timerecharge, costenergy));
                            }
                        }
                        InformationEnklave.getInstance().setCombatId(response.getJSONObject("enklave_combat").getInt("enklave_combat_id"));
                    }

                    ManagerAssets.getInstance().getAssetsCombat().setCombat = true;
                }else{
                    Gdx.app.log("raspuns error "+httpResponse.getStatus().getStatusCode()," err "+ httpResponse.getResultAsString());
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("raspuns code","rasp mess: "+t);
            }

            @Override
            public void cancelled() {

            }
        });
    }
}
