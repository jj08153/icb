package com.icb123.Service.Imp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icb123.Dao.AccessoriesInfoDao;
import com.icb123.Dao.AccessoriesMatchDao;
import com.icb123.Dao.AccessoriesModelDao;
import com.icb123.Dao.AccessoriesStorageDao;
import com.icb123.Dao.AccessoriesStorageRecordDao;
import com.icb123.Service.AccessoriesInfoManager;
import com.icb123.Service.CarInfoManager;
import com.icb123.Util.CodeUtil;
import com.icb123.Util.OutputUtil;
import com.icb123.Util.SystemModelExceptionBase;
import com.icb123.bean.AccessoriesInfo;
import com.icb123.bean.AccessoriesMatch;
import com.icb123.bean.AccessoriesModel;
import com.icb123.bean.AccessoriesStorage;
import com.icb123.bean.AccessoriesStorageRecord;
@Transactional
@Service("accessoriesInfoManager")
public class AccessoriesInfoManagerImpl extends SystemModelExceptionBase implements AccessoriesInfoManager{
	@Resource
	private AccessoriesInfoDao accessoriesInfoDao;
	@Resource
	private AccessoriesMatchDao accessoriesMatchDao;
	@Resource
	private AccessoriesModelDao accessoriesModelDao;
	@Resource
	private  AccessoriesStorageDao accessoriesStorageDao;
	@Resource
	private  AccessoriesStorageRecordDao accessoriesStorageRecordDao;
	@Resource
	private CarInfoManager carInfoManager;

	@Override
	public List<AccessoriesInfo> selectAllAccessoriesInfo(Map map)  {
		
		return accessoriesInfoDao.selectAll(map);
	}

	@Override
	public int saveAccessoriesInfo(AccessoriesInfo icbAccessoriesInfo)  {
		
		return accessoriesInfoDao.add(icbAccessoriesInfo);
	}

	@Override
	public int deleteAccessoriesInfoById(int id)  {
		
		return accessoriesInfoDao.deleteById(id);
	}

