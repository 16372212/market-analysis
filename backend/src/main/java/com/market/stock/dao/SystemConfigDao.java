package com.market.stock.dao;

import java.util.List;

import com.market.stock.model.po.SystemConfig;

public interface SystemConfigDao {

    List<SystemConfig> getByName(String name);

    List<SystemConfig> getAll();

    void updateState(int state, int id);

}
