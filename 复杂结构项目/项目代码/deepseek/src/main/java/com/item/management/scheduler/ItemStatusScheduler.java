// ItemStatusScheduler.java - 物品状态自动更新定时任务
package com.item.management.scheduler;

import com.item.management.entity.BorrowRecord;
import com.item.management.entity.Item;
import com.item.management.enums.BorrowStatus;
import com.item.management.enums.ItemStatus;
import com.item.management.repository.BorrowRecordRepository;
import com.item.management.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ItemStatusScheduler {

    private final ItemRepository itemRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    /**
     * 每天凌晨2点执行，自动更新物品状态
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void autoUpdateItemStatus() {
        log.info("开始执行物品状态自动更新任务...");

        // 1. 更新逾期借阅记录对应的物品状态
        updateOverdueItemStatus();

        // 2. 检查并更新长时间未归还的物品
        updateLongTimeBorrowedItems();

        // 3. 自动将维护中的物品恢复为可借状态（如果维护时间过长）
        autoRecoverMaintenanceItems();

        log.info("物品状态自动更新任务完成");
    }

    /**
     * 更新逾期借阅记录对应的物品状态
     */
    private void updateOverdueItemStatus() {
        // 查找所有逾期的借阅记录
        List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueRecords(
                LocalDate.now(),
                org.springframework.data.domain.Pageable.unpaged()
        ).getContent();

        int updatedCount = 0;
        for (BorrowRecord record : overdueRecords) {
            if (record.getItem() != null && record.getItem().getStatus() != ItemStatus.BORROWED) {
                // 如果物品状态不是BORROWED，更新为BORROWED
                record.getItem().setStatus(ItemStatus.BORROWED);
                itemRepository.save(record.getItem());
                updatedCount++;
            }
        }

        log.info("更新逾期物品状态完成，共更新 {} 个物品", updatedCount);
    }

    /**
     * 检查并更新长时间未归还的物品
     * 超过预计归还时间30天仍未归还的物品，自动标记为异常
     */
    private void updateLongTimeBorrowedItems() {
        LocalDate thresholdDate = LocalDate.now().minusDays(30);

        List<BorrowRecord> longTimeBorrows = borrowRecordRepository.findAll().stream()
                .filter(record -> record.getStatus() == BorrowStatus.ACTIVE)
                .filter(record -> record.getExpectedReturnDate().isBefore(thresholdDate))
                .collect(java.util.stream.Collectors.toList());

        int markedCount = 0;
        for (BorrowRecord record : longTimeBorrows) {
            // 这里可以添加额外的处理逻辑，如发送通知等
            log.warn("发现长时间未归还物品: 物品ID={}, 借阅ID={}, 预计归还时间={}",
                    record.getItem().getId(), record.getId(), record.getExpectedReturnDate());
            markedCount++;
        }

        log.info("检查长时间未归还物品完成，共发现 {} 个物品", markedCount);
    }

    /**
     * 自动将维护中的物品恢复为可借状态
     * 维护时间超过14天的物品自动恢复
     */
    private void autoRecoverMaintenanceItems() {
        LocalDateTime thresholdDateTime = LocalDateTime.now().minusDays(14);

        List<Item> maintenanceItems = itemRepository.findByStatus(ItemStatus.MAINTENANCE,
                org.springframework.data.domain.Pageable.unpaged()).getContent();

        int recoveredCount = 0;
        for (Item item : maintenanceItems) {
            // 检查最后更新时间是否超过阈值
            if (item.getUpdatedAt() != null && item.getUpdatedAt().isBefore(thresholdDateTime)) {
                item.setStatus(ItemStatus.AVAILABLE);
                itemRepository.save(item);
                recoveredCount++;
                log.info("自动恢复维护中物品: {} (ID: {})", item.getName(), item.getId());
            }
        }

        log.info("自动恢复维护中物品完成，共恢复 {} 个物品", recoveredCount);
    }

    /**
     * 每小时执行一次，检查物品状态一致性
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void checkItemStatusConsistency() {
        log.debug("开始执行物品状态一致性检查...");

        // 获取所有被借出的物品
        List<Item> borrowedItems = itemRepository.findByStatus(ItemStatus.BORROWED,
                org.springframework.data.domain.Pageable.unpaged()).getContent();

        int fixedCount = 0;
        for (Item item : borrowedItems) {
            // 检查是否存在对应的活跃借阅记录
            boolean hasActiveBorrow = borrowRecordRepository.findActiveBorrows(
                            org.springframework.data.domain.Pageable.unpaged()).getContent().stream()
                    .anyMatch(record -> record.getItem().getId().equals(item.getId()));

            if (!hasActiveBorrow) {
                // 如果没有活跃的借阅记录，但物品状态是BORROWED，则恢复为AVAILABLE
                item.setStatus(ItemStatus.AVAILABLE);
                itemRepository.save(item);
                fixedCount++;
                log.warn("修复物品状态不一致: {} (ID: {})，从BORROWED恢复为AVAILABLE",
                        item.getName(), item.getId());
            }
        }

        if (fixedCount > 0) {
            log.info("物品状态一致性检查完成，共修复 {} 个物品", fixedCount);
        } else {
            log.debug("物品状态一致性检查完成，未发现不一致");
        }
    }
}