package graph.server.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import graph.server.importdata.ImportSet;
import graph.server.service.GraphEmbeddedDriverService;
import graph.server.service.ImportService;

@RestController
public class ImportController {

	@Autowired
	ImportService importservice;
	@Autowired
	GraphEmbeddedDriverService graphEmbeddedDriverService;

	/**
	 * @param path
	 *            数据源路径（文本数据用）
	 * @param encoding
	 *            编码方式
	 * @param separator
	 *            分隔符
	 * @return 预览的数据源的前几行数据数组
	 */
	@RequestMapping(value = "/previewtxt", method = RequestMethod.POST)
	@ResponseBody
	public List<String[]> Previewtxt(@RequestParam(value = "path") String path,
			@RequestParam(value = "encoding") String encoding, @RequestParam(value = "separator") String separator) {

		List<String[]> result = importservice.getpreviewtext(path, encoding, separator);

		return result;
	}

	/**
	 * @param address
	 *            数据源地址
	 * @param port
	 *            数据源端口号（数据库有）
	 * @param path
	 *            数据源路径（文本数据用）
	 * @param encoding
	 *            编码方式
	 * @param separator
	 *            分隔符
	 * @return 预览的数据源的前几行数据数组
	 */
	@RequestMapping(value = "/previewdatabase", method = RequestMethod.POST)
	@ResponseBody
	public String Previewdatabase(@RequestParam(value = "database_type") String database_type,
			@RequestParam(value = "address") String address, @RequestParam(value = "port") String port,
			@RequestParam(value = "path") String path, @RequestParam(value = "database_name") String database_name,
			@RequestParam(value = "user_name") String user_name, @RequestParam(value = "password") String password) {

		String s = address + " " + port + " " + path;

		return s;
	}

	@RequestMapping(value = "/batchimportntxt", method = RequestMethod.POST)
	@ResponseBody
	public String Batchimporttxt(@RequestBody ImportSet importset)
			throws IllegalStateException, IOException, InterruptedException, URISyntaxException {

		if (importset.getType()) {
			importservice.ImportNodefromtxt(importset);
		} else {
			importservice.ImportRelfromtxt(importset);
		}

		return "导入成功";
	}

	@RequestMapping(value = "/getAllNodeLabelAndProperty", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<String>> getAllNodeLabelAndReferProperty() throws IOException {

		Map<String, List<String>> allNodeLabelAndReferProperty = new HashMap<>();
		allNodeLabelAndReferProperty = graphEmbeddedDriverService.getAllNodeLabelAndReferProperty();
		System.out.println(allNodeLabelAndReferProperty);
		return allNodeLabelAndReferProperty;

	}

}
