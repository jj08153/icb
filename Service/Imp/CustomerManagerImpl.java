package com.icb123.Service.Imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;











































import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;












































import com.icb123.Dao.CarSituationDao;
import com.icb123.Dao.CustomerAppointementDao;
import com.icb123.Dao.CustomerCarDao;
import com.icb123.Dao.CustomerCarSituationDao;
import com.icb123.Dao.CustomerDao;
import com.icb123.Dao.CustomerToServiceDao;
import com.icb123.Service.CarInfoManager;
import com.icb123.Service.CustomerManager;
import com.icb123.Service.WeixinCustomerManager;
import com.icb123.Util.CodeUtil;
import com.icb123.Util.Page;
import com.icb123.Util.SystemModelExceptionBase;
import com.icb123.bean.CarSituation;
import com.icb123.bean.Customer;
import com.icb123.bean.CustomerAppointement;
import com.icb123.bean.CustomerCar;
import com.icb123.bean.CustomerCarSituation;
import com.icb123.bean.CustomerService;

@Service("customerManager")
public class CustomerManagerImpl extends SystemModelExceptionBase implements CustomerManager{

	@Resource
	private CustomerDao customerDao;
	@Resource
	private CustomerCarDao customerCarDao;
	@Resource
	private WeixinCustomerManager weixinCustomerManager;
	@Resource
	private CarInfoManager carInfoManager;
	@Resource
	private CustomerCarSituationDao customerCarSituationDao;
	@Resource
	private CustomerAppointementDao customerAppointementDao;
	@Resource
	private CustomerToServiceDao customerToServiceDao;
	
	@Override
	public void saveCustomer(Customer icbCustomer){
		customerDao.save(icbCustomer);
	}

	@Override
	public void updateCustomer(Customer icbCustomer){
		customerDao.update(icbCustomer);
	}

	@Override
	public Customer findCustomerById(int id){
		return customerDao.findById(id);
	}

	@Override
	public List<Customer> findCustomerByWeixinCode(String weixinCode){
		return customerDao.findByWeixinCode(weixinCode);
	}

	@Override
	public Customer findCustomerByMobile(String mobile){
		return customerDao.findByMobile(mobile);
	}

	@Override
	public void updateCustomerStatusBycode(String code, String status){
		customerDao.updateStatusBycode(code, status);
	}

	@Override
	public void updateCustomerStatusByWeixinCode(String weixinCode, String status){
		customerDao.updateStatusByWeixinCode(weixinCode, status);
	}

	@Override
	public void updateCustomerMobile(String oldMobile, String newMobile){
		try {
			Customer cus=findCustomerByMobile(oldMobile);
			if(cus!=null){
				cus.setMobile(newMobile);
				customerDao.update(cus);
			}		
		} catch (Exception e) {
			outPutErrorInfor(CustomerManagerImpl.class.getName(), "updateCustomerMobile", e);
		}		
	}

	@Override
	public void updateCustomerMobileByOpenid(String openid, String mobile){
		customerDao.updateMobileByOpenid(openid, mobile);
	}

	@Override
	public Customer findCustomerByOpenid(String openid){
		// TODO Auto-generated method stub
		return customerDao.findByOpenid(openid);
	}

	@Override
	public Customer findCustomerByCode(String code){
		// TODO Auto-generated method stub
		return customerDao.findByCode(code);
	}

	@Override
	public CustomerCar findCarByCode(String carCode) {
		// TODO Auto-generated method stub
		return customerCarDao.findByCode(carCode);
	}

