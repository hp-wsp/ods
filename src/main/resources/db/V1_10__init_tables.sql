-------------------------------------------------------------------------
--短信日志
-------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS l_sms (
  id CHAR(32) NOT NULL,
  phone VARCHAR(20) NOT NULL COMMENT '联系电话',
  content VARCHAR(500) NOT NULL COMMENT '短信内容',
  err_code INT NOT NULL DEFAULT 0 COMMENT '错误码',
  err_msg VARCHAR(200) COMMENT '错误信息',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;