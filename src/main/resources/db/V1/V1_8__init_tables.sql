-------------------------------------------------------------------------
--导出任务
-------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS e_export (
  id CHAR(32) NOT NULL,
  eva_id CHAR(32) NOT NULL COMMENT '测评编号',
  eva_name VARCHAR(30) NOT NULL COMMENT '测评名称',
  status ENUM('RUNNING','SUCCESS','FAIL') NOT NULL DEFAULT 'RUNNING' COMMENT '状态',
  path VARCHAR(200) COMMENT '保存路径',
  err_msg VARCHAR(200) COMMENT '错误信息',
  from_time DATETIME NOT NULL COMMENT '开始时间',
  to_time DATETIME COMMENT '结束时间',
  PRIMARY KEY (id),
  INDEX idx_eva_id (eva_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;