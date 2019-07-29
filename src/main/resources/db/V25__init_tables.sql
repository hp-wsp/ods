--------------------------------------------------------------------------
--添加单位名称和姓名
--------------------------------------------------------------------------
ALTER TABLE l_sms ADD company_name VARCHAR(30) DEFAULT '' COMMENT '单位名称';
ALTER TABLE l_sms ADD name VARCHAR(30)  DEFAULT '' COMMENT '姓名';