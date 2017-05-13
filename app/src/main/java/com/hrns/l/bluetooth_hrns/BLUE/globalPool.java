package com.hrns.l.bluetooth_hrns.BLUE;

import android.app.Application;

import com.hrns.l.bluetooth_hrns.bluetooth.BluetoothSppClient;
import com.hrns.l.bluetooth_hrns.storage.CJsonStorage;
import com.hrns.l.bluetooth_hrns.storage.CKVStorage;

/**
 * Created by l on 4/29/2017.
 */

public class globalPool extends Application {
    public BluetoothSppClient mBSC = null;

    public CKVStorage mDS = null;

    @Override
    public void onCreate(){
        super.onCreate();
        this.mDS = new CJsonStorage(this, getString(R.string.app_name));
    }

    public boolean createConn(String sMac){
        if(null == this.mBSC){
            this.mBSC = new BluetoothSppClient(sMac);
            if(this.mBSC.creatConn())
                return true;
            else{
                this.mBSC = null;
                return false;
            }

        }else
            return true;
    }

    public void closeConn(){
        if(null != this.mBSC){
            this.mBSC.closeConn();
            this.mBSC = null;
        }
    }
}
