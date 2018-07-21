/**
 * 
 */
// 显示具体关系


function showInfo(){
	
	var nodeSum = layout.graph.nodes.length;
	var linkSum = layout.graph.links.length;
	
	var nodeLabel = [];
	var nlTemp = [];
	var linkLabel = [];
	var llTemp = [];
	
	for(var i=0;i<nodeSum;i++){
		var label = layout.graph.nodes[i].label;
		var index = jQuery.inArray(label,nlTemp);
		if(index==-1){
			nlTemp.push(label);
			nodeLabel.push({label:label,count:1});
		}else{
			nodeLabel[index].count++;
		}
	}
	
	for(var i=0;i<linkSum;i++){
		var label = layout.graph.links[i].label;
		var index = jQuery.inArray(label,llTemp);
		if(index==-1){
			llTemp.push(label);
			linkLabel.push({label:label,count:1});
		}else{
			linkLabel[index].count++;
		}
	}
	
	var nodeHtml = "";
	if(nodeSum>0){
		nodeHtml = "节点：&nbsp&nbsp";
		for(var i in nodeLabel){
			var t = nodeLabel[i];
			nodeHtml += t.label+" :"+t.count+"个&nbsp&nbsp&nbsp&nbsp";
		}
	}
	
	var linkHtml = "";
	if(linkSum>0){
		linkHtml = "关系:&nbsp&nbsp";
		for(var i in linkLabel){
			var t = linkLabel[i];
			linkHtml += t.label+" :"+t.count+"个&nbsp&nbsp&nbsp&nbsp";
		}
	}
	var initHtml = (nodeSum>0 ? (nodeSum+" 节点&nbsp&nbsp&nbsp&nbsp"):"")+(linkSum>0 ? (linkSum+" 关系&nbsp&nbsp&nbsp&nbsp"):"")+(linkSum+nodeSum>0 ? ("其中包括：&nbsp&nbsp&nbsp&nbsp"):"");
	
	var insertHtml = initHtml+nodeHtml+linkHtml;
	$("#nodeSize").html(insertHtml);
	
}




$(document).ready(function(){
	$('#kzbtn').click(function(){
		 $('#kzchain').fadeIn(1000);
		 $('#dbchain').fadeOut(0);
		 $('#jychain').fadeOut(0);
		 
	 });
	 $('#dbbtn').click(function(){
		 $('#dbchain').fadeIn(1000);
		 $('#jychain').fadeOut(0);
		 $('#kzchain').fadeOut(0);
		 $("#list").hide();
		 
	 });
	$('#jybtn').click(function(){
	  $('#jychain').fadeIn(1000);
	  $('#kzchain').fadeOut(0);
	  $('#dbchain').fadeOut(0);
	 });	

	$('#zybtn,#exprotbtn,#search1,#zddbtn,#ydzcbtn,#hlzdbrbtn,#fdcbtn,#zqqhbtn,#frdbbtn,#ygbtn,#yjhxbtn,#topdbol,#topdbom').click(function(){
		  $('#jychain').fadeOut(0);
		  $('#kzchain').fadeOut(0);
		  $('#dbchain').fadeOut(0);
		  $("#list").hide();
		  $("#center").hide();
		 });
	$("#submitJYbtn,#search1").click(function(){
		$("#jyfx").fadeOut(0);
	});
	$("#listView,#listView1").click(function(){
		$("#list").show();
		$('#dbchain').fadeOut(0);
		$("#center").hide();
		
	});
	$("#closeList").click(function(){
		$("#list").hide();
//		$("#listTable").removeClass("searchList");
	});
	
	$("#centerView").click(function(){
		$("#center").show();
		$('#dbchain').fadeOut(0);
		$("#list").hide();
	});
	$("#closeCenter").click(function(){
		$("#center").hide();
	})
});


$(document).ready(function(){
	// 点击交易分析的提交按钮进行的判断
	  $("#submitJYbtn").click(function(){
		  var min=$("#minJYM").val();
		  var max=$('#maxJYM').val();
		  if(isNaN(min)||isNaN(max)){
		  		alert("请输入数字");
		  }
		  else if(parseInt(min)>parseInt(max)){
			  	alert("所输入的交易金额的最小值大于最大值，请重新输入");
		  }
		  else{
//			  $("#submitJYbtn").submit(searchJYByFD());
		  }
	  });
	// 点击导航栏上的搜索按钮进行的判断
	  $("#search1").click(function(){
		  var t=$("#searchAcount").val();
		  if(t==""){
			  alert("请输入搜索内容");
		  }
		  else{
			  $("#search1").submit(search());
		  }
	  });
	// 点击担保圈的提交按钮进行的判断
	  $("#submitDBbtn").click(function(){
		  var t=$("#searchDBchain").val();
		  if(t==""){
			  alert("请输入节点数目");
		  }
		  else if(isNaN(t)){
		  		alert("请输入数字");
		  }
		  else{
			  $("#submitDBbtn").submit();//
		  }
	  });   
});


$(document).ready(function(){
	// 关系筛选动态显示
	  $("#shaixuanbtn").click(function(){
		  var items = document.getElementsByName("realtion");
		  for(var i=0;i<11;i++){
		  	items[i].style.display = 'block';
		  }
		  var arr = new Array(6);
		  var r =d3.select("g").selectAll(".link").size()-d3.select("g").selectAll(".link.shadow").size();
		  arr[0]=d3.select("g").selectAll(".link.DB").size();
		  arr[1]=d3.select("g").selectAll(".link.JY").size();
		  arr[2]=d3.select("g").selectAll(".link.DK").size();
		  arr[3]=d3.select("g").selectAll(".link.DY").size();
		  arr[4]=d3.select("g").selectAll(".link.ZDB").size();
		  arr[5]=d3.select("g").selectAll(".link.GL").size();
	
	     for(var i=0;i<6;i++){
	    	 if(arr[i]!=0){// 去掉多余的分隔符
	    		 var sum=0;
	    		 
	    		 for(var j=0;j<=i;j++){
	    			 sum+=arr[j];
	    		 }
	    		 console.log("i="+i+"  sum="+sum+" n="+r);
	    		 if(r==sum && i<5)// 如果没有其它关系，去掉多余的分隔符
	    			 items[2*i+1].style.display='none';
	    	 }
	    	 else{
	    	 	items[2*i].style.display='none';
				if(i<5){
	    	 	items[2*i+1].style.display='none';}
	    	 }
	     } 
	  });

	  $("#closeJYfx").click(function(){
	  	$("#jyfx").hide();
	  });
	  
	// 节点筛选动态显示
	  $("#nodeshaixuanbtn").click(function(){
		  var items = document.getElementsByName("node");
		  for(var i=0;i<7;i++){
		  	items[i].style.display = 'block';
		  }
		  var arr = new Array(4);
		  var r =d3.select("g").selectAll(".node").size();
		  arr[0]=d3.select("g").selectAll(".node.B").size();
		  arr[1]=d3.select("g").selectAll(".node.P").size();
		  arr[2]=d3.select("g").selectAll(".node.C").size();
		  arr[3]=d3.select("g").selectAll(".node.Com").size()+d3.select("g").selectAll(".node.Per").size();
	
	     for(var i=0;i<4;i++){
	    	 if(arr[i]!=0){// 去掉多余的分隔符
	    		 var sum=0;
	    		 
	    		 for(var j=0;j<=i;j++){
	    			 sum+=arr[j];
	    		 }
	    		 console.log("i="+i+"  sum="+sum+" n="+r);
	    		 if(r==sum && i<3)// 如果没有其它关系，去掉多余的分隔符
	    			 items[2*i+1].style.display='none';
	    	 }
	    	 else{
	    	 	items[2*i].style.display='none';
				if(i<3){
	    	 	items[2*i+1].style.display='none';}
	    	 }
	     } 
	  });

	});

