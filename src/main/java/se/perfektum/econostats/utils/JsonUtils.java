package se.perfektum.econostats.utils;

import static com.google.gson.JsonParser.parseString;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class JsonUtils {

    /**
     * Gets a list of top element objects <T> from provided json String.
     *
     * @param clazz The type element to extract from json file
     * @param json  The json file as a simple String
     * @param <T>   Type parameter
     * @return List of objects of given type
     */
    public static <T> List<T> getJsonElement(Class<T> clazz, String json) {
        JsonObject transactions = (JsonObject) parseString(json);

        Type listType = TypeToken.getParameterized(ArrayList.class, clazz).getType();

        String elementName = Character.toLowerCase(clazz.getSimpleName().charAt(0)) + clazz.getSimpleName().substring(1) + "s";
        List<T> result = new Gson().fromJson(transactions.getAsJsonArray(elementName), listType);

        return result;
    }

    /**
     * Converts any json list to a String representation of a json object
     *
     * @param json        List of json objects
     * @param rootElement The root element of the json object
     * @return String representation of json object
     */
    public static String convertObjectsToJson(List<?> json, String rootElement) {
        Map<String, List<?>> m = new TreeMap<>();
        m.put(rootElement, json);
        return new Gson().toJson(m);
    }
}
