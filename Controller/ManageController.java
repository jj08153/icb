package com.icb123.Controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.icb123.Common.Constants;
import com.icb123.Common.WeixinCons;
import com.icb123.Service.AccessoriesInfoManager;
import com.icb123.Service.AccessoriesTypeManager;
import com.icb123.Service.CarInfoManager;
import com.icb123.Service.CustomerManager;
import com.icb123.Service.EmployeeManager;
import com.icb123.Service.FalseAccountManager;
import com.icb123.Service.IntegralManager;
import com.icb123.Service.MenumManager;
import com.icb123.Service.RoleManager;
import com.icb123.Service.ServiceManager;
import com.icb123.Service.SystemParamManager;
import com.icb123.Service.WeixinCustomerManager;
import com.icb123.Util.OutputUtil;
import com.icb123.Util.Page;
import com.icb123.Util.SystemModelExceptionBase;
import com.icb123.Util.SystemStaticArgsSet;
import com.icb123.bean.AccessoriesInfo;
import com.icb123.bean.AccessoriesStorage;
import com.icb123.bean.AccessoriesType;
import com.icb123.bean.CarInfo;
import com.icb123.bean.Customer;
import com.icb123.bean.CustomerCar;
import com.icb123.bean.Department;
import com.icb123.bean.Employee;
import com.icb123.bean.EmployeeRole;
import com.icb123.bean.FalseAccount;
import com.icb123.bean.Menu;
import com.icb123.bean.Position;
import com.icb123.bean.Role;
import com.icb123.bean.Service;
import com.icb123.bean.ServiceTime;
import com.icb123.bean.WeixinCustomer;
import com.icb123.weixin.WeixinUntil;
import com.icb123.weixin.Manager.MenuManager;

/**
 * 后台管理
 * */
@Controller
@RequestMapping("/manage")
public class ManageController extends SystemModelExceptionBase{

	@Resource
	private CarInfoManager carInfoManager;
	@Resource
	private ServiceManager serviceManager;
	@Resource
	private AccessoriesInfoManager accessoriesInfoManager;
	@Resource
	private EmployeeManager employeeManager;
	@Resource
	public SystemParamManager systemParamManager;	
	@Resource
	private CustomerManager customerManager;	
	@Resource
	private MenumManager menuManager;	
	@Resource
	private WeixinCustomerManager weixinCustomerManager;
	@Resource
	private RoleManager roleManager;
	@Resource
	private FalseAccountManager falseAccountManager;
	@Resource
	private MenuManager wmenuManager;
	@Resource
	private AccessoriesTypeManager accessoriesTypeManager;

