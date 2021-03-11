package com.cgp.common.constant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * manaphy属性
 *
 * @author Manaphy
 * @date 2020-10-13
 */
@Getter
@Setter
@EnableAutoConfiguration
@ConfigurationProperties("manaphy")
public class ManaphyProperties {
    private boolean weblog;
    private boolean rest;
    private boolean redis;
    private boolean exception;
}
