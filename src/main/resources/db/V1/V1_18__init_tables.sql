-------------------------------------------------------------------------
--登录失败
-------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS l_login_limit (
  id INT(20) NOT NULL AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL COMMENT '用户名',
  l_day VARCHAR(8) NOT NULL COMMENT '天格式yyMMdd',
  l_count SMALLINT NOT NULL COMMENT '失败次数',
  PRIMARY KEY (id),
  UNIQUE KEY idx_username_day (username, l_day)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-------------------------------------------------------------------------
--验证码表
-------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS l_kaptcha (
  id INT(20) NOT NULL AUTO_INCREMENT,
  code_key VARCHAR(64) NOT NULL COMMENT '验证码KEY',
  code_value VARCHAR(8) NOT NULL COMMENT '验证码',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_code_key (code_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin