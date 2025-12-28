// BorrowRecordRepository.java - 借阅记录数据访问接口
package com.item.management.repository;

import com.item.management.entity.BorrowRecord;
import com.item.management.enums.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    Page<BorrowRecord> findByUserId(Long userId, Pageable pageable);

    Page<BorrowRecord> findByItemId(Long itemId, Pageable pageable);

    Page<BorrowRecord> findByUserIdAndStatus(Long userId, BorrowStatus status, Pageable pageable);

    Optional<BorrowRecord> findByUserIdAndItemIdAndStatus(Long userId, Long itemId, BorrowStatus status);

    @Query("SELECT br FROM BorrowRecord br WHERE br.status = :status AND br.deleted = false")
    Page<BorrowRecord> findByStatus(@Param("status") BorrowStatus status, Pageable pageable);

    @Query("SELECT br FROM BorrowRecord br WHERE " +
            "br.expectedReturnDate BETWEEN :startDate AND :endDate AND " +
            "br.status = 'ACTIVE' AND " +
            "br.deleted = false")
    Page<BorrowRecord> findUpcomingReturns(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("SELECT br FROM BorrowRecord br WHERE " +
            "br.expectedReturnDate < :date AND " +
            "br.status = 'ACTIVE' AND " +
            "br.deleted = false")
    Page<BorrowRecord> findOverdueRecords(@Param("date") LocalDate date, Pageable pageable);

    @Query("SELECT br FROM BorrowRecord br WHERE " +
            "br.user.id = :userId AND " +
            "br.expectedReturnDate < :date AND " +
            "br.status = 'ACTIVE' AND " +
            "br.deleted = false")
    Page<BorrowRecord> findUserOverdueRecords(
            @Param("userId") Long userId,
            @Param("date") LocalDate date,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE BorrowRecord br SET br.status = 'OVERDUE' WHERE " +
            "br.expectedReturnDate < CURRENT_DATE AND " +
            "br.status = 'ACTIVE'")
    int updateOverdueRecords();

    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE " +
            "br.user.id = :userId AND " +
            "br.status = 'OVERDUE' AND " +
            "br.deleted = false")
    long countOverdueByUserId(@Param("userId") Long userId);

    @Query("SELECT br FROM BorrowRecord br WHERE " +
            "br.item.status = 'BORROWED' AND " +
            "br.status IN ('ACTIVE', 'OVERDUE') AND " +
            "br.deleted = false")
    Page<BorrowRecord> findActiveBorrows(Pageable pageable);

    @Query("SELECT br FROM BorrowRecord br WHERE " +
            "br.user.id = :userId AND " +
            "br.item.id = :itemId AND " +
            "br.status = 'ACTIVE' AND " +
            "br.deleted = false")
    Optional<BorrowRecord> findActiveBorrowByUserAndItem(
            @Param("userId") Long userId,
            @Param("itemId") Long itemId
    );

    // 在BorrowRecordRepository中添加以下方法：
    @Query("SELECT br FROM BorrowRecord br WHERE " +
            "(:userId IS NULL OR br.user.id = :userId) AND " +
            "(:itemId IS NULL OR br.item.id = :itemId) AND " +
            "(:status IS NULL OR br.status = :status) AND " +
            "(:overdue IS NULL OR (br.status = 'ACTIVE' AND br.expectedReturnDate < CURRENT_DATE)) AND " +
            "(:borrowDateStart IS NULL OR DATE(br.borrowDate) >= :borrowDateStart) AND " +
            "(:borrowDateEnd IS NULL OR DATE(br.borrowDate) <= :borrowDateEnd) AND " +
            "(:expectedReturnDateStart IS NULL OR br.expectedReturnDate >= :expectedReturnDateStart) AND " +
            "(:expectedReturnDateEnd IS NULL OR br.expectedReturnDate <= :expectedReturnDateEnd)")
    Page<BorrowRecord> searchBorrowRecords(
            @Param("userId") Long userId,
            @Param("itemId") Long itemId,
            @Param("status") BorrowStatus status,
            @Param("overdue") Boolean overdue,
            @Param("borrowDateStart") LocalDate borrowDateStart,
            @Param("borrowDateEnd") LocalDate borrowDateEnd,
            @Param("expectedReturnDateStart") LocalDate expectedReturnDateStart,
            @Param("expectedReturnDateEnd") LocalDate expectedReturnDateEnd,
            Pageable pageable
    );

    @Query("SELECT br FROM BorrowRecord br WHERE " +
            "br.user.id = :userId AND " +
            "br.status = 'ACTIVE' AND " +
            "br.expectedReturnDate BETWEEN :startDate AND :endDate")
    Page<BorrowRecord> findUserUpcomingReturns(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE " +
            "br.user.id = :userId AND " +
            "br.status = 'ACTIVE'")
    long countActiveBorrowsByUser(@Param("userId") Long userId);

    @Query("SELECT br.item.id, COUNT(br) as borrowCount FROM BorrowRecord br " +
            "GROUP BY br.item.id ORDER BY borrowCount DESC")
    List<Object[]> findMostBorrowedItems(Pageable pageable);

    @Query("SELECT br.user.id, COUNT(br) as borrowCount FROM BorrowRecord br " +
            "GROUP BY br.user.id ORDER BY borrowCount DESC")
    List<Object[]> findMostActiveUsers(Pageable pageable);

    // 在BorrowRecordRepository中添加以下方法：
    @Query("SELECT br.user.id, u.username, COUNT(br) as overdueCount " +
            "FROM BorrowRecord br " +
            "JOIN User u ON br.user.id = u.id " +
            "WHERE br.status = 'ACTIVE' AND br.expectedReturnDate < CURRENT_DATE " +
            "GROUP BY br.user.id, u.username " +
            "ORDER BY overdueCount DESC")
    List<Object[]> findMostOverdueUsers(Pageable pageable);

    @Query("SELECT i.category, COUNT(br) as overdueCount " +
            "FROM BorrowRecord br " +
            "JOIN Item i ON br.item.id = i.id " +
            "WHERE br.status = 'ACTIVE' AND br.expectedReturnDate < CURRENT_DATE " +
            "AND i.category IS NOT NULL " +
            "GROUP BY i.category " +
            "ORDER BY overdueCount DESC")
    List<Object[]> findMostOverdueCategories(Pageable pageable);

    @Query("SELECT br FROM BorrowRecord br WHERE " +
            "br.status = 'ACTIVE' AND " +
            "br.expectedReturnDate BETWEEN :startDate AND :endDate AND " +
            "(:userId IS NULL OR br.user.id = :userId)")
    Page<BorrowRecord> findUpcomingReturnsByUser(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE " +
            "br.status = 'ACTIVE' AND " +
            "br.expectedReturnDate BETWEEN CURRENT_DATE AND :endDate")
    long countUpcomingReturns(@Param("endDate") LocalDate endDate);

    @Query("SELECT br FROM BorrowRecord br WHERE " +
            "br.status = 'ACTIVE' AND " +
            "br.expectedReturnDate < :date " +
            "ORDER BY br.expectedReturnDate ASC")
    Page<BorrowRecord> findOverdueRecordsWithSort(
            @Param("date") LocalDate date,
            Pageable pageable
    );
}