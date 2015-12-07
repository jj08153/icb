package com.icb123.Service;

import java.util.List;
import java.util.Map;

import com.icb123.bean.WorkRecord;

public interface WorkRecordManager {

	public WorkRecord saveWorkRecord(WorkRecord wr);
	public WorkRecord saveWorkRecordInfo(String appCode, String distributeCode, String empCode, String teamCode, String teamRole);
	public List<WorkRecord> findByAppTime(String appTime);
	public List<WorkRecord> findByAppCode(String code);
	public void updateById(WorkRecord wr);
	public void updateByAppCode(String appCode, String serviceEmp, String teamCode,String distributeCode);
}