// 侧边栏显示
$(function(){
	$("#showPanel").click(function(){
		$("#showPanel").animate({left:"-100%"},"slow");
		$("#nodePanel").animate({left:"0",opacity: 'show'},"slow");
		
		var w1 = $("#testPanel").css("height");
		var w2 = $("body").css("height");
		if(parseInt(w1)/parseInt(w2)>0.8){
/*
 * console.log("panel="+$("#testPanel").css("height"));
 * console.log("body="+$("body").css("height"));
 */
			$("#testPanel").css("height","85%");
		}
	
	});
	$("#closePanel").click(function(){
		$("#nodePanel").animate({left:"-100%"},"slow");
		$("#showPanel").animate({left:"-40%"},"slow");// 与显示节点信息面板的宽度(15%)相同
		$("#nodeInfo").empty();
	});
	
	/*
	 * $("#listView").click(function(){ $("#list").animate({right:"0%",opacity:
	 * 'show'},"slow"); });
	 * $("#closeList,#dbbtn,#topdbol,#topdbom").click(function(){
	 * $("#list").animate({right:"-25%"},"fast"); });
	 */
});

// 日期选择菜单的开关
function change(n){
	if($("#dateMenu")[0].style.display=="block") n=0;
	$("#dateMenu")[0].style.display=n?'block':'none';
}

// 日期转换
function dateTranslate(t){
	return t.replace(/^(\d{4})(\d{2})(\d{2})$/, "$1/$2/$3");
}

