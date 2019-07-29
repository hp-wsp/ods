-------------------------------------------------------------------------------------------
-- 修改字段类型
-------------------------------------------------------------------------------------------
ALTER TABLE t_card MODIFY COLUMN score SMALLINT DEFAULT 0 NOT NULL COMMENT '赋权';
ALTER TABLE t_card MODIFY COLUMN grade_score SMALLINT DEFAULT 0 NOT NULL COMMENT '得分';