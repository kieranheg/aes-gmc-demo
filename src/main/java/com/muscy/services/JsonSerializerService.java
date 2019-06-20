package com.muscy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muscy.annotations.JsonField;
import com.muscy.exceptions.JsonSerializeException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public class JsonSerializerService {
	
	public String serialize(Object object) throws JsonSerializeException {
		try {
			Class<?> objectClass = requireNonNull(object).getClass();
			Map<String, String> jsonElements = new HashMap<>();
			
			for (Field field: objectClass.getDeclaredFields()) {
				field.setAccessible(true);
				if (field.isAnnotationPresent(JsonField.class)) {
					jsonElements.put(getSerializedKey(field), (String) field.get(object));
				}
			}
			return toJsonString(jsonElements);
		}
		catch (IllegalAccessException e) {
			throw new JsonSerializeException(e.getMessage());
		}
	}
	
	public <T> T deserialize(final String serializedStr, Class<T> valueType) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(serializedStr, valueType);
	}
	
	private String toJsonString(Map<String, String> jsonMap) {
		String elementsString = jsonMap.entrySet()
		        .stream()
		        .map(entry -> "\""  + entry.getKey() + "\":\"" + entry.getValue() + "\"")
		        .collect(joining(","));
		return "{" + elementsString + "}";
	}
	
	private static String getSerializedKey(Field field) {
		String annotationValue = field.getAnnotation(JsonField.class).value();
		
		if (annotationValue.isEmpty()) {
			return field.getName();
		}
		else {
			return annotationValue;
		}
	}
}
