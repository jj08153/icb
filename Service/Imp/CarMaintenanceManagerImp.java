package com.icb123.Service.Imp;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.icb123.Dao.AppointmentTimeDao;
import com.icb123.Dao.CustomerAppointementDao;
import com.icb123.Dao.CustomerToServiceDao;
import com.icb123.Dao.ServiceDao;
import com.icb123.Dao.TeamDao;
import com.icb123.Service.AccessoriesInfoManager;
import com.icb123.Service.CarMaintenanceManager;
import com.icb123.Service.ServiceManager;
import com.icb123.Util.CodeUtil;
import com.icb123.Util.SystemModelExceptionBase;
import com.icb123.bean.AccessoriesInfo;
import com.icb123.bean.AccessoriesModel;
import com.icb123.bean.CustomerAppointement;
import com.icb123.bean.CustomerService;
import com.icb123.bean.Team;

@Service("carMaintenanceManager")
public class CarMaintenanceManagerImp extends SystemModelExceptionBase implements CarMaintenanceManager{

	@Resource
	private AppointmentTimeDao appointmentTimeDao;
	@Resource
	private CustomerAppointementDao customerAppointementDao;
	@Resource
	private CustomerToServiceDao customerToServiceDao;
	@Resource
	private TeamDao teamDao;
	@Resource
	private ServiceDao serviceDao;
	@Resource
	private AccessoriesInfoManager accessoriesInfoManager;
	@Resource
	private ServiceManager serviceManager;
	
	@Override
	public int updateCustomerServicebyAppointment(String service,String appCode,String empCode){
		try {
			String[] serviceStr=service.split("_");
			String serviceType=serviceStr[0];
			String accessoriesCode=serviceStr[1];
			String accessoriesModel=serviceStr[2];
			String accessoriesNumber=serviceStr[3];
			String code=serviceStr[4];//customerServiceCode为空时方法为添加，不为空时为修改	
			//通过项目型号找到项目的所有信息
			if(("-2".equals(accessoriesCode))&&(!"0".equals(code))){//删除该条信息
				return customerToServiceDao.deleteByCode(code);
			}else if((!"-2".equals(accessoriesCode))&&("0".equals(code))){//增加这条信息
				CustomerService cs= saveCustomerServiceInfo(CodeUtil.creatUUIDCode(), appCode, serviceType, accessoriesCode, accessoriesModel, accessoriesNumber, null, "");
				if(cs!=null){
					return customerToServiceDao.add(cs);	
				}else{
					return 0;
				}	
			}else if((!"-2".equals(accessoriesCode))&&(!"0".equals(code))){//修改这条信息
				CustomerService cs= saveCustomerServiceInfo(code, appCode, serviceType, accessoriesCode, accessoriesModel, accessoriesNumber, empCode, "");
				if(cs!=null){
					return customerToServiceDao.update(cs);
				}else{
					return 0;
				}
			}
			return 1;
		} catch (Exception e) {
			outPutErrorInfor(CarMaintenanceManagerImp.class.getName(), "updateCustomerServicebyAppointment", e);
			return 0;
		}
	}

	@Override
	public List<Team> findAllTeam() {
		// TODO Auto-generated method stub
		return teamDao.findAll();
	}

	@Override
	public CustomerService saveCustomerServiceInfo(String code,
			String subscribeCode, String serviceCode, String accessoriesCode,
			String accessoriesModel, String accessoriesNum, String modifiedId, String carCode) {
		try {
			CustomerService cs = new CustomerService();
			if(StringUtils.isNotBlank(code)){
				cs.setCode(code);
			}else{
				return null;
			}
			if(StringUtils.isNotBlank(subscribeCode)){
				cs.setSubscribeCode(subscribeCode);
			}else{
				return null;
			}
			if(StringUtils.isNotBlank(serviceCode)){
				com.icb123.bean.Service service=serviceManager.findServiceByCode(serviceCode);
				if(service!=null){
					cs.setServiceCode(service.getCode());
					cs.setServiceName(service.getName());
					cs.setServicePrices(service.getPrice()*service.getDiscount());
				}else{
					return null;
				}
			}else{
				return null;
			}
			if(StringUtils.isNotBlank(accessoriesCode)){
				AccessoriesInfo acc=accessoriesInfoManager.findAccseeoriesByCode(accessoriesCode);
				if(acc!=null){
					cs.setAccessoriesCode(acc.getCode());
					cs.setAccessoriesName(acc.getName());
					cs.setAccessoriesPrices(acc.getOutPrice());
				}else if("0".equals(accessoriesCode)){
					cs.setAccessoriesCode(accessoriesCode);
					cs.setAccessoriesName("待定");
					cs.setAccessoriesPrices(0.0);
				}else if("-1".equals(accessoriesCode)){
					cs.setAccessoriesCode(accessoriesCode);
					cs.setAccessoriesName("自备");
					cs.setAccessoriesPrices(0.0);
				}
				if(StringUtils.isBlank(accessoriesModel)){
					if(StringUtils.isNotBlank(carCode)){
						AccessoriesModel am=accessoriesInfoManager.findBestModelByCarCodeAndAccCode(carCode, accessoriesCode);
						if(am!=null){
							cs.setAccessoriesModel(am.getModel());
						}
					}		
				}else{
					cs.setAccessoriesModel(accessoriesModel);
				}
				if(StringUtils.isNotBlank(accessoriesNum)){
					cs.setAccessoriesNum(Integer.valueOf(accessoriesNum));
				}else{
					cs.setAccessoriesNum(Integer.valueOf(1));
				}
			}
			if(StringUtils.isBlank(modifiedId)){
				cs.setCreatTime(new Date());
			}else{
				cs.setModifiedId(modifiedId);
				cs.setModifiedTime(new Date());
			}
			return cs;
		} catch (NumberFormatException e) {
			outPutErrorInfor(CarMaintenanceManagerImp.class.getName(), "saveCustomerServiceInfo", e);
			return null;
		}
	}
}
