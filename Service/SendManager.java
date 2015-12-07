package com.icb123.Service;

import java.util.Map;

public interface SendManager {

	public Map<String, String> mobileValidate(String mobile);
	public int sendMsgToMobile(String mobile,String msg);
	public void sendTextMsgToWeixinUser(String toUser,String content);
	public void sendTextMsgToMobileAndWeixin(String mobile,String toUser,String msg);
	public void sendMsgToWeixinKefu(String ToUserName, String FromUserName);
}
