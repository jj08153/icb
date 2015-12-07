package com.icb123.Service;


import java.util.List;

import com.icb123.bean.FalseAccount;
/**
 * 虚拟客户业务类
 * */
public interface FalseAccountManager {
	
	public void saveAccount(String name,String address,String models,String phone);
	
	public List<FalseAccount> selectAllFalseAccount();
	
	public int deleteById(int id);

}
