package com.icb123.Service.Imp;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.icb123.Dao.ConsumptionRecordDao;
import com.icb123.Dao.IntegralProportionDao;
import com.icb123.Dao.RewardDetailDao;
import com.icb123.Service.EmployeeManager;
import com.icb123.Service.IntegralManager;
import com.icb123.Service.WeixinCustomerManager;
import com.icb123.Util.CodeUtil;
import com.icb123.Util.SystemModelExceptionBase;
import com.icb123.bean.ConsumptionRecord;
import com.icb123.bean.IntegralProportion;
import com.icb123.bean.RewardDetail;
import com.icb123.bean.WeixinCustomer;
/*积分使用场景
 * 1.关注
 * 2.自己保养
 * 3.推荐人保养
 * */
@Service("integralManager")
public class IntegralManagerImp extends SystemModelExceptionBase implements IntegralManager{

	@Resource
	private WeixinCustomerManager weixinCustomerManager;
	@Resource
	private EmployeeManager employeeManager;
	@Resource
	private RewardDetailDao rewardDetailDao;
	@Resource
	private ConsumptionRecordDao consumptionRecordDao; 
	@Resource
	private IntegralProportionDao integralProportionDao; 
	
	@Override
	public double inIntegral(String openid, int busnessType) {
		try {
			int roleLevel=getRoleLevel(openid);
			WeixinCustomer wx=weixinCustomerManager.findByOpenid(openid);
			RewardDetail rd=rewardDetailDao.findByBusness(busnessType);
			if(rd.getNum()>0){
				double integral=wx.getIntegral();
				IntegralProportion pro=integralProportionDao.findByRoleAndBusnessAndType(roleLevel,busnessType,1);
				if(pro!=null){
					double add=rd.getNum()*pro.getPropor();
					wx.setIntegral(integral+add);
					weixinCustomerManager.update(wx);
					int flag=integralRecord(wx.getCode(), add, add,rd.getContent(),1);
					if(flag==1){
						return add;
					}else{
						return flag;
					}
	
				}else{
					outPutErrorInfor(IntegralManagerImp.class.getName(), "inIntegral", "未设置角色获取积分比例");
					return -1;
				}
			}else{
				return 1;
			}
		} catch (Exception e) {
			outPutErrorInfor(IntegralManagerImp.class.getName(), "inIntegral", e);
			return -1;
		}
	}

	@Override
	public double maxOutIntegral(String openid, int busnessType,double pay) {
		try {
			int roleLevel=getRoleLevel(openid);
			IntegralProportion pro=integralProportionDao.findByRoleAndBusnessAndType(roleLevel,busnessType,0);		
			return (int)Math.rint(pay*pro.getPropor());
		} catch (Exception e) {
			outPutErrorInfor(IntegralManagerImp.class.getName(), "maxOutIntegral", e);
			return -1;
		}
	}
	
	@Override
	public int outIntegral(String openid, int busnessType,double pay) {
		try {
			int flag=0;
			WeixinCustomer wx=weixinCustomerManager.findByOpenid(openid);
			double integral=wx.getIntegral();
			if(integral>=pay){
				wx.setIntegral(integral-pay);
				weixinCustomerManager.update(wx);
				String reason="";
				if(busnessType==2){
					reason="汽车保养";
				}else{
					reason=busnessType+"";
				}
				flag=integralRecord(wx.getCode(), pay, pay,reason,0);
				return flag;
			}else{
				return -2;
			}
			
		} catch (Exception e) {
			outPutErrorInfor(IntegralManagerImp.class.getName(), "outIntegral", e);
			return -1;
		}
	}
	
	@Override
	public List<ConsumptionRecord> searchIntegralRecord(String wxCode) {
		return consumptionRecordDao.searchByWeixinCode(wxCode);
	}
	
	private int integralRecord(String wxCode,Double num,Double offsetMoney,String reason,int type) {
		try {
			ConsumptionRecord cr=new ConsumptionRecord();
			cr.setCreatTime(new Date());
			cr.setNum(num);
			cr.setOffsetMoney(offsetMoney);
			cr.setReason(reason);
			cr.setType(type);
			cr.setWeixinCode(wxCode);
			cr.setCode(CodeUtil.creatUUIDCode());
			int flag=consumptionRecordDao.save(cr);
			return flag;
		} catch (Exception e) {
			outPutErrorInfor(IntegralManagerImp.class.getName(), "integralRecord", e);
			return -1;
		}
	}

	private int getRoleLevel(String openid){	
		String empCode=employeeManager.findEmployeeCodeByOpenid(openid);
		if(StringUtils.isBlank(empCode)){
			WeixinCustomer wx=weixinCustomerManager.findByOpenid(openid);
			return wx.getVip();
		}else{
			return -1;
		}	
	}
}
