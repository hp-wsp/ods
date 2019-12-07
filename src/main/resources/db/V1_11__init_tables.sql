--------------------------------------------------------------------------
--添加申报进度字段
--------------------------------------------------------------------------
ALTER TABLE t_card ADD item_count SMALLINT DEFAULT 0 NOT NULL COMMENT '测评指标数';
ALTER TABLE t_card ADD dec_count SMALLINT DEFAULT 0 NOT NULL  COMMENT '申报数';
ALTER TABLE t_item ADD is_declare TINYINT DEFAULT 0 NOT NULL  COMMENT '是否申报';