/*
 * ruoyi-vue-pro OA 精简数据脚本（在导入原始 ruoyi-vue-pro.sql 之后再执行）
 *
 * 设计目标：
 * - 保留【系统必需】的表结构、字典、菜单、角色等配置；
 * - 清空各种【日志、令牌、运行时记录】；
 * - 清空官方自带的【演示表 yudao_demoXX_*】数据；
 * - 清空示例公告、站内信等演示内容；
 *
 * 使用方式（MySQL 命令行示例）：
 *   1）先执行官方初始化脚本：
 *      source sql/mysql/ruoyi-vue-pro.sql;
 *   2）再执行本精简脚本：
 *      source sql/mysql/ruoyi-vue-pro-oa-clean.sql;
 *
 * 如需进一步精简（例如删除多余账号、部门），可在本文件后面追加自己的 DELETE 语句。
 */

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

USE `ruoyi-vue-pro`;

-- ============================================================================
-- 一、清空运行日志类数据（不影响功能）
-- ============================================================================

TRUNCATE TABLE `infra_api_access_log`;
TRUNCATE TABLE `infra_api_error_log`;
TRUNCATE TABLE `infra_job_log`;

TRUNCATE TABLE `system_login_log`;
TRUNCATE TABLE `system_operate_log`;

TRUNCATE TABLE `system_sms_code`;
TRUNCATE TABLE `system_sms_log`;
TRUNCATE TABLE `system_mail_log`;

-- ============================================================================
-- 二、清空 OAuth2 / 社交登录 等令牌类运行数据
-- ============================================================================

TRUNCATE TABLE `system_oauth2_access_token`;
TRUNCATE TABLE `system_oauth2_approve`;
TRUNCATE TABLE `system_oauth2_code`;
TRUNCATE TABLE `system_oauth2_refresh_token`;

TRUNCATE TABLE `system_social_user`;
TRUNCATE TABLE `system_social_user_bind`;

-- ============================================================================
-- 三、清空站内信、公告等演示内容（保留模板配置本身）
-- ============================================================================

TRUNCATE TABLE `system_notice`;
TRUNCATE TABLE `system_notify_message`;

-- ============================================================================
-- 四、清空示例演示表（yudao_demoXX_*）
-- ============================================================================

TRUNCATE TABLE `yudao_demo01_contact`;
TRUNCATE TABLE `yudao_demo02_category`;
TRUNCATE TABLE `yudao_demo03_course`;
TRUNCATE TABLE `yudao_demo03_grade`;
TRUNCATE TABLE `yudao_demo03_student`;

-- ============================================================================
-- 五、可选：清空文件记录（如果你希望从“空附件库”开始）
--    如不想清空，可注释掉下面两行。
-- ============================================================================

-- TRUNCATE TABLE `infra_file`;
-- TRUNCATE TABLE `infra_file_content`;

-- ============================================================================
-- 六、可选：精简账号 / 部门（默认只提示，不直接删除）
--
-- 下面示例仅供参考，你可以按需要取消注释并调整条件：
--  1）只保留 admin 超级管理员，删除其它用户：
--      DELETE FROM `system_users` WHERE `username` <> 'admin';
--  2）清空公告、演示部门等，可按 id 条件删除：
--      DELETE FROM `system_dept` WHERE `id` NOT IN (100); -- 仅保留公司主节点
--
-- 建议：先用 Navicat / DBeaver 看好想删哪些数据，再执行对应 DELETE 语句。
-- ============================================================================

SET FOREIGN_KEY_CHECKS = 1;

