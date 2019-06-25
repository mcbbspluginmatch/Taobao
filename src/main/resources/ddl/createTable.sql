-- 店铺表
create table if not exists `taobao_shop` (
  `id`          bigint(20) unsigned not null auto_increment,
  `sys`     bit(1)              not null default 0 comment '是否是系统商店',
  `owner`      varchar(32)         not null comment '店铺所有者',
  `title`      varchar(32)         not null comment '店铺名字',
  `icon`        mediumtext          not null comment '图标',
  `goods_sale_count` int(11)         not null default 0 comment '出售商品数量',
  `goods_buy_count` int(11)         not null  default 0 comment '收购商品数量',
  `total_sale_count` int(11)         not null  default 0 comment '总共卖出去多少个商品',
  `total_sale_cost` bigint(20)         not null  default 0 comment '出售总利润',
  `total_buy_count`      int(11)         not null  default 0 comment '总共收购了多少个商品',
  `total_buy_cost` bigint(20)         not null  default 0 comment '收购总花费',
  `total_cost` bigint(20)         not null  default 0 comment '总营业额',
  `deleted`     bit(1)              not null default 0,
  `updated`     timestamp            not null default current_timestamp on update current_timestamp,
  `created`     timestamp                null,
  primary key (`id`),
  key `taobao_shop_idx_total_cost` (`total_cost`),
  key `taobao_shop_idx_owner` (`owner`),
  key `taobao_shop_idx_updated` (`updated`)
);
--
-- 商品表
create table if not exists `taobao_shop_item` (
  `id`          bigint(20) unsigned not null auto_increment,
  `shop_id`      varchar(32)         not null,
  `type` varchar(8) not null comment '类型 sale or buy',
  `price` bigint(20)         not null comment '单价',
  `count`       int                 not null comment '库存数量',
  `max_count`       int                 not null comment '库存上限',
  `item`        mediumtext          not null comment '物品',
  `total_deal_count`       int                 not null  default 0 comment '累计交易数量',
  `total_deal_cost`       int                 not null  default 0 comment '累计交易金额',
  `order`       int                 not null comment '排列序号,用来排序',
  `deleted`     bit(1)              not null default 0,
  `updated`     timestamp            not null default current_timestamp on update current_timestamp,
  `created`     timestamp                null,
  primary key (`id`)
);
--
-- 交易记录
create table if not exists `taobao_deal_log` (
  `id`          bigint(20) unsigned not null auto_increment,
  `shop_id`      bigint(20)         not null,
  `shot_item_type` varchar(8) not null comment '类型 sale or buy',
  `shop_item_id`      bigint(20)         not null,
  `player` varchar(32)         not null ,
  `count`   int(11) not null,
  `cost`   bigint(20) not null comment '交易额',
  `tax`   bigint(20) not null comment '税',
  `day`  date not null,
  `deleted`     bit(1)              not null default 0,
  `updated`     timestamp            not null default current_timestamp on update current_timestamp,
  `created`     timestamp                null,
  primary key (`id`)
);
--
-- 交易规则
create table if not exists `taobao_sale_rule` (
  `id`          bigint(20) unsigned not null auto_increment,
  `allow_sale`   bit(1)              not null default 1,
  `match_durability`   bit(1)              not null default 1,
  `match_nbt`   bit(1)              not null default 0,
  `min_price` bigint(20) not null,
  `max_price` bigint(20) not null,
  `item`        mediumtext          not null comment '物品',
  `deleted`     bit(1)              not null default 0,
  `updated`     timestamp            not null default current_timestamp on update current_timestamp,
  `created`     timestamp                null,
  primary key (`id`)
);