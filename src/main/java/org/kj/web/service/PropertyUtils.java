package org.kj.web.service;

public class PropertyUtils {

	private PropertyUtils() {
	}

	public static String getComparisonProperty(String apiName) {
		return "field.compare." + apiName;
	}
}