// 表格
var table1,table2;
function showTable(tag,flag){
	   $("#results,#dbtjTable").dataTable().fnClearTable();
	   $("#results,#dbtjTable").dataTable().fnDestroy();

	   jQuery.fn.dataTableExt.oSort['chinese-asc'] = function(x,y){
				return x.localeCompare(y,'zh');
			};		
	   jQuery.fn.dataTableExt.oSort['chinese-desc'] = function(x,y){
		    return y.localeCompare(x,'zh');
	   	};
	   jQuery.fn.dataTableExt.aTypes.push(function(sData){
		    var reg =/^[\u4e00-\u9fa5]{0,}$/;
		    if(reg.test(sData)){
		    	return 'chinese';
		    	}
		    return null;
	   });
	   var table;
	   var isTrue = false;
	   if(tag == 1){// 交易关系的表格
		   console.log("交易关系");
		   isTrue = true;
		   
		   var jygx = [];
		   var count = 0;
		   var len = layout.graph.links.length;
		   for(var i = 0;i < len; i++){
		   		if(layout.graph.links[i].label == 'JY')
		   			jygx[count++] = layout.graph.links[i];
		   }
		
		   table=$("#results").DataTable({
	            ordering: true,// 是否启用排序
	            searching: true,// 搜索
	            autoWidth:false,
	            "data":jygx,
	            columns:[{
	            	"data":null,
	            	"orderable":false
	            },{
	            	"data":"jyjgmc"// 交易机构名称
	            },{
	            	"data":"jyzh"// 交易账号
	            },{
	            	"data":"jyhm"// 交易户名
	            },{
	            	"data":"yxjgdm"// 银行机构代码
	            },{
	            	"data":"dfjgmc"// 对方机构名称
	            },{
	            	"data":"dfhm"// 对方户名
	            },{
	            	"data":"jyje", // 交易金额
	            	"render":function(data){
	            		return data/10000 + "万元";
	            	}   
	            },{
	            	"data":"jyrq"// 交易日期
	            },{
	            	"data":"zhye",// 账户余额
	            	"render":function(data){
	            		return data/10000 + "万元";
	            	}
	            }],
	            "columnDefs": [{
	            	"aTypes": "chinese-asc", 
	            	"aTargets": [0,1,2,3,4,5,6,7,8,9] 
	            }],
	            language: {
	                lengthMenu: '显示 <select style="height: 30px;width: 50px">'+
	                '<option value="5">5</option>'+
	                '<option value="10">10</option>'+
	                '<option value="20">20</option>'+
	                '<option value="30">30</option>'+
	                '<option value="40">40</option>'+
	                '<option value="50">50</option>'+
	                '<option value="-1">不限</option>'+
	                '</select>条数据',
	                search: '<span class="label label-info">搜索：</span>',
	
	                paginate: {// 分页的样式内容。
	                    previous: "上一页",
	                    next: "下一页",
	                    first: "首页",
	                    last: "末页"
	                },
	                zeroRecords: "没有内容",// table tbody内容为空时，tbody的内容。
	                // 下面三者构成了总体的左下角的内容。
	                info: "总共_PAGES_ 页，当前显示第_START_条到第 _END_条，筛选之后得到 _TOTAL_ 条，初始共_MAX_ 条 ",// 左下角的信息显示，大写的词为关键字。
	                infoEmpty: "0条记录",// 筛选为空时左下角的显示。
	                infoFiltered: ""// 筛选之后的左下角筛选提示，
	            },
	            paging:true,
	            pagingType: "full_numbers",// 分页样式的类型
	            lengthChange: true,
	            "dom":
	        		"<'row'<'col-xs-1'l><'col-xs-5'B><'col-xs-6'f>>" +
	        		"<'row'<'col-xs-12't>>"+
	        		"<'row'<'col-xs-6'i><'col-xs-6'p>>",
	            buttons: [{'extend':'excel','text':'导出','className':'btn btn-default','title':'交易信息','exportOptions':{'columns':[1,2,3,4,5,6,7,8,9]}}],
	            createdRow : function ( row, data, index ) {
	                $('td', row).css('font-weight',"bold").css("text-align","center");
	            }
	      });

	   }else if(tag == 2){// 担保环统计的表格
		   console.log("担保环统计");
		   isTrue = false;
		   // var dbdata =
			// {"DbCircleInfo":[{"nodesNum":2,"count":3},{"nodesNum":2,"count":3},{"nodesNum":2,"count":3},{"nodesNum":2,"count":3}]};
		   table=$("#dbtjTable").DataTable({
	            ordering: true,// 是否启用排序
	            searching: true,// 搜索
	            autoWidth:false,
	            destroy:true,
	            "data":dbdata.DbCircleInfo,
	            columns:[/*
							 * { "data":null, "orderable":false },
							 */{
	            	"data":"nodesNum",// 担保环长度
	            	"render":function(data){
	            		return data;
	            	}
	            },{
	            	"data":"count"// 担保圈数量
	            }],
	            "columnDefs": [{
	            	"aTypes": "chinese-asc", 
	            	"aTargets": [0,1] 
	            }],
	            language: {
	                lengthMenu: '每页显示 <select style="height: 30px;width: 50px">'+
	                '<option value="1">1</option>'+
	                '<option value="5">5</option>'+
	                '<option value="10">10</option>'+
	                '<option value="20">20</option>'+
	                '<option value="30">30</option>'+
	                '<option value="40">40</option>'+
	                '<option value="50">50</option>'+
	                '<option value="-1">不限</option>'+
	                '</select>条数据',
	                paginate: {// 分页的样式内容。
	                    previous: "上一页",
	                    next: "下一页",
	                    first: "首页",
	                    last: "末页"
	                },
	                zeroRecords: "没有内容",// table tbody内容为空时，tbody的内容。
	                // 下面三者构成了总体的左下角的内容。
	                info: "共_PAGES_ 页，_MAX_ 条，当前显示_START_到 _END_条 ",// 左下角的信息显示，大写的词为关键字。
	                infoEmpty: "0条记录",// 筛选为空时左下角的显示。
	                infoFiltered: ""// 筛选之后的左下角筛选提示，
	            },
	            paging:true,
	            pagingType: "full_numbers",// 分页样式的类型
	            lengthChange: true,
	            "dom":
	        		"<'row'<'col-xs-1'l><'col-xs-5'><'col-xs-6'i>>" +
	        		"<'row'<'col-xs-12't>>"+
	        		"<'row'<'col-xs-12'p>>",
	            buttons: [{'extend':'excel','text':'导出','className':'btn btn-default','title':'担保环数目统计','exportOptions':{'columns':[1,2]}}],
	            createdRow : function ( row, data, index ) {
	                $('td', row).css('font-weight',"bold").css("text-align","center");
	            }
	      });
	   }else if(tag == 3){// 担保详细数据
		   console.log("担保详细数据");
		   
		   isTrue = true;
		   
		   var linksData = layout.graph.links;
		  
		   var dbgx = [];
		   var count = 0;
		   var len = layout.graph.links.length;
		   for(var i = 0;i < len; i++){
			   if(linksData[i].label == 'DB' || linksData[i].label == 'ZDB'){
			   		if(linksData[i].label == 'DB'){
			   			
			   			linksData[i].zdbje = linksData[i].dbzje;
			   			linksData[i].zdbcs = 1;
			   		}	
		   			linksData[i].theStart = linksData[i].source.khxm || linksData[i].source.khmc;
		   			linksData[i].theEnd = linksData[i].target.khxm || linksData[i].target.khmc;
			   		dbgx[count++] = linksData[i];
			   }
		   }
		   console.log(dbgx);
		   table=$("#results").DataTable({
	            ordering: true,// 是否启用排序
	            searching: true,// 搜索
	            autoWidth:false,
	            "data":dbgx,
	            columns:[{
	            	"data":null,
	            	"orderable":false
	            },{
	            	"data":"theStart"// 担保主体
	            },{
	            	"data":"label",// 担保类型
	            	"render":function(data){
	            		return maprel(data);
	            	}         	
	            },{
	            	"data":"theEnd"// 担保客体
	            },{
	            	"data":"zdbje",// 担保金额
	            	"render":function(data){
	            		return data/10000 + "万元";
	            	}   
	            },{
	            	"data":"zdbcs"// 担保次数
	            }],
	            "columnDefs": [{
	            	"aTypes": "chinese-asc", 
	            	"aTargets": [0,1,2,3,4,5] 
	            }],
	            language: {
	                lengthMenu: '显示 <select style="height: 30px;width: 50px">'+
	                '<option value="5">5</option>'+
	                '<option value="10">10</option>'+
	                '<option value="20">20</option>'+
	                '<option value="30">30</option>'+
	                '<option value="40">40</option>'+
	                '<option value="50">50</option>'+
	                '<option value="-1">不限</option>'+
	                '</select>条数据',
	                search: '<span class="label label-info">搜索：</span>',
	
	                paginate: {// 分页的样式内容。
	                    previous: "上一页",
	                    next: "下一页",
	                    first: "首页",
	                    last: "末页"
	                },
	                zeroRecords: "没有内容",// table tbody内容为空时，tbody的内容。
	                // 下面三者构成了总体的左下角的内容。
	                info: "总共_PAGES_ 页，当前显示第_START_条到第 _END_条，筛选之后得到 _TOTAL_ 条，初始共_MAX_ 条 ",// 左下角的信息显示，大写的词为关键字。
	                infoEmpty: "0条记录",// 筛选为空时左下角的显示。
	                infoFiltered: ""// 筛选之后的左下角筛选提示，
	            },
	            paging:true,
	            pagingType: "full_numbers",// 分页样式的类型
	            lengthChange: true,
	            "dom":
	        		"<'row'<'col-xs-1'l><'col-xs-5'B><'col-xs-6'f>>" +
	        		"<'row'<'col-xs-12't>>"+
	        		"<'row'<'col-xs-6'i><'col-xs-6'p>>",
	            buttons: [{'extend':'excel','text':'导出','className':'btn btn-default','title':'担保环','exportOptions':{'columns':[1,2,3,4,5]}}],
	            createdRow : function ( row, data, index ) {
	                $('td', row).css('font-weight',"bold").css("text-align","center");
	            }
	      });
	   }else if(tag==4){// 列表显示
		   console.log("列表显示");
		   var list_data;
		   if(flag){
			   $("#listTable").DataTable().destroy();
			   $("#listTable").addClass("searchList");
			   list_data = search_list;
		   }else{
			   list_data = dblist;
		   }
		   if($.fn.DataTable.isDataTable( '#listTable' )){
			   if($("#listTable").hasClass("searchList")){
				   $("#listTable").DataTable().destroy();
				   $("#listTable").removeClass("searchList");
			   }else{
				   console.log("已经初始化了");
				   return;
			   }			   
		   }
		   
		   console.log("开始初始化");
		   isTrue = true;// 延迟渲染后应为false
		   		   
		   var start = new Date();
		   table1=$("#listTable").DataTable({
	            ordering: false,// 是否启用排序
	            searching: true,// 搜索
	            autoWidth:false,
	            // deferRender: true,//延迟渲染数据
	            "data":list_data,
	            columns:[{
	            	"data":null,
	            	"orderable":false
	            },/*
					 * {//延迟渲染时启用，无序号 "data":"count", "orderable":false,
					 * "render":function(){return null;} },
					 */{
	            	"data":"itemName"// 担保圈名字
	            }],
	            "columnDefs": [{
	            	"types": "chinese-asc", 
	            	"targets": [1] 
	            },{ 
	            	"width": "35px", "targets": [0]
	            }],
	            language: {
	                lengthMenu: '显示 <select style="height: 30px;width: 50px">'+
	                '<option value="5">5</option>'+
	                '<option value="10">10</option>'+
	                '</select>条数据',
	                search: '<span class="label label-info">模糊查询：</span>',
	
	                paginate: {// 分页的样式内容。
	                    previous: "上页",
	                    next: "下页",
	                    first: "首页",
	                    last: "末页"
	                },
	                zeroRecords: "没有内容",// table tbody内容为空时，tbody的内容。
	                // 下面三者构成了总体的左下角的内容。
	                info: "共_PAGES_ 页，_MAX_ 条。当前显示_START_~_END_条，筛选之后 _TOTAL_ 条 ",// 左下角的信息显示，大写的词为关键字。
	                infoEmpty: "0条记录",// 筛选为空时左下角的显示。
	                infoFiltered: ""// 筛选之后的左下角筛选提示，
	            },
	            paging:true,
	            pagingType: "simple_numbers",// 分页样式的类型
	            lengthChange: true,
	            "dom":
	        		"<'row'<'col-xs-5'l><'col-xs-7'f>>" +
	        		"<'row'<'col-xs-12't>>"+
	        		// "<'row'<'col-xs-12'i>>"+//延迟渲染无法添加序号，需要此说明
	        		"<'row'<'col-xs-12'p>>",
	            // buttons: [{'extend':'excel','text':'导出','className':'btn
				// btn-default','title':'担保环','exportOptions':{'columns':[1,2,3,4,5]}}],
	            createdRow : function ( row, data, index ) {
	                $('td', row).css('font-weight',"normal").css("text-align","left");
		            $('td:eq(1)', row).html(data.itemName+"&nbsp;<button type='button' class='btn btn-default btn-sm'><span class='glyphicon glyphicon-plus'></span></button>");
	            }
	      });
		   var end1 = new Date();
	   }else if(tag==5){// 中心性显示
		   console.log("中心性显示");
		   if(/* $("#listTable").hasClass("dataTable") */$.fn.DataTable.isDataTable( '#centerTable' )){
			   console.log("已经初始化了");
			   return;
		   }
		   console.log("开始初始化");
		   isTrue = true;// 延迟渲染后应为false
		   
		   var start = new Date();
		   table2=$("#centerTable").DataTable({
	            ordering: false,// 是否启用排序
	            searching: true,// 搜索
	            autoWidth:false,
	            // deferRender: true,//延迟渲染数据
	            "data":centerList.DegreeCentralityListInfo,
	            columns:[{
	            	"data":null,
	            	"orderable":false
	            },/*
					 * {//延迟渲染时启用，无序号 "data":"count", "orderable":false,
					 * "render":function(){return null;} },
					 */{
	            	"data":"itemName"// 中心。。名字
	            }],
	            "columnDefs": [{
	            	"types": "chinese-asc", 
	            	"targets": [1] 
	            },{ 
	            	"width": "35px", "targets": [0]
	            }],
	            language: {
	                lengthMenu: '显示 <select style="height: 30px;width: 50px">'+
	                '<option value="5">5</option>'+
	                '<option value="10">10</option>'+
	                '</select>条数据',
	                search: '<span class="label label-info">搜索：</span>',
	
	                paginate: {// 分页的样式内容。
	                    previous: "上页",
	                    next: "下页",
	                    first: "首页",
	                    last: "末页"
	                },
	                zeroRecords: "没有内容",// table tbody内容为空时，tbody的内容。
	                // 下面三者构成了总体的左下角的内容。
	                info: "共_PAGES_ 页，_MAX_ 条。当前显示_START_~_END_条，筛选之后 _TOTAL_ 条 ",// 左下角的信息显示，大写的词为关键字。
	                infoEmpty: "0条记录",// 筛选为空时左下角的显示。
	                infoFiltered: ""// 筛选之后的左下角筛选提示，
	            },
	            paging:true,
	            pagingType: "simple_numbers",// 分页样式的类型
	            lengthChange: true,
	            "dom":
	        		"<'row'<'col-xs-5'l><'col-xs-7'f>>" +
	        		"<'row'<'col-xs-12't>>"+
	        		// "<'row'<'col-xs-12'i>>"+//延迟渲染无法添加序号，需要此说明
	        		"<'row'<'col-xs-12'p>>",
	            // buttons: [{'extend':'excel','text':'导出','className':'btn
				// btn-default','title':'担保环','exportOptions':{'columns':[1,2,3,4,5]}}],
	            createdRow : function ( row, data, index ) {
	                $('td', row).css('font-weight',"normal").css("text-align","left");
		            $('td:eq(1)', row).html(data.itemName+"&nbsp;<button type='button' class='btn btn-default btn-sm'><span class='glyphicon glyphicon-plus'></span></button>");
	            }
	      });
	   }else if(tag==6){// 分析模型列表显示
		   console.log("分析模型列表显示");
		   var list_data;
		   var tempClass = 'searchList'/*('searchList-'+$("form option:selected")[0].index).toString()*/;
		   if(flag){
			   $("#listTable1").DataTable().destroy();
			   $("#listTable1").addClass(tempClass);
			   list_data = search_pattern_list;
		   }else{
			   list_data = patternList;;
		   }
		   console.log(list_data);
		   if($.fn.DataTable.isDataTable( '#listTable1' )){
			   $("#listTable1").DataTable().destroy();
		   }
		  /* if($.fn.DataTable.isDataTable( '#listTable1' )){
			   if($("#listTable1").hasClass(tempClass)){
				   $("#listTable1").DataTable().destroy();
				   $("#listTable1").removeClass(tempClass);
			   }else{
				   console.log("已经初始化了");
				   return;
			   }			   
		   }	   */	   
		   console.log("开始初始化");
		   isTrue = true;// 延迟渲染后应为false
		   		   
		   var start = new Date();
		   table1=$("#listTable1").DataTable({
	            ordering: false,// 是否启用排序
	            searching: true,// 搜索
	            autoWidth:false,
	            // deferRender: true,//延迟渲染数据
	            "data":list_data,
	            columns:[{
	            	"data":null,
	            	"orderable":false
	            },/*
					 * {//延迟渲染时启用，无序号 "data":"count", "orderable":false,
					 * "render":function(){return null;} },
					 */{
	            	"data":"itemName"// 担保圈名字
	            }],
	            "columnDefs": [{
	            	"types": "chinese-asc", 
	            	"targets": [1] 
	            },{ 
	            	"width": "35px", "targets": [0]
	            }],
	            language: {
	                lengthMenu: '显示 <select style="height: 30px;width: 50px">'+
	                '<option value="5">5</option>'+
	                '<option value="10">10</option>'+
	                '</select>条数据',
	                search: '<span class="label label-info">模糊查询：</span>',
	
	                paginate: {// 分页的样式内容。
	                    previous: "上页",
	                    next: "下页",
	                    first: "首页",
	                    last: "末页"
	                },
	                zeroRecords: "没有内容",// table tbody内容为空时，tbody的内容。
	                // 下面三者构成了总体的左下角的内容。
	                info: "共_PAGES_ 页，_MAX_ 条。当前显示_START_~_END_条，筛选之后 _TOTAL_ 条 ",// 左下角的信息显示，大写的词为关键字。
	                infoEmpty: "0条记录",// 筛选为空时左下角的显示。
	                infoFiltered: ""// 筛选之后的左下角筛选提示，
	            },
	            paging:true,
	            pagingType: "simple_numbers",// 分页样式的类型
	            lengthChange: true,
	            "dom":
	        		"<'row'<'col-xs-5'l><'col-xs-7'f>>" +
	        		"<'row'<'col-xs-12't>>"+
	        		// "<'row'<'col-xs-12'i>>"+//延迟渲染无法添加序号，需要此说明
	        		"<'row'<'col-xs-12'p>>",
	            // buttons: [{'extend':'excel','text':'导出','className':'btn
				// btn-default','title':'担保环','exportOptions':{'columns':[1,2,3,4,5]}}],
	            createdRow : function ( row, data, index ) {
	                $('td', row).css('font-weight',"normal").css("text-align","left");
		            $('td:eq(1)', row).html(data.itemName+"&nbsp;<button type='button' class='btn btn-default btn-sm'><span class='glyphicon glyphicon-plus'></span></button>");
	            }
	      });
		   var end1 = new Date();
	   }else return;
 
	   if(isTrue){// 添加序号
			if(tag==4 || tag==6) table = table1;
			if(tag==5) table = table2;
			table.on('order.dt search.dt',
					function() {
					    table.column(0, {
					        "search": 'applied',
					        "order": 'applied'
					    }).nodes().each(function(cell, i) {
					        cell.innerHTML = i + 1;
					    });
			}).draw();
			
			var end2 = new Date();
			
			console.log("1:"+(end1-start)+" ms");
			console.log("2:"+(end2-start)+" ms");
	   }
	   console.log("1:"+(end1-start)+" ms");
}	

