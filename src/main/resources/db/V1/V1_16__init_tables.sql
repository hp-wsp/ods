-------------------------------------------------------------------------
--操作日志
-------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS l_operator (
  id INT(20) NOT NULL AUTO_INCREMENT,
  detail VARCHAR(200) NOT NULL COMMENT '描述',
  params VARCHAR(200) COMMENT '参数',
  username VARCHAR(64) NOT NULL COMMENT '用户名',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  INDEX idx_username (username),
  INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;