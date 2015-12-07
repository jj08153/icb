package com.icb123.Service.Imp;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.icb123.Dao.MenuDao;
import com.icb123.Service.MenumManager;
import com.icb123.Util.SystemModelExceptionBase;
import com.icb123.bean.Menu;
@Service("menuManager")
public class MenuManagerImpl extends SystemModelExceptionBase implements MenumManager{

	@Resource
	private MenuDao menuDao;
	 
	@Override
	public List<Menu> quanXian(int id){		
		return menuDao.quanXian(id);
	}
	@Override
	public List<Menu> findMenu(String code) {
		List<Menu> list=null;
		try {
			return menuDao.findMenu(code);
		} catch (Exception e) {
			outPutErrorInfor(MenuManagerImpl.class.getName(), "quanXian", e);
		}
		return list;
	}

	@Override
	public List<Menu> selectAllMenu() {
		List<Menu> list=null;
		try {
			return menuDao.selectAllMenu();
		} catch (Exception e) {
			outPutErrorInfor(MenuManagerImpl.class.getName(), "quanXian", e);
		}
	   return list;
	}
}
