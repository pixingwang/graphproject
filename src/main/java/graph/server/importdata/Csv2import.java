package graph.server.importdata;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class Csv2import {
	public void readCsv(String path,String target){
		
		List<String> tmp= new ArrayList<>();
		
		try{
			
			CsvReader reader=new CsvReader(path, ',',Charset.forName("UTF-8"));
			//放弃第一行信息
//			reader.readHeaders();
			reader.readRecord();

			CsvWriter P2P=new CsvWriter(target+"\\P2P.csv", ',',Charset.forName("UTF-8"));
			CsvWriter P2C=new CsvWriter(target+"\\P2C.csv", ',',Charset.forName("UTF-8"));
			CsvWriter C2P=new CsvWriter(target+"\\C2P.csv", ',',Charset.forName("UTF-8"));
			CsvWriter C2C=new CsvWriter(target+"\\C2C.csv", ',',Charset.forName("UTF-8"));
			
			P2P.writeRecord(reader.getValues());
			P2C.writeRecord(reader.getValues());
			C2P.writeRecord(reader.getValues());
			C2C.writeRecord(reader.getValues());
			
			
			
			int count=0;
			while(reader.readRecord()){
				String a[]=reader.getValues();
//				if(tmp.contains(a[4]))
//					a[3]=Integer.toString(tmp.indexOf(a[4]));
//				else
//					{
//					tmp.add(a[4]);
//					a[3]=Integer.toString(tmp.size()-1);
//					}
//				
//				if(tmp.contains(a[8]))
//					a[7]=Integer.toString(tmp.indexOf(a[8]));
//				else
//					{
//					tmp.add(a[8]);
//					a[7]=Integer.toString(tmp.size()-1);
//					}
				if(a[6].charAt(0)=='P'){
					if(a[10].charAt(0)=='P'){
						P2P.writeRecord(a);
					}else{
						P2C.writeRecord(a);
					}
				}else{
					if(a[10].charAt(0)=='P'){
						C2P.writeRecord(a);
					}else{
						C2C.writeRecord(a);
					}	
				}
				count++;
				System.out.println(count);
				
				}
					
			//输出总数
			System.out.println(count);		
			P2P.close();
			P2C.close();
			C2P.close();
			C2C.close();
			reader.close();

		}catch(Exception err){
			System.out.println(err);
		}
	}
	
	
	
}
