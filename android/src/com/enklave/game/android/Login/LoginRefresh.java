package com.enklave.game.android.Login;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.enklave.game.R;
import com.enklave.game.android.Utils.PreferencesShared;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginRefresh {
    private final HttpURLConnection con;
    private Context context;
    private PreferencesShared preferencesShared;
    public LoginRefresh(Context context,PreferencesShared pref) throws IOException {
        this.context = context;
        preferencesShared = pref;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        con = (HttpURLConnection) (new URL(this.context.getString(R.string.api_url)+this.context.getString(R.string.login_url))).openConnection();
        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(true);
        con.connect();

    }
    public boolean connectLogin(String user,String password) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("client_id",context.getString(R.string.clientid));
        jsonObject.accumulate("client_secret", context.getString(R.string.clientsecret));
        jsonObject.accumulate("grant_type", "password");
        jsonObject.accumulate("username", user);
        jsonObject.accumulate("password", password);
        String json = jsonObject.toString();
        con.getOutputStream().write(json.getBytes());
        Log.d("intra"," "+con.getResponseMessage());
        if(con.getResponseCode()<300){
            JSONObject finalResuslt = response(con);
            preferencesShared.putString("accesstoken",finalResuslt.getString("access_token"));
            preferencesShared.putString("refreshToken",finalResuslt.getString("refresh_token"));
            preferencesShared.putLong("timeout", (System.currentTimeMillis() +((finalResuslt.getLong("expires_in") - 1000) * 1000)));
            preferencesShared.putString("username", user);
            preferencesShared.commit();
            con.disconnect();
            return true;
        }
        else{
            Log.d("error", " " + con.getResponseCode() + " mes " + con.getResponseMessage() + con.getContentEncoding());
            Toast.makeText(context, "User Name Or Password is Wrong!", Toast.LENGTH_LONG).show();
        }
        Log.d("raspuns "+con.getResponseCode(),"sagg");
        con.disconnect();
        return false;
    }
    public boolean refresh() throws JSONException, IOException {
        long timeout = preferencesShared.getLong("timeout",0);
        if(timeout > System.currentTimeMillis()){
            return true;
        }else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("client_id", context.getString(R.string.clientid));
            jsonObject.accumulate("client_secret", context.getString(R.string.clientsecret));
            jsonObject.accumulate("grant_type", "refresh_token");
            jsonObject.accumulate("username", preferencesShared.getString("username", ""));
            jsonObject.accumulate("refresh_token", preferencesShared.getString("refreshToken", ""));
            String json = jsonObject.toString();
            con.getOutputStream().write(json.getBytes());
            Log.d("intra"," "+con.getResponseMessage());
            if (con.getResponseCode() < 300) {
                JSONObject finalResuslt = response(con);
                preferencesShared.putString("accesstoken", finalResuslt.getString("access_token"));
                preferencesShared.putString("refreshToken", finalResuslt.getString("refresh_token"));
                preferencesShared.putLong("timeout", (System.currentTimeMillis() + ((finalResuslt.getLong("expires_in") - 100) * 1000)));
                preferencesShared.commit();
                con.disconnect();
                return true;
            } else {
                con.disconnect();
                return false;
            }
        }
    }
    private JSONObject response(HttpURLConnection con) throws IOException, JSONException {
        InputStream in = con.getInputStream();
        StringBuilder builder = new StringBuilder();
        String line;
        BufferedReader r1 = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        while ((line = r1.readLine()) != null) {
            builder.append(line).append("\n");
        }
        return new JSONObject(String.valueOf(builder));
    }
}
