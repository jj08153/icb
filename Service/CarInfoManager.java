package com.icb123.Service;

import java.util.List;

import com.icb123.bean.CarInfo;
import com.icb123.bean.CarSituation;

/**
 * 车辆信息业务
 * */
public interface CarInfoManager {

	public String findNameByCode(String code);
	public CarInfo findByCode(String code);
	public void save(CarInfo car);
	public List<CarInfo> findListByParentCode(String parentCode);
	public String findMaxCode(String parentCode);
	public int updateByCode(String name,String code,String orderCol);
	public int findName(String name,String parentCode);
	public int deleteByCode(String code);
	public String saveCar(String name,String parentCode,String orderCol,String maxCode);
	public  List<CarInfo> findNewCar(String parentCode);
	public  List<CarInfo> findUpdateCar(String parentCode);
	public CarSituation findCarSituationBySituCode(String situCode);
}
