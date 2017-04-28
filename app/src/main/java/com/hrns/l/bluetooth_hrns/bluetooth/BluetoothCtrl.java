package com.hrns.l.bluetooth_hrns.bluetooth;

import android.bluetooth.BluetoothDevice;

import java.lang.reflect.Method;

/**
 * Created by l on 4/26/2017.
 */

public class BluetoothCtrl {
    static public final String PARING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";

    static public boolean createBond(BluetoothDevice btDevice) throws Exception{
        Class<? extends BluetoothDevice> btClass = btDevice.getClass();
        Method createBondMethod = btClass.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }


    static public boolean removeBond(BluetoothDevice btDevice) throws Exception{
        Class<? extends BluetoothDevice> btClass = btDevice.getClass
    }
}
