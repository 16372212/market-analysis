package com.market.stock.service;

import java.util.List;

import com.market.stock.model.po.ExecuteInfo;
import com.market.stock.model.vo.PageParam;
import com.market.stock.model.vo.PageVo;
import com.market.stock.model.vo.TaskVo;

public interface TaskService {

    List<ExecuteInfo> getTaskListById(int... id);

    List<ExecuteInfo> getPendingTaskListById(int... id);

    void executeTask(ExecuteInfo executeInfo) throws Exception;

    PageVo<TaskVo> getAllTask(PageParam pageParam);

    void changeTaskState(int state, int id);

}
