package graph.server.importdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.springframework.beans.factory.annotation.Value;

public class Batch {

	@Value("${Neo4jDatabasePath}")
	String Neo4jDatabasePath;
	@Value("${Neo4jPath}")
	String Neo4jPath;
	@Value("${Data_GR}")
	String Data_GR;
	@Value("${Data_DG}")
	String Data_DG;
	@Value("${Data_YH}")
	String Data_YH;
	@Value("${Data_GRCKZH}")
	String Data_GRCKZH;
	@Value("${Data_DGCKZH}")
	String Data_DGCKZH;
	@Value("${Data_JY}")
	String Data_JY;
	@Value("${Data_DB}")
	String Data_DB;
	@Value("${Data_DK}")
	String Data_DK;
	@Value("${Data_DY}")
	String Data_DY;
	@Value("${Data_GL}")
	String Data_GL;
	@Value("${Data_ZDB}")
	String Data_ZDB;
	
	public void Update() {
		try {
			UpdateDB(Neo4jDatabasePath, Data_GR,"GR");
			UpdateDB(Neo4jDatabasePath, Data_DG,"DG");
			UpdateDB(Neo4jDatabasePath, Data_YH,"YH");
			UpdateDB(Neo4jDatabasePath, Data_GRCKZH,"GRCKZH");
			UpdateDB(Neo4jDatabasePath, Data_DGCKZH,"DGCKZH");
			UpdateDB(Neo4jDatabasePath, Data_JY,"JY");
			UpdateDB(Neo4jDatabasePath, Data_DB,"DB");
			UpdateDB(Neo4jDatabasePath, Data_DK,"DK");
			UpdateDB(Neo4jDatabasePath, Data_DY,"DY");
			UpdateDB(Neo4jDatabasePath, Data_GL,"GL");
			UpdateDB(Neo4jDatabasePath, Data_ZDB,"ZDB");
		}catch(Exception e) {
			System.out.println("文件问题");
		}
	}
	// 增加数据进入数据库
	public String UpdateDB(String databasepath, String filepath, String type) throws IllegalStateException, IOException {

		boolean index;
	
//		//读取当前文件夹目录下文件
//		File importfile=new File(filepath);
//		String filename[]=importfile.list();
		
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
		
		GraphDatabaseService batchDb = new GraphDatabaseFactory()
				.newEmbeddedDatabase(new File(databasepath).getAbsoluteFile());
		//等待索引服务上线
		try (Transaction tx = batchDb.beginTx()) {
			batchDb.schema().awaitIndexesOnline(10, TimeUnit.SECONDS);
		}

		try (Transaction tx = batchDb.beginTx()) {
			 index = batchDb.schema().getIndexes().iterator().hasNext();
			debug(index+batchDb.schema().getIndexes().toString());
		}

		batchDb.shutdown();

		final Map<String, String> config = new HashMap<>();
		config.put("cache_type", "none");
		config.put("use_memory_mapped_buffers", "true");
		config.put("dbms.memory.heap.max_size", "10g");
		BatchInserter inserter = BatchInserters.inserter(
				new File(databasepath).getAbsoluteFile(), config);

		Label p = Label.label("P");
		Label n = Label.label("N");
		Label c = Label.label("C");
		Label b = Label.label("B");
		Label per = Label.label("Per");
		Label com = Label.label("Com");
		Label ck = Label.label("CK");


		if(!index) {
		 //主键索引
		 inserter.createDeferredSchemaIndex(n).on("creditcode").create();
		 inserter.createDeferredSchemaIndex(p).on("creditcode").create();
		 inserter.createDeferredSchemaIndex(c).on("creditcode").create();
		
		 inserter.createDeferredSchemaIndex(b).on("bankcode").create();
		
		 inserter.createDeferredSchemaIndex(per).on("accountcode").create();
		 inserter.createDeferredSchemaIndex(com).on("accountcode").create();
		 inserter.createDeferredSchemaIndex(ck).on("accountcode").create();
		
		 //其他索引
		 inserter.createDeferredSchemaIndex(p).on("khxm").create();
		 inserter.createDeferredSchemaIndex(c).on("khmc").create();
		 inserter.createDeferredSchemaIndex(b).on("yxjgmc").create();
		}

		long starttime = System.currentTimeMillis();

		
		for (int i = 0; i < filepaths.size(); i++) {
//			debug(filepath + String.format("%05d", i));
//			CsvReader reader = new CsvReader(filepath
//					+"\\"+filename[i], '\001', Charset.forName("UTF-8"));
			
			//读取当前HDFS中的一个文件
			FSDataInputStream fsdis = hdfs.open(filepaths.get(i));
			BufferedReader dis = new BufferedReader(new InputStreamReader(fsdis.getWrappedStream()));

			switch (type) {
			case "GR":
				grxx(inserter, dis);
				break;
			case "DG":
				dgkh(inserter, dis);
				break;
			case "YH":
				yhjg(inserter, dis);
				break;
			case "GRCKZH":
				grckzh(inserter, dis);
				break;
			case "ZDB":
				zdb(inserter, dis);
				break;
			case "DGCKZH":
				dgckzh(inserter, dis);
				break;
			case "GL":
				gl(inserter, dis);
				break;
			case "JY":
				jy(inserter, dis);
				break;
			case "DB":
				db(inserter, dis);
				break;
			case "DK":
				dk(inserter, dis);
				break;
			case "DY":
				dy(inserter, dis);
				break;
			}
			//停止流
			dis.close();
		}

		//关闭读取分布式文件系统
		hdfs.close();
		// 插入结束
		inserter.shutdown();

		long end = (System.currentTimeMillis() - starttime) / 1000;
		debug("总共用时  " + end / 3600 + "时" + (end / 60) % 60 + "分" + end % 60
				+ "秒");

		return "数据库已更新";
	}

