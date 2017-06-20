package com.enklave.game.Requests;

import com.enklave.game.Combat.Request.CombatUserCheck;
import com.enklave.game.Profile.InformationProfile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.net.HttpRequestBuilder;

import org.json.JSONObject;


public class GetProfile {
    private String url = "http://enklave-1720445391.us-west-2.elb.amazonaws.com/";
    private String prof = "user/profile/";
    private InformationProfile profile;

    public GetProfile() {
        profile = InformationProfile.getInstance();
    }

    public void makeRequestProfile(){
        final Preferences pref = Gdx.app.getPreferences("informationLog");
        final String acces = pref.getString("accesstoken");
//        Gdx.app.log("response "," access token   "+acces);
        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.GET).url(url + prof).build();
        request.setHeader("Authorization", "Bearer " + acces);
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                if(httpResponse.getStatus().getStatusCode()<300) {
                    JSONObject json = new JSONObject(httpResponse.getResultAsString());
                    profile.getDateUser().setId(json.getInt("id"));
                    profile.getDateUser().setUserName(json.getString("username"));
                    profile.getDateUser().setFristName(json.getString("first_name"));
                    profile.getDateUser().setLastName(json.getString("last_name"));
                    profile.getDateUser().setEmail(json.getString("email"));
                    profile.getDateUser().setPhoneNumber(json.getString("phone_number"));

                    if (json.getString("faction") == "null") {
                        profile.getDateUserGame().setFaction(0);
                    } else {
                        profile.getDateUserGame().setFaction(json.getInt("faction"));
                    }
                    profile.getDateUserGame().setScrap(json.getInt("scrap"));
                    profile.getDateUserGame().setLevel(json.getInt("level"));
                    profile.getDateUserGame().setEnergyProgress(json.getJSONObject("energy_config").getInt("PLAYER_RECHARGE_PROGRESSION"));
                    profile.getDateUserGame().setEnergyLevel(json.getJSONObject("energy_config").getInt("PLAYER_ENERGY_PROGRESSION"));
                    profile.getDateUserGame().setEnergy(json.getInt("energy"));
                    profile.getDateUserGame().setExperienceLevel(json.getJSONObject("xp_for_level").getInt("PLAYER_XP_PROGRESSION"));
                    profile.getDateUserGame().setExperience(json.getInt("experience"));
                    profile.getDateUserGame().setDistanceWalked(json.getInt("distance_walked"));
//                profile.getDateUserGame().setElectronics(json.getInt("electronics"));

                    profile.getDateBrick().setNumber(json.getInt("nr_bricks"));
                    profile.getDateBrick().setRateEnergy(json.getJSONObject("brick_config").getInt("CRAFT_BRICK_ENERGY_COST"));
                    profile.getDateBrick().setRateScrap(json.getJSONObject("brick_config").getInt("CRAFT_BRICK_SCRAP_COST"));
                    profile.getDateBrick().setRateTime(json.getJSONObject("brick_config").getInt("CRAFT_BRICK_TIME_COST"));
                    profile.getDateBrick().setUsageEnergy(json.getJSONObject("place_brick_config").getInt("PLACE_BRICK_ENERGY_COST"));
                    profile.getDateBrick().setUsageTime(json.getJSONObject("place_brick_config").getInt("PLACE_BRICK_TIME_COST"));

                    profile.getDateCell().setCellNumber(json.getInt("nr_cells"));
                    profile.getDateCell().setCellValue(json.getJSONObject("cell_configs").getInt("ENERGY_CELL_RECHARGE_VALUE"));
                    profile.getDateCell().setCellRateTime(json.getJSONObject("cell_configs").getInt("CRAFTING_TIME_CELL"));
                    profile.getDateCell().setCellRateScrap(json.getJSONObject("cell_configs").getInt("CONVERSION_RATE_CELL"));
                    profile.getDateCell().setCellRateEnergy(profile.getDateCell().getCellValue() * (json.getJSONObject("cell_configs").getInt("CELL_ENERGY_PERCENTAGE_COST") / 100) + profile.getDateCell().getCellValue());
                    profile.getDateCell().setCellUsageEnergy(json.getJSONObject("cell_configs").getInt("PLAYER_CELL_RELOAD_TIME"));
                    profile.getDateCell().setCellUsageTime(json.getJSONObject("cell_configs").getInt("PLAYER_ENERGY_CELL_USAGE_COST"));

                    Gdx.app.log("response profil " + "status : " + httpResponse.getStatus().getStatusCode(), acces + " message " + json.toString());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    new CombatUserCheck().makeRequest();
                                }
                            });
                        }
                    }).start();
                }else{
                    if(httpResponse.getStatus().getStatusCode() == 401){
                        
                    }
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("response : " + t.getMessage(), "fdg " + t.getLocalizedMessage());
                new GetProfile().makeRequestProfile();
            }

            @Override
            public void cancelled() {

            }
        });
    }
}
