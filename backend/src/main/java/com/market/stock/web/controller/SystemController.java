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

    @RequestMapping("taskList")
    public PageVo<TaskVo> getTaskList(PageParam pageParam) {
        return taskService.getAllTask(pageParam);
    }

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

    @PostMapping("executeTask")
    public CommonResponse executeTask(int id) {
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

    @RequestMapping("configList")
    public PageVo<SystemConfig> getSystemConfigList(PageParam pageParam) {
        List<SystemConfig> list = systemConfigService.getAll();
        return new PageVo<>(subList(list, pageParam), list.size());
    }

}
