package org.kj.utils;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(JSONUtils.class);

	private static ObjectMapper objectMapper;

	public static <T, C extends Collection<T>> C createCollection(String json,
			Class<T> type, Class<C> colType) {

		C tokens = null;
		ObjectMapper objectMapper = objectMapper();
		try {
			tokens = objectMapper.readValue(json, objectMapper.getTypeFactory()
					.constructCollectionType(colType, type));
		} catch (IOException e) {
			logger.error("Invalid JSON: " + json + " Collection Type: "
					+ colType + " Type: " + type, e);
		}
		return tokens;
	}

	public static String writeJson(Object object, boolean formatRequired) {
		String value = null;
		ObjectMapper objectMapper = null;
		objectMapper = objectMapper();
		try {
			if (formatRequired) {
				// format the JSON
				value = objectMapper.writerWithDefaultPrettyPrinter()
						.writeValueAsString(object);
			} else {
				value = objectMapper.writeValueAsString(object);
			}
		} catch (JsonProcessingException e) {
			logger.error("Error writting JSON : " + e.getMessage());
		}
		return value;
	}

	// returns custom object mapper, if already initialized
	// if not then tries to initialize and return custom object mapper
	// otherwise returns a default object mapper
	private static ObjectMapper objectMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
			objectMapper.configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			objectMapper.configure(
					DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL,
					true);
			objectMapper.configure(
					DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
			objectMapper.configure(
					DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
		}
		return objectMapper;
	}

	public static Optional<Map<?, ?>> readJsonAsMap(String data) {
		Map<?, ?> jsonMap = null;
		ObjectMapper objectMapper = objectMapper();
		try {
			jsonMap = objectMapper.readValue(data, Map.class);
		} catch (IOException e) {
			logger.error("Error reading JSON : " + e.getMessage());
		}
		return Optional.ofNullable(jsonMap);
	}

	public static JsonNode readJsonAsNode(String data) {
		JsonNode jsonNode = null;
		ObjectMapper objectMapper = objectMapper();
		try {
			jsonNode = objectMapper.readTree(data);
		} catch (IOException e) {
			logger.error("Error reading JSON : " + e.getMessage());
		}
		return jsonNode;
	}

	public static <T> T readJson(String data, Class<T> clazz) {
		T value = null;
		ObjectMapper objectMapper = objectMapper();
		try {
			value = objectMapper.readValue(data, clazz);
		} catch (IOException e) {
			logger.error("Error reading JSON : " + e.getMessage());
		}
		return value;
	}

	public static <T> T readJson(String data, Class<T> clazz, boolean safe) {
		if (!safe)
			return readJson(data, clazz);

		T value = null;
		ObjectMapper objectMapper = objectMapper();
		try {
			value = objectMapper.readValue(data, clazz);
		} catch (IOException e) {
			logger.error("Error reading JSON : " + e.getMessage());
		}
		return value;
	}
}