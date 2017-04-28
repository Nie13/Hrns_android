package com.hrns.l.bluetooth_hrns.util;

/**
 * Created by l on 4/26/2017.
 */

import java.util.Locale;

public class CHexConver {

    private final static char[] mChars = "0123456789ABCDEF".toCharArray();
    private final static String mHexStr = "0123456789ABCDEF";

    public static boolean checkHexStr(String sHex){
        String sTmp = sHex.toString().trim().replace(" ", " ").toUpperCase(Locale.US);

        int iLen = sTmp.length();
        if(iLen > 1 && iLen % 2 == 0 ){
            for (int i = 0; i < iLen; i++)
                if (!mHexStr.contains(sTmp.substring(i, i + 1)))
                    return false;
            return true;
        }else {
            return false;
        }
        //return false;
    }

    public static String str2HexStr(String str){
        StringBuilder sb = new StringBuilder();
        byte[] bs = str.getBytes();

        for (int i = 0; i < bs.length; i++){
            sb.append(mChars[(bs[i] & 0xFF) >> 4]);
            sb.append(mChars[bs[i] & 0xFF]);
            sb.append( ' ');
        }

        return sb.toString().trim();

    }

    public static String hexStr2Str(String hexStr){
        hexStr = hexStr.toString().trim().replace(" ", " ").toUpperCase(Locale.US);
        char[] hexes = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int iTmp = 0x00;
        for (int i = 0 ; i < bytes.length; i ++){
            iTmp = mHexStr.indexOf(hexes[ 2 * i ]) << 4;
            iTmp |= mHexStr.indexOf(hexes[2 * i + 1]);
            bytes[i] = (byte) (iTmp & 0xFF);
        }

        return new String(bytes);
    }

    public static String byte2HexStr(byte[] b, int iLen){
        StringBuilder sb = new StringBuilder();
        for (int n = 0 ; n < iLen; n ++){
            sb.append(mChars[(b[n] & 0xFF) >> 4]);
            sb.append(mChars[b[n] & 0x0F]);
            sb.append(' ');
        }
        return sb.toString().trim().toUpperCase(Locale.US);
    }

    public static byte[] hexStr2Bytes(String src){
        src = src.trim().replace(" ", "").toUpperCase(Locale.US);
        int m = 0, n = 0;
        int iLen = src.length() / 2;
        byte[] ret = new byte[iLen];

        for (int i = 0; i < iLen ; i ++){
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = (byte)(Integer.decode("0x" + src.substring(i * 2, m) + src.substring(m , n)) & 0xFF);

        }
        return ret;

    }

    public static String strToUnicode(String strText) throws Exception{
            char c;
            StringBuilder str = new StringBuilder();
            int intAsc;
            String strHex;
            for( int i = 0 ; i < strText.length(); i ++){
                c = strText.charAt(i);
                intAsc = (int) c;
                strHex = Integer.toHexString(intAsc);
                if (intAsc > 128)
                    str.append("\\u");
                else
                    str.append("\\u00");
                str.append(strHex);
            }

            return str.toString();

    }

    public static String unicodeToString(String hex){
        int t = hex.length() / 6 ;
        int iTmp = 0;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < t; i ++){
            String s = hex.substring(i * 6, (i + 1) * 6);
            iTmp = (Integer.valueOf(s.substring(2,4), 16) << 8);
            Integer.valueOf(s.substring(4),16);
            str.append(new String(Character.toChars(iTmp)));

        }
        return str.toString();
    }





}
