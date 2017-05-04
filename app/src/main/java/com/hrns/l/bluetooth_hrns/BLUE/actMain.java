package com.hrns.l.bluetooth_hrns.BLUE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hrns.l.bluetooth_hrns.R;
import com.hrns.l.bluetooth_hrns.bluetooth.BluetoothCtrl;
import com.hrns.l.bluetooth_hrns.storage.CKVStorage;
import com.hrns.l.bluetooth_hrns.storage.CSharedPreferences;

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
        this.mtvDeviceInfo = (TextView) this.findViewById(R.id.actMain_tv_device_info);
        this.mtvServiceUUID = (TextView) this.findViewById(R.id.actMain_tv_service_uuid);
        this.mllDeviceCtrl = (LinearLayout) this.findViewById(R.id.actMain_ll_device_ctrl);
        this.mllChooseMode = (LinearLayout) this.findViewById(R.id.actMain_ll_choose_mode);
        this.mbtnPair = (Button) this.findViewById(R.id.actMain_btn_pair);
        this.mbtComm = (Button) this.findViewById(R.id.actMain_btn_conn);
        this.initActivityView();

        this.mGP = ((globalPool) this.getApplicationContext());

        //new startBluetoothDeviceTask().execute("");
    }

    private void initFirstInstallTimestemp(){
        CKVStorage oDS = new CSharedPreferences(this);
        if(oDS.getLongVal("SYSTEM", "FIRST_INSTALL_TIMESTEMP") == 0){
            oDS.setVal("SYSTEM", "FIRST_INSTALL_TIMESTEMP", System.currentTimeMillis()).saveStorage();
        }
    }

    private void initActivityView(){
        this.mllDeviceCtrl.setVisibility(View.GONE);
        this.mbtnPair.setVisibility(View.GONE);
        this.mbtComm.setVisibility(View.GONE);
        this.mllChooseMode.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mGP.closeConn();

        if(null != mBT && !this.mbBleStatusBefore)
            mBT.disable();
    }

    private void openDiscovery(){
        //Intent intent = new Intent(this, actDiscovery.class);
        //this.startActivityForResult(intent, REQUEST_DISCOVERY);
    }

    private void openAbout(){
        //Intent intent = new Intent(this, actAbout.class);
        //this.startActivityForResult(intent, REQUEST_ABOUT);
    }

    private void showDeviceInfo(){
        this.mtvDeviceInfo.setText(String.format(getString(R.string.actMain_device_info), this.mhtDeviceInfo.get("NAME"), this.mhtDeviceInfo.get("MAC"),this.mhtDeviceInfo.get("COD"), this.mhtDeviceInfo.get("RSSI"),this.mhtDeviceInfo.get("DEVICE_TYPE"),this.mhtDeviceInfo.get("BOND")));
    }

    private void showServiceUUIDs(){
        if(Build.VERSION.SDK_INT >= 15){
            new GetUUIDServiceTask().execute("");
        }else {
            this.mtvServiceUUID.setText(getString(R.string.actMain_msg_does_not_support_uuid_service));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_DISCOVERY){
            if(Activity.RESULT_OK == resultCode){
                this.mllDeviceCtrl.setVisibility(View.VISIBLE);
                this.mhtDeviceInfo.put("NAME", data.getStringExtra("NAME"));
                this.mhtDeviceInfo.put("MAC", data.getStringExtra("MAC"));
                this.mhtDeviceInfo.put("COD", data.getStringExtra("COD"));
                this.mhtDeviceInfo.put("RSSI", data.getStringExtra("RSSI"));
                this.mhtDeviceInfo.put("DEVICE_TYPE", data.getStringExtra("DEVICE_TYPE"));
                this.mhtDeviceInfo.put("BOND", data.getStringExtra("BOND"));
                this.showDeviceInfo();
                if(this.mhtDeviceInfo.get("BOND").equals(getString(R.string.actDiscovery_bond_nothing))){
                    this.mbtnPair.setVisibility(View.VISIBLE);
                    this.mbtComm.setVisibility(View.VISIBLE);
                    this.mtvServiceUUID.setText(getString(R.string.actMain_tv_hint_service_uuid_not_bond));
                }else {
                    this.mBDevice = this.mBT.getRemoteDevice(this.mhtDeviceInfo.get("MAC"));
                    this.showServiceUUIDs();
                    this.mbtnPair.setVisibility(View.GONE);
                    this.mbtComm.setVisibility(View.VISIBLE);
                }
            }else if(Activity.RESULT_CANCELED == resultCode){
                this.finish();
            }
        }else if(REQUEST_BYTE_STREAM == requestCode || REQUEST_CMD_LINE == requestCode || REQUEST_KEY_BOARD == requestCode){
            if(null == this.mGP.mBSC || !this.mGP.mBSC.isConnect()){
                this.mllChooseMode.setVisibility(View.GONE);
                this.mbtComm.setVisibility(View.VISIBLE);
                this.mGP.closeConn();
                Toast.makeText(this, getString(R.string.msg_msg_bt_connect_lost), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClickBtnPair(View v){
        new PairTask().execute(this.mhtDeviceInfo.get("MAC"));
        this.mbtnPair.setEnabled(false);
    }

    public void onClickBtnConn(View v){
        new connSocketTask().execute(this.mBDevice.getAddress());
    }

    public void onClickBtnSerialStreamMode(View v){
       // Intent intent = new Intent(this, actByteStream.class);
        //this.startActivityForResult(intent, REQUEST_BYTE_STREAM);
    }

    public void onClickBtnbKeyBoardMode(View v){
        Intent intent = new Intent(this, actRealTime.class);
        this.startActivityForResult(intent, REQUEST_KEY_BOARD);
    }

    public void onClickBtnCommandLine(View v){
        //Intent intent = new Intent(this, actCmdLine.class);
        //this.startActivityForResult(intent, REQUEST_CMD_LINE);
    }

    private class startBluetoothDeviceTask extends AsyncTask<String, String, Integer>{
        private static final int RET_BLUETOOTH_IS_START = 0x0001;
        private static final int RET_BLUETOOTH_START_FAIL = 0x04;
        private static final int miWATI_TIME = 15;
        private static final int miSLEEP_TIME = 150;
        private ProgressDialog mpd;

        @Override
        public void onPreExecute(){
            mpd = new ProgressDialog(actMain.this);
            mpd.setMessage(getString(R.string.actDiscovery_msg_starting_device));
            mpd.setCancelable(false);
            mpd.setCanceledOnTouchOutside(false);
            mpd.show();
            mbBleStatusBefore = mBT.isEnabled();
        }

        @Override
        protected Integer doInBackground(String... arg0){
            int iWait = miWATI_TIME * 1000;
            if(!mBT.isEnabled()){
                mBT.enable();
                while(iWait > 0){
                    if(!mBT.isEnabled())
                        iWait -= miSLEEP_TIME;
                    else
                        break;
                    SystemClock.sleep(miSLEEP_TIME);
                }
                if(iWait < 0)
                    return RET_BLUETOOTH_START_FAIL;
            }
            return RET_BLUETOOTH_IS_START;
        }

        @Override
        public void onPostExecute(Integer result){
            if(mpd.isShowing())
                mpd.dismiss();
            if(RET_BLUETOOTH_START_FAIL == result){
                AlertDialog.Builder builder = new AlertDialog.Builder(actMain.this);
                builder.setTitle(getString(R.string.actDiscovery_msg_start_bluetooth_fail));
                builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        mBT.disable();
                        finish();
                    }
                });
                builder.create().show();

            }else if(RET_BLUETOOTH_IS_START == result){
                openDiscovery();
            }
        }
    }

    private class PairTask extends AsyncTask<String, String, Integer> {
        static private final int RET_BOND_OK = 0x00;

        static private final int RET_BOND_FAIL = 0x01;

        static private final int iTIMEOUT = 1000 * 10;

        @Override
        public void onPreExecute(){
            Toast.makeText(actMain.this, getString(R.string.actMain_msg_bluetooth_Bonding),Toast.LENGTH_SHORT).show();
            registerReceiver(_mPairingRequest, new IntentFilter(BluetoothCtrl.PAIRING_REQUEST));
            registerReceiver(_mPairingRequest, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        }

        @Override
        protected Integer doInBackground(String... arg0){
            final int iStepTime = 150;
            int iWait = iTIMEOUT;
            try{
                mBDevice = mBT.getRemoteDevice(arg0[0]);
                BluetoothCtrl.createBond(mBDevice);
                mbBonded = false;
            }catch (Exception e1){
                Log.d(getString(R.string.app_name), "create Bond failed!");
                e1.printStackTrace();
                return RET_BOND_FAIL;
            }
            while(!mbBonded && iWait > 0){
                SystemClock.sleep(iStepTime);
                iWait -= iStepTime;
            }
            return (int) ((iWait > 0)? RET_BOND_OK : RET_BOND_FAIL);
        }

        @Override
        public void onPostExecute(Integer result){
            unregisterReceiver(_mPairingRequest);

            if (RET_BOND_OK == result){
                Toast.makeText(actMain.this,
                        getString(R.string.actMain_msg_bluetooth_Bond_Success),
                        Toast.LENGTH_SHORT).show();
                mbtnPair.setVisibility(View.GONE);
                mbtComm.setVisibility(View.VISIBLE);
                mhtDeviceInfo.put("BOND", getString(R.string.actDiscovery_bond_bonded));
                showDeviceInfo();
                showServiceUUIDs();
            }else{
                Toast.makeText(actMain.this, getString(R.string.actMain_msg_bluetooth_Bond_fail),Toast.LENGTH_LONG).show();
                try{
                    BluetoothCtrl.removeBond(mBDevice);
                }catch (Exception e){
                    Log.d(getString(R.string.app_name), "removeBond failed!");
                    e.printStackTrace();
                }
                mbtnPair.setEnabled(true);
            }
        }

    }

    private class GetUUIDServiceTask extends AsyncTask<String, String, Integer> {
        private static final int miWATI_TIME = 4 * 1000;

        private static final int miREF_TIME = 200;

        private boolean mbFindServiceIsRun = false;

        @Override
        public void onPreExecute() {
            mslUuidList.clear();
            mtvServiceUUID.setText(getString(R.string.actMain_find_service_uuids));
            registerReceiver(_mGetUuidServiceReceiver, new IntentFilter(BluetoothDevice.ACTION_UUID));
            this.mbFindServiceIsRun = mBDevice.fetchUuidsWithSdp();
        }

        @Override
        protected Integer doInBackground(String... arg0) {
            int iWait = miWATI_TIME;
            if (!this.mbFindServiceIsRun)
                return null;
            while (iWait > 0) {
                if (mslUuidList.size() > 0 && iWait > 1500)
                    iWait = 1500;
                SystemClock.sleep(miREF_TIME);
                iWait -= miREF_TIME;
            }
            return null;
        }

        @Override
        public void onPostExecute(Integer result) {
            StringBuilder sbTmp = new StringBuilder();
            unregisterReceiver(_mGetUuidServiceReceiver);
            if (mslUuidList.size() > 0) {
                for (int i = 0; i < mslUuidList.size(); i++)
                    sbTmp.append(mslUuidList.get(i) + "\n");
                mtvServiceUUID.setText(sbTmp.toString());
            } else
                mtvServiceUUID.setText(R.string.actMain_not_find_service_uuids);
        }
    }

    private class connSocketTask extends AsyncTask<String, String, Integer>{
        private ProgressDialog mpd = null;
        private static final int CONN_FAIL = 0x01;
        private static final int CONN_SUCCESS = 0x02;


        @Override
        public void onPreExecute(){
            this.mpd = new ProgressDialog(actMain.this);
            this.mpd.setMessage(getString(R.string.actMain_msg_device_connecting));
            this.mpd.setCancelable(false);
            this.mpd.setCanceledOnTouchOutside(false);
            this.mpd.show();
        }
        @Override
        protected Integer doInBackground(String... arg0){
            if (mGP.createConn(arg0[0]))
                return CONN_SUCCESS;
            else
                return CONN_FAIL;
        }


        @Override
        public void onPostExecute(Integer result){
            this.mpd.dismiss();

            if (CONN_SUCCESS == result){
                mbtComm.setVisibility(View.GONE);
                mllChooseMode.setVisibility(View.VISIBLE);
                Toast.makeText(actMain.this,
                        getString(R.string.actMain_msg_device_connect_succes),
                        Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(actMain.this,
                        getString(R.string.actMain_msg_device_connect_fail),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    }
