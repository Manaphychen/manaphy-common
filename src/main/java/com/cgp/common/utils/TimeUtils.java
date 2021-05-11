package com.cgp.common.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期时间工具
 *
 * @author Manaphy
 * @date 2020-10-13
 */
@SuppressWarnings("unused")
public class TimeUtils extends DateUtils {

    private static final String[] PARSE_PATTERNS = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM", "HH:mm"};

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 获取今天日期字符串
     *
     * @return {@link String}
     */
    public static String getTodayDate() {
        return DateFormatUtils.format(new Date(), DATE_PATTERN);
    }

    /**
     * 获取今天时间字符串
     *
     * @return {@link String}
     */
    public static String getTodayTime() {
        return DateFormatUtils.format(new Date(), TIME_PATTERN);
    }

    /**
     * 获取今天时间字符串
     *
     * @return {@link String}
     */
    public static String getTodayDateTime() {
        return DateFormatUtils.format(new Date(), DATE_TIME_PATTERN);
    }

    /**
     * 获取星期
     *
     * @param date 日期
     * @return 如:星期日
     */
    public static String getWeek(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        return sdf.format(date);
    }

    /**
     * 格式化日期
     *
     * @param date    日期
     * @param pattern 模式
     * @return {@link String}
     */
    public static String format(Date date, String pattern) {
        return DateFormatUtils.format(date, pattern);
    }

    /**
     * 格式化日期为日期格式
     *
     * @param date 日期
     * @return {@link String}
     */
    public static String formatDate(Date date) {
        return DateFormatUtils.format(date, DATE_PATTERN);
    }

    /**
     * 格式化日期为日期时间格式
     *
     * @param date 日期
     * @return {@link String}
     */
    public static String formatDateTime(Date date) {
        return DateFormatUtils.format(date, DATE_TIME_PATTERN);
    }

    /**
     * 判断是否是今天
     *
     * @param date 日期
     * @return boolean
     */
    public static boolean isToday(Date date) {
        String today = getTodayDate();
        String format = format(date, DATE_PATTERN);
        return today.equals(format);
    }

    /**
     * 判断时间是否在此之前
     *
     * @param date 日期
     * @return boolean
     */
    public static boolean beforeNow(Date date) {
        return new Date().after(date);
    }

    /**
     * 判断时间是否在此之后
     *
     * @param date 日期
     * @return boolean
     */
    public static boolean afterNow(Date date) {
        return new Date().before(date);
    }

    /**
     * 格式化字符串日期
     *
     * @param stringDate 字符串日期
     * @param oldPattern 字符串日期格式
     * @param newPattern 新的字符串日期格式
     * @return {@link String}
     */
    public static String format(String stringDate, String oldPattern, String newPattern) throws ParseException {
        Date date = DateUtils.parseDate(stringDate, oldPattern);
        return DateFormatUtils.format(date, newPattern);
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), PARSE_PATTERNS);
        } catch (ParseException e) {
            return null;
        }
    }

}
