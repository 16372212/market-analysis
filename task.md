# 大致要求

针对不同的⾏情发布机构和不同 的⾏情协议开发特定的程序来接收⾏情信息

后端功能：
1. 接收外部⾏情信息(上海或深圳交易所股票买卖⾏情信息导⼊)
2. 以相对统⼀的数据结构将⾏情信息落地到本地关系型数据库和内存数据库中
3. 将⾏情信息⼴播推送给相应的内部订阅者。

⾼性能接收，⾼性能发布

前端功能：⾏情查询界⾯，输⼊股票代码可以查出5档⾏情


# 可选⽬标： 

1. 开发前端界⾯绘制⾏情⾛势（参考腾讯分时⾏情绘制⾯版） 
2. 框架可扩展，后续可以通过插件⽅式接⼊各期货交易所及全球主要市场⾏情 
3. ⾏情可监控，未来可以开发各种监控程序，基于⾏情变化做相应的提醒或⾃动下指令处理。 
4. ⾏情存储对象抽象统⼀，不会因不同交易所不同消息协议⼤改⾏情存储结构

# 产出：

数据库脚本、代码、设计文档、说明文档


# 计划

11.3-11.5 数据介入，系统写入后端
11.5-11.7 前段代码实现

https://github.com/2557606319/H5-Kline


# 接口设计：

## 1. 获取行情数据 （内部接口）

> 这里是根据什么频率读取的呢，读取的数据在存下来，可是这个数据是固定的，存储什么类型的数据呢 

## 2. 用户门户网站

用户注册、注销、修改密码、登陆

## 3. 查看行情（不同时间）

读取

查看

## 4. 订阅股票

用户订阅股票

查看某用户订阅的股票

查看自己订阅股票集合

取消订阅股票

## 5. 广播栏目

查看订阅这个股票的用户

发送系统推送

给用户发短信

用户设计出发阈值的等级？