package graph.server.importdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.springframework.stereotype.Component;

/**
 * 获取导入数据源
 * @author dx
 *
 */
@Component
public class Sourcedata {
	
	/**
	 * 获取导入的文件流，注意需要关闭文件流
	 * @param filepath  数据文件夹所在位置
	 * @param charset   文件流编码类型
	 * @return     当前文件夹下方所有文件流列表
	 * 
	 */
	public List<BufferedReader> getOpenSourceStreamfromdisk(String filepath,Charset charset)  {
		//读取文件夹下所有文件名
		File importfile=new File(filepath);
		File files[]=null;
		
		if(importfile.isDirectory())
		files=importfile.listFiles();
		else {
		files=new File[1];
		files[0]=importfile;
		}
		
		List<BufferedReader> readerlist=new ArrayList<>();
		
		if(files==null) return null;
		
		for(File file:files) {
			if(file.isDirectory()) continue;
			
		try {
		FileInputStream fis=new FileInputStream(file);
		BufferedReader bf=new BufferedReader(new InputStreamReader(fis,charset));
		readerlist.add(bf);
			}catch (Exception e) {
				e.printStackTrace();
				System.out.println(file.getPath()+"  文件存在问题,该种数据导入失败");
				return null;
			}
		}
		
		return readerlist;
	}
	
	/**
	 * 从hadoop中获取导入的文件流列表，注意需要关闭文件流 
	 * @param filepath
	 * @param charset
	 * @return  当前hadoop文件夹下文件流列表
	 */
	public List<BufferedReader> getOpenSourceStreamfromhadoop(String filepath,Charset charset)  {
		
		List<BufferedReader> readerlist=new ArrayList<>();
		
		try {
			Configuration conf=new Configuration();
			FileSystem hdfs = FileSystem.get(conf);
			Path hdfsPath = new Path(filepath);
			RemoteIterator<LocatedFileStatus> it = hdfs.listFiles(hdfsPath, false);
			//储存HDFS中的文件路径
			List<Path> filepaths=new ArrayList<>();
			while (it.hasNext()) {
				LocatedFileStatus status = it.next();
				if (status.isFile()) {
					filepaths.add(status.getPath());
				}
			}
			
			for (int i = 0; i < filepaths.size(); i++) {
			//读取当前HDFS中的一个文件
			FSDataInputStream fsdis = hdfs.open(filepaths.get(i));
			BufferedReader dis = new BufferedReader(new InputStreamReader(fsdis.getWrappedStream(),charset));
			readerlist.add(dis);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("hadoop系统或文件存在问题，该种数据导入失败");
			return null;
		}
		
		return readerlist;
	}
	
	
}