	// 个人基础信息
	final void grxx(BatchInserter inserter, BufferedReader reader)
			throws NumberFormatException, IOException {
		Label n = Label.label("N");
		Label p = Label.label("P");
		Map<String, Object> properties = new HashMap<>();

		long starttime = System.currentTimeMillis();

		int count = 0;
		
		String line=reader.readLine();
		while (line !=null) {
			
			String[] tmp = line.split("\001");
			if (tmp == null)
				continue;

			count++;
			properties.put("creditcode", tmp[1]);
			properties.put("khxm", tmp[2]);
			properties.put("gj", tmp[3]);
			properties.put("mz", tmp[4]);
			properties.put("xb", tmp[5]);
			properties.put("csrq", tmp[6]);
			properties.put("zy", tmp[7]);
			properties.put("gh", tmp[8]);
			properties.put("sfygbz", tmp[9]);
			properties.put("yxjgdm", tmp[10]);
			properties.put("yxjgmc", tmp[11]);
			properties.put("zhmc", tmp[12]);

			int id = Integer.parseInt(tmp[0]);

			if (inserter.nodeExists(id))
				inserter.setNodeProperties(id, properties);
			else
				inserter.createNode(id, properties, n, p);
			
			line=reader.readLine();
		}

		long end = (System.currentTimeMillis() - starttime) / 1000;
		debug("本次共插入节点数目：" + count + "插入用时  " + end / 3600 + "时" + (end / 60)
				% 60 + "分" + end % 60 + "秒");

	}

	// 公司信息
	final void dgkh(BatchInserter inserter, BufferedReader reader)
			throws NumberFormatException, IOException {
		Label n = Label.label("N");
		Label c = Label.label("C");
		Map<String, Object> properties = new HashMap<>();

		long starttime = System.currentTimeMillis();

		int count = 0;
		String line=reader.readLine();
		while (line !=null) {
			
			String[] tmp = line.split("\001");
			if (tmp == null)
				continue;

			count++;

			properties.put("creditcode", tmp[1]);
			properties.put("khmc", tmp[2]);
			properties.put("frdb", tmp[3]);
			properties.put("frdbzjhm", tmp[4]);
			properties.put("zczb", tmp[5]);
			properties.put("zcdz", tmp[6]);
			properties.put("ssxy", tmp[7]);
			properties.put("sszb", tmp[8]);
			properties.put("zzc", parseDouble(tmp[9]));
			properties.put("jzc", parseDouble(tmp[10]));
			properties.put("xzqhdm", tmp[11]);
			properties.put("fxyjxh", tmp[12]);

			int id = Integer.parseInt(tmp[0]);

			if (inserter.nodeExists(id))
				inserter.setNodeProperties(id, properties);
			else
				inserter.createNode(id, properties, n, c);
			
			line=reader.readLine();
		}

		long end = (System.currentTimeMillis() - starttime) / 1000;
		debug("本次共插入节点数目：" + count + "插入用时  " + end / 3600 + "时" + (end / 60)
				% 60 + "分" + end % 60 + "秒");

	}

