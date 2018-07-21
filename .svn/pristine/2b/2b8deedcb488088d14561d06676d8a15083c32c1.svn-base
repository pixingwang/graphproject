package graph.server.tools;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import graph.server.config.GraphHubConfiguration;






@Component
public class GetFileSize {
	@Autowired
	GraphHubConfiguration graphHubConfiguration;
	
	private final static ForkJoinPool forkJoinPool = new ForkJoinPool();
	

	private static class FileSizeFinder extends RecursiveTask<Long> {
		final File file;

		public FileSizeFinder(final File theFile) {
			file = theFile;
		}

		@Override
		public Long compute() {
			long size = 0;
			if (file.isFile()) {
				size = file.length();
			} else {
				final File[] children = file.listFiles();
				if (children != null) {
					List<ForkJoinTask<Long>> tasks = new ArrayList<ForkJoinTask<Long>>();
					for (final File child : children) {
						if (child.isFile()) {
							size += child.length();
						} else {
							tasks.add(new FileSizeFinder(child));
						}
					}
					for (final ForkJoinTask<Long> task : invokeAll(tasks)) {
						size += task.join();
					}
				}
			}
			return size;
		}
	}
	
	public long getSize() throws URISyntaxException {
		long total = forkJoinPool.invoke(new FileSizeFinder(new File(new URI(graphHubConfiguration.getEmbbeded_databasePath()))));
		return total;
	}
}
