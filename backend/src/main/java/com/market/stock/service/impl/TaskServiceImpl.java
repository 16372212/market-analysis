package com.market.stock.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.market.stock.dao.ExecuteInfoDao;
import com.market.stock.model.po.DailyIndex;
import com.market.stock.model.po.ExecuteInfo;
import com.market.stock.model.po.StockInfo;
import com.market.stock.model.po.StockLog;
import com.market.stock.model.po.StockSelected;
import com.market.stock.model.po.Task;
import com.market.stock.model.vo.PageParam;
import com.market.stock.model.vo.PageVo;
import com.market.stock.model.vo.TaskVo;
import com.market.stock.service.HolidayCalendarService;
import com.market.stock.service.MessageService;
import com.market.stock.service.StockCrawlerService;
import com.market.stock.service.StockSelectedService;
import com.market.stock.service.StockService;
import com.market.stock.service.SystemConfigService;
import com.market.stock.service.TaskService;
import com.market.stock.util.DecimalUtil;
import com.market.stock.util.StockConsts;
import com.market.stock.util.StockUtil;

@Service
public class TaskServiceImpl implements TaskService {

    private final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final Map<String, BigDecimal> lastPriceMap = new HashMap<>();

    @Value("${ocr.service}")
    private String ocrServiceName;

    @Autowired
    private HolidayCalendarService holidayCalendarService;

    @Autowired
    private ExecuteInfoDao executeInfoDao;

    @Autowired
    private StockCrawlerService stockCrawlerService;

    @Autowired
    private StockService stockService;

    @Autowired
    private MessageService messageServicve;

    @Autowired
    private StockSelectedService stockSelectedService;

    @Autowired
    private SystemConfigService systemConfigService;

    private static final boolean CrawIndexFromSina = false;

    @Override
    public List<ExecuteInfo> getTaskListById(int... id) {
        return executeInfoDao.getByTaskIdAndState(id, null);
    }

    @Override
    public List<ExecuteInfo> getPendingTaskListById(int... id) {
        return executeInfoDao.getByTaskIdAndState(id, StockConsts.TaskState.Pending.value());
    }

    @Override
    public void executeTask(ExecuteInfo executeInfo) throws Exception {
        executeInfo.setStartTime(new Date());
        executeInfo.setMessage("");
        int id = executeInfo.getTaskId();
        Task task = Task.valueOf(id);
        try {
            switch (task) {
            case BeginOfYear:
                holidayCalendarService.updateCurrentYear();
                break;
            case BeginOfDay:
                lastPriceMap.clear();
                break;
            case UpdateOfStock:
                runUpdateOfStock();
                break;
            case UpdateOfDailyIndex:
                runUpdateOfDailyIndex();
                break;
            case Ticker:
                runTicker();
                break;
            default:
                break;
            }
        } catch (Exception e) {
            executeInfo.setMessage(e.getMessage());
            logger.error("task {} {} error", task.getName(), executeInfo.getId(), e);

            String body = String.format("task: %s, error: %s", task.getName(), e.getMessage());
            messageServicve.send(body);
        }

        executeInfo.setCompleteTime(new Date());
        executeInfoDao.update(executeInfo);
    }

    private void runUpdateOfStock() {
        List<StockInfo> list = stockService.getAll().stream().filter(v -> !v.isIndex()).collect(Collectors.toList());
        Map<String, List<StockInfo>> dbStockMap = list.stream().collect(Collectors.groupingBy(StockInfo::getCode));

        ArrayList<StockInfo> needAddedList = new ArrayList<>();
        ArrayList<StockInfo> needUpdatedList = new ArrayList<>();
        ArrayList<StockLog> stockLogList = new ArrayList<>();

        final Date date = new Date();

        List<StockInfo> crawlerList = stockCrawlerService.getStockList();
        for (StockInfo stockInfo : crawlerList) {
            StockConsts.StockLogType stocLogType = null;
            List<StockInfo> stockGroupList = dbStockMap.get(stockInfo.getCode());
            String oldValue = null;
            String newValue = null;
            if (stockGroupList == null) {
                stocLogType = StockConsts.StockLogType.New;
                oldValue = "";
                newValue = stockInfo.getName();
            } else {
                StockInfo stockInfoInDb = stockGroupList.get(0);
                if (!stockInfo.getName().equals(stockInfoInDb.getName())
                        && StockUtil.isOriName(stockInfo.getName())) {
                    stocLogType = StockConsts.StockLogType.Rename;
                    oldValue = stockInfoInDb.getName();
                    newValue = stockInfo.getName();
                    stockInfo.setId(stockInfoInDb.getId());
                }
            }

            if (stocLogType != null) {
                StockLog stockLog = new StockLog(stockInfo.getId(), date, stocLogType.value(), oldValue, newValue);
                if (stocLogType == StockConsts.StockLogType.New) {
                    needAddedList.add(stockInfo);
                } else {
                    needUpdatedList.add(stockInfo);
                }
                stockLogList.add(stockLog);
            }
        }

        stockService.update(needAddedList, needUpdatedList, stockLogList);
    }

