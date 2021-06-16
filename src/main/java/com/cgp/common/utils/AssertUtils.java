package com.cgp.common.utils;


import com.cgp.common.exception.CustomException;

/**
 * 断言工具
 *
 * @author Manaphy
 * @date 2021/04/29
 */
public class AssertUtils {

    public static void isNull(Object obj, int code, String desc) {
        if (obj == null) {
            throw new CustomException(desc, code);
        }
    }


    public static void isTrue(boolean match, int code, String desc) {
        if (match) {
            throw new CustomException(desc, code);
        }
    }
}
