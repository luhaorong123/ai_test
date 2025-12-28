// BorrowService.java - 借阅服务接口
package com.item.management.service;

import com.item.management.dto.request.BorrowRequest;
import com.item.management.dto.request.ReturnRequest;
import com.item.management.dto.response.BorrowStatsDTO;
import com.item.management.entity.BorrowRecord;
import com.item.management.entity.Item;
import com.item.management.entity.User;
import com.item.management.enums.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface BorrowService {

    // 借阅操作
    BorrowRecord borrowItem(BorrowRequest request);

    BorrowRecord returnItem(Long borrowId, ReturnRequest request);

    BorrowRecord renewBorrow(Long borrowId, LocalDate newExpectedReturnDate);

    BorrowRecord cancelBorrow(Long borrowId);

    // 借阅记录查询
    BorrowRecord getBorrowRecordById(Long borrowId);

    BorrowRecord getActiveBorrowByUserAndItem(Long userId, Long itemId);

    Page<BorrowRecord> getAllBorrowRecords(Pageable pageable);

    Page<BorrowRecord> getBorrowRecordsByUser(Long userId, Pageable pageable);

    Page<BorrowRecord> getBorrowRecordsByItem(Long itemId, Pageable pageable);

    Page<BorrowRecord> getBorrowRecordsByStatus(BorrowStatus status, Pageable pageable);

    Page<BorrowRecord> getBorrowRecordsByUserAndStatus(Long userId, BorrowStatus status, Pageable pageable);

    // 逾期管理
    Page<BorrowRecord> getOverdueBorrowRecords(Pageable pageable);

    Page<BorrowRecord> getOverdueBorrowRecordsByUser(Long userId, Pageable pageable);

    Page<BorrowRecord> getUpcomingReturns(LocalDate date, Pageable pageable);

    Page<BorrowRecord> getUpcomingReturnsByUser(Long userId, LocalDate date, Pageable pageable);

    // 统计信息
    long countAllBorrowRecords();

    long countBorrowRecordsByUser(Long userId);

    long countBorrowRecordsByStatus(BorrowStatus status);

    long countOverdueBorrowRecords();

    long countOverdueBorrowRecordsByUser(Long userId);

    BorrowStatsDTO getBorrowStats();

    BorrowStatsDTO getUserBorrowStats(Long userId);

    // 验证
    boolean canUserBorrowMore(Long userId);

    boolean isItemAvailableForBorrow(Long itemId);

    boolean hasOverdueBorrowRecords(Long userId);

    // 批量操作
    void batchUpdateOverdueStatus();

    List<BorrowRecord> getExpiringBorrowRecords(int daysThreshold);

    // 计算相关
    int calculateOverdueDays(BorrowRecord record);

    double calculateOverdueFine(BorrowRecord record, double dailyFineRate);

    boolean isBorrowOverdue(BorrowRecord record);

    void autoUpdateBorrowStatus();
}