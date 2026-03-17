-- ========== 增量变更脚本（在现有表基础上执行）2026-03-02 ==========
-- 执行前请确认表已存在，若字段已存在则跳过对应 ALTER

-- 1. 员工薪资档案：五险、公积金、个税、其他
ALTER TABLE `hr_employee_salary` ADD COLUMN `social_insurance` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '五险（元）' AFTER `allowance`;
ALTER TABLE `hr_employee_salary` ADD COLUMN `housing_fund` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '公积金（元）' AFTER `social_insurance`;
ALTER TABLE `hr_employee_salary` ADD COLUMN `tax` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '个税（元）' AFTER `housing_fund`;
ALTER TABLE `hr_employee_salary` ADD COLUMN `other` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '其他（元，杂项扣款）' AFTER `tax`;
ALTER TABLE `hr_employee_salary` ADD COLUMN `other_remark` varchar(200) NOT NULL DEFAULT '' COMMENT '其他扣款备注说明' AFTER `other`;

-- 2. 花名册：银行卡、学历、现居住地、户口类型（转正日期 formal_date 若已有则跳过）
-- ALTER TABLE `hr_employee` ADD COLUMN `formal_date` date NULL COMMENT '转正日期' AFTER `join_date`;
ALTER TABLE `hr_employee` ADD COLUMN `bank_card_no` varchar(30) NOT NULL DEFAULT '' COMMENT '银行卡号' AFTER `leave_date`;
ALTER TABLE `hr_employee` ADD COLUMN `bank_name` varchar(100) NOT NULL DEFAULT '' COMMENT '开户行' AFTER `bank_card_no`;
ALTER TABLE `hr_employee` ADD COLUMN `education` varchar(20) NOT NULL DEFAULT '' COMMENT '学历（如：本科、硕士）' AFTER `bank_name`;
ALTER TABLE `hr_employee` ADD COLUMN `current_address` varchar(200) NOT NULL DEFAULT '' COMMENT '现居住地' AFTER `education`;
ALTER TABLE `hr_employee` ADD COLUMN `household_type` varchar(20) NOT NULL DEFAULT '' COMMENT '户口类型（如：城镇、农村）' AFTER `current_address`;

-- 3. 月度薪资明细：五险、公积金、个税、其他
ALTER TABLE `hr_salary_monthly` ADD COLUMN `social_insurance` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '五险快照' AFTER `allowance`;
ALTER TABLE `hr_salary_monthly` ADD COLUMN `housing_fund` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '公积金快照' AFTER `social_insurance`;
ALTER TABLE `hr_salary_monthly` ADD COLUMN `tax` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '个税快照' AFTER `housing_fund`;
ALTER TABLE `hr_salary_monthly` ADD COLUMN `other` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '其他（杂项）快照' AFTER `tax`;
ALTER TABLE `hr_salary_monthly` ADD COLUMN `other_remark` varchar(200) NOT NULL DEFAULT '' COMMENT '其他扣款备注说明快照' AFTER `other`;

-- 4. 合同：服务类型
ALTER TABLE `infra_contract` ADD COLUMN `service_type` tinyint NOT NULL DEFAULT 1 COMMENT '服务类型（1=长期 2=短期）' AFTER `type`;
