package com.cgp.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * web日志
 *
 * @author Manaphy
 * @date 2020/09/27
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebLog {
    /**
     * 类方法
     */
    private String classMethod;
    /**
     * 操作描述
     */
    private String description;
    /**
     * 操作用户
     */
    private String username;
    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    /**
     * 消耗时间
     */
    private Integer spendTime;
    /**
     * 根路径
     */
    private String basePath;
    /**
     * URI
     */
    private String uri;
    /**
     * URL
     */
    private String url;
    /**
     * 请求类型
     */
    private String method;
    /**
     * IP地址
     */
    private String ip;
    /**
     * 请求参数
     */
    private Object parameter;
    /**
     * 请求返回的结果
     */
    private Object result;
}