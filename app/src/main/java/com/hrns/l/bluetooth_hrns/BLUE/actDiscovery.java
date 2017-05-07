package com.hrns.l.bluetooth_hrns.BLUE;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Objects;

/**
 * Created by l on 5/4/2017.
 */

public class actDiscovery extends Activity {
    public static final int MENU_SCAN = 1;
    public static final int MENU_QUIT = 2;
    public static final int DEVICE_TYPE_BREDER = 0x01;
    public static final int DEVICE_TYPE_BLE = 0x02;
    public static final int DEVICE_TYPE = 0x03;

    public final static String EXTRA_DEVICE_TYPE = "andorid.bluetooth.device.extra.DEVICE_TYPE";
    private boolean _discoveryFinished;

    private BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();
    private ListView mlvList = null;

    private Hashtable<String, Hashtable<String, String>> mhtFDB = null;
    private ArrayList<HashMap<String, Object>> malListItem = null;
    private SimpleAdapter msaListItemAdapter = null;
    private BroadcastReceiver _foundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }


}
