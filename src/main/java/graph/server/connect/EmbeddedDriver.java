package graph.server.connect;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import org.neo4j.ogm.exception.ConnectionException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import graph.server.config.GraphHubConfiguration;


@Component
public class EmbeddedDriver {
	@Autowired
	GraphHubConfiguration graphHubConfiguration;

	private static GraphDatabaseService graphDb;
	private static final int TIMEOUT = 60_000;
	

	public synchronized void configure() {
		try {
			String fileStoreUri = graphHubConfiguration.getEmbbeded_databasePath();
			System.out.println(graphHubConfiguration.getEmbbeded_databasePath());
			// if no URI is set, create a temporary folder for the graph db
			// that will persist only for the duration of the JVM
			// This is effectively what the ImpermanentDatabase does.
			if (fileStoreUri == null) {
				//fileStoreUri = createTemporaryFileStore();
				System.out.println("EmbeddedPath id Null!");
			} else {
				createPermanentFileStore(fileStoreUri);
			}
			File file = new File(new URI(fileStoreUri));
			if (!file.exists()) {
				throw new RuntimeException("Could not create/open filestore: " + fileStoreUri);
			}
			// do we want to start a HA instance or a community instance?
			setGraphDatabase(file);
		} catch (Exception e) {
			throw new ConnectionException("Error connecting to embedded graph", e);
		}
	}

	private void setGraphDatabase(File file) {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(file);
		System.out.println(graphDb.hashCode());
	}

	public GraphDatabaseService getGraphDatabaseService() throws IOException {
		if (graphDb != null && graphDb.isAvailable(TIMEOUT)) {
			return graphDb;
		} else {
			//setUp();
			configure();
			return graphDb;
		}
	}


	private String createTemporaryFileStore() {
		try {
			Path path = Files.createTempDirectory("neo4j.db");
			final File f = path.toFile();
			URI uri = f.toURI();
			final String fileStoreUri = uri.toString();
			System.out.println(("Creating temporary file store: " + fileStoreUri));

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				close();
				try {
					System.out.println(("Deleting temporary file store: " + fileStoreUri));
					FileUtils.deleteDirectory(f);
				} catch (IOException e) {
					throw new RuntimeException("Failed to delete temporary files in " + fileStoreUri, e);
				}
			}));

			return fileStoreUri;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void createPermanentFileStore(String strPath) {
		System.out.println(strPath);
		try {
			URI uri = new URI(strPath);
			File file = new File(uri);
			if (!file.exists()) {
				Path graphDir = Files.createDirectories(file.toPath());
				System.out.println(("Creating new permanent file store: " + graphDir.toString()));
			}
			Runtime.getRuntime().addShutdownHook(new Thread(this::close));
		} catch (FileAlreadyExistsException e) {
			System.out.println(("Using existing permanent file store: " + strPath));
		} catch (IOException | URISyntaxException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	public synchronized void close() {

		if (graphDb != null) {
			System.out.println("Shutting down Embedded driver {} " + graphDb.hashCode());
			graphDb.shutdown();
			graphDb = null;
		}
	}
}
