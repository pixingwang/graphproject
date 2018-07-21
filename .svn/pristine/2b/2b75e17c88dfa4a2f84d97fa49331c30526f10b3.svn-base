package graph.server.importdata;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class ImportCSV {

	public void readCsv(String path){
		
		List<String> tmp= new ArrayList<>();
		
		try{
			
			CsvReader reader=new CsvReader(path, ',',Charset.forName("UTF-8"));
			//放弃第一行信息
//			reader.readHeaders();
			reader.readRecord();
			String target="E://0.csv";
			CsvWriter wr=new CsvWriter(target, ',',Charset.forName("UTF-8"));
//			CsvWriter br=new CsvWriter(target, ',',Charset.forName("UTF-8"));
			String[] st=reader.getValues();
			wr.writeRecord(st);
			
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
					wr.writeRecord(a);
//				for(int i=0;i<800;i++){
//					a[3]=""+(int)(Math.random()*100000000);
//					a[4]=""+(int)(Math.random()*100000000);
//					a[5]=""+(char)(0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)))+(char)(0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)))+(char)(0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)));
//					a[7]=""+(int)(Math.random()*100000000);
//					a[8]=""+(int)(Math.random()*100000000);
//					a[9]=""+(char)(0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)))+(char)(0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)))+(char)(0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)));
//					wr.writeRecord(a);
//				}
				count++;
				System.out.println(count);
				if(count%5000000==0)
				{
					wr.close();
					wr=new CsvWriter("E://"+count/5000000+".csv", ',',Charset.forName("UTF-8"));
					wr.writeRecord(st);
				}
				}
					
			//输出总数
			System.out.println(count);		
			wr.close();
			reader.close();

		}catch(Exception err){
			System.out.println(err);
		}
	}
	
	
	
}
