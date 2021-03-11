package com.cgp.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.util.List;

public class XmlUtil {

    private XmlUtil() {
    }

    // 定义xml对象
    private static final XmlMapper XML_MAPPER = new XmlMapper();

    /**
     * 将对象转换为xml字符串
     *
     * @param data 对象
     * @return xml字符串
     */
    public static String objectToXml(Object data) {
        try {
            return XML_MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将xml字符串转化为对象
     *
     * @param xmlData  xml数据
     * @param beanType 对象中的object类型
     * @return {@link T}
     */
    public static <T> T xmlToObject(String xmlData, Class<T> beanType) {
        try {
            return XML_MAPPER.readValue(xmlData, beanType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json数据转换成pojo对象list
     *
     * @param xmlData  json数据
     * @param beanType bean类型
     * @return {@link List <T>}
     */
    public static <T> List<T> xmlToList(String xmlData, Class<T> beanType) {
        JavaType javaType = XML_MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            return XML_MAPPER.readValue(xmlData, javaType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析xml返回json字符串
     *
     * @param xml xml
     * @param key 字段
     * @return {@link String}
     */
    public static String parseXml(String xml, String key) {
        try {
            JsonNode rootNode = XML_MAPPER.readTree(xml);
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
}
