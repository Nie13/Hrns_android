package com.hrns.l.bluetooth_hrns.BLUE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hrns.l.bluetooth_hrns.R;
import com.hrns.l.bluetooth_hrns.bluetooth.BluetoothSppClient;
import com.hrns.l.bluetooth_hrns.storage.CJsonStorage;
import com.hrns.l.bluetooth_hrns.storage.CKVStorage;



import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by l on 5/3/2017.
 */

public class BaseCommActivity extends BaseActivity {
    protected final static byte MENU_CLEAR = 0x01;
    protected final static byte MENU_IO_MODE = 0x02;
    protected final static byte MENU_SAVE_TO_FILE = 0x03;
    protected final static byte MENU_CLEAR_CMD_HISTORY = 0x04;
    protected final static byte MENU_HELPER = 0x05;
    protected final static String KEY_IO_MODE = "key_io_mode";
    protected final static String[] msEND_FLGS = {"\r\n","\n"};
    protected static final String HISTORY_SPLIT = "&#&";
    protected static final String KEY_HISTORY = "send_history";
    protected ArrayList<String> malCmdHistory = new ArrayList<String>();

    protected boolean mbThreadStop = false;
    private TextView mtvTxdCount = null;
    private TextView mtvRxdCount = null;
    private TextView mtvHoleRun = null;
    protected byte mbtInputMode = BluetoothSppClient.IO_MODE_STRING;
    protected byte mbtOutputMode = BluetoothSppClient.IO_MODE_STRING;
    protected BluetoothSppClient mBSC = null;
    protected CKVStorage mDS = null;

    protected static ExecutorService FULL_TASK_EXECUTOR;
    static{
        FULL_TASK_EXECUTOR = (ExecutorService) Executors.newCachedThreadPool();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.mBSC = ((globalPool)this.getApplicationContext()).mBSC;
        this.mDS = ((globalPool) this.getApplicationContext()).mDS;

        if(null == this.mBSC || this.mBSC.isConnect()){
            this.setResult(Activity.RESULT_CANCELED);
            this.finish();
            return;
        }
    }

    protected void usedDataCount(){
        //this.mtvTxdCount = (TextView)this.findViewById(R.id.tv_txd_count);
        //this.mtvRxdCount = (TextView)this.findViewById(R.id.tv_rxd_count);
        //this.mtvHoleRun = (TextView)this.findViewById(R.id.tv_connect_hold_time);
        //this.refreshTxdCount();
        //this.refreshRxdCount();
        //this.refreshHoldTime();
    }

    protected void refreshTxdCount(){
        long lTmp = 0;
        if(null != this.mtvTxdCount){
            lTmp = this.mBSC.getTxd();
            //this.mtvTxdCount.setText(String.format(getString(R.string.templet_txd, lTmp)));
            lTmp = this.mBSC.getConnectHoldTime();
            //this.mtvHoleRun.setText(String.format(getString(R.string.templet_hole_time, lTmp)));
        }
    }

    protected void refreshRxdCount(){
        long lTmp = 0;
        if(null != this.mtvRxdCount){
            lTmp = this.mBSC.getRxd();
            //this.mtvRxdCount.setText(String.format(getString(R.string.templet_rxd, lTmp)));
            lTmp = this.mBSC.getConnectHoldTime();
            //this.mtvHoleRun.setText(String.format(getString(R.string.templet_hole_time, lTmp)));
        }
    }

    protected void refreshHoldTime(){
        if(null != this.mtvHoleRun){
            long lTmo = this.mBSC.getConnectHoldTime();
            //this.mtvHoleRun.setText(String.format(getString(R.string.templet_hole_time, lTmo)));
        }
    }

    protected void initIO_Mode(){
        this.mbtInputMode = (byte)this.mDS.getIntVal(KEY_IO_MODE,"input_mode");
        if(this.mbtInputMode == 0)
            this.mbtInputMode = BluetoothSppClient.IO_MODE_STRING;
        this.mbtOutputMode = (byte)this.mDS.getIntVal(KEY_IO_MODE,"output_mode");
        if(this.mbtOutputMode == 0)
            this.mbtOutputMode = BluetoothSppClient.IO_MODE_STRING;
        mBSC.setRxdMode(mbtInputMode);
        mBSC.setTxdMode(mbtOutputMode);
    }

