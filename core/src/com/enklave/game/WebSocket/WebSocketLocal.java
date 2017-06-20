package com.enklave.game.WebSocket;

import com.badlogic.gdx.Gdx;
import com.enklave.game.Combat.InfoPlayer;
import com.enklave.game.Combat.ListOfAttachers;
import com.enklave.game.Combat.ListOfDefenders;
import com.enklave.game.Combat.UpdateDisplayCombat;
import com.enklave.game.Enklave.DescEnklave.InformationEnklave;
import com.enklave.game.FontLabel.Font;
import com.enklave.game.Profile.InformationProfile;
import com.enklave.game.Screens.ProgressBarEnergy;
import com.enklave.game.Screens.ScreenChat;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class WebSocketLocal implements InterfaceWebSocket {
    private String url="ws://enklave-1720445391.us-west-2.elb.amazonaws.com:8888/ws/";
    private WebSocketClient webSocketClient;
    private boolean connected = false;
    private double referenceLatitude = 0.0,referenceLongitude = 0.0;
    private InformationEnklave informationEnklave;
    private InformationProfile informationProfile;
    private WebSocketLocal socketWeb =this;

    private static WebSocketLocal ourInstance = new WebSocketLocal();

    public static WebSocketLocal getInstance() {
        return ourInstance;
    }

    public void connectWebSocket(JSONObject json){
        URI uri = null;
        try {
            uri =new URI(url+json.getString("ticket"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        webSocketClient = new WebSocketClient(uri,new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Gdx.app.log("response"+serverHandshake.getHttpStatus(),"connectat"+serverHandshake.getHttpStatusMessage());
                connected = true;
            }

            @Override
            public void onMessage(String s) {
                final JSONObject json = new JSONObject(s);
                if(json.getString("msg_type").contentEquals("scrap")){
                    informationProfile.getDateUserGame().setScrap(json.getJSONObject("message").getInt("total_user_scrap"));
                    Gdx.app.log("response scrap "," "+json.getJSONObject("message").getInt("total_user_scrap"));
                }else if(json.getString("msg_type").contentEquals("faction_message")){
                    String sdate = json.getString("created_at").replace("Z","+00:00");
                    Date date =null;
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ").parse(sdate);
                    } catch (ParseException e) {
                        Gdx.app.log("eroare","  "+e);
                        e.printStackTrace();
                    }
                    ScreenChat.getInstance().addmessage(json.getString("message"),json.getString("from_user"),date);
                    Gdx.app.log("response faction","este"+s);
                }else if(json.getString("msg_type").contentEquals("location_message")){
                    String sdate = json.getString("created_at").replace("Z","+00:00");
                    Date date =null;
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ").parse(sdate);
                    } catch (ParseException e) {
                        Gdx.app.log("eroare","  "+e);
                        e.printStackTrace();
                    }
                    ScreenChat.getInstance().addmesschatpublic(json.getString("message"),ScreenChat.getInstance().detcolor(json.getInt("faction_id")),json.getString("from_user"),date);
                    Gdx.app.log("response location","este"+s);
                }else if (json.getString("msg_type").contentEquals("joined_combat")){
                    JSONObject objectJson = json.getJSONObject("attacker");
                    int faction = objectJson.getInt("faction_id");
                    int id = objectJson.getInt("combatant_id");
                    String name = objectJson.getString("username");
                    int energy = objectJson.getInt("energy");
                    int damage = objectJson.getJSONObject("attack_config").getInt("PLAYER_DAMAGE_PROGRESSION");
                    int time = objectJson.getInt("time_recharging");
                    int cost = objectJson.getJSONObject("attack_config").getInt("PLAYER_SHOT_COST_PROGRESSION");
                    if(faction != informationEnklave.getFaction()){
                        ListOfAttachers.getInstance().add(new InfoPlayer(id,name,energy,2500,damage,faction,time,cost));
                    }else{
                        ListOfDefenders.getInstance().add(new InfoPlayer(id,name,energy,2500,damage,faction,time,cost));
                    }
                    UpdateDisplayCombat.getInstance().update();
                }else if (json.getString("msg_type").contentEquals("hit")){
                    if(!json.isNull("attacked_user")){
                        int uid = json.getJSONObject("attacked_user").getInt("combatant_id");
                        int energy = json.getJSONObject("attacked_user").getInt("energy");
                        int id = json.getJSONObject("attacker").getInt("combatant_id");
                        int faction = json.getJSONObject("attacker").getInt("faction_id");
                        if(faction != informationEnklave.getFaction()){
                            ListOfAttachers.getInstance().get(id).energy = json.getJSONObject("attacker").getInt("energy");
                            ListOfDefenders.getInstance().get(uid).energy = energy;
                        }else{
                            ListOfDefenders.getInstance().get(id).energy = json.getJSONObject("attacker").getInt("energy");
                            ListOfAttachers.getInstance().get(uid).energy = energy;
                        }
                        UpdateDisplayCombat.getInstance().update();
                        if(uid == informationProfile.getDateUserGame().getEnklaveCombatId()){
                            informationProfile.getDateUserGame().setEnergy(energy);
                            ProgressBarEnergy.getInstance().updateEnergyCombat();
                        }else if(id == informationProfile.getDateUserGame().getEnklaveCombatId()){
                            informationProfile.getDateUserGame().setEnergy(energy);
                            ProgressBarEnergy.getInstance().updateEnergyCombat();
                        }
                    }else if(json.getJSONObject("attack_status").getString("message").contentEquals("shield_hit")){
                        int id = json.getJSONObject("attacker").getInt("combatant_id");
                        int energy_shields = json.getJSONObject("attacked_enklave").getInt("remaining_energy");
                        int energy_user = json.getJSONObject("attacker").getInt("energy");
                        informationEnklave.setShields(energy_shields);
                        ListOfAttachers.getInstance().get(id).energy = energy_user;
                        UpdateDisplayCombat.getInstance().update();
                        if(id == informationProfile.getDateUserGame().getEnklaveCombatId()){
                            informationProfile.getDateUserGame().setEnergy(energy_user);
                            ProgressBarEnergy.getInstance().updateEnergyCombat();
                        }
                    }else if(json.getJSONObject("attack_status").getString("message").contentEquals("brick_hit")){
                        JSONObject hitbrick = json.getJSONObject("attack_status");
                        int energybrick = hitbrick.getInt("brick_energy_remaining");
                        informationEnklave.setEnergyBrick(energybrick == 0 ? 350 : energybrick);
                        informationEnklave.setBricks(hitbrick.getInt("bricks_count"));
                        int id = json.getJSONObject("attacker").getInt("combatant_id");
                        int energy_user = json.getJSONObject("attacker").getInt("energy");
                        ListOfAttachers.getInstance().get(id).energy = energy_user;
                        UpdateDisplayCombat.getInstance().update();
                        UpdateDisplayCombat.getInstance().updateMak();
                        if(id == informationProfile.getDateUserGame().getEnklaveCombatId()){
                            informationProfile.getDateUserGame().setEnergy(energy_user);
                            ProgressBarEnergy.getInstance().updateEnergyCombat();
                        }
                    }else if(json.getJSONObject("attacked_enklave").getBoolean("enklave_conquered")){
                        informationEnklave.setEnergyBrick(0);
                        informationEnklave.setBricks(0);
                        int id = json.getJSONObject("attacker").getInt("combatant_id");
                        if(informationProfile.getDateUserGame().getFaction() == informationEnklave.getFaction()) {
                            ListOfDefenders.getInstance().get(id).energy = json.getJSONObject("attacker").getInt("energy");
                        }else{
                            ListOfAttachers.getInstance().get(id).energy = json.getJSONObject("attacker").getInt("energy");
                        }
                        UpdateDisplayCombat.getInstance().update();
                        UpdateDisplayCombat.getInstance().updateMak();
                        if(json.getString("enklave_combat_status").contentEquals("Attackers won")){
                            UpdateDisplayCombat.getInstance().messageCombat("Attackers Won!!!");
                        }

                    }
                }else if(json.getString("msg_type").contentEquals("left_combat")){
                    JSONObject jsonuser = json.getJSONObject("user");
                    int id = jsonuser.getInt("combatant_id");
                    int faction = jsonuser.getInt("faction_id");
                    if(informationEnklave.getFaction() == faction){
                        ListOfDefenders.getInstance().removePlayer(id);
                    }else{
                        ListOfAttachers.getInstance().removePlayer(id);
                    }
                    if(ListOfAttachers.getInstance().size() == 0){
                        UpdateDisplayCombat.getInstance().messageCombat("Deffenders Won!!!");
                    }else if(ListOfDefenders.getInstance().size() == 0 && informationEnklave.getBricks() == 0){
                        UpdateDisplayCombat.getInstance().messageCombat("Attackers Won!!!");
                    }
                    if(json.getJSONObject("user").getInt("id") == informationProfile.getDateUser().getId()){
                        UpdateDisplayCombat.getInstance().messageCombat("You lose!");
                    }
                    UpdateDisplayCombat.getInstance().update();
                }
                Gdx.app.log("websocket ", "message receive: "+json.toString() );

            }

            @Override
            public void onClose(int i, String s, boolean b) {
                connected = false;
                if(s == ""){
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            RequestTokenSocket.getTicket(socketWeb);
                        }
                    },2000);
                    Gdx.app.log("response  websochet  onclose  : ","  "+s);
                }
            }

            @Override
            public void onError(Exception e) {
                Gdx.app.log("response onerror  : ","  "+e.getMessage());
            }
        };
        webSocketClient.connect();
    }

    private WebSocketLocal() {
        RequestTokenSocket.getTicket(this);
        informationEnklave = InformationEnklave.getInstance();
        informationProfile = InformationProfile.getInstance();
        new Font();
    }

    public void reconnectSocket(){
        if(!connected){
            RequestTokenSocket.getTicket(socketWeb);
        }
    }

    @Override
    public void connectClient(String ip) {

    }

    @Override
    public boolean sendMsg(double lat,double lng) {
        if(connected)
        {
            JSONObject latlng = new JSONObject();
            latlng.accumulate("lat",lat);
            latlng.accumulate("long",lng);
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("type","scrap");
            jsonObject.accumulate("params",latlng);
            webSocketClient.send(jsonObject.toString());
            Gdx.app.log("response ", "sdg  " +jsonObject.toString());
            return true;
        }else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    RequestTokenSocket.getTicket(socketWeb);
                }
            },2000);
            return false;
        }
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public int getIdClient() {
        return 0;
    }

    @Override
    public void close() {

    }

    public double getReferenceLatitude() {
        return referenceLatitude;
    }

    public void setReferenceLatitude(double referenceLatitude) {
        this.referenceLatitude = referenceLatitude;
    }

    public double getReferenceLongitude() {
        return referenceLongitude;
    }

    public void setReferenceLongitude(double referenceLongitude) {
        this.referenceLongitude = referenceLongitude;
    }
}
