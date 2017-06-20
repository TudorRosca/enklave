package com.enklave.game.android.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;

import com.enklave.game.R;
import com.enklave.game.android.Utils.PreferencesShared;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;


public class LoginFacebook {
    private final CallbackManager callbackManager;
    private Activity context;
    private PreferencesShared preferencesShared;

    public LoginFacebook(LoginButton login, Activity context, PreferencesShared pref, final Intent intent) {
        callbackManager = CallbackManager.Factory.create();
        this.context = context;
        preferencesShared = pref;
        login.setReadPermissions(Arrays.asList("public_profile", "user_friends"));
        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Log.d("facebook", "succes" + loginResult.getAccessToken().getToken() + "id" + loginResult.getAccessToken().getExpires() + "data" + loginResult.getAccessToken().getUserId());
                conectedwithFacebook(loginResult.getAccessToken().getToken(),intent);
            }

            @Override
            public void onCancel() {
                Log.d("intra","facebook");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("facebook", "error" + error.toString());
            }

        });
    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }
    public void conectedwithFacebook(String Token, Intent intent) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpURLConnection con;
        JSONObject jsonObject;
        try {
            con = (HttpURLConnection) (new URL(this.context.getString(R.string.api_url) + this.context.getString(R.string.convert_token))).openConnection();
//            con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(true);
            con.connect();

            String json = "client_id=" + URLEncoder.encode(context.getString(R.string.clientid), "UTF-8") +
                    "&client_secret=" + URLEncoder.encode(context.getString(R.string.clientsecret), "UTF-8") +
                    "&grant_type=convert_token" +
                    "&token=" + URLEncoder.encode(Token, "UTF-8") +
                    "&backend=facebook";
            DataOutputStream outputStream = new DataOutputStream(con.getOutputStream());
            outputStream.writeBytes(json);
            outputStream.flush();
            outputStream.close();
            if (con.getResponseCode() < 300) {
                JSONObject finalResuslt = response(con);
                preferencesShared.putString("accesstoken", finalResuslt.getString("access_token"));
                preferencesShared.putString("refreshToken", finalResuslt.getString("refresh_token"));
                preferencesShared.putLong("timeout", (System.currentTimeMillis() + ((finalResuslt.getLong("expires_in") - 100) * 1000)));
                preferencesShared.commit();
                context.startActivity(intent);
                context.finish();
                con.disconnect();
            } else
                Log.d("facebook" + con.getRequestProperty("Content-Type"), "" + con.getResponseMessage() + "request" + con.getContentType() );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
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
