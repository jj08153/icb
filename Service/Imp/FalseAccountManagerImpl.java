package com.icb123.Service.Imp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.icb123.Dao.FalseAccountDao;
import com.icb123.Service.FalseAccountManager;
import com.icb123.Util.SystemModelExceptionBase;
import com.icb123.bean.FalseAccount;
@Service("falseAccountManager")
public class FalseAccountManagerImpl extends SystemModelExceptionBase implements FalseAccountManager{

	@Resource
	private FalseAccountDao falseAccountDao;
	@Override
	public void saveAccount(String name, String address, String models, String phone) {
		try {
			FalseAccount falseAccount=new FalseAccount();
			SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd E HH:mm:ss");
			Date time =new Date();
			String time1=form.format(time);
			falseAccount.setAddress(address);
			falseAccount.setModels(models);
			falseAccount.setName(name);
			falseAccount.setPhone(phone);
			falseAccount.setCreatTime(time1);
			falseAccountDao.saveAccount(falseAccount);
		} catch (Exception e) {
			outPutErrorInfor(FalseAccountManagerImpl.class.getName(), "saveAccount", e);
		}
	
		
	}
	@Override
	public List<FalseAccount> selectAllFalseAccount() {
		List<FalseAccount> list =null;
		 try {
			 return falseAccountDao.selectAllFalseAccount();
		} catch (Exception e) {
			outPutErrorInfor(FalseAccountManagerImpl.class.getName(), "saveAccount", e);
		}
	    return list;
	}
	@Override
	public int deleteById(int id) {
		int num = 0;
	  try {
			return falseAccountDao.deleteById(id);
	} catch (Exception e) {
		outPutErrorInfor(FalseAccountManagerImpl.class.getName(), "deleteById", e);
	}
	return num;
	}

}
