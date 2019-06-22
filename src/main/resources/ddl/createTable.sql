-- 店铺表
CREATE TABLE IF NOT EXISTS `taobao_shop` (
  `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `sys`     bit(1)              NOT NULL DEFAULT 0 comment '是否是系统商店',
  `owner`      varchar(32)         NOT NULL comment '店铺所有者',
  `title`      varchar(32)         NOT NULL comment '店铺名字',
  `icon`        mediumtext          not null comment '图标',
  `goods_sale_count` int(11)         NOT NULL DEFAULT 0 comment '出售商品数量',
  `goods_buy_count` int(11)         NOT NULL  DEFAULT 0 comment '收购商品数量',
  `total_sale_count` int(11)         NOT NULL  DEFAULT 0 comment '总共卖出去多少个商品',
  `total_sale_cost` bigint(20)         NOT NULL  DEFAULT 0 comment '出售总利润',
  `total_buy_count`      int(11)         NOT NULL  DEFAULT 0 comment '总共收购了多少个商品',
  `total_buy_cost` bigint(20)         NOT NULL  DEFAULT 0 comment '收购总花费',
  `total_cost` bigint(20)         NOT NULL  DEFAULT 0 comment '总营业额',
  `deleted`     bit(1)              NOT NULL DEFAULT 0,
  `created`     datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated`     datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP
  ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_total_cost` (`total_cost`),
  KEY `idx_owner` (`owner`),
  KEY `idx_updated` (`updated`)
);
--
-- 商品表
CREATE TABLE IF NOT EXISTS `taobao_shop_item` (
  `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `shop_id`      varchar(32)         NOT NULL,
  `type` varchar(8) not null comment '类型 sale or buy',
  `price` bigint(20)         NOT NULL comment '单价',
  `count`       int                 not null comment '库存数量',
  `max_count`       int                 not null comment '库存上限',
  `item`        mediumtext          not null comment '物品',
  `total_deal_count`       int                 not null  DEFAULT 0 comment '累计交易数量',
  `total_deal_cost`       int                 not null  DEFAULT 0 comment '累计交易金额',
  `order`       int                 not null comment '排列序号,用来排序',
  `deleted`     bit(1)              NOT NULL DEFAULT 0,
  `created`     datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated`     datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP
  ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);
--
-- 交易记录
CREATE TABLE IF NOT EXISTS `taobao_deal_log` (
  `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `shop_id`      bigint(20)         NOT NULL,
  `shot_item_type` varchar(8) not null comment '类型 sale or buy',
  `shop_item_id`      bigint(20)         NOT NULL,
  `player` varchar(32)         NOT NULL ,
  `count`   int(11) not null,
  `cost`   bigint(20) not null comment '交易额',
  `tax`   bigint(20) not null comment '税',
  `day`  date not null,
  `deleted`     bit(1)              NOT NULL DEFAULT 0,
  `created`     datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated`     datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP
  ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);
--
-- 交易规则
CREATE TABLE IF NOT EXISTS `taobao_sale_rule` (
  `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `allow_sale`   bit(1)              NOT NULL DEFAULT 1,
  `match_durability`   bit(1)              NOT NULL DEFAULT 1,
  `match_nbt`   bit(1)              NOT NULL DEFAULT 0,
  `min_price` bigint(20) not null,
  `max_price` bigint(20) not null,
  `item`        mediumtext          not null comment '物品',
  `deleted`     bit(1)              NOT NULL DEFAULT 0,
  `created`     datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated`     datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP
  ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);