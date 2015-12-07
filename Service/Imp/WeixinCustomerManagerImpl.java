package com.icb123.Service.Imp;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.icb123.Common.WeixinCons;
import com.icb123.Dao.ConsumptionRecordDao;
import com.icb123.Dao.CustomerDao;
import com.icb123.Dao.FinishConditionsDao;
import com.icb123.Dao.SystemParamDao;
import com.icb123.Dao.VipConditionsDao;
import com.icb123.Dao.WeixinAcceptRecordDao;
import com.icb123.Dao.WeixinCustomerDao;
import com.icb123.Service.CustomerManager;
import com.icb123.Service.EmployeeManager;
import com.icb123.Service.IntegralManager;
import com.icb123.Service.SystemParamManager;
import com.icb123.Service.WeixinCustomerManager;
import com.icb123.Util.CodeUtil;
import com.icb123.Util.FormatForI18N;
import com.icb123.Util.PropertiesUtils;
import com.icb123.Util.SystemModelExceptionBase;
import com.icb123.bean.ConsumptionRecord;
import com.icb123.bean.Customer;
import com.icb123.bean.Employee;
import com.icb123.bean.FinishConditions;
import com.icb123.bean.VipConditions;
import com.icb123.bean.WeixinAcceptRecord;
import com.icb123.bean.WeixinCustomer;
import com.icb123.weixin.WeixinUntil;
import com.icb123.weixin.Manager.CustomerBusinessManager;
@Service("weixinCustomerManager")
public class WeixinCustomerManagerImpl extends SystemModelExceptionBase implements WeixinCustomerManager {

	@Autowired
	private WeixinCustomerDao weixinCustomerDao;
	@Autowired
	private VipConditionsDao vipConditionsDao;
	@Autowired
	private FinishConditionsDao finishConditionsDao;
	@Autowired
	private CustomerManager customerManager;
	@Autowired
	private IntegralManager integralManager;
	@Autowired
	private ConsumptionRecordDao consumptionRecordDao;
	@Autowired
	private EmployeeManager employeeManager;
	@Autowired
	private WeixinAcceptRecordDao weixinAcceptRecordDao;
	@Resource
	private CustomerBusinessManager customerBusinessManager;
	
	@Override
	public void save(WeixinCustomer cus){
		weixinCustomerDao.save(cus);
	}

	@Override
	public int update(WeixinCustomer cus){
		return weixinCustomerDao.update(cus);
	}

	@Override
	public void updateStatusByOpenid(String status,String openid){
		weixinCustomerDao.updateStatusByOpenid(openid,status);
	}

	@Override
	public WeixinCustomer findByOpenid(String Openid){	
		return weixinCustomerDao.findByOpenid(Openid);
	}

	@Override
	public Integer findInviNumByOpenid(String openid){
		WeixinCustomer wc=findByOpenid(openid);
		if(wc!=null){
			return wc.getInviNum();
		}else
			return null;
	}

	@Override
	public void updateInviNum(int num, String openid){
		weixinCustomerDao.updateInviNum(num, openid);
	}

	@Override
	public void delete(String openid){
		weixinCustomerDao.updateStatusByOpenid(openid,"0");
	}

	@Override
	public String creatEwmByWeixinCustomer(String openid) {
		int sceneID = 0;
		try {
			WeixinCustomer wx=findByOpenid(openid);
			if(StringUtils.isBlank(wx.getEwmPath())){
				sceneID = Integer.valueOf("1"+wx.getCode()).intValue()-10000000+100;
				String path=WeixinUntil.getEwmFromWechatServer(2, sceneID);
				wx.setEwmPath(path);
				update(wx);
				return path;
			}else{
				return wx.getEwmPath();
			}		
		} catch (Exception e) {
			outPutErrorInfor(WeixinCustomerManagerImpl.class.getName(), "creatEwmByWeixinCustomer", e);
		}
		return null;
	}

	@Override
	public WeixinCustomer findByCode(String inviCode) {
		return weixinCustomerDao.findByCode(inviCode);
	}

