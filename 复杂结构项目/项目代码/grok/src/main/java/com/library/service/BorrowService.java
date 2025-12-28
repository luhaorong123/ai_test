package com.library.service;

import com.library.entity.BorrowRecord;
import com.library.entity.Item;
import com.library.entity.User;
import com.library.enums.BorrowStatus;
import com.library.enums.ItemStatus;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.ItemRepository;
import com.library.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class BorrowService {

    private static final int DEFAULT_BORROW_DAYS = 7; // 默认借阅天数

    private final BorrowRecordRepository borrowRecordRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemService itemService; // 用于同步物品库存与状态

    public BorrowService(BorrowRecordRepository borrowRecordRepository,
                         ItemRepository itemRepository,
                         UserRepository userRepository,
                         ItemService itemService) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemService = itemService;
    }

    /**
     * 借出图书
     */
    @Transactional
    public BorrowRecord borrowItem(Long userId, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("图书不存在"));

        // 检查图书是否可借
        if (item.getStatus() != ItemStatus.AVAILABLE || item.getAvailableCopies() <= 0) {
            throw new RuntimeException("该图书当前不可借");
        }

        // 检查用户是否有未归还的逾期记录（可选严格策略）
        // long overdueCount = borrowRecordRepository.findByReturnDateIsNullAndDueDateBefore(LocalDate.now())
        //         .stream().filter(br -> br.getUser().getId().equals(userId)).count();
        // if (overdueCount > 0) {
        //     throw new RuntimeException("您有逾期未归还图书，无法借阅");
        // }

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(DEFAULT_BORROW_DAYS);

        BorrowRecord record = BorrowRecord.builder()
                .user(user)
                .item(item)
                .borrowDate(borrowDate)
                .dueDate(dueDate)
                .status(BorrowStatus.BORROWED)
                .renewCount(0)
                .build();

        // 更新物品可用数量（-1）
        itemService.syncItemStatus(itemId, -1);

        return borrowRecordRepository.save(record);
    }

    /**
     * 归还图书
     */
    @Transactional
    public BorrowRecord returnItem(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("借阅记录不存在"));

        if (record.getReturnDate() != null) {
            throw new RuntimeException("该图书已归还");
        }

        LocalDate returnDate = LocalDate.now();
        record.setReturnDate(returnDate);

        // 自动更新状态
        updateBorrowStatus(record);

        borrowRecordRepository.save(record);

        // 更新物品可用数量（+1）
        itemService.syncItemStatus(record.getItem().getId(), +1);

        return record;
    }

    /**
     * 获取单条借阅记录（含逾期天数）
     */
    public BorrowRecord getBorrowRecord(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("借阅记录不存在"));
        updateBorrowStatus(record); // 确保状态最新
        return record;
    }

    /**
     * 计算并返回逾期天数（正数表示逾期，0或负数表示未逾期）
     */
    public long calculateOverdueDays(BorrowRecord record) {
        if (record.getReturnDate() != null) {
            return 0; // 已归还无逾期
        }
        LocalDate today = LocalDate.now();
        if (today.isAfter(record.getDueDate())) {
            return ChronoUnit.DAYS.between(record.getDueDate(), today);
        }
        return 0;
    }

    /**
     * 自动更新借阅记录状态
     * BORROWED → OVERDUE（如果逾期且未归还）
     * 已归还 → RETURNED
     */
    @Transactional
    public void updateBorrowStatus(BorrowRecord record) {
        if (record.getReturnDate() != null) {
            record.setStatus(BorrowStatus.RETURNED);
        } else if (LocalDate.now().isAfter(record.getDueDate())) {
            record.setStatus(BorrowStatus.OVERDUE);
        } else {
            record.setStatus(BorrowStatus.BORROWED);
        }
    }

    /**
     * 查询用户的借阅记录（分页）
     */
    public java.util.List<BorrowRecord> getUserBorrowRecords(Long userId, int page, int size) {
        // 简单实现，可后续改为 Pageable
        return borrowRecordRepository.findByUserId(userId);
    }
}