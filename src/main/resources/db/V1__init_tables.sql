-------------------------------------------------------------------------
--创建业务数据表
-------------------------------------------------------------------------

-------------------------------------------------------------------------
--单位
-------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS b_company (
  id CHAR(32) NOT NULL,
  name VARCHAR(30) NOT NULL COMMENT '名称',
  phone VARCHAR(20) COMMENT '联系电话',
  contact VARCHAR(15) COMMENT '联系人',
  is_delete TINYINT DEFAULT 0 NOT NULL COMMENT '1=删除',
  update_time DATETIME NOT NULL COMMENT '修改时间',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-------------------------------------------------------------------------
--管理员表
-------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS b_manager (
  id CHAR(32) NOT NULL,
  username VARCHAR(30) NOT NULL COMMENT '用户名',
  password VARCHAR(50) NOT NULL COMMENT '密码',
  name VARCHAR(30) COMMENT '姓名',
  phone VARCHAR(20) DEFAULT '' COMMENT '联系电话',
  email VARCHAR(50) DEFAULT '' COMMENT '邮件地址',
  role VARCHAR(30) DEFAULT '' NOT NULL COMMENT '权限角色',
  is_root TINYINT DEFAULT 0 NOT NULL COMMENT '1=超级用户',
  is_forbid TINYINT DEFAULT 1 NOT NULL COMMENT '1=禁用',
  is_delete TINYINT DEFAULT 0 NOT NULL COMMENT '1=删除',
  update_time DATETIME NOT NULL COMMENT '修改时间',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--初始化系统管理员
INSERT INTO b_manager(id, username, password, name, role, is_root, is_forbid, is_delete, update_time, create_time)
VALUES ('1', 'admin', '12345678', 'admin', "ROLE_SYS", 1, 0, 0, now(), now());

-------------------------------------------------------------------------
--申报员表
-------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS b_member (
  id CHAR(32) NOT NULL,
  username VARCHAR(30) NOT NULL COMMENT '用户名',
  password VARCHAR(50) NOT NULL COMMENT '密码',
  name VARCHAR(30) COMMENT '姓名',
  phone VARCHAR(20) DEFAULT '' COMMENT '联系电话',
  company_id CHAR(32) NOT NULL COMMENT '单位编号',
  company_name VARCHAR(32) NOT NULL COMMENT '单位名称',
  is_delete TINYINT DEFAULT 0 NOT NULL COMMENT '1=删除',
  update_time DATETIME NOT NULL COMMENT '修改时间',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_username (username),
  INDEX idx_company_id (company_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-------------------------------------------------------------------------
--测评
-------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS e_evaluation (
  id CHAR(32) NOT NULL,
  name VARCHAR(30) NOT NULL COMMENT '名称',
  remark VARCHAR(200) COMMENT '测评说明',
  from_time DATETIME NOT NULL COMMENT '开始时间',
  to_time DATETIME NOT NULL COMMENT '结束时间',
  status ENUM('WAIT','OPEN','CLOSE') NOT NULL DEFAULT 'WAIT' COMMENT '状态',
  is_delete TINYINT DEFAULT 0 NOT NULL COMMENT '1=删除',
  update_time DATETIME NOT NULL COMMENT '修改时间',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-------------------------------------------------------------------------
--测评指标
-------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS e_item (
  id CHAR(32) NOT NULL,
  eva_id CHAR(32) NOT NULL COMMENT '评测编号',
  eva_num VARCHAR(30) NOT NULL COMMENT '测评指标编号',
  require_content VARCHAR(300) NOT NULL COMMENT '具体要求',
  grade_content VARCHAR(200) NOT NULL COMMENT '评分标准',
  results VARCHAR(200) NOT NULL COMMENT '结果集合',
  remark VARCHAR(50) COMMENT '说明',
  update_time DATETIME NOT NULL COMMENT '修改时间',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_eva_numb (eva_num)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-------------------------------------------------------------------------
--评测卡
-------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS t_card (
  id CHAR(32) NOT NULL,
  eva_id CHAR(32) NOT NULL COMMENT '评测编号',
  eva_name VARCHAR(30) NOT NULL COMMENT '评测名称',
  company_id CHAR(32) NOT NULL COMMENT '单位编号',
  company_name VARCHAR(30) NOT NULL COMMENT '单位名称',
  ass_id CHAR(32) NOT NULL COMMENT '审核员编号',
  ass_username VARCHAR(30) NOT NULL COMMENT '审核用户名',
  dec_id CHAR(32) NOT NULL COMMENT '申报人员编号',
  dec_username VARCHAR(30) NOT NULL COMMENT '申报人员用户名',
  is_open TINYINT DEFAULT 0 NOT NULL COMMENT '1=开启',
  status ENUM('WAIT', 'SUBMIT', 'BACK', 'GRADE') NOT NULL DEFAULT 'WAIT' COMMENT '状态',
  update_time DATETIME NOT NULL COMMENT '修改时间',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_eva_id_company_id (eva_id, company_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-------------------------------------------------------------------------
--测评卡指标
-------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS t_item (
  id CHAR(32) NOT NULL,
  card_id CHAR(32) NOT NULL COMMENT '评测编号',
  eva_item_id CHAR(32) NOT NULL COMMENT '评测指标系统编号',
  eva_num VARCHAR(30) NOT NULL COMMENT '评测指标编号',
  require_content VARCHAR(300) NOT NULL COMMENT '具体要求',
  grade_content VARCHAR(200) NOT NULL COMMENT '评分标准',
  remark VARCHAR(50) COMMENT '说明',
  score SMALLINT NOT NULL COMMENT '赋权',
  result_labels VARCHAR(200) NOT NULL COMMENT '结果标签集合',
  result_scores VARCHAR(200) NOT NULL COMMENT '结果分数集合',
  is_grade TINYINT NOT NULL DEFAULT 0 COMMENT '是否评分1=评分',
  grade_level VARCHAR(10) COMMENT '评分等级',
  grade_score TINYINT DEFAULT 0  NOT NULL COMMENT '得分',
  grade_remark VARCHAR(50) COMMENT '材料问题',
  update_time DATETIME NOT NULL COMMENT '修改时间',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_eva_item_id_card_id (card_id, eva_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

---------------------------------------------------------------------------
--测评申报资源
---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS t_declaration (
  id CHAR(32) NOT NULL,
  card_id CHAR(32) NOT NULL COMMENT '测评卡编号',
  item_id CHAR(32) NOT NULL COMMENT '评测卡指标编号',
  file_name VARCHAR(64) NOT NULL COMMENT '申报资源文件名',
  file_size INT NOT NULL COMMENT '文件大小',
  content_type VARCHAR(64) NOT NULL COMMENT '文件类型',
  path VARCHAR(200) NOT NULL COMMENT '保存路径',
  remark VARCHAR(50) COMMENT '说明',
  dec_username VARCHAR(30) NOT NULL COMMENT '申报员用户名',
  is_delete TINYINT NOT NULL DEFAULT 0 COMMENT '1=删除',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  INDEX idx_card_id (card_id),
  INDEX idx_item_id (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;