// 隐藏加载框
function hideModal(){  
    $('#showloding').modal('hide');  
}  
// 显示加载框
function showModal(){  
    $('#showloding').modal({backdrop:'static',keyboard:false});  
}


var curPage = 1;// 当前页
var skipValue = 0;

var temp = null;
document.onclick = function(event){

	var e = event || window.event;
	var elem = e.srcElement || e.target;

    temp =  "当前显示：";

	switch(elem.id){
		case "dbbtn":temp = temp+"担保环";break;
		case "radiobutton1":temp = temp+"担保环";getTotalNumOfDBByCount(1,false);break;
		case "radiobutton2":temp = temp+"担保环";getTotalNumOfDBByCount(2,false);break;
		case "radiobutton3":temp = temp+"担保环";getTotalNumOfDBByCount(3,false);break;
		case "radiobutton4":temp = temp+"担保环";getTotalNumOfDBByCount(4,false);break;
		case "radiobutton5":temp = temp+"担保环";getTotalNumOfDBByCount(5,false);break;
		case "ydzcbtn":temp = temp+"贷款用于银承保证金";break;
		case "hlzdbrbtn":temp = temp+"贷款资金回流至担保人";break;
		case "fdcbtn":temp = temp+"信贷资金违规进入房地产行业";break;
		case "zqqhbtn":temp = temp+"信贷资金流入证券、期货";break;
		case "frdbbtn":temp = temp+"信贷资金回流至法人代表账户";break;
		case "ygbtn":temp = temp+"向本行员工发放经营性贷款";break;
		case "yjhxbtn":temp = temp+"贷款以新还旧";break;
		case "xfdbtn":temp= temp+"消费贷资金违规进入房地产行业";break;
		case "submitDBbtn":temp = temp+"担保环.";getTotalNumOfDBByCount(0,true);break;
		case "search1":temp = null;break;
		case "topdbol":temp = temp+"总长度前十的担保环";break;
		case "topdbom":temp = temp+"总金额前十的担保环";break;
		case "listView":temp = null;break;
		case "listView1":temp = null;break;
		case "centerView":temp = null;break;
		default:temp = false;
	}
	
	
	if(temp || temp == null){
		$("#currentShowed").html(temp);
		curPage = 1;skipValue = 0;
		if(temp &&elem.id!="topdbol" && elem.id!="topdbom"){
			$("#page").show();
		}else $("#page").hide();
	}
	// console.log(curPage);
	$("#currentPage").html("当前显示第"+curPage+"页");
	$("#totalPage").html("共"+totalPage+"页");
	if(curPage < 2) {
		$("#first,#pre").attr('disabled',true);
	}
	else {
		$("#first,#pre").attr('disabled',false);
	}
	if(curPage >= totalPage) {
		$("#last,#nex").attr('disabled',true);
	}
	else {
		$("#last,#nex").attr('disabled',false);
	}
}

