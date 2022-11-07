package com.market.stock.service;

import java.util.Date;
import java.util.List;

import com.market.stock.model.vo.DailyIndexVo;
import com.market.stock.model.vo.PageParam;
import com.market.stock.model.vo.PageVo;
import com.market.stock.model.po.DailyIndex;
import com.market.stock.model.po.StockInfo;
import com.market.stock.model.po.StockLog;

public interface StockService {

    List<StockInfo> getAll();

    List<StockInfo> getAllListed();

    void addStockLog(List<StockLog> list);

    void update(List<StockInfo> needAddedList, List<StockInfo> needUpdatedList, List<StockLog> stockLogList);

    void saveDailyIndexToFile(String rootPath);

    void saveDailyIndexFromFile(String rootPath);

    void saveDailyIndex(List<DailyIndex> list);

    PageVo<StockInfo> getStockList(PageParam pageParam);

    StockInfo getStockByFullCode(String code);

    PageVo<DailyIndexVo> getDailyIndexList(PageParam pageParam);

    List<DailyIndex> getDailyIndexListByDate(Date date);

    void fixDailyIndex(int date, List<String> codeList);

}
