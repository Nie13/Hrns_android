package com.hrns.l.bluetooth_hrns.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by l on 4/28/2017.
 */

public abstract class BTSerialComm {
    public final static String UUID_SPP = "00001101-0000-1000-8000-00805F9B34FB";
    private static final int iBUF_TOTAL = 1024*50;
    private final byte[] mbReceiveBufs = new byte[iBUF_TOTAL];
    private int miBufDataSite = 0;
    private String msMAC;
    private boolean mbConectOk = false;
    private BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();
    private InputStream misIn = null;
    private OutputStream mosOut = null;
    private BluetoothSocket mbsSocket = null;
    private long mlRxd = 0;
    private long mlTxd = 0;
    private long mlConnEnableTime = 0;
    private long mlConnDisableTime = 0;


    private boolean mbReceivedThread = false;

    private final CResourcePV mresReceiveBuf = new CResourcePV(1);

    private boolean mbKillReceiveData_StopFlg = false;

    private static ExecutorService FULL_TASK_EXECUTOR;

    private static final int SDK_VER;
    static {
        FULL_TASK_EXECUTOR = (ExecutorService) Executors.newCachedThreadPool();
        SDK_VER = Build.VERSION.SDK_INT;
    }

    public BTSerialComm(String sMAC){
        this.msMAC = sMAC;
    }

    public long getConnectHoldTime(){
        if (0 == this.mlConnEnableTime)
            return 0;
        else if ( 0 == this.mlConnDisableTime)
            return (System.currentTimeMillis() - this.mlConnEnableTime) / 1000;
        else
            return (this.mlConnDisableTime - this.mlConnEnableTime) / 1000;
    }

    public void closeConn(){
        if (this.mbConectOk){
            try{
                if(null != this.misIn)
                    this.misIn.close();
                if (null != this.mosOut)
                    this.mosOut.close();
                if (null != this.mbsSocket)
                    this.mbsSocket.close();
                this.mbConectOk = false;
            }catch (IOException e){
                this.misIn = null;
                this.mosOut = null;
                this.mbsSocket = null;
                this.mbConectOk = false;
            }finally{
                this.mlConnDisableTime = System.currentTimeMillis();
            }
        }
    }


    final public boolean creatConn(){
        if (!mBT.isEnabled())
            return false;

        if(mbConectOk)
            this.closeConn();

        final BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(this.msMAC);

        final UUID uuidSPP = UUID.fromString(BluetoothSppClient.UUID_SPP);

        try{
            if(SDK_VER >= 10)
                this.mbsSocket = device.createInsecureRfcommSocketToServiceRecord(uuidSPP);
            else
                this.mbsSocket = device.createRfcommSocketToServiceRecord(uuidSPP);
            this.mbsSocket.connect();

            this.mosOut = this.mbsSocket.getOutputStream();
            this.misIn = this.mbsSocket.getInputStream();
            this.mbConectOk = true;
            this.mlConnEnableTime = System.currentTimeMillis();
        }catch (IOException e){
            this.closeConn();
            return false;
        }finally{
            this.mlConnDisableTime = 0;
        }

        return true;
    }

    public boolean isConnect() {
        return this.mbConectOk;
    }

    public long getRxd(){
        return this.mlRxd;
    }

    public long getTxd(){
        return this.mlTxd;
    }

    public int getReceiveBufLen(){
        int iBufSize = 0;
        this.P(this.mresReceiveBuf);
        iBufSize = this.miBufDataSite;
        this.V(this.mresReceiveBuf);
        return iBufSize;
    }

    protected int SendData(byte[] btData){
        if(this.mbConectOk){
            try{
                mosOut.write(btData);
                this.mlTxd += btData.length;
                return btData.length;
            }catch (IOException e){
                this.closeConn();
                return -3;
            }
        }

        else
            return -2;
    }

