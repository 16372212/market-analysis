package com.market.stock.service.impl;

import java.util.Collections;
import java.util.List;

import com.market.stock.model.po.DailyIndex;
import com.market.stock.model.vo.DailyIndexVo;
import com.market.stock.service.StockSelectedService;
import com.market.stock.util.StockConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.market.stock.dao.StockSelectedDao;
import com.market.stock.model.po.StockSelected;

@Service
public class StockSelectedServiceImpl implements StockSelectedService {

    private final Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);

    @Autowired
    private StockSelectedDao stockSelectedDao;

    @Override
    public List<StockSelected> getList() {
        return stockSelectedDao.getList();
    }

    @Override
    public void add(DailyIndexVo dailyIndexVo) {
        if(stockSelectedDao.isNotExist(dailyIndexVo.getCode())){
            stockSelectedDao.add(Collections.singletonList(dailyIndexVo));
        }
    }

    @Override
    public void deleteByCode(String fullCode) {
        if(stockSelectedDao.isNotExist(fullCode)){
            logger.info("{}: this stock you not select ", fullCode);
            return;
        }
        stockSelectedDao.deleteByCode(fullCode);
    }

}
