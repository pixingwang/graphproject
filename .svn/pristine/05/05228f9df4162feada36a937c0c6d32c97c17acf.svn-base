package graph.server.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Component
public class UpdateSaveOld {
	@Autowired
	MapUtilForEmbeddedDriver mapUtil;
	
public Map<String, List<Map<String, Object>>> update(String nodes,String links){
		
		
		
		List<Map<String,Object>> new_nodes=new ArrayList<>();
		
		JSONArray json_node=JSONArray.fromObject(nodes);
		
		
		
		if(json_node.size()>0){
				for(int i=0;i<json_node.size();i++){
					JSONObject job = json_node.getJSONObject(i); // 遍历 jsonarray 数组，把每一个对象转成 json 对象
					Map<String, Object> result = new HashMap<String, Object>();
					result=job;			
					new_nodes.add(result);
				}
				
		}

		List<Map<String,Object>> new_links=new ArrayList<>();
		
		JSONArray json_link=JSONArray.fromObject(links);
		
		if(json_link.size()>0){
				for(int i=0;i<json_link.size();i++){
					JSONObject job = json_link.getJSONObject(i); // 遍历 jsonarray 数组，把每一个对象转成 json 对象
					Map<String, Object> result = new HashMap<String, Object>();
					int source=0,target=0;
					result=job;
					
					JSONObject sourceJs=(JSONObject) job.get("source");
					//int sourceId=sourceJs.getInt("id");
					String sourceId=sourceJs.getString("id");
					for(int j=0;j<new_nodes.size();j++) {
						if(sourceId.equals(new_nodes.get(j).get("id").toString())) {
							source=j;
						}
					}
					result.put("source",source);
					JSONObject targetJs=(JSONObject) job.get("target");
					
					//int targetId=targetJs.getInt("id");
					String targetId=targetJs.getString("id");
					for(int j=0;j<new_nodes.size();j++) {
						if(targetId.equals(new_nodes.get(j).get("id").toString())) {
							target=j;
						}
					}
					result.put("target", target);
					
					new_links.add(result);
				}
				
		}
		
		return mapUtil.map("nodes",new_nodes,"links",new_links);
	}

}
