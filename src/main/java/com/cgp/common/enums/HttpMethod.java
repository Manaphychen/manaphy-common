package com.cgp.common.enums;

import lombok.AllArgsConstructor;

/**
 * http方法
 *
 * @author Manaphy
 * @date 2021/01/06
 */
@AllArgsConstructor
public enum HttpMethod {
    // Http请求方法
    GET, POST_FORM, POST, POST_JSON, POST_XML, PUT, DELETE, PATCH
}
