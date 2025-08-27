package com.xingye.bankrisk.repository;

import com.xingye.bankrisk.entity.ProductTagRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 产品标签关联数据访问层
 */
@Repository
public interface ProductTagRelationRepository extends JpaRepository<ProductTagRelation, Long> {

    /**
     * 根据产品ID查找所有标签关联
     */
    List<ProductTagRelation> findByProductId(Long productId);

    /**
     * 根据标签ID查找所有产品关联
     */
    List<ProductTagRelation> findByTagId(Long tagId);

    /**
     * 删除指定产品的所有标签关联
     */
    void deleteByProductId(Long productId);

    /**
     * 删除指定标签的所有产品关联
     */
    void deleteByTagId(Long tagId);

    /**
     * 检查产品和标签的关联是否存在
     */
    boolean existsByProductIdAndTagId(Long productId, Long tagId);

    /**
     * 根据产品ID列表查找标签关联
     */
    @Query("SELECT ptr FROM ProductTagRelation ptr WHERE ptr.productId IN :productIds")
    List<ProductTagRelation> findByProductIds(@Param("productIds") List<Long> productIds);

    /**
     * 根据标签ID列表查找产品关联
     */
    @Query("SELECT ptr FROM ProductTagRelation ptr WHERE ptr.tagId IN :tagIds")
    List<ProductTagRelation> findByTagIds(@Param("tagIds") List<Long> tagIds);

    /**
     * 获取产品的标签ID列表
     */
    @Query("SELECT ptr.tagId FROM ProductTagRelation ptr WHERE ptr.productId = :productId")
    List<Long> findTagIdsByProductId(@Param("productId") Long productId);

    /**
     * 获取标签的产品ID列表
     */
    @Query("SELECT ptr.productId FROM ProductTagRelation ptr WHERE ptr.tagId = :tagId")
    List<Long> findProductIdsByTagId(@Param("tagId") Long tagId);
}
