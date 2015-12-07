package com.icb123.Service.Imp;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.icb123.Dao.CarInfoDao;
import com.icb123.Dao.CarSituationDao;
import com.icb123.Service.CarInfoManager;
import com.icb123.Util.CodeUtil;
import com.icb123.Util.SystemModelExceptionBase;
import com.icb123.bean.CarInfo;
import com.icb123.bean.CarSituation;
@Service("carInfoManager")
public class CarInfoManagerImpl extends SystemModelExceptionBase implements CarInfoManager{

	@Resource
	private CarInfoDao carInfoDao;
	@Resource
	private CarSituationDao carSituationDao;
	
	@Override
	public String findNameByCode(String code){
		CarInfo car=carInfoDao.findByCode(code);
		if(car!=null){
			return car.getName();
		}else
			return null;
	}

	@Override
	public CarInfo findByCode(String code){
		CarInfo car=carInfoDao.findByCode(code);
		if(car!=null){
			return car;
		}else
			return null;
	}
	@Override
	public void save(CarInfo car){
		carInfoDao.save(car);
	}

	@Override
	public List<CarInfo> findListByParentCode(String parentCode) {
		return carInfoDao.findListByParentCode(parentCode);
	}
	
	@Override
	public String findMaxCode(String parentCode){
	
		return carInfoDao.findMaxCode(parentCode);
	}

	@Override
	public int updateByCode(String name,String code,String orderCol){
		try {
			Date time=new Date();
			CarInfo car =new CarInfo();
			car.setCode(code);
			car.setName(name);
			car.setOrderCol(orderCol);
			car.setModifiedTime(time);
			car.setStatus("1");
			return carInfoDao.updateByCode(car);
		} catch (Exception e) {
			outPutErrorInfor(CarInfoManagerImpl.class.getName(), "updateByCode", e);
			return 0;
		}
	}

	@Override
	public String saveCar(String name, String parentCode, String orderCol,String maxCode){
		 try {
			CarInfo car =new CarInfo();
			 Date time=new Date();
			 String code="";
			 if(maxCode==null || maxCode.trim().length() == 0){
				 code = parentCode+"-"+"001";
			 }else{
			 String[] codeArr=maxCode.split("-");
			  if(codeArr.length==1){
				 code=CodeUtil.creatCarTreeCode(Integer.valueOf(codeArr[0]).intValue()+1, parentCode);
			 }else if(codeArr.length==2){
				 code=CodeUtil.creatCarTreeCode(Integer.valueOf(codeArr[1]).intValue()+1, parentCode);
			 }else if (codeArr.length==3) {
				 code=CodeUtil.creatCarTreeCode(Integer.valueOf(codeArr[2]).intValue()+1, parentCode);
			  }
			 }
			car.setCode(code);
			car.setName(name);
			car.setParentCode(parentCode);
			car.setOrderCol(orderCol);
			car.setCreatTime(time);
			car.setStatus("1");
			carInfoDao.save(car);
			return code;
		} catch (NumberFormatException e) {
			outPutErrorInfor(CarInfoManagerImpl.class.getName(), "saveCar", e);
			return null;
		}
	}
	@Override
	public int findName(String name,String parentCode){
		 try {
			CarInfo car =new CarInfo();
			 car.setParentCode(parentCode);
			 car.setName(name);
			return carInfoDao.findName(car);
		} catch (Exception e) {
			outPutErrorInfor(CarInfoManagerImpl.class.getName(), "findName", e);
			return 0;
		}
	}

	@Override
	public int deleteByCode(String code){
		// TODO Auto-generated method stub
		return carInfoDao.deleteByCode(code);
	}
	
	@Override
	public List<CarInfo> findNewCar(String parentCode) {
		List<CarInfo> list =null;
		try {
			return carInfoDao.findNewCar(parentCode);
		} catch (Exception e) {
			outPutErrorInfor(CarInfoManagerImpl.class.getName(), "findNewCar", e);
		}
		return list;
	}

	@Override
	public List<CarInfo> findUpdateCar(String parentCode) {
		List<CarInfo> list =null; 
		try {
			return carInfoDao.findUpdateCar(parentCode);
		} catch (Exception e) {
			outPutErrorInfor(CarInfoManagerImpl.class.getName(), "findUpdateCar", e);
		}
     	return list;
	}
	
	@Override
	public CarSituation findCarSituationBySituCode(String code) {
		return carSituationDao.findByCode(code);
	}
}
