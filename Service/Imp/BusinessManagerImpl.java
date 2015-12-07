package com.icb123.Service.Imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icb123.Common.Constants;
import com.icb123.Common.WeixinCons;
import com.icb123.Dao.AppointmentTimeDao;
import com.icb123.Dao.CustomerAppointementDao;
import com.icb123.Dao.CustomerToServiceDao;
import com.icb123.Dao.SystemParamDao;
import com.icb123.Dao.TeamDao;
import com.icb123.Service.AccessoriesInfoManager;
import com.icb123.Service.BusinessManager;
import com.icb123.Service.CarInfoManager;
import com.icb123.Service.CarMaintenanceManager;
import com.icb123.Service.CustomerManager;
import com.icb123.Service.EmployeeManager;
import com.icb123.Service.IntegralManager;
import com.icb123.Service.SendManager;
import com.icb123.Service.ServiceManager;
import com.icb123.Service.SystemParamManager;
import com.icb123.Service.WeixinCustomerManager;
import com.icb123.Service.WorkRecordManager;
import com.icb123.Util.CodeUtil;
import com.icb123.Util.FormatForI18N;
import com.icb123.Util.PropertiesUtils;
import com.icb123.Util.SystemModelExceptionBase;
import com.icb123.bean.AccessoriesInfo;
import com.icb123.bean.AccessoriesModel;
import com.icb123.bean.AppointmentTime;
import com.icb123.bean.CarSituation;
import com.icb123.bean.Customer;
import com.icb123.bean.CustomerAppointement;
import com.icb123.bean.CustomerCar;
import com.icb123.bean.CustomerCarSituation;
import com.icb123.bean.CustomerService;
import com.icb123.bean.Employee;
import com.icb123.bean.ServiceTime;
import com.icb123.bean.Team;
import com.icb123.bean.WeixinCustomer;
import com.icb123.bean.WorkRecord;
import com.icb123.web.bean.AppointmentPageBean;
import com.icb123.web.bean.PageBean;
@Service("businessManager")
@Transactional
public class BusinessManagerImpl extends SystemModelExceptionBase implements BusinessManager {
	@Resource
	private AppointmentTimeDao appointmentTimeDao;
	@Resource
	private CustomerAppointementDao customerAppointementDao;
	@Resource
	private CustomerToServiceDao customerToServiceDao;
	@Resource
	private TeamDao teamDao;
	@Resource
	private CustomerManager customerManager;
	@Resource
	private ServiceManager serviceManager;
	@Resource
	private AccessoriesInfoManager accessoriesInfoManager;
	@Resource
	private SystemParamManager systemParamManager;
	@Resource
	private EmployeeManager employeeManager;
	@Resource
	private WeixinCustomerManager weixinCustomerManager;
	@Resource
	private WorkRecordManager workRecordManager;
	@Resource
	private SendManager sendManager;
	@Resource
	private IntegralManager integralManager;
	@Resource
	private CarMaintenanceManager carMaintenanceManager;
	@Resource
	private CarInfoManager carInfoManager;

