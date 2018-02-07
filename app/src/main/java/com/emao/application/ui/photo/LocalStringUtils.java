package com.emao.application.ui.photo;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * 此文件涉及嵌入版，请在同步独立版程序时，同步嵌入版程序
 *
 */

public class LocalStringUtils
{

	/**
	 * 将字符串转成URI
	 * 
	 * @param str
	 * @return
	 */
	public static Uri toUriByStr(String str)
	{
		if (isEmpty(str)) {
			return null;
		}
		Uri uri = null;
		if (str.indexOf("/storage") > -1)
		{
			uri = Uri.fromFile(new File(str.substring(str.indexOf("/storage"))));
		}
		else if (str.indexOf("/sd") > -1)
		{
			uri = Uri.fromFile(new File(str.substring(str.indexOf("/sd"))));
		}
		else
		{
			uri = Uri.parse(str);
		}
		return uri;
	}

	public static String formatVoiceTime(long audioTime)
	{
		// 分钟
		long minute = audioTime / 60;
		// 秒
		long second = audioTime % 60;
		// 设置录制时间
		return (minute < 10 ? "0" + String.valueOf(minute) : String.valueOf(minute)) + ":" + (second < 10 ? "0" + String.valueOf(second) : String.valueOf(second));
	}

	public static String uTF8GetString(byte[] bytearr)
	{
		return uTF8GetString(bytearr, 0, bytearr.length);
	}

