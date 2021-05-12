package com.cgp.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Chen
 */
@SuppressWarnings("unused")
public class JsonUtil {

    private JsonUtil() {
    }

    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 将对象转换成json字符串。
     * Jackson只会把那些拥有getter方法的属性或者声明为public的字段序列化，否则不会被该字段不会被序列化。
     *
     * @param data 对象
     * @return json字符串
     */
    public static String objectToJson(Object data) {
        try {
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将json结果集转化为对象
     *
     * @param jsonData json数据
     * @param beanType 对象中的object类型
     * @return {@link T}
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        try {
            return MAPPER.readValue(jsonData, beanType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json数据转换成pojo对象list
     *
     * @param jsonData json数据
     * @param beanType bean类型
     * @return {@link List<T>}
     */
    public static <T> List<T> jsonToList(String jsonData, Class<T> beanType) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            return MAPPER.readValue(jsonData, javaType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 解析json返回字符串
     *
     * @param json json
     * @param key  字段
     * @return {@link String}
     */
    public static String parseJson(String json, String key) {
        try {
            JsonNode rootNode = MAPPER.readTree(json);
            JsonNode path = rootNode.path(key);
            if (path.isTextual()) {
                return path.asText();
            } else {
                return path.toString();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析json返回布尔值
     *
     * @param json json
     * @param key  key
     * @return {@link Boolean}
     */
    public static Boolean parseJsonToBool(String json, String key) {
        try {
            JsonNode rootNode = MAPPER.readTree(json);
            JsonNode path = rootNode.path(key);
            if (path.isBoolean()) {
                return path.asBoolean();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析json返回数值
     *
     * @param json json
     * @param key  关键
     * @return {@link Integer}
     */
    public static Integer parseJsonToInteger(String json, String key) {
        try {
            JsonNode node = MAPPER.readTree(json).path(key);
            if (node.isInt()) {
                return node.asInt();
            } else {
                String value = node.toString();
                return castToInt(value);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析数组
     *
     * @param json  json
     * @param index 数组下标
     * @return {@link String}
     */
    public static String parseArray(String json, int index) {
        try {
            JsonNode jsonNode = MAPPER.readTree(json);
            if (jsonNode.isArray()) {
                int size = jsonNode.size();
                if (index >= size) {
                    throw new RuntimeException("超出数组范围");
                }
                JsonNode node = jsonNode.get(index);
                return node.toString();
            } else {
                throw new RuntimeException("不是Json数组");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析数组
     *
     * @param json json
     * @return {@link List<String>}
     */
    public static List<String> parseArray(String json) {
        try {
            JsonNode jsonNode = MAPPER.readTree(json);
            if (!jsonNode.isArray()) {
                throw new RuntimeException("不是Json数组");
            }
            int size = jsonNode.size();
            List<String> list = new ArrayList<>();
            for (JsonNode node : jsonNode) {
                list.add(node.toString());
            }
            return list;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析Json数组获取第一个数组
     *
     * @param json json
     * @param key  key
     * @return {@link String}
     */
    public static String parseArrayJson(String json, String key) {
        try {
            JsonNode jsonNode = MAPPER.readTree(json);
            if (jsonNode.isArray()) {
                JsonNode node = jsonNode.get(0);
                JsonNode path = node.path(key);
                if (path.isTextual()) {
                    return path.asText();
                } else {
                    return path.toString();
                }
            } else {
                throw new RuntimeException("不是Json数组");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取数组的长度
     * 如果不是数组则返回0
     *
     * @param json json
     * @return {@link String}
     */
    public static int arrayLength(String json) {
        try {
            JsonNode jsonNode = MAPPER.readTree(json);
            if (jsonNode.isArray()) {
                return jsonNode.size();
            } else {
                return 0;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 判断是否存在某key
     *
     * @param json json
     * @param key  key
     * @return boolean
     */
    public static boolean containsKey(String json, String key) {
        try {
            JsonNode jsonNode = MAPPER.readTree(json);
            return jsonNode.has(key);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Integer castToInt(String value) {
        if (value.length() == 0 || "null".equals(value) || "NULL".equals(value)) {
            return null;
        }
        if (value.contains("\"")) {
            value = value.replaceAll("\"", "");
        }
        return Integer.parseInt(value);
    }

}
