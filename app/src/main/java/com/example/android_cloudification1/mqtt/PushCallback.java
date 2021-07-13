package com.example.android_cloudification1.mqtt;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class PushCallback implements MqttCallbackExtended {
    private static final String TAG = "PusherCallback";
    private MqttClient mqttClient = null;

    public PushCallback(MqttClient client) {
        mqttClient = client;
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.i(TAG, "connection fails");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String msg = new String(message.getPayload());
        Log.i(TAG, "message arrived,message: " + msg);

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.i(TAG, "message sent");

    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        Log.i(TAG, "connection setup");

    }
}
