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
public class Neo4jManager {

	@Autowired
    private AdminService adminService;
	private Format format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	//Neo4j平台列表
	@RequestMapping("/Neo4jList")
	public String neo4jList(Neo4jModel neo4j, Model model,HttpSession httpSession) {
		
		long user_Id = (long) httpSession.getAttribute("UserID");
		System.out.println("-----------------------------------"+user_Id);
		List<Neo4jModel> newsList = adminService.findNeo4jListByUserID(user_Id);
		List<Neo4jLocalModel> newsListLocal = adminService.findNeo4jListLocalByUserID(user_Id);
		System.out.println("newsList: " + newsList);
		model.addAttribute("newsList", newsList);
		model.addAttribute("newsListLocal", newsListLocal);
		return "new/Neo4jManager";
	}
	//Neo4j平台删除
	@RequestMapping(value="/Neo4jDelete/{id}")
	public String neo4jDelete(@PathVariable(value="id") Integer neo4j_id) {
		System.out.println("删除来了。。。。。。。。。。");
		int deleteResult = adminService.deleteNeo4jById(neo4j_id);
		System.out.println("deleteResult: " + deleteResult);
		return "redirect:/Neo4jList";
	}
	//Neo4j本地平台删除
	@RequestMapping(value="/Neo4jLocalDelete/{id}")
	public String neo4jLocalDelete(@PathVariable(value="id") Integer embedded_neo4j_id) {
		System.out.println("删除来了。。。。。。。。。。");
		int deleteLocalResult = adminService.deleteNeo4jLocalById(embedded_neo4j_id);
		System.out.println("deleteLocalResult: " + deleteLocalResult);
		return "redirect:/Neo4jList";
	}
	
	//Neo4j平台修改跳转
	@RequestMapping(value="/Neo4jEditInput/{id}")
	public String neo4jEditInput(@PathVariable(value="id") Integer neo4j_id,Neo4jModel neo4j,Model model) {

		neo4j = adminService.findNeo4jListByNeo4jID(neo4j_id);
		neo4j.setNeo4j_id(neo4j_id);
		model.addAttribute("neo4j", neo4j);
		System.out.println("跳转到修改页面。。。。。。。。。。");
		return "new/Neo4jEdit";
	}
	
	//Neo4j平台修改提交
	@RequestMapping(value="/Neo4jPost")
	public String neo4jPost(Neo4jModel neo4j) {
		System.out.println(neo4j);
		int EditResult = adminService.updateNeo4jById(neo4j);
		System.out.println("EditResult: " + EditResult);
		return "redirect:/Neo4jList";
	}
	
	//Neo4j本地平台修改跳转
	@RequestMapping(value="/Neo4jLocalEditInput/{id}")
	public String neo4jLocalEditInput(@PathVariable(value="id") Integer embedded_neo4j_id,Neo4jLocalModel neo4jLocal,Model model) {

		neo4jLocal = adminService.findNeo4jListLocalByNeo4jID(embedded_neo4j_id);
		neo4jLocal.setEmbedded_neo4j_id(embedded_neo4j_id);
		model.addAttribute("neo4jLocal", neo4jLocal);
		System.out.println(neo4jLocal);
		System.out.println("跳转到修改页面。。。。。。。。。。");
		return "new/Neo4jLocalEdit";
	}
	
	//Neo4j本地平台修改提交
	@RequestMapping(value="/Neo4jLocalPost")
	public String neo4jLocalPost(Neo4jLocalModel neo4jLocal) {
		System.out.println(neo4jLocal);
		int EditResult = adminService.updateNeo4jLocalById(neo4jLocal);
		System.out.println("EditResult: " + EditResult);
		return "redirect:/Neo4jList";
	}
	
	//Neo4j平台添加跳转
	@RequestMapping(value="/Neo4jAddInput")
	public String neo4jAddInput(Model model) {
		System.out.println("跳转到添加页面。。。。。。。。。。");
		return "new/Neo4jAdd";
	}
	
	//Neo4j平台添加提交
	@RequestMapping(value="/Neo4jAddPost")
	public String neo4jAddPost(Neo4jModel neo4j) {
		neo4j.setNeo4j_createtime(new Date());
		int AddResult = adminService.insertNeo4j(neo4j);
		System.out.println("EditResult: " + AddResult);
		return "redirect:/Neo4jList";
	}
	
	//Neo4j本地平台添加跳转
	@RequestMapping(value="/Neo4jLocalAddInput")
	public String neo4jLocalAddInput(Model model) {
		System.out.println("跳转到添加页面。。。。。。。。。。");
		return "new/Neo4jLocalAdd";
	}
	
	//Neo4j本地平台添加提交
	@RequestMapping(value="/Neo4jLocalAddPost")
	public String neo4jLocalAddPost(Neo4jLocalModel neo4jLocal) {
		neo4jLocal.setEmbedded_neo4j_createtime(new Date());
		int AddResult = adminService.insertNeo4jLocal(neo4jLocal);
		System.out.println("EditResult: " + AddResult);
		return "redirect:/Neo4jList";
	}
	
