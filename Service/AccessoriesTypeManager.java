package com.icb123.Service;

import java.util.List;

import com.icb123.bean.AccessoriesType;

public interface AccessoriesTypeManager {
	
    public List<AccessoriesType> selectAll();
  	
	public int addAccessoriesType(String code,String typeName);
	
	public String findMaxCode();
	
	public int textName(String type);

}