	// 银行机构
	final void yhjg(BatchInserter inserter, BufferedReader reader)
			throws NumberFormatException, IOException {
		Label b = Label.label("B");

		Map<String, Object> properties = new HashMap<>();

		long starttime = System.currentTimeMillis();

		int count = 0;
		String line=reader.readLine();
		while (line !=null) {
			
			String[] tmp = line.split("\001");
			if (tmp == null)
				continue;

			count++;

			properties.put("yxjgdm", tmp[1]);
			properties.put("nbjgh", tmp[2]);
			properties.put("jrxkzh", tmp[3]);
			properties.put("yxjgmc", tmp[4]);  //TODO yxjgmc
			properties.put("jglb", tmp[5]);
			properties.put("yzbm", tmp[6]);
			properties.put("wdh", tmp[7]);
			properties.put("yyzt", tmp[8]);
			properties.put("clsj", tmp[9]);
			properties.put("jggzkssj", tmp[10]);
			properties.put("jggzzzsj", tmp[11]);
			properties.put("jgdz", tmp[12]);
			properties.put("fzrxm", tmp[13]);
			properties.put("fzrzw", tmp[14]);
			properties.put("fzrlxdh", tmp[15]);
			properties.put("cjrq", tmp[16]);

			int id = Integer.parseInt(tmp[0]);

			if (inserter.nodeExists(id))
				inserter.setNodeProperties(id, properties);
			else
				inserter.createNode(id, properties, b);
			
			line=reader.readLine();
		}

		long end = (System.currentTimeMillis() - starttime) / 1000;
		debug("本次共插入节点数目：" + count + "插入用时  " + end / 3600 + "时" + (end / 60)
				% 60 + "分" + end % 60 + "秒");

	}

	// 个人存款账户
	final void grckzh(BatchInserter inserter, BufferedReader reader)
			throws NumberFormatException, IOException {
//		Label ac = Label.label("Ac");
		Label per = Label.label("Per");
		Label ck = Label.label("CK");

		Map<String, Object> properties = new HashMap<>();

		long starttime = System.currentTimeMillis();

		int count = 0;
		String line=reader.readLine();
		while (line !=null) {
			
			String[] tmp = line.split("\001");
			if (tmp == null)
				continue;

			count++;

			properties.put("creditcode", tmp[1]);
			properties.put("ckzh", tmp[2]);
			properties.put("khtybh", tmp[3]);
			properties.put("yxjgdm", tmp[4]);
			properties.put("yxjgmc", tmp[5]);
			properties.put("zhmc", tmp[6]);
			properties.put("bzjzhbz", tmp[7]);
			properties.put("ckye", parseDouble(tmp[8]));
			properties.put("bz", "CNY");//TODO 增加币种信息

			int id = Integer.parseInt(tmp[0]);

			if (inserter.nodeExists(id))
				inserter.setNodeProperties(id, properties);
			else
				inserter.createNode(id, properties, per, ck);
			
			line=reader.readLine();
		}

		long end = (System.currentTimeMillis() - starttime) / 1000;
		debug("本次共插入节点数目：" + count + "插入用时  " + end / 3600 + "时" + (end / 60)
				% 60 + "分" + end % 60 + "秒");

	}

//	// 个人贷款账户
//	final void grdkzh(BatchInserter inserter, CsvReader reader)
//			throws NumberFormatException, IOException {
////		Label ac = Label.label("Ac");
//		Label per = Label.label("Person");
//		Label dk = Label.label("DK");
//
//		Map<String, Object> properties = new HashMap<>();
//
//		long starttime = System.currentTimeMillis();
//
//		int count = 0;
//		while (reader.readRecord()) {
//
//			String[] tmp = reader.getValues();
//			if (tmp == null)
//				continue;
//
//			count++;
//
//			properties.put("zjhm", tmp[1]);
//			properties.put("dkfhzh", tmp[2]);
//			properties.put("xdjjh", tmp[3]);
//			properties.put("khtybh", tmp[4]);
//			properties.put("yxjgdm", tmp[5]);
//			properties.put("mxkmbh", tmp[6]);
//			properties.put("yxjgmc", tmp[7]);
//			properties.put("mxkmmc", tmp[8]);
//			properties.put("zhmc", tmp[9]);
//			properties.put("bz", tmp[10]);
//			properties.put("zjhkrq", tmp[11]);
//			properties.put("dkhth", tmp[12]);
//			properties.put("xdyxm", tmp[13]);
//			properties.put("dkwjfl", tmp[14]);
//			properties.put("hkzh", tmp[15]);
//			properties.put("dkrzzh", tmp[16]);
//			properties.put("dkbjze", parseDouble(tmp[17]));
//			properties.put("dkzcye", parseDouble(tmp[18]));
//			properties.put("dkyqye", parseDouble(tmp[19]));
//			properties.put("qbye", parseDouble(tmp[20]));
//			properties.put("dqrq", tmp[21]);
//			properties.put("qxrq", tmp[22]);
//			properties.put("khrq", tmp[23]);
//			properties.put("qxrq_num", tmp[24]);
//
//			int id = Integer.parseInt(tmp[0]);
//
//			if (inserter.nodeExists(id))
//				inserter.setNodeProperties(id, properties);
//			else
//				inserter.createNode(id, properties, per, dk);
//		}
//
//		long end = (System.currentTimeMillis() - starttime) / 1000;
//		debug("本次共插入节点数目：" + count + "插入用时  " + end / 3600 + "时" + (end / 60)
//				% 60 + "分" + end % 60 + "秒");
//
//	}

