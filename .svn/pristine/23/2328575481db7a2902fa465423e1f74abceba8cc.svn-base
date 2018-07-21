package com.iqingmai.hdfs;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

public class ListFileTest {
	
	public static void main(String[] args) throws IOException {
		Configuration conf=new Configuration();
		FileSystem hdfs = FileSystem.get(conf);
		Path hdfsPath = new Path("/test");
		RemoteIterator<LocatedFileStatus> it = hdfs.listFiles(hdfsPath, false);
		while (it.hasNext()) {
			LocatedFileStatus status = it.next();
			if (status.isFile()) {
				System.out.println(status.getPath().toString());
			}
		}
		hdfs.close();
	}
}
