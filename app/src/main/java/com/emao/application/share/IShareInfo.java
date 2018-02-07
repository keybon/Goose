package com.emao.application.share;

import android.graphics.Bitmap;

/**
 * 
 * 移动IM开发项目组 — @author kfzx-tangw 于 2014-9-1 
 * IMAPP_C_ANDROID.com.icbc.im.ui.activity.share.IShareInfo.java
 * <p>
 * 分享信息的接口, 包含文字、图片、链接的分享.</br> 
 * 详细的分享实现由实现此接口的类完成.
 * <p>
 * 这里描述历史变更清单，按照变更日期—变更人—变更事项顺序编写。
 * 1、创建基本程序……
 *
 */
public interface IShareInfo {
	/**
	 * 方法简介：与授权相关的操作</br> 
	 * 输入项说明： 
	 * 返回项说明：
	 */
	public void auth();

	/**
	 * 方法简介：分享纯文字</br> 
	 * 输入项说明：待分享的文字信息</br> 
	 * 返回项说明：
	 * 
	 * @param text 待分享的文字信息
	 */
	public void shareText(String text);

	/**
	 * 方法简介：分享图片</br> 
	 * 输入项说明：待分享的图片资源</br> 
	 * 返回项说明：
	 * 
	 * @param image 待分享的图片资源
	 */
	public void shareImage(Bitmap image);

	/**
	 * 方法简介：分享链接</br> 
	 * 输入项说明：链接地址</br> 
	 * 返回项说明：
	 * 
	 * @param url 链接地址
	 */
	public void shareUrl(String url);
}