	// 对公存款账户
	final void dgckzh(BatchInserter inserter, BufferedReader reader)
			throws NumberFormatException, IOException {
//		Label ac = Label.label("Ac");
		Label com = Label.label("Com");
		Label ck = Label.label("CK");

		Map<String, Object> properties = new HashMap<>();

		long starttime = System.currentTimeMillis();

		int count = 0;
		String line=reader.readLine();
		while (line !=null) {
			
			String[] tmp = line.split("\001");
			if (tmp == null)
				continue;

			count++;

			properties.put("creditcode", tmp[1]);
			properties.put("ckzh", tmp[2]);
			properties.put("khtybh", tmp[3]);
			properties.put("yxjgdm", tmp[4]);
			properties.put("yxjgmc", tmp[5]);
			properties.put("bz", tmp[6]);
			properties.put("zhmc", tmp[7]);
			properties.put("bzjzhbz", tmp[8]);
			properties.put("ckye", parseDouble(tmp[9]));

			int id = Integer.parseInt(tmp[0]);

			if (inserter.nodeExists(id))
				inserter.setNodeProperties(id, properties);
			else
				inserter.createNode(id, properties,com, ck);
			
			line=reader.readLine();
		}

		long end = (System.currentTimeMillis() - starttime) / 1000;
		debug("本次共插入节点数目：" + count + "插入用时  " + end / 3600 + "时" + (end / 60)
				% 60 + "分" + end % 60 + "秒");

	}

//	// 对公贷款账户
//	final void dgdkzh(BatchInserter inserter, CsvReader reader)
//			throws NumberFormatException, IOException {
////		Label ac = Label.label("Ac");
//		Label com = Label.label("Company");
//		Label dk = Label.label("DK");
//
//		Map<String, Object> properties = new HashMap<>();
//
//		long starttime = System.currentTimeMillis();
//
//		int count = 0;
//		while (reader.readRecord()) {
//
//			String[] tmp = reader.getValues();
//			if (tmp == null)
//				continue;
//
//			count++;
//
//			properties.put("zzjgdm", tmp[1]);
//			properties.put("dkfhzh", tmp[2]);
//			properties.put("xdjjh", tmp[3]);
//			properties.put("khtybh", tmp[4]);
//			properties.put("yxjgdm", tmp[5]);
//			properties.put("mxkmbh", tmp[6]);
//			properties.put("yxjgmc", tmp[7]);
//			properties.put("mxkmmc", tmp[8]);
//			properties.put("zhmc", tmp[9]);
//			properties.put("bz", tmp[10]);
//			properties.put("zjhkrq", tmp[11]);
//			properties.put("dkhth", tmp[12]);
//			properties.put("dkwjfl", tmp[13]);
//			properties.put("hkzh", tmp[14]);
//			properties.put("dkrzzh", tmp[15]);
//			properties.put("dkbjze", parseDouble(tmp[16]));
//			properties.put("dkzcye", parseDouble(tmp[17]));
//			properties.put("dkyqye", parseDouble(tmp[18]));
//			properties.put("qbye", parseDouble(tmp[19]));
//			properties.put("bwqxye", parseDouble(tmp[20]));
//			properties.put("dqrq", tmp[21]);
//			properties.put("qxrq", tmp[22]);
//			properties.put("khrq", tmp[23]);
//			properties.put("xhrq", tmp[24]);
//			properties.put("yqrq", tmp[25]);
//			properties.put("zhzt", tmp[26]);
//			properties.put("qxrq_num", tmp[27]);
//
//			int id = Integer.parseInt(tmp[0]);
//
//			if (inserter.nodeExists(id))
//				inserter.setNodeProperties(id, properties);
//			else
//				inserter.createNode(id, properties,com, dk);
//		}
//		long end = (System.currentTimeMillis() - starttime) / 1000;
//		debug("本次共插入节点数目：" + count + "插入用时  " + end / 3600 + "时" + (end / 60)
//				% 60 + "分" + end % 60 + "秒");
//
//	}

