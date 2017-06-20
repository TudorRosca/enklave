package com.enklave.game.WebSocket;

/**
 * Created by adrian on 18.03.2016.
 */
public interface InterfaceWebSocket {

    public void connectClient (String ip);
    public boolean sendMsg(double lat,double lng);
    public boolean isConnected();
    public int getIdClient();
    public void close();
}