	@RequestMapping(value="/forAjax")
	public void forAjax(HttpServletRequest request, HttpServletResponse response){
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			Constants.root=request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+request.getContextPath();
			String sysRootPath = request.getSession().getServletContext().getRealPath("\\");
			SystemStaticArgsSet.setSysRootPath(sysRootPath);
			String requestType=request.getParameter("requestType") == null ? "": request.getParameter("requestType");
			Employee employee=(Employee) request.getSession().getAttribute("Employee");
			if("car1".equals(requestType)){//获取指定车辆类别信息
				String parentCode=request.getParameter("carCode") == null ? "": request.getParameter("carCode");
				List<CarInfo> list=carInfoManager.findListByParentCode(parentCode);
				OutputUtil.outPutJsonArrary(response, list);
			}else if("time1".equals(requestType)){//获得所有服务时间
				List<ServiceTime> list=serviceManager.findAllServiceTime();
				OutputUtil.outPutJsonArrary(response, list);
			}else if("service1".equals(requestType)){//获得指定服务的下级服务内容
				String parentCode=request.getParameter("code") == null ? "": request.getParameter("code");
				List<Service> list=serviceManager.findServiceByParentCode(parentCode);
				OutputUtil.outPutJsonArrary(response, list);
			}else if("access1".equals(requestType)){//根据车型与服务内容获得配套配件
				String serviceCode=request.getParameter("serviceCode") == null ? "": request.getParameter("serviceCode");
				String carCode=request.getParameter("carCode") == null ? "": request.getParameter("carCode");
				List<AccessoriesInfo> list= accessoriesInfoManager.findServiceMatchAccessories(serviceCode,carCode);
				OutputUtil.outPutJsonArrary(response, list);
			}else if("access2".equals(requestType)){//根据配件类型获取所有配件
				String type=request.getParameter("type") == null ? "": request.getParameter("type");
				List<AccessoriesInfo> list =accessoriesInfoManager.findAccessoriesByType(type);
				OutputUtil.outPutJsonArrary(response, list);
			}else if("wxMenu".equals(requestType)){//微信菜单生成
				String accessToken=WeixinUntil.getAccessToken();
				int flag=wmenuManager.creatMenu(accessToken);
				Map<String, String> map=new HashMap<String, String>();
				map.put("flag", flag+"");
				OutputUtil.outPutJsonObject(response, map);
			}else if("100".equals(requestType)){//添加微信客服
				String loginName=request.getParameter("loginName") == null ? "": request.getParameter("loginName");
				String password=request.getParameter("password") == null ? "": request.getParameter("password");
				String nickname=request.getParameter("nickname") == null ? "": request.getParameter("nickname");
				int flag=employeeManager.saveWeixinKefu(loginName,password,nickname,employee.getCode());
				Map<String, String> map=new HashMap<String, String>();
				map.put("flag", flag+"");
				OutputUtil.outPutJsonObject(response, map);
			}else if("1".equals(requestType)){//判断修改密码时旧密码是否正确
				String psw1=request.getParameter("psw") == null ? "": request.getParameter("psw");
				String id1=request.getParameter("id") == null ? "": request.getParameter("id");
				int i=Integer.valueOf(id1).intValue();
				Employee icbEmployee = new Employee();
				Map<String, String> result=new HashMap<String, String>();
				icbEmployee.setId(i);
				icbEmployee.setPassword(psw1);
				int num=employeeManager.findPsw(icbEmployee);	
				if(num == 1){
					result.put("msg", "1");
					OutputUtil.outPutJsonObject(response, result);
				}
				else{
					result.put("msg", "0");
					OutputUtil.outPutJsonObject(response, result);
				}				
			}else if("2".equals(requestType)){//根据员工id修改员工登录密码
				String psw1=request.getParameter("psw") == null ? "": request.getParameter("psw");
				String id1=request.getParameter("id") == null ? "": request.getParameter("id");
				int id=Integer.valueOf(id1).intValue();
				int num=employeeManager.updatePwd(id,psw1);
				Map<String, String> result=new HashMap<String, String>();
				if(num != 0){
					result.put("msg", "1");
					OutputUtil.outPutJsonObject(response, result);
				 }else{					
					 result.put("msg", "0");
					OutputUtil.outPutJsonObject(response, result);
				 }
			}else if("3".equals(requestType)){//判断session是否为空
				String num="";
			    if(null!=request.getSession()){
			    Employee user = (Employee)request.getSession().getAttribute("Employee");
			   if(null!=user && !"".equals(user)){
				   num="1";
				   response.getWriter().write(num);   
			   }else{
				   num="2";
				   response.getWriter().write(num);
			     }
			    }	 
		   }else if("4".equals(requestType)){//根据id查到需要修改的用户信息
			   String code=request.getParameter("code") == null ? "": request.getParameter("code");
				 Map<String,Object>map=employeeManager.findEmployee(code);
		    	 OutputUtil.outPutJsonObject(response, map);		
		   }else if("5".equals(requestType)){//判断用户名是否已存在				
			 String nam=request.getParameter("name") == null ? "": request.getParameter("name");
				int num=employeeManager.findEmployeeName(nam);
				Map<String, String> result=new HashMap<String, String>();
				if( num >= 1){
					result.put("msg", "1");
					OutputUtil.outPutJsonObject(response, result);
				}else{
					result.put("msg", "0");
					OutputUtil.outPutJsonObject(response, result);
				}
			}else if("6".equals(requestType)){//判断登录名是否已存在
				String logN=request.getParameter("loginName") == null ? "": request.getParameter("loginName");
				 int num=employeeManager.findEmployeeloName(logN);
				 Map<String, String> result=new HashMap<String, String>();
				if( num >= 1){
					result.put("msg", "1");
					OutputUtil.outPutJsonObject(response, result);
				}else{
					result.put("msg", "0");
					OutputUtil.outPutJsonObject(response, result);
				}			
			}else if("9".equals(requestType)){//查询所有角色
				 List <Role> listRole =	 employeeManager.findAllRole();  
		    	 OutputUtil.outPutJsonArrary(response, listRole);	 
			}else if("10".equals(requestType)){//查询所有职位
				List<Position> listposition = employeeManager.findPosition();
				 OutputUtil.outPutJsonArrary(response, listposition);	 
			}else if("11".equals(requestType)){//查询所有部门
				 List<Department>  listDepartment =  employeeManager.findDepartment();
				 OutputUtil.outPutJsonArrary(response, listDepartment);
			}else if("12".equals(requestType)){//根据昵称查询微信Code
				String nickName=request.getParameter("nickName") == null ? "": request.getParameter("nickName");
				WeixinCustomer weixinCustomer=weixinCustomerManager.findByName(nickName);
				Map<String, String> result=new HashMap<String, String>();
		    	if(weixinCustomer == null){
		    		result.put("msg", "0");
		    		OutputUtil.outPutJsonObject(response, result);
		    	   
		    	}else{
		    		String weixinCode =weixinCustomer.getCode();
		    		String code= (String) request.getSession().getAttribute("code");
		    		int num=employeeManager.saveEmployeeweixinCode(code,weixinCode);
		    		if(num == 1)
		    		 { 
		    			result.put("msg", "1");
		    			OutputUtil.outPutJsonObject(response, result);
		    		 }	
			    }		
			}else if("13".equals(requestType)){
				  String code=request.getParameter("weixinCode") == null ? "": request.getParameter("weixinCode");
				  WeixinCustomer weixinCustomer= weixinCustomerManager.findByCode(code);
				  Map<String, String> result=new HashMap<String, String>();
				  if(weixinCustomer == null){
					     result.put("msg", "0");
			    		OutputUtil.outPutJsonObject(response, result);  
				  }else{
					  result.put("msg", "1");
			    		OutputUtil.outPutJsonObject(response, result);    
				  }
			}else if("14".equals(requestType)){//添加车辆
				 String name=request.getParameter("name") == null ? "": request.getParameter("name");
				 String orderCol=request.getParameter("orderCol") == null ? "": request.getParameter("orderCol");
				 String parentCode=request.getParameter("parentCode") == null ? "": request.getParameter("parentCode");
				 Map<String, String> result=new HashMap<String, String>();
				 String maxCode=carInfoManager.findMaxCode(parentCode);
				 String code;
				 if(maxCode==null || maxCode.trim().length() == 0){
					 maxCode="";
					 code= carInfoManager.saveCar(name,parentCode,orderCol,maxCode);
				 }else{
					 code= carInfoManager.saveCar(name,parentCode,orderCol,maxCode);
				 }
				 if(StringUtils.isNotBlank(code)){
					 result.put("msg", "1");
					 result.put("code", code);
					 OutputUtil.outPutJsonObject(response, result);    
				 }else{
					 result.put("msg", "0");
					 OutputUtil.outPutJsonObject(response, result);   
				 }	    
			 }else if("15".equals(requestType)){//找到需要修改的车辆
				String code=request.getParameter("code") == null ? "": request.getParameter("code");
				CarInfo listCarInfo = carInfoManager.findByCode(code);
				if(listCarInfo != null){ 
					 OutputUtil.outPutJsonObject(response, listCarInfo);
				}
			 }else if("16".equals(requestType)){//修改车辆
		    	String code=request.getParameter("code") == null ? "": request.getParameter("code");
		    	String name=request.getParameter("name") == null ? "": request.getParameter("name");
		    	String orderCol=request.getParameter("orderCol") == null ? "": request.getParameter("orderCol");
		    	int number=carInfoManager.updateByCode(name, code,orderCol);
		    	Map<String, String> result=new HashMap<String, String>();
		    	if(number == 1){
		    		 result.put("msg", "1");
		    		 result.put("code", code);
		    		 OutputUtil.outPutJsonObject(response, result);
		    	}
			 }else if("17".equals(requestType)){//判断车辆名字是否冲突
		    	String name=request.getParameter("name") == null ? "": request.getParameter("name");
		    	String parentCode=request.getParameter("parentCode") == null ? "": request.getParameter("parentCode");
		    	Map<String, String> result=new HashMap<String, String>();
		    	int number=carInfoManager.findName(name,parentCode);
		    	if(number>=1){
		    		 result.put("msg", "1");
		    		 OutputUtil.outPutJsonObject(response, result);
		    	}
			 }else if("18".equals(requestType)){//删除车辆
		    	String code=request.getParameter("code") == null ? "": request.getParameter("code");
		    	Map<String, String> result=new HashMap<String, String>();
		    	int num=carInfoManager.deleteByCode(code);
		    	if(num == 1){
		    		result.put("msg", "1");
		    		OutputUtil.outPutJsonObject(response, result);
		    	}	
			 }else if("19".equals(requestType)){//拿到最新的Car
		    	String parentCode =request.getParameter("parentCode") == null ? "":request.getParameter("parentCode");
		    	List<CarInfo> list=carInfoManager.findListByParentCode(parentCode);
		    	OutputUtil.outPutJsonArrary(response, list);
			}else if("20".equals(requestType)){//拿到最新修改的信息 2015/11/11 王小伟
		    	String parentCode =request.getParameter("parentCode") == null ? "":request.getParameter("parentCode");
		    	List<CarInfo> list=carInfoManager.findListByParentCode(parentCode);
		    	OutputUtil.outPutJsonArrary(response, list);
			}else if("21".equals(requestType)){
			   String accCode =request.getParameter("name") == null ? "":request.getParameter("name");
		       String num =request.getParameter("num") == null ? "":request.getParameter("num");
		       String ioType =request.getParameter("ioType") == null ? "":request.getParameter("ioType");
		       String carCode =request.getParameter("carCode") == null ? "":request.getParameter("carCode");
		       String beizhu =request.getParameter("beizhu") == null ? "":request.getParameter("beizhu");
		       String price =request.getParameter("price") == null ? "":request.getParameter("price");
		       String empCode =request.getParameter("empCode") == null ? "":request.getParameter("empCode");
		       String moneyType =request.getParameter("moneyType") == null ? "":request.getParameter("moneyType");
		       Map<String, String> result =accessoriesInfoManager.accInAndOutStorage(accCode,num,price,ioType,beizhu,empCode,employee.getCode(),carCode,moneyType);
		       OutputUtil.outPutJsonObject(response, result);
			}else if("22".equals(requestType)){//添加修改库存配件信息
			     String creatCode=employee.getCode();
				 String id =request.getParameter("id") == null ? "":request.getParameter("id");  
				 String newBrand =request.getParameter("newBrand") == null ? "":request.getParameter("newBrand"); 
				 String newManufacturer =request.getParameter("newManufacturer") == null ? "":request.getParameter("newManufacturer"); 
				 String typeCode =request.getParameter("typeCode") == null ? "":request.getParameter("typeCode"); 
				 String unit =request.getParameter("unit") == null ? "":request.getParameter("unit"); 
				 String newModel =request.getParameter("newModel") == null ? "":request.getParameter("newModel"); 
				 Map<String, String> result=accessoriesInfoManager.saveOrUpdateAccessoriesStorage(id,newBrand, newManufacturer, typeCode, unit,  newModel, creatCode);
				 OutputUtil.outPutJsonObject(response, result);
			}else if("23".equals(requestType)){//根据id查到配件信息 2015/11/11 王小伟
			   String id1=request.getParameter("id") == null ? "":request.getParameter("id");
			   int id=Integer.valueOf(id1).intValue();
			   AccessoriesStorage accessoriesStorage=accessoriesInfoManager.findAccessoriesStorageById(id);
			   if(accessoriesStorage != null){
				   OutputUtil.outPutJsonObject(response, accessoriesStorage);
			   } 
			}else if("24".equals(requestType)){//保存虚假客户 2015/11/11 王小伟
				String name=request.getParameter("name") == null ? "":request.getParameter("name");  
				String address=request.getParameter("address") == null ? "":request.getParameter("address");
				String phone=request.getParameter("phone") == null ? "":request.getParameter("phone");
				String models=request.getParameter("models") == null ? "":request.getParameter("models");
				falseAccountManager.saveAccount(name, address, models, phone);
				Map<String, String> result=new HashMap<String, String>();
				result.put("msg", "1");
				OutputUtil.outPutJsonObject(response, result);
			}else if("25".equals(requestType)){//删除虚假客户 2015/11/11 王小伟
				String id1=request.getParameter("id") == null ? "":request.getParameter("id");  
				int id=Integer.valueOf(id1).intValue(); 
				Map<String, String> result=new HashMap<String, String>();
				int number = falseAccountManager.deleteById(id);
				if(number == 1){
					result.put("msg", "1");
					OutputUtil.outPutJsonObject(response, result);
				}
			}else if("26".equals(requestType)){//拿到该角色的权限 2015/11/11 王小伟
				String code=request.getParameter("code") == null ? "":request.getParameter("code");  
				List<Menu> list1=menuManager.findMenu(code);
				OutputUtil.outPutJsonArrary(response, list1);
			}else if("27".equals(requestType)){//拿到该角色信息
				String code = request.getParameter("code") == null ? "":request.getParameter("code");  
				List<Map<String, String>> list = roleManager.findByCodeUpdate(code);
				OutputUtil.outPutJsonArrary(response, list);
			}else if("28".equals(requestType)){//查询所有权限 2015/11/11 王小伟
			    List<Menu> list = menuManager.selectAllMenu();
			    if(list.size()>0){
			    	OutputUtil.outPutJsonArrary(response, list);
			    }
			}else if("29".equals(requestType)){//修改角色comment 2015/11/11 王小伟
		        String comment = request.getParameter("comment") == null ? "":request.getParameter("comment");  
			    String RoleName = request.getParameter("name") == null ? "":request.getParameter("name");  
			    String chestr = request.getParameter("chestr") == null ? "":request.getParameter("chestr");  
			    String roleCode = request.getParameter("code") == null ? "":request.getParameter("code"); 
			    int number =  roleManager.updateNameByCode(RoleName, roleCode,comment,employee.getCode());
			    int num=roleManager.deleteRoleByCode(roleCode);
			    if(num>0){
				    String[] cheStrings = chestr.split(",");
			        for(int i=0; i<cheStrings.length;i++){
			    	    String menuCode=cheStrings[i];
			    	    roleManager.add(roleCode, menuCode);
			    	}
				    if(number == 1){
				    	 Map<String, String> result=new HashMap<String, String>();
						 result.put("msg", "1");
						 OutputUtil.outPutJsonObject(response, result);
				    }
				}
			}else if("30".equals(requestType)){//删除角色 2015/11/11 王小伟
			    String isValid= "0";
			    String code = request.getParameter("code") == null ? "":request.getParameter("code");  
			    int number = roleManager.deleteRole(isValid,code);
			    if(number>0){
				    Map<String, String> result=new HashMap<String, String>();
					result.put("msg", "1");
					OutputUtil.outPutJsonObject(response, result);
				}
			}else if("31".equals(requestType)){//添加角色 2015/11/11 王小伟
			    String isValid="1";
			    String comment=  request.getParameter("comment") == null ? "":request.getParameter("comment"); 
			    String chestr = request.getParameter("chestr") == null ? "":request.getParameter("chestr"); 
			    String name = request.getParameter("name") == null ? "":request.getParameter("name"); 
			    String MaxCode=roleManager.findMaxCode();
			    int IntCode=Integer.valueOf(MaxCode).intValue();
			    int number=IntCode+1;
			    String code="0"+String.valueOf(number);
			    int num = roleManager.addOneRole(name, comment, isValid, code,employee.getCode());
			    if(num == 1){
			       String[] cheStrings = chestr.split(",");
			       for(int i=0;i<cheStrings.length;i++){
					   String menuCode=cheStrings[i];
			    	   roleManager.add(code, menuCode);
		    	   }
			       Map<String, String> result=new HashMap<String, String>();
			       result.put("msg", "1");
			       OutputUtil.outPutJsonObject(response, result);
			   }
			}else if("32".equals(requestType)){//根据code 拿到客户预约保养信息 2015/11/11 王小伟
	    	  String code=  request.getParameter("code") == null ? "":request.getParameter("code"); 
	    	  List<Map<String, Object>>list= customerManager.findCustomerAllService(code);
	    	  OutputUtil.outPutJsonArrary(response, list);
			}else if("33".equals(requestType)){//查询客户车辆详情
		    	String code=  request.getParameter("code") == null ? "":request.getParameter("code"); 
		    	CustomerCar customerCar=customerManager.findCustomerCar(code);
		    	Map<Object, Object> map =new HashMap<Object, Object>();
		    	Date time=customerCar.getInsuranceTime();
		    	String insuranceTime ="";
		    	if(time!=null){
		    		insuranceTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(time);
		    	}
		    	Date time1 = customerCar.getCarefulTime();
		    	String carefulTime ="";
		    	if(time1!=null){
		    		carefulTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(time1);
		    	}    	
		    	map.put("insuranceTime", insuranceTime);
		    	map.put("carefulTime", carefulTime);
		    	map.put("virCode", customerCar.getVirCode());
		    	map.put("engineCode", customerCar.getEngineCode());
		    	map.put("mileage", customerCar.getMileage());
		        if(map !=null){
		        	OutputUtil.outPutJsonObject(response, map);
		        }
			}else if("34".equals(requestType)){//拿到所有库存配件
			  String code=  request.getParameter("code") == null ? "":request.getParameter("code");	
	    	  List<AccessoriesStorage> list = accessoriesInfoManager.findAccessoriesStorageByTypeCode(code);
	    	  if(list != null){
	    		  OutputUtil.outPutJsonArrary(response, list);
	    	  }
			}else if("35".equals(requestType)){//拿到所有员工名
	    	  List<Employee> list = employeeManager.findAllEmployee();
	    	  if(list != null){
	    		  OutputUtil.outPutJsonArrary(response, list); 
	    	  }
	       }else if("36".equals(requestType)){//配件出库记录 2015.11.25 王小伟
	    	 String code=  request.getParameter("code") == null ? "":request.getParameter("code");
	    	 List<Map<String, String>> list = accessoriesInfoManager.findOutAccessoriesStorageRecord(code);
	    	 OutputUtil.outPutJsonArrary(response, list);	           
	       }else if("37".equals(requestType)){//配件入库记录 2015.11.25 王小伟
	    	   String code=  request.getParameter("code") == null ? "":request.getParameter("code");
		       List<Map<String, String>> list = accessoriesInfoManager.findInAccessoriesStorageRecord(code);
		       OutputUtil.outPutJsonArrary(response, list);
	      }else if("39".equals(requestType)){
        	 List<AccessoriesStorage> list = accessoriesInfoManager.findAllAccessoriesStorage();
        	 if(list!=null&&list.size()!=0){
        		 OutputUtil.outPutJsonArrary(response, list); 
        	 }
	      }else if("40".equals(requestType)){//出库数量判断
	    	  String code=  request.getParameter("name") == null ? "":request.getParameter("name");
	    	  String num=  request.getParameter("num") == null ? "":request.getParameter("num");
	    	  Map<String, Object> result=accessoriesInfoManager.outNumberChecked(code,num);
	    	  OutputUtil.outPutJsonObject(response, result); 
	      }else if("41".equals(requestType)){//会员升级 王小伟 2015.11.28
	    	 String code= request.getParameter("code") == null ? "":request.getParameter("code");
	    	 int number = weixinCustomerManager.setVip(code);
	    	 Map<String, Object> result=new HashMap<String, Object>();
			 result.put("msg", number);
			 OutputUtil.outPutJsonObject(response, result);  
		  }else if("42".equals(requestType)){//拿到所有配件名称
			  List<AccessoriesType> list = accessoriesTypeManager.selectAll();
              Iterator<AccessoriesType > iterator = list.iterator();
              while(iterator.hasNext()){
            	  System.out.println(iterator.next());
              }
			  if(list.size()!=0){
				  OutputUtil.outPutJsonArrary(response, list);   
			  }
		 }else if("43".equals(requestType)){
			 String typeName= request.getParameter("typeName") == null ? "":request.getParameter("typeName");
			 String maxCode = accessoriesTypeManager.findMaxCode();
			 System.out.println(maxCode+"aaa");
			 if(maxCode !=""){
			 int num = Integer.valueOf(maxCode).intValue();
			 int num1 = num+1;
			 System.out.println(num1);
			 String code = String.valueOf(num1);
			 int number = accessoriesTypeManager.addAccessoriesType(code,typeName);
			 if(number == 1){
				 Map<String, Object> result=new HashMap<String, Object>();
				 result.put("msg", number);
				 result.put("typeName", typeName);
				 result.put("maxCode", code);
				 OutputUtil.outPutJsonObject(response, result); 	 
			   }
			  }	 
		 }else if("44".equals(requestType)){//检测配件类型是否存在
			 String typeName= request.getParameter("typeName") == null ? "":request.getParameter("typeName");
			 int num2 = accessoriesTypeManager.textName(typeName);
			 Map<String, Object> result=new HashMap<String, Object>();
			 result.put("msg", num2);
			 OutputUtil.outPutJsonObject(response, result); 
		 }else if("45".equals(requestType)){//拿到最新添加的车辆
		    	String parentCode =request.getParameter("parentCode") == null ? "":request.getParameter("parentCode");
		    	List<CarInfo> list=carInfoManager.findNewCar(parentCode);
		    	System.out.println(list.size());
		    	OutputUtil.outPutJsonArrary(response, list);
		 }else if("46".equals(requestType)){//拿到最新修改的车辆
		    	String parentCode =request.getParameter("parentCode") == null ? "":request.getParameter("parentCode");
		    	List<CarInfo> list=carInfoManager.findUpdateCar(parentCode);
		    	OutputUtil.outPutJsonArrary(response, list);
		 }
		
		} catch (UnsupportedEncodingException e) {
			outPutErrorInfor(ManageController.class.getName(), "操作失败", e);
		} catch (IOException e) {
			outPutErrorInfor(ManageController.class.getName(), "操作失败", e);
		}
	}
	
	@RequestMapping(value="/forJsp")
	public String forJsp(HttpServletRequest request, HttpServletResponse response) throws Exception{
	    try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			Constants.root=request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+request.getContextPath();
			String sysRootPath = request.getSession().getServletContext().getRealPath("\\");
			SystemStaticArgsSet.setSysRootPath(sysRootPath);
			String requestType=request.getParameter("requestType") == null ? "": request.getParameter("requestType");
			int pageSize=10;
			Employee emp=(Employee) request.getSession().getAttribute("Employee");	
			if("1".equals(requestType)){//安全退出清空session
				//System.out.println("requestType");
				request.getSession().removeAttribute("Employee");
				request.getSession().removeAttribute("code");
			    return "/pcManager/index.html";
			}else if ("2".equals(requestType)) {//查询所有员工信息
		    	String nam1=request.getParameter("num1") == null ? "": request.getParameter("num1");
		    	String code = request.getParameter("code") == null ? "":  request.getParameter("code");
		    	int rowCount= employeeManager.countEmployee();
		    	int num=Integer.parseInt(nam1);
				Page page = new Page(pageSize, num, rowCount);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("startRow", page.getStartRow());
				map.put("pageSize", pageSize);
				map.put("code", code);
				List<LinkedHashMap<String, String>> list =employeeManager.selectAllEmployee(map);
			    request.setAttribute("list", list);
			    request.setAttribute("page", page);
			    request.setAttribute("code", code);
			    return "/pcManager/jsp/Employee/listUser.jsp";
			}else if("3".equals(requestType)){// 根据id删除员工
				String code=request.getParameter("id") == null ? "": request.getParameter("id");
				int num1=employeeManager.deleteEmployeeByCode(code);
				if(num1>0){
					System.out.println(num1);
					request.setAttribute("ok", "删除成功!!!");
				}
				return "manage/forJsp.do?pageSize1=4&num1=1&requestType=2";
			}else if("4".equals(requestType)){//修改或添加员工信息
				String id=request.getParameter("id") == null ? "": request.getParameter("id");
				String name=request.getParameter("name1") == null ? "": request.getParameter("name1");
	    		String weixinCode=request.getParameter("weixinCode") == null ? "": request.getParameter("weixinCode");
	    		String mobile=request.getParameter("mobile") == null ? "": request.getParameter("mobile");
	    		String loginName=request.getParameter("loginName") == null ? "": request.getParameter("loginName");
	    		String positionCode=request.getParameter("positionCode") == null ? "": request.getParameter("positionCode");
	    		String departmentCode=request.getParameter("departmentCode") == null ? "": request.getParameter("departmentCode");
	    		String password=request.getParameter("password") == null ? "": request.getParameter("password");
	    		String roleCode=request.getParameter("roleCode") == null ? "": request.getParameter("roleCode");
				int id1=Integer.parseInt(id);
				if(id1!=0){
				String modifiedCode=(String) request.getSession().getAttribute("code");
				Employee e= employeeManager.findEmployeeById(id1);
				String employeeCodeString = e.getCode();
				List<EmployeeRole> list = employeeManager.findRoleByCode(employeeCodeString);
				if(list.size() != 0){
					for(int i = 0;i<list.size();i++){
						String RoleCode1 =  list.get(i).getRoleCode();
						if(!RoleCode1.equals(roleCode)){
							employeeManager.updateEmployeeRole(e.getCode(), roleCode);
							int num = employeeManager.updateIcbEmployeeById(name, departmentCode, positionCode,loginName,password,id1,mobile,weixinCode,modifiedCode);
							if(num !=0){
							 request.setAttribute("ok", "修改成功");
							  }
						    }else{
						    int num = employeeManager.updateIcbEmployeeById(name, departmentCode, positionCode,loginName,password,id1,mobile,weixinCode,modifiedCode);
						    if(num !=0){
						    request.setAttribute("ok", "修改成功");
						    }
						   }
					     }
						}else{
							employeeManager.saveEmployeeRole(e.getCode(), roleCode);
							int num = employeeManager.updateIcbEmployeeById(name, departmentCode, positionCode,loginName,password,id1,mobile,weixinCode,modifiedCode);
							if(num !=0){
							request.setAttribute("ok", "修改成功");
							}
						}	
				}else{
					String code=systemParamManager.employeeCode();
					employeeManager.saveEmployee(name,mobile,loginName,password,positionCode,departmentCode,code,emp.getCode(),weixinCode);
					employeeManager.saveEmployeeRole(code,roleCode);
					request.setAttribute("ok", "添加成功");
				 }  
				return "manage/forJsp.do?requestType=2&num1=1";  
			}else if("5".equals(requestType)){//查询所有客户信息
		    	String nam1=request.getParameter("num1") == null ? "": request.getParameter("num1");
		    	String name=request.getParameter("name1") == null ? "": request.getParameter("name1");
		    	String mobile=request.getParameter("mobile1") == null ? "": request.getParameter("mobile1");
		    	List<Map<String, String>> list1=new ArrayList<Map<String,String>>();
		    	if(name ==""&&mobile == ""){
			    	int rowCount = customerManager.countCustomer();
			    	int num=Integer.parseInt(nam1);
			    	Page page = new Page(pageSize, num, rowCount);
			    	Map<String, Object> map = new HashMap<String, Object>();
			    	map.put("startRow", page.getStartRow());
			    	map.put("pageSize", pageSize);
			    	list1 = customerManager.selectAllCustomer(map);
			    	request.setAttribute("list1", list1);
			    	request.setAttribute("page", page);
			    	return "/pcManager/jsp/customer/listCustomer.jsp";	
		    	}else{
			    	Map<String, Object> map1 = new HashMap<String, Object>();
			    	map1.put("name", name);
			    	map1.put("mobile",mobile);
			    	int rowCount = customerManager.countByNameOrmobile(map1);
				    int num=Integer.parseInt(nam1);
				    Page page = new Page(pageSize, num, rowCount);
			    	Map<String, Object> map = new HashMap<String, Object>();
			    	map.put("name", name);
			    	map.put("mobile", mobile);
			    	map.put("startRow", page.getStartRow());
			    	map.put("pageSize", pageSize);
			    	list1 = customerManager.findByNameOrmobile(map);
			    	request.setAttribute("list1", list1);
			    	request.setAttribute("page", page);
			    	request.setAttribute("mobile", mobile);
			    	request.setAttribute("name", name);
			    	return "/pcManager/jsp/customer/listCustomer.jsp";		
		    	}
			}else if("6".equals(requestType)){//车辆管理 2015/11/11 王小伟
				return "/pcManager/jsp/car/listCar.jsp";
			}else if("7".equals(requestType)){//分页查询所有配件和按条件分页查询配件 2015/11/11 王小伟
				String typeCode = "";
				String nam1=request.getParameter("num1") == null ? "": request.getParameter("num1");
			    typeCode=request.getParameter("code") == null ? "": request.getParameter("code");
			    List<AccessoriesType> typeList = accessoriesTypeManager.selectAll();
				int num=Integer.parseInt(nam1);
				if(typeCode == ""){
					Map<String, Object> map = new HashMap<String, Object>();
					int rowCount=accessoriesInfoManager.countAccessoriesStorage();
					Page page = new Page(pageSize, num, rowCount);
					map.put("startRow", page.getStartRow());
			    	map.put("pageSize", pageSize);
			    	List<AccessoriesStorage> list = accessoriesInfoManager.searchAccessoriesStorage(map);
			    	request.setAttribute("list", list);
			    	request.setAttribute("page", page);
			    	request.setAttribute("typeCode", typeCode);
			    	request.setAttribute("typeList", typeList);
					return "/pcManager/jsp/accessories/listAccessories.jsp";
				}else{
					Map<String, Object> map = new HashMap<String, Object>();
					int rowCount = accessoriesInfoManager.countByTypeCode(typeCode);
					Page page = new Page(pageSize, num, rowCount);
					map.put("startRow", page.getStartRow());
			    	map.put("pageSize", pageSize);
			    	map.put("typeCode", typeCode);
			    	List<AccessoriesStorage> list = accessoriesInfoManager.searchByTypeCode(map);
			    	request.setAttribute("list", list);
			    	request.setAttribute("typeList", typeList);
			    	request.setAttribute("page", page);
			    	request.setAttribute("typeCode", typeCode);
					return "/pcManager/jsp/accessories/listAccessories.jsp";
				}
			}else if("8".equals(requestType)){//拿到所有虚假客户 2015/11/11 王小伟
			        List<FalseAccount>  list= falseAccountManager.selectAllFalseAccount();
			        request.setAttribute("list", list);
				    return "/pcManager/jsp/customer/addAccount.jsp";
			}else if("9".equals(requestType)){//拿到所有角色信息 2015/11/11 王小伟
				    List <Role> list =	 employeeManager.findAllRole();  
				    request.setAttribute("list", list);
				    return "/pcManager/jsp/Role/listRole.jsp";
			}else if("10".equals(requestType)){//客户车辆 2015/11/12 王小伟
					String nam1=request.getParameter("num1") == null ? "": request.getParameter("num1");
					String name=request.getParameter("name1") == null ? "": request.getParameter("name1");
					String licensePlate=request.getParameter("licensePlate1") == null ? "": request.getParameter("licensePlate1");
					if(name == "" && licensePlate == ""){
						int num=Integer.parseInt(nam1);
						int rowCount = customerManager.countCustomerCar();
						Page page = new Page(pageSize, num, rowCount);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("startRow", page.getStartRow());
				    	map.put("pageSize", pageSize);
						List<Map<String, Object>> list =  customerManager.selectCustomerCar(map);
						request.setAttribute("list", list);
				    	request.setAttribute("page", page);
						return "/pcManager/jsp/customer/customerCar.jsp";
				    }else{
				    	Map<String, Object> map = new HashMap<String, Object>();
				    	map.put("name", name);
				    	map.put("licensePlate", licensePlate);
				    	int rowCount = customerManager.countByNameOrlicensePlate(map);
				    	int num=Integer.parseInt(nam1);
				    	System.out.println(rowCount);
						Page page = new Page(pageSize, num, rowCount);
						Map<String, Object> map1 = new HashMap<String, Object>();
				    	map1.put("name", name);
				    	map1.put("licensePlate", licensePlate);
				    	map1.put("startRow", page.getStartRow());
				    	map1.put("pageSize", pageSize);
				       List<Map<String, Object>>list= customerManager.selectByNameOrlicensePlate(map1);
				    	request.setAttribute("list", list);
				    	request.setAttribute("page", page);
				    	request.setAttribute("name", name);
				    	request.setAttribute("licensePlate", licensePlate);
				    	return "/pcManager/jsp/customer/customerCar.jsp";
				      }
			}	
		} catch (Exception e) {
			outPutErrorInfor(ManageController.class.getName(), "forJsp", e);
			e.printStackTrace();
		}
	    return null;	
}
	
	/******
	 * 登录
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/login")
	public String logIn(HttpServletRequest request, HttpServletResponse response){
		String loginName1=request.getParameter("loginName") == null ? "": request.getParameter("loginName");
		String password1=request.getParameter("password") == null ? "": request.getParameter("password");
		String sysRootPath = request.getSession().getServletContext().getRealPath("\\");
		SystemStaticArgsSet.setSysRootPath(sysRootPath);
		
		try {
			Employee icbEmployee = new Employee();	
			icbEmployee.setLoginName(loginName1);
			icbEmployee.setPassword(password1);
			icbEmployee = employeeManager.login(icbEmployee);
			if (icbEmployee != null) {
				int id=icbEmployee.getId();
				String code=icbEmployee.getCode();
				List<Menu> list2=menuManager.quanXian(id);				
				request.setAttribute("list", list2);
				request.getSession().setAttribute("Employee",icbEmployee);	
				request.getSession().setAttribute("code",code);
				icbEmployee = null;
				return "/pcManager/workframe.jsp";
			} else {
				request.setAttribute("no", "账户或密码错误！！");
				return "/pcManager/index.html";
			}
		} catch (Exception e) {
			outPutErrorInfor(ManageController.class.getName(), "logIn", e);
			return null;
		}
	}
}
