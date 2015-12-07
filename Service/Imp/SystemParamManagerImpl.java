package com.icb123.Service.Imp;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.icb123.Common.WeixinCons;
import com.icb123.Dao.SystemParamDao;
import com.icb123.Service.SystemParamManager;
import com.icb123.Util.SystemModelExceptionBase;
import com.icb123.bean.SystemParam;
@Service("systemParamManager")
public class SystemParamManagerImpl extends SystemModelExceptionBase implements SystemParamManager {

	@Autowired
	public SystemParamDao systemParamDao;
	@Override
	public SystemParam findById(String id){
		// TODO Auto-generated method stub
		return systemParamDao.findById(id);
	}

	@Override
	public String findValueByName(String name){
		// TODO Auto-generated method stub
		return systemParamDao.findValueByName(name);
	}

	@Override
	public String findValueById(String id){
		SystemParam sp=systemParamDao.findById(id);
		if(sp!=null){
			return sp.getValue();
		}
		return null;
	}

	@Override
	public List<SystemParam> findByParentId(String parentId){
		// TODO Auto-generated method stub
		return systemParamDao.findByParentId(parentId);
	}
	/**
	 * 预加载系统参数
	 * @throws Exception 
	 * */
	@Override
	public void setParams(){
		/*WeixinCons.appid=systemParamDao.findValueByName("appid");
		WeixinCons.appsecret=systemParamDao.findValueByName("appsecret");
		WeixinCons.token=systemParamDao.findValueByName("token");	*/
	}

	@Override
	public void afterPropertiesSet(){
		// TODO Auto-generated method stub
		setParams();
	}

	@Override
	public synchronized String customerCode(){
		String code = null;
		try {
			code = "1"+systemParamDao.findValueByName("weixin_code");
			//System.out.println("db===="+icbSystemParamDao.findValueByName("weixin_code"));
			int num=Integer.valueOf(code);
			num++;
			code=num+"";
			//System.out.println("cus===="+code);
			code=StringUtils.replace(code, "4", "5");
			//code.replace("4", "5");	
			code=code.substring(1, code.length());
			systemParamDao.saveValueByName("weixin_code",code);
		} catch (NumberFormatException e) {
			outPutErrorInfor(SystemParamManagerImpl.class.getName(), "customerCode", e);
			return null;
		}
		return code;
	}

	@Override
	public List<String> findValueListByName(String name){
		// TODO Auto-generated method stub
		return systemParamDao.findValueListByName(name);
	}

	@Override
	public String employeeCode() {
		String code = null;
		try {
			code = "1"+systemParamDao.findValueByName("employee_code");
			int num=Integer.valueOf(code);
			num++;
			code=num+"";
			code=StringUtils.replace(code, "4", "5");
			code=code.substring(1, code.length());
			systemParamDao.saveValueByName("employee_code",code);
		} catch (NumberFormatException e) {
			outPutErrorInfor(SystemParamManagerImpl.class.getName(), "employeeCode", e);
			return null;
		}
		return code;
	}

}