function getTotalNumOfDBByCount(n,isInput){
// var dbdata =
// {"DbCircleInfo":[{"nodesNum":2,"count":2},{"nodesNum":3,"count":3},{"nodesNum":4,"count":4},{"nodesNum":5,"count":5},{"nodesNum":15,"count":100}]};
	var dblen;// 担保环长度
	if(isInput){
		dblen=document.getElementById("searchDBchain").value;
	}else dblen = n;
	dbdata.DbCircleInfo.forEach(function(d){
		if(d.nodesNum == dblen){
			totalPage = Math.ceil(d.count/numPerPage);
		}
	});
}

// 定义一个日历
laydate.render({
  elem: '#dateRange',
  range: true,
  btns: ['clear', 'now', 'confirm'],
  theme: 'molv',
  calendar: true
});



$(function(){
	$("#radiobutton1,#radiobutton2,#radiobutton3,#radiobutton4,#radiobutton5,#submitDBbtn").click(function(){
		curPage = 1;skipValue = 0;
	});
	
})

var numPerPage = 1;// 每页展示的数目
var totalPage=0;// 总页数
function setSkip(id){
   
   switch(id){
      case "first": curPage = 1;break;
      case "pre": if(curPage>1) curPage --;break;
      case "nex": curPage ++;break;
      case "last":curPage = totalPage;break;
      case "goto": var inputPage = $("#gotopage").val();
      				if(inputPage==""){alert("请输入要跳转的页数");return}
      				if(isNaN(inputPage)){
      					alert("请输入数字");
      					return;
      				};
      				if(inputPage>totalPage || inputPage<=0){
      					alert("所输入的页码非法，请重新输入");return;
      				}curPage=inputPage;break;
   }
	
	skipValue = (curPage-1) * numPerPage;
	
	console.log("当前页面："+curPage);
	var tag = $("#currentShowed").html();
	switch(tag){
		case "当前显示：担保环":searchDBCircle(skipValue);break;
		case "当前显示：向本行员工发放经营性贷款":searchPattern("01",skipValue);break;
		case "当前显示：贷款资金回流至担保人":searchPattern("02",skipValue);break;
		case "当前显示：信贷资金回流至法人代表账户":searchPattern("03",skipValue);break;
		case "当前显示：贷款用于银承保证金":searchPattern("04",skipValue);break;
		case "当前显示：信贷资金违规进入房地产行业":searchPattern("05",skipValue);break;
		case "当前显示：信贷资金流入证券、期货":searchPattern("06",skipValue);break;
		case "当前显示：消费贷资金违规进入房地产行业":searchPattern("07",skipValue);break;
		case "当前显示：贷款以新还旧":searchPattern("08",skipValue);break;
		case "当前显示：担保环.":searchDBCircleByInput(skipValue);break;
	}
}

