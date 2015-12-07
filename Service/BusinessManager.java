package com.icb123.Service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.icb123.bean.AppointmentTime;
import com.icb123.bean.CustomerCar;
import com.icb123.bean.Customer;
import com.icb123.bean.CustomerAppointement;
import com.icb123.bean.CustomerService;
import com.icb123.bean.Employee;
import com.icb123.bean.Team;
import com.icb123.web.bean.PageBean;
/**
 * 客户业务相关
 * */
public interface BusinessManager {

	/**
	 * 预约服务业务
	 * @param time 预约时间
	 * @param cus 预约用户
	 * @param car 预约车辆
	 * @param app 预约基本信息
	 * @param serviceList 预约服务内容
	 * @return 0.未知原因失败 1.成功  2.预约时间已占用 
	 * */
	public Map<String, String> appointmentMaintenance(String date,String timeCode,String mobile,String name,String openid,String carCode,String license,String code,String totalPriceStr,String address,String remark,String appTypeCode,String type,String[] serviceArr)throws Exception;

	/**
	 * 预约取消
	 * @param reason 
	 * @param time 预约时间
	 * @param empl 操作人
	 * @param app 预约基本信息
	 * @param serviceList 预约服务内容
	 * @return 0.失败1.成功
	 * */
	public Map<String, String> appointmentCancel(String code,String employeeCode, String reason);
	
	public PageBean selectAppointment(Map<String,String> argMap);

	public CustomerAppointement findAppByCode(String code);
	
	public Map<String, String> appointmentConfirm(String timeCode, String date,
			String teamCode, String time, String carCode, String license,
			String haveCar,String modelsCode,String modelsStr, String cusCode, String mobile, String name,
			String address, String code, String remark, String[] serviceArr, String confirmCode,String status,String wxCode) throws Exception;

	/**
	 * 获得预约详情
	 * */
	public Map findDetailedAppointment(String code);

	public List<Team> findFreeTeam(String data, String time);
	
	public Map<String, String> distributeWork(String appCode, String distributeCode,String teamCode,String[] emp);

	public PageBean selectPersonalAppointment(Map<String, String> argMap);

	public List<Employee> findFreeEmp(String code, String position);

	public Map<String, String> daleteDistributeWork(String code, String code2);

	public Map<String, String> mobileValidate(String mobile, String validate);

	public Map<String, String> finishAppointment(String code,String vipCondition);

	//public Map<String, String> writeAccessoriesModel(String code, String model, String weixinCode);

	public List<Map<String, Object>> searchCustomerPensonalAppointmentByMobile(
			String mobile);

	public List<Map> findCurrentAppByOpenid(String openid);

	public List<Map> searchCustomerPensonalAppointmentByOpenid(String openid);

	public List<Map> findCurrentCar(String openid);

	public Map<String, String> integralPay(String code, String openid,
			String busnessType, String pay);

	public Map<String, String> inPutFinishInfo(JSONObject json, String empCode);

	/**
	 * 获得指定日期的排班详情
	 * 
	 * */
	public List<AppointmentTime> findAppTime(String date);

	public Map<String, String> saveCustomerScore(String code, String score);

	public List<CustomerCar> findHistoryCar(String openid);

	public List<Map> findHistoryAppByCarCode(String carCdoe);
}
