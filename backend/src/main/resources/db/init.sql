-- Bank Risk Assessment System Database Initialization Script
-- Author: Bank Risk Assessment Team
-- Version: 1.0.0

-- Create database
CREATE DATABASE IF NOT EXISTS bank_risk_review
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE bank_risk_review;

-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    id_card VARCHAR(18) NOT NULL UNIQUE,
    email VARCHAR(100),
    age INT,
    occupation VARCHAR(100),
    annual_income DECIMAL(15,2),
    investment_experience VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_phone (phone),
    INDEX idx_id_card (id_card),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
);

-- Create risk_assessments table
CREATE TABLE IF NOT EXISTS risk_assessments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    risk_score INT NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    investment_amount DECIMAL(15,2) NOT NULL,
    questionnaire_answers TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    remarks VARCHAR(500),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_risk_level (risk_level),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- Create review_processes table
CREATE TABLE IF NOT EXISTS review_processes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    assessment_id BIGINT NOT NULL,
    process_instance_id VARCHAR(100),
    current_level VARCHAR(20) NOT NULL DEFAULT 'PRIMARY',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    expected_completion_time DATETIME,
    actual_completion_time DATETIME,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (assessment_id) REFERENCES risk_assessments(id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_assessment_id (assessment_id),
    INDEX idx_current_level (current_level),
    INDEX idx_status (status),
    INDEX idx_priority (priority)
);

-- Create investment_advices table
CREATE TABLE IF NOT EXISTS investment_advices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    assessment_id BIGINT NOT NULL,
    advice_type VARCHAR(50) NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    product_code VARCHAR(50),
    allocation_percentage DECIMAL(5,2) NOT NULL,
    expected_return DECIMAL(5,2),
    risk_level VARCHAR(20) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (assessment_id) REFERENCES risk_assessments(id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_assessment_id (assessment_id),
    INDEX idx_advice_type (advice_type),
    INDEX idx_risk_level (risk_level)
);

-- Create review_records table
CREATE TABLE IF NOT EXISTS review_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_process_id BIGINT NOT NULL,
    reviewer_id VARCHAR(100) NOT NULL,
    reviewer_name VARCHAR(100) NOT NULL,
    review_level VARCHAR(20) NOT NULL,
    review_action VARCHAR(50) NOT NULL,
    review_opinion TEXT,
    review_result VARCHAR(20) NOT NULL,
    review_time DATETIME NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (review_process_id) REFERENCES review_processes(id),
    INDEX idx_review_process_id (review_process_id),
    INDEX idx_reviewer_id (reviewer_id),
    INDEX idx_review_level (review_level),
    INDEX idx_review_result (review_result)
);

-- Create notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    recipient VARCHAR(100) NOT NULL,
    recipient_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    sent_time DATETIME,
    read_time DATETIME,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_notification_type (notification_type),
    INDEX idx_status (status),
    INDEX idx_recipient (recipient)
);

-- Insert sample data for testing

-- Sample customers
INSERT INTO customers (name, phone, id_card, email, age, occupation, annual_income, investment_experience, status) VALUES
('张三', '13800138001', '110101199001011234', 'zhangsan@example.com', 35, '软件工程师', 150000.00, 'INTERMEDIATE', 'ACTIVE'),
('李四', '13800138002', '110101199002022345', 'lisi@example.com', 28, '产品经理', 120000.00, 'BEGINNER', 'ACTIVE'),
('王五', '13800138003', '110101199003033456', 'wangwu@example.com', 45, '企业高管', 500000.00, 'EXPERT', 'ACTIVE'),
('赵六', '13800138004', '110101199004044567', 'zhaoliu@example.com', 52, '退休人员', 80000.00, 'NONE', 'ACTIVE'),
('孙七', '13800138005', '110101199005055678', 'sunqi@example.com', 32, '金融分析师', 200000.00, 'ADVANCED', 'ACTIVE');

