-------------------------------------------------------------------------------------------
-- 增加字段长度
-------------------------------------------------------------------------------------------
ALTER TABLE t_declaration MODIFY COLUMN content_type varchar(128) ;
ALTER TABLE b_resource MODIFY COLUMN content_type varchar(128) ;