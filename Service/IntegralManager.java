package com.icb123.Service;

import java.util.List;

import com.icb123.bean.ConsumptionRecord;

public interface IntegralManager {

	/**
	 * 积分收入
	 * @return >0成功 ,<=0失败
	 * */
	public double inIntegral(String openid,int busnessType);
	//最大积分支出
	public double maxOutIntegral(String openid, int busnessType,double pay);
	//积分支出
	public int outIntegral(String openid, int busnessType,double pay);
	//查看积分明细
	public List<ConsumptionRecord> searchIntegralRecord(String wxCode);
	
}