-- Sample risk assessments
INSERT INTO risk_assessments (customer_id, risk_score, risk_level, investment_amount, questionnaire_answers, status) VALUES
(1, 45, 'MODERATE', 100000.00, '{"ageRange":"26-35","incomeLevel":"100000-200000","investmentExperience":"1-3年","riskTolerance":"平衡","investmentGoal":"平衡增长","investmentPeriod":"3-5年"}', 'PENDING'),
(2, 25, 'CONSERVATIVE', 50000.00, '{"ageRange":"26-35","incomeLevel":"100000-200000","investmentExperience":"1年以下","riskTolerance":"保守","investmentGoal":"保值","investmentPeriod":"1-3年"}', 'PENDING'),
(3, 75, 'AGGRESSIVE', 500000.00, '{"ageRange":"36-45","incomeLevel":"500000以上","investmentExperience":"5年以上","riskTolerance":"激进","investmentGoal":"高收益","investmentPeriod":"5-10年"}', 'PENDING'),
(4, 15, 'CONSERVATIVE', 30000.00, '{"ageRange":"46-55","incomeLevel":"50000-100000","investmentExperience":"无经验","riskTolerance":"保守","investmentGoal":"保值","investmentPeriod":"1年以下"}', 'PENDING'),
(5, 60, 'MODERATE', 200000.00, '{"ageRange":"26-35","incomeLevel":"200000-500000","investmentExperience":"3-5年","riskTolerance":"积极","investmentGoal":"积极增长","investmentPeriod":"5-10年"}', 'PENDING');

-- Sample review processes
INSERT INTO review_processes (customer_id, assessment_id, current_level, status, priority) VALUES
(1, 1, 'PRIMARY', 'PENDING', 'NORMAL'),
(2, 2, 'PRIMARY', 'PENDING', 'NORMAL'),
(3, 3, 'PRIMARY', 'PENDING', 'HIGH'),
(4, 4, 'PRIMARY', 'PENDING', 'NORMAL'),
(5, 5, 'PRIMARY', 'PENDING', 'NORMAL');

-- Sample investment advices
INSERT INTO investment_advices (customer_id, assessment_id, advice_type, product_name, product_code, allocation_percentage, expected_return, risk_level, description) VALUES
(1, 1, '基金', '华夏成长混合基金', '000001', 40.00, 6.50, 'MODERATE', '适合稳健型投资者的混合基金'),
(1, 1, '基金', '易方达消费行业股票基金', '110022', 30.00, 8.00, 'AGGRESSIVE', '消费行业主题基金'),
(1, 1, '债券', '国债逆回购', 'GC001', 30.00, 3.50, 'CONSERVATIVE', '低风险固定收益产品'),
(2, 2, '基金', '货币市场基金', '000009', 60.00, 3.00, 'CONSERVATIVE', '低风险货币基金'),
(2, 2, '债券', '企业债券', '122000', 40.00, 4.50, 'CONSERVATIVE', '中等风险债券产品'),
(3, 3, '基金', '股票型基金', '000011', 50.00, 12.00, 'AGGRESSIVE', '高收益股票基金'),
(3, 3, '期货', '商品期货', 'AU2406', 30.00, 15.00, 'AGGRESSIVE', '高风险期货产品'),
(3, 3, '基金', '指数基金', '510300', 20.00, 10.00, 'MODERATE', '跟踪大盘指数的基金');

-- Sample notifications
INSERT INTO notifications (customer_id, notification_type, title, content, recipient, recipient_type, status) VALUES
(1, 'ASSESSMENT_SUBMITTED', '风险评估提交成功', '您的风险评估已成功提交，正在等待审核。', '13800138001', 'SMS', 'SENT'),
(2, 'ASSESSMENT_SUBMITTED', '风险评估提交成功', '您的风险评估已成功提交，正在等待审核。', '13800138002', 'SMS', 'SENT'),
(3, 'ASSESSMENT_SUBMITTED', '风险评估提交成功', '您的风险评估已成功提交，正在等待审核。', '13800138003', 'SMS', 'SENT'),
(4, 'ASSESSMENT_SUBMITTED', '风险评估提交成功', '您的风险评估已成功提交，正在等待审核。', '13800138004', 'SMS', 'SENT'),
(5, 'ASSESSMENT_SUBMITTED', '风险评估提交成功', '您的风险评估已成功提交，正在等待审核。', '13800138005', 'SMS', 'SENT');
