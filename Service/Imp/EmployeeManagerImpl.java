package com.icb123.Service.Imp;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.set.CompositeSet.SetMutator;
import org.springframework.stereotype.Service;

import com.icb123.Dao.DepartmentDao;
import com.icb123.Dao.EmployeeDao;
import com.icb123.Dao.EmployeeRoleDao;
import com.icb123.Dao.PositionDao;
import com.icb123.Dao.RoleDao;
import com.icb123.Service.EmployeeManager;
import com.icb123.Util.CodeUtil;
import com.icb123.Util.SystemModelExceptionBase;
import com.icb123.bean.Department;
import com.icb123.bean.Employee;
import com.icb123.bean.EmployeeRole;
import com.icb123.bean.Position;
import com.icb123.bean.Role;
import com.icb123.weixin.WeixinUntil;
@Service("employeeManager")
public class EmployeeManagerImpl extends SystemModelExceptionBase implements EmployeeManager {
	
	@Resource
	public EmployeeDao employeeDao;
	@Resource
	public EmployeeRoleDao employeeRoleDao;
	@Resource
	private DepartmentDao departmentDao;
	@Resource
	private PositionDao positionDao;
	@Resource
	private RoleDao roleDao;
	
	@Override
	public Employee login(Employee icbEmployee) {
		
		return employeeDao.login(icbEmployee);
	}

	@Override
	public void saveEmployee(String name,String mobile,String loginName,String password,String positionCode,String departmentCode,String code, String creatCode,String weixinCode) {
	       Date timeDate=new Date();	
	       Employee icbEmployee =new Employee();
	       icbEmployee.setCreatCode(creatCode);
	       icbEmployee.setCreatTime(timeDate);
	       icbEmployee.setDepartmentCode(departmentCode);
	       icbEmployee.setPositionCode(positionCode);
	       icbEmployee.setName(name);
	       icbEmployee.setPassword(password);
           icbEmployee.setLoginName(loginName);
           icbEmployee.setCode(code);
           icbEmployee.setStatus(1);
           icbEmployee.setMobile(mobile);
           icbEmployee.setWeixinCode(weixinCode);
		   employeeDao.saveEmployee(icbEmployee);	
	}

	@Override
	public String findEmployeeOpenidByCode(String code) {
		return employeeDao.findOpenidByCode(code);
	}

	@Override
	public List<Employee> findEmployeeByPosition(String position) {
		return employeeDao.findByPosition(position);
	}

	@Override
	public List<LinkedHashMap<String, String>> selectAllEmployee(Map map){
		return employeeDao.selectAllEmployee(map);
	}

	@Override
	public int findPsw(Employee employee){
		return employeeDao.findPsw(employee);
	}

	@Override
	public int updatePwd(int id, String psw){
		Employee icbEmployee = new Employee();
		icbEmployee.setId(id);
		icbEmployee.setPassword(psw);
		icbEmployee.setStatus(1);
		return employeeDao.updateIcbEmployeeById(icbEmployee);
	}

	@Override
	public int countEmployee(){
		return employeeDao.countEmployee();
	}

	@Override
	public int deleteEmployeeByCode(String code){
		Employee employee=new Employee();
		employee.setCode(code);
		employee.setStatus(0);
		return employeeDao.updateEmployeeByCode(employee);
	}

	@Override
	public int findEmployeeName(String name){
		return employeeDao.findName(name);
	}

	@Override
	public int findEmployeeloName(String logN){
		return employeeDao.findloName(logN);
	}

	@Override
	public int saveEmployeeweixinCode(String code, String weixinCode){
		Employee employee=new Employee();
		employee.setCode(code);
		employee.setWeixinCode(weixinCode);
		employee.setStatus(1);
		return employeeDao.updateEmployeeByCode(employee);
	}
	@Override
	public int updateIcbEmployeeById(String name,String departmentCode,String positionCode,String loginName,String password,int id,String mobile,String weixinCode, String modifiedCode){
	    Employee employee =new Employee();	
	    Date modifiedTime=new Date();
	    employee.setName(name);
	    employee.setDepartmentCode(departmentCode);
	    employee.setPositionCode(positionCode);
	    employee.setLoginName(loginName);
	    employee.setPassword(password);
	    employee.setId(id);
	    employee.setMobile(mobile);
	    employee.setModifiedTime(modifiedTime);
	    employee.setModifiedCode(modifiedCode);
	    employee.setWeixinCode(weixinCode);
	    employee.setStatus(1);
	    return employeeDao.updateIcbEmployeeById(employee);	
	}

	@Override
	public Map<String, Object> findEmployee(String code) {
		return employeeDao.findEmployee(code);
	}

	@Override
	public List<Role> findAllRole() {
		return roleDao.findAllRole();
	}

	@Override
	public List<Position> findPosition() {
		return positionDao.findPosition();
	}

	@Override
	public List<Department> findDepartment(){
		return departmentDao.findDepartment();
	}

	@Override
	public String findEmployeeCodeByOpenid(String openid) {
		// TODO Auto-generated method stub
		if(employeeDao.findByOpenid(openid)!=null){
			return employeeDao.findByOpenid(openid).getCode();
		}else{
			return null;
		}
		
	}

	@Override
	public void saveEmployeeRole(String code, String roleCode) {
		EmployeeRole employeeRole=new EmployeeRole();
		employeeRole.setRoleCode(roleCode);
		employeeRole.setEmplCode(code);
		employeeRole.setStatus("1");
		employeeRoleDao.saveEmployeeRole(employeeRole);		
	}

	@Override
	public Employee findEmployeeById(int id) {
		// TODO Auto-generated method stub
		return employeeDao.findById(id);
	}

	@Override
	public int updateEmployeeRole(String code, String roleCode) {
		EmployeeRole employeeRole=new EmployeeRole();
		employeeRole.setRoleCode(roleCode);
		employeeRole.setEmplCode(code);
		employeeRole.setStatus("1");
		return employeeRoleDao.updateEmployeeRole(employeeRole);
	}

	@Override
	public Employee findeByOpenid(String openid) {
		// TODO Auto-generated method stub
		return employeeDao.findByOpenid(openid);
	}

	@Override
	public List<Employee> findEmpByRoleCode(String roleCode) {
		return employeeDao.findByRoleCode(roleCode);
	}

	@Override
	public int saveWeixinKefu(String loginName, String password, String nickname,String empCode) {
		int flag=WeixinUntil.creatKefu(loginName, password, nickname);
		if(flag==1){
			Employee emp=new Employee();
			emp.setCode(CodeUtil.creatUUIDCode());
			emp.setName(nickname);
			emp.setLoginName(loginName);
			emp.setPassword(password);
			emp.setStatus(3);
			emp.setCreatTime(new Date());
			emp.setCreatCode(empCode);
			flag=saveEmployee(emp);
			return flag;
		}else{
			return 0;
		}
		
	}

	@Override
	public int saveEmployee(Employee emp) {
		return employeeDao.saveEmployee(emp);
	}

	@Override
	public List<Employee> findAllEmployee() {
		// TODO Auto-generated method stub
		return employeeDao.findAllEmployee();
	}
	
	@Override
	public List<EmployeeRole> findRoleByCode(String code) {
		 try {
			return employeeRoleDao.findRoleByCode(code);
		} catch (Exception e) {
			outPutErrorInfor(EmployeeManagerImpl.class.getName(), "findRoleByCode", e);
			return null;
		}
	}
}
