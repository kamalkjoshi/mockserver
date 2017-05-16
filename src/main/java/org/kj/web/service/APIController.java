package org.kj.web.service;

import static org.kj.web.service.Constants.MOCK_API_PREFIX;
import static org.kj.web.service.Constants.NO_REQUEST_MATCH;
import static org.kj.web.service.Constants.REQUEST_DIR;
import static org.kj.web.service.Constants.REQUEST_PATH;
import static org.kj.web.service.Constants.RESPONSE_DIR;
import static org.kj.web.service.Constants.UNSUPPORTED_SERVICE;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.kj.utils.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * End point for handling mock api requests.
 * 
 * @author kamal
 *
 */
@RestController
@RequestMapping(MOCK_API_PREFIX)
public class APIController {

	@Autowired
	private Environment environment;

	@RequestMapping("/**")
	@ResponseBody
	public String mockAPI(@RequestBody(required = false) String json,
			@RequestAttribute(REQUEST_PATH) String requestPath) throws Exception {
		if (!StringUtils.isEmpty(json)) {
			String response = processRequest(requestPath, json);
			return response;
		}
		return "Aggx mock server: " + requestPath;
	}

	String processRequest(String requestPath, String requestJson) throws Exception {
		assert requestPath != null : "Invalid Request path!";
		String serviceUrl = getFilePath(requestPath);

		if (serviceUrl == null) {
			return errorResponse(UNSUPPORTED_SERVICE);
		}

		String apiName = requestPath.substring(requestPath.lastIndexOf("/") + 1);

		File requestDir = ResourceUtils.getFile("classpath:" + serviceUrl + "/" + REQUEST_DIR);
		// Request filtering. can be done via file naming with request hash.
		// This is required to match response for
		String matchedFile = "";
		if (requestDir.exists() && requestDir.isDirectory()) {
			for (File file : requestDir.listFiles()) {
				String content = new String(Files.readAllBytes(file.toPath()), Charset.forName("UTF-8"));
				boolean match = matchRequest(requestJson, content, apiName);
				if (match) {
					matchedFile = file.getName();
					break;
				}
			}
		}
		if (StringUtils.isEmpty(matchedFile)) {
			return errorResponse(NO_REQUEST_MATCH);
		}
		// Process response.
		String suffix = matchedFile.substring(matchedFile.indexOf("_") + 1);
		File responseFile = ResourceUtils
				.getFile("classpath:" + serviceUrl + "/" + RESPONSE_DIR + "/" + "response_" + suffix);
		String response = new String(Files.readAllBytes(responseFile.toPath()), Charset.forName("UTF-8"));
		return response;

	}

	private String errorResponse(String type) throws Exception {
		File unsupportedService = ResourceUtils.getFile("classpath:data/error/" + type + ".json");
		String response = new String(Files.readAllBytes(unsupportedService.toPath()), Charset.forName("UTF-8"));
		return response;
	}

	private String getFilePath(String requestPath) {
		String serviceUrl = "";
		if (requestPath.startsWith(MOCK_API_PREFIX)) {
			// serviceUrl = requestPath.substring(MOCK_API_PREFIX.length() + 1);
			serviceUrl = requestPath.replace("/", ".");
			serviceUrl = environment.getProperty(serviceUrl.substring(1));
		}
		return serviceUrl;
	}

	// RequestMatching. This needs to be improved. We need to ensure field names
	// which are
	// getting compared are at same level.
	// TODO: Write JsonNode comparator which checks for field names to match.
	boolean matchRequest(String requestJson, String fileJson, String apiName) {
		JsonNode requestInFile = JSONUtils.readJsonAsNode(fileJson);
		JsonNode request = JSONUtils.readJsonAsNode(requestJson);

		String[] fields = environment.getProperty(PropertyUtils.getComparisonProperty(apiName)).split(",");
		for (String field : fields) {
			JsonNode requestNode = request.findValue(field);
			JsonNode fileNode = requestInFile.findValue(field);
			if (fileNode.equals(requestNode)) {
				return true;
			}
		}
		return false;
	}
}