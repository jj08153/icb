package com.icb123.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.icb123.bean.Department;
import com.icb123.bean.Employee;
import com.icb123.bean.EmployeeRole;
import com.icb123.bean.Position;
import com.icb123.bean.Role;
/**
 * 员工管理业务
 * */
public interface EmployeeManager {
	/**********
	 * 登录
	 * @param icbEmployee
	 * @return
	 */
	public Employee login(Employee icbEmployee);

	public String findEmployeeOpenidByCode(String code);
	
	public String findEmployeeCodeByOpenid(String openid);

	public List<Employee> findEmployeeByPosition(String position);
	
	/*******
	 * 删除用户
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public int deleteEmployeeByCode(String code);

	public int findPsw(Employee icbEmployee);

	public int updatePwd(int id, String psw);

	public Map<String, Object> findEmployee(String code);

	public int findEmployeeName(String nam);

	public int findEmployeeloName(String logN);

	public int countEmployee();

	public List<LinkedHashMap<String, String>> selectAllEmployee(Map<String, Object> map);

	public int updateIcbEmployeeById(String name, String departmentCode,
			String positionCode, String loginName,String password, int id1, String mobile,
			String weixinCode, String modifiedCode);

	public int saveEmployeeweixinCode(String code, String weixinCode);

	public List<Role> findAllRole();

	public List<Position> findPosition();

	public List<Department> findDepartment();

	public void saveEmployeeRole(String code, String roleCode);

	public void saveEmployee(String name, String mobile, String loginName,
			String password, String positionCode, String departmentCode,
			String code, String creatCode, String weixinCode);

	public Employee findEmployeeById(int id1);

	public int updateEmployeeRole(String code, String roleCode);

	public Employee findeByOpenid(String openid);
	
	public List<Employee> findEmpByRoleCode(String reoleCode);

	public int saveWeixinKefu(String loginName, String password, String nickname,String empCode);
	
	public int saveEmployee(Employee emp);

	public List<Employee> findAllEmployee();
	
	public List<EmployeeRole> findRoleByCode(String code);
}