function model(){
    exit();   
	temp =  "当前显示：";
	var index=$("form option:selected")[0].index;
//	getPatternList(('0'+index).toString());
	switch(index){
		case 1:getPatternList("01");totalPage=Math.ceil(totalNum[0].dkffzyg/numPerPage);temp = temp+"向本行员工发放经营性贷款";modelfade();break;
		case 2:getPatternList("02");totalPage=Math.ceil(totalNum[1].dkhlzdbr/numPerPage);temp = temp+"贷款资金回流至担保人";modelfade();break;
		case 3:getPatternList("03");totalPage=Math.ceil(totalNum[2].dkhlzfrdb/numPerPage);temp = temp+"信贷资金回流至法人代表账户";modelfade();break;
		case 4:getPatternList("04");totalPage=Math.ceil(totalNum[3].dkyyycbz/numPerPage);temp = temp+"贷款用于银承保证金";modelfade();break;
		case 5:getPatternList("05");totalPage=Math.ceil(totalNum[4].dkzrfdc/numPerPage);temp = temp+"信贷资金违规进入房地产行业";modelfade();break;
		case 6:getPatternList("06");totalPage=Math.ceil(totalNum[5].dkzrrzdb/numPerPage);temp = temp+"信贷资金流入证券、期货";modelfade();break;
		case 7:getPatternList("07");totalPage=Math.ceil(totalNum[6].xfdkzrfdc/numPerPage);temp= temp+"消费贷资金违规进入房地产行业";modelfade();break;
		case 8:getPatternList("08");totalPage=Math.ceil(totalNum[7].dkyjhx/numPerPage);temp = temp+"贷款以新还旧";modelfade();break;
		
	}
	var isShow = ($("#list").css("display") == 'block'?true:false);
	if(!isShow){
		var kind = ('0'+index).toString();
		searchPattern(kind,0);
	}
}
function modelfade(){
		  $('#jychain').fadeOut(0);
		  $('#kzchain').fadeOut(0);
		  $('#dbchain').fadeOut(0);
		  $("#list").hide();
		  $("#center").hide();

	if(temp || temp == null){
		$("#currentShowed").html(temp);
		curPage = 1;skipValue = 0;
		if(temp){
			$("#page").show();
		}else $("#page").hide();	
	}

}


$(function(){
	// $('#listTable').on("page.dt",function(){alert("分页1");eventFired();});
   $('#listTable tbody').on('click', 'td .btn', function () {
//担保列表
	   var tr = $(this).closest('tr');
	   var row = table1.row(tr);
	   
	   if ( row.child.isShown() ) {// This row is already open - close it
            row.child.hide();
            $(this).children().addClass("glyphicon-plus").removeClass("glyphicon-minus");
        }else {// Open this row,close others at the same time
        	
        	tr.siblings().find("span").addClass("glyphicon-plus").removeClass("glyphicon-minus");// 未点击的都变加号
        	tr.siblings().each(function(){// 关闭未点击但已显示的子表
        		if(table1.row($(this)).child.isShown()){
        			table1.row($(this)).child.hide();
        		}
        	})
        	/*
			 * var closerows = tr.siblings(); //var closerows =
			 * tr.siblings().find("span").parents("tr"); var len =
			 * closerows.length; for(var i = 0;i<len;i++){ var closerow =
			 * table1.row(closerows[i]); if(closerow.child.isShown())
			 * closerow.child.hide(); }
			 */
        	exit();
			searchDBCircleByList(row.data().nodesNum || row.data().size,row.data().skip);
            row.child(format()).show();
            // row.child(format(row.data())).show();
            $(this).children().addClass("glyphicon-minus").removeClass("glyphicon-plus");
        }
	   
   });
   
   $('#listTable').on("page.dt",function(){// 翻页时关闭前一页已打开的子表
	   $("#listInfo").find("span").addClass("glyphicon-plus").removeClass("glyphicon-minus");// 符号全变加号
	   // 关闭子表
	   $("#listInfo").children().each(function(){
		   if(table1.row($(this)).child.isShown()){
			   table1.row($(this)).child.hide();
			   // console.log(table1.row($(this)).data());
			   // console.log("已经关闭");
		   }
	   })
   });
   
   
   $('#listTable1 tbody').on('click', 'td .btn', function () {

	   var tr = $(this).closest('tr');
	   var row = table1.row(tr);
	   exit();
	   getPatternByList(row.data().ID);
   });
   
   
   
   
   // 中心性
   $('#centerTable tbody').on('click', 'td .btn', function () {
	   var tr = $(this).closest('tr');
	   var row = table2.row(tr);
	   searchDBCenterByList(row.data().startNodeId);
   })
})

