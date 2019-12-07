--------------------------------------------------------------------------
--增加开启审核
--------------------------------------------------------------------------
ALTER TABLE b_member ADD is_manager TINYINT DEFAULT 0 NOT NULL COMMENT '1=是管理员';
ALTER TABLE b_member DROP company_name;