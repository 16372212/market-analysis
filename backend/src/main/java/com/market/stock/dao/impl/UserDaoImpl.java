package com.market.stock.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.market.stock.dao.BaseDao;
import com.market.stock.dao.UserDao;
import com.market.stock.model.po.User;

@Repository
public class UserDaoImpl extends BaseDao implements UserDao {

    private static final String SQL_SELECT = "select id, username, password, name, mobile, email, create_time as createTime, update_time as updateTime from user where 1 = 1";

    @Override
    public User get(String username, String password) {
        List<User> list = jdbcTemplate.query(
                UserDaoImpl.SQL_SELECT + " and username = ? and password = ?",
                BeanPropertyRowMapper.newInstance(User.class), username, password);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public User get(int id) {
        List<User> list = jdbcTemplate.query(
                UserDaoImpl.SQL_SELECT + " and id = ?",
                BeanPropertyRowMapper.newInstance(User.class), id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public void update(User user) {
        jdbcTemplate.update("update user set password = ? where id = ?", user.getPassword(), user.getId());
    }

}
