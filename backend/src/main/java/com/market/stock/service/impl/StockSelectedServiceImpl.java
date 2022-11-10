package com.market.stock.service.impl;

import java.util.Collections;
import java.util.List;

import com.market.stock.model.po.DailyIndex;
import com.market.stock.model.vo.DailyIndexVo;
import com.market.stock.service.StockSelectedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.market.stock.dao.StockSelectedDao;
import com.market.stock.model.po.StockSelected;

@Service
public class StockSelectedServiceImpl implements StockSelectedService {

    @Autowired
    private StockSelectedDao stockSelectedDao;

    @Override
    public List<StockSelected> getList() {
        return stockSelectedDao.getList();
    }

    @Override
    public void add(DailyIndexVo dailyIndexVo) {
        stockSelectedDao.add(Collections.singletonList(dailyIndexVo));
    }

    @Override
    public void deleteByCode(String fullCode) {
        stockSelectedDao.deleteByCode(fullCode.substring(2));
    }

}
