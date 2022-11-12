function renderReportMenu(current) {
  var arr = [
    { id: 1, title: '股票列表', url: '/report/stockList.html' },
    { id: 2, title: '每日统计', url: '/report/dailyList.html' },
    { id: 3, title: '股票查询', url: '/report/searchStock.html' },
    { id: 4, title: '我的订阅', url: '/report/stockBookList.html' }
  ];

  renderMenu(arr, '.menu-nav', current);
}
