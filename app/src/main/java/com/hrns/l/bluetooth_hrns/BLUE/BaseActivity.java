package com.hrns.l.bluetooth_hrns.BLUE;

import android.app.ActionBar;
import android.app.Activity;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.widget.Toast;

import com.hrns.l.bluetooth_hrns.R;
import com.hrns.l.bluetooth_hrns.util.LocalIOTools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Locale;

/**
 * Created by l on 5/3/2017.
 */

public class BaseActivity extends Activity {
    protected void enabledBack(){
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    protected void save2SD(String sData){
        String sRoot = null;
        String sFileName = null;
        String sPath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sFileName = (new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())).format(new Date()) + ".txt";
        }else
            sFileName = "GANNIWEIMAOYONGNAMEDIBAN";
        sPath = sRoot.concat("/").concat(this.getString(R.string.app_name));
        if(LocalIOTools.coverByte2File(sPath, sFileName, sData.getBytes())){
            String sMsg = ("save to:").concat(sPath).concat("/").concat(sFileName);
            Toast.makeText(this, sMsg, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, getString(R.string.msg_save_file_fail), Toast.LENGTH_SHORT).show();
        }
    }

    public String getStringFormRawFile(int iRawID){
        InputStream is = this.getResources().openRawResource(iRawID);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i;
        try{
            i = is.read();
            while(i != -1){
                baos.write(i);
                i = is.read();
            }
            is.close();
            return baos.toString().trim();
        }catch (IOException e){
            return null;
        }
    }
}
