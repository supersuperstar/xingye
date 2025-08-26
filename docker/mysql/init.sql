-- 创建数据库
CREATE DATABASE IF NOT EXISTS bank_risk_review DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE bank_risk_review;

-- 创建客户表
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '客户姓名',
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号码',
    id_card VARCHAR(18) NOT NULL UNIQUE COMMENT '身份证号',
    email VARCHAR(100) COMMENT '电子邮箱',
    age INT COMMENT '年龄',
    occupation VARCHAR(100) COMMENT '职业',
    annual_income DECIMAL(15,2) COMMENT '年收入',
    investment_experience INT COMMENT '投资经验（年）',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '客户状态',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_by VARCHAR(50) COMMENT '更新人',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    deleted_at DATETIME COMMENT '删除时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_phone (phone),
    INDEX idx_id_card (id_card),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户信息表';

-- 创建风险评估表
CREATE TABLE IF NOT EXISTS risk_assessments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    risk_score INT NOT NULL COMMENT '风险评分',
    risk_level VARCHAR(20) NOT NULL COMMENT '风险等级',
    investment_amount DECIMAL(15,2) COMMENT '投资金额',
    questionnaire_answers TEXT COMMENT '问卷答案（JSON格式）',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '评估状态',
    remarks VARCHAR(500) COMMENT '评估备注',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_by VARCHAR(50) COMMENT '更新人',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    deleted_at DATETIME COMMENT '删除时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_risk_level (risk_level),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风险评估表';

-- 创建审核流程表
CREATE TABLE IF NOT EXISTS review_processes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    assessment_id BIGINT NOT NULL COMMENT '风险评估ID',
    process_instance_id VARCHAR(100) COMMENT '流程实例ID',
    current_level VARCHAR(20) COMMENT '当前审核级别',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '审核状态',
    priority VARCHAR(20) DEFAULT 'NORMAL' COMMENT '优先级',
    expected_completion_time DATETIME COMMENT '预计完成时间',
    actual_completion_time DATETIME COMMENT '实际完成时间',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_by VARCHAR(50) COMMENT '更新人',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    deleted_at DATETIME COMMENT '删除时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (assessment_id) REFERENCES risk_assessments(id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_assessment_id (assessment_id),
    INDEX idx_status (status),
    INDEX idx_current_level (current_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核流程表';

-- 创建投资建议表
CREATE TABLE IF NOT EXISTS investment_advices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    assessment_id BIGINT NOT NULL COMMENT '风险评估ID',
    portfolio_name VARCHAR(100) NOT NULL COMMENT '投资组合名称',
    portfolio_type VARCHAR(20) NOT NULL COMMENT '投资组合类型',
    expected_return DECIMAL(5,2) COMMENT '预期年化收益率',
    risk_level VARCHAR(20) NOT NULL COMMENT '风险等级',
    asset_allocation TEXT COMMENT '资产配置（JSON格式）',
    recommendation TEXT COMMENT '投资建议',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '建议状态',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_by VARCHAR(50) COMMENT '更新人',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    deleted_at DATETIME COMMENT '删除时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (assessment_id) REFERENCES risk_assessments(id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_assessment_id (assessment_id),
    INDEX idx_portfolio_type (portfolio_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投资建议表';

-- 创建审核记录表
CREATE TABLE IF NOT EXISTS review_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    process_id BIGINT NOT NULL COMMENT '审核流程ID',
    reviewer_id VARCHAR(50) NOT NULL COMMENT '审核员ID',
    reviewer_name VARCHAR(50) NOT NULL COMMENT '审核员姓名',
    review_level VARCHAR(20) NOT NULL COMMENT '审核级别',
    review_action VARCHAR(20) NOT NULL COMMENT '审核动作',
    review_opinion TEXT COMMENT '审核意见',
    review_result VARCHAR(20) NOT NULL COMMENT '审核结果',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (process_id) REFERENCES review_processes(id),
    INDEX idx_process_id (process_id),
    INDEX idx_reviewer_id (reviewer_id),
    INDEX idx_review_level (review_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核记录表';

-- 创建通知记录表
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    notification_type VARCHAR(20) NOT NULL COMMENT '通知类型',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    recipient VARCHAR(100) NOT NULL COMMENT '接收人',
    send_method VARCHAR(20) NOT NULL COMMENT '发送方式',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '发送状态',
    sent_at DATETIME COMMENT '发送时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_notification_type (notification_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知记录表';

-- 插入测试数据
INSERT INTO customers (name, phone, id_card, email, age, occupation, annual_income, investment_experience, status) VALUES
('张三', '13800138001', '110101199001011234', 'zhangsan@example.com', 35, '工程师', 500000.00, 3, 'ACTIVE'),
('李四', '13800138002', '110101199002022345', 'lisi@example.com', 28, '设计师', 300000.00, 1, 'ACTIVE'),
('王五', '13800138003', '110101199003033456', 'wangwu@example.com', 45, '经理', 800000.00, 5, 'ACTIVE'),
('赵六', '13800138004', '110101199004044567', 'zhaoliu@example.com', 52, '教师', 200000.00, 0, 'ACTIVE'),
('钱七', '13800138005', '110101199005055678', 'qianqi@example.com', 38, '销售', 400000.00, 2, 'ACTIVE');

-- 插入风险评估测试数据
INSERT INTO risk_assessments (customer_id, risk_score, risk_level, investment_amount, status) VALUES
(1, 65, 'MODERATE', 500000.00, 'ASSESSED'),
(2, 75, 'AGGRESSIVE', 300000.00, 'ASSESSED'),
(3, 45, 'CONSERVATIVE', 800000.00, 'ASSESSED'),
(4, 35, 'CONSERVATIVE', 200000.00, 'ASSESSED'),
(5, 55, 'MODERATE', 400000.00, 'ASSESSED');

-- 插入审核流程测试数据
INSERT INTO review_processes (customer_id, assessment_id, current_level, status, priority) VALUES
(1, 1, 'PRIMARY', 'PENDING', 'NORMAL'),
(2, 2, 'SENIOR', 'IN_PROGRESS', 'HIGH'),
(3, 3, 'PRIMARY', 'APPROVED', 'NORMAL'),
(4, 4, 'PRIMARY', 'APPROVED', 'LOW'),
(5, 5, 'INTERMEDIATE', 'PENDING', 'NORMAL');
