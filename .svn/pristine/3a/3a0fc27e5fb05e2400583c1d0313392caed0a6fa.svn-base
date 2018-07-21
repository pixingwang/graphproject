/**
 * 
 */

$(function(){  
  $(document).keydown(function(event){  
    if(event.keyCode==13){  
       $("#search1").click();  
    }  
 });  
}); 

//定位节点--------------------------------------
function find(){
	var name=document.getElementById("searchAcount").value;
	noded.each(function(d,i){
		   if(d.name.indexOf(name)>=0){
		   d3.select(this).style("fill","#FFFF33");
	       }else{
		   d3.select(this).style("fill",function(d){ return d.label=="P"?"#37ff00":"#ff0000";});
		   }
	});

	
}

//清空数据
var exit=function(){
	$("#nodeSize").html(" ");
	$("input[type='checkbox']").prop("checked","true");
	 layout.svg.attr("transform","translate(0,0)scale(1)");
		layout.zoom.scale(1);
		layout.zoom.translate([0,0]);
	if(layout.graph.nodes.length>0){
/*    	link.data([]).exit().remove();
    	node.data([]).exit().remove();
    	linktext.data([]).exit().remove();
    	nodetext.data([]).exit().remove();*/
    }
    	
    	
    }


var isFixed=true;

var obj = document.getElementById("resetbtn");
obj.onclick = function(){
	node.each(function(d){
		  d.fixed=isFixed;
	});	
	if(isFixed) {
		$("#resetbtn").addClass("isClicked");
//		$("#resetbtn").prop("title","恢复");
	}
	else {
		$("#resetbtn").removeClass("isClicked");
//		$("#resetbtn").prop("title","固定");
	}
	isFixed=!isFixed;
	
//	obj.innerHTML = (obj.innerHTML =="固定"?"恢复":"固定");
}
/*obj.onclick = function(){
	noded.each(function(d){
		  d.fixed=isFixed;
	});	
	isFixed=!isFixed;
	obj.innerHTML = (obj.innerHTML =="固定"?"恢复":"固定");
}*/
