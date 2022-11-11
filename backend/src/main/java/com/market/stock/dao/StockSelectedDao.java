package com.market.stock.dao;

import java.util.List;

import com.market.stock.model.po.HolidayCalendar;
import com.market.stock.model.po.StockSelected;
import com.market.stock.model.vo.DailyIndexVo;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

public interface StockSelectedDao {

    List<StockSelected> getList();

    void add(List<DailyIndexVo> list);

    void deleteByCode(String code);

    boolean isNotExist(String code);
}
