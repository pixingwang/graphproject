    !function(){
        var layout={name:"forcelayer"};

        //带有数据的相关图形
        var link,node,graph={"nodes":[],"links":[]},selecteddata=null;
        var label=[],type=[];

        //zoomflag 控制是否允许缩放 
        // mouseout 确认是否点击位于元素外
        var zoomflag=true,mouseout=false;
        //鼠标按键按下时间    鼠标按键抬起时间
        var first=0,last=0;

        //进入更新数据
        var update=function(d){
            //获取节点信息
            var h=graph.nodes.length;
            var i=d.nodes.length;

            for(var j=0;j<i;j++){
                for(var k=0;k<h;k++){
                    if(d.nodes[j].id==graph.nodes[k].id){
                        d.nodes[j]=graph.nodes[k];
                        break;
                    }
                }
            }

            graph.nodes=d.nodes;
            graph.links=d.links;

            //清除标签集合
            for(var m=0;m<d.links.length;m++){
                //加入标签
                if(!type.includes(d.links[m].label))
                    type.push(d.links[m].label);
            }

            for(var n=0;n<d.nodes.length;n++){
                //加入标签
                if(!label.includes(d.nodes[n].label))
                    label.push(d.nodes[n].label);
            }

            //开始渲染
            render();
        }

        //力导向图设定
        var force = d3.layout.force()
            .charge(-400).linkDistance(200).size([1400, 900])
            .friction(0.8)
            .gravity(0.05);

        //x方向比例尺
        var x=d3.scale.linear()
            .domain([0,1400])
            .range([0,1400]);
        //y方向比例尺
        var y=d3.scale.linear()
            .domain([0,900])
            .range([900,0]);

        //缩放
        var zoom=d3.behavior.zoom()
            .x(x).y(y)
            .scaleExtent([1/8,10])
            .on("zoom",function(){
                if(zoomflag){
                    d3.select(this).selectAll("#map").attr("transform","translate("+d3.event.translate+")scale("+d3.event.scale+")");
                }
            });

        //设置svg元素并添加鼠标属性
        var svg = d3.select("#graph").append("svg")
            .attr("width", "1400px").attr("height", "900px")
			.attr("pointer-events","auto")
            .on("click",function(d){ if(last-first<200&&mouseout) {contract();}})
            .on("mousedown",function(d){first=new Date().getTime();})
            .on("mouseup",function(d){last=new Date().getTime();})
            .call(zoom)
            .append("g")
            .attr("id","map");

        svg.append("g").attr("class","layer links");
        svg.append("g").attr("class","layer nodes");
		
		// 对外暴露变量节点关系数据  节点绘图组合  关系绘图组合  绘图层面标签  缩放工具 更新函数
        layout.graph=graph;
		layout.node=node;
		layout.link=link;
		layout.svg=svg;
		layout.zoom=zoom;
		layout.update=update;

        //颜色插值器
        var colorline=d3.scale.category20();
        var colorlinec=d3.scale.category20c();

//拖动就锁定
        var  drag=force.drag()
            .on("dragstart",function(d,i){
                d.fixed=true;
            });
//样条曲线生成器
        var lineFunction = d3.svg.line()
            .interpolate("basis")
            .x(function(d){return d.x})
            .y(function(d){return d.y});

//按键按下设置不允许缩放
        var mousedown=function(d){
            zoomflag=false;
        }
//按键抬起设置允许缩放
        var mouseup=function(d){
            zoomflag=true;
        }

//根据时间判断是否点击并创建环菜单
        var click=function(select,data){
			
            if(last-first<200){
                    mouseout=false;
					if(selecteddata==null||data.id!=selecteddata.id){
					contract();
					select.attr("class","selected"); 
					selecteddata=data;
                    expand(select);
					}
            }


        }

//设置环生成器
        var arc =d3.svg.arc();

        var menu=[];
        for(var i=0;i<3;i++)
            menu.push({"startAngle":(i*120+2)*Math.PI/180,"endAngle":(i*120+118)*Math.PI/180,"innerRadius":20,"outerRadius":40});


//设置扩张菜单
        var expand=function(select){

            //添加环
            select.selectAll(".arc").data(menu).enter()
                .append("path").attr("class","arc").attr("d",function(d){return arc(d);})
                .on("click",function(d,i){if(i==0) nodedelete(); if(i==1) nodeexpand(); if(i==2) nodefix(); 
				contract(); d3.event.stopPropagation();},true);//禁止事件冒泡传递

            //添加文本
            select.selectAll(".arctext")
                .data(menu).enter()
                .append("text").attr("class","arctext")
                .attr("text-anchor","middle")
                .attr("pointer-events", "none")
                .attr("transform",function(d){return "translate("+arc.centroid(d)+")";})
                .attr("fill","white")
                .text(function(d,i){if(i==0) return "\uf00d"; if(i==1) return "\uf0b2"; return "\uf09c";})
                .attr("font-family","FontAwesome");

        }

//节点解除固定
        var nodefix=function(){
            selecteddata.fixed=false;
        }

//菜单删除节点
        var nodedelete=function(){
			
            var ID=selecteddata.id;

            var k=graph.links.length;
			
            for(var j=0;j<k;j++){
                if(graph.links[j].source.id==ID||graph.links[j].target.id==ID){
                    graph.links.splice(j,1);
                    j--;k--;
                }
            }

            for(var i in graph.nodes){
                if(graph.nodes[i].id==ID){
                    graph.nodes.splice(i,1);
                    break;
                }
            }

//更新实际线条
            link.data(graph.links,function(d){return d.id;}).exit().remove();
            node.data(graph.nodes,function(d){return d.id;}).exit().remove();
        }

        //设置清除数据收缩菜单
        var contract=function(d){
			
            svg.select(".selected").selectAll(".arc,.arctext").remove();
			svg.select(".selected").attr("class",null);
			selecteddata=null;
        }

//设置拓展节点
        var nodeexpand=function(d){

            var id=selecteddata.id;
            var nodes=JSON.stringify(graph.nodes);
            var links=JSON.stringify(graph.links);

            jQuery.ajax({
                type: 'POST',
                data:JSON.stringify({
                    id:id,
                    nodes:nodes,
                    links:links
                }),
                url: 'extendById.do',
                dataType: 'json',
				contentType:'application/json',
                beforeSend:function (){
                    showModal();
                },
                success: function(data){
                    hideModal();
                    update(data);
                    showInfo();
                },
                error: function(XMLHttpRequest, textStatus, errorThrown){
                    hideModal();
                    alert("连接失败");
                    console.log(XMLHttpRequest.responseText);
                }
            });

        }

        var render=function(){

            //设置箭头
            var marker=svg.selectAll("marker")
                .data(type);

            marker.enter().append("marker")
                .attr("markerUnits","userSpaceOnUse")
                .attr("id",function(d,i){return d;})
                .attr("viewBox","0 -5 10 10")
                .attr("refX",25)
                .attr("refY",0)
                .attr("markerWidth",8)
                .attr("markerHeight",8)
                .attr("orient","auto")
                .attr("stroke-width",1)
                .append("path")
                .attr("d","M0,-5L10,0L0,5")
                .attr("fill",function(d,i){return colorlinec(d);});


            //进入圆形
            node=svg.select(".layer.nodes")
                .selectAll("g")
                .data(graph.nodes,function(d){return d.id;});

            var nodeupdate=node.enter().append("g")
                .on("mouseup",function(d){ mouseup(d); })
                .on("mousedown",function(d){ mousedown(d); })
                .on("click",function(d){  click(d3.select(this),d);showDetails(d,1);})
                .on("mouseout",function(d,i){ mouseout=true; })
                .call(drag);

            nodeupdate.append("circle")
                .attr("class", function (d) { return "node";})
                .attr("r", function(d){ return 15;})
                .attr("fill",function(d){ return colorline(d.label);});

            //设置文本
            nodeupdate.append("text")
                .attr("dx",15)
                .attr("dy",-5)
                .attr("class", "text")
                .attr("pointer-events", "none")
                .text(function(d){ return d.label; });


            //进入线条路径
            link=svg.select(".layer.links")
                .selectAll("g")
                .data(graph.links,function(d){return d.id;});

            var linkupdate=link.enter().append("g");

            linkupdate.append("path")
                .attr("class", function(d) { return "link T"; })
                .attr("id",function(d,i){return "link"+d.id})
                .attr("pointer-events","none")
                .style("stroke",function(d){ return colorlinec(d.label); })
                .attr("marker-end",function(d){return "url(#"+d.label+")";})
                .attr("fill","none");

            //阴影路径
            linkupdate.append("path").attr("class", function(d) { return "link shadow"})
                .attr("opacity","0")
                .attr("stroke-width","10")
                .attr("pointer-events","auto")//自动确认鼠标点击位置防止有问题覆盖
                .on("mousedown",function(d,i){ mousedown(d); showDetails(d,1);});

            //设置文本
            linkupdate.append("text")
                .attr("pointer-events", "none")
                .attr("text-anchor","middle")
                .attr("dominant-baseline","middle")
                .attr("class", "text")
                .attr("id",function(d,i){return "link"+d.id})
                .attr("pointer-events", "none")
                .text(function(d){return d.label;});


            //清理不需要的数据图形
            link.exit().remove();
            node.exit().remove();
            marker.exit().remove();


            //力图开始模拟
            force.nodes(graph.nodes).links(graph.links).start();

            //图形随动作刷新
            force.on("tick", function() {

                //点移动时候坐标更新
                node.attr("transform", function(d) { return "translate("+d.x+","+d.y+")"; });
                //连线平移及旋转
                link.attr("transform",function(d){ 
				
				//自环进行旋转
				if(d.source.id==d.target.id){
					return "translate("+d.source.x+","+d.source.y+")"+"rotate("+d.no*360/(d.sum+8)+")";
				}
				
				return "translate("+d.source.x+","+d.source.y+")"
				+"rotate("+Math.atan2(d.target.y-d.source.y,d.target.x-d.source.x)/Math.PI*180+")";
				});

                //连线文本翻转
                link.select("text")
				.attr("x",function(d){
					// 如果是自环进行固定值处理
					if(d.source.x==d.target.x&&d.source.y==d.target.y)
						return 105;
					
					var center=Math.sqrt(Math.pow((d.target.x - d.source.x),2)+Math.pow((d.target.y-d.source.y),2))/2;
					return center;
				})
				.attr("y",function(d){
					// 如果是自环进行固定值处理
					if(d.source.x==d.target.x&&d.source.y==d.target.y)
						return 0;
					var y=(d.no-d.sum/2-0.5) * 30;
					//处理关系翻转问题
					if(d.source.id>d.target.id)
						y=-y;
					return y;
				})
				.attr("transform",function(d){
					// 如果是自环就进行翻转
					if(d.source.x==d.target.x&&d.source.y==d.target.y){
						return "rotate("+90+" 105"+" 0)";
					}
						
					var center=Math.sqrt(Math.pow((d.target.x - d.source.x),2)+Math.pow((d.target.y-d.source.y),2))/2;
					
					if(d.target.x<d.source.x){
						//处理关系翻转问题
						if(d.source.id>d.target.id)
							return "rotate("+180+" "+center+" "+(d.no-d.sum/2-0.5) * -30+" )";
						
					return "rotate("+180+" "+center+" "+(d.no-d.sum/2-0.5) * 30+" )";
					}
				});


                //连线实时进行曲线运算
                link.selectAll(".link")
                    .attr('d',function(d){
						//自环路径绘制
                        if(d.source.x==d.target.x&&d.source.y==d.target.y){
							
							var centerx=100,centery=30;
							
                            return lineFunction([{"x":0,"y":0},{"x":centerx,"y":-centery},{"x":centerx,"y":centery},{"x":0,"y":0}]);
                        }

                        //设定字符占用宽度
                        var width=d.label.length*10;

                        var start={
                            x:0,
                            y:0
                        }

                        var end={
                            x:Math.sqrt(Math.pow((d.target.x - d.source.x),2)+Math.pow((d.target.y-d.source.y),2)) ,
                            y:0
                        }
                        //中心控制点
                        var center={
                            x: end.x / 2,
                            y: 0
                        };
						//文本左控制点
                        var center1={
                            x:center.x-width/2.0,
                            y:(d.no-d.sum/2.0-0.5) * 30
                        }
						//文本右控制点
                        var center2={
                            x:center.x+width/2.0,
                            y:(d.no-d.sum/2.0-0.5) * 30
                        }
						
						//处理关系翻转问题
						if(d.source.id>d.target.id){
							center1.y=-center1.y;
							center2.y=-center2.y;
						}
						
						//左控制点
                        var point1={
                            x:center1.x/3.0*2,
                            y:center1.y/3.0*2*1.1
                        }
						//右控制点
                        var point2={
                            x:(center2.x*2+end.x)/3.0,
                            y:(center2.y)/3.0*2*1.1
                        }

						
						
                        return lineFunction([{"x":start.x,"y":start.y},{"x":point1.x,"y":point1.y},{"x":center1.x,"y":center1.y}])+
                            lineFunction([{"x":center2.x,"y":center2.y},{"x":point2.x,"y":point2.y},{"x":end.x,"y":end.y}]);

                    });

            });

        }


        if (typeof define === "function" && define.amd) define(layout); else if (typeof module === "object" && module.exports) module.exports = layout;
        this.layout = layout;
    }();
	
	
