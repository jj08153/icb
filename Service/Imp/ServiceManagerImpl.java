package com.icb123.Service.Imp;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.icb123.Dao.ServiceDao;
import com.icb123.Dao.ServiceTimeDao;
import com.icb123.Service.ServiceManager;
import com.icb123.Util.SystemModelExceptionBase;
import com.icb123.bean.ServiceTime;

@Service("serviceManager")
public class ServiceManagerImpl extends SystemModelExceptionBase implements ServiceManager{

	@Resource
	private ServiceDao serviceDao;
	@Resource
	private ServiceTimeDao serviceTimeDao;
	@Override
	public ServiceTime findTimeByCode(String timeCode){
		return serviceTimeDao.findByCode(timeCode);
	}
	@Override
	public com.icb123.bean.Service findServiceByCode(String serviceCode){
		return serviceDao.findByCode(serviceCode);
	}
	@Override
	public List<ServiceTime> findAllServiceTime() {
		// TODO Auto-generated method stub
		return serviceTimeDao.findAllServiceTime();
	}
	@Override
	public List<com.icb123.bean.Service> findServiceByParentCode(
			String parentCode) {
		// TODO Auto-generated method stub
		return serviceDao.findServiceByParentCode(parentCode);
	}
}
