---------------------------------------------------------------------------
--测评索引问题修复
---------------------------------------------------------------------------
ALTER TABLE e_item ADD format_num VARCHAR(30) DEFAULT '' NOT NULL COMMENT '格式化测评指标编号(排序使用)';
CREATE INDEX idx_eva_id_format_numb ON e_item (eva_id, format_num);