	@Override
	public String findCodeByOpenid(String openid){
		WeixinCustomer wc=findByOpenid(openid);
		if(wc!=null){
			return wc.getCode();
		}else
			return null;
	}

	@Override
	public String findOpenidByCode(String code) {
		WeixinCustomer wc=findByCode(code);
		if(wc!=null){
			return wc.getOpenid();
		}else
			return null;
	}

	@Override
	public WeixinCustomer findByName(String nickName) {
		return weixinCustomerDao.findByName(nickName);
	}

	@Override
	public boolean isNewWeixinCustomer(String openid) {
		boolean isNew=true;
		WeixinCustomer customer=findByOpenid(openid);
		if(customer!=null){
			isNew=false;
		}
		return isNew;
	}

	@Override
	public void writeFinishVipCondition(String weixinCode, String conditionCode) {
		try {
			VipConditions vc=vipConditionsDao.findByCode(conditionCode);
			FinishConditions fc=new FinishConditions();
			fc.setWeixinCode(weixinCode);
			fc.setConditionCode(conditionCode);
			fc.setCode(CodeUtil.creatUUIDCode());
			fc.setVip(vc.getVip());
			fc.setCreatTime(new Date());
			finishConditionsDao.save(fc);
		} catch (Exception e) {
			outPutErrorInfor(WeixinCustomerManagerImpl.class.getName(), "writeFinishVipCondition", e);
		}
	}

	@Override
	public void upVip(String openid) {
		try {
			WeixinCustomer wx=findByOpenid(openid);
			List<VipConditions> l1=vipConditionsDao.findByVip(wx.getVip()+1);
			List<FinishConditions> l2=finishConditionsDao.findByWeixinCode(wx.getCode(),wx.getVip()+1);
			if(l1.size()==l2.size()){
				wx.setVip(wx.getVip()+1);
				update(wx);
			}
		} catch (Exception e) {
			outPutErrorInfor(WeixinCustomerManagerImpl.class.getName(), "upVip", e);
		}	
	}

	@Override
	public Map<String, String> saveCustomerInfo(String openid, String name,
			String mobile) {
		Map<String, String> map=new HashMap<String, String>();
		try {
			Customer cus=customerManager.findCustomerByOpenid(openid);
			if(cus==null){
				WeixinCustomer wx=findByOpenid(openid);
				cus=customerManager.findCustomerByMobile(mobile);
				if(cus==null){		
					cus=new Customer();
					cus.setCode(CodeUtil.creatUUIDCode());
					cus.setMobile(mobile);
					cus.setName(name);
					cus.setCreatTime(new Date());
					cus.setStatus("1");
					cus.setWeixinCode(wx.getCode());
					customerManager.saveCustomer(cus);
					integralManager.inIntegral(openid, 4);
				}else{
					cus.setName(name);
					cus.setStatus("1");
					cus.setWeixinCode(wx.getCode());
					cus.setModifiedTime(new Date());
					customerManager.updateCustomer(cus);
				}
				
			}else{
				cus.setName(name);
				cus.setMobile(mobile);
				customerManager.updateCustomer(cus);
			}
			map.put("flag", "1");
		} catch (Exception e) {
			outPutErrorInfor(WeixinCustomerManagerImpl.class.getName(), "saveCustomerInfo", e);
			map.put("flag", "0");
		}
		return map;
	}

	@Override
	public List<Map<String, String>> findConsumptionRecordByOpenid(String openid) {
		 List<Map<String, String>> result=new ArrayList<Map<String,String>>();
		WeixinCustomer wx=findByOpenid(openid);
		List<ConsumptionRecord> list=consumptionRecordDao.searchByWeixinCode(wx.getCode());
		Map<String, String> map=null;
		for(ConsumptionRecord cr:list){
			map=new HashMap<String, String>();
			map.put("time", FormatForI18N.getFormatDate(cr.getCreatTime(), "yyyy-MM-dd HH:mm:ss"));
			map.put("reason", cr.getReason());
			map.put("num", cr.getNum()+"");
			if(cr.getType().intValue()==1){
				map.put("type", "+");
			}else{
				map.put("type", "-");
			}
			result.add(map);
		}
		return result;
	}

