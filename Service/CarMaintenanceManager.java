package com.icb123.Service;

import java.util.List;

import com.icb123.bean.CustomerAppointement;
import com.icb123.bean.CustomerService;
import com.icb123.bean.Team;

/**
 * 汽车保养业务类
 * */
public interface CarMaintenanceManager {

	/**
	 * 派工单录入时更新服务项
	 * */
	public int updateCustomerServicebyAppointment(String service, String appCode,String empCode);

	public List<Team> findAllTeam();

	/**
	 * 封装用户服务项目详细详细
	 * */
	public CustomerService saveCustomerServiceInfo(String code,String subscribeCode,String serviceCode,String accessoriesCode,String accessoriesModel,String accessoriesNum,String modifiedId,String carCode);

}
