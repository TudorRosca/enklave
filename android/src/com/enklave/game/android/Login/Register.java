package com.enklave.game.android.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.enklave.game.android.Utils.PreferencesShared;
import com.enklave.game.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Register {
    private final HttpURLConnection con;
    private Activity context;
    private PreferencesShared preferencesShared;

    public Register(Activity cont, PreferencesShared pref) throws IOException {
        this.context = cont;
        preferencesShared = pref;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        con = (HttpURLConnection) (new URL(this.context.getString(R.string.api_url)+this.context.getString(R.string.register_url))).openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type","application/json");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(true);
        con.connect();
    }
    public boolean Register(String nick, String email, String password, Intent intent) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject();
//        jsonObject.accumulate("client_id",context.getString(R.string.clientid));
//        jsonObject.accumulate("client_secret", context.getString(R.string.clientsecret));
//        jsonObject.accumulate("grant_type", "password");
        jsonObject.accumulate("email", email);
        jsonObject.accumulate("password", password);
        jsonObject.accumulate("first_name",nick);
        String json = jsonObject.toString();
        Log.d("mesaj"," "+json);
        con.getOutputStream().write(json.getBytes());
        if(con.getResponseCode()<300){
            JSONObject finalResuslt = response(con);
            Log.d("response","register  "+finalResuslt);
            preferencesShared.putString("accesstoken",finalResuslt.getString("access_token"));
            preferencesShared.putString("refreshToken",finalResuslt.getString("refresh_token"));
            preferencesShared.putLong("timeout", (System.currentTimeMillis() +((finalResuslt.getLong("expires_in") - 100) * 1000)));
            preferencesShared.putString("username", finalResuslt.getString("username"));
            preferencesShared.commit();
            con.disconnect();
            context.startActivity(intent);
            context.finish();
            return true;
        }
        else{
            InputStream stream = con.getErrorStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String line;
            StringBuilder str = new StringBuilder();
            while((line = br.readLine())!=null){
                str.append(line).append("\n");
            }
            Log.d("error", " " + con.getResponseCode() + " mes " + con.getResponseMessage()+"  fc  " + str.toString());
            Toast.makeText(context, str.toString(), Toast.LENGTH_LONG).show();
        }
        con.disconnect();
        return false;
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