//显示节点文字
function showNodetext(n){
	if(n.checked){
		var count=[];
	    link.each(function(d){if(d3.select(this).attr("visibility")=="hidden") return;if(count.indexOf(d.source.id)<0) count.push(d.source.id);if(count.indexOf(d.target.id)<0) count.push(d.target.id);});
		d3.select("#graph").select(".layer.nodetexts").selectAll(".text").each(function(d){if(count.indexOf(d.id)>=0) d3.select(this).attr("visibility","visible");});
	}else{
		d3.select("#graph").select(".layer.nodetexts").selectAll(".text").attr("visibility","hidden");
	}
	
}

//显示链接文字
function showLinktext(l){

	if(l.checked){
		var count=[];
	    link.each(function(d){if(d3.select(this).attr("visibility")!="hidden") count.push(d.id);});
		d3.select("#graph").select(".layer.linktexts").selectAll(".text").each(function(d){if(count.indexOf(d.id)>=0) d3.select(this).attr("visibility","visible");});
	}else{
		d3.select("#graph").select(".layer.linktexts").selectAll(".text").attr("visibility","hidden");
	}
}

//显隐不同线条
function showlinetext(l){
    if(l.checked){
	    d3.select("#graph").select(".layer.links").selectAll(".link").each(function(d){if(d.label==l.value)d3.select(this).attr("visibility","visible")});
		if(($('#showlink').is(':checked')))
		d3.select("#graph").select(".layer.linktexts").selectAll(".text").each(function(d){ if(d.label==l.value)d3.select(this).attr("visibility","visible")});
	}else{
	    d3.select("#graph").select(".layer.links").selectAll(".link").each(function(d){if(d.label==l.value)d3.select(this).attr("visibility","hidden")});
		d3.select("#graph").select(".layer.linktexts").selectAll(".text").each(function(d){if(d.label==l.value)d3.select(this).attr("visibility","hidden")});
	}
    ordergraph();
}


