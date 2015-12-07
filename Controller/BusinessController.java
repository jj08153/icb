package com.icb123.Controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.icb123.Common.Constants;
import com.icb123.Service.AccessoriesInfoManager;
import com.icb123.Service.BusinessManager;
import com.icb123.Service.CarInfoManager;
import com.icb123.Service.CarMaintenanceManager;
import com.icb123.Service.CustomerManager;
import com.icb123.Service.EmployeeManager;
import com.icb123.Service.IntegralManager;
import com.icb123.Service.SendManager;
import com.icb123.Service.ServiceManager;
import com.icb123.Service.SystemParamManager;
import com.icb123.Service.WeixinCustomerManager;
import com.icb123.Service.WorkRecordManager;
import com.icb123.Util.IdentifyingImg;
import com.icb123.Util.OutputUtil;
import com.icb123.Util.PropertiesUtils;
import com.icb123.Util.SystemModelExceptionBase;
import com.icb123.Util.SystemStaticArgsSet;
import com.icb123.bean.AppointmentTime;
import com.icb123.bean.ConsumptionRecord;
import com.icb123.bean.Customer;
import com.icb123.bean.CustomerAppointement;
import com.icb123.bean.CustomerCar;
import com.icb123.bean.CustomerCarSituation;
import com.icb123.bean.Employee;
import com.icb123.bean.Team;
import com.icb123.bean.WeixinAcceptRecord;
import com.icb123.bean.WeixinCustomer;
import com.icb123.bean.WorkRecord;
import com.icb123.web.bean.PageBean;
import com.icb123.weixin.WeixinUntil;
import com.icb123.weixin.Manager.CustomerBusinessManager;

/**
 * 客户业务
 * */
@Controller
@RequestMapping("/business")
public class BusinessController extends SystemModelExceptionBase{

