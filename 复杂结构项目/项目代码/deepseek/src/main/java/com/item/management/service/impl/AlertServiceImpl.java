// AlertServiceImpl.java - 预警服务实现类
package com.item.management.service.impl;

import com.item.management.converter.BorrowConverter;
import com.item.management.dto.response.AlertStatsDTO;
import com.item.management.dto.response.AlertSummaryDTO;
import com.item.management.dto.response.BorrowSummaryDTO;
import com.item.management.entity.BorrowRecord;
import com.item.management.entity.User;
import com.item.management.enums.BorrowStatus;
import com.item.management.exception.BusinessException;
import com.item.management.repository.BorrowRecordRepository;
import com.item.management.repository.UserRepository;
import com.item.management.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final UserRepository userRepository;
    private final BorrowConverter borrowConverter;

    @Override
    public Page<BorrowSummaryDTO> getUpcomingReturns(Integer days, Pageable pageable) {
        if (days == null || days <= 0) {
            days = 3; // 默认3天
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days);

        Page<BorrowRecord> borrowPage = borrowRecordRepository.findUpcomingReturns(startDate, endDate, pageable);
        return borrowPage.map(borrowConverter::toSummaryDTO);
    }

    @Override
    public Page<BorrowSummaryDTO> getUpcomingReturnsByUser(Long userId, Integer days, Pageable pageable) {
        // 验证用户是否存在
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在，ID: " + userId));

        if (days == null || days <= 0) {
            days = 3; // 默认3天
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days);

        // 先查询所有即将到期的记录
        Page<BorrowRecord> allUpcoming = borrowRecordRepository.findUpcomingReturns(startDate, endDate, pageable);

        // 过滤出指定用户的记录
        List<BorrowRecord> userRecords = allUpcoming.getContent().stream()
                .filter(record -> record.getUser() != null && record.getUser().getId().equals(userId))
                .collect(Collectors.toList());

        // 创建新的Page对象
        Page<BorrowRecord> userUpcoming = new org.springframework.data.domain.PageImpl<>(
                userRecords,
                pageable,
                userRecords.size()
        );

        return userUpcoming.map(borrowConverter::toSummaryDTO);
    }

    @Override
    public Page<BorrowSummaryDTO> getOverdueBorrowRecords(Pageable pageable) {
        Page<BorrowRecord> borrowPage = borrowRecordRepository.findOverdueRecords(LocalDate.now(), pageable);
        return borrowPage.map(borrowConverter::toSummaryDTO);
    }

    @Override
    public Page<BorrowSummaryDTO> getOverdueBorrowRecordsByUser(Long userId, Pageable pageable) {
        // 验证用户是否存在
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在，ID: " + userId));

        Page<BorrowRecord> borrowPage = borrowRecordRepository.findUserOverdueRecords(userId, LocalDate.now(), pageable);
        return borrowPage.map(borrowConverter::toSummaryDTO);
    }

    @Override
    public Page<BorrowSummaryDTO> getAllAlerts(Pageable pageable) {
        // 获取所有预警记录：包括即将到期和已逾期
        LocalDate now = LocalDate.now();
        LocalDate threeDaysLater = now.plusDays(3);

        // 查询即将到期（3天内）的记录
        Page<BorrowRecord> upcomingPage = borrowRecordRepository.findUpcomingReturns(now, threeDaysLater, pageable);

        // 查询已逾期的记录
        Page<BorrowRecord> overduePage = borrowRecordRepository.findOverdueRecords(now, pageable);

        // 合并结果（注意：这里简化为只返回逾期记录，实际项目中可能需要更复杂的合并逻辑）
        return overduePage.map(borrowConverter::toSummaryDTO);
    }

    @Override
    public Page<BorrowSummaryDTO> getAlertsByUser(Long userId, Pageable pageable) {
        // 验证用户是否存在
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在，ID: " + userId));

        LocalDate now = LocalDate.now();
        LocalDate threeDaysLater = now.plusDays(3);

        // 查询用户即将到期的记录
        List<BorrowRecord> userUpcoming = borrowRecordRepository.findUpcomingReturns(now, threeDaysLater, Pageable.unpaged())
                .getContent().stream()
                .filter(record -> record.getUser() != null && record.getUser().getId().equals(userId))
                .collect(Collectors.toList());

        // 查询用户已逾期的记录
        Page<BorrowRecord> userOverduePage = borrowRecordRepository.findUserOverdueRecords(userId, now, pageable);

        // 这里简化为只返回逾期记录的分页
        return userOverduePage.map(borrowConverter::toSummaryDTO);
    }

    @Override
    public AlertStatsDTO getAlertStats() {
        AlertStatsDTO stats = new AlertStatsDTO();

        LocalDate now = LocalDate.now();
        LocalDate threeDaysLater = now.plusDays(3);

        // 统计即将到期的记录
        long upcomingCount = borrowRecordRepository.findUpcomingReturns(now, threeDaysLater, Pageable.unpaged())
                .getTotalElements();

        // 统计已逾期的记录
        long overdueCount = borrowRecordRepository.findOverdueRecords(now, Pageable.unpaged())
                .getTotalElements();

        // 统计今天到期的记录
        long todayDueCount = borrowRecordRepository.findUpcomingReturns(now, now, Pageable.unpaged())
                .getTotalElements();

        // 统计严重逾期（超过7天）的记录
        long severeOverdueCount = borrowRecordRepository.findOverdueRecords(now.minusDays(7), Pageable.unpaged())
                .getContent().stream()
                .filter(record -> ChronoUnit.DAYS.between(record.getExpectedReturnDate(), now) > 7)
                .count();

        // 统计按用户分组
        Map<String, Long> userOverdueStats = new HashMap<>();
        List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueRecords(now, Pageable.unpaged())
                .getContent();

        for (BorrowRecord record : overdueRecords) {
            if (record.getUser() != null) {
                String username = record.getUser().getUsername();
                userOverdueStats.put(username, userOverdueStats.getOrDefault(username, 0L) + 1);
            }
        }

        // 统计按物品类别分组
        Map<String, Long> categoryOverdueStats = new HashMap<>();
        for (BorrowRecord record : overdueRecords) {
            if (record.getItem() != null && record.getItem().getCategory() != null) {
                String category = record.getItem().getCategory();
                categoryOverdueStats.put(category, categoryOverdueStats.getOrDefault(category, 0L) + 1);
            }
        }

        stats.setUpcomingCount(upcomingCount);
        stats.setOverdueCount(overdueCount);
        stats.setTodayDueCount(todayDueCount);
        stats.setSevereOverdueCount(severeOverdueCount);
        stats.setUserOverdueStats(userOverdueStats);
        stats.setCategoryOverdueStats(categoryOverdueStats);

        // 计算预警率（逾期数/总活跃借阅数）
        long activeCount = borrowRecordRepository.findByStatus(BorrowStatus.ACTIVE, Pageable.unpaged())
                .getTotalElements();

        if (activeCount > 0) {
            double alertRate = (double) (upcomingCount + overdueCount) / activeCount * 100;
            stats.setAlertRate(String.format("%.2f%%", alertRate));
        }

        return stats;
    }

    @Override
    public AlertStatsDTO getUserAlertStats(Long userId) {
        AlertStatsDTO stats = new AlertStatsDTO();

        // 验证用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在，ID: " + userId));

        LocalDate now = LocalDate.now();
        LocalDate threeDaysLater = now.plusDays(3);

        // 统计用户即将到期的记录
        long userUpcomingCount = borrowRecordRepository.findUpcomingReturns(now, threeDaysLater, Pageable.unpaged())
                .getContent().stream()
                .filter(record -> record.getUser() != null && record.getUser().getId().equals(userId))
                .count();

        // 统计用户已逾期的记录
        long userOverdueCount = borrowRecordRepository.findUserOverdueRecords(userId, now, Pageable.unpaged())
                .getTotalElements();

        // 统计用户今天到期的记录
        long userTodayDueCount = borrowRecordRepository.findUpcomingReturns(now, now, Pageable.unpaged())
                .getContent().stream()
                .filter(record -> record.getUser() != null && record.getUser().getId().equals(userId))
                .count();

        // 统计用户严重逾期记录
        List<BorrowRecord> userOverdueRecords = borrowRecordRepository.findUserOverdueRecords(userId, now, Pageable.unpaged())
                .getContent();

        long userSevereOverdueCount = userOverdueRecords.stream()
                .filter(record -> ChronoUnit.DAYS.between(record.getExpectedReturnDate(), now) > 7)
                .count();

        stats.setUpcomingCount(userUpcomingCount);
        stats.setOverdueCount(userOverdueCount);
        stats.setTodayDueCount(userTodayDueCount);
        stats.setSevereOverdueCount(userSevereOverdueCount);

        // 计算用户逾期率
        long userActiveCount = borrowRecordRepository.findByUserIdAndStatus(userId, BorrowStatus.ACTIVE, Pageable.unpaged())
                .getTotalElements();

        if (userActiveCount > 0) {
            double userAlertRate = (double) (userUpcomingCount + userOverdueCount) / userActiveCount * 100;
            stats.setAlertRate(String.format("%.2f%%", userAlertRate));
        }

        // 设置用户信息
        stats.setUsername(user.getUsername());
        stats.setUserId(userId);

        return stats;
    }

    @Override
    public Map<String, Object> getAlertSummary() {
        Map<String, Object> summary = new HashMap<>();

        LocalDate now = LocalDate.now();
        LocalDate threeDaysLater = now.plusDays(3);

        // 获取预警统计
        AlertStatsDTO stats = getAlertStats();
        summary.put("stats", stats);

        // 获取即将到期的前10条记录
        Page<BorrowSummaryDTO> upcomingTop10 = getUpcomingReturns(3,
                org.springframework.data.domain.PageRequest.of(0, 10));
        summary.put("upcomingTop10", upcomingTop10.getContent());

        // 获取已逾期的前10条记录
        Page<BorrowSummaryDTO> overdueTop10 = getOverdueBorrowRecords(
                org.springframework.data.domain.PageRequest.of(0, 10));
        summary.put("overdueTop10", overdueTop10.getContent());

        // 获取预警最严重的用户（逾期最多的用户）
        List<Object[]> worstUsers = borrowRecordRepository.findMostOverdueUsers(
                org.springframework.data.domain.PageRequest.of(0, 5));

        List<Map<String, Object>> worstUserList = new ArrayList<>();
        for (Object[] row : worstUsers) {
            if (row.length >= 2) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("userId", row[0]);
                userInfo.put("username", row[1]);
                userInfo.put("overdueCount", row[2]);
                worstUserList.add(userInfo);
            }
        }
        summary.put("worstUsers", worstUserList);

        // 获取最常逾期的物品类别
        List<Object[]> worstCategories = borrowRecordRepository.findMostOverdueCategories(
                org.springframework.data.domain.PageRequest.of(0, 5));

        List<Map<String, Object>> worstCategoryList = new ArrayList<>();
        for (Object[] row : worstCategories) {
            if (row.length >= 2) {
                Map<String, Object> categoryInfo = new HashMap<>();
                categoryInfo.put("category", row[0]);
                categoryInfo.put("overdueCount", row[1]);
                worstCategoryList.add(categoryInfo);
            }
        }
        summary.put("worstCategories", worstCategoryList);

        return summary;
    }

    @Override
    public void sendOverdueNotifications() {
        log.info("开始发送逾期通知...");

        LocalDate now = LocalDate.now();
        List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueRecords(now, Pageable.unpaged())
                .getContent();

        int notificationCount = 0;
        for (BorrowRecord record : overdueRecords) {
            if (record.getUser() != null && record.getItem() != null) {
                // 这里可以调用通知服务发送邮件、短信等
                // 实际项目中应该异步发送通知
                log.info("发送逾期通知: 用户={}, 物品={}, 逾期天数={}",
                        record.getUser().getUsername(),
                        record.getItem().getName(),
                        record.getOverdueDays());
                notificationCount++;
            }
        }

        log.info("逾期通知发送完成，共发送 {} 条通知", notificationCount);
    }

    @Override
    public void sendUpcomingNotifications() {
        log.info("开始发送即将到期通知...");

        LocalDate now = LocalDate.now();
        LocalDate threeDaysLater = now.plusDays(3);

        List<BorrowRecord> upcomingRecords = borrowRecordRepository.findUpcomingReturns(now, threeDaysLater, Pageable.unpaged())
                .getContent();

        int notificationCount = 0;
        for (BorrowRecord record : upcomingRecords) {
            if (record.getUser() != null && record.getItem() != null) {
                long daysUntilReturn = ChronoUnit.DAYS.between(now, record.getExpectedReturnDate());

                // 这里可以调用通知服务发送邮件、短信等
                log.info("发送即将到期通知: 用户={}, 物品={}, 剩余天数={}",
                        record.getUser().getUsername(),
                        record.getItem().getName(),
                        daysUntilReturn);
                notificationCount++;
            }
        }

        log.info("即将到期通知发送完成，共发送 {} 条通知", notificationCount);
    }

    @Override
    public List<AlertSummaryDTO> getDailyAlerts() {
        List<AlertSummaryDTO> dailyAlerts = new ArrayList<>();
        LocalDate now = LocalDate.now();

        // 获取今天到期的记录
        List<BorrowRecord> todayDue = borrowRecordRepository.findUpcomingReturns(now, now, Pageable.unpaged())
                .getContent();

        for (BorrowRecord record : todayDue) {
            AlertSummaryDTO alert = AlertSummaryDTO.builder()
                    .type("TODAY_DUE")
                    .title("物品今天到期")
                    .message(String.format("物品《%s》今天到期，请及时归还",
                            record.getItem() != null ? record.getItem().getName() : "未知物品"))
                    .userId(record.getUser() != null ? record.getUser().getId() : null)
                    .username(record.getUser() != null ? record.getUser().getUsername() : null)
                    .itemId(record.getItem() != null ? record.getItem().getId() : null)
                    .itemName(record.getItem() != null ? record.getItem().getName() : null)
                    .borrowId(record.getId())
                    .expectedReturnDate(record.getExpectedReturnDate())
                    .createdAt(LocalDateTime.now())
                    .build();
            dailyAlerts.add(alert);
        }

        // 获取严重逾期的记录（超过7天）
        List<BorrowRecord> severeOverdue = borrowRecordRepository.findOverdueRecords(now.minusDays(7), Pageable.unpaged())
                .getContent().stream()
                .filter(record -> ChronoUnit.DAYS.between(record.getExpectedReturnDate(), now) > 7)
                .collect(Collectors.toList());

        for (BorrowRecord record : severeOverdue) {
            long overdueDays = ChronoUnit.DAYS.between(record.getExpectedReturnDate(), now);

            AlertSummaryDTO alert = AlertSummaryDTO.builder()
                    .type("SEVERE_OVERDUE")
                    .title("物品严重逾期")
                    .message(String.format("物品《%s》已严重逾期 %d 天，请立即处理",
                            record.getItem() != null ? record.getItem().getName() : "未知物品", overdueDays))
                    .userId(record.getUser() != null ? record.getUser().getId() : null)
                    .username(record.getUser() != null ? record.getUser().getUsername() : null)
                    .itemId(record.getItem() != null ? record.getItem().getId() : null)
                    .itemName(record.getItem() != null ? record.getItem().getName() : null)
                    .borrowId(record.getId())
                    .expectedReturnDate(record.getExpectedReturnDate())
                    .overdueDays((int) overdueDays)
                    .createdAt(LocalDateTime.now())
                    .build();
            dailyAlerts.add(alert);
        }

        return dailyAlerts;
    }

    @Override
    public boolean hasUpcomingReturns(Long userId, Integer days) {
        if (days == null || days <= 0) {
            days = 3;
        }

        LocalDate now = LocalDate.now();
        LocalDate endDate = now.plusDays(days);

        List<BorrowRecord> upcoming = borrowRecordRepository.findUpcomingReturns(now, endDate, Pageable.unpaged())
                .getContent();

        if (userId == null) {
            return !upcoming.isEmpty();
        } else {
            return upcoming.stream()
                    .anyMatch(record -> record.getUser() != null && record.getUser().getId().equals(userId));
        }
    }

    @Override
    public boolean hasOverdueRecords(Long userId) {
        LocalDate now = LocalDate.now();

        if (userId == null) {
            long overdueCount = borrowRecordRepository.findOverdueRecords(now, Pageable.unpaged())
                    .getTotalElements();
            return overdueCount > 0;
        } else {
            long userOverdueCount = borrowRecordRepository.findUserOverdueRecords(userId, now, Pageable.unpaged())
                    .getTotalElements();
            return userOverdueCount > 0;
        }
    }

    @Override
    public int countUpcomingReturns(Integer days) {
        if (days == null || days <= 0) {
            days = 3;
        }

        LocalDate now = LocalDate.now();
        LocalDate endDate = now.plusDays(days);

        return (int) borrowRecordRepository.findUpcomingReturns(now, endDate, Pageable.unpaged())
                .getTotalElements();
    }

    @Override
    public int countOverdueRecords() {
        LocalDate now = LocalDate.now();
        return (int) borrowRecordRepository.findOverdueRecords(now, Pageable.unpaged())
                .getTotalElements();
    }
}