//显隐数据源线条
function showSJYtext(l,txt){
	if(l.checked){
	    d3.select("#graph").select(".layer.links").selectAll(".link").each(function(d){if(d.jyjgmc_zh==txt)d3.select(this).attr("visibility","visible")});
		if(($('#showlink').is(':checked')))
		d3.select("#graph").select(".layer.linktexts").selectAll(".text").each(function(d){if(d.jyjgmc_zh==txt)d3.select(this).attr("visibility","visible")});
	}else{
	    d3.select("#graph").select(".layer.links").selectAll(".link").each(function(d){if(d.jyjgmc_zh==txt)d3.select(this).attr("visibility","hidden")});
		d3.select("#graph").select(".layer.linktexts").selectAll(".text").each(function(d){if(d.jyjgmc_zh==txt)d3.select(this).attr("visibility","hidden")});
	}
	ordergraph();
}


//显隐不同节点
function showdottext(l){
    if(l.checked){
        d3.select("#graph").select(".layer.links").selectAll(".link").each(function (d) {if(tmp.indexOf(d.source.label)>=0||tmp.indexOf(d.target.label)>=0 ) d3.select(this).attr("visibility","visible") });
        if(($('#showlink').is(':checked')))
            d3.select("#graph").select(".layer.linktexts").selectAll(".text").each(function(d){if(tmp.indexOf(d.source.label)>=0||tmp.indexOf(d.target.label)>=0 )d3.select(this).attr("visibility","visible")});
    }else{
        d3.select("#graph").select(".layer.links").selectAll(".link").each(function(d){if(tmp.indexOf(d.source.label)>=0||tmp.indexOf(d.target.label)>=0 )d3.select(this).attr("visibility","hidden")});
        d3.select("#graph").select(".layer.linktexts").selectAll(".text").each(function(d){if(tmp.indexOf(d.source.label)>=0||tmp.indexOf(d.target.label)>=0 )d3.select(this).attr("visibility","hidden")});
    }
    ordergraph();
}


