-- 银行投资风险审核系统 - 个性化产品推荐功能演示脚本
-- 此脚本展示了如何使用新的产品推荐功能

-- 1. 查看产品多样性
SELECT
    product_name,
    product_type,
    risk_level,
    expected_return,
    expected_volatility,
    sharpe_ratio,
    sector
FROM products
ORDER BY expected_return DESC
LIMIT 10;

-- 2. 查看产品标签系统
SELECT
    p.product_name,
    GROUP_CONCAT(pt.tag_name SEPARATOR ', ') as tags
FROM products p
JOIN product_tag_relations ptr ON p.id = ptr.product_id
JOIN product_tags pt ON ptr.tag_id = pt.tag_id
GROUP BY p.id, p.product_name
ORDER BY p.product_name;

-- 3. 查看高评分产品
SELECT
    p.product_name,
    pr.overall_rating,
    pr.sharpe_ratio,
    pr.performance_rating,
    pr.rating_agency
FROM products p
JOIN product_ratings pr ON p.id = pr.product_id
WHERE pr.rating_date = (SELECT MAX(rating_date) FROM product_ratings WHERE product_id = p.id)
ORDER BY pr.overall_rating DESC
LIMIT 5;

-- 4. 按风险等级查看产品分布
SELECT
    risk_level,
    COUNT(*) as product_count,
    ROUND(AVG(expected_return), 2) as avg_return,
    ROUND(AVG(expected_volatility), 2) as avg_volatility,
    ROUND(AVG(sharpe_ratio), 4) as avg_sharpe
FROM products
GROUP BY risk_level
ORDER BY FIELD(risk_level, 'CONSERVATIVE', 'MODERATE', 'AGGRESSIVE');

-- 5. 查看行业板块分布
SELECT
    sector,
    COUNT(*) as product_count,
    ROUND(AVG(expected_return), 2) as avg_return
FROM products
WHERE sector IS NOT NULL
GROUP BY sector
ORDER BY product_count DESC;

-- 6. 查找最适合保守型投资者的产品
SELECT
    product_name,
    expected_return,
    expected_volatility,
    sharpe_ratio,
    max_drawdown,
    liquidity_score
FROM products
WHERE risk_level = 'CONSERVATIVE'
ORDER BY sharpe_ratio DESC, liquidity_score DESC
LIMIT 5;

-- 7. 查找最适合激进型投资者的产品
SELECT
    product_name,
    expected_return,
    expected_volatility,
    sharpe_ratio,
    max_drawdown,
    sector
FROM products
WHERE risk_level = 'AGGRESSIVE'
ORDER BY expected_return DESC, sharpe_ratio DESC
LIMIT 5;

-- 8. 查看不同产品类型的表现对比
SELECT
    product_type,
    COUNT(*) as count,
    ROUND(AVG(expected_return), 2) as avg_return,
    ROUND(AVG(expected_volatility), 2) as avg_volatility,
    ROUND(AVG(sharpe_ratio), 4) as avg_sharpe
FROM products
GROUP BY product_type
ORDER BY avg_sharpe DESC;

-- 9. 模拟用户推荐场景
-- 假设用户风险评分：65分（稳健型）
-- 系统会推荐：30%保守 + 50%平衡 + 20%激进的产品组合

-- 查看保守型产品（30%分配）
SELECT
    product_name,
    expected_return,
    expected_volatility,
    sharpe_ratio
FROM products
WHERE risk_level = 'CONSERVATIVE'
ORDER BY sharpe_ratio DESC
LIMIT 3;

-- 查看平衡型产品（50%分配）
SELECT
    product_name,
    expected_return,
    expected_volatility,
    sharpe_ratio
FROM products
WHERE risk_level = 'MODERATE'
ORDER BY sharpe_ratio DESC
LIMIT 3;

-- 查看激进型产品（20%分配）
SELECT
    product_name,
    expected_return,
    expected_volatility,
    sharpe_ratio
FROM products
WHERE risk_level = 'AGGRESSIVE'
ORDER BY sharpe_ratio DESC
LIMIT 3;

-- 10. 查看系统统计信息
SELECT
    '总产品数' as metric,
    COUNT(*) as value
FROM products
UNION ALL
SELECT
    '产品标签数' as metric,
    COUNT(*) as value
FROM product_tags
UNION ALL
SELECT
    '产品评分记录数' as metric,
    COUNT(*) as value
FROM product_ratings
UNION ALL
SELECT
    '标签类别数' as metric,
    COUNT(DISTINCT tag_category) as value
FROM product_tags
WHERE tag_category IS NOT NULL;

-- 演示说明：
-- 1. 本脚本展示了新系统的产品多样性
-- 2. 演示了标签系统的灵活性
-- 3. 展示了评分系统的完整性
-- 4. 模拟了推荐算法的选择逻辑
-- 5. 提供了系统统计信息

-- 使用方法：
-- 1. 确保数据库已初始化（运行mysql.sql）
-- 2. 连接到数据库
-- 3. 逐条执行上述查询
-- 4. 观察结果，了解新系统的功能特点
