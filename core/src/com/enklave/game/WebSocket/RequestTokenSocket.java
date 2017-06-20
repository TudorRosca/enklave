package com.enklave.game.WebSocket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;

import org.json.JSONObject;

/**
 * Created by adrian on 18.03.2016.
 */
public class RequestTokenSocket {
    private static String api_url = "http://enklave-1720445391.us-west-2.elb.amazonaws.com";
    private static String d_login = "/socket/ticket/get/";
    public static void getTicket(final WebSocketLocal webSocket){
        final JSONObject[] finalResult = new JSONObject[1];
        String accesToken = Gdx.app.getPreferences("informationLog").getString("accesstoken");
        HttpRequestBuilder buider = new HttpRequestBuilder();
        Net.HttpRequest request = buider.newRequest().url(api_url + d_login).method(Net.HttpMethods.POST).build();
        request.setHeader("Authorization", "Bearer " + accesToken);
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String r = httpResponse.getResultAsString();
                webSocket.connectWebSocket(new JSONObject(r));
                Gdx.app.log("response", "ticket "+r);
            }

            @Override
            public void failed(Throwable t) {
                webSocket.connectWebSocket(new JSONObject());
            }

            @Override
            public void cancelled() {
                webSocket.connectWebSocket(new JSONObject());
            }
        });
    }

}
