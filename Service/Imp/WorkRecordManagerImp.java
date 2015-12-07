package com.icb123.Service.Imp;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.icb123.Dao.WorkRecordDao;
import com.icb123.Service.EmployeeManager;
import com.icb123.Service.WorkRecordManager;
import com.icb123.Util.CodeUtil;
import com.icb123.Util.SystemModelExceptionBase;
import com.icb123.bean.WorkRecord;
@Service("workRecordManager")
public class WorkRecordManagerImp extends SystemModelExceptionBase implements WorkRecordManager{

	@Resource
	private WorkRecordDao workRecordDao;
	@Resource
	private EmployeeManager employeeManager;
	
	@Override
	public WorkRecord saveWorkRecord(WorkRecord wr) {
		return workRecordDao.save(wr);
	}
	@Override
	public WorkRecord saveWorkRecordInfo(String appCode, String distributeCode, String empCode, String teamCode, String teamRole) {		
		try {
			WorkRecord wr = new WorkRecord();
			wr.setAppCode(appCode);
			wr.setDistributeCode(distributeCode);
			wr.setDistributeTime(new Date());
			wr.setEmpCode(empCode);
			wr.setTeamCode(teamCode);
			wr.setTeamRole(teamRole);		
			wr.setStatus("1");
			wr.setScore(-1);
			return wr;
		} catch (Exception e) {
			outPutErrorInfor(WorkRecordManagerImp.class.getName(), "saveWorkRecordInfo", e);
			return null;
		}	
	}
	@Override
	public List<WorkRecord> findByAppTime(String appTime) {
		// TODO Auto-generated method stub
		return workRecordDao.findByAppTime(appTime);
	}
	@Override
	public List<WorkRecord> findByAppCode(String code) {
		// TODO Auto-generated method stub
		return workRecordDao.findByAppCode(code);
	}
	@Override
	public void updateById(WorkRecord wr) {
		workRecordDao.updateById(wr);
		
	}
	@Override
	public void updateByAppCode(String appCode, String serviceEmp,String teamCode,String distributeCode) {
		String[] serviceEmpStr= serviceEmp.split("_");
		String empCode=serviceEmpStr[0];
		String teamRole=serviceEmpStr[1];
		List<WorkRecord> list = findByAppCode(appCode);
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i).getTeamRole().equals(teamRole) && !"0".equals(empCode)){//删除原记录增加一条新纪录
				if(!list.get(i).getEmpCode().equals(empCode)){
					WorkRecord wrOld =list.get(i);
					wrOld.setStatus("0");
					workRecordDao.updateById(wrOld);//删除原记录
					WorkRecord wrNew = new WorkRecord();
					wrNew.setAppCode(appCode);
					wrNew.setTeamRole(teamRole);
					wrNew.setEmpCode(empCode);
					wrNew.setTeamCode(teamCode);
					wrNew.setStatus("1");
					wrNew.setDistributeTime(new Date());
					wrNew.setDistributeCode(empCode);
					wrNew.setCode(CodeUtil.creatUUIDCode());
					workRecordDao.save(wrNew);
					break;
				}else{
					WorkRecord wrOld =list.get(i);
					wrOld.setTeamCode(teamCode);
					workRecordDao.updateById(wrOld);//只更新汽车信息
					break;
				}
			}else if(i==list.size()-1 && !list.get(i).getTeamRole().equals(teamRole)){//增加这条信息
				WorkRecord wr = new WorkRecord();
				wr.setAppCode(appCode);
				wr.setTeamRole(teamRole);
				if("0".equals(empCode)){
					return;
				}else{
					wr.setEmpCode(empCode);
				}
				wr.setTeamCode(teamCode);
				wr.setStatus("1");
				wr.setDistributeTime(new Date());
				wr.setDistributeCode(list.get(0).getDistributeCode());
				wr.setCode(CodeUtil.creatUUIDCode());
				workRecordDao.save(wr);
				break;
				/*修改*/
			}else if("0".equals(empCode)&&list.get(i).getTeamRole().equals("学徒")){//删除学徒信息
				WorkRecord wr =list.get(i);
				wr.setStatus("0");
				wr.setTeamCode(teamCode);
				workRecordDao.updateById(wr);
				break;
			}
		}
	}
}
