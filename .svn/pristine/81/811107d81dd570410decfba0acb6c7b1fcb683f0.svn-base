package graph.server.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class FilterImport {

	public HashMap<Integer, List<filter>> explain(String[] filters) {

		HashMap<Integer,List<filter>> filtermap=new HashMap<>();
		
		for(int i=0;i<filters.length;i=i+3) {
			List<filter> value= filtermap.getOrDefault(filters[i], new ArrayList<filter>());
			
			int col= Integer.parseInt(filters[i]);
			filter f=new filter(filters[i+1],filters[i+2]);
			value.add(f);
			
			filtermap.put(col, value);
		}
		
		return filtermap;
	}

//	内部类存储处理筛选规则
	public class filter{
		public String parameter1;
		public String parameter2;
		
		public filter(String parameter1,String parameter2) {
			this.parameter1=parameter1;
			this.parameter2=parameter2;
		}
		
		public boolean compare(String value) {
			
			switch(parameter1) {
			case ">":return Double.parseDouble(value)>Double.parseDouble(parameter2);
			case "<":return Double.parseDouble(value)<Double.parseDouble(parameter2);
			case "=":return Double.parseDouble(value)==Double.parseDouble(parameter2);
			case ">=":return Double.parseDouble(value)>=Double.parseDouble(parameter2);
			case "<=":return Double.parseDouble(value)<=Double.parseDouble(parameter2);
			case "!=":return Double.parseDouble(value)!=Double.parseDouble(parameter2);
			case "exist":return String.valueOf(value).matches(parameter2);
			case "noexist":return !String.valueOf(value).matches(parameter2);
			}
			
			return true;
		}
	}

}
