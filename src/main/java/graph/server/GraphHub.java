package graph.server;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import graph.server.config.GraphHubConfiguration;
import graph.server.connect.BoltDriver;
import graph.server.connect.EmbeddedDriver;
import graph.server.model.GraphubModel;
/**
 * @author 王瑶
 *
 */
@Service
public class GraphHub {
	
	@Autowired
	BoltDriver boltDriver;
	@Autowired
	EmbeddedDriver embeddedDriver;
	@Autowired
	GraphHubConfiguration configuration;
	
	
	/**
	 * 初始化配置对象
	 * @param graphub
	 */
	public void init(GraphubModel graphub) {
		configuration.setConfiguration(graphub);
	}
	
	
	/**
	 * 获取neo4j服务器连接服务
	 * @return
	 */
	public BoltDriver getBoltDriver() {
		return boltDriver;
	}
	/**
	 * 获取neo4j嵌入式连接对象
	 * @return
	 */
	public EmbeddedDriver getEmbeddeddriver() {
		return embeddedDriver;
	}

}
