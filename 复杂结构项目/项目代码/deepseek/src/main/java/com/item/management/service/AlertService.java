// AlertService.java - 预警服务接口
package com.item.management.service;

import com.item.management.dto.response.AlertStatsDTO;
import com.item.management.dto.response.AlertSummaryDTO;
import com.item.management.dto.response.BorrowSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AlertService {

    // 预警查询
    Page<BorrowSummaryDTO> getUpcomingReturns(Integer days, Pageable pageable);

    Page<BorrowSummaryDTO> getUpcomingReturnsByUser(Long userId, Integer days, Pageable pageable);

    Page<BorrowSummaryDTO> getOverdueBorrowRecords(Pageable pageable);

    Page<BorrowSummaryDTO> getOverdueBorrowRecordsByUser(Long userId, Pageable pageable);

    Page<BorrowSummaryDTO> getAllAlerts(Pageable pageable);

    Page<BorrowSummaryDTO> getAlertsByUser(Long userId, Pageable pageable);

    // 统计信息
    AlertStatsDTO getAlertStats();

    AlertStatsDTO getUserAlertStats(Long userId);

    Map<String, Object> getAlertSummary();

    // 批量处理
    void sendOverdueNotifications();

    void sendUpcomingNotifications();

    List<AlertSummaryDTO> getDailyAlerts();

    // 验证和检查
    boolean hasUpcomingReturns(Long userId, Integer days);

    boolean hasOverdueRecords(Long userId);

    int countUpcomingReturns(Integer days);

    int countOverdueRecords();
}