	// 交易信息
	final void jy(BatchInserter inserter, BufferedReader reader)
			throws NumberFormatException, IOException {

		Map<String, Object> properties = new HashMap<>();

		long starttime = System.currentTimeMillis();

		int count = 0;
		String line=reader.readLine();
		while (line !=null) {
			
			String[] tmp = line.split("\001");
			if (tmp == null)
				continue;
            count++;
			properties.put("jyzh", tmp[1]);
			properties.put("dfxh", tmp[2]);
			properties.put("jygx", tmp[3]);
			properties.put("hxjylsh", tmp[4]);
			properties.put("bcxh", tmp[5]);
			properties.put("jyrq", tmp[6]);
			properties.put("jyrq_sj", tmp[7]);
			properties.put("yxjgdm", tmp[8]);
			properties.put("mxkmbh", tmp[9]);
			properties.put("jysj", tmp[10]);
			properties.put("jyjgmc", tmp[11]);
			properties.put("jyjgmc_zh", tmp[12]);
			properties.put("jyhm", tmp[13]);
			properties.put("dfjgmc", tmp[14]);
			properties.put("dfzh", tmp[16]);
			properties.put("dfhm", tmp[17]);
			properties.put("jyje", parseDouble(tmp[18]));
			properties.put("zhye", parseDouble(tmp[19]));
			properties.put("jyjdbz", tmp[20]);
			properties.put("xzbz", tmp[21]);
			properties.put("bz", tmp[22]);
			properties.put("ywlx", tmp[23]);
			properties.put("jylx", tmp[24]);
			properties.put("jyqd", tmp[25]);
			properties.put("jyjzh", tmp[26]);
			properties.put("czgyh", tmp[27]);
			properties.put("zy", tmp[28]);
			properties.put("zhbz", tmp[29]);
			properties.put("kxhbz", tmp[30]);
			properties.put("cjrq", tmp[31]);
			
			if(tmp[0].equals("\\N")||tmp[15].equals("\\N"))
				continue;

			int startid = Integer.parseInt(tmp[0]);
			int endid = Integer.parseInt(tmp[15]);

			RelationshipType rel = RelationshipType.withName("JY");

				
			try {
				inserter.createRelationship(startid, endid, rel, properties);
			} catch (Exception e) {
				error("错误行号：" + count);
			}
			

			line=reader.readLine();
		}
		long end = (System.currentTimeMillis() - starttime) / 1000;
		debug("本次共插入关系数目：" + count + "插入用时  " + end / 3600 + "时" + (end / 60)
				% 60 + "分" + end % 60 + "秒");

	}

