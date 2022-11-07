package com.market.stock.service;

import com.market.stock.model.po.Robot;

public interface RobotService {

    Robot getSystem();

    Robot getById(int id);

}
