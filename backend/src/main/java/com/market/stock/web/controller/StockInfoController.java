package com.market.stock.web.controller;

import com.market.stock.model.vo.DailyIndexVo;
import com.market.stock.model.vo.PageParam;
import com.market.stock.model.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.market.stock.model.po.StockInfo;
import com.market.stock.service.StockService;

@RestController
@RequestMapping("report")
public class StockInfoController extends BaseController {

    @Autowired
    private StockService stockService;

    @RequestMapping("stockList")
    public PageVo<StockInfo> getStockList(PageParam pageParam) {
        return stockService.getStockList(pageParam);
    }

    @RequestMapping("dailyIndexList")
    public PageVo<DailyIndexVo> getDailyIndexList(PageParam pageParam) {
        return stockService.getDailyIndexList(pageParam);
    }

}
