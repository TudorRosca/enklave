package com.enklave.game.android.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import com.enklave.game.R;
import com.enklave.game.android.Utils.PreferencesShared;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

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

/**
 * Created by adrian on 03.03.2016.
 */
public class LoginGoogle implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private final GoogleApiClient mGoogleApiClient;
    private Context context;
    private PreferencesShared preferencesShared;

    public LoginGoogle(final Context context, SignInButton button, final Activity act, PreferencesShared pref) {
        this.context = context;
        preferencesShared = pref;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("473547758853-nm840bumsu5km04gbgtdee1fhtod1ji6.apps.googleusercontent.com").build();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) .requestScopes(new Scope(Scopes.PLUS_LOGIN)).requestScopes(new Scope(Scopes.PLUS_ME)).requestEmail().requestIdToken("473547758853-nm840bumsu5km04gbgtdee1fhtod1ji6.apps.googleusercontent.com").build();
        mGoogleApiClient = new GoogleApiClient.Builder(context.getApplicationContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        button.setSize(SignInButton.SIZE_STANDARD);
        button.setScopes(gso.getScopeArray());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                act.startActivityForResult(signInIntent, 101);

            }
        });
    }
    public void handleSignInResult(GoogleSignInResult result) {
        Log.d("google","asdg"+result.getStatus()+"dg    ");
        if (result.isSuccess()) {
//            GoogleSignInAccount acct = result.getSignInAccount();
//            //conectedwithGoogle(acct.getIdToken());
//            Log.d("google", "handleSignInResult:" + acct.getIdToken());
            //signInButton.setVisibility(View.GONE);

//            try {
//                Log.d("google","token");
//                String token = GoogleAuthUtil.getToken(context,result.getSignInAccount().getEmail(),"oauth2:https://www.googleapis.com/auth/userinfo.profile");
//                Log.d("google","token"+token);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (GoogleAuthException e) {
//                e.printStackTrace();
//            }
        } else {

            // Signed out, show unauthenticated UI.
//            updateUI(false);
        }
    }
    public void conectedwithGoogle(String Token) {
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
                    "&backend=google-oauth2";
            DataOutputStream outputStream = new DataOutputStream(con.getOutputStream());
            outputStream.writeBytes(json);
            outputStream.flush();
            outputStream.close();
            if (con.getResponseCode() < 300) {
                JSONObject finalResuslt = response(con);
                Log.d("google"," "+finalResuslt.toString());
//                preferencesShared.putString("accesstoken", finalResuslt.getString("access_token"));
//                preferencesShared.putString("refreshToken", finalResuslt.getString("refresh_token"));
//                preferencesShared.putLong("timeout", (System.currentTimeMillis() + ((finalResuslt.getLong("expires_in") - 100) * 1000)));
//                preferencesShared.commit();
                con.disconnect();
            } else
                Log.d("google" + con.getRequestProperty("Content-Type"), "" + con.getResponseMessage() + "request"+json );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("google",""+connectionResult.getErrorMessage()+"cod"+connectionResult.getErrorCode());
    }
    public void obtainCode(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpURLConnection con;
        JSONObject jsonObject;
        try {
            con = (HttpURLConnection) (new URL("https://accounts.google.com/o/oauth2/device/code")).openConnection();
//            con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(true);
            con.connect();

            String json = "client_id=612970444001-ka3hfe946g9lv23mer25kjvkhtmac001.apps.googleusercontent.com" +
                    "&scope=email";
            DataOutputStream outputStream = new DataOutputStream(con.getOutputStream());
            outputStream.writeBytes(json);
            outputStream.flush();
            outputStream.close();
            if (con.getResponseCode() < 300) {
                JSONObject finalResuslt = response(con);
                Log.d("google",""+finalResuslt);
                con.disconnect();
            } else
                Log.d("google" + con.getRequestProperty("Content-Type"), "" + con.getResponseMessage() + "request" + con.getContentType() );
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
