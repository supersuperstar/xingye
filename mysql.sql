-- 统一字符集与引擎
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1) 用户表
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
  risk_level     ENUM('conservative','moderate','aggressive'),
  latest_questionnaire_id BIGINT,   -- 标记“最新问卷”
  created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_users_tel(telephone),
  INDEX idx_users_nuid(nuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2) 问卷表
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
  status ENUM('conservative','moderate','aggressive'),
  answers JSON,
  score_breakdown JSON,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_questionnaire_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_questionnaire_user (user_id, ctime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 把 users.latest_questionnaire_id 建立外键（需要问卷表已存在）
ALTER TABLE users
  ADD CONSTRAINT fk_users_latest_q
  FOREIGN KEY (latest_questionnaire_id) REFERENCES questionnaires(id)
  ON DELETE SET NULL;

-- 3) 投资产品表
DROP TABLE IF EXISTS products;
CREATE TABLE products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_name VARCHAR(200) NOT NULL,
  product_type ENUM('CASH','BOND','CREDIT','ETF','STOCK','ALT','REITS','COMMODITY','OTHER') NOT NULL,
  risk_level   ENUM('conservative','moderate','aggressive') NOT NULL,
  expected_return DECIMAL(6,2),
  code VARCHAR(64),
  currency VARCHAR(16) DEFAULT 'CNY',
  is_active BOOLEAN DEFAULT TRUE,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_products_type (product_type, risk_level),
  INDEX idx_products_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4) 工单表（合并“申请”与“审核意见” + 最终建议）
DROP TABLE IF EXISTS work_orders;
CREATE TABLE work_orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  customer_id BIGINT NOT NULL,              -- 谁提交的
  -- 可根据当前环节指派处理人（不是四级审的固定人）
  reviewer_id BIGINT,

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
  risk_category ENUM('conservative','moderate','aggressive'),

  -- 建议：系统/用户选择/大模型/最终发布
  user_choice       BIGINT,        -- 用户选择的组合 ,映射到 表5


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

-- 5) 组合明细：现在指向 work_orders（而非 applications）
DROP TABLE IF EXISTS portfolio_recommendations;
CREATE TABLE portfolio_recommendations (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id       BIGINT NOT NULL,     -- 绑定用户
  customer_id   BIGINT NOT NULL,     -- 冗余出来，便于按人查询
  product_ids   JSON  NOT NULL,      -- 如: [101,202,303]
  alloc_pcts    JSON  NOT NULL,      -- 如: [35.0,35.0,30.0]  (与 product_ids 一一对应)
  llm_suggestion    JSON,        -- GPT/其他大模型生成或优化 每个组合下面都有一个AI 推荐理由
  created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_pr_wo       FOREIGN KEY (work_order_id) REFERENCES work_orders(id) ON DELETE CASCADE,
  CONSTRAINT fk_pr_customer FOREIGN KEY (customer_id)   REFERENCES users(id)      ON DELETE CASCADE,

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


SET FOREIGN_KEY_CHECKS = 1;