	@Override
	public Customer saveNewCustomer(String code, String mobile, String name,
			String recentAddress, String recentVehicle, String openid,Date creatTime,Date modifiedTime,String wxCode){
		Customer cus=new Customer();
		try {
			if(code!=null&&!"".equals(code)){
				cus.setCode(code);
			}else{
				return null;
			}
			if(mobile!=null&&!"".equals(mobile)){
				cus.setMobile(mobile);
			}else{
				return null;
			}
			if(name!=null&&!"".equals(name)){
				cus.setName(name);
			}else{
				return null;
			}
			if(recentAddress!=null&&!"".equals(recentAddress)){
				cus.setRecentAddress(recentAddress);
			}
			if(recentVehicle!=null&&!"".equals(recentVehicle)){
				cus.setRecentVehicle(recentVehicle);
			}
			if(StringUtils.isNotBlank(wxCode)){
				cus.setWeixinCode(CodeUtil.formateWeixinCode(wxCode));
			}
			if(openid!=null&&!"".equals(openid)){
				String weixinCode=weixinCustomerManager.findCodeByOpenid(openid);
				if(weixinCode!=null){
					cus.setWeixinCode(weixinCode);
				}
			}
			if(creatTime!=null){
				cus.setCreatTime(creatTime);
			}
			if(modifiedTime!=null){
				cus.setModifiedTime(modifiedTime);
			}		
			cus.setStatus("1");
		} catch (Exception e) {
			outPutErrorInfor(CustomerManagerImpl.class.getName(), "saveNewCustomer", e);
			return null;
		}
		return cus;
	}

	@Override
	public CustomerCar findByCusAndCarInfo(String cusCode, String carCode,
			String licensePlate) {
		// TODO Auto-generated method stub
		return customerCarDao.findByCusAndCarInfo(cusCode, carCode, licensePlate);
	}

	@Override
	public CustomerCar saveNewCustomerCar(String code,String cusCode, String carCode,
			String license,Date creatTime,Date modifiedTime,String fixedYear,Date carefulTime,String maintenanceCycle,
			String engineCode,Date insuranceTime,String virCode,String mileage) {
		CustomerCar car=new CustomerCar();
		try {
			if(code!=null&&!"".equals(code)){
				car.setCode(code);
			}else{
				return null;
			}
			if(cusCode!=null&&!"".equals(cusCode)){
				car.setCustomerCode(cusCode);
			}else{
				return null;
			}
			if(carCode!=null&&!"".equals(carCode)){
				String[] carStr=carCode.split("-");
				car.setBrand(carInfoManager.findNameByCode(carStr[0]));
				car.setBrandCode(carStr[0]);
				car.setSeries(carInfoManager.findNameByCode(carStr[0]+"-"+carStr[1]));
				car.setSeriesCode(carStr[0]+"-"+carStr[1]);
				car.setModels(carInfoManager.findNameByCode(carCode));
				car.setModelsCode(carCode);;
			}
			if(license!=null&&!"".equals(license)){
				car.setLicensePlate(license);
			}else{
				return null;
			}
			if(creatTime!=null){
				car.setCreatTime(creatTime);
			}
			if(modifiedTime!=null){
				car.setModifiedTime(modifiedTime);
			}	
			car.setCarefulTime(carefulTime);
			car.setEngineCode(engineCode);
			car.setFixedYear(fixedYear);
			car.setInsuranceTime(insuranceTime);
			car.setMaintenanceCycle(maintenanceCycle);
			if(mileage!=null&&!"".equals(mileage)){
				car.setMileage(Double.valueOf(mileage));
			}
			car.setVirCode(virCode);
			car.setStatus("1");	
		} catch (Exception e) {
			outPutErrorInfor(CustomerManagerImpl.class.getName(), "saveNewCustomerCar", e);
			return null;
		}	
		return car;
	}

	@Override
	public void saveCustomerCar(CustomerCar car) {
		customerCarDao.add(car);
	}

	@Override
	public void deleteCarByCode(String carCode) {
		customerCarDao.deleteByCode(carCode);
	}

	@Override
	public void updateCustomerCar(CustomerCar car) {
		customerCarDao.update(car);
	}

	@Override
	public String findOpenidByCusCode(String cusCode) {	
		return customerDao.findOpenidByCusCode(cusCode);
	}

