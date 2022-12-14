package com.market.stock.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.market.stock.model.po.ExecuteInfo;
import com.market.stock.model.po.SystemConfig;
import com.market.stock.model.vo.TaskVo;
import com.market.stock.service.CacheClient;
import com.market.stock.service.SystemConfigService;
import com.market.stock.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.market.stock.exception.FieldInputException;
import com.market.stock.model.vo.CacheVo;
import com.market.stock.model.vo.CommonResponse;
import com.market.stock.model.vo.PageParam;
import com.market.stock.model.vo.PageVo;
import com.market.stock.util.StockConsts;

@RestController
@RequestMapping("system")
public class SystemController extends BaseController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private CacheClient redisClient;

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 得到所有的定时任务
     * @param pageParam
     * @return
     */
    @RequestMapping("taskList")
    public PageVo<TaskVo> getTaskList(PageParam pageParam) {
        return taskService.getAllTask(pageParam);
    }

    /**
     * 改变定时任务状态为自动执行。任务状态一旦改为`执行态2`后，便开始自动执行任务
     * @param id
     * @param state
     * @return
     */
    @PostMapping("changeTaskState")
    public CommonResponse changeTaskState(int id, int state) {
        FieldInputException e = null;
        if (state != 0 && state != 2) {
            e = new FieldInputException();
            e.addError("state", "state invalid");
        }
        if (id < 0) {
            if (e == null) {
                e = new FieldInputException();
            }
            e.addError("id", "id invalid");
        }
        if (e != null && e.hasErrors()) {
            throw e;
        }
        taskService.changeTaskState(state, id);
        return CommonResponse.buildResponse("success");
    }

    /**
     * 用户手动执行某任务
     * @param id
     * @return
     * @throws Exception
     */
    @PostMapping("executeTask")
    public CommonResponse executeTask(int id) throws Exception {
        List<ExecuteInfo> list = taskService.getTaskListById(id);
        for (ExecuteInfo executeInfo : list) {
            taskService.executeTask(executeInfo);
        }
        return CommonResponse.buildResponse("ok");
    }

    @RequestMapping("cacheList")
    public PageVo<CacheVo> getCacheList(PageParam pageParam) {
        List<CacheVo> list = redisClient.getAll();
        list = list.stream().filter(v -> !v.getName().equals(StockConsts.CACHE_KEY_TOKEN)).collect(Collectors.toList());
        return new PageVo<>(subList(list, pageParam), list.size());
    }

    @PostMapping("deleteCache")
    public CommonResponse deleteCache(String name, String key) {
        if (!StringUtils.hasLength(name)) {
            FieldInputException e = new FieldInputException();
            e.addError("name", "name invalid");
            throw e;
        }
        if (!StringUtils.hasLength(key)) {
            FieldInputException e = new FieldInputException();
            e.addError("key", "key invalid");
            throw e;
        }
        redisClient.remove(name, key);
        return CommonResponse.buildResponse("success");
    }

    /**
     * 现实系统支持的所有数据源
     * @param pageParam
     * @return
     */
    @RequestMapping("configList")
    public PageVo<SystemConfig> getSystemConfigList(PageParam pageParam) {
        List<SystemConfig> list = systemConfigService.getAll();
        return new PageVo<>(subList(list, pageParam), list.size());
    }

    /**
     * 修改数据源状态。用户选择使用哪些数据源，选定后修改状态为1即为该数据源可用
     * @param state
     * @param id
     * @return
     */
    @PostMapping("changeConfigState")
    public CommonResponse ChangeConfigState(int state, int id) {
        FieldInputException e = null;
        if (state != 0 && state != 2) {
            e = new FieldInputException();
            e.addError("state", "state invalid");
        }
        if (id < 0) {
            if (e == null) {
                e = new FieldInputException();
            }
            e.addError("id", "id invalid");
        }
        systemConfigService.changeConfigState(state, id);
        return CommonResponse.buildResponse("success");
    }
}
