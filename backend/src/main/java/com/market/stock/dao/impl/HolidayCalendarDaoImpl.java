package com.market.stock.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.market.stock.model.po.HolidayCalendar;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.market.stock.dao.BaseDao;
import com.market.stock.dao.HolidayCalendarDao;

@Repository
public class HolidayCalendarDaoImpl extends BaseDao implements HolidayCalendarDao {

    @Override
    public void deleteByYear(int year) {
        jdbcTemplate.update("delete from holiday_calendar where DATE_FORMAT(date, '%Y') = ?", year);
    }

    @Override
    public void save(List<HolidayCalendar> list) {
        jdbcTemplate.batchUpdate("insert into holiday_calendar(date) values(?)", new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setDate(1, new java.sql.Date(list.get(i).getDate().getTime()));
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }

        });
    }

    @Override
    public HolidayCalendar getByDate(Date date) {
        List<HolidayCalendar> list = jdbcTemplate.query(
                "select id, date from holiday_calendar where date = date(?)",
                BeanPropertyRowMapper.newInstance(HolidayCalendar.class), date);
        return list.isEmpty() ? null : list.get(0);
    }

}
