package com.hrns.l.bluetooth_hrns.BLUE;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hrns.l.bluetooth_hrns.R;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by l on 4/29/2017.
 */

public class actMain extends Activity{
    public static final byte MENU_RESCAN = 0x01;

    public static final byte MENU_EXIT = 0x02;

    public static final byte MENU_ABOUT = 0x03;

    private globalPool mGP = null;

    private BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();

    private BluetoothDevice mBDevice = null;

    private TextView mtvDeviceInfo = null;

    private TextView mtvServiceUUID = null;

    private LinearLayout mllDeviceCtrl = null;

    private LinearLayout mllChooseMode = null;

    private Button mbtnPair = null;

    private Button mbtComm = null;

    public static final byte REQUEST_DISCOVERY = 0x01;

    public static final byte REQUEST_BYTE_STREAM = 0x02;

    public static final byte REQUEST_CMD_LINE = 0x03;

    public static final byte REQUEST_KEY_BOARD = 0x04;

    public static final byte REQUEST_ABOUT = 0x05;

    private Hashtable<String, String > mhtDeviceInfo = new Hashtable<String, String >();

    private boolean mbBonded = false;

    private ArrayList<String> mslUuidList = new ArrayList<String>();

    private boolean mbBleStatusBefore = false;

    private BroadcastReceiver _mGetUuidServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int iLoop = 0;
            if(BluetoothDevice.ACTION_UUID.equals(action)){
                Parcelable[] uuidExtra = intent.getParcelableArrayExtra("android.bluetooth.device.extra.UUID");
                if(null != uuidExtra)
                    iLoop = uuidExtra.length;
                for(int i = 0; i <iLoop; i++)
                    mslUuidList.add(uuidExtra[i].toString());
            }
        }
    };

    private BroadcastReceiver _mPairingRequest = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = null;
            if(intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState() == BluetoothDevice.BOND_BONDED)
                    mbBonded = true;
                else
                    mbBonded = false;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        MenuItem miScan = menu.add(0, MENU_RESCAN, 0, getString(R.string.actMain_menu_rescan));
        miScan.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        MenuItem miAbout = menu.add(0, MENU_ABOUT, 1, getString(R.string.menu_about));
        miAbout.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        MenuItem miExit = menu.add(0, MENU_EXIT, 2, getString(R.string.menu_close));
        miExit.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onMenuItemSelected(int featuredId, MenuItem item){
        switch (item.getItemId()){
            case MENU_RESCAN:
                this.mGP.closeConn();
                this.initActivityView();
                this.openDiscovery();
                return true;
            case MENU_EXIT:
                this.finish();
                return true;
            case MENU_ABOUT:
                this.openAbout();
                return true;
            default:
                return super.onMenuItemSelected(featuredId, item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        if(null == mBT){
            Toast.makeText(this, "Bluetooth not found", Toast.LENGTH_LONG).show();
            this.finish();
        }

        this.initFirstInstallTimestemp();
        this.mtvDeviceInfo = (TextView) this.findViewById(R.id.actMain)
    }


}
