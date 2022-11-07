package com.market.stock.dao;

import java.util.List;

import com.market.stock.model.po.Robot;

public interface RobotDao {

    Robot getById(int id);

    List<Robot> getListByType(int type);

}
