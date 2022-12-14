package com.market.stock.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.market.stock.dao.BaseDao;
import com.market.stock.dao.StockInfoDao;
import com.market.stock.model.vo.PageParam;
import com.market.stock.model.vo.PageVo;
import com.market.stock.util.SqlCondition;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.market.stock.model.po.StockInfo;

@Repository
public class StockInfoDaoImpl extends BaseDao implements StockInfoDao {

    private static final String SELECT_SQL = "select id, code, name, exchange, abbreviation, state, type, create_time as createTime, update_time as updateTime from stock_info where 1 = 1";

    @Override
    public void add(List<StockInfo> list) {
        jdbcTemplate.batchUpdate(
                "insert into stock_info(code, name, exchange, abbreviation, state, type) values(?, ?, ?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        StockInfo stockInfo = list.get(i);
                        ps.setString(1, stockInfo.getCode());
                        ps.setString(2, stockInfo.getName());
                        ps.setString(3, stockInfo.getExchange());
                        ps.setString(4, stockInfo.getAbbreviation());
                        ps.setInt(5, stockInfo.getState());
                        ps.setInt(6, stockInfo.getType());
                    }

                    @Override
                    public int getBatchSize() {
                        return list.size();
                    }
                });
    }

    @Override
    public void update(List<StockInfo> list) {
        jdbcTemplate.batchUpdate(
                "update stock_info set name = ?, abbreviation = ? where id = ?",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        StockInfo stockInfo = list.get(i);
                        ps.setString(1, stockInfo.getName());
                        ps.setString(2, stockInfo.getAbbreviation());
                        ps.setInt(3, stockInfo.getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return list.size();
                    }
                });
    }

    @Override
    public PageVo<StockInfo> get(PageParam pageParam) {
        SqlCondition dataSqlCondition = new SqlCondition(
                StockInfoDaoImpl.SELECT_SQL,
                pageParam.getCondition());

        int totalRecords = jdbcTemplate.queryForObject(dataSqlCondition.getCountSql(), Integer.class,
                dataSqlCondition.toArgs());

        dataSqlCondition.addSql(" limit ?, ?");
        dataSqlCondition.addPage(pageParam.getStart(), pageParam.getLength());

        List<StockInfo> list = jdbcTemplate.query(dataSqlCondition.toSql(), BeanPropertyRowMapper.newInstance(StockInfo.class),
                dataSqlCondition.toArgs());
        return new PageVo<>(list, totalRecords);
    }

    @Override
    public StockInfo getStockByFullCode(String code) {
        List<StockInfo> list = jdbcTemplate.query(StockInfoDaoImpl.SELECT_SQL + " and concat(exchange, code) = ?",
                BeanPropertyRowMapper.newInstance(StockInfo.class), code);
        return list.isEmpty() ? null : list.get(0);
    }

}
