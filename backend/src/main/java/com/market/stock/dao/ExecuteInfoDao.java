package com.market.stock.dao;

import java.util.List;

import com.market.stock.model.po.ExecuteInfo;
import com.market.stock.model.vo.TaskVo;
import com.market.stock.model.vo.PageParam;
import com.market.stock.model.vo.PageVo;

public interface ExecuteInfoDao {

    List<ExecuteInfo> getByTaskIdAndState(int[] id, Integer value);

    void update(ExecuteInfo executeInfo);

    PageVo<TaskVo> get(PageParam pageParam);

    void updateState(int state, int id);

}
