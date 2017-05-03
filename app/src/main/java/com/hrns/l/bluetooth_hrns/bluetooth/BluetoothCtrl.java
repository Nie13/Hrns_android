package com.hrns.l.bluetooth_hrns.bluetooth;

import android.bluetooth.BluetoothDevice;

import java.lang.reflect.Method;

import android.util.Log;

/**
 * Created by l on 4/26/2017.
 */

public class BluetoothCtrl {
    static public final String PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";

    static public boolean createBond(BluetoothDevice btDevice) throws Exception{
        Class<? extends BluetoothDevice> btClass = btDevice.getClass();
        Method createBondMethod = btClass.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }


    static public boolean removeBond(BluetoothDevice btDevice) throws Exception{
        Class<? extends BluetoothDevice> btClass = btDevice.getClass();
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    static public boolean setPin (BluetoothDevice btDevice, String str) throws Exception{
        Boolean returnValue = false;
        try{
            Class<? extends BluetoothDevice> btClass = btDevice.getClass();
            Method removeBondMethod = btClass.getDeclaredMethod("setPin", new Class[]{byte[].class});
            returnValue = (Boolean) removeBondMethod.invoke(btDevice, new Object[] { str.getBytes() });
            Log.d("returnValue", ">>setPin" + returnValue.toString());

        }catch ( SecurityException e){
            Log.e("returnValue", ">>setPin:" + e.getMessage());
            e.printStackTrace();
        }catch ( IllegalArgumentException e){
            Log.e("returnValue", ">>setPin:" + e.getMessage());
            e.printStackTrace();
        }catch (Exception e){
            Log.e("returnValue" , ">>setPin" + e.getMessage());
            e.printStackTrace();
        }
        return returnValue.booleanValue();
    }

    static public boolean cancelPairingUserInput(BluetoothDevice btDevice) throws Exception{
        Class<? extends BluetoothDevice> btClass = btDevice.getClass();
        Method createBondMethod = btClass.getMethod("cancelPairingUserInput");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return  returnValue.booleanValue();
    }

    static public boolean cancelBondProcess(BluetoothDevice btDevice) throws Exception{
        Boolean returnValue = false;
        try{
            Class<? extends BluetoothDevice> btClass = btDevice.getClass();
            Method createBondMethod = btClass.getMethod("cancelBondProcess");
            returnValue = (Boolean) createBondMethod.invoke(btDevice);

        }catch(SecurityException e){
            Log.e("returnValue", ">>cancelBondProcess:" + e.getMessage());
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            Log.e("returnValue", ">>cancelBondProcess:" + e.getMessage());
            e.printStackTrace();
        }catch (Exception e){
            Log.e("returnValue", ">>cancelBondProcess: " + e.getMessage());
            e.printStackTrace();
        }

        return returnValue.booleanValue();
    }
}


// this part can be changed later for a automatic scan and connect activity
