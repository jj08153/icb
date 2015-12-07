package com.icb123.Service;

import java.util.List;
import java.util.Map;

import com.icb123.bean.AccessoriesInfo;
import com.icb123.bean.AccessoriesMatch;
import com.icb123.bean.AccessoriesModel;
import com.icb123.bean.AccessoriesStorage;
import com.icb123.bean.AccessoriesStorageRecord;
/**
 * 配件管理业务
 * */
public interface AccessoriesInfoManager {
	
	public List<AccessoriesInfo> selectAllAccessoriesInfo(Map map);

	public int saveAccessoriesInfo(AccessoriesInfo icbAccessoriesInfo );
	   
	public int deleteAccessoriesInfoById(int id);
	   
	public int updateAccessoriesInfoById(String name,String brand,String model,String manufacturer,String unit,String typeCode,int id,String code);
	   
	public  AccessoriesInfo findAccessoriesInfoById(int id);

	public AccessoriesInfo findAccseeoriesByCode(String code);

	public List<AccessoriesInfo> findServiceMatchAccessories(String serviceCode, String carCode);

	public void saveAccessMatch(AccessoriesMatch am);

	public List<AccessoriesInfo> findAccessoriesByType(String type);
	
	public AccessoriesModel findBestModelByCarCodeAndAccCode(String carCode, String accCode);
	
	public List<AccessoriesModel> findModelByCarCodeAndAccCode(String carCode, String accCode);
	
	public void saveModel(AccessoriesModel model);
	
	public AccessoriesModel saveModelInfo(String code,String carCode,String accCode,String model);
	
	public int countAccessoriesInfo();
	
	public List<AccessoriesInfo> findAccessoriesInfoByCode(Map map);
	
	public int countAccessoriesInfoBycode(String typeCode);
	
	public void saveAccessoriesInfo(String name,String brand,String model,String unit,String typeCode,String manufacturer,String code);

	public void updateAccessoriesModel(String service, String carModelCode);
	
	/**
	 * 封装配件进出库记录信息
	 * */
	public AccessoriesStorageRecord addAccessoriesStorageRecordInfo(String accCode,int num,double price,String empCode,String creatCode,String code,String ioType,String carCode,String beizhu,String moneyType);
	/**
	 * 保存配件进出库记录信息
	 * */
	public int addAccessoriesStorageRecordInfo(AccessoriesStorageRecord record);
	/**
	 * 获取配件出库记录
	 * */
	public List<Map<String, String>> findOutAccessoriesStorageRecord(String code);
	
	/**
	 * 获取配件入库记录
	 * */
	public List<Map<String, String>> findInAccessoriesStorageRecord(String code);
	
	/**
	 * 分页查询配件库存（数据）
	 * */
	public List<AccessoriesStorage> searchAccessoriesStorage(Map map);
	/**
	 * 分页查询配件库存（数量）
	 * */
    public int countAccessoriesStorage();
    /**
     * 获取所有配件库存
     * */
    public List<AccessoriesStorage> findAllAccessoriesStorage();
   /**
    * 获取指定配件
    * */
    public AccessoriesStorage findAccessoriesStorageById(int id);
   
    /**
     * 封装配件库存信息
     * */
    public AccessoriesStorage saveAccessoriesStorageInfo(String code,String newBrand,
			String newManufacturer, String typeCode, String unit,
			 String newModel,String creatCode, String modifiedCode);
    /**
     * 保存配件库存信息
     * */
    public int saveAccessoriesStorage(AccessoriesStorage storage);
    
    /**
     * 配件进出库操作
     * @param carCode 
     * */
	public Map<String, String> accInAndOutStorage(String accCode, String num,
			String price, String ioType, String beizhu, String empCode,
			String creatCode, String carCode,String moneyType);
	/**
	 * 更新配件库存
	 * */
	public int updateAccessoriesStorageById(AccessoriesStorage storage);
	
	/**
	 * 获取指定配件库存
	 * */
	public AccessoriesStorage findStorageByCode(String accCode);

	/**
	 * 添加或修改客村信息
	 * */
	public Map<String, String> saveOrUpdateAccessoriesStorage(String id,
			String newBrand, String newManufacturer, String typeCode,
			String unit, String newModel, String creatCode);

	/**
	 * 出库数量判断
	 * */
	public Map<String, Object> outNumberChecked(String code, String outStr);

	public int countByTypeCode(String typeCode);

	public List<AccessoriesStorage> searchByTypeCode(Map<String, Object> map);
	
	/**
	 * 获取指定类型所有配件信息
	 * */
	public List<AccessoriesStorage> findAccessoriesStorageByTypeCode(String code);
}
