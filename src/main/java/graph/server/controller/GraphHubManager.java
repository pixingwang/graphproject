package graph.server.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import graph.server.GraphHub;
import graph.server.model.*;

import graph.server.service.AdminService;

@Controller
public class GraphHubManager {
	
	@Autowired
    private AdminService adminService;
	@Autowired 
	GraphHub graphHub;
	private Format format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	//登陆
	@RequestMapping(value="/login",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,String> login(@RequestParam("username") String user_name,@RequestParam("password") String password, 
			Model model, HttpSession httpSession) {
		UserModel user = new UserModel();
		user.setUser_name(user_name);
		user.setPassword(password);
		Map<String,String> map=new HashMap<>();
		map.put("neo4jLocal", "0");
		map.put("neo4jFar", "0");
		
		UserModel adminRes = adminService.findByNameAndPassword(user);
		System.out.println("adminRes :"+adminRes);
		if (adminRes != null) {
			//map.put("UserName", adminRes.getUser_name());
			httpSession.setAttribute("UserName", adminRes.getUser_name());
			httpSession.setAttribute("UserID", adminRes.getUser_id());
			GraphubModel graphubActive = adminService.findGraphubActiveByUser_ID(adminRes.getUser_id());
			System.out.println(graphubActive);
			if(graphubActive.getNeo4j() == null && graphubActive.getNeo4jLocal() != null) {
				map.put("neo4jLocal", "1");
				map.put("neo4j", graphubActive.getNeo4jLocal().getEmbedded_neo4j_path());
			}
			if(graphubActive.getNeo4j() != null && graphubActive.getNeo4jLocal() == null) {
				map.put("neo4jFar", "1");
				map.put("neo4j_user", graphubActive.getNeo4j().getNeo4j_user());
				map.put("neo4j_password", graphubActive.getNeo4j().getNeo4j_password());
				map.put("neo4j_ip", graphubActive.getNeo4j().getNeo4j_ip());
				map.put("neo4j_port", graphubActive.getNeo4j().getNeo4j_port());
			}
			return map;
			//return "new/new_index";
		} else {
			map.put("error", "登陆出错，请重新登陆");
			return map;
		}
	}
	
	//注销用户
    @RequestMapping(value="/logout")  
    public String logout(HttpSession httpSession){
    	httpSession.removeAttribute("UserID");
    	httpSession.removeAttribute("UserName");
  
        return "pages-sign-in";  
          
    }  
	
	//注册用户
	@RequestMapping(value="/register",method=RequestMethod.POST)
	public String register(@RequestParam(value="password1") String ps, UserModel user, Model model, Map<String, Object> map, HttpSession httpSession) {
		
		//密码格式错误
		//model.addAttribute("error2", true);
		if(ps.equals(user.getPassword())) {
			int intsertUserResult = adminService.intsertUser(user);
			System.out.println("intsertUserResult:---" + intsertUserResult);
			UserModel adminRes = adminService.findByNameAndPassword(user);
			httpSession.setAttribute("NewUserName", adminRes.getUser_name());
			httpSession.setAttribute("NewUserID", adminRes.getUser_id());
			
			//设置默认neo4j本地路径，并插入到Neo4jLocal表中
			Neo4jLocalModel neo4jLocal = new Neo4jLocalModel();
			neo4jLocal.setEmbedded_neo4j_name("默认Neo4j");
			neo4jLocal.setUser_Id(adminRes.getUser_id());
			neo4jLocal.setEmbedded_neo4j_path("E:\\neo4j.txt");
			neo4jLocal.setEmbedded_neo4j_createtime(new Date());
			int resultInsertNeo4jLocal = adminService.insertNeo4jLocal(neo4jLocal);
			//插入到GrapHub表中
			GraphubModel graphub = new GraphubModel();
			graphub.setGraphub_name("默认GrapHub");
			graphub.setUser_Id(adminRes.getUser_id());
			List<Neo4jLocalModel> newneo4jLocal = adminService.findNeo4jListLocalByUserID(adminRes.getUser_id());
			graphub.setNeo4jLocal(newneo4jLocal.get(0));
			graphub.setGraphub_createtime(new Date());
			graphub.setState(1);
			int resultInsertGrapHub = adminService.insertGraphub(graphub);
			
			if(resultInsertNeo4jLocal == 1 && resultInsertGrapHub == 1) {
				return "pages-sign-in";
			}else {
				model.addAttribute("error3", true);
				return "pages-sign-up";
			}
			
		}
		else {
			model.addAttribute("error1", true);
			return "pages-sign-up";
		}
	}
	//GrapHub连接
	@RequestMapping("/GraphubConnect")
	public String graphubConnect(Neo4jLocalModel neo4jLocal, Model model,HttpSession httpSession) throws IOException {
		
		neo4jLocal.setUser_Id((long) httpSession.getAttribute("UserID"));
		Neo4jLocalModel Neo4jLocalConnect = adminService.findNeo4jLocalByEmbedded_neo4j_path(neo4jLocal);
		System.out.println("path:"+ Neo4jLocalConnect);
		Neo4jLocalConnect.getEmbedded_neo4j_id();
		GraphubModel graphub = new GraphubModel();
		graphub.setUser_Id((long) httpSession.getAttribute("UserID"));
		graphub.setNeo4jLocal(Neo4jLocalConnect);
		
		GraphubModel GraphubConnect = adminService.findGraphubConnectByPath(graphub);
		System.out.println("Graphub:"+ GraphubConnect);
		graphHub.init(graphub);
		graphHub.getEmbeddeddriver().getGraphDatabaseService();
		return "new/new_index";
	}
	//GrapHub平台状态改变
	@RequestMapping("/GraphubStateChange/{id}")
	public String graphubStateChange(@PathVariable(value="id") Integer graphub_id, GraphubModel graphub, Model model,HttpSession httpSession) throws IOException {
		graphHub.getEmbeddeddriver().getGraphDatabaseService().shutdown();
		long user_Id = (long) httpSession.getAttribute("UserID");
		graphub.setUser_Id(user_Id);
		graphub.setGraphub_id(graphub_id);
		//查询该id是否激活
		int l = adminService.GraphubState1Or0(graphub);
		System.out.println("l:----"+l);
		if(l == 1) {
			//状态归0
			int StateChangeTo0 = adminService.GraphubStateChangeTo0(user_Id);
			System.out.println("状态归0结果:---" + StateChangeTo0);
		}else {
			//状态归0
			int StateChangeTo0 = adminService.GraphubStateChangeTo0(user_Id);
			System.out.println("状态归0结果:---" + StateChangeTo0);
			int StateChangeTo1 = adminService.GraphubStateChangeTo1(graphub);
			System.out.println("状态改变结果:---" + StateChangeTo1);
		}
		graphub=adminService.findGraphubActiveByUser_ID(user_Id);
		System.out.println("Graphub:"+ graphub);
		graphHub.init(graphub);
		graphHub.getEmbeddeddriver().getGraphDatabaseService();
		return "redirect:/GraphHubManager";
	}
	//GrapHub平台列表显示
	@RequestMapping(value="/GraphHubManager")
	public String graphHubManager(GraphubModel graphub, Model model,HttpSession httpSession) {
		long user_Id = (long) httpSession.getAttribute("UserID");
		graphub.setUser_Id(user_Id);
		List<GraphubModel> GraphubList = adminService.findGraphubListByUserID(user_Id);
		List<Neo4jModel> Neo4jList = adminService.findNeo4jListByUserID(user_Id);
		List<GraphXModel> GraphXList = adminService.findGraphXListByUserID(user_Id);
		List<Neo4jLocalModel> Neo4jListLocal = adminService.findNeo4jListLocalByUserID(user_Id);
		model.addAttribute("newsList", GraphubList);
		model.addAttribute("Neo4jList", Neo4jList);
		System.out.println(Neo4jList);
		model.addAttribute("GraphXList", GraphXList);
		model.addAttribute("Neo4jListLocal", Neo4jListLocal);
		System.out.println(GraphXList);
		return "new/GraphHubManager";
	}
	//GrapHub平台删除
	@RequestMapping(value="/GraphubDelete/{id}")
	public String graphubDelete(@PathVariable(value="id") Integer graphub_id) {
		System.out.println("删除来了。。。。。。。。。。");
		int deleteResult = adminService.deleteById(graphub_id);
		System.out.println("deleteResult: " + deleteResult);
		return "redirect:/GraphHubManager";
	}
	
	//GrapHub平台修改跳转
	@RequestMapping(value="/GraphubEditInput/{id}")
	public String graphubEditInput(@PathVariable(value="id") Integer graphub_id,GraphubModel graphub,Model model,HttpSession httpSession) {
		long user_Id = (long) httpSession.getAttribute("UserID");
		List<Neo4jModel> allNeo4j = adminService.findAllNeo4j(user_Id);
		List<GraphXModel> allGraphX = adminService.findAllGraphX(user_Id);
		List<Neo4jLocalModel> allNeo4jLocal = adminService.findAllNeo4jLocal(user_Id);
		model.addAttribute("allNeo4j", allNeo4j);
		model.addAttribute("allGraphX", allGraphX);
		model.addAttribute("allNeo4jLocal", allNeo4jLocal);
		
		
		graphub = adminService.findGraphubListByGraphubID(graphub_id);
		graphub.setGraphub_id(graphub_id);
		model.addAttribute("graphub", graphub);
		String G_name = "";
		if(graphub.getNeo4j() == null) {
			model.addAttribute("neo4j_name", G_name);
		}else {
			model.addAttribute("neo4j_name", graphub.getNeo4j().getNeo4j_name());
		}
		if(graphub.getGraphX() == null) {
			model.addAttribute("graphX_name", G_name);
		}else {
			model.addAttribute("graphX_name", graphub.getGraphX().getGraphX_name());
		}
		System.out.println("跳转到修改页面。。。。。。。。。。");
		return "new/GraphHubEdit";
	}
	
	//GrapHub平台修改提交
	@RequestMapping(value="/GraphubPost")
	public String graphubPost(@RequestParam(value="graphub_id") String graphub_id,
							  @RequestParam(value="graphub_name") String graphub_name,
		 					  @RequestParam(value="user_Id") String user_Id,
 							  @RequestParam(value="neo4j.neo4j_id") String neo4j_id,
 							  @RequestParam(value="neo4jLocal.embedded_neo4j_id") String embedded_neo4j_id,
							  @RequestParam(value="graphX.graphX_id") String graphX_id) {
		
		GraphubModel graphub = new GraphubModel();
		graphub.setGraphub_id(Integer.valueOf(graphub_id));
		graphub.setGraphub_name(graphub_name);
		graphub.setUser_Id(Integer.valueOf(user_Id));
		if(neo4j_id.equals("--请选择远程连接--")) {
			Neo4jLocalModel neo4jLocal = new Neo4jLocalModel();
			neo4jLocal.setEmbedded_neo4j_id(Integer.valueOf(embedded_neo4j_id));
			graphub.setNeo4jLocal(neo4jLocal);
		}
		if(embedded_neo4j_id.equals("--请选择本地连接--")) {
			Neo4jModel neo4j = new Neo4jModel();
			neo4j.setNeo4j_id(Integer.valueOf(neo4j_id));
			graphub.setNeo4j(neo4j);
		}
		GraphXModel graphX = new GraphXModel();
		graphX.setGraphX_id(Integer.valueOf(graphX_id));
		graphub.setGraphX(graphX);
	
		int EditResult = adminService.updateGraphubById(graphub);
		System.out.println("EditResult: " + EditResult);
		return "redirect:/GraphHubManager";
	}
	//GrapHub平台添加跳转
	@RequestMapping(value="/GraphubAddInput")
	public String graphubAddInput(Model model, HttpSession httpSession) {
		long user_Id = (long) httpSession.getAttribute("UserID");
		List<Neo4jModel> allNeo4j = adminService.findAllNeo4j(user_Id);
		List<GraphXModel> allGraphX = adminService.findAllGraphX(user_Id);
		List<Neo4jLocalModel> allNeo4jLocal = adminService.findAllNeo4jLocal(user_Id);
		model.addAttribute("allNeo4j", allNeo4j);
		model.addAttribute("allGraphX", allGraphX);
		model.addAttribute("allNeo4jLocal", allNeo4jLocal);
		System.out.println("跳转到添加页面。。。。。。。。。。");
		return "new/GraphHubAdd";
	}
	
//	//GrapHub平台添加提交
//	@RequestMapping(value="/GraphubAddPost")
//	public String graphubAddPost(Graphub graphub) {
//		System.out.println(graphub);
//		graphub.setGraphub_createtime(new Date());
//		graphub.setState(0);
//		int AddResult = adminService.insertGraphub(graphub);
//		System.out.println("AddResult: " + AddResult);
//		return "redirect:/GraphHubManager";
//	}
	
	//GrapHub平台添加提交
	@RequestMapping(value="/GraphubAddPost")
	public String graphubAddPost(@RequestParam(value="graphub_name") String graphub_name,
								 @RequestParam(value="user_Id") String user_Id,
						 		 @RequestParam(value="neo4j.neo4j_id") String neo4j_id,
					 		 	 @RequestParam(value="neo4jLocal.embedded_neo4j_id") String embedded_neo4j_id,
				 		 	 	 @RequestParam(value="graphX.graphX_id") String graphX_id){
		System.out.println(graphub_name);
		System.out.println(user_Id);
		System.out.println(neo4j_id);
		System.out.println(embedded_neo4j_id);
		System.out.println(graphX_id);
		GraphubModel graphub = new GraphubModel();
		graphub.setGraphub_name(graphub_name);
		graphub.setUser_Id(Integer.valueOf(user_Id));
		if(neo4j_id.equals("--请选择远程连接--")) {
			Neo4jLocalModel neo4jLocal = new Neo4jLocalModel();
			neo4jLocal.setEmbedded_neo4j_id(Integer.valueOf(embedded_neo4j_id));
			graphub.setNeo4jLocal(neo4jLocal);
		}
		if(embedded_neo4j_id.equals("--请选择本地连接--")) {
			Neo4jModel neo4j = new Neo4jModel();
			neo4j.setNeo4j_id(Integer.valueOf(neo4j_id));
			graphub.setNeo4j(neo4j);
		}
		GraphXModel graphX = new GraphXModel();
		graphX.setGraphX_id(Integer.valueOf(graphX_id));
		graphub.setGraphX(graphX);
		
		graphub.setGraphub_createtime(new Date());
		graphub.setState(0);
		int AddResult = adminService.insertGraphub(graphub);
		System.out.println("AddResult: " + AddResult);
		return "redirect:/GraphHubManager";
	}
	//GrapHub查看GraphX
		@RequestMapping(value="/GraphubToGraphXLook")
		@ResponseBody
		public Map<String,String> graphubToGraphXLook(@RequestParam("graphub_id") Integer graphub_id,GraphubModel graphub,Model model,HttpSession httpSession) {

			graphub = adminService.findGraphubListByGraphubID(graphub_id);
			Map<String,String> map=new HashMap<>();
			map.put("graphX_id", String.valueOf(graphub.getGraphX().getGraphX_id()));
			map.put("graphX_name", graphub.getGraphX().getGraphX_name());
			map.put("graphX_ip", graphub.getGraphX().getGraphX_ip());
			map.put("graphX_port", graphub.getGraphX().getGraphX_port());
			map.put("userName", (String) httpSession.getAttribute("UserName"));
			map.put("graphX_version", graphub.getGraphX().getGraphX_version());
			map.put("graphX_algorithm", graphub.getGraphX().getGraphX_algorithm());
			map.put("graphX_operation", graphub.getGraphX().getGraphX_operation());
			map.put("graphX_createtime", format.format(graphub.getGraphX().getGraphX_createtime()).toString());
			System.out.println(map);
			return map;
		}
		//GrapHub查看Neo4j,Neo4jLocal
		@RequestMapping(value="/GraphubToNeo4jAndLocalLook")
		@ResponseBody
		public Map<String,String> graphubToNeo4jAndLocalLook(@RequestParam("graphub_id") Integer graphub_id,GraphubModel graphub,HttpSession httpSession) {

			graphub = adminService.findGraphubListByGraphubID(graphub_id);
			Map<String,String> map=new HashMap<>();
			map.put("Neo4jOrNeo4jLocal", "0");
			if(graphub.getNeo4j() != null) {
				map.put("neo4j_id", String.valueOf(graphub.getNeo4j().getNeo4j_id()));
				map.put("neo4j_name", graphub.getNeo4j().getNeo4j_name());
				map.put("neo4j_ip", graphub.getNeo4j().getNeo4j_ip());
				map.put("neo4j_port", graphub.getNeo4j().getNeo4j_port());
				map.put("userName", (String) httpSession.getAttribute("UserName"));
				map.put("neo4j_user", graphub.getNeo4j().getNeo4j_user());
				map.put("neo4j_password", graphub.getNeo4j().getNeo4j_password());
				map.put("neo4j_editon", graphub.getNeo4j().getNeo4j_editon());
				map.put("neo4j_createtime", format.format(graphub.getNeo4j().getNeo4j_createtime()).toString());
			}else {
				map.put("Neo4jOrNeo4jLocal", "1");
				map.put("embedded_neo4j_id", String.valueOf(graphub.getNeo4jLocal().getEmbedded_neo4j_id()));
				map.put("embedded_neo4j_name", graphub.getNeo4jLocal().getEmbedded_neo4j_name());
				map.put("embedded_neo4j_path", graphub.getNeo4jLocal().getEmbedded_neo4j_path());
				map.put("userName", (String) httpSession.getAttribute("UserName"));
				map.put("embedded_neo4j_createtime", format.format(graphub.getNeo4jLocal().getEmbedded_neo4j_createtime()).toString());
			}
			System.out.println(map);
			return map;
		}
}
