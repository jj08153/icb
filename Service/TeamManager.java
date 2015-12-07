package com.icb123.Service;

import java.util.List;

import com.icb123.bean.Team;

/**
 * 服务队管理
 * */
public interface TeamManager {
	/**
	 * 获得所有有效车队
	 * */
	public List<Team> findAll();
}
