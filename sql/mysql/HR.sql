-- 考勤模块
CREATE TABLE `hr_attendance_rule` (
  `id`                          bigint       NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `name`                        varchar(50)  NOT NULL COMMENT '规则名称（如：早班、晚班）',
  `shift_type`                  tinyint      NOT NULL DEFAULT 2 COMMENT '班次类型（1=早班 2=晚班）',
  `work_days_per_week`          tinyint      NOT NULL DEFAULT 5 COMMENT '每周工作天数（5=周一至周五，6=含周六）',
  `work_start_time`             time         NOT NULL DEFAULT '09:00:00' COMMENT '上班时间',
  `work_end_time`               time         NOT NULL DEFAULT '18:00:00' COMMENT '下班时间',
  `late_threshold_minutes`      int          NOT NULL DEFAULT 15 COMMENT '迟到阈值（分钟，超过此值记迟到）',
  `early_leave_threshold_minutes` int        NOT NULL DEFAULT 15 COMMENT '早退阈值（分钟）',
  `is_follow_holiday`           bit(1)       NOT NULL DEFAULT b'1' COMMENT '是否遵循法定节假日（1=遵循，节假日自动休息）',
  `status`                      tinyint      NOT NULL DEFAULT 0 COMMENT '状态（0=启用 1=停用）',
  `remark`                      varchar(500) NOT NULL DEFAULT '' COMMENT '备注',
  `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='HR 排班规则表';

-- 插入默认早班规则
INSERT INTO `hr_attendance_rule` (`name`, `shift_type`, `work_days_per_week`, `work_start_time`, `work_end_time`,
  `late_threshold_minutes`, `early_leave_threshold_minutes`, `is_follow_holiday`, `status`, `tenant_id`)
VALUES ('早班', 1, 6, '08:00:00', '17:00:00', 15, 15, b'1', 0, 1);

-- 插入默认晚班规则（系统默认班次）
INSERT INTO `hr_attendance_rule` (`name`, `shift_type`, `work_days_per_week`, `work_start_time`, `work_end_time`,
  `late_threshold_minutes`, `early_leave_threshold_minutes`, `is_follow_holiday`, `status`, `tenant_id`)
VALUES ('晚班', 2, 6, '13:00:00', '22:00:00', 15, 15, b'1', 0, 1);


-- 法定节假日模块
CREATE TABLE `hr_holiday` (
  `id`           bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`         varchar(50)  NOT NULL COMMENT '节假日名称（如：春节、元旦）',
  `holiday_date` date         NOT NULL COMMENT '具体日期',
  `type`         tinyint      NOT NULL DEFAULT 1 COMMENT '类型（1=休息日 2=调班工作日）',
  `year`         smallint     NOT NULL COMMENT '所属年份（用于按年批量查询）',
  `remark`       varchar(200) NOT NULL DEFAULT '' COMMENT '备注',
  `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date` (`holiday_date`, `tenant_id`),
  KEY `idx_year` (`year`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='HR 法定节假日表';


-- 员工花名册模块
CREATE TABLE `hr_employee` (
  `id`                  bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`             bigint       NOT NULL DEFAULT 0 COMMENT '关联系统用户ID（用户被删除后置0，记录保留）',
  `nickname`            varchar(30)  NOT NULL DEFAULT '' COMMENT '姓名（快照，用户被删后不丢失）',
  `dept_id`             bigint       NOT NULL DEFAULT 0 COMMENT '部门ID（快照）',
  `dept_name`           varchar(50)  NOT NULL DEFAULT '' COMMENT '部门名称（快照）',
  `post_id`             bigint       NOT NULL DEFAULT 0 COMMENT '岗位ID',
  `post_name`           varchar(50)  NOT NULL DEFAULT '' COMMENT '岗位名称（快照）',
  `mobile`              varchar(11)  NOT NULL DEFAULT '' COMMENT '手机号',
  `email`               varchar(50)  NOT NULL DEFAULT '' COMMENT '邮箱',
  `sex`                 tinyint      NOT NULL DEFAULT 0 COMMENT '性别（0=未知 1=男 2=女）',
  `avatar`              varchar(500) NOT NULL DEFAULT '' COMMENT '头像URL',
  `employment_type`     tinyint      NOT NULL DEFAULT 1 COMMENT '雇佣类型（1=全职 2=实习 3=兼职）',
  `employment_status`   tinyint      NOT NULL DEFAULT 1 COMMENT '在职状态（1=在职 2=离职 3=待入职）',
  `default_rule_id`     bigint       NOT NULL DEFAULT 0 COMMENT '默认排班规则ID（关联 hr_attendance_rule.id，0=使用系统默认晚班）',
  `join_date`           date         NULL COMMENT '入职日期',
  `formal_date`         date         NULL COMMENT '转正日期',
  `leave_date`          date         NULL COMMENT '离职日期',
  `bank_card_no`       varchar(30)   NOT NULL DEFAULT '' COMMENT '银行卡号',
  `bank_name`           varchar(100) NOT NULL DEFAULT '' COMMENT '开户行',
  `education`          varchar(20)   NOT NULL DEFAULT '' COMMENT '学历（如：本科、硕士）',
  `current_address`     varchar(200) NOT NULL DEFAULT '' COMMENT '现居住地',
  `household_type`      varchar(20)   NOT NULL DEFAULT '' COMMENT '户口类型（如：城镇、农村）',
  `id_card`             varchar(18)  NOT NULL DEFAULT '' COMMENT '身份证号（敏感字段，权限控制）',
  `emergency_contact`   varchar(20)  NOT NULL DEFAULT '' COMMENT '紧急联系人姓名',
  `emergency_phone`     varchar(11)  NOT NULL DEFAULT '' COMMENT '紧急联系电话',
  `remark`              varchar(500) NOT NULL DEFAULT '' COMMENT '备注',
  `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`, `tenant_id`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_employment_status` (`employment_status`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='HR 员工花名册表';


-- 月度排班模块
CREATE TABLE `hr_attendance_schedule` (
  `id`              bigint   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`         bigint   NOT NULL COMMENT '员工用户ID（关联 system_users.id）',
  `rule_id`         bigint   NOT NULL COMMENT '当日应用的排班规则ID（决定上下班时间和班次类型）',
  `shift_type`      tinyint  NOT NULL DEFAULT 2 COMMENT '班次类型快照（1=早班 2=晚班，冗余自 rule，便于直接展示）',
  `schedule_date`   date     NOT NULL COMMENT '排班日期',
  `year_month`      int      NOT NULL COMMENT '年月（如 202602，用于按月查询）',
  `day_type`        tinyint  NOT NULL DEFAULT 1 COMMENT '日类型（1=工作日 2=周末休息 3=节假日休息 4=调班工作日）',
  `is_need_clock`   bit(1)   NOT NULL DEFAULT b'1' COMMENT '是否需要打卡（0=不需要 1=需要）',
  `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`, `schedule_date`, `tenant_id`),
  KEY `idx_year_month` (`year_month`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='HR 月度排班表';


-- 打卡记录模块
CREATE TABLE `hr_attendance_record` (
  `id`               bigint    NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`          bigint    NOT NULL COMMENT '员工用户ID',
  `schedule_id`      bigint    NOT NULL DEFAULT 0 COMMENT '关联排班ID（0=无对应排班的异常打卡）',
  `attendance_date`  date      NOT NULL COMMENT '考勤日期（冗余，便于按日查询）',
  `clock_in_time`    datetime  NULL COMMENT '签到时间',
  `clock_out_time`   datetime  NULL COMMENT '签退时间',
  `clock_in_ip`      varchar(50)  NOT NULL DEFAULT '' COMMENT '签到IP',
  `clock_out_ip`     varchar(50)  NOT NULL DEFAULT '' COMMENT '签退IP',
  `status`           tinyint   NOT NULL DEFAULT 0 COMMENT '考勤状态（0=正常 1=迟到 2=早退 3=缺勤 4=请假 5=加班）',
  `remark`           varchar(200) NOT NULL DEFAULT '' COMMENT '备注（如请假说明）',
  `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`, `attendance_date`),
  KEY `idx_schedule_id` (`schedule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='HR 打卡记录表';


-- 薪资项目配置模块
CREATE TABLE `hr_salary_config` (
  `id`                      bigint         NOT NULL AUTO_INCREMENT COMMENT '主键',
  `enable_base_salary`      bit(1)         NOT NULL DEFAULT b'1' COMMENT '是否启用基本工资',
  `enable_position_salary`  bit(1)         NOT NULL DEFAULT b'1' COMMENT '是否启用岗位工资',
  `enable_performance`      bit(1)         NOT NULL DEFAULT b'1' COMMENT '是否启用绩效',
  `enable_commission`       bit(1)         NOT NULL DEFAULT b'1' COMMENT '是否启用提成',
  `enable_allowance`        bit(1)         NOT NULL DEFAULT b'1' COMMENT '是否启用补贴',
  `enable_full_attendance`  bit(1)         NOT NULL DEFAULT b'1' COMMENT '是否启用全勤奖',
  `full_attendance_amount`  decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '全勤奖金额（元）',
  `commission_source`       tinyint        NOT NULL DEFAULT 1 COMMENT '提成来源（1=合同管理 2=手动录入）',
  `performance_source`      tinyint        NOT NULL DEFAULT 2 COMMENT '绩效来源（1=绩效审核表 2=手动录入）',
  `remark`                  varchar(500)   NOT NULL DEFAULT '' COMMENT '备注',
  `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='HR 薪资项目配置表';

-- 初始化一条默认配置
INSERT INTO `hr_salary_config` (`enable_base_salary`, `enable_position_salary`, `enable_performance`,
  `enable_commission`, `enable_allowance`, `enable_full_attendance`, `full_attendance_amount`,
  `commission_source`, `performance_source`, `tenant_id`)
VALUES (b'1', b'1', b'1', b'1', b'1', b'1', 200.00, 1, 2, 1);


-- 员工薪资档案模块
CREATE TABLE `hr_employee_salary` (
  `id`               bigint         NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`          bigint         NOT NULL COMMENT '员工用户ID（唯一）',
  `base_salary`      decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '基本工资（元）',
  `position_salary`  decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '岗位工资（元）',
  `allowance`        decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '补贴（元）',
  `social_insurance` decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '五险（元）',
  `housing_fund`      decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '公积金（元）',
  `tax`              decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '个税（元）',
  `other`            decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '其他（元，杂项扣款）',
  `other_remark`     varchar(200)   NOT NULL DEFAULT '' COMMENT '其他扣款备注说明',
  `effective_date`   date           NOT NULL COMMENT '生效日期（用于薪资调整追溯）',
  `remark`           varchar(500)   NOT NULL DEFAULT '' COMMENT '备注（如调薪原因）',
  `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='HR 员工薪资档案表';


-- 月度薪资明细模块
CREATE TABLE `hr_salary_monthly` (
  `id`                    bigint         NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`               bigint         NOT NULL COMMENT '员工用户ID',
  `year_month`            int            NOT NULL COMMENT '年月（如 202602）',
  `base_salary`           decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '基本工资快照',
  `position_salary`       decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '岗位工资快照',
  `performance`           decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '绩效（手动录入或绩效表取数）',
  `commission`            decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '提成（从合同管理取数或手动录入）',
  `allowance`             decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '补贴快照',
  `social_insurance`      decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '五险快照',
  `housing_fund`          decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '公积金快照',
  `tax`                   decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '个税快照',
  `other`                 decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '其他（杂项）快照',
  `other_remark`          varchar(200)   NOT NULL DEFAULT '' COMMENT '其他扣款备注说明快照',
  `full_attendance_bonus` decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '全勤奖（满勤则取配置金额，否则为0）',
  `total_salary`          decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '应发工资合计（以上各项之和）',
  `should_attend_days`    int            NOT NULL DEFAULT 0 COMMENT '应出勤天数（排班中工作日数量）',
  `actual_attend_days`    int            NOT NULL DEFAULT 0 COMMENT '实际出勤天数',
  `late_count`            int            NOT NULL DEFAULT 0 COMMENT '迟到次数',
  `absent_count`          int            NOT NULL DEFAULT 0 COMMENT '缺勤天数',
  `status`                tinyint        NOT NULL DEFAULT 0 COMMENT '状态（0=待确认 1=已确认 2=已发放）',
  `remark`                varchar(500)   NOT NULL DEFAULT '' COMMENT '备注',
  `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_month` (`user_id`, `year_month`, `tenant_id`),
  KEY `idx_year_month` (`year_month`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='HR 月度薪资明细表';


-- 在线文档分类表
CREATE TABLE `infra_doc_category` (
  `id`        bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`      varchar(50)  NOT NULL COMMENT '分类名称',
  `parent_id` bigint       NOT NULL DEFAULT 0 COMMENT '父分类ID（0=根分类）',
  `sort`      int          NOT NULL DEFAULT 0 COMMENT '排序',
  `status`    tinyint      NOT NULL DEFAULT 0 COMMENT '状态（0=启用 1=停用）',
  `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='在线文档分类表';


-- 在线文档表
CREATE TABLE `infra_doc` (
  `id`           bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_id`  bigint       NOT NULL DEFAULT 0 COMMENT '所属分类ID',
  `name`         varchar(100) NOT NULL COMMENT '文档名称',
  `type`         tinyint      NOT NULL DEFAULT 1 COMMENT '文档类型（1=上传文件预览 2=外部链接 3=下载附件）',
  `file_url`     varchar(500) NOT NULL DEFAULT '' COMMENT '文件URL（type=1/3 时有效，通过 infra/file/upload 上传）',
  `file_type`    varchar(20)  NOT NULL DEFAULT '' COMMENT '文件扩展名（docx/xlsx/pdf 等，用于前端选择预览方案）',
  `external_url` varchar(500) NOT NULL DEFAULT '' COMMENT '外部链接（type=2 时有效）',
  `file_size`    bigint       NOT NULL DEFAULT 0 COMMENT '文件大小（字节）',
  `sort`         int          NOT NULL DEFAULT 0 COMMENT '排序',
  `status`       tinyint      NOT NULL DEFAULT 0 COMMENT '状态（0=公开 1=仅内部）',
  `view_count`   int          NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `remark`       varchar(500) NOT NULL DEFAULT '' COMMENT '简介说明',
  `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='在线文档表';


-- 合同管理模块
CREATE TABLE `infra_contract` (
  `id`                   bigint         NOT NULL AUTO_INCREMENT COMMENT '主键',
  `contract_no`          varchar(50)    NOT NULL DEFAULT '' COMMENT '合同编号（可自动生成或手动输入）',
  `contract_name`        varchar(100)   NOT NULL COMMENT '合同名称',
  `type`                 tinyint        NOT NULL DEFAULT 1 COMMENT '合同类型（1=销售合同 2=采购合同 3=劳动合同 4=其他）',
  `service_type`         tinyint        NOT NULL DEFAULT 1 COMMENT '服务类型（1=长期 2=短期）',
  `customer_name`        varchar(100)   NOT NULL DEFAULT '' COMMENT '客户/供应商/乙方名称',
  `amount`               decimal(15,2)  NOT NULL DEFAULT 0.00 COMMENT '合同金额（元）',
  `sign_date`            date           NULL COMMENT '签订日期',
  `start_date`           date           NULL COMMENT '合同开始日期',
  `end_date`             date           NULL COMMENT '合同结束日期',
  `responsible_user_id`  bigint         NOT NULL DEFAULT 0 COMMENT '负责人用户ID（关联 system_users.id）',
  `responsible_username` varchar(30)    NOT NULL DEFAULT '' COMMENT '负责人姓名（快照）',
  `file_url`             varchar(500)   NOT NULL DEFAULT '' COMMENT '合同附件URL（通过 infra/file/upload 上传）',
  `commission_rate`      decimal(5,4)   NOT NULL DEFAULT 0.0000 COMMENT '提成比例（如 0.0500 = 5%）',
  `commission_amount`    decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '提成金额（元，= amount * commission_rate，由后端自动计算）',
  `commission_year_month` int           NOT NULL DEFAULT 0 COMMENT '提成归属年月（0=按签订日期归属，非0=手动指定，保留兼容）',
  `commission_start_date` date          NULL COMMENT '提成归属开始日期（为空时取 start_date）',
  `commission_end_date`   date          NULL COMMENT '提成归属结束日期（为空时取 end_date）',
  `status`               tinyint        NOT NULL DEFAULT 0 COMMENT '合同状态（0=草稿 1=生效 2=已完成 3=已终止）',
  `remark`               varchar(500)   NOT NULL DEFAULT '' COMMENT '备注',
  `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_responsible_user` (`responsible_user_id`, `tenant_id`),
  KEY `idx_status` (`status`, `tenant_id`),
  KEY `idx_commission_month` (`commission_year_month`, `responsible_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合同管理表';

-- 若表已存在，执行以下语句添加提成归属时间区间字段：
-- ALTER TABLE `infra_contract` ADD COLUMN `commission_start_date` date NULL COMMENT '提成归属开始日期（为空时取 start_date）' AFTER `commission_year_month`;
-- ALTER TABLE `infra_contract` ADD COLUMN `commission_end_date` date NULL COMMENT '提成归属结束日期（为空时取 end_date）' AFTER `commission_start_date`;


-- ========== 法定节假日初始数据（2026-2031年，国务院公布+农历推算） ==========
-- type: 1=休息日 2=调班工作日（补班）
-- 说明：2026年为国务院正式公布；2027-2031年为预估（基于农历及惯例，待官方发布后需核对）

-- 2026年（国务院正式公布）
INSERT INTO `hr_holiday` (`name`, `holiday_date`, `type`, `year`, `remark`, `tenant_id`) VALUES
('元旦', '2026-01-01', 1, 2026, '元旦假期', 1),
('元旦', '2026-01-02', 1, 2026, '元旦假期', 1),
('元旦', '2026-01-03', 1, 2026, '元旦假期', 1),
('春节调班', '2026-02-14', 2, 2026, '补春节假期班', 1),
('春节', '2026-02-15', 1, 2026, '除夕', 1),
('春节', '2026-02-16', 1, 2026, '正月初一', 1),
('春节', '2026-02-17', 1, 2026, '正月初二', 1),
('春节', '2026-02-18', 1, 2026, '正月初三', 1),
('春节', '2026-02-19', 1, 2026, '正月初四', 1),
('春节', '2026-02-20', 1, 2026, '正月初五', 1),
('春节', '2026-02-21', 1, 2026, '正月初六', 1),
('春节', '2026-02-22', 1, 2026, '正月初七', 1),
('春节', '2026-02-23', 1, 2026, '正月初八', 1),
('春节调班', '2026-02-28', 2, 2026, '补春节假期班', 1),
('清明节', '2026-04-04', 1, 2026, '清明节', 1),
('清明节', '2026-04-05', 1, 2026, '清明节', 1),
('清明节', '2026-04-06', 1, 2026, '清明节', 1),
('劳动节', '2026-05-01', 1, 2026, '劳动节', 1),
('劳动节', '2026-05-02', 1, 2026, '劳动节', 1),
('劳动节', '2026-05-03', 1, 2026, '劳动节', 1),
('劳动节', '2026-05-04', 1, 2026, '劳动节', 1),
('劳动节', '2026-05-05', 1, 2026, '劳动节', 1),
('劳动节调班', '2026-05-09', 2, 2026, '补劳动节假期班', 1),
('端午节', '2026-06-19', 1, 2026, '端午节', 1),
('端午节', '2026-06-20', 1, 2026, '端午节', 1),
('端午节', '2026-06-21', 1, 2026, '端午节', 1),
('国庆调班', '2026-09-20', 2, 2026, '补国庆假期班', 1),
('中秋节', '2026-09-25', 1, 2026, '中秋节', 1),
('中秋节', '2026-09-26', 1, 2026, '中秋节', 1),
('中秋节', '2026-09-27', 1, 2026, '中秋节', 1),
('国庆节', '2026-10-01', 1, 2026, '国庆节', 1),
('国庆节', '2026-10-02', 1, 2026, '国庆节', 1),
('国庆节', '2026-10-03', 1, 2026, '国庆节', 1),
('国庆节', '2026-10-04', 1, 2026, '国庆节', 1),
('国庆节', '2026-10-05', 1, 2026, '国庆节', 1),
('国庆节', '2026-10-06', 1, 2026, '国庆节', 1),
('国庆节', '2026-10-07', 1, 2026, '国庆节', 1),
('国庆调班', '2026-10-10', 2, 2026, '补国庆假期班', 1);

-- 2027年（预估）
INSERT INTO `hr_holiday` (`name`, `holiday_date`, `type`, `year`, `remark`, `tenant_id`) VALUES
('元旦', '2027-01-01', 1, 2027, '元旦假期', 1),
('春节', '2027-02-05', 1, 2027, '除夕', 1),
('春节', '2027-02-06', 1, 2027, '正月初一', 1),
('春节', '2027-02-07', 1, 2027, '正月初二', 1),
('春节', '2027-02-08', 1, 2027, '正月初三', 1),
('春节', '2027-02-09', 1, 2027, '正月初四', 1),
('春节', '2027-02-10', 1, 2027, '正月初五', 1),
('春节', '2027-02-11', 1, 2027, '正月初六', 1),
('清明节', '2027-04-05', 1, 2027, '清明节', 1),
('劳动节', '2027-05-01', 1, 2027, '劳动节', 1),
('劳动节', '2027-05-02', 1, 2027, '劳动节', 1),
('端午节', '2027-06-07', 1, 2027, '端午节', 1),
('端午节', '2027-06-08', 1, 2027, '端午节', 1),
('端午节', '2027-06-09', 1, 2027, '端午节', 1),
('中秋节', '2027-09-15', 1, 2027, '中秋节', 1),
('中秋节', '2027-09-16', 1, 2027, '中秋节', 1),
('中秋节', '2027-09-17', 1, 2027, '中秋节', 1),
('国庆节', '2027-10-01', 1, 2027, '国庆节', 1),
('国庆节', '2027-10-02', 1, 2027, '国庆节', 1),
('国庆节', '2027-10-03', 1, 2027, '国庆节', 1),
('国庆节', '2027-10-04', 1, 2027, '国庆节', 1),
('国庆节', '2027-10-05', 1, 2027, '国庆节', 1),
('国庆节', '2027-10-06', 1, 2027, '国庆节', 1),
('国庆节', '2027-10-07', 1, 2027, '国庆节', 1);

-- 2028年（预估）
INSERT INTO `hr_holiday` (`name`, `holiday_date`, `type`, `year`, `remark`, `tenant_id`) VALUES
('元旦', '2028-01-01', 1, 2028, '元旦假期', 1),
('春节', '2028-01-25', 1, 2028, '除夕', 1),
('春节', '2028-01-26', 1, 2028, '正月初一', 1),
('春节', '2028-01-27', 1, 2028, '正月初二', 1),
('春节', '2028-01-28', 1, 2028, '正月初三', 1),
('春节', '2028-01-29', 1, 2028, '正月初四', 1),
('春节', '2028-01-30', 1, 2028, '正月初五', 1),
('春节', '2028-01-31', 1, 2028, '正月初六', 1),
('清明节', '2028-04-04', 1, 2028, '清明节', 1),
('劳动节', '2028-05-01', 1, 2028, '劳动节', 1),
('端午节', '2028-05-28', 1, 2028, '端午节', 1),
('国庆节', '2028-10-01', 1, 2028, '国庆节', 1),
('国庆节', '2028-10-02', 1, 2028, '国庆节', 1),
('中秋节', '2028-10-03', 1, 2028, '中秋节（与国庆连休）', 1),
('国庆节', '2028-10-04', 1, 2028, '国庆节', 1),
('国庆节', '2028-10-05', 1, 2028, '国庆节', 1),
('国庆节', '2028-10-06', 1, 2028, '国庆节', 1);

-- 2029年（预估）
INSERT INTO `hr_holiday` (`name`, `holiday_date`, `type`, `year`, `remark`, `tenant_id`) VALUES
('元旦', '2029-01-01', 1, 2029, '元旦假期', 1),
('春节', '2029-02-12', 1, 2029, '除夕', 1),
('春节', '2029-02-13', 1, 2029, '正月初一', 1),
('春节', '2029-02-14', 1, 2029, '正月初二', 1),
('春节', '2029-02-15', 1, 2029, '正月初三', 1),
('春节', '2029-02-16', 1, 2029, '正月初四', 1),
('春节', '2029-02-17', 1, 2029, '正月初五', 1),
('春节', '2029-02-18', 1, 2029, '正月初六', 1),
('清明节', '2029-04-04', 1, 2029, '清明节', 1),
('劳动节', '2029-05-01', 1, 2029, '劳动节', 1),
('端午节', '2029-06-15', 1, 2029, '端午节', 1),
('端午节', '2029-06-16', 1, 2029, '端午节', 1),
('端午节', '2029-06-17', 1, 2029, '端午节', 1),
('中秋节', '2029-09-21', 1, 2029, '中秋节', 1),
('中秋节', '2029-09-22', 1, 2029, '中秋节', 1),
('中秋节', '2029-09-23', 1, 2029, '中秋节', 1),
('国庆节', '2029-10-01', 1, 2029, '国庆节', 1),
('国庆节', '2029-10-02', 1, 2029, '国庆节', 1),
('国庆节', '2029-10-03', 1, 2029, '国庆节', 1),
('国庆节', '2029-10-04', 1, 2029, '国庆节', 1),
('国庆节', '2029-10-05', 1, 2029, '国庆节', 1),
('国庆节', '2029-10-06', 1, 2029, '国庆节', 1),
('国庆节', '2029-10-07', 1, 2029, '国庆节', 1);

-- 2030年（预估）
INSERT INTO `hr_holiday` (`name`, `holiday_date`, `type`, `year`, `remark`, `tenant_id`) VALUES
('元旦', '2030-01-01', 1, 2030, '元旦假期', 1),
('春节', '2030-02-02', 1, 2030, '除夕', 1),
('春节', '2030-02-03', 1, 2030, '正月初一', 1),
('春节', '2030-02-04', 1, 2030, '正月初二', 1),
('春节', '2030-02-05', 1, 2030, '正月初三', 1),
('春节', '2030-02-06', 1, 2030, '正月初四', 1),
('春节', '2030-02-07', 1, 2030, '正月初五', 1),
('春节', '2030-02-08', 1, 2030, '正月初六', 1),
('清明节', '2030-04-05', 1, 2030, '清明节', 1),
('劳动节', '2030-05-01', 1, 2030, '劳动节', 1),
('端午节', '2030-06-04', 1, 2030, '端午节', 1),
('端午节', '2030-06-05', 1, 2030, '端午节', 1),
('端午节', '2030-06-06', 1, 2030, '端午节', 1),
('中秋节', '2030-09-11', 1, 2030, '中秋节', 1),
('中秋节', '2030-09-12', 1, 2030, '中秋节', 1),
('中秋节', '2030-09-13', 1, 2030, '中秋节', 1),
('国庆节', '2030-10-01', 1, 2030, '国庆节', 1),
('国庆节', '2030-10-02', 1, 2030, '国庆节', 1),
('国庆节', '2030-10-03', 1, 2030, '国庆节', 1),
('国庆节', '2030-10-04', 1, 2030, '国庆节', 1),
('国庆节', '2030-10-05', 1, 2030, '国庆节', 1),
('国庆节', '2030-10-06', 1, 2030, '国庆节', 1),
('国庆节', '2030-10-07', 1, 2030, '国庆节', 1);

-- 2031年（预估）
INSERT INTO `hr_holiday` (`name`, `holiday_date`, `type`, `year`, `remark`, `tenant_id`) VALUES
('元旦', '2031-01-01', 1, 2031, '元旦假期', 1),
('春节', '2031-01-22', 1, 2031, '除夕', 1),
('春节', '2031-01-23', 1, 2031, '正月初一', 1),
('春节', '2031-01-24', 1, 2031, '正月初二', 1),
('春节', '2031-01-25', 1, 2031, '正月初三', 1),
('春节', '2031-01-26', 1, 2031, '正月初四', 1),
('春节', '2031-01-27', 1, 2031, '正月初五', 1),
('春节', '2031-01-28', 1, 2031, '正月初六', 1),
('清明节', '2031-04-05', 1, 2031, '清明节', 1),
('劳动节', '2031-05-01', 1, 2031, '劳动节', 1),
('端午节', '2031-05-25', 1, 2031, '端午节', 1),
('端午节', '2031-05-26', 1, 2031, '端午节', 1),
('端午节', '2031-05-27', 1, 2031, '端午节', 1),
('中秋节', '2031-08-31', 1, 2031, '中秋节', 1),
('中秋节', '2031-09-01', 1, 2031, '中秋节', 1),
('中秋节', '2031-09-02', 1, 2031, '中秋节', 1),
('国庆节', '2031-10-01', 1, 2031, '国庆节', 1),
('国庆节', '2031-10-02', 1, 2031, '国庆节', 1),
('国庆节', '2031-10-03', 1, 2031, '国庆节', 1),
('国庆节', '2031-10-04', 1, 2031, '国庆节', 1),
('国庆节', '2031-10-05', 1, 2031, '国庆节', 1),
('国庆节', '2031-10-06', 1, 2031, '国庆节', 1),
('国庆节', '2031-10-07', 1, 2031, '国庆节', 1);