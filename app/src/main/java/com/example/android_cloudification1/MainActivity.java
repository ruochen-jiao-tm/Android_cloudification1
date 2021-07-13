package com.example.android_cloudification1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android_cloudification1.mqtt.MQTTService;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private LocalBroadcastManager localBroadcastManager;
    private MyBroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastReceiver = new MyBroadcastReceiver();
        intentFilter = new IntentFilter("myaction");
        localBroadcastManager.registerReceiver(broadcastReceiver,intentFilter);
        Log.i("broadcast","end");
        //start service
        Intent intent = new Intent(this, MQTTService.class);
        startService(intent);

        Button publishButton = findViewById(R.id.publishButton);

        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //QoS=0，only send once, may be lost
                //QoS=1, at least send once and may duplicate
                //QoS=2, only send once and make sure only one will be sent successfully
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setId((int) Math.random());
                mqttMessage.setQos(1);
                mqttMessage.setPayload("test_publish".getBytes());

                /*set retain*/
                mqttMessage.setRetained(false);

                // set the publish, subscribe and broadcast
                try {
                    MQTTService.mqttClient.publish("testtopic/12",mqttMessage);
                    Intent bintent = new Intent("myaction");
                    bintent.putExtra("data","activity to service");
                    localBroadcastManager.sendBroadcast(bintent);
                    MQTTService.mqttClient.subscribe("testtopic/#");

                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // define the onReceive class
    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            if("myaction".equals(action)){
                Log.i("message:" , intent.getStringExtra("data"));
            }
        }
    }
}