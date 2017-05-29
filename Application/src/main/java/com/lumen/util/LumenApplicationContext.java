package com.lumen.util;


public class LumenApplicationContext {

    public enum BroadcastRecieverClients{
        UI_ACTIVITY,
        SERVICE
    }

    private static  BroadcastRecieverClients broadcastRecieverClient;

    public static BroadcastRecieverClients getBroadcastRecieverClient(){
        return broadcastRecieverClient;
    }

    public static void setBroadcastRecieverClient(BroadcastRecieverClients client){
        broadcastRecieverClient = client;
    }

}
