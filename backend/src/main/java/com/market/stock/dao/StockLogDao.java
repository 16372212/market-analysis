package com.market.stock.dao;

import java.util.List;

import com.market.stock.model.po.StockLog;

public interface StockLogDao {

    void add(List<StockLog> list);

    void setStockIdByCodeType(List<String> list, int type);

}
