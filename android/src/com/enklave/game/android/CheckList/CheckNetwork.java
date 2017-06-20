package com.enklave.game.android.CheckList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.enklave.game.android.Activity.Logare;


public class CheckNetwork {
    public static boolean check(final Context context){
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if(networkinfo == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("CANNOT CONNECT TO SERVER!\n PLEASE CHECK YOUR INTERNET\n CONNECTION, THEN RESTART THE GAME").setNeutralButton("RESTART", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    context.startActivity(new Intent(context, Logare.class));
                    System.exit(0);
                }
            }).setCancelable(false);
            builder.create().show();
            return false;
        }else{
            if(networkinfo.isConnectedOrConnecting()){
                return true;
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("CANNOT CONNECT TO SERVER!\n PLEASE CHECK YOUR INTERNET\n CONNECTION, THEN RESTART THE GAME").setNeutralButton("RESTART", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(context,Logare.class));
                        System.exit(0);
                    }
                }).setCancelable(false);
                builder.create().show();
                return false;
            }
        }
    }
}