	@Override
	public int updateAccessoriesInfoById(String name,String brand,String model,String manufacturer,String unit,String typeCode,int id,String code) {
		int num = 0; 
		try {
			AccessoriesInfo accessoriesInfo=new AccessoriesInfo();
			Date time =new Date();
			accessoriesInfo.setName(name);
			accessoriesInfo.setBrand(brand);
			accessoriesInfo.setModel(model);
			accessoriesInfo.setManufacturer(manufacturer);
			accessoriesInfo.setUnit(unit);
			accessoriesInfo.setTypeCode(typeCode);
			accessoriesInfo.setId(id);
			accessoriesInfo .setModifiedTime(time);
			accessoriesInfo.setModifiedCode(code);
			return accessoriesInfoDao.updateById(accessoriesInfo);
		} catch (Exception e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "updateById", e);
		}
		return num;
	}

	@Override
	public AccessoriesInfo findAccessoriesInfoById(int id) {
		// TODO Auto-generated method stub
		return accessoriesInfoDao.findById(id);
	}

	@Override
	public AccessoriesInfo findAccseeoriesByCode(String code){
		// TODO Auto-generated method stub
		return accessoriesInfoDao.findByCode(code);
	}

	@Override
	public List<AccessoriesInfo> findServiceMatchAccessories(String serviceCode,String carCode) {
		// TODO Auto-generated method stub
		return accessoriesInfoDao.findServiceMatchAccessories(serviceCode,carCode);
	}

	@Override
	public void saveAccessMatch(AccessoriesMatch am) {
		accessoriesMatchDao.save(am);		
	}

	@Override
	public List<AccessoriesInfo> findAccessoriesByType(String type) {
		// TODO Auto-generated method stub
		return accessoriesInfoDao.findAccessoriesByType(type);
	}

	@Override
	public AccessoriesModel findBestModelByCarCodeAndAccCode(String carCode, String accCode) {
		// TODO Auto-generated method stub
		return accessoriesModelDao.findBestModelByCarCodeAndAccCode(carCode,accCode);
	}
	
	@Override
	public List<AccessoriesModel> findModelByCarCodeAndAccCode(String carCode, String accCode) {
		// TODO Auto-generated method stub
		return accessoriesModelDao.findModelByCarCodeAndAccCode(carCode,accCode);
	}

	@Override
	public void saveModel(AccessoriesModel model) {
		// TODO Auto-generated method stub
		accessoriesModelDao.saveModel(model);
	}

	@Override
	public AccessoriesModel saveModelInfo(String code, String carCode,
			String accCode, String model) {
		try {
			AccessoriesModel accModel=new AccessoriesModel();
			if(StringUtils.isNotBlank(code)){
				accModel.setCode(code);
			}else{
				return null;
			}
			if(StringUtils.isNotBlank(carCode)){
				accModel.setCarCode(carCode);
			}else{
				return null;
			}
			if(StringUtils.isNotBlank(accCode)){
				accModel.setAccCode(accCode);
			}else{
				return null;
			}
			if(StringUtils.isNotBlank(model)){
				accModel.setModel(model);
			}else{
				return null;
			}
			return accModel;
		} catch (Exception e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "saveModel", e);
			return null;
		}
	}

	@Override
	public int countAccessoriesInfo() {
		int num = 0;
		try {
			return accessoriesInfoDao.countAccessoriesInfo();
		} catch (Exception e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "countAccessoriesInfo", e);
		}
		return num;
	}

	@Override
	public void saveAccessoriesInfo(String name, String brand, String model, String unit,String typeCode,String manufacturer,String code) {
		try {
			AccessoriesInfo accessoriesInfo=new AccessoriesInfo();
			Date time=new Date();
			accessoriesInfo.setBrand(brand);
			accessoriesInfo.setName(name);
			accessoriesInfo.setUnit(unit);
			accessoriesInfo.setModel(model);
			accessoriesInfo.setCreatTime(time);
			accessoriesInfo.setTypeCode(typeCode);
			accessoriesInfo.setManufacturer(manufacturer);
			accessoriesInfo.setCreatCode(code);
			accessoriesInfoDao.add(accessoriesInfo);	
		} catch (Exception e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "add", e);
		}
		
	}

	@Override
	public List<AccessoriesInfo> findAccessoriesInfoByCode(Map map) {
		List<AccessoriesInfo> list =null;
		 try {
			 return accessoriesInfoDao.findAccessoriesInfoByCode(map);
		} catch (Exception e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "findAccessoriesInfoByCode", e);
		}
		 return list;
	}

	@Override
	public int countAccessoriesInfoBycode(String typeCode) {
		 int num = 0;
		 try {
			 return accessoriesInfoDao.countBycode(typeCode);
		} catch (Exception e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "countBycode", e);
		}
		return num;
	}

	@Override
	public void updateAccessoriesModel(String service, String carModelCode) {
		try {
			String[] serviceStr=service.split("_");
			String accCode=serviceStr[0];
			String accessoriesModel=serviceStr[1];
			List<AccessoriesModel> list = findModelByCarCodeAndAccCode(carModelCode, accCode);
			if(list.size()==0){//第一次添加
				AccessoriesModel newam = new AccessoriesModel();
				newam.setAccCode(accCode);
				newam.setCarCode(carModelCode);
				newam.setModel(accessoriesModel);
				newam.setCode(CodeUtil.creatUUIDCode());
				newam.setIsBest("1");
				accessoriesModelDao.saveModel(newam);
			}else if(list.size()>0){
				for (int i = 0; i < list.size(); i++) {
					AccessoriesModel am =list.get(i);
					if(accessoriesModel.equals(am.getModel())){
						return;//如果找到跳出循环结束操作
					}else if(i == list.size()-1){//如果到最后一个还没有跳出循环则保存这条信息
						AccessoriesModel newam = new AccessoriesModel();
						newam.setAccCode(accCode);
						newam.setCarCode(carModelCode);
						newam.setModel(accessoriesModel);
						newam.setCode(CodeUtil.creatUUIDCode());
						newam.setIsBest("0");
						accessoriesModelDao.saveModel(newam);
					}
				}
				
			}
		} catch (Exception e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "updateAccessoriesModel", e);
		}
	}
	
	@Override
	public AccessoriesStorageRecord addAccessoriesStorageRecordInfo(String accCode, int num, double price, String empCode,String creatCode,String code,String ioType,String carCode,String beizhu,String moneyType) {
	    try {
	    	AccessoriesStorageRecord accessoriesStorageRecord=new AccessoriesStorageRecord();
	    	Date time =new Date();
	    	accessoriesStorageRecord.setAccCode(accCode);
	    	accessoriesStorageRecord.setNum(num);
	    	accessoriesStorageRecord.setPrice(price);
	    	accessoriesStorageRecord.setEmpCode(empCode);
	    	accessoriesStorageRecord.setCreatCode(creatCode);
	    	accessoriesStorageRecord.setIoType(ioType);
	    	accessoriesStorageRecord.setCreatTime(time);
	    	accessoriesStorageRecord.setCode(code);
	    	accessoriesStorageRecord.setCarCode(carCode);
	    	accessoriesStorageRecord.setBeizhu(beizhu);
	    	accessoriesStorageRecord.setIsPay(moneyType);
	    	//accessoriesStorageRecordDao.addAccessoriesStorageRecord(accessoriesStorageRecord);
	    	return accessoriesStorageRecord;
		} catch (Exception e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "addAccessoriesStorageRecord", e);
			return null;
		}
		
	}
	
	@Override
	public int addAccessoriesStorageRecordInfo(AccessoriesStorageRecord record){
		return accessoriesStorageRecordDao.addAccessoriesStorageRecord(record);
	}
	
	@Override
	public List<Map<String, String>> findOutAccessoriesStorageRecord(String code) {
	   	try {
			List<Map<String, String>> list=accessoriesStorageRecordDao.findRecordByAccCodeAndIoType(code,"0");
			for(Map<String, String> map:list){
				if(StringUtils.isBlank(map.get("carCode"))){
					map.put("carName","");
				}else{
					String[] strs=map.get("carCode").split("-");
			    	String fristName = carInfoManager.findNameByCode(strs[0]);
			    	String twoNameCode = strs[0]+"-"+strs[1];
			    	String twoName = carInfoManager.findNameByCode(twoNameCode);
			    	String ThirdNameCode = strs[0]+"-"+strs[1]+"-"+strs[2];
			    	String ThirdName = carInfoManager.findNameByCode(ThirdNameCode);
			    	map.put("carName",fristName+twoName+ThirdName);
				}
			}
			return list;
		} catch (Exception e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "findOutAccessoriesStorageRecord", e);
			return null;
		}
	}

	@Override
	public List<Map<String, String>> findInAccessoriesStorageRecord(String code) {
		List<Map<String, String>> list =new ArrayList<Map<String,String>>();
		 try {
			return accessoriesStorageRecordDao.findRecordByAccCodeAndIoType(code,"1");
		} catch (Exception e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "findInAccessoriesStorageRecord", e);
		}
		return list;
	}

	@Override
	public List<AccessoriesStorage> searchAccessoriesStorage(Map map) {
		List<AccessoriesStorage> list =new ArrayList<AccessoriesStorage>();
		try {
			return accessoriesStorageDao.searchAccessoriesStorage(map);
		} catch (Exception e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "searchAccessoriesStorage", e);
		}
		return list;
	}

	@Override
	public int countAccessoriesStorage() {
		int num = 0;
		try {
			return accessoriesStorageDao.countAccessoriesStorage();
		} catch (Exception e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "countAccessoriesStorage", e);
		}
		return num;
	}

	@Override
	public List<AccessoriesStorage> findAllAccessoriesStorage() {
		List<AccessoriesStorage> list =new ArrayList<AccessoriesStorage>();
		try {
			return accessoriesStorageDao.findAllAccessoriesStorage();
		} catch (Exception e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "findAllName", e);
		}
		return list;
	}

	@Override
	public AccessoriesStorage findAccessoriesStorageById(int id) {
		AccessoriesStorage list =new AccessoriesStorage();
		try {
			return accessoriesStorageDao.findById(id);
		} catch (Exception e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "findById", e);
		}
		return list;
	}

	@Override
	public AccessoriesStorage saveAccessoriesStorageInfo(String code,String newBrand,
			String newManufacturer, String typeCode, String unit,
			 String newModel,String creatCode, String modifiedCode)  {
		   try {
			   AccessoriesStorage accessoriesStorage =new AccessoriesStorage();
			   accessoriesStorage.setBrand(newBrand);
			   accessoriesStorage.setManufacturer(newManufacturer);
			   accessoriesStorage.setTypeCode(typeCode);
			   accessoriesStorage.setUnit(unit);
			   accessoriesStorage.setModel(newModel);
			   accessoriesStorage.setCode(code);
			   if(StringUtils.isNotBlank(creatCode)){
				   accessoriesStorage.setNum(0);
				   accessoriesStorage.setCreatTime(new Date());
				   accessoriesStorage.setCreatCode(creatCode);
			   }
			   if(StringUtils.isNotBlank(modifiedCode)){
				   accessoriesStorage.setModifiedCode(modifiedCode);
				   accessoriesStorage.setModifiedTime(new Date());
			   }
			   return   accessoriesStorage;  
		} catch (Exception e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "addNewAccessoriesStorage", e);
		}
		return null;
	}

	@Override
	public Map<String, String> accInAndOutStorage(String accCode, String num,
			String price, String ioType, String beizhu, String empCode,
			String creatCode, String carCode,String moneyType) {
		Map<String, String> map=new HashMap<String, String>();
		int flag=0;
		try {
			AccessoriesStorage storage=findStorageByCode(accCode);
			int n=Integer.valueOf(num);
			if("1".endsWith(ioType)){
				storage.setNum(storage.getNum()+n);
			}else if("0".equals(ioType)){
				storage.setNum(storage.getNum()-n);
			}
			storage.setModifiedCode(creatCode);
			storage.setModifiedTime(new Date());
			flag=updateAccessoriesStorageById(storage);
			if(flag==1){
				AccessoriesStorageRecord record= addAccessoriesStorageRecordInfo(accCode, Integer.valueOf(num), Double.valueOf(price), empCode, creatCode, CodeUtil.creatUUIDCode(), ioType, carCode, beizhu,moneyType);
				flag=addAccessoriesStorageRecordInfo(record);
				if(flag==0){
					map.put("msg", "0");
				}else{
					map.put("msg", "1");
				}
			}else{
				map.put("msg", "0");
			}
		} catch (NumberFormatException e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "accInAndOutStorage", e);
		}
		return map;
	}
	
	@Override
	public int updateAccessoriesStorageById(AccessoriesStorage storage) {
		return accessoriesStorageDao.updateAccessoriesStorageById(storage);
	}

	@Override
	public AccessoriesStorage findStorageByCode(String accCode) {
		return accessoriesStorageDao.findByCode(accCode);
	}

	@Override
	public int saveAccessoriesStorage(AccessoriesStorage storage) {
		// TODO Auto-generated method stub
		return accessoriesStorageDao.addNewAccessoriesStorage(storage);
	}

	@Override
	public Map<String, String> saveOrUpdateAccessoriesStorage(String id,
			String brand, String manufacturer, String typeCode,
			String unit, String model, String empCode) {
		Map<String, String> result =new HashMap<String, String>();
		int flag=0;
		try {
			boolean isChecked=AccessoriesStorageChecked(id,brand,model);
			if(isChecked){
				result.put("msg", "3");
			}else{
				if(id == ""){//保存
					 AccessoriesStorage storage = saveAccessoriesStorageInfo(CodeUtil.creatUUIDCode(),brand, manufacturer, typeCode, unit,  model, empCode,null);
					 if(storage!=null){
						 flag=saveAccessoriesStorage(storage);
						 if(flag!=0){
							 result.put("msg", "1");
						 }else{
							 result.put("msg", "0");
						 }
					 }else{
						 result.put("msg", "0");
					 }						   	    	   
				 }else{
					 AccessoriesStorage storage = saveAccessoriesStorageInfo(null,brand, manufacturer, typeCode, unit,  model,null ,empCode);
					 if(storage!=null){
						 storage.setId(Integer.valueOf(id));
						 flag=updateAccessoriesStorageById(storage);
						 if(flag!=0){
							 result.put("msg", "2");
						 }else{
							 result.put("msg", "0");
						 }
					 }else{
						 result.put("msg", "0");
					 }	
				 }
			}
		} catch (Exception e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "saveOrUpdateAccessoriesStorage", e);
		}
		return result;	   
	}

	private boolean AccessoriesStorageChecked(String id, String brand,
			String model) {
		try {
			List<AccessoriesStorage> list=findByBrandAndModel(brand,model);
			if(StringUtils.isBlank(id)){
				if(list!=null&&list.size()>0){
					return true;
				}else{
					return false;
				}
			}else{
				if(list!=null&&list.size()>0){
					if(list.size()==1&&list.get(0).getId()==Integer.valueOf(id).intValue()){
						return false;
					}else{
						return true;
					}
				}else{
					return false;
				}
			}
		} catch (Exception e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "AccessoriesStorageChecked", e);
			return true;
		}
	}

	private List<AccessoriesStorage> findByBrandAndModel(String brand,
			String model) {
		return accessoriesStorageDao.findByBrandAndModel(brand,model);
	}

	@Override
	public Map<String, Object> outNumberChecked(String code, String outStr) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			AccessoriesStorage storage=findStorageByCode(code);
			int num=Integer.valueOf(storage.getNum());
			int out=Integer.valueOf(outStr);
			if(num>=out){
				result.put("msg", 1);
			}else{
				result.put("msg", 2);
			}
		} catch (NumberFormatException e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "outNumberChecked", e);
		}
		return result;
	}

	@Override
	public int countByTypeCode(String typeCode) {
		try {
			return accessoriesStorageDao.countByTypeCode(typeCode);
		} catch (Exception e) {
			outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "countByTypeCode", e);
			return 0;
		}	
	}

	@Override
	public List<AccessoriesStorage> searchByTypeCode(Map<String, Object> map) {
		List<AccessoriesStorage> list =new ArrayList<AccessoriesStorage>();
		try {
			return accessoriesStorageDao.searchByTypeCode(map);
		}catch (Exception e) {
			 outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "selectAllByTypeCode", e);
		}
	    return list;
	}

	@Override
	public List<AccessoriesStorage> findAccessoriesStorageByTypeCode(String code) {/////修改
		List<AccessoriesStorage> list =new ArrayList<AccessoriesStorage>();
		try{
			return accessoriesStorageDao.findAccessoriesStorageByTypeCode(code);
		}catch (Exception e) {
			 outPutErrorInfor(AccessoriesInfoManagerImpl.class.getName(), "findAccessoriesStorageByCode", e);
		}
		 return list;
	}
}
