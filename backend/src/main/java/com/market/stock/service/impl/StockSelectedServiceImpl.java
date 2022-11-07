package com.market.stock.service.impl;

import java.util.List;

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

}
