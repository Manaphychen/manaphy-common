package com.cgp.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 转换工具
 *
 * @author Manaphy
 * @date 2021/05/14
 */
@Slf4j
public class ConvertUtils {

    private ConvertUtils() {
    }

    /**
     * 转换
     *
     * @param source 源
     * @param target 目标
     */
    public static void convert(Object source, Object target) {
        BeanUtils.copyProperties(source, target);
    }

    /**
     * 转换
     *
     * @param source           源
     * @param clazz            目标类
     * @param ignoreProperties 忽略属性
     * @return {@link T}
     */
    public static <T> T convert(Object source, Class<T> clazz, String... ignoreProperties) {
        if (source == null) return null;

        T target = BeanUtils.instantiateClass(clazz);
        BeanUtils.copyProperties(source, target, ignoreProperties);
        return target;
    }

    /**
     * 转换
     *
     * @param source 源
     * @param clazz  目标类
     * @return {@link T}
     */
    public static <T> T convert(Object source, Class<T> clazz) {
        return convert(source, clazz, (String[]) null);
    }


    /**
     * 转换
     *
     * @param source 源 List
     * @param clazz  目标类
     * @return {@link List<T>}
     */
    @SuppressWarnings("rawtypes")
    public static <T> List<T> convert(List source, Class<T> clazz) {
        return convert(source, clazz, (String[]) null);
    }

    /**
     * 转换
     *
     * @param source           源 List
     * @param clazz            目标类
     * @param ignoreProperties 忽略属性
     * @return {@link List<T>}
     */
    @SuppressWarnings("rawtypes")
    public static <T> List<T> convert(List source, Class<T> clazz, String... ignoreProperties) {
        ArrayList<T> result = new ArrayList<>();
        if (source == null || source.size() == 0) return result;
        for (Object obj : source) {
            result.add(convert(obj, clazz, ignoreProperties));
        }
        return result;
    }

    /**
     * 忽略null值转换
     * 忽略源对象(List)为null值的转换
     *
     * @param source 源
     * @param target 目标
     */
    public static void convertIgnoreNull(Object source, Object target) {
        String[] nullPropertyNames = getNullPropertyName(source);
        BeanUtils.copyProperties(source, target, nullPropertyNames);
    }

    /**
     * 忽略null值转换
     * 忽略源对象(List)为null值的转换
     *
     * @param source      源
     * @param targetClass 目标类
     * @return {@link T}
     */
    public static <T> T convertIgnoreNull(Object source, Class<T> targetClass) {
        String[] nullPropertyNames = getNullPropertyName(source);
        return convert(source, targetClass, nullPropertyNames);
    }

    /**
     * 忽略null值转换
     * 忽略源对象(List)为null值的转换
     *
     * @param source      源 List
     * @param targetClass 目标类
     * @return {@link List<T>}
     */
    @SuppressWarnings("rawtypes")
    public static <T> List<T> convertIgnoreNull(List source, Class<T> targetClass) {
        String[] nullPropertyNames = getNullPropertyName(source);
        return convert(source, targetClass, nullPropertyNames);
    }

    /**
     * 忽略目标对象有值转换
     * 忽略目标对象的属性不为null值的转换
     *
     * @param source 源
     * @param target 目标
     */
    public static void convertIgnoreProperty(Object source, Object target) {
        String[] nullPropertyNames = getNotNullPropertyName(target);
        BeanUtils.copyProperties(source, target, nullPropertyNames);
    }

    /**
     * 将Bean转换为Map
     *
     * @param bean 豆
     * @return {@link Map<>}
     */
    public static Map<String, Object> beanToMap(Object bean) {
        Map<String, Object> map = new HashMap<>(8);
        BeanWrapper beanWrapper = new BeanWrapperImpl(bean);
        // 获取属性描述器
        PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();
        try {
            //对属性迭代
            for (PropertyDescriptor pd : pds) {
                //属性名称
                String propertyName = pd.getName();
                if ("class".equals(propertyName)) continue;
                //属性值,用getter方法获取
                Method m = pd.getReadMethod();
                Object properValue = m.invoke(bean);//用对象执行getter方法获得属性值
                //把属性名-属性值 存到 Map 中
                map.put(propertyName, properValue);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 将Map转换为Bean
     *
     * @param map         Map
     * @param targetClass 目标类
     * @return {@link T}
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> targetClass) {
        //创建一个需要转换为的类型的对象
        T target = BeanUtils.instantiateClass(targetClass);
        //使用BeanWrapper封装传入的类
        BeanWrapper beanWrapper = new BeanWrapperImpl(target);
        PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            if ("class".equals(pd.getName())) continue;
            //得到属性的setter方法
            Method setter = pd.getWriteMethod();
            //得到key名字和属性名字相同的value设置给属性
            try {
                setter.invoke(target, map.get(pd.getName()));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return target;
    }

    /**
     * 获取对象中属性为null的属性
     *
     * @param source 源
     * @return {@link String[]}
     */
    private static String[] getNullPropertyName(Object source) {
        //使用BeanWrapper封装传入的类
        BeanWrapper beanWrapper = new BeanWrapperImpl(source);
        //获取bean类所有的属性定义
        PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            //获取属性值
            Object beanValue = beanWrapper.getPropertyValue(pd.getName());
            if (beanValue == null)
                emptyNames.add(pd.getName());
        }
        return emptyNames.toArray(new String[0]);
    }


    /**
     * 获取对象中属性不为null的属性
     *
     * @param source 源
     * @return {@link String[]}
     */
    private static String[] getNotNullPropertyName(Object source) {
        //使用BeanWrapper封装传入的类
        BeanWrapper beanWrapper = new BeanWrapperImpl(source);
        //获取bean类所有的属性定义
        PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            //获取属性值
            Object beanValue = beanWrapper.getPropertyValue(pd.getName());
            if (beanValue != null)
                emptyNames.add(pd.getName());
        }
        return emptyNames.toArray(new String[0]);
    }

    /**
     * Object转map
     */
    public static Map<String, String> objectToMap(Object object) {
        try {
            Map<String, String> map = new HashMap<>();
            Class<?> clazz = object.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = field.get(object);
                if (null == value) {
                    map.put(fieldName, "");
                } else {
                    map.put(fieldName, value.toString());
                }
            }
            return map;
        } catch (Exception e) {
            log.error("Object转map失败", e);
        }

        return null;

    }

    /**
     * Object转map
     */
    public static Map<String, Object> objectToObjectMap(Object object) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(object);
            if (null == value) {
                map.put(fieldName, "");
            } else {
                map.put(fieldName, value);
            }
        }
        return map;
    }

}