	@Override
	public Map<String, Object> findCustomerInfo(String openid) {	
		upVip(openid);
		WeixinCustomer wx=findByOpenid(openid);	
		Map<String, Object> map=new HashMap<String, Object>();
		Employee emp= employeeManager.findeByOpenid(openid);
		wx.setNickName(CodeUtil.getFromBase64(wx.getNickName()));
		map.put("wx", wx);
		if(emp==null){//客户
			map.put("type", "0");
		}else if("jishi".equals(emp.getPositionCode())||"xuetu".equals(emp.getPositionCode())){//技师
			map.put("type", "1");
		}else{//排班
			//List<String> paibanList=systemParamManager.findValueListByName("paiban");
			List<Employee> paibanList=employeeManager.findEmpByRoleCode(PropertiesUtils.getValueByKey("role_paigong"));
			for(Employee paiban:paibanList){
				if((paiban.getCode()).equals(emp.getCode())){
					map.put("type", "2");
				}
			}
		}	
		return map;
	}

	@Override
	public FinishConditions findFinishVipCondition(String weixinCode,
			String conditionCode) {
		return finishConditionsDao.findFinishVipCondition(weixinCode, conditionCode);
	}

	@Override
	public int saveAcceptRecorde(WeixinAcceptRecord record) {
		return weixinAcceptRecordDao.save(record);
	}

	@Override
	public WeixinAcceptRecord saveAcceptRecordeInfo(String weixinCode,
			String accept) {
		WeixinAcceptRecord record=new WeixinAcceptRecord();
		record.setAccept(accept);
		record.setAcceptTime(new Date());
		record.setWeixinCode(weixinCode);
		return record;
	}

	@Override
	public List<WeixinAcceptRecord> findAcceptRecordByWeixinCode(
			String weixinCode) {
		return weixinAcceptRecordDao.findByWeixinCode(weixinCode);
	}

	@Override
	public List<Map<String, String>> creatGiftButton() {
		List<Map<String, String>> list=new ArrayList<Map<String,String>>();
		Map<String, String> map=null;
		try {
			String giftStr=PropertiesUtils.getValueByKey("gift");
			String[] giftArr=giftStr.split(",");
			for(int i=0;i<giftArr.length;i++){
				map=new HashMap<String, String>();
				System.out.println(giftArr[i]);
				map.put("name", giftArr[i]);
				list.add(map);
			}
		} catch (Exception e) {
			outPutErrorInfor(WeixinCustomerManagerImpl.class.getName(), "creatGiftButton", e);
		}
		return list;
	}

	@Override
	public List<WeixinAcceptRecord> acceptGift(String weixinCode, String accept) {
		try {
			WeixinAcceptRecord record=saveAcceptRecordeInfo(weixinCode, accept);
			int flag=saveAcceptRecorde(record);
			if(flag!=0){
				return findAcceptRecordByWeixinCode(weixinCode);
			}else{
				return null;
			}
		} catch (Exception e) {
			outPutErrorInfor(WeixinCustomerManagerImpl.class.getName(), "acceptGift", e);
			return null;
		}
	}

	@Override
	public Map<String, Object> openGiftView(String weixinCode) {
		Map<String, Object> result=new HashMap<String, Object>();
		try {
			result.put("gift", creatGiftButton());
			result.put("record", findAcceptRecordByWeixinCode(weixinCode));
		} catch (Exception e) {
			outPutErrorInfor(WeixinCustomerManagerImpl.class.getName(), "openGiftView", e);
		}
		return result;
	}

	@Override
	public int setVip(String code) {
		WeixinCustomer wx=findByCode(code);
		if(wx!=null){
			wx.setVip(wx.getVip()+1);
			return update(wx);
		}else{
			return 2;
		}
	}
}
