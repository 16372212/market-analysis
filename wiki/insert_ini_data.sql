INSERT INTO `user` (`id`, `username`, `password`, `name`) VALUES ('1', 'wild', 'e10adc3949ba59abbe56e057f20f883e', 'wild');

INSERT INTO `system_config` (`id`, `name`, `value1`, `value2`, `value3`, `state`) VALUES ('1', 'trade_mock', '0', '', '', '1');
INSERT INTO `system_config` (`id`, `name`, `value1`, `value2`, `value3`, `state`) VALUES ('2', 'apply_new_convertible_bond', '1', '', '', '1');
INSERT INTO `system_config` (`id`, `name`, `value1`, `value2`, `value3`, `state`) VALUES ('3', 'trade_cr', '1', '', '', 1);


INSERT INTO `task` (`id`, `name`, `state`, `description`) VALUES ('1', 'begin_of_year', '1', 'begin of year');
INSERT INTO `task` (`id`, `name`, `state`, `description`) VALUES ('2', 'begin_of_day', '1', 'begin of day');
INSERT INTO `task` (`id`, `name`, `state`, `description`) VALUES ('3', 'update_of_stock', '1', 'update of stock');
INSERT INTO `task` (`id`, `name`, `state`, `description`) VALUES ('4', 'update_of_daily_index', '1', 'update of daily index');
INSERT INTO `task` (`id`, `name`, `state`, `description`) VALUES ('5', 'ticker', '1', 'ticker');
INSERT INTO `task` (`id`, `name`, `state`, `description`) VALUES ('6', 'trade_ticker', '1', 'trade ticker');
INSERT INTO `task` (`id`, `name`, `state`, `description`) VALUES ('7', 'apply_new_stock', '1', 'apply new stock');
INSERT INTO `task` (`id`, `name`, `state`, `description`) VALUES ('8', 'auto_login', '1', 'auto login');

INSERT INTO `execute_info` (`id`, `task_id`, `state`) VALUES ('1', '1', '2');
INSERT INTO `execute_info` (`id`, `task_id`, `state`) VALUES ('2', '2', '2');
INSERT INTO `execute_info` (`id`, `task_id`, `state`) VALUES ('3', '3', '2');
INSERT INTO `execute_info` (`id`, `task_id`, `state`) VALUES ('4', '4', '2');
INSERT INTO `execute_info` (`id`, `task_id`, `state`) VALUES ('5', '5', '2');
INSERT INTO `execute_info` (`id`, `task_id`, `state`) VALUES ('6', '6', '2');
INSERT INTO `execute_info` (`id`, `task_id`, `state`) VALUES ('7', '7', '2');
INSERT INTO `execute_info` (`id`, `task_id`, `state`) VALUES ('8', '8', '2');

INSERT INTO `robot` (`id`, `type`, `webhook`, `state`) VALUES ('1', '0', 'http://webhook', '1');

INSERT INTO `stock_selected` (`id`, `code`, `rate`) VALUES ('1', '300059', '0.02');

