package se.perfektum.econostats.common;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
        JsonParser parser = new JsonParser();
        JsonObject transactions = (JsonObject) parser.parse(json);

        Type lolType = TypeToken.getParameterized(ArrayList.class, clazz).getType();

        String elementName = Character.toLowerCase(clazz.getSimpleName().charAt(0)) + clazz.getSimpleName().substring(1) + "s";
        List<T> result = new Gson().fromJson(transactions.getAsJsonArray(elementName), lolType);

        return result;
    }
}
