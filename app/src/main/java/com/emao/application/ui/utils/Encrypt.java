package com.emao.application.ui.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author keybon
 */

public class Encrypt {


    public static final String GOOSE_TOKEN = "feather";

    // 1514198675207   937323        69DA2D852A17F85499966C0C090C8D9A  test

    private static Map<String,Object> map;

    public static String timeStamp ;
    public static String randomStr ;


    public static String getSignature(){
//        timeStamp = "151490085";
//        randomStr = "528176";
        timeStamp = String.valueOf(System.currentTimeMillis());
        randomStr = String.valueOf(getRandomStr());


        map = new HashMap<String,Object>();
        map.put("atimeStamp", timeStamp);
        map.put("randomStr",randomStr);
        map.put("signature",GOOSE_TOKEN+Constants.KEY);
        return md5Encry(sha1Encry(map)).toUpperCase();
    }

    public static int getRandomStr(){
        int random = (int) ((Math.random()*9+1)*100000);
        return random;
    }


    public static String sha1Encry(Map<String,Object> maps) {
        //获取信息摘要 - 参数字典排序后字符串
        String decrypt = getOrderByLexicographic(maps);
        StringBuffer strResult = new StringBuffer();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        md.update(decrypt.getBytes());
        byte[] encryptedBytes = md.digest();
        String stmp;
        for (int n = 0; n < encryptedBytes.length; n++) {
            stmp = (Integer.toHexString(encryptedBytes[n] & 0XFF));
            if (stmp.length() == 1) {
                strResult.append("0");
                strResult.append(stmp);
            } else {
                strResult.append(stmp);
            }
        }
        return strResult.toString();
    }


    /**
     * 获取参数的字典排序
     */
    private static String getOrderByLexicographic(Map<String,Object> maps){
        return splitParams(lexicographicOrder(getParamsName(maps)),maps);
    }
    /**
     * 获取参数名称 key
     */
    private static List<String> getParamsName(Map<String,Object> maps){
        List<String> paramNames = new ArrayList<String>();
        for(Map.Entry<String,Object> entry : maps.entrySet()){
            paramNames.add(entry.getKey());
        }
        return paramNames;
    }
    /**
     * 参数名称按字典排序
     */
    private static List<String> lexicographicOrder(List<String> paramNames){
        Collections.sort(paramNames);
        return paramNames;
    }
    /**
     * 拼接排序好的参数名称和参数值
     */
    private static String splitParams(List<String> paramNames,Map<String,Object> maps){
        StringBuilder paramStr = new StringBuilder();
        for(String paramName : paramNames){
//            paramStr.append(paramName);
            for(Map.Entry<String,Object> entry : maps.entrySet()){
                if(paramName.equals(entry.getKey())){
                    paramStr.append(String.valueOf(entry.getValue()));
                }
            }
        }
        return paramStr.toString();
    }


    /**
     * 计算字符串的MD5值
     */
    public static String md5(String string) {
        if (string.isEmpty()) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes("UTF-8"));
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String md5Encry(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * 计算文件的MD5值
     * @param file 文件File
     * @return     文件的MD5值
     */
    public static String md5(File file) {
        if (file == null || !file.isFile() || !file.exists()) {
            return "";
        }
        FileInputStream in = null;
        String result = "";
        byte buffer[] = new byte[0124];
        int len;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer)) != -1) {
                md5.update(buffer, 0, len);
            }
            byte[] bytes = md5.digest();

            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(null!=in){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
