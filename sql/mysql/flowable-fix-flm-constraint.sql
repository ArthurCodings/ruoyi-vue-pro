/*
 Flowable 事件注册表 - 重复外键约束修复

 问题：Flowable 启动时报 Duplicate foreign key constraint name 'FLM_FK_EVENT_RSRC_DPL'
      因 FLM_EVENT_RESOURCE 表上该外键已存在，Flowable 再次尝试添加时失败

 解决：删除重复的外键约束。配合 application-dev.yaml 中 eventregistry.enabled: false 使用

 用法：docker exec -i mysql mysql -f -u root -pTH0901th ruoyi-vue-pro < flowable-fix-flm-constraint.sql
*/

USE `ruoyi-vue-pro`;

SET FOREIGN_KEY_CHECKS = 0;

-- 删除 FLM_EVENT_RESOURCE 上的重复外键（表名/约束名以 information_schema 为准）
-- 若表或约束不存在会报错，用 -f 忽略继续
ALTER TABLE `FLM_EVENT_RESOURCE` DROP FOREIGN KEY `FLM_FK_EVENT_RSRC_DPL`;

SET FOREIGN_KEY_CHECKS = 1;
