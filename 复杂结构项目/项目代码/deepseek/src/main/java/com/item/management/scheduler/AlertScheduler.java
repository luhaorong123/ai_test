// AlertScheduler.java - 预警定时任务
package com.item.management.scheduler;

import com.item.management.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AlertScheduler {

    private final AlertService alertService;

    /**
     * 每天上午8点执行，发送即将到期通知
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDailyUpcomingNotifications() {
        log.info("开始执行每日即将到期通知任务...");

        try {
            alertService.sendUpcomingNotifications();
            log.info("每日即将到期通知任务完成");
        } catch (Exception e) {
            log.error("每日即将到期通知任务失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 每天上午10点执行，发送逾期通知
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void sendDailyOverdueNotifications() {
        log.info("开始执行每日逾期通知任务...");

        try {
            alertService.sendOverdueNotifications();
            log.info("每日逾期通知任务完成");
        } catch (Exception e) {
            log.error("每日逾期通知任务失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 每天下午3点执行，生成预警报告
     */
    @Scheduled(cron = "0 0 15 * * ?")
    public void generateDailyAlertReport() {
        log.info("开始生成每日预警报告...");

        try {
            // 获取预警统计
            var stats = alertService.getAlertStats();

            log.info("每日预警报告:");
            log.info("- 即将到期数量: {}", stats.getUpcomingCount());
            log.info("- 已逾期数量: {}", stats.getOverdueCount());
            log.info("- 今天到期数量: {}", stats.getTodayDueCount());
            log.info("- 严重逾期数量: {}", stats.getSevereOverdueCount());
            log.info("- 预警率: {}", stats.getAlertRate());

            // 获取每日预警
            var dailyAlerts = alertService.getDailyAlerts();
            if (!dailyAlerts.isEmpty()) {
                log.info("今日重要预警:");
                for (var alert : dailyAlerts) {
                    log.info("  - {}: {}", alert.getTitle(), alert.getMessage());
                }
            }

            log.info("每日预警报告生成完成");
        } catch (Exception e) {
            log.error("生成每日预警报告失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 每小时执行一次，检查预警状态
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkAlertStatus() {
        log.debug("检查预警状态...");

        try {
            int upcomingCount = alertService.countUpcomingReturns(3);
            int overdueCount = alertService.countOverdueRecords();

            if (upcomingCount > 0 || overdueCount > 0) {
                log.debug("当前预警状态: 即将到期={}, 逾期={}", upcomingCount, overdueCount);
            }
        } catch (Exception e) {
            log.error("检查预警状态失败: {}", e.getMessage(), e);
        }
    }
}