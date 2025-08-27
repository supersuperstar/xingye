-- 统一字符集与引擎
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1) 用户表（无问题）
DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  account        VARCHAR(64)  NOT NULL UNIQUE,
  key_hash       VARCHAR(255) NOT NULL,
  name           VARCHAR(128) NOT NULL,
  telephone      VARCHAR(32),
  nuid           VARCHAR(32) UNIQUE,   -- 身份证号，注意上线需脱敏/单向加密
  email          VARCHAR(160),
  occupation     VARCHAR(128),
  invest_amount  DECIMAL(16,2) DEFAULT 0,
  status         ENUM('ACTIVE','LOCKED','DELETED') DEFAULT 'ACTIVE',
  evaluation_time DATETIME DEFAULT '2200-01-01 00:00:00',
  risk_level     ENUM('conservative','moderate','aggressive') NULL,
  latest_questionnaire_id BIGINT,   -- 标记“最新问卷”
  created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_users_tel(telephone),
  INDEX idx_users_nuid(nuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2) 问卷表（无问题）
DROP TABLE IF EXISTS questionnaires;
CREATE TABLE questionnaires (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id  BIGINT NOT NULL,
  ctime    DATETIME DEFAULT '2200-01-01 00:00:00',
  is_latest BOOLEAN DEFAULT FALSE,        -- 不做唯一索引，由 users.latest_questionnaire_id 标记最新
  age INT,
  annual DECIMAL(14,2),
  invest_time INT,
  max_loss DECIMAL(6,2),
  target VARCHAR(64),
  year_for_invest INT,
  score INT,
  status ENUM('conservative','moderate','aggressive') NULL,
  answers JSON,
  score_breakdown JSON,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_questionnaire_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_questionnaire_user (user_id, ctime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 把 users.latest_questionnaire_id 建立外键（无问题）
ALTER TABLE users
  ADD CONSTRAINT fk_users_latest_q
  FOREIGN KEY (latest_questionnaire_id) REFERENCES questionnaires(id)
  ON DELETE SET NULL;

-- 3) 投资产品表（无问题）
DROP TABLE IF EXISTS products;
CREATE TABLE products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_name VARCHAR(200) NOT NULL,
  product_type ENUM('CASH','BOND','CREDIT','ETF','STOCK','ALT','REITS','COMMODITY','OTHER') NOT NULL,
  risk_level   ENUM('conservative','moderate','aggressive') NOT NULL,
  expected_return DECIMAL(6,2),
  expected_volatility DECIMAL(6,2),  -- 预期波动率
  sharpe_ratio DECIMAL(8,4),        -- 夏普比率
  max_drawdown DECIMAL(6,2),        -- 最大回撤
  historical_return_1y DECIMAL(6,2), -- 1年历史收益率
  historical_return_3y DECIMAL(6,2), -- 3年历史收益率
  historical_return_5y DECIMAL(6,2), -- 5年历史收益率
  expense_ratio DECIMAL(6,4),       -- 费率
  minimum_investment DECIMAL(12,2), -- 最低投资额
  liquidity_score INT,              -- 流动性评分（1-10）
  market_cap ENUM('SMALL','MID','LARGE') NULL, -- 市值规模
  sector VARCHAR(100),              -- 行业板块
  code VARCHAR(64),
  currency VARCHAR(16) DEFAULT 'CNY',
  is_active BOOLEAN DEFAULT TRUE,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_products_type (product_type, risk_level),
  INDEX idx_products_active (is_active),
  INDEX idx_products_return (expected_return),
  INDEX idx_products_volatility (expected_volatility),
  INDEX idx_products_sharpe (sharpe_ratio),
  INDEX idx_products_sector (sector)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4) 产品标签表（无问题）