    private void runUpdateOfDailyIndex() {
        List<StockInfo> list = stockService.getAll().stream()
                .filter(stockInfo -> (stockInfo.isA() || stockInfo.isIndex()) && stockInfo.isValid())
                .collect(Collectors.toList());

        Date date = new Date();

        List<DailyIndex> dailyIndexList = stockService.getDailyIndexListByDate(date);
        List<String> codeList = dailyIndexList.stream().map(DailyIndex::getCode).collect(Collectors.toList());
        list = list.stream().filter(v -> !codeList.contains(v.getFullCode())).collect(Collectors.toList());

        if (CrawIndexFromSina) {
            crawDailyIndexFromSina(list);
        } else {
            crawDailyIndexFromSina(list.stream().filter(StockInfo::isIndex).collect(Collectors.toList()));
            crawDailyIndexFromEastMoney(list);
        }
    }

    private void crawDailyIndexFromEastMoney(List<StockInfo> list) {
        List<DailyIndex> dailyIndexList = stockCrawlerService.getDailyIndexFromEastMoney();
        dailyIndexList = dailyIndexList.stream().filter(d -> list.stream().anyMatch(s -> d.getCode().equals(s.getFullCode()))).collect(Collectors.toList());
        stockService.saveDailyIndex(filterInvalid(dailyIndexList));
    }

    private void crawDailyIndexFromSina(List<StockInfo> list) {
        final int tCount = 500;
        ArrayList<String> stockCodeList = new ArrayList<>(tCount);
        for (StockInfo stockInfo : list) {
            stockCodeList.add(stockInfo.getFullCode());
            if (stockCodeList.size() == tCount) {
                saveDailyIndex(stockCodeList);
                stockCodeList.clear();
            }
        }

        if (!stockCodeList.isEmpty()) {
            saveDailyIndex(stockCodeList);
        }
    }

    private void saveDailyIndex(ArrayList<String> stockCodeList) {
        List<DailyIndex> dailyIndexList = stockCrawlerService.getDailyIndex(stockCodeList);
        stockService.saveDailyIndex(filterInvalid(dailyIndexList));
    }

    private List<DailyIndex> filterInvalid(List<DailyIndex> dailyIndexList) {
        final String currentDateStr = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        return dailyIndexList.stream().filter(dailyIndex ->
            DecimalUtil.bg(dailyIndex.getOpeningPrice(), BigDecimal.ZERO)
            && dailyIndex.getTradingVolume() > 0
            && DecimalUtil.bg(dailyIndex.getTradingValue(), BigDecimal.ZERO)
            && currentDateStr.equals(DateFormatUtils.format(dailyIndex.getDate(), "yyyy-MM-dd"))
        ).collect(Collectors.toList());
    }

    private void runTicker() throws Exception {
        List<StockSelected> selectList = stockSelectedService.getList();
        List<String> codeList = selectList.stream().map(v -> StockUtil.getFullCode(v.getCode())).collect(Collectors.toList());
        List<DailyIndex> dailyIndexList = stockCrawlerService.getDailyIndex(codeList);

        StringBuilder sb = new StringBuilder();
        for (StockSelected stockSelected : selectList) {
            String code = stockSelected.getCode().substring(2);
            DailyIndex dailyIndex = dailyIndexList.stream().filter(d -> d.getCode().contains(stockSelected.getCode())).findAny().orElse(null);
            if (dailyIndex == null) {
                continue;
            }
            if (lastPriceMap.containsKey(code)) {
                BigDecimal lastPrice = lastPriceMap.get(code);
                double rate = Math.abs(StockUtil.calcIncreaseRate(dailyIndex.getClosingPrice(), lastPrice).doubleValue());
                if (Double.compare(rate, stockSelected.getRate().doubleValue()) >= 0) {
                    lastPriceMap.put(code, dailyIndex.getClosingPrice());
                    String name = stockService.getStockByFullCode(StockUtil.getFullCode(code)).getName();
                    String body = String.format("%s:当前价格:%.03f, 涨幅%.02f%%", name,
                        dailyIndex.getClosingPrice().doubleValue(),
                        StockUtil.calcIncreaseRate(dailyIndex.getClosingPrice(),
                                dailyIndex.getPreClosingPrice()).movePointRight(2).doubleValue());
                    sb.append(body + "\n");
                }
            } else {
                lastPriceMap.put(code, dailyIndex.getPreClosingPrice());
                String name = stockService.getStockByFullCode(StockUtil.getFullCode(code)).getName();
                String body = String.format("%s:当前价格:%.03f", name, dailyIndex.getClosingPrice().doubleValue());
                sb.append(body + "\n");
            }
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
            messageServicve.send(sb.toString());
        }
    }

    @Override
    public PageVo<TaskVo> getAllTask(PageParam pageParam) {
        return executeInfoDao.get(pageParam);
    }

    @Override
    public void changeTaskState(int state, int id) {
        executeInfoDao.updateState(state, id);
    }

}
