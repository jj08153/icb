package com.icb123.Service;

import java.util.List;
import java.util.Map;

import com.icb123.bean.ConsumptionRecord;
import com.icb123.bean.FinishConditions;
import com.icb123.bean.WeixinAcceptRecord;
import com.icb123.bean.WeixinCustomer;
/**
 * 微信用户中心业务相关
 * */
public interface WeixinCustomerManager {

	public void save(WeixinCustomer cus);
	public int update(WeixinCustomer cus);
	public void delete(String openid);
	public void updateStatusByOpenid(String status,String openid);
	public WeixinCustomer findByOpenid(String openid);
	/**
	 * 获得邀请用户数
	 * */
	public Integer findInviNumByOpenid(String openid);
	/**
	 * 修改邀请用户数
	 * */
	public void updateInviNum(int num, String openid);
	/**
	 * 生成该用户的二维码
	 * @return 
	 * */
	public String creatEwmByWeixinCustomer(String openid);
	/**
	 * 通过会员号查找用户
	 * */
	public WeixinCustomer findByCode(String code);
	public String findCodeByOpenid(String openid);
	public String findOpenidByCode(String code);
	public WeixinCustomer findByName(String nickName);
	public boolean isNewWeixinCustomer(String openid);
	public void writeFinishVipCondition(String weixinCode,String conditionCode);
	public void upVip(String openid);
	public Map<String, String> saveCustomerInfo(String openid, String name,
			String mobile);
	public List<Map<String, String>> findConsumptionRecordByOpenid(String openid);
	public Map<String, Object> findCustomerInfo(String openid);
	public FinishConditions findFinishVipCondition(String weixinCode, String conditionCode);
	
	public int saveAcceptRecorde(WeixinAcceptRecord record);
	public WeixinAcceptRecord saveAcceptRecordeInfo(String weixinCode,String accept);
	public List<WeixinAcceptRecord> findAcceptRecordByWeixinCode(String weiinCode);
	public List<Map<String, String>> creatGiftButton();
	public List<WeixinAcceptRecord> acceptGift(String weixinCode,String accept);
	public Map<String, Object> openGiftView(String weixinCode);
	public int setVip(String code);
}
