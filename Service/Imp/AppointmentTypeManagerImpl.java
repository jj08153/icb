package com.icb123.Service.Imp;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.icb123.Dao.AppointmentTypeDao;
import com.icb123.Service.AppointmentTypeManager;
import com.icb123.Util.SystemModelExceptionBase;
@Service("appointmentTypeManager")
public class AppointmentTypeManagerImpl extends SystemModelExceptionBase implements AppointmentTypeManager{

	@Resource
	private AppointmentTypeDao appointmentTypeDao;
}
