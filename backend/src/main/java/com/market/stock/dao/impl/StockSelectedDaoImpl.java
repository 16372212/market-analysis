package com.market.stock.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.market.stock.model.po.StockLog;
import com.market.stock.model.vo.DailyIndexVo;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.market.stock.dao.BaseDao;
import com.market.stock.dao.StockSelectedDao;
import com.market.stock.model.po.StockSelected;

@Repository
public class StockSelectedDaoImpl extends BaseDao implements StockSelectedDao {

    private static final String SQL_SELECT_BASE_COLUMNS = "select id as id, code as code, rate as rate, create_time as createTime, update_time as updateTime, description as description from stock_selected where 1 = 1";

    @Override
    public List<StockSelected> getList() {
        List<StockSelected> list = jdbcTemplate.query(SQL_SELECT_BASE_COLUMNS,
                BeanPropertyRowMapper.newInstance(StockSelected.class));
        return list;
    }

    @Override
    public void add(List<DailyIndexVo> list) {
        jdbcTemplate.batchUpdate(
                "insert into stock_selected(code, rate) values(?, ?)",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        DailyIndexVo dailyIndexVo = list.get(i);
                        ps.setString(1, dailyIndexVo.getCode());
                        ps.setBigDecimal(2, dailyIndexVo.getRurnoverRate());
                    }

                    @Override
                    public int getBatchSize() {
                        return list.size();
                    }
                });
    }

    @Override
    public void deleteByCode(String code) {
        jdbcTemplate.update("delete from stock_selected where code = ?", code);
    }

}
