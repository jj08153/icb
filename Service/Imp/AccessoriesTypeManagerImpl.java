package com.icb123.Service.Imp;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.icb123.Dao.AccessoriesTypeDao;
import com.icb123.Service.AccessoriesInfoManager;
import com.icb123.Service.AccessoriesTypeManager;
import com.icb123.Util.SystemModelExceptionBase;
import com.icb123.bean.AccessoriesType;
@Service("accessoriesTypeManager")
public class AccessoriesTypeManagerImpl extends SystemModelExceptionBase implements AccessoriesTypeManager{

	public AccessoriesTypeManagerImpl() {
		// TODO Auto-generated constructor stub
	}
	@Resource
	private AccessoriesTypeDao accessoriesTypeDao;

	@Override
	public List<AccessoriesType> selectAll() {
		List<AccessoriesType> list =new ArrayList<AccessoriesType>();
		 try{
			return  accessoriesTypeDao.selectAll();
		 } catch (Exception e) {
				outPutErrorInfor(AccessoriesTypeManagerImpl.class.getName(), "selectAll", e);
				return null;
		}	
	}

	@Override
	public int addAccessoriesType(String code,String typeName) {
		 try{
			 AccessoriesType accessoriesType =new AccessoriesType();
			 accessoriesType.setCode(code);
			 accessoriesType.setType(typeName);
			return accessoriesTypeDao.addAccessoriesType(accessoriesType);
		 }catch (Exception e) {
			outPutErrorInfor(AccessoriesTypeManagerImpl.class.getName(), "addAccessoriesType", e);
			return 0;
			}	
	    }

	@Override
	public String findMaxCode() {
		String string=null;
		try {
			return accessoriesTypeDao.findMaxCode();
		} catch (Exception e) {
			outPutErrorInfor(AccessoriesTypeManagerImpl.class.getName(), "addAccessoriesType", e);
			return string;
		}
	
	}

	@Override
	public int textName(String type) {
	  try {
		return accessoriesTypeDao.textName(type);
	} catch (Exception e) {
		outPutErrorInfor(AccessoriesTypeManagerImpl.class.getName(), "textName", e);
		return 0;
	   }
	  }
    }
