package com.market.stock.dao.impl;

import java.util.List;

import com.market.stock.dao.BaseDao;
import com.market.stock.model.po.SystemConfig;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.market.stock.dao.SystemConfigDao;
import com.market.stock.util.StockConsts;

@Repository
public class SystemConfigDaoImpl extends BaseDao implements SystemConfigDao {

    private static final String SQL_SELECT_BASE_COLUMNS = "select id as id, name, value1, value2, value3, state, create_time as createTime, update_time as updateTime from system_config where 1 = 1";

    @Override
    public List<SystemConfig> getByName(String name) {
        return jdbcTemplate.query(SQL_SELECT_BASE_COLUMNS + " and name = ? and state = ?",
                BeanPropertyRowMapper.newInstance(SystemConfig.class), name, StockConsts.TradeState.Valid.value());
    }

    @Override
    public List<SystemConfig> getAll() {
        return jdbcTemplate.query(SQL_SELECT_BASE_COLUMNS, BeanPropertyRowMapper.newInstance(SystemConfig.class));
    }

    @Override
    public void updateState(int state, int id) {
        jdbcTemplate.update("update system_config set state = ? where id = ?", state, id);
    }
}