	@Resource
	private BusinessManager businessManager;
	@Resource
	private CarInfoManager carInfoManager;
	@Resource
	private ServiceManager serviceManager;
	@Resource
	private AccessoriesInfoManager accessoriesInfoManager;
	@Resource
	private SystemParamManager systemParamManager;
	@Resource
	private CustomerManager customerManager;
	@Resource
	private WorkRecordManager workRecordManager;
	@Resource
	private EmployeeManager employeeManager;
	@Resource
	private SendManager sendManager;
	@Resource
	private WeixinCustomerManager weixinCustomerManager;
	@Resource
	private IntegralManager integralManager;
	@Resource
	private CarMaintenanceManager carMaintenanceManager;
	@Resource
	private CustomerBusinessManager customerBusinessManager;
	@RequestMapping(value="/appoint")
	public void customerAppointment(HttpServletRequest request, HttpServletResponse response){
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			Constants.root=request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+request.getContextPath();
			String sysRootPath = request.getSession().getServletContext().getRealPath("\\");
			SystemStaticArgsSet.setSysRootPath(sysRootPath);
			String requestType=request.getParameter("requestType") == null ? "": request.getParameter("requestType");
			String userCode=request.getParameter("userCode") == null ? "": request.getParameter("userCode");
			String openid=request.getParameter("openid") == null ? "": request.getParameter("openid");
			//openid="111111";
			if(StringUtils.isBlank(openid)&&StringUtils.isNotBlank(userCode)){
				openid=WeixinUntil.getUserOoenid(userCode);
			}
			if(weixinCustomerManager.findByOpenid(openid)==null){
				String accessToken=WeixinUntil.getAccessToken();
				WeixinCustomer wx=customerBusinessManager.getCustomerInfo(openid, accessToken, "");
				weixinCustomerManager.save(wx);
			}
			//System.out.println(PropertiesUtils.getValueByKey("token"));
			Employee emp=(Employee) request.getSession().getAttribute("Employee");
			String empCode="";
			if(emp!=null){
				empCode=emp.getCode();
			}else{	
				if(!"".equals(openid)){
					empCode=employeeManager.findEmployeeCodeByOpenid(openid);
				}
			}	
			if("1".equals(requestType)){//客户预约
				String paramObj=request.getParameter("paramObj");
				JSONObject json =JSONObject.fromObject(paramObj);
				JSONArray arr=(JSONArray) json.get("service");
				String[] serviceArray = new String[arr.size()];
				serviceArray=(String[]) arr.toArray(serviceArray);
				Map<String, String> result = null;
				if(StringUtils.isNotBlank((String)json.get("userCode"))){
					openid=WeixinUntil.getUserOoenid((String)json.get("userCode"));
				}else{
					openid=null;
				}
				try {
					result = businessManager.appointmentMaintenance((String)json.get("date"), (String)json.get("timeCode"), (String)json.get("mobile"), (String)json.get("name"), openid, (String)json.get("carCode"), (String)json.get("license"), (String)json.get("code"), (String)json.get("totalPrice"), (String)json.get("address"), (String)json.get("remark"), (String)json.get("appTypeCode"), (String)json.get("type"),serviceArray);
				} catch (Exception e) {
					outPutErrorInfor(BusinessController.class.getName(), "客户预约", e);
				}
				OutputUtil.outPutJsonObject(response, result);
			}else if("2".equals(requestType)){//预约查询			
				String status=request.getParameter("status") == null ? "": request.getParameter("status");
				String currentPageStr=request.getParameter("currentPage") == null ? "": request.getParameter("currentPage");
				String sizeStr=request.getParameter("pageSize") == null ? "": request.getParameter("pageSize");
				String ctime=request.getParameter("ctime") == null ? "": request.getParameter("ctime");
				String mobile=request.getParameter("smobile") == null ? "": request.getParameter("smobile");
				String atime=request.getParameter("atime") == null ? "": request.getParameter("atime");
				try {
					Map<String, String> argMap=new HashMap<String, String>();
					argMap.put("status", status);		
					if(StringUtils.isNotBlank(currentPageStr)&&StringUtils.isNotBlank(sizeStr)){
						int currentPage=Integer.valueOf(currentPageStr);
						int size=Integer.valueOf(sizeStr);
						int begin=(currentPage-1)*size;
						argMap.put("size", size+"");
						argMap.put("begin", begin+"");
						argMap.put("currentPage", currentPageStr);
						argMap.put("ctime", ctime);
						argMap.put("atime", atime);
						argMap.put("mobile", mobile);
					}
					PageBean page=businessManager.selectAppointment(argMap);
					OutputUtil.outPutJsonObject(response, page);
				} catch (Exception e) {
					outPutErrorInfor(BusinessController.class.getName(), "预约信息查询失败", e);
				}
			}else if("3".equals(requestType)){//预约详情查看
				String appcode=request.getParameter("code") == null ? "": request.getParameter("code");
				Map map=businessManager.findDetailedAppointment(appcode);
				OutputUtil.outPutJsonObject(response, map);
			}else if("4".equals(requestType)){//确认预约
				String paramObj=request.getParameter("paramObj");
				JSONObject json =JSONObject.fromObject(paramObj);
				JSONArray arr=(JSONArray) json.get("service");
				String[] serviceArray = new String[arr.size()];
				serviceArray=(String[]) arr.toArray(serviceArray);
				Map<String, String> result = null;
				try {
					result = businessManager.appointmentConfirm((String)json.get("timeCode"), (String)json.get("date"), (String)json.get("time"), (String)json.get("carCode"), (String)json.get("license"), (String)json.get("haveCar"),(String)json.get("modelsCode"), (String)json.get("modelsStr"),(String)json.get("cusCode"), (String)json.get("mobile"), (String)json.get("name"), (String)json.get("address"), (String)json.get("code"), (String)json.get("remark"), (String)json.get("totalPrice"), serviceArray, emp.getCode(),(String)json.get("status"),(String)json.get("wxCode"));
				} catch (Exception e) {
					outPutErrorInfor(BusinessController.class.getName(), "确认预约失败", e);
				}
				OutputUtil.outPutJsonObject(response, result);
			}else if("5".equals(requestType)){//取消预约
				String appcode=request.getParameter("code") == null ? "": request.getParameter("code");
				String reason=request.getParameter("reason") == null ? "": request.getParameter("reason");
				Map<String, String> result = businessManager.appointmentCancel(appcode, empCode,reason);
				OutputUtil.outPutJsonObject(response, result);
			}else if("6".equals(requestType)){//获得空闲服务队
				String date=request.getParameter("date") == null ? "": request.getParameter("date");
				String time=request.getParameter("time") == null ? "": request.getParameter("time");
				List<Team> list=businessManager.findFreeTeam(date,time);
				OutputUtil.outPutJsonArrary(response, list);
			}else if("7".equals(requestType)){//分派人员
				String appCode=request.getParameter("appCode") == null ? "": request.getParameter("appCode");
				String teamCode=request.getParameter("teamCode") == null ? "": request.getParameter("teamCode");
				String[] empArr=request.getParameterValues("emp");
				Map<String, String> result=businessManager.distributeWork(appCode, empCode, teamCode, empArr);
				OutputUtil.outPutJsonObject(response, result);
			}else if("8".equals(requestType)){//查看分给自己的预约
				String currentPageStr=request.getParameter("currentPage") == null ? "": request.getParameter("currentPage");
				String sizeStr=request.getParameter("pageSize") == null ? "": request.getParameter("pageSize");
				try {
					Map<String, String> argMap=new HashMap<String, String>();
					argMap.put("empCode", empCode);
					if(StringUtils.isNotBlank(currentPageStr)&&StringUtils.isNotBlank(sizeStr)){
						int currentPage=Integer.valueOf(currentPageStr);
						int size=Integer.valueOf(sizeStr);
						int begin=(currentPage-1)*size;
						argMap.put("size", size+"");
						argMap.put("begin", begin+"");
						argMap.put("currentPage", currentPageStr);
					}
					PageBean page=businessManager.selectPersonalAppointment(argMap);
					OutputUtil.outPutJsonObject(response, page);
				} catch (Exception e) {
					outPutErrorInfor(BusinessController.class.getName(), "查看分给自己的预约失败", e);
				}
			}else if("9".equals(requestType)){//获得空闲技师
				String empcode=request.getParameter("code") == null ? "": request.getParameter("code");
				String position=request.getParameter("position") == null ? "": request.getParameter("position");
				List<Employee> list=businessManager.findFreeEmp(empcode,position);
				OutputUtil.outPutJsonArrary(response, list);
			}else if("10".equals(requestType)){//删除指定预约排班
				String appcode=request.getParameter("code") == null ? "": request.getParameter("code");
				Map<String, String> result=businessManager.daleteDistributeWork(appcode,empCode);
				OutputUtil.outPutJsonObject(response, result);
			}else if("11".equals(requestType)){//查看预约排班情况
				String appcode=request.getParameter("code") == null ? "": request.getParameter("code");
				List<WorkRecord> list=workRecordManager.findByAppCode(appcode);
				OutputUtil.outPutJsonArrary(response, list);
			}else if("12".equals(requestType)){//生成手机验证码
				String mobile=request.getParameter("mobile") == null ? "": request.getParameter("mobile");
				Map<String, String> result=sendManager.mobileValidate(mobile);
				OutputUtil.outPutJsonObject(response, result);
			}else if("13".equals(requestType)){//手机验证
				String mobile=request.getParameter("mobile") == null ? "": request.getParameter("mobile");
				String validate=request.getParameter("validate") == null ? "": request.getParameter("validate");
				Map<String, String> result=businessManager.mobileValidate(mobile,validate);//手机验证
				OutputUtil.outPutJsonObject(response, result);
			}else if("14".equals(requestType)){//记录配件型号
				/*String model=request.getParameter("model") == null ? "": request.getParameter("model");
				String appcode=request.getParameter("code") == null ? "": request.getParameter("code");
				String weixinCode=request.getParameter("weixinCode") == null ? "": request.getParameter("weixinCode");
				Map<String, String> result=businessManager.writeAccessoriesModel(appcode,model,weixinCode);
				OutputUtil.outPutJsonObject(response, result);*/
			}else if("15".equals(requestType)){//任务完结
				String appcode=request.getParameter("code") == null ? "": request.getParameter("code");
				String vipCondition=request.getParameter("condition") == null ? "": request.getParameter("condition");
				Map<String, String> result=businessManager.finishAppointment(appcode,vipCondition);
				OutputUtil.outPutJsonObject(response, result);
			}else if("16".equals(requestType)){//客服超级密码
				String kfpwd = "qweasd";
				String validate=request.getParameter("code") == null ? "": request.getParameter("code");
				Map<String, String> result= new HashMap<String, String>();
				if(validate.equals(kfpwd)){
					result.put("flag", "1");
				}else{
					result.put("flag", "-1");
				}
				OutputUtil.outPutJsonObject(response, result);
			}else if("17".equals(requestType)){//查询历史保养信息
				String mobile=request.getParameter("mobile") == null ? "": request.getParameter("mobile");
				List<Map<String, Object>> result=businessManager.searchCustomerPensonalAppointmentByMobile(mobile);
				OutputUtil.outPutJsonArrary(response, result);
			}else if("18".equals(requestType)){
				 //设置不缓存图片  
		        response.setHeader("Pragma", "No-cache");  
		        response.setHeader("Cache-Control", "No-cache");  
		        response.setDateHeader("Expires", 0) ;  
		        //指定生成的相应图片  
		        response.setContentType("image/jpeg") ;
		        IdentifyingImg img=new IdentifyingImg();
		        ImageIO.write(img.creat(), "JPEG", response.getOutputStream()) ;
			}else if("19".equals(requestType)){//获取微信用户信息
				Map<String, Object> map=weixinCustomerManager.findCustomerInfo(openid);
				OutputUtil.outPutJsonObject(response, map);
			}else if("20".equals(requestType)){//微信用户获取当前订单信息
				List<Map> result=businessManager.findCurrentAppByOpenid(openid);
				OutputUtil.outPutJsonArrary(response, result);
			}else if("21".equals(requestType)){//微信用户获取历史保养车辆
				//List<Map> result=businessManager.searchCustomerPensonalAppointmentByOpenid(openid);
				List<CustomerCar> result=businessManager.findHistoryCar(openid);
				OutputUtil.outPutJsonArrary(response, result);
			}else if("22".equals(requestType)){//微信用户获取个人二维码
				Map<String, String> result=new HashMap<String, String>();
				String path=weixinCustomerManager.creatEwmByWeixinCustomer(openid);
				result.put("path", path);
				OutputUtil.outPutJsonObject(response, result);
			}else if("23".equals(requestType)){//微信用户个人信息面板展示	
				Map<String, String> result=null;
				Customer cus=customerManager.findCustomerByOpenid(openid);
				if(cus!=null){
					result=new HashMap<String, String>();
					result.put("name", cus.getName());
					result.put("mobile", cus.getMobile());
				}	
				OutputUtil.outPutJsonObject(response, result);
			}else if("24".equals(requestType)){//微信用户个人信息获取
				String name=request.getParameter("name") == null ? "": request.getParameter("name");
				String mobile=request.getParameter("mobile") == null ? "": request.getParameter("mobile");
				Map<String, String> result=weixinCustomerManager.saveCustomerInfo(openid,name,mobile);
				OutputUtil.outPutJsonObject(response, result);
			}else if("25".equals(requestType)){//微信用户当前车况获取
				List<Map> result=businessManager.findCurrentCar(openid);
				OutputUtil.outPutJsonArrary(response, result);
			}else if("26".equals(requestType)){//微信用户获取积分使用明细
				List<Map<String, String>> list=weixinCustomerManager.findConsumptionRecordByOpenid(openid);
				OutputUtil.outPutJsonArrary(response, list);
			}else if("27".equals(requestType)){//微信用户获取积分使用上限
				String busnessType=request.getParameter("type") == null ? "": request.getParameter("type");
				String totalPrice=request.getParameter("totalPrice") == null ? "": request.getParameter("totalPrice");
				double pay=integralManager.maxOutIntegral(openid, Integer.valueOf(busnessType), Double.valueOf(totalPrice));
				Map<String, String> map=new HashMap<String, String>();
				map.put("pay", (int)pay+"");
				OutputUtil.outPutJsonObject(response, map);
			}else if("28".equals(requestType)){//微信用户使用积分支付
				String code=request.getParameter("code") == null ? "": request.getParameter("code");
				String busnessType=request.getParameter("type") == null ? "": request.getParameter("type");
				String pay=request.getParameter("pay") == null ? "": request.getParameter("pay");
				Map<String, String> map=businessManager.integralPay(code,openid,busnessType,pay);
				OutputUtil.outPutJsonObject(response, map);
			}else if("29".equals(requestType)){//排班详情
				String date=request.getParameter("date") == null ? "": request.getParameter("date");
				List<AppointmentTime> list=businessManager.findAppTime(date);
				OutputUtil.outPutJsonArrary(response, list);
			}else if("30".equals(requestType)){//保存用户评分
				String code=request.getParameter("code") == null ? "": request.getParameter("code");
				String score=request.getParameter("scorce") == null ? "": request.getParameter("scorce");
				Map<String, String> result=businessManager.saveCustomerScore(code,score);
				OutputUtil.outPutJsonObject(response, result);
			}else if("31".equals(requestType)){//打开个人礼物界面
				String code=request.getParameter("weixinCode") == null ? "": request.getParameter("weixinCode");
				Map<String, Object> result=weixinCustomerManager.openGiftView(code);
				OutputUtil.outPutJsonObject(response, result);
			}else if("32".equals(requestType)){//客户领奖
				String code=request.getParameter("weixinCode") == null ? "": request.getParameter("weixinCode");
				String accept=request.getParameter("accept") == null ? "": request.getParameter("accept");
				List<WeixinAcceptRecord> list=weixinCustomerManager.acceptGift(code, accept);
				OutputUtil.outPutJsonArrary(response, list);
			}else if("33".equals(requestType)){//微信有用户获取指定车辆历史记录
				String carCdoe=request.getParameter("code") == null ? "": request.getParameter("code");
				List<Map> list=businessManager.findHistoryAppByCarCode(carCdoe);
				OutputUtil.outPutJsonArrary(response, list);
			}else if("100".equals(requestType)){//技师录入页面提交
				String paramObj=request.getParameter("paramObj")==null?"":request.getParameter("paramObj");
				JSONObject json =JSONObject.fromObject(paramObj);
				Map<String, String> result = businessManager.inPutFinishInfo(json,emp.getCode());
				OutputUtil.outPutJsonObject(response, result);
			}else if("101".equals(requestType)){//检测项目查看
				String appCode=request.getParameter("appCode") == null ? "": request.getParameter("appCode");
				List<CustomerCarSituation> list = customerManager.findByAppCode(appCode);
				Map<String, List<CustomerCarSituation>> result=new HashMap<String, List<CustomerCarSituation>>();
				result.put("ccs", list);
				OutputUtil.outPutJsonObject(response, result);
			}else if("102".equals(requestType)){//查询员工页面
				String position=request.getParameter("position") == null ? "": request.getParameter("position");
				List<Employee> list = employeeManager.findEmployeeByPosition(position);
				OutputUtil.outPutJsonArrary(response, list);
			}else if("103".equals(requestType)){//查询订单对应的服务人员
				String appCode=request.getParameter("appCode") == null ? "": request.getParameter("appCode");
				List<WorkRecord> list = workRecordManager.findByAppCode(appCode);
				OutputUtil.outPutJsonArrary(response, list);
			}else if("104".equals(requestType)){//查询所有车队
				List<Team> list = carMaintenanceManager.findAllTeam();
				OutputUtil.outPutJsonArrary(response, list);
			}
		} catch (UnsupportedEncodingException e){
			// TODO Auto-generated catch block
			outPutErrorInfor(BusinessController.class.getName(), "操作失败", e);
		} catch (IOException e) {
			outPutErrorInfor(BusinessController.class.getName(), "操作失败", e);
		}
	}	
}