var ordergraph=function(){
	var count=[];
	link.each(function(d){if(d3.select(this).attr("visibility")=="hidden") return;if(count.indexOf(d.source.id)<0) count.push(d.source.id);if(count.indexOf(d.target.id)<0) count.push(d.target.id);});
	node.each(function(d){var i=count.indexOf(d.id); if(i<0) d3.select(this).attr("visibility","hidden"); else  d3.select(this).attr("visibility","visible"); });
	if(($('#shownode').is(':checked')))
	nodetext.each(function(d){var i=count.indexOf(d.id); if(i<0) d3.select(this).attr("visibility","hidden"); else  d3.select(this).attr("visibility","visible"); });
}

//svg 保存成Png  fuction
var downloadimg=function(){
   Date.prototype.Format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "h+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
    }	
	
	var date=(new Date).Format("yyyyMMddhhmmss");
//	var str=date.getFullYear()+""+(date.getMonth()+1)+""+date.getDate()+""+date.getHours()+""+date.getMinutes()+""+date.getSeconds();
      saveSvgAsPng(document.getElementsByTagName("svg")[0], date+".png",{backgroundColor:'#FFFFFF',height:4000,width:6000,left:-2500,top:-1500,scale:2});
}

var up = function(){
	var formData=new FormData($("#upfile")[0]);
	var start =new Date().getTime();
	$.ajax({
		url:"/upload.do",
		type:"POST",
		cache:false,
		dataType:"text",
		data:formData,
		processData:false,
		contentType:false
	}).done(function(data){
		$("#close").click();
        if(data=="空文件")
		alert("空文件！");
	    else{
		var time=(new Date().getTime()-start)/1000;
	    alert("数据上传完毕 "+"用时:"+Math.floor(time/60)+"分"+Math.floor(time%60)+"秒");}
	}).fail(function(XMLHttpRequest, textStatus, errorThrown) {  
          alert("数据上传出现错误");  
     });
	 	 
}

var del=function(){
	$.ajax({
		url:"/delete.do",
		type:"GET",
		dataType:"text",
	}).done(function(data){	
		alert(data+"数据库已清空");
	}).fail(function(XMLHttpRequest, textStatus, errorThrown) {  
          alert("删除错误");  
     });
}

