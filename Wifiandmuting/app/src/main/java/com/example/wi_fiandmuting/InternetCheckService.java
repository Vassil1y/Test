package com.example.wi_fiandmuting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class InternetCheckService extends BroadcastReceiver {
    List<String> Net_List = MainActivity.Net_List;
    String Wf = "Wifi connected";

    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtil.getNetworkState(context);

        AudioManager myAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        Log.e("TTT", status);
        if(status.equals(Wf)) {
            WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
            Log.d("TTT", ssid);

            if (ssid.startsWith("\"") && ssid.endsWith("\"") && ssid.length() > 2) {
                ssid = ssid.substring(1, ssid.length() - 1);}

            if(Net_List.contains(ssid) && myAudioManager.getRingerMode()!=AudioManager.RINGER_MODE_SILENT){
                MainActivity.last_mod = myAudioManager.getRingerMode();
                myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
        }else{myAudioManager.setRingerMode(MainActivity.last_mod);}

    }
}
