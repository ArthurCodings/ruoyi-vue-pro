-- ============================================================
-- HR 薪资结算与流程联动 - 完整说明
-- ============================================================
-- 【考勤与薪资规则】
-- 1. 日薪公式：基本工资 / (当月总天数 - 周日天数 - 法定节假日休息天数)
--    法定节假日与周日重叠的不重复计算
-- 2. 迟到：每月允许1次迟到，第2次起每次扣30元，且无全勤奖
-- 3. 病假：工资为正常出勤的0.8倍，即扣20%日薪×天数
-- 4. 事假/缺勤：直接扣除当天工资（日薪×天数）
-- 5. 绩效：绩效薪酬 = 基本薪资 × 0.45 × 绩效系数（系数由打分表映射）
-- 6. 提成：按合同归属期时间比例分摊到当月
--
-- 【流程与考勤联动】
-- 请病假(hr_sick_leave)审批通过 → 考勤 status=6, leave_type=1
-- 请事假(hr_personal_leave)审批通过 → 考勤 status=7, leave_type=2
-- 补打卡(hr_clock_supplement)审批通过 → 补写/更新打卡记录，重新计算状态
-- 表单字段：leaveStartDate(开始)、leaveEndDate(结束)、leaveDays(天数)，审批后自动更新考勤
-- ============================================================

-- ============================================================
-- 【1】hr_attendance_record 增加请假类型和流程实例关联字段
--     status 枚举扩展：新增 6=病假（流程审批后）、7=事假（流程审批后）
-- ============================================================
ALTER TABLE `hr_attendance_record`
    ADD COLUMN `leave_type` tinyint NOT NULL DEFAULT 0
        COMMENT '请假类型（0=无 1=病假 2=事假，配合流程审批结果写入）' AFTER `status`,
    ADD COLUMN `process_instance_id` varchar(64) NOT NULL DEFAULT ''
        COMMENT '来源流程实例ID（考勤被流程审批调整时记录，用于溯源）' AFTER `leave_type`;

-- ============================================================
-- 【2】hr_employee_salary 增加个人全勤奖金额字段
--     0 = 跟随全局配置（hr_salary_config.full_attendance_amount）
--     非0 = 该员工独立全勤奖金额
-- ============================================================
ALTER TABLE `hr_employee_salary`
    ADD COLUMN `full_attendance_amount` decimal(10,2) NOT NULL DEFAULT 0.00
        COMMENT '个人全勤奖金额（元，0=使用全局配置）' AFTER `allowance`;

-- ============================================================
-- 【3】hr_salary_monthly 增加考勤扣款明细、日薪快照、计算明细JSON
--     新的 total_salary 公式：
--     = base_salary + position_salary + performance + commission
--       + allowance + full_attendance_bonus - attendance_deduction
-- ============================================================
ALTER TABLE `hr_salary_monthly`
    ADD COLUMN `daily_salary` decimal(10,2) NOT NULL DEFAULT 0.00
        COMMENT '日薪快照（基本工资 ÷ 当月实际工作日数，四舍五入保留2位）' AFTER `full_attendance_bonus`,
    ADD COLUMN `late_deduction` decimal(10,2) NOT NULL DEFAULT 0.00
        COMMENT '迟到扣款（第2次起每次扣30元，公式：max(0, late_count-1)*30）' AFTER `daily_salary`,
    ADD COLUMN `sick_leave_days` int NOT NULL DEFAULT 0
        COMMENT '病假天数（流程审批通过后统计）' AFTER `late_deduction`,
    ADD COLUMN `sick_leave_deduction` decimal(10,2) NOT NULL DEFAULT 0.00
        COMMENT '病假扣款（扣20%：daily_salary * sick_leave_days * 0.2）' AFTER `sick_leave_days`,
    ADD COLUMN `casual_leave_days` int NOT NULL DEFAULT 0
        COMMENT '事假天数（流程审批通过后统计）' AFTER `sick_leave_deduction`,
    ADD COLUMN `casual_leave_deduction` decimal(10,2) NOT NULL DEFAULT 0.00
        COMMENT '事假扣款（扣全天：daily_salary * casual_leave_days）' AFTER `casual_leave_days`,
    ADD COLUMN `absent_deduction` decimal(10,2) NOT NULL DEFAULT 0.00
        COMMENT '缺勤扣款（扣全天：daily_salary * absent_count）' AFTER `casual_leave_deduction`,
    ADD COLUMN `attendance_deduction` decimal(10,2) NOT NULL DEFAULT 0.00
        COMMENT '考勤扣款合计 = late_deduction+sick_leave_deduction+casual_leave_deduction+absent_deduction' AFTER `absent_deduction`,
    ADD COLUMN `salary_detail` json NULL
        COMMENT '薪资计算明细快照（JSON，用于前端"查看明细"功能）' AFTER `attendance_deduction`,
    ADD COLUMN `confirm_notice_id` bigint NOT NULL DEFAULT 0
        COMMENT '关联薪资确认通知单ID（0=尚未发送确认通知）' AFTER `salary_detail`;


