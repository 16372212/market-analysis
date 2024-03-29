package com.market.stock.service.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.market.stock.dao.DailyIndexDao;
import com.market.stock.dao.StockInfoDao;
import com.market.stock.dao.StockLogDao;
import com.market.stock.model.vo.DailyIndexVo;
import com.market.stock.model.vo.PageParam;
import com.market.stock.model.vo.PageVo;
import com.market.stock.parser.DailyIndexParser;
import com.market.stock.util.StockConsts;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import com.market.stock.model.po.DailyIndex;
import com.market.stock.model.po.StockInfo;
import com.market.stock.model.po.StockLog;
import com.market.stock.service.StockCrawlerService;
import com.market.stock.service.StockService;

@Service
public class StockServiceImpl implements StockService {

    private final Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);

    private static final String LIST_MESSAGE = "'list' must not be null";

    @Autowired
    private StockInfoDao stockInfoDao;

    @Autowired
    private StockLogDao stockLogDao;

    @Autowired
    private DailyIndexDao dailyIndexDao;

    @Autowired
    private StockCrawlerService stockCrawlerService;

    @Autowired
    private DailyIndexParser dailyIndexParser;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<StockInfo> getAll() {
        PageParam pageParam = new PageParam();
        pageParam.setStart(0);
        pageParam.setLength(Integer.MAX_VALUE);
        PageVo<StockInfo> pageVo = stockInfoDao.get(pageParam);
        return pageVo.getData();
    }

    @Override
    public List<StockInfo> getAllListed() {
        return getAll().stream().filter(stockInfo ->
            stockInfo.isValid() && stockInfo.isA()
        ).collect(Collectors.toList());
    }

    @Override
    public void addStockLog(List<StockLog> list) {
        Assert.notNull(list, StockServiceImpl.LIST_MESSAGE);
        if (!list.isEmpty()) {
            stockLogDao.add(list);
        }
    }

    @CacheEvict(value = StockConsts.CACHE_KEY_DATA_STOCK, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(List<StockInfo> needAddedList, List<StockInfo> needUpdatedList, List<StockLog> stockLogList) {
        if (needAddedList != null) {
            add(needAddedList);
        }
        if (needUpdatedList != null) {
            update(needUpdatedList);
        }
        if (stockLogList != null) {
            addStockLog(stockLogList);
        }
        if (needAddedList != null && !needAddedList.isEmpty()) {
            List<String> newCodeList = needAddedList.stream().map(StockInfo::getCode)
                    .collect(Collectors.toList());
            stockLogDao.setStockIdByCodeType(newCodeList, StockConsts.StockLogType.New.value());
        }

        // update Redis
        needAddedList.forEach( stockInfo -> {
            redisTemplate.opsForValue().set(stockInfo.getCode(), JSON.toJSONString(stockInfo), 20, TimeUnit.SECONDS);
        });

        needUpdatedList.forEach(stockInfo -> {
            redisTemplate.opsForValue().set(stockInfo.getCode(), JSON.toJSONString(stockInfo), 20, TimeUnit.SECONDS);
        });
    }

    private void add(List<StockInfo> list) {
        Assert.notNull(list, StockServiceImpl.LIST_MESSAGE);
        if (!list.isEmpty()) {
            stockInfoDao.add(list);
        }
    }

    private void update(List<StockInfo> list) {
        Assert.notNull(list, StockServiceImpl.LIST_MESSAGE);
        if (!list.isEmpty()) {
            stockInfoDao.update(list);
        }
    }

    @Override
    public void saveDailyIndexToFile(String rootPath) {
        List<StockInfo> list = getAll().stream().filter(StockInfo::isA).collect(Collectors.toList());

        File root = new File(rootPath);

        list.forEach(stockInfo -> {
            logger.info("start save {}: {}", stockInfo.getName(), stockInfo.getCode());
            try {
                File file = new File(root, stockInfo.getExchange() + "/" + stockInfo.getCode() + ".js");
                if (file.length() < 5 * 1024) {
                    // String content = stockCrawlerService.getHistoryDailyIndexsString(stockInfo.getCode());
                    List<DailyIndex> resultList = stockCrawlerService.getHistoryDailyIndexs(stockInfo.getCode());
                    List<List<String>> cont = new ArrayList<>();
                    resultList.forEach(dailyIndex -> {
                        List<String> temp = new ArrayList<>();

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                        String transformDate=simpleDateFormat.format(dailyIndex.getDate());
                        temp.add("'" + transformDate + "'");

                        temp.add(dailyIndex.getOpeningPrice().toString());
                        temp.add(dailyIndex.getClosingPrice().toString());
                        temp.add(dailyIndex.getLowestPrice().toString());
                        temp.add(dailyIndex.getHighestPrice().toString());
                        temp.add(dailyIndex.getPreClosingPrice().toString());
                        cont.add(temp);
                    });
                    try (FileWriter out = new FileWriter(file)) {

                        String content = "var kdata = " + cont + "; var mdata = {};";
                        FileCopyUtils.copy(content, out);
                    }
                }
            } catch (Exception e) {
                logger.error("get daily index error {} {}", stockInfo.getName(), stockInfo.getCode(), e);
            }
        });
    }

    @Override
    public void saveDailyIndexFromFile(String rootPath) {
        List<StockInfo> list = getAll().stream().filter(StockInfo::isA).collect(Collectors.toList());

        File root = new File(rootPath);

        CountDownLatch countDownLatch = new CountDownLatch(list.size());

        list.forEach(stockInfo -> threadPoolTaskExecutor.execute(() -> {
            try {
                handleStockDaily(root, stockInfo);
            } finally {
                countDownLatch.countDown();
            }
        }));

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("countDownLatch await interrupt", e);
        }
        logger.info("sub task is not completed");
    }

    private void handleStockDaily(File root, StockInfo stockInfo) {
        logger.info("start save {}: {}", stockInfo.getName(), stockInfo.getCode());
        try {
            File file = new File(root, stockInfo.getExchange() + "/" + stockInfo.getCode() + ".txt");
            try (FileReader in = new FileReader(file)) {
                String content = FileCopyUtils.copyToString(in);
                List<DailyIndex> dailyIndexList = dailyIndexParser.parseHistoryDailyIndexList(content);
                dailyIndexList.forEach(dailyIndex -> dailyIndex.setCode(stockInfo.getFullCode()));
                dailyIndexDao.save(dailyIndexList);
            }
        } catch (Exception e) {
            logger.error("save daily index error {}", stockInfo.getFullCode(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveDailyIndex(List<DailyIndex> list) {
        dailyIndexDao.save(list);
    }

    @Override
    public PageVo<StockInfo> getStockList(PageParam pageParam) {
        return stockInfoDao.get(pageParam);
    }

    @Cacheable(value = StockConsts.CACHE_KEY_DATA_STOCK, key = "#code")
    @Override
    public StockInfo getStockByFullCode(String code) {
        String jsonString = (String) redisTemplate.opsForValue().get(code);
        StockInfo stockInfo = JSON.parseObject(jsonString, StockInfo.class);
        if (Objects.nonNull(stockInfo)) {
            logger.info("从Redis中查询到数据 {}: {}", stockInfo.getName(), stockInfo.getCode());
            return stockInfo;
        }

        stockInfo = stockInfoDao.getStockByFullCode(code);
        if (stockInfo == null) {
            stockInfo = new StockInfo();
            stockInfo.setAbbreviation("wlrzq");
            stockInfo.setCode(code);
            stockInfo.setName("未录入证券");
            stockInfo.setExchange(StockConsts.Exchange.SH.getName());
            stockInfo.setState(StockConsts.StockState.Terminated.value());
            stockInfo.setType(StockConsts.StockType.A.value());
        } else {
            redisTemplate.opsForValue().set(code, JSON.toJSONString(stockInfo), 20, TimeUnit.SECONDS);
        }
        return stockInfo;
    }

    @Override
    public PageVo<DailyIndexVo> getDailyIndexList(PageParam pageParam) {
        pageParam.getCondition().put("date", DateFormatUtils.format(new Date(), "yyyy-MM-dd") );
        return dailyIndexDao.getDailyIndexList(pageParam);
    }

    @Override
    public List<DailyIndex> getDailyIndexListByDate(Date date) {
        return dailyIndexDao.getDailyIndexListByDate(date);
    }

    @Override
    public DailyIndexVo getDailyIndexByCode(String code) {
        DailyIndex dailyIndex = stockCrawlerService.getDailyIndex(code);
        DailyIndexVo dailyIndexVo = new DailyIndexVo();
        if (Objects.isNull(dailyIndex)) {
            return dailyIndexVo;
        }
        dailyIndexVo.setCode(code);
        dailyIndexVo.setDate(dailyIndex.getDate());
        dailyIndexVo.setHighestPrice(dailyIndex.getHighestPrice());
        dailyIndexVo.setClosingPrice(dailyIndex.getClosingPrice());
        dailyIndexVo.setLowestPrice(dailyIndex.getLowestPrice());
        dailyIndexVo.setOpeningPrice(dailyIndex.getOpeningPrice());
        dailyIndexVo.setPreClosingPrice(dailyIndex.getPreClosingPrice());
        dailyIndexVo.setRurnoverRate(dailyIndex.getRurnoverRate());
        return dailyIndexVo;
    }

    @Override
    public void fixDailyIndex(int date, List<String> code) {
        List<StockInfo> list = getAll().stream().filter(StockInfo::isA).collect(Collectors.toList());
        if (code != null && !code.isEmpty()) {
            list = getAll().stream().filter(v -> code.contains(v.getCode())).collect(Collectors.toList());
        }
        list.forEach(stockInfo -> {
            logger.info("start fixDailyIndex {}: {}", stockInfo.getName(), stockInfo.getCode());
            try {
                int year = date / 100;
                int season = (date % 100 - 1) / 3 + 1;
                String content = stockCrawlerService.getHistoryDailyIndexsStringFromSina(stockInfo.getCode(), year, season);
                List<DailyIndex> dailyIndexList = dailyIndexParser.parse163HistoryDailyIndexList(content);
                dailyIndexList.forEach(d -> d.setCode(stockInfo.getFullCode()));
                dailyIndexDao.save(dailyIndexList);
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                logger.error("fixDailyIndex error {} {}", stockInfo.getName(), stockInfo.getCode(), e);
            }
        });
    }

}
