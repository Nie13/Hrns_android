package com.hrns.l.bluetooth_hrns.storage;

/**
 * Created by l on 4/29/2017.
 */

public abstract class CKVStorage {
    protected boolean _bSrorageIsReady = false;

    public boolean isReady(){
        return this._bSrorageIsReady;
    }

    public abstract boolean saveStorage();

    public abstract CKVStorage setVal(String sKey, String sSubKey, String sVal);

    public abstract CKVStorage setVal(String sKey, String sSubKey, double dbVal);

    public abstract CKVStorage setVal(String sKey, String sSubKey, int iVal);

    public abstract CKVStorage setVal(String sKey, String sSubKey, long lVal);

    public abstract CKVStorage setVal(String sKey, String sSubKey, boolean bVal);

    public abstract String getStringVal(String sKey, String sSubKey);

    public abstract double getDoubleVal(String sKey, String sSubKey);

    public abstract int getIntVal(String sKey, String sSubKey);

    public abstract long getLongVal(String sKey, String sSubKey);

    public abstract boolean getBooleanVal(String sKey, String sSubKey);

    public abstract CKVStorage removeVal(String sKey, String sSubKey);
}
