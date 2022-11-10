package com.market.stock.web.controller;

import com.market.stock.exception.FieldInputException;
import com.market.stock.model.vo.DailyIndexVo;
import com.market.stock.model.vo.PageParam;
import com.market.stock.model.vo.PageVo;
import com.market.stock.model.vo.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.market.stock.model.po.StockInfo;
import com.market.stock.service.StockService;
import com.market.stock.model.po.DailyIndex;

import java.util.Objects;

@RestController
@RequestMapping("report")
public class StockInfoController extends BaseController {

    @Autowired
    private StockService stockService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping("stockList")
    public PageVo<StockInfo> getStockList(PageParam pageParam) {
        return stockService.getStockList(pageParam);
    }

    @RequestMapping("dailyIndexList")
    public PageVo<DailyIndexVo> getDailyIndexList(PageParam pageParam) {
        return stockService.getDailyIndexList(pageParam);
    }

    @RequestMapping(value="dailyStock/{code}", method=RequestMethod.GET)
    public DailyIndexVo getDailyIndex(@PathVariable String code) {
        if (!StringUtils.hasLength(code)) {
            FieldInputException e = new FieldInputException();
            e.addError("code", "stock code could not be null");
            throw e;
        }
        DailyIndexVo res = stockService.getDailyIndexByCode(code);
        if(Objects.isNull(res)) {
            FieldInputException e = new FieldInputException();
            e.addError("code", "this stock does not exist, check it again");
            throw e;
        }
        return res;
    }
}