/*
 * var eventFired = function(){
 * 
 * //table1.on('page.dt', // function() { table1.column(0, { "search":
 * 'applied', "order": 'applied' }).nodes().each(function(cell, i) {
 * console.log("i="+i); cell.innerHTML = i + 1; }); //}).draw(); alert("分页成功"); }
 */

function format(d){

	var totalDBMoney=0;
	var len = layout.graph.links.length;
	for(var i = 0;i < len; i++){
		if(layout.graph.links[i].label == 'DB' || layout.graph.links[i].label == 'ZDB'){
			totalDBMoney = totalDBMoney + (layout.graph.links[i].zdbje || layout.graph.links[i].dbzje);
		}
	}
	var comCount = layout.graph.nodes.length;
	// console.log(d.nodesNum);
	console.log("6");
	return '<table class="table table-striped table-hover table-bordered" style="table-layout:fixed">'+
   		  '<tr><td width="65px">担保金额</td><td>'+(totalDBMoney/10000)+'万元</td><td width="65px">企业个数</td><td>'+comCount+'</td></tr>'+
   		  '<tr><td>疑似风险客户数</td><td>'+''+'</td><td>涉及地区</td><td>'+''+'</td></tr>'+
   		  '<tr><td>涉及银行</td><td colspan="3">'+''+'</td></tr>'+
   		  '<tr><td>贷款余额</td><td>'+''+'</td><td>正常类占比</td><td>'+''+'</td></tr>'+
   		  '</table>';
}

/**
 * 以表格形式显示平台信息
 * @param data
 * @returns
 */
function platformTable(data){
	var buf=[];
	var start = new Date();
	for(var i in data){
		if(data[i] instanceof Array){
			var temp = data[i];
			buf.push({key:i,value:temp[0]});
		}else
		buf.push({key:i,value:data[i]});
	}
	$("#platformInfo").DataTable({
        ordering: true,// 是否启用排序
        searching: true,// 搜索
        autoWidth:false,
        data:buf,
        columns:[{
        	"data":"key"// key
        },{
        	"data":"value"// value
        }],
        "columnDefs": [{"aTypes": "chinese-asc", "aTargets": [0,1] }],
        language: {
            lengthMenu: '显示 <select style="height: 30px;width: 50px">'+
            '<option value="5">5</option>'+
            '<option value="10">10</option>'+
            '<option value="-1">不限</option>'+
            '</select>条数据',
            search: '<span class="label label-info">搜索：</span>',
            paginate: {previous: "上一页",next: "下一页"},
            zeroRecords: "没有内容",
        },
        paging:true,
        pagingType: "simple_numbers",// 分页样式的类型
        lengthChange: true,
        "dom":"<'row'<'col-xs-12'l>>" +"<'row'<'col-xs-12't>>"+"<'row'<'col-xs-offset-3 col-xs-9'p>>",
        createdRow : function ( row, data, index ) {
            $('td', row).css('font-weight',"bold").css("text-align","center");
        }
  });	
}
/**
 * 表格显示数据库的节点标签，关系类型，属性信息
 * @param data
 * @returns
 */
function modelTable(data){
	var start = new Date();
	data.labels.forEach(function(obj){
		obj.type="Node Labels";
	})
	data.relTypes.forEach(function(obj){
		obj.type="Relationship Types";
	})
	data.propertys.forEach(function(obj){
		obj.type="Property Keys";
	})
	data.labels = data.labels.concat(data.relTypes,data.propertys);
	var buf=[];
	for(var i=0;i<data.labels.length;i++){
		//buf[i].type = data.labels[i].type;
		data.labels[i].num = data.labels[i].count || data.labels[i].numNodes;
		data.labels[i].name = data.labels[i].labelname || data.labels[i].typename || data.labels[i].prop;
		delete (data.labels[i].count || data.labels[i].numNodes);
		delete (data.labels[i].labelname || data.labels[i].typename || data.labels[i].prop);
	}
	
	buf = data.labels;
	var DBdetail = $("#detail").DataTable({
        ordering: true,// 是否启用排序
        searching: true,// 搜索
        autoWidth:false,
        data:buf,
        columns:[{
        	"data":null,
        	"orderable":false
        },{
        	"data":"name",// 名称
        },{
        	"data":"type"// 类别
        },{
        	"data":"num"//数量
        }],
        "columnDefs": [{
        	"aTypes": "chinese-asc", 
        	"aTargets": [0,1,2,3] 
        }],
        language: {
            lengthMenu: '显示 <select style="height: 30px;width: 50px">'+
            '<option value="5">5</option>'+
            '<option value="10">10</option>'+
            '<option value="20">20</option>'+
            '<option value="30">30</option>'+
            '<option value="40">40</option>'+
            '<option value="50">50</option>'+
            '<option value="-1">不限</option>'+
            '</select>条数据',
            search: '<span class="label label-info">搜索：</span>',

            paginate: {// 分页的样式内容。
                previous: "上一页",
                next: "下一页",
                first: "首页",
                last: "末页"
            },
            zeroRecords: "没有内容",// table tbody内容为空时，tbody的内容。
            // 下面三者构成了总体的左下角的内容。
            info: "总共_PAGES_ 页，共_MAX_ 条 ",// 左下角的信息显示，大写的词为关键字。
            infoEmpty: "0条记录",// 筛选为空时左下角的显示。
            infoFiltered: ""// 筛选之后的左下角筛选提示，
        },
        paging:true,
        pagingType: "full",// 分页样式的类型
        lengthChange: true,
        "dom":
    		"<'row'<'col-xs-3'l><'col-xs-9'f>>" +
    		"<'row'<'col-xs-12't>>"+
    		"<'row'<'col-xs-4'i><'col-xs-8'p>>",
        createdRow : function ( row, data, index ) {
            $('td', row).css('font-weight',"bold").css("text-align","center");
        }
  });	
	DBdetail.on('order.dt search.dt',
			function() {
		DBdetail.column(0, {
			        "search": 'applied',
			        "order": 'applied'
			    }).nodes().each(function(cell, i) {
			        cell.innerHTML = i + 1;
			    });
	}).draw();
	
}

