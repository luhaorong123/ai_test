// BorrowStatusScheduler.java - 借阅状态自动更新定时任务
package com.item.management.scheduler;

import com.item.management.service.BorrowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class BorrowStatusScheduler {

    private final BorrowService borrowService;

    /**
     * 每天凌晨1点执行，自动更新借阅状态为逾期
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void autoUpdateBorrowStatus() {
        log.info("开始执行借阅状态自动更新任务...");

        try {
            borrowService.autoUpdateBorrowStatus();
            log.info("借阅状态自动更新任务完成");
        } catch (Exception e) {
            log.error("借阅状态自动更新任务失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 每天上午9点执行，检查即将到期的借阅
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkUpcomingReturns() {
        log.info("开始检查即将到期的借阅...");

        try {
            // 获取3天内到期的借阅记录
            var expiringBorrows = borrowService.getExpiringBorrowRecords(3);

            if (!expiringBorrows.isEmpty()) {
                log.info("发现 {} 条即将到期的借阅记录", expiringBorrows.size());

                // 这里可以添加发送通知的逻辑，比如发送邮件、短信等
                for (var borrow : expiringBorrows) {
                    log.info("即将到期借阅: 用户={}, 物品={}, 预计归还时间={}",
                            borrow.getUser().getUsername(),
                            borrow.getItem().getName(),
                            borrow.getExpectedReturnDate());
                }
            } else {
                log.info("没有发现即将到期的借阅记录");
            }
        } catch (Exception e) {
            log.error("检查即将到期借阅失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 每周一上午10点执行，生成借阅统计报告
     */
    @Scheduled(cron = "0 0 10 ? * MON")
    public void generateWeeklyReport() {
        log.info("开始生成借阅统计周报...");

        try {
            var stats = borrowService.getBorrowStats();

            // 记录统计信息
            log.info("借阅统计周报:");
            log.info("- 总借阅次数: {}", stats.getTotalBorrows());
            log.info("- 当前借阅中: {}", stats.getActiveBorrows());
            log.info("- 逾期借阅: {}", stats.getOverdueBorrows());
            log.info("- 已归还: {}", stats.getReturnedBorrows());
            log.info("- 归还率: {}", stats.getReturnRate());
            log.info("- 逾期率: {}", stats.getOverdueRate());
            log.info("- 平均借阅时长: {}", stats.getAverageBorrowDays());

            // 这里可以添加发送报告的逻辑
            log.info("借阅统计周报生成完成");
        } catch (Exception e) {
            log.error("生成借阅统计周报失败: {}", e.getMessage(), e);
        }
    }
}