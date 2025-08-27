package com.xingye.bankrisk.repository;

import com.xingye.bankrisk.entity.ProductTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 产品标签数据访问层
 */
@Repository
public interface ProductTagRepository extends JpaRepository<ProductTag, Long> {

    /**
     * 根据标签名称查找标签
     */
    Optional<ProductTag> findByTagName(String tagName);

    /**
     * 根据标签类别查找标签
     */
    List<ProductTag> findByTagCategory(String tagCategory);

    /**
     * 根据标签名称模糊查询
     */
    @Query("SELECT t FROM ProductTag t WHERE t.tagName LIKE %:tagName%")
    List<ProductTag> findByTagNameLike(@Param("tagName") String tagName);

    /**
     * 获取所有标签类别
     */
    @Query("SELECT DISTINCT t.tagCategory FROM ProductTag t WHERE t.tagCategory IS NOT NULL")
    List<String> findAllTagCategories();

    /**
     * 统计各标签类别的数量
     */
    @Query("SELECT t.tagCategory, COUNT(t) FROM ProductTag t WHERE t.tagCategory IS NOT NULL GROUP BY t.tagCategory")
    List<Object[]> countTagsByCategory();
}
