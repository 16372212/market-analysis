package com.market.stock.web.controller;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.market.stock.model.vo.PageParam;
import com.market.stock.util.StockConsts;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public abstract class BaseController {

    protected int getUserId() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if (requestAttributes == null) {
            return -1;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        Integer userId = (Integer) request.getAttribute(StockConsts.KEY_AUTH_USER_ID);
        return userId != null ? userId : -1;
    }

    protected int getTradeUserId(Integer tradeUserId) {
        return tradeUserId != null ? tradeUserId : 1;
    }

    protected <T> List<T> subList(List<T> list, PageParam pageParam) {
        if (list.isEmpty()) {
            return list;
        }
        int start = pageParam.getStart();
        if (start > list.size()) {
            return Collections.emptyList();
        }
        int end = pageParam.getStart() + pageParam.getLength();
        if (end > list.size()) {
            end = list.size();
        }
        return list.subList(start, end);
    }

}