DROP TABLE IF EXISTS product_tags;
CREATE TABLE product_tags (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tag_name VARCHAR(50) NOT NULL UNIQUE,
  tag_category VARCHAR(50),  -- 标签类别：风险、策略、行业等
  description VARCHAR(200),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_tags_category (tag_category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5) 产品标签关联表（无问题）
DROP TABLE IF EXISTS product_tag_relations;
CREATE TABLE product_tag_relations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ptr_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  CONSTRAINT fk_ptr_tag FOREIGN KEY (tag_id) REFERENCES product_tags(id) ON DELETE CASCADE,
  UNIQUE KEY unique_product_tag (product_id, tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6) 产品评分表（无问题）
DROP TABLE IF EXISTS product_ratings;
CREATE TABLE product_ratings (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id BIGINT NOT NULL,
  rating_date DATE NOT NULL,
  overall_rating DECIMAL(3,1),     -- 综合评分（1-10）
  risk_adjusted_rating DECIMAL(3,1), -- 风险调整后评分
  performance_rating DECIMAL(3,1),   -- 业绩评分
  liquidity_rating DECIMAL(3,1),     -- 流动性评分
  rating_agency VARCHAR(100),       -- 评级机构
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_rating_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  INDEX idx_ratings_product_date (product_id, rating_date),
  INDEX idx_ratings_overall (overall_rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7) 工单表（修复：注释错误）
DROP TABLE IF EXISTS work_orders;
CREATE TABLE work_orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  customer_id BIGINT NOT NULL,              -- 谁提交的
  reviewer_id BIGINT,                       -- 当前环节处理人

  -- 当前工单状态：四个待审 + 已通过/已拒绝
  status ENUM(
    'PENDING_JUNIOR',
    'PENDING_MID',
    'PENDING_SENIOR',
    'PENDING_COMMITTEE',
    'APPROVED',
    'REJECTED'
  ) DEFAULT 'PENDING_JUNIOR',

  priority ENUM('LOW','MEDIUM','HIGH','CRITICAL') DEFAULT 'MEDIUM',
  advise   TEXT,                 -- 系统/人工的摘要性建议或说明
  sla_deadline DATETIME,

  -- 本单对应的风险评估快照（为避免再连问卷/规则引擎，写入快照）
  risk_score INT,
  risk_category ENUM('conservative','moderate','aggressive') NULL,

  -- 修复：注释修正（原“表5”错误，改为“表8 portfolio_recommendations”）
  user_choice       BIGINT,        -- 用户选择的组合 ,映射到 表8 portfolio_recommendations


  -- 四级审核的意见与时间（每级仅一条）
  junior_reviewer_id   BIGINT,
  junior_comment       TEXT,
  junior_commit_time   DATETIME,

  mid_reviewer_id      BIGINT,
  mid_comment          TEXT,
  mid_commit_time      DATETIME,

  senior_reviewer_id   BIGINT,
  senior_comment       TEXT,
  senior_commit_time   DATETIME,

  committee_reviewer_id BIGINT,
  committee_comment     TEXT,
  committee_commit_time DATETIME,

  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  CONSTRAINT fk_wo_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_wo_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE SET NULL,
  CONSTRAINT fk_wo_jr FOREIGN KEY (junior_reviewer_id) REFERENCES users(id) ON DELETE SET NULL,
  CONSTRAINT fk_wo_mr FOREIGN KEY (mid_reviewer_id) REFERENCES users(id) ON DELETE SET NULL,
  CONSTRAINT fk_wo_sr FOREIGN KEY (senior_reviewer_id) REFERENCES users(id) ON DELETE SET NULL,
  CONSTRAINT fk_wo_cr FOREIGN KEY (committee_reviewer_id) REFERENCES users(id) ON DELETE SET NULL,

  INDEX idx_wo_status (status, priority),
  INDEX idx_wo_customer (customer_id, created_at),
  INDEX idx_wo_reviewer (reviewer_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8) 组合明细：修复3个问题（多余逗号、缺失外键、缺失索引）
DROP TABLE IF EXISTS portfolio_recommendations;
CREATE TABLE portfolio_recommendations (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  work_order_id BIGINT NOT NULL,     -- 关联工单ID
  user_id       BIGINT NOT NULL,     -- 绑定用户
  customer_id   BIGINT NOT NULL,     -- 冗余出来，便于按人查询
  product_ids   JSON  NOT NULL,      -- 如: [101,202,303]
  alloc_pcts    JSON  NOT NULL,      -- 如: [35.0,35.0,30.0]  (与 product_ids 一一对应)
  llm_suggestion    JSON NULL,        -- GPT/其他大模型生成或优化 每个组合下面都有一个AI 推荐理由
  created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,

  -- 修复1：为user_id添加外键（确保绑定用户有效）
  CONSTRAINT fk_pr_user     FOREIGN KEY (user_id)       REFERENCES users(id)      ON DELETE CASCADE,
  CONSTRAINT fk_pr_wo       FOREIGN KEY (work_order_id) REFERENCES work_orders(id) ON DELETE CASCADE,
  -- 修复2：移除最后一个外键后的多余逗号
  CONSTRAINT fk_pr_customer FOREIGN KEY (customer_id)   REFERENCES users(id)      ON DELETE CASCADE,

  -- 修复3：添加常用查询索引（提升按用户、工单查询的效率）
  INDEX idx_pr_user (user_id, created_at),
  INDEX idx_pr_customer (customer_id, work_order_id),
  INDEX idx_pr_wo (work_order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


SET FOREIGN_KEY_CHECKS = 1;

-- 初始化数据（无问题，已校验字段对应关系）
-- 插入产品标签
INSERT INTO product_tags (tag_name, tag_category, description) VALUES
('高流动性', '流动性', '资金进出灵活，适合短期投资'),
('低波动', '风险', '价格波动较小，风险较低'),
('稳健增长', '策略', '注重长期稳健收益'),
('高收益', '收益', '预期收益较高'),
('科技股', '行业', '科技板块投资'),
('医药股', '行业', '医药板块投资'),
('新能源', '行业', '新能源板块投资'),
('价值投资', '策略', '寻找被低估的投资标的'),
('成长投资', '策略', '关注高增长企业'),
('债券型', '类型', '以债券为主要投资标的'),
('股票型', '类型', '以股票为主要投资标的'),
('混合型', '类型', '股票和债券混合投资'),
('指数型', '类型', '跟踪指数表现'),
('主动型', '类型', '基金经理主动选股');

-- 插入示例产品数据
INSERT INTO products (product_name, product_type, risk_level, expected_return, expected_volatility,
                     sharpe_ratio, max_drawdown, historical_return_1y, historical_return_3y,
                     historical_return_5y, expense_ratio, minimum_investment, liquidity_score,
                     market_cap, sector, code, currency, is_active) VALUES
-- 现金类产品
('货币基金A', 'CASH', 'conservative', 2.50, 0.10, 25.0000, 0.05, 2.30, 2.45, 2.55, 0.0010, 100.00, 10, NULL, '货币市场', 'CASH001', 'CNY', 1),
('货币基金B', 'CASH', 'conservative', 2.80, 0.12, 23.3333, 0.08, 2.65, 2.70, 2.80, 0.0012, 1000.00, 9, NULL, '货币市场', 'CASH002', 'CNY', 1),
('定期存款', 'CASH', 'conservative', 3.00, 0.05, 60.0000, 0.02, 2.95, 3.05, 3.15, 0.0000, 10000.00, 8, NULL, '存款', 'DEPOSIT001', 'CNY', 1),

-- 债券类产品
('国债ETF', 'BOND', 'conservative', 3.50, 0.80, 4.3750, 2.50, 3.20, 3.45, 3.60, 0.0005, 100.00, 9, 'LARGE', '政府债券', 'BOND001', 'CNY', 1),
('企业债基金A', 'BOND', 'moderate', 4.20, 1.20, 3.5000, 3.80, 4.00, 4.15, 4.25, 0.0008, 100.00, 8, 'MID', '企业债券', 'BOND002', 'CNY', 1),
('可转债基金', 'BOND', 'moderate', 4.80, 1.50, 3.2000, 4.20, 4.50, 4.75, 4.85, 0.0010, 100.00, 7, 'MID', '可转债', 'BOND003', 'CNY', 1),

-- ETF产品
('沪深300ETF', 'ETF', 'moderate', 8.50, 2.80, 3.0357, 15.20, 8.20, 8.45, 8.60, 0.0005, 100.00, 10, 'LARGE', '宽基指数', 'ETF001', 'CNY', 1),
('创业板ETF', 'ETF', 'aggressive', 12.00, 4.50, 2.6667, 25.80, 11.50, 11.80, 12.20, 0.0005, 100.00, 10, 'MID', '创业板', 'ETF002', 'CNY', 1),
('医药ETF', 'ETF', 'moderate', 9.50, 3.20, 2.9688, 18.50, 9.00, 9.35, 9.60, 0.0005, 100.00, 9, 'MID', '医药', 'ETF003', 'CNY', 1),

-- 股票型基金
('价值成长基金A', 'STOCK', 'moderate', 10.50, 3.80, 2.7632, 22.00, 10.20, 10.40, 10.65, 0.0015, 100.00, 8, 'LARGE', '价值股', 'STOCK001', 'CNY', 1),
('科技创新基金', 'STOCK', 'aggressive', 15.00, 5.20, 2.8846, 30.50, 14.50, 14.80, 15.20, 0.0018, 100.00, 7, 'MID', '科技股', 'STOCK002', 'CNY', 1),
('新能源基金', 'STOCK', 'aggressive', 14.50, 4.80, 3.0208, 28.00, 14.00, 14.30, 14.60, 0.0016, 100.00, 8, 'MID', '新能源', 'STOCK003', 'CNY', 1),
('医药健康基金', 'STOCK', 'moderate', 11.00, 3.50, 3.1429, 20.50, 10.80, 10.95, 11.10, 0.0014, 100.00, 8, 'MID', '医药', 'STOCK004', 'CNY', 1),

-- 另类投资
('REITs基金A', 'REITS', 'moderate', 7.50, 2.20, 3.4091, 12.00, 7.20, 7.40, 7.60, 0.0012, 1000.00, 6, 'MID', '房地产', 'REITS001', 'CNY', 1),
('商品期货基金', 'COMMODITY', 'aggressive', 13.00, 4.20, 3.0952, 24.50, 12.50, 12.80, 13.10, 0.0020, 5000.00, 5, 'LARGE', '商品期货', 'COMM001', 'CNY', 1);

-- 插入产品标签关联（无问题，tag_id与product_id对应正确）
INSERT INTO product_tag_relations (product_id, tag_id) VALUES
-- 货币基金A
(1, 1), (1, 2), (1, 3),
-- 货币基金B
(2, 1), (2, 2),
-- 定期存款
(3, 1), (3, 2), (3, 3),
-- 国债ETF
(4, 2), (4, 3), (4, 10),
-- 企业债基金A
(5, 2), (5, 10), (5, 12),
-- 可转债基金
(6, 2), (6, 10), (6, 12),
-- 沪深300ETF
(7, 1), (7, 13), (7, 8),
-- 创业板ETF
(8, 1), (8, 13), (8, 9),
-- 医药ETF
(9, 1), (9, 13), (9, 6),
-- 价值成长基金A
(10, 8), (10, 12), (10, 14),
-- 科技创新基金
(11, 5), (11, 9), (11, 12), (11, 14),
-- 新能源基金
(12, 7), (12, 9), (12, 12), (12, 14),
-- 医药健康基金
(13, 6), (13, 8), (13, 12), (13, 14),
-- REITs基金A
(14, 12), (14, 3),
-- 商品期货基金
(15, 4), (15, 9), (15, 12);

-- 插入产品评分数据（无问题，字段格式与表结构匹配）
INSERT INTO product_ratings (product_id, rating_date, overall_rating, risk_adjusted_rating,
                           performance_rating, liquidity_rating, rating_agency) VALUES
(1, '2024-01-15', 9.5, 9.8, 9.2, 10.0, '晨星'),
(2, '2024-01-15', 9.0, 9.5, 8.8, 9.5, '晨星'),
(3, '2024-01-15', 8.5, 9.0, 8.0, 9.0, '央行评级'),
(4, '2024-01-15', 9.2, 9.5, 9.0, 9.5, '晨星'),
(5, '2024-01-15', 8.8, 9.0, 8.5, 9.0, '晨星'),
(6, '2024-01-15', 8.5, 8.8, 8.2, 8.8, '晨星'),
(7, '2024-01-15', 9.0, 8.5, 9.5, 9.8, '晨星'),
(8, '2024-01-15', 8.5, 8.0, 9.0, 9.5, '晨星'),
(9, '2024-01-15', 8.8, 8.5, 9.2, 9.0, '晨星'),
(10, '2024-01-15', 9.0, 8.8, 9.2, 8.5, '晨星'),
(11, '2024-01-15', 8.2, 8.0, 8.5, 8.0, '晨星'),
(12, '2024-01-15', 8.5, 8.2, 8.8, 8.2, '晨星'),
(13, '2024-01-15', 8.8, 8.5, 9.0, 8.5, '晨星'),
(14, '2024-01-15', 7.5, 8.0, 7.0, 7.5, '晨星'),
(15, '2024-01-15', 7.0, 7.5, 6.5, 7.0, '晨星');