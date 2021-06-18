package com.hfi.insurance.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5Util {

    public MD5Util() {
    }

    /**
     * 计算获取文件md5值
     *
     * @param path
     * @return
     */
    public static String MD5ForFile(String path) {
        //windows系统命令certutil -hashfile E:\smkkey.rar MD5
        //linux系统命令md5sum smkkey.rar
        try {
            File e = new File(path);
            FileInputStream in = new FileInputStream(e);
            FileChannel ch = in.getChannel();
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0L, e.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            //base64位返回
            //String value = Base64.getEncoder().encodeToString(md5.digest());
            //16进制返回
            byte[] rs = md5.digest();
            StringBuffer digestHexStr = new StringBuffer();
            for (int i = 0; i < 16; i++) {
                digestHexStr.append(byteHEX(rs[i]));
            }
            //String value = DigestUtils.md5Hex(md5.digest());
            return digestHexStr.toString();
        } catch (FileNotFoundException var8) {
            var8.printStackTrace();
        } catch (NoSuchAlgorithmException var9) {
            var9.printStackTrace();
        } catch (IOException var10) {
            var10.printStackTrace();
        }

        return null;
    }

    public static String MD5ForString(String str) {
        try {
            MessageDigest e = MessageDigest.getInstance("MD5");
            String value = Base64.encodeBase64String(e.digest(str.getBytes()));
            //value = EncodeUtil.encodeObject(value);
            return value;
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    private static String byteHEX(byte ib) {
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] ob = new char[2];
        ob[0] = Digit[(ib >>> 4) & 0X0F];
        ob[1] = Digit[ib & 0X0F];
        String s = new String(ob);
        return s;
    }

	/**
	 * @Title: encrypt32
	 * @Description: md5 32位加密
	 * @param encryptStr
	 * @return
	 */
	public static String encrypt32(String encryptStr) {
    	MessageDigest md5;
    	try {
    		md5 = MessageDigest.getInstance("MD5");
    		byte[] md5Bytes = md5.digest(encryptStr.getBytes());
    		StringBuffer hexValue = new StringBuffer();
    		for (int i = 0; i < md5Bytes.length; i++) {
    			int val = ((int) md5Bytes[i]) & 0xff;
    			if (val < 16)
    				hexValue.append("0");
    			hexValue.append(Integer.toHexString(val));
    		}
    		encryptStr = hexValue.toString();
    	} catch (Exception e) {
    		throw new RuntimeException(e);
		}
    	return encryptStr;
	}
    
	/**
	 * @Title: encrypt16
	 * @Description: md5 16位加密
	 * @param encryptStr
	 * @return
	 */
	public static String encrypt16(String encryptStr) {
		return encrypt32(encryptStr).substring(8, 24);
	}
    
    public static void main(String[] args) throws InterruptedException {
    	System.out.println(encrypt16("ceshi"+encrypt16("ceshi"))); //0ba1ee64c5f055b9
    }
}