	@Override
	public List<Map<String, String>> selectAllCustomer(Map map) {
		return customerDao.selectAllCustomer(map);
	}

	@Override
	public int countCustomer() {
		return customerDao.countCustomer();
	}

	@Override
	public void updateCustomerById(Customer customer) {
		customerDao.updateById();
	}

	@Override
	public List<CustomerCarSituation> findByAppCode(String appCode) {
		// TODO Auto-generated method stub
		return customerCarSituationDao.findByAppCode(appCode);
	}
	
	@Override
	public CustomerCar findCustomerCar(String code) {
		CustomerCar car = null;
		try {
			return customerCarDao.findCustomerCar(code);
		} catch (Exception e) {
			outPutErrorInfor(CustomerManagerImpl.class.getName(), "findCustomerCar", e);
		}
	 return car; 
	}

	@Override
	public List<Map<String, String>> findByNameOrmobile(Map map) {
		List<Map<String, String>> list =null;
		try {
			return customerDao.findByNameOrmobile(map);
		} catch (Exception e) {
			outPutErrorInfor(CustomerManagerImpl.class.getName(), "findByNameOrmobile", e);
		}
		return list;
	}

	@Override
	public int countByNameOrmobile(Map map) {
		int num = 0;
		try {
			return customerDao.countByNameOrmobile(map);
		} catch (Exception e) {
			outPutErrorInfor(CustomerManagerImpl.class.getName(), "countByNameOrmobile", e);
		}
		return num;
	}

	@Override
	public int countByNameOrlicensePlate(Map map) {
		int num = 0;
		try {
			return customerCarDao.countByNameOrlicensePlate(map);
		} catch (Exception e) {
			outPutErrorInfor(CustomerManagerImpl.class.getName(), "countByNameOrlicensePlate", e);
		}
		return num;
	}

	@Override
	public List<Map<String, Object>> selectByNameOrlicensePlate(Map map) {
		List<Map<String, Object>> list =null;
		try {
			return customerCarDao.selectByNameOrlicensePlate(map);
		} catch (Exception e) {
			outPutErrorInfor(CustomerManagerImpl.class.getName(), "selectByNameOrlicensePlate", e);
		}
	 return list;
	}
	
	@Override
	public List<Map<String, Object>> findCustomerAllService(String code) {
		List<Map<String, Object>> result=new ArrayList<Map<String,Object>>();
		Map<String, Object> map=null;
		List<CustomerAppointement> list1 = customerAppointementDao.findByCustomerCode(code,null);
		for(CustomerAppointement ca:list1){
			List<CustomerService> list= customerToServiceDao.findAppServiceByAppCode(ca.getCode());
			map=new HashMap<String, Object>();
			map.put("app", ca);
			map.put("service", list);
			result.add(map);
		}
		return result;
	}
	@Override
	public List<Map<String, Object>> selectCustomerCar(Map map) {
	   
		return customerCarDao.selectCustomerCar(map);
	}

	@Override
	public int countCustomerCar() {
		return customerCarDao.countCustomerCar();
	}

	@Override
	public int saveCarSituation(String appCode, String situCode,
			String result, String situName, String status, String situType) {
		CustomerCarSituation ccs = new CustomerCarSituation();
		ccs.setCode(CodeUtil.creatUUIDCode());
		ccs.setAppCode(appCode);
		ccs.setResult(result);
		ccs.setSituCode(situCode);
		ccs.setSituName(situName);
		ccs.setSituType(situType);
		int falg;
		if("4".equals(status)){//初次保存
			falg = customerCarSituationDao.save(ccs);
		}else{//再次保存
			falg = customerCarSituationDao.update(ccs);
		}
		return falg;
	}

	@Override
	public List<CustomerCar> findCustomerCarByCusCode(String cusCode) {
		return customerCarDao.findByCusCode(cusCode);
	}
}
