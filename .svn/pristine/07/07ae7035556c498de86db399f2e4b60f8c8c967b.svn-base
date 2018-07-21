package com.iqingmai.hdfs;


import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class DownloadFileTest {
	
	public static void main(String[] args) throws IOException {
		Configuration conf=new Configuration();
		FileSystem hdfs = FileSystem.get(conf);
		Path hdfsPath = new Path("/test.txt");
		Path localPath = new Path("E:\\test.txt");
		hdfs.copyToLocalFile(hdfsPath, localPath);
		hdfs.close();
	}
}
