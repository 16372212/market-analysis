package com.market.stock.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.market.stock.dao.RobotDao;
import com.market.stock.model.po.Robot;
import com.market.stock.service.RobotService;
import com.market.stock.util.StockConsts;

@Service
public class RobotServiceImpl implements RobotService {

    private static final String ID_SYSTEM = "1";

    @Autowired
    private RobotDao robotDao;

    @Cacheable(value = StockConsts.CACHE_KEY_CONFIG_ROBOT, key = "'" + RobotServiceImpl.ID_SYSTEM + "'")
    @Override
    public Robot getSystem() {
        return getById(Integer.parseInt(RobotServiceImpl.ID_SYSTEM));
    }

    @Cacheable(value = StockConsts.CACHE_KEY_CONFIG_ROBOT, key = "#id.toString()")
    @Override
    public Robot getById(int id) {
        return robotDao.getById(id);
    }

}
