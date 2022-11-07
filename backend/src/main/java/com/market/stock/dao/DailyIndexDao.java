package com.market.stock.dao;

import java.util.Date;
import java.util.List;

import com.market.stock.model.vo.DailyIndexVo;
import com.market.stock.model.po.DailyIndex;
import com.market.stock.model.vo.PageParam;
import com.market.stock.model.vo.PageVo;

public interface DailyIndexDao {

    void save(List<DailyIndex> list);

    PageVo<DailyIndexVo> getDailyIndexList(PageParam pageParam);

    List<DailyIndex> getDailyIndexListByDate(Date date);

}