function showTableView(thead,rowsData){
	
	
	if($.fn.DataTable.isDataTable( '#table_view' )){
		$("#table_view").DataTable().destroy();
	}
	$("#table_view tbody,#table_view thead").empty();//清空表格
	
	var columns = [];
	var render = function(data){
		if(data===undefined || JSON.stringify(data) == "{}"){
			return "<pre>(empty)</pre>";
		}
		var temp = JSON.stringify(data, null, 4);
		return "<pre>"+temp+"</pre>";
	};
	
	var reg = /^(.+)\.(.+)$/g;
	for(var i=0;i<thead.length;i++){
		var col = {};
		//属性名(key)有"."时，需要将"."转义。如
		//match (m)-[r]->(n) return r.yxjgdm,r 中的r.yxjgdm
		col.data = thead[i].replace(reg,'$1\\.$2');
		col.render = render;
		col.title = thead[i];
		columns.push(col);
	}
	
	//解决初始化时表头错位
	$('a[data-toggle="tab"]').on( 'shown.bs.tab', function (e) {
        $.fn.dataTable.tables( {visible: true, api: true} ).columns.adjust();
    } );
	
	var table_view = $("#table_view").DataTable({
        ordering: false,// 是否启用排序
        searching: false,// 搜索
        autoWidth:true,
        scrollY: "600px",//580px//"650px"
        scrollX: true,
        scrollCollapse: true,
        data:rowsData,
        columns:columns,
        language: {
            lengthMenu: '显示 <select style="height: 30px;width: 50px">'+
            '<option value="5">5</option>'+
            '<option value="10">10</option>'+
            '<option value="-1">不限</option>'+
            '</select>条数据',

            paginate: {// 分页的样式内容。
                previous: "上一页",
                next: "下一页",
                first: "首页",
                last: "末页"
            },
            zeroRecords: "没有内容",// table tbody内容为空时，tbody的内容。
            // 下面三者构成了总体的左下角的内容。
            info: "共_PAGES_ 页，_MAX_ 条记录。当前显示第_START_到_END_条 ",// 左下角的信息显示，大写的词为关键字。
            infoEmpty: "0条记录",// 筛选为空时左下角的显示。
            infoFiltered: ""// 筛选之后的左下角筛选提示，
        },
        paging:true,
        pagingType: "full_numbers",// 分页样式的类型
        lengthChange: true,
        "dom":
        	"<'row'<'col-xs-3'l>>" +
    		"<'row'<'col-xs-12't>>"+
    		"<'row'<'col-xs-4'i><'col-xs-8'p>>",
        createdRow : function ( row, data, index ) {
            $('td', row).css('font-weight',"normal").css("text-align","left");
        }
  });	
	
}
var list_view_table=null;
function showListView(subGraphData){
	
	if($.fn.DataTable.isDataTable( '#list_view_table' )){
		$("#list_view_table").DataTable().destroy();
	}
	
	list_view_table=$("#list_view_table").DataTable({
        ordering: false,// 是否启用排序
        searching: false,// 搜索
        autoWidth:true,
        data:subGraphData,
        columns:[{
        	"data":"rowNo",// 子图编号
        	"title":"编号"
        },{
        	"data":"rowAbstract",// 摘要
        	"title":"摘要"
        },{
        	"data":null,// 操作
        	"title":"操作"
        },{
        	"data":"links",// 
        	"title":"关系",
        	"visible": false
        },{
        	"data":"nodes",//
        	"title":"节点",
        	"visible": false
        },{
        	"data":"columnName",// 列名
        	"title":"列名",
        	"visible": false
        },{
        	"data":"rowData",// 当前列表项对应的表视图
        	"title":"表视图数据",
        	"visible": false
        }],
        language: {
            lengthMenu: '显示 <select style="height: 30px;width: 50px">'+
            '<option value="5">5</option>'+
            '<option value="10">10</option>'+
            '</select>条数据',

            paginate: {// 分页的样式内容。
                previous: "上页",
                next: "下页",
                first: "首页",
                last: "末页"
            },
            zeroRecords: "没有内容",// table tbody内容为空时，tbody的内容。
            // 下面三者构成了总体的左下角的内容。
            info: "共_PAGES_ 页，_MAX_ 条。当前显示_START_~_END_条，筛选之后 _TOTAL_ 条 ",// 左下角的信息显示，大写的词为关键字。
            infoEmpty: "0条记录",// 筛选为空时左下角的显示。
            infoFiltered: ""// 筛选之后的左下角筛选提示，
        },
        paging:true,
        pagingType: "full",// 分页样式的类型
        lengthChange: true,
        "dom":
    		"<'row'<'col-xs-5'l>>" +
    		"<'row'<'col-xs-12't>>"+
    		"<'row'<'col-xs-12'p>>",
        createdRow : function ( row, data, index ) {
            $('td', row).css({'font-weight':"normal","text-align":"center",'font-size':'13px'});
            
            $('td:eq(2)', row).html("<button type='button' class='btn btn-success btn-sm'><i class='fa fa-eye'></i>查看</button>");
        }
	});
	
	$('#list_view_table tbody').on('click', 'td .btn', function () {
		
		   var tr = $(this).closest('tr');
		   var row = list_view_table.row(tr);
		   exit();
		   var d = {links:row.data().links,nodes:row.data().nodes};
		   layout.update(d);
		   showInfo();
		   
		   showTableView(row.data().columnName,row.data().rowData);
		   
	});
}


var showDetails = function(d,flag){
	var noShowAttrs = ["px","py","id","index","x","y","weight","fixed","no","sum","source","target"];
	var noShowAttrs_1 = ["px","py","id","index","x","y","weight","fixed","sum","source","target"];
	
	console.log(d);
	
	var insertHtml = "";
	var count = 0;
	
	var showAtBottom = "";
	
	for(var i in d){
		
		if(d.fixed){//节点
			if(jQuery.inArray(i,noShowAttrs_1)==-1){
				count++;
				if(count%2==1){
					insertHtml +='<tr>'
					insertHtml += '<td><strong>'+i+'</strong></td><td>'+d[i]+'</td>'; 
				}else{
					insertHtml += '<td><strong>'+i+'</strong></td><td>'+d[i]+'</td>'; 
					insertHtml +='</tr>'
				}
				
				showAtBottom += "<strong>"+i+"</strong>:"+d[i]+"&nbsp&nbsp";
			}
		}else{
			if(jQuery.inArray(i,noShowAttrs)==-1){
				count++;
				if(count%2==1){
					insertHtml +='<tr>'
					insertHtml += '<td><strong>'+i+'</strong></td><td>'+d[i]+'</td>'; 
				}else{
					insertHtml += '<td><strong>'+i+'</strong></td><td>'+d[i]+'</td>'; 
					insertHtml +='</tr>'
				}
				
				showAtBottom += "<strong>"+i+"</strong>:"+d[i]+"&nbsp&nbsp";
			}
		}
		
	}
	if(count%2==1)
		insertHtml += '</tr>';
	
	console.log(insertHtml);
	
	if(flag==1)
		$("#nodeInfo").html(insertHtml);
	else
		$("#nodeSize").html(showAtBottom);
	
}







