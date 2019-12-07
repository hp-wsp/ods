--------------------------------------------------------------------------
--修改操作日志
--------------------------------------------------------------------------
ALTER TABLE l_operator ADD t_name VARCHAR(80) DEFAULT "" NOT NULL COMMENT '日志名称';
ALTER TABLE l_operator ADD t_type VARCHAR(32) DEFAULT "" NOT NULL COMMENT '日志类型';
ALTER TABLE l_operator DROP params;
CREATE INDEX idx_t_type ON l_operator (t_type);

DROP TABLE l_evaluation;

--------------------------------------------------------------------------
--增加自动开启申报标志
--------------------------------------------------------------------------
ALTER TABLE e_evaluation ADD is_auto TINYINT DEFAULT 0 NOT NULL COMMENT '1=自动开启申报';
