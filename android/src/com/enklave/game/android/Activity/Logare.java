package com.enklave.game.android.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.enklave.game.R;
import com.enklave.game.android.CheckList.CheckNetwork;
import com.enklave.game.android.Login.LoginFacebook;
import com.enklave.game.android.Login.LoginGoogle;
import com.enklave.game.android.Login.LoginRefresh;
import com.enklave.game.android.Login.Register;
import com.enklave.game.android.Utils.PreferencesShared;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;

import org.json.JSONException;

import java.io.IOException;

import static android.widget.Toast.LENGTH_LONG;

public class Logare extends Activity {
    private String st_access = "accesstoken";
    private LoginFacebook fb;
    private LoginGoogle googleLog;
    private Intent gameStart;
    private LoginButton btnfb;
    private SignInButton btngoogle;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.login);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        findViewById(R.id.scrollView2).setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return false;
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findViewById(R.id.scrollView2).setNestedScrollingEnabled(false);
        }
        TabHost tabHost = (TabHost)findViewById(R.id.tabhost);
        tabHost.setup();
        this.setNewTab(this, tabHost, "tab1", R.string.tablogintag, R.id.logintab);
        this.setNewTab(this, tabHost, "tab2", R.string.tabsinguptag, R.id.signuptab);

        gameStart = new Intent(this,Game.class);
        if(!CheckNetwork.check(this)) {
            findViewById(R.id.logscreen).setVisibility(View.INVISIBLE);
        }else {
            PreferencesShared preferencesShared = new PreferencesShared(getApplicationContext(), getString(R.string.Details));
            try {
                if (preferencesShared.contains(st_access)) {
                    LoginRefresh log = new LoginRefresh(getApplicationContext(), preferencesShared);
                    if (log.refresh()) {
                        startActivity(gameStart);
                        finish();
                    }
                }
                btnfb = (LoginButton) findViewById(R.id.login_button_fb);
                fb = new LoginFacebook(btnfb, this, preferencesShared, gameStart);
                btngoogle = (SignInButton) findViewById(R.id.sign_in_button);
                googleLog = new LoginGoogle(getApplicationContext(), (SignInButton) findViewById(R.id.sign_in_button), this, preferencesShared);
                connected(preferencesShared);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }
        ImageButton btnlogfb = (ImageButton)findViewById(R.id.imageButton);
        btnlogfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnfb.performClick();
            }
        });
        ImageButton btnloggoogle = (ImageButton)findViewById(R.id.imageButton2);
        btnloggoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                btngoogle.performClick();
            }
        });
        ImageButton btnimagereg = (ImageButton)findViewById(R.id.btnEnrole);
        btnimagereg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkdataforRegister();
            }
        });
    }

    private void checkdataforRegister() {
        EditText nickname = (EditText)findViewById(R.id.nickname);
        EditText email = (EditText)findViewById(R.id.emailAddr);
        EditText pass = (EditText)findViewById(R.id.passwordSingUp);
        EditText repeatpass = (EditText)findViewById(R.id.repeatpassword);
        PreferencesShared preferencesShared = new PreferencesShared(getApplicationContext(), getString(R.string.Details));
        if(nickname.getText().length() != 0 && email.getText().length() != 0 && pass.getText().length() != 0 && repeatpass.getText().length() != 0){
            if(Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                if (pass.getText().toString().contentEquals(repeatpass.getText().toString())) {
                    try {
                        new Register(this, preferencesShared).Register(nickname.getText().toString(), email.getText().toString(), pass.getText().toString(),gameStart);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(this, "The confirmation password don't match. ", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "All fields are necessary. ", Toast.LENGTH_SHORT).show();
        }
    }

    private void setNewTab(Context context, TabHost tabHost, String tag, int title, int contentID ){
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(tag);
        tabSpec.setIndicator(getTabIndicator(tabHost.getContext(), title)); // new function to inject our own tab layout
        tabSpec.setContent(contentID);
        tabHost.addTab(tabSpec);
    }

    private View getTabIndicator(Context context, int title) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(title);
        return view;
    }

    private void connected(final PreferencesShared pref) throws IOException {
        final EditText username = (EditText)findViewById(R.id.username);
        final EditText password = (EditText)findViewById(R.id.password);
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    if (!username.getText().equals("") && !password.getText().equals("")) {
                        try {
                            LoginRefresh log = new LoginRefresh(getApplicationContext(), pref);
                            if (log.connectLogin(username.getText().toString(), password.getText().toString())) {
                                startActivity(gameStart);
                                finish();
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Username or password is wrong!", LENGTH_LONG).show();
                        }
                    }
                }
                return false;
            }
        });
        ImageButton btn = (ImageButton)findViewById(R.id.buttonLogin);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(username.getText().equals("")) && !(password.getText().equals(""))) {
                    try {
                        LoginRefresh log = new LoginRefresh(getApplicationContext(), pref);
                        if (log.connectLogin(username.getText().toString(), password.getText().toString())) {
                            startActivity(gameStart);
                            finish();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fb.getCallbackManager().onActivityResult(requestCode,resultCode,data);
        if (requestCode == 101) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            googleLog.handleSignInResult(result);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //AppEventsLogger.activateApp(this.getApplication());
        if(CheckNetwork.check(this)){
            findViewById(R.id.logscreen).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.logscreen).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
