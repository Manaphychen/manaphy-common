package com.cgp.common.controller;

import com.cgp.common.constant.Constants;
import com.cgp.common.entity.PageSupport;
import com.cgp.common.exception.CustomException;
import com.cgp.common.utils.StringUtils;
import com.cgp.common.utils.TimeUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;

/**
 * web层通用数据处理
 *
 * @author Manaphy
 * @date 2020-10-14
 */
public class BaseController {

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(TimeUtils.parseDate(text));
            }
        });
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage() {
        PageSupport pageSupport = PageSupport.buildPageRequest();
        Integer pageNum = pageSupport.getPageNum();
        Integer pageSize = pageSupport.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            String orderBy = escapeOrderBySql(pageSupport.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }

    }


    /**
     * 检查字符，防止注入绕过
     */
    private static String escapeOrderBySql(String value) {
        if (StringUtils.isNotEmpty(value) && !isValidOrderBySql(value)) {
            throw new CustomException("参数不符合规范，不能进行查询");
        }
        return value;
    }

    /**
     * 验证 order by 语法是否符合规范
     */
    private static boolean isValidOrderBySql(String value) {
        return value.matches(Constants.SQL_PATTERN);
    }
}
