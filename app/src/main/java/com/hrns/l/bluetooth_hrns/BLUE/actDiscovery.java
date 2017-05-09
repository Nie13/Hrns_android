package com.hrns.l.bluetooth_hrns.BLUE;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.hrns.l.bluetooth_hrns.R;

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

    private Hashtable<String, Hashtable<String, String>> mhtFDS = null;
    private ArrayList<HashMap<String, Object>> malListItem = null;
    private SimpleAdapter msaListItemAdapter = null;
    private BroadcastReceiver _foundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Hashtable<String, String> htDeviceInfo = new Hashtable<String, String>();
            Log.d(getString(R.string.app_name), ">>Scan for Bluetooth devices");
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Bundle b = intent.getExtras();
            htDeviceInfo.put("RSSI", String.valueOf(b.get(BluetoothDevice.EXTRA_RSSI)));
            if(null == device.getName())
                htDeviceInfo.put("NAME", "Null");
            else
                htDeviceInfo.put("NAME", device.getName());

            htDeviceInfo.put("COD", String.valueOf(b.get(BluetoothDevice.EXTRA_CLASS)));
            if(device.getBondState() == BluetoothDevice.BOND_BONDED)
                htDeviceInfo.put("BOND",getString(R.string.actDiscovery_bond_bonded));
            else
                htDeviceInfo.put("BOND", getString(R.string.actDiscovery_bond_nothing));
            String sDeviceType = String.valueOf(b.get(EXTRA_DEVICE_TYPE));
            if(!sDeviceType.equals("null"))
                htDeviceInfo.put("DEVICE_TYPE", sDeviceType);
            else
                htDeviceInfo.put("DEVICE_TYPE", "-1");

            mhtFDS.put(device.getAddress(), htDeviceInfo);

            //showDevices();
        }
    };

    private BroadcastReceiver _finishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(getString(R.string.app_name), ">>Bluetooth scanning is finished");
            _discoveryFinished = true;
            unregisterReceiver(_foundReceiver);
            unregisterReceiver(_finishedReceiver);

            if (null != mhtFDS && mhtFDS.size() > 0){
                Toast.makeText(actDiscovery.this, getString(R.string.actDiscovery_msg_not_find_device), Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.);
        this.mlvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sMAC = "MAC";
                Intent result = new Intent();
                result.putExtra("MAC",sMAC);
            }
        });
    }


}
