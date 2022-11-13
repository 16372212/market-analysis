package com.market.stock.web.controller;

import com.market.stock.exception.FieldInputException;
import com.market.stock.model.po.StockSelected;
import com.market.stock.model.vo.*;
import com.market.stock.service.StockSelectedService;
import com.market.stock.service.impl.MessageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.market.stock.model.po.StockInfo;
import com.market.stock.service.StockService;

import java.util.Objects;

@RestController
@RequestMapping("report")
public class StockInfoController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(StockInfoController.class);

    @Autowired
    private StockService stockService;

    @Autowired
    private StockSelectedService stockSelectedService;

    /**
     * 列出股票列表，方便用户查阅股票对应的代码
     * @param pageParam
     * @return
     */
    @RequestMapping("stockList")
    public PageVo<StockInfo> getStockList(PageParam pageParam) {
        return stockService.getStockList(pageParam);
    }

    /**
     * 列出股票五档行情列表
     * @param pageParam
     * @return
     */
    @RequestMapping("dailyIndexList")
    public PageVo<DailyIndexVo> getDailyIndexList(PageParam pageParam) {
        return stockService.getDailyIndexList(pageParam);
    }

    /**
     * 查看某股票五档行情数据
     * @param code
     * @return
     */
    @RequestMapping(value="dailyStock/{code}", method=RequestMethod.GET)
    public DailyIndexVo getDailyIndex(@PathVariable String code) {
        checkStockParam(code);
        DailyIndexVo res = stockService.getDailyIndexByCode(code);
        if(Objects.isNull(res)) {
            FieldInputException e = new FieldInputException();
            e.addError("code", "this stock does not exist, check it again");
            throw e;
        }
        return res;
    }

    /**
     * 用户订阅某股票。先判断该股票是否存在，如果存在则定义该股票
     * @param code
     * @return
     */
    @PostMapping("selectStock")
    public CommonResponse selectStock(String code, String name) {
        checkStockParam(code);
        DailyIndexVo dailyIndexVo = stockService.getDailyIndexByCode(code);
        if(Objects.isNull(dailyIndexVo)) {
            FieldInputException e = new FieldInputException();
            e.addError("code", "this stock does not exist, check it again");
            throw e;
        }
        dailyIndexVo.setName(name);
        stockSelectedService.add(dailyIndexVo);
        StockInfoController.logger.info("user book stock: {}, {}, {}", dailyIndexVo.getRurnoverRate(), name, code);
        return CommonResponse.buildResponse("success");
    }

    /**
     * 用户取消订阅某股票，判断用户输入的股票代码是否存在，若存在则取消订阅。
     * @param code
     * @return
     */
    @RequestMapping(value="selectStock/cancel/{code}", method=RequestMethod.GET)
    public CommonResponse deleteSelectedStock(@PathVariable String code) {
        checkStockParam(code);
        DailyIndexVo dailyIndexVo = stockService.getDailyIndexByCode(code);
        if(Objects.isNull(dailyIndexVo)) {
            FieldInputException e = new FieldInputException();
            e.addError("code", "this stock does not exist, check it again");
            throw e;
        }
        stockSelectedService.deleteByCode(code);
        return CommonResponse.buildResponse("success");
    }

    private void checkStockParam(String code){
        if (!StringUtils.hasLength(code)) {
            FieldInputException e = new FieldInputException();
            e.addError("code", "stock code could not be null");
            throw e;
        }
    }

    /**
     * 用户获取所有股票表单
     * @return
     */
    @RequestMapping(value="selectStock/list", method=RequestMethod.GET)
    public PageVo<StockSelected> SelectedStockList() {
        return new PageVo<>(stockSelectedService.getList(), 10);
    }


    @RequestMapping(value="history", method=RequestMethod.GET)
    public CommonResponse saveHistoryDailyIndex(){
        stockService.saveDailyIndexToFile("/Users/zhenziyang/Documents/Git Workspace/market-analysis/frontend/report/kdata");
        return CommonResponse.buildResponse("success");
    }
}
