package com.icb123.Service;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import com.icb123.bean.SystemParam;
/**
 * 系统参数
 * */
public interface SystemParamManager extends InitializingBean{
	public SystemParam findById(String id);
	public String findValueByName(String name);
	public List<String> findValueListByName(String name);
	public String findValueById(String id);
	public List<SystemParam> findByParentId(String parentId);
	public void setParams();
	public void afterPropertiesSet();
	public String customerCode();
	public String employeeCode();
}
