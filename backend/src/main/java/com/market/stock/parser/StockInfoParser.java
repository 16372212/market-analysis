package com.market.stock.parser;

import com.market.stock.model.po.DailyIndex;
import com.market.stock.model.po.StockInfo;

import java.util.List;

public interface StockInfoParser {

    List<EmStock> parseStockInfoList(String content);

    public static class EmStock {

        private StockInfo stockInfo;
        private DailyIndex dailyIndex;

        public StockInfo getStockInfo() {
            return stockInfo;
        }

        public void setStockInfo(StockInfo stockInfo) {
            this.stockInfo = stockInfo;
        }

        public DailyIndex getDailyIndex() {
            return dailyIndex;
        }

        public void setDailyIndex(DailyIndex dailyIndex) {
            this.dailyIndex = dailyIndex;
        }

    }

}
