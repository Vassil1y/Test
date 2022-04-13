package com.example.wi_fiandmuting;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = null;

    public static List<String> Net_List = new ArrayList();
    public static boolean FlagServ=false;

    public static int last_mod;
    private AudioManager myAudioManager;

    ArrayAdapter<String> adapter;

    ListView list;
    Button btn_st, btn_inp;
    EditText edText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Permission_ask();// Запрос разрешений

        list = findViewById(R.id.list);
        edText = findViewById(R.id.input_ssid);
        btn_st = findViewById(R.id.check);
        btn_inp = findViewById(R.id.btn_add);

        broadcastReceiver = new InternetCheckService();
        myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Net_List);
        list.setAdapter(adapter);

        btn_st.setText("Запустить");
        btn_st.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!FlagServ){
                    FlagServ=!FlagServ;
                    last_mod = myAudioManager.getRingerMode();
                    btn_st.setText("Остановить");
                    registerReceiver(broadcastReceiver,
                            new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                }
                else{
                    FlagServ=!FlagServ;
                    btn_st.setText("Запустить");
                    last_mod = myAudioManager.getRingerMode();
                    unregisterReceiver(broadcastReceiver);
                }
            }
        });//  Обработка кнопки запуска/выключения ресивера

        btn_inp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txt = edText.getText().toString();
                if(!txt.equals("") && !txt.equals(" ")){
                    Net_List.add(edText.getText().toString());
                    adapter.notifyDataSetChanged();
                    edText.setText("");
                    if(FlagServ){
                        unregisterReceiver(broadcastReceiver);
                        registerReceiver(broadcastReceiver,
                                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                    }
                }
            }
        });// Обработка кнопки ввода SSID

        list.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                Net_List.remove(pos);
                adapter.notifyDataSetChanged();
                return true;
            }
        });//     Удаление элемента из списка долгим нажатием

    }
    void Permission_ask(){
        int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE
                    }, 1);}
        NotificationManager n = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if(!n.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivityForResult(intent, 1);
        }
    }
}
