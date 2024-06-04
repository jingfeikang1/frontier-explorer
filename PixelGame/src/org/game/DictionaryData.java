package org.game;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Converts data from resources.dictionary to Java objects
public class DictionaryData {
	public static Map<String, Map<String, Object>> locationData = new HashMap<>();
	public static Map<String, Map<String, Object>> objectData = new HashMap<>();
	public static Map<String, ArrayList<Map<String, Object>>> itemTransformation = new HashMap<>();
	public static ArrayList<ArrayList<String>> gameMap = new ArrayList<>();
	public static Map<String, ArrayList<String>> objectList = new HashMap<>();
	
	public static void init() {
		// load location dictionary
		String json = null;
		try {
			json = Files.readString(Path.of("src/resources/dictionary/LocationData.json"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		locationData = new flexjson.JSONDeserializer<Map<String, Map<String, Object>>>().deserialize(json);
		
		// load object dictionary
		json = null;
		try {
			json = Files.readString(Path.of("src/resources/dictionary/ObjectData.json"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		objectData = new flexjson.JSONDeserializer<Map<String, Map<String, Object>>>().deserialize(json);
		
		// load map
		json = null;
		try {
			json = Files.readString(Path.of("src/resources/dictionary/GameMap.json"));
			gameMap = new flexjson.JSONDeserializer<ArrayList<ArrayList<String>>>().deserialize(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// load item transformation data
		json = null;
		try {
			json = Files.readString(Path.of("src/resources/dictionary/ItemTransformation.json"));
			itemTransformation = new flexjson.JSONDeserializer<Map<String, ArrayList<Map<String, Object>>>>().deserialize(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// load object list
		json = null;
		try {
			json = Files.readString(Path.of("src/resources/dictionary/ObjectList.json"));
			objectList = new flexjson.JSONDeserializer<Map<String, ArrayList<String>>>().deserialize(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static <T> T getObjectData(String type, String property) {
		return (T) objectData.get(type).get(property);
	}
	
	public static <T> T getObjectData(String type, String property, T placeholder) {
		var value = objectData.get(type).get(property);
		if (value == null) {
			return placeholder;
		}
		return (T) value;
	}
}