    final protected synchronized byte[] ReceiveData(){
        byte[] btBufs = null;
        if(mbConectOk){
            if(!this.mbReceivedThread){
                if(SDK_VER >= 11)
                    new ReceiveThread().executeOnExecutor(FULL_TASK_EXECUTOR);
                else
                    new ReceiveThread().execute("");

                return null;
            }

            this.P(this.mresReceiveBuf);
            if(this.miBufDataSite > 0){
                btBufs = new byte[this.miBufDataSite];
                for(int i=0; i < this.miBufDataSite; i++)
                    btBufs[i] = this.mbReceiveBufs[i];

                this.miBufDataSite = 0;
            }

            this.V(this.mresReceiveBuf);
        }

        return btBufs;
    }


    private static boolean CompByte(byte[] src, byte[] dest){
        if(src.length != dest.length)
            return false;
        for (int i = 0, iLen=src.length ; i < iLen; i++)
            if(src[i] != dest[i])
                return false;
        return true;
    }

    final protected byte[] ReceiveData_StopFlg(byte[] btStopFlg){
        int iStopCharLen =btStopFlg.length;
        int iReceiveLen = 0;
        byte[] btCmp = new byte[iStopCharLen];
        byte[] btBufs = null;

        if (mbConectOk) {
            if(!this.mbReceivedThread) {
                if (SDK_VER >= 11)
                    new ReceiveThread().executeOnExecutor(FULL_TASK_EXECUTOR);
                else
                    new ReceiveThread().execute("");
                SystemClock.sleep(50);
            }


            while(true){
                this.P(this.mresReceiveBuf);
                iReceiveLen = this.miBufDataSite - iStopCharLen;
                this.V(this.mresReceiveBuf);
                if(iReceiveLen > 0)
                    break;
                else
                    SystemClock.sleep(50);
            }


            this.mbKillReceiveData_StopFlg = false;
            while(this.mbConectOk && !this.mbKillReceiveData_StopFlg){
                this.P(this.mresReceiveBuf);
                for(int i =0 ; i< iStopCharLen; i++)
                    btCmp[i] = this.mbReceiveBufs[this.miBufDataSite - iStopCharLen + i];
                this.V(this.mresReceiveBuf);

                if(CompByte(btCmp,btStopFlg)){
                    this.P(this.mresReceiveBuf);
                    btBufs = new byte[this.miBufDataSite - iStopCharLen];
                    for(int i = 0, iLen = this.miBufDataSite - iStopCharLen; i<iLen; i++)
                        btBufs[i] = this.mbReceiveBufs[i];
                    this.miBufDataSite = 0;
                    this.V(this.mresReceiveBuf);
                    break;
                }else {
                    SystemClock.sleep(50);
                }

            }
        }
        return btBufs;
    }


    public void killReceiveData_StopFlg(){
        this.mbKillReceiveData_StopFlg = true;
    }

    private void P(CResourcePV res){
        while(!res.seizeRes())
            SystemClock.sleep(2);
    }

    private void V(CResourcePV res){
        res.revert();
    }

    private class ReceiveThread extends AsyncTask<String, String, Integer>{
        static private final int BUFF_MAX_COUNT = 1024*5;
        static private final int CONNECT_LOST = 0x01;
        static private final int THREAD_END = 0x02;

        @Override
        public void onPreExecute(){
            mbReceivedThread = true;
            miBufDataSite = 0;
        }

        @Override
        protected Integer doInBackground(String... arg0){
            int iReadCnt = 0;
            byte[] btButTmp = new byte[BUFF_MAX_COUNT];

            while(mbConectOk){
                try{
                    iReadCnt = misIn.read(btButTmp);
                }catch (IOException e){
                    return CONNECT_LOST;
                }

                P(mresReceiveBuf);
                mlRxd += iReadCnt;
                if((miBufDataSite + iReadCnt) > iBUF_TOTAL)
                    miBufDataSite = 0;
                for(int i = 0; i<iReadCnt; i++)
                    mbReceiveBufs[miBufDataSite + i] = btButTmp[i];

                miBufDataSite += iReadCnt;

                V(mresReceiveBuf);
            }

            return THREAD_END;
        }

        @Override
        public void onPostExecute(Integer result){
            mbReceivedThread = false;
            if (CONNECT_LOST == result){
                closeConn();
            }else {
                try{
                    misIn.close();
                    misIn = null;
                }catch (IOException e){
                    misIn = null;
                }
            }
        }
    }



}
