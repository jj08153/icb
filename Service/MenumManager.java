package com.icb123.Service;

import java.util.List;

import com.icb123.bean.Menu;

public interface MenumManager {
	
	public List<Menu> quanXian(int id)throws Exception;

	public List<Menu> findMenu(String code);

	public List<Menu> selectAllMenu();
}
