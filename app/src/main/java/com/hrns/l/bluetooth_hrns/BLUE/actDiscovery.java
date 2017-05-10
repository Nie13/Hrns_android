package com.hrns.l.bluetooth_hrns.BLUE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hrns.l.bluetooth_hrns.R;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Created by l on 5/4/2017.
 */

public class actDiscovery extends Activity {
    public static final int MENU_SCAN = 1;
    public static final int MENU_QUIT = 2;
    public static final int DEVICE_TYPE_BREDER = 0x01;
    public static final int DEVICE_TYPE_BLE = 0x02;
    public static final int DEVICE_TYPE_DUMO = 0x03;

    public final static String EXTRA_DEVICE_TYPE = "andorid.bluetooth.device.extra.DEVICE_TYPE";
    private boolean _discoveryFinished;

    private BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();
    private ListView mlvList = null;

    private Button mbtnRescan = null;
    private Button mbtnConn = null;

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

            showDevices();
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
        setContentView(R.layout.act_discovery);
        this.mbtnRescan = (Button)this.findViewById(R.id.discovery_btn_rescan);
        this.mbtnConn = (Button)this.findViewById(R.id.discovery_btn_connect);
        this.mbtnConn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sMAC = ((TextView)v.findViewById(R.id.device_item_ble_mac)).getText().toString();
                Intent result = new Intent();
                result.putExtra("MAC", sMAC);
                result.putExtra("RSSI", mhtFDS.get(sMAC).get("RSSI"));
                result.putExtra("NAME",mhtFDS.get(sMAC).get("NAME"));
                result.putExtra("COD",mhtFDS.get(sMAC).get("COD"));
                result.putExtra("BOND",mhtFDS.get(sMAC).get("BOND"));
                result.putExtra("DEVICE_TYPE",toDeviceTypeString(mhtFDS.get(sMAC).get("DEVICE_TYPE")));
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });

        new scanDeviceTask().execute("");
        /*
        this.mlvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sMAC = "MAC";
                Intent result = new Intent();
                result.putExtra("MAC",sMAC);
            }
        });*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuItem miScan = menu.add(0, MENU_SCAN, 0, getString(R.string.actDiscovery_btn_rescan));
        MenuItem miClose = menu.add(0, MENU_QUIT, 1, getString(R.string.menu_close));
        miScan.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        miClose.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item){
        switch (item.getItemId()){
            case MENU_SCAN:
                new scanDeviceTask().execute("");
                return true;
            case MENU_QUIT:
                this.setResult(Activity.RESULT_CANCELED, null);
                this.finish();
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mBT.isDiscovering())
            mBT.cancelDiscovery();
    }

    private void startSearch(){
        _discoveryFinished = false;
        if(null == mhtFDS)
            this.mhtFDS= new Hashtable<String, Hashtable<String, String>>();
        else
            this.mhtFDS.clear();

        IntentFilter discoveryFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(_finishedReceiver,discoveryFilter);
        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(_foundReceiver, foundFilter);
        mBT.startDiscovery();
        this.showDevices();
    }

    private String toDeviceTypeString(String sDeviceTypeId){
        Pattern pt = Pattern.compile("^[-\\+]?[\\d]+$");
        if(pt.matcher(sDeviceTypeId).matches()){
            switch (Integer.valueOf(sDeviceTypeId)){
                case DEVICE_TYPE_BREDER:
                    return getString(R.string.device_type_bredr);
                case DEVICE_TYPE_BLE:
                    return getString(R.string.device_type_ble);
                case DEVICE_TYPE_DUMO:
                    return getString(R.string.device_type_dumo);
                default:
                    return getString(R.string.device_type_bredr);
            }
        }else
            return sDeviceTypeId;
    }

    protected void showDevices(){
        if(null == this.malListItem)
            this.malListItem = new ArrayList<HashMap<String, Object>>();
        if(null == this.msaListItemAdapter){
            this.msaListItemAdapter = new SimpleAdapter(this, malListItem, R.layout.act_discovery, new String[] {"NAME", "MAC", "COD", "RSSI", "DEVICE_TYPE", "BOND"}, new int[] {R.id.device_item_ble_name, R.id.device_item_ble_mac, R.id.device_item_ble_cod, R.id.device_item_ble_rssi, R.id.device_item_ble_device_type, R.id.device_item_ble_bond});
            this.mlvList.setAdapter(this.msaListItemAdapter);
        }
        this.malListItem.clear();
        Enumeration<String> e = this.mhtFDS.keys();
        while(e.hasMoreElements()){
            HashMap<String, Object> map = new HashMap<String, Object>();
            String sKey = e.nextElement();
            map.put("MAC", sKey);
            map.put("NAME", this.mhtFDS.get(sKey).get("NAME"));
            map.put("RSSI", this.mhtFDS.get(sKey).get("RSSI"));
            map.put("COD", this.mhtFDS.get(sKey).get("COD"));
            map.put("BOND", this.mhtFDS.get(sKey).get("BOND"));
            map.put("DEVICE_TYPE", toDeviceTypeString(this.mhtFDS.get(sKey).get("DEVICE_TYPE")));
            this.malListItem.add(map);
        }
        this.msaListItemAdapter.notifyDataSetChanged();

    }

    private class scanDeviceTask extends AsyncTask<String, String, Integer>{
        private static final int RET_BLUETOOTH_NOT_START = 0x0001;
        private static final int RET_SCAN_DEVICE_FINISHED = 0x0002;
        private static final int miWATI_TIME = 10;
        private static final int miSLEEP_TIME = 150;
        private ProgressDialog mpd = null;

        @Override
        public void onPreExecute(){
            this.mpd = new ProgressDialog(actDiscovery.this);
            this.mpd.setMessage(getString(R.string.actDiscovery_msg_scaning_device));
            this.mpd.setCancelable(true);
            this.mpd.setCanceledOnTouchOutside(true);
            this.mpd.setOnCancelListener(new DialogInterface.OnCancelListener(){
                @Override
                public void onCancel(DialogInterface dialog){
                    _discoveryFinished = true;
                }
            });
            this.mpd.show();
            startSearch();
        }
        @Override
        protected Integer doInBackground(String... params){
            if(!mBT.isEnabled())
                return RET_BLUETOOTH_NOT_START;
            int iWait = miWATI_TIME * 1000;
            while(iWait > 0){
                if(_discoveryFinished)
                    return RET_SCAN_DEVICE_FINISHED;
                else
                    iWait -= miSLEEP_TIME;
                SystemClock.sleep(miSLEEP_TIME);
            }
            return RET_SCAN_DEVICE_FINISHED;
        }

        @Override
        public void onPostExecute(Integer result){
            if(this.mpd.isShowing())
                this.mpd.dismiss();
            if(mBT.isDiscovering())
                mBT.cancelDiscovery();
            if(RET_SCAN_DEVICE_FINISHED == result){

            }else if(RET_BLUETOOTH_NOT_START == result){
                Toast.makeText(actDiscovery.this, getString(R.string.actDiscovery_msg_start_bluetooth_fail), Toast.LENGTH_SHORT).show();
            }
        }
    }



}
