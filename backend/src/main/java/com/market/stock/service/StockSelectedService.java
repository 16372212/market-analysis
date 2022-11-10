package com.market.stock.service;

import java.util.List;

import com.market.stock.model.po.StockLog;
import com.market.stock.model.po.StockSelected;
import com.market.stock.model.vo.DailyIndexVo;

public interface StockSelectedService {

    List<StockSelected> getList();

    void add(DailyIndexVo dailyIndexVo);

    void deleteByCode(String fullCode);
}
