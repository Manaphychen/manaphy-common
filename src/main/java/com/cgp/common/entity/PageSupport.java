package com.cgp.common.entity;

import com.cgp.common.utils.StringUtils;
import com.cgp.common.utils.WebUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 分页支持
 *
 * @author Manaphy
 * @date 2020-10-14
 */
@Getter
@Setter
public class PageSupport {
    private Integer pageNum;
    private Integer pageSize;
    private String orderBy;
    private String isAsc = "asc";

    public String getOrderBy() {
        if (StringUtils.isEmpty(orderBy)) {
            return "";
        }
        return StringUtils.toUnderScoreCase(orderBy) + " " + isAsc;
    }

    public static PageSupport buildPageRequest() {
        PageSupport pageSupport = new PageSupport();
        // 从请求体中获取分页参数
        String pageNum = WebUtil.getParameter("pageNum");
        // 如果请求体没有pageNum参数,则设置默认值1
        if (pageNum == null) {
            pageSupport.pageNum = 1;
        } else {
            pageSupport.pageNum = Integer.valueOf(pageNum);
        }
        String pageSize = WebUtil.getParameter("pageSize");
        // 如果请求体没有pageSize参数,则设置默认值10
        if (pageSize == null) {
            pageSupport.pageSize = 10;
        } else {
            pageSupport.pageSize = Integer.valueOf(pageSize);
        }
        pageSupport.orderBy = WebUtil.getParameter("orderBy");
        String isAsc = WebUtil.getParameter("isAsc");
        if (isAsc != null) {
            pageSupport.isAsc = isAsc;
        }
        return pageSupport;
    }
}
