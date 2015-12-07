package com.icb123.Service.Imp;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cloopen.rest.sdk.CCPRestSDK;
import com.icb123.Common.Constants;
import com.icb123.Common.WeixinCons;
import com.icb123.Dao.SystemParamDao;
import com.icb123.Service.SendManager;
import com.icb123.Util.PropertiesUtils;
import com.icb123.Util.SystemModelExceptionBase;
import com.icb123.weixin.WeixinUntil;
/**
 * 信息发送业务
 * */
@Service("sendManager")
public class SendManagerImp extends SystemModelExceptionBase implements SendManager{
	@Resource
	public SystemParamDao systemParamDao;
	
	@Override
	public Map<String, String> mobileValidate(String mobile){
		String model=PropertiesUtils.getValueByKey("DX_Validate");
		int n = 0 ;
		while(n < 100000){
			n = (int)(Math.random()*1000000);
		}	
		Constants.cusMobile.put(mobile, n+"");
		String[] msg={n+"","1"};
		Map<String, String> result=sendMsgToMobile(model, mobile, msg);
		return result;
	}

	@Override
	public int sendMsgToMobile(String mobile, String msg) {
		String[] msgArray={msg};
		Map<String, String> result=sendMsgToMobile("", mobile, msgArray);
		if("1".equals(result.get("flag"))){
			return 1;
		}else{
			return -1;
		}
	}
	
	private Map<String, String> sendMsgToMobile(String model,String mobile,String[] msgArray) {
		String accountSid=PropertiesUtils.getValueByKey("DX_accountSid");
		String accountToken=PropertiesUtils.getValueByKey("DX_accountToken");
		String appId=PropertiesUtils.getValueByKey("DX_AppId");
		HashMap<String, Object> result = null;
		Map<String, String> r=new HashMap<String, String>();
		CCPRestSDK restAPI = new CCPRestSDK();
		restAPI.init("app.cloopen.com", "8883");
		restAPI.setAccount(accountSid, accountToken);
		restAPI.setAppId(appId);
		
		result = restAPI.sendTemplateSMS(mobile,model ,msgArray);

		if("000000".equals(result.get("statusCode"))){
			r.put("flag", "1");
		}else{
			r.put("flag", "0");
			outPutErrorInfor(SendManagerImp.class.getName(), "sendMsgToMobile", "错误码=" + result.get("statusCode") +" 错误信息="+result.get("statusMsg"));
		}
		return r;		
	}
	
	@Override
	public void sendTextMsgToWeixinUser(String toUser,String content){
		String json = "{\"touser\": \""+toUser+"\",\"msgtype\": \"text\", \"text\": {\"content\": \""+content+"\"}}";
		sendTextMsgToWeixin(json);
	}
	
	private void sendTextMsgToWeixin(String msg) {
		try {
			String at=WeixinUntil.getAccessToken();
			String requestUrl = WeixinCons.send_msg_url.replace("ACCESS_TOKEN", at);
			WeixinUntil.httpRequest(requestUrl, "POST", msg);
		} catch (Exception e) {
			outPutErrorInfor(SendManagerImp.class.getName(), "sendTextMsgToWeixin", e);
		}
	}

	@Override
	public void sendTextMsgToMobileAndWeixin(String mobile, String toUser,
			String msg) {
		sendMsgToMobile(mobile, msg);
		sendTextMsgToWeixinUser(toUser, msg);
	}
	
	@Override
	public void sendMsgToWeixinKefu(String ToUserName, String FromUserName){
		StringBuffer msg=new StringBuffer("");
		msg.append("<xml>");
		msg.append("<ToUserName><![CDATA["+ToUserName+"]]></ToUserName>");
		msg.append("<FromUserName><![CDATA["+FromUserName+"]]></FromUserName>");
		msg.append("<CreateTime>"+Calendar.getInstance().getTimeInMillis() / 1000+"</CreateTime>");
		msg.append("<MsgType><![CDATA[transfer_customer_service]]></MsgType>");
		msg.append("</xml>");
		sendTextMsgToWeixin(msg.toString());
	}
}
