---------------------------------------------------------------------------
--评审资源添加测评指标编号
---------------------------------------------------------------------------
ALTER TABLE t_declaration ADD eva_item_id CHAR(32) DEFAULT '' NOT NULL COMMENT '测评指标编号';
CREATE INDEX idx_eva_item_id ON t_declaration (eva_item_id);