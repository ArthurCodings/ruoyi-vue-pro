/*
 Quartz 表名大小写修复脚本

 问题：Linux MySQL lower_case_table_names=0 下，Quartz 查 QRTZ_* 而实际为 qrtz_*，导致
      "Table 'ruoyi-vue-pro.QRTZ_LOCKS' doesn't exist" 等错误

 解决：先删除可能存在的空大写表，再将 qrtz_* 小写表重命名为 QRTZ_* 大写（保留数据）

 注意：必须用 -f 执行，部分表可能不存在会报错，-f 会继续执行

 用法：docker exec -i mysql mysql -f -u root -pTH0901th ruoyi-vue-pro < quartz-fix-table-case.sql
*/

USE `ruoyi-vue-pro`;

SET FOREIGN_KEY_CHECKS = 0;

-- ========== 第一步：删除可能存在的空大写表 ==========
DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
DROP TABLE IF EXISTS `QRTZ_LOCKS`;
DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
DROP TABLE IF EXISTS `QRTZ_SIMPROP_TRIGGERS`;
DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;

-- ========== 第二步：将小写表重命名为大写（保留数据） ==========
RENAME TABLE `qrtz_blob_triggers` TO `QRTZ_BLOB_TRIGGERS`;
RENAME TABLE `qrtz_calendars` TO `QRTZ_CALENDARS`;
RENAME TABLE `qrtz_cron_triggers` TO `QRTZ_CRON_TRIGGERS`;
RENAME TABLE `qrtz_fired_triggers` TO `QRTZ_FIRED_TRIGGERS`;
RENAME TABLE `qrtz_job_details` TO `QRTZ_JOB_DETAILS`;
RENAME TABLE `qrtz_locks` TO `QRTZ_LOCKS`;
RENAME TABLE `qrtz_paused_trigger_grps` TO `QRTZ_PAUSED_TRIGGER_GRPS`;
RENAME TABLE `qrtz_scheduler_state` TO `QRTZ_SCHEDULER_STATE`;
RENAME TABLE `qrtz_simple_triggers` TO `QRTZ_SIMPLE_TRIGGERS`;
RENAME TABLE `qrtz_simprop_triggers` TO `QRTZ_SIMPROP_TRIGGERS`;
RENAME TABLE `qrtz_triggers` TO `QRTZ_TRIGGERS`;

SET FOREIGN_KEY_CHECKS = 1;
