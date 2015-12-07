package com.icb123.Service.Imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.icb123.Dao.TeamDao;
import com.icb123.Dao.TeamMambersDao;
import com.icb123.Service.TeamManager;
import com.icb123.Util.SystemModelExceptionBase;
import com.icb123.bean.Team;
@Service("teamManager")
public class TeamManagerImpl extends SystemModelExceptionBase implements TeamManager {

	@Autowired
	private TeamDao teamDao;
	@Autowired
	private TeamMambersDao teamMambersDao;
	
	@Override
	public List<Team> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
