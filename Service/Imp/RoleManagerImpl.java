package com.icb123.Service.Imp;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.icb123.Dao.RoleDao;
import com.icb123.Service.RoleManager;
import com.icb123.Util.SystemModelExceptionBase;
import com.icb123.bean.Role;
@Service("roleManager")
public class RoleManagerImpl extends SystemModelExceptionBase implements RoleManager{
	@Resource
	private RoleDao roleDao;
	
	//保存角色信息
	@Override
	public int addOneRole(String roleName, String comment, String isValid,
			String code, String creatCode) {
			Role role=new Role();
		   role.setCode(code);
		   role.setComment(comment);
		   role.setIsValid(isValid);
		   role.setRoleName(roleName);
		   role.setCreatCode(creatCode);
		   role.setCreatTime(new Date());
		return roleDao.addOneRole(role);
	}
	
	//拿到最需要修改的角色信息
	@Override
	public List<Map<String, String>> findByCodeUpdate(String code) {
		return roleDao.findByCodeUpdate(code);
	}
	
	//删除关联表信息
	@Override
	public int deleteRoleByCode(String code) {
		return roleDao.deleteRoleByCode(code);
	}
	
	//将信息保存到关联表
	@Override
	public void add(String roleCode, String menuCode) {
		roleDao.add(roleCode, menuCode);	
	}
	
	//修改角色信息
	@Override
	public int updateNameByCode(String RoleName, String roleCode,String comment, String modifiedCode) {
		Role role =new Role();
		role.setCode(roleCode);
		role.setComment(comment);
		role.setRoleName(RoleName);
		role.setModifiedCode(modifiedCode);
		role.setModifiedTime(new Date());
		return roleDao.updateNameByCode(role);
	}
	
	//将角色状态设为0
	@Override
	public int deleteRole(String isValid, String code) {
		return roleDao.deleteRole(isValid, code);
	}
	
	//拿到最大code
	@Override
	public String findMaxCode() {
		return roleDao.findMaxCode();
	}

}
