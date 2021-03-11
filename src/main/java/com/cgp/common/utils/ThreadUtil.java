package com.cgp.common.utils;

import java.util.concurrent.TimeUnit;

/**
 * 线程工具
 *
 * @author Manaphy
 * @date 2020-09-24
 */
public class ThreadUtil {

    public static void sleep(int second) {
        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (InterruptedException ignored) {
        }
    }
}