	public static String uTF8GetString(byte[] bytearr, int start, int utflen)
	{
		char str[] = new char[utflen];
		int c, char2, char3;
		int count = start;
		int strlen = 0;

		while (count < start + utflen)
		{
			c = (int) bytearr[count] & 0xff;
			switch (c >> 4)
			{
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
					/* 0xxxxxxx */
					count++;
					str[strlen++] = (char) c;
				break;
				case 12: // C 1100
				case 13: // D 1101

					/* 110x xxxx 10xx xxxx */
					count += 2;
					if (count > start + utflen)
					{
						return new String(bytearr, start, utflen);
					}
					char2 = (int) bytearr[count - 1];
					if ((char2 & 0xC0) != 0x80)
					{
						return new String(bytearr, start, utflen);
					}
					str[strlen++] = (char) (((c & 0x1F) << 6) | (char2 & 0x3F));
				break;
				case 14: // E 1110

					/* 1110 xxxx 10xx xxxx 10xx xxxx */
					count += 3;
					if (count > start + utflen)
					{
						return new String(bytearr, start, utflen);
					}
					char2 = (int) bytearr[count - 2];
					char3 = (int) bytearr[count - 1];
					if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
					{
						return new String(bytearr, start, utflen);
					}
					str[strlen++] = (char) (((c & 0x0F) << 12) | ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0));
				break;
				default:

					/* 10xx xxxx, 1111 xxxx */
					return new String(bytearr, start, utflen);
			}
		}
		return new String(str, 0, strlen);
	}

	/**
	 * 
	 * @param instring
	 *            String
	 * @return byte[]
	 */
	public static byte[] UTF8GetBytes(String instring)
	{
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeUTF(instring);
			byte[] jdata = bos.toByteArray();
			bos.close();
			dos.close();
			byte[] buff = new byte[jdata.length - 2];
			System.arraycopy(jdata, 2, buff, 0, buff.length);
			return buff;
		}
		catch (IOException e)
		{
			return null;
		}
	}

	/**
	 * 获得当前Bitmap数组
	 * 
	 * @param img
	 * @return
	 */
	// Compress image and then return with byte array
	public static byte[] bitmapToByteArray(Bitmap img)
	{
		int size = img.getWidth() * img.getHeight() * 4;
		byte[] data;
		ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		try
		{
			img.compress(Bitmap.CompressFormat.JPEG, 100, out);
			data = out.toByteArray();
			out.flush();
			out.close();
		}
		catch (IOException e)
		{
			return null;
		}
		return data;
	}

	public static String toHex(byte[] data)
	{
		StringBuffer strBuffer = new StringBuffer();
		String hexStr = "";

		for (int i = 0; i < data.length; i++)
		{
			strBuffer.append(byteToHex(data[i]));
		}

		if (strBuffer != null && strBuffer.length() > 0)
		{
			hexStr = strBuffer.toString();
		}
		return hexStr;
	}

	public static String byteToHex(byte a)
	{
		int aaa = (a < 0) ? a + 256 : a;
		StringBuffer sb = new StringBuffer();
		switch (aaa / 16)
		{
			case 10:
				sb.append("A");
			break;
			case 11:
				sb.append("B");
			break;
			case 12:
				sb.append("C");
			break;
			case 13:
				sb.append("D");
			break;
			case 14:
				sb.append("E");
			break;
			case 15:
				sb.append("F");
			break;
			default:
				sb.append(aaa / 16);
			break;
		}
		switch (aaa % 16)
		{
			case 10:
				sb.append("A");
			break;
			case 11:
				sb.append("B");
			break;
			case 12:
				sb.append("C");
			break;
			case 13:
				sb.append("D");
			break;
			case 14:
				sb.append("E");
			break;
			case 15:
				sb.append("F");
			break;
			default:
				sb.append(aaa % 16);
			break;
		}
		return sb.toString();
	}

	public static byte[] join(byte[] arrayI, byte[] arrayII)
	{
		byte[] array = null;

		if (arrayI != null && arrayII != null)
		{
			array = new byte[arrayI.length + arrayII.length];

			System.arraycopy(arrayI, 0, array, 0, arrayI.length);
			System.arraycopy(arrayII, 0, array, arrayI.length, arrayII.length);
		}
		return array;
	}

	public static byte[] join(byte[] arrayI, byte[] arrayII, byte[] arrayIII)
	{
		byte[] array = null;

		if (arrayI != null && arrayII != null && arrayIII != null)
		{
			array = new byte[arrayI.length + arrayII.length + arrayIII.length];

			System.arraycopy(arrayI, 0, array, 0, arrayI.length);
			System.arraycopy(arrayII, 0, array, arrayI.length, arrayII.length);
			System.arraycopy(arrayIII, 0, array, arrayI.length + arrayII.length, arrayIII.length);
		}
		return array;
	}

	/**
	 * 方法简介：将byte数组转换成十六进制字符串
	 * <p>输入项说明：无
	 * <p>返回项说明：无
	 * @param data
	 * @return
	 */
	public static String byteArray2HEXString(byte[] data)
	{
		if (null == data || 0 == data.length)
			return "";

		StringBuilder sb = new StringBuilder(data.length * 2);
		for (byte b : data)
		{
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}

	/**
	 * 方法简介：将字符串转换成十六进制
	 * <p>输入项说明：无
	 * <p>返回项说明：无
	 * @param str
	 * @return
	 */
	public static byte[] hexToBytes(String str)
	{
		if (str == null)
		{
			return null;
		}
		else
			if (str.length() < 2)
			{
				return null;
			}
			else
			{
				int len = str.length() / 2;
				byte[] buffer = new byte[len];
				for (int i = 0; i < len; i++)
				{
					buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
				}
				return buffer;
			}
	}

	public static boolean isEmpty(String str)
	{
		return str == null || "null".equals(str);
	}

	public static int getGBKLength(String s)
	{
		// s.getBytes(Charset.forName("GBK"))
		int length = 0;
		try
		{
			length = s.getBytes("GBK").length;
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return length;
	}

	/**
	 * 去掉字符串开头的标点
	 * @param str
	 * @return 如果都是标点,则返回""
	 */
	public static String removePunctuate(String str){
		if(str != null){
			boolean isMatch= false;
//			Pattern p = Pattern.compile("[\\pP‘’“”]");	// 匹配任意符号
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				String s = String.valueOf(c);
				if (!s.matches("[,\\.\\?!，。？！]")) {//[(.|,|\"|\\?|!|:;')]
					str = str.substring(i);
					isMatch = true;
					break;
				} 
			}
			if(!isMatch){
				str = "";
			}
		}
		return str;
	}
	
	public static String stringToPinyin(String s) {
		if(s==null){
			return "";
		}else{
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < s.length(); ++i) {
				char c = s.charAt(i);
				sb.append(charToPinyin(c));
			}
			return sb.toString();
		}

	}
	
	public static String charToPinyin(char c) {
		String ret = String.valueOf(c);
		//String[] print = PinyinHelper.toHanyuPinyinStringArray(c);
		return "A";
//		if(print != null)
//			ret = print[0].substring(0, print[0].length() - 1);
//		return ret;
	}
	public static ArrayList<String> subURL(String url) {
		ArrayList<String> list = new ArrayList<String>();
		if(url.indexOf("?") != -1){
			url = url.substring(url.indexOf("?"), url.length());
		}
		String subUrl[] = url.split("&");
		for(int i=0;i<subUrl.length;i++){
			String str = subUrl[i];
			list.add(str);
		}
		return list;
	}
	
	/**
	 * 字符串补齐
	 * @param source 源字符串
	 * @param fillLength 补齐长度
	 * @param fillChar 补齐的字符
	 * @param isLeftFill true为左补齐，false为右补齐
	 * @return
	 */
	public static String stringFill(String source, int fillLength, char fillChar, boolean isLeftFill) {
	    if (source == null || source.length() >= fillLength) return source;
	     
	    char[] c = new char[fillLength];
	    char[] s = source.toCharArray();
	    int len = s.length;
	    if(isLeftFill){
	        int fl = fillLength - len;
	        for(int i = 0; i<fl; i++){
	            c[i] = fillChar;
	        }
	        System.arraycopy(s, 0, c, fl, len);
	    }else{
	        System.arraycopy(s, 0, c, 0, len);
	        for(int i = len; i<fillLength; i++){
	            c[i] = fillChar;
	        }
	    }
	    return String.valueOf(c);
	}

}

