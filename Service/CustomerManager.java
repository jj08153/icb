package com.icb123.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.icb123.bean.CarSituation;
import com.icb123.bean.Customer;
import com.icb123.bean.CustomerCar;
import com.icb123.bean.CustomerCarSituation;
/**
 * 用户中心业务
 * */
public interface CustomerManager {
	
	public void saveCustomer(Customer icbCustomer);
	public void saveCustomerCar(CustomerCar car);
	public void updateCustomer(Customer icbCustomer);
	public Customer findCustomerById(int id);
	public List<Customer> findCustomerByWeixinCode(String weixinCode);
	public Customer findCustomerByMobile(String mobile);
	public Customer findCustomerByCode(String code);
	public void updateCustomerStatusBycode(String code,String status);
	public void updateCustomerStatusByWeixinCode(String weixinCode,String status);
	
	/**
	 * 修改绑定手机
	 * */
	public void updateCustomerMobile(String oldMobile,String newMobile);
	/**
	 * 通过微信号修改绑定手机
	 * */
	public void updateCustomerMobileByOpenid(String openid,String mobile);
	/**
	 * 通过微信号获取用户
	 * */
	public Customer findCustomerByOpenid(String openid);
	public CustomerCar findCarByCode(String carCode);
	public CustomerCar findByCusAndCarInfo(String cusCode, String carCode,
			String license);
	public CustomerCar saveNewCustomerCar(String code,String cusCode, String carCode,
			String license,Date creatTime,Date modifiedTime,String fixedYear,Date carefulTime,String maintenanceCycle,
			String engineCode,Date insuranceTime,String virCode,String mileage);
	public Customer saveNewCustomer(String code, String mobile, String name,
			String recentAddress, String recentVehicle, String openid,
			Date creatTime, Date modifiedTime,String wxCode);
	public void deleteCarByCode(String carCode);
	public void updateCustomerCar(CustomerCar car);
	public String findOpenidByCusCode(String cusCode);
	public void updateCustomerById(Customer customer);
	public int countCustomer();
	public List<Map<String, String>> selectAllCustomer(Map<String, Object> map);
	public List<CustomerCarSituation> findByAppCode(String code);
	public CustomerCar findCustomerCar(String code);
	public List<Map<String, String>> findByNameOrmobile(Map map);
	public int countByNameOrmobile(Map map);
	
	public int countByNameOrlicensePlate(Map map);
	
	public List<Map<String, Object>> selectByNameOrlicensePlate(Map map);
	/**
	 * 获得用户所有服务信息
	 * */
	public List<Map<String, Object>> findCustomerAllService(String code);
	public List<Map<String, Object>> selectCustomerCar(Map map);
	public int countCustomerCar();
	public int saveCarSituation(String appCode, String situCode,
			String situresult, String situreName, String status, String situType);
	public List<CustomerCar> findCustomerCarByCusCode(String cusCode);
}
