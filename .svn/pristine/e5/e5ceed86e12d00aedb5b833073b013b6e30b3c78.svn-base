package graph.server.controller;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import graph.server.model.*;

import graph.server.service.AdminService;

@Controller
public class GraphXManager {
	
	@Autowired
    private AdminService adminService;
	private Format format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	//GraphX平台列表
	@RequestMapping("/GraphXList")
	public String graphXList(GraphXModel graphX, Model model,HttpSession httpSession) {
		
		long user_Id = (long) httpSession.getAttribute("UserID");
		System.out.println("-----------------------------------"+user_Id);
		graphX.setUser_Id(user_Id);
		List<GraphXModel> newsList = adminService.findGraphXListByUserID(user_Id);
		System.out.println("newsList: " + newsList);
		model.addAttribute("newsList", newsList);
		return "new/GraphXManager";
	}
	
	//GraphX平台删除
	@RequestMapping(value="/GraphXDelete/{id}")
	public String graphXDelete(@PathVariable(value="id") Integer graphX_id) {
		System.out.println("删除来了。。。。。。。。。。");
		int deleteResult = adminService.deleteGraphXById(graphX_id);
		System.out.println("deleteResult: " + deleteResult);
		return "redirect:/GraphXList";
	}
	//GraphX平台修改跳转
	@RequestMapping(value="/GraphXEditInput/{id}")
	public String graphXEditInput(@PathVariable(value="id") Integer graphX_id,GraphXModel graphX,Model model) {

		graphX = adminService.findGraphXListBygraphXID(graphX_id);
		graphX.setGraphX_id(graphX_id);
		model.addAttribute("graphX", graphX);
		System.out.println("跳转到修改页面。。。。。。。。。。");
		return "new/GraphXEdit";
	}
	
	//GraphX平台修改提交
	@RequestMapping(value="/GraphXPost")
	public String graphXPost(GraphXModel graphX) {
		System.out.println(graphX);
		int EditResult = adminService.updateGraphXById(graphX);
		System.out.println("EditResult: " + EditResult);
		return "redirect:/GraphXList";
	}
	
	//GraphX平台添加跳转
	@RequestMapping(value="/GraphXAddInput")
	public String graphXAddInput(Model model) {
		System.out.println("跳转到添加页面。。。。。。。。。。");
		return "new/GraphXAdd";
	}
	
	//GraphX平台添加提交
	@RequestMapping(value="/GraphXAddPost")
	public String graphXAddPost(GraphXModel graphX) {
		graphX.setGraphX_createtime(new Date());
		int AddResult = adminService.insertGraphX(graphX);
		System.out.println("AddResult: " + AddResult);
		return "redirect:/GraphXList";
	}
	
	//----------------------------------------------------------------------------------------------------------
	
	//GraphX配置查看
	@RequestMapping(value="/GraphXLook")
	@ResponseBody
	public Map<String, String> graphXLook(@RequestParam("graphX_id") Integer graphX_id,GraphXModel graphX,HttpSession httpSession) {
		
		graphX = adminService.findGraphXListBygraphXID(graphX_id);
		Map<String,String> map=new HashMap<>();
		map.put("graphX_id", String.valueOf(graphX.getGraphX_id()));
		map.put("graphX_name", graphX.getGraphX_name());
		map.put("graphX_ip", graphX.getGraphX_ip());
		map.put("graphX_port", graphX.getGraphX_port());
		map.put("userName", (String) httpSession.getAttribute("UserName"));
		map.put("graphX_version", graphX.getGraphX_version());
		map.put("graphX_algorithm", graphX.getGraphX_algorithm());
		map.put("graphX_operation", graphX.getGraphX_operation());
		map.put("graphX_createtime", format.format(graphX.getGraphX_createtime()).toString());
		System.out.println(map);
		return map;
	}
	
	//GraphX平台删除
	@RequestMapping(value="/GraphXDel")
	@ResponseBody
	public String graphXDel(@RequestParam("graphX_id") Integer graphX_id) {
		System.out.println(graphX_id);
		int deleteResult = adminService.deleteGraphXById(graphX_id);
		if(deleteResult == 1) {
			return "1";
		}else {
			return "0";
		}
	}
	
	//GraphX平台修改提交
	@RequestMapping(value="/GraphXSubmit")
	@ResponseBody
	public String graphXSubmit(GraphXModel graphX,HttpSession httpSession) {
		System.out.println("------------------------------------------");
		graphX.setUser_Id((long) httpSession.getAttribute("UserID"));
		System.out.println(graphX);
		int EditResult = adminService.updateGraphXById(graphX);
		System.out.println("EditResult: " + EditResult);
		return String.valueOf(EditResult);
	}
	
	//GraphX平台添加提交
	@RequestMapping(value="/GraphXAddSubmitPost")
	@ResponseBody
	public String graphXAddSubmitPost(GraphXModel graphX) {
		System.out.println("+++++++++++++"+ graphX);
		graphX.setGraphX_createtime(new Date());
		System.out.println("---------------"+graphX);
		int AddResult = adminService.insertGraphX(graphX);
		System.out.println("AddResult: " + AddResult);
		return String.valueOf(AddResult);
	}
	
	

}