	// 担保关系
	final void db(BatchInserter inserter, BufferedReader reader)
			throws NumberFormatException, IOException {

		Map<String, Object> properties = new HashMap<>();

		long starttime = System.currentTimeMillis();

		int count = 0;
		String line=reader.readLine();
		while (line !=null) {
			
			String[] tmp = line.split("\001");
			if (tmp == null)
				continue;

			count++;

			properties.put("dkrzjhm", tmp[1]);
			properties.put("dbrzjhm", tmp[3]);
			properties.put("db", tmp[4]);
			properties.put("yxjgdm", tmp[5]);
			properties.put("yxjgmc", tmp[6]);
			properties.put("yxjgmc_zh", tmp[7]);
			properties.put("dbqsrq", tmp[8]);
			properties.put("dbqsrq_sj", tmp[9]);
			properties.put("dbdqrq", tmp[10]);
			properties.put("dbdqrq_sj", tmp[11]);
			properties.put("dbhtzt", tmp[12]);
			properties.put("dbzje", parseDouble(tmp[13]));
			properties.put("dbbz", tmp[14]);

			if(tmp[0].equals("\\N")||tmp[2].equals("\\N"))
				continue;
			
			int startid = Integer.parseInt(tmp[2]);
			int endid = Integer.parseInt(tmp[0]);

			if(startid==endid) continue;//去除自己担保
			
			RelationshipType rel = RelationshipType.withName("DB");

			try {
				inserter.createRelationship(startid, endid, rel, properties);
			} catch (Exception e) {
				error("错误行号：" + count);
			}
			
			line=reader.readLine();
		}

		long end = (System.currentTimeMillis() - starttime) / 1000;
		debug("本次共插入关系数目：" + count + "插入用时  " + end / 3600 + "时" + (end / 60)
				% 60 + "分" + end % 60 + "秒");

	}

	// 贷款关系
	final void dk(BatchInserter inserter, BufferedReader reader)
			throws NumberFormatException, IOException {

		Map<String, Object> properties = new HashMap<>();

		long starttime = System.currentTimeMillis();

		int count = 0;
		String line=reader.readLine();
		while (line !=null) {
			
			String[] tmp = line.split("\001");
			if (tmp == null)
				continue;

			count++;
			properties.put("yxjgdm", tmp[1]);
			properties.put("zjhm", tmp[2]);
			properties.put("dkrzzh", tmp[4]);
			properties.put("dkgx", tmp[5]);
			properties.put("xdjjh", tmp[6]);
			properties.put("khtybh", tmp[7]);
			properties.put("khmc", tmp[8]);
			properties.put("xdhth", tmp[9]);
			properties.put("yxjgmc", tmp[10]);
			properties.put("yxjgmc_zh", tmp[11]);
			properties.put("xdywzl", tmp[12]);
			properties.put("bz", tmp[13]);
			properties.put("je", parseDouble(tmp[14]));
			properties.put("jkye", parseDouble(tmp[15]));
			properties.put("dkqx", tmp[16]);
			properties.put("zqcs", tmp[17]);
			properties.put("zqs", tmp[18]);
			properties.put("fkfs", tmp[19]);
			properties.put("dksjffrq", tmp[20]);
			properties.put("dksjffrq_sj", tmp[21]);
			properties.put("dksjdqrq", tmp[22]);
			properties.put("dksjdqrq_sj", tmp[23]);
			properties.put("dklx", tmp[24]);
			properties.put("dkwjfl", tmp[25]);
			properties.put("hkfs", tmp[26]);
			properties.put("hkzh", tmp[27]);
			properties.put("bzjje", tmp[28]);
			properties.put("xdyxm", tmp[29]);
			properties.put("xdygh", tmp[30]);
			properties.put("zydbfs", tmp[31]);
			properties.put("dkyt", tmp[32]);
			properties.put("dktxxy", tmp[33]);

			if(tmp[0].equals("\\N")||tmp[3].equals("\\N"))
				continue;
			
			int startid = Integer.parseInt(tmp[0]);
			int endid = Integer.parseInt(tmp[3]);

			RelationshipType rel = RelationshipType.withName("DK");

			try {
				inserter.createRelationship(startid, endid, rel, properties);
			} catch (Exception e) {
				error("错误行号：" + count);
			}

			line=reader.readLine();
		}

		long end = (System.currentTimeMillis() - starttime) / 1000;
		debug("本次共插入关系数目：" + count + "插入用时  " + end / 3600 + "时" + (end / 60)
				% 60 + "分" + end % 60 + "秒");

	}

