package com.example.android_cloudification1.mqtt;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class MQTTService extends Service {
    private static final String TAG = "MQTTService";
    public static final String BROKER_URL = "tcp://10.0.2.2:1883";
    public static final String CLIENT_ID = "gongzhonghaobadaodecehngxvyuan";

    public static MqttClient mqttClient;
    //mqtt connection settings
    private MqttConnectOptions mqttOptions;
    private String username = "admin";
    private String password = "public";

    private LocalBroadcastManager localBroadcastManager ;
    private MyBroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    public MQTTService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: begin");

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        MyBroadcastReceiver broadcastReceiver = new MyBroadcastReceiver();
        intentFilter = new IntentFilter("myaction");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        Intent bintent = new Intent("myaction");
        bintent.putExtra("data","service to activity");

        localBroadcastManager.sendBroadcast(bintent);
        try {
            //the third para means making the service persistent
            mqttClient = new MqttClient(BROKER_URL, CLIENT_ID, new MemoryPersistence());
            //mqtt connection setting
            mqttOptions = new MqttConnectOptions();
            mqttOptions.setUserName(username);
            mqttOptions.setPassword(password.toCharArray());
            //overtime: s
            mqttOptions.setConnectionTimeout(10);
            mqttOptions.setKeepAliveInterval(20);
            //false means can receive offline message
            mqttOptions.setCleanSession(false);
            mqttOptions.setAutomaticReconnect(true);
            // callback
            mqttClient.setCallback(new PushCallback(mqttClient));
            Log.i(TAG, "onStartCommand: before connect");

//            MqttTopic topic1 = mqttClient.getTopic(TOPIC);
//            mqttOptions.setWill(topic1, "close".getBytes(), 2, true);
            mqttClient.connect(mqttOptions);
            Log.i(TAG, "onStartCommand: after connect");
            Log.i(TAG, "connection sets up");

        } catch (MqttException e) {
            e.printStackTrace();
            Log.i(TAG, e.getMessage());
        }

        return super.onStartCommand(intent, flags, startId);
    }
    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            if("myaction".equals(action)){
                Log.i("message:", intent.getStringExtra("data"));
            }
        }
    }
    @Override
    public void onDestroy() {
        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
