// ItemRepository.java - 物品数据访问接口
package com.item.management.repository;

import com.item.management.entity.Item;
import com.item.management.enums.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByName(String name);

    Page<Item> findByCategory(String category, Pageable pageable);

    Page<Item> findByStatus(ItemStatus status, Pageable pageable);

    Page<Item> findByCategoryAndStatus(String category, ItemStatus status, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE " +
            "(:name IS NULL OR i.name LIKE %:name%) AND " +
            "(:category IS NULL OR i.category LIKE %:category%) AND " +
            "(:status IS NULL OR i.status = :status)")
    Page<Item> searchItems(
            @Param("name") String name,
            @Param("category") String category,
            @Param("status") ItemStatus status,
            Pageable pageable
    );

    @Query(value = "SELECT * FROM items i WHERE " +
            "MATCH(i.name, i.description, i.category) AGAINST(:keyword IN BOOLEAN MODE) AND " +
            "(:status IS NULL OR i.status = :status) AND " +
            "i.deleted = false",
            countQuery = "SELECT COUNT(*) FROM items i WHERE " +
                    "MATCH(i.name, i.description, i.category) AGAINST(:keyword IN BOOLEAN MODE) AND " +
                    "(:status IS NULL OR i.status = :status) AND " +
                    "i.deleted = false",
            nativeQuery = true)
    Page<Item> fullTextSearch(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE Item i SET i.status = :status WHERE i.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") ItemStatus status);

    @Query("SELECT i FROM Item i WHERE i.status = 'AVAILABLE' AND i.deleted = false")
    Page<Item> findAvailableItems(Pageable pageable);

    long countByStatus(ItemStatus status);

    // 在ItemRepository中添加以下方法：
    @Query("SELECT DISTINCT i.category FROM Item i WHERE i.category IS NOT NULL AND i.deleted = false")
    List<String> findAllCategories();

    @Query("SELECT i FROM Item i WHERE i.updatedAt < :threshold AND i.status = 'MAINTENANCE'")
    List<Item> findMaintenanceItemsBefore(@Param("threshold") LocalDateTime threshold);

    @Query("SELECT i FROM Item i WHERE i.createdAt >= :startDate AND i.createdAt <= :endDate")
    Page<Item> findItemsByCreateTimeRange(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          Pageable pageable);

    @Query("SELECT i.category, COUNT(i) as count FROM Item i WHERE i.deleted = false GROUP BY i.category")
    List<Object[]> countItemsByCategory();

}