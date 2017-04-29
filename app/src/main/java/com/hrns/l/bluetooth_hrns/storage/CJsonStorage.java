package com.hrns.l.bluetooth_hrns.storage;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by l on 4/29/2017.
 */

final public class CJsonStorage extends CKVStorage{
    private Context _c = null;

    private String _sPkgName;

    private String _sPROFILES_NAME = "profiles.json";

    private JSONObject _json = null;

    public CJsonStorage (Context C){
        this._c = C;

        try{
            this._sPkgName = (_c.getPackageManager().getPackageInfo(_c.getPackageName(), 0)).packageName;
            this._bSrorageIsReady=this.readStorage();
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
    }

    public CJsonStorage(Context C, String sExtRootPath){
        this._c = C;
        this._sPkgName = sExtRootPath;
        this._bSrorageIsReady = this.readStorage();
    }

    public CJsonStorage(Context C, String sExtRootPath, String sConfigFile){
        this._c = C;
        this._sPkgName = sExtRootPath;
        this._sPROFILES_NAME = sConfigFile.concat(".json");
        this._bSrorageIsReady = this.readStorage();
    }

    private boolean readStorage(){
        char[] cBuf = new char[512];
        StringBuilder sb = new StringBuilder();
        int iRet = 0;
        try{
            FileInputStream fis = new FileInputStream(this.getFilehd());
            InputStreamReader reader = new InputStreamReader(fis);
            while((iRet = reader.read(cBuf)) > 0){
                sb.append(cBuf, 0, iRet);
            }

            reader.close();
            fis.close();
            String sTmp = sb.toString();
            if(sTmp.length() > 0)
                _json = new JSONObject(sTmp);
            else
                _json = new JSONObject();
            return true;
        }catch (FileNotFoundException e){
            _json = new JSONObject();
            return true;
        }catch (IOException e){
            _json = new JSONObject();
            return  true;
        }catch (JSONException e){
            _json = new JSONObject();
            return true;
        }
    }

    private File getFilehd(){
        File f = null;
        String sRoot = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            sRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
            f = new File(sRoot.concat("/").concat(_sPkgName).concat("/"));
            if(!f.exists())
                f.mkdirs();
            f = new File(sRoot.concat("/").concat(_sPkgName).concat("/"), _sPROFILES_NAME);
            Log.v(_sPkgName, sRoot.concat("/").concat(_sPkgName).concat("/")+ _sPROFILES_NAME);
        }else {
            f = new File(_c.getFilesDir(), _sPROFILES_NAME);
        }

        return f;

    }


    @Override
    public boolean saveStorage(){
        File f = getFilehd();
        if(f.exists())
            f.delete();
        try{
            FileOutputStream fso = new FileOutputStream(f);
            fso.write(this._json.toString().getBytes());
            fso.close();
            fso = null;
            return true;

        }catch (FileNotFoundException e){
            e.printStackTrace();
            return false;

        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CKVStorage setVal(String sKey, String sSubKey, String sVal){
        if(this.isReady()){
            try{
                JSONObject jTmp = this._json.optJSONObject(sKey);
                if(null == jTmp){
                    if(null == sVal)
                        sVal = "";
                    this._json.put(sKey,new JSONObject().put(sSubKey, sVal));
                }else
                    jTmp.put(sSubKey, sVal);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return this;
    }

    @Override
    public CKVStorage setVal(String sKey, String sSubKey, int iVal){
        if(this.isReady()){
            try{
                JSONObject jTmp = this._json.optJSONObject(sKey);
                if(null == jTmp){
                    this._json.put(sKey,new JSONObject().put(sSubKey, iVal));
                }else
                    jTmp.put(sSubKey, iVal);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return this;
    }

    @Override
    public CKVStorage setVal(String sKey, String sSubKey, double dbVal){
        if(this.isReady()){
            try{
                JSONObject jTmp = this._json.optJSONObject(sKey);
                if(null == jTmp){
                    this._json.put(sKey,new JSONObject().put(sSubKey, dbVal));
                }else
                    jTmp.put(sSubKey, dbVal);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return this;
    }

    @Override
    public CKVStorage setVal(String sKey, String sSubKey, long lVal){
        if(this.isReady()){
            try{
                JSONObject jTmp = this._json.optJSONObject(sKey);
                if(null == jTmp){
                    this._json.put(sKey,new JSONObject().put(sSubKey, lVal));
                }else
                    jTmp.put(sSubKey, lVal);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return this;
    }

    @Override
    public CKVStorage setVal(String sKey, String sSubKey, boolean bVal){
        if(this.isReady()){
            try{
                JSONObject jTmp = this._json.optJSONObject(sKey);
                if(null == jTmp){
                    this._json.put(sKey,new JSONObject().put(sSubKey, bVal));
                }else
                    jTmp.put(sSubKey, bVal);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return this;
    }

    @Override
    public String getStringVal(String sKey, String sSubKey){
        JSONObject jsObj = null;
        if(this.isReady()){
            if(null != (jsObj = this._json.optJSONObject(sKey))){
                try{
                    return jsObj.getString(sSubKey);
                }catch (JSONException e){
                    //e.printStackTrace();
                    return "";
                }
            }
        }

        return "";
    }

    @Override
    public double getDoubleVal(String sKey, String sSubKey){
        JSONObject jsObj = null;
        if(this.isReady()){
            if(null != (jsObj = this._json.optJSONObject(sKey))){
                try{
                    return jsObj.getDouble(sSubKey);
                }catch (JSONException e){
                    //e.printStackTrace();
                    return 0.0d;
                }
            }
        }

        return 0.0d;
    }

    @Override
    public int getIntVal(String sKey, String sSubKey){
        JSONObject jsObj = null;
        if(this.isReady()){
            if(null != (jsObj = this._json.optJSONObject(sKey))){
                try{
                    return jsObj.getInt(sSubKey);
                }catch (JSONException e){
                    //e.printStackTrace();
                    return 0;
                }
            }
        }

        return 0;
    }

    @Override
    public long getLongVal(String sKey, String sSubKey){
        JSONObject jsObj = null;
        if(this.isReady()){
            if(null != (jsObj = this._json.optJSONObject(sKey))){
                try{
                    return jsObj.getLong(sSubKey);
                }catch (JSONException e){
                    //e.printStackTrace();
                    return 01;
                }
            }
        }

        return 01;
    }

    @Override
    public boolean getBooleanVal(String sKey, String sSubKey){
        JSONObject jsObj = null;
        if(this.isReady()){
            if(null != (jsObj = this._json.optJSONObject(sKey))){
                try{
                    return jsObj.getBoolean(sSubKey);
                }catch (JSONException e){
                    //e.printStackTrace();
                    return false;
                }
            }
        }

        return false;
    }

    @Override
    public CKVStorage removeVal(String sKey, String sSubKey){
        JSONObject jsObj = null;
        if(this.isReady()){
            if( null != (jsObj = this._json.optJSONObject(sKey))){
                jsObj.remove(sSubKey);
                if(jsObj.length() == 0)
                    jsObj.remove(sKey);
            }
        }

        return this;
    }


}
