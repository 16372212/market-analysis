package com.market.stock.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.market.stock.dao.SystemConfigDao;
import com.market.stock.model.po.SystemConfig;
import com.market.stock.service.SystemConfigService;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigDao systemConfigDao;

    @Override
    public boolean isMock() {
        List<SystemConfig> list = systemConfigDao.getByName("trade_mock");
        return !list.isEmpty() && list.get(0).getValue1().equals("1");
    }

    @Override
    public List<SystemConfig> getAll() {
        return systemConfigDao.getAll();
    }

    @Override
    public void changeConfigState(int state, int id) {
        systemConfigDao.updateState(state, id);
    }
}
