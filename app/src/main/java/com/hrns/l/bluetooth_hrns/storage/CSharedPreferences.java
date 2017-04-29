package com.hrns.l.bluetooth_hrns.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * Created by l on 4/29/2017.
 */

final public class CSharedPreferences extends CKVStorage {
    private static final String msDELIMITER = "|_|";

    private Context mc = null;

    private String msPkgName;

    private Editor meSaveData = null;

    private SharedPreferences mSP = null;

    public CSharedPreferences(Context C) {
        this.mc = C;

        PackageManager manager = mc.getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(mc.getPackageName(), 0);
            this.msPkgName = info.packageName;
            this.mSP = mc.getSharedPreferences(this.msPkgName, Context.MODE_PRIVATE);
            this._bSrorageIsReady = true;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            ;
            this.msPkgName = "";
            this._bSrorageIsReady = false;
        }
    }

    private void newStorage() {
        if (null == this.meSaveData)
            this.meSaveData = mc.getSharedPreferences(this.msPkgName, Context.MODE_PRIVATE).edit();
    }

    private String getIdxKey(String sKey, String sSubKey) {
        return sKey + msDELIMITER + sSubKey;
    }

    @Override
    public boolean saveStorage(){
        if(null != this.meSaveData){
            this.meSaveData.commit();
            this.meSaveData = null;
            return true;
        }else
            return false;
    }

    @Override
    public CKVStorage setVal(String sKey, String sSubKey, String sVal){
        this.newStorage();
        this.meSaveData.putString(this.getIdxKey(sKey, sSubKey),sVal);
        return this;
    }

    @Override
    public CKVStorage setVal(String sKey, String sSubKey, int iVal){
        this.newStorage();
        this.meSaveData.putInt(this.getIdxKey(sKey, sSubKey),iVal);
        return this;
    }

    @Override
    public CKVStorage setVal(String sKey, String sSubKey, double dbVal){
        this.newStorage();
        this.meSaveData.putFloat(this.getIdxKey(sKey, sSubKey),(float) dbVal);
        return this;
    }

    @Override
    public CKVStorage setVal(String sKey, String sSubKey, boolean bVal){
        this.newStorage();
        this.meSaveData.putBoolean(this.getIdxKey(sKey,sSubKey), bVal);
        return this;
    }

    @Override
    public CKVStorage setVal(String sKey, String sSubKey, long lVal){
        this.newStorage();;
        this.meSaveData.putLong(this.getIdxKey(sKey,sSubKey),lVal);
        return this;
    }

    @Override
    public String getStringVal(String sKey, String sSubKey){
        return this.mSP.getString(this.getIdxKey(sKey,sSubKey), "");
    }

    @Override
    public int getIntVal(String sKey, String sSubKey){
        return this.mSP.getInt(this.getIdxKey(sKey, sSubKey), 0);
    }

    @Override
    public double getDoubleVal(String sKey, String sSubKey){
        return (double) this.mSP.getFloat(this.getIdxKey(sKey,sSubKey), 0.0f);
    }

    @Override
    public boolean getBooleanVal(String sKey, String sSubKey){
        return this.mSP.getBoolean(this.getIdxKey(sKey,sSubKey), false);
    }

    @Override
    public long getLongVal(String sKey, String sSubKey){
        return this.mSP.getLong(this.getIdxKey(sKey, sSubKey), 0);
    }

    @Override
    public CKVStorage removeVal(String sKey, String sSubKey){
        this.newStorage();
        this.meSaveData.remove(this.getIdxKey(sKey,sSubKey));
        return this;
    }
}