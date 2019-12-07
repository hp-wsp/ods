---------------------------------------------------------------------------
--测评索引问题修复
---------------------------------------------------------------------------
ALTER TABLE e_item DROP INDEX  idx_eva_numb;
CREATE UNIQUE INDEX idx_eva_id_eva_numb ON e_item (eva_id, eva_num);