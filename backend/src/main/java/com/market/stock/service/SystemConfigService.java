package com.market.stock.service;

import java.util.List;

import com.market.stock.model.po.SystemConfig;

public interface SystemConfigService {

    boolean isMock();

    boolean isApplyNewConvertibleBond();

    boolean isCr();

    List<SystemConfig> getAll();

}
