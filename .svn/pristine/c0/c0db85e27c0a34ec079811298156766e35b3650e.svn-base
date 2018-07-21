package graph.server.controller;


import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;




@Controller
public class PageController {
	
	@RequestMapping(value="/loginRequest")
	public String loginRequest() {
		return "pages-sign-in";
	}
	
	@RequestMapping(value="/registerRequest")
	public String registerRequest() {
		return "pages-sign-up";
	}
	

	
	@RequestMapping(value="/db")//担保环界面
	public String dbPage() throws IOException  {

		return "page/db";
	}
	
	@RequestMapping(value="/admin")//更新数据库界面
	public String adminPage()  {
		
		return "page/admin";
	}
	
	@RequestMapping(value="/searchAccount")//账户搜索界面
	public String searchAcc() {
		return "page/searchAccount";
	}
	
	@RequestMapping(value="/searchCompany")//公司搜索界面
	public String searchCom() {
		return "page/searchCompany";
	}
	@RequestMapping(value="/index")//公司搜索界面
	public String index() {
		return "page/index.html";
	}
	@RequestMapping(value="/new_index")
	public String test1() {
		return "new/new_index";
	}
	@RequestMapping(value="/QueryModel")
	public String test2() {
		return "new/QueryModel";
	}
	@RequestMapping(value="/GraphBrowse")
	public String test3() {
		return "new/GraphBrowse";
	}
	@RequestMapping(value="/MiningModel")
	public String test4() {
		return "new/MiningModel";
	}
	@RequestMapping(value="/LoadData")
	public String test5() {
		return "new/LoadData";
	}
}