-- ============================================================
-- 【4】绩效评分模板主表
-- ============================================================
CREATE TABLE `hr_performance_template` (
    `id`                     bigint        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`                   varchar(100)  NOT NULL COMMENT '模板名称（如：财务岗绩效模板、管理岗绩效模板）',
    `description`            varchar(500)  NOT NULL DEFAULT '' COMMENT '模板描述',
    `performance_base_ratio` decimal(5,4)  NOT NULL DEFAULT 0.4500
        COMMENT '绩效薪酬基准系数（绩效薪酬 = 基本薪资 × 此系数 × 绩效系数，默认0.45）',
    `coefficient_rules`      json          NULL
        COMMENT '分数-绩效系数映射规则（JSON数组，每条含minScore/maxScore/coefficient）',
    `status`                 tinyint       NOT NULL DEFAULT 0 COMMENT '状态（0=启用 1=停用）',
    `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
    `create_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
    `update_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='HR 绩效评分模板表';

-- 插入默认系数规则（与图片一致），可以在创建模板时作为默认值
-- coefficient_rules JSON 格式示例：
-- [{"minScore":95,"maxScore":100,"coefficient":1.2},{"minScore":90,"maxScore":94,"coefficient":1.0},
--  {"minScore":81,"maxScore":89,"coefficient":0.9},{"minScore":71,"maxScore":80,"coefficient":0.7},
--  {"minScore":61,"maxScore":70,"coefficient":0.5},{"minScore":21,"maxScore":60,"coefficient":0.2},
--  {"minScore":0,"maxScore":20,"coefficient":0.0}]


-- ============================================================
-- 【5】绩效模板评分区块表（基础指标/加分项/扣分项）
-- ============================================================
CREATE TABLE `hr_performance_template_section` (
    `id`           bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `template_id`  bigint       NOT NULL COMMENT '关联模板ID',
    `section_name` varchar(100) NOT NULL COMMENT '区块名称（如：一、基础指标（100分））',
    `section_type` tinyint      NOT NULL
        COMMENT '区块类型（1=基础指标 2=加分项 3=扣分项）',
    `max_score`    decimal(6,1) NOT NULL DEFAULT 0.0 COMMENT '区块最高分',
    `sort`         int          NOT NULL DEFAULT 0 COMMENT '显示排序',
    `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
    `create_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
    `update_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_template_id` (`template_id`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='HR 绩效模板评分区块表';


-- ============================================================
-- 【6】绩效模板评分条目表
-- ============================================================
CREATE TABLE `hr_performance_template_item` (
    `id`                   bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `template_id`          bigint       NOT NULL COMMENT '关联模板ID',
    `section_id`           bigint       NOT NULL COMMENT '关联区块ID',
    `item_name`            varchar(200) NOT NULL COMMENT '评价指标名称（如：账务处理合规与时效）',
    `max_score`            decimal(6,1) NOT NULL DEFAULT 0.0 COMMENT '该条目满分',
    `scoring_criteria`     text         NULL COMMENT '量化评价标准描述',
    `evidence_desc`        varchar(300) NOT NULL DEFAULT '' COMMENT '佐证材料说明',
    `notes`                varchar(500) NOT NULL DEFAULT '' COMMENT '备注（如：重大差错本项得0分）',
    `is_fixed_score`       bit(1)       NOT NULL DEFAULT b'0'
        COMMENT '是否为固定分（1=由系统自动计算，如全勤固定2分；0=需人工打分）',
    `fixed_score_condition` varchar(300) NOT NULL DEFAULT ''
        COMMENT '固定分值条件说明（is_fixed_score=1时有效，如：无考勤扣分则加2分）',
    `sort`         int          NOT NULL DEFAULT 0 COMMENT '显示排序',
    `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
    `create_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
    `update_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_section_id` (`section_id`),
    KEY `idx_template_id` (`template_id`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='HR 绩效模板评分条目表';


-- ============================================================
-- 【7】绩效模板应用人员表（一个模板可应用多个员工）
-- ============================================================
CREATE TABLE `hr_performance_template_user` (
    `id`          bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `template_id` bigint NOT NULL COMMENT '关联模板ID',
    `user_id`     bigint NOT NULL COMMENT '应用该模板的员工用户ID',
    `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
    `create_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
    `update_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_template_user` (`template_id`, `user_id`, `tenant_id`),
    KEY `idx_user_id` (`user_id`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='HR 绩效模板应用人员表';


-- ============================================================
-- 【8】绩效打分记录表（按年月，每人每月一条）
-- ============================================================
CREATE TABLE `hr_performance_score` (
    `id`                       bigint        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`                  bigint        NOT NULL COMMENT '被考核员工用户ID',
    `template_id`              bigint        NOT NULL COMMENT '使用的绩效模板ID',
    `year_month`               int           NOT NULL COMMENT '考核年月（如202603）',
    `reviewer_user_id`         bigint        NOT NULL DEFAULT 0 COMMENT '考核人用户ID',
    `reviewer_name`            varchar(30)   NOT NULL DEFAULT '' COMMENT '考核人姓名（快照）',
    `base_section_score`       decimal(6,2)  NOT NULL DEFAULT 0.00
        COMMENT '基础指标审核得分合计',
    `bonus_section_score`      decimal(6,2)  NOT NULL DEFAULT 0.00
        COMMENT '加分项审核得分合计（含全勤固定2分）',
    `deduction_section_score`  decimal(6,2)  NOT NULL DEFAULT 0.00
        COMMENT '扣分项审核得分合计',
    `attendance_deduction_score` decimal(6,2) NOT NULL DEFAULT 0.00
        COMMENT '考勤扣分（由人事部门根据打卡记录填写）',
    `final_score`              decimal(6,2)  NOT NULL DEFAULT 0.00
        COMMENT '最终得分 = 基础指标 + 加分项 - 考勤扣分 - 扣分项',
    `performance_coefficient`  decimal(4,2)  NOT NULL DEFAULT 0.00
        COMMENT '绩效系数（由final_score自动映射，如95-100→1.2）',
    `performance_salary`       decimal(10,2) NOT NULL DEFAULT 0.00
        COMMENT '绩效薪酬 = 基本薪资 × performance_base_ratio × 绩效系数',
    `status`                   tinyint       NOT NULL DEFAULT 0
        COMMENT '状态（0=草稿 1=已提交 2=审核通过 3=已驳回）',
    `review_comment`           varchar(500)  NOT NULL DEFAULT '' COMMENT '审核意见',
    `appeal_content`           varchar(500)  NOT NULL DEFAULT '' COMMENT '员工申诉内容',
    `appeal_result`            varchar(500)  NOT NULL DEFAULT '' COMMENT '申诉处理结果',
    `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
    `create_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
    `update_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_month` (`user_id`, `year_month`, `tenant_id`),
    KEY `idx_year_month` (`year_month`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='HR 绩效打分记录表';


-- ============================================================
-- 【9】绩效打分条目明细表
-- ============================================================
CREATE TABLE `hr_performance_score_item` (
    `id`               bigint        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `score_id`         bigint        NOT NULL COMMENT '关联绩效打分记录ID',
    `template_item_id` bigint        NOT NULL COMMENT '关联模板条目ID',
    `section_type`     tinyint       NOT NULL
        COMMENT '区块类型（1=基础指标 2=加分项 3=扣分项，冗余便于统计）',
    `self_score`       decimal(6,2)  NOT NULL DEFAULT 0.00 COMMENT '初得分（被考核人自报/初评）',
    `review_score`     decimal(6,2)  NOT NULL DEFAULT 0.00 COMMENT '审核分（考核人审定）',
    `evidence_url`     varchar(500)  NOT NULL DEFAULT '' COMMENT '佐证材料URL（多文件逗号分隔）',
    `remark`           varchar(200)  NOT NULL DEFAULT '' COMMENT '备注',
    `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
    `create_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
    `update_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_score_id` (`score_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='HR 绩效打分条目明细表';


-- ============================================================
-- 【10】薪资确认通知单表
-- ============================================================
CREATE TABLE `hr_salary_confirm_notice` (
    `id`                bigint        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `salary_monthly_id` bigint        NOT NULL COMMENT '关联月度薪资ID（hr_salary_monthly.id）',
    `user_id`           bigint        NOT NULL COMMENT '接收通知的员工用户ID',
    `year_month`        int           NOT NULL COMMENT '薪资所属年月（如202603）',
    `total_salary`      decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '应发薪资快照（发送时取值）',
    `notice_content`    json          NULL
        COMMENT '通知内容（JSON，含各项薪资明细快照，供员工查看）',
    `send_time`         datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    `confirm_time`      datetime      NULL COMMENT '员工确认时间（NULL=未确认）',
    `status`            tinyint       NOT NULL DEFAULT 0
        COMMENT '状态（0=待确认 1=已确认）',
    `remark`            varchar(200)  NOT NULL DEFAULT '' COMMENT '备注',
    `creator`    varchar(64)  DEFAULT '' COMMENT '创建者',
    `create_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`    varchar(64)  DEFAULT '' COMMENT '更新者',
    `update_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`    bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`  bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_salary_monthly_id` (`salary_monthly_id`),
    KEY `idx_user_month` (`user_id`, `year_month`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='HR 薪资确认通知单表';


-- BPM 流程初始化 SQL
-- > 重要说明：下面的 SQL 负责创建流程分类和表单（含固定字段名，代码将依赖这些 __vModel__ 字段名）。流程模型（审批节点图）需要你在系统后台的流程设计器中手动创建，绑定到对应表单，并使用我约定的流程标识（Key），代码会用这个 Key 来识别是哪种流程：
-- > - 请病假：hr_sick_leave
-- > - 请事假：hr_personal_leave
-- > - 补打卡：hr_clock_supplement
-- ============================================================
-- 【11】插入流程分类：人事管理
-- ============================================================
INSERT INTO `bpm_category` (`name`, `code`, `description`, `status`, `sort`, `tenant_id`)
VALUES ('人事管理', 'hr', '人力资源相关审批流程，含请假、补打卡等', 0, 10, 1);


-- ============================================================
-- 【12】插入三个流程表单
-- 注意：fields 中每个条目的 __vModel__ 值就是代码读取的字段 Key，不可改动
-- ============================================================

-- (a) 请病假申请表单（流程标识Key对应：hr_sick_leave）
INSERT INTO `bpm_form` (`name`, `status`, `conf`, `fields`, `remark`, `tenant_id`)
VALUES (
    '请病假申请',
    0,
    '{"labelWidth":100,"labelPosition":"right","size":"","formBtns":true}',
    JSON_ARRAY(
        '{"__config__":{"label":"请假开始日期","showLabel":true,"changeTag":true,"tag":"el-date-picker","tagIcon":"date","required":true,"layout":"colFormItem","span":12},"__slot__":{},"placeholder":"请选择请假开始日期","type":"date","format":"yyyy-MM-dd","value-format":"yyyy-MM-dd","__vModel__":"leaveStartDate"}',
        '{"__config__":{"label":"请假结束日期","showLabel":true,"changeTag":true,"tag":"el-date-picker","tagIcon":"date","required":true,"layout":"colFormItem","span":12},"__slot__":{},"placeholder":"请选择请假结束日期","type":"date","format":"yyyy-MM-dd","value-format":"yyyy-MM-dd","__vModel__":"leaveEndDate"}',
        '{"__config__":{"label":"请假天数","showLabel":true,"changeTag":true,"tag":"el-input-number","tagIcon":"number","required":true,"layout":"colFormItem","span":12},"__slot__":{},"placeholder":"请输入请假天数","min":0.5,"step":0.5,"precision":1,"__vModel__":"leaveDays"}',
        '{"__config__":{"label":"病假原因","showLabel":true,"changeTag":true,"tag":"el-input","tagIcon":"textarea","required":true,"layout":"colFormItem","span":24},"__slot__":{},"placeholder":"请填写病假原因，如有医院证明请在附件中上传","type":"textarea","rows":3,"__vModel__":"leaveReason"}',
        '{"__config__":{"label":"医院证明附件","showLabel":true,"changeTag":true,"tag":"el-upload","tagIcon":"upload","required":false,"layout":"colFormItem","span":24},"__slot__":{"list-type":"text"},"action":"","accept":".jpg,.jpeg,.png,.pdf","__vModel__":"attachmentUrls"}'
    ),
    '请病假流程表单。代码读取字段：leaveStartDate(开始日期)、leaveEndDate(结束日期)、leaveDays(天数)，审批通过后自动更新考勤status=6(病假)',
    1
);

-- (b) 请事假申请表单（流程标识Key对应：hr_personal_leave）
INSERT INTO `bpm_form` (`name`, `status`, `conf`, `fields`, `remark`, `tenant_id`)
VALUES (
    '请事假申请',
    0,
    '{"labelWidth":100,"labelPosition":"right","size":"","formBtns":true}',
    JSON_ARRAY(
        '{"__config__":{"label":"请假开始日期","showLabel":true,"changeTag":true,"tag":"el-date-picker","tagIcon":"date","required":true,"layout":"colFormItem","span":12},"__slot__":{},"placeholder":"请选择请假开始日期","type":"date","format":"yyyy-MM-dd","value-format":"yyyy-MM-dd","__vModel__":"leaveStartDate"}',
        '{"__config__":{"label":"请假结束日期","showLabel":true,"changeTag":true,"tag":"el-date-picker","tagIcon":"date","required":true,"layout":"colFormItem","span":12},"__slot__":{},"placeholder":"请选择请假结束日期","type":"date","format":"yyyy-MM-dd","value-format":"yyyy-MM-dd","__vModel__":"leaveEndDate"}',
        '{"__config__":{"label":"请假天数","showLabel":true,"changeTag":true,"tag":"el-input-number","tagIcon":"number","required":true,"layout":"colFormItem","span":12},"__slot__":{},"placeholder":"请输入请假天数","min":0.5,"step":0.5,"precision":1,"__vModel__":"leaveDays"}',
        '{"__config__":{"label":"事假原因","showLabel":true,"changeTag":true,"tag":"el-input","tagIcon":"textarea","required":true,"layout":"colFormItem","span":24},"__slot__":{},"placeholder":"请填写事假原因","type":"textarea","rows":3,"__vModel__":"leaveReason"}',
        '{"__config__":{"label":"相关附件","showLabel":true,"changeTag":true,"tag":"el-upload","tagIcon":"upload","required":false,"layout":"colFormItem","span":24},"__slot__":{"list-type":"text"},"action":"","accept":".jpg,.jpeg,.png,.pdf","__vModel__":"attachmentUrls"}'
    ),
    '请事假流程表单。代码读取字段：leaveStartDate(开始日期)、leaveEndDate(结束日期)、leaveDays(天数)，审批通过后自动更新考勤status=7(事假)',
    1
);

-- (c) 补打卡申请表单（流程标识Key对应：hr_clock_supplement）
INSERT INTO `bpm_form` (`name`, `status`, `conf`, `fields`, `remark`, `tenant_id`)
VALUES (
    '补打卡申请',
    0,
    '{"labelWidth":100,"labelPosition":"right","size":"","formBtns":true}',
    JSON_ARRAY(
        '{"__config__":{"label":"补打卡日期","showLabel":true,"changeTag":true,"tag":"el-date-picker","tagIcon":"date","required":true,"layout":"colFormItem","span":12},"__slot__":{},"placeholder":"请选择需要补打卡的日期","type":"date","format":"yyyy-MM-dd","value-format":"yyyy-MM-dd","__vModel__":"supplementDate"}',
        '{"__config__":{"label":"补卡类型","showLabel":true,"changeTag":true,"tag":"el-select","tagIcon":"select","required":true,"layout":"colFormItem","span":12},"__slot__":{"options":[{"label":"补签到","value":"1"},{"label":"补签退","value":"2"},{"label":"补签到+签退","value":"3"}]},"placeholder":"请选择补卡类型","__vModel__":"clockType"}',
        '{"__config__":{"label":"实际签到时间","showLabel":true,"changeTag":true,"tag":"el-time-picker","tagIcon":"time","required":false,"layout":"colFormItem","span":12},"__slot__":{},"placeholder":"请选择实际签到时间","value-format":"HH:mm:ss","__vModel__":"clockInTime"}',
        '{"__config__":{"label":"实际签退时间","showLabel":true,"changeTag":true,"tag":"el-time-picker","tagIcon":"time","required":false,"layout":"colFormItem","span":12},"__slot__":{},"placeholder":"请选择实际签退时间","value-format":"HH:mm:ss","__vModel__":"clockOutTime"}',
        '{"__config__":{"label":"补卡原因","showLabel":true,"changeTag":true,"tag":"el-input","tagIcon":"textarea","required":true,"layout":"colFormItem","span":24},"__slot__":{},"placeholder":"请填写忘记打卡的原因及情况说明","type":"textarea","rows":3,"__vModel__":"supplementReason"}',
        '{"__config__":{"label":"相关附件","showLabel":true,"changeTag":true,"tag":"el-upload","tagIcon":"upload","required":false,"layout":"colFormItem","span":24},"__slot__":{"list-type":"text"},"action":"","accept":".jpg,.jpeg,.png,.pdf","__vModel__":"attachmentUrls"}'
    ),
    '补打卡流程表单。代码读取字段：supplementDate(补卡日期)、clockType(1=签到 2=签退 3=签到+签退)、clockInTime、clockOutTime，审批通过后自动写入/更新打卡记录',
    1
);