	//---------------------------------------------------------------------------------------
	//Neo4j配置查看
	@RequestMapping(value="/Neo4jLook")
	@ResponseBody
	public Map<String, String> neo4jLook(@RequestParam("neo4j_id") Integer neo4j_id,Neo4jModel neo4j,HttpSession httpSession) {

		neo4j = adminService.findNeo4jListByNeo4jID(neo4j_id);
		Map<String,String> mapNeo4j=new HashMap<>();
		mapNeo4j.put("neo4j_id", String.valueOf(neo4j.getNeo4j_id()));
		mapNeo4j.put("neo4j_name", neo4j.getNeo4j_name());
		mapNeo4j.put("neo4j_ip", neo4j.getNeo4j_ip());
		mapNeo4j.put("neo4j_port", neo4j.getNeo4j_port());
		mapNeo4j.put("userName", (String) httpSession.getAttribute("UserName"));
		mapNeo4j.put("neo4j_user", neo4j.getNeo4j_user());
		mapNeo4j.put("neo4j_password", neo4j.getNeo4j_password());
		mapNeo4j.put("neo4j_editon", neo4j.getNeo4j_editon());
		mapNeo4j.put("neo4j_createtime", format.format(neo4j.getNeo4j_createtime()).toString());
		System.out.println(mapNeo4j);
		return mapNeo4j;
	}
	
	//Neo4j本地配置查看
	@RequestMapping(value="/Neo4jLocalLook")
	@ResponseBody
	public Map<String, String> neo4jLocalLook(@RequestParam("embedded_neo4j_id") Integer embedded_neo4j_id,Neo4jLocalModel neo4jLocal,HttpSession httpSession) {

		neo4jLocal = adminService.findNeo4jListLocalByNeo4jID(embedded_neo4j_id);
		Map<String,String> mapNeo4jLocal=new HashMap<>();
		mapNeo4jLocal.put("embedded_neo4j_id", String.valueOf(neo4jLocal.getEmbedded_neo4j_id()));
		mapNeo4jLocal.put("embedded_neo4j_name", neo4jLocal.getEmbedded_neo4j_name());
		mapNeo4jLocal.put("embedded_neo4j_path", neo4jLocal.getEmbedded_neo4j_path());
		mapNeo4jLocal.put("userName", (String) httpSession.getAttribute("UserName"));
		mapNeo4jLocal.put("embedded_neo4j_createtime", format.format(neo4jLocal.getEmbedded_neo4j_createtime()).toString());
		System.out.println(mapNeo4jLocal);
		return mapNeo4jLocal;
	}
	
	//Neo4j平台删除
	@RequestMapping(value="/Neo4jDel")
	@ResponseBody
	public String neo4jDel(@RequestParam("neo4j_id") Integer neo4j_id) {
		System.out.println(neo4j_id);
		int deleteResult = adminService.deleteNeo4jById(neo4j_id);
		if(deleteResult == 1) {
			return "1";
		}else {
			return "0";
		}
		
	}
	//Neo4j本地平台删除
	@RequestMapping(value="/Neo4jLocalDel")
	@ResponseBody
	public String neo4jLocalDel(@RequestParam("embedded_neo4j_id") Integer embedded_neo4j_id) {
		System.out.println(embedded_neo4j_id);
		int deleteLocalResult = adminService.deleteNeo4jLocalById(embedded_neo4j_id);
		if(deleteLocalResult == 1) {
			return "1";
		}else {
			return "0";
		}
	}
	
	//Neo4j本地平台修改
	@RequestMapping(value="/Neo4jSubmit")
	@ResponseBody
	public String neo4jSubmit(Neo4jModel neo4j,HttpSession httpSession) {
		
		neo4j.setUser_Id((long) httpSession.getAttribute("UserID"));
		System.out.println(neo4j);
		int EditResult = adminService.updateNeo4jById(neo4j);
		System.out.println("EditResult: " + EditResult);
		return String.valueOf(EditResult);
	}
	
	//Neo4j本地平台修改
	@RequestMapping(value="/Neo4jLocalSubmit")
	@ResponseBody
	public String neo4jLocalSubmit(Neo4jLocalModel neo4jLocal,HttpSession httpSession) {
		neo4jLocal.setUser_Id((long) httpSession.getAttribute("UserID"));
		System.out.println(neo4jLocal);
		int EditResult = adminService.updateNeo4jLocalById(neo4jLocal);
		System.out.println("EditResult: " + EditResult);
		return String.valueOf(EditResult);
	}
	
	//Neo4j平台添加提交
	@RequestMapping(value="/Neo4jAddSubmitPost")
	@ResponseBody
	public String neo4jAddSubmitPost(Neo4jModel neo4j) {
		neo4j.setNeo4j_createtime(new Date());
		int AddResult = adminService.insertNeo4j(neo4j);
		System.out.println("EditResult: " + AddResult);
		return String.valueOf(AddResult);
	}
	
	//Neo4j本地平台添加提交
	@RequestMapping(value="/Neo4jLocalAddSubmitPost")
	@ResponseBody
	public String neo4jLocalAddSubmitPost(Neo4jLocalModel neo4jLocal) {
		neo4jLocal.setEmbedded_neo4j_createtime(new Date());
		int AddResult = adminService.insertNeo4jLocal(neo4jLocal);
		System.out.println("EditResult: " + AddResult);
		return String.valueOf(AddResult);
	}
}
