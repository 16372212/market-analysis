package com.market.stock.dao;

import com.market.stock.model.po.User;

public interface UserDao {

    User get(String username, String password);

    User get(int id);

    void update(User user);

}
