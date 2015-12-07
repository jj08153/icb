package com.icb123.Service;

import java.util.List;
import java.util.Map;

/**
 * 角色管理业务
 * */
public interface RoleManager {
	//增加一个角色信息
	public int addOneRole(String roleName,String comment,String isValid,String code, String creatCode);
	//删除一条角色信息
	public int deleteRoleByCode(String code);
	//拿到需要修改的数据
	public List<Map<String, String>> findByCodeUpdate(String code);
	//保存关联表
	public void add(String roleCode,String menuCode);
	//修改角色信息
	public int updateNameByCode(String RoleName,String roleCode,String comment, String modifiedCode);
	//将角色状态设为0
	public int deleteRole(String isValid,String code);
	//拿到最大code
	public String findMaxCode();

}
