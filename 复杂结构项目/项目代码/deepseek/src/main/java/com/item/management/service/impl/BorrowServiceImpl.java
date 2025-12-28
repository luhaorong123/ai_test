// BorrowServiceImpl.java - 借阅服务实现类
package com.item.management.service.impl;

import com.item.management.config.BorrowProperties;
import com.item.management.dto.request.BorrowRequest;
import com.item.management.dto.request.ReturnRequest;
import com.item.management.dto.response.BorrowStatsDTO;
import com.item.management.entity.BorrowRecord;
import com.item.management.entity.Item;
import com.item.management.entity.User;
import com.item.management.enums.BorrowStatus;
import com.item.management.enums.ItemStatus;
import com.item.management.enums.UserStatus;
import com.item.management.exception.BusinessException;
import com.item.management.repository.BorrowRecordRepository;
import com.item.management.repository.ItemRepository;
import com.item.management.repository.UserRepository;
import com.item.management.service.BorrowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BorrowProperties borrowProperties;

    @Override
    @Transactional
    public BorrowRecord borrowItem(BorrowRequest request) {
        // 验证用户
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("用户不存在，ID: " + request.getUserId()));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("用户账号已被禁用，无法借阅物品");
        }

        // 验证物品
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new BusinessException("物品不存在，ID: " + request.getItemId()));

        if (!item.isAvailable()) {
            throw new BusinessException("物品当前不可借出，状态为: " + item.getStatus().getDescription());
        }

        // 检查用户是否已经有逾期记录
        if (hasOverdueBorrowRecords(request.getUserId())) {
            throw new BusinessException("用户有逾期未还记录，请先归还逾期物品");
        }

        // 检查用户是否已达到最大借阅数量
        if (!canUserBorrowMore(request.getUserId())) {
            throw new BusinessException("已达到最大借阅数量，无法再借阅新物品");
        }

        // 检查物品是否已被其他人借出
        if (!isItemAvailableForBorrow(request.getItemId())) {
            throw new BusinessException("物品已被其他人借出");
        }

        // 设置预计归还时间（默认7天）
        LocalDate expectedReturnDate = request.getExpectedReturnDate();
        if (expectedReturnDate == null) {
            expectedReturnDate = LocalDate.now().plusDays(borrowProperties.getDefaultBorrowDays());
        }

        // 验证预计归还时间是否合理
        if (expectedReturnDate.isBefore(LocalDate.now())) {
            throw new BusinessException("预计归还时间不能早于当前时间");
        }

        long maxBorrowDays = borrowProperties.getMaxBorrowDays();
        if (ChronoUnit.DAYS.between(LocalDate.now(), expectedReturnDate) > maxBorrowDays) {
            throw new BusinessException("借阅时间不能超过 " + maxBorrowDays + " 天");
        }

        // 创建借阅记录
        BorrowRecord borrowRecord = BorrowRecord.builder()
                .user(user)
                .item(item)
                .expectedReturnDate(expectedReturnDate)
                .status(BorrowStatus.ACTIVE)
                .build();

        BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);

        // 更新物品状态
        item.borrow();
        itemRepository.save(item);

        log.info("用户 {} 借出物品 {} 成功，借阅ID: {}",
                user.getUsername(), item.getName(), savedRecord.getId());

        return savedRecord;
    }

    @Override
    @Transactional
    public BorrowRecord returnItem(Long borrowId, ReturnRequest request) {
        BorrowRecord borrowRecord = getBorrowRecordById(borrowId);

        // 验证借阅记录状态
        if (borrowRecord.isReturned()) {
            throw new BusinessException("物品已归还，无需重复操作");
        }

        // 验证用户权限（普通用户只能归还自己的物品，管理员可以归还任何物品）
        // 这个验证在Controller层进行

        // 设置实际归还时间
        LocalDate actualReturnDate = request.getActualReturnDate();
        if (actualReturnDate == null) {
            actualReturnDate = LocalDate.now();
        }

        // 验证实际归还时间
        if (actualReturnDate.isBefore(borrowRecord.getBorrowDate().toLocalDate())) {
            throw new BusinessException("实际归还时间不能早于借出时间");
        }

        // 归还物品
        borrowRecord.returnItem(actualReturnDate);
        BorrowRecord updatedRecord = borrowRecordRepository.save(borrowRecord);

        // 更新物品状态（在BorrowRecord的returnItem方法中已经更新）

        log.info("归还物品成功，借阅ID: {}, 物品: {}",
                updatedRecord.getId(), updatedRecord.getItem().getName());

        return updatedRecord;
    }

    @Override
    @Transactional
    public BorrowRecord renewBorrow(Long borrowId, LocalDate newExpectedReturnDate) {
        BorrowRecord borrowRecord = getBorrowRecordById(borrowId);

        // 验证借阅记录状态
        if (!borrowRecord.isActive()) {
            throw new BusinessException("只有进行中的借阅记录才能续借");
        }

        // 验证是否已逾期
        if (isBorrowOverdue(borrowRecord)) {
            throw new BusinessException("逾期借阅记录不能续借，请先处理逾期");
        }

        // 验证续借时间
        if (newExpectedReturnDate == null) {
            throw new BusinessException("必须指定新的预计归还时间");
        }

        if (newExpectedReturnDate.isBefore(borrowRecord.getExpectedReturnDate())) {
            throw new BusinessException("新的预计归还时间不能早于原定时间");
        }

        long maxRenewDays = borrowProperties.getMaxRenewDays();
        long originalDays = ChronoUnit.DAYS.between(
                borrowRecord.getBorrowDate().toLocalDate(),
                borrowRecord.getExpectedReturnDate()
        );
        long newDays = ChronoUnit.DAYS.between(
                borrowRecord.getBorrowDate().toLocalDate(),
                newExpectedReturnDate
        );

        if (newDays - originalDays > maxRenewDays) {
            throw new BusinessException("续借时间不能超过 " + maxRenewDays + " 天");
        }

        // 更新预计归还时间
        borrowRecord.setExpectedReturnDate(newExpectedReturnDate);
        borrowRecord.calculateOverdueDays(); // 重新计算逾期天数

        BorrowRecord updatedRecord = borrowRecordRepository.save(borrowRecord);

        log.info("续借成功，借阅ID: {}, 新的预计归还时间: {}",
                updatedRecord.getId(), updatedRecord.getExpectedReturnDate());

        return updatedRecord;
    }

    @Override
    @Transactional
    public BorrowRecord cancelBorrow(Long borrowId) {
        BorrowRecord borrowRecord = getBorrowRecordById(borrowId);

        // 验证借阅记录状态
        if (!borrowRecord.isActive()) {
            throw new BusinessException("只有进行中的借阅记录才能取消");
        }

        // 取消借阅
        borrowRecord.setStatus(BorrowStatus.RETURNED);
        borrowRecord.setActualReturnDate(LocalDate.now());

        // 更新物品状态
        Item item = borrowRecord.getItem();
        if (item != null) {
            item.returnItem();
            itemRepository.save(item);
        }

        BorrowRecord updatedRecord = borrowRecordRepository.save(borrowRecord);

        log.info("取消借阅成功，借阅ID: {}", updatedRecord.getId());

        return updatedRecord;
    }

    @Override
    public BorrowRecord getBorrowRecordById(Long borrowId) {
        return borrowRecordRepository.findById(borrowId)
                .orElseThrow(() -> new BusinessException("借阅记录不存在，ID: " + borrowId));
    }

    @Override
    public BorrowRecord getActiveBorrowByUserAndItem(Long userId, Long itemId) {
        return borrowRecordRepository.findActiveBorrowByUserAndItem(userId, itemId)
                .orElseThrow(() -> new BusinessException("未找到活跃的借阅记录"));
    }

    @Override
    public Page<BorrowRecord> getAllBorrowRecords(Pageable pageable) {
        return borrowRecordRepository.findAll(pageable);
    }

    @Override
    public Page<BorrowRecord> getBorrowRecordsByUser(Long userId, Pageable pageable) {
        return borrowRecordRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<BorrowRecord> getBorrowRecordsByItem(Long itemId, Pageable pageable) {
        return borrowRecordRepository.findByItemId(itemId, pageable);
    }

    @Override
    public Page<BorrowRecord> getBorrowRecordsByStatus(BorrowStatus status, Pageable pageable) {
        return borrowRecordRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<BorrowRecord> getBorrowRecordsByUserAndStatus(Long userId, BorrowStatus status, Pageable pageable) {
        return borrowRecordRepository.findByUserIdAndStatus(userId, status, pageable);
    }

    @Override
    public Page<BorrowRecord> getOverdueBorrowRecords(Pageable pageable) {
        return borrowRecordRepository.findOverdueRecords(LocalDate.now(), pageable);
    }

    @Override
    public Page<BorrowRecord> getOverdueBorrowRecordsByUser(Long userId, Pageable pageable) {
        return borrowRecordRepository.findUserOverdueRecords(userId, LocalDate.now(), pageable);
    }

    @Override
    public Page<BorrowRecord> getUpcomingReturns(LocalDate date, Pageable pageable) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(borrowProperties.getAlertDays());
        return borrowRecordRepository.findUpcomingReturns(startDate, endDate, pageable);
    }

    @Override
    public Page<BorrowRecord> getUpcomingReturnsByUser(Long userId, LocalDate date, Pageable pageable) {
        // 这里需要自定义查询，我们先返回用户的所有活跃借阅记录
        return borrowRecordRepository.findByUserIdAndStatus(userId, BorrowStatus.ACTIVE, pageable);
    }

    @Override
    public long countAllBorrowRecords() {
        return borrowRecordRepository.count();
    }

    @Override
    public long countBorrowRecordsByUser(Long userId) {
        return borrowRecordRepository.findByUserId(userId, Pageable.unpaged()).getTotalElements();
    }

    @Override
    public long countBorrowRecordsByStatus(BorrowStatus status) {
        return borrowRecordRepository.findByStatus(status, Pageable.unpaged()).getTotalElements();
    }

    @Override
    public long countOverdueBorrowRecords() {
        return borrowRecordRepository.findOverdueRecords(LocalDate.now(), Pageable.unpaged()).getTotalElements();
    }

    @Override
    public long countOverdueBorrowRecordsByUser(Long userId) {
        return borrowRecordRepository.findUserOverdueRecords(userId, LocalDate.now(), Pageable.unpaged()).getTotalElements();
    }

    @Override
    public BorrowStatsDTO getBorrowStats() {
        BorrowStatsDTO stats = new BorrowStatsDTO();

        long totalBorrows = countAllBorrowRecords();
        long activeBorrows = countBorrowRecordsByStatus(BorrowStatus.ACTIVE);
        long overdueBorrows = countOverdueBorrowRecords();
        long returnedBorrows = countBorrowRecordsByStatus(BorrowStatus.RETURNED);

        stats.setTotalBorrows(totalBorrows);
        stats.setActiveBorrows(activeBorrows);
        stats.setOverdueBorrows(overdueBorrows);
        stats.setReturnedBorrows(returnedBorrows);

        // 计算归还率
        if (totalBorrows > 0) {
            double returnRate = (double) returnedBorrows / totalBorrows * 100;
            stats.setReturnRate(String.format("%.2f%%", returnRate));
        }

        // 计算逾期率
        if (activeBorrows > 0) {
            double overdueRate = (double) overdueBorrows / activeBorrows * 100;
            stats.setOverdueRate(String.format("%.2f%%", overdueRate));
        }

        // 计算平均借阅时长
        List<BorrowRecord> returnedRecords = borrowRecordRepository.findByStatus(
                BorrowStatus.RETURNED, Pageable.unpaged()).getContent();

        if (!returnedRecords.isEmpty()) {
            long totalDays = 0;
            for (BorrowRecord record : returnedRecords) {
                if (record.getActualReturnDate() != null && record.getBorrowDate() != null) {
                    long days = ChronoUnit.DAYS.between(
                            record.getBorrowDate().toLocalDate(),
                            record.getActualReturnDate()
                    );
                    totalDays += days;
                }
            }
            double avgDays = (double) totalDays / returnedRecords.size();
            stats.setAverageBorrowDays(String.format("%.1f天", avgDays));
        }

        return stats;
    }

    @Override
    public BorrowStatsDTO getUserBorrowStats(Long userId) {
        BorrowStatsDTO stats = new BorrowStatsDTO();

        long totalBorrows = countBorrowRecordsByUser(userId);
        long activeBorrows = getBorrowRecordsByUserAndStatus(userId, BorrowStatus.ACTIVE, Pageable.unpaged())
                .getTotalElements();
        long overdueBorrows = countOverdueBorrowRecordsByUser(userId);
        long returnedBorrows = getBorrowRecordsByUserAndStatus(userId, BorrowStatus.RETURNED, Pageable.unpaged())
                .getTotalElements();

        stats.setTotalBorrows(totalBorrows);
        stats.setActiveBorrows(activeBorrows);
        stats.setOverdueBorrows(overdueBorrows);
        stats.setReturnedBorrows(returnedBorrows);

        // 计算归还率
        if (totalBorrows > 0) {
            double returnRate = (double) returnedBorrows / totalBorrows * 100;
            stats.setReturnRate(String.format("%.2f%%", returnRate));
        }

        // 计算逾期率
        if (activeBorrows > 0) {
            double overdueRate = (double) overdueBorrows / activeBorrows * 100;
            stats.setOverdueRate(String.format("%.2f%%", overdueRate));
        }

        return stats;
    }

    @Override
    public boolean canUserBorrowMore(Long userId) {
        long currentBorrows = getBorrowRecordsByUserAndStatus(userId, BorrowStatus.ACTIVE, Pageable.unpaged())
                .getTotalElements();
        return currentBorrows < borrowProperties.getMaxBorrowItems();
    }

    @Override
    public boolean isItemAvailableForBorrow(Long itemId) {
        try {
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new BusinessException("物品不存在"));
            return item.isAvailable();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean hasOverdueBorrowRecords(Long userId) {
        return countOverdueBorrowRecordsByUser(userId) > 0;
    }

    @Override
    @Transactional
    public void batchUpdateOverdueStatus() {
        int updatedCount = borrowRecordRepository.updateOverdueRecords();
        log.info("批量更新逾期状态完成，共更新 {} 条记录", updatedCount);
    }

    @Override
    public List<BorrowRecord> getExpiringBorrowRecords(int daysThreshold) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(daysThreshold);
        return borrowRecordRepository.findUpcomingReturns(startDate, endDate, Pageable.unpaged())
                .getContent();
    }

    @Override
    public int calculateOverdueDays(BorrowRecord record) {
        if (record == null || record.isReturned()) {
            return 0;
        }

        if (record.getExpectedReturnDate().isAfter(LocalDate.now())) {
            return 0;
        }

        return (int) ChronoUnit.DAYS.between(record.getExpectedReturnDate(), LocalDate.now());
    }

    @Override
    public double calculateOverdueFine(BorrowRecord record, double dailyFineRate) {
        int overdueDays = calculateOverdueDays(record);
        return overdueDays * dailyFineRate;
    }

    @Override
    public boolean isBorrowOverdue(BorrowRecord record) {
        return calculateOverdueDays(record) > 0;
    }

    /**
     * 自动更新借阅记录的逾期状态和天数
     * 定时任务调用此方法
     */
    @Transactional
    public void autoUpdateBorrowStatus() {
        log.info("开始自动更新借阅记录状态...");

        // 获取所有活跃的借阅记录
        Page<BorrowRecord> activeBorrows = borrowRecordRepository.findByStatus(
                BorrowStatus.ACTIVE, Pageable.unpaged());

        int updatedCount = 0;
        for (BorrowRecord record : activeBorrows) {
            try {
                // 计算是否逾期
                boolean isOverdue = isBorrowOverdue(record);

                if (isOverdue && record.getStatus() != BorrowStatus.OVERDUE) {
                    // 更新为逾期状态
                    record.setStatus(BorrowStatus.OVERDUE);
                    record.calculateOverdueDays();
                    borrowRecordRepository.save(record);
                    updatedCount++;

                    log.info("借阅记录ID: {} 已标记为逾期，逾期天数: {}",
                            record.getId(), record.getOverdueDays());
                }
            } catch (Exception e) {
                log.error("更新借阅记录状态失败: ID={}, 错误: {}", record.getId(), e.getMessage());
            }
        }

        log.info("自动更新借阅记录状态完成，共更新 {} 条记录", updatedCount);
    }


}