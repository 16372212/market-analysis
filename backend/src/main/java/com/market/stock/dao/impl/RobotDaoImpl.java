package com.market.stock.dao.impl;

import java.util.List;

import com.market.stock.dao.BaseDao;
import com.market.stock.model.po.Robot;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.market.stock.dao.RobotDao;

@Repository
public class RobotDaoImpl extends BaseDao implements RobotDao {

    private static final String SELECT_SQL = "select id, type, webhook, state from robot where 1 = 1";

    @Override
    public Robot getById(int id) {
        String sql = RobotDaoImpl.SELECT_SQL + " and id = ?";
        return jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Robot.class), id);
    }

    @Override
    public List<Robot> getListByType(int type) {
        String sql = RobotDaoImpl.SELECT_SQL + " and type = ?";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Robot.class), type);
    }

}
