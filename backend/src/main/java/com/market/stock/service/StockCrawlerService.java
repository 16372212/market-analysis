package com.market.stock.service;

import com.market.stock.model.po.DailyIndex;
import com.market.stock.model.po.StockInfo;

import java.util.List;

public interface StockCrawlerService {

    List<StockInfo> getStockList();

    DailyIndex getDailyIndex(String code);

    List<DailyIndex> getDailyIndex(List<String> codeList);

    List<DailyIndex> getDailyIndexFromEastMoney();

    List<DailyIndex> getHistoryDailyIndexs(String code);

    String getHistoryDailyIndexsString(String code);

    String getHistoryDailyIndexsStringFromSina(String code, int year, int season);

}