	@Override
	@Transactional(rollbackFor=Exception.class)
	public Map<String, String> appointmentMaintenance(String date, String timeCode,
			String mobile, String name, String openid, String carCode,
			String license, String code,
			String totalPriceStr, String address, String remark,
			String appTypeCode, String type, String[] serviceArr)throws Exception{
		Map<String, String> result=new HashMap<String, String>();
		try {
			if(StringUtils.isBlank(code)){result.put("flag", "0");result.put("msg", "操作异常");return result;}
			if(StringUtils.isBlank(date)){result.put("flag", "3");result.put("msg", "操作异常");return result;}
			if(StringUtils.isBlank(timeCode)){result.put("flag", "3");result.put("msg", "预约时间获取错误");return result;}
			if(StringUtils.isBlank(mobile)){result.put("flag", "4");result.put("msg", "客户信息获取错误");return result;}
			if(StringUtils.isBlank(name)){result.put("flag", "4");result.put("msg", "客户信息获取错误");return result;}
			if(StringUtils.isBlank(license)){result.put("flag", "5");result.put("msg", "客户车辆信息获取错误");return result;}
			if(StringUtils.isBlank(totalPriceStr)){result.put("flag", "6");result.put("msg", "客户预约信息获取错误");return result;}
			if(StringUtils.isBlank(address)){result.put("flag", "6");result.put("msg", "客户预约信息获取错误");return result;}
			if(StringUtils.isBlank(type)){result.put("flag", "6");result.put("msg", "客户预约信息获取错误");return result;}
			if(serviceArr==null||serviceArr.length==0){result.put("flag", "7");result.put("msg", "服务内容获取错误");return result;}
			boolean isApp=customerAppointementDao.findByCode(code)==null?false:true;//重复提交
			if(!isApp){
				boolean timeIsTakeup=timeIsTakeUp(date,timeCode);//时间占用
				if(!timeIsTakeup){
					AppointmentTime at=saveAppointmentTime(CodeUtil.creatUUIDCode(), date, timeCode, null);
					if(at==null){result.put("flag", "3");result.put("msg", "预约时间获取错误");return result;}
					appointmentTimeDao.save(at);
					Customer cus=customerManager.findCustomerByMobile(mobile);//判断用户是否已存在
					if(cus==null){
						cus=customerManager.saveNewCustomer(CodeUtil.creatUUIDCode(), mobile, name, address, null, openid,new Date(),null,null);
						if(cus==null){result.put("flag", "4");result.put("msg", "客户信息获取错误");return result;}
						customerManager.saveCustomer(cus);
					}else{
						cus.setModifiedTime(new Date());
						cus.setName(name);
						cus.setRecentAddress(address);
						if(StringUtils.isNotBlank(openid)){
							cus.setWeixinCode(weixinCustomerManager.findCodeByOpenid(openid));
						}		
						customerManager.updateCustomer(cus);
					}
					CustomerCar car=customerManager.findByCusAndCarInfo(cus.getCode(),carCode,license);//判断用户车辆是否已存在
					if(car==null){
						car=customerManager.saveNewCustomerCar(CodeUtil.creatUUIDCode(),cus.getCode(),carCode,license,new Date(),null, null, null, null, null, null, null, null);
						if(car==null){result.put("flag", "5");result.put("msg", "客户车辆信息获取错误");return result;}
						customerManager.saveCustomerCar(car);
					}
					cus.setRecentVehicle(car.getCode());
					customerManager.updateCustomer(cus);
					CustomerAppointement ca=saveNewCustomerAppointement(code,cus.getCode(),car.getCode(),address,at,appTypeCode,null,remark,totalPriceStr,type, new Date(), null,"1",name, null, null, null, null, null, null);
					if(ca==null){result.put("flag", "6");result.put("msg", "客户预约信息获取错误");return result;}
					customerAppointementDao.add(ca);
					String accCode="";
					if(serviceArr!=null&&serviceArr.length>0){
						CustomerService cs=null;
						for(int i=0;i<serviceArr.length;i++){
							String[] info=serviceArr[i].split("_");
							//cs=saveNewCustomerService(CodeUtil.creatUUIDCode(), ca.getCode(), info[0], info[1], info[2],carCode,new Date(),null);
							if("0".equals(info[1])||"-1".equals(info[1])){
								accCode=info[1];
							}else{
								accCode=info[0]+info[1];
							}
							cs=carMaintenanceManager.saveCustomerServiceInfo(CodeUtil.creatUUIDCode(), ca.getCode(), info[0], accCode, null, info[2], null, carCode);
							if(cs==null){result.put("flag", "7");result.put("msg", "服务内容获取错误");return result;}
							customerToServiceDao.add(cs);
						}
					}else{result.put("flag", "7");result.put("msg", "服务内容获取错误");return result;}
					result.put("flag", "1");
					result.put("msg", "操作成功");
					List<Employee> kefuList=employeeManager.findEmpByRoleCode(PropertiesUtils.getValueByKey("role_kefu"));
					for(Employee kefu:kefuList){
						String kefuWX=employeeManager.findEmployeeOpenidByCode(kefu.getCode());
						if(kefuWX!=null&&!"".equals(kefuWX)){
							sendManager.sendTextMsgToWeixinUser(kefuWX, "有新的预约了！");;
						}
					}
					return result;
				}else{result.put("flag", "2");result.put("msg", "时间已预约");return result;}
			}
			result.put("flag", "1");
			result.put("msg", "操作成功");
		} catch (Exception e) {
			outPutErrorInfor(BusinessManagerImpl.class.getName(), "appointmentMaintenance", e);
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public synchronized Map<String, String> appointmentConfirm(String timeCode, String date, String time, String carCode, String license, String haveCar,String modelsCode,String modelsStr, String cusCode, String mobile, String name, String address, String code, String remark, String totalPriceStr, String[] serviceArr, String confirmCode, String status,String wxCode) throws Exception{
		Map<String, String> result=new HashMap<String, String>();
		if(StringUtils.isBlank(timeCode)){result.put("flag", "0");result.put("msg", "预约时间获取错误");return result;}
		if(StringUtils.isBlank(date)){result.put("flag", "0");result.put("msg", "预约时间获取错误");return result;}
		if(StringUtils.isBlank(time)){result.put("flag", "0");result.put("msg", "预约时间获取错误");return result;}
		if(StringUtils.isBlank(carCode)){result.put("flag", "0");result.put("msg", "车辆信息获取错误");return result;}
		if(StringUtils.isBlank(haveCar)){result.put("flag", "0");result.put("msg", "车辆信息获取错误");return result;}
		if(StringUtils.isBlank(license)){result.put("flag", "0");result.put("msg", "车辆信息获取错误");return result;}
		if(StringUtils.isBlank(cusCode)){result.put("flag", "0");result.put("msg", "客户信息获取错误");return result;}
		if(StringUtils.isBlank(mobile)){result.put("flag", "0");result.put("msg", "客户信息获取错误");return result;}
		if(StringUtils.isBlank(name)){result.put("flag", "0");result.put("msg", "客户信息获取错误");return result;}
		if(StringUtils.isBlank(code)){result.put("flag", "0");result.put("msg", "预约信息获取错误");return result;}
		if(StringUtils.isBlank(address)){result.put("flag", "0");result.put("msg", "预约信息获取错误");return result;}
		if(StringUtils.isBlank(totalPriceStr)){result.put("flag", "0");result.put("msg", "预约信息获取错误");return result;}
		if(serviceArr==null||serviceArr.length==0){result.put("flag", "0");result.put("msg", "服务内容获取错误");return result;}
		
		AppointmentTime at=appointmentTimeDao.findByCode(timeCode);
		if(at!=null){
			if(!at.getDate().equals(date)||!at.getServiceCode().equals(time)){
				boolean timeIsTakeup=timeIsTakeUp(date,time);//时间占用
				if(!timeIsTakeup){
					at.setDate(date);
					at.setServiceCode(time);
					ServiceTime st=serviceManager.findTimeByCode(time);
					at.setTime(st.getTime());
					at.setAddress(address);
					at.setStatus("2");
					appointmentTimeDao.update(at);
				}else{
					result.put("flag", "0");result.put("msg", "预约时间已满");return result;
				}
			}
		}else{
			result.put("flag", "0");result.put("msg", "预约时间获取错误");return result;
		}	
		CustomerCar car=null;
		if(haveCar.equals("1")){
			car=customerManager.findByCusAndCarInfo(cusCode, modelsCode, license);
			if(car!=null){
				if(!car.getCode().equals(carCode)){
					customerManager.deleteCarByCode(carCode);
					carCode=car.getCode();
				}
			}else{
				car=customerManager.saveNewCustomerCar(carCode, cusCode, modelsCode, license, null, new Date(), null, null, null, null, null, null, null);
			}
			if(car!=null){
				customerManager.updateCustomerCar(car);
			}else{result.put("flag", "0");result.put("msg", "车辆信息获取错误");return result;}
		}else if(haveCar.equals("0")){
			car=customerManager.findCarByCode(carCode);
			if(car!=null&&StringUtils.isNotBlank(modelsStr)){
				car.setLicensePlate(license);
				String[] str=modelsStr.split("_");
				car.setBrand(str[0]);
				car.setBrandCode(null);
				car.setSeries(str[1]);
				car.setSeriesCode(null);
				car.setModels(str[2]);
				car.setModelsCode(null);
				car.setModifiedTime(new Date());
				customerManager.updateCustomerCar(car);
			}else{result.put("flag", "0");result.put("msg", "车辆信息获取错误");return result;}					
		}				
		Customer cus=customerManager.saveNewCustomer(cusCode, mobile, name, address, carCode, null,null,new Date(),wxCode);
		if(cus!=null){
			customerManager.updateCustomer(cus);
		}else{result.put("flag", "0");result.put("msg", "客户信息获取错误");return result;}		
		if("1".equals(status)){//为1表示为新确认的订单，需将订单状态改为分配
			status="2";
		}
		CustomerAppointement ca=saveNewCustomerAppointement(code, cusCode, carCode, address, at, null, null, remark, totalPriceStr, null, null, confirmCode,status,name, null, null, null, null, null, null);
		if(ca!=null){
			customerAppointementDao.update(ca);
		}else{result.put("flag", "0");result.put("msg", "预约信息获取错误");return result;}
		String accCode="";
		if(serviceArr!=null&&serviceArr.length>0){
			List<CustomerService> newList=new ArrayList<CustomerService>();
			CustomerService cs=null;
			for(int i=0;i<serviceArr.length;i++){
				String[] info=serviceArr[i].split("_");
				//cs=saveNewCustomerService(info[0], ca.getCode(), info[1], info[2].length()>4?info[2].substring(4, info[2].length()):info[2], info[3],modelsCode,null,confirmCode);
				cs=carMaintenanceManager.saveCustomerServiceInfo(info[0], ca.getCode(), info[1], info[2], null, info[3], confirmCode, modelsCode);
				if(cs!=null){
					if("add".equals(cs.getCode())){
						cs.setCode(CodeUtil.creatUUIDCode());
						cs.setCreatTime(new Date());
						customerToServiceDao.add(cs);
					}
					customerToServiceDao.update(cs);
					newList.add(cs);
				}else{result.put("flag", "0");result.put("msg", "服务内容获取错误");return result;}
			}
			List<CustomerService> oldList=customerToServiceDao.findAppServiceByAppCode(ca.getCode());
			for(CustomerService old:oldList){
				boolean delete=true;
				for(CustomerService n:newList){
					if(old.getCode().equals(n.getCode())){
						delete=false;
					}
				}
				if(delete){
					customerToServiceDao.deleteById(old.getId());
				}
			}
		}
		String cusOpenid=customerManager.findOpenidByCusCode(cusCode);
		if(StringUtils.isNotBlank(cusOpenid)&&"1".equals(status)){
			sendManager.sendTextMsgToWeixinUser(cusOpenid, "您的预约已成功排班！");
		}
		List<WorkRecord> erList=workRecordManager.findByAppCode(code);
		if(erList!=null&&erList.size()>0){
			for(WorkRecord wr:erList){
				String openid=employeeManager.findEmployeeOpenidByCode(wr.getEmpCode());
				if(StringUtils.isNotBlank(openid)){
					//String url=WeixinCons.user_authorization_url.replace("REDIRECT_URI", Constants.root+"/wxManager/appointmentView.html").replace("APPID", WeixinCons.appid).replace("SCOPE", "snsapi_base").replace("STATE", "123");
					sendManager.sendTextMsgToWeixinUser(openid, "任务有修改了！ ");
				}
			}	
		}else{
			List<Employee> paibanList=employeeManager.findEmpByRoleCode("role_paigong");//systemParamManager.findValueListByName("paiban");
			for(Employee paiban:paibanList){
				String paibanWX=employeeManager.findEmployeeOpenidByCode(paiban.getCode());
				
				if(paibanWX!=null&&!"".equals(paibanWX)){
					if("1".equals(status)){
						//String url=WeixinCons.user_authorization_url.replace("REDIRECT_URI", Constants.root+"/wxManager/appointmentDistribute.html").replace("APPID", WeixinCons.appid).replace("SCOPE", "snsapi_base").replace("STATE", "123");
						sendManager.sendTextMsgToWeixinUser(paibanWX, "有新的预约需要排班了！ ");;
					}else if("2".equals(status)){
						//String url=WeixinCons.user_authorization_url.replace("REDIRECT_URI", Constants.root+"/wxManager/appointmentDistribute.html").replace("APPID", WeixinCons.appid).replace("SCOPE", "snsapi_base").replace("STATE", "123");
						sendManager.sendTextMsgToWeixinUser(paibanWX, "预约内容有修改了！ ");
					}
				}
			}
		}
		result.put("flag", "1");
		return result;
	}

	@Override
	public Map<String, String> appointmentCancel(String code,String employeeCode,String reason){
		Map<String, String> result=new HashMap<String, String>();
		try {
			CustomerAppointement ca=customerAppointementDao.findByCode(code);
			if(ca.getStatus().equals("3")){
				List<WorkRecord> wrList=workRecordManager.findByAppCode(code);
				for(WorkRecord wr :wrList){
					String openid=employeeManager.findEmployeeOpenidByCode(wr.getEmpCode());
					sendManager.sendTextMsgToWeixinUser(openid, code+"号 订单取消"); 
				}
			}
			ca.setStatus("0");
			ca.setConfirmCode(employeeCode);
			ca.setConfirmTime(new Date());
			ca.setEndRemark(reason);
			customerAppointementDao.update(ca);
			appointmentTimeDao.deleteByCode(ca.getAppTimeCode());
			result.put("flag", "1");
		} catch (Exception e) {
			outPutErrorInfor(BusinessManagerImpl.class.getName(), "appointmentCancel", e);
			result.put("flag", "0");
			return result;
		}
		return result;
	}

	@Override
	public PageBean selectAppointment(Map<String,String> argMap) {
		PageBean page=new PageBean();
		List<AppointmentPageBean> result=customerAppointementDao.selectAppointmentByArgs(argMap);
		Integer count=customerAppointementDao.countAppointmentByArgs(argMap);		
		page.setCount(count);
		page.setResultList(result);
		if(argMap.get("currentPage")!=null&&argMap.get("size")!=null){
			int size=Integer.valueOf(argMap.get("size"));
			page.setTotalPage(page.getTotalPage(count, size));
			page.setSize(size);
			page.setCurrentPage(argMap.get("currentPage"));
		}	
		return page;
	}

	@Override
	public CustomerAppointement findAppByCode(String code) {
		// TODO Auto-generated method stub
		return customerAppointementDao.findByCode(code);
	}

	private boolean timeIsTakeUp(String date,String timeCode) {
		/*List<AppointmentTime> timeList=appointmentTimeDao.findByDataAndTimeCode(date, timeCode);	
		List<Team>teamList=teamDao.findAll();
		if(timeList==null||timeList.size()<teamList.size()){
			return false;
		}else
			return true;*/
		return false;
	}

	private AppointmentTime saveAppointmentTime(String code, String date,
			String serviceCode, String teamCode) {
		AppointmentTime at=new AppointmentTime();
		try {
			if(code!=null&&!"".equals(code)){
				at.setCode(code);
			}else{
				return null;
			}
			if(date!=null&&!"".equals(date)){
				at.setDate(date);
			}else{
				return null;
			}
			if(serviceCode!=null&&!"".equals(serviceCode)){
				//ServiceTime st=serviceManager.findTimeByCode(serviceCode);
				at.setServiceCode(serviceCode);
				at.setStatus("1");
			}else{
				return null;
			}
			if(teamCode!=null&&!"".equals(teamCode)){
				at.setTeamCode(teamCode);
			}
		} catch (Exception e) {
			outPutErrorInfor(BusinessManagerImpl.class.getName(), "saveAppointmentTime", e);
			return null;
		}
		return at;
	}
	
	private CustomerAppointement saveNewCustomerAppointement(String code,
			String cusCode, String carCode, String address, AppointmentTime at,
			String appTypeCode, String teamCode, String remark,
			String totalPriceStr, String type, Date creatTime, String confirmCode, String status, String cusName,
			String implementCode,
			String payPrice,String advice,
			String score,String endRemark,Date startTime) {
		CustomerAppointement ca=new CustomerAppointement();
		try {
			if(code!=null&&!"".equals(code)){
				ca.setCode(code);
			}else{
				return null;
			}
			if(cusCode!=null&&!"".equals(cusCode)){
				ca.setCustomerCode(cusCode);
			}else{
				return null;
			}
			if(cusName!=null&&!"".equals(cusName)){
				ca.setCusName(cusName);
			}else{
				return null;
			}
			if(carCode!=null&&!"".equals(carCode)){
				ca.setCarCode(carCode);
			}else{
				return null;
			}
			if(address!=null&&!"".equals(address)){
				ca.setAddress(address);
			}else{
				return null;
			}
			if(at!=null){
				ca.setAppTimeCode(at.getCode());
				ServiceTime time=serviceManager.findTimeByCode(at.getServiceCode());
				ca.setAppTime(at.getDate()+" "+time.getTime());
			}else if(Integer.valueOf(status).intValue()<4){
				return null;
			}
			if(appTypeCode!=null&&!"".equals(appTypeCode)){
				ca.setAppTypeCode(appTypeCode);
			}
			if(teamCode!=null&&!"".equals(teamCode)){
				ca.setTeamCode(teamCode);
			}
			if(remark!=null&&!"".equals(remark)){
				ca.setRemark(remark);
			}
			if(totalPriceStr!=null&&!"".equals(totalPriceStr)){
				double totlaPrice=Double.valueOf(totalPriceStr);
				ca.setTotlaPrice(totlaPrice);
			}
			if(type!=null&&!"".equals(type)){
				ca.setType(type);
			}
			if(creatTime!=null){
				ca.setCreatTime(creatTime);
			}
			if(StringUtils.isNotBlank(confirmCode)){
				ca.setConfirmCode(confirmCode);
				ca.setConfirmTime(new Date());
			}
			if(StringUtils.isNotBlank(status)){
				ca.setStatus(status);
			}
			ca.setImplementCode(implementCode);
			if(StringUtils.isNotBlank(payPrice)){
				ca.setPayPrice(Double.valueOf(payPrice));
			}
			if(StringUtils.isNotBlank(score)){
				ca.setScore(Double.valueOf(score));
			}
			ca.setEndRemark(endRemark);
			ca.setAdvice(advice);
			ca.setStartTime(startTime);
		} catch (NumberFormatException e) {
			outPutErrorInfor(BusinessManagerImpl.class.getName(), "saveNewCustomerAppointement", e);
			return null;
		}	
		return ca;
	}

	@Override
	public Map findDetailedAppointment(String code) {
		Map map=new HashMap();
		CustomerAppointement ca=findAppByCode(code);
		AppointmentTime at=appointmentTimeDao.findByCode(ca.getAppTimeCode());
		Customer cus=customerManager.findCustomerByCode(ca.getCustomerCode());
		CustomerCar car=customerManager.findCarByCode(ca.getCarCode());
		List<CustomerService> csList=customerToServiceDao.findAppServiceByAppCode(code);
		map.put("at", at);
		map.put("ca", ca);
		map.put("cus", cus);
		map.put("car", car);
		map.put("cs", csList);
		map.put("time", FormatForI18N.getFormatDate(ca.getCreatTime(), "yyyy-MM-dd HH:mm:ss"));
		return map;
	}

	@Override
	public List<Team> findFreeTeam(String date, String time) {
		List<AppointmentTime> atList=appointmentTimeDao.findByDataAndTimeCode(date, time);
		List<Team> teamList=teamDao.findAll();
		List<Team> result=new ArrayList<Team>();
		for(Team t:teamList){
			boolean isFree=true;
			for(AppointmentTime at:atList){
				if(t.getCode().equals(at.getTeamCode())){
					isFree=false;
				}
			}
			if(isFree){
				result.add(t);
			}
		}
		return result;
	}
	
	@Override
	public Map<String, String> distributeWork(String appCode,
			String distributeCode, String teamCode, String[] emp) {
		Map<String, String> result=new HashMap<String, String>();
		CustomerAppointement ca=customerAppointementDao.findByCode(appCode);
		if(ca!=null){
			ca.setTeamCode(teamCode);
			ca.setStatus("3");
			customerAppointementDao.update(ca);
		}else{
			result.put("flag", "0");
			result.put("msg", "订单获取失败");
			return result;
		}
		AppointmentTime at=appointmentTimeDao.findByCode(ca.getAppTimeCode());
		if(at!=null){
			at.setTeamCode(teamCode);
			appointmentTimeDao.update(at);
		}else{
			result.put("flag", "0");
			result.put("msg", "预约时间获取失败");
			return result;
		}
		WorkRecord wr=new WorkRecord();
		for(int i=0;i<emp.length;i++){
			String[] str=emp[i].split("_");
			wr=workRecordManager.saveWorkRecordInfo(appCode, distributeCode, str[0], teamCode, str[1]);
			if(wr==null){
				result.put("flag", "0");
				result.put("msg", "信息获取错误");
				return result;
			}else{
				wr=workRecordManager.saveWorkRecord(wr);
				if(wr==null){
					result.put("flag", "0");
					result.put("msg", "系统异常");
					return result;
				}
			}			
			String openid=employeeManager.findEmployeeOpenidByCode(str[0]);
			if(StringUtils.isNotBlank(openid)){
				//String url=WeixinCons.user_authorization_url.replace("REDIRECT_URI", Constants.root+"/wxManager/appointmentView.html").replace("APPID", WeixinCons.appid).replace("SCOPE", "snsapi_base").replace("STATE", "123");
				sendManager.sendTextMsgToWeixinUser(openid, "有新的任务了！ ");
				/*int flag=sendAppointmentMsgToEmployee(openid, appCode);
				if(flag!=1){
					result.put("flag", "0");
					result.put("msg", "技师接受消息错误");
					return result;
				}*/
			}		
		}
		result.put("flag", "1");
		return result;
	}
	
	@Override
	public PageBean selectPersonalAppointment(Map<String, String> argMap) {
		try {
			PageBean page=new PageBean();
			List<AppointmentPageBean> result=customerAppointementDao.selectPersonalAppointment(argMap);
			Integer count=customerAppointementDao.countPersonalAppointmentByArgs(argMap);
			page.setCount(count);
			page.setResultList(result);
			if(argMap.get("size")!=null&&argMap.get("currentPage")!=null){
				int size=Integer.valueOf(argMap.get("size"));
				page.setSize(size);
				page.setTotalPage(page.getTotalPage(count, size));
				page.setCurrentPage(argMap.get("currentPage"));
			}
			return page;
		} catch (NumberFormatException e) {
			outPutErrorInfor(BusinessManagerImpl.class.getName(), "selectPersonalAppointment", e);
			return null;
		}
	}

	@Override
	public List<Employee> findFreeEmp(String code, String position) {
		try {
			CustomerAppointement ca=customerAppointementDao.findByCode(code);
			List<Employee> list1=employeeManager.findEmployeeByPosition(position);		
			AppointmentTime at=appointmentTimeDao.findByCode(ca.getAppTimeCode());
			if(at.getStatus().equals("2")){
				return list1;
			}else{
				List<WorkRecord> list2=workRecordManager.findByAppTime(ca.getAppTime());
				List<Employee> list=new ArrayList<Employee>();
				for(Employee emp:list1){
					boolean free=true;
					for(WorkRecord wr:list2){				
						if(emp.getCode().equals(wr.getEmpCode())){
							free=false;
						}
					}
					if(free){
						list.add(emp);
					}
				}
				return list;
			}
			
		} catch (Exception e) {
			outPutErrorInfor(BusinessManagerImpl.class.getName(), "findFreeEmp", e);
			return null;
		}
	}

	@Override
	public Map<String, String> daleteDistributeWork(String code, String empCode){
		Map<String, String> result=new HashMap<String, String>();
		try {
			List<WorkRecord> list =workRecordManager.findByAppCode(code);
			for(WorkRecord wr:list){
				wr.setDistributeCode(empCode);
				wr.setDistributeTime(new Date());
				wr.setStatus("0");
				workRecordManager.updateById(wr);
				String openid=employeeManager.findEmployeeOpenidByCode(wr.getEmpCode());
				sendManager.sendTextMsgToWeixinUser(openid, code+"号 订单取消");
			}
			CustomerAppointement ca=customerAppointementDao.findByCode(code);
			ca.setStatus("2");
			customerAppointementDao.update(ca);
			result.put("flag", "1");
			return result;
		} catch (Exception e) {
			outPutErrorInfor(BusinessManagerImpl.class.getName(), "findFreeEmp", e);
			result.put("flag", "0");
			result.put("msg", "操作异常");
			return result;
		}
	}

	@Override
	public Map<String, String> mobileValidate(String mobile, String validate) {
		Map<String, String> r=new HashMap<String, String>();
		if(validate.equals(Constants.cusMobile.get(mobile))){
			r.put("flag", "1");
		}else{
			r.put("flag", "0");
			r.put("msg", "验证码错误");
		}
		return r;
	}

	@Override
	public Map<String, String> finishAppointment(String code,String vipCondition) {
		Map<String, String> result=new HashMap<String, String>();
		try {
			CustomerAppointement ca=customerAppointementDao.findByCode(code);
			ca.setStatus("4");
			ca.setFinishTime(new Date());
			customerAppointementDao.update(ca);
			Customer cus=customerManager.findCustomerByCode(ca.getCustomerCode());
			if(StringUtils.isNotBlank(cus.getWeixinCode())){
				WeixinCustomer wx1=weixinCustomerManager.findByCode(cus.getWeixinCode());
				integralManager.inIntegral(wx1.getOpenid(), 2);
				if("1".equals(vipCondition)){
					if(weixinCustomerManager.findFinishVipCondition(wx1.getCode(), "3")==null){
						weixinCustomerManager.writeFinishVipCondition(wx1.getCode(), "3");
					}		
				}
				if(StringUtils.isNotBlank(wx1.getInviCode())){
					WeixinCustomer wx2=weixinCustomerManager.findByCode(wx1.getInviCode());
					integralManager.inIntegral(wx2.getOpenid(), 3);
					if(weixinCustomerManager.findFinishVipCondition(wx2.getCode(), "2")==null){
						weixinCustomerManager.writeFinishVipCondition(wx2.getCode(), "2");
					}		
				}
			}
			result.put("flag", "1");
			return result;
		} catch (Exception e) {
			outPutErrorInfor(BusinessManagerImpl.class.getName(), "finishAppointment", e);
			result.put("flag", "0");
			result.put("msg", "操作失败");
			return result;
		}
	}

	/*@Override
	public Map<String, String> writeAccessoriesModel(String code, String model,String weixinCode) {
		Map<String, String> result=new HashMap<String, String>();
		try {
			CustomerAppointement ca=customerAppointementDao.findByCode(code);
			List<CustomerService> csList=customerToServiceDao.findAppServiceByAppCode(code);
			if(StringUtils.isNotBlank(model)){
				CustomerCar car=customerManager.findCarByCode(ca.getCarCode());
				String[] modelArr=model.split(",");
				for(int i=0;i<modelArr.length;i++){
					if(StringUtils.isNotBlank(modelArr[i])){
						String[] arr=modelArr[i].split(":");
						AccessoriesModel am=accessoriesInfoManager.findModelByCarCodeAndAccCode(car.getModelsCode(), arr[0]);
						if(am==null){
							accessoriesInfoManager.saveModel(CodeUtil.creatUUIDCode(), car.getModelsCode(), arr[0], arr[1]);
						}
						for(CustomerService cs:csList){
							if(cs.getAccessoriesCode()!=null&&cs.getAccessoriesCode().equals(arr[0])){
								cs.setAccessoriesModel(arr[1]);
								customerToServiceDao.update(cs);
							}
						}
					}
				}
				if(StringUtils.isNotBlank("weixinCode")){
					Customer cus=customerManager.findCustomerByCode(ca.getCustomerCode());
					cus.setWeixinCode(weixinCustomerManager.formateWeixinCode(weixinCode));
					customerManager.updateCustomer(cus);
				}
				result.put("flag", "1");
				return result;
			}else{
				result.put("flag", "0");
				result.put("msg", "未获得信息");
				return result;
			}
		} catch (Exception e) {
			outPutErrorInfor(BusinessManagerImpl.class.getName(), "writeAccessoriesModel", e);
			result.put("flag", "0");
			result.put("msg", "操作失败");
			return result;
		}
	}*/

	@Override
	public List<Map<String, Object>> searchCustomerPensonalAppointmentByMobile(
			String mobile) {
		List<Map<String, Object>> result=new ArrayList<Map<String,Object>>();
		Map<String, Object> map=null;
		CustomerCar car =null;
		List<CustomerService> csList=null;
		Customer cus=customerManager.findCustomerByMobile(mobile);
		List<CustomerAppointement> appList=customerAppointementDao.findByCustomerCode(cus.getCode(),null);
		for(CustomerAppointement app:appList){
			map=new HashMap<String, Object>();
			csList=customerToServiceDao.findAppServiceByAppCode(app.getCode());
			car=customerManager.findCarByCode(app.getCarCode());
			map.put("car", car);
			map.put("app", app);
			map.put("service", csList);
			result.add(map);
		}
		return result;
	}

	@Override
	public List<Map> findCurrentAppByOpenid(String openid) {
		List<Map> result=new ArrayList<Map>();
		Map map=null;
		try {
			Customer cus=customerManager.findCustomerByOpenid(openid);
			System.out.println(cus);
			if(cus!=null){
				List<CustomerAppointement> appList=customerAppointementDao.findByCustomerCode(cus.getCode(),null);
				if(appList!=null&&appList.size()>0){
					int status=0;
					for(CustomerAppointement app:appList){
						status=Integer.valueOf(app.getStatus()).intValue();
						if(status>0&&status<5){
							map=new HashMap();
							map.put("app", app);
							map.put("time", FormatForI18N.getFormatDate(app.getCreatTime(), "yyyy-MM-dd HH:mm:ss"));
							CustomerCar car=customerManager.findCarByCode(app.getCarCode());
							map.put("car", car);
							List<CustomerService> list=customerToServiceDao.findAppServiceByAppCode(app.getCode());
							map.put("service", list);
							if(status>=3){
								List<WorkRecord> wrList= workRecordManager.findByAppCode(app.getCode());
								map.put("emp", wrList);
							}
							result.add(map);
						}
					}
				}		
			}
			return result;
		} catch (NumberFormatException e) {
			outPutErrorInfor(BusinessManagerImpl.class.getName(), "findCurrentAppByOpenid", e);
		}
		return null;
	}

	@Override
	public List<Map> searchCustomerPensonalAppointmentByOpenid(
			String openid) {
		List<Map> result=new ArrayList<Map>();
		try {
			Map map1=null;
			Map map2=null;
			Customer cus=customerManager.findCustomerByOpenid(openid);
			if(cus!=null){
				List<CustomerCar> carList=customerManager.findCustomerCarByCusCode(cus.getCode());
				if(carList!=null&&carList.size()>0){
					for(CustomerCar car:carList){
						List<CustomerAppointement> appList=customerAppointementDao.findByCustomerCode(cus.getCode(),car.getCode());
						if(appList!=null&&appList.size()>0){
							map1=new HashMap();
							int status=0;
							for(CustomerAppointement app:appList){
								status=Integer.valueOf(app.getStatus()).intValue();
								if(status>=5){
									List<CustomerService> cslist=customerToServiceDao.findAppServiceByAppCode(app.getCode());
									map2=new HashMap();
									map2.put("app", app);		
									map2.put("cs", cslist);
									map2.put("time", FormatForI18N.getFormatDate(app.getCreatTime(), "yyyy-MM-dd HH:mm:ss"));		
								}
							}
							map1.put("car", car);
							map1.put("history", map2);
							result.add(map1);
						}
					}		
				}
				return result;
			}
		} catch (NumberFormatException e) {
			outPutErrorInfor(BusinessManagerImpl.class.getName(), "searchCustomerPensonalAppointmentByOpenid", e);
		}
		return null;
	}

	@Override
	public List<Map> findCurrentCar(String openid) {
		List<Map> result=new ArrayList<Map>();
		Map map=null;
		try {
			Customer cus=customerManager.findCustomerByOpenid(openid);
			if(cus!=null){
				List<CustomerCar> carList=customerManager.findCustomerCarByCusCode(cus.getCode());
				if(carList!=null&&carList.size()>0){
					for(CustomerCar car:carList){
						List<CustomerAppointement> appList=customerAppointementDao.findByCustomerCode(cus.getCode(),car.getCode());
						if(appList!=null&&appList.size()>0){
							int status=0;
							boolean top=true;
							for(CustomerAppointement app:appList){
								status=Integer.valueOf(app.getStatus()).intValue();
								if(status>=5&&top){
									map=new HashMap();
									map.put("app", app);
									map.put("time", FormatForI18N.getFormatDate(app.getInPutTime(), "yyyy-MM-dd"));
									//CustomerCar car=customerManager.findCarByCode(app.getCarCode());
									map.put("car", car);
									List<CustomerCarSituation> list=customerManager.findByAppCode(app.getCode());
									map.put("situ", list);
									result.add(map);
									top=false;
								}
							}
						}	
					}
				}	
			}	
			return result;
		} catch (NumberFormatException e) {
			outPutErrorInfor(BusinessManagerImpl.class.getName(), "findCurrentCar", e);
			return null;
		}
	}

	@Override
	public Map<String, String> integralPay(String code, String openid,
			String busnessType, String pay) {
		Map<String, String> map=new HashMap<String, String>();
		try {
			int flag=integralManager.outIntegral(openid, Integer.valueOf(busnessType), Double.valueOf(pay));
			if(flag==1){
				CustomerAppointement ca=customerAppointementDao.findByCode(code);
				ca.setOffsetPrice(Double.valueOf(pay));
				customerAppointementDao.update(ca);
				Customer cus=customerManager.findCustomerByOpenid(openid);
				String msg="手机号为"+cus.getMobile()+"的"+cus.getName()+"用户使用会员积分冲抵了"+pay+"元的消费金额。";
				List<WorkRecord> list=workRecordManager.findByAppCode(code);
				for(WorkRecord wr:list){
					if(wr.getTeamRole().equals("技师")){
						String emp=employeeManager.findEmployeeOpenidByCode(wr.getEmpCode());
						sendManager.sendTextMsgToWeixinUser(emp, msg);
					}
				}				
				map.put("flag","1");
				return map;
			}else{
				map.put("flag", flag+"");
				return map;
			}
		} catch (NumberFormatException e) {
			outPutErrorInfor(BusinessManagerImpl.class.getName(), "integralPay", e);
			map.put("flag","0");
			return map;
		}
	}

	@Override
	public Map<String, String> inPutFinishInfo(JSONObject json,String empCode) {
		Map<String, String> result= new HashMap<String, String>();
		try {
			//客户信息
			String mobile=(String) json.get("mobile");
			String cusCode=(String) json.get("customerCode");//客户编号
			String cusName=(String) json.get("cusname");//姓名
			//订单信息
			String status=(String)json.get("status");//获取订单状态
			String appCode = (String) json.get("appCode");//得到订单号
			String implementCode=(String) json.get("implementCode");//编号
			String address=(String)json.get("address");//地址
			String startTime=(String)json.get("time");//接车时间
			String payPrice=(String)json.get("payPrice");//实付金额
			String advice=(String)json.get("advice");//技师建议
			String score=(String)json.get("score");//技师打分
			String endRemark=(String)json.get("endRemark");//总结备注
			String teamCode=(String)json.get("teamCode");//服务车
			//汽车信息
			String carModelCode = (String)json.get("modelsCode");//汽车型号
			String carCode = (String)json.get("carCode");//汽车编号
			String licensePlate=(String)json.get("carcard");//车牌
			String fixedYear =(String)json.get("caryear");//汽车年限
			String carefulTime=(String)json.get("shendate");//年审日期
			String maintenanceCycle=(String)json.get("bydate");//保养周期
			String engineCode=(String) json.get("engine");//发动机号
			String insuranceTime=(String) json.get("insurance");//保险日期
			String virCode=(String) json.get("vir");//vir码
			String mileage=(String)json.get("mileage");//里程
			//服务信息
			JSONArray arr1=(JSONArray) json.get("itemconfirm");//34项array
			JSONArray arr2=(JSONArray) json.get("goodsconfirm");//物品确认
			JSONArray arr3=(JSONArray) json.get("carSitu");//环车检查
			JSONArray arr4=(JSONArray) json.get("service");//服务项目
			JSONArray arr5=(JSONArray) json.get("serviceEmp");//服务人员
			String[] itemconfirmsArray = new String[arr1.size()];//得到34项检查 
			String[] goodsconfirmArray = new String[arr2.size()];//得到物品确认
			String[] carSituArray = new String[arr3.size()];//得到环车检查
			String[] serviceArray = new String[arr4.size()];//服务项目
			String[] serviceEmpArray = new String[arr5.size()];//服务人员
			itemconfirmsArray=(String[]) arr1.toArray(itemconfirmsArray);
			goodsconfirmArray=(String[]) arr2.toArray(goodsconfirmArray);
			carSituArray=(String[]) arr3.toArray(carSituArray);
			serviceArray=(String[]) arr4.toArray(serviceArray);
			serviceEmpArray=(String[]) arr5.toArray(serviceEmpArray);
			int flag=0;
			//更新客户信息
			customerManager.saveNewCustomer(cusCode, mobile, cusName, address, null, null, null, new Date(),null);
			//更新汽车信息
			CustomerCar car = customerManager.saveNewCustomerCar(carCode, cusCode, null, licensePlate, null, new Date(), fixedYear, FormatForI18N.getDate(carefulTime), maintenanceCycle, engineCode, FormatForI18N.getDate(insuranceTime), virCode, mileage);
			customerManager.updateCustomerCar(car);
			//更新服务项目
			for (int i = 0; i < serviceArray.length; i++) {
				flag=carMaintenanceManager.updateCustomerServicebyAppointment(serviceArray[i], appCode,empCode);//更新服务项目
				if(flag==0){
					outPutErrorInfor(BusinessManagerImpl.class.getName(), "inPutFinishInfo", "服务项目详细获取失败");
					result.put("msg","0");
					return result;
				}
				accessoriesInfoManager.updateAccessoriesModel(serviceArray[i], carModelCode);
			}
			//更新工作表单信息
			for (int i = 0; i < serviceEmpArray.length; i++) {
				workRecordManager.updateByAppCode(appCode, serviceEmpArray[i],teamCode,empCode);
			}
			//更新汽车检测项目
			int a=0;
			int b=0;
			for (int i = 0; i < itemconfirmsArray.length; i++) {
				String situCode=itemconfirmsArray[i].substring(0, 4);
				String situresult=itemconfirmsArray[i].substring(5);			
				//根据situCode的到situName
				CarSituation situ=carInfoManager.findCarSituationBySituCode(situCode);
				a = customerManager.saveCarSituation(appCode, situCode, situresult, situ.getName(),status,situ.getType());
				b++;
				if(a==0){
					break;
				}	
			}
			for (int i = 0; i < goodsconfirmArray.length; i++) {
				String situCode=goodsconfirmArray[i].substring(0, 4);
				String situresult=goodsconfirmArray[i].substring(5);			
				//根据situCode的到situName
				CarSituation situ=carInfoManager.findCarSituationBySituCode(situCode);
				a = customerManager.saveCarSituation(appCode, situCode, situresult, situ.getName(),status,situ.getType());
				b++;
				if(a==0){
					break;
				}
			}
			for (int i = 0; i < carSituArray.length; i++) {
				String situCode=carSituArray[i].substring(0, 4);
				String situresult=carSituArray[i].substring(5);			
				//根据situCode的到situName
				CarSituation situ=carInfoManager.findCarSituationBySituCode(situCode);
				a = customerManager.saveCarSituation(appCode, situCode, situresult, situ.getName(),status,situ.getType());
				b++;
				if(a==0){
					break;
				}
			}
			status = "4".equals((String)json.get("status"))?"5":(String)json.get("status");//改变订单状态
			//更新订单信息
			CustomerAppointement ca = saveNewCustomerAppointement(appCode, cusCode, carCode, address, null, null, teamCode, null, null, null, null, null, status, cusName, implementCode, payPrice, advice, score, endRemark, FormatForI18N.getDate(startTime));
			ca.setInPutCode(empCode);
			ca.setInPutTime(new Date());
			customerAppointementDao.update(ca);
			if(b==itemconfirmsArray.length+carSituArray.length+goodsconfirmArray.length){
				result.put("msg","1");
			}else{
				result.put("msg","0");
			}
		} catch (Exception e) {
			outPutErrorInfor(BusinessManagerImpl.class.getName(), "inPutFinishInfo", e);
		}
		return result;
	}

	@Override
	public List<AppointmentTime> findAppTime(String date) {
		return appointmentTimeDao.findAppTime(date);
	}

	@Override
	public Map<String, String> saveCustomerScore(String code, String score) {
		Map<String, String> map=new HashMap<String, String>();
		try {
			List<WorkRecord> list=workRecordManager.findByAppCode(code);
			for(WorkRecord wr:list){
				if(!wr.getTeamRole().equals("学徒")){
					wr.setScore(Integer.valueOf(score));
					workRecordManager.updateById(wr);
				}
			}
			map.put("flag", "1");
			return map;
		} catch (NumberFormatException e) {
			outPutErrorInfor(BusinessManagerImpl.class.getName(), "saveCustomerScore", e);
		}
		return null;
	}

	@Override
	public List<CustomerCar> findHistoryCar(String openid) {
		List<CustomerCar> result=new ArrayList<CustomerCar>();
		try {
			Customer cus=customerManager.findCustomerByOpenid(openid);
			if(cus!=null){
				List<CustomerCar> carList=customerManager.findCustomerCarByCusCode(cus.getCode());
				if(carList!=null&&carList.size()>0){
					for(CustomerCar car:carList){
						List<CustomerAppointement> appList=customerAppointementDao.findByCustomerCode(cus.getCode(),car.getCode());
						if(appList!=null&&appList.size()>0){
							int status=0;
							boolean add=true;
							for(CustomerAppointement app:appList){
								status=Integer.valueOf(app.getStatus()).intValue();
								if(status>=5&&add){
									result.add(car);
									add=false;
								}
							}
						}	
					}
				}	
			}	
			return result;
		}catch(Exception e){
			outPutErrorInfor(BusinessManagerImpl.class.getName(), "findHistoryCar", e);
			return null;
		}
	}

	@Override
	public List<Map> findHistoryAppByCarCode(String carCdoe) {
		try {
			List<CustomerAppointement> appList=customerAppointementDao.findByCustomerCode(null,carCdoe);
			List<Map> result =new ArrayList<Map>();
			Map map=null;
			if(appList!=null&&appList.size()>0){
				int status=0;
				for(CustomerAppointement app:appList){
					status=Integer.valueOf(app.getStatus()).intValue();
					if(status>=5){
						map=new HashMap();
						map.put("ca", app);
						map.put("cs", customerToServiceDao.findAppServiceByAppCode(app.getCode()));
						map.put("car", customerManager.findCarByCode(app.getCarCode()));
						result.add(map);
					}
				}
			}
			return result;
		} catch (Exception e) {
			outPutErrorInfor(BusinessManagerImpl.class.getName(), "findHistoryAppByCarCode", e);
			return null;
		}
	}		
}
