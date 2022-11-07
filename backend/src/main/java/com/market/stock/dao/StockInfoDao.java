package com.market.stock.dao;

import java.util.List;

import com.market.stock.model.po.StockInfo;
import com.market.stock.model.vo.PageParam;
import com.market.stock.model.vo.PageVo;

public interface StockInfoDao {

    void add(List<StockInfo> list);

    void update(List<StockInfo> list);

    PageVo<StockInfo> get(PageParam pageParam);

    StockInfo getStockByFullCode(String code);

}
