package com.icb123.Service;

import java.util.List;

import com.icb123.bean.Service;
import com.icb123.bean.ServiceTime;

/**
 * 客户服务管理
 * */
public interface ServiceManager {

	public ServiceTime findTimeByCode(String timeCode);

	public Service findServiceByCode(String serviceCode);

	public List<ServiceTime> findAllServiceTime();

	public List<Service> findServiceByParentCode(String parentCode);

}
