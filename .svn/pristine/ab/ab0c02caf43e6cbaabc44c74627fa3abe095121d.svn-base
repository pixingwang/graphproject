package com.iqingmai.hdfs;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class ReadFileTest {
	
	public static void main111() throws IOException {
		Configuration conf=new Configuration();
		System.out.println(conf.getResource("core-site.xml").getPath());
		FileSystem hdfs = FileSystem.get(conf);
		Path hdfsPath = new Path("/user/root/6.txt");
		FSDataInputStream fsdis = hdfs.open(hdfsPath);
		BufferedReader dis = new BufferedReader(new InputStreamReader(fsdis.getWrappedStream()));
		String line = dis.readLine();
		while (null != line) {
			System.out.println(line);
			line = dis.readLine();
		}
		hdfs.close();
	}
}
