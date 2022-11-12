function renderTradeMenu(current) {
  var arr = [
    { id: 1, title: '定时任务', url: '/system/taskList.html' },
    { id: 3, title: '数据来源配置', url: '/system/configList.html' }
  ];

  renderMenu(arr, '.menu-nav', current);
}
