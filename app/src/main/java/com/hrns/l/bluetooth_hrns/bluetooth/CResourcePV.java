package com.hrns.l.bluetooth_hrns.bluetooth;

/**
 * Created by l on 4/28/2017.
 */

final public class CResourcePV {
    private int iCount = 0;

    public CResourcePV(int iResourceCount){
        this.iCount = iResourceCount;
    }

    public boolean isExist(){
        synchronized (this){
            return iCount == 0;
        }
    }

    public boolean seizeRes(){
        synchronized (this){
            if(this.iCount > 0){
                iCount--;
                return true;
            }else
                return false;
        }
    }

    public void revert(){
        synchronized (this){
            iCount++;
        }
    }
}
