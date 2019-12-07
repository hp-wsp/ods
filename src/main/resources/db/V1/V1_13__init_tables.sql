--------------------------------------------------------------------------
--添加添加发送短信通知字段
--------------------------------------------------------------------------
ALTER TABLE e_evaluation ADD is_sms TINYINT DEFAULT 0 NOT NULL COMMENT '是否发送申报通知短信';

--------------------------------------------------------------------------
--删除导出评测表
--------------------------------------------------------------------------
DROP TABLE e_export;

--------------------------------------------------------------------------
--增加执行任务表
--------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS l_exec (
  id CHAR(32) NOT NULL,
  task_key VARCHAR(64) NOT NULL COMMENT '任务KEY',
  remark VARCHAR(30) COMMENT '备注',
  status ENUM('RUNNING','SUCCESS','FAIL') NOT NULL DEFAULT 'RUNNING' COMMENT '状态',
  err_msg VARCHAR(200) COMMENT '错误信息',
  from_time DATETIME NOT NULL COMMENT '开始时间',
  to_time DATETIME COMMENT '结束时间',
  PRIMARY KEY (id),
  INDEX idx_task_key (task_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;