	// 对应关系
	final void dy(BatchInserter inserter, BufferedReader reader)
			throws NumberFormatException, IOException {

		Map<String, Object> properties = new HashMap<>();

		long starttime = System.currentTimeMillis();

		int count = 0;
		String line=reader.readLine();
		while (line !=null) {
			
			String[] tmp = line.split("\001");
			if (tmp == null)
				continue;
			
			count++;
			
			if(tmp[0].equals("\\N")||tmp[1].equals("\\N"))
				continue;
			
			int startid = Integer.parseInt(tmp[1]);
			int endid = Integer.parseInt(tmp[0]);

			RelationshipType rel = RelationshipType.withName("DY");

			try {
				inserter.createRelationship(startid, endid, rel, properties);
			} catch (Exception e) {
				error("错误行号：" + count);
			}
			
			line=reader.readLine();
		}

		long end = (System.currentTimeMillis() - starttime) / 1000;
		debug("本次共插入关系数目：" + count + "插入用时  " + end / 3600 + "时" + (end / 60)
				% 60 + "分" + end % 60 + "秒");

	}
	
	// 关联关系
	final void gl(BatchInserter inserter, BufferedReader reader)
			throws NumberFormatException, IOException {

		Map<String, Object> properties = new HashMap<>();

		long starttime = System.currentTimeMillis();

		int count = 0;
		String line=reader.readLine();
		while (line !=null) {
			
			String[] tmp = line.split("\001");
			if (tmp == null)
				continue;
			
			count++;
						
			int startid = Integer.parseInt(tmp[4]);
			int endid = Integer.parseInt(tmp[0]);
			
			properties.put("gxlx", tmp[10]);
			
			RelationshipType rel = RelationshipType.withName("GL");

			try {
				inserter.createRelationship(startid, endid, rel, properties);
			} catch (Exception e) {
				error("错误行号：" + count);
			}
			
			line=reader.readLine();
		}

		long end = (System.currentTimeMillis() - starttime) / 1000;
		debug("本次共插入关系数目：" + count + "插入用时  " + end / 3600 + "时" + (end / 60)
				% 60 + "分" + end % 60 + "秒");

	}

	// 总担保关系
	final void zdb(BatchInserter inserter, BufferedReader reader)
			throws NumberFormatException, IOException {

		Map<String, Object> properties = new HashMap<>();

		long starttime = System.currentTimeMillis();

		int count = 0;
		String line=reader.readLine();
		while (line !=null) {
			
			String[] tmp = line.split("\001");
			if (tmp == null)
				continue;

			count++;

			properties.put("zdbcs", tmp[2]);
			properties.put("zdbje", tmp[3]);

			if(tmp[1].equals("\\N")||tmp[3].equals("\\N"))
				continue;
			
			int startid = Integer.parseInt(tmp[0]);
			int endid = Integer.parseInt(tmp[1]);

			if(startid==endid) continue;//去除自己担保
			
			RelationshipType rel = RelationshipType.withName("ZDB");

			try {
				inserter.createRelationship(startid, endid, rel, properties);
			} catch (Exception e) {
				error("错误行号：" + count);
			}
			line=reader.readLine();
		}

		long end = (System.currentTimeMillis() - starttime) / 1000;
		debug("本次共插入关系数目：" + count + "插入用时  " + end / 3600 + "时" + (end / 60)
				% 60 + "分" + end % 60 + "秒");

	}
	
	
	Double parseDouble(String arg) {
		Double res = 0.0;
		try {
			res = Double.parseDouble(arg);
		} catch (Exception e) {
			res = 0.0;
		}
		return res;
	}

	void debug(String s) {
		System.out.println(new Date().toLocaleString() + " >>> " + s);
	}

	void error(String s) {
		System.err.println(new Date().toLocaleString() + " >>> " + s);
	}
	
	//更新前删除数据库
	public void Deletedatabase() {
		
		File databasefile=new File(Neo4jDatabasePath);
		File files[]=databasefile.listFiles();
		
		//清空数据库文件
		for(File f:files){
			f.delete();
		}
	}
}
