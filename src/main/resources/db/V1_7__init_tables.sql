-------------------------------------------------------------------------
--资源
-------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS b_resource (
  id CHAR(32) NOT NULL,
  file_name VARCHAR(30) NOT NULL COMMENT '文件名',
  file_size INT NOT NULL COMMENT '文件大小',
  content_type VARCHAR(64) NOT NULL COMMENT '文件类型',
  path VARCHAR(200) NOT NULL COMMENT '保存路径',
  type VARCHAR(20) COMMENT '资源类型',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--------------------------------------------------------------------------
--添加导出字段
--------------------------------------------------------------------------
ALTER TABLE e_evaluation ADD is_export TINYINT DEFAULT 0 NOT NULL COMMENT '1=导出';
ALTER TABLE e_evaluation ADD export_id VARCHAR(32)  COMMENT '导出编号';