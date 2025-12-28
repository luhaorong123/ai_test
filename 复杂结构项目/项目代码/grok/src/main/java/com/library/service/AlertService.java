package com.library.service;

import com.library.entity.BorrowRecord;
import com.library.enums.BorrowStatus;
import com.library.repository.BorrowRecordRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BorrowService borrowService; // 用于计算逾期天数和更新状态

    public AlertService(BorrowRecordRepository borrowRecordRepository,
                        BorrowService borrowService) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.borrowService = borrowService;
    }

    /**
     * 查询所有已逾期记录（分页）
     */
    public Page<BorrowRecord> getOverdueRecords(int page, int size) {
        LocalDate today = LocalDate.now();
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());

        // 先找未归还且已过应还日期的记录
        Page<BorrowRecord> overduePage = borrowRecordRepository
                .findByReturnDateIsNullAndDueDateBefore(today, pageable);

        // 确保状态最新
        overduePage.forEach(borrowService::updateBorrowStatus);

        return overduePage;
    }

    /**
     * 查询所有3天内（含今天）即将到期的记录（未归还）
     */
    public Page<BorrowRecord> getUpcomingDueRecords(int page, int size) {
        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);

        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());

        Page<BorrowRecord> upcomingPage = borrowRecordRepository
                .findByReturnDateIsNullAndDueDateBetween(today, threeDaysLater, pageable);

        upcomingPage.forEach(borrowService::updateBorrowStatus);

        return upcomingPage;
    }

    /**
     * 查询指定用户的逾期记录（分页）
     */
    public Page<BorrowRecord> getOverdueRecordsByUser(Long userId, int page, int size) {
        LocalDate today = LocalDate.now();
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());

        Page<BorrowRecord> userOverdue = borrowRecordRepository
                .findByUserIdAndReturnDateIsNullAndDueDateBefore(userId, today, pageable);

        userOverdue.forEach(borrowService::updateBorrowStatus);

        return userOverdue;
    }

    /**
     * 查询指定用户的3天内即将到期记录
     */
    public Page<BorrowRecord> getUpcomingDueRecordsByUser(Long userId, int page, int size) {
        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);

        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());

        Page<BorrowRecord> userUpcoming = borrowRecordRepository
                .findByUserIdAndReturnDateIsNullAndDueDateBetween(userId, today, threeDaysLater, pageable);

        userUpcoming.forEach(borrowService::updateBorrowStatus);

        return userUpcoming;
    }
}