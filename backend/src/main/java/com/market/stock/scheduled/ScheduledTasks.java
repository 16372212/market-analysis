package com.market.stock.scheduled;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.market.stock.api.request.GetAssetsRequest;
import com.market.stock.api.response.GetAssetsResponse;
import com.market.stock.model.po.ExecuteInfo;
import com.market.stock.model.po.Task;
import com.market.stock.service.HolidayCalendarService;
import com.market.stock.service.TaskService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.market.stock.exception.UnauthorizedException;

@Component
public class ScheduledTasks {

    private final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private HolidayCalendarService holidayCalendarService;

    @Autowired
    private TaskService taskService;

    /**
     * begin of year
     */
    @Scheduled(cron = "0 0 0 1 1 ?")
    public void runBeginOfYear() {
        try {
            List<ExecuteInfo> list = taskService.getPendingTaskListById(Task.BeginOfYear.getId());
            executeTask(list);
        } catch (Exception e) {
            logger.error("task runBeginOfYear error", e);
        }
    }

    /**
     * begin of day, 6 clock everyday
     */
    @Scheduled(cron = "0 0 6 ? * MON-FRI")
    public void runBeginOfDay() {
        if (isNotBusinessDate()) {
            return;
        }

        try {
            List<ExecuteInfo> list = taskService.getPendingTaskListById(Task.BeginOfDay.getId());
            executeTask(list);
        } catch (Exception e) {
            logger.error("task runBeginOfDay error", e);
        }
    }

    /**
     * update of stock
     */
    @Scheduled(cron = "0 0 9 ? * MON-FRI")
    public void runUpdateOfStock() {
        if (isNotBusinessDate()) {
            return;
        }

        try {
            List<ExecuteInfo> list = taskService.getPendingTaskListById(Task.UpdateOfStock.getId());
            executeTask(list);
        } catch (Exception e) {
            logger.error("task runUpdateOfStock error", e);
        }
    }

    /**
     * update of daily index
     */
    @Scheduled(cron = "0 1 17,18,19 ? * MON-FRI")
    public void runUpdateOfDailyIndex() {
        if (isNotBusinessDate()) {
            return;
        }

        try {
            List<ExecuteInfo> list = taskService.getPendingTaskListById(Task.UpdateOfDailyIndex.getId());
            executeTask(list);
        } catch (Exception e) {
            logger.error("task runUpdateOfDailyIndex error", e);
        }
    }

    /**
     * ticker
     */
    @Scheduled(cron = "0,1,30,45 * 9,10,11,13,14 ? * MON-FRI")
    public void runTicker() {
        if (isNotBusinessTime()) {
             return;
        }

        try {
            List<ExecuteInfo> list = taskService.getPendingTaskListById(Task.Ticker.getId(), Task.TradeTicker.getId());
            executeTask(list);
        } catch (Exception e) {
            logger.error("task runTicker error", e);
        }
    }

    /**
     * apply new stock
     */
    @Scheduled(cron = "0 1 10,14 ? * MON-FRI")
    public void applyNewStock() {
        if (isNotBusinessTime()) {
            return;
        }

        try {
            List<ExecuteInfo> list = taskService.getPendingTaskListById(Task.ApplyNewStock.getId());
            executeTask(list);
        } catch (Exception e) {
            logger.error("task applyNewStock error", e);
        }
    }

    private boolean isNotBusinessTime() {
        return isNotBusinessDate() || !holidayCalendarService.isBusinessTime(new Date());
    }

    private boolean isNotBusinessDate() {
        return !holidayCalendarService.isBusinessDate(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
    }

    private void executeTask(List<ExecuteInfo> list) throws Exception {
        for (ExecuteInfo executeInfo : list) {
            taskService.executeTask(executeInfo);
        }
    }

}