    protected void setIOModeDialog(){
        final RadioButton rbInChar, rbInHex;
        final RadioButton rbOutChar, rbOutHex;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.dialog_title_io_mode_set));
        LayoutInflater inflater = LayoutInflater.from(this);

        final View view = inflater.inflate(R.layout.dialog_io_mode, null);
        rbInChar =(RadioButton)view.findViewById(R.id.rb_io_mode_set_in_string);
        rbInHex =(RadioButton)view.findViewById(R.id.rb_io_mode_set_in_hex);
        rbOutChar =(RadioButton)view.findViewById(R.id.rb_io_mode_set_out_string);
        rbOutHex =(RadioButton)view.findViewById(R.id.rb_io_mode_set_out_hex);


        if (BluetoothSppClient.IO_MODE_STRING == this.mbtInputMode)
            rbInChar.setChecked(true);
        else
            rbInHex.setChecked(true);
        if (BluetoothSppClient.IO_MODE_STRING == this.mbtOutputMode)
            rbOutChar.setChecked(true);
        else
            rbOutHex.setChecked(true);

        builder.setView(view);
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){

                mbtInputMode = (rbInChar.isChecked())? BluetoothSppClient.IO_MODE_STRING : BluetoothSppClient.IO_MODE_HEX;
                mbtOutputMode = (rbOutChar.isChecked())? BluetoothSppClient.IO_MODE_STRING : BluetoothSppClient.IO_MODE_HEX;

                mDS.setVal(KEY_IO_MODE, "input_mode", mbtInputMode)
                        .setVal(KEY_IO_MODE, "output_mode", mbtOutputMode)
                        .saveStorage();
                mBSC.setRxdMode(mbtInputMode);
                mBSC.setTxdMode(mbtOutputMode);
            }
        });
        builder.create().show();
    }

    protected void saveAutoComplateCmdHistory(String sClass){
        CKVStorage kvAutoComplate = new CJsonStorage(this, getString(R.string.app_name), "AutoComplateList");
        if(malCmdHistory.isEmpty())
            kvAutoComplate.setVal(KEY_HISTORY, sClass, "").saveStorage();
        else{
            StringBuilder sbBuf = new StringBuilder();
            String sTmp = null;
            for(int i=0; i<malCmdHistory.size(); i++)
                sbBuf.append(malCmdHistory.get(i) + HISTORY_SPLIT);
            sTmp = sbBuf.toString();
            kvAutoComplate.setVal(KEY_HISTORY, sClass, sTmp.substring(0, sTmp.length()-3)).saveStorage();
        }
        kvAutoComplate = null;
    }

    protected void loadAutoComplateCmdHistory(String sClass, AutoCompleteTextView v){
        CKVStorage kvAutoComplate = new CJsonStorage(this, getString(R.string.app_name), "AutoComplateList");
        String sTmp = kvAutoComplate.getStringVal(KEY_HISTORY, sClass);
        kvAutoComplate = null;
        if(!sTmp.equals("")){
            String[] sT = sTmp.split(HISTORY_SPLIT);
            for (int i=0;i<sT.length; i++)
                this.malCmdHistory.add(sT[i]);
            v.setAdapter(
                    new ArrayAdapter<String>(this,
                            android.R.layout.simple_dropdown_item_1line,sT)
            );
        }
    }

    protected void addAutoComplateVal(String sData, AutoCompleteTextView v){
        if (this.malCmdHistory.indexOf(sData) == -1){
            this.malCmdHistory.add(sData);
            v.setAdapter(
                    new ArrayAdapter<String>(this,
                            android.R.layout.simple_dropdown_item_1line,
                            malCmdHistory.toArray(new String[malCmdHistory.size()]))
            );
        }
    }

    protected void clearAutoComplate(AutoCompleteTextView v){
        this.malCmdHistory.clear();
        v.setAdapter(
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_dropdown_item_1line)
        );
    }

}
