-------------------------------------------------------------------------
--短信日志
-------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS l_evaluation (
  id CHAR(32) NOT NULL,
  eva_id  CHAR(32) NOT NULL COMMENT '评测编号',
  day VARCHAR(12) NOT NULL COMMENT '天(yyyy/MM/dd)',
  detail VARCHAR(200) NOT NULL COMMENT '描述',
  username VARCHAR(200) NOT NULL COMMENT '用户名',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  INDEX idx_eva_id (eva_id),
  INDEX idx_day (day)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;