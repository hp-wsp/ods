-----------------------------------------------------------------------------
--评分比例
-----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS b_grade_rate (
  id CHAR(32) NOT NULL,
  level VARCHAR(5) NOT NULL COMMENT '级别',
  rate TINYINT DEFAULT 0 NOT NULL COMMENT '比例',
  update_time DATETIME NOT NULL COMMENT '修改时间',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;