-------------------------------------------------------------------------
--测评卡增加得分字段
-------------------------------------------------------------------------
ALTER TABLE t_card ADD score TINYINT DEFAULT 0 NOT NULL COMMENT '得分';