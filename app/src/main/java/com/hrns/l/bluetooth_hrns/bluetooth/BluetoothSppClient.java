package com.hrns.l.bluetooth_hrns.bluetooth;

import com.hrns.l.bluetooth_hrns.util.CHexConver;

import java.io.UnsupportedEncodingException;

/**
 * Created by l on 4/28/2017.
 */

public final class BluetoothSppClient extends BTSerialComm {

    public final static byte IO_MODE_HEX = 0x01;

    public final static byte IO_MODE_STRING = 0x02;

    private byte mbtTxDMode = IO_MODE_STRING;
    private byte mbtRxDMode = IO_MODE_STRING;

    private byte[] mbtEndFlg = null;

    protected String msCharsetName = null;

    public BluetoothSppClient(String MAC){
        super(MAC);
    }

    public void setTxdMode(byte bOutputMode){
        this.mbtTxDMode = bOutputMode;
    }

    public byte getTxdMode(){
        return this.mbtTxDMode;
    }

    public void setRxdMode(byte bOutputMode){
        this.mbtRxDMode = bOutputMode;
    }

    public int Send(String sData){
        if(IO_MODE_HEX == this.mbtTxDMode){
            if(CHexConver.checkHexStr(sData))
                return SendData(CHexConver.hexStr2Bytes(sData));
            else
                return 0;

        }else{
            if(null != this.msCharsetName){
                try{
                    return this.SendData(sData.getBytes(this.msCharsetName));
                }catch (UnsupportedEncodingException e){
                    return this.SendData(sData.getBytes());
                }
            }

            else
                return this.SendData(sData.getBytes());
        }
    }

    public String Receive(){
        byte[] btTmp = this.ReceiveData();
        if(null != btTmp){
            if(IO_MODE_HEX == this.mbtRxDMode)
                return (CHexConver.byte2HexStr(btTmp, btTmp.length)).concat(" ");
            else
                return new String(btTmp);
        }else
            return null;


    }


    public void setReceiveStopFlg(String sFlg){
        this.mbtEndFlg = sFlg.getBytes();
    }

    public void setCharset(String sCharset){
        this.msCharsetName = sCharset;
    }

    public String ReceiveStopFlg(){
        byte[] btTmp = null;

        if(null == this.mbtEndFlg)
            return new String();

        btTmp = this.ReceiveData_StopFlg(this.mbtEndFlg);
        if(null == btTmp)
            return null;
        else{
            if(null == this.msCharsetName)
                return new String(btTmp);
            else{
                try{
                    return new String(btTmp, this.msCharsetName);
                }catch(UnsupportedEncodingException e){
                    return new String(btTmp);
                }
            }
        }
    }

}
